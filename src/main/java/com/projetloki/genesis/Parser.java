package com.projetloki.genesis;

import javax.annotation.Nullable;

/**
 * A parser of CSS code.
 *
 * @param <T> the result of parsing CSS code with the parser
 *
 * @author Clément Roux
 */
abstract class Parser<T> {
  /**
   * Reads a T from the given input.
   * Some implementations may return null if no T could be read from the input.
   * Other implementations may fail systematically. This should be documented.
   */
  abstract @Nullable T tryParse(ParserInput input);

  /**
   * Returns a small string that indicates what this parser expects.
   * Example: "selector".
   * Used when generating error messages.
   */
  abstract String what();

  /**
   * Same as {@link #tryParse(ParserInput)}, but always fails instead of
   * returning null.
   */
  final T parse(ParserInput input) {
    T result = tryParse(input);
    if (result == null) {
      throw input.throwExpectedException(what());
    }
    return result;
  }

  /**
   * Reads a T from the given input, and expects the given input to contain
   * nothing else.
   */
  final T from(String string) {
    ParserInput input = new ParserInput(string);
    T result = parse(input);
    input.skipAllSpacesAndComments();
    input.checkEmpty();
    return result;
  }
}
