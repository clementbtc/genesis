package com.projetloki.genesis;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;

import com.google.common.base.Ascii;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;

/**
 * A background-position.
 * See
 * <a href="http://www.w3.org/TR/css3-background/#the-background-position">www.w3.org</a>.
 *
 * @author Clément Roux
 */
final class BackgroundPosition extends SimpleStringWrapper {
  private static final Joiner SPACE_JOINER = Joiner.on(' ');

  // See http://www.w3.org/TR/css3-background/#ltpositiongt
  static BackgroundPosition from(String css) {
    css = Ascii.toLowerCase(css);
    List<String> terms = ImmutableList.copyOf(
        Splitter.on(Util.WHITESPACE).omitEmptyStrings().split(css));
    checkArgument(!terms.isEmpty(), "empty");
    if (3 <= terms.size()) {
      return fromMoreTerms(terms);
    }
    String first = terms.get(0);
    boolean firstHKeyword = isHorizontalKeyword(first);
    boolean firstVKeyword = isVerticalKeyword(first);
    boolean keyword = firstHKeyword || firstVKeyword;
    checkArgument(Util.isDimensionOrPercentage(first) || keyword,
        "unexpected term: %s", first);
    if (terms.size() == 1) {
      // According to the CSS specification "3px" is equivalent to "3px center"
      // We find that very confusing, so we make it illegal
      checkArgument(firstHKeyword || firstVKeyword,
          "term expected after %s", first);
      return new BackgroundPosition(first);
    }
    String second = terms.get(1);
    if (firstVKeyword && !firstHKeyword) {
      // From the CSS specification:
      // Note that a pair of keywords can be reordered while a combination of
      // keyword and length or percentage cannot. So 'center left' is valid
      // while '50% left' is not.
      checkArgument(isHorizontalKeyword(second),
          "unexpected term: %s", second);
      String temp = first;
      first = second;
      second = temp;
    } else {
      checkArgument(
          Util.isDimensionOrPercentage(second) || isVerticalKeyword(second),
          "unexpected term: %s", second);
    }
    return create(first, second);
  }

  // From the CSS specification
  // If three or four values are given, then each <percentage> or<length>
  // represents an offset and must be preceded by a keyword, which specifies
  // from which edge the offset is given. For example,
  // ‘background-position: bottom 10px right 20px’ represents a ‘10px’ vertical
  // offset up from the bottom edge and a ‘20px’ horizontal offset leftward
  // from the right edge. If three values are given, the missing offset is
  // assumed to be zero.
  private static BackgroundPosition fromMoreTerms(List<String> terms) {
    PeekingIterator<String> iterator =
        Iterators.peekingIterator(terms.iterator());
    String first = iterator.next();
    boolean firstHKeyword = isHorizontalKeyword(first);
    boolean firstVertical = isVerticalKeyword(first);
    boolean firstCenter = firstHKeyword && firstVertical;
    checkArgument(firstHKeyword || firstVertical,
        "unexpected term: %s", first);
    // horizontalKeyword && verticalKeyword means center
    if (!firstCenter) {
      String dim = iterator.peek();
      if (Util.isDimensionOrPercentage(dim)) {
        iterator.next();
      }
    }
    String second = iterator.next();
    boolean secondHKeyword = isHorizontalKeyword(second);
    boolean secondVKeyword = isVerticalKeyword(second);
    boolean secondCenter = secondHKeyword && secondVKeyword;
    checkArgument(secondHKeyword || secondVKeyword,
        "unexpected term: %s", second);
    if (iterator.hasNext() && !secondCenter) {
      String dim = iterator.next();
      checkArgument(Util.isDimensionOrPercentage(dim),
          "unexpected term: %s", dim);
    }
    if (iterator.hasNext()) {
      String next = iterator.next();
      throw new IllegalArgumentException("unexpected term: " + next);
    }
    checkArgument(
        firstCenter || secondCenter || firstHKeyword == secondVKeyword,
        "unexpected term: %s", second);
    return new BackgroundPosition(SPACE_JOINER.join(terms));
  }

  private static final ImmutableSet<String> HORIZONTAL_KEYWORDS =
      ImmutableSet.of("left", "center", "right");

  private static final ImmutableSet<String> VERTICAL_KEYWORDS =
      ImmutableSet.of("top", "center", "bottom");

  private static boolean isHorizontalKeyword(String input) {
    return HORIZONTAL_KEYWORDS.contains(input);
  }

  private static boolean isVerticalKeyword(String input) {
    return VERTICAL_KEYWORDS.contains(input);
  }

  // We choose the shortest representation,
  // e.g. "right".length() < "100% 0".length()
  static final BackgroundPosition LEFT_TOP =
      new BackgroundPosition("0 0");
  static final BackgroundPosition LEFT = new BackgroundPosition("left");
  static final BackgroundPosition LEFT_BOTTOM =
      new BackgroundPosition("0 100%");
  static final BackgroundPosition TOP = new BackgroundPosition("top");
  static final BackgroundPosition CENTER =
      new BackgroundPosition("center");
  static final BackgroundPosition BOTTOM =
      new BackgroundPosition("bottom");
  static final BackgroundPosition RIGHT_TOP =
      new BackgroundPosition("100% 0");
  static final BackgroundPosition RIGHT =
      new BackgroundPosition("right");
  static final BackgroundPosition RIGHT_BOTTOM =
      new BackgroundPosition("100% 100%");

  static BackgroundPosition px(int left, int top) {
    return create(LengthUnit.PX.format(left), LengthUnit.PX.format(top));
  }

  static BackgroundPosition pxPct(int left, double top) {
    return create(LengthUnit.PX.format(left), Format.percentageOrZero(top));
  }

  static BackgroundPosition pctPx(double left, int top) {
    return create(Format.percentageOrZero(left), LengthUnit.PX.format(top));
  }

  static BackgroundPosition pct(double left, double top) {
    return create(Format.percentageOrZero(left), Format.percentageOrZero(top));
  }

  private static BackgroundPosition create(String left, String top) {
    return new BackgroundPosition(left + " " + top);
  }

  private BackgroundPosition(String css) {
    super(css);
  }
}
