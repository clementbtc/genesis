package com.projetloki.genesis.image;

import static com.google.common.base.Preconditions.checkArgument;

import java.awt.image.BufferedImage;
import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * A possibly empty rectangle with integer coordinates. There is a unique value
 * representing an empty rectangle, and it does not have coordinates -
 * {@code Rect.EMPTY.x0()} will fail.
 *
 * @author Clément Roux
 */
abstract class Rect implements HasSize {
  /**
   * Returns a rectangle whose top-left corner is the origin, and with the given
   * size.
   * Returns empty if the width or the height is negative or zero.
   */
  static Rect atOrigin(HasSize size) {
    return forCornerAndSize(0, 0, size.width(), size.height());
  }

  /**
   * Returns a rectangle whose top-left corner is the origin, and with the given
   * size.
   * Returns empty if the width or the height is negative or zero.
   */
  static Rect createAtOrigin(int width, int height) {
    return forCornerAndSize(0, 0, width, height);
  }

  /**
   * Returns a rectangle with the given top-left corner coordinates and
   * bottom-right corner coordinates.
   * Returns empty if the second point is not strictly at the bottom right of
   * the first point.
   */
  static Rect forCorners(int x0, int y0, int x1, int y1) {
    return forCornerAndSize(x0, y0, x1 - x0, y1 - y0);
  }

  /**
   * Returns a rectangle with the given top-left corner coordinates and size.
   * Returns empty if the width or the height is negative or zero.
   */
  static Rect forCornerAndSize(int x0, int y0, HasSize size) {
    return forCornerAndSize(x0, y0, size.width(), size.height());
  }

  /**
   * Returns a rectangle with the given top-left corner coordinates and size.
   * Returns empty if the width or the height is negative or zero.
   */
  static Rect forCornerAndSize(int x0, int y0, int width, int height) {
    if (width <= 0 || height <= 0) {
      return EMPTY;
    }
    return new Impl(x0, y0, width, height);
  }

  /** Returns whether {@code this} is the empty rectangle. */
  abstract boolean isEmpty();

  /**
   * Returns the x-coordinate of the left side of the rectangle.
   * Fails if {@code this} is the empty rectangle.
   */
  abstract int x0();

  /**
   * Returns the y-coordinate of the top side of the rectangle.
   * Fails if {@code this} is the empty rectangle.
   */
  abstract int y0();

  /**
   * Returns the x-coordinate of the right side of the rectangle.
   * Fails if {@code this} is the empty rectangle.
   */
  abstract int x1();

  /**
   * Returns the y-coordinate of the bottom side of the rectangle.
   * Fails if {@code this} is the empty rectangle.
   */
  abstract int y1();

  abstract BufferedImage getSubimage(BufferedImage input);

  abstract Rect translate(int dx, int dy);

  abstract Rect intersection(Rect other);

  /**
   * Returns a list of rectangles, that don't intersect with {@code this}
   * rectangle nor with the given rectangle, and whose union with the given
   * rectangle is {@code this} rectangle.
   */
  abstract List<Rect> partitionMinus(Rect other);

  Rect() {}

  /** The empty rectangle. */
  static final Rect EMPTY = new Rect() {
    @Override boolean isEmpty() {
      return true;
    }

    @Override int x0() {
      throw new IllegalStateException();
    }

    @Override int y0() {
      throw new IllegalStateException();
    }

    @Override int x1() {
      throw new IllegalStateException();
    }

    @Override int y1() {
      throw new IllegalStateException();
    }

    @Override public int width() {
      return 0;
    }

    @Override public int height() {
      return 0;
    }

    @Override BufferedImage getSubimage(BufferedImage input) {
      throw new IllegalStateException();
    }

    @Override Rect translate(int dx, int dy) {
      return this;
    }

    @Override Rect intersection(Rect other) {
      return this;
    }

    @Override List<Rect> partitionMinus(Rect other) {
      return ImmutableList.of();
    }

    @Override public String toString() {
      return "EMPTY";
    }
  };

  private static class Impl extends Rect {
    private final int x0;
    private final int y0;
    private final int x1;
    private final int y1;
    private final int width;
    private final int height;

    Impl(int x0, int y0, int width, int height) {
      checkArgument(0 < width);
      checkArgument(0 < height);
      this.x0 = x0;
      this.y0 = y0;
      this.width = width;
      this.height = height;
      x1 = x0 + width;
      y1 = y0 + height;
    }

    @Override boolean isEmpty() {
      return false;
    }

    @Override int x0() {
      return x0;
    }

    @Override int y0() {
      return y0;
    }

    @Override int x1() {
      return x1;
    }

    @Override int y1() {
      return y1;
    }

    @Override public int width() {
      return width;
    }

    @Override public int height() {
      return height;
    }

    @Override BufferedImage getSubimage(BufferedImage input) {
      return input.getSubimage(x0, y0, width, height);
    }

    @Override Rect translate(int dx, int dy) {
      if (dx == 0 && dy == 0) {
        return this;
      }
      return forCornerAndSize(x0 + dx, y0 + dy, width, height);
    }

    @Override Rect intersection(Rect other) {
      if (other.isEmpty()) {
        return EMPTY;
      }
      Impl that = (Impl) other;
      int newX0 = Math.max(x0, that.x0);
      int newY0 = Math.max(y0, that.y0);
      int newX1 = Math.min(x1, that.x1);
      int newY1 = Math.min(y1, that.y1);
      if (newX0 == x0 && newY0 == y0 && newX1 == x1 && newY1 == y1) {
        return this;
      }
      if (newX0 == that.x0 && newY0 == that.y0 &&
          newX1 == that.x1 && newY1 == that.y1) {
        return that;
      }
      return forCorners(newX0, newY0, newX1, newY1);
    }

    @Override List<Rect> partitionMinus(Rect other) {
      other = intersection(other);
      if (other.isEmpty()) {
        return ImmutableList.<Rect>of(this);
      }
      List<Rect> result = Lists.newArrayList();
      Rect top = Rect.forCorners(x0, y0, x1, other.y0());
      Rect left = Rect.forCorners(x0, other.y0(), other.x0(), other.y1());
      Rect right = Rect.forCorners(other.x1(), other.y0(), x1, other.y1());
      Rect bottom = Rect.forCorners(x0, other.y1(), x1, y1);
      for (Rect part : ImmutableList.of(top, left, right, bottom)) {
        if (!part.isEmpty()) {
          result.add(part);
        }
      }
      return result;
    }

    @Override public boolean equals(Object object) {
      if (object == this) {
        return true;
      } else if (object instanceof Impl) {
        Impl that = (Impl) object;
        return x0 == that.x0 && x1 == that.x1 &&
            width == that.width && height == that.height;
      }
      return false;
    }

    @Override public int hashCode() {
      return Objects.hashCode(x0, y0, width, height);
    }

    @Override public String toString() {
      return String.format("forCornerAndSize(%s, %s, %s, %s),",
          x0, y0, width, height);
    }
  }
}
