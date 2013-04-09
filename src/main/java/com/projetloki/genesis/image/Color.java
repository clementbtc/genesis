package com.projetloki.genesis.image;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.Serializable;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

/**
 * An immutable color in the red/green/blue color space. Does not hold any
 * transparency value.
 *
 * @author Clément Roux
 */
public final class Color implements Serializable {
  /**
   * <img src="../../../../resources/genesis/ACQUA.png"/> The color with
   * hexadecimal value 0FF.
   *
   * <p>One of the sixteen named colors defined in the HTML 4.01 specification.
   * </p>
   */
  public static final Color ACQUA = new Color(0, 255, 255);

  /**
   * <img src="../../../../resources/genesis/BLACK.png"/> The color with
   * hexadecimal value 000.
   *
   * <p>One of the sixteen named colors defined in the HTML 4.01 specification.
   * </p>
   */
  public static final Color BLACK = new Color(0, 0, 0);

  /**
   * <img src="../../../../resources/genesis/BLUE.png"/> The color with
   * hexadecimal value 00F.
   *
   * <p>One of the sixteen named colors defined in the HTML 4.01 specification.
   * </p>
   */
  public static final Color BLUE = new Color(0, 0, 255);

  /**
   * <img src="../../../../resources/genesis/FUCHSIA.png"/> The color
   * with hexadecimal value F0F.
   *
   * <p>One of the sixteen named colors defined in the HTML 4.01 specification.
   * </p>
   */
  public static final Color FUCHSIA = new Color(255, 0, 255);

  /**
   * <img src="../../../../resources/genesis/GRAY.png"/> The color with
   * hexadecimal value 808080.
   *
   * <p>One of the sixteen named colors defined in the HTML 4.01 specification.
   * </p>
   */
  public static final Color GRAY = new Color(128, 128, 128);

  /**
   * <img src="../../../../resources/genesis/GREEN.png"/> The color with
   * hexadecimal value 008000.
   *
   * <p>One of the sixteen named colors defined in the HTML 4.01 specification.
   * </p>
   */
  public static final Color GREEN = new Color(0, 128, 0);

  /**
   * <img src="../../../../resources/genesis/LIME.png"/> The color with
   * hexadecimal value 0F0.
   *
   * <p>One of the sixteen named colors defined in the HTML 4.01 specification.
   * </p>
   */
  public static final Color LIME = new Color(0, 255, 0);

  /**
   * <img src="../../../../resources/genesis/MAROON.png"/> The color with
   * hexadecimal value 800000.
   *
   * <p>One of the sixteen named colors defined in the HTML 4.01 specification.
   * </p>
   */
  public static final Color MAROON = new Color(128, 0, 0);

  /**
   * <img src="../../../../resources/genesis/NAVY.png"/> The color with
   * hexadecimal value 000080.
   *
   * <p>One of the sixteen named colors defined in the HTML 4.01 specification.
   * </p>
   */
  public static final Color NAVY = new Color(0, 0, 128);

  /**
   * <img src="../../../../resources/genesis/OLIVE.png"/> The color with
   * hexadecimal value 808000.
   *
   * <p>One of the sixteen named colors defined in the HTML 4.01 specification.
   * </p>
   */
  public static final Color OLIVE = new Color(128, 128, 0);

  /**
   * <img src="../../../../resources/genesis/PURPLE.png"/> The color with
   * hexadecimal value 800080.
   *
   * <p>One of the sixteen named colors defined in the HTML 4.01 specification.
   * </p>
   */
  public static final Color PURPLE = new Color(128, 0, 128);

  /**
   * <img src="../../../../resources/genesis/RED.png"/> The color with
   * hexadecimal value F00.
   *
   * <p>One of the sixteen named colors defined in the HTML 4.01 specification.
   * </p>
   */
  public static final Color RED = new Color(255, 0, 0);

  /**
   * <img src="../../../../resources/genesis/SILVER.png"/> The color with
   * hexadecimal value C0C0C0.
   *
   * <p>One of the sixteen named colors defined in the HTML 4.01 specification.
   * </p>
   */
  public static final Color SILVER = new Color(192, 192, 192);

