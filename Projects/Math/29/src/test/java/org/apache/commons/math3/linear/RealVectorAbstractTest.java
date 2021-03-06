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
package org.apache.commons.math3.linear;

import java.util.Arrays;
import java.util.Random;

import org.apache.commons.math3.TestUtils;
import org.apache.commons.math3.analysis.function.Abs;
import org.apache.commons.math3.analysis.function.Acos;
import org.apache.commons.math3.analysis.function.Asin;
import org.apache.commons.math3.analysis.function.Atan;
import org.apache.commons.math3.analysis.function.Cbrt;
import org.apache.commons.math3.analysis.function.Ceil;
import org.apache.commons.math3.analysis.function.Cos;
import org.apache.commons.math3.analysis.function.Cosh;
import org.apache.commons.math3.analysis.function.Exp;
import org.apache.commons.math3.analysis.function.Expm1;
import org.apache.commons.math3.analysis.function.Floor;
import org.apache.commons.math3.analysis.function.Inverse;
import org.apache.commons.math3.analysis.function.Log;
import org.apache.commons.math3.analysis.function.Log10;
import org.apache.commons.math3.analysis.function.Log1p;
import org.apache.commons.math3.analysis.function.Power;
import org.apache.commons.math3.analysis.function.Rint;
import org.apache.commons.math3.analysis.function.Signum;
import org.apache.commons.math3.analysis.function.Sin;
import org.apache.commons.math3.analysis.function.Sinh;
import org.apache.commons.math3.analysis.function.Sqrt;
import org.apache.commons.math3.analysis.function.Tan;
import org.apache.commons.math3.analysis.function.Tanh;
import org.apache.commons.math3.analysis.function.Ulp;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;
import org.junit.Assert;
import org.junit.Test;


public abstract class RealVectorAbstractTest {

    private enum BinaryOperation {
        ADD, SUB, MUL, DIV
    };

    /**
     * Creates a new instance of {@link RealVector}, with specified entries.
     * The returned vector must be of the type currently tested. It should be
     * noted that some tests assume that no references to the specified
     * {@code double[]} are kept in the returned object: if necessary, defensive
     * copy of this array should be made.
     *
     * @param data the entries of the vector to be created
     * @return a new {@link RealVector} of the type to be tested
     */
    public abstract RealVector create(double[] data);

    /**
     * Creates a new instance of {@link RealVector}, with specified entries.
     * The type of the returned vector must be different from the type currently
     * tested. It should be noted that some tests assume that no references to
     * the specified {@code double[]} are kept in the returned object: if
     * necessary, defensive copy of this array should be made.
     *
     * @param data the entries of the vector to be created
     * @return a new {@link RealVector} of an alien type
     */
    public abstract RealVector createAlien(double[] data);

    /**
     * Returns a preferred value of the entries, to be tested specifically. Some
     * implementations of {@link RealVector} (e.g. {@link OpenMapRealVector}) do
     * not store specific values of entries. In order to ensure that all tests
     * take into account this specific value, some entries of the vectors to be
     * tested are deliberately set to the value returned by the present method.
     * The default implementation returns {@code 0.0}.
     *
     * @return a value which <em>should</em> be present in all vectors to be
     * tested
     */
    public double getPreferredEntryValue() {
        return 0.0;
    }

    /** verifies that two vectors are close (sup norm) */
    protected void assertClose(String msg, double[] m, double[] n,
            double tolerance) {
        if (m.length != n.length) {
            Assert.fail("vectors have different lengths");
        }
        for (int i = 0; i < m.length; i++) {
            Assert.assertEquals(msg + " " +  i + " elements differ", m[i],n[i],tolerance);
        }
    }

    protected double[][] ma1 = {{1d, 2d, 3d}, {4d, 5d, 6d}, {7d, 8d, 9d}};
    protected double[] vec1 = {1d, 2d, 3d};
    protected double[] vec2 = {4d, 5d, 6d};
    protected double[] vec3 = {7d, 8d, 9d};
    protected double[] vec4 = {1d, 2d, 3d, 4d, 5d, 6d, 7d, 8d, 9d};
    protected double[] vec5 = { -4d, 0d, 3d, 1d, -6d, 3d};
    protected double[] vec_null = {0d, 0d, 0d};
    protected Double[] dvec1 = {1d, 2d, 3d, 4d, 5d, 6d, 7d, 8d, 9d};
    protected double[][] mat1 = {{1d, 2d, 3d}, {4d, 5d, 6d},{ 7d, 8d, 9d}};

    /**
     * Data which can be used to create a specific vector. The array is
     * interspersed with the value returned by
     * {@link #getPreferredEntryValue()}.
     */
    private final double[] data1;


    /**
     * Data which can be used to create a specific vector. The array is
     * interspersed with the value returned by
     * {@link #getPreferredEntryValue()}.
     */
    private final double[] data2;

    public RealVectorAbstractTest() {
        final double x = getPreferredEntryValue();
        data1 = new double[] {x, 1d, 2d, x, x};
        data2 = new double[] {x, x, 3d, x, 4d, x};
    }

    // tolerances
    protected double entryTolerance = 10E-16;
    protected double normTolerance = 10E-14;

    @Test
    public void testGetDimension() {
        Assert.assertEquals(data1.length, create(data1).getDimension());
    }

    @Test
    public void testGetEntry() {
        final RealVector v = create(data1);
        for (int i = 0; i < data1.length; i++) {
            Assert.assertEquals("entry " + i, data1[i], v.getEntry(i), 0d);
        }
    }

    @Test(expected=OutOfRangeException.class)
    public void testGetEntryInvalidIndex1() {
        create(data1).getEntry(-1);
    }

    @Test(expected=OutOfRangeException.class)
    public void testGetEntryInvalidIndex2() {
        create(data1).getEntry(data1.length);
    }

    @Test
    public void testSetEntry() {
        final double[] expected = MathArrays.copyOf(data1);
        final RealVector actual = create(data1);

        /*
         * Try setting to any value.
         */
        for (int i = 0; i < data1.length; i++) {
            final double oldValue = data1[i];
            final double newValue = oldValue + 1d;
            expected[i] = newValue;
            actual.setEntry(i, newValue);
            TestUtils.assertEquals("while setting entry #" + i, expected,
                actual, 0d);
            expected[i] = oldValue;
            actual.setEntry(i, oldValue);
        }

        /*
         * Try setting to the preferred value.
         */
        final double x = getPreferredEntryValue();
        for (int i = 0; i < data1.length; i++) {
            final double oldValue = data1[i];
            final double newValue = x;
            expected[i] = newValue;
            actual.setEntry(i, newValue);
            TestUtils.assertEquals("while setting entry #" + i, expected,
                actual, 0d);
            expected[i] = oldValue;
            actual.setEntry(i, oldValue);
        }
    }

    @Test(expected=OutOfRangeException.class)
    public void testSetEntryInvalidIndex1() {
        create(data1).setEntry(-1, getPreferredEntryValue());
    }

    @Test(expected=OutOfRangeException.class)
    public void testSetEntryInvalidIndex2() {
        create(data1).setEntry(data1.length, getPreferredEntryValue());
    }

    @Test
    public void testAddToEntry() {
        final double[] expected = MathArrays.copyOf(data1);
        final RealVector actual = create(data1);

        /*
         * Try adding any value.
         */
        double increment = 1d;
        for (int i = 0; i < data1.length; i++) {
            final double oldValue = data1[i];
            expected[i] += increment;
            actual.addToEntry(i, increment);
            TestUtils.assertEquals("while incrementing entry #" + i, expected,
                actual, 0d);
            expected[i] = oldValue;
            actual.setEntry(i, oldValue);
        }

        /*
         * Try incrementing so that result is equal to preferred value.
         */
        final double x = getPreferredEntryValue();
        for (int i = 0; i < data1.length; i++) {
            final double oldValue = data1[i];
            increment = x - oldValue;
            expected[i] = x;
            actual.addToEntry(i, increment);
            TestUtils.assertEquals("while incrementing entry #" + i, expected,
                actual, 0d);
            expected[i] = oldValue;
            actual.setEntry(i, oldValue);
        }
    }

    @Test(expected=OutOfRangeException.class)
    public void testAddToEntryInvalidIndex1() {
        create(data1).addToEntry(-1, getPreferredEntryValue());
    }

    @Test(expected=OutOfRangeException.class)
    public void testAddToEntryInvalidIndex2() {
        create(data1).addToEntry(data1.length, getPreferredEntryValue());
    }

