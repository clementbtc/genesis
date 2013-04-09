package com.projetloki.genesis;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.projetloki.genesis.image.Color;

/**
 * A shadow to add to a text or a box. Example:
 * <pre><code> Properties props = Properties.builder()
 *     .setColor(Color.RED)
 *     .setTextShadow(
 *         Shadow.offsetPx(1, 1)
 *             .blurPx(2)
 *             .color(Color.BLACK),
 *         Shadow.offsetPx(0, 0)
 *             .blurEm(1)
 *             .color(Color.BLUE))
 *     .build();
 * </code></pre>
 *
 * <p>
 * Some properties of shadow only apply to boxes. When the shadow is
 * {@linkplain Properties.Builder#setTextShadow(Shadow...) applied} to a text,
 * such properties are ignored and don't figure in the generated CSS code.</p>
 *
 * <p>
 * Shadows are immutable. Methods such as {@link #blurPx(int)} perform a copy of
 * the instance they are called on, modify the copy and return it. The original
 * instance is left unchanged.</p>
 *
 * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/text-shadow#Examples">text-shadow examples</a><span> (MDN)</span>
 * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/box-shadow#Examples">box-shadow examples</a><span> (MDN)</span>
 * @see <a href="http://caniuse.com/css-textshadow">Browser support</a><span> (text shadows)</spam>
 * @see <a href="http://caniuse.com/css-boxshadow">Browser support</a><span> (box shadows)</span>
 *
 * @author Clément Roux
 */
@PoorBrowserSupport
public final class Shadow {
  /**
   * Returns a new shadow with the given offset, specified in pixels.
   * Negative values place the shadow to the left of/above the element.
   * The blur radius and the spread radius are both zero, and the color is the
   * color of the text for text shadows, and depends on the browser for box
   * shadows.
   */
  public static Shadow offsetPx(int xOffsetPx, int yOffsetPx) {
    return new Shadow(LengthUnit.PX.format(xOffsetPx),
        LengthUnit.PX.format(yOffsetPx));
  }

  /**
   * Returns a new shadow with the given offset, specified in pixels.
   * Negative values place the shadow to the left of/above the element.
   * The blur radius and the spread radius are both zero, and the color is the
   * color of the text for text shadows, and depends on the browser for box
   * shadows.
   */
  public static Shadow offsetEm(double xOffsetEm, double yOffsetEm) {
    return new Shadow(LengthUnit.EM.format(xOffsetEm),
        LengthUnit.EM.format(yOffsetEm));
  }

  private final String xOffset;
  private final String yOffset;
  private String blur = "0";
  private String spread = "0";
  private Color color;
  private String inset;
  private volatile String string;
  private volatile String stringForTextShadow;

  private Shadow(String xOffset, String yOffset) {
    this.xOffset = xOffset;
    this.yOffset = yOffset;
  }

  private Shadow(Shadow copyFrom) {
    xOffset = copyFrom.xOffset;
    yOffset = copyFrom.yOffset;
    blur = copyFrom.blur;
    spread = copyFrom.spread;
    color = copyFrom.color;
    inset = copyFrom.inset;
  }

  /**
   * Returns a shadow that is similar to {@code this}, with the blur radius
   * property set to the given length. Specified in pixels.
   * The larger the length, the bigger the blur, so the shadow becomes bigger
   * and lighter. Negative values are not allowed.
   * @throws IllegalArgumentException if the length is negative
   */
  public Shadow blurPx(int blurPx) {
    return blur(LengthUnit.PX.formatPositive(blurPx));
  }

  /**
   * Returns a shadow that is similar to {@code this}, with the blur radius
   * property set to the given length. Specified in ems.
   * The larger the length, the bigger the blur, so the shadow becomes bigger
   * and lighter. Negative values are not allowed.
   * @throws IllegalArgumentException if the length is negative
   */
  public Shadow blurPx(double blurEm) {
    return blur(LengthUnit.EM.formatPositive(blurEm));
  }

  private Shadow blur(String blur) {
    if (blur.equals(this.blur)) {
      return this;
    }
    Shadow result = new Shadow(this);
    result.blur = blur;
    return result;
  }

  /**
   * Returns a new shadow similar to {@code this}, with the spread radius
   * property set to the given length. Specified in pixels.
   * Positive values will cause the shadow to expand and grow bigger, negative
   * values will cause the shadow to shrink.
   *
   * <p>Only applies to box shadows.</p>
   */
  public Shadow spreadPx(int spreadPx) {
    return spread(LengthUnit.PX.format(spreadPx));
  }

  /**
   * Returns a new shadow similar to {@code this}, with the spread radius
   * property set to the given length. Specified in ems.
   * Positive values will cause the shadow to expand and grow bigger, negative
   * values will cause the shadow to shrink.
   *
   * <p>Only applies to box shadows.</p>
   */
  public Shadow spreadPx(double spreadEm) {
    return spread(LengthUnit.EM.format(spreadEm));
  }

  private Shadow spread(String spread) {
    if (spread.equals(this.spread)) {
      return this;
    }
    Shadow result = new Shadow(this);
    result.spread = spread;
    return result;
  }

  /**
   * Returns a new shadow similar to {@code this}, with the color property set
   * to the given color.
   */
  public Shadow color(Color color) {
    if (color.equals(this.color)) {
      return this;
    }
    Shadow result = new Shadow(this);
    result.color = color;
    return result;
  }

  /**
   * Returns a new shadow similar to {@code this}, with the inset property.
   * Inset shadows are drawn inside the border (even transparent ones), above
   * the background, but below content.
   *
   * <p>Only applies to box shadows.</p>
   */
  public Shadow inset() {
    if (inset != null) {
      return this;
    }
    Shadow result = new Shadow(this);
    result.inset = "inset";
    return result;
  }

  /** For {@link #equals(Object)} and {@link #hashCode()}. */
  private Object[] components() {
    return new Object[] {
        xOffset,
        yOffset,
        blur,
        spread,
        color,
        inset
    };
  }

  @Override public boolean equals(Object object) {
    if (object == this) {
      return true;
    } else if (object instanceof Shadow) {
      Shadow that = (Shadow) object;
      return Arrays.equals(components(), that.components());
    }
    return false;
  }

  @Override public int hashCode() {
    return Arrays.hashCode(components());
  }

  /**
   * Returns the CSS code for this shadow, omitting properties that don't apply
   * to text shadows.
   */
  String toStringForTextShadow() {
    String result = stringForTextShadow;
    if (result == null) {
      List<String> components = Lists.newArrayListWithCapacity(4);
      components.add(xOffset);
      components.add(yOffset);
      if (!blur.equals("0")) {
        components.add(blur);
      }
      if (color != null) {
        components.add(color.toCssCode());
      }
      stringForTextShadow = result = Joiner.on(' ').join(components);
    }
    return result;
  }

  @Override public String toString() {
    String result = string;
    if (result == null) {
      List<String> components = Lists.newArrayListWithCapacity(6);
      components.add(xOffset);
      components.add(yOffset);
      if (spread.equals("0")) {
        if (!blur.equals("0")) {
          components.add(blur);
        }
      } else {
        components.add(blur);
        components.add(spread);
      }
      if (color != null) {
        components.add(color.toCssCode());
      }
      if (inset != null) {
        components.add("inset");
      }
      string = result = Joiner.on(' ').join(components);
    }
    return result;
  }
}
