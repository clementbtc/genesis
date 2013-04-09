package com.projetloki.genesis.image;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.PrimitiveSink;

/**
 * Handles hte rasterization of a shape.
 *
 * @see RasterOnShrinkShape
 * @author Clément Roux
 */
final class RasterShapeManager {
  private final RasterOnShrinkShape<?> original;
  private final int width;
  private final int height;
  private transient volatile DummyBitmap originalBitmap;
  final MathVector toPosition;

  static final int SCALE_FACTOR = 8;

  RasterShapeManager(RasterOnShrinkShape<?> original,
      int width, int height) {
    this.original = original;
    this.width = width;
    this.height = height;
    toPosition = Point.ORIGIN.to(original.position());
  }

  Shape shrinkShape(double margin) {
    if (margin == 0) {
      return original;
    }
    return new RasterShape(original, margin).translate(toPosition);
  }

  DummyBitmap createBitmap(double margin) {
    DummyBitmap bitmap = originalBitmap;
    if (bitmap == null) {
      synchronized (this) {
        bitmap = originalBitmap;
        if (bitmap == null) {
          originalBitmap = bitmap =
              createOriginalBitmap(original.translate(toPosition.negate()),
                  width, height, SCALE_FACTOR);
        }
      }
    }
    byte bb = (margin < 0) ? (byte) 1 : (byte) 0;
    double radius = Math.abs(SCALE_FACTOR * margin) + 0.5;
    int iRadius = (int) radius;
    int dim = 2 * iRadius + 1;
    Shape diskff = Shapes.ellipse(
        iRadius + 0.5d - radius, iRadius + 0.5d - radius,
        2 * radius, 2 * radius);
    Shape disktf = Shapes.intersection(diskff,
        diskff.translate(-1, 0).negate());
    Shape diskft = Shapes.intersection(diskff,
        diskff.translate(0, -1).negate());
    Shape disktt = Shapes.intersection(diskff,
        diskff.translate(-1, 0).negate(),
        diskff.translate(0, -1).negate());
    List<IPoint> p00 = getPoints(createOriginalBitmap(diskff, dim, dim, 1),
        iRadius, iRadius);
    List<IPoint> p10 = getPoints(createOriginalBitmap(disktf, dim, dim, 1),
        iRadius, iRadius);
    List<IPoint> p01 = getPoints(createOriginalBitmap(diskft, dim, dim, 1),
        iRadius, iRadius);
    List<IPoint> p11 = getPoints(createOriginalBitmap(disktt, dim, dim, 1),
        iRadius, iRadius);
    int ww = bitmap.width();
    int hh = bitmap.height();
    DummyBitmap result = new DummyBitmap(ww, hh);
    if (0 <= margin) {
      result.fill();
    }
    for (int i = -1; i <= ww; i++) {
      for (int j = -1; j <= hh; j++) {
        if (bitmap.get(i, j) == bb) {
          List<IPoint> points;
          boolean ptf = (bitmap.get(i - 1, j) == bb);
          boolean pft = (bitmap.get(i, j - 1) == bb);
          if (i == -1 || j == -1) {
            points = p00;
          } else if (ptf) {
            points = pft ? p11 : p10;
          } else {
            points = pft ? p01 : p00;
          }
          for (IPoint p : points) {
            result.set(i + p.plusX, j + p.plusY, bb);
          }
        }
      }
    }
    return result;
  }

  private static DummyBitmap createOriginalBitmap(Shape original,
      int width, int height, int scaleFactor) {
    int ww = scaleFactor * width;
    int hh = scaleFactor * height;
    DummyBitmap bitmap = new DummyBitmap(ww, hh);
    for (int i = 0; i < ww; i++) {
      for (int j = 0; j < hh; j++) {
        double xx = (i + 0.5d) / scaleFactor;
        double yy = (j + 0.5d) / scaleFactor;
        Point p = new Point(xx, yy);
        if (original.contains(p)) {
          bitmap.set(i, j, (byte) 1);
        }
      }
    }
    return bitmap;
  }

  private static List<IPoint> getPoints(DummyBitmap bitmap,
      int centerX, int centerY) {
    ImmutableList.Builder<IPoint> builder = ImmutableList.builder();
    for (int i = 0; i < bitmap.width(); i++) {
      for (int j = 0; j < bitmap.height(); j++) {
        if (bitmap.get(i, j) == 1) {
          IPoint point = new IPoint(i - centerX, j - centerY);
          builder.add(point);
        }
      }
    }
    return builder.build();
  }

  private static class IPoint {
    final int plusX;
    final int plusY;

    IPoint(int plusX, int plusY) {
      this.plusX = plusX;
      this.plusY = plusY;
    }
  }

  private static class RasterShape extends HashCachingShape<RasterShape>
      implements Serializable {
    private final RasterOnShrinkShape<?> original;
    private final double margin;
    private transient volatile DummyBitmap bitmap;

    RasterShape(RasterOnShrinkShape<?> original, double margin) {
      checkArgument(margin != 0, margin);
      this.original = checkNotNull(original);
      this.margin = margin;
    }

    @Override public boolean contains(Point p) {
      DummyBitmap b = bitmap;
      if (b == null) {
        synchronized (this) {
          bitmap = b = original.manager().createBitmap(margin);
        }
      }
      int i = (int) (SCALE_FACTOR * p.x + 0.5);
      int j = (int) (SCALE_FACTOR * p.y + 0.5);
      return b.get(i, j) != 0;
    }

    @Override public Shape shrink(double margin) {
      if (margin == 0) {
        return this;
      }
      double newMargin = margin + this.margin;
      if (newMargin == 0) {
        return original.translate(original.manager().toPosition);
      }
      return new RasterShape(original, newMargin);
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(-6726143396250977132L)
          .putBytes(original.hash().asBytes())
          .putDouble(margin);
    }

    @Override public boolean doEquals(RasterShape that) {
      return original.equals(that.original) && margin == that.margin;
    }

    @Override public String toString() {
      return original + ".shrink(" + margin + ")";
    }
    private static final long serialVersionUID = 0;
  }
}
