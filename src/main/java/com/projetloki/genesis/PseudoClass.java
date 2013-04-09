package com.projetloki.genesis;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Pattern;

import com.google.common.base.Ascii;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * A pseudo-class specifies a special state of the element to be selected.
 * For example :hover will apply a style when the user hovers over the element
 * specified by the selector.
 *
 * @see Selector#on(PseudoClass)
 * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/Pseudo-classes">MDN</a>
 *
 * @author Clément Roux
 */
public final class PseudoClass extends SimpleStringWrapper {
  /**
   * Selects links inside elements. This will select any link, even those
   * already styled using selector with other link-related pseudo-classes like
   * :hover, :active or :visited.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/:link">MDN</a>
   */
  public static final PseudoClass LINK = new PseudoClass("link");
  /**
   * Select only links that have been visited.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/:visited">MDN</a>
   */
  public static final PseudoClass VISITED = new PseudoClass("visited");
  /**
   * Matches when an element is being activated by the user. It allows the page
   * to give a feedback that the activation has been detected by the browser.
   * When interacting with a mouse, this is typically the time between the user
   * presses the mouse button and releases it. It is often used on &lt;a&gt; and
   * &lt;button&gt; HTML elements.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/:active">MDN</a>
   */
  public static final PseudoClass ACTIVE = new PseudoClass("active");
  /**
   * Matches when the user designates an element with a pointing device, but
   * does not necessarily activate it.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/:hover">MDN</a>
   */
  public static final PseudoClass HOVER = new PseudoClass("hover");
  /**
   * Applied when a element has received focus, either from the user selecting
   * it with the use of a keyboard or by activating with the mouse.
   * Example: a form input.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/:focus">MDN</a>
   */
  public static final PseudoClass FOCUS = new PseudoClass("focus");
  /**
   * Represents any element that is the first child element of its parent.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/:first-child">MDN</a>
   */
  public static final PseudoClass FIRST_CHILD = new PseudoClass("first-child");
  /**
   * Represents any element that is the last child element of its parent.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/:last-child">MDN</a>
   * @see <a href="http://caniuse.com/css-sel3">Browser support</a>
   */
  @PoorBrowserSupport
  public static final PseudoClass LAST_CHILD = new PseudoClass("last-child");
  /**
   * Represents the first sibling of the given type in the list of children of
   * its parent element.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/:first-of-type">MDN</a>
   * @see <a href="http://caniuse.com/css-sel3">Browser support</a>
   */
  @PoorBrowserSupport
  public static final PseudoClass FIRST_OF_TYPE =
      new PseudoClass("first-of-type");
  /**
   * Represents the last sibling of the given type in the list of children of
   * its parent element.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/:last-of-type">MDN</a>
   * @see <a href="http://caniuse.com/css-sel3">Browser support</a>
   */
  @PoorBrowserSupport
  public static final PseudoClass LAST_OF_TYPE =
      new PseudoClass("last-of-type");
  /**
   * Represents any element which is the only child of its parent.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/:only-child">MDN</a>
   * @see <a href="http://caniuse.com/css-sel3">Browser support</a>
   */
  @PoorBrowserSupport
  public static final PseudoClass ONLY_CHILD = new PseudoClass("only-child");
  /**
   * Represents any element that has no siblings of the given type.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/:only-of-type">MDN</a>
   * @see <a href="http://caniuse.com/css-sel3">Browser support</a>
   */
  @PoorBrowserSupport
  public static final PseudoClass ONLY_OF_TYPE =
      new PseudoClass("only-of-type");
  /**
   * Matches the root element of a tree representing the document.
   * Applied to HTML, :root represents the &lt;html&gt; element and is identical
   * to the type selector html.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/:root">MDN</a>
   * @see <a href="http://caniuse.com/css-sel3">Browser support</a>
   */
  @PoorBrowserSupport
  public static final PseudoClass ROOT = new PseudoClass("root");
  /**
   * Represents any element that has no children at all.
   * Only element nodes and text (including whitespace) are considered.
   * Comments or processing instructions do not affect whether an element is
   * considered empty or not.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/:empty">MDN</a>
   * @see <a href="http://caniuse.com/css-sel3">Browser support</a>
   */
  @PoorBrowserSupport
  public static final PseudoClass EMPTY = new PseudoClass("empty");
  /**
   * Represents the unique element, if any, with an id matching the fragment
   * identifier of the URI of the document.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/:target">MDN</a>
   * @see <a href="http://caniuse.com/css-sel3">Browser support</a>
   */
  @PoorBrowserSupport
  public static final PseudoClass TARGET = new PseudoClass("target");
  /**
   * Represents any enabled element. An element is enabled if it can be
   * activated (e.g. selected, clicked on or accept text input) or accept focus.
   * The element also has an disabled state, in which it can't be activated or
   * accept focus.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/:enabled">MDN</a>
   * @see <a href="http://caniuse.com/css-sel3">Browser support</a>
   */
  @PoorBrowserSupport
  public static final PseudoClass ENABLED = new PseudoClass("enabled");
  /**
   * Represents any disabled element. An element is disabled if it can't be
   * activated (e.g. selected, clicked on or accept text input) or accept focus.
   * The element also has an enabled state, in which it can be activated or
   * accept focus.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/:disabled">MDN</a>
   * @see <a href="http://caniuse.com/css-sel3">Browser support</a>
   */
  @PoorBrowserSupport
  public static final PseudoClass DISABLED = new PseudoClass("disabled");
  /**
   * Represents any radio (&lt;input type="radio"&gt;), checkbox (&lt;input
   * type="checkbox"&gt;) or option (&lt;option&gt; in a &lt;select&gt;) element
   * that is checked or toggled to an on state.
   * The user can change this state by clicking on the element, or selecting a
   * different value, in which case the :checked pseudo-class no longer applies
   * to this element, but will to the relevant one.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/:checked">MDN</a>
   * @see <a href="http://caniuse.com/css-sel3">Browser support</a>
   */
  @PoorBrowserSupport
  public static final PseudoClass CHECKED = new PseudoClass("checked");

