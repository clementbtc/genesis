package com.projetloki.genesis.image;

import java.io.Serializable;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashCodes;
import com.google.common.hash.PrimitiveSink;

/**
 * Static utility methods pertaining to {@link Shape} instances.
 *
 * <p>All methods return serializable shapes as long as the arguments are
 * serializable.</p>
 *
 * @author Clément Roux
 */
public final class Shapes {
  /**
   * Returns a shape that does not contain any point. Equivalent of the empty
   * set in the set theory.
   */
  public static Shape empty() {
    return Empty.INSTANCE;
  }

  /**
   * Returns shape that contains all the points of the plane.
   */
  public static Shape plane() {
    return Plane.INSTANCE;
  }

  /**
   * Returns a shape bounded by the rectangle with the given dimensions and
   * top-left corner.
   * @param p0 the top-left corner
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   */
  public static Shape rectangle(Point p0, double width, double height) {
    return rectangle(p0.x, p0.y, width, height);
  }

  /**
   * Returns a shape bounded by the rectangle with the given dimensions and
   * top-left corner.
   * @param x0 the x-coordinate of the top-left corner
   * @param y0 the y-coordinate of the top-left corner
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   */
  public static Shape rectangle(double x0, double y0,
      double width, double height) {
    return new Rectangle(x0, y0, width, height).resolve();
  }

  /**
   * Returns a shape that contains all the points P(x, y) which verify y &lt;=
   * y0
   * @param y0 the y-coordinate of the points on the boundary
   */
  public static Shape upperHalfPlane(double y0) {
    return new UpperHalfPlane(y0);
  }

  /**
   * Returns a shape that contains all the points P(x, y) which verify x &gt;=
   * x0
   * @param x0 the x-coordinate of the points on the boundary
   */
  public static Shape rightHalfPlane(double x0) {
    return new RightHalfPlane(x0);
  }

  /**
   * Returns a shape that contains all the points P(x, y) which verify y &gt;=
   * y0
   * @param y0 the y-coordinate of the points on the boundary
   */
  public static Shape lowerHalfPlane(double y0) {
    return new LowerHalfPlane(y0);
  }

  /**
   * Returns a shape that contains all the points P(x, y) which verify x &lt;=
   * x0
   * @param x0 the x-coordinate of the points on the boundary
   */
  public static Shape leftHalfPlane(double x0) {
    return new LeftHalfPlane(x0);
  }

  /**
   * Returns the half-place on the right side of the given oriented line.
   * @param p0 a point on the line
   * @param p1 a second point on the line, that defines the orientation of the
   *     line. It must be different from the first point.
   */
  public static Shape halfPlane(Point p0, Point p1) {
    return halfPlane(p0, p0.to(p1));
  }

  /**
   * Returns the half-place on the right side of the given oriented line.
   * @param p0 a point on the line
   * @param v a vector that defines the direction and orientation of the line.
   *     It must not be the zero vector.
   */
  public static Shape halfPlane(Point p0, MathVector v) {
    Preconditions.checkNotNull(p0);
    Preconditions.checkArgument(!v.isZero(), "zero vector");
    if (v.dx == 0) {
      if (v.dy < 0) {
        return rightHalfPlane(p0.x);
      }
      return leftHalfPlane(p0.x);
    } else if (v.dy == 0) {
      if (v.dx < 0) {
        return upperHalfPlane(p0.y);
      }
      return lowerHalfPlane(p0.y);
    }
    return new HalfPlane(p0, v);
  }

  /**
   * Superimposes the given shapes. The union contains a point P if at least one
   * shape contain P.
   *
   * <p>Shrinking an union is equivalent to shrinking the shapes in the union.
   * </p>
   * @param shapes the shapes in the union
   * @return the union of all the shapes
   */
  public static Shape union(Iterable<? extends Shape> shapes) {
    return Union.create(ImmutableSet.copyOf(shapes));
  }

