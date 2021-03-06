/*
 * Copyright 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.javascript.jscomp;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import junit.framework.TestCase;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * Tests for {@link JSModule}
 *
*
 */
public class JSModuleTest extends TestCase {
  private JSModule mod1;
  private JSModule mod2;  // depends on mod1
  private JSModule mod3;  // depends on mod1
  private JSModule mod4;  // depends on mod2, mod3
  private JSModule mod5;  // depends on mod1

  @Override
  protected void setUp() {
    List<JSModule> modulesInDepOrder = new ArrayList<JSModule>();

    mod1 = new JSModule("mod1");
    modulesInDepOrder.add(mod1);

    mod2 = new JSModule("mod2");
    mod2.addDependency(mod1);
    modulesInDepOrder.add(mod2);

    mod3 = new JSModule("mod3");
    mod3.addDependency(mod1);
    modulesInDepOrder.add(mod3);

    mod4 = new JSModule("mod4");
    mod4.addDependency(mod2);
    mod4.addDependency(mod3);
    modulesInDepOrder.add(mod4);

    mod5 = new JSModule("mod5");
    mod5.addDependency(mod1);
    modulesInDepOrder.add(mod5);
  }

  public void testDependencies() {
    assertEquals(ImmutableSet.of(), mod1.getAllDependencies());
    assertEquals(ImmutableSet.of(mod1), mod2.getAllDependencies());
    assertEquals(ImmutableSet.of(mod1), mod3.getAllDependencies());
    assertEquals(ImmutableSet.of(mod1, mod2, mod3), mod4.getAllDependencies());

    assertEquals(ImmutableSet.of(mod1), mod1.getThisAndAllDependencies());
    assertEquals(ImmutableSet.of(mod1, mod2), mod2.getThisAndAllDependencies());
    assertEquals(ImmutableSet.of(mod1, mod3), mod3.getThisAndAllDependencies());
    assertEquals(ImmutableSet.of(mod1, mod2, mod3, mod4),
                 mod4.getThisAndAllDependencies());
  }

  public void testSortInputs() {
    JSModule mod = new JSModule("mod");
    mod.add(JSSourceFile.fromCode("a.js",
        "goog.require('b');goog.require('c')"));
    mod.add(JSSourceFile.fromCode("b.js",
        "goog.provide('b');goog.require('d')"));
    mod.add(JSSourceFile.fromCode("c.js",
        "goog.provide('c');goog.require('d')"));
    mod.add(JSSourceFile.fromCode("d.js",
        "goog.provide('d')"));
    Compiler compiler = new Compiler(System.err);
    compiler.initCompilerOptionsIfTesting();
    mod.sortInputsByDeps(compiler);

    assertEquals(4, mod.getInputs().size());
    assertEquals("d.js", mod.getInputs().get(0).getSourceFile().getName());
    assertEquals("b.js", mod.getInputs().get(1).getSourceFile().getName());
    assertEquals("c.js", mod.getInputs().get(2).getSourceFile().getName());
    assertEquals("a.js", mod.getInputs().get(3).getSourceFile().getName());
  }

  public void testSortJsModules() {
    // already in order:
    assertEquals(ImmutableList.of(mod1, mod2, mod3, mod4),
        Arrays.asList(JSModule.sortJsModules(
            ImmutableList.of(mod1, mod2, mod3, mod4))));
    assertEquals(ImmutableList.of(mod1, mod3, mod2, mod4),
        Arrays.asList(JSModule.sortJsModules(
            ImmutableList.of(mod1, mod3, mod2, mod4))));

    // one out of order:
    assertEquals(ImmutableList.of(mod1, mod2, mod3, mod4),
        Arrays.asList(JSModule.sortJsModules(
            ImmutableList.of(mod4, mod3, mod2, mod1))));
    assertEquals(ImmutableList.of(mod1, mod3, mod2, mod4),
        Arrays.asList(JSModule.sortJsModules(
            ImmutableList.of(mod3, mod1, mod2, mod4))));

    // more out of order:
    assertEquals(ImmutableList.of(mod1, mod2, mod3, mod4),
        Arrays.asList(JSModule.sortJsModules(
            ImmutableList.of(mod4, mod3, mod1, mod2))));
  }
}
