package com.projetloki.genesis.image;

import java.io.Serializable;

/**
 * A serialization proxy, as defined in Josh Blosh's Effective Java, Item 78.
 *
 * @author Clément Roux
 */
abstract class SerializationProxy<T> implements Serializable {
  abstract T doReadResolve() throws Exception;

  /** Do not call. Can't be private, see {@link java.io.Serializable} doc. */
  Object readResolve() throws Exception {
    return doReadResolve();
  }

  private static final long serialVersionUID = 0;
}
