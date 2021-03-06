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
package org.apache.commons.math.stat.inference;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test cases for the OneWayAnovaImpl class.
 *
 * @version $Revision$ $Date$
 */

public class OneWayAnovaTest extends TestCase {

    protected OneWayAnova testStatistic = new OneWayAnovaImpl();
    
    private char[] wrongArray = { 'a', 'b', 'c' };
    private double[] emptyArray = {};

    private double[] classA =
            {93.0, 103.0, 95.0, 101.0, 91.0, 105.0, 96.0, 94.0, 101.0 };
    private double[] classB =
            {99.0, 92.0, 102.0, 100.0, 102.0, 89.0 };
    private double[] classC =
            {110.0, 115.0, 111.0, 117.0, 128.0, 117.0 };

    public OneWayAnovaTest(String name) {
        super(name);
    }

    public void setUp() {
    }

    public static Test Norun() {
        TestSuite suite = new TestSuite(OneWayAnovaTest.class);
        suite.setName("TestStatistic Tests");
        return suite;
    }

    public void testAnovaFValue() throws Exception {
        // Target comparison values computed using R version 2.6.0 (Linux version)
        List threeClasses = new ArrayList();
        threeClasses.add(classA);
        threeClasses.add(classB);
        threeClasses.add(classC);

        assertEquals("ANOVA F-value",  24.67361709460624,
                 testStatistic.anovaFValue(threeClasses), 1E-12);

        List twoClasses = new ArrayList();
        twoClasses.add(classA);
        twoClasses.add(classB);
        
        assertEquals("ANOVA F-value",  0.0150579150579,
                 testStatistic.anovaFValue(twoClasses), 1E-12);

        // now try some input hashes which should fail
        List wrongContents = new ArrayList();
        wrongContents.add(classC);
        wrongContents.add(wrongArray);
        try {
            testStatistic.anovaFValue(wrongContents);
            fail("non double[] hash value for key classX, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected
        }  

        List emptyContents = new ArrayList();
        emptyContents.add(emptyArray);
        emptyContents.add(classC);
        try {
            testStatistic.anovaFValue(emptyContents);
            fail("empty array for key classX, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected
        }  

        List tooFew = new ArrayList();
        tooFew.add(classA);
        try {
            testStatistic.anovaFValue(tooFew);
            fail("less than two classes, IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            // expected
        }  
    }
    

    public void testAnovaPValue() throws Exception {
        // Target comparison values computed using R version 2.6.0 (Linux version)
        List threeClasses = new ArrayList();
        threeClasses.add(classA);
        threeClasses.add(classB);
        threeClasses.add(classC);

        assertEquals("ANOVA P-value", 6.959446E-06,
                 testStatistic.anovaPValue(threeClasses), 1E-12);

        List twoClasses = new ArrayList();
        twoClasses.add(classA);
        twoClasses.add(classB);
        
        assertEquals("ANOVA P-value",  0.904212960464,
                 testStatistic.anovaPValue(twoClasses), 1E-12);

    }

    public void testAnovaTest() throws Exception {
        // Target comparison values computed using R version 2.3.1 (Linux version)
        List threeClasses = new ArrayList();
        threeClasses.add(classA);
        threeClasses.add(classB);
        threeClasses.add(classC);

        assertTrue("ANOVA Test P<0.01", testStatistic.anovaTest(threeClasses, 0.01));

        List twoClasses = new ArrayList();
        twoClasses.add(classA);
        twoClasses.add(classB);
        
        assertFalse("ANOVA Test P>0.01", testStatistic.anovaTest(twoClasses, 0.01));
    }

}