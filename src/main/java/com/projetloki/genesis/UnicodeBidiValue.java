package com.projetloki.genesis;

/**
 * Enum for the unicode-bidi property.
 * Together with the direction property, relates to the handling of
 * bidirectional text in a document. For example, if a block of text contains
 * both left-to-right and right-to-left text then the user-agent uses a complex
 * Unicode algorithm to decide how to display the text. This property overrides
 * this algorithm and allows the developer to control the text embedding.
 *
 * <p>Note: This property is intended for DTD designers. Web designers and
 * similar authors should not override it.</p>
 *
 * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/unicode-bidi">MDN</a>
 * @author Clément Roux
 */
public enum UnicodeBidiValue {
  /**
   * For inline elements this creates an override. For block container elements
   * this creates an override for inline-level descendants not within another
   * block container element. This means that inside the element, reordering is
   * strictly in sequence according to the direction property; the implicit part
   * of the bidirectional algorithm is ignored.
   */
  BIDI_OVERRIDE("bidi-override"),
  /**
   * If the element is inline, this value opens an additional level of embedding
   * with respect to the bidirectional algorithm. The direction of this
   * embedding level is given by the direction property.
   */
  EMBED("embed"),
  /**
   * The element does not offer a additional level of embedding with respect to
   * the bidirectional algorithm. For inline elements implicit reordering works
   * across element boundaries.
   */
  NORMAL("normal"),
  INHERIT("inherit");
  final String css;

  private UnicodeBidiValue(String css) {
    this.css = css;
  }

  @Override public String toString() {
    return css;
  }
}
