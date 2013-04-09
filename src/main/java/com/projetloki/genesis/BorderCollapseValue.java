package com.projetloki.genesis;

/**
 * Enum for the border-collapse property.
 * Selects a table's border model. This has a big influence on the look and
 * style of the table cells.
 *
 * <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/border-collapse">MDN</a>
 *
 * @author Clément Roux
 */
public enum BorderCollapseValue {
  /** Requests the use of the collapsed-border table rendering model. */
  COLLAPSE("collapse"),
  /** Requests the use of the separated-border table rendering model. */
  SEPARATE("separate"),
  INHERIT("inherit");
  final String css;

  private BorderCollapseValue(String css) {
    this.css = css;
  }

  @Override public String toString() {
    return css;
  }
}
