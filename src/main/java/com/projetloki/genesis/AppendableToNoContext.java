package com.projetloki.genesis;

/**
 * An {@link AppendableTo} independent from the context.
 *
 * @author Clément Roux
 */
abstract class AppendableToNoContext extends AppendableTo {
  @Deprecated
  @Override final void appendTo(StringBuilder out,
      CssGenerationContext context) {
    appendTo(out);
  }

  abstract void appendTo(StringBuilder out);
}
