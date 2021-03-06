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
package org.apache.commons.math.transform;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.math.analysis.SinFunction;
import org.apache.commons.math.analysis.UnivariateFunction;
import org.apache.commons.math.analysis.function.Sinc;
import org.apache.commons.math.exception.MathIllegalArgumentException;
import org.apache.commons.math.exception.MathIllegalStateException;
import org.apache.commons.math.util.FastMath;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test case for fast sine transformer.
 * <p>
 * FST algorithm is exact, the small tolerance number is used only
 * to account for round-off errors.
 *
 * @version $Id$
 */
@RunWith(value = Parameterized.class)
public final class FastSineTransformerTest extends RealTransformerAbstractTest {

    private final boolean standard;

    private final int[] invalidDataSize;

    private final double[] relativeTolerance;

    private final int[] validDataSize;

    public FastSineTransformerTest(final boolean standard) {
        this.standard = standard;
        this.validDataSize = new int[] {
            1, 2, 4, 8, 16, 32, 64, 128
        };
        this.invalidDataSize = new int[] {
            129
        };
        this.relativeTolerance = new double[] {
            1E-15, 1E-15, 1E-14, 1E-14, 1E-13, 1E-12, 1E-11, 1E-11
        };
    }

    /**
     * Returns an array containing {@code true, false} in order to check both
     * standard and orthogonal DSTs.
     *
     * @return an array of parameters for this parameterized test
     */
    @Parameters
    public static Collection<Object[]> data() {
        final Object[][] data = new Boolean[][] {
            {
                Boolean.TRUE
            }, {
                Boolean.FALSE
            }
        };
        return Arrays.asList(data);
    }

    /**
     * {@inheritDoc}
     *
     * Overriding the default implementation allows to ensure that the first
     * element of the data set is zero.
     */
    @Override
    double[] createRealData(final int n) {
        final double[] data = super.createRealData(n);
        data[0] = 0.0;
        return data;
    }

    @Override
    RealTransformer createRealTransformer() {
        if (standard) {
            return new FastSineTransformer(DstNormalization.STANDARD_DST_I);
        } else {
            return new FastSineTransformer(DstNormalization.ORTHOGONAL_DST_I);
        }
    }

    @Override
    int getInvalidDataSize(final int i) {
        return invalidDataSize[i];
    }

    @Override
    int getNumberOfInvalidDataSizes() {
        return invalidDataSize.length;
    }

    @Override
    int getNumberOfValidDataSizes() {
        return validDataSize.length;
    }

    @Override
    double getRelativeTolerance(final int i) {
        return relativeTolerance[i];
    }

    @Override
    int getValidDataSize(final int i) {
        return validDataSize[i];
    }

    @Override
    UnivariateFunction getValidFunction() {
        return new Sinc();
    }

    @Override
    double getValidLowerBound() {
        return 0.0;
    }

    @Override
    double getValidUpperBound() {
        return FastMath.PI;
    }

    @Override
    double[] transform(final double[] x, final TransformType type) {
        final int n = x.length;
        final double[] y = new double[n];
        final double[] sin = new double[2 * n];
        for (int i = 0; i < sin.length; i++) {
            sin[i] = FastMath.sin(FastMath.PI * i / (double) n);
        }
        for (int j = 0; j < n; j++) {
            double yj = 0.0;
            for (int i = 0; i < n; i++) {
                yj += x[i] * sin[(i * j) % sin.length];
            }
            y[j] = yj;
        }
        final double s;
        if (type == TransformType.FORWARD) {
            s = standard ? 1.0 : FastMath.sqrt(2.0 / (double) n);
        } else if (type == TransformType.INVERSE){
            s = standard ? 2.0 / n : FastMath.sqrt(2.0 / (double) n);
        } else {
            /*
             * Should never occur. This clause is a safeguard in case other
             * types are used to TransformType (which should not be done).
             */
            throw new MathIllegalStateException();
        }
        TransformUtils.scaleArray(y, s);
        return y;
    }

