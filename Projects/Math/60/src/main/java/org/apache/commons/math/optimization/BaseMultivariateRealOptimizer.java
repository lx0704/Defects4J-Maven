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

import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.apache.commons.math.exception.MathUserException;

/**
 * This interface is mainly intended to enforce the internal coherence of
 * Commons-FastMath. Users of the API are advised to base their code on
 * the following interfaces:
 * <ul>
 *  <li>{@link org.apache.commons.math.optimization.MultivariateRealOptimizer}</li>
 *  <li>{@link org.apache.commons.math.optimization.DifferentiableMultivariateRealOptimizer}</li>
 * </ul>
 *
 * @param <FUNC> Type of the objective function to be optimized.
 *
 * @version $Revision$ $Date$
 * @since 3.0
 */
public interface BaseMultivariateRealOptimizer<FUNC extends MultivariateRealFunction>
    extends BaseOptimizer<RealPointValuePair> {
    /**
     * Optimize an objective function.
     *
     * @param f Objective function.
     * @param goalType Type of optimization goal: either
     * {@link GoalType#MAXIMIZE} or {@link GoalType#MINIMIZE}.
     * @param startPoint Start point for optimization.
     * @return the point/value pair giving the optimal value for objective
     * function.
     * @throws org.apache.commons.math.exception.DimensionMismatchException
     * if the start point dimension is wrong.
     * @throws org.apache.commons.math.exception.TooManyEvaluationsException
     * if the maximal number of evaluations is exceeded.
     * @throws org.apache.commons.math.exception.NullArgumentException if
     * any argument is {@code null}.
     * @throws MathUserException if objective function throws one during search
     */
    RealPointValuePair optimize(FUNC f, GoalType goalType, double[] startPoint) throws MathUserException;
}
