package com.projetloki.genesis;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import com.projetloki.genesis.image.Image;
import com.projetloki.genesis.image.ImageFormat;
import com.projetloki.genesis.image.Images;

/**
 * The result of configuring a CSS module.
 * Contains a set of rules, but also CSS imports, font-face definition,
 * keyframes definitions and module dependencies.
 *
 * @author Clément Roux
 */
final class Rules extends AppendableTo {
  final CssModule module;
  final ImmutableList<RuleOnMediaCondition> rules;
  final ImmutableSet<CssModule> dependencies;
  final ImmutableSet<CssImport> imports;
  final ImmutableSet<FontFace> fontFaces;
  final ImmutableSet<Keyframes> animations;

  Rules(
      CssModule module,
      Collection<RuleOnMediaCondition> rules,
      Collection<CssModule> dependencies,
      Collection<CssImport> imports,
      Collection<FontFace> fontFaces,
      Collection<Keyframes> animations) {
    this.module = checkNotNull(module);
    this.rules = ImmutableList.copyOf(rules);
    this.dependencies = ImmutableSet.copyOf(dependencies);
    this.imports = ImmutableSet.copyOf(imports);
    this.fontFaces = ImmutableSet.copyOf(fontFaces);
    this.animations = ImmutableSet.copyOf(animations);
  }

  /**
   * {@inheritDoc}
   * Only appends the rules, not the imports, font-faces and animations.
   */
  @Override void appendTo(StringBuilder out, CssGenerationContext context) {
    PeekingIterator<RuleOnMediaCondition> it =
        Iterators.peekingIterator(rules.iterator());
    MediaCondition cond = MediaQuery.ALL;
    while (it.hasNext()) {
      RuleOnMediaCondition rule = it.next();
      Selector selector = rule.selector.selector;
      Properties properties = rule.properties;
      MediaCondition newCond = rule.selector.condition;
      if (!newCond.equals(cond)) {
        if (cond != MediaQuery.ALL) {
          // Close the previously-opened @media rule
          out.append("}\n");
        }
        if (newCond != MediaQuery.ALL) {
          out.append("@media ");
          newCond.appendTo(out);
          out.append("{\n");
        }
      }
      cond = newCond;
      if (cond != MediaQuery.ALL) {
        out.append("  ");
      }
      selector.appendTo(out);
      while (it.hasNext() && equalButSelector(rule, it.peek())) {
        out.append(',');
        it.next().selector.selector.appendTo(out);
      }
      out.append('{');
      properties.appendTo(out, context);
      out.append("}\n");
    }
    if (cond != MediaQuery.ALL) {
      // Close the previously-opened @media rule
      out.append("}\n");
    }
  }

  /**
   * Returns whether first and second have the same properties AND selector
   * condition.
   */
  private static boolean equalButSelector(RuleOnMediaCondition first,
      RuleOnMediaCondition second) {
    return first.properties.equals(second.properties) &&
        first.selector.condition.equals(second.selector.condition);
  }

  void registerImages(SpriteManagerImpl.Builder out) {
    for (RuleOnMediaCondition rule : rules) {
      Properties properties = rule.properties;
      if (properties.hasListStyleImage()) {
        out.addImage(properties.listStyleImage(), BackgroundRepeat.REPEAT);
      }
      if (properties.hasLayers()) {
        for (BackgroundLayer layer : properties.layers()) {
          Image image = layer.getImage();
          boolean png = Images.bestFormat(image) == ImageFormat.PNG;
          BackgroundRepeat repeat;
          if (png && layer.getPosition().equals(BackgroundPosition.LEFT_TOP)) {
            repeat = BackgroundRepeat.from(!layer.getFillX(), !layer.getFillY());
          } else {
            repeat = BackgroundRepeat.REPEAT;
          }
          out.addImage(image, repeat);
        }
      }
    }
  }
}
