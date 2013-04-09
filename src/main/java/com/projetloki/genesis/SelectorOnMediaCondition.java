package com.projetloki.genesis;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

/**
 * An immutable (selector, media condition) pair.
 *
 * @author Clément Roux
 */
final class SelectorOnMediaCondition {
  final Selector selector;
  final MediaCondition condition;
  private final int hashCode;

  SelectorOnMediaCondition(Selector selector, MediaCondition condition) {
    this.selector = checkNotNull(selector);
    this.condition = checkNotNull(condition);
    hashCode = Objects.hashCode(selector, condition);
  }

  @Override public boolean equals(Object object) {
    if (object == this) {
      return true;
    } else if (object instanceof SelectorOnMediaCondition) {
      SelectorOnMediaCondition that = (SelectorOnMediaCondition) object;
      return selector.equals(that.selector) &&
          condition.equals(that.condition);
    }
    return false;
  }

  @Override public int hashCode() {
    return hashCode;
  }

  /**
   * Returns the string representation of the selector
   * (omits the media condition).
   */
  @Override public String toString() {
    // Omit the media
    return selector.toString();
  }
}
