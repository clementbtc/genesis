package com.projetloki.genesis.image;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import com.google.common.hash.PrimitiveSink;

/**
 * A shape in the plane, defined by its interior.
 *
 * <p>Shapes are immutable. All the methods transforming a shape, e.g.
 * {@link #translate(double, double)}, return a new shape which is the result of
 * the transformation and leave the original shape unmodified.</p>
 *
 * <p>All methods return serializable shapes as long as {@code this} and the
 * arguments are serializable.</p>
 *
 * @see Shapes
 * @see Image#mask(Shape)
 * @author Clément Roux
 */
public abstract class Shape implements Hashable {
  /** Returns whether the point lies within the interior of the shape. */
  public abstract boolean contains(Point p);

  /**
   * Returns the shape obtained by shrinking this shape, so that the distance
   * between the boundary of each shape equals {@code margin}. If {@code margin}
   * is negative, the shape grows instead of shrinking.
   * @param margin the distance between the bounds of the two shapes
   */
  public abstract Shape shrink(double margin);

  // ---------------------------------------------------------------------------
  // Internal methods that can be overridden for optimization
  // ---------------------------------------------------------------------------

  /** Same as {@link Image#features(). Opaque must be false. */
  ImageFeatures features() {
    return ImageFeatures.start();
  }

  /**
   * Returns the shape obtained by reversing the interior and exterior of this
   * shape.
   */
  public Shape negate() {
    return new NegateShape(this);
  }

  /**
   * Returns the translate of this shape by the vector with coordinates
   * ({@code dx}, {@code dy})
   * @param dx the x-coordinate of the vector
   * @param dy the y-coordinate of the vector
   * @see #translate(MathVector)
   */
  public final Shape translate(double dx, double dy) {
    return translate(new MathVector(dx, dy));
  }

  /**
   * Returns the translate of this shape by the given vector.
   * @param v the vector
   * @see #translate(double, double)
   */
  public Shape translate(MathVector v) {
    if (v.isZero()) {
      return this;
    }
    return new TranslateShape(this, v);
  }

  /**
   * Returns the image of this shape by the clockwise rotation through the given
   * angle around the given point.
   * @param theta in radians
   * @param center the rotation center
   */
  public Shape rotate(double theta, Point center) {
    checkNotNull(center);
    if (theta == 0d) {
      return this;
    }
    return new RotateShape(this, theta, center);
  }

  /**
   * Returns the shape obtained by flipping this shape over the axis x = x0
   * @param x0 the x-coordinate of the axis
   */
  public Shape flipX(double x0) {
    return new FlipXShape(this, x0);
  }

  /**
   * Returns the shape obtained by flipping this shape over the axis y = y0
   * @param y0 the y-coordinate of the axis
   */
  public Shape flipY(double y0) {
    return new FlipYShape(this, y0);
  }

  /**
   * Returns the shape obtained by reflecting the given upper half-plane on the
   * complementary lower half-plane.
   * @param y0 the y-coordinate of the axis of symmetry
   */
  public Shape reflectUpperHalf(double y0) {
    return new ReflectUpperHalfShape(this, y0);
  }

  /**
   * Returns the shape obtained by reflecting the given right half-plane on the
   * complementary left half-plane.
   * @param x0 the x-coordinate of the axis of symmetry
   */
  public Shape reflectRightHalf(double x0) {
    return new ReflectRightHalfShape(this, x0);
  }

  /**
   * Returns the shape obtained by reflecting the given lower half-plane on the
   * complementary upper half-plane.
   * @param y0 the y-coordinate of the axis of symmetry
   */
  public Shape reflectLowerHalf(double y0) {
    return new ReflectLowerHalfShape(this, y0);
  }

  /**
   * Returns the shape obtained by reflecting the given left half-plane on the
   * complementary right half-plane.
   * @param x0 the x-coordinate of the axis of symmetry
   */
  public Shape reflectLeftHalf(double x0) {
    return new ReflectLeftHalfShape(this, x0);
  }

  Shape rotateCw(int width) {
    return new RotateCwShape(this, width);
  }

  Shape rotateCcw(int height) {
    return new RotateCcwShape(this, height);
  }

  @Override public final int hashCode() {
    return hash().hashCode();
  }

  // ---------------------------------------------------------------------------
  // Implementations
  // ---------------------------------------------------------------------------

