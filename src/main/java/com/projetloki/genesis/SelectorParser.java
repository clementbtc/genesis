package com.projetloki.genesis;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.MoreObjects;

/**
 * Parser for CSS selectors.
 *
 * @author Clément Roux
 */
final class SelectorParser extends Parser<Selector> {
  @Override Selector tryParse(ParserInput input) {
    Selector simpleSelector = null;
    while (true) {
      input.skipAllSpacesAndComments();
      if (simpleSelector == null && input.startsWithThenMove("*")) {
        simpleSelector = Selector.ANY;
      } else if (input.startsWithThenMove("#")) {
        String id = input.readId();
        simpleSelector = MoreObjects.firstNonNull(simpleSelector, Selector.ANY)
            .onId(id);
      } else if (input.startsWithThenMove(".")) {
        String className = input.readId();
        simpleSelector = MoreObjects.firstNonNull(simpleSelector, Selector.ANY)
            .onClass(className);
      } else if (input.startsWithThenMove("[")) {
        AttributePredicate pred = AttributePredicate.PARSER.parse(input);
        simpleSelector = MoreObjects.firstNonNull(simpleSelector, Selector.ANY)
            .on(pred);
      } else if (input.startsWithThenMove(":")) {
        simpleSelector = MoreObjects.firstNonNull(simpleSelector, Selector.ANY);
        boolean doubleColon = input.startsWithThenMove(":");
        int oldPosition = input.position();
        String id = input.readId().toLowerCase();
        if (PseudoElement.STRING_TO_CONSTANT.containsKey(id)) {
          PseudoElement pseudoEl =
              PseudoElement.STRING_TO_CONSTANT.get(id);
          simpleSelector = simpleSelector.on(pseudoEl);
          checkArgument(doubleColon || !pseudoEl.css3(),
              "CSS3 double colon notation expected: %s", id);
        } else {
          input.setPosition(oldPosition);
          // Not a pseudo-element so it must be a pseudo-class
          PseudoClass pseudoCl = PseudoClass.PARSER.parse(input);
          checkArgument(!doubleColon,
              "CSS3 double colon notation cannot be used on pseudo-class: %s",
              pseudoCl);
          simpleSelector = simpleSelector.on(pseudoCl);
        }
      } else {
        String tagNameOrNull = input.tryReadId();
        if (tagNameOrNull == null) {
          return simpleSelector;
        }
        checkArgument(simpleSelector == null,
            "tag name must be first: %s", tagNameOrNull);
        simpleSelector = Selector.ANY.onTag(tagNameOrNull);
      }
      boolean hasSpace = input.skipAllSpacesAndComments();
      // Closing parenthesis as a delimiter, in the case of :not(s)
      if (input.isEmpty() || input.startsWith(")")) {
        // It was the last simple selector
        return simpleSelector;
      }
      Combinator combinator = hasSpace ? Combinator.DESCENDANT : null;
      if (input.startsWithThenMove(">")) {
        combinator = Combinator.CHILD;
      } else if (input.startsWithThenMove("+")) {
        combinator = Combinator.ADJACENT_SIBLING;
      } else if (input.startsWithThenMove("~")) {
        combinator = Combinator.GENERAL_SIBLING;
      }
      if (combinator != null) {
        Selector right;
        if (combinator == Combinator.DESCENDANT) {
          right = tryParse(input);
          if (right == null) {
            return simpleSelector;
          }
        } else {
          // Required
          right = parse(input);
        }
        return combinator.combine(simpleSelector, right);
      }
    }
  }

  @Override String what() {
    return "selector";
  }

  private enum Combinator {
    DESCENDANT {
      @Override Selector combine(Selector left, Selector right) {
        return left.asAncestorOf(right);
      }
    },
    CHILD {
      @Override Selector combine(Selector left, Selector right) {
        return left.asParentOf(right);
      }
    },
    ADJACENT_SIBLING {
      @Override Selector combine(Selector left, Selector right) {
        return left.asPrecedingSiblingOf(right);
      }
    },
    GENERAL_SIBLING {
      @Override Selector combine(Selector left, Selector right) {
        return left.asSiblingOf(right);
      }
    };
    abstract Selector combine(Selector left, Selector right);
  }
}
