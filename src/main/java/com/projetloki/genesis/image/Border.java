package com.projetloki.genesis.image;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import com.google.common.base.Objects;

/**
 * A border around the edge of a shape. Borders are immutable.
 *
 * @see Image#mask(Shape, Border[])
 * @author Clément Roux
 */
public final class Border implements Serializable {
  private final double width;
  private final Color color;
  private final double alpha;

  /**
   * Creates a fully opaque border with the specified width and color.
   * @param width the width of the border. Must be positive.
   * @param color the color of the border
   * @see #Border(double, Color, double)
   */
  public Border(double width, Color color) {
    this(width, color, 1);
  }

  /**
   * Creates a border with the specified width, color and opacity.
   * @param width the width of the border. Must be positive.
   * @param color the color of the border
   * @param alpha the opacity of the border. Must be between 0 and 1.
   */
  public Border(double width, Color color, double alpha) {
    checkNotNull(color);
    checkArgument(width >= 0, "width: %s", width);
    checkArgument(0 <= alpha && alpha <= 1, "alpha: %s", alpha);
    this.width = width;
    this.color = color;
    this.alpha = alpha;
  }

  /** Returns the width of the border. */
  public double width() {
    return width;
  }

  /** Returns the color of the border. */
  public Color color() {
    return color;
  }

  /** Returns the opacity of the border. */
  public double alpha() {
    return alpha;
  }

  Image getForeIm(int width, int height) {
    return Images.canvas(width, height, color, alpha);
  }

  @Override public boolean equals(Object object) {
    if (object == this) {
      return true;
    } else if (object instanceof Border) {
      Border that = (Border) object;
      return width == that.width && color.equals(that.color) &&
          alpha == that.alpha;
    }
    return false;
  }

  @Override public int hashCode() {
    return Objects.hashCode(width, color, alpha);
  }

  @Override public String toString() {
    return Objects.toStringHelper(this)
        .add("width", width)
        .add("color", color)
        .add("alpha", alpha)
        .toString();
  }
  private static final long serialVersionUID = 0;
}
