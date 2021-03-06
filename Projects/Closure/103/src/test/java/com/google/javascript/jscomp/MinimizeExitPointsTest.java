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

import com.google.javascript.rhino.Node;

/**
*
 */
public class MinimizeExitPointsTest extends CompilerTestCase {

  @Override
  protected CompilerPass getProcessor(final Compiler compiler) {
    return new CompilerPass() {
      public void process(Node externs, Node js) {
        NodeTraversal.traverse(compiler, js, new MinimizeExitPoints(compiler));
      }
    };
  }

  @Override
  protected int getNumRepetitions() {
    return 1;
  }

  void foldSame(String js) {
    testSame(js);
  }

  void fold(String js, String expected) {
    test(js, expected);
  }

  void fold(String js, String expected, DiagnosticType warning) {
    test(js, expected, warning);
  }

  public void testBreakOptimization() throws Exception {
    fold("f:{if(true){a();break f;}else;b();}",
         "f:{if(true){a()}else{b()}}");
    fold("f:{if(false){a();break f;}else;b();break f;}",
         "f:{if(false){a()}else{b()}}");
    fold("f:{if(a()){b();break f;}else;c();}",
         "f:{if(a()){b();}else{c();}}");
    fold("f:{if(a()){b()}else{c();break f;}}",
         "f:{if(a()){b()}else{c();}}");
    fold("f:{if(a()){b();break f;}else;}",
         "f:{if(a()){b();}else;}");
    fold("f:{if(a()){break f;}else;}",
         "f:{if(a()){}else;}");

    fold("f:while(a())break f;",
         "f:while(a())break f");
    foldSame("f:for(x in a())break f");

    fold("f:{while(a())break;}",
         "f:{while(a())break;}");
    foldSame("f:{for(x in a())break}");

    fold("f:try{break f;}catch(e){break f;}",
         "f:try{}catch(e){}");
    fold("f:try{if(a()){break f;}else{break f;} break f;}catch(e){}",
         "f:try{if(a()){}else{}}catch(e){}");

    fold("f:g:break f",
         "");
    fold("f:g:{if(a()){break f;}else{break f;} break f;}",
         "f:g:{if(a()){}else{}}");
  }

  public void testFunctionReturnOptimization() throws Exception {
    fold("function(){if(a()){b();if(c())return;}}",
         "function(){if(a()){b();if(c());}}");
    fold("function(){if(x)return; x=3; return; }",
         "function(){if(x); else x=3}");
    fold("function(){if(true){a();return;}else;b();}",
         "function(){if(true){a();}else{b();}}");
    fold("function(){if(false){a();return;}else;b();return;}",
         "function(){if(false){a();}else{b();}}");
    fold("function(){if(a()){b();return;}else;c();}",
         "function(){if(a()){b();}else{c();}}");
    fold("function(){if(a()){b()}else{c();return;}}",
         "function(){if(a()){b()}else{c();}}");
    fold("function(){if(a()){b();return;}else;}",
         "function(){if(a()){b();}else;}");
    fold("function(){if(a()){return;}else{return;} return;}",
         "function(){if(a()){}else{}}");
    fold("function(){if(a()){return;}else{return;} b();}",
         "function(){if(a()){}else{return;b()}}");

    fold("function(){while(a())return;}",
         "function(){while(a())return}");
    foldSame("function(){for(x in a())return}");

    fold("function(){while(a())break;}",
         "function(){while(a())break}");
    foldSame("function(){for(x in a())break}");

    fold("function(){try{return;}catch(e){return;}finally{return}}",
         "function(){try{}catch(e){}finally{}}");
    fold("function(){try{return;}catch(e){return;}}",
         "function(){try{}catch(e){}}");
    fold("function(){try{return;}finally{return;}}",
         "function(){try{}finally{}}");
    fold("function(){try{if(a()){return;}else{return;} return;}catch(e){}}",
         "function(){try{if(a()){}else{}}catch(e){}}");

    fold("function(){g:return}",
         "function(){}");
    fold("function(){g:if(a()){return;}else{return;} return;}",
         "function(){g:if(a()){}else{}}");
    fold("function(){try{g:if(a()){} return;}finally{return}}",
         "function(){try{g:if(a()){}}finally{}}");
  }

  public void testWhileContinueOptimization() throws Exception {
    fold("while(true){if(x)continue; x=3; continue; }",
         "while(true)if(x);else x=3");
    foldSame("while(true){a();continue;b();}");
    fold("while(true){if(true){a();continue;}else;b();}",
         "while(true){if(true){a();}else{b()}}");
    fold("while(true){if(false){a();continue;}else;b();continue;}",
         "while(true){if(false){a()}else{b();}}");
    fold("while(true){if(a()){b();continue;}else;c();}",
         "while(true){if(a()){b();}else{c();}}");
    fold("while(true){if(a()){b();}else{c();continue;}}",
         "while(true){if(a()){b();}else{c();}}");
    fold("while(true){if(a()){b();continue;}else;}",
         "while(true){if(a()){b();}else;}");
    fold("while(true){if(a()){continue;}else{continue;} continue;}",
         "while(true){if(a()){}else{}}");
    fold("while(true){if(a()){continue;}else{continue;} b();}",
         "while(true){if(a()){}else{continue;b();}}");

    fold("while(true)while(a())continue;",
         "while(true)while(a());");
    fold("while(true)for(x in a())continue",
         "while(true)for(x in a());");

    fold("while(true)while(a())break;",
         "while(true)while(a())break");
    fold("while(true)for(x in a())break",
         "while(true)for(x in a())break");

    fold("while(true){try{continue;}catch(e){continue;}}",
         "while(true){try{}catch(e){}}");
    fold("while(true){try{if(a()){continue;}else{continue;}" +
         "continue;}catch(e){}}",
         "while(true){try{if(a()){}else{}}catch(e){}}");

    fold("while(true){g:continue}",
         "while(true){}");
    // This case could be improved.
    fold("while(true){g:if(a()){continue;}else{continue;} continue;}",
         "while(true){g:if(a());else;}");
  }

