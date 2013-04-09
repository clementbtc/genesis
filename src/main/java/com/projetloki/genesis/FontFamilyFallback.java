package com.projetloki.genesis;

/**
 * A font family that is guaranteed to be available on any browser and any
 * platform. It is also called a <em>generic font family</em>.
 *
 * <p>
 * Genesis forces you to specify a fallback when
 * {@linkplain Properties.Builder#setFontFamily(FontFamilyFallback, String...) setting}
 * the font-family property with a builder, in case all other font families are
 * not available. It must be specified first in the Java code, because Java does
 * not allow vararg parameters to precede other parameters. In the CSS code, the
 * fallback ends the list.</p>
 *
 * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/font-family">MDN</a>
 *
 * @author Clément Roux
 */
public enum FontFamilyFallback {
  /**
   * Glyphs have finishing strokes, flared or tapering ends, or have actual
   * serifed endings.
   */
  SERIF("serif"),
  /** Glyphs have stroke endings that are plain. */
  SANS_SERIF("sans-serif"),
  /**
   * Glyphs in cursive fonts generally have either joining strokes or other
   * cursive characteristics beyond those of italic typefaces. The glyphs are
   * partially or completely connected, and the result looks more like
   * handwritten pen or brush writing than printed letterwork.
   */
  CURSIVE("cursive"),
  /**
   * Fantasy fonts are primarily decorative fonts that contain playful
   * representations of characters.
   */
  FANTASY("fantasy"),
  /** All glyphs have the same fixed width. */
  MONOSPACE("monospace");
  final String css;

  FontFamilyFallback(String css) {
    this.css = css;
  }

  @Override public String toString() {
    return css;
  }
}
