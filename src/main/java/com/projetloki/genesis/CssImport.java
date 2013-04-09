package com.projetloki.genesis;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

/**
 * A CSS import rule, with a media condition.
 *
 * @author Clément Roux
 */
final class CssImport extends AppendableToNoContext {
  // The non-quoted non-escaped URI
  final String uri;
  // The media condition on which to do the import
  // MediaQuery.ALL by default
  final MediaCondition condition;

  CssImport(String uri, MediaCondition condition) {
    Util.checkUri(uri);
    this.uri = uri;
    this.condition = checkNotNull(condition);
  }

  @Override void appendTo(StringBuilder out) {
    out.append("@import url('");
    out.append(Format.escape(uri));
    out.append("')");
    if (condition != MediaQuery.ALL) {
      out.append(' ');
      condition.appendTo(out);
    }
    out.append(';');
  }

  @Override public boolean equals(Object object) {
    if (object == this) {
      return true;
    } else if (object instanceof CssImport) {
      CssImport that = (CssImport) object;
      return uri.equals(that.uri) && condition.equals(that.condition);
    }
    return false;
  }

  @Override public int hashCode() {
    return Objects.hashCode(uri, condition);
  }
}
