package com.projetloki.genesis;

/**
 * Enum for the display property.
 * Specifies the type of rendering box used for an element. In HTML, default
 * display property values are taken from behaviors described in the HTML
 * specifications or from the browser/user default stylesheet. The default value
 * in XHTML is inline.
 *
 * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/display">MDN</a>
 *
 * @author Clément Roux
 */
public enum DisplayValue {
  /** The element generates a block element box. */
  BLOCK("block"),
  /** The element generates one or more inline element boxes. */
  INLINE("inline"),
  /**
   * The element generates a block element box that will be flowed with
   * surrounding content as if it were a single inline box (behaving much like a
   * replaced element would).
   */
  INLINE_BLOCK("inline-block"),
  /**
   * Behaves like a &lt;table&gt; HTML element, but as an inline box, rather
   * than a block-level box. Inside the table box is a block-level context.
   */
  INLINE_TABLE("inline-table"),
  /**
   * The element generates a block box for the content and a separate list-item
   * inline box.
   */
  LIST_ITEM("list-item"),
  /**
   * If the run-in box contains a block box, same as block.
   * If a block box follows the run-in box, the run-in box becomes the first
   * inline box of the block box.
   * If a inline box follows, the run-in box becomes a block box.
   */
  RUN_IN("run-in"),
  /** Behaves like a &lt;table&gt; HTML element. */
  TABLE("table"),
  /** Behaves like a &lt;caption&gt; element. */
  TABLE_CAPTION("table-caption"),
  /** Behaves like a &lt;td&gt; element. */
  TABLE_CELL("table-cell"),
  /** Behaves like a &lt;col&gt; element. */
  TABLE_COLUMN("table-column"),
  /** Behaves like a &lt;colgroup&gt; element. */
  TABLE_COLUMN_GROUP("table-column-group"),
  /** Behaves like a &lt;tfoot&gt; element. */
  TABLE_FOOTER_GROUP("table-footer-group"),
  /** Behaves like a &lt;thead&gt; element. */
  TABLE_HEADER_GROUP("table-header-group"),
  /** Behaves like a &lt;tr&gt; element. */
  TABLE_ROW("table-row"),
  /** Behaves like a &lt;tbody&gt; element. */
  TABLE_ROW_GROUP("table-row-group"),
  /**
   * This value causes an element to not appear in the document.
   * It has no effect on layout.
   */
  NONE("none");
  final String css;

  private DisplayValue(String css) {
    this.css = css;
  }

  @Override public String toString() {
    return css;
  }
}