  /**
   * <img src="../../../../resources/genesis/TEAL.png"/> The color with
   * hexadecimal value 008080.
   *
   * <p>One of the sixteen named colors defined in the HTML 4.01 specification.
   * </p>
   */
  public static final Color TEAL = new Color(0, 128, 128);

  /**
   * <img src="../../../../resources/genesis/WHITE.png"/> The color with
   * hexadecimal value FFF.
   *
   * <p>One of the sixteen named colors defined in the HTML 4.01 specification.
   * </p>
   */
  public static final Color WHITE = new Color(255, 255, 255);

  /**
   * <img src="../../../../resources/genesis/YELLOW.png"/> The color with
   * hexadecimal value FF0.
   *
   * <p>One of the sixteen named colors defined in the HTML 4.01 specification.
   * </p>
   */
  public static final Color YELLOW = new Color(255, 255, 0);

  /**
   * <img src="../../../../resources/genesis/ALUMINIUM_1.png"/> The color
   * with hexadecimal value EEEEEC.
   *
   * <p>From the palette of 27 colors provided by the Tango Desktop Project to
   * ensure a consistent look across Tango icons.</p>
   * @see <a href="http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines">http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines</a>
   */
  public static final Color ALUMINIUM_1 = new Color(238, 238, 236);

  /**
   * <img src="../../../../resources/genesis/ALUMINIUM_2.png"/> The color
   * with hexadecimal value D3D7CF.
   *
   * <p>From the palette of 27 colors provided by the Tango Desktop Project to
   * ensure a consistent look across Tango icons.</p>
   * @see <a href="http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines">http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines</a>
   */
  public static final Color ALUMINIUM_2 = new Color(211, 215, 207);

  /**
   * <img src="../../../../resources/genesis/ALUMINIUM_3.png"/> The color
   * with hexadecimal value BABDB6.
   *
   * <p>From the palette of 27 colors provided by the Tango Desktop Project to
   * ensure a consistent look across Tango icons.</p>
   * @see <a href="http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines">http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines</a>
   */
  public static final Color ALUMINIUM_3 = new Color(186, 189, 182);

  /**
   * <img src="../../../../resources/genesis/ALUMINIUM_4.png"/> The color
   * with hexadecimal value 888A85.
   *
   * <p>From the palette of 27 colors provided by the Tango Desktop Project to
   * ensure a consistent look across Tango icons.</p>
   * @see <a href="http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines">http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines</a>
   */
  public static final Color ALUMINIUM_4 = new Color(136, 138, 133);

  /**
   * <img src="../../../../resources/genesis/ALUMINIUM_5.png"/> The color
   * with hexadecimal value 555753.
   *
   * <p>From the palette of 27 colors provided by the Tango Desktop Project to
   * ensure a consistent look across Tango icons.</p>
   * @see <a href="http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines">http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines</a>
   */
  public static final Color ALUMINIUM_5 = new Color(85, 87, 83);

  /**
   * <img src="../../../../resources/genesis/ALUMINIUM_6.png"/> The color
   * with hexadecimal value 2E3436.
   *
   * <p>From the palette of 27 colors provided by the Tango Desktop Project to
   * ensure a consistent look across Tango icons.</p>
   * @see <a href="http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines">http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines</a>
   */
  public static final Color ALUMINIUM_6 = new Color(46, 52, 54);

  /**
   * <img src="../../../../resources/genesis/BUTTER_1.png"/> The color
   * with hexadecimal value FCE94F.
   *
   * <p>From the palette of 27 colors provided by the Tango Desktop Project to
   * ensure a consistent look across Tango icons.</p>
   * @see <a href="http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines">http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines</a>
   */
  public static final Color BUTTER_1 = new Color(252, 234, 79);

  /**
   * <img src="../../../../resources/genesis/BUTTER_2.png"/> The color
   * with hexadecimal value EDD400.
   *
   * <p>From the palette of 27 colors provided by the Tango Desktop Project to
   * ensure a consistent look across Tango icons.</p>
   * @see <a href="http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines">http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines</a>
   */
  public static final Color BUTTER_2 = new Color(237, 212, 0);

