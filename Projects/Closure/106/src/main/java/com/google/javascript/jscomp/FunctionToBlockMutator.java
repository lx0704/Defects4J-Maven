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

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.javascript.jscomp.MakeDeclaredNamesUnique.InlineRenamer;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * A class to transform the body of a function into a generic block suitable
 * for inlining.
 *
*
 */
class FunctionToBlockMutator {

  private AbstractCompiler compiler;
  private Supplier<String> safeNameIdSupplier;


  FunctionToBlockMutator(
      AbstractCompiler compiler, Supplier<String> safeNameIdSupplier) {
    this.compiler = compiler;
    this.safeNameIdSupplier = safeNameIdSupplier;
  }

  /**
   * @param fnName The name to use when preparing human readable names.
   * @param fnNode The function to prepare.
   * @param callNode The call node that will be replaced.
   * @param resultName Function results should be assigned to this name.
   * @param needsDefaultResult Whether the result value must be set.
   * @param isCallInLoop Whether the function body must be prepared to be
   *   injected into the body of a loop.
   * @return A clone of the function body mutated to be suitable for injection
   *   as a statement into another code block.
   */
  Node mutate(String fnName, Node fnNode, Node callNode,
      String resultName, boolean needsDefaultResult, boolean isCallInLoop) {
    Node newFnNode = fnNode.cloneTree();
    // Now that parameter names have been replaced, make sure all the local
    // names are unique, to allow functions to be inlined multiple times
    // without causing conflicts.
    makeLocalNamesUnique(newFnNode, isCallInLoop);

    // TODO(johnlenz): Mark NAME nodes constant for parameters that are not
    // modified.
    Set<String> namesToAlias =
        FunctionArgumentInjector.findModifiedParameters(newFnNode);
    LinkedHashMap<String, Node> args =
        FunctionArgumentInjector.getFunctionCallParameterMap(
            newFnNode, callNode, this.safeNameIdSupplier);
    boolean hasArgs = !args.isEmpty();
    if (hasArgs) {
      FunctionArgumentInjector.maybeAddTempsForCallArguments(
          newFnNode, args, namesToAlias, compiler.getCodingConvention());
    }

    Node newBlock = NodeUtil.getFunctionBody(newFnNode);
    // Make the newBlock insertable .
    newBlock.detachFromParent();

    if (hasArgs) {
      Node inlineResult = aliasAndInlineArguments(newBlock,
          args, namesToAlias);
      Preconditions.checkState(newBlock == inlineResult);
    }

    //
    // For calls inlined into loops, VAR declarations are not reinitialized to
    // undefined as they would have been if the function were called, so ensure
    // that they are properly initialized.
    //
    if (isCallInLoop) {
      fixUnitializedVarDeclarations(newBlock);
    }

    String labelName = getLabelNameForFunction(fnName);
    Node injectableBlock = replaceReturns(
        newBlock, resultName, labelName, needsDefaultResult);
    Preconditions.checkState(injectableBlock != null);

    return injectableBlock;
  }


  /**
   *  For all VAR node with uninitialized declarations, set
   *  the values to be "undefined".
   */
  private void fixUnitializedVarDeclarations(Node n) {
    // Inner loop structure must already have logic to initialize its
    // variables.  In particular FOR-IN structures must not be modified.
    if (NodeUtil.isLoopStructure(n)) {
      return;
    }

    // For all VARs
    if (NodeUtil.isVar(n)) {
      Node name = n.getFirstChild();
      // It isn't initialized.
      if (!name.hasChildren()) {
        name.addChildToBack(Node.newString(Token.NAME, "undefined"));
      }
      return;
    }

    for (Node c = n.getFirstChild(); c != null; c = c.getNext()) {
      fixUnitializedVarDeclarations(c);
    }
  }


  /**
   * Fix-up all local names to be unique for this subtree.
   * @param fnNode A mutable instance of the function to be inlined.
   */
  private void makeLocalNamesUnique(Node fnNode, boolean isCallInLoop) {
    NodeTraversal.traverse(
        compiler, fnNode, new MakeDeclaredNamesUnique(
            new InlineRenamer(
                compiler.getUniqueNameIdSupplier(), 
                "JSCompiler_inline_",
                isCallInLoop)));
  }

