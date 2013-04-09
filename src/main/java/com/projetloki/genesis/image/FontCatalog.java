package com.projetloki.genesis.image;

import java.io.IOException;

/**
 * A catalog of free and open source fonts, by
 * <a target="_blank" href="http://www.theleagueofmoveabletype.com">The League of Moveable Type</a>.
 *
 * @author Clément Roux
 */
public final class FontCatalog {
  /**
   * <img src="../../../../resources/genesis/blackout.png"/>
   * @see <a href="http://www.theleagueofmoveabletype.com/blackout">http://www.theleagueofmoveabletype.com/blackout</a>
   */
  public static Font blackout() {
    return get("Blackout_Midnight.ttf");
  }

  /**
   * <img src="../../../../resources/genesis/chunk.png"/>
   * @see <a href="http://www.theleagueofmoveabletype.com/chunk">http://www.theleagueofmoveabletype.com/chunk</a>
   */
  public static Font chunk() {
    return get("Chunk.ttf");
  }

  /**
   * <img src="../../../../resources/genesis/fanwood.png"/>
   * @see <a href="http://www.theleagueofmoveabletype.com/fanwood">http://www.theleagueofmoveabletype.com/fanwood</a>
   */
  public static Font fanwood() {
    return get("Fanwood.otf");
  }

  /**
   * <img src="../../../../resources/genesis/fanwoodItalic.png"/>
   * @see <a href="http://www.theleagueofmoveabletype.com/fanwood">http://www.theleagueofmoveabletype.com/fanwood</a>
   */
  public static Font fanwoodItalic() {
    return get("Fanwood_Italic.otf");
  }

  /**
   * <img src="../../../../resources/genesis/fanwoodTextItalic.png"/>
   * @see <a href="http://www.theleagueofmoveabletype.com/fanwood">http://www.theleagueofmoveabletype.com/fanwood</a>
   */
  public static Font fanwoodTextItalic() {
    return get("Fanwood_Text_Italic.otf");
  }

  /**
   * <img src="../../../../resources/genesis/fanwoodText.png"/>
   * @see <a href="http://www.theleagueofmoveabletype.com/fanwood">http://www.theleagueofmoveabletype.com/fanwood</a>
   */
  public static Font fanwoodText() {
    return get("Fanwood_Text.otf");
  }

  /**
   * <img src="../../../../resources/genesis/goudyBookletter1991.png"/>
   * @see <a href="http://www.theleagueofmoveabletype.com/goudy-bookletter-1911">http://www.theleagueofmoveabletype.com/goudy-bookletter-1911</a>
   */
  public static Font goudyBookletter1991() {
    return get("GoudyBookletter1911.otf");
  }

  /**
   * <img src="../../../../resources/genesis/junction.png"/>
   * @see <a href="http://www.theleagueofmoveabletype.com/junction">http://www.theleagueofmoveabletype.com/junction</a>
   */
  public static Font junction() {
    return get("Junction.otf");
  }

  /**
   * <img src="../../../../resources/genesis/knewave.png"/>
   * @see <a href="http://www.theleagueofmoveabletype.com/knewave">http://www.theleagueofmoveabletype.com/knewave</a>
   */
  public static Font knewave() {
    return get("knewave.ttf");
  }

  /**
   * <img src="../../../../resources/genesis/knewaveOutline.png"/>
   * @see <a href="http://www.theleagueofmoveabletype.com/knewave">http://www.theleagueofmoveabletype.com/knewave</a>
   */
  public static Font knewaveOutline() {
    return get("knewave-outline.ttf");
  }

  /**
   * <img src="../../../../resources/genesis/leagueGothic.png"/>
   * @see <a href="http://www.theleagueofmoveabletype.com/league-gothic">http://www.theleagueofmoveabletype.com/league-gothic</a>
   */
  public static Font leagueGothic() {
    return get("League_Gothic.otf");
  }

  /**
   * <img src="../../../../resources/genesis/leagueScriptNumberOne.png"/>
   * @see <a href="http://www.theleagueofmoveabletype.com/league-script-number-one">http://www.theleagueofmoveabletype.com/league-script-number-one</a>
   */
  public static Font leagueScriptNumberOne() {
    return get("LeagueScriptNumberOne.otf");
  }

  /**
   * <img src="../../../../resources/genesis/lindenHill.png"/>
   * @see <a href="http://www.theleagueofmoveabletype.com/linden-hill">http://www.theleagueofmoveabletype.com/linden-hill</a>
   */
  public static Font lindenHill() {
    return get("Linden_Hill.otf");
  }

  /**
   * <img src="../../../../resources/genesis/lindenHillItalic.png"/>
   * @see <a href="http://www.theleagueofmoveabletype.com/linden-hill">http://www.theleagueofmoveabletype.com/linden-hill</a>
   */
  public static Font lindenHillItalic() {
    return get("Linden_Hill_Italic.otf");
  }

  /**
   * <img src="../../../../resources/genesis/orbitron.png"/>
   * @see <a href="http://www.theleagueofmoveabletype.com/orbitron">http://www.theleagueofmoveabletype.com/orbitron</a>
   */
  public static Font orbitron() {
    return get("Orbitron_Medium.otf");
  }

