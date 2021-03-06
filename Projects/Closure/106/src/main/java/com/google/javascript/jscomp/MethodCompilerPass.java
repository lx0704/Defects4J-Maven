/*
 * Copyright 2007 Google Inc.
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

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import com.google.javascript.jscomp.NodeTraversal.AbstractPostOrderCallback;
import com.google.javascript.jscomp.NodeTraversal.Callback;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;

import java.util.List;
import java.util.Set;

/**
 * Finds all method declarations and pulls them into data structures
 * for use during cleanups such as arity checks or inlining.
 *
*
*
 */
abstract class MethodCompilerPass implements CompilerPass {
  /** List of methods defined in externs */
  final Set<String> externMethods = Sets.newHashSet();

  /** List of extern methods without signatures that we can't warn about */
  final Set<String> externMethodsWithoutSignatures = Sets.newHashSet();

  /** List of property names that may not be methods */
  final Set<String> nonMethodProperties = Sets.newHashSet();

  final Multimap<String, Node> methodDefinitions =
      Multimaps.newHashMultimap();

  final AbstractCompiler compiler;

  /**
   * The signature storage is provided by the implementing class.
   */
  interface SignatureStore {
    public void reset();
    public void addSignature(
        String functionName, Node functionNode, String sourceFile);
    public void removeSignature(String functionName);
  }

  MethodCompilerPass(AbstractCompiler compiler) {
    this.compiler = compiler;
  }

  public void process(Node externs, Node root) {
    externMethods.clear();
    externMethodsWithoutSignatures.clear();
    getSignatureStore().reset();
    methodDefinitions.clear();

    if (externs != null) {
      NodeTraversal.traverse(compiler, externs, new GetExternMethods());
    }


    List<Node> externsAndJs = Lists.newArrayList(externs, root);
    NodeTraversal.traverseRoots(
        compiler, Lists.newArrayList(externs, root), new GatherSignatures());
    NodeTraversal.traverseRoots(
        compiler, externsAndJs, getActingCallback());
  }

  /**
   * Subclasses should return a callback that does the actual work they
   * want to perform given the computed list of method signatures
   */
  abstract Callback getActingCallback();

  /**
   * Subclasses should return a SignatureStore for storing discovered
   * signatures.
   */
  abstract SignatureStore getSignatureStore();

  /**
   * Adds a node that may represent a function signature (if it's a function
   * itself or the name of a function).
   */
  private void addPossibleSignature(String name, Node node, NodeTraversal t) {
    boolean signatureAdded = false;

    if (node.getType() == Token.FUNCTION) {
      // The node we're looking at is a function, so we can add it directly
      addSignature(name, node, t.getSourceName());
      signatureAdded = true;
    } else if (node.getType() == Token.NAME) {
      // The one we're looking at is the name of a function, so look it up in
      // the current scope
      String functionName = node.getString();
      Scope.Var v = t.getScope().getVar(functionName);

      if (v == null) {
        if (compiler.isIdeMode()) {
          return;
        } else {
          throw new IllegalStateException(
              "VarCheck should have caught this undefined function");
        }
      }

      Node function = v.getInitialValue();
      if (function != null &&
          function.getType() == Token.FUNCTION) {
        addSignature(name, function, v.getInputName());
        signatureAdded = true;
      }
    }

    if (!signatureAdded) {
      nonMethodProperties.add(name);
    }
  }

  private void addSignature(String name, Node function, String fnSourceName) {
    if (externMethodsWithoutSignatures.contains(name)) {
      return;
    }

    getSignatureStore().addSignature(name, function, fnSourceName);
    methodDefinitions.put(name, function);
  }

  /**
   * Gathers methods from the externs file. Methods that are listed there but
   * do not have a signature are flagged to be ignored when doing arity checks.
   * Methods that do include signatures will be checked.
   */
  private class GetExternMethods extends AbstractPostOrderCallback {

    public void visit(NodeTraversal t, Node n, Node parent) {
      switch (n.getType()) {
        case Token.GETPROP:
        case Token.GETELEM: {
          Node dest = n.getFirstChild().getNext();

          if (dest.getType() != Token.STRING) {
            return;
          }

          String name = dest.getString();

          // We have a signature. Parse tree of the form:
          // assign                       <- parent
          //      getprop                 <- n
          //          name methods
          //          string setTimeout
          //      function
          if (parent.getType() == Token.ASSIGN &&
              parent.getFirstChild() == n &&
              n.getNext().getType() == Token.FUNCTION) {
            addSignature(name, n.getNext(), t.getSourceName());
          } else {
            getSignatureStore().removeSignature(name);
            externMethodsWithoutSignatures.add(name);
          }

          externMethods.add(name);
        } break;

        case Token.OBJECTLIT: {
          // assumes the object literal is well formed
          // (has an even number of children)
          for (Node key = n.getFirstChild();
               key != null; key = key.getNext().getNext()) {
            if (key.getType() == Token.STRING) {
              Node value = key.getNext();
              String name = key.getString();
              if (value.getType() == Token.FUNCTION) {
                addSignature(name, value, t.getSourceName());
              } else {
                getSignatureStore().removeSignature(name);
                externMethodsWithoutSignatures.add(name);
              }
              externMethods.add(name);
            }
          }
        } break;
      }
    }
  }

  /**
   * Gather signatures from the source to be compiled.
   */
  private class GatherSignatures extends AbstractPostOrderCallback {

    public void visit(NodeTraversal t, Node n, Node parent) {
      switch (n.getType()) {
        case Token.GETPROP:
        case Token.GETELEM:
          Node dest = n.getFirstChild().getNext();

          if (dest.getType() == Token.STRING) {
            if (dest.getString().equals("prototype")) {
              processPrototypeParent(t, parent);
            } else {
              // Static methods of the form Foo.bar = function() {} or
              // Static methods of the form Foo.bar = baz (where baz is a
              // function name). Parse tree looks like:
              // assign                 <- parent
              //      getprop           <- n
              //          name Foo
              //          string bar
              //      function or name  <- n.getNext()
              if (parent.getType() == Token.ASSIGN &&
                  parent.getFirstChild() == n) {
                addPossibleSignature(dest.getString(), n.getNext(), t);
              }
            }
          }
          break;

        case Token.OBJECTLIT:
          // assumes the object literal is well formed
          // (has an even number of children)
          for (Node key = n.getFirstChild();
               key != null; key = key.getNext().getNext()) {
            if (key.getType() == Token.STRING) {
              Node value = key.getNext();
              addPossibleSignature(key.getString(), value, t);
            }
          }
          break;
      }
    }

    /**
     * Processes the parent of a GETPROP prototype, which can either be
     * another GETPROP (in the case of Foo.prototype.bar), or can be
     * an assignment (in the case of Foo.prototype = ...).
     */
    private void processPrototypeParent(NodeTraversal t, Node n) {
      switch (n.getType()) {
        // Foo.prototype.getBar = function() { ... } or
        // Foo.prototype.getBar = getBaz (where getBaz is a function)
        // parse tree looks like:
        // assign                          <- parent
        //     getprop                     <- n
        //         getprop
        //             name Foo
        //             string prototype
        //         string getBar
        //     function or name            <- assignee
        case Token.GETPROP:
        case Token.GETELEM:
          Node dest = n.getFirstChild().getNext();
          Node parent = n.getParent().getParent();

          if (dest.getType() == Token.STRING &&
              parent.getType() == Token.ASSIGN) {
            Node assignee = parent.getFirstChild().getNext();

            addPossibleSignature(dest.getString(), assignee, t);
          }
          break;
      }
    }
  }
}
