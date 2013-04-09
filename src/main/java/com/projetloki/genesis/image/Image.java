package com.projetloki.genesis.image;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkPositionIndex;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.PrimitiveSink;

/**
 * An image maps every point of a rectangle to a color and an opacity. Since
 * points are represented with floating point coordinates, no loss of
 * information occurs when performing operations like resizing.
 *
 * <p>Images are immutable. All the methods transforming an image, e.g.
 * {@link #crop(int, int, int, int)}, return a new image which is the result of
 * the transformation and leave the original image unmodified.</p>
 *
 * <p>All methods return serializable images as long as {@code this} and the
 * arguments are serializable.</p>
 *
 * <p>Example:
 * <pre><code> Image im = Images.canvas(80, 80, Color.PLUM_1)
 *     .fill(Color.PLUM_2, Gradients.linear(Direction.TOP))
 *     .mask(Shapers.roundedBox(8), new Border(1, Color.PLUM_3, 0.5))
 *     .pasteThat(TangoIcon.APPLICATIONS_MULTIMEDIA.toImage(32), 24, 24);
 * Images.show(im);</code></pre>
 * <img src="../../../../resources/genesis/34qzzf5ole.png"/></p>
 *
 * @see Images
 * @author Clément Roux
 */
public abstract class Image implements HasSize, Hashable {
  /** Returns the width of the image. */
  @Override public abstract int width();

  /** Returns the height of the image. */
  @Override public abstract int height();

  /**
   * Returns the color at the given point. The result of this method is
   *     undefined if the point is out of the image bounds. An exception may or
   *     may not be thrown.
   * @param p a point in the image bounds
   */
  public abstract Color getColor(Point p);

  /**
   * Returns the opacity (between 0 and 1) at the given point. 1 is for fully
   * opaque, 0 is for transparent. The result of this method is undefined if the
   *     point is out of the image bounds. An exception may or may not be
   *     thrown.
   * @param p a point in the image bounds
   */
  public abstract double getAlpha(Point p);

  // ---------------------------------------------------------------------------
  // Internal methods that can be overridden for optimization
  // ---------------------------------------------------------------------------

  ImageFeatures features() {
    return ImageFeatures.start();
  }

  void drawSubimage(BufferedImage out, Rect rect) {
    Images.doDrawSubimage(this, out, rect);
  }

  ImageFormat bestFormat() {
    return ImageFormat.PNG;
  }

  void save(File out, ImageFormat format) throws IOException {
    Images.doSave(this, out, format);
  }

  /**
   * Returns the image obtained by pasting the given image to the right of this
   * image. The two images must have the same height.
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/aiuepfop6q.png"/></td>
   *   <td><img src="../../../../resources/genesis/i6alpewd5q.png"/></td>
   *   <td><img src="../../../../resources/genesis/kukfpb7soi.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code im1}</td>
   *   <td>{@code im2}</td>
   *   <td>{@code im1.concatX(im2)}</td>
   *  </tr>
   * </table>
   * </p>
   * @param rightIm the image to paste to the right of this image
   * @throws IllegalArgumentException if the two images don't have the same
   *     height
   */
  public Image concatX(Image rightIm) {
    return new ConcatXImage(this, checkNotNull(rightIm));
  }

  /**
   * Returns the image obtained by pasting the given image at the bottom of this
   * image. The two images must have the same width.
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/aiuepfop6q.png"/></td>
   *   <td><img src="../../../../resources/genesis/lb7uo47vyq.png"/></td>
   *   <td><img src="../../../../resources/genesis/f7fwn4dsrq.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code im1}</td>
   *   <td>{@code im2}</td>
   *   <td>{@code im1.concatY(im2)}</td>
   *  </tr>
   * </table>
   * </p>
   * @param bottomIm the image to paste at the bottom of this image
   * @throws IllegalArgumentException if the two images don't have the same
   *     width
   */
  public Image concatY(Image bottomIm) {
    return new ConcatYImage(this, checkNotNull(bottomIm));
  }

  /**
   * Returns the image obtained by flipping this image along the horizontal
   * axis.
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/mjdlrbnbn4.png"/></td>
   *   <td><img src="../../../../resources/genesis/kiyire47cu.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code im}</td>
   *   <td>{@code im.flipX()}</td>
   *  </tr>
   * </table>
   * </p>
   */
  public Image flipX() {
    return new FlipXImage(this);
  }

  /**
   * Returns the image obtained by flipping this image along the vertical axis.
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/aqaojh7c4e.png"/></td>
   *   <td><img src="../../../../resources/genesis/j5yywq5uuy.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code im}</td>
   *   <td>{@code im.flipY()}</td>
   *  </tr>
   * </table>
   * </p>
   */
  public Image flipY() {
    return new FlipYImage(this);
  }

  /**
   * Returns the image obtained by pasting this image on top of a monochromatic
   * opaque image. The return image is fully opaque.
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/4xglkmgq74.png"/></td>
   *   <td><img src="../../../../resources/genesis/sjs432ghwi.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code im}</td>
   *   <td>{@code im.frame(CHOCOLATE_1)}</td>
   *  </tr>
   * </table>
   * </p>
   * @param backgroundColor the color of the background image
   */
  public final Image frame(Color backgroundColor) {
    return frame(backgroundColor, 0, 0, 0, 0);
  }

