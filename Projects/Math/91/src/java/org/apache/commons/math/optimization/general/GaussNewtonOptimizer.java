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

package org.apache.commons.math.optimization.general;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.linear.DenseRealMatrix;
import org.apache.commons.math.linear.InvalidMatrixException;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.decomposition.DecompositionSolver;
import org.apache.commons.math.linear.decomposition.LUDecompositionImpl;
import org.apache.commons.math.linear.decomposition.QRDecompositionImpl;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.SimpleVectorialValueChecker;
import org.apache.commons.math.optimization.VectorialPointValuePair;

/** 
 * Gauss-Newton least-squares solver.
 * <p>
 * This class solve a least-square problem by solving the normal equations
 * of the linearized problem at each iteration. Either LU decomposition or
 * QR decomposition can be used to solve the normal equations. LU decomposition
 * is faster but QR decomposition is more robust for difficult problems.
 * </p>
 *
 * @version $Revision$ $Date$
 * @since 2.0
 *
 */

public class GaussNewtonOptimizer extends AbstractLeastSquaresOptimizer {

    /** Serializable version identifier */
    private static final long serialVersionUID = 7011643996279553223L;

    /** Indicator for using LU decomposition. */
    private final boolean useLU;

    /** Simple constructor with default settings.
     * <p>The convergence check is set to a {@link SimpleVectorialValueChecker}
     * and the maximal number of evaluation is set to
     * {@link AbstractLinearOptimizer#DEFAULT_MAX_ITERATIONS}.
     * @param useLU if true, the normal equations will be solved using LU
     * decomposition, otherwise they will be solved using QR decomposition
     */
    public GaussNewtonOptimizer(final boolean useLU) {
        this.useLU = useLU;
    }

    /** {@inheritDoc} */
    public VectorialPointValuePair doOptimize()
        throws FunctionEvaluationException, OptimizationException, IllegalArgumentException {

        // iterate until convergence is reached
        VectorialPointValuePair current = null;
        for (boolean converged = false; !converged;) {

            incrementIterationsCounter();

            // evaluate the objective function and its jacobian
            VectorialPointValuePair previous = current;
            updateResidualsAndCost();
            updateJacobian();
            current = new VectorialPointValuePair(point, objective);

            // build the linear problem
            final double[]   b = new double[cols];
            final double[][] a = new double[cols][cols];
            for (int i = 0; i < rows; ++i) {

                final double[] grad   = jacobian[i];
                final double weight   = weights[i];
                final double residual = objective[i] - target[i];

                // compute the normal equation
                final double wr = weight * residual;
                for (int j = 0; j < cols; ++j) {
                    b[j] += wr * grad[j];
                }

                // build the contribution matrix for measurement i
                for (int k = 0; k < cols; ++k) {
                    double[] ak = a[k];
                    double wgk = weight * grad[k];
                    for (int l = 0; l < cols; ++l) {
                        ak[l] += wgk * grad[l];
                    }
                }

            }

            try {

                // solve the linearized least squares problem
                RealMatrix mA = new DenseRealMatrix(a);
                DecompositionSolver solver = useLU ?
                        new LUDecompositionImpl(mA).getSolver() :
                        new QRDecompositionImpl(mA).getSolver();
                final double[] dX = solver.solve(b);

                // update the estimated parameters
                for (int i = 0; i < cols; ++i) {
                    point[i] += dX[i];
                }

            } catch(InvalidMatrixException e) {
                throw new OptimizationException("unable to solve: singular problem");
            }

            // check convergence
            if (previous != null) {
                converged = checker.converged(getIterations(), previous, current);
            }

        }

        // we have converged
        return current;

    }

}