  private static class NegateShape extends HashCachingShape<NegateShape>
      implements Serializable {
    final Shape operand;

    NegateShape(Shape operand) {
      this.operand = operand;
    }

    @Override public boolean contains(Point p) {
      return !operand.contains(p);
    }

    @Override public Shape shrink(double margin) {
      return operand.shrink(-margin).negate();
    }

    @Override ImageFeatures features() {
      return operand.features();
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(-8481433442113168823L)
          .putBytes(operand.hash().asBytes());
    }

    @Override public boolean doEquals(NegateShape that) {
      return operand.equals(that.operand);
    }

    @Override public Shape negate() {
      return operand;
    }

    @Override public String toString() {
      return operand + ".negate()";
    }

    private static final long serialVersionUID = 0;
  }

  private static class TranslateShape extends HashCachingShape<TranslateShape>
      implements Serializable {
    final Shape operand;
    final MathVector v;

    TranslateShape(Shape operand, MathVector v) {
      this.operand = operand;
      this.v = v.negate();
    }

    @Override public boolean contains(Point p) {
      return operand.contains(p.translate(v));
    }

    @Override public Shape shrink(double margin) {
      return operand.shrink(margin).translate(v.negate());
    }

    @Override ImageFeatures features() {
      return operand.features()
          .andRaster(Util.isInt(v.dx) && Util.isInt(v.dy));
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(2260377423813708714L)
          .putBytes(operand.hash().asBytes())
          .putDouble(v.dx)
          .putDouble(v.dy);
    }

    @Override public boolean doEquals(TranslateShape that) {
      return operand.equals(that.operand) && v.equals(that.v);
    }

    @Override public Shape translate(MathVector v) {
      return operand.translate(v.plus(this.v));
    }

    @Override public String toString() {
      return operand + ".translate(" + v.negate() + ")";
    }

    private static final long serialVersionUID = 0;
  }

  private static class RotateShape extends HashCachingShape<RotateShape>
      implements Serializable {
    final Shape operand;
    final RotationMatrix theta;
    final Point center;

    RotateShape(Shape operand, double theta, Point center) {
      this.operand = operand;
      this.theta = new RotationMatrix(theta);
      this.center = center;
    }

    @Override public boolean contains(Point p) {
      return operand.contains(p.rotate(theta, center));
    }

    @Override public Shape shrink(double margin) {
      return operand.shrink(margin).rotate(theta.theta(), center);
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(7862627069926073335L)
          .putBytes(operand.hash().asBytes())
          .putDouble(theta.theta())
          .putDouble(center.x)
          .putDouble(center.y);
    }

    @Override public boolean doEquals(RotateShape that) {
      return operand.equals(that.operand) && theta.equals(that.theta) &&
          center.equals(that.center);
    }

    @Override public Shape rotate(double theta, Point center) {
      if (center.equals(this.center)) {
        return operand.rotate(theta + this.theta.theta(), center);
      }
      return super.rotate(theta, center);
    }

    @Override public String toString() {
      return operand + ".rotate(" + theta + ", " + center + ")";
    }

    private static final long serialVersionUID = 0;
  }

  private static class FlipXShape
      extends ShapeWithSerializationProxy<FlipXShape> {
    final Shape operand;
    final double x0;
    final double width;

    FlipXShape(Shape operand, double x0) {
      this.operand = operand;
      this.x0 = x0;
      width = 2 * x0;
    }

    @Override public Shape shrink(double margin) {
      return operand.shrink(margin).flipX(x0);
    }

    @Override public boolean contains(Point p) {
      return operand.contains(new Point(width - p.x, p.y));
    }

    @Override ImageFeatures features() {
      return operand.features().andRaster(Util.isInt(width));
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(-2330486238634929903L)
          .putBytes(operand.hash().asBytes())
          .putDouble(x0);
    }

    @Override public boolean doEquals(FlipXShape that) {
      return operand.equals(that.operand) && x0 == that.x0;
    }

    @Override public SerializationProxy<FlipXShape> doWriteReplace() {
      return serializationProxy(operand, x0);
    }

    private static SerializationProxy<FlipXShape> serializationProxy(
        final Shape operand, final double x0) {
      return new SerializationProxy<FlipXShape>() {
        @Override FlipXShape doReadResolve() {
          return new FlipXShape(operand, x0);
        }
      };
    }

    @Override public Shape flipX(double x0) {
      double d = x0 - this.x0;
      MathVector v = new MathVector(2 * d, 0);
      return operand.translate(v);
    }

    @Override public String toString() {
      return operand + ".flipX(" + x0 + ")";
    }

    private static final long serialVersionUID = 0;
  }

