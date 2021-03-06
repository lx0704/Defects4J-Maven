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

package org.apache.commons.math.optimization;

import java.util.Arrays;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.random.RandomGenerator;

/** 
 * Special implementation of the {@link UnivariateRealOptimizer} interface adding
 * multi-start features to an existing optimizer.
 * <p>
 * This class wraps a classical optimizer to use it several times in
 * turn with different starting points in order to avoid being trapped
 * into a local extremum when looking for a global one.
 * </p>
 * @version $Revision$ $Date$
 * @since 2.0
 */
public class MultiStartUnivariateRealOptimizer implements UnivariateRealOptimizer {

    /** Serializable version identifier. */
    private static final long serialVersionUID = 5983375963110961019L;

    /** Underlying classical optimizer. */
    private final UnivariateRealOptimizer optimizer;

    /** Maximal number of iterations allowed. */
    private int maxIterations;

    /** Number of iterations already performed for all starts. */
    private int totalIterations;

    /** Number of starts to go. */
    private int starts;

    /** Random generator for multi-start. */
    private RandomGenerator generator;

    /** Found optima. */
    private double[] optima;

    /**
     * Create a multi-start optimizer from a single-start optimizer
     * @param optimizer single-start optimizer to wrap
     * @param starts number of starts to perform (including the
     * first one), multi-start is disabled if value is less than or
     * equal to 1
     * @param generator random generator to use for restarts
     */
    public MultiStartUnivariateRealOptimizer(final UnivariateRealOptimizer optimizer,
                                             final int starts,
                                             final RandomGenerator generator) {
        this.optimizer        = optimizer;
        this.maxIterations    = Integer.MAX_VALUE;
        this.totalIterations  = 0;
        this.starts           = starts;
        this.generator        = generator;
        this.optima           = null;
    }

    /** {@inheritDoc} */
    public double getFunctionValue() {
        return optimizer.getFunctionValue();
    }

    /** {@inheritDoc} */
    public double getResult() {
        return optimizer.getResult();
    }

    /** {@inheritDoc} */
    public double getAbsoluteAccuracy() {
        return optimizer.getAbsoluteAccuracy();
    }

    /** {@inheritDoc} */
    public int getIterationCount() {
        return totalIterations;
    }

    /** {@inheritDoc} */
    public int getMaximalIterationCount() {
        return maxIterations;
    }

    /** {@inheritDoc} */
    public double getRelativeAccuracy() {
        return optimizer.getRelativeAccuracy();
    }

    /** {@inheritDoc} */
    public void resetAbsoluteAccuracy() {
        optimizer.resetAbsoluteAccuracy();
    }

    /** {@inheritDoc} */
    public void resetMaximalIterationCount() {
        optimizer.resetMaximalIterationCount();
    }

    /** {@inheritDoc} */
    public void resetRelativeAccuracy() {
        optimizer.resetRelativeAccuracy();
    }

    /** {@inheritDoc} */
    public void setAbsoluteAccuracy(double accuracy) {
        optimizer.setAbsoluteAccuracy(accuracy);
    }

    /** {@inheritDoc} */
    public void setMaximalIterationCount(int count) {
        this.maxIterations = count;
    }

    /** {@inheritDoc} */
    public void setRelativeAccuracy(double accuracy) {
        optimizer.setRelativeAccuracy(accuracy);
    }

    /** Get all the optima found during the last call to {@link
     * #optimize(MultivariateRealFunction, GoalType, double[]) optimize}.
     * <p>The optimizer stores all the optima found during a set of
     * restarts. The {@link #optimize(MultivariateRealFunction, GoalType,
     * double[]) optimize} method returns the best point only. This
     * method returns all the points found at the end of each starts,
     * including the best one already returned by the {@link
     * #optimize(MultivariateRealFunction, GoalType, double[]) optimize}
     * method.
     * </p>
     * <p>
     * The returned array as one element for each start as specified
     * in the constructor. It is ordered with the results from the
     * runs that did converge first, sorted from best to worst
     * objective value (i.e in ascending order if minimizing and in
     * descending order if maximizing), followed by and null elements
     * corresponding to the runs that did not converge. This means all
     * elements will be null if the {@link #optimize(MultivariateRealFunction,
     * GoalType, double[]) optimize} method did throw a {@link
     * ConvergenceException ConvergenceException}). This also means that
     * if the first element is non null, it is the best point found across
     * all starts.</p>
     * @return array containing the optima
     * @exception IllegalStateException if {@link #optimize(MultivariateRealFunction,
     * GoalType, double[]) optimize} has not been called
     */
    public double[] getOptima() throws IllegalStateException {
        if (optima == null) {
            throw MathRuntimeException.createIllegalStateException("no optimum computed yet");
        }
        return optima.clone();
    }

    /** {@inheritDoc} */
    public double optimize(final UnivariateRealFunction f, final GoalType goalType,
                           final double min, final double max)
        throws ConvergenceException,
            FunctionEvaluationException {
        return optimize(f, goalType, min, max, min + generator.nextDouble() * (max - min));
    }

    /** {@inheritDoc} */
    public double optimize(final UnivariateRealFunction f, final GoalType goalType,
                           final double min, final double max, final double startValue)
            throws ConvergenceException, FunctionEvaluationException {

        optima          = new double[starts];
        totalIterations = 0;

        // multi-start loop
        for (int i = 0; i < starts; ++i) {

            try {
                optimizer.setMaximalIterationCount(maxIterations - totalIterations);
                optima[i] = optimizer.optimize(f, goalType, min, max,
                                               (i == 0) ? startValue : generator.nextDouble() * (max - min));
            } catch (FunctionEvaluationException fee) {
                optima[i] = Double.NaN;
            } catch (ConvergenceException ce) {
                optima[i] = Double.NaN;
            }

            totalIterations  += optimizer.getIterationCount();

        }

        // sort the optima from best to worst, followed by NaN elements
        int lastNaN = optima.length;
        for (int i = 0; i < lastNaN; ++i) {
            if (Double.isNaN(optima[i])) {
                optima[i] = optima[--lastNaN];
                optima[lastNaN + 1] = Double.NaN;
            }
        }
        Arrays.sort(optima, 0, lastNaN);
        if (goalType == GoalType.MAXIMIZE) {
            for (int i = 0, j = lastNaN - 1; i < j; ++i, --j) {
                double tmp = optima[i];
                optima[i] = optima[j];
                optima[j] = tmp;
            }
        }

        if (Double.isNaN(optima[0])) {
            throw new OptimizationException(
                    "none of the {0} start points lead to convergence",
                    starts);
        }

        // return the found point given the best objective function value
        return optima[0];

    }

}
