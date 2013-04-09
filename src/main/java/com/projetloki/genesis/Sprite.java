package com.projetloki.genesis;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;
import com.projetloki.genesis.image.Color;
import com.projetloki.genesis.image.Image;
import com.projetloki.genesis.image.Images;

/**
 * A tile set, i.e. a collection of smaller images.
 *
 * @author Clément Roux
 */
abstract class Sprite implements Serializable {
  /** Returns a sprite made of the single given image. */
  static Sprite forSingleImage(Image image) {
    return new SingleImageImpl(image);
  }

  /**
   * Returns an Image view of this sprite. Blank surfaces are painted black.
   */
  abstract Image asImage();

  /** Returns the area - width times height - of the sprite. */
  final long area() {
    Image image = asImage();
    return image.width() * (long) image.height();
  }

  /** Returns the smaller images this sprite is made of. */
  abstract ImmutableSet<Image> subImages();

  /**
   * Returns the (negative) position of the given smaller image in this sprite,
   * or throws an illegal argument exception if the sprite does not contain the
   * given smaller image.
   *
   * <p>
   * For example, if the position of the top-left corner of the image in the
   * sprite is (200, 100), returns -200px -100px.
   */
  abstract BackgroundPosition getPosition(Image image);

  // Internal, do not call
  abstract void putImagesInto(
      ImmutableMap.Builder<Image, BackgroundPosition> out,
      int plusX, int plusY);

  /**
   * Returns a sprite that is the result of concatenating horizontally this
   * sprite and the given sprite. If one of the sprites is smaller in height
   * than the other, it gets extended vertically.
   *
   * <p>
   * The other sprite MUST NOT contain an image contained in this sprite.
   */
  final Sprite concatX(Sprite right) {
    Image leftImage = asImage();
    Image rightImage = right.asImage();
    int height = Math.max(leftImage.height(), rightImage.height());
    leftImage = extendY(leftImage, height);
    rightImage = extendY(rightImage, height);
    return new ConcatImpl(this, right, leftImage.concatX(rightImage),
        leftImage.width(), 0);
  }

  /**
   * Returns a sprite that is the result of concatenating vertically this
   * sprite and the given sprite. If one of the sprites is smaller in width
   * than the other, it gets extended horizontally.
   *
   * <p>
   * The other sprite MUST NOT contain an image contained in this sprite.
   */
  final Sprite concatY(Sprite bottom) {
    Image topImage = asImage();
    Image bottomImage = bottom.asImage();
    int width = Math.max(topImage.width(), bottomImage.width());
    topImage = extendX(topImage, width);
    bottomImage = extendX(bottomImage, width);
    return new ConcatImpl(this, bottom, topImage.concatY(bottomImage),
        0, topImage.height());
  }

  /**
   * Either {@link #concatX(Sprite)} or {@link #concatY(Sprite)}, depending
   * on which method returns the sprite with the smaller area. In case of a
   * tie, concatenates horizontally.
   *
   * <p>
   * The other sprite MUST NOT contain an image contained in this sprite.
   */
  final Sprite concatXOrY(Sprite other) {
    int width = asImage().width();
    int height = asImage().height();
    int otherWidth = other.asImage().width();
    int otherHeight = other.asImage().height();
    int areaIfX = (width + otherWidth) * Math.max(height, otherHeight);
    int areaIfY = Math.max(width, otherWidth) * (height + otherHeight);
    if (areaIfX <= areaIfY) {
      return concatX(other);
    }
    return concatY(other);
  }

  @Override public final String toString() {
    return "[" + asImage() + "]";
  }

  private static class SingleImageImpl extends Sprite {
    private final Image singleImage;

    SingleImageImpl(Image singleImage) {
      this.singleImage = checkNotNull(singleImage);
    }

    @Override Image asImage() {
      return singleImage;
    }

    @Override ImmutableSet<Image> subImages() {
      return ImmutableSet.of(singleImage);
    }

    @Override BackgroundPosition getPosition(Image image) {
      checkArgument(image.equals(singleImage));
      return BackgroundPosition.LEFT_TOP;
    }

    @Override void putImagesInto(ImmutableMap.Builder<Image,
        BackgroundPosition> out, int plusX, int plusY) {
      out.put(singleImage, BackgroundPosition.px(-plusX, -plusY));
    }

    @Override public boolean equals(Object object) {
      if (object instanceof SingleImageImpl) {
        SingleImageImpl that = (SingleImageImpl) object;
        return singleImage.equals(that.singleImage);
      }
      return false;
    }

    @Override public int hashCode() {
      return singleImage.hashCode() - 82285823;
    }
    private static final long serialVersionUID = 0;
  }

  private static class ConcatImpl extends Sprite {
    private final Sprite leftOrTop;
    private final Sprite rightOrBottom;
    private final Image asImage;
    private final int plusX;
    private final int plusY;
    private transient volatile ImmutableMap<Image, BackgroundPosition>
        imageToPosition;
    private transient volatile int hashCode;

    ConcatImpl(Sprite leftOrTop, Sprite rightOrBottom, Image asImage,
        int plusX, int plusY) {
      this.leftOrTop = checkNotNull(leftOrTop);
      this.rightOrBottom = checkNotNull(rightOrBottom);
      this.asImage = checkNotNull(asImage);
      this.plusX = plusX;
      this.plusY = plusY;
    }

    @Override Image asImage() {
      return asImage;
    }

    @Override ImmutableSet<Image> subImages() {
      return imageToPosition().keySet();
    }

    @Override BackgroundPosition getPosition(Image image) {
      BackgroundPosition result = imageToPosition().get(image);
      checkArgument(result != null);
      return result;
    }

    @Override void putImagesInto(Builder<Image, BackgroundPosition> out,
        int plusX, int plusY) {
      leftOrTop.putImagesInto(out, plusX, plusY);
      rightOrBottom.putImagesInto(out, plusX + this.plusX, plusY + this.plusY);
    }

    private ImmutableMap<Image, BackgroundPosition> imageToPosition() {
      ImmutableMap<Image, BackgroundPosition> result = imageToPosition;
      if (result == null) {
        ImmutableMap.Builder<Image, BackgroundPosition> builder =
            ImmutableMap.builder();
        putImagesInto(builder, 0, 0);
        imageToPosition = result = builder.build();
      }
      return result;
    }

    @Override public boolean equals(Object object) {
      if (object instanceof ConcatImpl) {
        ConcatImpl that = (ConcatImpl) object;
        return leftOrTop.equals(that.leftOrTop) &&
            rightOrBottom.equals(that.rightOrBottom);
      }
      return false;
    }

    @Override public int hashCode() {
      int result = hashCode;
      if (result == 0) {
        hashCode = result = Objects.hashCode(leftOrTop, rightOrBottom);
      }
      return result;
    }
    private static final long serialVersionUID = 0;
  }

  private static Image extendX(Image im, int newWidth) {
    checkArgument(im.width() <= newWidth, "", im.width(), newWidth);
    if (im.width() == newWidth) {
      return im;
    }
    Image canvas = Images.canvas(newWidth - im.width(), im.height(),
        Color.BLACK, 0);
    return im.concatX(canvas);
  }

  private static Image extendY(Image im, int newHeight) {
    checkArgument(im.height() <= newHeight, "", im.height(), newHeight);
    if (im.height() == newHeight) {
      return im;
    }
    Image canvas = Images.canvas(im.width(), newHeight - im.height(),
        Color.BLACK, 0);
    return im.concatY(canvas);
  }
  private static final long serialVersionUID = 0;
}
