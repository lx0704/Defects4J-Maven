/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.math.ode;

import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.IOException;

/**
 * This class implements an interpolator for the Gragg-Bulirsch-Stoer
 * integrator.

 * <p>This interpolator compute dense output inside the last step
 * produced by a Gragg-Bulirsch-Stoer integrator.</p>

 * <p>
 * This implementation is basically a reimplementation in Java of the
 * <a
 * href="http://www.unige.ch/math/folks/hairer/prog/nonstiff/odex.f">odex</a>
 * fortran code by E. Hairer and G. Wanner. The redistribution policy
 * for this code is available <a
 * href="http://www.unige.ch/~hairer/prog/licence.txt">here</a>, for
 * convenience, it is reproduced below.</p>
 * </p>

 * <table border="0" width="80%" cellpadding="10" align="center" bgcolor="#E0E0E0">
 * <tr><td>Copyright (c) 2004, Ernst Hairer</td></tr>

 * <tr><td>Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 * <ul>
 *  <li>Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.</li>
 *  <li>Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.</li>
 * </ul></td></tr>

 * <tr><td><strong>THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A  PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.</strong></td></tr>
 * </table>

 * @see GraggBulirschStoerIntegrator

 * @version $Id: GraggBulirschStoerStepInterpolator.java 1705 2006-09-17 19:57:39Z luc $
 * @author E. Hairer and G. Wanner (fortran version)

 */