  private static class FlipYShape
      extends ShapeWithSerializationProxy<FlipYShape> {
    final Shape operand;
    final double y0;
    final double height;

    FlipYShape(Shape operand, double y0) {
      this.operand = operand;
      this.y0 = y0;
      height = 2 * y0;
    }

    @Override public Shape shrink(double margin) {
      return operand.shrink(margin).flipY(y0);
    }

    @Override public boolean contains(Point p) {
      return operand.contains(new Point(p.x, height - p.y));
    }

    @Override ImageFeatures features() {
      return operand.features().andRaster(Util.isInt(height));
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(-756341333718538774L)
          .putBytes(operand.hash().asBytes())
          .putDouble(y0);
    }

    @Override public boolean doEquals(FlipYShape that) {
      return operand.equals(that.operand) && y0 == that.y0;
    }

    @Override public SerializationProxy<FlipYShape> doWriteReplace() {
      return serializationProxy(operand, y0);
    }

    private static SerializationProxy<FlipYShape> serializationProxy(
        final Shape operand, final double y0) {
      return new SerializationProxy<FlipYShape>() {
        @Override FlipYShape doReadResolve() {
          return new FlipYShape(operand, y0);
        }
      };
    }

    @Override public Shape flipY(double y0) {
      double d = y0 - this.y0;
      MathVector v = new MathVector(0, d);
      return operand.translate(v);
    }

    @Override public String toString() {
      return operand + ".flipY(" + y0 + ")";
    }

    private static final long serialVersionUID = 0;
  }

  private static class RotateCwShape extends HashCachingShape<RotateCwShape>
      implements Serializable {
    final Shape operand;
    final int width;

    RotateCwShape(Shape operand, int width) {
      this.operand = operand;
      this.width = width;
    }

    @Override public Shape shrink(double margin) {
      return operand.shrink(margin).rotateCw(width);
    }

    @Override public boolean contains(Point p) {
      return operand.contains(new Point(p.y, width - p.x));
    }

    @Override ImageFeatures features() {
      ImageFeatures features = operand.features();
      return features.withXUniform(features.isYUniform())
          .withYUniform(features.isXUniform());
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(6557881680792826593L)
          .putBytes(operand.hash().asBytes())
          .putInt(width);
    }

    @Override public boolean doEquals(RotateCwShape that) {
      return operand.equals(that.operand) && width == that.width;
    }

    @Override public String toString() {
      return operand + ".rotateCw(" + width + ")";
    }

    private static final long serialVersionUID = 0;
  }

  private static class RotateCcwShape extends HashCachingShape<RotateCcwShape>
      implements Serializable {
    final Shape operand;
    final int height;

    RotateCcwShape(Shape operand, int height) {
      this.operand = operand;
      this.height = height;
    }

    @Override public Shape shrink(double margin) {
      return operand.shrink(margin).rotateCcw(height);
    }

    @Override public boolean contains(Point p) {
      return operand.contains(new Point(height - p.y, p.x));
    }

    @Override ImageFeatures features() {
      ImageFeatures features = operand.features();
      return features.withXUniform(features.isYUniform())
          .withYUniform(features.isXUniform());
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(-7576340017760234156L)
          .putBytes(operand.hash().asBytes())
          .putInt(height);
    }

    @Override public boolean doEquals(RotateCcwShape that) {
      return operand.equals(that.operand) && height == that.height;
    }

    @Override public String toString() {
      return operand + ".rotateCcw(" + height + ")";
    }

    private static final long serialVersionUID = 0;
  }

  private static class ReflectUpperHalfShape
      extends ShapeWithSerializationProxy<ReflectUpperHalfShape> {
    final Shape operand;
    final double y0;
    final double height;

    ReflectUpperHalfShape(Shape operand, double y0) {
      this.operand = operand;
      this.y0 = y0;
      height = 2 * y0;
    }

    @Override public Shape shrink(double margin) {
      return operand.shrink(margin).reflectUpperHalf(y0);
    }

    @Override public boolean contains(Point p) {
      if (y0 < p.y) {
        p = new Point(p.x, height - p.y);
      }
      return operand.contains(p);
    }

    @Override ImageFeatures features() {
      return operand.features().andRaster(Util.isInt(height));
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(571531769980922476L)
          .putBytes(operand.hash().asBytes())
          .putDouble(y0);
    }

    @Override public boolean doEquals(ReflectUpperHalfShape that) {
      return operand.equals(that.operand) && y0 == that.y0;
    }

    @Override public SerializationProxy<ReflectUpperHalfShape>
        doWriteReplace() {
      return serializationProxy(operand, y0);
    }

    private static SerializationProxy<ReflectUpperHalfShape> serializationProxy(
        final Shape operand, final double y0) {
      return new SerializationProxy<ReflectUpperHalfShape>() {
        @Override ReflectUpperHalfShape doReadResolve() {
          return new ReflectUpperHalfShape(operand, y0);
        }
      };
    }

    @Override public String toString() {
      return operand + ".reflectUpperHalf(" + y0 + ")";
    }

    private static final long serialVersionUID = 0;
  }

