package com.projetloki.genesis;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * A library for generating CSS from Java (Tutorial). Simplifies the process
 * of writing and maintaining a medium or large style sheet.
 *
 * <div style="background:url('../../../resources/genesis/genesis-logo.png');width:460px;height:160px;margin:-10px 0 10px 0;">
 * <h1 style="visibility:hidden;">Genesis</h1>
 * </div>
 *
 * <p>
 * All documentation is kept in Javadocs because it guarantees consistency
 * between what's on the web and what's in the source code. Also, it makes
 * possible to access documentation straight from the IDE even if you work
 * offline.</p>
 *
 * <div id="contents">
 * <h2>Contents</h2>
 * <p>
 * <a href="#Modularity">Modularity</a><br/>
 * <a href="#Background">Background image generation</a><br/>
 * <a href="#Variables">Variables and expressions</a><br/>
 * <a href="#Type">Type safety</a><br/>
 * <a href="#Theming">Theming</a><br/>
 * <a href="#Sprites">Sprites</a><br/>
 * <a href="#CSS3">CSS3 and cross-browser compatibility</a><br/>
 * <a href="#Compiling">Compiling modules</a>
 * </p>
 * </div>
 *
 * <!---------------------------------- MODULARITY ---------------------------->
 * <h3><a name="Modularity">Modularity</a></h3>
 *
 * <p>
 * When a generic widget is intended to be used in different projects, it
 * usually comes with a piece of CSS that the user is expected to link to, or
 * copy into the main style sheet (<a target="_blank"
 * href="https://code.google.com/p/jpicker/source/browse/trunk/css/jPicker-1.1.6.css">example</a>).
 * This method has some disadvantages:
 * <ul>
 * <li>Switching to a newer version of the widget library is not guaranteed to
 * go smoothly.<br/>
 * Since the HTML structure of the widget may change between two versions of the
 * library, the user needs to update the CSS as well. All the manual
 * modifications that have been applied to the CSS (change the value of a
 * property, the location of a background-image) need to be made again.</li>
 * <li>Making changes to the style is not easy.<br/>
 * It is done by directly modifying the CSS that is provided.
 * Changing a high-level property, such as a color or a dimension, can be tricky
 * and require to change several CSS properties, and possibly re-generate images.
 * The user has to read the CSS, understand clearly what effect every CSS
 * property has on the widget, make changes to the CSS, and then test these
 * changes, if possible in several browsers.</li>
 * <li>The main style sheet can get very big as the number of widgets increases.
 * <br/>
 * It becomes harder to maintain. Imports solve this problem, but they increase
 * the number of RPCs the browser has to make so it takes more time for the page
 * to load.</li>
 * <li>Background images need to be managed manually.<br/>
 * They must be put in a local directory, URLs in the CSS referring to them must
 * be updated accordingly. When the CSS changes, images that are no longer used
 * need to be removed. For better performance, images that are not repeated can
 * be merged into
 * <a href="http://en.wikipedia.org/wiki/Sprite_(computer_graphics)#Sprites_by_CSS">sprites</a>.
 * The amount of work this requires can get significant as the number of widgets
 * increases.
 * </li>
 * </ul>
 * </p>
 *
 * <p>
 * Genesis offers a solution to these issues, by introducing the concept of CSS
 * {@linkplain CssModule module}. A module is a Java object with a
 * {@link CssModule#configure(CssBuilder)} method that builds a part of the
 * final CSS code. Example:
 * <pre><code class="prettyprint"> static final CssModule MY_BUTTON_CSS_MODULE = new CssModule() {
 *   &#064;Override public void configure(CssBuilder out) {
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
 *     out.addRule("div.myBox", Properties.builder()
 *         .set(FloatValue.LEFT)
 *         .setWidthPct(50));
 *   }
 * };</code></pre>
 * This generates the CSS:
 * <pre><code class="prettyprint lang-css"> .myButton {
 *   width: 80px;
 *   height: 20px;
 *   background: #babdb6;
 *   cursor: pointer:
 *   -webkit-border-radius: 2px;
 *   -moz-border-radius: 2px;
 *   border-radius: 2px;
 * }
 *
 * .myButton:hover {
 *   color: #d3d7cf;
 *   font-weight: bold;
 * }
 *
 * div.myBox {
 *   float: left;
 *   width: 50%;
 * }</code></pre>
 * Widget developers expose a CSS module instead of a piece of CSS. Users only
 * have to add a dependency to this module to have the piece of CSS inserted
 * into the main style sheet. Let's see how this solves the issues described
 * above:
 * <ul>
 * <li>Switching to a newer version of the widget library is as easy as can be.
 * </li>
 * <li>Making changes to the style is done by passing parameters to the
 * constructor of the CSS module. See <a href="#Theming">Theming</a>.</li>
 * <li>Style rules are partitioned into modules rather than being aggregated
 * into a single file.</li>
 * <li>Genesis manages background images and other resources for you.</li>
 * </ul>
 * </p>
 *
 * <!---------------------------------- BACKGROUND IMAGE GENERATION ----------->
 * <h3><a name="Background">Background image generation</a></h3>
 *
 * <p>
 * Genesis comes will a small library that allows you to quickly generate
 * background images without using Photoshop or GIMP. See the examples below.
 * <pre><code class="prettyprint"> // Linear gradient
 * Color from = Color.of(114, 159, 207);
 * Color to = Color.of(52, 101, 164);
 * Image im = Images.canvas(40, 5, from)
 *     .fill(to, Gradients.linear(Direction.RIGHT));</code></pre>
 * Produces this image - <img src="../../../resources/genesis/genesis-example-1.png" style="vertical-align:top;"/>
 * <pre><code class="prettyprint"> // Square button
 * Image im = Images.canvas(25, 25, Color.ALUMINIUM_1)
 *     .fill(Color.ALUMINIUM_2, Gradients.circular(0.5, 1))
 *     .mask(Shapers.roundedBox(2), new Border(1, Color.ALUMINIUM_3));</code></pre>
 * Produces this image - <img src="../../../resources/genesis/genesis-example-2.png" style="vertical-align:top;"/>
 * <pre><code class="prettyprint"> // Diagonal stripes
 * Image im = Images.canvas(40, 40, Color.CHOCOLATE_1)
 *     .mask(Shapes.diagonalStripes(20, 20, 0.5, true),
 *         new Border(1, Color.CHOCOLATE_3))
 *     .frame(Color.CHOCOLATE_2);</code></pre>
 * Produces this image - <img src="../../../resources/genesis/genesis-example-3.png" style="vertical-align:top;"/>
 * <pre><code class="prettyprint"> // Comic book balloon tail
 * Image im = Images.canvas(40, 40, Color.PLUM_1)
 *     .mask(Shapers.tail(10, 30, 20, 20).pad(0, 0, 4, 0),
 *         new Border(1, Color.PLUM_2))
 *     .frame(Color.PLUM_3.mix(Color.WHITE, 0.5));</code></pre>
 * Produces this image - <img src="../../../resources/genesis/genesis-example-4.png" style="vertical-align:top;"/>
 * </p>
 *
 * <p>This library can save you a lot of time because it is often required to
 * re-generate background images during the web design process, with slightly
 * different dimensions or colors. Going through these iterations with Photoshop
 * can be very laborious. Vector graphics editors such as Inkscape make it
 * easier but it takes a lot of time and motivation to master them.
 * </p>
 *
 * <p>Using generated images in the CSS is very simple:
 * <pre><code class="prettyprint"> &#064;Override public void configure(CssBuilder out) {
 *   int width = 25;
 *   int height = 25;
 *
 *   Image im = Images.canvas(width, height, Color.ALUMINIUM_1)
 *       .fill(Color.ALUMINIUM_2, Gradients.circular(0.5, 1))
 *       .mask(Shapers.roundedBox(2), new Border(1, Color.ALUMINIUM_3));
 *
 *   out.addRule(".squareButton", Properties.builder()
 *       .setWidthPx(width)
 *       .setHeightPx(height)
 *       .setBackground(BackgroundLayer.of(im));
 * }</code></pre></p>
 *
 * <!---------------------------------- VARIABLES AND EXPRESSIONS ------------->
 * <h3><a name="Variables">Variables and expressions</a></h3>
 *
 * <p>CSS does not support variables (or named constants) and expressions,
 * although this is a feature many developers request. The
 * <a href="http://www.w3.org/People/Bos/CSS-variables">justification</a> is
 * absolutely understandable but the fact remains that in many situations it
 * would make the life of CSS writers better. Several languages extending CSS
 * have been created to solve this issue:
 * <a href="http://en.wikipedia.org/wiki/Less_css">LESS</a>,
 * <a target="_blank" href="https://github.com/tomyeh/ZUSS">ZUSS</a>,
 * <a href="http://en.wikipedia.org/wiki/Sass_(stylesheet_language)">Saas</a>.
 * </p>
 *
 * <p>
 * With Genesis you write Java code, so you can take advantage of all the
 * features of the Java language.
 * Below are examples of things you can easily do, and the implementation:
 * <ul>
 * <li>Set the width of an element to the sum of the widths of other elements.
 * </li>
 * <li>Set the dimensions of an element to the dimensions of its background
 * image.</li>
 * <li>Set the border color of an element to a darker version of its background
 * color.</li>
 * <li>Set the background image of an element separating two elements, to a
 * linear gradient from the background color of the element on the left to the
 * background color of the element on the right.</li>
 * </ul>
 * <pre><code class="prettyprint"> // Constants
 * static final int LEFT_WIDTH       = 120;
 * static final int MIDDLE_WIDTH     = 10;
 * static final int RIGHT_WIDTH      = 220;
 *
 * static final Color LEFT_BG_COLOR  = Color.BLUE;
 * static final Color RIGHT_BG_COLOR = Color.SILVER;
 *
 * static final Color ICON_BG_COLOR  = Color.ALUMINIUM_1;
 *
 * &#064;Override public void configure(CssBuilder out) {
 *   out.addRule(".left", Properties.builder()
 *       .set(FloatValue.LEFT)
 *       .setWidthPx(LEFT_WIDTH)
 *       .setBackground(LEFT_BG_COLOR));
 *
 *   out.addRule(".right", Properties.builder()
 *       .set(FloatValue.LEFT)
 *       .setWidthPx(RIGHT_WIDTH)
 *       .setBackground(RIGHT_BG_COLOR));
 *
 *   // Separates left and right
 *   out.addRule(".middle", Properties.builder()
 *       .setWidthPx(MIDDLE_WIDTH));
 *
 *   Image grad = Images.canvas(MIDDLE_WIDTH, 4, LEFT_BG_COLOR)
 *       .fill(RIGHT_BG_COLOR, Gradients.linear(Direction.RIGHT));
 *
 *   out.addRule(".container", Properties.builder()
 *       .setWidthPx(LEFT_WIDTH + MIDDLE_WIDTH + RIGHT_WIDTH)
 *       .setBackground(BackgroundLayer.of(grad)));
 *
 *   Image icon = createIconImage();
 *
 *   out.addRule(".icon", Properties.builder()
 *       .setWidthPx(icon.width())
 *       .setHeightPx(icon.height())
 *       .setBackground(BackgroundLayer.of(icon), ICON_BG_COLOR)
 *       .setBorder(1, ICON_BG_COLOR.mix(Color.BLACK, 0.4),
 *           BorderStyleValue.SOLID));
 * }</code></pre>
 * </p>
 *
 * <!---------------------------------- TYPE SAFETY --------------------------->
 * <h3><a name="Type">Type safety</a></h3>
 *
 * <p>
 * One advantage of generating CSS code from Java is that Java is a compiled
 * language with static typing. It removes the risk of making typos in the CSS,
 * setting properties that don't exist, or setting properties to illegal values,
 * as those errors will be detected at compile-time. For example:
 * <pre><code class="prettyprint"> // COMPILE-TIME ERROR
 * // Should be FontStyle and not TextStyle
 * Properties.builder().set(TextStyle.ITALIC);
 *
 * // COMPILE-TIME ERROR
 * // An int is required
 * Properties.builder().setBorderWidthPx("solid");
 *
 * // RUNTIME ERROR
 * // Padding can't be negative
 * // Not as good as a compile-time error, but better than no error
 * Properties.builder().setPaddingPx(-2);</code></pre>
 * </p>
 *
 * <!---------------------------------- THEMING ------------------------------->
 * <h3><a name="Theming">Theming</a></h3>
 *
 * <p>
 * With Genesis, it is the widget developer who defines the high-level style
 * properties the user can set, and not the user who hacks into the CSS to
 * replace some targeted CSS property values.
 * High-level properties are parameters, of any type, that are passed to a
 * module through setters or through a class constructor.
 * They will be converted to CSS rules in the
 * {@link CssModule#configure(CssBuilder)} method although the user does not
 * have to know how. For example:
 * <pre><code class="prettyprint"> // CSS module for an online code editor with syntactic highlighting
 * public class CodeEditorCssModule implements CssModule {
 *   // High-level style properties
 *   private final Map&lt;Section, Color&gt; sectionToColor;
 *   private Color backgroundColor;
 *
 *   public CodeEditorCssModule() {
 *     sectionToColor = Maps.newEnumMap(Section.class);
 *     // Default style
 *     sectionToColor.put(Section.COMMENT, Color.RED);
 *     sectionToColor.put(Section.KEYWORD, Color.BLUE);
 *     sectionToColor.put(Section.NUMBER, Color.GREEN);
 *     sectionToColor.put(Section.STRING_LITERAL, Color.GREEN);
 *     backgroundColor = Color.WHITE;
 *   }
 *
 *   public void setSectionColor(Section section, Color color) {
 *     sectionToColor.put(section, color);
 *   }
 *
 *   public void setBackgroundColor(BackgroundColor backgroundColor) {
 *     this.backgroundColor = backgroundColor;
 *   }
 *
 *   &#064;Override public void configure(CssBuilder out) {
 *     out.put(".codeEditor", Properties.builder()
 *         .setBackground(backgroundColor));
 *     for (Section section : Section.values()) {
 *       String selector = "." + section.className;
 *       Color color = sectionToColor.get(section);
 *       out.put(selector, Properties.builder()
 *           .setColor(color));
 *       // Invert the background color and the text color when the
 *       // 'invert' class name is on
 *       out.put(selector + ".invert", Properties.builder()
 *           .setBackground(color)
 *           .setColor(backgroundColor));
 *     }
 *   }
 *
 *   public enum Section {
 *     COMMENT("comment"),
 *     KEYWORD("keyword"),
 *     NUMBER("number"),
 *     STRING_LITERAL("string-literal");
 *     final String className;
 *
 *     Section(String className) {
 *       this.className = className;
 *     }
 *   }
 * }</code></pre>
 * See {@link CssModule} for how to enable multiple themes for different
 * instances of a widget appearing in a page.
 * </p>
 *
 * <!---------------------------------- SPRITES ------------------------------->
 * <h3><a name="Sprites">Sprites</a></h3>
 *
 * <p>
 * To reduce the number of requests the browser makes to the server, a technique
 * consists of combining numerous small images or icons into a larger image
 * called a sprite. The background-position CSS property is used to select the
 * part of the composite image to display at different points in the page. If a
 * page has ten 1 kB images, they can be combined into one 10 kB image,
 * downloaded with a single HTTP request, and then positioned with CSS. Reducing
 * the number of HTTP requests can make a Web page load much faster.</p>
 *
 * <p>
 * With Genesis, the generation of sprites is automatic and transparent.
 * The only thing you have to do is call {@link BackgroundLayer#fill()}. This
 * method informs Genesis that the background-image entirely fills the
 * background painting area, and thus, that it can be put into a sprite. It is a
 * required condition because if the image does not fill the painting area but
 * is put into a sprite, other images from the sprite may appear unexpectedly
 * next to it.</p>
 *
 * <p>
 * Spriting example:
 * <pre><code class="prettyprint"> &#064;Override public void configure(CssBuilder out) throws IOException {
 *   // Load icons from Java resources
 *   Image cutIcon = Images.load(getClass().getResource("cut-32x32.png"));
 *   Image copyIcon = Images.load(getClass().getResource("copy-32x32.png"));
 *   Image pasteIcon = Images.load(getClass().getResource("paste-32x32.png"));
 *
 *   out.addRule(".cutButton", Properties.builder()
 *       .setBackground(BackgroundLayer.of(cutIcon).fill()));
 *   out.addRule(".copyButton", Properties.builder()
 *       .setBackground(BackgroundLayer.of(copyIcon).fill()));
 *   out.addRule(".pasteButton", Properties.builder()
 *       .setBackground(BackgroundLayer.of(pasteIcon).fill()));
 * }</code></pre>
 * The generated CSS is:
 * <pre><code class="prettyprint lang-css"> .cutButton {
 *   background: url('w7rdyh4tmi.png') no-repeat;
 * }
 *
 * .copyButton {
 *   background: url('w7rdyh4tmi.png') no-repeat -32px 0;
 * }
 *
 * .pasteButton{
 *   background: url('w7rdyh4tmi.png') no-repeat -64px 0;
 * }</code></pre>
 * And the unique image file referred to in it is - <img src="../../../resources/genesis/genesis-sprite.png" style="vertical-align:top;"/>
 * </p>
 *
 * <!---------------------------------- SPRITES ------------------------------->
 * <h3><a name="CSS3">CSS3 support and cross-browser compatibility</a></h3>
 *
 * <p>
 * CSS3 is a standard in construction, but Genesis has setters for the
 * most commonly-accepted CSS3 properties. This includes animations,
 * transitions, colons, border radiuses, border shadows and geometric
 * transformations (rotate, scale, etc.). Note that it is always possible to set
 * a property with the non-typesafe
 * {@linkplain Properties.Builder#set(String, String) setter(String, String)},
 * so you will never be blocked because Genesis does not <em>support</em> a
 * property yet.</p>
 *
 * <p>
 * As of 2013, many CSS3 properties are supported by main browsers through what
 * is called vendor-specific properties. Each browser that is concerned requires
 * a particular prefix to be inserted before the name of the property. Genesis
 * takes care of setting vendor-specific properties for you, so you don't have
 * to care about it. For example,
 * <code class="prettyprint">setTransform(TranformFunction.rotateDeg(7))</code>
 * translates into:
 * <pre><code class="prettyprint css-lang"> transform:rotate(7deg);
 * -ms-transform:rotate(7deg);
 * -moz-transform:rotate(7deg);
 * -webkit-transform:rotate(7deg);
 * -o-transform:rotate(7deg);</code></pre></p>
 *
 * <p>
 * Besides CSS3 properties, Genesis supports CSS3 grammar, the at-rules
 * keyframes and font-face, CSS3 selectors, and media features. See a complete
 * example below:
 * <pre><code class="prettyprint"> static final FontFace MY_GENTIUM = FontFace.forLocalSource("myGentium")
 *     .sourceAdded("../Gentium.ttf")
 *     .withFamilyName("myGentium");
 *
 * static final Keyframes KEYFRAMES = Keyframes.from("top: 0;left; 0")
 *     .through(30, "top: 50%")
 *     .through(68, Properties.builder().setLeftPct(50))
 *     .to("top: 100%;left:100%");
 *
 * &#064;Override public void configure(CssBuilder out) {
 *   out.use(MY_GENTIUM);
 *   out.use(KEYFRAMES);
 *
 *   out.addRule("#header", Properties.builder()
 *       .setTransform(TransformFunction.rotateDeg(7))
 *       .setBorderRadiusPx(5)
 *       .setAnimation(Animation.on(KEYFRAMES.name())
 *           .durationSeconds(12)
 *           .timingFunction(TimingFunction.EASE_OUT)));
 *
 *   MediaQuery screenQuery = MediaQuery.SCREEN;
 *   MediaQuery tvQuery = MediaQuery.TV
 *       .and(MediaFeature.progressiveScan())
 *       .and(MediaFeature.minWidth(1024, LengthUnit.PX));
 *
 *   out.addRule(
 *       ":not(#footer)",
 *       Properties.builder().setFontFamily("my-gentium"),
 *       screenQuery.or(tvQuery.not()));
 * }</code></pre></p>
 *
 * <!---------------------------------- COMPILING MODULES --------------------->
 * <h3><a name="Compiling">Compiling modules</a></h3>
 *
 * <p>
 * Use the {@linkplain Genesis} class to compile a set of CSS modules into a
 * style sheet. There are two possible ways to do so. You can create an instance
 * of Genesis with a builder, for example during the construction of the server
 * or a servlet:
 * <pre><code class="prettyprint"> Genesis genesis = Genesis.builder()
 *     .install(MY_CSS_MODULE)
 *     .install(new DatePickerCssModule())
 *     .setImageFolderUri("/media")
 *     .build();</code></pre>
 *
 * You can also execute the main method of Genesis from the command line, as an
 * offline process. The parameters are:
 * <dl>
 * <dt>--entry_class</dt>
 * <dd>Class that declares a single public or package-private static field
 * assignable to CssModule. Required.
 * <br/>Note: the fact that the entry class must declare only one module is not
 * not an issue since modules can have dependencies. When a module is installed,
 * all its dependencies are installed as well.
 * </dd>
 * <dt>--image_folder</dt>
 * <dd>Path to a local directory in which background images are generated. If
 * not set, background images are not generated.</dd>
 * <dt>--image_folder_url</dt>
 * <dd>URI of the image directory. Absolute or relative to the  URI of the CSS
 * or the document containing the CSS code. <.> by default.</dd>
 * <dt>--out</dt>
 * <dd>Path to the output CSS file. Stdout by default.</dd>
 * </dl>
 * Example:
 * <pre><code class="prettyprint lang-css">Genesis \
 *   --entry_class       my.java.package.Foobar \
 *   --image_folder      /home/john/mysite/img \
 *   --image_folder_url  img \
 *   --out               /home/john/mysite/style.css</code></pre>
 * </p>
 *
 * <!-- Enable syntactic highlighting -->
 * <link href="../../../resources/prettify.css" type="text/css" rel="stylesheet"/>
 * <!-- <link href="../../../resources/sunburst.css" type="text/css" rel="stylesheet"/> -->
 * <script type="text/javascript" src="../../../resources/prettify.js"></script>
 * <script type="text/javascript" src="../../../resources/lang-css.js"></script>
 * <script type="text/javascript">prettyPrint();</script>
 * <!-- End -->
 *
 * @author Clément Roux
 */
