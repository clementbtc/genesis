package com.projetloki.genesis;

/**
 * Enum for the font-weight property.
 * Specifies the weight or boldness of the font. Some fonts are not available in
 * all weights; some are available only on normal and bold.
 *
 * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/font-weight">MDN</a>
 *
 * @author Clément Roux
 */
public enum FontWeightValue {
  /** Bold font weight. */
  BOLD("bold"),
  /**
   * One font weight darker than the parent element
   * (among the available weights of the font).
   */
  BOLDER("bolder"),
  /**
   * One font weight lighter than the parent element
   * (among the available weights of the font).
   */
  LIGHTER("lighter"),
  /** Normal font weight. */
  NORMAL("normal"),
  INHERIT("inherit");
  final String css;

  private FontWeightValue(String css) {
    this.css = css;
  }

  @Override public String toString() {
    return css;
  }
}
