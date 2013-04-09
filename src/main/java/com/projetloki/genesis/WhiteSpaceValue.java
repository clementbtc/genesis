package com.projetloki.genesis;

/**
 * Enum for the white-space property.
 * Used to to describe how whitespace inside the element is handled.
 *
 * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/white-space">MDN</a>
 *
 * @author Clément Roux
 */
public enum WhiteSpaceValue {
  /**
   * Sequences of whitespace are collapsed. Newline characters in the source are
   * handled as other whitespace. Breaks lines as necessary to fill line boxes.
   */
  NORMAL("normal"),
  /**
   * Collapses whitespace as for normal, but suppresses line breaks (text
   * wrapping) within text.
   */
  NOWRAP("nowrap"),
  /**
   * Sequences of whitespace are preserved, lines are only broken at newline
   * characters in the source and at &lt;br&gt; elements.
   */
  PRE("pre"),
  /**
   * Sequences of whitespace are collapsed. Lines are broken at newline
   * characters, at &lt;br&gt;, and as necessary to fill line boxes.
   */
  PRE_LINE("pre-line"),
  /**
   * Sequences of whitespace are preserved. Lines are broken at newline
   * characters, at &lt;br&gt;, and as necessary to fill line boxes.
   */
  PRE_WRAP("pre-wrap"),
  INHERIT("inherit");
  final String css;

  private WhiteSpaceValue(String css) {
    this.css = css;
  }

  @Override public String toString() {
    return css;
  }
}
