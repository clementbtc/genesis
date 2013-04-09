package com.projetloki.genesis.image;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.Graphics;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteStreams;
import com.google.common.util.concurrent.ExecutionError;
import com.google.common.util.concurrent.UncheckedExecutionException;

/**
 * An image loaded from a file.
 *
 * @author Clément Roux
 */
abstract class RasterImage extends Image {
  /** See {@link Images#load(URL)}. */
  static RasterImage load(URL source) throws IOException {
    return load(source.toString());
  }

  /** See {@link Images#load(File)}. */
  static RasterImage load(File source) throws IOException {
    return load(source.toURI().toString());
  }

  static RasterImage load(String source) throws IOException {
    try {
      return globalCache.get(source);
    } catch (UncheckedExecutionException ex) {
      throw (RuntimeException) ex.getCause();
    } catch (ExecutionException ex) {
      throw (IOException) ex.getCause();
    } catch (ExecutionError error) {
      throw (Error) error.getCause();
    }
  }

  private static final LoadingCache<String, RasterImage>
      globalCache = CacheBuilder.newBuilder()
          .weakValues()
          .build(new CacheLoader<String, RasterImage>() {
            @Override public RasterImage load(String source)
                throws IOException {
              return doLoad(new URL(source));
            }
          });

  static RasterImage doLoad(URL source) throws IOException {
    HashCode hash = Util.hash(source);
    InputStream is = source.openStream();
    try {
      ImageInputStream iis = ImageIO.createImageInputStream(is);
      boolean jpeg = RasterImage.isJpeg(iis);
      BufferedImage javaIm = ImageIO.read(iis);
      if (javaIm == null) {
        throw new IOException("unknown image format: " + source);
      }
      return new RasterImageFromUrl(javaIm, jpeg, source, hash);
    } finally {
      is.close();
    }
  }

  static void notifyWrite(File file) {
    globalCache.invalidate(file.toURI().toString());
  }

  /** See {@link Images#forJavaImage(java.awt.Image)}. */
  static RasterImage forJavaImage(java.awt.Image javaIm) {
    if (javaIm instanceof BufferedImage) {
      return new JavaImageWrapper((BufferedImage) javaIm);
    }
    java.awt.Image im = new ImageIcon(javaIm).getImage();
    int width = im.getWidth(null);
    int height = im.getHeight(null);
    int type = BufferedImage.TYPE_INT_ARGB;
    BufferedImage delegate = new BufferedImage(width, height, type);
    Graphics g = delegate.createGraphics();
    try {
      g.drawImage(im, 0, 0, null);
    } finally {
      g.dispose();
    }
    return new JavaImageWrapper(delegate);
  }

  final BufferedImage javaIm;
  final boolean jpeg;

  RasterImage(BufferedImage javaIm, boolean jpeg) {
    this.javaIm = checkNotNull(javaIm);
    this.jpeg = jpeg;
  }

  @Override public final int width() {
    return javaIm.getWidth();
  }

  @Override public final int height() {
    return javaIm.getHeight();
  }

  @Override public final Color getColor(Point p) {
    int x = (int) (p.x);
    int y = (int) (p.y);
    int rgb = javaIm.getRGB(x, y);
    int r = (rgb >> 16) & 0xff;
    int g = (rgb >> 8) & 0xff;
    int b = rgb & 0xff;
    return new Color(r, g, b);
  }

  @Override public final double getAlpha(Point p) {
    int x = (int) (p.x);
    int y = (int) (p.y);
    int rgb = javaIm.getRGB(x, y);
    int a = (rgb >> 24) & 0xff;
    return a / 255d;
  }

  @Override final ImageFeatures features() {
    int transparency = javaIm.getColorModel().getTransparency();
    return ImageFeatures.start()
        .withRaster()
        .withOpaque(transparency == Transparency.OPAQUE);
  }

  @Override ImageFormat bestFormat() {
    return jpeg ? ImageFormat.JPEG : ImageFormat.PNG;
  }

  @Override final void drawSubimage(BufferedImage out, Rect rect) {
    if (rect.isEmpty()) {
      return;
    }
    Graphics g = out.createGraphics();
    try {
      g.drawImage(rect.getSubimage(javaIm), 0, 0, null);
    } finally {
      g.dispose();
    }
  }

  private static class JavaImageWrapper extends RasterImage {
    private volatile HashCode hash;

    JavaImageWrapper(BufferedImage javaIm) {
      super(javaIm, false);
    }

    @Override public HashCode hash() {
      HashCode result = hash;
      if (result == null) {
        Hasher hasher = Hashing.murmur3_128().newHasher();
        hasher.putLong(9044153269869625857L);
        hasher.putInt(width());
        hasher.putInt(height());
        for (int i = 0; i < width(); i++) {
          for (int j = 0; j < height(); j++) {
            hasher.putInt(javaIm.getRGB(i, j));
          }
        }
        hash = result = hasher.hash();
      }
      return result;
    }

    @Override public String toString() {
      RasterImage that = this;
      return "forJavaImage(" + that.javaIm + ")";
    }
  }

  private static class RasterImageFromUrl extends RasterImage {
    private final URL source;
    private final HashCode hash;

    RasterImageFromUrl(BufferedImage javaIm, boolean jpeg, URL source,
        HashCode hash) {
      super(javaIm, jpeg);
      this.source = checkNotNull(source);
      this.hash = checkNotNull(hash);
    }

    @Override public void save(File out, ImageFormat format)
        throws IOException {
      if (jpeg && format == ImageFormat.JPEG) {
        // Don't re-encode a JPEG image
        if (source.toString().equals(out.toURI().toString())) {
          return;
        }
        ByteStreams.copy(Util.asInputSupplier(source),
            Util.asOutputSupplier(out));
      } else {
        Images.doSave(this, out, format);
      }
    }

    @Override public HashCode hash() {
      return hash;
    }

    @Override public String toString() {
      return "load(" + source + ")";
    }
  }

  private static boolean isJpeg(ImageInputStream iis) throws IOException {
    Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
    while (readers.hasNext()) {
      ImageReader reader = readers.next();
      if (reader.getFormatName().equals("JPEG")) {
        return true;
      }
    }
    return false;
  }
}
