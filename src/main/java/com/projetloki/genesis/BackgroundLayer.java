package com.projetloki.genesis;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import com.projetloki.genesis.image.Image;

/**
 * A background image to paint on the background area. Includes properties that
 * specify the position in the background area at which the image must be
 * painted, whether the image must be repeated horizontally/vertically, whether
 * the image must be resized.
 *
 * <p>Background layers are immutable. All the methods setting a property, e.g.
 * {@link #noRepeatX()}, return a new background layer that is similar to this
 * background layer with the new property set.</p>
 *
 * @see Properties.Builder#setBackground(BackgroundLayer...)
 * @see <a href="http://www.w3.org/TR/css3-background/#ltbg-layergt">http://www.w3.org/TR/css3-background/#ltbg-layergt</a>
 *
 * @author Clément Roux
 */
public final class BackgroundLayer extends AppendableTo {
  /**
   * Returns a new background layer for the given background image. The image
   * is painted as-is at the top-left corner of the area, and then repeated
   * horizontally and vertically.
   */
  public static BackgroundLayer of(Image image) {
    return new BackgroundLayer(image);
  }

  private final Image image;
  private String repeatX;
  private String repeatY;
  private boolean fillX;
  private boolean fillY;
  private BackgroundPosition position;
  private BackgroundSize size;
  private String attachment;
  private String origin;
  private String clip;

  private BackgroundLayer(Image image) {
    this.image = checkNotNull(image);
    repeatX = "repeat";
    repeatY = "repeat";
    position = BackgroundPosition.LEFT_TOP;
    size = BackgroundSize.AUTO;
    attachment = "scroll";
    origin = "padding-box";
    clip = "border-box";
  }

  private BackgroundLayer(BackgroundLayer from) {
    image = from.image;
    repeatX = from.repeatX;
    repeatY = from.repeatY;
    fillX = from.fillX;
    fillY = from.fillY;
    position = from.position;
    size = from.size;
    attachment = from.attachment;
    origin = from.origin;
    clip = from.clip;
  }

  /**
   * Sets the background-repeat property to 'no-repeat'. The image is placed
   * once and not repeated in any direction.
   *
   * <p>There are two methods that set the background-repeat property to
   * 'no-repeat'. To know which one to call, you must determine whether the
   * background image will fill the painting area. You could always call
   * {@link #noRepeat()} but then Genesis won't be able to create
   * CSS sprites to reduce the number of images to download - there may be a
   * performance penalty. But if you call {@link #fill()} when you are not
   * supposed to, you may end up with bad surprises.
   * See these examples:
   * <uL>
   * <li>The painting area is 20x20, the image is 20x20 and the background
   * position is (0, 0) → fill
   * <li>The painting area is 20x20, the image is 30x30 and the background
   * position is (-5, -5) → fill
   * <li>The painting area is 20x20, the image is 10x10 and the background
   * position is (5, 5) → no-repeat
   * <li>The painting area is 20x20, the image is 20x20 and the background
   * position is (-1, -1) → no-repeat
   * </ul>
   * </p>
   */
  public BackgroundLayer noRepeat() {
    return repeat("no-repeat", false);
  }

  /**
   * Sets the background-repeat property to 'no-repeat', and specifies that the
   * image fills the background painting area. Makes CSS sprite optimization
   * possible.
   *
   * <p>There are two methods that set the background-repeat property to
   * 'no-repeat'. To know which one to call, you must determine whether the
   * background image will fill the painting area. You could always call
   * {@link #noRepeat()} but then Genesis won't be able to create
   * CSS sprites to reduce the number of images to download - there may be a
   * performance penalty. But if you call {@link #fill()} when you are not
   * supposed to, you may end up with bad surprises.
   * See these examples:
   * <uL>
   * <li>The painting area is 20x20, the image is 20x20 and the background
   * position is (0, 0) → fill
   * <li>The painting area is 20x20, the image is 30x30 and the background
   * position is (-5, -5) → fill
   * <li>The painting area is 20x20, the image is 10x10 and the background
   * position is (5, 5) → no-repeat
   * <li>The painting area is 20x20, the image is 20x20 and the background
   * position is (-1, -1) → no-repeat
   * </ul>
   * </p>
   */
  public BackgroundLayer fill() {
    return repeat("no-repeat", true);
  }