public final class Genesis {
  private final ImmutableMap<CssModule, Rules> moduleToRules;
  private final SpriteManager spriteManager;
  // Caches the result of #getCss()
  private volatile String cssCache;

  Genesis(String imageFolderUri, Map<CssModule, Rules> moduleToRules) {
    this.moduleToRules = ImmutableMap.copyOf(moduleToRules);
    // Collect images that need to be generated
    SpriteManagerImpl.Builder builder =
        SpriteManagerImpl.builder(imageFolderUri);
    for (Rules rules : moduleToRules.values()) {
      rules.registerImages(builder);
    }
    spriteManager = builder.build();
  }

  public static void main(String[] args)
      throws ClassNotFoundException, IOException {
    GenesisMain.doMain(args);
  }

  /**
   * Writes all background images used in the CSS to the given folder.
   *
   * <p>
   * It is safe for several threads and even processes to write images to the
   * same folder at the same time. If the images to write are already present in
   * the folder, this method figures it out and returns very fast. Feel free to
   * call this method during the initialization of a servlet.</p>
   * @throws IllegalArgumentException if the given file is not a folder
   */
  public void writeImages(File folder) throws IOException {
    checkArgument(folder.isDirectory() || folder.mkdir(),
        "not a directory: %s", folder);
    spriteManager.writeSprites(folder);
  }

