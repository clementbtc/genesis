package com.projetloki.genesis.image;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.Serializable;

import com.google.common.hash.HashCode;
import com.google.common.hash.PrimitiveSink;

/**
 * Static utility methods pertaining to {@link Shaper} instances.
 *
 * <p>All methods return serializable shapers as long as the arguments are
 * serializable.</p>
 *
 * @author Clément Roux
 */
public final class Shapers {
  private static final Shaper BOX = new RoundedBox(0, false, false, false,
      false);

  /**
   * Returns a shaper that generates an arrow made of a rectangle and
   * an isosceles rectangle pointing to the right. Call {@link Shaper#flipX()},
   * {@link Shaper#rotateCcw()} or {@link Shaper#rotateCw()} to have the arrow
   * point to another direction.
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/3io46avmwu.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code Shapers.arrow(40, 40)}</td>
   *  </tr>
   * </table>
   * </p>
   * @param bodyWidth the width of the rectangle. Must be positive.
   * @param bodyHeight the height of the rectangle. Must be positive.
   */
  public static Shaper arrow(double bodyWidth, double bodyHeight) {
    checkArgument(bodyWidth >= 0, "bodyWidth: %s", bodyWidth);
    checkArgument(bodyHeight >= 0, "bodyHeight: %s", bodyHeight);
    return new Arrow(bodyWidth, bodyHeight);
  }

  /**
   * Returns a shaper that generates a rectangle.
   * @see #roundedBox(double)
   * @see #roundedBox(double, boolean, boolean, boolean, boolean)
   */
  public static Shaper box() {
    return BOX;
  }

  /**
   * Returns a shaper that generates a rectangle with rounded corners.
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/nwabbuexby.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code Shapers.roundedBox(20)}</td>
   *  </tr>
   * </table>
   * </p>
   * @param radius for the corners. Must be positive.
   */
  public static Shaper roundedBox(double radius) {
    return roundedBox(radius, true, true, true, true);
  }

  /**
   * Returns a shaper that generates a rectangle with rounded corners. The
   * boolean parameters let you specify which corners are to be rounded.
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/usxcsgpjzm.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code Shapers.roundedBox(20, true, false, false, true)}</td>
   *  </tr>
   * </table>
   * </p>
   * @param radius for the corners. Must be positive.
   * @param topRight whether the top-right corner is to be rounded
   * @param bottomRight whether the bottom-right corner is to be rounded
   * @param bottomLeft whether the bottom-left corner is to be rounded
   * @param topLeft whether the top-left corner is to be rounded
   */
  public static Shaper roundedBox(double radius, boolean topRight,
      boolean bottomRight, boolean bottomLeft, boolean topLeft) {
    checkArgument(radius >= 0d, "radius: %s", radius);
    return new RoundedBox(radius, topRight, bottomRight, bottomLeft, topLeft);
  }

  /**
   * Returns a shaper that generates a five-pointed star. This shaper can only
   * be applied to a square image.
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/sgxqcnllrq.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code Shapers.star(0.45)}</td>
   *  </tr>
   * </table>
   * </p>
   * @param spokeRatio the spoke ratio, in [0, 1]. If == 0, the shape is empty.
   *     If == 1, the shape is a ten-pointed regular polygon. A value for a
   *     good-looking star is between 0.4 and 0.5
   */
  public static Shaper star(double spokeRatio) {
    checkArgument(0 <= spokeRatio && spokeRatio <= 1,
        "spokeRatio: %s", spokeRatio);
    return new Star(spokeRatio);
  }