  /**
   * Sets the background-repeat property to 'space'. The image is repeated as
   * often as will fit within the background positioning area without being
   * clipped and then the images are spaced out to fill the area. The first and
   * last images touch the edges of the area.
   * @see <a href="http://caniuse.com/background-img-opts">Browser support</a>
   */
  @PoorBrowserSupport
  public BackgroundLayer space() {
    return repeat("space", false);
  }

  /**
   * Sets the background-repeat property to 'round'. The image is repeated as
   * often as will fit within the background positioning area. If it doesn't fit
   * a whole number of times, it is rescaled so that it does.
   * @see <a href="http://caniuse.com/background-img-opts">Browser support</a>
   */
  @PoorBrowserSupport
  public BackgroundLayer round() {
    return repeat("round", false);
  }

  private BackgroundLayer repeat(String repeat, boolean fill) {
    BackgroundLayer result = new BackgroundLayer(this);
    result.fillX = result.fillY = fill;
    result.repeatX = result.repeatY = repeat;
    return result;
  }

  /**
   * Sets the x-component of the background-repeat property to 'no-repeat'. The
   * image is placed once and not repeated horizontally.
   *
   * <p>There are two methods that set the x-component of the background-repeat
   * property to 'no-repeat'. To know which one to call, you must determine
   * whether the background image will fill the width of the painting area. You
   * could always call {@link #noRepeatX()} but then Genesis won't be able to
   * create CSS sprites to reduce the number of images to download - there may
   * be a performance penalty. But if you call {@link #fillX()} when you are not
   * supposed to, you may end up with bad surprises.
   * See these examples:
   * <uL>
   * <li>The painting area is 20x?, the image is 20x? and the background
   * position is (0, ?) → fill-x
   * <li>The painting area is 20x?, the image is 30x? and the background
   * position is (-5, ?) → fill-x
   * <li>The painting area is 20x?, the image is 10x? and the background
   * position is (5, ?) → no-repeat-x
   * <li>The painting area is 20x?, the image is 20x? and the background
   * position is (-1, ?) → no-repeat-x
   * </ul>
   * </p>
   */
  public BackgroundLayer noRepeatX() {
    return repeatX("no-repeat", false);
  }

  /**
   * Sets the x-component of the background-repeat property to 'no-repeat', and
   * specifies that the image fills the width of the background painting area.
   * The image is placed once and not repeated horizontally.
   *
   * <p>There are two methods that set the x-component of the background-repeat
   * property to 'no-repeat'. To know which one to call, you must determine
   * whether the background image will fill the width of the painting area. You
   * could always call {@link #noRepeatX()} but then Genesis won't be able to
   * create CSS sprites to reduce the number of images to download - there may
   * be a performance penalty. But if you call {@link #fillX()} when you are not
   * supposed to, you may end up with bad surprises.
   * See these examples:
   * <uL>
   * <li>The painting area is 20x?, the image is 20x? and the background
   * position is (0, ?) → fill-x
   * <li>The painting area is 20x?, the image is 30x? and the background
   * position is (-5, ?) → fill-x
   * <li>The painting area is 20x?, the image is 10x? and the background
   * position is (5, ?) → no-repeat-x
   * <li>The painting area is 20x?, the image is 20x? and the background
   * position is (-1, ?) → no-repeat-x
   * </ul>
   * </p>
   */
  public BackgroundLayer fillX() {
    return repeatX("no-repeat", true);
  }

  /**
   * Sets the x-component of the background-repeat property to 'space'. The
   * image is repeated horizontally as often as will fit within the background
   * positioning area without being clipped and then the images are spaced out
   * to fill the area. The first and last images touch the edges of the area.
   * @see <a href="http://caniuse.com/background-img-opts">Browser support</a>
   */
  @PoorBrowserSupport
  public BackgroundLayer spaceX() {
    return repeatX("space", false);
  }

  /**
   * Sets the x-component of the background-repeat property to 'round'. The
   * image is repeated horizontally as often as will fit within the background
   * positioning area. If it doesn't fit a whole number of times, it is rescaled
   * so that it does.
   * @see <a href="http://caniuse.com/background-img-opts">Browser support</a>
   */
  @PoorBrowserSupport
  public BackgroundLayer roundX() {
    return repeatX("round", false);
  }