  /**
   * Writes the CSS to the given file.
   * @throws IllegalArgumentException if there is a conflict between rules
   *     defined in two installed modules; see {@link CssModule}
   */
  public void writeCssFile(File out) throws IOException {
    // So we know it's cached
    getCss();
    OutputStream os = new FileOutputStream(out);
    try {
      Writer writer = new OutputStreamWriter(os, Charsets.UTF_8);
      try {
        writer.write("@charset \'UTF-8\';\n");
        writer.write(getCss());
      } finally {
        writer.close();
      }
    } finally {
      os.close();
    }
  }

  /**
   * Returns the generated CSS as a string.
   *
   * <p>
   * A servlet can call this method and insert the CSS into a &lt;style&gt;
   * element. Compared to linking to an external style sheet, this is one less
   * RPC. Note that if the document is a XHTML, the CSS needs to be escaped.</p>
   * @throws IllegalArgumentException if there is a conflict between rules
   *     defined in two installed modules; see {@link CssModule}
   */
  public String getCss() {
    return getCssPart(moduleToRules.keySet());
  }

  /**
   * Returns the part of the {@linkplain #getCss() CSS} that gets generated from
   * the given installed modules and their dependencies.
   *
   * <p>
   * A servlet can call this method and insert the CSS into a &lt;style&gt;
   * element. Compared to linking to an external style sheet, this is one less
   * RPC. Note that if the document is a XHTML, the CSS needs to be escaped.</p>
   * @throws IllegalArgumentException if one of the module has not been
   *     {@linkplain Builder#install(CssModule) installed}
   */
  public String getCssPart(Iterable<? extends CssModule> installedModules) {
    return getCssPart(ImmutableSet.copyOf(installedModules));
  }

