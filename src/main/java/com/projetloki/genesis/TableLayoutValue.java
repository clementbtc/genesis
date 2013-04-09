package com.projetloki.genesis;

/**
 * Enum for the table-layout property.
 * Defines the algorithm to be used to layout the table cells, rows, and
 * columns.
 *
 * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/table-layout">MDN</a>
 *
 * @author Clément Roux
 */
public enum TableLayoutValue {
  /**
   * An automatic table layout algorithm is commonly used by most browsers for
   * table layout. The width of the table and its cells depends on the content
   * thereof.
   */
  AUTO("auto"),
  /**
   * Table and column widths are set by the widths of table and col elements or
   * by the width of the first row of cells. Cells in subsequent rows do not
   * affect column widths.
   */
  FIXED("fixed");
  final String css;

  private TableLayoutValue(String css) {
    this.css = css;
  }

  @Override public String toString() {
    return css;
  }
}
