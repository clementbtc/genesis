package com.projetloki.genesis;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import com.google.common.base.Ascii;
import com.google.common.base.CharMatcher;

/**
 * A wrapper around a piece of CSS code (unmodifiable), with a modifiable field
 * representing the current position.
 * Passed to {@link Parser#parse(ParserInput)}.
 *
 * Several methods have the same signature as methods of String, e.g.
 * {@link #charAt(int)} and {@link #startsWith(String)}. These methods apply to
 * the substring starting at the current position.
 *
 * Mutable and thread-unsafe.
 *
 * @author Clément Roux
 */
final class ParserInput {
  private static final CharMatcher NOT_WHITESPACE = Util.WHITESPACE.negate();
  private final String string;
  private int position;

  ParserInput(String string) {
    this.string = checkNotNull(string);
  }

  /**
   * Returns the character at the given index,
   * starting from the current position.
   */
  char charAt(int index) {
    return string.charAt(position + index);
  }

  /** Returns true if there is no character left. */
  boolean isEmpty() {
    return position == string.length();
  }

  /**
   * Throws an {@code IllegalArgumentException} if there is no character left.
   * @param what a short string that indicates what was expected
   */
  void checkNotEmpty(String what) {
    String format = what.length() == 1 ?
        "character %s expected" : "%s expected";
    check(!isEmpty(), format, what);
  }

  /**
   * Throws an {@code IllegalArgumentException} if there is at least one
   * character left.
   */
  void checkEmpty() {
    check(isEmpty(), "unexpected");
  }

  /**
   * Returns whether the substring starting at the current position starts with
   * the given prefix.
   */
  boolean startsWith(String prefix) {
    if (remainingChars() < prefix.length()) {
      return false;
    }
    for (int i = 0; i < prefix.length(); i++) {
      if (string.charAt(position + i) != prefix.charAt(i)) {
        return false;
      }
    }
    return true;
  }

  /** Same as {@link #startsWith(String)} but case-insensitive. */
  boolean startsWithIgnoreCase(String prefix) {
    if (remainingChars() < prefix.length()) {
      return false;
    }
    for (int i = 0; i < prefix.length(); i++) {
      char c1 = string.charAt(position + i);
      char c2 = prefix.charAt(i);
      if (Ascii.toLowerCase(c1) != Ascii.toLowerCase(c2)) {
        return false;
      }
    }
    return true;
  }

  /**
   * If the substring starting at the current position starts with the given
   * prefix, move by {@code prefix.length()} characters and returns true.
   * Otherwise, returns false.
   */
  boolean startsWithThenMove(String prefix) {
    boolean result = startsWith(prefix);
    if (result) {
      move(prefix.length());
    }
    return result;
  }

  /** Same as {@link #startsWithThenMove(String)} but case-insensitive. */
  boolean startsWithIgnoreCaseThenMove(String prefix) {
    boolean result = startsWithIgnoreCase(prefix);
    if (result) {
      move(prefix.length());
    }
    return result;
  }

  /**
   * Same as {@link #startsWithThenMove(String)}, but fails instead of returning
   * false.
   */
  void checkStartsWithAndMove(String prefix) {
    String format = prefix.length() == 1 ?
        "character %s expected" : "%s expected";
    check(startsWithThenMove(prefix), format, prefix);
  }

  /**
   * Same as {@link #startsWithIgnoreCaseThenMove(String)}, but fails instead of
   * returning false.
   */
  void checkStartsWithIgnoreCaseAndMove(String prefix) {
    String format = prefix.length() == 1 ?
        "character %s expected" : "%s expected";
    check(startsWithIgnoreCaseThenMove(prefix), format, prefix);
  }

  /** Returns the current position. */
  int position() {
    return position;
  }

  /** Sets the current position. */
  void setPosition(int position) {
    this.position = position;
  }

  /**
   * Move by the given number of characters.
   * Same as {@code setPosition(position() + plus)}.
   */
  void move(int plus) {
    position += plus;
  }

  /**
   * Reads a CSS identifier starting from the current position.
   * If no CSS identifier could be read, fails.
   */
  String readId() {
    return read(Util.ID_PATTERN, "identifier");
  }

  /**
   * Reads a CSS identifier starting from the current position.
   * If no CSS identifier could be read, returns null.
   */
  @Nullable String tryReadId() {
    return tryRead(Util.ID_PATTERN);
  }

  /**
   * Reads a number, starting from the current position.
   * If no number could be read, fails.
   */
  String readNumber() {
    return read(Util.NUMBER_PATTERN, "number");
  }

  /**
   * Reads a number, starting from the current position.
   * If no number could be read, returns false.
   */
  @Nullable String tryReadNumber() {
    return tryRead(Util.NUMBER_PATTERN);
  }

  /**
   * Reads a string matching the given pattern, starting from the current
   * position.
   * If no string could be read, fails.
   * @param what a short string that indicates what was expected
   */
  String read(Pattern pattern, String what) {
    String result = tryRead(pattern);
    check(result != null, "%s expected", what);
    return result;
  }

  /**
   * Reads a string matching the given pattern, starting from the current
   * position.
   * If no string could be read, returns null.
   */
  @Nullable String tryRead(Pattern pattern) {
    Matcher matcher = pattern.matcher(string.substring(position));
    if (!matcher.lookingAt()) {
      return null;
    }
    String id = matcher.group();
    move(id.length());
    return id;
  }

  /**
   * Move past all white spaces starting from the current position.
   * @return true if the current position has changed
   */
  boolean skipSpaces() {
    int oldPosition = position;
    position = NOT_WHITESPACE.indexIn(string, oldPosition);
    if (position == -1) {
      position = string.length();
    }
    return position != oldPosition;
  }

  /**
   * Move past all comments starting from the current position.
   * @return true if the current position has changed
   */
  boolean skipComments() {
    int oldPosition = position;
    while (startsWith("/*")) {
      move(2);
      int newPosition = string.indexOf("*/", position);
      check(newPosition != -1, "unclosed comment");
      position = newPosition + 2;
    }
    return position != oldPosition;
  }

  /**
   * Move past all white spaces and comments,
   * starting from the current position.
   * @return true if the current position has changed
   */
  boolean skipAllSpacesAndComments() {
    boolean result = false;
    while (skipSpaces() || skipComments()) {
      result = true;
    }
    return result;
  }

  /** Returns the number of remaining characters. */
  private int remainingChars() {
    return string.length() - position;
  }

  /** Returns the string from old position to the current position. */
  String past(int oldPosition) {
    return string.substring(oldPosition, position);
  }

  IllegalArgumentException throwExpectedException(String what) {
    check(false, "%s expected", what);
    // Will never reach
    throw new AssertionError();
  }

  private void check(boolean condition, String format) {
    check(condition, format, null);
  }

  private void check(boolean condition, String format, @Nullable Object arg) {
    if (!condition) {
      StringBuilder builder = new StringBuilder();
      builder.append("error while parsing [");
      builder.append(string);
      builder.append("] at ");
      builder.append(position);
      builder.append(": ");
      if (arg == null) {
        builder.append(format);
      } else {
        builder.append(format.replace("%s", arg.toString()));
      }
      throw new IllegalArgumentException(builder.toString());
    }
  }
}
