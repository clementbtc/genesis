package com.projetloki.genesis;

/**
 * Enum for the border-style property.
 * Sets the style of the border on an element.
 * Borders are placed on top of the element’s background.
 *
 * <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/border-style">MDN</a>
 *
 * @author Clément Roux
 */
public enum BorderStyleValue {
  /** No border. Color and width are ignored (i.e., the border has width 0). */
  NONE("none"),
  /**
   * Same as 'none', but has different behavior in the border conflict
   * resolution rules for border-collapsed tables.
   */
  HIDDEN("hidden"),
  /** A series of round dots. */
  DOTTED("dotted"),
  /** A series of square-ended dashes. */
  DASHED("dashed"),
  /** A single line segment. */
  SOLID("solid"),
  /**
   * Two parallel solid lines with some space between them. The thickness of the
   * lines is not specified, but the sum of the lines and the space must equal
   * 'border-width'.
   */
  DOUBLE("double"),
  /** Looks as if it were carved in the canvas. */
  GROOVE("groove"),
  /** Looks as if it were coming out of the canvas. */
  RIDGE("ridge"),
  /**
   * Looks as if the content on the inside of the border is sunken into the
   * canvas.
   */
  INSET("inset"),
  /**
   * Looks as if the content on the inside of the border is coming out of the
   * canvas.
   */
  OUTSET("outset");
  final String css;

  private BorderStyleValue(String css) {
    this.css = css;
  }

  @Override public String toString() {
    return css;
  }
}