  /**
   * Superimposes the given shapes. The union contains a point P if at least one
   * shape contain P.
   *
   * <p>Shrinking an union is equivalent to shrinking the shapes in the union.
   * </p>
   * @param shapes the shapes in the union
   * @return the union of all the shapes
   */
  public static Shape union(Shape... shapes) {
    return Union.create(ImmutableSet.copyOf(shapes));
  }

  /**
   * Intersects the given shapes. The intersection contains a point P if all the
   * shapes contain P.
   *
   * <p>Shrinking an intersection is equivalent to shrinking the shapes in the
   * intersection.</p>
   * @param shapes the shapes in the intersection
   * @return the intersection of all the shapes
   */
  public static Shape intersection(Iterable<? extends Shape> shapes) {
    return Intersection.create(ImmutableSet.copyOf(shapes));
  }

  /**
   * Intersects the given shapes. The intersection contains a point P if all the
   * shapes contain P.
   *
   * <p>Shrinking an intersection is equivalent to shrinking the shapes in the
   * intersection.</p>
   * @param shapes the shapes in the intersection
   * @return the intersection of all the shapes
   */
  public static Shape intersection(Shape... shapes) {
    return Intersection.create(ImmutableSet.copyOf(shapes));
  }

  /**
   * Returns a shape bounded by the given ellipse. The ellipse is defined by a
   * bounding rectangle.
   * @param p0 the top-left corner
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   */
  public static Shape ellipse(Point p0, double width, double height) {
    return new Ellipse(p0.x, p0.y, width, height).resolve();
  }

  /**
   * Returns a shape bounded by the given ellipse. The ellipse is defined by a
   * bounding rectangle.
   * @param x0 the x-coordinate of the top-left corner
   * @param y0 the y-coordinate of the top-left corner
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   */
  public static Shape ellipse(double x0, double y0,
      double width, double height) {
    return new Ellipse(x0, y0, width, height).resolve();
  }

  /**
   * Returns a shape made of an infinite sequence of horizontal stripes.
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/vws5grqwie.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code Shapes.horizontalStripes(20, 15)}</td>
   *  </tr>
   * </table>
   * </p>
   * @param tileHeight the height of a horizontal tile. Equals the the height of
   *     a stripe plus the spacing between two stripes. Must be &gt;= 1
   * @param stripeHeight the height of a stripe. Must be positive and &lt;
   *     {@code tileHeight}
   */
  public static Shape horizontalStripes(int tileHeight, int stripeHeight) {
    Preconditions.checkArgument(stripeHeight >= 0, "stripeHeight: %s",
        stripeHeight);
    Preconditions.checkArgument(tileHeight >= Math.max(1, stripeHeight),
        "tileHeight: %s", tileHeight);
    return new HorizontalStripes(tileHeight, stripeHeight, 0);
  }

  /**
   * Returns a shape made of an infinite sequence of vertical stripes.
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/fkkntlegwm.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code Shapes.verticalStripes(20, 15)}</td>
   *  </tr>
   * </table>
   * </p>
   * @param tileWidth the width of a vertical tile. Equals the the width of a
   *     stripe plus the spacing between two stripes. Must be &gt;= 1
   * @param stripeWidth the width of a stripe. Must be positive and &lt;
   *     {@code tileWidth}
   */
  public static Shape verticalStripes(int tileWidth, int stripeWidth) {
    Preconditions.checkArgument(tileWidth >= 1, "tileWidth: %s", tileWidth);
    Preconditions.checkArgument(0 <= stripeWidth && stripeWidth <= tileWidth,
        "stripeWidth: %s", stripeWidth);
    return new HorizontalStripes(tileWidth, stripeWidth, 0).rotateCcw(0);
  }

