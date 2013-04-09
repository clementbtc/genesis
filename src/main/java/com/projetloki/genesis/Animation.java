package com.projetloki.genesis;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

/**
 * CSS3 animations make it possible to animate transitions from one CSS style
 * configuration to another. Animations consist of two components, a style
 * describing the CSS animation and a set of {@linkplain Keyframes keyframes}
 * that indicate the start and end states of the animation's style, as well as
 * possible intermediate waypoints along the way. This class represents the
 * first component.
 *
 * <p>There are three key advantages to CSS animations over traditional
 * script-driven animation techniques:
 * <ol>
 * <li>They're easy to use for simple animations; you can create them without
 * even having to know JavaScript.</li>
 * <li>The animations run well, even under moderate system load. Simple
 * animations can often perform poorly in JavaScript (unless they're well made).
 * The rendering engine can use frame-skipping and other techniques to keep the
 * performance as smooth as possible.</li>
 * <li>Letting the browser control the animation sequence lets the browser
 * optimize performance and efficiency by, for example, reducing the update
 * frequency of animations running in tabs that aren't currently visible.</li>
 * </ol>
 * </p>
 *
 * <p>Example:<pre><code> Keyframes kf = Keyframes.from("top: 0;left; 0")
 *     .through(30, "top: 50%")
 *     .through(68, "left: 50%")
 *     .to("top: 100%; left:100%");
 *
 * cssBuilder.use(kf);
 * cssBuilder.addRule("div.myButton", Properties.builder()
 *     .setAnimation(
 *         Animation.on(kf.name())
 *             .durationSeconds(10)
 *             .delaySeconds(2)));
 * </code></pre>
 * </p>
 *
 * <p>Animations are immutable. Methods such as {@link #durationSeconds(double)}
 * perform a copy of the instance they are called on, modify the copy and return
 * it. The original instance is left unchanged.
 * </p>
 *
 * @see Keyframes
 * @see Transition
 * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/Tutorials/Using_CSS_animations">MDN</a>
 * @see <a href="http://caniuse.com/css-animations">Browser support</a>
 *
 * @author Clément Roux
 */
public final class Animation extends AppendableToNoContext {
  public static Animation on(String name) {
    return new Animation(name);
  }

  private final String name;
  private String duration;
  private TimingFunction timingFunction;
  private String delay;
  private String iterationCount;
  private String direction;

  private Animation(String name) {
    this.name = checkNotNull(name);
    this.duration = "0";
    this.timingFunction = TimingFunction.EASE;
    this.delay = "0";
    iterationCount = "0";
    direction = "normal";
  }

  private Animation(Animation copyFrom) {
    this.name = copyFrom.name;
    this.duration = copyFrom.duration;
    this.timingFunction = copyFrom.timingFunction;
    this.delay = copyFrom.delay;
    this.iterationCount = copyFrom.iterationCount;
    this.direction = copyFrom.direction;
  }

  /**
   * Returns a new animation similar to {@code this}, with the duration property
   * set to the given value.
   * Specifies the time the animation should take to complete one cycle.
   * Expressed in seconds.
   * @throws IllegalArgumentException if the duration is negative
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/animation-duration">MDN</a>
   */
  public Animation durationSeconds(double seconds) {
    Animation result = new Animation(this);
    result.duration = Format.formatSeconds(seconds);
    return result;
  }

  /**
   * Returns a new animation similar to {@code this}, with the timing-function
   * property set to the given value.
   * Specifies how a CSS animation should progress over the duration of each
   * cycle.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/animation-timing-function">MDN</a>
   */
  public Animation timingFunction(TimingFunction function) {
    Animation result = new Animation(this);
    result.timingFunction = checkNotNull(function);
    return result;
  }

  /**
   * Returns a new animation similar to {@code this}, with the delay property
   * set to the given value.
   * Specifies when the animation should start. This lets the animation sequence
   * begin some time after it's applied to an element.
   * Expressed in seconds.
   * @throws IllegalArgumentException if the duration is negative
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/animation-delay">MDN</a>
   */
  public Animation delaySeconds(double seconds) {
    Animation result = new Animation(this);
    result.delay = Format.formatSeconds(seconds);
    return result;
  }

  /**
   * Returns a new animation similar to {@code this}, with the iteration-count
   * property set to the given value.
   * Defines the number of times an animation cycle should be played before
   * stopping.
   * @throws IllegalArgumentException if the given number is negative
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/animation-iteration-count">MDN</a>
   */
  public Animation iterationCount(int count) {
    checkArgument(0 <= count, "negative count: %s", count);
    Animation result = new Animation(this);
    result.iterationCount = count + "";
    return result;
  }

  /**
   * Returns a new animation similar to {@code this}, with the iteration-count
   * property set to 'infinite'.
   * The animation will repeat forever.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/animation-iteration-count">MDN</a>
   */
  public Animation infinite() {
    Animation result = new Animation(this);
    result.iterationCount = "infinite";
    return result;
  }

  /**
   * Returns a new animation similar to {@code this}, with the direction
   * property set to 'alternate'.
   * The animation will repeat forever. The animation should reverse direction
   * each cycle. When playing in reverse, the animation steps are performed
   * backward. In addition, timing functions are also reversed; for example, an
   * ease-in animation is replaced with an ease-out animation when played in
   * reverse. The count to determinate if it is an even or an odd iteration
   * starts at one.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/animation-direction">MDN</a>
   */
  public Animation alternate() {
    Animation result = new Animation(this);
    result.direction = "alternate";
    return result;
  }

  @Override void appendTo(StringBuilder out) {
    out.append(name);
    if (!duration.equals("0") || !delay.equals("0") ||
        !iterationCount.equals("0")) {
      out.append(' ');
      out.append(duration);
    }
    if (timingFunction != TimingFunction.EASE) {
      out.append(' ');
      out.append(timingFunction);
    }
    if (!delay.equals("0") || iterationCount.equals("0")) {
      out.append(' ');
      out.append(delay);
    }
    if (!iterationCount.equals("0")) {
      out.append(' ');
      out.append(iterationCount);
    }
    if (!direction.equals("normal")) {
      out.append(' ');
      out.append(direction);
    }
  }

  @Override public boolean equals(Object object) {
    if (object == this) {
      return true;
    } else if (object instanceof Animation) {
      Animation that = (Animation) object;
      return name.equals(that.name) &&
          duration.equals(that.duration) &&
          timingFunction.equals(that.timingFunction) &&
          delay.equals(that.delay) &&
          iterationCount.equals(that.iterationCount) &&
          direction.equals(that.direction);
    }
    return false;
  }

  @Override public int hashCode() {
    return Objects.hashCode(name, duration, timingFunction, delay,
        iterationCount, direction);
  }
}
