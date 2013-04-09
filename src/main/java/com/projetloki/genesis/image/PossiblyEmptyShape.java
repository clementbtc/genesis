package com.projetloki.genesis.image;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import com.google.common.hash.HashCode;

/**
 * A shape that can be empty when shrunk. If the shape is empty, the
 * {@code #resolve} method returns an object with a very fast contains method
 * always evaluating to false. Unlike {@code Shapes#plane()}, it can be unshrunk
 * to obtain a non-empty shape.
 *
 * <p>
 * As this implementation extends {@link ShapeWithSerializationProxy}, the same
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
abstract class PossiblyEmptyShape<T> extends ShapeWithSerializationProxy<T> {
  /** Returns whether this shape contains no point. */
  abstract boolean isEmpty();

  /**
   * This method must be called every time a new instance is constructed.
   * If the shape contains no point, returns a faster implementation.
   */
  final Shape resolve() {
    return isEmpty() ? new EmptyWrapper<T>(this) : this;
  }

  private static class EmptyWrapper<T> extends Shape implements Serializable {
    final PossiblyEmptyShape<T> delegate;

    EmptyWrapper(PossiblyEmptyShape<T> delegate) {
      this.delegate = checkNotNull(delegate);
    }

    @Override public boolean contains(Point p) {
      return false;
    }

    @Override public Shape shrink(double margin) {
      return delegate.shrink(margin);
    }

    @Override ImageFeatures features() {
      return ImageFeatures.start().withRaster().withXUniform().withYUniform();
    }

    @Override public HashCode hash() {
      return delegate.hash();
    }

    @Override public boolean equals(Object object) {
      return object instanceof EmptyWrapper &&
          (((EmptyWrapper<?>) object).delegate).equals(delegate);
    }

    private Object writeReplace() {
      return delegate;
    }

    private static final long serialVersionUID = 0;
  }
}
