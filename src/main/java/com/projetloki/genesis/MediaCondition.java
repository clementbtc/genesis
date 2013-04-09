package com.projetloki.genesis;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

/**
 * A single media query, or several media queries combined with the
 * {@linkplain #or(MediaCondition) or} operator. Example:
 * <pre><code> MediaCondition condition = MediaQuery.HANDHELD
 *     .or(MediaQuery.ALL.and(MediaFeature.minWidthPx(700)));
 *
 * cssBuilder.addRules(condition, "div.topButton{width:200px}");
 * </code></pre>
 *
 * <p>In CSS terminology, it is referred to as a <em>media query list</em>.
 * The name has been changed because it conflicts with Java terminology.
 * </p>
 *
 * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/Media_queries">MDN</a>
 *
 * @author Clément Roux
 */
public abstract class MediaCondition extends AppendableToNoContext {
  /**
   * Returns a media condition that evaluates to true if both {@code this} and
   * the given condition to.
   */
  public final MediaCondition or(MediaCondition other) {
    if (equals(MediaQuery.ALL) || other.equals(MediaQuery.ALL)) {
      return MediaQuery.ALL;
    }
    return new OrMediaCondition(this, other);
  }

  private static class OrMediaCondition extends MediaCondition {
    final MediaCondition left;
    final MediaCondition right;

    OrMediaCondition(MediaCondition left, MediaCondition right) {
      this.left = checkNotNull(left);
      this.right = checkNotNull(right);
    }

    @Override public boolean equals(Object object) {
      if (object == this) {
        return true;
      } else if (object instanceof OrMediaCondition) {
        OrMediaCondition that = (OrMediaCondition) object;
        return left.equals(that.left) && right.equals(that.right);
      }
      return false;
    }

    @Override public int hashCode() {
      return Objects.hashCode(left, right);
    }

    @Override void appendTo(StringBuilder out) {
      left.appendTo(out);
      out.append(",");
      right.appendTo(out);
    }
  }
}
