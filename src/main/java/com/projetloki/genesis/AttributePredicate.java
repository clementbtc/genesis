package com.projetloki.genesis;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.projetloki.genesis.Util.checkIdentifier;


/**
 * Matches elements on the basis of either the presence of an attribute, or the
 * match of an attribute value.
 *
 * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/Attribute_selectors">MDN</a>
 *
 * @author Clément Roux
 */
public final class AttributePredicate extends SimpleStringWrapper {
  /**
   * Returns a predicate that matches any element with the given attribute set,
   * regardless of the attribute value. The CSS notation is
   * <blockquote>[ <em>attributeName</em> ]</blockquote>
   * with no operator.
   *
   * <p>For example, the predicate [foo]
   * will match both elements {@code <p foo>} and {@code <p foo="bar">}.</p>
   * @throws IllegalArgumentException if the given attribute name is not a
   *     valid CSS identifier
   */
  public static AttributePredicate exists(String attributeName) {
    checkIdentifier(attributeName);
    return new AttributePredicate("[" + attributeName + "]");
  }

  /**
   * Returns a predicate that matches any element with the given attribute set
   * to the given value. The CSS notation is
   * <blockquote>[ <em>attributeName</em> = "<em>value</em>" ]</blockquote>
   *
   * <p>For example, the predicate [foo = "bar"] will match the element
   * {@code <p foo="bar">} but not the element {@code <p foo="hello">}</p>
   *
   * @param value the expected (unescaped) value
   * @throws IllegalArgumentException if the given attribute name is not a
   *     valid CSS identifier
   */
  public static AttributePredicate is(String attributeName, String value) {
    return operates(attributeName, "=", value, false);
  }

  /**
   * Returns a predicate that matches any element with the given attribute
   * set to a value containing the given word. The attribute value is expected
   * to be a whitespace-separated list of words. The CSS notation is
   * <blockquote>[ <em>attributeName</em> ~= "<em>word</em>" ]</blockquote>
   *
   * <p>For example, the predicate [foo ~= "bar"] will match the elements
   * {@code <p foo="bar">} and {@code <p foo="hello bar world">} but not the
   * element {@code <p foo="babar">}.</p>
   *
   * @param word the (unescaped) word whose presence in the value is to be
   *     tested
   * @throws IllegalArgumentException if the given attribute name is not a
   *     valid CSS identifier, or if the word contains a whitespace
   */
  public static AttributePredicate containsWord(String attributeName,
      String word) {
    checkArgument(Util.WHITESPACE.matchesNoneOf(word),
        "word must not contain any whitespace: %s", word);
    return operates(attributeName, "~=", word, false);
  }

  /**
   * Returns a predicate that matches any element with the given attribute set
   * to the given subtag, or to a value that starts with the subtag followed by
   * a hyphen. The CSS notation is
   * <blockquote>[ <em>attributeName</em> |= "<em>subtag</em>" ]</blockquote>
   *
   * <p>For example, the predicate [foo |= "bar"] will match the elements
   * {@code <p foo="bar">} and {@code <p foo="bar-hello">} but not the
   * element {@code <p foo="barhello">}.</p>
   * @param subtag the (unescaped) subtag
   * @throws IllegalArgumentException if the given attribute name is not a
   *     valid CSS identifier
   */
  public static AttributePredicate startsWithSubtag(String attributeName,
      String subtag) {
    return operates(attributeName, "|=", subtag, false);
  }

  /**
   * Returns a predicate that matches any element with the given attribute set
   * to a value starting with the given prefix. The CSS notation is
   * <blockquote>[ <em>attributeName</em> ^= "<em>prefix</em>" ]</blockquote>
   *
   * <p>For example, the predicate [foo ^= "bar"] will match the elements
   * {@code <p foo="bar">} and {@code <p foo="barhello">} but not the
   * element {@code <p foo="hello bar">}.</p>
   * @param prefix the (unescaped) prefix
   * @throws IllegalArgumentException if the given attribute name is not a
   *     valid CSS identifier
   */
  public static AttributePredicate startsWith(String attributeName,
      String prefix) {
    return operates(attributeName, "^=", prefix, false);
  }

  /**
   * Returns a predicate that matches any element with the given attribute set
   * to a value ending with the given suffix. The CSS notation is
   * <blockquote>[ <em>attributeName</em> $= "<em>suffix</em>" ]</blockquote>
   *
   * <p>For example, the predicate [foo $= "bar"] will match the elements
   * {@code <p foo="bar">} and {@code <p foo="hello-bar">} but not the
   * element {@code <p foo="bar hello">}.</p>
   * @param suffix the (unescaped) suffix
   * @throws IllegalArgumentException if the given attribute name is not a
   *     valid CSS identifier
   */
  public static AttributePredicate endsWith(String attributeName,
      String suffix) {
    return operates(attributeName, "$=", suffix, false);
  }

  /**
   * Returns a predicate that matches any element with the given attribute set
   * to a value containing the given string. The CSS notation is
   * <blockquote>[ <em>attributeName</em> *= "<em>string</em>" ]</blockquote>
   *
   * <p>For example, the predicate [foo *= "bar"] will match the elements
   * {@code <p foo="bar">} and {@code <p foo="hellobarworld">} but not the
   * element {@code <p foo="b-hello-a-world-r">}.</p>
   * @param string the (unescaped) string
   * @throws IllegalArgumentException if the given attribute name is not a
   *     valid CSS identifier
   */
  public static AttributePredicate contains(String attributeName,
      String string) {
    return operates(attributeName, "*=", string, false);
  }

  static AttributePredicate operates(String attributeName,
      String operator, String operand, boolean literal) {
    checkIdentifier(attributeName);
    // Strings need to be escaped
    String escaped = literal ? operand : Format.escapeAndQuote(operand);
    String string = "[" + attributeName + operator + escaped + "]";
    return new AttributePredicate(string);
  }

  static final Parser<AttributePredicate> PARSER =
      new Parser<AttributePredicate>() {
    @Override AttributePredicate tryParse(ParserInput input) {
      input.skipAllSpacesAndComments();
      String attributeName = input.readId();
      input.skipAllSpacesAndComments();
      if (input.startsWithThenMove("]")) {
        return exists(attributeName);
      }
      Operator op = null;
      for (Operator it : Operator.values()) {
        if (input.startsWithThenMove(it.operator)) {
          op = it;
          break;
        }
      }
      input.checkNotEmpty("]");
      input.skipAllSpacesAndComments();
      String operand;
      String idOrNull = input.tryReadId();
      if (idOrNull != null) {
        operand = Format.escapeAndQuote(idOrNull);
      } else {
        operand = Util.readStringLiteralCheckQuote(input);
      }
      input.skipAllSpacesAndComments();
      input.checkStartsWithAndMove("]");
      return operates(attributeName, op.operator, operand, true);
    }

    @Override String what() {
      return "attribute predicate";
    }
  };

  // To prevent instantiation outside of the package
  private AttributePredicate(String css) {
    super(css);
  }

  // For parsing
  private enum Operator {
    IS("="),
    CONTAINS_WORD("~="),
    STARTS_WITH_SUBTAG("|="),
    STARTS_WITH("^="),
    ENDS_WITH("$="),
    CONTAINS("*=");
    final String operator;

    private Operator(String operator) {
      this.operator = checkNotNull(operator);
    }
  }
}