  static final ImmutableMap<String, PseudoClass> STRING_TO_CONSTANT =
      ImmutableMap.<String, PseudoClass>builder()
          .put("link", LINK)
          .put("visited", VISITED)
          .put("active", ACTIVE)
          .put("hover", HOVER)
          .put("focus", FOCUS)
          .put("first-child", FIRST_CHILD)
          .put("last-child", LAST_CHILD)
          .put("first-of-type", FIRST_OF_TYPE)
          .put("last-of-type", LAST_OF_TYPE)
          .put("only-child", ONLY_CHILD)
          .put("only-of-type", ONLY_OF_TYPE)
          .put("root", ROOT)
          .put("empty", EMPTY)
          .put("target", TARGET)
          .put("enabled", ENABLED)
          .put("disabled", DISABLED)
          .put("checked", CHECKED)
          .build();

  // Names of pseudo-classes taking a an+b expression as a parameter
  static final ImmutableSet<String> NTHED_PSEUDO_CLASSES =
      ImmutableSet.of(
          "nth-child",
          "nth-last-child",
          "nth-of-type",
          "nth-last-of-type");

  // Every character but * and )
  static final Pattern EXPR_STRING_PATTERN = Pattern.compile(
      "([^\\*\\)])*");

  /**
   * Returns a pseudo-class that matches elements based on the language the
   * element is determined to be in. In HTML, the language is determined by a
   * combination of the lang attribute, the &lt;meta&gt; element, and possibly
   * by information from the protocol (such as HTTP headers).
   * There is a match if the language code of the element starts with
   * {@code lang} followed by a hyphen.
   *
   * <p>For example, if {@code lang} is {@link Locale#FRENCH}, the pseudo-class
   * will match elements whose language specification is fr, fr-CA, fr-FR, and
   * so on.</p>
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/:lang">MDN</a>
   */
  public static PseudoClass lang(Locale lang) {
    // A BCP47 language tag doesn't contains characters that need to be escaped
    // It should be lang.toLanguageTag(), but not compatible with Java 6
    // #toString() should be good enough in the context of CSS
    String string = "lang(" + lang.toString().replace('_', '-') + ")";
    return new PseudoClass(string);
  }

