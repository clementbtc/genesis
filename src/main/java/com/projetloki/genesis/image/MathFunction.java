package com.projetloki.genesis.image;

import java.io.Serializable;

import com.google.common.base.Preconditions;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

/**
 * A function from [0, 1] to [0, 1].
 *
 * <p>Functions are immutable. It means that all the methods transforming a
 * function, e.g. {@link #flipX()}, don't actually modify the function but
 * return a new function which is the result of the transformation.</p>
 *
 * <p>All methods return serializable functions as long as {@code this} and the
 * arguments are serializable.</p>
 *
 * @see MathFunctions
 * @see Gradient#transform(MathFunction)
 * @author Clément Roux
 */
public abstract class MathFunction implements Hashable {
  /**
   * Returns the function value at x.
   * @param x the input. Must be between 0 and 1.
   * @return the output. Must be between 0 and 1.
   */
  public abstract double get(double x);

  /**
   * Returns the function obtained by flipping horizontally the graph of
   * {@code this} function. The new function satisfies f'(x) = f(1 - x)
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/cqo5vpw36a.png"/></td>
   *   <td><img src="../../../../resources/genesis/6tuuygjmdq.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code f}</td>
   *   <td>{@code f.flipX()}</td>
   *  </tr>
   * </table>
   * </p>
   */
  public final MathFunction flipX() {
    return new FlipXMathFunction(this);
  }

  /**
   * Returns the function obtained by flipping vertically the graph of
   * {@code this} function. The new function satisfies f'(x) = 1 - f(x)
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/cqo5vpw36a.png"/></td>
   *   <td><img src="../../../../resources/genesis/tk5gacataq.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code f}</td>
   *   <td>{@code f.flipY()}</td>
   *  </tr>
   * </table>
   * </p>
   */
  public final MathFunction flipY() {
    return new FlipYMathFunction(this);
  }

  /**
   * Returns the composition of the two functions. The new function satisfies
   * f'(x) = f(g(x))
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/2cnxftlgmu.png"/></td>
   *   <td><img src="../../../../resources/genesis/cqo5vpw36a.png"/></td>
   *   <td><img src="../../../../resources/genesis/jf75x4g3xm.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code f}</td>
   *   <td>{@code g}</td>
   *   <td>{@code f.compose(g)}</td>
   *  </tr>
   * </table>
   * </p>
   */
  public final MathFunction compose(MathFunction g) {
    Preconditions.checkNotNull(g);
    return new ComposeMathFunction(this, g);
  }

  @Override public final int hashCode() {
    return hash().hashCode();
  }

  // ---------------------------------------------------------------------------
  // Implementations
  // ---------------------------------------------------------------------------

  static class FlipXMathFunction extends MathFunction
      implements Serializable {
    final MathFunction operand;

    FlipXMathFunction(MathFunction operand) {
      this.operand = operand;
    }

    @Override public double get(double x) {
      return operand.get(1d - x);
    }

    @Override public HashCode hash() {
      return Hashing.murmur3_128().newHasher()
          .putLong(-701964635284095892L)
          .putBytes(operand.hash().asBytes())
          .hash();
    }

    @Override public boolean equals(Object object) {
      if (object == this) {
        return true;
      } else if (object instanceof FlipXMathFunction) {
        FlipXMathFunction that = (FlipXMathFunction) object;
        return operand.equals(that.operand);
      }
      return false;
    }

    @Override public String toString() {
      return operand + ".flipX()";
    }
    private static final long serialVersionUID = 0;
  }

  private static class FlipYMathFunction extends MathFunction
      implements Serializable {
    final MathFunction operand;

    FlipYMathFunction(MathFunction operand) {
      this.operand = operand;
    }

    @Override public double get(double x) {
      return 1d - operand.get(x);
    }

    @Override public HashCode hash() {
      return Hashing.murmur3_128().newHasher()
          .putLong(3430957250648804815L)
          .putBytes(operand.hash().asBytes())
          .hash();
    }

    @Override public boolean equals(Object object) {
      if (object == this) {
        return true;
      } else if (object instanceof FlipYMathFunction) {
        FlipYMathFunction that = (FlipYMathFunction) object;
        return operand.equals(that.operand);
      }
      return false;
    }

    @Override public String toString() {
      return operand + ".flipY()";
    }
    private static final long serialVersionUID = 0;
  }

  private static class ComposeMathFunction extends MathFunction
      implements Serializable {
    final MathFunction left;
    final MathFunction right;

    ComposeMathFunction(MathFunction left, MathFunction right) {
      this.left = left;
      this.right = right;
    }

    @Override public double get(double x) {
      return left.get(right.get(x));
    }

    @Override public HashCode hash() {
      return Hashing.murmur3_128().newHasher()
          .putLong(-371678632901418923L)
          .putBytes(left.hash().asBytes())
          .putBytes(right.hash().asBytes())
          .hash();
    }

    @Override public boolean equals(Object object) {
      if (object == this) {
        return true;
      } else if (object instanceof ComposeMathFunction) {
        ComposeMathFunction that = (ComposeMathFunction) object;
        return left.equals(that.left) && right.equals(that.right);
      }
      return false;
    }

    @Override public String toString() {
      return left + ".compose(" + right + ")";
    }
    private static final long serialVersionUID = 0;
  }
}