  private BackgroundLayer repeatX(String repeatX, boolean fillX) {
    BackgroundLayer result = new BackgroundLayer(this);
    result.fillX = fillX;
    result.repeatX = repeatX;
    return result;
  }

  /**
   * Sets the y-component of the background-repeat property to 'no-repeat'. The
   * image is placed once and not repeated vertically.
   *
   * <p>There are two methods that set the y-component of the background-repeat
   * property to 'no-repeat'. To know which one to call, you must determine
   * whether the background image will fill the height of the painting area. You
   * could always call {@link #noRepeatY()} but then Genesis won't be able to
   * create CSS sprites to reduce the number of images to download - there may
   * be a performance penalty. But if you call {@link #fillY()} when you are not
   * supposed to, you may end up with bad surprises.
   * See these examples:
   * <uL>
   * <li>The painting area is ?x20, the image is ?x20 and the background
   * position is (?, 0) → fill-y
   * <li>The painting area is ?x20, the image is ?x30 and the background
   * position is (?, -5) → fill-y
   * <li>The painting area is ?x20, the image is ?x10 and the background
   * position is (?, 5) → no-repeat-y
   * <li>The painting area is ?x20, the image is ?x20 and the background
   * position is (?, -1) → no-repeat-y
   * </ul>
   * </p>
   */
  public BackgroundLayer noRepeatY() {
    return repeatY("no-repeat", false);
  }

  /**
   * Sets the y-component of the background-repeat property to 'no-repeat', and
   * specifies that the image fills the height of the background painting area.
   * The image is placed once and not repeated vertically.
   *
   * <p>There are two methods that set the y-component of the background-repeat
   * property to 'no-repeat'. To know which one to call, you must determine
   * whether the background image will fill the height of the painting area. You
   * could always call {@link #noRepeatY()} but then Genesis won't be able to
   * create CSS sprites to reduce the number of images to download - there may
   * be a performance penalty. But if you call {@link #fillY()} when you are not
   * supposed to, you may end up with bad surprises.
   * See these examples:
   * <uL>
   * <li>The painting area is ?x20, the image is ?x20 and the background
   * position is (?, 0) → fill-y
   * <li>The painting area is ?x20, the image is ?x30 and the background
   * position is (?, -5) → fill-y
   * <li>The painting area is ?x20, the image is ?x10 and the background
   * position is (?, 5) → no-repeat-y
   * <li>The painting area is ?x20, the image is ?x20 and the background
   * position is (?, -1) → no-repeat-y
   * </ul>
   * </p>
   */
  public BackgroundLayer fillY() {
    return repeatY("no-repeat", true);
  }

  /**
   * Sets the y-component of the background-repeat property to 'space'. The
   * image is repeated vertically as often as will fit within the background
   * positioning area without being clipped and then the images are spaced out
   * to fill the area. The first and last images touch the edges of the area.
   * @see <a href="http://caniuse.com/background-img-opts">Browser support</a>
   */
  @PoorBrowserSupport
  public BackgroundLayer spaceY() {
    return repeatY("space", false);
  }

  /**
   * Sets the y-component of the background-repeat property to 'round'. The
   * image is repeated vertically as often as will fit within the background
   * positioning area. If it doesn't fit a whole number of times, it is rescaled
   * so that it does.
   * @see <a href="http://caniuse.com/background-img-opts">Browser support</a>
   */
  @PoorBrowserSupport
  public BackgroundLayer roundY() {
    return repeatY("round", false);
  }

  private BackgroundLayer repeatY(String repeatY, boolean fillY) {
    BackgroundLayer result = new BackgroundLayer(this);
    result.fillY = fillY;
    result.repeatY = repeatY;
    return result;
  }

  /**
   * Sets the background-position property, that is the position in the
   * background area at which the image is painted.
   * @throws IllegalArgumentException if the given CSS code is not a valid
   *     background-position
   * @see <a href="http://www.w3.org/TR/css3-background/#background-position">http://www.w3.org/TR/css3-background/#background-position</a>
   */
  public BackgroundLayer position(String cssCode) {
    return position(BackgroundPosition.from(cssCode));
  }

  /**
   * Sets the background-position property, in pixels. The background position
   * is the position in the background area at which the image is painted.
   */
  public BackgroundLayer positionPx(int left, int top) {
    return position(BackgroundPosition.px(left, top));
  }

