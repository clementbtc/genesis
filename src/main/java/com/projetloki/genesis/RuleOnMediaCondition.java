package com.projetloki.genesis;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

/**
 * An immutable (selector, media condition, properties) tuple.
 *
 * @author Clément Roux
 */
final class RuleOnMediaCondition {
  final SelectorOnMediaCondition selector;
  final Properties properties;

  RuleOnMediaCondition(SelectorOnMediaCondition selector,
      Properties properties) {
    this.selector = checkNotNull(selector);
    this.properties = checkNotNull(properties);
  }

  @Override public boolean equals(Object object) {
    if (object == this) {
      return true;
    } else if (object instanceof RuleOnMediaCondition) {
      RuleOnMediaCondition that = (RuleOnMediaCondition) object;
      return selector.equals(that.selector) &&
          properties.equals(that.properties);
    }
    return false;
  }

  @Override public int hashCode() {
    return Objects.hashCode(selector, properties);
  }
}