  /**
   * Returns the image obtained by pasting this image on top of a larger
   * monochromatic opaque image. The return image is fully opaque.
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/4xglkmgq74.png"/></td>
   *   <td><img src="../../../../resources/genesis/xc6z77xlpq.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code im}</td>
   *   <td>{@code im.frame(CHOCOLATE_1, 10, 20, 40, 10)}</td>
   *  </tr>
   * </table>
   * </p>
   * @param backgroundColor the color of the background image
   * @param topPadding the height of the top border. Can be negative.
   * @param rightPadding the width of the right border. Can be negative.
   * @param bottomPadding the height of the bottom border. Can be negative.
   * @param leftPadding the width of the left border. Can be negative.
   * @throws IllegalArgumentException if the new image has a negative width or
   *     height (in the case of negative padding)
   */
  public Image frame(Color backgroundColor,
      int topPadding, int rightPadding, int bottomPadding, int leftPadding) {
    int newWidth = width() + rightPadding + leftPadding;
    int newHeight = height() + topPadding + bottomPadding;
    checkArgument(newWidth >= 0, "newWidth: %s", newWidth);
    checkArgument(newHeight >= 0, "newHeight: %s", newWidth);
    return pasteThis(Images.canvas(newWidth, newHeight, backgroundColor),
        leftPadding, topPadding);
  }

  /**
   * Returns a rectangular region from this image.
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/7mn7frwtay.png"/></td>
   *   <td><img src="../../../../resources/genesis/z4m57ai7ve.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code im}</td>
   *   <td>{@code im.crop(10, 10, 60, 30)}</td>
   *  </tr>
   * </table>
   * </p>
   * @param rectX the x-coordinate of the left side of the rectangle. Must be
   *     positive.
   * @param rectY the y-coordinate of the top side of the rectangle. Must be
   *     positive.
   * @param rectWidth the width of the rectangle. Must be positive.
   * @param rectHeight the height of the rectangle. Must be positive.
   * @throws IllegalArgumentException if {@code rectX}, {@code rectY},
   *     {@code rectWidth} or {@code rectHeight} is negative
   * @throws IndexOutOfBoundsException if {@code rectX} + {@code rectWidth} &gt;
   *     {@code getWidth()}, or {@code rectY} + {@code rectHeight} &gt;
   *     {@code getHeight()}
   */
  public Image crop(int rectX, int rectY, int rectWidth, int rectHeight) {
    checkArgument(rectX >= 0, "rectX: %s", rectX);
    checkArgument(rectY >= 0, "rectY: %s", rectY);
    checkArgument(rectWidth >= 0, "rectWidth: %s", rectWidth);
    checkArgument(rectHeight >= 0, "rectHeight: %s", rectHeight);
    checkPositionIndex(rectX + rectWidth, width());
    checkPositionIndex(rectY + rectHeight, height());
    int width = width();
    int height = height();
    checkPositionIndex(rectX + rectWidth, width);
    checkPositionIndex(rectY + rectHeight, height);
    return new CropImage(this, rectX, rectY, rectWidth, rectHeight);
  }

  /**
   * Returns the image obtained by rotating this image clockwise.
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/bzd3f4q52q.png"/></td>
   *   <td><img src="../../../../resources/genesis/kfzzqaboru.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code im}</td>
   *   <td>{@code im.rotateCw()}</td>
   *  </tr>
   * </table>
   * </p>
   */
  public Image rotateCw() {
    return new RotateCwImage(this);
  }

  /**
   * Returns the image obtained by rotating this image counterclockwise.
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/bzd3f4q52q.png"/></td>
   *   <td><img src="../../../../resources/genesis/sgjbggkuhq.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code im}</td>
   *   <td>{@code im.rotateCcw()}</td>
   *  </tr>
   * </table>
   * </p>
   */
  public Image rotateCcw() {
    return new RotateCcwImage(this);
  }

  /**
   * Returns the image obtained by resizing this image.
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/5yxz53jrre.png"/></td>
   *   <td><img src="../../../../resources/genesis/ynalanxbau.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code im}</td>
   *   <td>{@code im.scale(200, 100)}</td>
   *  </tr>
   * </table>
   * </p>
   * @param newWidth the width of the new image. Must be positive.
   * @param newHeight the height of the new image. Must be positive.
   * @throws IllegalArgumentException if the width or the height of the new
   *     image is negative
   */
  public Image scale(int newWidth, int newHeight) {
    checkArgument(newWidth >= 0, "newWidth: %s", newWidth);
    checkArgument(newHeight >= 0, "newHeight: %s", newHeight);
    return new ScaleImage(this, newWidth, newHeight);
  }

  /**
   * Returns the image obtained by erasing this image uniformly. The color stays
   * unchanged, only the opacity decreases.
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/jhtotcbpx4.png"/></td>
   *   <td><img src="../../../../resources/genesis/ze4fmdmyty.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code im}</td>
   *   <td>{@code im.erase(0.75)}</td>
   *  </tr>
   * </table>
   * </p>
   * @param rate the rate, between 0 and 1. 0 returns this image, 1 returns a
   *     transparent image.
   * @throws IllegalArgumentException if the rate is not between 0 and 1
   */
  public Image erase(double rate) {
    checkArgument(0 <= rate && rate <= 1, "rate: %s", rate);
    if (rate == 0) {
      return this;
    }
    return new EraseImage(this, rate);
  }

  /**
   * Returns the image obtained by erasing this image gradually. The color stays
   * unchanged, only the opacity decreases.
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/jhtotcbpx4.png"/></td>
   *   <td><img src="../../../../resources/genesis/3qpnqx6qrq.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code im}</td>
   *   <td>{@code im.erase(Gradients.linear(BOTTOM))}</td>
   *  </tr>
   * </table>
   * </p>
   * @param grad the gradient
   */
  public Image erase(Gradient grad) {
    return new EraseWithGradImage(this, checkNotNull(grad));
  }

  /**
   * Returns the image obtained by filling this image uniformly.
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/4t4aadfpze.png"/></td>
   *   <td><img src="../../../../resources/genesis/sjh4gxvvam.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code im}</td>
   *   <td>{@code im.fill(WHITE, 0.25)}</td>
   *  </tr>
   * </table>
   * </p>
   * @param color the color to fill the image with
   * @param alpha the opacity of the new layer, between 0 and 1. 0 returns this
   *     image, 1 returns a monochromatic image.
   * @throws IllegalArgumentException if the opacity is not between 0 and 1
   */
  public Image fill(Color color, double alpha) {
    checkArgument(0 <= alpha && alpha <= 1, "alpha: %s", alpha);
    return new FillImage(this, checkNotNull(color), alpha);
  }