  /**
   * Returns a shape made of an infinite sequence of parallel slanted stripes.
   * The surface in every tile is identical.
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/3mdyfu2mjq.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code Shapes.diagonalStripes(30, 20, 0.75, false)}</td>
   *  </tr>
   * </table>
   * </p>
   * @param tileWidth the width of the tile. Must be &gt;= 1
   * @param tileHeight the height of the tile. Must be &gt;= 1
   * @param areaRatio the area ratio. Must be in [0, 1]
   * @param topRight whether the stripes are oriented bottom-left to
   *     top-right (true) or top-left to bottom-right (false)
   */
  public static Shape diagonalStripes(int tileWidth, int tileHeight,
      double areaRatio, boolean topRight) {
    Preconditions.checkArgument(tileWidth >= 1, "tileWidth: %s", tileWidth);
    Preconditions.checkArgument(tileHeight >= 1, "tileHeight: %s", tileHeight);
    Preconditions.checkArgument(0 <= areaRatio && areaRatio <= 1,
        "areaRatio: %s", areaRatio);
    Shape result = new DiagonalStripes(tileWidth, tileHeight, areaRatio, 0);
    if (!topRight) {
      result = result.flipX(0);
    }
    return result;
  }

  // Base class for Empty and Plane
  private static abstract class Constant extends Shape implements Serializable {
    Constant() {}

    @Override public final Shape shrink(double margin) {
      return this;
    }

    @Override public final ImageFeatures features() {
      return ImageFeatures.start()
          .withRaster()
          .withXUniform()
          .withYUniform();
    }

    @Override public final Shape translate(MathVector v) {
      return this;
    }

    @Override public final Shape rotate(double theta, Point center) {
      return this;
    }

    @Override public final Shape flipX(double x0) {
      return this;
    }

    @Override public final Shape flipY(double y0) {
      return this;
    }

    @Override public Shape reflectUpperHalf(double y0) {
      return this;
    }

    @Override public Shape reflectRightHalf(double x0) {
      return this;
    }

    @Override public Shape reflectLowerHalf(double y0) {
      return this;
    }

    @Override public Shape reflectLeftHalf(double x0) {
      return this;
    }

    @Override Shape rotateCw(int width) {
      return this;
    }

    @Override Shape rotateCcw(int height) {
      return this;
    }
  }

  private static class Empty extends Constant {
    static final Empty INSTANCE = new Empty();

    @Override public boolean contains(Point p) {
      return false;
    }

    @Override public Shape negate() {
      return Plane.INSTANCE;
    }

    @Override public HashCode hash() {
      return HashCodes.fromLong(-3971682349212737902L);
    }

    @Override public String toString() {
      return "EMPTY";
    }

    // See singleton pattern and Java serialization
    private Object readResolve() {
      return INSTANCE;
    }

    private static final long serialVersionUID = 0;
  }

  private static class Plane extends Constant {
    static final Plane INSTANCE = new Plane();

    @Override public boolean contains(Point p) {
      return true;
    }

    @Override public Shape negate() {
      return Empty.INSTANCE;
    }

    @Override public HashCode hash() {
      return HashCodes.fromLong(5140383400602744647L);
    }

    @Override public String toString() {
      return "PLANE";
    }

    // See singleton pattern and Java serialization
    private Object readResolve() {
      return INSTANCE;
    }

    private static final long serialVersionUID = 0;
  }

  private static class Rectangle extends PossiblyEmptyShape<Rectangle> {
    final double x0;
    final double y0;
    final double width;
    final double height;
    final double x1;
    final double y1;

    Rectangle(double x0, double y0, double width, double height) {
      this.x0 = x0;
      this.y0 = y0;
      this.width = width;
      this.height = height;
      x1 = x0 + width;
      y1 = y0 + height;
    }

    @Override public boolean contains(Point p) {
      return x0 <= p.x && p.x <= x1 &&
          y0 <= p.y && p.y <= y1;
    }

    @Override public final Shape shrink(double margin) {
      return rectangle(x0 + margin, y0 + margin,
          width - 2 * margin, height - 2 * margin);
    }

    @Override public boolean isEmpty() {
      return width < 0 || height < 0;
    }

    @Override ImageFeatures features() {
      return ImageFeatures.start()
          .withRaster(Util.isInt(x0) && Util.isInt(y0) &&
              Util.isInt(x1) && Util.isInt(y1));
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(1952791314630612072L)
          .putDouble(x0)
          .putDouble(y0)
          .putDouble(width)
          .putDouble(height);
    }

