package com.projetloki.genesis;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * An expression that check for the conditions of particular media features.
 *
 * @see MediaQuery#and(MediaFeature)
 * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/Media_queries#Media_features">MDN</a>
 * @see <a href="http://caniuse.com/#feat=css-mediaqueries">Browser support</a>
 *
 * @author Clément Roux
 */
@PoorBrowserSupport
public final class MediaFeature extends SimpleStringWrapper {
  /**
   * Returns a feature that checks that the width of the targeted display area
   * is exactly the given length.
   * For continuous media, refers to the width of the viewport including the
   * size of a rendered scroll bar (if any). For paged media, refers to the
   * width of the page box.
   * @throws IllegalArgumentException if the given length is negative
   * @see <a href="http://www.w3.org/TR/css3-mediaqueries/#width">http://www.w3.org/TR/css3-mediaqueries/#width</a>
   */
  public static MediaFeature width(double length, LengthUnit unit) {
    return new MediaFeature("(width:" + unit.formatPositive(length) + ")");
  }

  /**
   * Returns a feature that checks that the width of the targeted display area
   * is at least the given length.
   * For continuous media, refers to the width of the viewport including the
   * size of a rendered scroll bar (if any). For paged media, refers to the
   * width of the page box.
   * @throws IllegalArgumentException if the given length is negative
   * @see <a href="http://www.w3.org/TR/css3-mediaqueries/#width">http://www.w3.org/TR/css3-mediaqueries/#width</a>
   */
  public static MediaFeature minWidth(double length, LengthUnit unit) {
    return new MediaFeature("(min-width:" + unit.formatPositive(length) + ")");
  }

  /**
   * Returns a feature that checks that the width of the targeted display area
   * is at most the given length.
   * For continuous media, refers to the width of the viewport including the
   * size of a rendered scroll bar (if any). For paged media, refers to the
   * width of the page box.
   * @throws IllegalArgumentException if the given length is negative
   * @see <a href="http://www.w3.org/TR/css3-mediaqueries/#width">http://www.w3.org/TR/css3-mediaqueries/#width</a>
   */
  public static MediaFeature maxWidth(double length, LengthUnit unit) {
    return new MediaFeature("(max-width:" + unit.formatPositive(length) + ")");
  }

  /**
   * Returns a feature that checks that the height of the targeted display area
   * is exactly the given length.
   * For continuous media, refers to the height of the viewport including the
   * size of a rendered scroll bar (if any). For paged media, refers to the
   * height of the page box.
   * @throws IllegalArgumentException if the given length is negative
   * @see <a href="http://www.w3.org/TR/css3-mediaqueries/#height">http://www.w3.org/TR/css3-mediaqueries/#height</a>
   */
  public static MediaFeature height(double length, LengthUnit unit) {
    return new MediaFeature("(height:" + unit.formatPositive(length) + ")");
  }

  /**
   * Returns a feature that checks that the height of the targeted display area
   * is at least the given length.
   * For continuous media, refers to the height of the viewport including the
   * size of a rendered scroll bar (if any). For paged media, refers to the
   * height of the page box.
   * @throws IllegalArgumentException if the given length is negative
   * @see <a href="http://www.w3.org/TR/css3-mediaqueries/#height">http://www.w3.org/TR/css3-mediaqueries/#height</a>
   */
  public static MediaFeature minHeight(double length, LengthUnit unit) {
    return new MediaFeature("(min-height:" + unit.formatPositive(length) + ")");
  }

  /**
   * Returns a feature that checks that the height of the targeted display area
   * is at most the given length.
   * For continuous media, refers to the height of the viewport including the
   * size of a rendered scroll bar (if any). For paged media, refers to the
   * height of the page box.
   * @throws IllegalArgumentException if the given length is negative
   * @see <a href="http://www.w3.org/TR/css3-mediaqueries/#height">http://www.w3.org/TR/css3-mediaqueries/#height</a>
   */
  public static MediaFeature maxHeight(double length, LengthUnit unit) {
    return new MediaFeature("(max-height:" + unit.formatPositive(length) + ")");
  }

