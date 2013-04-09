package com.projetloki.genesis;

import com.projetloki.genesis.image.Image;

/**
 * Base class for all objects that can append CSS code to a string builder.
 *
 * @author Clément Roux
 */
abstract class AppendableTo {
  private volatile String string;

  /** Appends the CSS code to the given string builder. */
  abstract void appendTo(StringBuilder out, CssGenerationContext context);

  // Not final
  @Override public String toString() {
    String result = string;
    if (result == null) {
      StringBuilder builder = new StringBuilder();
      appendTo(builder, CONTEXT_FOR_TO_STRING);
      string = result = builder.toString();
    }
    return result;
  }

  private static final CssGenerationContext CONTEXT_FOR_TO_STRING =
      new CssGenerationContext() {
    @Override public String getImageUrl(Image image) {
      return image.toString();
    }

    @Override public BackgroundPosition getBackgroundPosition(Image image) {
      return BackgroundPosition.LEFT_TOP;
    }
  };
}
