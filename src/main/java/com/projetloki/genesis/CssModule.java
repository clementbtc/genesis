package com.projetloki.genesis;

import java.io.IOException;

/**
 * A CSS module is responsible for generating a part of the CSS code.
 * It contains a single method, {@link #configure(CssBuilder)}, where new rules
 * are defined and added to the CSS builder.
 *
 * Example:
 * <pre><code> static final CssModule MY_BUTTON_CSS_MODULE = new CssModule() {
 *   &#064;Override public void configure(CssBuilder out) throws IOException {
 *     out.addRule(".myButton", Properties.builder()
 *         .setWidthPx(80)
 *         .setHeightPx(20)
 *         .setBackground(Color.ALUMINIUM_3)
 *         .set(CursorValue.POINTER)
 *         .setBorderRadiusPx(2));
 *
 *     out.addRule(".myButton:hover", Properties.builder()
 *         .setBackground(Color.ALUMINIUM_2)
 *         .set(FontWeightValue.BOLD));
 *
 *     int iconWidth = 16;
 *     int iconHeight = 16;
 *     Image icon = Images.load(getClass().getResource("my-icon.png"))
 *         .rotateCw()
 *         .scale(iconWidth, iconHeight);
 *     out.addRule(".myButton-icon", Properties.builder()
 *         .setWidthPx(iconWidth)
 *         .setHeightPx(iconHeight)
 *         .setMarginPx(2)
 *         .set(FloatValue.LEFT)
 *         .setBackground(BackgroundLayer.of(icon)));
 *   }
 * };
 * </code></pre>
 *
 * <p>
 * Conflicts between rules defined in different modules are not allowed.
 * There is a conflict if the same (selector, property) pair is found in both
 * sets of rules. For example, if a module defines the rule
 * <code>#head{background: blue}</code>, another can't define the rule
 * <code>#head{background: green}</code>, or an exception will be thrown when
 * generating the CSS.</p>
 *
 * <p>
 * Modules can depend on each other, through the
 * {@link CssBuilder#install(CssModule)} method. When a module is installed, all
 * the modules it depends on are also installed. Cyclic dependencies are
 * allowed.</p>
 *
 * <h5>Theming</h5>
 *
 * <p>
 * When creating a reusable widget, it's important to give users the ability to
 * change some style properties (colors, dimensions) so the widget fits better
 * with the general style of the page.
 * Usually, it's done by asking the user to directly modify the piece of CSS
 * that comes with the widget. This is not a perfect solution because this
 * requires the user to have a clear understanding of what effect every property
 * in the piece of CSS has. Very often, changing a high-level requires to change
 * more than one properties in the CSS.
 * Also, if the HTML structure of the widget changes in a future version, the
 * CSS does not work anymore.</p>
 *
 * <p>
 * CSS modules offer an alternative solution. Since modules are regular Java
 * objects, their constructors can take parameters.
 * <br/>Consider a widget that renders text in a rectangular box, and below the
 * box there is a tail that makes the box looks like a comic strip balloon. The
 * tail is a CSS background image, its color needs to be equal to the widget's
 * background-color property. The module below lets the user specify the color
 * as a parameter.
 * <pre><code> class BalloonCssModule implements CssModule {
 *   final Color backgroundColor;
 *
 *   BalloonCssModule(Color backgroundColor) {
 *     this.backgroundColor = backgroundColor;
 *   }
 *
 *   &#064;Override public void configure(CssBuilder out) throws IOException {
 *     int tailHeight = 20;
 *
 *     // Generate the tail background image
 *     Image tail = Images.canvas(30, tailHeight, backgroundColor)
 *         .mask(Shapers.tail(0, 15, 30, tailHeight));
 *
 *     out.addRule(".balloon", Properties.builder()
 *         .setHeightPct(100)
 *         .setPaddingBottomPx(tailHeight)
 *         .setBackground(BackgroundLayer.of(tail)
 *             .position("4px bottom")));
 *
 *     // The text box inside the balloon
 *     out.addRule(".balloon-box", Properties.builder()
 *         .setHeightPct(100)
 *         .setBackground(backgroundColor));
 *   }
 * }
 * </code></pre>
 * It is the simplest solution, it does the work in most cases, but it has a
 * limitation. What if the user would like to have a gray balloon AND a green
 * balloon on the same page? Fortunately, there is a solution for this.
 * <pre><code> class BalloonCssModule implements CssModule {
 *   // The theme only applies to balloons that have an ancestor
 *   // matched by this selector
 *   final Selector ancestor;
 *   final Color backgroundColor;
 *
 *   static final int tailHeight = 20;
 *
 *   BalloonCssModule(Selector ancestor, Color backgroundColor) {
 *     this.ancestor = ancestor;
 *     this.backgroundColor = backgroundColor;
 *   }
 *
 *   &#064;Override public void configure(CssBuilder out) {
 *     out.install(BASE_MODULE);
 *
 *     // Generate the tail background image
 *     Image tail = Images.canvas(30, tailHeight, backgroundColor)
 *         .mask(Shapers.tail(0, 15, 30, 0));
 *
 *     out.addRule(ancestor.asAncestorOf(".balloon"), Properties.builder()
 *         .setBackground(BackgroundLayer.of(tail)
 *             .position("4px bottom")));
 *
 *     // The text box inside the balloon
 *     out.addRule(ancestor.asAncestorOf(".balloon-box"), Properties.builder()
 *         .setBackground(backgroundColor));
 *   }
 *
 *   // CSS for all balloons regardless of the theme
 *   private static final CssModule BASE_MODULE = new CssModule() {
 *     &#064;Override public void configure(CssBuilder out) {
 *       // These rules can't be added in BalloonCssModule#configure because
 *       // different modules (per the #equals method) can't set the same
 *       // properties, even if the values are identical
 *
 *       // An exception would be thrown if the user installed two distinct
 *       // instances of BalloonCssModule
 *
 *       out.addRule(".balloon", Properties.builder()
 *           .setHeightPct(100)
 *           .setPaddingBottomPx(tailHeight));
 *
 *       // The text box inside the balloon
 *       out.addRule(".balloon-box", Properties.builder()
 *           .setHeightPct(100));
 *     }
 *   };
 * }
 * </code></pre>
 * The user can install two instances of this module, with different background
 * colors and ancestors, to obtain two different styles on the same page.
 * </p>
 *
 * @author Clément Roux
 */
public interface CssModule {
  /**
   * Adds rules to the given builder. See the {@linkplain CssModule top-level}
   * comment for examples.
   *
   * To install a module, you must NOT call this method directly. Instead, call
   * {@link CssBuilder#install(CssModule)}.
   */
  void configure(CssBuilder out) throws IOException;

  /**
   * Indicates whether another object is equal to this CSS module.
   *
   * <p>
   * Only some implementations will have a reason to override the behavior of
   * {@link Object#equals(java.lang.Object)}. Genesis calls this method to check
   * if a module has already been installed. If several modules depend on the
   * same module, but don't share a reference to this module (e.g. call
   * {@code new} every time), then overriding this method and {@code #hashCode}
   * is required to prevent the module from being installed several times.
   * </p>
   */
  @Override
  boolean equals(Object object);
}
