package com.projetloki.genesis;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A CSS length unit.
 *
 * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/length#Units">MDN</a>
 *
 * @author Clément Roux
 */
public enum LengthUnit {
  /** The font size of the element. Relative. */
  EM("em"),
  /** The x-height of the element's font. Relative. */
  EX("ex"),
  /**
   * The width of the "0" (ZERO, U+0030) glyph in the element's font.
   * Relative.
   */
  @PoorBrowserSupport
  CH("ch"),
  /** The font size of the root element. Relative. */
  // Seems to be pretty well supported
  REM("rem"),
  /** The viewport's width. Relative. */
  @PoorBrowserSupport
  VW("vw"),
  /** The viewport's height. Relative. */
  @PoorBrowserSupport
  VH("vh"),
  /** The minimum of the viewport's height and width. Relative. */
  @PoorBrowserSupport
  VMIN("vmin"),
  /** The maximum of the viewport's height and width. Relative. */
  @PoorBrowserSupport
  VMAX("vmax"),
  /** One pixel. Absolute. */
  PX("px"),
  /** One millimeter. Absolute. */
  MM("mm"),
  /** One centimeter. Absolute. */
  CM("cm"),
  /** One inch. Absolute. */
  IN("in"),
  /** One points. Absolute. */
  PT("pt"),
  /** One pica. Absolute. */
  PC("pc");
  private final String unitName;

  private LengthUnit(String unitName) {
    this.unitName = unitName;
  }

  /**
   * Returns the CSS length obtained by multiplying this unit with the given
   * magnitude.
   * @throws IllegalArgumentException if magnitude is NaN; or if magnitude is
   *     infinite
   */
  public String format(double magnitude) {
    checkArgument(!Double.isNaN(magnitude), "NaN");
    checkArgument(!Double.isInfinite(magnitude), "infinite");
    return Format.formatOrZero(magnitude, unitName);
  }

  /**
   * Returns the CSS length obtained by multiplying this unit with the given
   * magnitude.
   */
  public String format(int magnitude) {
    return Format.formatOrZero(magnitude, unitName);
  }

  /**
   * Same as {@link #format(double)} but requires the magnitude to be positive
   * or zero.
   */
  String formatPositive(double magnitude) {
    checkArgument(0 <= magnitude, "negative magnitude: %s", magnitude);
    return format(magnitude);
  }

  /**
   * Same as {@link #format(int)} but requires the magnitude to be positive or
   * zero.
   */
  String formatPositive(int magnitude) {
    checkArgument(0 <= magnitude, "negative magnitude: %s", magnitude);
    return format(magnitude);
  }
}