  /**
   * Returns a pseudo-class that matches an element that has exactly n - 1
   * siblings before it in the document tree, and has a parent element.
   * @throws IllegalArgument if n is &lt;= 0
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/:nth-child">MDN</a>
   * @see <a href="http://caniuse.com/css-sel3">Browser support</a>
   */
  @PoorBrowserSupport
  public static PseudoClass nthChild(int n) {
    checkArgument(n >= 0, "number (%s) must be >= 1", n);
    return nthChild(Integer.valueOf(n));
  }

  /**
   * Returns a pseudo-class that matches an element that has exactly an + b - 1
   * siblings before it in the document tree, for any n &gt;= 0, and has a
   * parent element.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/:nth-child">MDN</a>
   * @see <a href="http://caniuse.com/css-sel3">Browser support</a>
   */
  @PoorBrowserSupport
  public static PseudoClass nthChild(int a, int b) {
    return nthChild(new Expression(a, b));
  }

  static PseudoClass nthChild(Object expression) {
    return new PseudoClass("nth-child(" + expression + ")");
  }

  /**
   * Returns a pseudo-class that matches an element that has exactly n - 1
   * siblings after it in the document tree, and has a parent element.
   * @throws IllegalArgument if n is &lt;= 0
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/:nth-last-child">MDN</a>
   * @see <a href="http://caniuse.com/css-sel3">Browser support</a>
   */
  @PoorBrowserSupport
  public static PseudoClass nthLastChild(int n) {
    checkArgument(n >= 0, "number (%s) must be >= 1", n);
    return nthLastChild(Integer.valueOf(n));
  }

  /**
   * Returns a pseudo-class that matches an element that has exactly an + b - 1
   * siblings after it in the document tree, for any n &gt;= 0, and has a parent
   * element.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/:nth-last-child">MDN</a>
   * @see <a href="http://caniuse.com/css-sel3">Browser support</a>
   */
  @PoorBrowserSupport
  public static PseudoClass nthLastChild(int a, int b) {
    return nthLastChild(new Expression(a, b));
  }

  static PseudoClass nthLastChild(Object expr) {
    return new PseudoClass("nth-last-child(" + expr + ")");
  }

  /**
   * Returns a pseudo-class that matches an element that has exactly n - 1
   * siblings with the same element type before it in the document tree, and has
   * a parent element.
   * @throws IllegalArgument if n is &lt;= 0
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/:nth-of-type">MDN</a>
   * @see <a href="http://caniuse.com/css-sel3">Browser support</a>
   */
  @PoorBrowserSupport
  public static PseudoClass nthOfType(int n) {
    checkArgument(n >= 0, "number (%s) must be >= 1", n);
    return nthOfType(Integer.valueOf(n));
  }

  /**
   * Returns a pseudo-class that matches an element that has exactly an + b - 1
   * siblings with the same element type before it in the document tree, for any
   * n &gt;= 0, and has a parent element.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/:nth-of-type">MDN</a>
   * @see <a href="http://caniuse.com/css-sel3">Browser support</a>
   */
  @PoorBrowserSupport
  public static PseudoClass nthOfType(int a, int b) {
    return nthOfType(new Expression(a, b));
  }

  static PseudoClass nthOfType(Object expression) {
    return new PseudoClass("nth-of-type(" + expression + ")");
  }