  private static class ReflectRightHalfShape
      extends ShapeWithSerializationProxy<ReflectRightHalfShape> {
    final Shape operand;
    final double x0;
    final double width;

    ReflectRightHalfShape(Shape operand, double x0) {
      this.operand = operand;
      this.x0 = x0;
      width = 2 * x0;
    }

    @Override public Shape shrink(double margin) {
      return operand.shrink(margin).reflectRightHalf(x0);
    }

    @Override public boolean contains(Point p) {
      if (x0 < p.x) {
        p = new Point(width - p.x, p.y);
      }
      return operand.contains(p);
    }


    @Override ImageFeatures features() {
      return operand.features().andRaster(Util.isInt(width));
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(-2824128027938947941L)
          .putBytes(operand.hash().asBytes())
          .putDouble(x0);
    }

    @Override public boolean doEquals(ReflectRightHalfShape that) {
      return operand.equals(that.operand) && x0 == that.x0;
    }

    @Override public SerializationProxy<ReflectRightHalfShape>
        doWriteReplace() {
      return serializationProxy(operand, x0);
    }

    private static SerializationProxy<ReflectRightHalfShape> serializationProxy(
        final Shape operand, final double x0) {
      return new SerializationProxy<ReflectRightHalfShape>() {
        @Override ReflectRightHalfShape doReadResolve() {
          return new ReflectRightHalfShape(operand, x0);
        }
      };
    }

    @Override public String toString() {
      return operand + ".reflectRightHalf(" + x0 + ")";
    }

    private static final long serialVersionUID = 0;
  }

  private static class ReflectLowerHalfShape
      extends ShapeWithSerializationProxy<ReflectLowerHalfShape> {
    final Shape operand;
    final double y0;
    final double height;

    ReflectLowerHalfShape(Shape operand, double y0) {
      this.operand = operand;
      this.y0 = y0;
      this.height = 2 * y0;
    }

    @Override public Shape shrink(double margin) {
      return operand.shrink(margin).reflectLowerHalf(y0);
    }

    @Override public boolean contains(Point p) {
      if (y0 < p.y) {
        p = new Point(p.x, height - p.y);
      }
      return operand.contains(p);
    }

    @Override ImageFeatures features() {
      return operand.features().andRaster(Util.isInt(height));
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(-5087666629635412916L)
          .putBytes(operand.hash().asBytes())
          .putDouble(y0);
    }

    @Override public boolean doEquals(ReflectLowerHalfShape that) {
      return operand.equals(that.operand) && y0 == that.y0;
    }

    @Override public SerializationProxy<ReflectLowerHalfShape>
        doWriteReplace() {
      return serializationProxy(operand, y0);
    }

    private static SerializationProxy<ReflectLowerHalfShape> serializationProxy(
        final Shape operand, final double y0) {
      return new SerializationProxy<ReflectLowerHalfShape>() {
        @Override ReflectLowerHalfShape doReadResolve() {
          return new ReflectLowerHalfShape(operand, y0);
        }
      };
    }

    @Override public String toString() {
      return operand + ".reflectLowerHalf(" + y0 + ")";
    }

    private static final long serialVersionUID = 0;
  }

  private static class ReflectLeftHalfShape
      extends ShapeWithSerializationProxy<ReflectLeftHalfShape> {
    final Shape operand;
    final double x0;
    final double width;

    ReflectLeftHalfShape(Shape operand, double x0) {
      this.operand = operand;
      this.x0 = x0;
      width = 2 * x0;
    }

    @Override public Shape shrink(double margin) {
      return operand.shrink(margin).reflectLeftHalf(x0);
    }

    @Override public boolean contains(Point p) {
      if (x0 < p.x) {
        p = new Point(width - p.x, p.y);
      }
      return operand.contains(p);
    }

    @Override ImageFeatures features() {
      return operand.features().andRaster(Util.isInt(width));
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(-5889441116598156468L)
          .putBytes(operand.hash().asBytes())
          .putDouble(x0);
    }

    @Override public boolean doEquals(ReflectLeftHalfShape that) {
      return operand.equals(that.operand) && x0 == that.x0;
    }

    @Override public SerializationProxy<ReflectLeftHalfShape>
        doWriteReplace() {
      return serializationProxy(operand, x0);
    }

    private static SerializationProxy<ReflectLeftHalfShape> serializationProxy(
        final Shape operand, final double x0) {
      return new SerializationProxy<ReflectLeftHalfShape>() {
        @Override ReflectLeftHalfShape doReadResolve() {
          return new ReflectLeftHalfShape(operand, x0);
        }
      };
    }

    @Override public String toString() {
      return operand + ".reflectLeft(" + x0 + ")";
    }

    private static final long serialVersionUID = 0;
  }
}
