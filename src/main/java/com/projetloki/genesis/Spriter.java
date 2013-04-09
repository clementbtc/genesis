package com.projetloki.genesis;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.projetloki.genesis.image.Image;

/**
 * A spriter joins a collection of images with the same repeat (x or y)
 * requirement into a smaller collection of
 * <a href="http://en.wikipedia.org/wiki/Sprite_(computer_graphics)#Sprites_by_CSS">sprites</a>.
 *
 * @author Clément Roux
 */
abstract class Spriter {
  static Spriter getDefault() {
    return Impl.INSTANCE;
  }

  final Collection<Sprite> join(Collection<? extends Image> images,
      boolean repeatsX, boolean repeatsY) {
    if (images.isEmpty()) {
      return ImmutableList.of();
    }
    List<Sprite> sprites = Lists.newArrayListWithCapacity(images.size());
    for (Image image : images) {
      sprites.add(Sprite.forSingleImage(image));
    }
    if (repeatsX && repeatsY) {
      return sprites;
    } else if (repeatsX || repeatsY) {
      Map<Integer, Sprite> dimToSprite = Maps.newLinkedHashMap();
      for (Sprite sprite : sprites) {
        Image image = sprite.asImage();
        int dim = repeatsX ? image.width() : image.height();
        Sprite oldSprite = dimToSprite.get(dim);
        Sprite newSprite;
        if (oldSprite == null) {
          newSprite = sprite;
        } else {
          newSprite = repeatsX
              ? sprite.concatY(oldSprite)
              : sprite.concatX(oldSprite);
        }
        dimToSprite.put(dim, newSprite);
      }
      return dimToSprite.values();
    }
    return doJoin(sprites);
  }

  /**
   * Joins a collection of sprites that have no repeat requirements, i.e. that
   * can be concatenated horizontally and vertically, and returns a smaller
   * collection.
   */
  abstract List<Sprite> doJoin(List<Sprite> sprites);

  private static class Impl extends Spriter {
    static final Impl INSTANCE = new Impl();

    @Override
    List<Sprite> doJoin(List<Sprite> sprites) {
      if (sprites.isEmpty()) {
        return ImmutableList.of();
      }
      List<Sprite> result = null;
      long minArea = Long.MAX_VALUE;
      for (int i = 0; i < 4 * sprites.size(); i++) {
        List<Sprite> copy = Lists.newLinkedList(sprites);
        Collections.shuffle(copy, new Random(i));
        while (copy.size() != 1) {
          Sprite sprite0 = copy.get(0);
          Sprite sprite1 = copy.get(1);
          Sprite newSprite = sprite0.concatXOrY(sprite1);
          copy.remove(0);
          copy.remove(0);
          copy.add(newSprite);
        }
        long area = copy.get(0).area();
        if (area < minArea) {
          minArea = area;
          result = copy;
        }
      }
      return result;
    }
  }
}
