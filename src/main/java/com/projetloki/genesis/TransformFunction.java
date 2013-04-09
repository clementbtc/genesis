package com.projetloki.genesis;

/**
 * A geometric transformation to apply to an element.
 *
 * <p>Geometric transformations are immutable.</p>
 *
 * @see Properties.Builder#setTransform(TransformFunction...)
 * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/transform#CSS_transform_functions">MDN</a>
 *
 * @author Clément Roux
 */
public final class TransformFunction extends SimpleStringWrapper {
  /**
   * Specifies a 2D translation by the vector [tx, ty].
   * Lengths are specified in pixels.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/transform#translate">MDN</a>
   */
  public static TransformFunction translatePx(int txPx, int tyPx) {
    return create("translate",
        LengthUnit.PX.format(txPx), LengthUnit.PX.format(tyPx));
  }

  /**
   * Specifies a 2D translation by the vector [tx, 0].
   * The length is specified in pixels.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/transform#translateX">MDN</a>
   */
  public static TransformFunction translateXPx(int txPx) {
    return create("translateX", LengthUnit.PX.format(txPx));
  }

  /**
   * Specifies a 2D translation by the vector [0, ty].
   * The length is specified in pixels.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/transform#translateY">MDN</a>
   */
  public static TransformFunction translateYPx(int tyPx) {
    return create("translateY", LengthUnit.PX.format(tyPx));
  }

  /**
   * Specifies a 2D scaling operation described by [kx, ky].
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/transform#scale">MDN</a>
   */
  public static TransformFunction scale(double kx, double ky) {
    return create("scale",
        Format.number(kx), Format.number(ky));
  }

  /**
   * Specifies a 2D scaling operation described by [kx, 1].
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/transform#scaleX">MDN</a>
   */
  public static TransformFunction scaleX(double kx) {
    return create("scaleX", Format.number(kx));
  }

  /**
   * Specifies a 2D scaling operation described by [1, ky].
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/transform#scaleY">MDN</a>
   */
  public static TransformFunction scaleY(double ky) {
    return create("scaleY", Format.number(ky));
  }

  /**
   * Rotates the element clockwise around its origin by the given angle,
   * specified in degrees.
   * The origin is defined with the transform-origin property.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/transform#rotateDeg">MDN</a>
   */
  public static TransformFunction rotateDeg(double deg) {
    return create("rotate", Format.formatDeg(deg));
  }

  /**
   * Skews the element around the X axis by the given angle.
   * The angle is specified in degrees.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/transform#skewX">MDN</a>
   */
  public static TransformFunction skewXDeg(double xDeg) {
    return create("skewX", Format.formatDeg(xDeg));
  }

  /**
   * Skews the element around the Y axis by the given angle.
   * The angle is specified in degrees.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/transform#skewY">MDN</a>
   */
  public static TransformFunction skewYDeg(double yDeg) {
    return create("skewY", Format.formatDeg(yDeg));
  }

  /**
   * Specifies a 2D transformation matrix comprised of the given six values.
   * @see <a href="http://www.w3.org/TR/css3-transforms/#MatrixDefined">www.w3.org</a>
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/transform#matrix">MDN</a>
   */
  public static TransformFunction matrix(double a, double b, double c, double d,
      double e, double f) {
    return create("matrix",
        Format.number(a),
        Format.number(b),
        Format.number(c),
        Format.number(d),
        Format.number(e),
        Format.number(f));
  }

  private static TransformFunction create(String functionName, String... args) {
    return new TransformFunction(Util.functionalNotation(functionName, args));
  }

  private TransformFunction(String css) {
    super(css);
  }
}