  /**
   * Returns a pseudo-class that matches an element that has exactly n - 1
   * siblings with the same element type after it in the document tree, and has
   * a parent element.
   * @throws IllegalArgument if n is &lt;= 0
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/:nth-last-of-type">MDN</a>
   * @see <a href="http://caniuse.com/css-sel3">Browser support</a>
   */
  @PoorBrowserSupport
  public static PseudoClass nthLastOfType(int n) {
    checkArgument(n >= 0, "number (%s) must be >= 1", n);
    return nthLastOfType(Integer.valueOf(n));
  }

  /**
   * Returns a pseudo-class that matches an element that has exactly an + b - 1
   * siblings with the same element type after it in the document tree, for any
   * n &gt;= 0, and has a parent element.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/:nth-last-of-type">MDN</a>
   * @see <a href="http://caniuse.com/css-sel3">Browser support</a>
   */
  @PoorBrowserSupport
  public static PseudoClass nthLastOfType(int a, int b) {
    return nthLastOfType(new Expression(a, b));
  }

  static PseudoClass nthLastOfType(Object expression) {
    return new PseudoClass("nth-last-of-type(" + expression + ")");
  }

  /**
   * Returns a pseudo-class that matches elements that are not matched by the
   * given simple selector. Simple selectors are:
   * <ul>
   * <li>the universal selector</li>
   * <li>ID selectors</li>
   * <li>class selectors</li>
   * <li>attribute selectors</li>
   * <li>pseudo-class selectors</li>
   * </ul>
   * Pseudo-element selectors are NOT simple selectors.
   *
   * <p>Additionally, the given simple selector can't be the negation
   * pseudo-class itself.</p>
   * @throws IllegalArgumentException if the given selector is not a simple
   *     selector, or the given selector is the negation pseudo-class itself
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/:not">MDN</a>
   */
  public static PseudoClass not(Selector simpleSelector) {
    checkArgument(simpleSelector.isNegatable(),
        "illagal operand: %s", simpleSelector);
    return new PseudoClass("not(" + simpleSelector + ")", true);
  }

  /**
   * Returns a pseudo-class that matches elements that are not matched by the
   * given simple selector. Simple selectors are:
   * <ul>
   * <li>the universal selector</li>
   * <li>ID selectors</li>
   * <li>class selectors</li>
   * <li>attribute selectors</li>
   * <li>pseudo-class selectors</li>
   * </ul>
   * Pseudo-element selectors are NOT simple selectors.
   *
   * <p>Additionally, the given simple selector can't be the negation
   * pseudo-class itself.</p>
   * @throws IllegalArgumentException if the given selector is not a simple
   *     selector, or the given selector is the negation pseudo-class itself
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/:not">MDN</a>
   */
  public static PseudoClass not(String simpleSelector) {
    return not(Selector.from(simpleSelector));
  }

  static final Parser<PseudoClass> PARSER = new Parser<PseudoClass>() {
    @Override PseudoClass tryParse(ParserInput input) {
      String id = Ascii.toLowerCase(input.readId());
      PseudoClass constant = STRING_TO_CONSTANT.get(id);
      if (constant != null) {
        return constant;
      } else if (id.equals("not")) {
        input.checkStartsWithAndMove("(");
        Selector selector = Selector.PARSER.parse(input);
        input.skipAllSpacesAndComments();
        input.checkStartsWithAndMove(")");
        return not(selector);
      } else if (id.equals("lang")) {
        input.checkStartsWithAndMove("(");
        input.skipAllSpacesAndComments();
        String langTag = input.readId();
        input.skipAllSpacesAndComments();
        input.checkStartsWithAndMove(")");
        return new PseudoClass("lang(" + langTag + ")");
      }
      if (!NTHED_PSEUDO_CLASSES.contains(id)) {
        return null;
      }
      input.checkStartsWithAndMove("(");
      String exprString = input.read(EXPR_STRING_PATTERN, "expression");
      input.skipAllSpacesAndComments();
      input.checkStartsWithAndMove(")");
      Expression expr = Expression.from(exprString);
      if (id.equals("nth-child")) {
        return nthChild(expr);
      } else if (id.equals("nth-last-child")) {
        return nthLastChild(expr);
      } else if (id.equals("nth-of-type")) {
        return nthOfType(expr);
      } else if (id.equals("nth-last-of-type")) {
        return nthLastOfType(expr);
      }
      // Can't happen
      throw new AssertionError(id);
    }

    @Override String what() {
      return "pseudo-class";
    }
  };