  /**
   * Create a unique label name.
   */
  private String getLabelNameForFunction(String fnName){
    String name = (fnName == null || fnName.isEmpty()) ? "anon" : fnName;
    return "JSCompiler_inline_label_" + name + "_" + safeNameIdSupplier.get();
  }

  /**
   * Inlines the arguments within the node tree using the given argument map,
   * replaces "unsafe" names with local aliases.
   *
   * The aliases for unsafe require new VAR declarations, so this function
   * can not be used in for direct CALL node replacement as VAR nodes can not be
   * created there.
   *
   * @return The node or its replacement.
   */
  private Node aliasAndInlineArguments(
      Node fnTemplateRoot, LinkedHashMap<String, Node> argMap,
      Set<String> namesToAlias) {

    if (namesToAlias == null || namesToAlias.isEmpty()) {
      // There are no names to alias, just inline the arguments directly.
      Node result = FunctionArgumentInjector.inject(
          fnTemplateRoot, null, argMap);
      Preconditions.checkState(result == fnTemplateRoot);
      return result;
    } else {
      // Create local alias of names that can not be safely
      // used directly.

      // An arg map that will be updated to contain the
      // safe aliases.
      Map<String, Node> newArgMap = Maps.newHashMap(argMap);

      // Declare the alias in the same order as they
      // are declared.
      List<Node> newVars = Lists.newLinkedList();
      // NOTE: argMap is a linked map so we get the parameters in the
      // order that they were declared.
      for (Entry<String, Node> entry : argMap.entrySet()) {
        String name = entry.getKey();
        if (namesToAlias.contains(name)) {
          Node newValue = entry.getValue().cloneTree();
          Node newNode = NodeUtil.newVarNode(name, newValue);
          newVars.add(0, newNode);
          // Remove the parameter from the list to replace.
          newArgMap.remove(name);
        }
      }

      // Inline the arguments.
      Node result = FunctionArgumentInjector.inject(
          fnTemplateRoot, null, newArgMap);
      Preconditions.checkState(result == fnTemplateRoot);

      // Now that the names have been replaced, add the new aliases for
      // the old names.
      for (Node n : newVars) {
        fnTemplateRoot.addChildToFront(n);
      }

      return result;
    }
  }

  /**
   *  Convert returns to assignments and breaks, as needed.
   *  For example, with a lableName of 'foo':
   *    {
   *      return a;
   *    }
   *  becomes:
   *    foo: {
   *      a;
   *      break foo;
   *    }
   *  or
   *    foo: {
   *      resultName = a;
   *      break foo;
   *    }
   *
   * @param resultMustBeSet Whether the result must always be set to a value.
   * @return The node containing the transformed block, this may be different
   *     than the passed in node 'block'.
   */
  private static Node replaceReturns(
      Node block, String resultName, String labelName,
      boolean resultMustBeSet) {
    Preconditions.checkNotNull(block);
    Preconditions.checkNotNull(labelName);

    Node root = block;

    boolean hasReturnAtExit = false;
    int returnCount = NodeUtil.getNodeTypeReferenceCount(block, Token.RETURN);
    if (returnCount > 0) {
      hasReturnAtExit = hasReturnAtExit(block);
      // TODO(johnlenz): Simpler not to special case this,
      // and let it be optimized later.
      if (hasReturnAtExit) {
        convertLastReturnToStatement(block, resultName);
        returnCount--;
      }

      if (returnCount > 0) {
        // A label and breaks are needed.

        // Add the breaks
        replaceReturnWithBreak(block, null, resultName, labelName);

        // Add label
        Node label = new Node(Token.LABEL);
        Node name = Node.newString(Token.NAME, labelName);
        label.addChildToFront(name);
        label.addChildToBack(block);

        Node newRoot = new Node(Token.BLOCK);
        newRoot.addChildrenToBack(label);

        // The label is now the root.
        root = newRoot;
      }
    }

    // If there wasn't an return at the end of the function block, and we need
    // a result, add one to the block.
    if (resultMustBeSet && !hasReturnAtExit && resultName != null) {
      addDummyAssignment(block, resultName);
    }

    return root;
  }

