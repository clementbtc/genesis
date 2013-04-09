package com.projetloki.genesis;

/**
 * Enum for the clear property.
 * Specifies whether an element can be next to floating elements that precede it
 * or must be moved down (cleared) below them.
 *
 * <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/clear">MDN</a>
 *
 * @author Clément Roux
 */
public enum ClearValue {
  /** The element is not moved down to clear past floating elements. */
  NONE("none"),
  /** The element is moved down to clear past left floats. */
  LEFT("left"),
  /** The element is moved down to clear past right floats. */
  RIGHT("right"),
  /** The element is moved down to clear past both left and right floats. */
  BOTH("both");
  final String css;

  private ClearValue(String css) {
    this.css = css;
  }

  @Override public String toString() {
    return css;
  }
}