  /**
   * Returns a feature that checks that the width of the output device's
   * rendering surface is exactly the given length.
   * For continuous media, refers to the width of the screen. For paged media,
   * refers to the width of the page sheet size.
   * @throws IllegalArgumentException if the given length is negative
   * @see <a href="http://www.w3.org/TR/css3-mediaqueries/#device-width">http://www.w3.org/TR/css3-mediaqueries/#device-width</a>
   */
  public static MediaFeature deviceWidth(double length, LengthUnit unit) {
    return new MediaFeature("(device-width:" +
        unit.formatPositive(length) + ")");
  }

  /**
   * Returns a feature that checks that the width of the output device's
   * rendering surface is at least the given length.
   * For continuous media, refers to the width of the screen. For paged media,
   * refers to the width of the page sheet size.
   * @throws IllegalArgumentException if the given length is negative
   * @see <a href="http://www.w3.org/TR/css3-mediaqueries/#device-width">http://www.w3.org/TR/css3-mediaqueries/#device-width</a>
   */
  public static MediaFeature minDeviceWidth(double length, LengthUnit unit) {
    return new MediaFeature("(min-device-width:" +
        unit.formatPositive(length) + ")");
  }

  /**
   * Returns a feature that checks that the width of the output device's
   * rendering surface is at most the given length.
   * For continuous media, refers to the width of the screen. For paged media,
   * refers to the width of the page sheet size.
   * @throws IllegalArgumentException if the given length is negative
   * @see <a href="http://www.w3.org/TR/css3-mediaqueries/#device-width">http://www.w3.org/TR/css3-mediaqueries/#device-width</a>
   */
  public static MediaFeature maxDeviceWidth(double length, LengthUnit unit) {
    return new MediaFeature("(max-device-width:" +
        unit.formatPositive(length) + ")");
  }

  /**
   * Returns a feature that checks that the height of the output device's
   * rendering surface is exactly the given length.
   * For continuous media, refers to the height of the screen. For paged media,
   * refers to the height of the page sheet size.
   * @throws IllegalArgumentException if the given length is negative
   * @see <a href="http://www.w3.org/TR/css3-mediaqueries/#device-height">http://www.w3.org/TR/css3-mediaqueries/#device-height</a>
   */
  public static MediaFeature deviceHeight(double length, LengthUnit unit) {
    return new MediaFeature("(device-height:" +
        unit.formatPositive(length) + ")");
  }

  /**
   * Returns a feature that checks that the height of the output device's
   * rendering surface is at least the given length.
   * For continuous media, refers to the height of the screen. For paged media,
   * refers to the height of the page sheet size.
   * @throws IllegalArgumentException if the given length is negative
   * @see <a href="http://www.w3.org/TR/css3-mediaqueries/#device-height">http://www.w3.org/TR/css3-mediaqueries/#device-height</a>
   */
  public static MediaFeature minDeviceHeight(double length, LengthUnit unit) {
    return new MediaFeature("(min-device-height:" +
        unit.formatPositive(length) + ")");
  }

  /**
   * Returns a feature that checks that the height of the output device's
   * rendering surface is at most the given length.
   * For continuous media, refers to the height of the screen. For paged media,
   * refers to the height of the page sheet size.
   * @throws IllegalArgumentException if the given length is negative
   * @see <a href="http://www.w3.org/TR/css3-mediaqueries/#device-height">http://www.w3.org/TR/css3-mediaqueries/#device-height</a>
   */
  public static MediaFeature maxDeviceHeight(double length, LengthUnit unit) {
    return new MediaFeature("(max-device-height:" +
        unit.formatPositive(length) + ")");
  }

  private static final MediaFeature PORTRAIT_ORIENTATION =
      new MediaFeature("(orientation:portrait)");

  /**
   * Returns a feature that checks that the value of the height media feature is
   * greater than or equal to the value of the width media feature.
   * @see <a href="http://www.w3.org/TR/css3-mediaqueries/#orientation">http://www.w3.org/TR/css3-mediaqueries/#orientation</a>
   */
  public static MediaFeature portraitOrientation() {
    return PORTRAIT_ORIENTATION;
  }

  private static final MediaFeature LANDSCAPE_ORIENTATION =
      new MediaFeature("(orientation:landscape)");

  /**
   * Returns a feature that checks that the value of the width media feature is
   * greater than the value of the height media feature.
   * @see <a href="http://www.w3.org/TR/css3-mediaqueries/#orientation">http://www.w3.org/TR/css3-mediaqueries/#orientation</a>
   */
  public static MediaFeature landscapeOrientation() {
    return LANDSCAPE_ORIENTATION;
  }