    @Override public final boolean doEquals(Rectangle that) {
      return x0 == that.x0 && y0 == that.y0 &&
          width == that.width && height == that.height;
    }

    @Override final SerializationProxy<Shape> doWriteReplace() {
      return serializationProxy(x0, y0, width, height);
    }

    static SerializationProxy<Shape> serializationProxy(
        final double x0,
        final double y0,
        final double width,
        final double height) {
      return new SerializationProxy<Shape>() {
        @Override Shape doReadResolve() {
          return rectangle(x0, y0, width, height);
        }
        private static final long serialVersionUID = 0;
      };
    }

    @Override public Shape translate(MathVector v) {
      Point p = new Point(x0, y0).translate(v);
      return rectangle(p, width, height);
    }

    @Override public final String toString() {
      return String.format("rectangle(%s, %s, %s, %s)", x0, y0, width, height);
    }
  }

  private abstract static class UpperOrLowerHalfPlane
      extends HashCachingShape<UpperOrLowerHalfPlane> implements Serializable {
    final double y0;

    UpperOrLowerHalfPlane(double y0) {
      this.y0 = y0;
    }

    @Override public final boolean doEquals(UpperOrLowerHalfPlane that) {
      return y0 == that.y0;
    }

    private static final long serialVersionUID = 0;
  }

  private abstract static class RightOrLeftHalfPlane
      extends HashCachingShape<RightOrLeftHalfPlane> implements Serializable {
    final double x0;

    RightOrLeftHalfPlane(double x0) {
      this.x0 = x0;
    }

    @Override public final boolean doEquals(RightOrLeftHalfPlane that) {
      return x0 == that.x0;
    }

    private static final long serialVersionUID = 0;
  }

  private static class UpperHalfPlane extends UpperOrLowerHalfPlane {
    UpperHalfPlane(double y0) {
      super(y0);
    }

    @Override public boolean contains(Point p) {
      return p.y <= y0;
    }

    @Override public Shape shrink(double margin) {
      return new UpperHalfPlane(y0 - margin);
    }

    @Override void doHash(PrimitiveSink out) {
      out.putLong(6940153283648441748L);
      out.putDouble(y0);
    }

    @Override public Shape translate(MathVector v) {
      return upperHalfPlane(y0 + v.dy);
    }

    @Override public String toString() {
      return "upperHalfPlane(" + y0 + ")";
    }

    private static final long serialVersionUID = 0;
  }

  private static class RightHalfPlane extends RightOrLeftHalfPlane {
    RightHalfPlane(double x0) {
      super(x0);
    }

    @Override public boolean contains(Point p) {
      return p.x >= x0;
    }

    @Override public Shape shrink(double margin) {
      return new RightHalfPlane(x0 + margin);
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(1061456902679385197L)
          .putDouble(x0);
    }

    @Override public Shape translate(MathVector v) {
      return rightHalfPlane(x0 + v.dx);
    }

    @Override public String toString() {
      return "rightHalfPlane(" + x0 + ")";
    }

    private static final long serialVersionUID = 0;
  }

  private static class LowerHalfPlane extends UpperOrLowerHalfPlane {
    LowerHalfPlane(double y0) {
      super(y0);
    }

    @Override public boolean contains(Point p) {
      return p.y >= y0;
    }

    @Override public Shape shrink(double margin) {
      return new LowerHalfPlane(y0 + margin);
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(-454660672041429201L)
          .putDouble(y0);
    }

    @Override public Shape translate(MathVector v) {
      return lowerHalfPlane(y0 + v.dy);
    }

    @Override public String toString() {
      return "lowerHalfPlane(" + y0 + ")";
    }

    private static final long serialVersionUID = 0;
  }

  private static class LeftHalfPlane extends RightOrLeftHalfPlane {
    LeftHalfPlane(double x0) {
      super(x0);
    }

    @Override public boolean contains(Point p) {
      return p.x <= x0;
    }

    @Override public Shape shrink(double margin) {
      return new LeftHalfPlane(x0 - margin);
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(-3816074033315208362L)
          .putDouble(x0);
    }

