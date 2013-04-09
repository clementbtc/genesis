package com.projetloki.genesis;

import com.projetloki.genesis.image.Image;

/**
 * Object passed to
 * {@link AppendableTo#appendTo(StringBuilder, CssGenerationContext)},
 * to map between Images and image files.
 *
 * @author Clément Roux
 */
interface CssGenerationContext {
  /**
   * Returns the CSS URI of the sprite containing the given image. The result
   * starts with the prefix {@code url(}.
   */
  String getImageUrl(Image image);

  /**
   * If the sprite containing the given image also contains several smaller
   * images, returns the position of the top-left corner of the given image.
   * Otherwise, returns {@link BackgroundPosition#LEFT_TOP}.
   */
  BackgroundPosition getBackgroundPosition(Image image);
}
