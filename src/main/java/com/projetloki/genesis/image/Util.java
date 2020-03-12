package com.projetloki.genesis.image;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.annotation.Nullable;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;

/**
 * Various utility methods.
 *
 * @author Clément Roux
 */
final class Util {
  /** Returns whether the given double is an integer. */
  static boolean isInt(double x) {
    return x == (int) x;
  }

  @Nullable
  static File asFileOrNull(URL source) {
    if (source.getProtocol().equals("file")) {
      String pathname = source.getFile();
      return new File(pathname);
    }
    return null;
  }

  static HashCode hash(final URL url) throws IOException {
    return asInputSupplier(url).hash(Hashing.murmur3_128());
  }

  static ByteSource asInputSupplier(final File file) {
    checkNotNull(file);
    return new ByteSource() {
      @Override public InputStream openStream() throws IOException {
        return new FileInputStream(file);
      }
    };
  }

  static ByteSource asInputSupplier(final URL url) {
    checkNotNull(url);
    return new ByteSource() {
      @Override public InputStream openStream() throws IOException {
        return url.openStream();
      }
    };
  }

  static ByteSink asOutputSupplier(final File file) {
    checkNotNull(file);
    return new  ByteSink() {
      @Override public OutputStream openStream() throws IOException {
        return new FileOutputStream(file);
      }
    };
  }

  // To prevent instantiation
  private Util() {}
}
