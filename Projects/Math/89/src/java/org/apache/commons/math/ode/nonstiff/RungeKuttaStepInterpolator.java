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

package org.apache.commons.math.ode.nonstiff;

import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.IOException;

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math.ode.sampling.AbstractStepInterpolator;

/** This class represents an interpolator over the last step during an
 * ODE integration for Runge-Kutta and embedded Runge-Kutta integrators.
 *
 * @see RungeKuttaIntegrator
 * @see EmbeddedRungeKuttaIntegrator
 *
 * @version $Revision$ $Date$
 * @since 1.2
 */

abstract class RungeKuttaStepInterpolator
  extends AbstractStepInterpolator {

  /** Simple constructor.
   * This constructor builds an instance that is not usable yet, the
   * {@link #reinitialize} method should be called before using the
   * instance in order to initialize the internal arrays. This
   * constructor is used only in order to delay the initialization in
   * some cases. The {@link RungeKuttaIntegrator} and {@link
   * EmbeddedRungeKuttaIntegrator} classes use the prototyping design
   * pattern to create the step interpolators by cloning an
   * uninitialized model and latter initializing the copy.
   */
  protected RungeKuttaStepInterpolator() {
    super();
    yDotK     = null;
    equations = null;
  }

  /** Copy constructor.

  * <p>The copied interpolator should have been finalized before the
  * copy, otherwise the copy will not be able to perform correctly any
  * interpolation and will throw a {@link NullPointerException}
  * later. Since we don't want this constructor to throw the
  * exceptions finalization may involve and since we don't want this
  * method to modify the state of the copied interpolator,
  * finalization is <strong>not</strong> done automatically, it
  * remains under user control.</p>

  * <p>The copy is a deep copy: its arrays are separated from the
  * original arrays of the instance.</p>

  * @param interpolator interpolator to copy from.

  */
  public RungeKuttaStepInterpolator(final RungeKuttaStepInterpolator interpolator) {

    super(interpolator);

    if (interpolator.currentState != null) {
      final int dimension = currentState.length;

      yDotK = new double[interpolator.yDotK.length][];
      for (int k = 0; k < interpolator.yDotK.length; ++k) {
        yDotK[k] = new double[dimension];
        System.arraycopy(interpolator.yDotK[k], 0,
                         yDotK[k], 0, dimension);
      }

    } else {
      yDotK = null;
    }

    // we cannot keep any reference to the equations in the copy
    // the interpolator should have been finalized before
    equations = null;

  }

  /** Reinitialize the instance
   * <p>Some Runge-Kutta integrators need fewer functions evaluations
   * than their counterpart step interpolators. So the interpolator
   * should perform the last evaluations they need by themselves. The
   * {@link RungeKuttaIntegrator RungeKuttaIntegrator} and {@link
   * EmbeddedRungeKuttaIntegrator EmbeddedRungeKuttaIntegrator}
   * abstract classes call this method in order to let the step
   * interpolator perform the evaluations it needs. These evaluations
   * will be performed during the call to <code>doFinalize</code> if
   * any, i.e. only if the step handler either calls the {@link
   * AbstractStepInterpolator#finalizeStep finalizeStep} method or the
   * {@link AbstractStepInterpolator#getInterpolatedState
   * getInterpolatedState} method (for an interpolator which needs a
   * finalization) or if it clones the step interpolator.</p>
   * @param equations set of differential equations being integrated
   * @param y reference to the integrator array holding the state at
   * the end of the step
   * @param yDotK reference to the integrator array holding all the
   * intermediate slopes
   * @param forward integration direction indicator
   */
  public void reinitialize(final FirstOrderDifferentialEquations equations,
                           final double[] y, final double[][] yDotK, final boolean forward) {
    reinitialize(y, forward);
    this.yDotK = yDotK;
    this.equations = equations;
  }

  /** {@inheritDoc} */
  @Override
  public void writeExternal(final ObjectOutput out)
    throws IOException {

    // save the state of the base class
    writeBaseExternal(out);

    // save the local attributes
    out.writeInt(yDotK.length);
    for (int k = 0; k < yDotK.length; ++k) {
      for (int i = 0; i < currentState.length; ++i) {
        out.writeDouble(yDotK[k][i]);
      }
    }

    // we do not save any reference to the equations

  }

  /** {@inheritDoc} */
  @Override
  public void readExternal(final ObjectInput in)
    throws IOException {

    // read the base class 
    final double t = readBaseExternal(in);

    // read the local attributes
    final int kMax = in.readInt();
    yDotK = new double[kMax][];
    for (int k = 0; k < kMax; ++k) {
      yDotK[k] = new double[currentState.length];
      for (int i = 0; i < currentState.length; ++i) {
        yDotK[k][i] = in.readDouble();
      }
    }

    equations = null;

    try {
      // we can now set the interpolated time and state
      setInterpolatedTime(t);
    } catch (DerivativeException e) {
      throw MathRuntimeException.createIOException(e);
    }

  }

  /** Slopes at the intermediate points */
  protected double[][] yDotK;

  /** Reference to the differential equations being integrated. */
  protected FirstOrderDifferentialEquations equations;

}
