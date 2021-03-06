/*
 * Copyright 2006 The Closure Compiler Authors.
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

/**
 * Tessts for {@link RemoveUnusedPrototypeProperties}.
 *
 * @author nicksantos@google.com (Nick Santos)
 */
public class RemoveUnusedPrototypePropertiesTest extends CompilerTestCase {
  private static final String EXTERNS =
      "IFoo.prototype.bar; var mExtern; mExtern.bExtern; mExtern['cExtern'];";

  private boolean canRemoveExterns = false;
  private boolean anchorUnusedVars = false;

  public RemoveUnusedPrototypePropertiesTest() {
    super(EXTERNS);
  }

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new RemoveUnusedPrototypeProperties(compiler,
        canRemoveExterns, anchorUnusedVars);
  }

  @Override
  public void setUp() {
    anchorUnusedVars = false;
    canRemoveExterns = false;
  }

  public void testAnalyzePrototypeProperties() {
    // Basic removal for prototype properties
    test("function e(){}" +
           "e.prototype.a = function(){};" +
           "e.prototype.b = function(){};" +
           "var x = new e; x.a()",
         "function e(){}" +
           "e.prototype.a = function(){};" +
           "var x = new e; x.a()");

    // Basic removal for prototype replacement
    test("function e(){}" +
           "e.prototype = {a: function(){}, b: function(){}};" +
           "var x=new e; x.a()",
         "function e(){}" +
           "e.prototype = {a: function(){}};" +
           "var x = new e; x.a()");

    // Unused properties that were referenced in the externs file should not be
    // removed
    test("function e(){}" +
           "e.prototype.a = function(){};" +
           "e.prototype.bExtern = function(){};" +
           "var x = new e;x.a()",
         "function e(){}" +
           "e.prototype.a = function(){};" +
           "e.prototype.bExtern = function(){};" +
           "var x = new e; x.a()");
    test("function e(){}" +
           "e.prototype = {a: function(){}, bExtern: function(){}};" +
           "var x = new e; x.a()",
         "function e(){}" +
           "e.prototype = {a: function(){}, bExtern: function(){}};" +
           "var x = new e; x.a()");
  }

  public void testAliasing() {
    // Aliasing a property is not enough for it to count as used
    test("function e(){}" +
           "e.prototype.method1 = function(){};" +
           "e.prototype.method2 = function(){};" +
           // aliases
           "e.prototype.alias1 = e.prototype.method1;" +
           "e.prototype.alias2 = e.prototype.method2;" +
           "var x = new e; x.method1()",
         "function e(){}" +
           "e.prototype.method1 = function(){};" +
           "var x = new e; x.method1()");

    // Using an alias should keep it
    test("function e(){}" +
           "e.prototype.method1 = function(){};" +
           "e.prototype.method2 = function(){};" +
           // aliases
           "e.prototype.alias1 = e.prototype.method1;" +
           "e.prototype.alias2 = e.prototype.method2;" +
           "var x=new e;x.alias1()",
         "function e(){}" +
           "e.prototype.method1 = function(){};" +
           "e.prototype.alias1 = e.prototype.method1;" +
           "var x = new e; x.alias1()");
  }

  public void testStatementRestriction() {
    test("function e(){}" +
           "var x = e.prototype.method1 = function(){};" +
           "var y = new e; x()",
         "function e(){}" +
           "var x = e.prototype.method1 = function(){};" +
           "var y = new e; x()");
  }

  public void testExportedMethodsByNamingConvention() {
    String classAndItsMethodAliasedAsExtern =
        "function Foo() {}" +
        "Foo.prototype.method = function() {};" +  // not removed
        "Foo.prototype.unused = function() {};" +  // removed
        "var _externInstance = new Foo();" +
        "Foo.prototype._externMethod = Foo.prototype.method";  // aliased here

    String compiled =
        "function Foo(){}" +
        "Foo.prototype.method = function(){};" +
        "var _externInstance = new Foo;" +
        "Foo.prototype._externMethod = Foo.prototype.method";

    test(classAndItsMethodAliasedAsExtern, compiled);
  }

  public void testMethodsFromExternsFileNotExported() {
    canRemoveExterns = true;
    String classAndItsMethodAliasedAsExtern =
        "function Foo() {}" +
        "Foo.prototype.bar_ = function() {};" +
        "Foo.prototype.unused = function() {};" +
        "var instance = new Foo;" +
        "Foo.prototype.bar = Foo.prototype.bar_";

    String compiled =
        "function Foo(){}" +
        "var instance = new Foo;";

    test(classAndItsMethodAliasedAsExtern, compiled);
  }

  public void testExportedMethodsByNamingConventionAlwaysExported() {
    canRemoveExterns = true;
    String classAndItsMethodAliasedAsExtern =
        "function Foo() {}" +
        "Foo.prototype.method = function() {};" +  // not removed
        "Foo.prototype.unused = function() {};" +  // removed
        "var _externInstance = new Foo();" +
        "Foo.prototype._externMethod = Foo.prototype.method";  // aliased here

    String compiled =
        "function Foo(){}" +
        "Foo.prototype.method = function(){};" +
        "var _externInstance = new Foo;" +
        "Foo.prototype._externMethod = Foo.prototype.method";

    test(classAndItsMethodAliasedAsExtern, compiled);
  }

  public void testExternMethodsFromExternsFile() {
    String classAndItsMethodAliasedAsExtern =
        "function Foo() {}" +
        "Foo.prototype.bar_ = function() {};" +  // not removed
        "Foo.prototype.unused = function() {};" +  // removed
        "var instance = new Foo;" +
        "Foo.prototype.bar = Foo.prototype.bar_";  // aliased here

    String compiled =
        "function Foo(){}" +
        "Foo.prototype.bar_ = function(){};" +
        "var instance = new Foo;" +
        "Foo.prototype.bar = Foo.prototype.bar_";

    test(classAndItsMethodAliasedAsExtern, compiled);
  }

  public void testPropertyReferenceGraph() {
    // test a prototype property graph that looks like so:
    // b -> a, c -> b, c -> a, d -> c, e -> a, e -> f
    String constructor = "function Foo() {}";
    String defA =
        "Foo.prototype.a = function() { Foo.superClass_.a.call(this); };";
    String defB = "Foo.prototype.b = function() { this.a(); };";
    String defC = "Foo.prototype.c = function() { " +
        "Foo.superClass_.c.call(this); this.b(); this.a(); };";
    String defD = "Foo.prototype.d = function() { this.c(); };";
    String defE = "Foo.prototype.e = function() { this.a(); this.f(); };";
    String defF = "Foo.prototype.f = function() { };";
    String fullClassDef = constructor + defA + defB + defC + defD + defE + defF;

    // ensure that all prototypes are compiled out if none are used
    test(fullClassDef, "");

    // make sure that the right prototypes are called for each use
    String callA = "(new Foo()).a();";
    String callB = "(new Foo()).b();";
    String callC = "(new Foo()).c();";
    String callD = "(new Foo()).d();";
    String callE = "(new Foo()).e();";
    String callF = "(new Foo()).f();";
    test(fullClassDef + callA, constructor + defA + callA);
    test(fullClassDef + callB, constructor + defA + defB + callB);
    test(fullClassDef + callC, constructor + defA + defB + defC + callC);
    test(fullClassDef + callD, constructor + defA + defB + defC + defD + callD);
    test(fullClassDef + callE, constructor + defA + defE + defF + callE);
    test(fullClassDef + callF, constructor + defF + callF);

    test(fullClassDef + callA + callC,
         constructor + defA + defB + defC + callA + callC);
    test(fullClassDef + callB + callC,
         constructor + defA + defB + defC + callB + callC);
    test(fullClassDef + callA + callB + callC,
         constructor + defA + defB + defC + callA + callB + callC);
  }

  public void testPropertiesDefinedWithGetElem() {
    testSame("function Foo() {} Foo.prototype['elem'] = function() {};");
    testSame("function Foo() {} Foo.prototype[1 + 1] = function() {};");
  }

  public void testNeverRemoveImplicitlyUsedProperties() {
    testSame("function Foo() {} " +
             "Foo.prototype.length = 3; " +
             "Foo.prototype.toString = function() { return 'Foo'; }; " +
             "Foo.prototype.valueOf = function() { return 'Foo'; }; ");
  }

  public void testPropertyDefinedInBranch() {
    test("function Foo() {} if (true) Foo.prototype.baz = function() {};",
         "if (true);");
    test("function Foo() {} while (true) Foo.prototype.baz = function() {};",
         "while (true);");
    test("function Foo() {} for (;;) Foo.prototype.baz = function() {};",
         "for (;;);");
    test("function Foo() {} do Foo.prototype.baz = function() {}; while(true);",
         "do; while(true);");
  }

  public void testUsingAnonymousObjectsToDefeatRemoval() {
    String constructor = "function Foo() {}";
    String declaration = constructor + "Foo.prototype.baz = 3;";
    test(declaration, "");
    testSame(declaration + "var x = {}; x.baz = 5;");
    testSame(declaration + "var x = {baz: 5};");
    test(declaration + "var x = {'baz': 5};",
         "var x = {'baz': 5};");
  }

  public void testGlobalFunctionsInGraph() {
    test(
        "var x = function() { (new Foo).baz(); };" +
        "var y = function() { x(); };" +
        "function Foo() {}" +
        "Foo.prototype.baz = function() { y(); };",
        "");
  }

  public void testGlobalFunctionsInGraph2() {
    // In this example, Foo.prototype.baz is a global reference to
    // Foo, and Foo has a reference to baz. So everything stays in.
    // TODO(nicksantos): We should be able to make the graph more fine-grained
    // here. Instead of Foo.prototype.bar creating a global reference to Foo,
    // it should create a reference from .bar to Foo. That will let us
    // compile this away to nothing.
    testSame(
        "var x = function() { (new Foo).baz(); };" +
        "var y = function() { x(); };" +
        "function Foo() { this.baz(); }" +
        "Foo.prototype.baz = function() { y(); };");
  }

  public void testGlobalFunctionsInGraph3() {
    test(
        "var x = function() { (new Foo).baz(); };" +
        "var y = function() { x(); };" +
        "function Foo() { this.baz(); }" +
        "Foo.prototype.baz = function() { x(); };",
        "var x = function() { (new Foo).baz(); };" +
        "function Foo() { this.baz(); }" +
        "Foo.prototype.baz = function() { x(); };");
  }

  public void testGlobalFunctionsInGraph4() {
    test(
        "var x = function() { (new Foo).baz(); };" +
        "var y = function() { x(); };" +
        "function Foo() { Foo.prototype.baz = function() { y(); }; }",
        "");
  }

  public void testGlobalFunctionsInGraph5() {
    test(
        "function Foo() {}" +
        "Foo.prototype.methodA = function() {};" +
        "function x() { (new Foo).methodA(); }" +
        "Foo.prototype.methodB = function() { x(); };",
        "");

    anchorUnusedVars = true;
    test(
        "function Foo() {}" +
        "Foo.prototype.methodA = function() {};" +
        "function x() { (new Foo).methodA(); }" +
        "Foo.prototype.methodB = function() { x(); };",

        "function Foo() {}" +
        "Foo.prototype.methodA = function() {};" +
        "function x() { (new Foo).methodA(); }");
  }

  public void testGlobalFunctionsInGraph6() {
    testSame(
        "function Foo() {}" +
        "Foo.prototype.methodA = function() {};" +
        "function x() { (new Foo).methodA(); }" +
        "Foo.prototype.methodB = function() { x(); };" +
        "(new Foo).methodB();");
  }

  public void testGlobalFunctionsInGraph7() {
    testSame(
        "function Foo() {}" +
        "Foo.prototype.methodA = function() {};" +
        "this.methodA();");
  }
}
