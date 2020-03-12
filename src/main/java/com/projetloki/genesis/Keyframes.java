package com.projetloki.genesis;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import com.projetloki.genesis.image.Image;

/**
 * Defines initial, final and intermediate steps in a CSS animation sequence.
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
 * <p>
 * In order to exist in the generated CSS, every keyframe must be registered
 * with {@link CssBuilder#use(Keyframes)}.</p>
 *
 * <p>Keyframes are immutable.</p>
 *
 * @see Animation
 * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/@keyframes">MDN</a>
 * @see <a href="http://caniuse.com/css-animation">Browser support</a>
 *
 * @author Clément Roux
 */
@PoorBrowserSupport
public final class Keyframes extends AppendableToNoContext {
  private final String name;
  private final ImmutableList<Keyframe> keyframes;

  // Encoding for generating a name from the hash when none is specified
  private static final BaseEncoding ENCODING =
      BaseEncoding.base32().omitPadding();

  Keyframes(ImmutableList<Keyframe> keyframes) {
    this.keyframes = keyframes;
    Hasher hasher = Hashing.sha1().newHasher();
    for (Keyframe keyframe : keyframes) {
      hasher.putDouble(keyframe.percentage);
      hasher.putUnencodedChars(keyframe.properties);
    }
    byte[] bytes = hasher.hash().asBytes();
    int len = Math.min(bytes.length, 8);
    name = "kf-" + ENCODING.encode(bytes, 0, len).toLowerCase();
  }

  private Keyframes(String name, ImmutableList<Keyframe> keyframes) {
    this.name = checkNotNull(name);
    this.keyframes = checkNotNull(keyframes);
  }

  /**
   * Returns the name of this keyframes. This is the string you must pass to
   * {@link Animation#on(String)}.
   *
   * <p>
   * If you haven't explicitly specified a name with {@link #withName(String)},
   * one will be generated automatically.</p>
   */
  public String name() {
    return name;
  }

  /**
   * Returns a keyframes instance similar to {@code this}, with the given name.
   * If you don't specify a name, one will be generated automatically.
   * Call this method if you want to refer to the keyframes the Javascript.
   */
  public Keyframes withName(String name) {
    if (name.equals(this.name)) {
      return this;
    }
    Util.checkIdentifier(name);
    return new Keyframes(name, keyframes);
  }

  private static final ImmutableList<String> TITLES = ImmutableList.of(
      "-moz-keyframes", "-o-keyframes", "-webkit-keyframes", "keyframes");

  @Override void appendTo(StringBuilder out) {
    for (String title : TITLES) {
      out.append("@");
      out.append(title);
      out.append(' ');
      out.append(name);
      out.append("{\n");
      for (Keyframe keyframe : keyframes) {
        out.append("  ");
        out.append(Format.percentage(keyframe.percentage));
        out.append('{');
        out.append(keyframe.properties);
        out.append("}\n");
      }
      out.append("}\n");
    }
  }

  @Override public boolean equals(Object object) {
    if (object == this) {
      return true;
    } else if (object instanceof Keyframes) {
      Keyframes that = (Keyframes) object;
      return name.equals(that.name) &&
          keyframes.equals(that.keyframes);
    }
    return false;
  }

  @Override public int hashCode() {
    return Objects.hashCode(name, keyframes);
  }

  /**
   * Returns a new builder with the given initial state.
   * @throws IllegalArgumentException if the given properties object contains
   *     an {@linkplain Image image}
   */
  public static Builder from(PropertiesOrBuilder properties) {
    return new Builder(properties.build());
  }

  /**
   * Returns a new builder with the given initial state.
   * @throws IllegalArgumentException if the given properties object contains
   *     an {@linkplain Image image};
   *     if the given string can't be parsed as a
   *     {@linkplain Properties#from(String) properties}
   */
  public static Builder from(String properties) {
    return from(Properties.from(properties));
  }

  static final SpriteManager ALWAYS_FAILING_SPRITE_MANAGER =
      new SpriteManager() {
    @Override public String getImageUrl(Image image) {
      throw new IllegalArgumentException(
          "image use is illegal in keyframe: " + image);
    }

    @Override public BackgroundPosition getBackgroundPosition(Image image) {
      getImageUrl(image);
      throw new AssertionError();
    }

    @Override public void writeSprites(File folder) {}
  };

  /** A state. */
  private static class Keyframe {
    final double percentage;
    final String properties;

    Keyframe(double percentage, Properties properties) {
      this.percentage = percentage;
      StringBuilder builder = new StringBuilder();
      properties.appendTo(builder, ALWAYS_FAILING_SPRITE_MANAGER);
      this.properties = builder.toString();
    }

    @Override public boolean equals(Object object) {
      if (object instanceof Keyframe) {
        Keyframe that = (Keyframe) object;
        return percentage == that.percentage &&
            properties.equals(that.properties);
      }
      return false;
    }

    @Override public int hashCode() {
      return Objects.hashCode(percentage, properties);
    }
  }

  /** A builder for creating keyframes. */
  public static final class Builder {
    private final List<Keyframe> keyframes = Lists.newArrayList();

    Builder(Properties from) {
      keyframes.add(new Keyframe(0, from));
    }

    /**
     * Adds an intermediate state (or keyframe) to this builder.
     * Intermediate states must be specified in order.
     * @param percentage percentage of the time through the animation sequence
     *     at which the specified keyframe should occur
     * @return this builder for chaining
     * @throws IllegalArgumentException if the given number is not in ]0, 100[;
     *     if the given number is not greater than the last number specified;
     *     if the given properties object contains an {@linkplain Image image}
     */
    public Builder through(double percentage, PropertiesOrBuilder properties) {
      checkArgument(0 < percentage, "percentage (%s) must be > 0", percentage);
      checkArgument(percentage < 100,
          "percentage (%s) must be < 100", percentage);
      if (!keyframes.isEmpty()) {
        Keyframe last = keyframes.get(keyframes.size() - 1);
        checkArgument(last.percentage < percentage,
            "frames must be added in order");
      }
      Keyframe keyframe = new Keyframe(percentage, properties.build());
      keyframes.add(keyframe);
      return this;
    }

    /**
     * Adds an intermediate state (or keyframe) to this builder.
     * Intermediate states must be specified in order.
     * @param percentage percentage of the time through the animation sequence
     *     at which the specified keyframe should occur
     * @return this builder for chaining
     * @throws IllegalArgumentException if the given number is not in ]0, 100[;
     *     if the given number is not greater than the last number specified;
     *     if the given properties object contains an {@linkplain Image image};
     *     if the given string can't be parsed as a
     *     {@linkplain Properties#from(String) properties}
     */
    public Builder through(double percentage, String properties) {
      return through(percentage, Properties.from(properties));
    }

    /**
     * Returns a newly-created keyframes object based on the content of this
     * builder, and with the given final state.
     * @throws IllegalArgumentException if the given properties object contains
     *     an {@linkplain Image image}
     */
    public Keyframes to(PropertiesOrBuilder properties) {
      return new Keyframes(ImmutableList.<Keyframe>builder()
          .addAll(keyframes)
          .add(new Keyframe(1, properties.build()))
          .build());
    }

    /**
     * Returns a newly-created keyframes object based on the content of this
     * builder, and with the given final state.
     * @throws IllegalArgumentException if the given properties object contains
     *     an {@linkplain Image image};
     *     if the given string can't be parsed as a
     *     {@linkplain Properties#from(String) properties}
     */
    public Keyframes to(String properties) {
      return to(Properties.from(properties));
    }
  }
}
