package com.projetloki.genesis.image;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.common.primitives.Doubles;

/**
 * Static utility methods pertaining to {@link MathFunction} instances.
 *
 * <p>All methods return serializable functions.</p>
 *
 * @author Clément Roux
 */
public final class MathFunctions {
  /**
   * Returns the identity function.
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/vjn4h67dfm.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code MathFunctions.identity()}</td>
   *  </tr>
   * </table>
   * </p>
   */
  public static MathFunction identity() {
    return IdentityMathFunction.INSTANCE;
  }

  /**
   * Returns a constant function. It associates the given value with each number
   * in [0, 1]
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/4jscey3xde.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code MathFunctions.constant(0.3)}</td>
   *  </tr>
   * </table>
   * </p>
   * @param value the function value. Must be between 0 and 1.
   */
  public static MathFunction constant(double value) {
    checkArgument(0 <= value && value <= 1, "value: %s", value);
    return new ConstantMathFunction(value);
  }

  /**
   * Returns the function f(x) = x ˆ r
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/cqo5vpw36a.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code MathFunctions.pow(0.5)}</td>
   *  </tr>
   * </table>
   * </p>
   * @param r the exponent
   */
  public static MathFunction pow(double r) {
    return new PowMathFunction(r);
  }

  /**
   * Returns a linear function with the given values at x = 0 and x = 1
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/ofvpgpy45m.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code MathFunctions.linear(0.2, 0.5)}</td>
   *  </tr>
   * </table>
   * </p>
   * @param y0 the function value at x = 0. Must be between 0 and 1.
   * @param y1 the function value at x = 1. Must be between 0 and 1.
   */
  public static MathFunction linear(double y0, double y1) {
    checkArgument(0 <= y0 && y0 <= 1, "y0: %s", y0);
    checkArgument(0 <= y1 && y1 <= 1, "y1: %s", y1);
    return new LinearMathFunction(y0, y1);
  }

  /**
   * Returns a function whose graph has the given vertices, joined by segments.
   *
   * <p>The list of vertices must satisfy these conditions:<ul>
   * <li>The x-coordinate of the first vertex must be 0</li>
   * <li>The x-coordinate of the last vertex must be 1</li>
   * <li>Each x/y-coordinate must be between 0 and 1</li>
   * <li>The vertices must be ordered by x-coordinate</li>
   * </ul></p>
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/olutyfehea.png"/></td>
   *  </tr>
   *  <tr>
   *   <td style="text-align:left"><pre>{@code MathFunctions.vertices(
   *    new Point(0, 0.6),
   *    new Point(0.25, 0.2),
   *    new Point(0.6, 0.5),
   *    new Point(0.6, 0.8),
   *    new Point(1, 1))}</pre></td>
   *  </tr>
   * </table>
   * </p>
   * @param v0 the first vertex
   * @param v1 the second vertex
   * @param others the other vertices
   */
  public static MathFunction forVertices(Point v0, Point v1, Point... others) {
    ImmutableList<Point> vertices = ImmutableList.<Point>builder()
        .add(v0)
        .add(v1)
        .add(others)
        .build();
    checkArgument(vertices.get(0).x == 0d,
        "x-coordinate of first vertex: %s", vertices.get(0).x);
    checkArgument(Iterables.getLast(vertices).x == 1d,
        "x-coordinate of last vertex: %s", Iterables.getLast(vertices).x);
    for (Point v : vertices) {
      checkArgument(0d <= v.y && v.y <= 1d,
          "y-coordinate of vertex: %s", v.y);
    }
    for (int i = 0; i < vertices.size() - 1; i++) {
      checkArgument(
          vertices.get(i).x <= vertices.get(i + 1).x,
          "vertices not ordered by x-coordinate: %s > %s",
          vertices.get(i).x,
          vertices.get(i + 1).x);
    }
    return new MathFunctionForVertices(vertices);
  }

  private static class IdentityMathFunction extends MathFunction
      implements Serializable {
    static final IdentityMathFunction INSTANCE = new IdentityMathFunction();

    @Override public double get(double x) {
      return x;
    }

    @Override public HashCode hash() {
      return HashCode.fromLong(-5838687867276611996L);
    }

    @Override public String toString() {
      return "IDENTITY";
    }

    // To enforce the singleton pattern
    private Object readResolve() {
      return INSTANCE;
    }
    private static final long serialVersionUID = 0;
  }

  private static class ConstantMathFunction extends MathFunction
      implements Serializable {
    final double value;

    ConstantMathFunction(double value) {
      this.value = value;
    }

    @Override public double get(double x) {
      return value;
    }

    @Override public HashCode hash() {
      return HashCode.fromLong(-1565035307198871281L);
    }

    @Override public boolean equals(Object object) {
      if (object instanceof ConstantMathFunction) {
        ConstantMathFunction that = (ConstantMathFunction) object;
        return value == that.value;
      }
      return false;
    }

    @Override public String toString() {
      return "constant(" + value + ")";
    }
    private static final long serialVersionUID = 0;
  }

