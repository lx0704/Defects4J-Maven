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

package org.apache.commons.math.analysis.solvers;

import org.apache.commons.math.MathException;
import org.apache.commons.math.analysis.SinFunction;
import org.apache.commons.math.analysis.QuinticFunction;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

/**
 * @version $Revision$ $Date$
 */
public class UnivariateRealSolverUtilsTest {

    protected UnivariateRealFunction sin = new SinFunction();

    @Test
    public void testSolveNull() {
        try {
            UnivariateRealSolverUtils.solve(null, 0.0, 4.0);
            Assert.fail();
        } catch(IllegalArgumentException ex){
            // success
        }
    }

    @Test
    public void testSolveBadEndpoints() {
        try { // bad endpoints
            double root = UnivariateRealSolverUtils.solve(sin, 4.0, -0.1, 1e-6);
            System.out.println("root=" + root);
            Assert.fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    @Test
    public void testSolveBadAccuracy() {
        try { // bad accuracy
            UnivariateRealSolverUtils.solve(sin, 0.0, 4.0, 0.0);
//             Assert.fail("Expecting IllegalArgumentException"); // TODO needs rework since convergence behaviour was changed
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    @Test
    public void testSolveSin() {
        double x = UnivariateRealSolverUtils.solve(sin, 1.0, 4.0);
        Assert.assertEquals(FastMath.PI, x, 1.0e-4);
    }

    @Test
    public void testSolveAccuracyNull()  {
        try {
            double accuracy = 1.0e-6;
            UnivariateRealSolverUtils.solve(null, 0.0, 4.0, accuracy);
            Assert.fail();
        } catch(IllegalArgumentException ex){
            // success
        }
    }

    @Test
    public void testSolveAccuracySin() {
        double accuracy = 1.0e-6;
        double x = UnivariateRealSolverUtils.solve(sin, 1.0,
                4.0, accuracy);
        Assert.assertEquals(FastMath.PI, x, accuracy);
    }

    @Test
    public void testSolveNoRoot() {
        try {
            UnivariateRealSolverUtils.solve(sin, 1.0, 1.5);
            Assert.fail("Expecting IllegalArgumentException ");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    @Test
    public void testBracketSin() {
        double[] result = UnivariateRealSolverUtils.bracket(sin,
                0.0, -2.0, 2.0);
        Assert.assertTrue(sin.value(result[0]) < 0);
        Assert.assertTrue(sin.value(result[1]) > 0);
    }

    @Test
    public void testBracketEndpointRoot() {
        double[] result = UnivariateRealSolverUtils.bracket(sin, 1.5, 0, 2.0);
        Assert.assertEquals(0.0, sin.value(result[0]), 1.0e-15);
        Assert.assertTrue(sin.value(result[1]) > 0);
    }

    @Test
    public void testNullFunction() {
        try { // null function
            UnivariateRealSolverUtils.bracket(null, 1.5, 0, 2.0);
            Assert.fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }
    
    @Test
    public void testBadInitial() {
        try { // initial not between endpoints
            UnivariateRealSolverUtils.bracket(sin, 2.5, 0, 2.0);
            Assert.fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }
    
    @Test
    public void testBadEndpoints() {
        try { // endpoints not valid
            UnivariateRealSolverUtils.bracket(sin, 1.5, 2.0, 1.0);
            Assert.fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }
    
    @Test
    public void testBadMaximumIterations() {
        try { // bad maximum iterations
            UnivariateRealSolverUtils.bracket(sin, 1.5, 0, 2.0, 0);
            Assert.fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    @Test
    public void testMisc() {
        UnivariateRealFunction f = new QuinticFunction();
        double result;
        // Static solve method
        result = UnivariateRealSolverUtils.solve(f, -0.2, 0.2);
        Assert.assertEquals(result, 0, 1E-8);
        result = UnivariateRealSolverUtils.solve(f, -0.1, 0.3);
        Assert.assertEquals(result, 0, 1E-8);
        result = UnivariateRealSolverUtils.solve(f, -0.3, 0.45);
        Assert.assertEquals(result, 0, 1E-6);
        result = UnivariateRealSolverUtils.solve(f, 0.3, 0.7);
        Assert.assertEquals(result, 0.5, 1E-6);
        result = UnivariateRealSolverUtils.solve(f, 0.2, 0.6);
        Assert.assertEquals(result, 0.5, 1E-6);
        result = UnivariateRealSolverUtils.solve(f, 0.05, 0.95);
        Assert.assertEquals(result, 0.5, 1E-6);
        result = UnivariateRealSolverUtils.solve(f, 0.85, 1.25);
        Assert.assertEquals(result, 1.0, 1E-6);
        result = UnivariateRealSolverUtils.solve(f, 0.8, 1.2);
        Assert.assertEquals(result, 1.0, 1E-6);
        result = UnivariateRealSolverUtils.solve(f, 0.85, 1.75);
        Assert.assertEquals(result, 1.0, 1E-6);
        result = UnivariateRealSolverUtils.solve(f, 0.55, 1.45);
        Assert.assertEquals(result, 1.0, 1E-6);
        result = UnivariateRealSolverUtils.solve(f, 0.85, 5);
        Assert.assertEquals(result, 1.0, 1E-6);
    }
}
