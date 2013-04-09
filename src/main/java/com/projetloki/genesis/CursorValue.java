package com.projetloki.genesis;

/**
 * Enum for the cursor property.
 * Specifies the mouse cursor displayed when the mouse pointer is over an
 * element.
 *
 * @see <a target="_blank" href="https://developer.mozilla.org/en-US/docs/CSS/cursor">MDN</a>
 *
 * @author Clément Roux
 */
public enum CursorValue {
  /**
   * The browser determines the cursor to display based on the current context.
   */
  AUTO("auto"),
  /** Cross cursor, often used to indicate selection in a bitmap. */
  CROSSHAIR("CROSSHAIR"),
  /** Default cursor, typically an arrow. */
  DEFAULT("DEFAULT"),
  /**
   * Indicate that some edge is to be moved. For example, the 'se-resize' cursor
   * is used when the movement starts from the south-east corner of the box.
   */
  E_RESIZE("e-resize"),
  /** Indicates help is available. */
  HELP("help"),
  /** The hovered object may be moved. */
  MOVE("move"),
  /**
   * Indicate that some edge is to be moved. For example, the 'se-resize' cursor
   * is used when the movement starts from the south-east corner of the box.
   */
  N_RESIZE("n-resize"),
  /**
   * Indicate that some edge is to be moved. For example, the 'se-resize' cursor
   * is used when the movement starts from the south-east corner of the box.
   */
  NE_RESIZE("ne-resize"),
  /**
   * Indicate that some edge is to be moved. For example, the 'se-resize' cursor
   * is used when the movement starts from the south-east corner of the box.
   */
  NW_RESIZE("nw-resize"),
  /** The cursor is a pointer that indicates a link. */
  POINTER("pointer"),
  /**
   * The program is busy in the background but the user can still interact with
   * the interface (unlike for wait).
   */
  PROGRESS("progress"),
  /**
   * Indicate that some edge is to be moved. For example, the 'se-resize' cursor
   * is used when the movement starts from the south-east corner of the box.
   */
  S_RESIZE("s-resize"),
  /**
   * Indicate that some edge is to be moved. For example, the 'se-resize' cursor
   * is used when the movement starts from the south-east corner of the box.
   */
  SE_RESIZE("se-resize"),
  /**
   * Indicate that some edge is to be moved. For example, the 'se-resize' cursor
   * is used when the movement starts from the south-east corner of the box.
   */
  SW_RESIZE("sw-resize"),
  /** Indicates text that may be selected. Often rendered as an I-beam. */
  TEXT("text"),
  /**
   * Indicate that some edge is to be moved. For example, the 'se-resize' cursor
   * is used when the movement starts from the south-east corner of the box.
   */
  W_RESIZE("w-resize"),
  /**
   * Indicates that the program is busy and the user should wait. Often
   * rendered as a watch or hourglass.
   */
  WAIT("wait");
  // No INHERIT in case we want to add url support
  final String css;

  private CursorValue(String css) {
    this.css = css;
  }

  @Override public String toString() {
    return css;
  }
}
