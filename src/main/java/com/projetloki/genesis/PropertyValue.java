package com.projetloki.genesis;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The value of a CSS property. Either an expression, or an expression followed
 * by !important.
 *
 * @author Clément Roux
 */
abstract class PropertyValue extends AppendableToNoContext {
  static final Parser<PropertyValue> PARSER = new Parser<PropertyValue>() {
    @Override PropertyValue tryParse(ParserInput input) {
      Expr expr = Expr.PARSER.parse(input);
      input.skipAllSpacesAndComments();
      if (input.startsWithThenMove("!")) {
        input.skipSpaces();
        input.checkStartsWithIgnoreCaseAndMove("important");
        return important(expr);
      }
      return expr;
    }

    @Override String what() {
      return "property value";
    }
  };

  static PropertyValue important(Expr expr) {
    return new Important(expr);
  }

  private static class Important extends PropertyValue {
    private final Expr expr;

    Important(Expr expr) {
      this.expr = checkNotNull(expr);
    }

    @Override void appendTo(StringBuilder out) {
      expr.appendTo(out);
      out.append(" !important");
    }

    @Override public boolean equals(Object object) {
      return object instanceof Important &&
          (((Important) object).expr).equals(expr);
    }

    @Override public int hashCode() {
      return expr.hashCode() + 577585;
    }
  }
}