    /*
     * Additional tests.
     */
    @Test
    public void testTransformRealFirstElementNotZero() {
        final TransformType[] type = TransformType.values();
        final double[] data = new double[] {
            1.0, 1.0, 1.0, 1.0
        };
        final RealTransformer transformer = createRealTransformer();
        for (int j = 0; j < type.length; j++) {
            try {
                transformer.transform(data, type[j]);
                Assert.fail(type[j].toString());
            } catch (MathIllegalArgumentException e) {
                // Expected: do nothing
            }
        }
    }

    /*
     * Additional (legacy) tests.
     */

    /**
     * Test of transformer for the ad hoc data.
     */
    @Test
    public void testAdHocData() {
        FastSineTransformer transformer = new FastSineTransformer(DstNormalization.STANDARD_DST_I);
        double result[], tolerance = 1E-12;

        double x[] = { 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0 };
        double y[] = { 0.0, 20.1093579685034, -9.65685424949238,
                       5.98642305066196, -4.0, 2.67271455167720,
                      -1.65685424949238, 0.795649469518633 };

        result = transformer.transform(x, TransformType.FORWARD);
        for (int i = 0; i < result.length; i++) {
            Assert.assertEquals(y[i], result[i], tolerance);
        }

        result = transformer.transform(y, TransformType.INVERSE);
        for (int i = 0; i < result.length; i++) {
            Assert.assertEquals(x[i], result[i], tolerance);
        }

        TransformUtils.scaleArray(x, FastMath.sqrt(x.length / 2.0));
        transformer = new FastSineTransformer(DstNormalization.ORTHOGONAL_DST_I);

        result = transformer.transform(y, TransformType.FORWARD);
        for (int i = 0; i < result.length; i++) {
            Assert.assertEquals(x[i], result[i], tolerance);
        }

        result = transformer.transform(x, TransformType.INVERSE);
        for (int i = 0; i < result.length; i++) {
            Assert.assertEquals(y[i], result[i], tolerance);
        }
    }

    /**
     * Test of transformer for the sine function.
     */
    @Test
    public void testSinFunction() {
        UnivariateFunction f = new SinFunction();
        FastSineTransformer transformer = new FastSineTransformer(DstNormalization.STANDARD_DST_I);
        double min, max, result[], tolerance = 1E-12; int N = 1 << 8;

        min = 0.0; max = 2.0 * FastMath.PI;
        result = transformer.transform(f, min, max, N, TransformType.FORWARD);
        Assert.assertEquals(N >> 1, result[2], tolerance);
        for (int i = 0; i < N; i += (i == 1 ? 2 : 1)) {
            Assert.assertEquals(0.0, result[i], tolerance);
        }

        min = -FastMath.PI; max = FastMath.PI;
        result = transformer.transform(f, min, max, N, TransformType.FORWARD);
        Assert.assertEquals(-(N >> 1), result[2], tolerance);
        for (int i = 0; i < N; i += (i == 1 ? 2 : 1)) {
            Assert.assertEquals(0.0, result[i], tolerance);
        }
    }

    /**
     * Test of parameters for the transformer.
     */
    @Test
    public void testParameters() throws Exception {
        UnivariateFunction f = new SinFunction();
        FastSineTransformer transformer = new FastSineTransformer(DstNormalization.STANDARD_DST_I);

        try {
            // bad interval
            transformer.transform(f, 1, -1, 64, TransformType.FORWARD);
            Assert.fail("Expecting IllegalArgumentException - bad interval");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            // bad samples number
            transformer.transform(f, -1, 1, 0, TransformType.FORWARD);
            Assert.fail("Expecting IllegalArgumentException - bad samples number");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            // bad samples number
            transformer.transform(f, -1, 1, 100, TransformType.FORWARD);
            Assert.fail("Expecting IllegalArgumentException - bad samples number");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }
}