  /**
   * Returns a shaper that generates a tail pointing to the bottom. The result
   * looks like a speech balloon, except that the bubble is not a closed surface
   * but an upper half-plane. Call {@link Shaper#flipY()},
   * {@link Shaper#rotateCcw()} or {@link Shaper#rotateCw()} to have the tail
   * point to another direction.
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/vous6krmgq.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code Shapers.tail(40, 30, 60, 20)}</td>
   *  </tr>
   * </table>
   * </p>
   * @param baseLeftX the x-coordinate of the left end of the tail base
   * @param tipX the x-coordinate of the tip of the tail
   * @param baseRightX the x-coordinate of the right end of the tail base. Must
   *     be greater than {@code baseLeftX}
   * @param tailHeight the height of the tail. Must be positive.
   * @see #balloon(double, double, double, int, Shaper)
   */
  public static Shaper tail(double baseLeftX, double tipX, double baseRightX,
      int tailHeight) {
    checkTail(baseLeftX, tipX, baseRightX, tailHeight);
    return new Balloon(baseLeftX, tipX, baseRightX, tailHeight,
        Planer.INSTANCE);
  }

  /**
   * Returns a shaper that generates a speech balloon made of a box on top of a
   * tail pointing to the bottom. Call {@link Shaper#flipY()},
   * {@link Shaper#rotateCcw()} or {@link Shaper#rotateCw()} to have the tail
   * point to another direction.
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/kyi7noe4di.png"/></td>
   *  </tr>
   *  <tr>
   *   <td><pre><code class="legend">Shapers.balloon(40, 30, 60, 20,
   *    Shapers.roundedBox(20))</code></pre></td>
   *  </tr>
   * </table>
   * </p>
   * @param baseLeftX the x-coordinate of the left end of the tail base
   * @param tipX the x-coordinate of the tip of the tail
   * @param baseRightX the x-coordinate of the right end of the tail base. Must
   *     be greater than {@code baseLeftX}
   * @param tailHeight the height of the tail. Must be positive.
   * @param box the shaper to generate the top box. Typically the result of
   *     {@link #box()}, {@link #roundedBox(double)} or
   *     {@link #roundedBox(double, boolean, boolean, boolean, boolean)}
   */
  public static Shaper balloon(double baseLeftX, double tipX, double baseRightX,
      int tailHeight, Shaper box) {
    checkTail(baseLeftX, tipX, baseRightX, tailHeight);
    return new Balloon(baseLeftX, tipX, baseRightX, tailHeight, box);
  }

  /**
   * Returns a shaper that generates a V.
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/kwmngx4l4a.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code Shapers.vee(20)}</td>
   *  </tr>
   * </table>
   * </p>
   * @param baseWidth the width of the base of each arm. Must be positive.
   */
  public static Shaper vee(double baseWidth) {
    checkArgument(baseWidth >= 0, "baseWidth: %s", baseWidth);
    return new Vee(baseWidth);
  }

  /**
   * Returns a shaper that generates a plus sign.
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/jhkrfugfn4.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code Shapers.plusSign(20)}</td>
   *  </tr>
   * </table>
   * </p>
   * @param barWidth the width of each bar. Must be positive.
   */
  public static Shaper plusSign(double barWidth) {
    checkArgument(barWidth >= 0, "barWidth: %s", barWidth);
    return new PlusSign(barWidth);
  }

  /**
   * Returns a shaper that generates a minus sign.
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/rvbo56xyme.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code Shapers.minusSign(20)}</td>
   *  </tr>
   * </table>
   * </p>
   * @param barWidth the width of the bar. Must be positive.
   */
  public static Shaper minusSign(double barWidth) {
    checkArgument(barWidth >= 0, "barWidth: %s", barWidth);
    return new MinusSign(barWidth);
  }

  /**
   * Returns a shaper that generates an X. This shaper can only be applied to a
   * square image.
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/zcdpeime5a.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code Shapers.ex(20)}</td>
   *  </tr>
   * </table>
   * </p>
   * @param barWidth the width of each bar. Must be positive.
   */
  public static Shaper ex(double barWidth) {
    checkArgument(barWidth >= 0, "barWidth: %s", barWidth);
    return new Ex(barWidth);
  }