  private static class PowMathFunction extends MathFunction
      implements Serializable {
    final double r;

    PowMathFunction(double r) {
      this.r = r;
    }

    @Override public double get(double x) {
      return Math.pow(x, r);
    }

    @Override public HashCode hash() {
      return HashCode.fromLong(6258949628338275487L);
    }

    @Override public boolean equals(Object object) {
      if (object instanceof PowMathFunction) {
        PowMathFunction that = (PowMathFunction) object;
        return r == that.r;
      }
      return false;
    }

    @Override public String toString() {
      return "pow(" + r + ")";
    }
    private static final long serialVersionUID = 0;
  }

  private static class LinearMathFunction extends MathFunction
      implements Serializable {
    final double y0;
    final double y1;
    final double delta;

    LinearMathFunction(double y0, double y1) {
      this.y0 = y0;
      this.y1 = y1;
      this.delta = y1 - y0;
    }

    @Override public double get(double x) {
      return y0 + delta * x;
    }

    @Override public HashCode hash() {
      return Hashing.murmur3_128().newHasher()
          .putLong(6451617954221379542L)
          .putDouble(y0)
          .putDouble(y1)
          .putDouble(delta)
          .hash();
    }

    @Override public boolean equals(Object object) {
      if (object == this) {
        return true;
      } else if (object instanceof LinearMathFunction) {
        LinearMathFunction that = (LinearMathFunction) object;
        return y0 == that.y0 && y1 == that.y1;
      }
      return false;
    }

    @Override public String toString() {
      return "linear(" + y0 + ", " + y1 + ")";
    }

    private static class SerializationProxy implements Serializable {
      final double y0;
      final double y1;
      SerializationProxy(double y0, double y1) {
        this.y0 = y0;
        this.y1 = y1;
      }
      private Object readResolve() {
        return new LinearMathFunction(y0, y1);
      }
      private static final long serialVersionUID = 0;
    }

    private Object writeReplace() {
      return new SerializationProxy(y0, y1);
    }

    private void readObject(ObjectInputStream stream)
        throws InvalidObjectException {
      throw new InvalidObjectException("Use SerializationProxy");
    }
  }

  private static class MathFunctionForVertices extends MathFunction
      implements Serializable {
    final ImmutableList<Point> vertices;
    final double[] xx;
    final double[] aa;
    final double[] bb;

    MathFunctionForVertices(ImmutableList<Point> vertices) {
      this.vertices = vertices;
      List<Double> xxTemp = Lists.newArrayList();
      List<Double> aaTemp = Lists.newArrayList();
      List<Double> bbTemp = Lists.newArrayList();
      for (int i = 0; i < vertices.size() - 1; i++) {
        double x0 = vertices.get(i).x;
        double x1 = vertices.get(i + 1).x;
        if (x0 != x1) {
          Point p0 = vertices.get(i);
          Point p1 = vertices.get(i + 1);
          xxTemp.add(x0);
          aaTemp.add(getA(p0, p1));
          bbTemp.add(getB(p0, p1));
        }
      }
      xx = Doubles.toArray(xxTemp);
      aa = Doubles.toArray(aaTemp);
      bb = Doubles.toArray(bbTemp);
    }

    @Override public double get(double x) {
      int i0 = 0;
      int i1 = xx.length - 1;
      while (i1 != i0) {
        int i = (i0 + i1 + 1) / 2;
        if (x < xx[i]) {
          i1 = i - 1;
        } else {
          i0 = i;
        }
      }
      return aa[i0] * x + bb[i0];
    }

    private static double getA(Point p0, Point p1) {
      return (p1.y - p0.y) / (p1.x - p0.x);
    }

    private static double getB(Point p0, Point p1) {
      return p0.y - getA(p0, p1) * p0.x;
    }

    @Override public HashCode hash() {
      Hasher hasher = Hashing.murmur3_128().newHasher()
          .putLong(-3361094966461639747L);
      for (Point v : vertices) {
        hasher.putDouble(v.x);
        hasher.putDouble(v.y);
      }
      return hasher.hash();
    }

    @Override public boolean equals(Object object) {
      if (object instanceof MathFunctionForVertices) {
        MathFunctionForVertices that = (MathFunctionForVertices) object;
        return vertices.equals(that.vertices);
      }
      return false;
    }

    @Override public String toString() {
      return "forVertices(" + vertices + ")";
    }

    private static class SerializationProxy implements Serializable {
      final ImmutableList<Point> vertices;
      SerializationProxy(ImmutableList<Point> vertices) {
        this.vertices = vertices;
      }
      private Object readResolve() {
        return new MathFunctionForVertices(vertices);
      }
      private static final long serialVersionUID = 0;
    }

    private Object writeReplace() {
      return new SerializationProxy(vertices);
    }

    private void readObject(ObjectInputStream stream)
        throws InvalidObjectException {
      throw new InvalidObjectException("Use SerializationProxy");
    }
  }

  // To prevent instantiation
  private MathFunctions() {}
}
