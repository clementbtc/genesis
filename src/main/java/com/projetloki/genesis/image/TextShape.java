package com.projetloki.genesis.image;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.PrimitiveSink;

/**
 * A shape whose interior corresponds to the rendering of a text. The shape is
 * positioned so as the top left corner of the bounding box enclosing the
 * rendered text is the point with coordinates (0, 0).
 *
 * <p>The rendered text is always a single line - end of line characters are
 * ignored. To render multiple lines of text, you must construct one text shape
 * per line.</p>
 *
 * @see FontCatalog
 * @author Clément Roux
 */
public final class TextShape extends RasterOnShrinkShape<TextShape> {
  private final String text;
  private final Font font;
  private final int fontSize;
  final GlyphVector vector;
  private final java.awt.Shape javaShape;
  private final int width;
  private final int height;
  final int offsetX;
  final int offsetY;
  private volatile ImmutableList<Glyph> glyphs;
  private volatile CenteredText centered;

  private static final FontRenderContext CONTEXT =
      new FontRenderContext(new AffineTransform(), false, false);

  /**
   * Constructs a {@code TextShape} with the given text, font and font size.
   * @param text the text to render
   * @param font the font to render the text with
   * @param fontSize the font size, in pixels. Must be positive.
   */
  // Public static factory method instead of public constructor so we have the
  // freedom to make this class abstract later
  // See Josh Blosh's Effective Java, item 1
  public static TextShape of(String text, Font font, int fontSize) {
    return new TextShape(text, font, fontSize);
  }

  private TextShape(String text, Font font, int fontSize) {
    checkNotNull(font);
    checkNotNull(text);
    checkArgument(fontSize >= 0, "fontSize: %s", fontSize);
    this.text = text;
    this.font = font;
    this.fontSize = fontSize;
    vector = font.javaFont()
        .deriveFont((float) fontSize)
        .createGlyphVector(CONTEXT, text);
    javaShape = vector.getOutline();
    Rectangle rect = javaShape.getBounds();
    width = rect.width;
    height = rect.height;
    offsetX = rect.x;
    offsetY = rect.y;
  }

  /** Returns the text to render. */
  public String text() {
    return text;
  }

  /** Returns the font to render the text with. */
  public Font font() {
    return font;
  }

  /** Returns the font size, in pixels. */
  public int fontSize() {
    return fontSize;
  }

  /**
   * Returns the y-coordinate of the
   * <a href="http://en.wikipedia.org/wiki/Baseline_(typography)">baseline</a>.
   * This value is measured from the top of the bounding box enclosing the
   * rendered text. As a consequence, the result of this method doesn't depend
   * only on the font and the font size, but also on the text itself. See the
   * example below.
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/pluo4a4sme.png"/></td>
   *   <td><img src="../../../../resources/genesis/3if6s6bfzy.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code baselineY = 32}</td>
   *   <td>{@code baselineY = 39}</td>
   *  </tr>
   * </table>
   * </p>
   *
   * <p>East Asian scripts have no baseline.</p>
   */
  public int baselineY() {
    return -offsetY;
  }

  /**
   * Returns the <a href="http://en.wikipedia.org/wiki/Glyph">glyphs</a> in this
   * text shape. In most cases there is a 1:1 mapping between glyphs and
   * characters of the text to render, but this is not guaranteed.
   *
   * <p><pre><code> Font font = FontCatalog.junction();
   * TextShape ts = TextShape.of("Bonsoir", font, 40);
   * int width = ts.width();
   * int height = ts.height();
   * Image redOIm = Images.canvas(width, height, Color.SCARLET_RED_3)
   *     .mask(ts.glyphs().get(4));
   * Image im = Images.canvas(width, height, Color.ALUMINIUM_4)
   *     .mask(ts)
   *     .pasteThat(redOIm)
   *     .frame(Color.ALUMINIUM_1, 10, 10, 10, 10);
   * Images.show(im);</code></pre>
   * <img src="../../../../resources/genesis/ouhoch4pd4.png"/></p>
   */
  public ImmutableList<Glyph> glyphs() {
    ImmutableList<Glyph> result = glyphs;
    if (result == null) {
      synchronized (this) {
        result = glyphs;
        if (result == null) {
          ImmutableList.Builder<Glyph> builder = ImmutableList.builder();
          for (int i = 0; i < vector.getNumGlyphs(); i++) {
            Glyph glyph = new Glyph(this, i);
            builder.add(glyph);
          }
          glyphs = result = builder.build();
        }
      }
    }
    return result;
  }

  /**
   * Returns a shaper that moves this shape to the center of the image to mask.
   * @see Image#mask(Shaper)
   */
  public Shaper centered() {
    CenteredText result = centered;
    if (result == null) {
      result = new CenteredText(this);
      centered = result;
    }
    return result;
  }

  /** Returns the width of the bounding box enclosing the rendered text. */
  @Override public int width() {
    return width;
  }

  /** Returns the height of the bounding box enclosing the rendered text. */
  @Override public int height() {
    return height;
  }