  /**
   * Returns a feature that checks that the ratio of width to height is
   * exactly the given ratio.
   * @param w the width/numerator
   * @param h the height/denominator
   * @see <a href="http://www.w3.org/TR/css3-mediaqueries/#aspect-ratio">http://www.w3.org/TR/css3-mediaqueries/#aspect-ratio</a>
   */
  public static MediaFeature aspectRatio(int w, int h) {
    return new MediaFeature("(aspect-ratio:" + w + "/" + h + ")");
  }

  /**
   * Returns a feature that checks that the ratio of width to height is
   * at least the given ratio.
   * @param w the width/numerator
   * @param h the height/denominator
   * @see <a href="http://www.w3.org/TR/css3-mediaqueries/#aspect-ratio">http://www.w3.org/TR/css3-mediaqueries/#aspect-ratio</a>
   */
  public static MediaFeature minAspectRatio(int w, int h) {
    return new MediaFeature("(min-aspect-ratio:" + w + "/" + h + ")");
  }

  /**
   * Returns a feature that checks that the ratio of width to height is
   * at most the given ratio.
   * @param w the width/numerator
   * @param h the height/denominator
   * @see <a href="http://www.w3.org/TR/css3-mediaqueries/#aspect-ratio">http://www.w3.org/TR/css3-mediaqueries/#aspect-ratio</a>
   */
  public static MediaFeature maxAspectRatio(int w, int h) {
    return new MediaFeature("(max-aspect-ratio:" + w + "/" + h + ")");
  }

  /**
   * Returns a feature that checks that the ratio of device width to
   * device height is exactly the given ratio.
   * @param w the width/numerator
   * @param h the height/denominator
   * @see <a href="http://www.w3.org/TR/css3-mediaqueries/#device-aspect-ratio">http://www.w3.org/TR/css3-mediaqueries/#device-aspect-ratio</a>
   */
  public static MediaFeature deviceAspectRatio(int w, int h) {
    return new MediaFeature("(device-aspect-ratio:" + w + "/" + h + ")");
  }

  /**
   * Returns a feature that checks that the ratio of device width to
   * device height is at least the given ratio.
   * @param w the width/numerator
   * @param h the height/denominator
   * @see <a href="http://www.w3.org/TR/css3-mediaqueries/#device-aspect-ratio">http://www.w3.org/TR/css3-mediaqueries/#device-aspect-ratio</a>
   */
  public static MediaFeature minDeviceAspectRatio(int w, int h) {
    return new MediaFeature("(min-device-aspect-ratio:" + w + "/" + h + ")");
  }

  /**
   * Returns a feature that checks that the ratio of device width to
   * device height is at most the given ratio.
   * @param w the width/numerator
   * @param h the height/denominator
   * @see <a href="http://www.w3.org/TR/css3-mediaqueries/#device-aspect-ratio">http://www.w3.org/TR/css3-mediaqueries/#device-aspect-ratio</a>
   */
  public static MediaFeature maxDeviceAspectRatio(int w, int h) {
    return new MediaFeature("(max-device-aspect-ratio:" + w + "/" + h + ")");
  }

  private static final MediaFeature COLOR = new MediaFeature("(color)");

  /**
   * Returns a feature that checks that the device is a color device.
   * @see <a href="http://www.w3.org/TR/css3-mediaqueries/#color">http://www.w3.org/TR/css3-mediaqueries/#color</a>
   */
  public static MediaFeature color() {
    return COLOR;
  }

  /**
   * Returns a feature that checks that the number of bits per color is exactly
   * the given number.
   * If the device is not a color device, the value is zero.
   * @throws IllegalArgumentException if the given number is negative
   * @see <a href="http://www.w3.org/TR/css3-mediaqueries/#color">http://www.w3.org/TR/css3-mediaqueries/#color</a>
   */
  public static MediaFeature color(int bitsPerColor) {
    checkArgument(0 <= bitsPerColor, bitsPerColor);
    return new MediaFeature("(color:" + bitsPerColor + ")");
  }

