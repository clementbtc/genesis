package com.projetloki.genesis.image;

/**
 * Enumeration of image formats.
 *
 * @author Clément Roux
 */
public enum ImageFormat {

  /**
   * The BMP image format. Most BMP files have a relatively large file size due
   * to lack of any compression. Does not support transparency. PNG is often a
   * better alternative.
   */
  BMP("bmp", "BMP"),

  /**
   * The GIF image format. The format supports up to 8 bits per pixel thus
   * allowing a single image to reference a palette of up to 256 distinct
   * colors. A pixel can only be fully opaque or transparent. PNG is often a
   * better alternative, though some old web
   * browsers do not support it.
   */
  GIF("gif", "GIF"),

  /**
   * The JPEG image format. It is a commonly used method of lossy compression
   * for digital photography.
   */
  JPEG("jpg", "JPEG"),

  /**
   * The PNG image format. The best bitmapped image format that employs lossless
   * data compression.
   */
  PNG("png", "PNG");
  private final String extension;
  private final String javaName;

  private ImageFormat(String extension, String javaName) {
    this.extension = extension;
    this.javaName = javaName;
  }

  /**
   * Returns the filename extension for this image format. The result does not
   * contain a dot, e.g. {@code "png"}
   */
  public String getExtension() {
    return extension;
  }

  String getJavaName() {
    return javaName;
  }
}
