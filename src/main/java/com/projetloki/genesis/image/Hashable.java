package com.projetloki.genesis.image;

import com.google.common.hash.HashCode;

/**
 * An object with a hash code of arbitrary bit length. The risk of hash
 * collision must be so low that it can be considered to be zero.
 * The hash function is expected to be consistent between execution runs, so it
 * is safe to persist hash codes.
 *
 * <p>
 * In the context of image generation, hash codes can be used to determine very
 * quickly if an image has already been saved to the disk during a previous
 * execution run. This requires the hash code of the image to be saved as well,
 * for example in the file name or as meta information.</p>
 *
 * @see <a href="http://code.google.com/p/guava-libraries/wiki/HashingExplained">Hashing Explained</a>
 *
 * @author Clément Roux
 */
public interface Hashable {
  /**
   * Returns a hash code of arbitrary bit length for this object.
   * @see Hashable
   */
  HashCode hash();
}
