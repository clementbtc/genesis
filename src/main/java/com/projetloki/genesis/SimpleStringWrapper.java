package com.projetloki.genesis;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Object that wraps around a String.
 * This abstract class provides an implementation of the
 * {@link #equals(Object)}, {@link #hashCode()}, {@link #toString()} and
 * {@link #appendTo(StringBuilder)} methods.
 *
 * @author Clément Roux
 */
abstract class SimpleStringWrapper extends AppendableToNoContext {
  final String css;

  SimpleStringWrapper(String css) {
    this.css = checkNotNull(css);
  }

  @Override final void appendTo(StringBuilder out) {
    out.append(css);
  }

  @Override public final boolean equals(Object object) {
    return object instanceof SimpleStringWrapper &&
        (((SimpleStringWrapper) object).css).equals(css);
  }

  @Override public final int hashCode() {
    return css.hashCode() + 80803;
  }

  @Override public final String toString() {
    return css;
  }
}
