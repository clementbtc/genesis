package com.projetloki.genesis;

/**
 * Enum for the empty-cells property.
 * Specifies how user agent should render borders and backgrounds around cells
 * that have no visible content.
 *
 * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/empty-cells">MDN</a>
 *
 * @author Clément Roux
 */
public enum EmptyCellsValue {
  /** Indicates that no border or background should be drawn. */
  HIDE("hide"),
  /**
   * Indicates that borders and backgrounds should be drawn like in a normal
   * cells.
   */
  SHOW("show"),
  INHERIT("inherit");
  final String css;

  private EmptyCellsValue(String css) {
    this.css = css;
  }

  @Override public String toString() {
    return css;
  }
}