  /**
   * <img src="../../../../resources/genesis/BUTTER_3.png"/> The color
   * with hexadecimal value C4A000.
   *
   * <p>From the palette of 27 colors provided by the Tango Desktop Project to
   * ensure a consistent look across Tango icons.</p>
   * @see <a href="http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines">http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines</a>
   */
  public static final Color BUTTER_3 = new Color(196, 160, 0);

  /**
   * <img src="../../../../resources/genesis/CHAMELEON_1.png"/> The color
   * with hexadecimal value 8AE234.
   *
   * <p>From the palette of 27 colors provided by the Tango Desktop Project to
   * ensure a consistent look across Tango icons.</p>
   * @see <a href="http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines">http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines</a>
   */
  public static final Color CHAMELEON_1 = new Color(138, 226, 52);

  /**
   * <img src="../../../../resources/genesis/CHAMELEON_2.png"/> The color
   * with hexadecimal value 73D216.
   *
   * <p>From the palette of 27 colors provided by the Tango Desktop Project to
   * ensure a consistent look across Tango icons.</p>
   * @see <a href="http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines">http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines</a>
   */
  public static final Color CHAMELEON_2 = new Color(115, 210, 22);

  /**
   * <img src="../../../../resources/genesis/CHAMELEON_3.png"/> The color
   * with hexadecimal value 4E9A06.
   *
   * <p>From the palette of 27 colors provided by the Tango Desktop Project to
   * ensure a consistent look across Tango icons.</p>
   * @see <a href="http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines">http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines</a>
   */
  public static final Color CHAMELEON_3 = new Color(78, 154, 6);

  /**
   * <img src="../../../../resources/genesis/CHOCOLATE_1.png"/> The color
   * with hexadecimal value E9B96E.
   *
   * <p>From the palette of 27 colors provided by the Tango Desktop Project to
   * ensure a consistent look across Tango icons.</p>
   * @see <a href="http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines">http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines</a>
   */
  public static final Color CHOCOLATE_1 = new Color(233, 185, 110);

  /**
   * <img src="../../../../resources/genesis/CHOCOLATE_2.png"/> The color
   * with hexadecimal value C17D11.
   *
   * <p>From the palette of 27 colors provided by the Tango Desktop Project to
   * ensure a consistent look across Tango icons.</p>
   * @see <a href="http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines">http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines</a>
   */
  public static final Color CHOCOLATE_2 = new Color(193, 125, 17);

  /**
   * <img src="../../../../resources/genesis/CHOCOLATE_3.png"/> The color
   * with hexadecimal value 8F5902.
   *
   * <p>From the palette of 27 colors provided by the Tango Desktop Project to
   * ensure a consistent look across Tango icons.</p>
   * @see <a href="http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines">http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines</a>
   */
  public static final Color CHOCOLATE_3 = new Color(143, 89, 2);

  /**
   * <img src="../../../../resources/genesis/ORANGE_1.png"/> The color
   * with hexadecimal value FCAF35.
   *
   * <p>From the palette of 27 colors provided by the Tango Desktop Project to
   * ensure a consistent look across Tango icons.</p>
   * @see <a href="http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines">http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines</a>
   */
  public static final Color ORANGE_1 = new Color(252, 175, 62);

  /**
   * <img src="../../../../resources/genesis/ORANGE_2.png"/> The color
   * with hexadecimal value F57900.
   *
   * <p>From the palette of 27 colors provided by the Tango Desktop Project to
   * ensure a consistent look across Tango icons.</p>
   * @see <a href="http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines">http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines</a>
   */
  public static final Color ORANGE_2 = new Color(245, 121, 0);

  /**
   * <img src="../../../../resources/genesis/ORANGE_3.png"/> The color
   * with hexadecimal value CE5C00.
   *
   * <p>From the palette of 27 colors provided by the Tango Desktop Project to
   * ensure a consistent look across Tango icons.</p>
   * @see <a href="http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines">http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines</a>
   */
  public static final Color ORANGE_3 = new Color(206, 92, 0);

