package com.projetloki.genesis.image;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A mutable class to mix colors together. Unlike with
 * {@link Color#mix(Color, double)}, it is possible to have more than
 * two colors in the mixture.
 *
 * <p>Usage example: <pre><code>
 * ColorMixer mixer = new ColorMixer();
 * mixer.add(Color.BLACK).add(Color.RED, 0.2).add(Color.GREEN, 0.3);
 * System.out.println(mixer.getColor().toHexString()); // 221a00
 * System.out.println(mixer.getAlpha()); // 0.5 = (1 + 0.2 + 0.3) / 3
 * </code></pre></p>
 *
 * @author Clément Roux
 */
final class ColorMixer {
  private int size = 0;
  private double sumAlpha = 0d;
  private double sumRed = 0d;
  private double sumGreen = 0d;
  private double sumBlue = 0d;

  /** Constructs a {@code ColorMixer} with no color in the mixture. */
  public ColorMixer() {}

  /**
   * Adds the specified color to the mixture. By default, alpha equals 1: the
   * color is fully opaque.
   * @param color the color to add to the mixture
   * @return this {@code ColorMixer} object
   */
  public ColorMixer add(Color color) {
    size++;
    sumAlpha += 1;
    sumRed += color.red();
    sumGreen += color.green();
    sumBlue += color.blue();
    return this;
  }

  /**
   * Adds the specified color/alpha pair to the mixture. The more a color is
   * transparent (alpha close to 0), the less it weights in the mixture.
   * @param color the color to add to the mixture
   * @param alpha the opacity/weight of the color, in [0, 1]
   * @return this {@code ColorMixer} object
   * @throws IllegalArgumentException if {@code alpha} not in [0, 1]
   */
  public ColorMixer add(Color color, double alpha) {
    checkArgument(0d <= alpha && alpha <= 1, "alpha: %s", alpha);
    size++;
    sumAlpha += alpha;
    sumRed += color.red() * alpha;
    sumGreen += color.green() * alpha;
    sumBlue += color.blue() * alpha;
    return this;
  }

  /**
   * Returns the result of the mixing. At least one color must have been added
   * to the mixture, otherwise this operation is impossible.
   *
   * <p>If for all the colors that were added to the mixture, alpha equals 0
   * (fully transparent), this method returns the arbitrary color black. This
   * is okay because this color will also be fully transparent.</p>
   */
  public Color getColor() {
    if (sumAlpha == 0d) {
      return Color.WHITE;
    }
    double red = sumRed / sumAlpha;
    double green = sumGreen / sumAlpha;
    double blue = sumBlue / sumAlpha;
    return new Color((int) (red + 0.5d), (int) (green + 0.5d),
        (int) (blue + 0.5d));
  }

  /**
   * Returns the average alpha for the colors added to the mixture. At least
   * one color must have been added to the mixture, otherwise this operation is
   * impossible.
   * @throws UnsupportedOperationException if no color was added to the mixture
   */
   public double getAlpha() {
    if (size == 0) {
      throw new IllegalStateException();
    }
    return sumAlpha / size;
  }
}
