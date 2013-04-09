package com.projetloki.genesis;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

/**
 * A CSS expression.
 *
 * @author Clément Roux
 */
abstract class Expr extends PropertyValue {
  static final Parser<Expr> PARSER = new Parser<Expr>() {
    @Override Expr tryParse(ParserInput input) {
      Term firstTerm = Term.PARSER.tryParse(input);
      if (firstTerm == null) {
        return null;
      }
      Builder builder = builder(firstTerm);
      while (true) {
        input.skipAllSpacesAndComments();
        ExprOp op;
        if (input.startsWithThenMove("/")) {
          op = ExprOp.SLASH;
        } else if (input.startsWithThenMove(",")) {
          op = ExprOp.COMMA;
        } else {
          op = ExprOp.EMPTY;
        }
        Term next;
        if (op == ExprOp.EMPTY) {
          next = Term.PARSER.tryParse(input);
          if (next == null) {
            return builder.build();
          }
        } else {
          next = Term.PARSER.parse(input);
        }
        builder.add(op, next);
      }
    }

    @Override String what() {
      return "expression";
    }
  };

  abstract boolean isTerm();

  abstract Term asTerm();

  abstract List<Expr> splitBy(ExprOp op);

  private static class TermList extends Expr {
    final List<Term> terms;
    final List<ExprOp> ops;

    TermList(List<Term> terms, List<ExprOp> ops) {
      checkArgument(terms.size() == ops.size() + 1);
      this.terms = terms;
      this.ops = ops;
    }

    @Override boolean isTerm() {
      return false;
    }

    @Override Term asTerm() {
      throw new IllegalStateException();
    }

    @Override List<Expr> splitBy(ExprOp op) {
      checkNotNull(op);
      List<Expr> result = Lists.newArrayList();
      List<Term> termsLeft = terms;
      List<ExprOp> opsLeft = ops;
      int index;
      while ((index = ops.indexOf(op)) != -1) {
        if (index == 0) {
          result.add(terms.get(0));
        } else {
          List<Term> termsHead = termsLeft.subList(0, index + 1);
          List<ExprOp> opsHead = opsLeft.subList(0, index);
          result.add(new TermList(termsHead, opsHead));
        }
        termsLeft = termsLeft.subList(index + 1, termsLeft.size());
        opsLeft = opsLeft.subList(index + 1, opsLeft.size());
      }
      if (opsLeft.isEmpty()) {
        result.add(termsLeft.get(0));
      } else {
        result.add(new TermList(termsLeft, opsLeft));
      }
      return result;
    }

    @Override void appendTo(StringBuilder out) {
      terms.get(0).appendTo(out);
      for (int i = 0; i < ops.size(); i++) {
        out.append(ops.get(i).c);
        terms.get(i + 1).appendTo(out);
      }
    }

    @Override public boolean equals(Object object) {
      if (object == this) {
        return true;
      } else if (object instanceof TermList) {
        TermList that = (TermList) object;
        return terms.equals(that.terms) && ops.equals(that.ops);
      }
      return false;
    }

    @Override public int hashCode() {
      return Objects.hashCode(terms, ops);
    }
  }

  static Builder builder(Term firstTerm) {
    return new Builder(firstTerm);
  }

  static final class Builder {
    Term firstTerm;
    private List<ExprOp> ops;
    private List<Term> terms;

    Builder(Term firstTerm) {
      this.firstTerm = checkNotNull(firstTerm);
    }

    Builder add(ExprOp op, Term term) {
      if (ops == null) {
        ops = Lists.newArrayList();
        terms = Lists.newArrayList(firstTerm);
      }
      ops.add(checkNotNull(op));
      terms.add(checkNotNull(term));
      return this;
    }

    Expr build() {
      if (ops == null) {
        return firstTerm;
      }
      return new TermList(terms, ops);
    }
  }
}