  /**
   * Returns the part of the {@linkplain #getCss() CSS} that gets generated from
   * the given installed modules and their dependencies.
   *
   * <p>
   * A servlet can call this method and insert the CSS into a &lt;style&gt;
   * element. Compared to linking to an external style sheet, this is one less
   * RPC. Note that if the document is a XHTML, the CSS needs to be escaped.</p>
   * @throws IllegalArgumentException if one of the module has not been
   *     {@linkplain Builder#install(CssModule) installed}
   */
  public String getCssPart(CssModule... installedModules) {
    return getCssPart(ImmutableSet.copyOf(installedModules));
  }

  private String getCssPart(Set<CssModule> modules) {
    // Whether modules is the largest set of modules possible
    boolean all = false;
    String localCssCache = cssCache;
    if (moduleToRules.keySet().equals(modules)) {
      if (localCssCache != null) {
        return localCssCache;
      }
      all = true;
    }
    for (CssModule module : modules) {
      checkArgument(moduleToRules.containsKey(module),
          "module %s is not installed", module);
    }
    modules = flattenModules(modules);
    if (!all && moduleToRules.keySet().equals(modules)) {
      if (localCssCache != null) {
        return localCssCache;
      }
      all = true;
    }
    // Check that there is conflict between rules
    PropertyConflictFinder conflictFinder = new PropertyConflictFinder();
    for (CssModule module : modules) {
      Rules rules = moduleToRules.get(module);
      conflictFinder.add(rules);
    }
    StringBuilder builder = new StringBuilder();
    // Imports must be first
    for (CssImport cssImport : getAllImports(modules)) {
      cssImport.appendTo(builder);
      builder.append('\n');
    }
    // Then font-faces
    for (FontFace fontFace : getAllFontFaces(modules)) {
      fontFace.appendTo(builder);
      builder.append("\n");
    }
    // And then animations
    for (Keyframes keyframes : getAllAnimations(modules)) {
      keyframes.appendTo(builder);
    }
    for (CssModule module : modules) {
      Rules rules = moduleToRules.get(module);
      rules.appendTo(builder, spriteManager);
    }
    String result = builder.toString();
    if (all) {
      cssCache = result;
    }
    return result;
  }