  public void testDoContinueOptimization() throws Exception {
    fold("do{if(x)continue; x=3; continue; }while(true)",
         "do if(x); else x=3; while(true)");
    foldSame("do{a();continue;b()}while(true)");
    fold("do{if(true){a();continue;}else;b();}while(true)",
         "do{if(true){a();}else{b();}}while(true)");
    fold("do{if(false){a();continue;}else;b();continue;}while(true)",
         "do{if(false){a();}else{b();}}while(true)");
    fold("do{if(a()){b();continue;}else;c();}while(true)",
         "do{if(a()){b();}else{c()}}while(true)");
    fold("do{if(a()){b();}else{c();continue;}}while(true)",
         "do{if(a()){b();}else{c();}}while(true)");
    fold("do{if(a()){b();continue;}else;}while(true)",
         "do{if(a()){b();}else;}while(true)");
    fold("do{if(a()){continue;}else{continue;} continue;}while(true)",
         "do{if(a()){}else{}}while(true)");
    fold("do{if(a()){continue;}else{continue;} b();}while(true)",
         "do{if(a()){}else{continue; b();}}while(true)");

    fold("do{while(a())continue;}while(true)",
         "do while(a());while(true)");
    fold("do{for(x in a())continue}while(true)",
         "do for(x in a());while(true)");

    fold("do{while(a())break;}while(true)",
         "do while(a())break;while(true)");
    fold("do for(x in a())break;while(true)",
         "do for(x in a())break;while(true)");

    fold("do{try{continue;}catch(e){continue;}}while(true)",
         "do{try{}catch(e){}}while(true)");
    fold("do{try{if(a()){continue;}else{continue;}" +
         "continue;}catch(e){}}while(true)",
         "do{try{if(a()){}else{}}catch(e){}}while(true)");

    fold("do{g:continue}while(true)",
         "do{}while(true)");
    // This case could be improved.
    fold("do{g:if(a()){continue;}else{continue;} continue;}while(true)",
         "do{g:if(a());else;}while(true)");

    fold("do { foo(); continue; } while(false)",
         "do { foo(); } while(false)");
    fold("do { foo(); break; } while(false)",
         "do { foo(); } while(false)");
  }

  public void testForContinueOptimization() throws Exception {
    fold("for(x in y){if(x)continue; x=3; continue; }",
         "for(x in y)if(x);else x=3");
    foldSame("for(x in y){a();continue;b()}");
    fold("for(x in y){if(true){a();continue;}else;b();}",
         "for(x in y){if(true)a();else b();}");
    fold("for(x in y){if(false){a();continue;}else;b();continue;}",
         "for(x in y){if(false){a();}else{b()}}");
    fold("for(x in y){if(a()){b();continue;}else;c();}",
         "for(x in y){if(a()){b();}else{c();}}");
    fold("for(x in y){if(a()){b();}else{c();continue;}}",
         "for(x in y){if(a()){b();}else{c();}}");
    fold("for(x=0;x<y;x++){if(a()){b();continue;}else;}",
         "for(x=0;x<y;x++){if(a()){b();}else;}");
    fold("for(x=0;x<y;x++){if(a()){continue;}else{continue;} continue;}",
         "for(x=0;x<y;x++){if(a()){}else{}}");
    fold("for(x=0;x<y;x++){if(a()){continue;}else{continue;} b();}",
         "for(x=0;x<y;x++){if(a()){}else{continue; b();}}");

    fold("for(x=0;x<y;x++)while(a())continue;",
         "for(x=0;x<y;x++)while(a());");
    fold("for(x=0;x<y;x++)for(x in a())continue",
         "for(x=0;x<y;x++)for(x in a());");

    fold("for(x=0;x<y;x++)while(a())break;",
         "for(x=0;x<y;x++)while(a())break");
    foldSame("for(x=0;x<y;x++)for(x in a())break");

    fold("for(x=0;x<y;x++){try{continue;}catch(e){continue;}}",
         "for(x=0;x<y;x++){try{}catch(e){}}");
    fold("for(x=0;x<y;x++){try{if(a()){continue;}else{continue;}" +
         "continue;}catch(e){}}",
         "for(x=0;x<y;x++){try{if(a()){}else{}}catch(e){}}");

    fold("for(x=0;x<y;x++){g:continue}",
         "for(x=0;x<y;x++){}");
    // This case could be improved.
    fold("for(x=0;x<y;x++){g:if(a()){continue;}else{continue;} continue;}",
         "for(x=0;x<y;x++){g:if(a());else;}");
  }

  public void testCodeMotionDoesntBreakFunctionHoisting() throws Exception {
    fold("function f() { if (x) return; foo(); function foo() {} }",
         "function f() { if (x); else { function foo() {} foo(); } }");
  }
}
