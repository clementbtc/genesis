package com.projetloki.genesis;

/**
 * Enum for the caption-side property.
 * Positions the content of a table's &lt;caption&gt; on the specified side.
 *
 * <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/caption-side">MDN</a>
 *
 * @author Clément Roux
 */
public enum CaptionSideValue {
  /** The caption box will be below the table. */
  BOTTOM("bottom"),
  /** The caption box will be above the table. */
  TOP("top"),
  INHERIT("inherit");
  final String css;

  private CaptionSideValue(String css) {
    this.css = css;
  }

  @Override public String toString() {
    return css;
  }
}
