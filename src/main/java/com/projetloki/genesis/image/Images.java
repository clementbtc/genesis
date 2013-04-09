package com.projetloki.genesis.image;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;

import javax.imageio.ImageIO;

import com.google.common.hash.PrimitiveSink;

/**
 * Static utility methods pertaining to {@link Image} instances.
 *
 * @author Clément Roux
 */
public final class Images {
  /**
   * Returns a blank image filled with the specified color.
   *
   * <p>The result is serializable.</p>
   * @param width the width of the canvas. Must be positive.
   * @param height the height of the canvas. Must be positive.
   * @param color the color to fill the canvas with
   * @see #canvas(int, int, Color, double)
   */
  public static Image canvas(int width, int height, Color color) {
    return canvas(width, height, color, 1);
  }

  /**
   * Returns a blank image filled with the specified color, and with the
   * specified opacity.
   *
   * <p>The result is serializable.</p>
   * @param width the width of the canvas. Must be positive.
   * @param height the height of the canvas. Must be positive.
   * @param color the color to fill the canvas with
   * @param alpha the opacity of the canvas. 1 is for fully opaque, 0 is for
   *     transparent. Must be in [0, 1]
   */
  public static Image canvas(int width, int height, Color color, double alpha) {
    checkNotNull(color);
    checkArgument(width >= 0, "width: %s", width);
    checkArgument(height >= 0, "height: %s", height);
    checkArgument(0 <= alpha && alpha <= 1, "alpha: %s", alpha);
    return new Canvas(width, height, color, alpha);
  }

  /**
   * Loads an image from an image file. Accepted formats are JPEG, PNG, BMP,
   * WBMP, and GIF.
   *
   * @param source the file to load the image from
   * @throws IOException if an error occurs during loading
   * @see #load(URL)
   */
  public static Image load(File source) throws IOException {
    return RasterImage.load(source);
  }

  /**
   * Loads an image from the image file at the given URL. Accepted formats are
   * JPEG, PNG, BMP, WBMP, and GIF.
   *
   * <p>Use this method to load an image from a Java resource. Example:
   * <pre>{@code Images.load(MyClass.class.getResource("resource.png"))}</pre>
   * </p>
   *
   * @param source the URL to load the image from
   * @throws IOException if an error occurs during loading
   * @see #load(File)
   */
  public static Image load(URL source) throws IOException {
    return RasterImage.load(source);
  }

  /**
   * Converts a Java image to an image. Ensures that all the pixels in the image
   * are loaded before returning.
   * @param javaIm a Java image
   * @return an image that is equivalent to the given Java image
   */
  public static Image forJavaImage(java.awt.Image javaIm) {
    return RasterImage.forJavaImage(javaIm);
  }

  /**
   * Draws the image on the given Java buffered image, at the specified
   * position. Transparent pixels are copied as-is, which simply means that the
   * image is not drawn "on top" of the Java buffered image.
   *
   * <p>The image is first rasterized and anti-aliased using
   * <a href="http://en.wikipedia.org/wiki/Super-sampling">supersampling</a>.
   * </p>
   * @param im the image to draw
   * @param out the Java buffered image, to draw the image on
   * @param x the x-coordinate in the Java buffered image
   * @param y the y-coordinate in the Java buffered image
   */
  public static void draw(Image im, BufferedImage out, int x, int y) {
    if (x != 0 || y != 0) {
      out = out.getSubimage(x, y, out.getWidth() - x, out.getHeight() - y);
    }
    im.drawSubimage(out, Rect.atOrigin(im));
  }

  // Unlike #draw(Image, BufferedImage, x, y), the coordinates refer to the
  // source and not the destination.
  static void doDrawSubimage(Image im, BufferedImage out, Rect rect) {
    if (rect.isEmpty()) {
      return;
    }
    checkArgument(rect.width() <= im.width(), rect);
    checkArgument(rect.height() <= im.height(), rect);
    int rectX = rect.x0();
    int rectY = rect.y0();
    int rectWidth = rect.width();
    int rectHeight = rect.height();
    ImageFeatures features = im.features();
    PixelColorGetter pcg = PixelColorGetter.getFor(features);
    if (features.isXUniform()) {
      if (features.isYUniform()) {
        // A canvas.
        int rgb = pcg.getRgb(im, rectX, rectY);
        for (int x = 0; x < rectWidth; x++) {
          for (int y = 0; y < rectHeight; y++) {
            out.setRGB(x, y, rgb);
          }
        }
      } else {
        // Identical columns.
        for (int y = 0; y < rectHeight; y++) {
          int rgb = pcg.getRgb(im, rectX, y + rectY);
          for (int x = 0; x < rectWidth; x++) {
            out.setRGB(x, y, rgb);
          }
        }
      }
    } else if (features.isYUniform()) {
      // Identical rows.
      for (int x = 0; x < rectWidth; x++) {
        int rgb = pcg.getRgb(im, x + rectY, rectY);
        for (int y = 0; y < rectHeight; y++) {
          out.setRGB(x, y, rgb);
        }
      }
    } else {
      for (int x = 0; x < rectWidth; x++) {
        for (int y = 0; y < rectHeight; y++) {
          int rgb = pcg.getRgb(im, x + rectX, y + rectY);
          out.setRGB(x, y, rgb);
        }
      }
    }
  }