  /** {@inheritDoc} */
  @Override public boolean contains(Point p) {
    return javaShape.contains(p.x + offsetX, p.y + offsetY);
  }

  @Override Point position() {
    return Point.ORIGIN;
  }

  @Override void doHash(PrimitiveSink sink) {
    sink.putLong(-4619176327031370938L)
        .putString(text)
        .putBytes(font.hash().asBytes())
        .putInt(fontSize);
  }

  @Override public boolean doEquals(TextShape that) {
    return text.equals(that.text) && font.equals(that.font) &&
        fontSize == that.fontSize;
  }

  @Override public String toString() {
    return "TextShape(" + text + ", " + font + ", " + fontSize + ")";
  }

  /**
   * A single <a href="http://en.wikipedia.org/wiki/Glyph">glyph</a> in a text
   * shape. There are two different glyph objects representing the letter B in
   * the text shape "BOB". One is the translate of the other. The union of all
   * the glyphs in a text shape is the text shape itself.
   *
   * <p>See {@link TextShape#glyphs()} for an example.</p>
   */
  public static final class Glyph extends RasterOnShrinkShape<Glyph> {
    final TextShape parent;
    final int glyphIndex;
    final int charIndex;
    final int leftX;
    final int topY;
    final int glyphWidth;
    final int glyphHeight;
    final Point center;
    final java.awt.Shape glyphJavaShape;

    Glyph(TextShape parent, int glyphIndex) {
      this.parent = checkNotNull(parent);
      this.glyphIndex = glyphIndex;
      charIndex = parent.vector.getGlyphCharIndex(glyphIndex);
      glyphJavaShape = parent.vector.getGlyphOutline(glyphIndex);
      Rectangle rect = glyphJavaShape.getBounds();
      glyphWidth = rect.width;
      glyphHeight = rect.height;
      leftX = glyphJavaShape.getBounds().x - parent.offsetX;
      topY = glyphJavaShape.getBounds().y - parent.offsetY;
      center = new Point(leftX + glyphWidth / 2d, topY + glyphHeight / 2d);
    }

    /** Returns the text shape that contains this glyph. */
    public TextShape parent() {
      return parent;
    }

    /**
     * Returns the index of this glyph in the list returned by
     * {@link TextShape#glyphs()}. In most cases, this is also the character
     * index.
     */
    public int glyphIndex() {
      return glyphIndex;
    }

    /**
     * Returns the index of the character this glyph is associated to, in the
     * text string. In most cases, this is also the glyph index.
     */
    public int charIndex() {
      return charIndex;
    }

    /**
     * Returns the y-coordinate of the top side of the bounding box enclosing
     * this glyph.
     */
    public int topY() {
      return topY;
    }

    /**
     * Returns the x-coordinate of the right side of the bounding box enclosing
     * this glyph.
     */
    public int rightX() {
      return leftX + glyphWidth;
    }

    /**
     * Returns the y-coordinate of the bottom side of the bounding box enclosing
     * this glyph.
     */
    public int bottomY() {
      return topY + glyphHeight;
    }

    /**
     * Returns the x-coordinate of the left side of the bounding box enclosing
     * this glyph.
     */
    public int leftX() {
      return leftX;
    }

    /** Returns the center of the bounding box enclosing this glyph. */
    public Point center() {
      return center;
    }

    /**
     * Returns the width of the bounding box enclosing this glyph. This is same
     * as {@code rightX() - leftX()}
     */
    @Override public int width() {
      return glyphWidth;
    }

    /**
     * Returns the height of the bounding box enclosing this glyph. This is same
     * as {@code bottomY() - tpY()}
     */
    @Override public int height() {
      return glyphHeight;
    }

    /** @inheritDoc */
    @Override public boolean contains(Point p) {
      return glyphJavaShape.contains(
          p.x + parent.offsetX,
          p.y + parent.offsetY);
    }

    @Override Point position() {
      return new Point(leftX, topY);
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(-3393493957850496786L)
          .putBytes(parent.hash().asBytes())
          .putInt(glyphIndex);
    }

    @Override public boolean doEquals(Glyph that) {
      return parent.equals(that.parent) && glyphIndex == that.glyphIndex;
    }

    @Override public String toString() {
      return parent + ".glyphs().get(" + glyphIndex + ")";
    }
  }

  private static class CenteredText
      extends HashCachingShaper<CenteredText> {
    final TextShape parent;

    public CenteredText(TextShape parent) {
      this.parent = parent;
    }

    @Override public Shape getShape(int width, int height) {
      double dx = (width - parent.width()) / 2d;
      double dy = (height - parent.height()) / 2d;
      return parent.translate(dx, dy);
    }

    @Override void doHash(PrimitiveSink sink) {
      sink.putLong(5720218257232458327L)
          .putBytes(parent.hash().asBytes());
    }

    @Override public boolean doEquals(CenteredText that) {
      return parent.equals(that.parent);
    }

    @Override public String toString() {
      return parent + ".centered()";
    }
  }
}
