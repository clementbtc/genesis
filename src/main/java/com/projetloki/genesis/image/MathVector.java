package com.projetloki.genesis.image;

import java.io.Serializable;

import com.google.common.base.Function;
import com.google.common.base.Objects;

/**
 * An immutable geometric vector, represented by its x- and y- coordinates in
 * the plane. Not to be confused with the collection vector.
 *
 * @author Clément Roux
 */
public final class MathVector implements Function<Point, Point>, Serializable {
  /** The vector with coordinates (1, 0). */
  public static final MathVector I = new MathVector(1d, 0d);

  /** The vector with coordinates (0, 1). */
  public static final MathVector J = new MathVector(0d, 1d);

  /** The vector with coordinates (0, 0). */
  public static final MathVector ZERO = new MathVector(0d, 0d);

  final double dx;
  final double dy;

  /**
   * Constructs a vector with the specified coordinates.
   * @param dx the x-coordinate of this vector
   * @param dy the y-coordinate of this vector
   * @see Point#to(Point) to construct a vector from two points
   */
  // Public static factory method instead of public constructor so we have the
  // freedom to make this class abstract later
  // See Josh Blosh's Effective Java, item 1
  public static MathVector of(double dx, double dy) {
    return new MathVector(dx, dy);
  }

  // Calling this constructor directly is probably slightly faster than calling
  // #of, so let's make it package-private.
  // Not sure we should keep this optimization though, the Java compiler should
  // be able to optimize calls to #of.
  MathVector(double dx, double dy) {
    this.dx = dx;
    this.dy = dy;
  }

  /**
   * Returns the dot product of {@code this} and the given vector, defined as dx
   * * dx' + dy * dy'.
   * @param v the right operand of the dot product
   * @return the dot product of {@code this} and {@code v}
   */
  public double dot(MathVector v) {
    return dx * v.dx + dy * v.dy;
  }

  /** Returns the x-coordinate of this vector. */
  public double dx() {
    return dx;
  }

  /** Returns the y-coordinate of this vector. */
  public double dy() {
    return dy;
  }

  /**
   * Returns the Euclidean norm (or length) of this vector. The term 'norm' is
   * preferred to the term 'length' to avoid confusion between the geometric
   * vector, and the collection.
   * @return the Euclidean norm of this vector
   */
  public double norm() {
    return norm(dx, dy);
  }

  /**
   * Returns the angle between vector {@link #I} and {@code this}, in the range
   * of -pi through pi. If {@code this} is the zero vector, returns
   * {@code Double.NaN}.
   * @return the angle between vector {@link #I} and {@code this}
   * @throws UnsupportedOperationException if {@code this} is the zero vector
   */
  public double theta() {
    MathVector unit = normalize();
    double result = Math.acos(unit.dx);
    if (unit.dy < 0d) {
      result = -result;
    }
    return result;
  }

  /**
   * Returns whether the norm of this vector equals 0.
   * @return whether the norm of this vector equals 0
   */
  public boolean isZero() {
    return dx == 0d && dy == 0d;
  }

  /**
   * Returns the vector obtained by substracting the coordinates of the given
   * vector from {@code this} vector's coordinates.
   * @param v the right operand of the substraction
   * @return {@code this} - {@code v}
   */
  public MathVector minus(MathVector v) {
    return plus(v.negate());
  }

  /**
   * Returns the vector obtained by multiplying this vector's coordinates by -1.
   * This is a shortcut for {@code scale(-1)}.
   * @return -{@code this}
   */
  public MathVector negate() {
    return scale(-1d);
  }

  /**
   * Returns the vector obtained by dividing this vector by its norm. Note that
   * although this vector is supposed to return a unit vector, the value
   * returned by {@link #norm()} is rarely exactly 1, because of loss of
   * precision.
   * @return a unit vector with the same direction as {@code this}
   * @throws UnsupportedOperationException if {@code this} is the zero vector
   */
  public MathVector normalize() {
    if (isZero()) {
      throw new UnsupportedOperationException();
    }
    double norm = norm();
    return new MathVector(dx / norm, dy / norm);
  }

  /**
   * Returns the vector obtained by adding the coordinates of the given vector
   * to {@code this} vector's coordinates.
   * @param v the right operand of the addition
   * @return {@code this} + {@code v}
   */
  public MathVector plus(MathVector v) {
    return new MathVector(dx + v.dx, dy + v.dy);
  }

  /**
   * Returns the image of this vector by the clockwise rotation through the
   * given angle.
   * @param theta in radians
   * @return the image of this vector by the specified rotation
   */
  public MathVector rotate(double theta) {
    return rotate(new RotationMatrix(theta));
  }

  /**
   * Returns the image of this vector by the clockwise rotation defined by the
   * given matrix.
   *
   * <p>If the rotation matrix is already constructed, this method is faster
   * than {@link #rotate(double)}.</p>
   * @param m the rotation matrix: one for every angle
   * @return the image of this vector by the specified rotation
   */
  MathVector rotate(RotationMatrix m) {
    Point p = new Point(dx, dy).rotate(m, Point.ORIGIN);
    return new MathVector(p.x, p.y);
  }

  /**
   * Returns the image of this vector by the counterclockwise rotation through
   * pi / 2. This is a shortcut for {@code rotate(-Math.PI / 2)}
   * @return the image of this vector by the counterclockwise rotation through
   *     the angle pi / 2
   */
  public MathVector rotateCcw() {
    return new MathVector(dy, -dx);
  }

  /**
   * Returns the image of this vector by the clockwise rotation through pi / 2.
   * This is a shortcut for {@code rotate(Math.PI / 2)}
   * @return the image of this vector by the clockwise rotation through the
   *     angle pi / 2
   */
  public MathVector rotateCw() {
    return new MathVector(-dy, dx);
  }

  /**
   * Returns the vector obtained by multiplying this vector's coordinates by the
   * given factor.
   * @param factor a scalar
   * @return {@code factor} . {@code this}
   */
  public MathVector scale(double factor) {
    return new MathVector(factor * dx, factor * dy);
  }

  /**
   * Discouraged. Provided to satisfy the {@code Function} interface; use
   * {@link Point#translate(MathVector)} instead.
   */
  @Override public Point apply(Point input) {
    return input.translate(this);
  }

  @Override public boolean equals(Object object) {
    if (object == this) {
      return true;
    } else if (object instanceof MathVector) {
      MathVector that = (MathVector)object;
      return dx == that.dx && dy == that.dy;
    } else {
      return false;
    }
  }

  @Override public int hashCode() {
    return Objects.hashCode(dx, dy) + 732814477;
  }

  @Override public String toString() {
    return "V(" + dx + ", " + dy + ")";
  }

  /**
   * Returns the norm of the vector with the given coordinates.
   * @param dx the x-coordinate of this vector
   * @param dy the y-coordinate of this vector
   * @return the norm of the specified vector
   */
  static double norm(double dx, double dy) {
    return Math.sqrt(dx * dx + dy * dy);
  }
  private static final long serialVersionUID = 0;
}
