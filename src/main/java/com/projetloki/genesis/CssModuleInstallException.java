package com.projetloki.genesis;

/**
 * The exception thrown when the installation of a {@linkplain CssModule module}
 * fails.
 *
 * @author Clément Roux
 */
public class CssModuleInstallException extends RuntimeException {
  public CssModuleInstallException() {}

  public CssModuleInstallException(String message) {
    super(message);
  }

  public CssModuleInstallException(Throwable cause) {
    super(cause);
  }

  public CssModuleInstallException(String message, Throwable cause) {
    super(message, cause);
  }
  private static final long serialVersionUID = 0;
}
