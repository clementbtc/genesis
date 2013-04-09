package com.projetloki.genesis;

/**
 * Enum for the direction property.
 * Should be set to match the direction of the text: rtl for Hebrew or Arabic
 * text and ltr for other scripts. This should normally be done as part of the
 * document (e.g., using the dir attribute in HTML) rather than through direct
 * use of CSS.
 *
 * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/direction">MDN</a>
 *
 * @author Clément Roux
 */
public enum DirectionValue {
  /** Left-to-right direction. */
  LTR("ltr"),
  /** Right-to-left direction. */
  RTL("rtl"),
  INHERIT("inherit");
  final String css;

  private DirectionValue(String css) {
    this.css = css;
  }

  @Override public String toString() {
    return css;
  }
}