  /**
   * <img src="../../../../resources/genesis/PLUM_1.png"/> The color with
   * hexadecimal value AD7FA8.
   *
   * <p>From the palette of 27 colors provided by the Tango Desktop Project to
   * ensure a consistent look across Tango icons.</p>
   * @see <a href="http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines">http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines</a>
   */
  public static final Color PLUM_1 = new Color(173, 127, 168);

  /**
   * <img src="../../../../resources/genesis/PLUM_2.png"/> The color with
   * hexadecimal value 75507B.
   *
   * <p>From the palette of 27 colors provided by the Tango Desktop Project to
   * ensure a consistent look across Tango icons.</p>
   * @see <a href="http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines">http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines</a>
   */
  public static final Color PLUM_2 = new Color(117, 80, 123);

  /**
   * <img src="../../../../resources/genesis/PLUM_3.png"/> The color with
   * hexadecimal value 5C3566.
   *
   * <p>From the palette of 27 colors provided by the Tango Desktop Project to
   * ensure a consistent look across Tango icons.</p>
   * @see <a href="http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines">http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines</a>
   */
  public static final Color PLUM_3 = new Color(92, 53, 102);

  /**
   * <img src="../../../../resources/genesis/SCARLET_RED_1.png"/> The
   * color with hexadecimal value EF2929.
   *
   * <p>From the palette of 27 colors provided by the Tango Desktop Project to
   * ensure a consistent look across Tango icons.</p>
   * @see <a href="http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines">http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines</a>
   */
  public static final Color SCARLET_RED_1 = new Color(239, 41, 41);

  /**
   * <img src="../../../../resources/genesis/SCARLET_RED_2.png"/> The
   * color with hexadecimal value CC0000.
   *
   * <p>From the palette of 27 colors provided by the Tango Desktop Project to
   * ensure a consistent look across Tango icons.</p>
   * @see <a href="http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines">http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines</a>
   */
  public static final Color SCARLET_RED_2 = new Color(204, 0, 0);

  /**
   * <img src="../../../../resources/genesis/SCARLET_RED_3.png"/> The
   * color with hexadecimal value A40000.
   *
   * <p>From the palette of 27 colors provided by the Tango Desktop Project to
   * ensure a consistent look across Tango icons.</p>
   * @see <a href="http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines">http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines</a>
   */
  public static final Color SCARLET_RED_3 = new Color(164, 0, 0);

  /**
   * <img src="../../../../resources/genesis/SKY_BLUE_1.png"/> The color
   * with hexadecimal value 729FCF.
   *
   * <p>From the palette of 27 colors provided by the Tango Desktop Project to
   * ensure a consistent look across Tango icons.</p>
   * @see <a href="http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines">http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines</a>
   */
  public static final Color SKY_BLUE_1 = new Color(114, 159, 207);

  /**
   * <img src="../../../../resources/genesis/SKY_BLUE_2.png"/> The color
   * with hexadecimal value 3465A4.
   *
   * <p>From the palette of 27 colors provided by the Tango Desktop Project to
   * ensure a consistent look across Tango icons.</p>
   * @see <a href="http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines">http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines</a>
   */
  public static final Color SKY_BLUE_2 = new Color(52, 101, 164);

  /**
   * <img src="../../../../resources/genesis/SKY_BLUE_3.png"/> The color
   * with hexadecimal value 204A87.
   *
   * <p>From the palette of 27 colors provided by the Tango Desktop Project to
   * ensure a consistent look across Tango icons.</p>
   * @see <a href="http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines">http://tango.freedesktop.org/Tango_Icon_Theme_Guidelines</a>
   */
  public static final Color SKY_BLUE_3 = new Color(32, 74, 135);