  private static void checkTail(double baseLeftX, double tipX,
      double baseRightX, int tailHeight) {
    checkArgument(baseLeftX <= baseRightX,
        "baseLeftX: %s, baseRightX: %s", baseLeftX, baseRightX);
    checkArgument(tailHeight >= 0, "tailHeight: %s", tailHeight);
    checkArgument(baseLeftX != tipX || tailHeight != 0,
        "baseLeftX == tipX so tailHeight cannot be 0");
    checkArgument(baseRightX != tipX || tailHeight != 0,
        "baseRightX == tipX so tailHeight cannot be 0");
  }

  static void checkSize(int width, int height) {
    checkArgument(width >= 0, "width: %s", width);
    checkArgument(height >= 0, "height: %s", height);
  }

  private static class Arrow extends HashCachingShaper<Arrow>
      implements Serializable {
    final double bodyWidth;
    final double bodyHeight;

    Arrow(double bodyWidth, double bodyHeight) {
      this.bodyWidth = bodyWidth;
      this.bodyHeight = bodyHeight;
    }

    @Override public Shape getShape(int width, int height) {
      checkSize(width, height);
      checkArgument(height != 0, "height: 0");
      double y = (height - bodyHeight) / 2;
      Point p1 = new Point(bodyWidth, 0);
      Point p2 = new Point(width, height / 2d);
      Point p3 = new Point(bodyWidth, height);
      return Shapes.intersection(
          Shapes.halfPlane(p1, p2),
          Shapes.halfPlane(p2, p3),
          Shapes.union(
              Shapes.intersection(
                  Shapes.lowerHalfPlane(y),
                  Shapes.rightHalfPlane(0),
                  Shapes.upperHalfPlane(height - y)),
              Shapes.rightHalfPlane(bodyWidth)));
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(-7920111574516184006L)
          .putDouble(bodyWidth)
          .putDouble(bodyHeight);
    }

    @Override public boolean doEquals(Arrow that) {
      return bodyWidth == that.bodyWidth && bodyHeight == that.bodyHeight;
    }

    @Override public String toString() {
      return "arrow(" + bodyWidth + ", " + bodyHeight + ")";
    }
    private static final long serialVersionUID = 0;
  }

  private static class RoundedBox extends HashCachingShaper<RoundedBox>
      implements Serializable {
    final double radius;
    final boolean topRight;
    final boolean bottomRight;
    final boolean bottomLeft;
    final boolean topLeft;

    RoundedBox(double radius, boolean topRight, boolean bottomRight,
        boolean bottomLeft, boolean topLeft) {
      this.radius = radius;
      this.topRight = topRight;
      this.bottomRight = bottomRight;
      this.bottomLeft = bottomLeft;
      this.topLeft = topLeft;
    }

    @Override public Shape getShape(int width, int height) {
      checkSize(width, height);
      if (radius == 0 || !(topRight || bottomRight || bottomLeft || topLeft)) {
        return Shapes.rectangle(0, 0, width, height);
      }
      return new RoundedBoxShape(this, width, height, 0);
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(5086852804176593283L)
          .putDouble(radius)
          .putBoolean(topRight)
          .putBoolean(bottomRight)
          .putBoolean(bottomLeft)
          .putBoolean(topLeft);
    }

    @Override public boolean doEquals(RoundedBox that) {
      return radius == that.radius && topRight == that.topRight &&
          bottomRight == that.bottomRight && bottomLeft == that.bottomLeft &&
          topLeft == that.topLeft;
    }

    @Override public String toString() {
      return String.format("roundedBox(%s, %s, %s, %s, %s)",
          radius, topRight, bottomRight, bottomLeft, topLeft);
    }
    private static final long serialVersionUID = 0;
  }