  /**
   * Returns the image obtained by filling this image gradually.
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/4t4aadfpze.png"/></td>
   *   <td><img src="../../../../resources/genesis/gfsawf7dqq.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code im}</td>
   *   <td>{@code im.fill(CHOCOLATE_1, Gradients.circular(2, -0.5))}</td>
   *  </tr>
   * </table>
   * </p>
   * @param color the color to fill the image with
   * @param grad the gradient the gradient
   */
  public Image fill(Color color, Gradient grad) {
    return new FillWithGradImage(this, checkNotNull(color), checkNotNull(grad));
  }

  /**
   * Returns the image obtained by pasting that image on top of this image, at
   * the given position.
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/jlauiachzi.png"/></td>
   *   <td><img src="../../../../resources/genesis/2fobzc7ppi.png"/></td>
   *   <td><img src="../../../../resources/genesis/6cntjnjvnm.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code im1}</td>
   *   <td>{@code im2}</td>
   *   <td>{@code im1.pasteThat(im2, 24, 24)}</td>
   *  </tr>
   * </table>
   * </p>
   * @param that the image to paste on top of this image
   * @param destX the y-coordinate at which to paste that image
   * @param destY the y-coordinate at which to paste that image
   * @see #pasteThis(Image, int, int)
   */
  public Image pasteThat(Image that, int destX, int destY) {
    Rect destRect = Rect.forCornerAndSize(destX, destY, that);
    if (Rect.atOrigin(this).intersection(destRect).isEmpty()) {
      return this;
    }
    if (that.features().isOpaque()) {
      return new PasteOpaqueImage(that, this, destX, destY);
    }
    return new PasteImage(that, this, destX, destY);
  }

  /**
   * Returns the image obtained by pasting this image on top of that image, at
   * the given position.
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/2fobzc7ppi.png"/></td>
   *   <td><img src="../../../../resources/genesis/jlauiachzi.png"/></td>
   *   <td><img src="../../../../resources/genesis/6cntjnjvnm.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code im1}</td>
   *   <td>{@code im2}</td>
   *   <td>{@code im1.pasteThis(im2, 24, 24)}</td>
   *  </tr>
   * </table>
   * </p>
   * @param that the image to paste this image on top of
   * @param destX the x-coordinate at which to paste this image
   * @param destY the y-coordinate at which to paste this image
   * @see #pasteThat(Image, int, int)
   */
  public final Image pasteThis(Image that, int destX, int destY) {
    return that.pasteThat(this, destX, destY);
  }

  /**
   * Returns the image obtained by pasting that image on top of this image. The
   * two image must have the same size.
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/jlauiachzi.png"/></td>
   *   <td><img src="../../../../resources/genesis/kvgpdnekz4.png"/></td>
   *   <td><img src="../../../../resources/genesis/mejuwoq4bq.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code im1}</td>
   *   <td>{@code im2}</td>
   *   <td>{@code im1.pasteThat(im2)}</td>
   *  </tr>
   * </table>
   * </p>
   * @param that the image to paste on top of this image
   * @throws IllegalArgumentException if the two images don't have the same size
   * @see #pasteThis(Image)
   */
  public final Image pasteThat(Image that) {
    int thisWidth = width();
    int thatWidth = that.width();
    int thisHeight = height();
    int thatHeight = that.height();
    checkArgument(thisWidth == thatWidth && thisHeight == thatHeight,
        "images must be the same size: (%s, %s), (%s, %s)",
        thisWidth, thisHeight, thatWidth, thatHeight);
    return pasteThat(that, 0, 0);
  }

  /**
   * Returns the image obtained by pasting this image on top of that image. The
   * two images must have the same size.
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/kvgpdnekz4.png"/></td>
   *   <td><img src="../../../../resources/genesis/jlauiachzi.png"/></td>
   *   <td><img src="../../../../resources/genesis/mejuwoq4bq.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code im1}</td>
   *   <td>{@code im2}</td>
   *   <td>{@code im1.pasteThis(im2)}</td>
   *  </tr>
   * </table>
   * </p>
   * @param that the image to paste this image on top of
   * @throws IllegalArgumentException if the two images don't have the same size
   * @see #pasteThat(Image)
   */
  public final Image pasteThis(Image that) {
    return that.pasteThat(this);
  }

  /**
   * Returns the image obtained by making all the points lying outside the given
   * shape transparent.
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/ih7pndmpw4.png"/></td>
   *   <td><img src="../../../../resources/genesis/wsxp76azi4.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code im}</td>
   *   <td>{@code im.mask(Shapes.ellipse(10, 10, 60, 60))}</td>
   *  </tr>
   * </table>
   * </p>
   * @param shape the shape
   * @see #mask(Shape, Border[])
   */
  public Image mask(Shape shape) {
    return new MaskImage(this, checkNotNull(shape), ImmutableList.<Border>of());
  }

  /**
   * Returns the image obtained by making all the points lying outside the given
   * shape transparent and drawing borders around the shape.
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/ih7pndmpw4.png"/></td>
   *   <td><img src="../../../../resources/genesis/ky2yb2bn4i.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code im}</td>
   *   <td style="text-align:left"><pre><code class="legend">im.mask(
   *    Shapes.ellipse(10, 10, 60, 60),
   *    new Border(4, CHOCOLATE_3, 0.75),
   *    new Border(2, BLACK, 0.5))</code></pre></td>
   *  </tr>
   * </table>
   * </p>
   * @param shape the shape
   * @param borders the borders, from outside to inside
   * @see #mask(Shape)
   */
  public Image mask(Shape shape, Border... borders) {
    return new MaskImage(this, checkNotNull(shape),
        ImmutableList.copyOf(borders));
  }

