package com.projetloki.genesis;

/**
 * Enum for the font-variant property.
 * Allows italic or oblique faces to be selected within a font-family.
 *
 * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/font-variant">MDN</a>
 *
 * @author Clément Roux
 */
public enum FontVariantValue {
  /** Specifies a normal font face. */
  NORMAL("normal"),
  /**
   * Specifies a font that is labeled as a small-caps font.
   * If a small-caps font is not available, some browsers will simulate a
   * small-caps font, i.e. by taking a normal font and replacing the lowercase
   * letters by scaled uppercase characters.
   */
  SMALL_CAPS("small-caps"),
  INHERIT("inherit");
  final String css;

  private FontVariantValue(String css) {
    this.css = css;
  }

  @Override public String toString() {
    return css;
  }
}
