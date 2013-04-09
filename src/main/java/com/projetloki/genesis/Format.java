package com.projetloki.genesis;

import static com.google.common.base.Preconditions.checkArgument;

import java.text.DecimalFormat;

/**
 * Static utility methods for converting Java values to CSS terms.
 *
 * @author Clément Roux
 */
final class Format {
  private static final DecimalFormat FORMAT = new DecimalFormat("#.####");

  static String number(double x) {
    return formatOrZero(x, "");
  }

  static String percentage(double percentage) {
    return number(percentage) + "%";
  }

  static String percentageOrZero(double percentage) {
    return formatOrZero(percentage, "%");
  }

  static String positivePercentageOrZero(double percentage) {
    checkArgument(percentage >= 0d, "negative percentage: %s", percentage);
    return percentageOrZero(percentage);
  }

  static String formatSeconds(double seconds) {
    checkArgument(0 <= seconds, "negative duration: %s", seconds);
    return formatOrZero(seconds, "0");
  }

  static String formatDeg(double deg) {
    return formatOrZero(deg, "deg");
  }

  static String formatOrZero(int value, String unit) {
    if (value == 0) {
      return "0";
    }
    return value + unit;
  }

  static String formatOrZero(double value, String unit) {
    checkArgument(!Double.isNaN(value), "NaN");
    checkArgument(!Double.isInfinite(value), "infinite");
    String formatted = FORMAT.format(value);
    if (formatted.equals("0") || formatted.equals("-0")) {
      return "0";
    }
    return formatted + unit;
  }

  static String escapeAndQuote(String input) {
    return "'" + escape(input) + "'";
  }

  static String escape(String input) {
    int charsToAdd = 0;
    for (int i = 0; i < input.length(); i++) {
      char c = input.charAt(i);
      switch (c) {
        case '\r':
        case '\n':
        case '\f': {
          charsToAdd += 2;
          break;
        }
        case '\'':
        case '"':
          ++charsToAdd;
      }
    }
    if (charsToAdd == 0) {
      return input;
    }
    StringBuilder builder = new StringBuilder(input.length() + charsToAdd);
    for (int i = 0; i < input.length(); i++) {
      char c = input.charAt(i);
      switch (c) {
        case '\r': {
          builder.append("\\D ");
          break;
        }
        case '\n': {
          builder.append("\\A ");
          break;
        }
        case '\f': {
          builder.append("\\C ");
          break;
        }
        case '\'':
        case '"': {
          builder.append('\\');
          // no break
        }
        default: {
          builder.append(c);
        }
      }
    }
    return builder.toString();
  }

  // To prevent instantiation
  private Format() {};
}