  /**
   * Returns the image obtained by making all the points lying outside the given
   * shape transparent. The shape is generated by the shaper for the size of
   * this image.
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/bsbjn4zuq4.png"/></td>
   *   <td><img src="../../../../resources/genesis/dpwlwzl2lm.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code im}</td>
   *   <td>{@code im.mask(Shapers.roundedBox(8))}</td>
   *  </tr>
   * </table>
   * </p>
   * @param shaper the shaper to generate the shape
   * @see #mask(Shaper, Border[])
   */
  public final Image mask(Shaper shaper) {
    return mask(shaper.getShape(width(), height()));
  }

  /**
   * Returns the image obtained by making all the points lying outside the given
   * shape transparent and drawing borders around the shape. The shape is
   * generated by the shaper for the size of this image.
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/bsbjn4zuq4.png"/></td>
   *   <td><img src="../../../../resources/genesis/hzv3t2szpy.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code im}</td>
   *   <td style="text-align:left"><pre><code class="legend">im.mask(
   *    Shapers.roundedBox(8),
   *    new Border(1, ALUMINIUM_3, 0.75),
   *    new Border(1, ALUMINIUM_3, 0.25))</code></pre></td>
   *  </tr>
   * </table>
   * </p>
   * @param shaper the shaper to generate the shape
   * @param borders the borders, from outside to inside
   * @see #mask(Shaper)
   */
  public final Image mask(Shaper shaper, Border... borders) {
    return mask(shaper.getShape(width(), height()), borders);
  }

  @Override public final int hashCode() {
    return hash().hashCode();
  }

  // ---------------------------------------------------------------------------
  // Implementations
  // ---------------------------------------------------------------------------

  private static class ConcatXImage
      extends ImageWithSerializationProxy<ConcatXImage> {
    final Image leftIm;
    final Image rightIm;
    final int leftWidth;
    final int width;

    ConcatXImage(Image leftIm, Image rightIm) {
      checkArgument(leftIm.width() >= 0);
      checkArgument(rightIm.width() >= 0);
      this.leftIm = leftIm;
      this.rightIm = rightIm;
      leftWidth = leftIm.width();
      width = leftWidth + rightIm.width();
      int height = leftIm.height();
      checkArgument(height == rightIm.height(),
          "images must have the same height: %s, %s",
          height, rightIm.height());
      initSize(width, height);
    }

    @Override public Color getColor(Point p) {
      if (p.x <= leftWidth) {
        return leftIm.getColor(p);
      }
      return rightIm.getColor(new Point(p.x - leftWidth, p.y));
    }

    @Override public double getAlpha(Point p) {
      if (p.x <= leftWidth) {
        return leftIm.getAlpha(p);
      }
      return rightIm.getAlpha(new Point(p.x - leftWidth, p.y));
    }

    @Override ImageFeatures features() {
      return leftIm.features()
          .and(rightIm.features())
          .withXUniform(false);
    }

    @Override void drawSubimage(BufferedImage out, Rect rect) {
      Rect leftRect = Rect.atOrigin(leftIm).intersection(rect);
      leftIm.drawSubimage(out, leftRect);
      Rect rightRect = Rect.atOrigin(rightIm)
          .intersection(rect.translate(-leftWidth, 0));
      if (!leftRect.isEmpty()) {
        out = out.getSubimage(leftRect.width(), 0,
            rightRect.width(), rightRect.height());
      }
      rightIm.drawSubimage(out, rightRect);
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(3798304663327050188L)
          .putBytes(leftIm.hash().asBytes())
          .putBytes(rightIm.hash().asBytes());
    }

    @Override public boolean doEquals(ConcatXImage that) {
      return leftIm.equals(that.leftIm) && rightIm.equals(that.rightIm);
    }

    @Override SerializationProxy<ConcatXImage> doWriteReplace() {
      return serializationProxy(leftIm, rightIm);
    }

    private static SerializationProxy<ConcatXImage> serializationProxy(
        final Image leftIm, final Image rightIm) {
      return new SerializationProxy<ConcatXImage>() {
        @Override ConcatXImage doReadResolve() {
          return new ConcatXImage(leftIm, rightIm);
        }
        private static final long serialVersionUID = 0;
      };
    }

    @Override public String toString() {
      return leftIm + ".concatX(" + rightIm + ")";
    }
  }

  private static class ConcatYImage
      extends ImageWithSerializationProxy<ConcatYImage> {
    final Image topIm;
    final Image bottomIm;
    final int topHeight;
    final int height;

    ConcatYImage(Image topIm, Image bottomIm) {
      checkArgument(topIm.height() >= 0);
      checkArgument(bottomIm.height() >= 0);
      this.topIm = topIm;
      this.bottomIm = bottomIm;
      topHeight = topIm.height();
      int width = topIm.width();
      height = topHeight + bottomIm.height();
      checkArgument(width == bottomIm.width(),
          "images must have the same width: %s, %s",
          height, bottomIm.width());
      initSize(width, height);
    }

    @Override public Color getColor(Point p) {
      if (p.y <= topHeight) {
        return topIm.getColor(p);
      }
      return bottomIm.getColor(new Point(p.x, p.y - topHeight));
    }

    @Override public double getAlpha(Point p) {
      if (p.y <= topHeight) {
        return topIm.getAlpha(p);
      }
      return bottomIm.getAlpha(new Point(p.x, p.y - topHeight));
    }

    @Override ImageFeatures features() {
      return bottomIm.features()
          .and(topIm.features())
          .withYUniform(false);
    }

    @Override void drawSubimage(BufferedImage out, Rect rect) {
      Rect topRect = Rect.atOrigin(topIm).intersection(rect);
      topIm.drawSubimage(out, topRect);
      Rect bottomRect = Rect.atOrigin(bottomIm)
          .intersection(rect.translate(0, -topHeight));
      if (!topRect.isEmpty()) {
        out = out.getSubimage(0, topRect.height(),
            bottomRect.width(), bottomRect.height());
      }
      bottomIm.drawSubimage(out, bottomRect);
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(2235388019782006253L)
          .putBytes(topIm.hash().asBytes())
          .putBytes(bottomIm.hash().asBytes());
    }

    @Override public boolean doEquals(ConcatYImage that) {
      return topIm.equals(that.topIm) && bottomIm.equals(that.bottomIm);
    }

    @Override SerializationProxy<ConcatYImage> doWriteReplace() {
      return serializationProxy(topIm, bottomIm);
    }

    private static SerializationProxy<ConcatYImage> serializationProxy(
        final Image topIm, final Image bottomIm) {
      return new SerializationProxy<ConcatYImage>() {
        @Override ConcatYImage doReadResolve() {
          return new ConcatYImage(topIm, bottomIm);
        }
        private static final long serialVersionUID = 0;
      };
    }

    @Override public String toString() {
      return topIm + ".concatY(" + bottomIm + ")";
    }
  }