  /**
   * Sets the background-position proxy. The x-component is expressed in pixels
   * and the y-component in percent of the background area's height.
   */
  public BackgroundLayer positionPxPct(int left, double top) {
    return position(BackgroundPosition.pxPct(left, top));
  }

  /**
   * Sets the background-position property. The x-component is expressed in
   * percent of the background area's width, and the y-component in pixels.
   */
  public BackgroundLayer positionPctPx(double left, int top) {
    return position(BackgroundPosition.pctPx(left, top));
  }

  /**
   * Sets the background-position property. The x-component is expressed in
   * percent of the background area's width, and the y-component in percent of
   * the background area's height.
   */
  public BackgroundLayer positionPct(double left, double top) {
    return position(BackgroundPosition.pct(left, top));
  }

  private BackgroundLayer position(BackgroundPosition position) {
    BackgroundLayer result = new BackgroundLayer(this);
    result.position = position;
    return result;
  }

  /**
   * Sets the background-size property. The image will be resized before being
   * painted on the background area..
   * @throws IllegalArgumentException if the given CSS code is not a valid
   *     background-size
   * @see <a href="http://www.w3.org/TR/css3-background/#background-size">http://www.w3.org/TR/css3-background/#background-size</a>
   * @see <a href="http://caniuse.com/background-img-opts">Browser support</a>
   */
  @PoorBrowserSupport
  public BackgroundLayer size(String cssCode) {
    return size(BackgroundSize.from(cssCode));
  }

  /**
   * Sets the background-size property. The new width and the new height are
   * expressed in pixels.
   * @see <a href="http://caniuse.com/background-img-opts">Browser support</a>
   */
  @PoorBrowserSupport
  public BackgroundLayer sizePx(int width, int height) {
    return size(BackgroundSize.px(width, height));
  }

  /**
   * Sets the background-size property. The new width is expressed in pixels,
   * and the new height in percent of the background area's height.
   * @see <a href="http://caniuse.com/background-img-opts">Browser support</a>
   */
  @PoorBrowserSupport
  public BackgroundLayer sizePxPct(int width, double height) {
    return size(BackgroundSize.pxPct(width, height));
  }

  /**
   * Sets the background-size property. The new width is expressed in percent of
   * the background area's width, and the new height in pixels.
   * @see <a href="http://caniuse.com/background-img-opts">Browser support</a>
   */
  @PoorBrowserSupport
  public BackgroundLayer sizePctPx(double width, int height) {
    return size(BackgroundSize.pctPx(width, height));
  }

  /**
   * Sets the background-size property. The new width is expressed in percent of
   * the background area's width, and the new height in percent of the
   * background area's height.
   * @see <a href="http://caniuse.com/background-img-opts">Browser support</a>
   */
  @PoorBrowserSupport
  public BackgroundLayer sizePct(double width, double height) {
    return size(BackgroundSize.pct(width, height));
  }

  private BackgroundLayer size(BackgroundSize size) {
    BackgroundLayer result = new BackgroundLayer(this);
    result.size = size;
    return result;
  }

  /**
   * Sets the background-attachment property to 'fixed'. The background is fixed
   * with regard to the viewport. By default, the background is fixed with
   * regard to the element itself and does not scroll with its contents.
   */
  public BackgroundLayer fixed() {
    BackgroundLayer result = new BackgroundLayer(this);
    result.attachment = "fixed";
    return result;
  }

  /**
   * Sets the background-attachment property to 'local'. The background is fixed
   * with regard to the element's contents: if the element has a scrolling
   * mechanism, the background scrolls with the element's contents. In this
   * case, the background behind the element's border (if any) scrolls as well,
   * even though the border itself does not scroll with the contents.
   * @see <a href="http://caniuse.com/background-img-opts">Browser support</a>
   */
  @PoorBrowserSupport
  public BackgroundLayer local() {
    BackgroundLayer result = new BackgroundLayer(this);
    result.attachment = "local";
    return result;
  }

  /**
   * Sets the background-clip property to 'padding-box'. The background is
   * painted within (clipped to) the border box.
   * @see <a href="http://www.w3.org/TR/css3-background/#background-clip">http://www.w3.org/TR/css3-background/#background-clip</a>
   * @see <a href="http://caniuse.com/background-img-opts">Browser support</a>
   */
  @PoorBrowserSupport
  public BackgroundLayer paddingBoxClip() {
    BackgroundLayer result = new BackgroundLayer(this);
    result.clip = "padding-box";
    return result;
  }

