package com.projetloki.genesis.image;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.common.hash.PrimitiveSink;

/**
 * Skeleton implementation of {@code Shaper}, to minimize the effort and the
 * number of lines required to implement {@code #equals(Object)} and to cache
 * the hash code.
 *
 * <p>
 * Subclasses MUST satisfy this condition: given two instances a and b, if a
 * is equal to b then {@code a.getClass() == b.getClass()}.
 * This can seem obvious, but this is not at all. In fact, very common types
 * don't satisfy this condition, e.g. {@code List}. Two lists can be equal, and
 * yet one list is an {@code ArrayList} and the other a {@code LinkedList}.</p>
 *
 * <p>
 * This is and should remain INTERNAL, make sure that this class does not leak
 * into the API.</p>
 *
 * @param <T> the type itself
 *
 * @author Clément Roux
 */
abstract class HashCachingShaper<T> extends Shaper {
  private transient HashCode hash;

  /** Called by {@code #hash()}. The result will be cached. */
  abstract void doHash(PrimitiveSink sink);

  /**
   * Called by {@code #equals(Object)} if the other object is not null and has
   * the same class as {@code this}.
   */
  abstract boolean doEquals(T that);

  @Override public final HashCode hash() {
    HashCode result = hash;
    if (result == null) {
      Hasher hasher = Hashing.murmur3_128().newHasher();
      doHash(hasher);
      hash = result = hasher.hash();
    }
    return result;
  }

  @Override public final boolean equals(Object object) {
    if (object == this) {
      return true;
    } else if (object == null || object.getClass() != getClass()) {
      return false;
    }
    @SuppressWarnings("unchecked")
    T that = (T) object;
    return doEquals(that);
  }
}
