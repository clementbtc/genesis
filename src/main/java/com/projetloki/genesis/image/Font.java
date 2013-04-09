package com.projetloki.genesis.image;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.util.concurrent.ExecutionError;
import com.google.common.util.concurrent.UncheckedExecutionException;

/**
 * A font with unspecified size. The name font is used here as a synonym for
 * <a href="http://en.wikipedia.org/wiki/Typeface">typeface</a>.
 *
 * @see FontCatalog
 * @see TextShape#TextShape(String, Font, int)
 * @author Clément Roux
 */
public abstract class Font implements Hashable {
  /**
   * Loads a font from the truetype font file at the given URL. Accepted
   * extensions are TTF and OTF.
   * @param truetypeSource the URL to load the font from
   * @throws IOException if an error occurs during loading
   */
  public static Font load(URL truetypeSource) throws IOException {
    return load(truetypeSource.toString());
  }

  /**
   * Loads a font from the given truetype font file. Accepted extensions are TTF
   * and OTF.
   * @param truetypeSource the file to load the front from
   * @throws IOException if an error occurs during loading
   */
  public static Font load(File truetypeSource) throws IOException {
    return load(truetypeSource.toURI().toString());
  }

  /**
   * Same as {@link #load(File)}, but the source has a last modification date.
   * Fails with a runtime exception if the file has been modified.
   */
  private static Font load(String sourceUrl) throws IOException {
    try {
      return globalCache.get(sourceUrl);
    } catch (UncheckedExecutionException ex) {
      throw (RuntimeException) ex.getCause();
    } catch (ExecutionException ex) {
      throw (IOException) ex.getCause();
    } catch (ExecutionError error) {
      throw (Error) error.getCause();
    }
  }

  private static final LoadingCache<String, Font>
      globalCache = CacheBuilder.newBuilder()
          .weakValues()
          .build(new CacheLoader<String, Font>() {
            @Override public Font load(String source) throws IOException {
              return Font.doLoad(new URL(source));
            }
          });

  static Font doLoad(URL source) throws IOException {
    HashCode hash = Util.hash(source);
    InputStream is = source.openStream();
    try {
      java.awt.Font javaFont =
          java.awt.Font.createFont(java.awt.Font.PLAIN, is);
      return new PlainFont(javaFont, source, hash);
    } catch (FontFormatException ex) {
      throw new IOException("not a valid truetype font: " + source, ex);
    } finally {
      is.close();
    }
  }

  private final java.awt.Font javaFont;

  // To prevent instantiation outside of the file
  Font(java.awt.Font javaFont) {
    this.javaFont = checkNotNull(javaFont);
  }

  final java.awt.Font javaFont() {
    return javaFont;
  }

  /**
   * Returns a new font which is a derivative of this font with a bold weight.
   * Calling this method a second time has no effect.
   *
   * <p>The algorithm used to make the font bolder may not always produce an
   * esthetic result. For this reason, when a font comes with a native bold
   * variant, e.g. {@link FontCatalog#orbitron()} and
   * {@link FontCatalog#orbitronBold()}, you should use this native variant
   * instead.</p>
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/dtqgftxcna.png"/></td>
   *   <td><img src="../../../../resources/genesis/2ujkojef2a.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code font}</td>
   *   <td>{@code font.bold()}</td>
   *  </tr>
   * </table>
   * </p>
   */
  public abstract Font bold();

  /**
   * Returns a new font which is a derivative of this font with the italic
   * style. Calling this method a second time has no effect.
   *
   * <p>The algorithm used to make the font italic may not always produce an
   * esthetic result. For this reason, when a font comes with a native italic
   * variant, e.g. {@link FontCatalog#fanwood()} and
   * {@link FontCatalog#fanwoodItalic()}, you should use this native variant
   * instead.</p>
   *
   * <p>
   * <table cellspacing="20" style="text-align:center">
   *  <tr>
   *   <td><img src="../../../../resources/genesis/hyeib732xq.png"/></td>
   *   <td><img src="../../../../resources/genesis/cjfdmfl674.png"/></td>
   *  </tr>
   *  <tr>
   *   <td>{@code font}</td>
   *   <td>{@code font.italic()}</td>
   *  </tr>
   * </table>
   * </p>
   */
  public abstract Font italic();

  private static class PlainFont extends Font {
    final LoadingCache<Integer, DerivedFont> derivedFonts =
        CacheBuilder.newBuilder()
            .weakValues()
            .build(new CacheLoader<Integer, DerivedFont>() {
              @Override public DerivedFont load(Integer style) {
                java.awt.Font javaFont = javaFont().deriveFont(style);
                return new DerivedFont(javaFont, PlainFont.this, style);
              }
            });

    final URL source;
    final HashCode hash;

    PlainFont(java.awt.Font javaFont, URL source, HashCode hash) {
      super(javaFont);
      this.source = checkNotNull(source);
      this.hash = checkNotNull(hash);
    }

    @Override public final Font bold() {
      return derivedFonts.getUnchecked(java.awt.Font.BOLD);
    }

    @Override public final Font italic() {
      return derivedFonts.getUnchecked(java.awt.Font.ITALIC);
    }

    @Override public HashCode hash() {
      return hash;
    }

    @Override public int hashCode() {
      return hash.hashCode();
    }

    @Override public String toString() {
      return "load(" + source + ")";
    }
  }

  private static class DerivedFont extends Font {
    final PlainFont plainFont;
    final int style;
    final HashCode hash;

    DerivedFont(java.awt.Font javaFont, PlainFont plainFont, int style) {
      super(javaFont);
      this.plainFont = plainFont;
      this.style = style;
      hash = Hashing.murmur3_128().newHasher()
          .putLong(-5781801031753008056L)
          .putBytes(plainFont.hash().asBytes())
          .putInt(style)
          .hash();

    }

    @Override public Font bold() {
      int newStyle = style | java.awt.Font.BOLD;
      return plainFont.derivedFonts.getUnchecked(newStyle);
    }

    @Override public Font italic() {
      int newStyle = style | java.awt.Font.ITALIC;
      return plainFont.derivedFonts.getUnchecked(newStyle);
    }

    @Override public HashCode hash() {
      return hash;
    }

    @Override public int hashCode() {
      return hash.hashCode();
    }

    @Override public String toString() {
      boolean bold = (style & java.awt.Font.BOLD) != 0;
      boolean italic = (style & java.awt.Font.ITALIC) != 0;
      StringBuilder builder = new StringBuilder();
      builder.append(plainFont);
      if (bold) {
        builder.append(".bold()");
      }
      if (italic) {
        builder.append(".italic()");
      }
      return builder.toString();
    }
  }
}