  /**
   * Shows the image in a new pop-up window. The background color (for transparent
   * pixels) is white.
   * @param im the image to show
   * @see #show(Image, Color)
   */
  public static void show(Image im) {
    show(im, Color.WHITE);
  }

  /**
   * Shows the image in a new pop-up window. This method lets you specify a
   * background color for transparent pixels.
   * @param im the image to show
   */
  public static void show(final Image im, Color backgroundColor) {
    final BufferedImage bufIm = new BufferedImage(im.width(), im.height(),
        im.features().isOpaque() ?
            BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB);
    draw(im, bufIm, 0, 0);
    final java.awt.Color javaColor = new java.awt.Color(
        colorToRgb(backgroundColor, 1d));
    String title = im.width() + "x" + im.height();
    final Frame frame = new Frame(title) {
      @Override public void paint(Graphics g) {
        super.paint(g);
        g.drawImage(bufIm, 20, 40, javaColor, null);
      }
    };
    frame.setResizable(false);
    frame.addWindowListener(new WindowAdapter() {
      @Override public void windowClosing(WindowEvent e) {
        frame.setVisible(false);
        frame.dispose();
        System.exit(0);
      }
    });
    frame.setVisible(true);
    frame.setSize(im.width() + 40, im.height() + 60);
  }

  /**
   * Writes the image to the specified file. The format used is the result of
   * {@link #bestFormat(com.projetloki.genesis.image.Image)}
   * @param im the image to save
   * @param out the file to write the image to
   * @throws IOException if an error occurs during writing
   * @see #save(Image, File, ImageFormat)
   */
  public static void save(Image im, File out) throws IOException {
    save(im, out, bestFormat(im));
  }

  /**
   * Writes the image to the specified file in the specified format.
   * @param im the image to save
   * @param out the file to write the image to
   * @param format the format in which to save the image
   * @throws IOException if an error occurs during writing
   * @see #save(Image, File)
   */
  public static void save(Image im, File out, ImageFormat format)
      throws IOException {
    im.save(out, format);
    RasterImage.notifyWrite(out);
  }

  static void doSave(Image im, File out, ImageFormat format)
      throws IOException {
    int imageType = im.features().isOpaque() ?
        BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
    BufferedImage bufIm = new BufferedImage(im.width(), im.height(),
        imageType);
    draw(im, bufIm, 0, 0);
    String formatName = format.getJavaName();
    if (!ImageIO.write(bufIm, formatName, out)) {
      throw new IOException("no ImageWriter supporting " + formatName);
    }
  }

  /**
   * Tries to infer the best format in which to save the image. This method will
   * return {@link ImageFormat#JPEG} if the image was originally loaded from a
   * JPEG source, and PNG in all other cases.
   * @param im the image to save
   * @return the supposed best format in which to save the image
   */
  public static ImageFormat bestFormat(Image im) {
    return im.bestFormat();
  }

  static int colorToRgb(Color color, double alpha) {
    int a = (int)(Math.round(255 * alpha));
    return a << 24 | (0xff & color.red()) << 16 |
        (0xff & color.green()) << 8 | (0xff & color.blue());
  }

  static int colorToRgb(Color color) {
    return (0xff & color.red()) << 16 | (0xff & color.green()) << 8 |
        (0xff & color.blue());
  }

