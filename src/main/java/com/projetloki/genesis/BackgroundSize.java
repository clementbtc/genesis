package com.projetloki.genesis;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;

import com.google.common.base.Ascii;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

/**
 * A background-size.
 * See
 * <a href="http://www.w3.org/TR/css3-background/#the-background-size">www.w3.org</a>.
 *
 * @author Clément Roux
 */
final class BackgroundSize extends SimpleStringWrapper {
  static BackgroundSize from(String cssCode) {
    cssCode = Ascii.toLowerCase(cssCode);
    List<String> terms = ImmutableList.copyOf(
        Splitter.on(Util.WHITESPACE).omitEmptyStrings().split(cssCode));
    if (terms.size() == 1) {
      String term = terms.get(0);
      if (term.equals("auto")) {
        return AUTO;
      } if (term.equals("contain")) {
        return CONTAIN;
      } else if (term.equals("cover")) {
        return COVER;
      }
    }
    checkArgument(terms.size() == 2, "wrong number of terms: %s",
        terms.size());
    for (String term : terms) {
      checkArgument(term.equals("auto") || Util.isDimensionOrPercentage(term),
          "unexpected term: %s", term);
    }
    return create(terms.get(0), terms.get(1));
  }

  private BackgroundSize(String css) {
    super(css);
  }

  static final BackgroundSize AUTO = new BackgroundSize("auto");
  static final BackgroundSize CONTAIN = new BackgroundSize("contain");
  static final BackgroundSize COVER = new BackgroundSize("cover");

  static BackgroundSize px(int width, int height) {
    return create(LengthUnit.PX.format(width), LengthUnit.PX.format(height));
  }

  static BackgroundSize pxPct(int width, double height) {
    return create(LengthUnit.PX.format(width), Format.percentageOrZero(height));
  }

  static BackgroundSize pctPx(double width, int height) {
    return create(Format.percentageOrZero(width), LengthUnit.PX.format(height));
  }

  static BackgroundSize pct(double width, double height) {
    return create(Format.percentageOrZero(width),
        Format.percentageOrZero(height));
  }

  private static BackgroundSize create(String width, String height) {
    return new BackgroundSize(width + " " + height);
  }
}
