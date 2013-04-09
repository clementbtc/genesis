package com.projetloki.genesis;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableMap;

/**
 * Pseudo-elements match virtual elements that don’t exist explicitly in the
 * document tree. They allow you to style certain parts of a document. For
 * example, the {@linkplain #FIRST_LINE first-line} pseudo-element targets only
 * the first line of an element specified by the selector.
 *
 * @see Selector#on(PseudoElement)
 * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/Pseudo-elements">MDN</a>
 *
 * @author Clément Roux
 */
public enum PseudoElement {
  /**
   * Selects the first letter of the first line of a block, if it is not
   * preceded by any other content (such as images or inline tables) on its
   * line.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/::first-letter">MDN</a>
   */
  FIRST_LETTER("first-letter", false),
  /**
   * Applies styles only to the first line of an element. The amount of the text
   * on the first line depends of numerous factors, like the width of the
   * elements or of the document, but also of the font size of the text. As all
   * pseudo-elements, the selectors containing ::first-line does not match any
   * real HTML element.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/::first-line">MDN</a>
   */
  FIRST_LINE("first-line", false),
  /**
   * Creates a pseudo-element that is the first child of the element matched.
   * Often used to add cosmetic content to an element, by using the content
   * property. This element is inline by default.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/::before">MDN</a>
   */
  BEFORE("before", false),
  /**
   * Matches a virtual last child of the selected element. Typically used to
   * add cosmetic content to an element, by using the content CSS property.
   * This element is inline by default.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/::after">MDN</a>
   */
  AFTER("after", false),
  /**
   * Applies rules to the portion of a document that has been highlighted
   * (e.g., selected with the mouse or another pointing device) by the user.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/::selection">MDN</a>
   * @see <a href="http://caniuse.com/css-sel3">Browser support</a>
   */
  @PoorBrowserSupport
  SELECTION("selection", true);
  static final ImmutableMap<String, PseudoElement> STRING_TO_CONSTANT =
      ImmutableMap.of(
          "first-letter", FIRST_LETTER,
          "first-line", FIRST_LINE,
          "before", BEFORE,
          "after", AFTER,
          "selection", SELECTION);

  private final boolean css3;
  private final String string;

  private PseudoElement(String string, boolean css3) {
    this.string = checkNotNull(string);
    this.css3 = css3;
  }

  @Override public String toString() {
    return string;
  }

  /** Returns whether this pseudo-element is CSS3-introduced. */
  boolean css3() {
    return css3;
  }
}
