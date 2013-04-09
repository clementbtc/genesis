package com.projetloki.genesis;

/**
 * Enum for the text-align property.
 * Describes how inline content like text is aligned in its parent block
 * element. Does not control the alignment of block elements itself, only
 * their inline content.
 *
 * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/text-align">MDN</a>
 *
 * @author Clément Roux
 */
public enum TextAlignValue {
  /** The inline contents are centered within the line box. */
  CENTER("center"),
  /**
   * The text is justified. Text should line up their left and right edges to
   * the left and right content edges of the paragraph.
   */
  JUSTIFY("justify"),
  /**
   * The inline contents are aligned to the left edge of the line box.
   *
   * <p>
   * Important: in most cases, what you really want is {@link #START}.</p>
   */
  LEFT("left"),
  /**
   * The inline contents are aligned to the right edge of the line box.
   *
   * <p>
   * Important: in most cases, what you really want is {@link #END}.</p>
   */
  RIGHT("right"),
  /**
   * Left if direction is left-to-right and right if direction is right-to-left.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/text-align#Browser_compatibility">Browser compatibility</a>
   */
  @PoorBrowserSupport
  START("start"),
  /**
   * Right if direction is left-to-right and left if direction is right-to-left.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/text-align#Browser_compatibility">Browser compatibility</a>
   */
  @PoorBrowserSupport
  END("end"),
  /**
   * Similar to inherit with the difference that the value start and end are
   * calculated according the parent's direction and are replaced by the
   * adequate left or right value.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/text-align#Browser_compatibility">Browser compatibility</a>
   */
  @PoorBrowserSupport
  MATCH_PARENT("match-parent"),
  INHERIT("inherit");
  final String css;

  private TextAlignValue(String css) {
    this.css = css;
  }

  @Override public String toString() {
    return css;
  }
}