  private static class RoundedBoxShape
      extends ShapeWithShaperAsSerializationProxy {
    final Shape boundingBox;
    final Shape upper;
    final Shape right;
    final Shape lower;
    final Shape left;
    final Shape topRightDisk;
    final Shape bottomRightDisk;
    final Shape bottomLeftDisk;
    final Shape topLeftDisk;

    RoundedBoxShape(RoundedBox shaper, int width, int height,
        double margin) {
      super(shaper, width, height, margin);
      double radius = shaper.radius;
      boundingBox = Shapes.rectangle(0, 0, width, height).shrink(margin);
      upper = Shapes.upperHalfPlane(radius);
      right = Shapes.rightHalfPlane(width - radius);
      lower = Shapes.lowerHalfPlane(height - radius);
      left = Shapes.leftHalfPlane(radius);
      double d = 2 * radius;
      topRightDisk = (shaper.topRight
          ? Shapes.ellipse(new Point(width - d, 0), d, d)
          : Shapes.plane()).shrink(margin);
      bottomRightDisk = (shaper.bottomRight
          ? Shapes.ellipse(new Point(width - d, height - d), d, d)
          : Shapes.plane()).shrink(margin);
      bottomLeftDisk = (shaper.bottomLeft
          ? Shapes.ellipse(new Point(0, height - d), d, d)
          : Shapes.plane()).shrink(margin);
      topLeftDisk = (shaper.topLeft
          ? Shapes.ellipse(new Point(0, 0), d, d)
          : Shapes.plane()).shrink(margin);
    }

    @Override public boolean contains(Point p) {
      if (boundingBox.contains(p)) {
        if (upper.contains(p)) {
          if (right.contains(p) && !topRightDisk.contains(p)) {
            return false;
          }
          if (left.contains(p) && !topLeftDisk.contains(p)) {
            return false;
          }
        }
        if (lower.contains(p)) {
          if (right.contains(p) && !bottomRightDisk.contains(p)) {
            return false;
          }
          if (left.contains(p) && !bottomLeftDisk.contains(p)) {
            return false;
          }
        }
        return true;
      }
      return false;
    }

    @Override public Shape shrink(double margin) {
      return new RoundedBoxShape((RoundedBox) shaper,
          width, height, margin + this.margin);
    }
  }

  private static class Star extends HashCachingShaper<Star>
      implements Serializable {
    final double spokeRatio;

    Star(double spokeRatio) {
      this.spokeRatio = spokeRatio;
    }

    @Override public Shape getShape(int width, int height) {
      checkSize(width, height);
      checkArgument(width == height,
          "image must be square (width: %s, height: %s)", width, height);
      return new StarShape(this, width, 0);
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(-6499699372027705921L)
          .putDouble(spokeRatio);
    }

    @Override public boolean doEquals(Star that) {
      return spokeRatio == that.spokeRatio;
    }

    @Override public String toString() {
      return "star(" + spokeRatio + ")";
    }
    private static final long serialVersionUID = 0;
  }

  private static class StarShape extends ShapeWithShaperAsSerializationProxy {
    private static final MathVector[] RADIUS_VECTORS =
        new MathVector[10];
    static {
      for (int i = 0; i < 5; i++) {
        RADIUS_VECTORS[i] = MathVector.J.rotate(i * Math.PI / 5);
      }
      for (int i = 0; i < 5; i++) {
        RADIUS_VECTORS[i + 5] = RADIUS_VECTORS[i].negate();
      }
    }
    final Shape[] halfDisks;
    final Shape[] sides;