  // Base class of FlipXImage, FlipYImage and FlipXYImage
  private abstract static class FlipImage
      extends PointTransformingImage<FlipImage> {
    FlipImage(Image operand) {
      super(operand);
      initSize(operand);
    }

    @Override final ImageFeatures features() {
      return operand.features();
    }

    @Override public final boolean doEquals(FlipImage that) {
      return operand.equals(that.operand);
    }
  }

  private static class FlipXImage extends FlipImage {
    final int width;

    FlipXImage(Image operand) {
      super(operand);
      width = operand.width();
    }

    @Override public Point transformPoint(Point p) {
      return new Point(width - p.x, p.y);
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(-5332335088123476348L)
          .putBytes(operand.hash().asBytes());
    }

    @Override SerializationProxy<FlipImage> doWriteReplace() {
      return serializationProxy(operand);
    }

    private static SerializationProxy<FlipImage> serializationProxy(
        final Image operand) {
      return new SerializationProxy<FlipImage>() {
        @Override FlipImage doReadResolve() {
          return new FlipXImage(operand);
        }
        private static final long serialVersionUID = 0;
      };
    }

    @Override public Image flipX() {
      return operand;
    }

    @Override public Image flipY() {
      return new FlipXYImage(operand);
    }

    @Override public String toString() {
      return operand + ".flipX()";
    }
  }

  private static class FlipYImage extends FlipImage {
    final int height;

    FlipYImage(Image operand) {
      super(operand);
      height = operand.height();
    }

    @Override public Point transformPoint(Point p) {
      return new Point(p.x, height - p.y);
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(-2575274948235708526L)
          .putBytes(operand.hash().asBytes());
    }

    @Override SerializationProxy<FlipImage> doWriteReplace() {
      return serializationProxy(operand);
    }

    private static SerializationProxy<FlipImage> serializationProxy(
        final Image operand) {
      return new SerializationProxy<FlipImage>() {
        @Override FlipImage doReadResolve() {
          return new FlipYImage(operand);
        }
        private static final long serialVersionUID = 0;
      };
    }

    @Override public Image flipX() {
      return new FlipXYImage(operand);
    }

    @Override public Image flipY() {
      return operand;
    }

    @Override public String toString() {
      return operand + ".flipY()";
    }
  }

  // Obtained by calling im.flipX().flipY() or im.rotateCw().rotateCw()
  // or im.rotateCcw().rotateCcw()
  private static class FlipXYImage extends FlipImage {
    final int width;
    final int height;

    FlipXYImage(Image operand) {
      super(operand);
      width = operand.width();
      height = operand.height();
    }

    @Override public Point transformPoint(Point p) {
      return new Point(width - p.x, height - p.y);
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(6175147000481217797L)
          .putBytes(operand.hash().asBytes());
    }

    @Override SerializationProxy<FlipImage> doWriteReplace() {
      return serializationProxy(operand);
    }

    private static SerializationProxy<FlipImage> serializationProxy(
        final Image operand) {
      return new SerializationProxy<FlipImage>() {
        @Override FlipImage doReadResolve() {
          return new FlipXYImage(operand);
        }
        private static final long serialVersionUID = 0;
      };
    }

    @Override public Image rotateCw() {
      return operand.rotateCcw();
    }

    @Override public Image rotateCcw() {
      return operand.rotateCw();
    }

    @Override public Image flipX() {
      return operand.flipY();
    }

    @Override public Image flipY() {
      return operand.flipX();
    }

    @Override public String toString() {
      return operand + ".flipX().flipY()";
    }
  }

  private static class CropImage extends PointTransformingImage<CropImage> {
    final double rectX;
    final double rectY;

    CropImage(Image operand, int rectX, int rectY,
        int rectWidth, int rectHeight) {
      super(operand);
      this.rectX = rectX;
      this.rectY = rectY;
      initSize(rectWidth, rectHeight);
    }

    @Override public Point transformPoint(Point p) {
      return new Point(p.x + rectX, p.y + rectY);
    }

    @Override ImageFeatures features() {
      return operand.features();
    }

    @Override void drawSubimage(BufferedImage out, Rect rect) {
      int xx = rect.x0() + (int) rectX;
      int yy = rect.y0() + (int) rectY;
      operand.drawSubimage(out, Rect.forCornerAndSize(xx, yy, rect));
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(7279737324539914899L)
          .putBytes(operand.hash().asBytes())
          .putInt((int) rectX)
          .putInt((int) rectY)
          .putInt(width())
          .putInt(height());
    }

    @Override public boolean doEquals(CropImage that) {
      return operand.equals(that.operand) &&
          rectX == that.rectX && rectY == that.rectY &&
          width() == that.width() && height() == that.height();
    }

    @Override SerializationProxy<CropImage> doWriteReplace() {
      return serializationProxy(operand, (int) rectX, (int) rectY, width(), height());
    }