  /**********************************************************************
   *  Functions following here are general node transformation functions
   **********************************************************************/

  /**
   * Example:
   *   a = (void) 0;
   */
  private static void addDummyAssignment(Node node, String resultName) {
    Preconditions.checkArgument(node.getType() == Token.BLOCK);

    // A result is needed create a dummy value.
    Node retVal = NodeUtil.newUndefinedNode();
    Node resultNode = createAssignStatementNode(resultName, retVal);

    node.addChildrenToBack(resultNode);
  }

  /**
   * Replace the 'return' statement with its child expression.
   *   "return foo()" becomes "foo()" or "resultName = foo()"
   *   "return" is removed or becomes "resultName = void 0".
   *
   * @param block
   * @param resultName
   */
  private static void convertLastReturnToStatement(
      Node block, String resultName) {
    Node ret = block.getLastChild();
    Preconditions.checkArgument(ret.getType() == Token.RETURN);
    Node resultNode = getReplacementReturnStatement(ret, resultName);

    if (resultNode == null) {
      block.removeChild(ret);
    } else {
      block.replaceChild(ret, resultNode);
    }
  }

  /**
   * Create a valid statement Node containing an assignment to name of the
   * given expression.
   */
  private static Node createAssignStatementNode(String name, Node expression) {
    // Create 'name = result-expression;' statement.
    // EXPR (ASSIGN (NAME, EXPRESSION))
    Node nameNode = Node.newString(Token.NAME, name);
    Node assign = new Node(Token.ASSIGN, nameNode, expression);
    return NodeUtil.newExpr(assign);
  }

  /**
   * Replace the 'return' statement with its child expression.
   * If the result is needed (resultName != null):
   *   "return foo()" becomes "resultName = foo()"
   *   "return" becomes "resultName = void 0".
   * Otherwise:
   *   "return foo()" becomes "foo()"
   *   "return", null is returned.
   */
  private static Node getReplacementReturnStatement(
      Node node, String resultName) {
    Node resultNode = null;

    Node retVal = null;
    if (node.hasChildren()) {
      // Clone the child as the child hasn't been removed
      // from the node yet.
      retVal = node.getFirstChild().cloneTree();
    }

    if (resultName == null) {
      if (retVal != null) {
        resultNode = NodeUtil.newExpr(retVal); // maybe null.
      }
    } else {
      if (retVal == null) {
        // A result is needed create a dummy value.
        retVal = NodeUtil.newUndefinedNode();
      }
      // Create a "resultName = retVal;" statement.
      resultNode = createAssignStatementNode(resultName, retVal);
    }

    return resultNode;
  }

  /**
   * @return Whether the given block end with an return statement.
   */
  private static boolean hasReturnAtExit(Node block) {
    // Only inline functions that return something (empty returns
    // will be handled by ConstFolding+EmptyFunctionRemoval)
    return (block.getLastChild().getType() == Token.RETURN);
  }

  /**
   * Replace the 'return' statement with its child expression.
   *   "return foo()" becomes "{foo(); break;}" or
   *      "{resultName = foo(); break;}"
   *   "return" becomes {break;} or "{resultName = void 0;break;}".
   */
  private static Node replaceReturnWithBreak(Node current, Node parent,
      String resultName, String labelName) {

    if (current.getType() == Token.FUNCTION
        || current.getType() == Token.EXPR_RESULT) {
      // Don't recurse into functions definitions, and expressions can't
      // contain RETURN nodes.
      return current;
    }

    if (current.getType() == Token.RETURN) {
      Preconditions.checkState(NodeUtil.isStatementBlock(parent));

      Node resultNode = getReplacementReturnStatement(current, resultName);
      Node name = Node.newString(Token.NAME, labelName);
      Node breakNode = new Node(Token.BREAK, name);

      // Replace the node in parent, and reset current to the first new child.
      parent.replaceChild(current, breakNode);
      if (resultNode != null) {
        parent.addChildBefore(resultNode, breakNode);
      }
      current = breakNode;
    } else {
      for (Node c = current.getFirstChild(); c != null; c = c.getNext()) {
        // c may be replaced.
        c = replaceReturnWithBreak(c, current, resultName, labelName);
      }
    }

    return current;
  }
}
