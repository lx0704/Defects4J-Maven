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

package org.apache.commons.math.analysis;

import org.apache.commons.math.analysis.function.Identity;

/**
 * Utilities for manipulating function objects.
 *
 * @version $Id$
 * @since 3.0
 */
public class FunctionUtils {
    /**
     * Class only contains static methods.
     */
    private FunctionUtils() {}

    /**
     * Compose functions.  The functions in the argument list are composed
     * sequentially, in the order given.  For example, compose(f1,f2,f3)
     * acts like f1(f2(f3(x))).
     *
     * @param f List of functions.
     * @return the composite function.
     */
    public static UnivariateRealFunction compose(final UnivariateRealFunction ... f) {
        return new UnivariateRealFunction() {
            /** {@inheritDoc} */
            public double value(double x) {
                double r = x;
                for (int i = f.length - 1; i >= 0; i--) {
                    r = f[i].value(r);
                }
                return r;
            }
        };
    }

    /**
     * Add functions.
     *
     * @param f List of functions.
     * @return a function that computes the sum of the functions.
     */
    public static UnivariateRealFunction add(final UnivariateRealFunction ... f) {
        return new UnivariateRealFunction() {
            /** {@inheritDoc} */
            public double value(double x) {
                double r = f[0].value(x);
                for (int i = 1; i < f.length; i++) {
                    r += f[i].value(x);
                }
                return r;
            }
        };
    }

    /**
     * Multiply functions.
     *
     * @param f List of functions.
     * @return a function that computes the product of the functions.
     */
    public static UnivariateRealFunction multiply(final UnivariateRealFunction ... f) {
        return new UnivariateRealFunction() {
            /** {@inheritDoc} */
            public double value(double x) {
                double r = f[0].value(x);
                for (int i = 1; i < f.length; i++) {
                    r *= f[i].value(x);
                }
                return r;
            }
        };
    }

    /**
     * Returns the univariate function <br/>
     * {@code h(x) = combiner(f(x), g(x))}.
     *
     * @param combiner Combiner function.
     * @param f Function.
     * @param g Function.
     * @return the composite function.
     */
    public static UnivariateRealFunction combine(final BivariateRealFunction combiner,
                                                 final UnivariateRealFunction f,
                                                 final UnivariateRealFunction g) {
        return new UnivariateRealFunction() {
            /** {@inheritDoc} */
            public double value(double x) {
                return combiner.value(f.value(x), g.value(x));
            }
        };
    }

    /**
     * Returns a MultivariateRealFunction h(x[]) defined by <pre> <code>
     * h(x[]) = combiner(...combiner(combiner(initialValue,f(x[0])),f(x[1]))...),f(x[x.length-1]))
     * </code></pre>
     *
     * @param combiner Combiner function.
     * @param f Function.
     * @param initialValue Initial value.
     * @return a collector function.
     */
    public static MultivariateRealFunction collector(final BivariateRealFunction combiner,
                                                     final UnivariateRealFunction f,
                                                     final double initialValue) {
        return new MultivariateRealFunction() {
            /** {@inheritDoc} */
            public double value(double[] point) {
                double result = combiner.value(initialValue, f.value(point[0]));
                for (int i = 1; i < point.length; i++) {
                    result = combiner.value(result, f.value(point[i]));
                }
                return result;
            }
        };
    }

    /**
     * Returns a MultivariateRealFunction h(x[]) defined by <pre> <code>
     * h(x[]) = combiner(...combiner(combiner(initialValue,x[0]),x[1])...),x[x.length-1])
     * </code></pre>
     *
     * @param combiner Combiner function.
     * @param initialValue Initial value.
     * @return a collector function.
     */
    public static MultivariateRealFunction collector(final BivariateRealFunction combiner,
                                                     final double initialValue) {
        return collector(combiner, new Identity(), initialValue);
    }

    /**
     * Create a unary function by fixing the first argument of a binary function.
     *
     * @param f Binary function.
     * @param fixed Value to which the first argument of {@code f} is set.
     * @return the unary function h(x) = f(fixed, x)
     */
    public static UnivariateRealFunction fix1stArgument(final BivariateRealFunction f,
                                                        final double fixed) {
        return new UnivariateRealFunction() {
            /** {@inheritDoc} */
            public double value(double x) {
                return f.value(fixed, x);
            }
        };
    }
    /**
     * Create a unary function by fixing the second argument of a binary function.
     *
     * @param f Binary function.
     * @param fixed Value to which the second argument of {@code f} is set.
     * @return the unary function h(x) = f(x, fixed)
     */
    public static UnivariateRealFunction fix2ndArgument(final BivariateRealFunction f,
                                                        final double fixed) {
        return new UnivariateRealFunction() {
            /** {@inheritDoc} */
            public double value(double x) {
                return f.value(x, fixed);
            }
        };
    }
}
