package com.projetloki.genesis;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

/**
 * Transitions, which are part of the CSS3 set of specifications, provide a way
 * to control animation speed when changing CSS properties. Instead of having
 * property changes take effect immediately, you can cause the changes in a
 * property to take place over a period of time. For example, if you change the
 * color of an element from white to black, usually the change is instantaneous.
 * With CSS transitions enabled, changes occur at time intervals that follow an
 * acceleration curve, all of which can be customized.
 *
 * <p>
 * Animations that involve transitioning between two states are often called
 * implicit transitions as the states in between the start and final states are
 * implicitly defined by the browser.</p>
 *
 * <p>Example:
 * <pre><code> void configure(CssBuilder out) {
 *   out.addRule(".box", Properties.builder()
 *       .setWidthPx(100)
 *       .setTransition(Transition.onProperty("width")
 *           .delaySeconds(0.5)
 *           .durationSeconds(3)));
 *
 *   // When the pointing device is on the box, the box will grow
 *   // horizontally with a nice transition effect
 *   out.addRule(".box:hover", Properties.builder()
 *       .setWidthPx(200));
 * }
 * </code></pre>
 * </p>
 *
 * <p>
 * Transitions are immutable. Methods such as {@link #durationSeconds(double)}
 * perform a copy of the instance they are called on, modify the copy and return
 * it. The original instance is left unchanged.</p>
 *
 * @see Animation
 * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/Tutorials/Using_CSS_transitions">MDN</a>
 * @see <a href="http://caniuse.com/css-transitions">Browser support</a>
 *
 * @author Clément Roux
 */
public final class Transition extends AppendableToNoContext {
  /**
   * Returns a transition for the given CSS property.
   * The delay and the duration are both zero (no animation occurs), and the
   * timing function is {@linkplain TimingFunction#EASE ease}.
   * @throws IllegalArgumentException if the given property is not
   *     a valid CSS identifier
   */
  public static Transition onProperty(String property) {
    Util.checkIdentifier(property);
    return new Transition(property);
  }

  private final String property;
  private String duration;
  private TimingFunction timingFunction;
  private String delay;

  private Transition(String property) {
    this.property = checkNotNull(property);
    this.duration = "0";
    this.timingFunction = TimingFunction.EASE;
    this.delay = "0";
  }

  private Transition(Transition copyFrom) {
    this.property = copyFrom.property;
    this.duration = copyFrom.duration;
    this.timingFunction = copyFrom.timingFunction;
    this.delay = copyFrom.delay;
  }

  /**
   * Returns a new transition similar to {@code this}, with the duration
   * property set to the given value.
   * Specifies the time the transition animation should take to complete.
   * Expressed in seconds.
   * @throws IllegalArgumentException if the duration is negative
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/transition-duration">MDN</a>
   */
  public Transition durationSeconds(double seconds) {
    Transition result = new Transition(this);
    result.duration = Format.formatSeconds(seconds);
    return result;
  }

  /**
   * Returns a new transition similar to {@code this}, with the timing-function
   * property set to the given value.
   * Describe how the intermediate values of the CSS properties being affected
   * by a transition effect are calculated.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/transition-timing-function">MDN</a>
   */
  public Transition timingFunction(TimingFunction function) {
    Transition result = new Transition(this);
    result.timingFunction = checkNotNull(function);
    return result;
  }

  /**
   * Returns a new transition similar to {@code this}, with the delay property
   * set to the given value.
   * Specifies the amount of time to wait between a change being requested to a
   * property that is to be transitioned and the start of the transition effect.
   * Expressed in seconds.
   * @throws IllegalArgumentException if the duration is negative
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/transition-delay">MDN</a>
   */
  public Transition delaySeconds(double seconds) {
    Transition result = new Transition(this);
    result.delay = Format.formatSeconds(seconds);
    return result;
  }

  @Override void appendTo(StringBuilder out) {
    out.append(property);
    if (!duration.equals("0") || !delay.equals("0")) {
      out.append(' ');
      out.append(duration);
    }
    if (timingFunction != TimingFunction.EASE) {
      out.append(' ');
      out.append(timingFunction);
    }
    if (!delay.equals("0")) {
      out.append(' ');
      out.append(delay);
    }
  }

  @Override public boolean equals(Object object) {
    if (object == this) {
      return true;
    } else if (object instanceof Transition) {
      Transition that = (Transition) object;
      return property.equals(that.property) &&
          duration.equals(that.duration) &&
          timingFunction.equals(that.timingFunction) &&
          delay.equals(that.delay);
    }
    return false;
  }

  @Override public int hashCode() {
    return Objects.hashCode(property, duration, timingFunction, delay);
  }
}
