package com.projetloki.genesis;

/**
 * Enum for the text-decoration property.
 * Specifies the decorations that will be applied to the text content of an
 * element. They are rendered in the color specified by the element’s color
 * property.
 *
 * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/text-decoration">MDN</a>
 *
 * @author Clément Roux
 */
public enum TextDecorationValue {
  /** Each line of text has a line through the middle. */
  LINE_THROUGH("line-through"),
  /** Each line of text has a line above it. */
  OVERLINE("overline"),
  /** Each line of text is underlined. */
  UNDERLINE("underline");
  final String css;

  private TextDecorationValue(String css) {
    this.css = css;
  }

  @Override public String toString() {
    return css;
  }
}