    @Override public Shape translate(MathVector v) {
      return leftHalfPlane(x0 + v.dx);
    }

    @Override public String toString() {
      return "leftHalfPlane(" + x0 + ")";
    }

    private static final long serialVersionUID = 0;
  }

  private static class HalfPlane extends HashCachingShape<HalfPlane>
      implements Serializable {
    final Point p0;
    final MathVector v;

    HalfPlane(Point p0, MathVector v) {
      this.p0 = p0;
      this.v = v;
    }

    @Override public boolean contains(Point p) {
      return v.dy * (p.x - p0.x) <= v.dx * (p.y - p0.y);
    }

    @Override public HalfPlane shrink(double margin) {
      Point pp = p0.translate(v.rotateCw().scale(margin / v.norm()));
      return new HalfPlane(pp, v);
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(9208796929877327058L);
      sink.putDouble(p0.x);
      sink.putDouble(p0.y);
      sink.putDouble(v.dx);
      sink.putDouble(v.dy);
    }

    @Override public boolean doEquals(HalfPlane that) {
      return p0.equals(that.p0) && v.equals(that.v);
    }

    @Override public Shape translate(MathVector v) {
      return halfPlane(p0.translate(v), this.v);
    }

    // TODO: override #rotate

    @Override public String toString() {
      return "halfPlane(" + p0 + ", " + v + ")";
    }

    private static final long serialVersionUID = 0;
  }

  private static class Union extends HashCachingShape<Union>
      implements Serializable {
    static Shape create(ImmutableSet<Shape> shapes) {
      Shape result = Empty.INSTANCE;
      for (Shape shape : shapes.asList().reverse()) {
        if (shape == Plane.INSTANCE) {
          result = Plane.INSTANCE;
          break;
        } else if (shape != Empty.INSTANCE) {
          if (result == Empty.INSTANCE) {
            result = shape;
          } else {
            result = new Union(shape, result);
          }
        }
      }
      return result;
    }

    final Shape op1;
    final Shape op2;

    Union(Shape op1, Shape op2) {
      this.op1 = op1;
      this.op2 = op2;
    }

    @Override public boolean contains(Point p) {
      return op1.contains(p) || op2.contains(p);
    }

    @Override public Shape shrink(double margin) {
      return new Union(op1.shrink(margin), op2.shrink(margin));
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(8811864637890302065L)
          .putBytes(op1.hash().asBytes())
          .putBytes(op2.hash().asBytes());
    }

    @Override public boolean doEquals(Union that) {
      return op1.equals(that.op1) && op2.equals(that.op2);
    }

    @Override public String toString() {
      return "union(" + op1 + ", " + op2 + ")";
    }

    private static final long serialVersionUID = 0;
  }

  private static class Intersection extends HashCachingShape<Intersection>
      implements Serializable {
    static Shape create(ImmutableSet<Shape> shapes) {
      Shape result = Plane.INSTANCE;
      for (Shape shape : shapes.asList().reverse()) {
        if (shape == Empty.INSTANCE) {
          result = Empty.INSTANCE;
          break;
        } else if (shape != Plane.INSTANCE) {
          if (result == Plane.INSTANCE) {
            result = shape;
          } else {
            result = new Intersection(shape, result);
          }
        }
      }
      return result;
    }

    final Shape op1;
    final Shape op2;

    Intersection(Shape op1, Shape op2) {
      this.op1 = op1;
      this.op2 = op2;
    }

    @Override public boolean contains(Point p) {
      return op1.contains(p) && op2.contains(p);
    }

    @Override public Shape shrink(double margin) {
      return new Intersection(op1.shrink(margin), op2.shrink(margin));
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(3239096426107717246L)
          .putBytes(op1.hash().asBytes())
          .putBytes(op2.hash().asBytes());
    }

    @Override public boolean doEquals(Intersection that) {
      return op1.equals(that.op1) && op2.equals(that.op2);
    }

    @Override public String toString() {
      return "intersection(" + op1 + ", " + op2 + ")";
    }

    private static final long serialVersionUID = 0;
  }

