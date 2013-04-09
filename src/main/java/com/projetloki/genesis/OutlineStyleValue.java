package com.projetloki.genesis;

/**
 * Enum for the outline-style property.
 * Used to set the style of the outline of an element. An outline is a line that
 * is drawn around elements, outside the border edge, to make the element stand
 * out.
 *
 * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/outline-style">MDN</a>
 *
 * @author Clément Roux
 */
public enum OutlineStyleValue {
  /** No outline (outline-width is 0). */
  NONE("none"),
  /** The outline is a series of dots. */
  DOTTED("dotted"),
  /** The outline is a series of short line segments. */
  DASHED("dashed"),
  /** The outline is a single line. */
  SOLID("solid"),
  /**
   * The outline is two single lines.
   * The outline-width is the sum of the two lines and the space between them.
   */
  DOUBLE("double"),
  /** The outline looks as though it were carved into the canvas. */
  GROOVE("groove"),
  /**
   * The opposite of groove:
   * the outline looks as though it were coming out of the canvas.
   */
  RIDGE("ridge"),
  /**
   * The outline makes the box look as though it were embedded in the canvas.
   */
  INSET("inset"),
  /**
   * The opposite of inset:
   * the outline makes the box look as though it were coming out of the canvas.
   */
  OUTSET("outset");
  final String css;

  private OutlineStyleValue(String css) {
    this.css = css;
  }

  @Override public String toString() {
    return css;
  }
}
