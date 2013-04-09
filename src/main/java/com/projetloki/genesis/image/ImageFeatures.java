package com.projetloki.genesis.image;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * A set of boolean parameters to indicate that an image has particular
 * features.
 *
 * <p>The goal is to draw images faster. For example, if an image is
 * x-uniform, the color does not depend on the x-coordinate. So
 * {@link Image#getColor(Point)} can be called (height) instead of (width) *
 * (height) times. The 1 * (height) rectangle obtained this way will be repeated
 * horizontally.</p>
 *
 * <p>Immutable.</p>
 */
final class ImageFeatures implements Serializable {
  // 4 boolean features => 16 different instances
  private static final ImageFeatures[] INSTANCES = new ImageFeatures[16];
  static {
    for (int value = 0; value < 16; value++) {
      INSTANCES[value] = new ImageFeatures(value);
    }
  }

  private static final int RASTER_MASK = 1;
  private static final int X_UNIFORM_MASK = 2;
  private static final int Y_UNIFORM_MASK = 4;
  private static final int OPAQUE_MASK = 8;

  private final int value;
  private final boolean raster;
  private final boolean xUniform;
  private final boolean yUniform;
  private final boolean opaque;

  /** Returns an instance with all parameters set to false. */
  static ImageFeatures start() {
    return INSTANCES[0];
  }

  private ImageFeatures(int value) {
    this.value = value;
    raster = (value & RASTER_MASK) != 0;
    xUniform = (value & X_UNIFORM_MASK) != 0;
    yUniform = (value & Y_UNIFORM_MASK) != 0;
    opaque = (value & OPAQUE_MASK) != 0;
  }

  /**
   * Returns whether the image is a raster image. The color does not vary within
   * every 1x1 square of a grid starting from the origin. Typically the case
   * with bitmaps.
   */
  boolean isRaster() {
    return raster;
  }

  /**
   * Returns whether the image is x-uniform, i.e. the color and transparency do
   * not depend on the x-coordinate.
   */
  boolean isXUniform() {
    return xUniform;
  }

  /**
   * Returns whether the image is y-uniform, i.e. the color and transparency do
   * not depend on the y-coordinate.
   */
  boolean isYUniform() {
    return yUniform;
  }

  /** Returns whether {@link Image#getAlpha(Point)} always evaluates to 1. */
  boolean isOpaque() {
    return opaque;
  }

  ImageFeatures withRaster() {
    return withRaster(true);
  }

  ImageFeatures withRaster(boolean raster) {
    int newValue = raster ? value | RASTER_MASK : value & ~RASTER_MASK;
    return INSTANCES[newValue];
  }

  ImageFeatures withXUniform() {
    return withXUniform(true);
  }

  ImageFeatures withXUniform(boolean xUniform) {
    int newValue = xUniform ? value | X_UNIFORM_MASK : value & ~X_UNIFORM_MASK;
    return INSTANCES[newValue];
  }

  ImageFeatures withYUniform() {
    return withYUniform(true);
  }

  ImageFeatures withYUniform(boolean yUniform) {
    int newValue = yUniform ? value | Y_UNIFORM_MASK : value & ~Y_UNIFORM_MASK;
    return INSTANCES[newValue];
  }

  ImageFeatures withOpaque() {
    return withOpaque(true);
  }

  ImageFeatures withOpaque(boolean opaque) {
    int newValue = opaque ? value | OPAQUE_MASK : value & ~OPAQUE_MASK;
    return INSTANCES[newValue];
  }

  ImageFeatures andRaster(boolean raster) {
    return withRaster(raster && this.raster);
  }

  ImageFeatures andXUniform(boolean xUniform) {
    return withXUniform(xUniform && this.xUniform);
  }

  ImageFeatures andYUniform(boolean yUniform) {
    return withYUniform(yUniform && this.yUniform);
  }

  ImageFeatures andOpaque(boolean opaque) {
    return withOpaque(opaque && this.opaque);
  }

  /** Ands all boolean parameters. */
  ImageFeatures and(ImageFeatures other) {
    int newValue = value & other.value;
    return INSTANCES[newValue];
  }

  @Override public String toString() {
    List<String> components = Lists.newArrayList();
    if (raster) {
      components.add("raster");
    }
    if (xUniform) {
      components.add("xUniform");
    }
    if (yUniform) {
      components.add("yUniform");
    }
    if (opaque) {
      components.add("opaque");
    }
    return components.toString();
  }

  static ImageFeatures internalGet(int value) {
    return INSTANCES[value];
  }

  private static class SerializationProxy implements Serializable {
    private final int value;
    SerializationProxy(int value) {
      this.value = value;
    }
    private Object readResolve() {
      return internalGet(value);
    }
  }
  private Object writeReplace() {
    return new SerializationProxy(value);
  }

  private void readObject(ObjectInputStream stream)
      throws InvalidObjectException {
    throw new InvalidObjectException("Use SerializationProxy");
  }
  private static final long serialVersionUID = 0;
}
