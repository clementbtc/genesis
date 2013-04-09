package com.projetloki.genesis.image;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * Skeleton implementation of {@code Image}, to minimize the effort required to
 * implement the serialization proxy pattern.
 *
 * <p>
 * Subclasses must invoke {@code #initSize} in their constructors. Otherwise, an
 * assertion error will be thrown when the dimensions of the image are
 * requested.</p>
 *
 * <p>
 * As this implementation extends {@link HashCachingImage}, the same
 * conditions apply, in particular a equal to b implies
 * {@code a.getClass() == b.getClass()}.</p>
 *
 * <p>
 * This is and should remain INTERNAL, make sure that this class does not leak
 * into the API.</p>
 *
 * @param <T> the type itself
 *
 * @see PointTransformingImage
 * @author Clément Roux
 */
abstract class ImageWithSerializationProxy<T> extends HashCachingImage<T>
    implements Serializable {
  private int width = -1;
  private int height = -1;

  /** Returns the serialization proxy for this instance. */
  abstract SerializationProxy<? super T> doWriteReplace();

  /**
   * Sets the width and the height of the image. Either this method or
   * {@link #initSize(int, int)} must be called in subclasses' constructors.
   */
  final void initSize(HasSize other) {
    initSize(other.width(), other.height());
  }

  /**
   * Sets the width and the height of the image. Either this method or
   * {@link #initSize(HasSize)} must be called in subclasses'
   * constructors.
   */
  final void initSize(int width, int height) {
    if (0 <= this.width) {
      throw new AssertionError();
    }
    checkArgument(0 <= width, "width: %s", width);
    checkArgument(0 <= height, "height: %s", height);
    this.width = width;
    this.height = height;
  }

  @Override public final int width() {
    if (width == -1) {
      throw new AssertionError();
    }
    return width;
  }

  @Override public final int height() {
    if (height == -1) {
      throw new AssertionError();
    }
    return height;
  }

  /** Do not call. Can't be private, see {@link java.io.Serializable} doc. */
  final Object writeReplace() {
    return doWriteReplace();
  }

  /** Do not call. Can't be private, see {@link java.io.Serializable} doc. */
  final void readObject(ObjectInputStream stream)
      throws InvalidObjectException {
    throw new InvalidObjectException("Use SerializationProxy");
  }
}