  /**
   * Flattens the given set of modules. For example, if A depends on B and C,
   * and B depends on D, then flattening (A, C) returns (A, B, C, D).
   */
  private Set<CssModule> flattenModules(Set<CssModule> input) {
    LinkedList<CssModule> toFlatten = Lists.newLinkedList(input);
    Set<CssModule> result = Sets.newLinkedHashSet();
    while (!toFlatten.isEmpty()) {
      CssModule first = toFlatten.pop();
      if (result.add(first)) {
        Rules rules = moduleToRules.get(first);
        toFlatten.addAll(rules.dependencies);
      }
    }
    return result;
  }

  private Set<CssImport> getAllImports(Set<CssModule> modules) {
    if (modules.isEmpty()) {
      return ImmutableSet.of();
    } else if (modules.size() == 1) {
      Rules rules = moduleToRules.get(modules.iterator().next());
      return rules.imports;
    }
    Set<CssImport> result = Sets.newLinkedHashSet();
    for (CssModule module : modules) {
      Rules rules = moduleToRules.get(module);
      result.addAll(rules.imports);
    }
    return result;
  }

  private Collection<FontFace> getAllFontFaces(Set<CssModule> modules) {
    Map<String, FontFace> nameToFontFace = Maps.newLinkedHashMap();
    for (CssModule module : modules) {
      Rules rules = moduleToRules.get(module);
      for (FontFace fontFace : rules.fontFaces) {
        FontFace oldValue = nameToFontFace.put(fontFace.familyName(), fontFace);
        checkArgument(oldValue == null || oldValue.equals(fontFace),
            "font-face name conflict: %s and %s",
            oldValue, fontFace.familyName());
      }
    }
    return nameToFontFace.values();
  }