    private static SerializationProxy<CropImage> serializationProxy(
        final Image operand,
        final int rectX,
        final int rectY,
        final int rectWidth,
        final int rectHeight) {
      return new SerializationProxy<CropImage>() {
        @Override CropImage doReadResolve() {
          return new CropImage(operand, rectX, rectY, rectWidth, rectHeight);
        }
        private static final long serialVersionUID = 0;
      };
    }

    @Override public String toString() {
      return String.format("%s.crop(%s, %s, %s, %s)",
          operand, rectX, rectY, width(), height());
    }
  }

  // Base class of RotateCwImage and RotateCcwImage
  private abstract static class RotateRightAngleImage
      extends PointTransformingImage<RotateRightAngleImage> {
    RotateRightAngleImage(Image operand) {
      super(operand);
      initSize(operand.height(), operand.width());
    }

    @Override final ImageFeatures features() {
      ImageFeatures features = operand.features();
      return features.withXUniform(features.isYUniform())
          .withYUniform(features.isXUniform());
    }

    @Override public final boolean doEquals(RotateRightAngleImage that) {
      return operand.equals(that.operand);
    }
  }

  private static class RotateCwImage extends RotateRightAngleImage {
    final int width;

    RotateCwImage(Image operand) {
      super(operand);
      width = operand.height();
    }

    @Override public Point transformPoint(Point p) {
      return new Point(p.y, width - p.x);
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(-8812612032105888623L)
          .putBytes(operand.hash().asBytes());
    }

    @Override SerializationProxy<RotateRightAngleImage> doWriteReplace() {
      return serializationProxy(operand);
    }

    private static SerializationProxy<RotateRightAngleImage> serializationProxy(
        final Image operand) {
      return new SerializationProxy<RotateRightAngleImage>() {
        @Override RotateRightAngleImage doReadResolve() {
          return new RotateCwImage(operand);
        }
        private static final long serialVersionUID = 0;
      };
    }

    @Override public Image rotateCw() {
      return new FlipXYImage(operand);
    }

    @Override public Image rotateCcw() {
      return operand;
    }

    @Override public String toString() {
      return operand + ".rotateCw()";
    }
  }

  private static class RotateCcwImage extends RotateRightAngleImage {
    final int height;

    RotateCcwImage(Image operand) {
      super(operand);
      height = operand.width();
    }

    @Override public Point transformPoint(Point p) {
      return new Point(height - p.y, p.x);
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(200364476481069873L)
          .putBytes(operand.hash().asBytes());
    }

    @Override SerializationProxy<RotateRightAngleImage> doWriteReplace() {
      return serializationProxy(operand);
    }

    private static SerializationProxy<RotateRightAngleImage> serializationProxy(
        final Image operand) {
      return new SerializationProxy<RotateRightAngleImage>() {
        @Override RotateRightAngleImage doReadResolve() {
          return new RotateCcwImage(operand);
        }
        private static final long serialVersionUID = 0;
      };
    }

    @Override public Image rotateCw() {
      return operand;
    }

    @Override public Image rotateCcw() {
      return new FlipXYImage(operand);
    }

    @Override public String toString() {
      return operand + ".rotateCcw()";
    }
  }

  private static class ScaleImage extends PointTransformingImage<ScaleImage> {
    final double kx;
    final double ky;

    ScaleImage(Image operand, int newWidth, int newHeight) {
      super(operand);
      kx = operand.width() / (double) newWidth;
      ky = operand.height() / (double) newHeight;
      initSize(newWidth, newHeight);
    }

    @Override public Point transformPoint(Point p) {
      return new Point(kx * p.x, ky * p.y);
    }

    @Override ImageFeatures features() {
      return operand.features()
          .andRaster(width() % operand.width() == 0 &&
              height() % operand.height() == 0);
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(-5975771944692916137L)
          .putBytes(operand.hash().asBytes())
          .putInt(width())
          .putInt(height());
    }

    @Override public boolean doEquals(ScaleImage that) {
      return operand.equals(that.operand) && width() == that.width() &&
          height() == that.height();
    }

    @Override SerializationProxy<ScaleImage> doWriteReplace() {
      return serializationProxy(operand, width(), height());
    }

    private static SerializationProxy<ScaleImage> serializationProxy(
        final Image operand, final int newWidth, final int newHeight) {
      return new SerializationProxy<ScaleImage>() {
        @Override ScaleImage doReadResolve() {
          return new ScaleImage(operand, newWidth, newHeight);
        }
        private static final long serialVersionUID = 0;
      };
    }

    @Override public String toString() {
      return operand + ".scale(" + width() + ", " + height() + ")";
    }
  }

  private static class EraseImage extends ImageWithSerializationProxy<EraseImage> {
    final Image operand;
    final double rate;
    final double k;

    EraseImage(Image operand, double rate) {
      this.operand = operand;
      this.rate = rate;
      k = 1 - rate;
      initSize(operand);
    }

    @Override public Color getColor(Point p) {
      return operand.getColor(p);
    }

    @Override public double getAlpha(Point p) {
      return k * operand.getAlpha(p);
    }

    @Override public ImageFeatures features() {
      return operand.features().withOpaque(false);
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(-3315733512070026334L)
          .putBytes(operand.hash().asBytes());
    }

    @Override public boolean doEquals(EraseImage that) {
      return operand.equals(that.operand) && rate == that.rate;
    }

    @Override SerializationProxy<EraseImage> doWriteReplace() {
      return serializationProxy(operand, rate);
    }

    private static SerializationProxy<EraseImage> serializationProxy(
        final Image operand, final double rate) {
      return new SerializationProxy<EraseImage>() {
        @Override EraseImage doReadResolve() {
          return new EraseImage(operand, rate);
        }
        private static final long serialVersionUID = 0;
      };
    }

    @Override public String toString() {
      return operand + ".erase(" + rate + ")";
    }
  }

