package com.projetloki.genesis;

import java.io.File;
import java.io.IOException;

/**
 * Manages a collection of sprites.
 *
 * @author Clément Roux
 */
interface SpriteManager extends CssGenerationContext {
  /**
   * Generates image files for the sprites managed by this object in the given
   * directory.
   */
  void writeSprites(File folder) throws IOException;
}