  /**
   * Returns a feature that checks that the number of bits per color is at least
   * the given number.
   * If the device is not a color device, the value is zero.
   * @throws IllegalArgumentException if the given number is negative
   * @see <a href="http://www.w3.org/TR/css3-mediaqueries/#color">http://www.w3.org/TR/css3-mediaqueries/#color</a>
   */
  public static MediaFeature minColor(int bitsPerColor) {
    if (bitsPerColor == 1) {
      return COLOR;
    }
    checkArgument(0 <= bitsPerColor, bitsPerColor);
    return new MediaFeature("(min-color:" + bitsPerColor + ")");
  }

  /**
   * Returns a feature that checks that the number of bits per color is at most
   * the given number.
   * If the device is not a color device, the value is zero.
   * @throws IllegalArgumentException if the given number is negative
   * @see <a href="http://www.w3.org/TR/css3-mediaqueries/#color">http://www.w3.org/TR/css3-mediaqueries/#color</a>
   */
  public static MediaFeature maxColor(int bitsPerColor) {
    checkArgument(0 <= bitsPerColor, bitsPerColor);
    return new MediaFeature("(max-color:" + bitsPerColor + ")");
  }

  /**
   * Returns a feature that checks that the number of entries in the color
   * lookup table is exactly the given number.
   * If the device does not use a color lookup table, the value is zero.
   * @throws IllegalArgumentException if the given number is negative
   * @see <a href="http://www.w3.org/TR/css3-mediaqueries/#color-index">http://www.w3.org/TR/css3-mediaqueries/#color-index</a>
   */
  public static MediaFeature colorIndex(int n) {
    checkArgument(0 <= n, n);
    return new MediaFeature("(color-index:" + n + ")");
  }

  /**
   * Returns a feature that checks that the number of entries in the color
   * lookup table is at least the given number.
   * If the device does not use a color lookup table, the value is zero.
   * @throws IllegalArgumentException if the given number is negative
   * @see <a href="http://www.w3.org/TR/css3-mediaqueries/#color-index">http://www.w3.org/TR/css3-mediaqueries/#color-index</a>
   */
  public static MediaFeature minColorIndex(int n) {
    checkArgument(0 <= n, n);
    return new MediaFeature("(min-color-index:" + n + ")");
  }

  /**
   * Returns a feature that checks that the number of entries in the color
   * lookup table is at most the given number.
   * If the device does not use a color lookup table, the value is zero.
   * @throws IllegalArgumentException if the given number is negative
   * @see <a href="http://www.w3.org/TR/css3-mediaqueries/#color-index">http://www.w3.org/TR/css3-mediaqueries/#color-index</a>
   */
  public static MediaFeature maxColorIndex(int n) {
    checkArgument(0 <= n, n);
    return new MediaFeature("(max-color-index:" + n + ")");
  }

  /**
   * Returns a feature that checks that the device is a monochrome device.
   * @see <a href="http://www.w3.org/TR/css3-mediaqueries/#monochrome">http://www.w3.org/TR/css3-mediaqueries/#monochrome</a>
   */
  private static final MediaFeature MONOCHROME = new MediaFeature("monochrome");

  public static MediaFeature monochrome() {
    return MONOCHROME;
  }

  /**
   * Returns a feature that checks that the number of bits per pixel in a
   * monochrome frame buffer is exactly the given number.
   * If the device is not a monochrome device, the value is 0.
   * @throws IllegalArgumentException if the given number is negative
   * @see <a href="http://www.w3.org/TR/css3-mediaqueries/#monochrome">http://www.w3.org/TR/css3-mediaqueries/#monochrome</a>
   */
  public static MediaFeature monochrome(int bitsPerPixel) {
    checkArgument(0 <= bitsPerPixel, bitsPerPixel);
    return new MediaFeature("(monochrome:" + bitsPerPixel + ")");
  }

  /**
   * Returns a feature that checks that the number of bits per pixel in a
   * monochrome frame buffer is at least the given number.
   * If the device is not a monochrome device, the value is 0.
   * @throws IllegalArgumentException if the given number is negative
   * @see <a href="http://www.w3.org/TR/css3-mediaqueries/#monochrome">http://www.w3.org/TR/css3-mediaqueries/#monochrome</a>
   */
  public static MediaFeature minMonochrome(int bitsPerPixel) {
    if (bitsPerPixel == 1) {
      return MONOCHROME;
    }
    checkArgument(0 <= bitsPerPixel, bitsPerPixel);
    return new MediaFeature("(min-monochrome:" + bitsPerPixel + ")");
  }

