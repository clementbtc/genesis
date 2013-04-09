package com.projetloki.genesis;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;

/**
 * The font-face rule allows authors to specify online fonts to display text on
 * their web pages. By allowing authors to provide their own fonts, font-face
 * eliminates the need to depend on the limited number of fonts users have
 * installed on their computers.
 *
 * <p>
 * Example:
 * <pre><code> static final FontFace MY_GENTIUM = FontFace.forLocalSource("myGentium")
 *     .sourceAdded("../Gentium.ttf")
 *     .withFamilyName("myGentium");
 *
 * static final CssModule MY_MODULE = new CssModule() {
 *   &#064;Override public void configure(CssBuilder out) {
 *     // Add the font-face to the CSS
 *     out.use(myGentium);
 *
 *     // Apply the newly-defined font to all elements of type p
 *     out.addRule("p", "font-family: myGentium");
 *   }
 * };
 * </code></pre>
 * </p>
 *
 * <p>
 * In order to exist in the generated CSS, every font-face must be registered
 * with {@link CssBuilder#use(FontFace)}.</p>
 *
 * <p>
 * Font-faces are immutable. Methods such as {@link #sourceAdded(String)}
 * perform a copy of the instance they are called on, modify the copy and return
 * it. The original instance is left unchanged.</p>
 *
 * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/@font-face">MDN</a>
 * @see <a href="http://caniuse.com/fontface">Browser support</a>
 *
 * @author Clément Roux
 */
public final class FontFace extends AppendableToNoContext {
  private static final Joiner COMMA_JOINER = Joiner.on(',');

  /**
   * Returns a new font-face that contains a single source.
   * The browser will download the font at the given URI.
   *
   * <p>If you know it, it is recommended that you
   * {@linkplain #forSource(String) specify} the format of the font file.</p>
   *
   * @throws IllegalArgumentException if the given string is not a valid URI
   */
  public static FontFace forSource(String sourceUri) {
    return new FontFace().sourceAdded(sourceUri);
  }

  /**
   * Returns a new font-face that contains a single source.
   * The browser will download the font at the given URI.
   *
   * @throws IllegalArgumentException if the given string is not a valid URI
   */
  public static FontFace forSource(String sourceUri, FontFormat format) {
    return new FontFace().sourceAdded(sourceUri, format);
  }

  /** Returns a new font-face that contains a single local source. */
  public static FontFace forLocalSource(String localName) {
    return new FontFace().localSourceAdded(localName);
  }

  private ImmutableList<String> sources;
  private String style;
  private String variant;
  private String weight;
  // null if the name has not been specified explicitly
  private String explicitNameOrNull;
  // IE compatibility trick
  private String firstEotSourceOrNull;
  // the name if none has been specified explicitly
  private volatile String autoName;

  private FontFace() {
    sources = ImmutableList.of();
  }

  private FontFace(FontFace copyFrom) {
    sources = copyFrom.sources;
    style = copyFrom.style;
    variant = copyFrom.variant;
    weight = copyFrom.weight;
    explicitNameOrNull = copyFrom.explicitNameOrNull;
    firstEotSourceOrNull = copyFrom.firstEotSourceOrNull;
    // Don't copy autoName as it is deduced from other fields
  }

  /**
   * Returns a new font-face that is a copy of this instance, with the given
   * source URI added. The browser will download the font at the given URI.
   * If the first source is not available, the browser will check the second
   * source, and so on.
   *
   * <p>If you know it, it is recommended that you
   * {@linkplain #sourceAdded(String, FontFormat) specify} the format of the
   * font file.</p>
   *
   * @throws IllegalArgumentException if the given string is not a valid URI
   */
  public FontFace sourceAdded(String sourceUri) {
    return sourceAddedImpl(sourceUri, null);
  }

  /**
   * Returns a new font-face that is a copy of this instance, with the given
   * source URI added. The browser will download the font at the given URI.
   * If the first source is not available, the browser will check the second
   * source, and so on.
   */
  public FontFace sourceAdded(String sourceUri, FontFormat format) {
    return sourceAddedImpl(sourceUri, checkNotNull(format));
  }

  private FontFace sourceAddedImpl(String sourceUri,
      @Nullable FontFormat format) {
    Util.checkUri(sourceUri);
    StringBuilder builder = new StringBuilder()
        .append("url('")
        .append(Format.escape(sourceUri))
        .append("')");
    if (format != null) {
      builder.append(" format('");
      builder.append(format);
      builder.append("')");
    }
    String uri = builder.toString();
    FontFace result = new FontFace(this);
    result.sources = ImmutableList.<String>builder()
        .addAll(result.sources)
        .add(uri)
        .build();
    if (format == FontFormat.EMBEDDED_OPENTYPE &&
        result.firstEotSourceOrNull == null) {
      result.firstEotSourceOrNull = uri;
    }
    return result;
  }