  /**
   * Constructs a color with the given hexadecimal representation. The format is
   * the same as the one used in CSS: rgb or rrggbb.
   * @param hexString the hexadecimal representation of a color: rgb or rrggbb.
   *     Digits must be in [0-9a-fA-F]
   * @return the color whose hexadecimal representation is {@code hexString}
   * @throws IllegalArgumentException if {@code hexString} does not match
   *     rgb or rrggbb
   * @see <a href="http://www.w3.org/TR/CSS21/syndata.html#color-units">http://www.w3.org/TR/CSS21/syndata.html#color-units</a>
   */
  public static Color forHexString(String hexString) {
    int red;
    int green;
    int blue;
    if (hexString.length() == 3) {
      checkArgument(hexString.matches("[0-9a-fA-F]{3}"), hexString);
      red = 17 * Integer.parseInt(hexString.substring(0, 1), 16);
      green = 17 * Integer.parseInt(hexString.substring(1, 2), 16);
      blue = 17 * Integer.parseInt(hexString.substring(2, 3), 16);
    } else if (hexString.length() == 6) {
      checkArgument(hexString.matches("[0-9a-fA-F]{6}"), hexString);
      red = Integer.parseInt(hexString.substring(0, 2), 16);
      green = Integer.parseInt(hexString.substring(2, 4), 16);
      blue = Integer.parseInt(hexString.substring(4, 6), 16);
    } else {
      throw new IllegalArgumentException(hexString);
    }
    return new Color(red, green, blue);
  }

  /**
   * Converts the given Java color to a color. The alpha value of the Java color
   * is ignored.
   * @param javaColor a Java color
   * @return a color that is equivalent to the given Java color
   */
  public static Color forJavaColor(java.awt.Color javaColor) {
    int r = javaColor.getRed();
    int g = javaColor.getGreen();
    int b = javaColor.getBlue();
    return new Color(r, g, b);
  }

  private final int red;
  private final int green;
  private final int blue;

  /**
   * Constructs a color with the specified components.
   * @param red the red component of the color, between 0 and 255 inclusive
   * @param green the green component of the color, between 0 and 255 inclusive
   * @param blue the blue component of the color, between 0 and 255 inclusive
   * @throws IllegalArgumentException if one of the components is not between 0
   *     and 255 inclusive
   */
  public static Color of(int red, int green, int blue) {
    if (red < 0 || red > 255) {
      throw new IllegalArgumentException("red: " + red);
    }
    if (green < 0 || green > 255) {
      throw new IllegalArgumentException("green: " + green);
    }
    if (blue < 0 || blue > 255) {
      throw new IllegalArgumentException("blue: " + blue);
    }
    return new Color(red, green, blue);
  }

  /** Constructor that does not check its arguments. */
  Color(int red, int green, int blue) {
    this.red = red;
    this.green = green;
    this.blue = blue;
  }

  /** Returns the red component of this color, between 0 and 255 inclusive. */
  public int red() {
    return red;
  }

  /** Returns the green component of this color, between 0 and 255 inclusive. */
  public int green() {
    return green;
  }

  /** Returns the blue component of this color, between 0 and 255 inclusive. */
  public int blue() {
    return blue;
  }

  /**
   * Returns the color obtained when mixing {@code this} and {@code that}. The
   * second parameter controls the weight of {@code that} in the mixing. For
   * example, if the weight is 0, the result is {@code this}, and if the weight
   * is 1, the result is {@code that}.
   * <p>This method offers a simple way to enlight or darken a color, e.g.
   * {@code color.mix(Color.BLACK, 0.75)}</p>
   * @param that the color mixed with {@code this}
   * @param thatWeight the weight of {@code that} in the mixing, between 0 and 1
   * @throws IllegalArgumentException if {@code thatWeight} is not between 0 and
   *     1 inclusive
   * @see ColorMixer
   */
  public Color mix(Color that, double thatWeight) {
    checkArgument(0d <= thatWeight && thatWeight <= 1d,
        "thatWeight: %s", thatWeight);
    double complement = 1 - thatWeight;
    double newRed = red() * complement + that.red() * thatWeight;
    double newGreen = green() * complement + that.green() * thatWeight;
    double newBlue = blue() * complement + that.blue() * thatWeight;
    return new Color((int) (newRed + 0.5d), (int) (newGreen + 0.5d),
        (int) (newBlue + 0.5d));
  }

