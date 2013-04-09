package com.projetloki.genesis;

/**
 * Enum for the font-style property.
 * Selects between normal, italic and oblique faces within a font family.
 *
 * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/font-style">MDN</a>
 *
 * @author Clément Roux
 */
public enum FontStyleValue {
  ITALIC("italic"),
  NORMAL("normal"),
  OBLIQUE("oblique"),
  INHERIT("inherit");
  final String css;

  private FontStyleValue(String css) {
    this.css = css;
  }

  @Override public String toString() {
    return css;
  }
}
