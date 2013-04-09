package com.projetloki.genesis.image;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

/**
 * A density map factory. Gradients are immutable.
 *
 * <p>All methods return serializable gradients as long as {@code this} and the
 * arguments are serializable.</p>
 * @see Gradients
 * @see Image#fill(Color, Gradient)
 * @see Image#erase(Gradient)
 *
 * @author Clément Roux
 */
public abstract class Gradient implements Hashable {
  /**
   * A density map associates each point of a rectangle to a value between 0 and
   * 1. Density maps are immutable.
   * @see Gradient#getDensityMap(int, int)
   */
  public static interface DensityMap {

    /**
     * Returns the density, between 0 and 1, at the given point.
     * @param p a point of the rectangle
     */
    public double getDensity(Point p);
  }

  /**
   * Returns a density map for a rectangle with the given size.
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   */
  public abstract DensityMap getDensityMap(int width, int height);

  // ---------------------------------------------------------------------------
  // Internal methods that can be overridden for optimization
  // ---------------------------------------------------------------------------

  /** Same as {@link Image#features(). Raster and opaque must be false. */
  ImageFeatures features() {
    return ImageFeatures.start();
  }

  /**
   * Returns a gradient that applies the specified function to the values
   * returned by {@code this} gradient.
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/pm7o2rcmti.png"/></td>
   *   <td><img src="../../../../resources/genesis/jfip3zxoem.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code grad}</td>
   *   <td>{@code grad.transform(MathFunctions.pow(2))}</td>
   *  </tr>
   * </table>
   * </p>
   * @param f the function
   */
  public Gradient transform(MathFunction f) {
    return new TransformGradient(this, checkNotNull(f));
  }

  /**
   * Returns a gradient that applies the specified linear function to the values
   * returned by {@code this} gradient.
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/pm7o2rcmti.png"/></td>
   *   <td><img src="../../../../resources/genesis/23nh6da4ry.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code grad}</td>
   *   <td>{@code grad.transform(1, 0.25)}</td>
   *  </tr>
   * </table>
   * </p>
   * @param y0 the value of the linear function for x = 0
   * @param y1 the value of the linear function for x = 1
   * @see #transform(MathFunction)
   */
  public Gradient transform(double y0, double y1) {
    return new TransformGradient(this, MathFunctions.linear(y0, y1));
  }

  @Override public final int hashCode() {
    return hash().hashCode();
  }

  // ---------------------------------------------------------------------------
  // Implementations
  // ---------------------------------------------------------------------------

  private static class TransformGradient extends Gradient
      implements Serializable {
    final Gradient operand;
    final MathFunction f;

    TransformGradient(Gradient operand, MathFunction f) {
      this.operand = operand;
      this.f = f;
    }

    @Override public Gradient.DensityMap getDensityMap(int width, int height) {
      return new DensityMap(operand.getDensityMap(width, height), f);
    }

    @Override public HashCode hash() {
      return Hashing.murmur3_128().newHasher()
          .putLong(6173276666929231793L)
          .putBytes(operand.hash().asBytes())
          .putBytes(f.hash().asBytes())
          .hash();
    }

    @Override public boolean equals(Object object) {
      if (object == this) {
        return true;
      } else if (object instanceof TransformGradient) {
        TransformGradient that = (TransformGradient) object;
        return operand.equals(that.operand) && f.equals(that.f);
      }
      return false;
    }

    @Override public String toString() {
      return operand + ".transform(" + f + ")";
    }

    private static class DensityMap implements Gradient.DensityMap {
      final Gradient.DensityMap operand;
      final MathFunction f;

      DensityMap(Gradient.DensityMap operand, MathFunction f) {
        this.operand = operand;
        this.f = f;
      }

      @Override public double getDensity(Point p) {
        return f.get(operand.getDensity(p));
      }
    }
    private static final long serialVersionUID = 0;
  }
}
