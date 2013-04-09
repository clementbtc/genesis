package com.projetloki.genesis;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Ascii;
import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.projetloki.genesis.image.Color;
import com.projetloki.genesis.image.Image;

/**
 * An immutable set of CSS properties.
 *
 * <p>There are two ways to construct an instance. You can either set properties
 * one by one using a {@linkplain Builder builder}, or directly pass the CSS
 * code to the {@linkplain #from(String) from} method. Example:
 * <pre><code> Properties props1 = Properties.builder()
 *     .setColor(Color.BLACK)
 *     .setMarginPx(2, 4)
 *     .setWidthPx(100)
 *     .build();
 *
 * Properties props2 = Properties.from(
 *     "color: #000;" +
 *     "margin: 2px 4px;" +
 *     "width: 100px");
 *
 * assertEquals(props1, props2);
 * </code></pre>
 * </p>
 *
 * @author Clément Roux
 */
public final class Properties extends AppendableTo
    implements PropertiesOrBuilder {
  final ImmutableMap<String, String> properties;
  // null if layers is null
  final Color backgroundColor;
  // Not-null if and only if properties maps "background" to ""
  // Cannot be empty
  final ImmutableList<BackgroundLayer> layers;
  // Not-null if and only if properties maps "list-style-image" to ""
  final Image listStyleImage;

  Properties(Builder builder) {
    properties = ImmutableMap.copyOf(builder.properties);
    backgroundColor = builder.backgroundColor;
    layers = builder.layers;
    listStyleImage = builder.listStyleImage;
    // Can only happen if concurrent threads access the builder in the same time
    // The builder is not documented as thread-safe, so it's arguable whether we
    // even have to perform these checks
    checkArgument(backgroundColor == null || layers != null);
    checkArgument((layers != null) ==
        "".equals(properties.get("background")));
    checkArgument((listStyleImage != null) ==
        "".equals(properties.get("list-style-image")));
  }

  /**
   * Constructs a new instance from a sequence of semicolon-separated name:value
   * CSS properties. Does not perform any kind of validation beyond grammar
   * check. For example, calling {@code from("border-width=2px")} will fail
   * because the name-value separator must be a colon,  but calling
   * {@code from("border-width:black")} will succeed.
   *
   * <p>The input string can contain CSS comments, though they will be
   * discarded.
   * There can be a semicolon after the last name:value property, but this is
   * not required.</p>
   * @throws IllegalArgumentException if the input string is not a
   *     grammatically-correct sequence of semicolon-separated name:value CSS
   *     properties
   */
  public static Properties from(String propertiesString) {
    return PARSER.from(propertiesString);
  }

  static final Parser<Properties> PARSER = new Parser<Properties>() {
    @Override Properties tryParse(ParserInput input) {
      Builder builder = builder();
      while (true) {
        input.skipAllSpacesAndComments();
        String nameOrNull = input.tryReadId();
        if (nameOrNull == null) {
          return builder.build();
        }
        input.skipAllSpacesAndComments();
        input.checkStartsWithAndMove(":");
        PropertyValue value = PropertyValue.PARSER.parse(input);
        builder.set(nameOrNull, value);
        if (!input.startsWithThenMove(";")) {
          return builder.build();
        }
      }
    }

    @Override String what() {
      return "properties";
    }
  };

  /**
   * Returns this.
   * Provided to satisfy the {@link PropertiesOrBuilder} interface.
   */
  @Override public Properties build() {
    return this;
  }

  @Override void appendTo(StringBuilder out, CssGenerationContext context) {
    boolean addSemicolon = false;
    for (Entry<String, String> entry : properties.entrySet()) {
      String name = entry.getKey();
      String value = entry.getValue();
      if (value.isEmpty() && name.equals("background")) {
        // Hack for browsers that don't support multiple background images
        // The background property is defined twice:
        //   background: [layerN] [color];
        //   background: [layer1] ... [layerN] [color];
        addSemicolon = addSemicolon(out, addSemicolon);
        if (2 <= layers.size()) {
          out.append("background:");
          computeBackgroundValueForCompatibility(out, context);
          out.append(';');
        }
        out.append("background:");
        computeBackgroundValue(out, context);
      } else {
        // Not background
        if (value.isEmpty() && name.equals("list-style-image")) {
          value = context.getImageUrl(listStyleImage);
        }
        addSemicolon = addSemicolon(out, addSemicolon);
        out.append(name);
        out.append(':');
        out.append(value);
      }
    }
  }

  // Syntactic sugar:
  //   boolean addSemicolon = false;
  //   for ( ; ; ) {
  //     addSemicolon = addSemicolon(out, addSemicolon);
  //     ...
  //   }
  private static boolean addSemicolon(StringBuilder out, boolean addSemicolon) {
    if (addSemicolon) {
      out.append(';');
    }
    return true;
  }

  // Internal getters

  boolean hasLayers() {
    return layers != null;
  }

  ImmutableList<BackgroundLayer> layers() {
    checkState(hasLayers());
    return layers;
  }

  boolean hasListStyleImage() {
    return listStyleImage != null;
  }

  Image listStyleImage() {
    checkState(hasListStyleImage());
    return listStyleImage;
  }

  private void computeBackgroundValue(StringBuilder out,
      CssGenerationContext context) {
    Iterator<BackgroundLayer> it = layers.iterator();
    if (it.hasNext()) {
      it.next().appendTo(out, context);
      while (it.hasNext()) {
        out.append(',');
        it.next().appendTo(out, context);
      }
    }
    if (backgroundColor != null) {
      out.append(' ');
      out.append(backgroundColor.toCssCode());
    }
  }

  private void computeBackgroundValueForCompatibility(StringBuilder out,
      CssGenerationContext context) {
    // Pick the deepest layer
    layers.get(layers.size() - 1).appendTo(out, context);
    if (backgroundColor != null) {
      out.append(' ');
      out.append(backgroundColor.toCssCode());
    }
  }

  @Override public boolean equals(Object object) {
    if (object == this) {
      return true;
    } else if (object instanceof Properties) {
      Properties that = (Properties) object;
      List<Entry<String, String>> propertyList =
          properties.entrySet().asList();
      List<Entry<String, String>> thatPropertyList =
          that.properties.entrySet().asList();
      return propertyList.equals(thatPropertyList) &&
          Objects.equal(backgroundColor, that.backgroundColor) &&
          Objects.equal(layers, that.layers) &&
          Objects.equal(listStyleImage, that.listStyleImage);
    }
    return false;
  }

  @Override public int hashCode() {
    return Objects.hashCode(properties, backgroundColor, layers,
        listStyleImage);
  }

  /** Returns a new builder. */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * A builder for creating sets of CSS properties.
   * Contains methods that set a single properties from one or several Java
   * values. For example, {@link #setLetterSpacingPx(int)} sets the
   * letter-spacing property to the given length expressed in pixels. Some
   * methods are simply named set, because they expect a single parameter of
   * type enum, and the name of the enum tells which property is being set.
   * For example, {@code set(TextAlignValue.CENTER)} sets the value of the
   * text-align property to 'center'.
   * All these methods are called typesafe setters.
   *
   * <p>Typesafe setters will fail with an illegal argument exception if the
   * given value is illegal according to the CSS specification. For example,
   * passing -2 to the {@linkplain #setBorderWidthPx(int) border-width} setter
   * will fail because it is specified that a border-width cannot be negative.
   * </p>
   *
   * <p>Some typesafe setters may set vendor-specific properties under the hood
   * to fix browser compatibility issues. This behavior is undocumented and
   * subject to change.
   * </p>
   *
   * <p>Typesafe setters won't allow you to set all existing CSS properties to
   * all possible values.
   * In some cases, you may have no other choice than calling
   * {@link #set(String, String)}. This method does not perform any kind of
   * verification (besides grammar check). It is recommended that you only call
   * it if there is no typesafe setter that would do the job.
   * </p>
   *
   * <p>Note: as in CSS the order in which properties are set DOES matter
   * sometimes. For example, setting the property margin after margin-top is
   * different from setting the property margin-top after margin.
   * </p>
   *
   * @author Clément Roux
   */
  public static final class Builder implements PropertiesOrBuilder {
    private static final Joiner COMMA_JOINER = Joiner.on(',');

    final Map<String, String> properties = Maps.newLinkedHashMap();
    Color backgroundColor;
    ImmutableList<BackgroundLayer> layers;
    Image listStyleImage;

    Builder() {}

    /**
     * Copies all the properties from the given object to this builder.
     * @return this builder for chaining
     */
    public Builder copyFrom(Properties properties) {
      putAllLast(properties.properties);
      if (properties.hasLayers()) {
        layers = properties.layers;
      }
      if (properties.hasListStyleImage()) {
        listStyleImage = properties.listStyleImage;
      }
      return this;
    }

    /**
     * Copies all the properties from the given object to this builder.
     * @return this builder for chaining
     * @throws IllegalArgumentException if the input string is not a
     *     grammatically-correct sequence of semicolon-separated name:value CSS
     *     properties
     */
    public Builder copyFrom(String propertiesString) {
      return copyFrom(Properties.from(propertiesString));
    }

    /**
     * Sets the property with the given name to the given value.
     * Does not perform any kind of verification (besides grammar check).
     *
     * <p>It is recommended that you only call this method if there is no
     * typesafe setter that would do the job.
     * </p>
     * @return this builder for chaining
     * @throws IllegalArgumentException if the given name is not a valid CSS
     *     identifier; if the given value is a grammatically-valid CSS
     *     expression
     */
    public Builder set(String name, String value) {
      // Try to parse the value for validation
      return set(Util.checkIdentifier(name), PropertyValue.PARSER.from(value));
    }

    Builder set(String name, PropertyValue value) {
      // name is already supposed to be a valid id
      name = Ascii.toLowerCase(name);
      putLast(name, value.toString());
      if (name.equals("background")) {
        backgroundColor = null;
        layers = null;
      } else if (name.equals("list-style-image")) {
        listStyleImage = null;
      }
      return this;
    }

    public Builder setBackground(Color backgroundColor,
        BackgroundLayer... layers) {
      if (layers.length == 0) {
        this.backgroundColor = null;
        this.layers = null;
        putLast("background", backgroundColor.toCssCode());
      } else {
        this.backgroundColor = checkNotNull(backgroundColor);
        this.layers = ImmutableList.copyOf(layers);
        // Will be replaced in #appendTo
        putLast("background", "");
      }
      return this;
    }

    public Builder setBackground(BackgroundLayer... layers) {
      if (layers.length == 0) {
        this.backgroundColor = null;
        this.layers = null;
        putLast("background", "none");
      } else {
        this.backgroundColor = null;
        this.layers = ImmutableList.copyOf(layers);
        // Will be replaced in #appendTo
        putLast("background", "");
      }
      return this;
    }

    public Builder setListStyleImage(Image image) {
      this.listStyleImage = checkNotNull(image);
      // Will be replaced in #appendTo
      putLast("list-style-image", "");
      return this;
    }

    public Builder setHeightPx(int px) {
      return putLast("height", LengthUnit.PX.formatPositive(px));
    }

    public Builder setHeightPct(double heightPct) {
      return putLast("height", Format.positivePercentageOrZero(heightPct));
    }

    public Builder setWidthPx(int px) {
      return putLast("width", LengthUnit.PX.formatPositive(px));
    }

    public Builder setWidthPct(double pct) {
      return putLast("width", Format.positivePercentageOrZero(pct));
    }

    public Builder setMinHeightPx(int px) {
      return putLast("min-height", LengthUnit.PX.formatPositive(px));
    }

    public Builder setMinHeightPct(double pct) {
      return putLast("min-height", Format.positivePercentageOrZero(pct));
    }

    public Builder setMinWidthPx(int px) {
      return putLast("min-width", LengthUnit.PX.formatPositive(px));
    }

    public Builder setMinWidthPct(double pct) {
      return putLast("min-width", Format.positivePercentageOrZero(pct));
    }

    public Builder setMaxHeightPx(int px) {
      return putLast("max-height", LengthUnit.PX.formatPositive(px));
    }

    public Builder setMaxHeightPct(double pct) {
      return putLast("max-height", Format.positivePercentageOrZero(pct));
    }

    public Builder setMaxWidthPx(int px) {
      return putLast("max-width", LengthUnit.PX.formatPositive(px));
    }

    public Builder setMaxWidthPct(double pct) {
      return putLast("max-width", Format.positivePercentageOrZero(pct));
    }

    public Builder setTopPx(int px) {
      return putLast("top", LengthUnit.PX.format(px));
    }

    public Builder setTopPct(double pct) {
      return putLast("top", Format.percentageOrZero(pct));
    }

    public Builder setRightPx(int px) {
      return putLast("right", LengthUnit.PX.format(px));
    }

    public Builder setRightPct(double pct) {
      return putLast("right", Format.percentageOrZero(pct));
    }

    public Builder setBottomPx(int px) {
      return putLast("bottom", LengthUnit.PX.format(px));
    }

    public Builder setBottomPct(double pct) {
      return putLast("bottom", Format.percentageOrZero(pct));
    }

    public Builder setLeftPx(int px) {
      return putLast("left", LengthUnit.PX.format(px));
    }

    public Builder setLeftPct(double pct) {
      return putLast("left", Format.percentageOrZero(pct));
    }

    public Builder setOutline(int widthPx, Color color,
        OutlineStyleValue style) {
      return putLast("outline", join(widthPx, color, style));
    }

    public Builder setOutlineWidthPx(int px) {
      return putLast("outline-width", LengthUnit.PX.formatPositive(px));
    }

    public Builder setOutlineColor(Color color) {
      return putLast("outline-color", color.toCssCode());
    }

    public Builder setColor(Color color) {
      return putLast("color", color.toCssCode());
    }

    public Builder setMarginPx(int px) {
      return putLast("margin", LengthUnit.PX.format(px));
    }

    public Builder setMarginPx(int vPx, int hPx) {
      return putLast("margin", joinPx(vPx, hPx));
    }

    public Builder setMarginPx(int topPx, int rightPx, int bottomPx,
        int leftPx) {
      return putLast("margin", joinPx(topPx, rightPx, bottomPx, leftPx));
    }

    public Builder setMarginTopPct(double pct) {
      return putLast("margin-top", Format.percentageOrZero(pct));
    }

    public Builder setMarginTopPx(int px) {
      return putLast("margin-top", LengthUnit.PX.format(px));
    }

    public Builder setMarginRightPct(double pct) {
      return putLast("margin-right", Format.percentageOrZero(pct));
    }

    public Builder setMarginRightPx(int px) {
      return putLast("margin-right", LengthUnit.PX.format(px));
    }

    public Builder setMarginBottomPct(double pct) {
      return putLast("margin-bottom", Format.percentageOrZero(pct));
    }

    public Builder setMarginBottomPx(int px) {
      return putLast("margin-bottom", LengthUnit.PX.format(px));
    }

    public Builder setMarginLeftPct(double pct) {
      return putLast("margin-left", Format.percentageOrZero(pct));
    }

    public Builder setMarginLeftPx(int px) {
      return putLast("margin-left", LengthUnit.PX.format(px));
    }

    public Builder setPaddingPx(int px) {
      return putLast("padding", LengthUnit.PX.format(px));
    }

    public Builder setPaddingPx(int vPx, int hPx) {
      return putLast("padding", joinPx(vPx, hPx));
    }

    public Builder setPaddingPx(int topPx, int rightPx, int bottomPx,
        int leftPx) {
      return putLast("padding", joinPx(topPx, rightPx, bottomPx, leftPx));
    }

    public Builder setPaddingTopPx(int px) {
      return putLast("padding-top", LengthUnit.PX.formatPositive(px));
    }

    public Builder setPaddingTopPct(double pct) {
      return putLast("padding-top", Format.positivePercentageOrZero(pct));
    }

    public Builder setPaddingRightPx(int px) {
      return putLast("padding-right", LengthUnit.PX.formatPositive(px));
    }

    public Builder setPaddingRightPct(double pct) {
      return putLast("padding-right", Format.positivePercentageOrZero(pct));
    }

    public Builder setPaddingBottomPx(int px) {
      return putLast("padding-bottom", LengthUnit.PX.formatPositive(px));
    }

    public Builder setPaddingBottomPct(double pct) {
      return putLast("padding-bottom", Format.positivePercentageOrZero(pct));
    }

    public Builder setPaddingLeftPx(int px) {
      return putLast("padding-left", LengthUnit.PX.formatPositive(px));
    }

    public Builder setPaddingLeftPct(double pct) {
      return putLast("padding-left", Format.positivePercentageOrZero(pct));
    }

    public Builder setBorderWidthPx(int px) {
      return putLast("border-width", LengthUnit.PX.formatPositive(px));
    }

    public Builder setBorderWidthPx(int vPx, int hPx) {
      return putLast("border-width", joinPxPositive(vPx, hPx));
    }

    public Builder setBorderWidthPx(int topPx, int rightPx, int bottomPx,
        int leftPx) {
      return putLast("border-width",
          joinPxPositive(topPx, rightPx, bottomPx, leftPx));
    }

    public Builder setBorderTopWidthPx(int px) {
      return putLast("border-top-width", LengthUnit.PX.formatPositive(px));
    }

    public Builder setBorderRightWidthPx(int px) {
      return putLast("border-right-width", LengthUnit.PX.formatPositive(px));
    }

    public Builder setBorderBottomWidthPx(int px) {
      return putLast("border-bottom-width", LengthUnit.PX.formatPositive(px));
    }

    public Builder setBorderLeftWidthPx(int px) {
      return putLast("border-left-width", LengthUnit.PX.formatPositive(px));
    }

    public Builder setBorderColor(Color color) {
      return putLast("border-color", color.toCssCode());
    }

    public Builder setBorderColor(Color vColor, Color hColor) {
      return putLast("border-color", join(vColor, hColor));
    }

    public Builder setBorderColor(Color topColor, Color rightColor,
        Color bottomColor, Color leftColor) {
      return putLast("border-color",
          join(topColor, rightColor, bottomColor, leftColor));
    }

    public Builder setBorderTopColor(Color color) {
      return putLast("border-top-color", color.toCssCode());
    }

    public Builder setBorderRightColor(Color color) {
      return putLast("border-right-color", color.toCssCode());
    }

    public Builder setBorderBottomColor(Color color) {
      return putLast("border-bottom-color", color.toCssCode());
    }

    public Builder setBorderLeftColor(Color color) {
      return putLast("border-left-color", color.toCssCode());
    }

    public Builder set(BorderStyleValue value) {
      return putLast("border-style", value.toString());
    }

    public Builder set(BorderStyleValue vValue, BorderStyleValue hValue) {
      return putLast("border-style",
          join(vValue.toString(), hValue.toString()));
    }

    public Builder set(BorderStyleValue topValue, BorderStyleValue rightValue,
        BorderStyleValue bottomValue, BorderStyleValue leftValue) {
      return putLast("border-style", join(topValue.toString(),
          rightValue.toString(), bottomValue.toString(), leftValue.toString()));
    }

    public Builder setBorderTopStyle(BorderStyleValue value) {
      return putLast("border-top-style", value.toString());
    }

    public Builder setBorderRightStyle(BorderStyleValue value) {
      return putLast("border-right-style", value.toString());
    }

    public Builder setBorderBottomStyle(BorderStyleValue value) {
      return putLast("border-bottom-style", value.toString());
    }

    public Builder setBorderLeftStyle(BorderStyleValue value) {
      return putLast("border-left-style", value.toString());
    }

    public Builder setBorderTop(int widthPx, Color color,
        BorderStyleValue style) {
      return putLast("border-top", join(widthPx, color, style));
    }

    public Builder setBorderRight(int widthPx, Color color,
        BorderStyleValue style) {
      return putLast("border-right", join(widthPx, color, style));
    }

    public Builder setBorderBottom(int widthPx, Color color,
        BorderStyleValue style) {
      return putLast("border-bottom", join(widthPx, color, style));
    }

    public Builder setBorderLeft(int widthPx, Color color,
        BorderStyleValue style) {
      return putLast("border-left", join(widthPx, color, style));
    }

    public Builder setBorder(int widthPx, Color color, BorderStyleValue style) {
      return putLast("border", join(widthPx, color, style));
    }

    public Builder setBorderRadiusPx(int px) {
      String str = LengthUnit.PX.formatPositive(px);
      return putLastWithMozWebkitPrefixes("border-radius", str);
    }

    public Builder setBorderTopRightRadiusPx(int px) {
      String str = LengthUnit.PX.formatPositive(px);
      return putLast("-webkit-border-top-right-radius", str)
          .putLast("-moz-border-radius-topright", str)
          .putLast("border-top-right-radius", str);
    }

    public Builder setBorderBottomRightRadiusPx(int px) {
      String str = LengthUnit.PX.formatPositive(px);
      return putLast("-webkit-border-bottom-right-radius", str)
          .putLast("-moz-border-radius-bottomright", str)
          .putLast("border-bottom-right-radius", str);
    }

    public Builder setBorderBottomLeftRadiusPx(int px) {
      String str = LengthUnit.PX.formatPositive(px);
      return putLast("-webkit-border-bottom-left-radius", str)
          .putLast("-moz-border-radius-bottomleft", str)
          .putLast("border-bottom-left-radius", str);
    }

    public Builder setBorderTopLeftRadiusPx(int px) {
      String str = LengthUnit.PX.formatPositive(px);
      return putLast("-webkit-border-top-left-radius", str)
          .putLast("-moz-border-radius-topleft", str)
          .putLast("border-top-left-radius", str);
    }

    public Builder setBorderSpacingPx(int px) {
      return putLast("border-spacing", LengthUnit.PX.formatPositive(px));
    }

    public Builder setFontFamily(FontFamilyFallback fallback, String... names) {
      List<String> quotedNames = Lists.newArrayListWithCapacity(
          names.length + 1);
      quotedNames.add(fallback.toString());
      for (String name : names) {
        quotedNames.add(Format.escapeAndQuote(name));
      }
      return setFontFamily(quotedNames);
    }

    public Builder setFontFamily(String... names) {
      List<String> quotedNames = Lists.newArrayListWithCapacity(names.length);
      for (String name : names) {
        quotedNames.add(Format.escapeAndQuote(name));
      }
      return setFontFamily(quotedNames);
    }

    private Builder setFontFamily(Iterable<String> quotedNames) {
      return putLast("font-family", COMMA_JOINER.join(quotedNames));
    }

    public Builder setFontSizePx(int px) {
      return putLast("font-size", LengthUnit.PX.formatPositive(px));
    }

    public Builder setFontSizeEm(double em) {
      return putLast("font-size", LengthUnit.EM.formatPositive(em));
    }

    public Builder setLetterSpacingPx(int px) {
      return putLast("letter-spacing", LengthUnit.PX.format(px));
    }

    public Builder setLetterSpacingEm(double em) {
      return putLast("letter-spacing", LengthUnit.EM.format(em));
    }

    public Builder setWordSpacingPx(int px) {
      return putLast("word-spacing", LengthUnit.PX.format(px));
    }

    public Builder setWordSpacingEm(double em) {
      return putLast("word-spacing", LengthUnit.EM.format(em));
    }

    public Builder setLineHeightPx(int px) {
      return putLast("line-height", LengthUnit.PX.formatPositive(px));
    }

    public Builder setLineHeightEm(double em) {
      return putLast("line-height", LengthUnit.EM.formatPositive(em));
    }

    public Builder setLineHeightPct(double pct) {
      return putLast("line-height", Format.positivePercentageOrZero(pct));
    }

    public Builder setTextIndentPx(int px) {
      return putLast("text-indent", LengthUnit.PX.format(px));
    }

    public Builder setTextIndentEm(double em) {
      return putLast("text-indent", LengthUnit.EM.format(em));
    }

    public Builder setTextShadow(Shadow... shadows) {
      String value;
      if (shadows.length == 0) {
        value = "none";
      } else if (shadows.length == 1) {
        value = shadows[0].toStringForTextShadow();
      } else {
        List<String> strs = Lists.newArrayListWithCapacity(shadows.length);
        for (Shadow shadow : shadows) {
          strs.add(shadow.toStringForTextShadow());
        }
        value = COMMA_JOINER.join(strs);
      }
      return putLast("text-shadow", value);
    }

    public Builder setBoxShadow(Shadow... shadows) {
      String value;
      if (shadows.length == 0) {
        value = "none";
      } else if (shadows.length == 1) {
        value = shadows[0].toString();
      } else {
        // ImmutableList.of to check for nulls
        value = COMMA_JOINER.join(ImmutableList.copyOf(shadows));
      }
      return putLastWithMozWebkitPrefixes("box-shadow", value);
    }

    public Builder setOpacity(double opacity) {
      checkArgument(0 <= opacity && opacity <= 1,
          "opacity (%s) must be in [0, 1]", opacity);
      // http://css-tricks.com/css-transparency-settings-for-all-broswers/
      // Thanks!
      return putLast("zoom", "1")
          .putLast("filter", "alpha(opacity=" +
              Format.number(100 * opacity) + ")")
          .putLast("opacity", Format.number(opacity));
    }

    public Builder setTransform(TransformFunction... functions) {
      String value;
      if (functions.length == 0) {
        value = "none";
      } else if (functions.length == 1) {
        value = functions[0].toString();
      } else {
        // ImmutableList.of to check for nulls
        value = COMMA_JOINER.join(
            Iterables.transform(
                ImmutableList.of(functions),
                Functions.toStringFunction()));
      }
      return putLast("-moz-transform", value)
          .putLast("-webkit-transform", value)
          .putLast("-o-transform", value)
          .putLast("transform", value);
    }

    public Builder setTransformOriginPx(int xPx, int yPx) {
      return setTransformOrigin(
          LengthUnit.PX.format(xPx), LengthUnit.PX.format(yPx));
    }

    public Builder setTransformOriginPxPct(int xPx, double yPct) {
      return setTransformOrigin(
          LengthUnit.PX.format(xPx), Format.percentageOrZero(yPct));
    }

    public Builder setTransformOriginPctPx(double xPct, int yPx) {
      return setTransformOrigin(
          Format.percentageOrZero(xPct), LengthUnit.PX.format(yPx));
    }

    public Builder setTransformOriginPct(double xPct, double yPct) {
      return setTransformOrigin(
          Format.percentageOrZero(xPct), Format.percentageOrZero(yPct));
    }

    private Builder setTransformOrigin(String x, String y) {
      return putLast("transform-origin", x + " " + y);
    }

    @PoorBrowserSupport
    public Builder setColumnCount(int columnCount) {
      checkArgument(0 < columnCount,
          "column count (%s) must be > 0", columnCount);
      return putLastWithMozWebkitPrefixes("column-count", columnCount + "");
    }

    @PoorBrowserSupport
    public Builder setColumnGapPx(int px) {
      return putLastWithMozWebkitPrefixes("column-gap",
          LengthUnit.PX.formatPositive(px));
    }

    @PoorBrowserSupport
    public Builder setColumnRule(int widthPx, Color color,
        BorderStyleValue style) {
      return putLastWithMozWebkitPrefixes("column-rule",
          join(widthPx, color, style));
    }

    @PoorBrowserSupport
    public Builder setColumnRuleWidthPx(int px) {
      return putLastWithMozWebkitPrefixes("column-rule-width",
          LengthUnit.PX.formatPositive(px));
    }

    @PoorBrowserSupport
    public Builder setColumnRuleColor(Color color) {
      return putLastWithMozWebkitPrefixes("column-rule-color",
          color.toCssCode());
    }

    @PoorBrowserSupport
    public Builder setColumnRuleStyle(BorderStyleValue style) {
      return putLastWithMozWebkitPrefixes("column-rule-style",
          style.toString());
    }

    @PoorBrowserSupport
    public Builder setColumnWidthPx(int px) {
      return putLastWithMozWebkitPrefixes("column-width",
          LengthUnit.PX.formatPositive(px));
    }

    @PoorBrowserSupport
    public Builder setTransition(Transition... singles) {
      String value = singles.length == 0
          ? "none" : COMMA_JOINER.join(singles);
      return putLast("-moz-transition", value)
          .putLast("-webkit-transition", value)
          .putLast("-o-transition", value)
          .putLast("transition", value);
    }

    @PoorBrowserSupport
    public Builder setAnimation(Animation... singles) {
      String value = singles.length == 0
          ? "none" : COMMA_JOINER.join(singles);
      return putLastWithMozWebkitPrefixes(value, value);
    }

    @PoorBrowserSupport
    public Builder setAnimationTimingFunction(TimingFunction function) {
      // Left for keyframes
      return putLastWithMozWebkitPrefixes("timing-function",
          function.toString());
    }

    @PoorBrowserSupport
    public Builder setAnimationPlayState(boolean running) {
      return putLastWithMozWebkitPrefixes("animation-play-state",
          running ? "running" : "paused");
    }

    public Builder setZIndex(int zIndex) {
      return putLast("z-index", zIndex + "");
    }

    public Builder set(BorderCollapseValue value) {
      return putLast("border-collapse", value.toString());
    }

    public Builder set(CaptionSideValue value) {
      return putLast("caption-side", value.toString());
    }

    public Builder set(ClearValue value) {
      return putLast("clear", value.toString());
    }

    public Builder set(CursorValue value) {
      return putLast("cursor", value.toString());
    }

    public Builder set(DirectionValue value) {
      return putLast("direction", value.toString());
    }

    public Builder set(DisplayValue value) {
      return putLast("display", value.toString());
    }

    public Builder set(EmptyCellsValue value) {
      return putLast("empty-cells", value.toString());
    }

    public Builder set(FloatValue value) {
      return putLast("float", value.toString());
    }

    public Builder set(FontStyleValue value) {
      return putLast("font-style", value.toString());
    }

    public Builder set(FontVariantValue value) {
      return putLast("font-variant", value.toString());
    }

    public Builder set(FontWeightValue value) {
      return putLast("font-weight", value.toString());
    }

    public Builder set(ListStylePositionValue value) {
      return putLast("list-style-position", value.toString());
    }

    public Builder set(ListStyleTypeValue value) {
      return putLast("list-style-type", value.toString());
    }

    public Builder set(OutlineStyleValue value) {
      return putLast("outline-style", value.toString());
    }

    public Builder set(OverflowValue value) {
      return putLast("overflow", value.toString());
    }

    public Builder set(PositionValue value) {
      return putLast("position", value.toString());
    }

    public Builder set(VerticalAlignValue value) {
      return putLast("vertical-align", value.toString());
    }

    public Builder set(TableLayoutValue value) {
      return putLast("table-layout", value.toString());
    }

    public Builder set(TextAlignValue value) {
      return putLast("text-align", value.toString());
    }

    public Builder set(TextDecorationValue value) {
      return putLast("text-decoration", value.toString());
    }

    public Builder set(TextTransformValue value) {
      return putLast("text-transform", value.toString());
    }

    public Builder set(UnicodeBidiValue value) {
      return putLast("unicode-bidi", value.toString());
    }

    public Builder set(VisibilityValue value) {
      return putLast("visibility", value.toString());
    }

    public Builder set(WhiteSpaceValue value) {
      return putLast("white-space", value.toString());
    }

    private static String join(String v, String h) {
      if (v.equals(h)) {
        return v.toString();
      }
      return v + " " + h;
    }

    private static String join(String top, String right, String bottom,
        String left) {
      if (top.equals(bottom) && right.equals(left)) {
        return join(top, right);
      }
      return top + " " + right + " " + bottom + " " + left;
    }

    private static String joinPx(int vPx, int hPx) {
      if (vPx == hPx) {
        return LengthUnit.PX.format(vPx);
      }
      return LengthUnit.PX.format(vPx) + " " + LengthUnit.PX.format(hPx);
    }

    private static String joinPx(int topPx, int rightPx, int bottomPx,
        int leftPx) {
      if (topPx == bottomPx && rightPx == leftPx) {
        return joinPx(topPx, rightPx);
      }
      return LengthUnit.PX.format(topPx) + " " +
          LengthUnit.PX.format(rightPx) + " " +
          LengthUnit.PX.format(bottomPx) + " " +
          LengthUnit.PX.format(leftPx);
    }

    private static String joinPxPositive(int vPx, int hPx) {
      if (vPx == hPx) {
        return LengthUnit.PX.formatPositive(vPx);
      }
      return LengthUnit.PX.formatPositive(vPx) + " " +
          LengthUnit.PX.formatPositive(hPx);
    }

    private static String joinPxPositive(int topPx, int rightPx, int bottomPx,
        int leftPx) {
      if (topPx == bottomPx && rightPx == leftPx) {
        return joinPxPositive(topPx, rightPx);
      }
      return LengthUnit.PX.formatPositive(topPx) + " " +
          LengthUnit.PX.formatPositive(rightPx) + " " +
          LengthUnit.PX.formatPositive(bottomPx) + " " +
          LengthUnit.PX.formatPositive(leftPx);
    }

    private static String join(Color vColor, Color hColor) {
      if (vColor.equals(hColor)) {
        return vColor.toCssCode();
      }
      return vColor.toCssCode() + " " + hColor.toCssCode();
    }

    private static String join(Color topColor, Color rightColor,
        Color bottomColor, Color leftColor) {
      if (topColor.equals(bottomColor) && rightColor.equals(leftColor)) {
        return join(topColor, rightColor);
      }
      return topColor.toCssCode() + " " +
          rightColor.toCssCode() + " " +
          bottomColor.toCssCode() + " " +
          leftColor.toCssCode();
    }

    private static String join(int widthPx, Color color, Enum<?> style) {
      return LengthUnit.PX.formatPositive(widthPx) + " " +
          color.toCssCode() + " " + checkNotNull(style);
    }

    /** Returns a newly-created property. */
    @Override public Properties build() {
      return new Properties(this);
    }

    private Builder putLastWithMozWebkitPrefixes(String name, String value) {
      return putLast("-moz-" + name, value)
          .putLast("-webkit-" + name, value)
          .putLast(name, value);
    }

    // Because LinkedHashMap#put does not change the iteration order when an
    // entry is replaced.
    private Builder putLast(String name, String value) {
      if (properties.put(name, value) != null) {
        properties.remove(name);
        properties.put(name, value);
      }
      return this;
    }

    private void putAllLast(Map<String, String> input) {
      for (Entry<String, String> entry : input.entrySet()) {
        putLast(entry.getKey(), entry.getValue());
      }
    }
  }
}