  private Collection<Keyframes> getAllAnimations(Set<CssModule> modules) {
    Map<String, Keyframes> nameToKeyframes = Maps.newLinkedHashMap();
    for (CssModule module : modules) {
      Rules rules = moduleToRules.get(module);
      for (Keyframes keyframes : rules.animations) {
        Keyframes oldValue = nameToKeyframes.put(keyframes.name(), keyframes);
        checkArgument(oldValue == null || oldValue.equals(keyframes),
            "keyframes name conflict: %s and %s", oldValue, keyframes.name());
      }
    }
    return nameToKeyframes.values();
  }

  /** Returns a new builder for creating Genesis instances. */
  public static Builder builder() {
    return new Builder();
  }

  /** A builder for creating Genesis instances. */
  public static final class Builder {
    private final Map<CssModule, Rules> moduleToRules = Maps.newLinkedHashMap();
    private String imageFolderUri = "";

    Builder() {}

    /**
     * Installs the given module and all its dependencies. If the module has
     * already been installed, this method does nothing.
     *
     * <p>
     * If the {@linkplain CssModule#configure(CssBuilder) configure} method of
     * the module throws an exception, it is wrapped into a
     * {@link CssModuleInstallException}.
     */
    public Builder install(CssModule module) {
      if (moduleToRules.containsKey(module)) {
        // Already installed
        return this;
      }
      CssBuilderImpl cssBuilder = new CssBuilderImpl(module);
      try {
        module.configure(cssBuilder);
      } catch (Exception ex) {
        String message = "error while installing module " + module;
        throw new CssModuleInstallException(message, ex);
      }
      Rules rules = cssBuilder.build();
      moduleToRules.put(module, rules);
      // Install all the dependencies if they are not already installed
      for (CssModule dependency : rules.dependencies) {
        install(dependency);
      }
      return this;
    }

