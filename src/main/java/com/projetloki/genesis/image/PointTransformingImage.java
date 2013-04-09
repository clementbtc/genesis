package com.projetloki.genesis.image;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Base class for all images obtained by moving all the points of another image
 * (the operand), leaving the color and the transparency unchanged.
 *
 * <p>
 * As this implementation extends {@link ImageWithSerializationProxy}, the same
 * conditions apply, in particular:
 * <ul>
 * <li>a equal to b implies {@code a.getClass() == b.getClass()}</li>
 * <li>{@code initSize} must be called in the constructor
 * </ul></p>
 *
 * <p>
 * This is and should remain INTERNAL, make sure that this class does not leak
 * into the API.</p>
 *
 * @param <T> the type itself
 *
 * @author Clément Roux
 */
abstract class PointTransformingImage<T>
    extends ImageWithSerializationProxy<T> {
  final Image operand;

  PointTransformingImage(Image operand) {
    this.operand = checkNotNull(operand);
  }

  /** Returns the point associated with the given point. */
  abstract Point transformPoint(Point p);

  @Override public final Color getColor(Point p) {
    return operand.getColor(transformPoint(p));
  }

  @Override public final double getAlpha(Point p) {
    return operand.getAlpha(transformPoint(p));
  }
}
