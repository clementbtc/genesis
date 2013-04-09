package com.projetloki.genesis;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

/**
 * A media type, and possibly one or more expressions
 * ({@linkplain MediaFeature media features}), which resolve to either true or
 * false.
 * The result of the query is true if the media type specified in the media
 * query matches the type of device the document is being displayed on and all
 * expressions in the media query are true.
 *
 * <p>Several media queries can be combined with the
 * {@linkplain #or(MediaCondition) or} operator. The result is a
 * {@linkplain MediaCondition media condition}.</p>
 *
 * <p>Media queries are immutable.</p>
 *
 * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/Media_queries">MDN</a>
 *
 * @author Clément Roux
 */
public abstract class MediaQuery extends MediaCondition {
  // Media groups
  /** Suitable for all devices. */
  public static final MediaQuery ALL = new MediaTypeOrGroup("all");

  // Media types
  /** Intended for braille tactile feedback devices. */
  public static final MediaQuery BRAILLE = new MediaTypeOrGroup("braille");
  /** Intended for paged braille printers. */
  public static final MediaQuery EMBOSSED = new MediaTypeOrGroup("embossed");
  /**
   * Intended for handheld devices (typically small screen, limited bandwidth).
   */
  public static final MediaQuery HANDHELD = new MediaTypeOrGroup("handheld");
  /**
   * Intended for paged material and for documents viewed on screen in print
   * preview mode.
   */
  public static final MediaQuery PRINT = new MediaTypeOrGroup("print");
  /** Intended for projected presentations, for example projectors. */
  public static final MediaQuery PROJECTION =
      new MediaTypeOrGroup("projection");
  /** Intended primarily for color computer screens. */
  public static final MediaQuery SCREEN = new MediaTypeOrGroup("screen");
  /** Intended for speech synthesizers. */
  public static final MediaQuery SPEECH = new MediaTypeOrGroup("speech");
  /**
   * Intended for media using a fixed-pitch character grid (such as teletypes,
   * terminals, or portable devices with limited display capabilities). Authors
   * should not use pixel units with the 'tty' media type.
   */
  public static final MediaQuery TTY = new MediaTypeOrGroup("tty");
  /**
   * Intended for television-type devices (low resolution, color,
   * limited-scrollability screens, sound available).
   */
  public static final MediaQuery TV = new MediaTypeOrGroup("tv");

  /**
   * Returns a new media query that evaluates to true if both {@code this} query
   * and the given media feature do.
   */
  @PoorBrowserSupport
  public MediaQuery and(MediaFeature feature) {
    return new AndQuery(this, feature);
  }

  /**
   * Returns a media condition that evaluates to true if and only if
   * {@code this} query evaluates to false.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/Media_queries#not">MDN</a>
   */
  @PoorBrowserSupport
  public MediaCondition not() {
    return new NotCondition(this);
  }

  /**
   * Returns a media condition that evaluates to true if {@code this} query does
   * and the browser supports media features.
   * Prevents older browsers that do not support media queries with media
   * features from applying styles.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/Media_queries#only">MDN</a>
   * @see <a href="http://caniuse.com/css-mediaqueries">Browser support</a>
   */
  public MediaCondition onlyIfFeaturesAreSupported() {
    return new OnlyCondition(this);
  }

  MediaQuery() {}

  private static class MediaTypeOrGroup extends MediaQuery {
    final String id;

    MediaTypeOrGroup(String id) {
      this.id = id;
    }

    @Override void appendTo(StringBuilder out) {
      out.append(id);
    }

    // No need to override #equals and #hashCode

    @Override public String toString() {
      return id;
    }
  }

  private static class NotCondition extends MediaCondition {
    final MediaQuery operator;

    NotCondition(MediaQuery operator) {
      this.operator = checkNotNull(operator);
    }

    @Override void appendTo(StringBuilder out) {
      out.append("not ");
      operator.appendTo(out);
    }

    @Override public boolean equals(Object object) {
      return object instanceof NotCondition &&
          (((NotCondition) object).operator).equals(operator);
    }

    @Override public int hashCode() {
      return operator.hashCode() + 16264219;
    }
  }

  private static class OnlyCondition extends MediaCondition {
    final MediaQuery operator;

    OnlyCondition(MediaQuery operator) {
      this.operator = checkNotNull(operator);
    }

    @Override void appendTo(StringBuilder out) {
      out.append("only ");
      operator.appendTo(out);
    }

    @Override public boolean equals(Object object) {
      return object instanceof OnlyCondition &&
          (((OnlyCondition) object).operator).equals(operator);
    }

    @Override public int hashCode() {
      return operator.hashCode()  -226326;
    }
  }

  private static class AndQuery extends MediaQuery {
    final MediaQuery left;
    final MediaFeature right;

    AndQuery(MediaQuery left, MediaFeature right) {
      this.left = checkNotNull(left);
      this.right = checkNotNull(right);
    }

    @Override void appendTo(StringBuilder out) {
      if (left != ALL) {
        left.appendTo(out);
        out.append(" and ");
      }
      out.append(right);
    }

    @Override public boolean equals(Object object) {
      if (object == this) {
        return true;
      } else if (object instanceof AndQuery) {
        AndQuery that = (AndQuery) object;
        return left.equals(that.left) && right.equals(that.right);
      }
      return false;
    }

    @Override public int hashCode() {
      return Objects.hashCode(left, right);
    }
  }
}
