package com.projetloki.genesis.image;

/**
 * A shape that does not shrink naturally. The shrunk shape is obtained by
 * rasterizing the original shape, and negating all pixels that are margin-far
 * from a false pixel. This process is expensive and not esthetic on zooming.
 *
 * @author Clément Roux
 */
abstract class RasterOnShrinkShape<T> extends HashCachingShape<T>
    implements HasSize {
  private volatile RasterShapeManager manager;

  /**
   * Returns the position of the top-left corner of the smallest bounding box
   * around the shape.
   */
  abstract Point position();

  RasterShapeManager manager() {
    RasterShapeManager result = manager;
    if (result == null) {
      synchronized (this) {
        result = manager;
        if (result == null) {
          manager = result = new RasterShapeManager(this, width(), height());
        }
      }
    }
    return result;
  }

  @Override public final Shape shrink(double margin) {
    return manager().shrinkShape(margin);
  }
}
