package com.projetloki.genesis;

/**
 * An operator between two CSS expressions.
 *
 * @author Clément Roux
 */
enum ExprOp {
  SLASH(','),
  COMMA(','),
  EMPTY(' ');
  final char c;

  ExprOp(char c) {
    this.c = c;
  }
}