  // Whether this pseudo-class is the negation pseudo-class
  private final boolean negation;

  PseudoClass(String css) {
    this(css, false);
  }

  private PseudoClass(String css, boolean negation) {
    super(css);
    this.negation = negation;
  }

  /** Returns whether this pseudo-class is the negation pseudo-class. */
  boolean isNegation() {
    return negation;
  }

  /** An aN+b expression. */
  private static class Expression {
    static final CharMatcher N = CharMatcher.anyOf("nN");
    static final Splitter N_SPLITTER = Splitter.on(N);
    static final CharMatcher DIGIT = CharMatcher.inRange('0', '9');

    static final Expression EVEN = new Expression(2, 0);
    static final Expression ODD = new Expression(2, 1);

    // See http://www.w3.org/TR/css3-selectors/#nth-child-pseudo
    // for the format specification
    static Expression from(String string) {
      String originalString = string;
      string = Util.WHITESPACE.trimFrom(string);
      checkArgument(!string.isEmpty(), "empty");
      if (string.equalsIgnoreCase("even")) {
        return EVEN;
      } else if (string.equalsIgnoreCase("odd")) {
        return ODD;
      }
      int nCount = N.countIn(string);
      checkArgument(nCount <= 1,
          "several occurrences of N: %s", originalString);
      if (nCount == 0) {
        // Just a number
        int bSign = 1;
        if (string.charAt(0) == '+') {
          string = string.substring(1);
        } else if (string.charAt(0) == '-') {
          bSign = -1;
          string = string.substring(1);
        }
        int b = parseInt(string);
        return new Expression(0, bSign * b);
      }
      // an+b
      // Absolute values of a and b
      int aAbs = 1;
      int bAbs = 0;
      // Signs of a and b, in {-1, 1}
      int aSign = 1;
      int bSign = 1;
      Iterator<String> parts = N_SPLITTER.split(string).iterator();
      String part0 = parts.next();
      String part1 = parts.next();
      if (part0.charAt(0) == '+') {
        part0 = part0.substring(1);
      } else if (part0.charAt(0) == '-') {
        aSign = -1;
        part0 = part0.substring(1);
      }
      if (!part0.isEmpty()) {
        aAbs = parseInt(part0);
      }
      if (!part1.isEmpty()) {
        part1 = Util.WHITESPACE.trimLeadingFrom(part1);
        if (part1.charAt(0) == '-') {
          bSign = -1;
        } else {
          checkArgument(part1.charAt(0) == '+',
              "operator + or - expected after N: %s", originalString);
        }
        part1 = Util.WHITESPACE.trimLeadingFrom(part1.substring(1));
        bAbs = Integer.valueOf(part1);
      }
      return new Expression(aAbs * aSign, bAbs * bSign);
    }

    static int parseInt(String string) {
      checkArgument(DIGIT.matchesAllOf(string), "not an integer: %s", string);
      return Integer.valueOf(string);
    }

    // an+b
    final int a;
    final int b;
    volatile String string;

    Expression(int a, int b) {
      this.a = a;
      this.b = b;
    }

    @Override public String toString() {
      String result = string;
      if (result == null) {
        if (a == 0) {
          // 0n+b can be simplified to b
          return Integer.toString(b);
        }
        StringBuilder builder = new StringBuilder();
        if (a != 1) {
          // 1n+b can be simplified to n+b
          builder.append(a);
        }
        builder.append('n');
        // an+0 can be simplified to an
        if (b != 0) {
          if (0 < b) {
            builder.append('+');
          }
          builder.append(b);
        }
        result = builder.toString();
        string = result;
      }
      return result;
    }
  }
}
