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
import com.google.common.io.ByteStreams;
import com.google.common.io.InputSupplier;
import com.google.common.io.OutputSupplier;

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
    return ByteStreams.hash(asInputSupplier(url), Hashing.murmur3_128());
  }

  static InputSupplier<InputStream> asInputSupplier(final File file) {
    checkNotNull(file);
    return new InputSupplier<InputStream>() {
      @Override public InputStream getInput() throws IOException {
        return new FileInputStream(file);
      }
    };
  }

  static InputSupplier<InputStream> asInputSupplier(final URL url) {
    checkNotNull(url);
    return new InputSupplier<InputStream>() {
      @Override public InputStream getInput() throws IOException {
        return url.openStream();
      }
    };
  }

  static OutputSupplier<OutputStream> asOutputSupplier(final File file) {
    checkNotNull(file);
    return new OutputSupplier<OutputStream>() {
      @Override public OutputStream getOutput() throws IOException {
        return new FileOutputStream(file);
      }
    };
  }

  // To prevent instantiation
  private Util() {}
}