  /**
   * Sets the background-clip property to 'content-box'. The background is
   * painted within (clipped to) the content box.
   * @see <a href="http://www.w3.org/TR/css3-background/#background-clip">http://www.w3.org/TR/css3-background/#background-clip</a>
   * @see <a href="http://caniuse.com/background-img-opts">Browser support</a>
   */
  @PoorBrowserSupport
  public BackgroundLayer contentBoxClip() {
    BackgroundLayer result = new BackgroundLayer(this);
    result.clip = "content-box";
    return result;
  }

  /**
   * Sets the background-origin property to 'border-box'. The background
   * position is relative to the border box.
   * @see <a href="http://www.w3.org/TR/css3-background/#background-origin">http://www.w3.org/TR/css3-background/#background-origin</a>
   * @see <a href="http://caniuse.com/background-img-opts">Browser support</a>
   */
  @PoorBrowserSupport
  public BackgroundLayer borderBoxOrigin() {
    BackgroundLayer result = new BackgroundLayer(this);
    result.origin = "border-box";
    return result;
  }

  /**
   * Sets the background-origin property to 'content-box'. The background
   * position is relative to the content box.
   * @see <a href="http://www.w3.org/TR/css3-background/#background-origin">http://www.w3.org/TR/css3-background/#background-origin</a>
   * @see <a href="http://caniuse.com/background-img-opts">Browser support</a>
   */
  @PoorBrowserSupport
  public BackgroundLayer contentBoxOrigin() {
    BackgroundLayer result = new BackgroundLayer(this);
    result.origin = "content-box";
    return result;
  }

  Image getImage() {
    return image;
  }

  boolean getFillX() {
    return fillX && size == BackgroundSize.AUTO;
  }

  boolean getFillY() {
    return fillY && size == BackgroundSize.AUTO;
  }

  BackgroundPosition getPosition() {
    return position;
  }

  @Override void appendTo(StringBuilder out, CssGenerationContext context) {
    String url = context.getImageUrl(image);
    out.append(url);
    if (repeatX.equals(repeatY)) {
      if (!repeatX.equals("repeat")) {
        out.append(' ');
        out.append(repeatX);
      }
    } else {
      // Special cases repeat-x and repeat-y
      if (repeatX.equals("repeat") && repeatY.equals("no-repeat")) {
        out.append(' ');
        out.append("repeat-x");
      } else if (repeatX.equals("no-repeat") && repeatY.equals("repeat")) {
        out.append(' ');
        out.append("repeat-y");
      } else {
        out.append(' ');
        out.append(repeatX);
        out.append(' ');
        out.append(repeatY);
      }
    }
    BackgroundPosition actualPos = position;
    if (actualPos.equals(BackgroundPosition.LEFT_TOP)) {
      actualPos = context.getBackgroundPosition(image);
    }
    if (!actualPos.equals(BackgroundPosition.LEFT_TOP) ||
        !size.equals(BackgroundSize.AUTO)) {
      out.append(' ');
      out.append(actualPos);
      if (!size.equals(BackgroundSize.AUTO)) {
        out.append('/');
        out.append(size);
      }
    }
    if (!attachment.equals("scroll")) {
      out.append(' ');
      out.append(attachment);
    }
    if (!origin.equals("padding-box") || !clip.equals("border-box")) {
      out.append(' ');
      out.append(origin);
      if (!clip.equals(origin)) {
        out.append(' ');
        out.append(clip);
      }
    }
  }

  @Override public boolean equals(Object object) {
    if (object instanceof BackgroundLayer) {
      BackgroundLayer that = (BackgroundLayer) object;
      return image.equals(that.image) &&
          repeatX.equals(that.repeatX) &&
          repeatY.equals(that.repeatY) &&
          fillX == that.fillX &&
          fillY == that.fillY &&
          position.equals(that.position) &&
          size.equals(that.size) &&
          attachment.equals(that.attachment) &&
          origin.equals(that.origin) &&
          clip.equals(that.clip);
    }
    return false;
  }

  @Override public int hashCode() {
    return Objects.hashCode(image, repeatX, repeatY, fillX, fillY, position,
        size, attachment, origin, clip);
  }
}
