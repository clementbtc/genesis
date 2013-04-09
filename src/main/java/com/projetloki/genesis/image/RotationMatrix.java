package com.projetloki.genesis.image;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import com.google.common.primitives.Doubles;

/**
 * A rotation matrix for a given angle.
 *
 * @author Clément Roux
 */
final class RotationMatrix implements Serializable {
  private final double theta;
  private final double a11;
  private final double a12;
  private final double a21;

  public RotationMatrix(double theta) {
    this.theta = theta;
    double cos = Math.cos(theta);
    double sin = Math.sin(theta);
    // Hacks to limit loss of precision
    // On my computer, Math.cos(Math.PI / 2) returns 6.123233995736766E-17
    // instead of 0, but Math.sin(Math.PI / 2) returns 1.0
    if (cos == 1d || cos == -1d) {
      sin = 0d;
    } else if (sin == 1d || sin == -1d) {
      cos = 0d;
    } else if (cos == 0d) {
      sin = (sin < 0d ? -1d : 1d);
    } else if (sin == 0) {
      cos = (cos < 0d ? -1d : 1d);
    }
    a11 = cos;
    a12 = -sin;
    a21 = sin;
  }

  public double theta() {
    return theta;
  }

  public double cos() {
    return a11;
  }

  public double sin() {
    return a21;
  }

  public double a11() {
    return a11;
  }

  public double a12() {
    return a12;
  }

  public double a21() {
    return a21;
  }

  public double a22() {
    return a11;
  }

  @Override public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    } else if (obj instanceof RotationMatrix) {
      RotationMatrix that = (RotationMatrix) obj;
      return theta == that.theta;
    } else {
      return false;
    }
  }

  @Override public int hashCode() {
    return Doubles.hashCode(theta) - 627721344;
  }

  @Override public String toString() {
    return Double.toString(theta);
  }

  private static class SerializationProxy implements Serializable {
    private final double theta;
    SerializationProxy(double theta) {
      this.theta = theta;
    }
    private Object readResolve() {
      return new RotationMatrix(theta);
    }
    private static final long serialVersionUID = 0;
  }

  private Object writeReplace() {
    return new SerializationProxy(theta);
  }

  private void readObject(ObjectInputStream stream)
      throws InvalidObjectException {
    throw new InvalidObjectException("Use SerializationProxy");
  }
}
