package com.projetloki.genesis;

/**
 * Enum for the list-style-position property.
 * Specifies the position of the marker box in the principal block box.
 *
 * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/list-style-position">MDN</a>
 *
 * @author Clément Roux
 */
public enum ListStylePositionValue {
  /**
   * The marker box is the first inline box in the principal block box,
   * after which the element's content flows.
   */
  INSIDE("inside"),
  /** The marker box is outside the principal block box. This is default. */
  OUTSIDE("outside"),
  INHERIT("inherit");
  final String css;

  private ListStylePositionValue(String css) {
    this.css = css;
  }

  @Override public String toString() {
    return css;
  }
}
