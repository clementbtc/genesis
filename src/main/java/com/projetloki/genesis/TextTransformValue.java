package com.projetloki.genesis;

/**
 * Enum for the text-transform property.
 * Specifies how to capitalize an element's text. It can be used to make text
 * appear in all-uppercase or all-lowercase, or with each word capitalized.
 *
 * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/text-transform">MDN</a>
 *
 * @author Clément Roux
 */
public enum TextTransformValue {
  /**
   * Forces the first letter of each word to be converted to uppercase. Other
   * characters are unchanged; that is, they retain their original case as
   * written in the element's text.
   */
  CAPITALIZE("capitalize"),
  /** Converts all characters to lowercase. */
  LOWERCASE("lowercase"),
  /** No capitalization effect. */
  NONE("none"),
  /** Converts all characters to uppercase. */
  UPPERCASE("uppercase"),
  INHERIT("inherit");
  final String css;

  private TextTransformValue(String css) {
    this.css = css;
  }

  @Override public String toString() {
    return css;
  }
}
