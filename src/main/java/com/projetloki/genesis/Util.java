package com.projetloki.genesis;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;

/**
 * Various static utility methods.
 *
 * @author Clément Roux
 */
final class Util {
  /**
   * Pattern for matching CSS identifiers.
   * May be a little more tolerant than the spec.
   */
  // See http://www.w3.org/TR/CSS21/grammar.html#scanner
  static final Pattern ID_PATTERN = Pattern.compile(
      "-?([_a-zA-Z]|[^\\p{ASCII}])([_a-zA-Z0-9-]|[^\\p{ASCII}])*");

  /** Pattern for matching CSS numbers (integers and reals). */
  static final Pattern NUMBER_PATTERN =
      Pattern.compile("-?([0-9]*\\.[0-9]+|[0-9]+)");

  /** Pattern for matching CSS color codes, right after #. */
  static final Pattern COLOR_CODE_PATTERN =
      Pattern.compile("[0-9a-fA-F]{3}([0-9a-fA-F]{3})?");

  // Copied verbatim from RFC 3986 Appendix B.
  // See http://www.w3.org/TR/2004/REC-xml11-20040204/#sec-white-space
  private static final Pattern URI_PATTERN = Pattern.compile(
      "^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?");

  /** Pattern for matching CSS whitespaces. */
  static final CharMatcher WHITESPACE = CharMatcher.anyOf(" \t\r\n\f");

  private static final Joiner SPACE_JOINER = Joiner.on(' ');

  /** Pattern for matching escape sequences within CSS string literals. */
  private static final Pattern ESCAPE_SEQUENCE =
      Pattern.compile("[0-9A-Fa-f]{1,6}");

  /**
   * Returns the CSS code for calling the function named {@code functionName}
   * with the given arguments.
   */
  static String functionalNotation(String functionName, String... args) {
    int length = functionName.length() + 2 + Math.max(0, args.length - 1);
    for (String arg : args) {
      length += arg.length();
    }
    StringBuilder builder = new StringBuilder(length);
    builder.append(functionName);
    builder.append('(');
    SPACE_JOINER.appendTo(builder, args);
    builder.append(')');
    return builder.toString();
  }

  /**
   * Returns whether the given string is a dimension (e.g. 20px, 0 but not 20)
   * or a percentage.
   */
  static boolean isDimensionOrPercentage(String input) {
    Matcher matcher = NUMBER_PATTERN.matcher(input);
    if (!matcher.lookingAt()) {
      return false;
    }
    int end = matcher.end();
    String suffix = input.substring(end);
    return (suffix.isEmpty() && input.matches("-?0+")) ||
        suffix.equals("%") ||
        ID_PATTERN.matcher(suffix).matches();
  }

  /**
   * Checks that the given string is a valid URI.
   * @throws IllegalArgumentException otherwise
   */
  static String checkUri(String uri) {
    checkArgument(URI_PATTERN.matcher(uri).matches(),
        "not a valid uri: %s", uri);
    return uri;
  }

  /**
   * Checks that the given string is a valid CSS identifier.
   * @throws IllegalArgumentException otherwise
   */
  static String checkIdentifier(String identifier) {
    checkArgument(ID_PATTERN.matcher(identifier).matches(),
        "invalid identifier: %s, must match: %s",
        identifier, ID_PATTERN.pattern());
    return identifier;
  }

  /**
   * Reads a CSS string literal from input. When this method returns, the next
   * character is the character after the closing quote.
   * Checks that the next character is a quote.
   * @return the string literal (NOT the string value), including quotes
   * @throws IllegalArgumentException if the next character is not a quote;
   *     or the string literal is malformed
   */
  static String readStringLiteralCheckQuote(ParserInput input) {
    input.checkNotEmpty("quote");
    char quote = input.charAt(0);
    if (quote != '\'' && quote != '"') {
      throw input.throwExpectedException("quote expected");
    }
    return readStringLiteral(input);
  }

  /**
   * Reads a CSS string literal from input. When this method returns, the next
   * character is the character after the closing quote.
   * The next character can be a quote or an open parenthesis.
   * @return the string literal (NOT the string value), including quotes
   * @throws IllegalArgumentException if the string literal is malformed
   */
  static String readStringLiteral(ParserInput input) {
    input.checkNotEmpty("quote");
    int oldPosition = input.position();
    String quote = Character.toString(input.charAt(0));
    input.move(1);
    readStringLiteral(input, quote);
    return input.past(oldPosition);
  }

  /**
   * Reads a CSS string literal from input. When this method returns, the next
   * character is the character after the closing quote.
   * Unlike other methods with the same name, the next character is expected to
   * be the character that follows the opening quote.
   * @return the string literal (NOT the string value), including quotes
   * @throws IllegalArgumentException if the string literal is malformed
   */
  static void readStringLiteral(ParserInput input, String endQuote) {
    while (true) {
      input.checkNotEmpty(endQuote);
      if (input.startsWithThenMove(endQuote)) {
        break;
      } else if (input.startsWithThenMove("\\")) {
        if (input.startsWithThenMove("\r\n")) {
          continue;
        }
        input.checkNotEmpty("character");
        String escapeSequenceOrNull = input.tryRead(ESCAPE_SEQUENCE);
        if (escapeSequenceOrNull != null) {
          // Skip the whitespace if less than 6 digits
          if (escapeSequenceOrNull.length() < 6) {
            if (input.startsWithThenMove("\r\n")) {
              // Do nothing, already done
            } else if (!input.isEmpty()) {
              char firstChar = input.charAt(0);
              if (WHITESPACE.matches(firstChar)) {
                input.move(1);
              }
            }
          }
        } else {
          // Move to the next character
          input.move(1);
        }
      } else {
        input.move(1);
      }
    }
  }

  // To prevent instantiation
  private Util() {}
}