  /**
   * Returns a new font-face that is a copy of this instance, with the given
   * local source added.
   * When authors would prefer to use a locally available copy of a given font
   * and download it if it's not, this method can be used.
   * @param localName the name of the local font
   */
  public FontFace localSourceAdded(String localName) {
    String source = "local('" + Format.escape(localName) + "')";
    FontFace result = new FontFace(this);
    result.sources = ImmutableList.<String>builder()
        .addAll(result.sources)
        .add(source)
        .build();
    return result;
  }

  /**
   * Returns a new font-face that is a copy of this instance, and that specifies
   * italic style.
   */
  public FontFace italic() {
    FontFace result = new FontFace(this);
    result.style = "italic";
    return result;
  }

  /**
   * Returns a new font-face that is a copy of this instance, and that specifies
   * italic style.
   */
  public FontFace oblique() {
    FontFace result = new FontFace(this);
    result.style = "oblique";
    return result;
  }

  /**
   * Returns a new font-face that is a copy of this instance, and that specifies
   * small-caps variant.
   */
  public FontFace smallCaps() {
    FontFace result = new FontFace(this);
    result.variant = "small-caps";
    return result;
  }

  /**
   * Returns a new font-face that is a copy of this instance, and that specifies
   * bold weight.
   */
  public FontFace bold() {
    FontFace result = new FontFace(this);
    result.weight = "bold";
    return result;
  }

  // Encoding for generating a family name from the hash when none is specified
  private static final BaseEncoding ENCODING =
      BaseEncoding.base32().omitPadding();

  /**
   * Returns the family name of this font-face. This is the string you must pass
   * to {@link Properties.Builder#setFontFamily(FontFamilyFallback, String...)}.
   *
   * <p>
   * If you haven't explicitly specified a family name with
   * {@link #withFamilyName(String)}, one will be generated automatically.</p>
   */
  public String familyName() {
    String result = explicitNameOrNull;
    if (result == null) {
      result = autoName;
      if (result == null) {
        // Compute a hash of this object
        Hasher hasher = Hashing.sha1().newHasher()
            .putString(Strings.nullToEmpty(style))
            .putString(Strings.nullToEmpty(variant))
            .putString(Strings.nullToEmpty(weight));
        for (String source : sources) {
          hasher.putString(source);
        }
        byte[] bytes = hasher.hash().asBytes();
        int len = Math.min(bytes.length, 8);
        autoName = result = "ff-" +
            ENCODING.encode(bytes, 0, len).toLowerCase();
      }
    }
    return result;
  }

  /**
   * Returns a new font-face that is a copy of this instance, and with the given
   * family name.
   * If you don't specify a family name, one will be generated automatically.
   * Call this method if you want to refer to the family name outside of the
   * CSS, e.g. in HTML or Javascript.
   */
  public FontFace withFamilyName(String name) {
    if (name.equals(explicitNameOrNull)) {
      return this;
    }
    FontFace result = new FontFace(this);
    result.explicitNameOrNull = name;
    return result;
  }

  @Override public boolean equals(Object object) {
    if (object instanceof FontFace) {
      FontFace that = (FontFace) object;
      return sources.equals(that.sources) &&
          Objects.equal(style, that.style) &&
          Objects.equal(variant, that.variant) &&
          Objects.equal(weight, that.weight) &&
          Objects.equal(explicitNameOrNull, that.explicitNameOrNull);
    }
    return false;
  }

  @Override public int hashCode() {
    return Objects.hashCode(sources, style, variant, weight,
        explicitNameOrNull);
  }

  @Override void appendTo(StringBuilder out) {
    out.append("@font-face{font-family:'");
    out.append(Format.escape(familyName()));
    out.append('\'');
    // IE compatibility trick
    if (firstEotSourceOrNull != null) {
      out.append(";src:");
      out.append(firstEotSourceOrNull);
    }
    out.append(";src:");
    // IE compatibility trick: add local('?') before the sources
    // http://paulirish.com/2009/bulletproof-font-face-implementation-syntax/
    if (firstEotSourceOrNull != null && !sources.get(0).startsWith("l")) {
      out.append("local('?'),");
    }
    COMMA_JOINER.appendTo(out, sources);
    if (style != null) {
      out.append(";font-style:");
      out.append(style);
    }
    if (variant != null) {
      out.append(";font-variant:");
      out.append(variant);
    }
    if (weight != null) {
      out.append(";font-weight:");
      out.append(weight);
    }
    out.append('}');
  }
}
