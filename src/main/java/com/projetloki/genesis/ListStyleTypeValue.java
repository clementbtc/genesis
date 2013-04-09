package com.projetloki.genesis;

/**
 * Enum for the list-style-type property.
 * Specifies appearance of a list item element.
 *
 * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/list-style-type">MDN</a>
 *
 * @author Clément Roux
 */
public enum ListStyleTypeValue {
  /** A hollow circle (○). */
  CIRCLE("circle"),
  /** A filled circle (●). */
  DISC("disc"),
  /** A filled square (■). */
  SQUARE("square"),
  /** Traditional uppercase Armenian numbering. */
  ARMENIAN("armenian"),
  /** Decimal numbers, beginning with 1. For ordered lists. */
  DECIMAL("decimal"),
  /**
   * Decimal numbers padded by initial zeros (e.g., 01, 02, 03, ..., 98, 99).
   * For ordered lists.
   */
  DECIMAL_LEADING_ZERO("decimal-leading-zero"),
  /**
   * Traditional Georgian numbering
   * (an, ban, gan, ..., he, tan, in, in-an, ...).
   */
  GEORGIAN("georgian"),
  /** Lowercase ascii letters (a, b, c, ... z). */
  LOWER_ALPHA("lower-alpha"),
  /** Lowercase classical Greek (α, β, γ, ...) */
  LOWER_GREEK("lower-greek"),
  /** Lowercase ascii letters (a, b, c, ... z). */
  LOWER_LATIN("lower-latin"),
  /** Lowercase roman numerals (i, ii, iii, iv, v, etc.). */
  LOWER_ROMAN("lower-roman"),
  /** Uppercase ascii letters (A, B, C, ... Z). */
  UPPER_ALPHA("upper-alpha"),
  /** Uppercase ascii letters (A, B, C, ... Z). */
  UPPER_LATIN("upper-latin"),
  /** Uppercase roman numerals (I, II, III, IV, V, etc.). */
  UPPER_ROMAN("upper-roman"),
  /** No item marker is shown. */
  NONE("none"),
  INHERIT("inherit");
  final String css;

  private ListStyleTypeValue(String css) {
    this.css = css;
  }

  @Override public String toString() {
    return css;
  }
}
