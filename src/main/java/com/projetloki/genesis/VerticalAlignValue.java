package com.projetloki.genesis;

/**
 * Enum for the vertical-align property.
 * Specifies the vertical alignment of an inline or table-cell element.
 *
 * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/vertical-align">MDN</a>
 *
 * @author Clément Roux
 */
public enum VerticalAlignValue {
  /** Aligns the baseline of the element with the baseline of its parent. */
  BASELINE("baseline"),
  /**
   * Align the bottom of the element and its descendants with the bottom of the
   * entire line.
   */
  BOTTOM("bottom"),
  /**
   * Aligns the middle of the element with the middle of lowercase letters in
   * the parent.
   */
  MIDDLE("middle"),
  /**
   * Aligns the baseline of the element with the subscript-baseline of its
   * parent.
   */
  SUB("sub"),
  /**
   * Aligns the baseline of the element with the superscript-baseline of its
   * parent.
   */
  SUPER("super"),
  /**
   * Aligns the bottom of the element with the bottom of the parent element's
   * font.
   */
  TEXT_BOTTOM("text-bottom"),
  /**
   * Aligns the top of the element with the top of the parent element's font.
   */
  TEXT_TOP("text-top"),
  /**
   * Align the top of the element and its descendants with the top of the entire
   * line.
   */
  TOP("top"),
  INHERIT("inherit");
  final String css;

  VerticalAlignValue(String css) {
    this.css = css;
  }

  @Override public String toString() {
    return css;
  }
}