  private static class Ellipse extends PossiblyEmptyShape<Ellipse> {
    final double x0;
    final double y0;
    final double width;
    final double height;
    final Shape rect;
    final double ox;
    final double oy;
    final double r2;
    final double k2;

    Ellipse(double x0, double y0, double width, double height) {
      this.x0 = x0;
      this.y0 = y0;
      this.width = width;
      this.height = height;
      rect = rectangle(x0, y0, width, height);
      ox = x0 + width / 2;
      oy = y0 + height / 2;
      r2 = width * width / 4;
      k2 = (width * width) / (height * height);
    }

    @Override public boolean contains(Point p) {
      if (!rect.contains(p)) {
        return false;
      }
      double dx = p.x - ox;
      double dy = p.y - oy;
      double dx2 = dx * dx;
      double dy2 = dy * dy;
      return dx2 + k2 * dy2 <= r2;
    }

    @Override public Shape shrink(double margin) {
      return ellipse(x0 + margin, y0 + margin,
          width - 2 * margin, height - 2 * margin);
    }

    @Override public boolean isEmpty() {
      return width < 0 || height < 0;
    }

    @Override public void doHash(PrimitiveSink sink) {
      sink.putLong(-2264135822281644928L)
          .putDouble(x0)
          .putDouble(y0)
          .putDouble(width)
          .putDouble(height);
    }

    @Override public boolean doEquals(Ellipse that) {
      return x0 == that.x0 && y0 == that.y0 &&
          width == that.width && height == that.height;
    }

    @Override final SerializationProxy<Shape> doWriteReplace() {
      return serializationProxy(x0, y0, width, height);
    }

    static SerializationProxy<Shape> serializationProxy(
        final double x0,
        final double y0,
        final double width,
        final double height) {
      return new SerializationProxy<Shape>() {
        @Override Shape doReadResolve() {
          return ellipse(x0, y0, width, height);
        }
        private static final long serialVersionUID = 0;
      };
    }

    @Override public Shape translate(MathVector v) {
      return ellipse(new Point(x0, y0).translate(v), width,height);
    }

    @Override public final String toString() {
      return String.format("ellipse(%s, %s, %s, %s)", x0, y0, width, height);
    }

    private static final long serialVersionUID = 0;
  }

  private static class HorizontalStripes
      extends ShapeWithSerializationProxy<HorizontalStripes> {
    final int tileHeight;
    final double stripeHeight;
    final double margin;
    final double yTop;
    final double yMiddle;
    final double yBottom;

    HorizontalStripes(int tileHeight, double stripeHeight, double margin) {
      this.stripeHeight = stripeHeight;
      this.tileHeight = tileHeight;
      this.margin = margin;
      yTop = margin;
      yMiddle = stripeHeight - margin;
      yBottom = tileHeight + margin;
    }

    @Override public boolean contains(Point p) {
      double yy = p.y % tileHeight;
      if (yy < 0) {
        yy = yy + tileHeight;
      }
      if (yy < yMiddle) {
        return yy >= yTop;
      }
      return yy >= yBottom;
    }

    @Override public Shape shrink(double margin) {
      return new HorizontalStripes(tileHeight, stripeHeight,
          margin + this.margin);
    }

    @Override ImageFeatures features() {
      return ImageFeatures.start()
          .withRaster(Util.isInt(tileHeight) && Util.isInt(margin))
          .withXUniform();
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(5012796988638948383L)
          .putInt(tileHeight)
          .putDouble(stripeHeight)
          .putDouble(margin);
    }

    @Override public boolean doEquals(HorizontalStripes that) {
      return tileHeight == that.tileHeight &&
          stripeHeight == that.stripeHeight && margin == that.margin;
    }

    @Override final SerializationProxy<HorizontalStripes> doWriteReplace() {
      return serializationProxy(tileHeight, stripeHeight, margin);
    }

    static SerializationProxy<HorizontalStripes> serializationProxy(
        final int tileHeight,
        final double stripeHeight,
        final double margin) {
      return new SerializationProxy<HorizontalStripes>() {
        @Override HorizontalStripes doReadResolve() {
          return new HorizontalStripes(tileHeight, stripeHeight, margin);
        }
        private static final long serialVersionUID = 0;
      };
    }

    @Override public String toString() {
      StringBuilder builder = new StringBuilder("horizontalStripes(")
          .append(tileHeight)
          .append(", ")
          .append(stripeHeight)
          .append(")");
      if (margin != 0) {
        builder.append(".shrink(").append(margin).append(")");
      }
      return builder.toString();
    }
  }