    /**
     * Sets the URI of the folder containing the images generated by Genesis.
     * It can be relative or absolute. Relative URIs are relative to the CSS
     * file, not to the HTML document that links to the stylesheet. If you
     * choose to inject the stylesheet into HTML pages instead of linking to a
     * separate file, you will have to use an absolute URI unless all HTML
     * pages are in the same directory.
     *
     * <p>
     * By default, the URI is "", which means that images are expected to be in
     * the same directory as the CSS file.
     *
     * <p>
     * Examples:
     * <blockquote><pre><code> Genesis.Builder builder = Genesis.builder();
     *
     * // 1) Images are expected to be in the same directory as the CSS file.
     * // This is default.
     * builder.setImageFolderUri("");
     *
     * // 2) Relative URI
     * builder.setImageFolderUri("../img");
     *
     * // 3) Absolute URI
     * builder.setImageFolderUri("/media/img");
     *
     * // 4) Another example of absolute URI
     * builder.setImageFolderUrl("http://media.mywebsite.com/");
     * </code></pre></blockquote>
     * @return this builder for chaining
     * @throws IllegalArgumentException if the given URI is not a valid URI
     */
    public Builder setImageFolderUri(String imageFolderUri) {
      if (!imageFolderUri.isEmpty() && !imageFolderUri.endsWith("/")) {
        imageFolderUri = imageFolderUri + "/";
      }
      // Just to verify that the URI is valid
      URI.create(imageFolderUri);
      this.imageFolderUri = imageFolderUri;
      return this;
    }

    /** Returns a newly-created Genesis. */
    public Genesis build() {
      return new Genesis(imageFolderUri, moduleToRules);
    }
  }
}
