package com.projetloki.genesis;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A mathematical function that describes how fast one-dimensional values change
 * during transitions or animations.
 *
 * <p>Timing functions are immutable.</p>
 *
 * @see Animation#timingFunction(TimingFunction)
 * @see Transition#timingFunction(TimingFunction)
 * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/timing-function">MDN</a>
 * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/transition-timing-function#Examples">Examples</a><span> on MDN</span>
 *
 * @author Clément Roux
 */
public final class TimingFunction extends SimpleStringWrapper {
  /**
   * The animation goes from its initial state to its final state with constant
   * speed.
   *
   * <p>Equivalent to {@code cubicBezier(0, 0, 1, 1)}.</p>
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/timing-function#linear">MDN</a>
   */
  public static final TimingFunction LINEAR = new TimingFunction("linear");
  /**
   * Similar to {@linkplain #EASE_IN_OUT ease-in-out}, but accelerates more
   * sharply at the beginning and the acceleration already starts to slow down
   * near the middle of it.
   *
   * <p>Equivalent to {@code cubicBezier(0.25, 0.1, 0.25, 1)}.</p>
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/timing-function#ease">MDN</a>
   */
  public static final TimingFunction EASE = new TimingFunction("ease");
  /**
   * Begins slowly, then progressively accelerates until the final state is
   * reached and the animation stops abruptly.
   *
   * <p>Equivalent to {@code cubicBezier(0.42, 0, 1, 1)}.</p>
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/timing-function#ease-in">MDN</a>
   */
  public static final TimingFunction EASE_IN = new TimingFunction("ease-in");
  /**
   * Starts quickly then slow progressively down when approaching to its final
   * state.
   *
   * <p>Equivalent to {@code  cubicBezier(0, 0, 0.58, 1)}.</p>
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/timing-function#ease-out">MDN</a>
   */
  public static final TimingFunction EASE_OUT = new TimingFunction("ease-out");
  /**
   * Starts slowly, accelerates than slows down when approaching to its final
   * state. At the begin, it behaves similarly to {@linkplain #EASE_IN ease-in},
   * at the end, it is similar to {@linkplain #EASE_OUT ease-out}.
   *
   * <p>Equivalent to {@code cubicBezier(0.42, 0, 0.58, 1)}.</p>
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/timing-function#ease-in-out">MDN</a>
   */
  public static final TimingFunction EASE_IN_OUT =
      new TimingFunction("ease-in-out");

  /**
   * Defines a cubic
   * <a href="http://en.wikipedia.org/wiki/B%C3%A9zier_curve">Bézier curve</a>.
   * As these curves are continuous, they are often used to smooth down the
   * start and end of the animation and are therefore sometimes called easing
   * functions.
   *
   * <p>The two x's must be in the range [0, 1].</p>
   * @param x1 the x-coordinate of the first point
   * @param y1 the y-coordinate of the first point
   * @param x2 the x-coordinate of the second point
   * @param y2 the y-coordinate of the second point
   * @throws IllegalArgumenException if x1 or x2 is not in [0, 1]
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/timing-function#The_cubic-bezier()_class_of_timing-functions">MDN</a>
   */
  public static TimingFunction cubicBezier(double x1, double y1,
      double x2, double y2) {
    checkArgument(0 <= x1 && x1 <= 1, "x1 (%s) must be in [0, 1]", x1);
    checkArgument(0 <= x2 && x2 <= 1, "x2 (%s) must be in [0, 1]", x2);
    String css = Util.functionalNotation("cubic-bezier",
        Format.number(x1),
        Format.number(y1),
        Format.number(x2),
        Format.number(y2));
    return new TimingFunction(css);
  }

  private TimingFunction(String css) {
    super(css);
  }
}
