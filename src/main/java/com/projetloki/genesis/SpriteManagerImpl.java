package com.projetloki.genesis;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Ascii;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.io.BaseEncoding;
import com.projetloki.genesis.image.Image;
import com.projetloki.genesis.image.ImageFormat;
import com.projetloki.genesis.image.Images;

/**
 * Standard implementation of {@link SpriteManager}.
 *
 * @author Clément Roux
 */
final class SpriteManagerImpl implements SpriteManager {
  private final FileSystem fileSystem;
  // Maps an image to the sprite it belongs to
  private final ImmutableMap<Image, Sprite> imageToSprite;
  // Maps the image view of a sprite to a Ref
  // Note that different sprites can have the same image view
  private final Map<Image, Ref> spriteImageToRef;

  @VisibleForTesting
  SpriteManagerImpl(String imageFolderUri, Collection<Sprite> sprites,
      FileSystem fileSystem) {
    this.fileSystem = checkNotNull(fileSystem);
    String escapedUri = Format.escape(imageFolderUri);
    ImmutableMap.Builder<Image, Sprite> builder = ImmutableMap.builder();
    spriteImageToRef = Maps.newHashMap();
    for (Sprite sprite : sprites) {
      for (Image subImage : sprite.subImages()) {
        builder.put(subImage, sprite);
      }
      Image spriteImage = sprite.asImage();
      if (spriteImageToRef.containsKey(spriteImage)) {
        continue;
      }
      Ref ref = new Ref(spriteImage, escapedUri);
      spriteImageToRef.put(spriteImage, ref);
    }
    imageToSprite = builder.build();
  }

  @Override public String getImageUrl(Image image) {
    Sprite sprite = imageToSprite.get(image);
    return spriteImageToRef.get(sprite.asImage()).url;
  }

  @Override public BackgroundPosition getBackgroundPosition(Image image) {
    Sprite sprite = imageToSprite.get(image);
    checkArgument(sprite != null);
    return sprite.getPosition(image);
  }

  @Override public void writeSprites(File folder) throws IOException {
    for (Ref ref : spriteImageToRef.values()) {
      File file = new File(folder, ref.filename);
      if (fileSystem.exists(file)) {
        continue;
      }
      File tempFile = fileSystem.createTempFile("img", ".temp", folder);
      try {
        fileSystem.saveImage(ref.spriteImage, tempFile);
        fileSystem.rename(tempFile, file);
      } finally {
        fileSystem.delete(tempFile);
      }
    }
  }

  static Builder builder(String imageFolderUri) {
    return new Builder(imageFolderUri);
  }

  private static class Ref {
    final Image spriteImage;
    final String filename;
    final String url;

    Ref(Image spriteImage, String escapedUri) {
      this.spriteImage = spriteImage;
      // Compute the filename from the hash of the image
      byte[] bytes = spriteImage.hash().asBytes();
      // With 6 bytes, the risk of collision is close enough to zero
      int len = Math.min(bytes.length, 6);
      String filenameBase = Ascii.toLowerCase(
          BaseEncoding.base32().omitPadding().encode(
              spriteImage.hash().asBytes(), 0, len));
      ImageFormat imageFormat = Images.bestFormat(spriteImage);
      filename = filenameBase + "." + imageFormat.getExtension();
      url = String.format("url('%s%s')", escapedUri, filename);
    }
  }

  static final class Builder {
    private final String imageFolderUri;
    private final Map<Image, BackgroundRepeat> imageToRepeat =
        Maps.newLinkedHashMap();

    Builder(String imageFolderUri) {
      this.imageFolderUri = checkNotNull(imageFolderUri);
    }

    Builder addImage(Image image, BackgroundRepeat repeat) {
      BackgroundRepeat old = imageToRepeat.get(image);
      BackgroundRepeat newRepeat = old != null ? old.or(repeat) : repeat;
      imageToRepeat.put(image, newRepeat);
      return this;
    }

    SpriteManagerImpl build() {
      Multimap<BackgroundRepeat, Image> repeatToImages = HashMultimap.create();
      Multimaps.invertFrom(Multimaps.forMap(imageToRepeat), repeatToImages);
      List<Sprite> sprites = Lists.newArrayList();
      for (BackgroundRepeat repeat : repeatToImages.keySet()) {
        Collection<Image> images = repeatToImages.get(repeat);
        Collection<Sprite> newSprites = Spriter.getDefault().join(images,
            repeat.repeatsX(), repeat.repeatsY());
        sprites.addAll(newSprites);
      }
      return new SpriteManagerImpl(imageFolderUri, sprites,
          DEFAULT_FILE_SYSTEM);
    }
  }

  @VisibleForTesting
  static interface FileSystem {
    boolean exists(File file);

    boolean rename(File from, File to);

    boolean delete(File file);

    File createTempFile(String prefix, String suffix, File directory)
        throws IOException;

    void saveImage(Image im, File out) throws IOException;
  }

  static final FileSystem DEFAULT_FILE_SYSTEM = new FileSystem() {
    @Override public boolean exists(File file) {
      return file.exists();
    }

    @Override public boolean rename(File from, File to) {
      return from.renameTo(to);
    }

    @Override public boolean delete(File file) {
      return file.delete();
    }

    @Override public File createTempFile(String prefix, String suffix,
        File directory) throws IOException {
      return File.createTempFile(prefix, suffix, directory);
    }

    @Override public void saveImage(Image im, File out) throws IOException {
      Images.save(im, out);
    }
  };
}
