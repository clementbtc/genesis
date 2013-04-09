package com.projetloki.genesis;

/**
 * Enum for the position property.
 * Chooses alternative rules for positioning elements, designed to be useful for
 * scripted animation effects.
 *
 * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/position">MDN</a>
 *
 * @author Clément Roux
 */
public enum PositionValue {
  /**
   * Do not leave space for the element. Instead, position it at a specified
   * position relative to its closest positioned ancestor or to the containing
   * block. Absolutely positioned boxes can have margins, they do not collapse
   * with any other margins.
   */
  ABSOLUTE("absolute"),
  /**
   * Do not leave space for the element. Instead, position it at a specified
   * position relative to the screen's viewport and doesn't move when scrolled.
   * When printing, position it at that fixed position on every page.
   */
  FIXED("fixed"),
  /**
   * Lay out all elements as though the element were not positioned, and then
   * adjust the element's position, without changing layout (and thus leaving a
   * gap for the element where it would have been had it not been positioned).
   * The effect of position:relative on table-*-group, table-row, table-column,
   * table-cell, and table-caption elements is undefined.
   */
  RELATIVE("relative"),
  /**
   * Normal behavior.  The top, right, bottom, and left properties do not apply.
   */
  STATIC("static");
  final String css;

  PositionValue(String css) {
    this.css = css;
  }

  @Override public String toString() {
    return css;
  }
}
