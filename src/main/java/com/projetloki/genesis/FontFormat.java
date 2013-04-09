package com.projetloki.genesis;

/**
 * Specifies the format of a source in a font-face rule.
 * Though specifying the format is not required, it is highly recommended to
 * allow browsers not to download font files (which can be very big) in formats
 * they can't handle.
 *
 * <p>
 * Note: Because there are no defined MIME types for TrueType, OpenType, and Web
 * Open File Format (WOFF) fonts, the MIME type of the file specified is not
 * considered.</p>
 *
 * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/@font-face">MDN</a>
 *
 * @author Clément Roux
 */
public enum FontFormat {
  WOFF("woff"),
  TRUETYPE("truetype"),
  OPENTYPE("opentype"),
  EMBEDDED_OPENTYPE("embedded-opentype"),
  SVG("svg");
  final String string;

  private FontFormat(String string) {
    this.string = string;
  }

  @Override public String toString() {
    return string;
  }
}
