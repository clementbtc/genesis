package com.projetloki.genesis.image;

import java.io.Serializable;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

/**
 * Static utility methods pertaining to {@link Gradient} instances.
 *
 * <p>All methods return serializable gradients.</p>
 *
 * @author Clément Roux
 */
public final class Gradients {
  /**
   * Returns a linear gradient with the specified direction.
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/q2sp7dnydm.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code Gradients.linear(Direction.RIGHT)}</td>
   *  </tr>
   * </table>
   * </p>
   * @param dir the direction of the gradient
   * @see #linear(double)
   * @see <a href="http://en.wikipedia.org/wiki/Color_gradient#Linear_gradients">http://en.wikipedia.org/wiki/Color_gradient#Linear_gradients</a>
   */
  public static Gradient linear(Direction dir) {
    switch (dir) {
      case TOP:
        return linear(-Math.PI / 2);
      case RIGHT:
        return linear(0);
      case BOTTOM:
        return linear(Math.PI / 2);
      case LEFT:
        return linear(Math.PI);
    }
    throw new NullPointerException();
  }

  /**
   * Returns a linear gradient with the specified direction, specified as an
   * angle from the vector (1, 0). Positive angles are measured clockwise, and
   * negative angles are measured anticlockwise.
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/kwzuvetkqm.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code Gradients.linear(Math.PI / 4)}</td>
   *  </tr>
   * </table>
   * </p>
   * @param theta in radians
   * @see <a href="http://en.wikipedia.org/wiki/Color_gradient#Linear_gradients">http://en.wikipedia.org/wiki/Color_gradient#Linear_gradients</a>
   */
  public static Gradient linear(double theta) {
    return new LinearGradient(theta);
  }

  /**
   * Returns a circular gradient. Colors are calculated by linear interpolation
   * based on distance from the focus. The coordinates of the focus are
   * expressed as a ratio of the width/height of the image.
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/jpkcsm4hn4.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code Gradients.circular(0.5, -1)}</td>
   *  </tr>
   * </table>
   * </p>
   * @param xRatio the number to multiply by the width of the image to obtain
   *     the x-coordinate of the focus. Does not have to be between 0 and 1.
   * @param yRatio the number to multiply by the height of the image to obtain
   *     the y-coordinate of the focus. Does not have to be between 0 and 1.
   * @see <a href="http://en.wikipedia.org/wiki/Color_gradient#Circular_gradients">http://en.wikipedia.org/wiki/Color_gradient#Circular_gradients</a>
   */
  public static Gradient circular(double xRatio, double yRatio) {
    return new CircularGradient(xRatio, yRatio);
  }

  private static class LinearGradient extends Gradient implements Serializable {
    final RotationMatrix m;

    LinearGradient(double theta) {
      m = new RotationMatrix(theta);
    }

    @Override public DensityMap getDensityMap(int width, int height) {
      return new LinearDensityMap(width, height, m);
    }

    @Override ImageFeatures features() {
      return ImageFeatures.start()
          .withXUniform(m.cos() == 0)
          .withYUniform(m.sin() == 0);
    }

    @Override public HashCode hash() {
      return Hashing.murmur3_128().newHasher()
          .putLong(-4341565584208373176L)
          .putDouble(m.theta())
          .hash();
    }

    @Override public boolean equals(Object object) {
      if (object == this) {
        return true;
      } else if (object instanceof LinearGradient) {
        LinearGradient that = (LinearGradient) object;
        return m.equals(that.m);
      }
      return false;
    }

    @Override public String toString() {
      return "linear(" + m.theta() + ")";
    }
    private static final long serialVersionUID = 0;
  }

  private static class LinearDensityMap implements Gradient.DensityMap {
    final double a;
    final double b;
    final double c;
    final double maxDistance;

    LinearDensityMap(int width, int height, RotationMatrix m) {
      a = m.cos();
      b = m.sin();
      double cornerX = (a < 0) ? width : 0;
      double cornerY = (b < 0) ? height : 0;
      Point corner = new Point(cornerX, cornerY);
      Point opp = new Point(width - cornerX, height - cornerY);
      c = -(a * corner.x + b * corner.y);
      maxDistance = Math.abs(a * opp.x + b * opp.y + c);
    }

    @Override public double getDensity(Point p) {
      return Math.abs(a * p.x + b * p.y + c) / maxDistance;
    }
  }

  private static class CircularGradient extends Gradient
      implements Serializable {
    final double xRatio;
    final double yRatio;

    CircularGradient(double xRatio, double yRatio) {
      this.xRatio = xRatio;
      this.yRatio = yRatio;
    }

    @Override public DensityMap getDensityMap(int width, int height) {
      return new CircularDensityMap(width, height, xRatio, yRatio);
    }

    @Override public HashCode hash() {
      return Hashing.murmur3_128().newHasher()
          .putLong(-6474485075633576565L)
          .putDouble(xRatio)
          .putDouble(yRatio)
          .hash();
    }

    @Override public boolean equals(Object object) {
      if (object == this) {
        return true;
      } else if (object instanceof CircularGradient) {
        CircularGradient that = (CircularGradient) object;
        return xRatio == that.xRatio && yRatio == that.yRatio;
      }
      return false;
    }

    @Override public String toString() {
      return "circular(" + xRatio + ", " + yRatio + ")";
    }
    private static final long serialVersionUID = 0;
  }

  private static class CircularDensityMap implements Gradient.DensityMap {
    final double minDistance;
    final Point center;
    final double delta;

    public CircularDensityMap(int width, int height,
        double xRatio, double yRatio) {
      center = new Point(xRatio * width, yRatio * height);
      Point closest = new Point(
          Math.max(0, Math.min(1, xRatio)) * width,
          Math.max(0, Math.min(1, yRatio)) * height);
      minDistance = closest.distanceTo(center);
      ImmutableList<Point> corners = ImmutableList.of(
          Point.ORIGIN,
          new Point(width, 0),
          new Point(0, height),
          new Point(width, height));
      double maxDistance = Double.MIN_VALUE;
      for (Point corner : corners) {
        double distance = corner.distanceTo(center);
        maxDistance = Math.max(maxDistance, distance);
      }
      delta = maxDistance - minDistance;
    }

    @Override public double getDensity(Point p) {
      return 1d - (p.distanceTo(center) - minDistance) / delta;
    }
  }

  // To prevent instantiation
  private Gradients() {}
}