  private static class DiagonalStripes
      extends ShapeWithSerializationProxy<DiagonalStripes> {
    final int tileWidth;
    final int tileHeight;
    final double areaRatio;
    final double margin;
    final Shape m;
    final Shape m1;
    final Shape m2;

    DiagonalStripes(int tileWidth, int tileHeight, double areaRatio,
        double margin) {
      this.tileWidth = tileWidth;
      this.tileHeight = tileHeight;
      this.areaRatio = areaRatio;
      this.margin = margin;
      m = halfPlane(
          new Point(0, tileHeight),
          new Point(tileWidth, 0)).shrink(margin);
      Shape temp11 = halfPlane(
          new Point(tileWidth, areaRatio * tileHeight),
          new Point(0, areaRatio * tileHeight + tileHeight));
      Shape temp12 = halfPlane(
          new Point(tileWidth, tileHeight),
          new Point(0, 2d * tileHeight)).negate();
      m1 = union(temp11, margin >= 0 ? Empty.INSTANCE : temp12)
          .shrink(margin);
      Shape temp21 = halfPlane(
          new Point(tileWidth, areaRatio * tileHeight - tileHeight),
          new Point(0, areaRatio * tileHeight));
      Shape temp22 = halfPlane(
          Point.ORIGIN,
          new Point(-tileWidth, tileHeight)).negate();
      m2 = intersection(temp21, margin <= 0 ? Plane.INSTANCE : temp22)
          .shrink(margin);
    }

    @Override public boolean contains(Point p) {
      double xx = p.x % tileWidth;
      double yy = p.y % tileHeight;
      if (xx < 0) {
        xx = xx + tileWidth;
      }
      if (yy < 0) {
        yy = yy + tileHeight;
      }
      Point pp = new Point(xx, yy);
      if (m.contains(pp)) {
        return m1.contains(pp);
      }
      return m2.contains(pp);
    }

    @Override public Shape shrink(double margin) {
      return new DiagonalStripes(tileWidth, tileHeight, areaRatio,
              margin + this.margin);
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(-2590018369238746516L)
          .putInt(tileWidth)
          .putInt(tileHeight)
          .putDouble(areaRatio)
          .putDouble(margin);
    }

    @Override public boolean doEquals(DiagonalStripes that) {
      return tileWidth == that.tileWidth && tileHeight == that.tileHeight &&
          areaRatio == that.areaRatio && margin == that.margin;
    }

    @Override final SerializationProxy<DiagonalStripes> doWriteReplace() {
      return serializationProxy(tileWidth, tileHeight, areaRatio, margin);
    }

    static SerializationProxy<DiagonalStripes> serializationProxy(
        final int tileWidth,
        final int tileHeight,
        final double areaRatio,
        final double margin) {
      return new SerializationProxy<DiagonalStripes>() {
        @Override DiagonalStripes doReadResolve() {
          return new DiagonalStripes(tileWidth, tileHeight, areaRatio, margin);
        }
        private static final long serialVersionUID = 0;
      };
    }

    @Override public String toString() {
      StringBuilder builder = new StringBuilder("diagonalStripes(")
          .append(tileWidth)
          .append(", ")
          .append(tileHeight)
          .append(", ")
          .append(areaRatio)
          .append(")");
      if (margin != 0) {
        builder.append(".shrink(").append(margin).append(")");
      }
      return builder.toString();
    }
  }

  // To prevent instantiation
  private Shapes() {}
}