  /**
   * <img src="../../../../resources/genesis/oribitronBlack.png"/>
   * @see <a href="http://www.theleagueofmoveabletype.com/orbitron">http://www.theleagueofmoveabletype.com/orbitron</a>
   */
  public static Font oribitronBlack() {
    return get("Orbitron_Black.otf");
  }

  /**
   * <img src="../../../../resources/genesis/orbitronBold.png"/>
   * @see <a href="http://www.theleagueofmoveabletype.com/orbitron">http://www.theleagueofmoveabletype.com/orbitron</a>
   */
  public static Font orbitronBold() {
    return get("Orbitron_Bold.otf");
  }

  /**
   * <img src="../../../../resources/genesis/oribitronLight.png"/>
   * @see <a href="http://www.theleagueofmoveabletype.com/orbitron">http://www.theleagueofmoveabletype.com/orbitron</a>
   */
  public static Font oribitronLight() {
    return get("Orbitron_Light.otf");
  }

  /**
   * <img src="../../../../resources/genesis/ostrichSans.png"/>
   * @see <a href="http://www.theleagueofmoveabletype.com/ostrich-sans">http://www.theleagueofmoveabletype.com/ostrich-sans</a>
   */
  public static Font ostrichSans() {
    return get("Ostrich_Regular.ttf");
  }

  /**
   * <img src="../../../../resources/genesis/ostrichSansBlack.png"/>
   * @see <a href="http://www.theleagueofmoveabletype.com/ostrich-sans">http://www.theleagueofmoveabletype.com/ostrich-sans</a>
   */
  public static Font ostrichSansBlack() {
    return get("Ostrich_Black.ttf");
  }

  /**
   * <img src="../../../../resources/genesis/ostrichSansBold.png"/>
   * @see <a href="http://www.theleagueofmoveabletype.com/ostrich-sans">http://www.theleagueofmoveabletype.com/ostrich-sans</a>
   */
  public static Font ostrichSansBold() {
    return get("Ostrich_Bold.ttf");
  }

  /**
   * <img src="../../../../resources/genesis/ostrichSansDashed.png"/>
   * @see <a href="http://www.theleagueofmoveabletype.com/ostrich-sans">http://www.theleagueofmoveabletype.com/ostrich-sans</a>
   */
  public static Font ostrichSansDashed() {
    return get("Ostrich_Dashed.ttf");
  }

  /**
   * <img src="../../../../resources/genesis/ostrichSansLight.png"/>
   * @see <a href="http://www.theleagueofmoveabletype.com/ostrich-sans">http://www.theleagueofmoveabletype.com/ostrich-sans</a>
   */
  public static Font ostrichSansLight() {
    return get("Ostrich_Light.ttf");
  }

  /**
   * <img src="../../../../resources/genesis/ostrichSansRounded.png"/>
   * @see <a href="http://www.theleagueofmoveabletype.com/ostrich-sans">http://www.theleagueofmoveabletype.com/ostrich-sans</a>
   */
  public static Font ostrichSansRounded() {
    return get("Ostrich_Rounded.ttf");
  }

  /**
   * <img src="../../../../resources/genesis/prociono.png"/>
   * @see <a href="http://www.theleagueofmoveabletype.com/prociono">http://www.theleagueofmoveabletype.com/prociono</a>
   */
  public static Font prociono() {
    return get("Prociono.otf");
  }

  /**
   * <img src="../../../../resources/genesis/raleway.png"/>
   * @see <a href="http://www.theleagueofmoveabletype.com/raleway">http://www.theleagueofmoveabletype.com/raleway</a>
   */
  public static Font raleway() {
    return get("Raleway_Thin.otf");
  }

  /**
   * <img src="../../../../resources/genesis/sniglet.png"/>
   * @see <a href="http://www.theleagueofmoveabletype.com/sniglet">http://www.theleagueofmoveabletype.com/sniglet</a>
   */
  public static Font sniglet() {
    return get("Sniglet_Regular.otf");
  }

  /**
   * <img src="../../../../resources/genesis/sortsMillGoudy.png"/>
   * @see <a href="http://www.theleagueofmoveabletype.com/sorts-mill-goudy">http://www.theleagueofmoveabletype.com/sorts-mill-goudy</a>
   */
  public static Font sortsMillGoudy() {
    return get("OFLGoudyStM.otf");
  }

  /**
   * <img src="../../../../resources/genesis/sortsMillGoudyItalic.png"/>
   * @see <a href="http://www.theleagueofmoveabletype.com/sorts-mill-goudy">http://www.theleagueofmoveabletype.com/sorts-mill-goudy</a>
   */
  public static Font sortsMillGoudyItalic() {
    return get("OFLGoudyStM-Italic.otf");
  }

  // To prevent instantiation
  private FontCatalog() {}

  private static Font get(String resourceName) {
    try {
      return Font.load(FontCatalog.class.getResource(resourceName));
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
}
