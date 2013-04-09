package com.projetloki.genesis.image;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * Skeleton implementation of {@code Shape}, to minimize the effort required to
 * implement the serialization proxy pattern.
 *
 * <p>
 * As this implementation extends {@link HashCachingShape}, the same
 * conditions apply, in particular a equal to b implies
 * {@code a.getClass() == b.getClass()}.</p>
 *
 * <p>
 * This is and should remain INTERNAL, make sure that this class does not leak
 * into the API.</p>
 *
 * @param <T> the type itself
 *
 * @author Clément Roux
 */
abstract class ShapeWithSerializationProxy<T>
    extends HashCachingShape<T> implements Serializable {
  /** Returns the serialization proxy for this instance. */
  abstract SerializationProxy<? super T> doWriteReplace();

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