  private static enum PixelColorGetter {
    /** Faster implementation for raster images. */
    FAST {
      @Override public int getRgb(Image im, int x, int y) {
        Point p = new Point(x + 0.5d, y + 0.5d);
        Color color = im.getColor(p);
        double alpha = im.getAlpha(p);
        return colorToRgb(color, alpha);
      }
    },
    /** Even faster implementation for raster and opaque images. */
    OPAQUE_FAST {
      @Override public int getRgb(Image im, int x, int y) {
        Point p = new Point(x + 0.5d, y + 0.5d);
        Color color = im.getColor(p);
        return colorToRgb(color);
      }
    },
    SLOW {
      @Override public int getRgb(Image im, int x, int y) {
        ColorMixer mixer = new ColorMixer();
        for (int i = 0; i < N; i++) {
          for (int j = 0; j < N; j++) {
            double xx = DELTA + i / (double) N;
            double yy = DELTA + j / (double) N;
            Point p = new Point(x + xx, y + yy);
            Color color = im.getColor(p);
            double alpha = im.getAlpha(p);
            mixer.add(color, alpha);
          }
        }
        Color color = mixer.getColor();
        double alpha = mixer.getAlpha();
        return colorToRgb(color, alpha);
      }
    },
    OPAQUE_SLOW {
      @Override public int getRgb(Image im, int x, int y) {
        ColorMixer mixer = new ColorMixer();
        for (int i = 0; i < N; i++) {
          for (int j = 0; j < N; j++) {
            double xx = DELTA + i / (double) N;
            double yy = DELTA + j / (double) N;
            Point p = new Point(x + xx, y + yy);
            Color color = im.getColor(p);
            mixer.add(color);
          }
        }
        Color color = mixer.getColor();
        return colorToRgb(color);
      }
    };
    static final int N = 4;
    static final double DELTA = 1 / (2d * N);

    static PixelColorGetter getFor(ImageFeatures features) {
      if (features.isOpaque()) {
        return features.isRaster() ? OPAQUE_FAST : OPAQUE_SLOW;
      }
      return features.isRaster() ? FAST : SLOW;
    }

    public abstract int getRgb(Image im, int x, int y);
  }

  private static class Canvas extends HashCachingImage<Canvas>
      implements Serializable {
    private final int width;
    private final int height;
    private final Color color;
    private final double alpha;

    Canvas(int width, int height, Color color, double alpha) {
      this.width = width;
      this.height = height;
      this.color = color;
      this.alpha = alpha;
    }

    @Override public int width() {
      return width;
    }

    @Override public int height() {
      return height;
    }

    @Override public Color getColor(Point p) {
      return color;
    }

    @Override public double getAlpha(Point p) {
      return alpha;
    }

    @Override ImageFeatures features() {
      return ImageFeatures.start()
          .withRaster()
          .withXUniform()
          .withYUniform()
          .withOpaque(alpha == 1);
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(9213632791258083284L)
          .putInt(width)
          .putInt(height)
          .putInt(color.toJavaColor().getRGB())
          .putDouble(alpha);
    }

    @Override public boolean doEquals(Canvas that) {
      return width == that.width && height == that.height &&
          color.equals(that.color) && alpha == that.alpha;
    }

    @Override public Image crop(int rectX, int rectY,
        int rectWidth, int rectHeight) {
      return canvas(rectWidth, rectHeight, color, alpha);
    }

    @Override public Image erase(double rate) {
      return canvas(width, height, color, (1 - rate) * alpha);
    }

    @Override public Image fill(Color color, double alpha) {
      Color newColor = super.fill(color, alpha).getColor(Point.ORIGIN);
      return canvas(width, height, newColor, this.alpha);
    }

    @Override public Image flipX() {
      return this;
    }

    @Override public Image flipY() {
      return this;
    }

    @Override public Image frame(Color backgroundColor,
        int topPadding, int rightPadding, int bottomPadding, int leftPadding) {
      Image result = super.frame(backgroundColor,
          topPadding, rightPadding, bottomPadding, leftPadding);
      if (topPadding == 0 && rightPadding == 0 && leftPadding == 0 &&
          bottomPadding == 0) {
        Color newColor = result.getColor(Point.ORIGIN);
        double newAlpha = result.getAlpha(Point.ORIGIN);
        return canvas(width, height, newColor, newAlpha);
      }
      return result;
    }

    @Override public Image rotateCw() {
      return canvas(height, width, color, alpha);
    }

    @Override public Image rotateCcw() {
      return canvas(height, width, color, alpha);
    }

    @Override public Image scale(int newWidth, int newHeight) {
      return canvas(newWidth, newHeight, color, alpha);
    }

    @Override public String toString() {
      return String.format("canvas(%s, %s, %s, %s)",
          width, height, color, alpha);
    }

    private static final long serialVersionUID = 0;
  }

  // To prevent instantiation
  private Images() {}
}
