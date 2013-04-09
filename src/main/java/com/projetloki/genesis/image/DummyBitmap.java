package com.projetloki.genesis.image;

/**
 * A mutable bitmap.
 *
 * @author Clément Roux
 */
final class DummyBitmap implements HasSize {
  private final int width;
  private final int height;
  private final byte[] bitmap;

  public DummyBitmap(int width, int height) {
    this.width = width;
    this.height = height;
    bitmap = new byte[width * height];
  }

  @Override public int width() {
    return width;
  }

  @Override public int height() {
    return height;
  }

  public byte get(int i, int j) {
    if (0 <= i && i < width && 0 <= j && j < height) {
      return bitmap[j * width + i];
    }
    return 0;
  }

  public void set(int i, int j, byte value) {
    if (0 <= i && i < width && 0 <= j && j < height) {
     bitmap[j * width + i] = value;
    }
  }

  public void fill() {
    for (int i = 0; i < bitmap.length; i++) {
      bitmap[i] = 1;
    }
  }
}