class GraggBulirschStoerStepInterpolator
  extends AbstractStepInterpolator {

  /** Slope at the beginning of the step. */
  private double[] y0Dot;

  /** State at the end of the step. */
  private double[] y1;

  /** Slope at the end of the step. */
  private double[] y1Dot;

  /** Derivatives at the middle of the step.
   * element 0 is state at midpoint, element 1 is first derivative ...
   */
  private double[][] yMidDots;

  /** Interpolation polynoms. */
  private double[][] polynoms;

  /** Error coefficients for the interpolation. */
  private double[] errfac;

  /** Degree of the interpolation polynoms. */
  private int currentDegree;

  /** Reallocate the internal tables.
   * Reallocate the internal tables in order to be able to handle
   * interpolation polynoms up to the given degree
   * @param maxDegree maximal degree to handle
   */
  private void resetTables(int maxDegree) {

    if (maxDegree < 0) {
      polynoms      = null;
      errfac        = null;
      currentDegree = -1;
    } else {

      double[][] newPols = new double[maxDegree + 1][];
      if (polynoms != null) {
        System.arraycopy(polynoms, 0, newPols, 0, polynoms.length);
        for (int i = polynoms.length; i < newPols.length; ++i) {
          newPols[i] = new double[currentState.length];
        }
      } else {
        for (int i = 0; i < newPols.length; ++i) {
          newPols[i] = new double[currentState.length];
        }
      }
      polynoms = newPols;

      // initialize the error factors array for interpolation
      if (maxDegree <= 4) {
        errfac = null;
      } else {
        errfac = new double[maxDegree - 4];
        for (int i = 0; i < errfac.length; ++i) {
          int ip5 = i + 5;
          errfac[i] = 1.0 / (ip5 * ip5);
          double e = 0.5 * Math.sqrt (((double) (i + 1)) / ip5);
          for (int j = 0; j <= i; ++j) {
            errfac[i] *= e / (j + 1);
          }
        }
      }

      currentDegree = 0;

    }

  }

  /** Simple constructor.
   * This constructor should not be used directly, it is only intended
   * for the serialization process.
   */
  public GraggBulirschStoerStepInterpolator() {
    y0Dot    = null;
    y1       = null;
    y1Dot    = null;
    yMidDots = null;
    resetTables(-1);
  }

  /** Simple constructor.
   * @param y reference to the integrator array holding the current state
   * @param y0Dot reference to the integrator array holding the slope
   * at the beginning of the step
   * @param y1 reference to the integrator array holding the state at
   * the end of the step
   * @param y1Dot reference to the integrator array holding the slope
   * at theend of the step
   * @param yMidDots reference to the integrator array holding the
   * derivatives at the middle point of the step
   * @param forward integration direction indicator
   */
  public GraggBulirschStoerStepInterpolator(double[] y, double[] y0Dot,
                                            double[] y1, double[] y1Dot,
                                            double[][] yMidDots,
                                            boolean forward) {

    super(y, forward);
    this.y0Dot    = y0Dot;
    this.y1       = y1;
    this.y1Dot    = y1Dot;
    this.yMidDots = yMidDots;

    resetTables(yMidDots.length + 4);

  }

  /** Copy constructor.
   * @param interpolator interpolator to copy from. The copy is a deep
   * copy: its arrays are separated from the original arrays of the
   * instance
   */
  public GraggBulirschStoerStepInterpolator
    (GraggBulirschStoerStepInterpolator interpolator) {

    super(interpolator);

    int dimension = currentState.length;

    // the interpolator has been finalized,
    // the following arrays are not needed anymore
    y0Dot    = null;
    y1       = null;
    y1Dot    = null;
    yMidDots = null;

    // copy the interpolation polynoms (up to the current degree only)
    if (interpolator.polynoms == null) {
      polynoms = null;
      currentDegree = -1;
    } else {
      resetTables(interpolator.currentDegree);
      for (int i = 0; i < polynoms.length; ++i) {
        polynoms[i] = new double[dimension];
        System.arraycopy(interpolator.polynoms[i], 0,
                         polynoms[i], 0, dimension);
      }
      currentDegree = interpolator.currentDegree;
    }

  }

  /**
   * Clone the instance.
   * the copy is a deep copy: its arrays are separated from the
   * original arrays of the instance
   * @return a copy of the instance
   */
  public Object clone() {
    return new GraggBulirschStoerStepInterpolator(this);
  }

  /** Compute the interpolation coefficients for dense output.
   * @param mu degree of the interpolation polynom
   * @param h current step
   */
  public void computeCoefficients(int mu, double h) {

    if ((polynoms == null) || (polynoms.length <= (mu + 4))) {
      resetTables(mu + 4);
    }

    currentDegree = mu + 4;

    for (int i = 0; i < currentState.length; ++i) {

      double yp0   = h * y0Dot[i];
      double yp1   = h * y1Dot[i];
      double ydiff = y1[i] - currentState[i];
      double aspl  = ydiff - yp1;
      double bspl  = yp0 - ydiff;

      polynoms[0][i] = currentState[i];
      polynoms[1][i] = ydiff;
      polynoms[2][i] = aspl;
      polynoms[3][i] = bspl;

      if (mu < 0) {
        return;
      }

      // compute the remaining coefficients
      double ph0 = 0.5 * (currentState[i] + y1[i]) + 0.125 * (aspl + bspl);
      polynoms[4][i] = 16 * (yMidDots[0][i] - ph0);

      if (mu > 0) {
        double ph1 = ydiff + 0.25 * (aspl - bspl);
        polynoms[5][i] = 16 * (yMidDots[1][i] - ph1);

        if (mu > 1) {
          double ph2 = yp1 - yp0;
          polynoms[6][i] = 16 * (yMidDots[2][i] - ph2 + polynoms[4][i]);

          if (mu > 2) {
            double ph3 = 6 * (bspl - aspl);
            polynoms[7][i] = 16 * (yMidDots[3][i] - ph3 + 3 * polynoms[5][i]);

            for (int j = 4; j <= mu; ++j) {
              double fac1 = 0.5 * j * (j - 1);
              double fac2 = 2 * fac1 * (j - 2) * (j - 3);
              polynoms[j+4][i] = 16 * (yMidDots[j][i]
                                       + fac1 * polynoms[j+2][i]
                                       - fac2 * polynoms[j][i]);
            }

          }
        }
      }
    }

  }

  /** Estimate interpolation error.
   * @param scale scaling array
   * @return estimate of the interpolation error
   */
  public double estimateError(double[] scale) {
    double error = 0;
    if (currentDegree >= 5) {
      for (int i = 0; i < currentState.length; ++i) {
        double e = polynoms[currentDegree][i] / scale[i];
        error += e * e;
      }
      error = Math.sqrt(error / currentState.length) * errfac[currentDegree-5];
    }
    return error;
  }

  /** Compute the state at the interpolated time.
   * This is the main processing method that should be implemented by
   * the derived classes to perform the interpolation.
   * @param theta normalized interpolation abscissa within the step
   * (theta is zero at the previous time step and one at the current time step)
   * @param oneMinusThetaH time gap between the interpolated time and
   * the current time
   * @throws DerivativeException this exception is propagated to the caller if the
   * underlying user function triggers one
   */
  protected void computeInterpolatedState(double theta,
                                          double oneMinusThetaH)
    throws DerivativeException {

    int dimension = currentState.length;

    double oneMinusTheta = 1.0 - theta;
    double theta05       = theta - 0.5;
    double t4            = theta * oneMinusTheta;
    t4 = t4 * t4;

    for (int i = 0; i < dimension; ++i) {
      interpolatedState[i] = polynoms[0][i]
        + theta * (polynoms[1][i]
                   + oneMinusTheta * (polynoms[2][i] * theta
                                      + polynoms[3][i] * oneMinusTheta));

      if (currentDegree > 3) {
        double c = polynoms[currentDegree][i];
        for (int j = currentDegree - 1; j > 3; --j) {
          c = polynoms[j][i] + c * theta05 / (j - 3);
        }
        interpolatedState[i] += t4 * c;
      }
    }

  }
    
  /** Save the state of the instance.
   * @param out stream where to save the state
   * @exception IOException in case of write error
   */
  public void writeExternal(ObjectOutput out)
    throws IOException {

    int dimension = currentState.length;

    // save the state of the base class
    writeBaseExternal(out);

    // save the local attributes (but not the temporary vectors)
    out.writeInt(currentDegree);
    for (int k = 0; k <= currentDegree; ++k) {
      for (int l = 0; l < dimension; ++l) {
        out.writeDouble(polynoms[k][l]);
      }
    }

  }

  /** Read the state of the instance.
   * @param in stream where to read the state from
   * @exception IOException in case of read error
   */
  public void readExternal(ObjectInput in)
    throws IOException {

    // read the base class 
    double t = readBaseExternal(in);
    int dimension = currentState.length;

    // read the local attributes
    int degree = in.readInt();
    resetTables(degree);
    currentDegree = degree;

    for (int k = 0; k <= currentDegree; ++k) {
      for (int l = 0; l < dimension; ++l) {
        polynoms[k][l] = in.readDouble();
      }
    }

    try {
      // we can now set the interpolated time and state
      setInterpolatedTime(t);
    } catch (DerivativeException e) {
      throw new IOException(e.getMessage());
    }

  }

  private static final long serialVersionUID = 7320613236731409847L;

}
