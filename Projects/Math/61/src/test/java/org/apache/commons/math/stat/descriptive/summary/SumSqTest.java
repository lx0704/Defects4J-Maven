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
package org.apache.commons.math.stat.descriptive.summary;

import org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic;
import org.apache.commons.math.stat.descriptive.StorelessUnivariateStatisticAbstractTest;
import org.apache.commons.math.stat.descriptive.UnivariateStatistic;

/**
 * Test cases for the {@link SumOfSquares} class.
 *
 * @version $Revision$ $Date$
 */
public class SumSqTest extends StorelessUnivariateStatisticAbstractTest{

    protected SumOfSquares stat;

    /**
     * @param name
     */
    public SumSqTest(String name) {
        super(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UnivariateStatistic getUnivariateStatistic() {
        return new SumOfSquares();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double expectedValue() {
        return this.sumSq;
    }

    public void testSpecialValues() {
        SumOfSquares sumSq = new SumOfSquares();
        assertEquals(0, sumSq.getResult(), 0);
        sumSq.increment(2d);
        assertEquals(4d, sumSq.getResult(), 0);
        sumSq.increment(Double.POSITIVE_INFINITY);
        assertEquals(Double.POSITIVE_INFINITY, sumSq.getResult(), 0);
        sumSq.increment(Double.NEGATIVE_INFINITY);
        assertEquals(Double.POSITIVE_INFINITY, sumSq.getResult(), 0);
        sumSq.increment(Double.NaN);
        assertTrue(Double.isNaN(sumSq.getResult()));
        sumSq.increment(1);
        assertTrue(Double.isNaN(sumSq.getResult()));
    }
    
    protected void checkClearValue(StorelessUnivariateStatistic statistic){
        assertEquals(0, statistic.getResult(), 0);
    }


}
