package com.projetloki.genesis;

/**
 * Enum for the overflow property.
 * Specifies whether to clip content, render scroll bars or display overflow
 * content of a block-level element.
 *
 * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/overflow">MDN</a>
 *
 * @author Clément Roux
 */
public enum OverflowValue {
  /**
   * Depends on the user agent.
   * Desktop browsers like Firefox provide scrollbars if content overflows.
   */
  AUTO("auto"),
  /** The content is clipped and no scrollbars are provided. */
  HIDDEN("hidden"),
  /**
   * The content is clipped and desktop browsers use scrollbars,
   * whether or not any content is clipped.
   * This avoids any problem with scrollbars appearing and disappearing in a
   * dynamic environment. Printers may print overflowing content.
   */
  SCROLL("scroll"),
  /**
   * Default value.
   * Content is not clipped, it may be rendered outside the content box.
   */
  VISIBLE("visible");
  final String css;

  private OverflowValue(String css) {
    this.css = css;
  }

  @Override public String toString() {
    return css;
  }
}