    StarShape(Star shaper, int width, double margin) {
      super(shaper, width, width, margin);
      Point center = new Point(width / 2d, width / 2d);
      halfDisks = new Shape[10];
      for (int i = 0; i < 10; i++) {
        halfDisks[i] = Shapes.halfPlane(center, RADIUS_VECTORS[i].negate());
      }
      MathVector[] sideVectors = new MathVector[10];
      for (int i = 0; i < 10; i++) {
        MathVector oa = RADIUS_VECTORS[i];
        MathVector ob = RADIUS_VECTORS[(i + 1) % 10];
        if (i % 2 == 0) {
          oa = oa.scale(shaper.spokeRatio);
        } else {
          ob = ob.scale(shaper.spokeRatio);
        }
        MathVector ab = ob.minus(oa);
        sideVectors[i] = ab;
      }
      sides = new Shape[10];
      for (int i = 0; i < 5; i++) {
        int before = 2 * i;
        int after = 2 * i + 1;
        Point p = center.translate(RADIUS_VECTORS[after].scale(width / 2d));
        Shape m1 = Shapes.halfPlane(p, sideVectors[before]);
        Shape m2 = Shapes.halfPlane(p, sideVectors[after]);
        sides[before] = m1.shrink(margin);
        sides[after] = m2.shrink(margin);
      }
    }

    @Override public boolean contains(Point p) {
      int start = 0;
      int stop = 10;
      while (start != stop - 1) {
        int mean = (start + stop) / 2;
        if (halfDisks[mean].contains(p)) {
          stop = mean;
        } else {
          start = mean;
        }
      }
      return sides[start].contains(p);
    }

    @Override public Shape shrink(double margin) {
      return new StarShape((Star) shaper, width, margin + this.margin);
    }
  }

  private static class Balloon extends HashCachingShaper<Balloon>
      implements Serializable {
    final double baseLeftX;
    final double tipX;
    final double baseRightX;
    final int tailHeight;
    final Shaper box;

    Balloon(double baseLeftX, double tipX, double baseRightX,
        int tailHeight, Shaper box) {
      this.baseLeftX = baseLeftX;
      this.tipX = tipX;
      this.baseRightX = baseRightX;
      this.tailHeight = tailHeight;
      this.box = box;
    }

    @Override public Shape getShape(int width, int height) {
      checkSize(width, height);
      return new BalloonShape(this, width, height, 0);
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(3050268605533387314L)
          .putDouble(baseLeftX)
          .putDouble(tipX)
          .putDouble(baseRightX)
          .putInt(tailHeight)
          .putBytes(box.hash().asBytes());
    }

    @Override public boolean doEquals(Balloon that) {
      return baseLeftX == that.baseLeftX && tipX == that.tipX &&
          baseRightX == that.baseRightX && tailHeight == that.tailHeight &&
          box.equals(that.box);
    }

    @Override public String toString() {
      if (box == Planer.INSTANCE) {
        return String.format("tail(%s, %s, %s, %s)",
            baseLeftX, tipX, baseRightX, tailHeight);
      }
      return String.format("balloon(%s, %s, %s, %s, %s)",
          baseLeftX, tipX, baseRightX, tailHeight, box);
    }
    private static final long serialVersionUID = 0;
  }

  private static class BalloonShape
      extends ShapeWithShaperAsSerializationProxy {
    final double y0;
    final Shape tail;
    final Shape box;

    BalloonShape(Balloon shaper, int width, int height, double margin) {
      super(shaper, width, height, margin);
      y0 = height - shaper.tailHeight - margin;
      Point baseLeft = new Point(shaper.baseLeftX, height - shaper.tailHeight);
      Point tip = new Point(shaper.tipX, height);
      Point baseRight = new Point(shaper.baseRightX,
          height - shaper.tailHeight);
      tail = Shapes.intersection(
          Shapes.halfPlane(baseRight, tip),
          Shapes.halfPlane(tip, baseLeft)).shrink(margin);
      this.box = shaper.box.getShape(width, height - shaper.tailHeight)
          .shrink(margin);
    }

    @Override public boolean contains(Point p) {
      if (p.y <= y0) {
        return box.contains(p);
      }
      return tail.contains(p);
    }

    @Override public Shape shrink(double margin) {
      return new BalloonShape((Balloon) shaper, width, height,
          margin + this.margin);
    }
  }