    private void doTestAppendVector(final String message, final RealVector v1,
        final RealVector v2, final double delta) {

        final int n1 = v1.getDimension();
        final int n2 = v2.getDimension();
        final RealVector v = v1.append(v2);
        Assert.assertEquals(message, n1 + n2, v.getDimension());
        for (int i = 0; i < n1; i++) {
            final String msg = message + ", entry #" + i;
            Assert.assertEquals(msg, v1.getEntry(i), v.getEntry(i), delta);
        }
        for (int i = 0; i < n2; i++) {
            final String msg = message + ", entry #" + (n1 + i);
            Assert.assertEquals(msg, v2.getEntry(i), v.getEntry(n1 + i), delta);
        }
    }

    @Test
    public void testAppendVector() {
        doTestAppendVector("same type", create(data1), create(data2), 0d);
        doTestAppendVector("mixed types", create(data1), createAlien(data2), 0d);
    }

    private void doTestAppendScalar(final String message, final RealVector v,
        final double d, final double delta) {

        final int n = v.getDimension();
        final RealVector w = v.append(d);
        Assert.assertEquals(message, n + 1, w.getDimension());
        for (int i = 0; i < n; i++) {
            final String msg = message + ", entry #" + i;
            Assert.assertEquals(msg, v.getEntry(i), w.getEntry(i), delta);
        }
        final String msg = message + ", entry #" + n;
        Assert.assertEquals(msg, d, w.getEntry(n), delta);
    }

    @Test
    public void testAppendScalar() {

        doTestAppendScalar("", create(data1), 1d, 0d);
        doTestAppendScalar("", create(data1), getPreferredEntryValue(), 0d);
    }

    @Test
    public void testGetSubVector() {
        final double x = getPreferredEntryValue();
        final double[] data = {x, x, x, 1d, x, 2d, x, x, 3d, x, x, x, 4d, x, x, x};
        final int index = 1;
        final int n = data.length - 5;
        final RealVector actual = create(data).getSubVector(index, n);
        final double[] expected = new double[n];
        System.arraycopy(data, index, expected, 0, n);
        TestUtils.assertEquals("", expected, actual, 0d);
    }

    @Test(expected = OutOfRangeException.class)
    public void testGetSubVectorInvalidIndex1() {
        final int n = 10;
        create(new double[n]).getSubVector(-1, 2);
    }

    @Test(expected = OutOfRangeException.class)
    public void testGetSubVectorInvalidIndex2() {
        final int n = 10;
        create(new double[n]).getSubVector(n, 2);
    }

    @Test(expected = OutOfRangeException.class)
    public void testGetSubVectorInvalidIndex3() {
        final int n = 10;
        create(new double[n]).getSubVector(0, n + 1);
    }

    @Test(expected = NotPositiveException.class)
    public void testGetSubVectorInvalidIndex4() {
        final int n = 10;
        create(new double[n]).getSubVector(3, -2);
    }

    @Test
    public void testSetSubVectorSameType() {
        final double x = getPreferredEntryValue();
        final double[] expected = {x, x, x, 1d, x, 2d, x, x, 3d, x, x, x, 4d, x, x, x};
        final double[] sub = {5d, x, 6d, 7d, 8d};
        final RealVector actual = create(expected);
        final int index = 2;
        actual.setSubVector(index, create(sub));

        for (int i = 0; i < sub.length; i++){
            expected[index + i] = sub[i];
        }
        TestUtils.assertEquals("", expected, actual, 0d);
    }

    @Test
    public void testSetSubVectorMixedType() {
        final double x = getPreferredEntryValue();
        final double[] expected = {x, x, x, 1d, x, 2d, x, x, 3d, x, x, x, 4d, x, x, x};
        final double[] sub = {5d, x, 6d, 7d, 8d};
        final RealVector actual = create(expected);
        final int index = 2;
        actual.setSubVector(index, createAlien(sub));

        for (int i = 0; i < sub.length; i++){
            expected[index + i] = sub[i];
        }
        TestUtils.assertEquals("", expected, actual, 0d);
    }

    @Test(expected = OutOfRangeException.class)
    public void testSetSubVectorInvalidIndex1() {
        create(new double[10]).setSubVector(-1, create(new double[2]));
    }

    @Test(expected = OutOfRangeException.class)
    public void testSetSubVectorInvalidIndex2() {
        create(new double[10]).setSubVector(10, create(new double[2]));
    }

    @Test(expected = OutOfRangeException.class)
    public void testSetSubVectorInvalidIndex3() {
        create(new double[10]).setSubVector(9, create(new double[2]));
    }

    @Test
    public void testIsNaN() {
        final RealVector v = create(new double[] {0, 1, 2});

        Assert.assertFalse(v.isNaN());
        v.setEntry(1, Double.NaN);
        Assert.assertTrue(v.isNaN());
    }

    @Test
    public void testIsInfinite() {
        final RealVector v = create(new double[] { 0, 1, 2 });

        Assert.assertFalse(v.isInfinite());
        v.setEntry(0, Double.POSITIVE_INFINITY);
        Assert.assertTrue(v.isInfinite());
        v.setEntry(1, Double.NaN);
        Assert.assertFalse(v.isInfinite());
    }

