package com.projetloki.genesis;

/**
 * A background-repeat.
 *
 * @author Clément Roux
 */
enum BackgroundRepeat {
  REPEAT("repeat", true, true),
  REPEAT_X("repeat-x", true, false),
  REPEAT_Y("repeat-y", false, true),
  NO_REPEAT("no-repeat", false, false);
  private final String cssCode;
  private final boolean repeatsX;
  private final boolean repeatsY;

  private BackgroundRepeat(String cssCode,
      boolean repeatsX, boolean repeatsY) {
    this.cssCode = cssCode;
    this.repeatsX = repeatsX;
    this.repeatsY = repeatsY;
  }

  static BackgroundRepeat from(boolean repeatsX, boolean repeatsY) {
    if (repeatsX) {
      return repeatsY ? REPEAT : REPEAT_X;
    }
    return repeatsY ? REPEAT_Y : NO_REPEAT;
  }

  boolean repeatsX() {
    return repeatsX;
  }

  boolean repeatsY() {
    return repeatsY;
  }

  BackgroundRepeat or(BackgroundRepeat other) {
    return from(repeatsX || other.repeatsX, repeatsY || other.repeatsY);
  }

  @Override public String toString() {
    return cssCode;
  }
}
