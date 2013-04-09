package com.projetloki.genesis;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

/**
 * A term in a CSS expression. Since a single term IS an expression, this class
 * extends {@link Expr}.
 *
 * @author Clément Roux
 */
abstract class Term extends Expr {
  static final Parser<Term> PARSER = new Parser<Term>() {
    @Override Term tryParse(ParserInput input) {
      input.skipAllSpacesAndComments();
      int oldPosition = input.position();
      String numberOrNull = input.tryReadNumber();
      // A number, a dimension or a percentage
      if (numberOrNull != null) {
        // A dimension?
        String unitOrNull = input.tryReadId();
        if (unitOrNull != null) {
          return new SimpleTerm(input.past(oldPosition), TermType.DIMENSION);
        } else if (input.startsWithThenMove("%")) {
          return new SimpleTerm(input.past(oldPosition), TermType.PERCENTAGE);
        }
        return new SimpleTerm(numberOrNull, TermType.NUMBER);
      }
      // A uri
      if (input.startsWithIgnoreCaseThenMove("url(")) {
        // Not clear whether comments are allowed here
        input.skipSpaces();
        if (input.startsWith("\"") || input.startsWith("'")) {
          Util.readStringLiteral(input);
          input.skipSpaces();
          input.checkStartsWithAndMove(")");
        } else {
          Util.readStringLiteral(input, ")");
        }
        return new SimpleTerm(input.past(oldPosition), TermType.URI);
      }
      // An identifier or a function
      String idOrNull = input.tryReadId();
      if (idOrNull != null) {
        // A function
        if (input.startsWithThenMove("(")) {
          input.skipAllSpacesAndComments();
          Expr arg = Expr.PARSER.parse(input);
          input.skipAllSpacesAndComments();
          input.checkStartsWithAndMove(")");
          return new FunctionTerm(idOrNull, arg);
        }
        // An identifier
        return new SimpleTerm(idOrNull, TermType.ID);
      }
      // A hexadecimal color code
      if (input.startsWithThenMove("#")) {
        input.read(Util.COLOR_CODE_PATTERN, "color code");
        return new SimpleTerm(input.past(oldPosition), TermType.COLOR);
      }
      // A string literal
      if (input.startsWith("\"") || input.startsWith("'")) {
        String stringLiteral = Util.readStringLiteral(input);
        return new SimpleTerm(stringLiteral, TermType.STRING);
      }
      return null;
    }

    @Override String what() {
      return "term";
    }
  };

  @Override final boolean isTerm() {
    return true;
  }

  @Override final Term asTerm() {
    return this;
  }

  @Override final List<Expr> splitBy(ExprOp op) {
    return ImmutableList.<Expr>of(this);
  }

  abstract TermType type();

  abstract String functionName();

  abstract Expr functionArg();

  static final class SimpleTerm extends Term {
    final String term;
    final TermType type;

    SimpleTerm(String term, TermType type) {
      this.term = checkNotNull(term);
      this.type = checkNotNull(type);
    }

    @Override TermType type() {
      return type;
    }

    @Override String functionName() {
      throw new IllegalStateException("not a function");
    }

    @Override Expr functionArg() {
      throw new IllegalStateException("not a function");
    }

    @Override void appendTo(StringBuilder out) {
      out.append(term);
    }

    @Override public boolean equals(Object object) {
      if (object instanceof SimpleTerm) {
        SimpleTerm that = (SimpleTerm) object;
        return term.equals(that.term) && type.equals(that.type);
      }
      return false;
    }

    @Override public int hashCode() {
      return Objects.hashCode(term, type);
    }

    @Override public String toString() {
      return term;
    }
  }

  static final class FunctionTerm extends Term {
    final String name;
    final Expr arg;

    FunctionTerm(String name, Expr arg) {
      this.name = checkNotNull(name);
      this.arg = checkNotNull(arg);
    }

    @Override TermType type() {
      return TermType.FUNCTION;
    }

    @Override String functionName() {
      return name;
    }

    @Override Expr functionArg() {
      return arg;
    }

    @Override void appendTo(StringBuilder out) {
      out.append(name);
      out.append('(');
      arg.appendTo(out);
      out.append(')');
    }

    @Override public boolean equals(Object object) {
      if (object instanceof FunctionTerm) {
        FunctionTerm that = (FunctionTerm) object;
        return name.equals(that.name) && arg.equals(that.arg);
      }
      return false;
    }

    @Override public int hashCode() {
      return Objects.hashCode(name, arg);
    }
  }
}
