package com.projetloki.genesis;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

/**
 * An immutable (selector, properties) pair. Does not contain a media condition.
 *
 * @author Clément Roux
 */
final class Rule {
  /**
   * A parser of CSS code, with the limitation that it does not accept at-rules.
   * Used in {@link CssBuilderImpl#addRules(String)}.
   */
  static final Parser<List<Rule>> LIST_PARSER = new Parser<List<Rule>>() {
    @Override List<Rule> tryParse(ParserInput input) {
      List<Rule> result = Lists.newArrayList();
      while (true) {
        List<Selector> selectors = Lists.newArrayList();
        Selector firstSelector = Selector.PARSER.tryParse(input);
        if (firstSelector == null) {
          return result;
        }
        selectors.add(firstSelector);
        input.skipAllSpacesAndComments();
        while (input.startsWithThenMove(",")) {
          Selector next = Selector.PARSER.tryParse(input);
          selectors.add(next);
          input.skipAllSpacesAndComments();
        }
        input.checkStartsWithAndMove("{");
        Properties properties = Properties.PARSER.parse(input);
        input.skipAllSpacesAndComments();
        input.checkStartsWithAndMove("}");
        for (Selector selector : selectors) {
          Rule rule = new Rule(selector, properties);
          result.add(rule);
        }
      }
    }

    @Override String what() {
      return "rules";
    }
  };

  final Selector selector;
  final Properties properties;

  Rule(Selector selector, Properties properties) {
    this.selector = checkNotNull(selector);
    this.properties = checkNotNull(properties);
  }

  @Override public boolean equals(Object object) {
    if (object instanceof Rule) {
      Rule that = (Rule) object;
      return selector.equals(that.selector) &&
          properties.equals(that.properties);
    }
    return false;
  }

  @Override public int hashCode() {
    return Objects.hashCode(selector, properties);
  }
}
