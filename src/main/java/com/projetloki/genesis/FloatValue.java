package com.projetloki.genesis;

/**
 * Enum for the float property.
 * Specifies that an element should be taken from the normal flow and placed
 * along the left or right side of its container, where text and inline elements
 * will wrap around it. A floating element is one where the computed value of
 * float is not none.
 *
 * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/float">MDN</a>
 * @author Clément Roux
 */
public enum FloatValue {
  /** Indicates that the element must not float. */
  NONE("none"),
  /**
   * Indicates that the element must float on the left side of its containing
   * block.
   */
  LEFT("left"),
  /**
   * Indicates that the element must float on the right side of its containing
   * block.
   */
  RIGHT("right");
  final String css;

  private FloatValue(String css) {
    this.css = css;
  }

  @Override public String toString() {
    return css;
  }
}