    private void doTestEbeBinaryOperation(final BinaryOperation op, final boolean mixed) {
        /*
         * Make sure that x, y, z are three different values. Also, x is the
         * preferred value (e.g. the value which is not stored in sparse
         * implementations).
         */
        final double x = getPreferredEntryValue();
        final double y = x + 1d;
        final double z = y + 1d;

        /*
         * This is an attempt at covering most particular cases of combining
         * two values.
         *
         * 1. Addition
         *    --------
         * The following cases should be covered
         * (2 * x) + (-x)
         * (-x) + 2 * x
         * x + y
         * y + x
         * y + z
         * y + (x - y)
         * (y - x) + x
         *
         * The values to be considered are: x, y, z, 2 * x, -x, x - y, y - x.
         *
         * 2. Subtraction
         *    -----------
         * The following cases should be covered
         * (2 * x) - x
         * x - y
         * y - x
         * y - z
         * y - (y - x)
         * (y + x) - y
         *
         * The values to be considered are: x, y, z, x + y, y - x.
         *
         * 3. Multiplication
         *    --------------
         * (x * x) * (1 / x)
         * (1 / x) * (x * x)
         * x * y
         * y * x
         * y * z
         *
         * The values to be considered are: x, y, z, 1 / x, x * x.
         *
         * 4. Division
         *    --------
         * (x * x) / x
         * x / y
         * y / x
         * y / z
         *
         * The values to be considered are: x, y, z, x * x.
         *
         * Also to be considered NaN, POSITIVE_INFINITY, NEGATIVE_INFINITY.
         */
        final double[] values = {x, y, z, 2 * x, -x, 1 / x, x * x, x + y, x - y, y - x};
        final double[] data1 = new double[values.length * values.length];
        final double[] data2 = new double[values.length * values.length];
        int k = 0;
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values.length; j++) {
                data1[k] = values[i];
                data2[k] = values[j];
                ++k;
            }
        }
        final RealVector v1 = create(data1);
        final RealVector v2 = mixed ? createAlien(data2) : create(data2);
        final RealVector actual;
        switch (op) {
            case ADD:
                actual = v1.add(v2);
                break;
            case SUB:
                actual = v1.subtract(v2);
                break;
            case MUL:
                actual = v1.ebeMultiply(v2);
                break;
            case DIV:
                actual = v1.ebeDivide(v2);
                break;
            default:
                throw new AssertionError("unexpected value");
        }
        final double[] expected = new double[data1.length];
        for (int i = 0; i < expected.length; i++) {
            switch (op) {
                case ADD:
                    expected[i] = data1[i] + data2[i];
                    break;
                case SUB:
                    expected[i] = data1[i] - data2[i];
                    break;
                case MUL:
                    expected[i] = data1[i] * data2[i];
                    break;
                case DIV:
                    expected[i] = data1[i] / data2[i];
                    break;
                default:
                    throw new AssertionError("unexpected value");
            }
        }
        for (int i = 0; i < expected.length; i++) {
            final String msg = "entry #"+i+", left = "+data1[i]+", right = " + data2[i];
            Assert.assertEquals(msg, expected[i], actual.getEntry(i), 0.0);
        }
    }

    private void doTestEbeBinaryOperationDimensionMismatch(final BinaryOperation op) {
        final int n = 10;
        switch (op) {
            case ADD:
                create(new double[n]).add(create(new double[n + 1]));
                break;
            case SUB:
                create(new double[n]).subtract(create(new double[n + 1]));
                break;
            case MUL:
                create(new double[n]).ebeMultiply(create(new double[n + 1]));
                break;
            case DIV:
                create(new double[n]).ebeDivide(create(new double[n + 1]));
                break;
            default:
                throw new AssertionError("unexpected value");
        }
    }

    @Test
    public void testAddSameType() {
        doTestEbeBinaryOperation(BinaryOperation.ADD, false);
    }

    @Test
    public void testAddMixedTypes() {
        doTestEbeBinaryOperation(BinaryOperation.ADD, true);
    }

    @Test(expected = DimensionMismatchException.class)
    public void testAddDimensionMismatch() {
        doTestEbeBinaryOperationDimensionMismatch(BinaryOperation.ADD);
    }

    @Test
    public void testSubtractSameType() {
        doTestEbeBinaryOperation(BinaryOperation.SUB, false);
    }

    @Test
    public void testSubtractMixedTypes() {
        doTestEbeBinaryOperation(BinaryOperation.SUB, true);
    }

    @Test(expected = DimensionMismatchException.class)
    public void testSubtractDimensionMismatch() {
        doTestEbeBinaryOperationDimensionMismatch(BinaryOperation.SUB);
    }

    @Test
    public void testEbeMultiplySameType() {
        doTestEbeBinaryOperation(BinaryOperation.MUL, false);
    }

    @Test
    public void testEbeMultiplyMixedTypes() {
        doTestEbeBinaryOperation(BinaryOperation.MUL, true);
    }

    @Test(expected = DimensionMismatchException.class)
    public void testEbeMultiplyDimensionMismatch() {
        doTestEbeBinaryOperationDimensionMismatch(BinaryOperation.MUL);
    }

    @Test
    public void testEbeDivideSameType() {
        doTestEbeBinaryOperation(BinaryOperation.DIV, false);
    }

    @Test
    public void testEbeDivideMixedTypes() {
        doTestEbeBinaryOperation(BinaryOperation.DIV, true);
    }

    @Test(expected = DimensionMismatchException.class)
    public void testEbeDivideDimensionMismatch() {
        doTestEbeBinaryOperationDimensionMismatch(BinaryOperation.DIV);
    }

    @Test
    public void testDataInOut() {
        final RealVector v1 = create(vec1);
        final RealVector v2 = create(vec2);
        final RealVector v4 = create(vec4);
        final RealVector v2_t = createAlien(vec2);

        final RealVector v_set1 = v1.copy();
        v_set1.setEntry(1, 11.0);
        Assert.assertEquals("testData is 11.0 ", 11.0, v_set1.getEntry(1), 0);
        try {
            v_set1.setEntry(3, 11.0);
            Assert.fail("OutOfRangeException expected");
        } catch (OutOfRangeException ex) {
            // expected behavior
        }

        final RealVector v_set2 = v4.copy();
        v_set2.setSubVector(3, v1);
        Assert.assertEquals("testData is 1.0 ", 1.0, v_set2.getEntry(3), 0);
        Assert.assertEquals("testData is 7.0 ", 7.0, v_set2.getEntry(6), 0);
        try {
            v_set2.setSubVector(7, v1);
            Assert.fail("OutOfRangeException expected");
        } catch (OutOfRangeException ex) {
            // expected behavior
        }

        final RealVector v_set3 = v1.copy();
        v_set3.set(13.0);
        Assert.assertEquals("testData is 13.0 ", 13.0, v_set3.getEntry(2), 0);

        try {
            v_set3.getEntry(23);
            Assert.fail("OutOfRangeException expected");
        } catch (OutOfRangeException ex) {
            // expected behavior
        }

        final RealVector v_set4 = v4.copy();
        v_set4.setSubVector(3, v2_t);
        Assert.assertEquals("testData is 1.0 ", 4.0, v_set4.getEntry(3), 0);
        Assert.assertEquals("testData is 7.0 ", 7.0, v_set4.getEntry(6), 0);
        try {
            v_set4.setSubVector(7, v2_t);
            Assert.fail("OutOfRangeException expected");
        } catch (OutOfRangeException ex) {
            // expected behavior
        }

        final RealVector vout10 = v1.copy();
        final RealVector vout10_2 = v1.copy();
        Assert.assertEquals(vout10, vout10_2);
        vout10_2.setEntry(0, 1.1);
        Assert.assertNotSame(vout10, vout10_2);
    }

    @Test
    public void testMapFunctions() {
        final RealVector v1 = create(vec1);

        //octave =  v1 .+ 2.0
        RealVector v_mapAdd = v1.mapAdd(2.0d);
        double[] result_mapAdd = {3d, 4d, 5d};
        assertClose("compare vectors" ,result_mapAdd,v_mapAdd.toArray(),normTolerance);

        //octave =  v1 .+ 2.0
        RealVector v_mapAddToSelf = v1.copy();
        v_mapAddToSelf.mapAddToSelf(2.0d);
        double[] result_mapAddToSelf = {3d, 4d, 5d};
        assertClose("compare vectors" ,result_mapAddToSelf,v_mapAddToSelf.toArray(),normTolerance);

        //octave =  v1 .- 2.0
        RealVector v_mapSubtract = v1.mapSubtract(2.0d);
        double[] result_mapSubtract = {-1d, 0d, 1d};
        assertClose("compare vectors" ,result_mapSubtract,v_mapSubtract.toArray(),normTolerance);

        //octave =  v1 .- 2.0
        RealVector v_mapSubtractToSelf = v1.copy();
        v_mapSubtractToSelf.mapSubtractToSelf(2.0d);
        double[] result_mapSubtractToSelf = {-1d, 0d, 1d};
        assertClose("compare vectors" ,result_mapSubtractToSelf,v_mapSubtractToSelf.toArray(),normTolerance);

        //octave =  v1 .* 2.0
        RealVector v_mapMultiply = v1.mapMultiply(2.0d);
        double[] result_mapMultiply = {2d, 4d, 6d};
        assertClose("compare vectors" ,result_mapMultiply,v_mapMultiply.toArray(),normTolerance);

        //octave =  v1 .* 2.0
        RealVector v_mapMultiplyToSelf = v1.copy();
        v_mapMultiplyToSelf.mapMultiplyToSelf(2.0d);
        double[] result_mapMultiplyToSelf = {2d, 4d, 6d};
        assertClose("compare vectors" ,result_mapMultiplyToSelf,v_mapMultiplyToSelf.toArray(),normTolerance);

        //octave =  v1 ./ 2.0
        RealVector v_mapDivide = v1.mapDivide(2.0d);
        double[] result_mapDivide = {.5d, 1d, 1.5d};
        assertClose("compare vectors" ,result_mapDivide,v_mapDivide.toArray(),normTolerance);

        //octave =  v1 ./ 2.0
        RealVector v_mapDivideToSelf = v1.copy();
        v_mapDivideToSelf.mapDivideToSelf(2.0d);
        double[] result_mapDivideToSelf = {.5d, 1d, 1.5d};
        assertClose("compare vectors" ,result_mapDivideToSelf,v_mapDivideToSelf.toArray(),normTolerance);


        //octave =  v1 .^ 2.0
        RealVector v_mapPow = v1.map(new Power(2));
        double[] result_mapPow = {1d, 4d, 9d};
        assertClose("compare vectors" ,result_mapPow,v_mapPow.toArray(),normTolerance);

        //octave =  v1 .^ 2.0
        RealVector v_mapPowToSelf = v1.copy();
        v_mapPowToSelf.mapToSelf(new Power(2));
        double[] result_mapPowToSelf = {1d, 4d, 9d};
        assertClose("compare vectors" ,result_mapPowToSelf,v_mapPowToSelf.toArray(),normTolerance);

        //octave =  exp(v1)
        RealVector v_mapExp = v1.map(new Exp());
        double[] result_mapExp = {2.718281828459045e+00d,7.389056098930650e+00d, 2.008553692318767e+01d};
        assertClose("compare vectors" ,result_mapExp,v_mapExp.toArray(),normTolerance);

        //octave =  exp(v1)
        RealVector v_mapExpToSelf = v1.copy();
        v_mapExpToSelf.mapToSelf(new Exp());
        double[] result_mapExpToSelf = {2.718281828459045e+00d,7.389056098930650e+00d, 2.008553692318767e+01d};
        assertClose("compare vectors" ,result_mapExpToSelf,v_mapExpToSelf.toArray(),normTolerance);


        //octave =  ???
        RealVector v_mapExpm1 = v1.map(new Expm1());
        double[] result_mapExpm1 = {1.718281828459045d,6.38905609893065d, 19.085536923187668d};
        assertClose("compare vectors" ,result_mapExpm1,v_mapExpm1.toArray(),normTolerance);

        //octave =  ???
        RealVector v_mapExpm1ToSelf = v1.copy();
        v_mapExpm1ToSelf.mapToSelf(new Expm1());
        double[] result_mapExpm1ToSelf = {1.718281828459045d,6.38905609893065d, 19.085536923187668d};
        assertClose("compare vectors" ,result_mapExpm1ToSelf,v_mapExpm1ToSelf.toArray(),normTolerance);

        //octave =  log(v1)
        RealVector v_mapLog = v1.map(new Log());
        double[] result_mapLog = {0d,6.931471805599453e-01d, 1.098612288668110e+00d};
        assertClose("compare vectors" ,result_mapLog,v_mapLog.toArray(),normTolerance);

        //octave =  log(v1)
        RealVector v_mapLogToSelf = v1.copy();
        v_mapLogToSelf.mapToSelf(new Log());
        double[] result_mapLogToSelf = {0d,6.931471805599453e-01d, 1.098612288668110e+00d};
        assertClose("compare vectors" ,result_mapLogToSelf,v_mapLogToSelf.toArray(),normTolerance);

        //octave =  log10(v1)
        RealVector v_mapLog10 = v1.map(new Log10());
        double[] result_mapLog10 = {0d,3.010299956639812e-01d, 4.771212547196624e-01d};
        assertClose("compare vectors" ,result_mapLog10,v_mapLog10.toArray(),normTolerance);

        //octave =  log(v1)
        RealVector v_mapLog10ToSelf = v1.copy();
        v_mapLog10ToSelf.mapToSelf(new Log10());
        double[] result_mapLog10ToSelf = {0d,3.010299956639812e-01d, 4.771212547196624e-01d};
        assertClose("compare vectors" ,result_mapLog10ToSelf,v_mapLog10ToSelf.toArray(),normTolerance);

        //octave =  ???
        RealVector v_mapLog1p = v1.map(new Log1p());
        double[] result_mapLog1p = {0.6931471805599453d,1.0986122886681096d,1.3862943611198906d};
        assertClose("compare vectors" ,result_mapLog1p,v_mapLog1p.toArray(),normTolerance);

        //octave =  ???
        RealVector v_mapLog1pToSelf = v1.copy();
        v_mapLog1pToSelf.mapToSelf(new Log1p());
        double[] result_mapLog1pToSelf = {0.6931471805599453d,1.0986122886681096d,1.3862943611198906d};
        assertClose("compare vectors" ,result_mapLog1pToSelf,v_mapLog1pToSelf.toArray(),normTolerance);

        //octave =  cosh(v1)
        RealVector v_mapCosh = v1.map(new Cosh());
        double[] result_mapCosh = {1.543080634815244e+00d,3.762195691083631e+00d, 1.006766199577777e+01d};
        assertClose("compare vectors" ,result_mapCosh,v_mapCosh.toArray(),normTolerance);

        //octave =  cosh(v1)
        RealVector v_mapCoshToSelf = v1.copy();
        v_mapCoshToSelf.mapToSelf(new Cosh());
        double[] result_mapCoshToSelf = {1.543080634815244e+00d,3.762195691083631e+00d, 1.006766199577777e+01d};
        assertClose("compare vectors" ,result_mapCoshToSelf,v_mapCoshToSelf.toArray(),normTolerance);

        //octave =  sinh(v1)
        RealVector v_mapSinh = v1.map(new Sinh());
        double[] result_mapSinh = {1.175201193643801e+00d,3.626860407847019e+00d, 1.001787492740990e+01d};
        assertClose("compare vectors" ,result_mapSinh,v_mapSinh.toArray(),normTolerance);

        //octave =  sinh(v1)
        RealVector v_mapSinhToSelf = v1.copy();
        v_mapSinhToSelf.mapToSelf(new Sinh());
        double[] result_mapSinhToSelf = {1.175201193643801e+00d,3.626860407847019e+00d, 1.001787492740990e+01d};
        assertClose("compare vectors" ,result_mapSinhToSelf,v_mapSinhToSelf.toArray(),normTolerance);

        //octave =  tanh(v1)
        RealVector v_mapTanh = v1.map(new Tanh());
        double[] result_mapTanh = {7.615941559557649e-01d,9.640275800758169e-01d,9.950547536867305e-01d};
        assertClose("compare vectors" ,result_mapTanh,v_mapTanh.toArray(),normTolerance);

        //octave =  tanh(v1)
        RealVector v_mapTanhToSelf = v1.copy();
        v_mapTanhToSelf.mapToSelf(new Tanh());
        double[] result_mapTanhToSelf = {7.615941559557649e-01d,9.640275800758169e-01d,9.950547536867305e-01d};
        assertClose("compare vectors" ,result_mapTanhToSelf,v_mapTanhToSelf.toArray(),normTolerance);

        //octave =  cos(v1)
        RealVector v_mapCos = v1.map(new Cos());
        double[] result_mapCos = {5.403023058681398e-01d,-4.161468365471424e-01d, -9.899924966004454e-01d};
        assertClose("compare vectors" ,result_mapCos,v_mapCos.toArray(),normTolerance);

        //octave =  cos(v1)
        RealVector v_mapCosToSelf = v1.copy();
        v_mapCosToSelf.mapToSelf(new Cos());
        double[] result_mapCosToSelf = {5.403023058681398e-01d,-4.161468365471424e-01d, -9.899924966004454e-01d};
        assertClose("compare vectors" ,result_mapCosToSelf,v_mapCosToSelf.toArray(),normTolerance);

        //octave =  sin(v1)
        RealVector v_mapSin = v1.map(new Sin());
        double[] result_mapSin = {8.414709848078965e-01d,9.092974268256817e-01d,1.411200080598672e-01d};
        assertClose("compare vectors" ,result_mapSin,v_mapSin.toArray(),normTolerance);

        //octave =  sin(v1)
        RealVector v_mapSinToSelf = v1.copy();
        v_mapSinToSelf.mapToSelf(new Sin());
        double[] result_mapSinToSelf = {8.414709848078965e-01d,9.092974268256817e-01d,1.411200080598672e-01d};
        assertClose("compare vectors" ,result_mapSinToSelf,v_mapSinToSelf.toArray(),normTolerance);

        //octave =  tan(v1)
        RealVector v_mapTan = v1.map(new Tan());
        double[] result_mapTan = {1.557407724654902e+00d,-2.185039863261519e+00d,-1.425465430742778e-01d};
        assertClose("compare vectors" ,result_mapTan,v_mapTan.toArray(),normTolerance);

        //octave =  tan(v1)
        RealVector v_mapTanToSelf = v1.copy();
        v_mapTanToSelf.mapToSelf(new Tan());
        double[] result_mapTanToSelf = {1.557407724654902e+00d,-2.185039863261519e+00d,-1.425465430742778e-01d};
        assertClose("compare vectors" ,result_mapTanToSelf,v_mapTanToSelf.toArray(),normTolerance);

        double[] vat_a = {0d, 0.5d, 1.0d};
        final RealVector vat = create(vat_a);

        //octave =  acos(vat)
        RealVector v_mapAcos = vat.map(new Acos());
        double[] result_mapAcos = {1.570796326794897e+00d,1.047197551196598e+00d, 0.0d};
        assertClose("compare vectors" ,result_mapAcos,v_mapAcos.toArray(),normTolerance);

        //octave =  acos(vat)
        RealVector v_mapAcosToSelf = vat.copy();
        v_mapAcosToSelf.mapToSelf(new Acos());
        double[] result_mapAcosToSelf = {1.570796326794897e+00d,1.047197551196598e+00d, 0.0d};
        assertClose("compare vectors" ,result_mapAcosToSelf,v_mapAcosToSelf.toArray(),normTolerance);

        //octave =  asin(vat)
        RealVector v_mapAsin = vat.map(new Asin());
        double[] result_mapAsin = {0.0d,5.235987755982989e-01d,1.570796326794897e+00d};
        assertClose("compare vectors" ,result_mapAsin,v_mapAsin.toArray(),normTolerance);

        //octave =  asin(vat)
        RealVector v_mapAsinToSelf = vat.copy();
        v_mapAsinToSelf.mapToSelf(new Asin());
        double[] result_mapAsinToSelf = {0.0d,5.235987755982989e-01d,1.570796326794897e+00d};
        assertClose("compare vectors" ,result_mapAsinToSelf,v_mapAsinToSelf.toArray(),normTolerance);

        //octave =  atan(vat)
        RealVector v_mapAtan = vat.map(new Atan());
        double[] result_mapAtan = {0.0d,4.636476090008061e-01d,7.853981633974483e-01d};
        assertClose("compare vectors" ,result_mapAtan,v_mapAtan.toArray(),normTolerance);

        //octave =  atan(vat)
        RealVector v_mapAtanToSelf = vat.copy();
        v_mapAtanToSelf.mapToSelf(new Atan());
        double[] result_mapAtanToSelf = {0.0d,4.636476090008061e-01d,7.853981633974483e-01d};
        assertClose("compare vectors" ,result_mapAtanToSelf,v_mapAtanToSelf.toArray(),normTolerance);

        //octave =  v1 .^-1
        RealVector v_mapInv = v1.map(new Inverse());
        double[] result_mapInv = {1d,0.5d,3.333333333333333e-01d};
        assertClose("compare vectors" ,result_mapInv,v_mapInv.toArray(),normTolerance);

        //octave =  v1 .^-1
        RealVector v_mapInvToSelf = v1.copy();
        v_mapInvToSelf.mapToSelf(new Inverse());
        double[] result_mapInvToSelf = {1d,0.5d,3.333333333333333e-01d};
        assertClose("compare vectors" ,result_mapInvToSelf,v_mapInvToSelf.toArray(),normTolerance);

        double[] abs_a = {-1.0d, 0.0d, 1.0d};
        final RealVector abs_v = create(abs_a);

        //octave =  abs(abs_v)
        RealVector v_mapAbs = abs_v.map(new Abs());
        double[] result_mapAbs = {1d,0d,1d};
        assertClose("compare vectors" ,result_mapAbs,v_mapAbs.toArray(),normTolerance);

        //octave = abs(abs_v)
        RealVector v_mapAbsToSelf = abs_v.copy();
        v_mapAbsToSelf.mapToSelf(new Abs());
        double[] result_mapAbsToSelf = {1d,0d,1d};
        assertClose("compare vectors" ,result_mapAbsToSelf,v_mapAbsToSelf.toArray(),normTolerance);

        //octave =   sqrt(v1)
        RealVector v_mapSqrt = v1.map(new Sqrt());
        double[] result_mapSqrt = {1d,1.414213562373095e+00d,1.732050807568877e+00d};
        assertClose("compare vectors" ,result_mapSqrt,v_mapSqrt.toArray(),normTolerance);

        //octave =  sqrt(v1)
        RealVector v_mapSqrtToSelf = v1.copy();
        v_mapSqrtToSelf.mapToSelf(new Sqrt());
        double[] result_mapSqrtToSelf = {1d,1.414213562373095e+00d,1.732050807568877e+00d};
        assertClose("compare vectors" ,result_mapSqrtToSelf,v_mapSqrtToSelf.toArray(),normTolerance);

        double[] cbrt_a = {-2.0d, 0.0d, 2.0d};
        final RealVector cbrt_v = create(cbrt_a);

        //octave =  ???
        RealVector v_mapCbrt = cbrt_v.map(new Cbrt());
        double[] result_mapCbrt = {-1.2599210498948732d,0d,1.2599210498948732d};
        assertClose("compare vectors" ,result_mapCbrt,v_mapCbrt.toArray(),normTolerance);

        //octave = ???
        RealVector v_mapCbrtToSelf = cbrt_v.copy();
        v_mapCbrtToSelf.mapToSelf(new Cbrt());
        double[] result_mapCbrtToSelf =  {-1.2599210498948732d,0d,1.2599210498948732d};
        assertClose("compare vectors" ,result_mapCbrtToSelf,v_mapCbrtToSelf.toArray(),normTolerance);

        double[] ceil_a = {-1.1d, 0.9d, 1.1d};
        ArrayRealVector ceil_v = new ArrayRealVector(ceil_a);

        //octave =  ceil(ceil_v)
        RealVector v_mapCeil = ceil_v.map(new Ceil());
        double[] result_mapCeil = {-1d,1d,2d};
        assertClose("compare vectors" ,result_mapCeil,v_mapCeil.toArray(),normTolerance);

        //octave = ceil(ceil_v)
        RealVector v_mapCeilToSelf = ceil_v.copy();
        v_mapCeilToSelf.mapToSelf(new Ceil());
        double[] result_mapCeilToSelf =  {-1d,1d,2d};
        assertClose("compare vectors" ,result_mapCeilToSelf,v_mapCeilToSelf.toArray(),normTolerance);

        //octave =  floor(ceil_v)
        RealVector v_mapFloor = ceil_v.map(new Floor());
        double[] result_mapFloor = {-2d,0d,1d};
        assertClose("compare vectors" ,result_mapFloor,v_mapFloor.toArray(),normTolerance);

        //octave = floor(ceil_v)
        RealVector v_mapFloorToSelf = ceil_v.copy();
        v_mapFloorToSelf.mapToSelf(new Floor());
        double[] result_mapFloorToSelf =  {-2d,0d,1d};
        assertClose("compare vectors" ,result_mapFloorToSelf,v_mapFloorToSelf.toArray(),normTolerance);

        //octave =  ???
        RealVector v_mapRint = ceil_v.map(new Rint());
        double[] result_mapRint = {-1d,1d,1d};
        assertClose("compare vectors" ,result_mapRint,v_mapRint.toArray(),normTolerance);

        //octave = ???
        RealVector v_mapRintToSelf = ceil_v.copy();
        v_mapRintToSelf.mapToSelf(new Rint());
        double[] result_mapRintToSelf =  {-1d,1d,1d};
        assertClose("compare vectors" ,result_mapRintToSelf,v_mapRintToSelf.toArray(),normTolerance);

        //octave =  ???
        RealVector v_mapSignum = ceil_v.map(new Signum());
        double[] result_mapSignum = {-1d,1d,1d};
        assertClose("compare vectors" ,result_mapSignum,v_mapSignum.toArray(),normTolerance);

        //octave = ???
        RealVector v_mapSignumToSelf = ceil_v.copy();
        v_mapSignumToSelf.mapToSelf(new Signum());
        double[] result_mapSignumToSelf =  {-1d,1d,1d};
        assertClose("compare vectors" ,result_mapSignumToSelf,v_mapSignumToSelf.toArray(),normTolerance);


        // Is with the used resolutions of limited value as test
        //octave =  ???
        RealVector v_mapUlp = ceil_v.map(new Ulp());
        double[] result_mapUlp = {2.220446049250313E-16d,1.1102230246251565E-16d,2.220446049250313E-16d};
        assertClose("compare vectors" ,result_mapUlp,v_mapUlp.toArray(),normTolerance);

        //octave = ???
        RealVector v_mapUlpToSelf = ceil_v.copy();
        v_mapUlpToSelf.mapToSelf(new Ulp());
        double[] result_mapUlpToSelf = {2.220446049250313E-16d,1.1102230246251565E-16d,2.220446049250313E-16d};
        assertClose("compare vectors" ,result_mapUlpToSelf,v_mapUlpToSelf.toArray(),normTolerance);
    }

    @Test
    public void testBasicFunctions() {
        final RealVector v1 = create(vec1);
        final RealVector v2 = create(vec2);
        final RealVector v5 = create(vec5);
        final RealVector v_null = create(vec_null);

        final RealVector v2_t = createAlien(vec2);

        // emacs calc: [-4, 0, 3, 1, -6, 3] A --> 8.4261497731763586307
        double d_getNorm = v5.getNorm();
        Assert.assertEquals("compare values  ", 8.4261497731763586307,
                            d_getNorm, normTolerance);

        // emacs calc: [-4, 0, 3, 1, -6, 3] vN --> 17
        double d_getL1Norm = v5.getL1Norm();
        Assert.assertEquals("compare values  ", 17.0, d_getL1Norm,
                            normTolerance);

        // emacs calc: [-4, 0, 3, 1, -6, 3] vn --> 6
        double d_getLInfNorm = v5.getLInfNorm();
        Assert.assertEquals("compare values  ", 6.0, d_getLInfNorm,
                            normTolerance);

        // octave = sqrt(sumsq(v1-v2))
        double dist = v1.getDistance(v2);
        Assert.assertEquals("compare values  ", v1.subtract(v2).getNorm(),
                            dist, normTolerance);

        // octave = sqrt(sumsq(v1-v2))
        double dist_2 = v1.getDistance(v2_t);
        Assert.assertEquals("compare values  ", v1.subtract(v2).getNorm(),
                            dist_2, normTolerance);

        // octave = ???
        double d_getL1Distance = v1.getL1Distance(v2);
        Assert.assertEquals("compare values  ", 9d, d_getL1Distance,
                            normTolerance);

        double d_getL1Distance_2 = v1.getL1Distance(v2_t);
        Assert.assertEquals("compare values  ", 9d, d_getL1Distance_2,
                            normTolerance);

        // octave = ???
        double d_getLInfDistance = v1.getLInfDistance(v2);
        Assert.assertEquals("compare values  ", 3d, d_getLInfDistance,
                            normTolerance);

        double d_getLInfDistance_2 = v1.getLInfDistance(v2_t);
        Assert.assertEquals("compare values  ", 3d, d_getLInfDistance_2,
                            normTolerance);

        // octave dot(v1,v2)
        double dot = v1.dotProduct(v2);
        Assert.assertEquals("compare val ", 32d, dot, normTolerance);

        // octave dot(v1,v2_t)
        double dot_2 = v1.dotProduct(v2_t);
        Assert.assertEquals("compare val ", 32d, dot_2, normTolerance);

        RealMatrix m_outerProduct = v1.outerProduct(v2);
        Assert.assertEquals("compare val ", 4d, m_outerProduct.getEntry(0, 0),
                            normTolerance);

        RealMatrix m_outerProduct_2 = v1.outerProduct(v2_t);
        Assert.assertEquals("compare val ", 4d,
                            m_outerProduct_2.getEntry(0, 0), normTolerance);

        RealVector v_unitVector = v1.unitVector();
        RealVector v_unitVector_2 = v1.mapDivide(v1.getNorm());
        assertClose("compare vect", v_unitVector.toArray(),
                    v_unitVector_2.toArray(), normTolerance);

        try {
            v_null.unitVector();
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
            // expected behavior
        }

        RealVector v_unitize = v1.copy();
        v_unitize.unitize();
        assertClose("compare vect" ,v_unitVector_2.toArray(),v_unitize.toArray(),normTolerance);
        try {
            v_null.unitize();
            Assert.fail("Expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {
            // expected behavior
        }

        RealVector v_projection = v1.projection(v2);
        double[] result_projection = {1.662337662337662, 2.0779220779220777, 2.493506493506493};
        assertClose("compare vect", v_projection.toArray(), result_projection, normTolerance);

        RealVector v_projection_2 = v1.projection(v2_t);
        double[] result_projection_2 = {1.662337662337662, 2.0779220779220777, 2.493506493506493};
        assertClose("compare vect", v_projection_2.toArray(), result_projection_2, normTolerance);
    }

    @Test
    public void testOuterProduct() {
        final RealVector u = create(new double[] {1, 2, -3});
        final RealVector v = create(new double[] {4, -2});

        final RealMatrix uv = u.outerProduct(v);

        final double tol = Math.ulp(1d);
        Assert.assertEquals(4, uv.getEntry(0, 0), tol);
        Assert.assertEquals(-2, uv.getEntry(0, 1), tol);
        Assert.assertEquals(8, uv.getEntry(1, 0), tol);
        Assert.assertEquals(-4, uv.getEntry(1, 1), tol);
        Assert.assertEquals(-12, uv.getEntry(2, 0), tol);
        Assert.assertEquals(6, uv.getEntry(2, 1), tol);
    }

    @Test
    public void testMisc() {
        RealVector v1 = create(vec1);
        RealVector v4 = create(vec4);
        RealVector v4_2 = create(vec4);

        String out1 = v1.toString();
        Assert.assertTrue("some output ",  out1.length()!=0);
        try {
            v1.checkVectorDimensions(2);
            Assert.fail("MathIllegalArgumentException expected");
        } catch (MathIllegalArgumentException ex) {
            // expected behavior
        }

       try {
            v1.checkVectorDimensions(v4);
            Assert.fail("MathIllegalArgumentException expected");
        } catch (MathIllegalArgumentException ex) {
            // expected behavior
        }

        try {
            v1.checkVectorDimensions(v4_2);
            Assert.fail("MathIllegalArgumentException expected");
        } catch (MathIllegalArgumentException ex) {
            // expected behavior
        }
    }

    @Test
    public void testPredicates() {
        final RealVector v = create(new double[] { 0, 1, 2 });

        v.setEntry(0, 0);
        Assert.assertEquals(v, create(new double[] { 0, 1, 2 }));
        Assert.assertNotSame(v, create(new double[] { 0, 1, 2 + FastMath.ulp(2)}));
        Assert.assertNotSame(v, create(new double[] { 0, 1, 2, 3 }));

        Assert.assertTrue(v.equals(v));
        Assert.assertTrue(v.equals(v.copy()));
        Assert.assertFalse(v.equals(null));
        Assert.assertFalse(v.equals(v.getSubVector(0, v.getDimension() - 1)));
        Assert.assertTrue(v.equals(v.getSubVector(0, v.getDimension())));
    }

    @Test
    public void testSerial()  {
        RealVector v = create(new double[] { 0, 1, 2 });
        Assert.assertEquals(v,TestUtils.serializeAndRecover(v));
    }

    @Test
    public void testMinMax() {
        final RealVector v1 = create(new double[] {0, -6, 4, 12, 7});
        Assert.assertEquals(1, v1.getMinIndex());
        Assert.assertEquals(-6, v1.getMinValue(), 1.0e-12);
        Assert.assertEquals(3, v1.getMaxIndex());
        Assert.assertEquals(12, v1.getMaxValue(), 1.0e-12);
        final RealVector v2 = create(new double[] {Double.NaN, 3, Double.NaN, -2});
        Assert.assertEquals(3, v2.getMinIndex());
        Assert.assertEquals(-2, v2.getMinValue(), 1.0e-12);
        Assert.assertEquals(1, v2.getMaxIndex());
        Assert.assertEquals(3, v2.getMaxValue(), 1.0e-12);
        final RealVector v3 = create(new double[] {Double.NaN, Double.NaN});
        Assert.assertEquals(-1, v3.getMinIndex());
        Assert.assertTrue(Double.isNaN(v3.getMinValue()));
        Assert.assertEquals(-1, v3.getMaxIndex());
        Assert.assertTrue(Double.isNaN(v3.getMaxValue()));
        final RealVector v4 = create(new double[0]);
        Assert.assertEquals(-1, v4.getMinIndex());
        Assert.assertTrue(Double.isNaN(v4.getMinValue()));
        Assert.assertEquals(-1, v4.getMaxIndex());
        Assert.assertTrue(Double.isNaN(v4.getMaxValue()));
    }

    @Test
    public void testCosine() {
        final RealVector v = create(new double[] {1, 0, 0});

        double[] wData = new double[] {1, 1, 0};
        RealVector w = create(wData);
        Assert.assertEquals(FastMath.sqrt(2) / 2, v.cosine(w), normTolerance);

        wData = new double[] {1, 0, 0};
        w = create(wData);
        Assert.assertEquals(1, v.cosine(w), normTolerance);

        wData = new double[] {0, 1, 0};
        w = create(wData);
        Assert.assertEquals(0, v.cosine(w), 0);

        wData = new double[] {-1, 0, 0};
        w = create(wData);
        Assert.assertEquals(-1, v.cosine(w), normTolerance);
    }

    @Test(expected=MathArithmeticException.class)
    public void testCosinePrecondition1() {
        final RealVector v = create(new double[] {0, 0, 0});
        final RealVector w = create(new double[] {1, 0, 0});
        v.cosine(w);
    }

    @Test(expected=MathArithmeticException.class)
    public void testCosinePrecondition2() {
        final RealVector v = create(new double[] {0, 0, 0});
        final RealVector w = create(new double[] {1, 0, 0});
        w.cosine(v);
    }

    @Test(expected=DimensionMismatchException.class)
    public void testCosinePrecondition3() {
        final RealVector v = create(new double[] {1, 2, 3});
        final RealVector w = create(new double[] {1, 2, 3, 4});
        v.cosine(w);
    }

    @Test(expected=DimensionMismatchException.class)
    public void testCombinePreconditionSameType() {
        final double a = 1d;
        final double b = 2d;
        double[] aux = new double[] { 3d, 4d, 5d };
        final RealVector x = create(aux);
        aux = new double[] { 6d, 7d };
        final RealVector y = create(aux);
        x.combine(a, b, y);
    }

    @Test
    public void testCombineSameType() {
        final Random random = new Random(20110726);
        final int dim = 10;
        final double a = (2 * random.nextDouble() - 1);
        final double b = (2 * random.nextDouble() - 1);
        final double[] dataX = new double[dim];
        final double[] dataY = new double[dim];
        final double[] expected = new double[dim];
        for (int i = 0; i < dim; i++) {
            dataX[i] = 2 * random.nextDouble() - 1;
            dataY[i] = 2 * random.nextDouble() - 1;
            expected[i] = a * dataX[i] + b * dataY[i];
        }
        final RealVector x = create(dataX);
        final RealVector y = create(dataY);
        final double[] actual = x.combine(a, b, y).toArray();
        for (int i = 0; i < dim; i++) {
            final double delta;
            if (expected[i] == 0d) {
                delta = Math.ulp(1d);
            } else {
                delta = Math.ulp(expected[i]);
            }
            Assert.assertEquals("elements [" + i + "] differ",
                                expected[i],
                                actual[i],
                                delta);
        }
    }

    @Test(expected=DimensionMismatchException.class)
    public void testCombinePreconditionMixedType() {
        final double a = 1d;
        final double b = 2d;
        double[] aux = new double[] { 3d, 4d, 5d };
        final RealVector x = create(aux);
        aux = new double[] { 6d, 7d };
        final RealVector y = create(aux);
        x.combine(a, b, y);
    }

    @Test
    public void testCombineMixedTypes() {
        final Random random = new Random(20110726);
        final int dim = 10;
        final double a = (2 * random.nextDouble() - 1);
        final double b = (2 * random.nextDouble() - 1);
        final double[] dataX = new double[dim];
        final double[] dataY = new double[dim];
        final double[] expected = new double[dim];
        for (int i = 0; i < dim; i++) {
            dataX[i] = 2 * random.nextDouble() - 1;
            dataY[i] = 2 * random.nextDouble() - 1;
            expected[i] = a * dataX[i] + b * dataY[i];
        }
        final RealVector x = create(dataX);
        final RealVector y = createAlien(dataY);

        final double[] actual = x.combine(a, b, y).toArray();
        for (int i = 0; i < dim; i++) {
            final double delta;
            if (expected[i] == 0d) {
                delta = Math.ulp(1d);
            } else {
                delta = Math.ulp(expected[i]);
            }
            Assert.assertEquals("elements [" + i + "] differ",
                                expected[i],
                                actual[i],
                                delta);
        }
    }

    @Test(expected=DimensionMismatchException.class)
    public void testCombineToSelfPreconditionSameType() {
        final double a = 1d;
        final double b = 2d;
        double[] aux = new double[] { 3d, 4d, 5d };
        final RealVector x = create(aux);
        aux = new double[] { 6d, 7d };
        final RealVector y = create(aux);
        x.combineToSelf(a, b, y);
    }

    @Test
    public void testCombineToSelfSameType() {
        final Random random = new Random(20110726);
        final int dim = 10;
        final double a = (2 * random.nextDouble() - 1);
        final double b = (2 * random.nextDouble() - 1);
        final double[] dataX = new double[dim];
        final double[] dataY = new double[dim];
        final double[] expected = new double[dim];
        for (int i = 0; i < dim; i++) {
            dataX[i] = 2 * random.nextDouble() - 1;
            dataY[i] = 2 * random.nextDouble() - 1;
            expected[i] = a * dataX[i] + b * dataY[i];
        }
        final RealVector x = create(dataX);
        final RealVector y = create(dataY);
        Assert.assertSame(x, x.combineToSelf(a, b, y));
        final double[] actual = x.toArray();
        for (int i = 0; i < dim; i++) {
            final double delta;
            if (expected[i] == 0d) {
                delta = Math.ulp(1d);
            } else {
                delta = Math.ulp(expected[i]);
            }
            Assert.assertEquals("elements [" + i + "] differ",
                                expected[i],
                                actual[i],
                                delta);
        }
    }

    @Test(expected=DimensionMismatchException.class)
    public void testCombineToSelfPreconditionMixedType() {
        final double a = 1d;
        final double b = 2d;
        double[] aux = new double[] { 3d, 4d, 5d };
        final RealVector x = create(aux);
        aux = new double[] { 6d, 7d };
        final RealVector y = createAlien(aux);
        x.combineToSelf(a, b, y);
    }

    @Test
    public void testCombineToSelfMixedTypes() {
        final Random random = new Random(20110726);
        final int dim = 10;
        final double a = (2 * random.nextDouble() - 1);
        final double b = (2 * random.nextDouble() - 1);
        final double[] dataX = new double[dim];
        final double[] dataY = new double[dim];
        final double[] expected = new double[dim];
        for (int i = 0; i < dim; i++) {
            dataX[i] = 2 * random.nextDouble() - 1;
            dataY[i] = 2 * random.nextDouble() - 1;
            expected[i] = a * dataX[i] + b * dataY[i];
        }
        final RealVector x = create(dataX);
        final RealVector y = create(dataY);
        Assert.assertSame(x, x.combineToSelf(a, b, y));
        final double[] actual = x.toArray();
        for (int i = 0; i < dim; i++) {
            final double delta;
            if (expected[i] == 0d) {
                delta = Math.ulp(1d);
            } else {
                delta = Math.ulp(expected[i]);
            }
            Assert.assertEquals("elements [" + i + "] differ",
                                expected[i],
                                actual[i],
                                delta);
        }
    }

    /*
     * TESTS OF THE VISITOR PATTERN
     */

    /** The whole vector is visited. */
    @Test
    public void testWalkInDefaultOrderPreservingVisitor1() {
        final double[] data = new double[] {
            0d, 1d, 0d, 0d, 2d, 0d, 0d, 0d, 3d
        };
        final RealVector v = create(data);
        final RealVectorPreservingVisitor visitor;
        visitor = new RealVectorPreservingVisitor() {

            private int expectedIndex;

            public void visit(final int actualIndex, final double actualValue) {
                Assert.assertEquals(expectedIndex, actualIndex);
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue, 0d);
                ++expectedIndex;
            }

            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(0, actualStart);
                Assert.assertEquals(data.length - 1, actualEnd);
                expectedIndex = 0;
            }

            public double end() {
                return 0.0;
            }
        };
        v.walkInDefaultOrder(visitor);
    }

    /** Visiting an invalid subvector. */
    @Test
    public void testWalkInDefaultOrderPreservingVisitor2() {
        final RealVector v = create(new double[5]);
        final RealVectorPreservingVisitor visitor;
        visitor = new RealVectorPreservingVisitor() {

            public void visit(int index, double value) {
                // Do nothing
            }

            public void start(int dimension, int start, int end) {
                // Do nothing
            }

            public double end() {
                return 0.0;
            }
        };
        try {
            v.walkInDefaultOrder(visitor, -1, 4);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInDefaultOrder(visitor, 5, 4);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInDefaultOrder(visitor, 0, -1);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInDefaultOrder(visitor, 0, 5);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInDefaultOrder(visitor, 4, 0);
            Assert.fail();
        } catch (NumberIsTooSmallException e) {
            // Expected behavior
        }
    }

    /** Visiting a valid subvector. */
    @Test
    public void testWalkInDefaultOrderPreservingVisitor3() {
        final double[] data = new double[] {
            0d, 1d, 0d, 0d, 2d, 0d, 0d, 0d, 3d
        };
        final int expectedStart = 2;
        final int expectedEnd = 7;
        final RealVector v = create(data);
        final RealVectorPreservingVisitor visitor;
        visitor = new RealVectorPreservingVisitor() {

            private int expectedIndex;

            public void visit(final int actualIndex, final double actualValue) {
                Assert.assertEquals(expectedIndex, actualIndex);
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue, 0d);
                ++expectedIndex;
            }

            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(expectedStart, actualStart);
                Assert.assertEquals(expectedEnd, actualEnd);
                expectedIndex = expectedStart;
            }

            public double end() {
                return 0.0;
            }
        };
        v.walkInDefaultOrder(visitor, expectedStart, expectedEnd);
    }

    /** The whole vector is visited. */
    @Test
    public void testWalkInOptimizedOrderPreservingVisitor1() {
        final double[] data = new double[] {
            0d, 1d, 0d, 0d, 2d, 0d, 0d, 0d, 3d
        };
        final RealVector v = create(data);
        final RealVectorPreservingVisitor visitor;
        visitor = new RealVectorPreservingVisitor() {
            private final boolean[] visited = new boolean[data.length];

            public void visit(final int actualIndex, final double actualValue) {
                visited[actualIndex] = true;
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue, 0d);
            }

            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(0, actualStart);
                Assert.assertEquals(data.length - 1, actualEnd);
                Arrays.fill(visited, false);
            }

            public double end() {
                for (int i = 0; i < data.length; i++) {
                    Assert.assertTrue("entry " + i + "has not been visited",
                                      visited[i]);
                }
                return 0.0;
            }
        };
        v.walkInOptimizedOrder(visitor);
    }

    /** Visiting an invalid subvector. */
    @Test
    public void testWalkInOptimizedOrderPreservingVisitor2() {
        final RealVector v = create(new double[5]);
        final RealVectorPreservingVisitor visitor;
        visitor = new RealVectorPreservingVisitor() {

            public void visit(int index, double value) {
                // Do nothing
            }

            public void start(int dimension, int start, int end) {
                // Do nothing
            }

            public double end() {
                return 0.0;
            }
        };
        try {
            v.walkInOptimizedOrder(visitor, -1, 4);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInOptimizedOrder(visitor, 5, 4);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInOptimizedOrder(visitor, 0, -1);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInOptimizedOrder(visitor, 0, 5);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInOptimizedOrder(visitor, 4, 0);
            Assert.fail();
        } catch (NumberIsTooSmallException e) {
            // Expected behavior
        }
    }

    /** Visiting a valid subvector. */
    @Test
    public void testWalkInOptimizedOrderPreservingVisitor3() {
        final double[] data = new double[] {
            0d, 1d, 0d, 0d, 2d, 0d, 0d, 0d, 3d
        };
        final int expectedStart = 2;
        final int expectedEnd = 7;
        final RealVector v = create(data);
        final RealVectorPreservingVisitor visitor;
        visitor = new RealVectorPreservingVisitor() {
            private final boolean[] visited = new boolean[data.length];

            public void visit(final int actualIndex, final double actualValue) {
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue, 0d);
                visited[actualIndex] = true;
            }

            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(expectedStart, actualStart);
                Assert.assertEquals(expectedEnd, actualEnd);
                Arrays.fill(visited, true);
            }

            public double end() {
                for (int i = expectedStart; i <= expectedEnd; i++) {
                    Assert.assertTrue("entry " + i + "has not been visited",
                                      visited[i]);
                }
                return 0.0;
            }
        };
        v.walkInOptimizedOrder(visitor, expectedStart, expectedEnd);
    }

    /** The whole vector is visited. */
    @Test
    public void testWalkInDefaultOrderChangingVisitor1() {
        final double[] data = new double[] {
            0d, 1d, 0d, 0d, 2d, 0d, 0d, 0d, 3d
        };
        final RealVector v = create(data);
        final RealVectorChangingVisitor visitor;
        visitor = new RealVectorChangingVisitor() {

            private int expectedIndex;

            public double visit(final int actualIndex, final double actualValue) {
                Assert.assertEquals(expectedIndex, actualIndex);
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue, 0d);
                ++expectedIndex;
                return actualIndex + actualValue;
            }

            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(0, actualStart);
                Assert.assertEquals(data.length - 1, actualEnd);
                expectedIndex = 0;
            }

            public double end() {
                return 0.0;
            }
        };
        v.walkInDefaultOrder(visitor);
        for (int i = 0; i < data.length; i++) {
            Assert.assertEquals("entry " + i, i + data[i], v.getEntry(i), 0.0);
        }
    }

    /** Visiting an invalid subvector. */
    @Test
    public void testWalkInDefaultOrderChangingVisitor2() {
        final RealVector v = create(new double[5]);
        final RealVectorChangingVisitor visitor;
        visitor = new RealVectorChangingVisitor() {

            public double visit(int index, double value) {
                return 0.0;
            }

            public void start(int dimension, int start, int end) {
                // Do nothing
            }

            public double end() {
                return 0.0;
            }
        };
        try {
            v.walkInDefaultOrder(visitor, -1, 4);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInDefaultOrder(visitor, 5, 4);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInDefaultOrder(visitor, 0, -1);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInDefaultOrder(visitor, 0, 5);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInDefaultOrder(visitor, 4, 0);
            Assert.fail();
        } catch (NumberIsTooSmallException e) {
            // Expected behavior
        }
    }

    /** Visiting a valid subvector. */
    @Test
    public void testWalkInDefaultOrderChangingVisitor3() {
        final double[] data = new double[] {
            0d, 1d, 0d, 0d, 2d, 0d, 0d, 0d, 3d
        };
        final int expectedStart = 2;
        final int expectedEnd = 7;
        final RealVector v = create(data);
        final RealVectorChangingVisitor visitor;
        visitor = new RealVectorChangingVisitor() {

            private int expectedIndex;

            public double visit(final int actualIndex, final double actualValue) {
                Assert.assertEquals(expectedIndex, actualIndex);
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue, 0d);
                ++expectedIndex;
                return actualIndex + actualValue;
            }

            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(expectedStart, actualStart);
                Assert.assertEquals(expectedEnd, actualEnd);
                expectedIndex = expectedStart;
            }

            public double end() {
                return 0.0;
            }
        };
        v.walkInDefaultOrder(visitor, expectedStart, expectedEnd);
        for (int i = expectedStart; i <= expectedEnd; i++) {
            Assert.assertEquals("entry " + i, i + data[i], v.getEntry(i), 0.0);
        }
    }

    /** The whole vector is visited. */
    @Test
    public void testWalkInOptimizedOrderChangingVisitor1() {
        final double[] data = new double[] {
            0d, 1d, 0d, 0d, 2d, 0d, 0d, 0d, 3d
        };
        final RealVector v = create(data);
        final RealVectorChangingVisitor visitor;
        visitor = new RealVectorChangingVisitor() {
            private final boolean[] visited = new boolean[data.length];

            public double visit(final int actualIndex, final double actualValue) {
                visited[actualIndex] = true;
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue, 0d);
                return actualIndex + actualValue;
            }

            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(0, actualStart);
                Assert.assertEquals(data.length - 1, actualEnd);
                Arrays.fill(visited, false);
            }

            public double end() {
                for (int i = 0; i < data.length; i++) {
                    Assert.assertTrue("entry " + i + "has not been visited",
                                      visited[i]);
                }
                return 0.0;
            }
        };
        v.walkInOptimizedOrder(visitor);
        for (int i = 0; i < data.length; i++) {
            Assert.assertEquals("entry " + i, i + data[i], v.getEntry(i), 0.0);
        }
    }

    /** Visiting an invalid subvector. */
    @Test
    public void testWalkInOptimizedOrderChangingVisitor2() {
        final RealVector v = create(new double[5]);
        final RealVectorChangingVisitor visitor;
        visitor = new RealVectorChangingVisitor() {

            public double visit(int index, double value) {
                return 0.0;
            }

            public void start(int dimension, int start, int end) {
                // Do nothing
            }

            public double end() {
                return 0.0;
            }
        };
        try {
            v.walkInOptimizedOrder(visitor, -1, 4);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInOptimizedOrder(visitor, 5, 4);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInOptimizedOrder(visitor, 0, -1);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInOptimizedOrder(visitor, 0, 5);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInOptimizedOrder(visitor, 4, 0);
            Assert.fail();
        } catch (NumberIsTooSmallException e) {
            // Expected behavior
        }
    }

    /** Visiting a valid subvector. */
    @Test
    public void testWalkInOptimizedOrderChangingVisitor3() {
        final double[] data = new double[] {
            0d, 1d, 0d, 0d, 2d, 0d, 0d, 0d, 3d
        };
        final int expectedStart = 2;
        final int expectedEnd = 7;
        final RealVector v = create(data);
        final RealVectorChangingVisitor visitor;
        visitor = new RealVectorChangingVisitor() {
            private final boolean[] visited = new boolean[data.length];

            public double visit(final int actualIndex, final double actualValue) {
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue, 0d);
                visited[actualIndex] = true;
                return actualIndex + actualValue;
            }

            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(expectedStart, actualStart);
                Assert.assertEquals(expectedEnd, actualEnd);
                Arrays.fill(visited, true);
            }

            public double end() {
                for (int i = expectedStart; i <= expectedEnd; i++) {
                    Assert.assertTrue("entry " + i + "has not been visited",
                                      visited[i]);
                }
                return 0.0;
            }
        };
        v.walkInOptimizedOrder(visitor, expectedStart, expectedEnd);
        for (int i = expectedStart; i <= expectedEnd; i++) {
            Assert.assertEquals("entry " + i, i + data[i], v.getEntry(i), 0.0);
        }
    }
}