  private static class EraseWithGradImage
      extends ImageWithSerializationProxy<EraseWithGradImage> {
    final Image operand;
    final Gradient grad;
    final Gradient.DensityMap map;

    EraseWithGradImage(Image operand, Gradient grad) {
      this.operand = operand;
      this.grad = grad;
      int width = operand.width();
      int height = operand.height();
      map = grad.getDensityMap(width, height);
      initSize(width, height);
    }

    @Override public Color getColor(Point p) {
      return operand.getColor(p);
    }

    @Override public double getAlpha(Point p) {
      double density = map.getDensity(p);
      return (1d - density) * operand.getAlpha(p);
    }

    @Override ImageFeatures features() {
      return operand.features().and(grad.features());
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(5804055912448499727L)
          .putBytes(operand.hash().asBytes())
          .putBytes(grad.hash().asBytes());
    }

    @Override public boolean doEquals(EraseWithGradImage that) {
      return operand.equals(that.operand) && grad.equals(that.grad);
    }

    @Override SerializationProxy<EraseWithGradImage> doWriteReplace() {
      return serializationProxy(operand, grad);
    }

    private static SerializationProxy<EraseWithGradImage> serializationProxy(
        final Image operand, final Gradient grad) {
      return new SerializationProxy<EraseWithGradImage>() {
        @Override EraseWithGradImage doReadResolve() {
          return new EraseWithGradImage(operand, grad);
        }
        private static final long serialVersionUID = 0;
      };
    }

    @Override public String toString() {
      return operand + ".erase(" + grad + ")";
    }
  }

  private static class FillImage extends HashCachingImage<FillImage>
      implements Serializable {
    final Image operand;
    final Color color;
    final double alpha;

    FillImage(Image operand, Color color, double alpha) {
      this.operand = operand;
      this.color = color;
      this.alpha = alpha;
    }

    @Override public int width() {
      return operand.width();
    }

    @Override public int height() {
      return operand.height();
    }

    @Override public Color getColor(Point p) {
      return operand.getColor(p).mix(color, alpha);
    }

    @Override public double getAlpha(Point p) {
      return operand.getAlpha(p);
    }

    @Override ImageFeatures features() {
      return operand.features();
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(-3532741631376093183L)
          .putBytes(operand.hash().asBytes())
          .putInt(color.toJavaColor().getRGB())
          .putDouble(alpha);
    }

    @Override public boolean doEquals(FillImage that) {
      return operand.equals(that.operand) && color.equals(that.color) &&
          alpha == that.alpha;
    }

    @Override public String toString() {
      return operand + ".fill(" + color + ", " + alpha + ")";
    }

    private static final long serialVersionUID = 0;
  }

  private static class FillWithGradImage
      extends ImageWithSerializationProxy<FillWithGradImage> {
    final Image operand;
    final Color color;
    final Gradient grad;
    final Gradient.DensityMap map;

    FillWithGradImage(Image operand, Color color, Gradient grad) {
      this.operand = operand;
      this.color = color;
      this.grad = grad;
      int width = operand.width();
      int height = operand.height();
      map = grad.getDensityMap(width, height);
      initSize(width, height);
    }

    @Override public Color getColor(Point p) {
      double density = map.getDensity(p);
      return operand.getColor(p).mix(color, density);
    }

    @Override public double getAlpha(Point p) {
      return operand.getAlpha(p);
    }

    @Override ImageFeatures features() {
      return operand.features().and(grad.features().withOpaque(true));
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(1151523809481740996L)
          .putBytes(operand.hash().asBytes())
          .putInt(color.toJavaColor().getRGB())
          .putBytes(grad.hash().asBytes());
    }

    @Override public boolean doEquals(FillWithGradImage that) {
      return operand.equals(that.operand) && color.equals(that.color) &&
          grad.equals(that.grad);
    }

    @Override SerializationProxy<FillWithGradImage> doWriteReplace() {
      return serializationProxy(operand, color, grad);
    }

    private static SerializationProxy<FillWithGradImage> serializationProxy(
        final Image operand, final Color color, final Gradient grad) {
      return new SerializationProxy<FillWithGradImage>() {
        @Override FillWithGradImage doReadResolve() {
          return new FillWithGradImage(operand, color, grad);
        }
        private static final long serialVersionUID = 0;
      };
    }

    @Override public String toString() {
      return operand + ".fill(" + color + ", " + grad + ")";
    }
  }