  /**
   * Returns a feature that checks that the number of bits per pixel in a
   * monochrome frame buffer is at most the given number.
   * If the device is not a monochrome device, the value is 0.
   * @throws IllegalArgumentException if the given number is negative
   * @see <a href="http://www.w3.org/TR/css3-mediaqueries/#monochrome">http://www.w3.org/TR/css3-mediaqueries/#monochrome</a>
   */
  public static MediaFeature maxMonochrome(int bitsPerPixel) {
    checkArgument(0 <= bitsPerPixel, bitsPerPixel);
    return new MediaFeature("(max-monochrome:" + bitsPerPixel + ")");
  }

  /**
   * Returns a feature that checks that the resolution of the output device is
   * exactly the given number of dots per inch.
   * Never matches devices with non-square pixels.
   * For printers, refers to the screening resolution (the resolution for
   * printing dots of arbitrary color).
   * @see <a href="http://www.w3.org/TR/css3-mediaqueries/#resolution">http://www.w3.org/TR/css3-mediaqueries/#resolution</a>
   */
  public static MediaFeature resolutionDpi(int dpi) {
    // The spec does not say that the given resolution must be >= 0
    return new MediaFeature("(resolution:" + dpi + "dpi)");
  }

  /**
   * Returns a feature that checks that the resolution of the output device is
   * at least the given number of dots per inch.
   * For printers, refers to the screening resolution (the resolution for
   * printing dots of arbitrary color).
   * @see <a href="http://www.w3.org/TR/css3-mediaqueries/#resolution">http://www.w3.org/TR/css3-mediaqueries/#resolution</a>
   */
  public static MediaFeature minResolutionDpi(int dpi) {
    // The spec does not say that the given resolution must be >= 0
    return new MediaFeature("(min-resolution:" + dpi + "dpi)");
  }

  /**
   * Returns a feature that checks that the resolution of the output device is
   * at most the given number of dots per inch.
   * For printers, refers to the screening resolution (the resolution for
   * printing dots of arbitrary color).
   * @see <a href="http://www.w3.org/TR/css3-mediaqueries/#resolution">http://www.w3.org/TR/css3-mediaqueries/#resolution</a>
   */
  public static MediaFeature maxResolutionDpi(int dpi) {
    // The spec does not say that the given resolution must be >= 0
    return new MediaFeature("(max-resolution:" + dpi + "dpi)");
  }

  private static final MediaFeature PROGRESSIVE_SCAN =
      new MediaFeature("(scan:progressive)");

  private static final MediaFeature INTERLACE_SCAN =
      new MediaFeature("(scan:interlace)");

  /**
   * Returns a feature that checks that the scanning process of the TV is
   * progressive.
   * Only applies to TV media types.
   * @see <a href="http://www.w3.org/TR/css3-mediaqueries/#scan">http://www.w3.org/TR/css3-mediaqueries/#scan</a>
   */
  public static MediaFeature progressiveScan() {
    return PROGRESSIVE_SCAN;
  }

  /**
   * Returns a feature that checks that the scanning process of the TV is
   * interlace.
   * Only applies to TV media types.
   * @see <a href="http://www.w3.org/TR/css3-mediaqueries/#scan">http://www.w3.org/TR/css3-mediaqueries/#scan</a>
   */
  public static MediaFeature interlaceScan() {
    return INTERLACE_SCAN;
  }

  private static final MediaFeature GRID = new MediaFeature("(grid)");

  /**
   * Returns a feature that checks that the output device is grid
   * (versus bitmap).
   * @see <a href="http://www.w3.org/TR/css3-mediaqueries/#grid">http://www.w3.org/TR/css3-mediaqueries/#grid</a>
   */
  public static MediaFeature grid() {
    return GRID;
  }

  private static final MediaFeature NOT_GRID = new MediaFeature("(grid:0)");

  /**
   * Returns a feature that checks that the output device is grid if true, or
   * bitmap if false.
   * @see <a href="http://www.w3.org/TR/css3-mediaqueries/#grid">http://www.w3.org/TR/css3-mediaqueries/#grid</a>
   */
  public static MediaFeature grid(boolean grid) {
    return grid ? GRID : NOT_GRID;
  }

  // To prevent instantiation outside of the class
  private MediaFeature(String css) {
    super(css);
  }
}