  /**
   * Returns the hexadecimal representation of this color, made of 3 or 6
   * digits: rgb or rrggbb. The format is the same as the one used in CSS.
   * @see <a href="http://www.w3.org/TR/CSS21/syndata.html#color-units">http://www.w3.org/TR/CSS21/syndata.html#color-units</a>
   */
  public String toHexString() {
    List<String> components = Lists.newArrayListWithCapacity(3);
    List<Character> firstDigits = Lists.newArrayListWithCapacity(3);
    for (int component : ImmutableList.of(red, green, blue)) {
      String hex = Integer.toHexString(component);
      if (hex.length() == 1) {
        hex = "0" + hex;
      }
      if (firstDigits != null && component % 17 == 0) {
        firstDigits.add(hex.charAt(0));
      } else {
        firstDigits = null;
      }
      components.add(hex);
    }
    return Joiner.on("").join(firstDigits != null ? firstDigits : components);
  }

  /**
   * Returns a CSS code for this color. A color can have several color codes,
   * for example #F5F5DC and <em>beige</em> are equivalent. It is unspecified
   * which one this method returns.
   */
  public String toCssCode() {
    return "#" + toHexString();
  }

  /**
   * Converts this color to a Java color. The alpha value of the Java color is
   * 1 (fully opaque).
   * @return a Java color that is equivalent to this color
   */
  public java.awt.Color toJavaColor() {
    return new java.awt.Color(red, green, blue);
  }

  @Override public boolean equals(Object object) {
    if (object == this) {
      return true;
    } if (object instanceof Color) {
      Color that = (Color) object;
      return (red == that.red) && (green == that.green) && (blue == that.blue);
    }
    return false;
  }

  @Override public int hashCode() {
    return Objects.hashCode(red, green, blue);
  }

  // Special constants for #toString()
  private static final ImmutableMap<Color, String> CONSTANTS =
      ImmutableMap.<Color, String>builder()
          .put(ACQUA, "ACQUA")
          .put(ALUMINIUM_1, "ALUMINIUM_1")
          .put(ALUMINIUM_2, "ALUMINIUM_2")
          .put(ALUMINIUM_3, "ALUMINIUM_3")
          .put(ALUMINIUM_4, "ALUMINIUM_4")
          .put(ALUMINIUM_5, "ALUMINIUM_5")
          .put(ALUMINIUM_6, "ALUMINIUM_6")
          .put(BLACK, "BLACK")
          .put(BLUE, "BLUE")
          .put(BUTTER_1, "BUTTER_1")
          .put(BUTTER_2, "BUTTER_2")
          .put(BUTTER_3, "BUTTER_3")
          .put(CHAMELEON_1, "CHAMELEON_1")
          .put(CHAMELEON_2, "CHAMELEON_2")
          .put(CHAMELEON_3, "CHAMELEON_3")
          .put(CHOCOLATE_1, "CHOCOLATE_1")
          .put(CHOCOLATE_2, "CHOCOLATE_2")
          .put(CHOCOLATE_3, "CHOCOLATE_3")
          .put(FUCHSIA, "FUCHSIA")
          .put(GRAY, "GRAY")
          .put(GREEN, "GREEN")
          .put(LIME, "LIME")
          .put(MAROON, "MAROON")
          .put(NAVY, "NAVY")
          .put(OLIVE, "OLIVE")
          .put(ORANGE_1, "ORANGE_1")
          .put(ORANGE_2, "ORANGE_2")
          .put(ORANGE_3, "ORANGE_3")
          .put(PLUM_1, "PLUM_1")
          .put(PLUM_2, "PLUM_2")
          .put(PLUM_3, "PLUM_3")
          .put(PURPLE, "PURPLE")
          .put(RED, "RED")
          .put(SCARLET_RED_1, "SCARLET_RED_1")
          .put(SCARLET_RED_2, "SCARLET_RED_2")
          .put(SCARLET_RED_3, "SCARLET_RED_3")
          .put(SILVER, "SILVER")
          .put(SKY_BLUE_1, "SKY_BLUE_1")
          .put(SKY_BLUE_2, "SKY_BLUE_2")
          .put(SKY_BLUE_3, "SKY_BLUE_3")
          .put(TEAL, "TEAL")
          .put(WHITE, "WHITE")
          .put(YELLOW, "YELLOW")
          .build();

  @Override public String toString() {
    if (CONSTANTS.containsKey(this)) {
      return CONSTANTS.get(this);
    }
    return ImmutableMap.of(
        "r", red,
        "g", green,
        "b", blue,
        "x", toHexString()).toString();
  }
  private static final long serialVersionUID = 1L;
}