  // Base class of PasteImage and PasteOpaqueImage
  private abstract static class AbstractPasteImage
      extends ImageWithSerializationProxy<AbstractPasteImage> {
    // Not private so they can be used by subclasses
    final Image backIm;
    final Image foreIm;
    final int destX;
    final int destY;
    final Shape rect;

    AbstractPasteImage(Image foreIm, Image backIm, int destX, int destY) {
      this.backIm = backIm;
      this.foreIm = foreIm;
      this.destX = destX;
      this.destY = destY;
      // Determine a shape that minimizes the number of operations to compute
      int width = backIm.width();
      int height = backIm.height();
      // First, measure distance to the four edges
      int rightMargin = width - destX - foreIm.width();
      int bottomMargin = height - destY - foreIm.height();
      if (rightMargin <= 0 && bottomMargin <= 0 && destX <= 0) {
        if (destY <= 0) {
          rect = Shapes.plane();
        } else {
          // T
          rect = Shapes.lowerHalfPlane(destY);
        }
      } else if (bottomMargin <= 0 && destX <= 0 && destY <= 0) {
        rect = Shapes.leftHalfPlane(width - rightMargin);
      } else if (destX <= 0 && destY <= 0 && rightMargin <= 0) {
        rect = Shapes.upperHalfPlane(height - bottomMargin);
      } else if (destX <= 0 && rightMargin <= 0 && bottomMargin <= 0) {
        rect = Shapes.rightHalfPlane(destX);
      } else {
        rect = Shapes.rectangle(destX, destY,
            foreIm.width(), foreIm.height());
      }
      initSize(backIm);
    }

    @Override public double getAlpha(Point p) {
      return backIm.getAlpha(p);
    }

    @Override ImageFeatures features() {
      // This image is x-uniform if backIm is x-uniform, foreIm is x-uniform,
      // AND foreIm covers the entire width of backIm
      // Same for y-uniformity
      ImageFeatures other = foreIm.features();
      return backIm.features()
          .andRaster(other.isRaster())
          .andXUniform(other.isXUniform() &&
              destX <= 0 && width() <= destX + foreIm.width())
          .andYUniform(other.isYUniform() &&
              destY <= 0 && height() <= destY + foreIm.height());
    }

    @Override void drawSubimage(BufferedImage out, Rect rect) {
      Rect destRect = Rect.forCornerAndSize(destX, destY, foreIm)
          .intersection(rect);
      if (destRect.isEmpty()) {
        // Special case - only draw the back image
        backIm.drawSubimage(out, rect);
        return;
      }
      int x0 = rect.x0();
      int y0 = rect.y0();
      for (Rect part : rect.partitionMinus(destRect)) {
        backIm.drawSubimage(part.translate(-x0, -y0).getSubimage(out), part);
      }
      if (backIm.features().isOpaque() && foreIm.features().isOpaque()) {
        foreIm.drawSubimage(destRect.translate(-x0, -y0).getSubimage(out),
            destRect.translate(-destX, -destY));
      } else {
        super.drawSubimage(destRect.translate(-x0, -y0).getSubimage(out),
            destRect);
      }
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(8573092467650806787L)
          .putBytes(foreIm.hash().asBytes())
          .putBytes(backIm.hash().asBytes())
          .putInt(destX)
          .putInt(destY);
    }

    @Override public final boolean doEquals(AbstractPasteImage that) {
      return foreIm.equals(that.foreIm) && backIm.equals(that.backIm) &&
          destX == that.destX && destY == that.destY;
    }

    @Override SerializationProxy<Image> doWriteReplace() {
      return serializationProxy(foreIm, backIm, destX, destY);
    }

    private static SerializationProxy<Image> serializationProxy(
        final Image foreIm,
        final Image backIm,
        final int destX,
        final int destY) {
      return new SerializationProxy<Image>() {
        @Override Image doReadResolve() {
          return backIm.pasteThat(foreIm, destX, destY);
        }
        private static final long serialVersionUID = 0;
      };
    }

    @Override public final String toString() {
      return String.format("%s.pasteThat(%s, %s, %s)",
          backIm, foreIm, destX, destY);
    }
  }

  private static class PasteImage extends AbstractPasteImage {
    PasteImage(Image foreIm, Image backIm, int destX, int destY) {
      super(foreIm, backIm, destX, destY);
    }

    @Override public Color getColor(Point p) {
      if (rect.contains(p)) {
        Point pp = new Point(p.x - destX, p.y - destY);
        double alpha = foreIm.getAlpha(pp);
        if (alpha == 0d) {
          return backIm.getColor(p);
        }
        Color col = foreIm.getColor(pp);
        if (alpha == 1d) {
          return col;
        }
        Color temp = backIm.getColor(p);
        return temp.mix(col, alpha);
      }
      return backIm.getColor(p);
    }
  }

  private static class PasteOpaqueImage extends AbstractPasteImage {
    PasteOpaqueImage(Image foreIm, Image backIm, int destX, int destY) {
      super(foreIm, backIm, destX, destY);
    }

    @Override public Color getColor(Point p) {
      if (rect.contains(p)) {
        Point pp = new Point(p.x - destX, p.y - destY);
        return foreIm.getColor(pp);
      }
      return backIm.getColor(p);
    }
  }

  private static class MaskImage
      extends ImageWithSerializationProxy<MaskImage> {
    final Image operand;
    final Shape shape;
    final ImmutableList<Border> borders;
    final Shape[] russianShapes;
    final Image[] russianImages;

    MaskImage(Image operand, Shape shape, ImmutableList<Border> borders) {
      this.operand = operand;
      this.shape = shape;
      this.borders = borders;
      int width = operand.width();
      int height = operand.height();
      russianShapes = new Shape[borders.size()];
      russianImages = new Image[borders.size()];
      double sum = 0;
      for (int i = 0; i < borders.size(); i++) {
        Border border = borders.get(i);
        sum += border.width();
        russianShapes[i] = shape.shrink(sum);
        Image foreIm = border.getForeIm(width, height);
        russianImages[i] = operand.pasteThat(foreIm);
      }
      initSize(width, height);
    }

    @Override public Color getColor(Point p) {
      for (int i = 0; i < russianShapes.length; i++) {
        if (!russianShapes[i].contains(p)) {
          return russianImages[i].getColor(p);
        }
      }
      return operand.getColor(p);
    }

    @Override public double getAlpha(Point p) {
      if (shape.contains(p)) {
        return operand.getAlpha(p);
      }
      return 0d;
    }

    @Override ImageFeatures features() {
      return operand.features().and(shape.features());
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(370015820884374523L)
          .putBytes(operand.hash().asBytes())
          .putBytes(shape.hash().asBytes());
      for (Border border : borders) {
        sink.putDouble(border.width());
        sink.putInt(border.color().toJavaColor().getRGB());
        sink.putDouble(border.alpha());
      }
    }

    @Override public boolean doEquals(MaskImage that) {
      return operand.equals(that.operand) && shape.equals(that.shape) &&
          borders.equals(that.borders);
    }

    @Override SerializationProxy<MaskImage> doWriteReplace() {
      return serializationProxy(operand, shape, borders);
    }

    private static SerializationProxy<MaskImage> serializationProxy(
        final Image operand,
        final Shape shape,
        final ImmutableList<Border> borders) {
      return new SerializationProxy<MaskImage>() {
        @Override MaskImage doReadResolve() {
          return new MaskImage(operand, shape, borders);
        }
        private static final long serialVersionUID = 0;
      };
    }

    @Override public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append(operand).append(".mask(").append(shape);
      if (!borders.isEmpty()) {
        builder.append(", ").append(borders);
      }
      return builder.toString();
    }
  }
}
