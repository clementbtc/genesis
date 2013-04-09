package com.projetloki.genesis;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.projetloki.genesis.Util.checkIdentifier;

import java.util.regex.Pattern;

import com.google.common.base.Objects;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * A CSS selector, to match a set of elements in an HTML or XML documents.
 * There are several matching criteria, such as the tag name, the id, the class
 * name, the  position in the tree, the presence of a pointing device on top of
 * the element. Elements matched by a selector are called the subjects of the
 * selector.
 *
 * <p>There are two ways to construct a selector. The first way is to specify
 * the CSS notation of the selector with the {@linkplain #from(String) from}
 * factory method. The second way is to filter the elements matched by the
 * universal selector with methods such as
 * {@linkplain #onTag(String) onTag} and {@linkplain #onId(String) onId}.</p>
 *
 * <p>The two selectors below are equal:
 * <blockquote><pre><code> Selector s1 = Selector.from("div.foo a");
 * Selector s2 = ANY.onTag("div").onClass("foo").asAncestor().onTag("a");
 * assertEquals(s1, s2);</code></pre></blockquote></p>
 *
 * <p>Selectors are immutable.</p>
 *
 * <p><strong>Note:</strong> Although this class is not final, it cannot be
 * subclassed as it has no public or protected constructors.</p>
 *
 * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/Getting_Started/Selectors">MDN</a>
 *
 * @author Clément Roux
 */
public abstract class Selector extends AppendableToNoContext {
  static final Parser<Selector> PARSER = new SelectorParser();

  /** The universal selector, matching any element. */
  public static final Selector ANY = UniversalSelector.INSTANCE;

  /**
   * Returns a selector instance that has the given CSS notation. The CSS
   * notation of a selector is the string that is used to represent the
   * selector in a CSS file.
   *
   * <p>Usage example:
   * <blockquote><pre><code> Selector s1 = Selector.from("div.foo a");
   * Selector s2 = ANY.onTag("div").onClass("foo").asAncestor().onTag("a");
   * assertEquals(s1, s2);</pre></code></blockquote></p>
   * @throws IllegalArgumentException if the given string is not the CSS
   *     notation of a selector
   */
  public static Selector from(String selectorString) {
    return PARSER.from(selectorString);
  }

  // To prevent instantiation outside of the package
  Selector() {}

  /**
   * Returns a selector that matches the descendants of {@code this} selector's
   * subjects. The CSS notation is
   * <blockquote>ancestor *</blockquote>
   * with a space separating the ancestor and the universal selector.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/Descendant_selectors">MDN</a>
   */
  public final Selector asAncestor() {
    return asAncestorOf(ANY);
  }

  /**
   * Returns a selector that matches the descendants of {@code this} selector's
   * subjects matched by the given selector. The CSS notation is
   * <blockquote>ancestor descendant</blockquote>
   * with a space separating the ancestor and the descendant.
   *
   * <p>This is same as passing the result of {@code Selector.from(descendant)}
   * to {@link #asAncestorOf(Selector)}.</p>
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/Descendant_selectors">MDN</a>
   * @throws IllegalArgumentException if the given string is not the CSS
   *     notation of a selector
   */
  public final Selector asAncestorOf(String descendant) {
    return asAncestorOf(Selector.from(descendant));
  }

  /**
   * Returns a selector that matches the descendants of {@code this} selector's
   * subjects matched by the given selector. The CSS notation is
   * <blockquote>ancestor descendant</blockquote>
   * with a space separating the ancestor and the descendant.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/Descendant_selectors">MDN</a>
   */
  public abstract Selector asAncestorOf(Selector descendant);

  /**
   * Returns a selector that matches the children of {@code this} selector's
   * subjects. It does NOT match the grand-children nor other descendants of
   * this selector. The CSS notation is
   * <blockquote>parent > *</blockquote>
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/Child_selectors">MDN</a>
   */
  public final Selector asParent() {
    return asParentOf(ANY);
  }

  /**
   * Returns a selector that matches the children of {@code this} selector's
   * subjects matched by the given selector. It does NOT match the
   * grand-children nor other descendants of this selector. The CSS notation is
   * <blockquote>parent > child</blockquote>
   *
   * <p>This is same as passing the result of {@code Selector.from(child)}
   * to {@link #asParentOf(Selector)}.</p>
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/Child_selectors">MDN</a>
   * @throws IllegalArgumentException if the given string is not the CSS
   *     notation of a selector
   */
  public final Selector asParentOf(String child) {
    return asParentOf(Selector.from(child));
  }

  /**
   * Returns a selector that matches the children of {@code this} selector's
   * subjects matched by the given selector. It does NOT match the
   * grand-children nor other descendants of this selector. The CSS notation is
   * <blockquote>parent > child</blockquote>
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/Child_selectors">MDN</a>
   */
  public abstract Selector asParentOf(Selector child);

  /**
   * Returns a selector that matches the siblings of {@code this} selector's
   * subjects. The CSS notation is
   * <blockquote>sibling - *</blockquote>
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/General_sibling_selectors">MDN</a>
   */
  public final Selector asSibling() {
    return asSiblingOf(ANY);
  }

  /**
   * Returns a selector that matches the siblings of {@code this} selector's
   * subjects matched by the given selector. The CSS notation is
   * <blockquote>sibling - selector</blockquote>
   *
   * <p>This is same as passing the result of {@code Selector.from(sibling)}
   * to {@link #asSiblingOf(Selector)}.</p>
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/General_sibling_selectors">MDN</a>
   * @throws IllegalArgumentException if the given string is not the CSS
   *     notation of a selector
   */
  public final Selector asSiblingOf(String sibling) {
    return asSiblingOf(Selector.from(sibling));
  }

  /**
   * Returns a selector that matches the siblings of {@code this} selector's
   * subjects matched by the given selector. The CSS notation is
   * <blockquote>sibling - selector</blockquote>
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/General_sibling_selectors">MDN</a>
   */
  public abstract Selector asSiblingOf(Selector sibling);

  /**
   * Returns a selector that matches the adjacent siblings of {@code this}
   * selector's subjects. The CSS notation is
   * <blockquote>sibling + *</blockquote>
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/Adjacent_sibling_selectors">MDN</a>
   */
  public final Selector asPrecedingSibling() {
    return asPrecedingSiblingOf(ANY);
  }

  /**
   * Returns a selector that matches the adjacent siblings of {@code this}
   * selector's subjects matched by the given selector. The CSS notation is
   * <blockquote>sibling + selector</blockquote>
   *
   * <p>This is same as passing the result of {@code Selector.from(nextSibling)}
   * to {@link #asPrecedingSiblingOf(Selector)}.</p>
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/Adjacent_sibling_selectors">MDN</a>
   * @throws IllegalArgumentException if the given string is not the CSS
   *     notation of a selector
   */
  public final Selector asPrecedingSiblingOf(String adjacentSibling) {
    return asPrecedingSiblingOf(Selector.from(adjacentSibling));
  }

  /**
   * Returns a selector that matches the adjacent siblings of {@code this}
   * selector's subjects matched by the given selector. The CSS notation is
   * <blockquote>sibling + selector</blockquote>
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/Adjacent_sibling_selectors">MDN</a>
   */
  public abstract Selector asPrecedingSiblingOf(Selector adjacentSibling);

  /**
   * Returns a selector matching elements matched {@code this} selector and
   * with the given tag name. For example, the selector
   * {@code ANY.onClass("foo").onTag("p")} matches all paragraphs with the
   * class name "foo", and is equivalent to
   * {@code ANY.onTag("p").onClass("foo")}.</p>
   *
   * <p>Another word for tag name is element type.</p>
   *
   * <p>Since elements can't have more than one tag name, it is not legal to
   * call this method on a selector already matching elements by tag name. For
   * instance,
   * <blockquote>{@code ANY.onTag("div").onTag("p")}</blockquote>
   * is illegal and throws a runtime exception, but the code below is legal
   * because the two element type selectors apply to different sets of elements
   * <blockquote>{@code ANY.onTag("div").asAncestor().onTag("p")}</blockquote>
   * </p>
   * @throws IllegalArgumentException if the given tag name is not a valid
   *     identifier
   * @throws IllegalStateException if this selector already matches elements by
   *     tag name
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/Type_selectors">MDN</a>
   */
  public abstract Selector onTag(String tagName);

  /**
   * Returns a selector matching elements matched by {@code this} selector and
   * with the given class name. The class HTML attribute assigns a
   * whitespace-separated list of class names to an element.
   *
   * <p>Class selectors can be combined. For example
   * {@code ANY.onClass("foo").onClass("bar")} matches all elements with both
   * class names "foo" and "bar".</p>
   * @throws IllegalArgumentException if the given class name is not a valid
   *     identifier
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/Class_selectors">MDN</a>
   */
  public abstract Selector onClass(String className);

  /**
   * Returns a selector matching elements matched by {@code this} selector and
   * with the given ID. Since ID attributes must have unique values, an ID
   * selector can never match more than one element in a document.
   * @throws IllegalArgumentException if the given ID is not a valid identifier
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/ID_selectors">MDN</a>
   */
  public abstract Selector onId(String id);

  /**
   * Returns a selector matching elements matched by {@code this} selector and
   * that satisfy the given attribute predicate.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/Attribute_selectors">MDN</a>
   */
  public abstract Selector on(AttributePredicate attributePredicate);

  /**
   * Returns a selector matching elements matched by {@code this} selector and
   * if the given pseudo-class applies.
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/Pseudo-classes">MDN</a>
   */
  public abstract Selector on(PseudoClass pseudoClass);

  /**
   * Returns a selector matching virtual elements within the elements matched
   * by {@code this} selector.
   *
   * <p>
   * Pseudo-elements may only be appended after the last simple selector of the
   * selector. It is forbidden to derive the selector returned by this method,
   * or an {@code IllegalStateException} is thrown.</p>
   * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/Pseudo-elements">MDN</a>
   */
  public abstract Selector on(PseudoElement pseudoElement);

  /** Whether this selector is the universal selector. */
  // Not final
  boolean isUniversal() {
    return false;
  }

  /** Whether this selector can be an operand of the negation pseudo-class. */
  // Not final
  boolean isNegatable() {
    return false;
  }

  // Not final, only overridden by UniversalSelector
  void appendToUnlessUniversal(StringBuilder out) {
    appendTo(out);
  }

  private static abstract class SimpleSelector extends Selector {
    SimpleSelector() {}

    @Override public final Selector asAncestorOf(Selector descendant) {
      return new CombinedSelector(this, " ", descendant);
    }

    @Override public final Selector asParentOf(Selector child) {
      return new CombinedSelector(this, ">", child);
    }

    @Override public final Selector asSiblingOf(Selector selector) {
      return new CombinedSelector(this, "-", selector);
    }

    @Override public final Selector asPrecedingSiblingOf(Selector child) {
      return new CombinedSelector(this, "+", child);
    }

    // Not final
    @Override public Selector onId(String id) {
      return new IdSelector(this, id, true);
    }

    // Not final
    @Override public Selector onClass(String className) {
      return new ClassSelector(this, className, true);
    }

    // Not final
    @Override public Selector on(AttributePredicate predicate) {
      return new AttributeSelector(this, predicate);
    }

    @Override public final Selector on(PseudoClass pseudoClass) {
      return new PseudoClassSelector(this, pseudoClass);
    }

    @Override public final Selector on(PseudoElement pseudoElement) {
      return new PseudoElementSelector(this, pseudoElement);
    }
  }

  private static class UniversalSelector extends SimpleSelector {
    static final UniversalSelector INSTANCE = new UniversalSelector();

    @Override public Selector onTag(String tagName) {
      return ElementTypeSelector.get(tagName);
    }

    @Override public boolean isUniversal() {
      return true;
    }

    @Override public boolean isNegatable() {
      return true;
    }

    @Override public void appendTo(StringBuilder out) {
      out.append("*");
    }

    @Override public String toString() {
      return "*";
    }

    @Override void appendToUnlessUniversal(StringBuilder out) {}
  }

  private static class ElementTypeSelector extends SimpleSelector {
    private static final LoadingCache<String, ElementTypeSelector>
        INSTANCE_CACHE = CacheBuilder.newBuilder().maximumSize(20).build(
            new CacheLoader<String, ElementTypeSelector>() {
              @Override public ElementTypeSelector load(String tagName) {
                return new ElementTypeSelector(tagName);
              }
            });

    private static final Pattern TAG_NAME_PATTERN =
        Pattern.compile("[A-Z_a-z0-9]+");

    static ElementTypeSelector get(String tagName) {
      return INSTANCE_CACHE.getUnchecked(tagName);
    }

    private final String tagName;

    // Use #get(String) instead
    ElementTypeSelector(String tagName) {
      checkArgument(TAG_NAME_PATTERN.matcher(tagName).matches(),
          "not a valid tag name: %s", tagName);
      this.tagName = tagName;
    }

    @Override public Selector onTag(String tagName) {
      throw new IllegalStateException(
          "elements already selected by tag name: " + this.tagName);
    }

    @Override public boolean isNegatable() {
      return true;
    }

    @Override public boolean equals(Object object) {
      if (object instanceof ElementTypeSelector) {
        ElementTypeSelector that = (ElementTypeSelector) object;
        return tagName.equals(that.tagName);
      }
      return false;
    }

    @Override public int hashCode() {
      return tagName.hashCode() + 32596075;
    }

    @Override public void appendTo(StringBuilder out) {
      out.append(tagName);
    }

    @Override public String toString() {
      return tagName;
    }
  }

  private static class IdSelector extends SimpleSelector {

    private final Selector base;
    private final String id;
    private final int hashCode;

    IdSelector(Selector base, String id, boolean toCheck) {
      if (toCheck) {
        checkIdentifier(id);
      }
      this.base = checkNotNull(base);
      this.id = checkNotNull(id);
      hashCode = Objects.hashCode(base, id) + 82385984;
    }

    @Override public Selector onTag(String tagName) {
      return new IdSelector(base.onTag(tagName), id, false);
    }

    @Override public Selector on(AttributePredicate predicate) {
      return new AttributeSelector(this, predicate);
    }

    @Override public boolean isNegatable() {
      return base.isNegatable();
    }

    @Override public boolean equals(Object object) {
      if (object instanceof IdSelector) {
        IdSelector that = (IdSelector) object;
        return base.equals(that.base) && id.equals(that.id);
      }
      return false;
    }

    @Override public int hashCode() {
      return hashCode;
    }

    @Override public void appendTo(StringBuilder out) {
      base.appendToUnlessUniversal(out);
      out.append('#');
      out.append(id);
    }
  }

  private static class ClassSelector extends SimpleSelector {

    private final Selector base;
    private final String className;
    private final int hashCode;

    ClassSelector(Selector base, String className, boolean toCheck) {
      if (toCheck) {
        checkIdentifier(className);
      }
      this.base = checkNotNull(base);
      this.className = checkNotNull(className);
      hashCode = Objects.hashCode(base, className) - 2103179;
    }

    @Override public Selector onTag(String tagName) {
      return new ClassSelector(base.onTag(tagName), className, false);
    }

    @Override public Selector onId(String id) {
      return new ClassSelector(base.onId(id), className, false);
    }

    @Override public Selector on(AttributePredicate predicate) {
      return new AttributeSelector(this, predicate);
    }

    @Override public boolean isNegatable() {
      return base.isNegatable();
    }

    @Override public boolean equals(Object object) {
      if (object instanceof ClassSelector) {
        ClassSelector that = (ClassSelector) object;
        return base.equals(that.base) && className.equals(that.className);
      }
      return false;
    }

    @Override public int hashCode() {
      return hashCode;
    }

    @Override public void appendTo(StringBuilder out) {
      base.appendToUnlessUniversal(out);
      out.append('.');
      out.append(className);
    }
  }

  private static class AttributeSelector extends SimpleSelector {
    private final Selector base;
    private final AttributePredicate predicate;
    private final int hashCode;

    AttributeSelector(Selector base, AttributePredicate predicate) {
      this.base = checkNotNull(base);
      this.predicate = checkNotNull(predicate);
      hashCode = Objects.hashCode(base, predicate) + 51896;
    }

    @Override public Selector onTag(String tagName) {
      return new AttributeSelector(base.onTag(tagName), predicate);
    }

    @Override public Selector onId(String id) {
      return new AttributeSelector(base.onId(id), predicate);
    }

    @Override public Selector onClass(String className) {
      return new AttributeSelector(base.onClass(className), predicate);
    }

    @Override public Selector on(AttributePredicate predicate) {
      return new AttributeSelector(this, predicate);
    }

    @Override public boolean isNegatable() {
      return base.isNegatable();
    }

    @Override public boolean equals(Object object) {
      if (object instanceof AttributeSelector) {
        AttributeSelector that = (AttributeSelector) object;
        return base.equals(that.base) && predicate.equals(that.predicate);
      }
      return false;
    }

    @Override public int hashCode() {
      return hashCode;
    }

    @Override public void appendTo(StringBuilder out) {
      base.appendToUnlessUniversal(out);
      out.append(predicate);
    }
  }

  private static class PseudoClassSelector extends SimpleSelector {
    private final Selector base;
    private final PseudoClass pseudoClass;
    private final int hashCode;

    PseudoClassSelector(Selector base, PseudoClass pseudoClass) {
      this.base = checkNotNull(base);
      this.pseudoClass = checkNotNull(pseudoClass);
      hashCode = Objects.hashCode(base, pseudoClass) - 6094353;
    }

    @Override public Selector onTag(String tagName) {
      return new PseudoClassSelector(base.onTag(tagName), pseudoClass);
    }

    @Override public Selector onId(String id) {
      return new PseudoClassSelector(base.onId(id), pseudoClass);
    }

    @Override public Selector onClass(String className) {
      return new PseudoClassSelector(base.onClass(className), pseudoClass);
    }

    @Override public Selector on(AttributePredicate predicate) {
      return new PseudoClassSelector(base.on(predicate), pseudoClass);
    }

    @Override public boolean isNegatable() {
      return base.isNegatable() && !pseudoClass.isNegation();
    }

    @Override public boolean equals(Object object) {
      if (object instanceof PseudoClassSelector) {
        PseudoClassSelector that = (PseudoClassSelector) object;
        return base.equals(that.base) && pseudoClass.equals(that.pseudoClass);
      }
      return false;
    }

    @Override public int hashCode() {
      return hashCode;
    }

    @Override public void appendTo(StringBuilder out) {
      base.appendToUnlessUniversal(out);
      out.append(':');
      out.append(pseudoClass);
    }
  }

  private static class PseudoElementSelector extends Selector {
    private final Selector base;
    private final PseudoElement pseudoElement;
    private final int hashCode;

    PseudoElementSelector(Selector base, PseudoElement pseudoElement) {
      this.base = checkNotNull(base);
      this.pseudoElement = checkNotNull(pseudoElement);
      hashCode = Objects.hashCode(base, pseudoElement) + 7203;
    }

    @Override public Selector asAncestorOf(Selector descendant) {
      throw new IllegalStateException("nothing can follow a pseudo-element");
    }

    @Override public Selector asParentOf(Selector child) {
      throw new IllegalStateException("nothing can follow a pseudo-element");
    }

    @Override public Selector asSiblingOf(Selector selector) {
      throw new IllegalStateException("nothing can follow a pseudo-element");
    }

    @Override public Selector asPrecedingSiblingOf(Selector child) {
      throw new IllegalStateException("nothing can follow a pseudo-element");
    }

    @Override public Selector onTag(String tagName) {
      throw new IllegalStateException("nothing can follow a pseudo-element");
    }

    @Override public Selector onClass(String className) {
      throw new IllegalStateException("nothing can follow a pseudo-element");
    }

    @Override public Selector onId(String id) {
      throw new IllegalStateException("nothing can follow a pseudo-element");
    }

    @Override public Selector on(AttributePredicate attributePredicate) {
      throw new IllegalStateException("nothing can follow a pseudo-element");
    }

    @Override public Selector on(PseudoClass pseudoClass) {
      throw new IllegalStateException("nothing can follow a pseudo-element");
    }

    @Override public Selector on(PseudoElement pseudoElement) {
      throw new IllegalStateException("nothing can follow a pseudo-element");
    }

    @Override public boolean equals(Object object) {
      if (object instanceof PseudoElementSelector) {
        PseudoElementSelector that = (PseudoElementSelector) object;
        return base.equals(that.base) &&
            pseudoElement.equals(that.pseudoElement);
      }
      return false;
    }

    @Override public int hashCode() {
      return hashCode;
    }

    @Override public void appendTo(StringBuilder out) {
      base.appendTo(out);
      // See http://stackoverflow.com/questions/10181729
      out.append(pseudoElement.css3() ? "::" : ":");
      out.append(pseudoElement);
    }
  }

  private static class CombinedSelector extends Selector {
    private final SimpleSelector left;
    private final String separator;
    private final Selector right;
    private final int hashCode;

    CombinedSelector(SimpleSelector left, String separator,
        Selector right) {
      this.left = checkNotNull(left);
      this.separator = checkNotNull(separator);
      this.right = checkNotNull(right);
      hashCode = Objects.hashCode(left, separator, right);
    }

    @Override public Selector asAncestorOf(Selector descendant) {
      return new CombinedSelector(left, separator,
          right.asAncestorOf(descendant));
    }

    @Override public Selector asParentOf(Selector child) {
      return new CombinedSelector(left, separator, right.asParentOf(child));
    }

    @Override public Selector asSiblingOf(Selector selector) {
      return new CombinedSelector(left, separator,
          right.asSiblingOf(selector));
    }

    @Override public Selector asPrecedingSiblingOf(Selector child) {
      return new CombinedSelector(left, separator,
          right.asPrecedingSiblingOf(child));
    }

    @Override public Selector onTag(String tagName) {
      return new CombinedSelector(left, separator, right.onTag(tagName));
    }

    @Override public Selector onId(String id) {
      return new CombinedSelector(left, separator, right.onId(id));
    }

    @Override public Selector onClass(String className) {
      return new CombinedSelector(left, separator, right.onClass(className));
    }

    @Override public Selector on(AttributePredicate attributePredicate) {
      return new CombinedSelector(left, separator,
          right.on(attributePredicate));
    }

    @Override public Selector on(PseudoClass pseudoClass) {
      return new CombinedSelector(left, separator, right.on(pseudoClass));
    }

    @Override public Selector on(PseudoElement pseudoElement) {
      return new PseudoElementSelector(this, pseudoElement);
    }

    @Override void appendTo(StringBuilder out) {
      left.appendTo(out);
      out.append(separator);
      right.appendTo(out);
    }

    @Override public boolean equals(Object object) {
      if (object == this) {
        return true;
      } else if (object instanceof CombinedSelector) {
        CombinedSelector that = (CombinedSelector) object;
        return left.equals(that.left) && separator.equals(that.separator) &&
            right.equals(that.right);
      }
      return false;
    }

    @Override public int hashCode() {
      return hashCode;
    }
  }
}