  private static class Vee extends HashCachingShaper<Vee>
      implements Serializable {
    final double baseWidth;

    Vee(double baseWidth) {
      this.baseWidth = baseWidth;
    }

    @Override public Shape getShape(int width, int height) {
      checkSize(width, height);
      Point p0 = new Point(width / 2d, height);
      Point p1 = new Point(baseWidth, 0);
      Point p2 = new Point(width / 2d + baseWidth, height);
      Shape temp = Shapes.intersection(
          Shapes.lowerHalfPlane(0),
          Shapes.upperHalfPlane(height),
          Shapes.halfPlane(p0, Point.ORIGIN),
          Shapes.halfPlane(p1, p2));
      return temp.reflectLeftHalf(width / 2d);
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(7537216165633146786L)
          .putDouble(baseWidth);
    }

    @Override public boolean doEquals(Vee that) {
      return baseWidth == that.baseWidth;
    }

    @Override public String toString() {
      return "vee(" + baseWidth + ")";
    }
    private static final long serialVersionUID = 0;
  }

  private static class PlusSign extends HashCachingShaper<PlusSign>
      implements Serializable {
    final double barWidth;

    PlusSign(double barWidth) {
      this.barWidth = barWidth;
    }

    @Override public Shape getShape(int width, int height) {
      checkSize(width, height);
      Point p0 = new Point((width - barWidth) / 2d, 0);
      Point p1 = new Point(0, (height - barWidth) / 2d);
      return Shapes.union(
          Shapes.rectangle(p0, barWidth, height),
          Shapes.rectangle(p1, width, barWidth));
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(3710260803064652562L)
          .putDouble(barWidth);
    }

    @Override public boolean doEquals(PlusSign that) {
      return barWidth == that.barWidth;
    }

    @Override public String toString() {
      return "plusSign(" + barWidth + ")";
    }
    private static final long serialVersionUID = 0;
  }

  private static class MinusSign extends HashCachingShaper<MinusSign>
      implements Serializable {
    final double barWidth;

    MinusSign(double barWidth) {
      this.barWidth = barWidth;
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(62272518503265731L)
          .putDouble(barWidth);
    }

    @Override public Shape getShape(int width, int height) {
      checkSize(width, height);
      Point p = new Point(0, (height - barWidth) / 2d);
      return Shapes.rectangle(p, width, barWidth);
    }

    @Override public boolean doEquals(MinusSign that) {
      return barWidth == that.barWidth;
    }

    @Override public String toString() {
      return "minusSign(" + barWidth + ")";
    }
    private static final long serialVersionUID = 0;
  }

  private static class Ex extends HashCachingShaper<Ex>
      implements Serializable {
    final double barWidth;

    Ex(double barWidth) {
      this.barWidth = barWidth;
    }

    @Override public Shape getShape(int width, int height) {
      checkSize(width, height);
      checkArgument(width == height,
          "image must be square (width: %s, height: %s)", width, height);
      double x0 = barWidth * Math.sqrt(2) / 2;
      double x1 = width - x0;
      Shape temp = Shapes.intersection(
          Shapes.halfPlane(new Point(x1, height), new Point(0, x0)),
          Shapes.halfPlane(new Point(0, x0), new Point(x0, 0)),
          Shapes.halfPlane(new Point(x0, 0), new Point(width, x1)));
      return temp.reflectLeftHalf(width / 2d)
          .reflectUpperHalf(width / 2d);
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(-7802307591352988644L)
          .putDouble(barWidth);
    }

    @Override public boolean doEquals(Ex that) {
      return barWidth == that.barWidth;
    }

    @Override public String toString() {
      return "ex(" + barWidth + ")";
    }
    private static final long serialVersionUID = 0;
  }

  private static class Planer extends Shaper implements Serializable {
    static final Planer INSTANCE = new Planer();

    @Override public Shape getShape(int width, int height) {
      return Shapes.plane();
    }

    @Override public HashCode hash() {
      return HashCode.fromLong(6022702161637027635L);
    }

    private Object readResolve() {
      return INSTANCE;
    }
  }

  // To prevent instantiation
  private Shapers() {}
}
