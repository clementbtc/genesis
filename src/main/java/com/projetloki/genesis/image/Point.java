package com.projetloki.genesis.image;

import java.io.Serializable;

import com.google.common.base.Objects;

/**
 * An immutable point, located by its x- and y- coordinates in the plane. In the
 * field of computer graphics, points with a low y-coordinate are higher on the
 * screen than points with a greater y-coordinate.
 *
 * @author Clément Roux
 */
public final class Point implements Serializable {
  /** The point with coordinates (0, 0). */
  public static final Point ORIGIN = new Point(0, 0);

  final double x;
  final double y;

  /**
   * Constructs a point with the specified coordinates.
   * @param x the x-coordinate of the point
   * @param y the y-coordinate of the point
   */
  // Unlike MathVector and Color, not a good candidate for public static factory
  // method
  public Point(double x, double y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Returns the Euclidean distance between this point and {@code p}. The same
   * value would be obtained by extracting the norm of the vector connecting
   * the two points.
   * @param p a point
   * @return the Euclidean distance between the two points
   */
  public double distanceTo(Point p) {
    return MathVector.norm(x - p.x, y - p.y);
  }

  /**
   * Constructs a vector connecting this point and the given points.
   * @param endPoint the image of this point under the returned translation
   */
  public MathVector to(Point endPoint) {
    return new MathVector(endPoint.x - x, endPoint.y - y);
  }

  @Override public boolean equals(Object object) {
    if (object == this) {
      return true;
    } else if (object instanceof Point) {
      Point that = (Point)object;
      return x == that.x && y == that.y;
    } else {
      return false;
    }
  }

  /**
   * Returns the x-coordinate of this point.
   * @return the x-coordinate of this point
   */
  public double x() {
    return x;
  }

  /**
   * Returns the y-coordinate of this point.
   * @return the y-coordinate of this point
   */
  public double y() {
    return y;
  }

  @Override public int hashCode() {
    return Objects.hashCode(x, y) - 244838061;
  }

  /**
   * Returns the image of this point by the clockwise rotation through the angle
   * {@code theta} around the point {@code center}.
   *
   * <p>If you plan to rotate a great number of points through the same angle,
   * it is faster to construct a rotation matrix for this angle and call
   * {@link #rotate(RotationMatrix, Point)} instead.
   * </p>
   * @param theta in radians
   * @param center the rotation center
   * @return the image of {@code this} by the specified rotation
   */
  public Point rotate(double theta, Point center) {
    return rotate(new RotationMatrix(theta), center);
  }

  /**
   * Returns the image of this point by the clockwise rotation defined by the
   * matrix {@code m} around the point {@code center}.
   *
   * <p>If the rotation matrix is already constructed, this method is faster
   * than {@link #rotate(double, Point)}.</p>
   * @param m the rotation matrix: one for every angle
   * @param center the rotation center
   * @return the image of {@code this} by the specified rotation
   */
  Point rotate(RotationMatrix m, Point center) {
    double ox = x - center.x;
    double oy = y - center.y;
    double xx = m.a11() * ox + m.a12() * oy + center.x;
    double yy = m.a21() * ox + m.a22() * oy + center.y;
    return new Point(xx, yy);
  }

  @Override public String toString() {
    return "P(" + x + ", " + y + ")";
  }

  /**
   * Returns the translate of this point by the vector with coordinates
   * ({@code dx}, {@code dy})
   * @param dx the x-coordinate of the vector
   * @param dy the y-coordinate of the vector
   * @return the translate of {@code this} by the specified vector
   */
  public Point translate(double dx, double dy) {
    return new Point(x + dx, y + dy);
  }

  /**
   * Returns the translate of this point by the specified vector.
   * @param v the vector
   * @return the translate of {@code this} by the specified vector
   */
  public Point translate(MathVector v) {
    return new Point(x + v.dx, y + v.dy);
  }
  private static final long serialVersionUID = 0;
}
