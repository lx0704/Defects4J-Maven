/*
 * Copyright 2004 Google Inc.
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
import com.google.javascript.rhino.Node;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * CodePrinter prints out js code in either pretty format or compact format.
 *
 * @see CodeGenerator
*
*
 */
class CodePrinter {
  // The number of characters after which we insert a line break in the code
  static final int DEFAULT_LINE_LENGTH_THRESHOLD = 500;


  // There are two separate CodeConsumers, one for pretty-printing and
  // another for compact printing.  Both implement the interface
  // HasGetCode as CodeConsumer does not have a method for getting the
  // formatted string.

  // There are two implementations because the CompactCodePrinter
  // potentially has a very different implementation to the pretty
  // version.

  private interface HasGetCode {
    String getCode();
  }

  private abstract static class MappedCodePrinter extends CodeConsumer {
    final private Stack<Mapping> mappings;
    final private List<Mapping> allMappings;
    final private boolean createSrcMap;

    MappedCodePrinter(boolean createSrcMap) {
      this.createSrcMap = createSrcMap;
      this.mappings = createSrcMap ? new Stack<Mapping>() : null;
      this.allMappings = createSrcMap ? new ArrayList<Mapping>() : null;
    }

    /**
     * Maintains a mapping from a given node to the position
     * in the source code at which its generated form was
     * placed. This position is relative only to the current
     * run of the CodeConsumer and will be normalized
     * later on by the SourceMap.
     *
     * @see SourceMap
     */
    private static class Mapping {
      Node node;
      Position start;
      Position end;
    }

    /**
     * Starts the source mapping for the given
     * node at the current position.
     */
    @Override
    void startSourceMapping(Node node) {
      if (createSrcMap
          && node.getProp(Node.SOURCEFILE_PROP) != null
          && node.getLineno() > 0) {
        int line = getCurrentLineIndex();
        int index = getCurrentCharIndex();

        // If the index is -1, we are not performing any mapping.
        if (index >= 0) {
          Mapping mapping = new Mapping();
          mapping.node = node;
          mapping.start = new Position(line, index);
          mappings.push(mapping);
          allMappings.add(mapping);
        }
      }
    }

    /**
     * Finishes the source mapping for the given
     * node at the current position.
     */
    @Override
    void endSourceMapping(Node node) {
      if (createSrcMap
          && node.getProp(Node.SOURCEFILE_PROP) != null
          && node.getLineno() > 0) {
        int line = getCurrentLineIndex();
        int index = getCurrentCharIndex();

        // If the index is -1, we are not performing any mapping.
        if (index >= 0) {
          Preconditions.checkState(
              !mappings.empty(), "Mismatch in start and end of mapping");

          Mapping mapping = mappings.pop();
          mapping.end = new Position(line, index);
        }
      }
    }

    /**
     * Generates the source map from the given code consumer,
     * appending the information it saved to the SourceMap
     * object given.
     */
    @Override
    void generateSourceMap(SourceMap map){
      if (createSrcMap) {
        for (Mapping mapping : allMappings) {
          map.addMapping(mapping.node, mapping.start, mapping.end);
        }
      }
    }

    /**
     * Reports to the code consumer that the given line has been cut at the
     * given position (i.e. a \n has been inserted there). All mappings in
     * the source maps after that position will be renormalized as needed.
     */
    void reportLineCut(int lineIndex, int characterPosition) {
      if (createSrcMap) {
        for (Mapping mapping : allMappings) {
          mapping.start = convertPosition(mapping.start, lineIndex,
                                          characterPosition);

          if (mapping.end != null) {
            mapping.end = convertPosition(mapping.end, lineIndex,
                                          characterPosition);
          }
        }
      }
    }

    /**
     * Converts the given position by normalizing it against the insertion
     * of a newline at the given line and character position.
     *
     * @param position The existing position before the newline was inserted.
     * @param lineIndex The index of the line at which the newline was inserted.
     * @param characterPosition The position on the line at which the newline
     *     was inserted.
     *
     * @return The normalized position.
     */
    private Position convertPosition(Position position, int lineIndex,
                                     int characterPosition) {
      int pLine = position.getLineNumber();
      int pChar = position.getCharacterIndex();

      // If the position falls on the line itself, then normalize it
      // if it falls at or after the place the newline was inserted.
      if (position.getLineNumber() == lineIndex) {
        if (position.getCharacterIndex() >= characterPosition) {
          pLine++;
          pChar -= characterPosition;
        }
      }

      // If the position falls on a line after the newline, increment its
      // line index.
      if (position.getLineNumber() > lineIndex) {
        pLine++;
      }

      return new Position(pLine, pChar);
    }
  }

  private static class PrettyCodePrinter
      extends MappedCodePrinter
      implements HasGetCode {
    // The number of characters after which we insert a line break in the code
    static final String INDENT = "  ";

    private final StringBuilder code = new StringBuilder(1024);
    private final int lineLengthThreshold;
    private int indent = 0;
    private int lineLength = 0;
    private int lineIndex = 0;

    /**
     * @param lineLengthThreshold The length of a line after which we force
     *                            a newline when possible.
     */
    private PrettyCodePrinter(
        int lineLengthThreshold, boolean createSourceMap) {
      super(createSourceMap);
      this.lineLengthThreshold = lineLengthThreshold;
    }

    public String getCode() {
      return code.toString();
    }

    @Override
    char getLastChar() {
      return (code.length() > 0) ? code.charAt(code.length() - 1) : '\0';
    }

    @Override
    int getCurrentBufferLength() {
      return code.length();
    }

    @Override
    int getCurrentCharIndex() {
      return lineLength;
    }

    @Override
    int getCurrentLineIndex() {
      return lineIndex;
    }

    /**
     * Appends a string to the code, keeping track of the current line length.
     */
    @Override
    void append(String str) {
      // For pretty printing: indent at the beginning of the line
      if (lineLength == 0) {
        for (int i = 0; i < indent; i++) {
          code.append(INDENT);
          lineLength += INDENT.length();
        }
      }
      code.append(str);
      lineLength += str.length();
    }

    /**
     * Adds a newline to the code, resetting the line length and handling
     * indenting for pretty printing.
     */
    @Override
    void startNewLine() {
      if (lineLength > 0) {
        code.append('\n');
        lineIndex++;
        lineLength = 0;
      }
    }

    @Override
    void maybeLineBreak() {
      maybeCutLine();
    }

    /**
     * This may start a new line if the current line is longer than the line
     * length threshold.
     */
    @Override
    void maybeCutLine() {
      if (lineLength > lineLengthThreshold) {
        startNewLine();
      }
    }

    @Override
    void endLine() {
      startNewLine();
    }

    @Override
    void appendBlockStart() {
      append(" {");
      indent++;
    }

    @Override
    void appendBlockEnd() {
      endLine();
      indent--;
      append("}");
    }

    @Override
    void listSeparator() {
      add(", ");
      maybeLineBreak();
    }

    @Override
    void endFunction(boolean statementContext) {
      super.endFunction(statementContext);
      if (statementContext) {
        startNewLine();
      }
    }

    @Override
    void beginCaseBody() {
      super.beginCaseBody();
      indent++;
      endLine();
    }

    @Override
    void endCaseBody() {
      super.endCaseBody();
      indent--;
      endStatement();
    }

    @Override
    void appendOp(String op, boolean binOp) {
      if (binOp) {
        if (getLastChar() != ' ') {
          append(" ");
        }
        append(op);
        append(" ");
      } else {
        append(op);
      }
    }
  }


  static class CompactCodePrinter
      extends MappedCodePrinter
      implements HasGetCode {

    // The CompactCodePrinter tries to emit just enough newlines to stop there
    // being lines longer than the threshold.  Since the output is going to be
    // gzipped, it makes sense to try to make the newlines appear in similar
    // contexts so that GZIP can encode them for 'free'.
    //
    // This version tries to break the lines at 'preferred' places, which are
    // between the top-level forms.  This works because top level forms tend to
    // be more uniform than arbitary legal contexts.  Better compression would
    // probably require explicit modelling of the gzip algorithm.

    private final StringBuilder code = new StringBuilder(1024);

    private final boolean lineBreak;
    private final int lineLengthThreshold;

    private int lineIndex = 0;
    private int lineLength = 0;
    private int lineStartPosition = 0;
    private int preferredBreakPosition = 0;

  /**
   * @param lineBreak break the lines a bit more aggressively
   * @param lineLengthThreshold The length of a line after which we force
   *                            a newline when possible.
   * @param createSrcMap Whether to gather source position
   *                            mapping information when printing.
   */
    private CompactCodePrinter(boolean lineBreak, int lineLengthThreshold,
        boolean createSrcMap) {
      super(createSrcMap);
      this.lineBreak = lineBreak;
      this.lineLengthThreshold = lineLengthThreshold;
    }

    public String getCode() {
      return code.toString();
    }

    @Override
    char getLastChar() {
      return (code.length() > 0) ? code.charAt(code.length() - 1) : '\0';
    }

    @Override
    int getCurrentBufferLength() {
      return code.length();
    }

    @Override
    int getCurrentCharIndex() {
      return lineLength;
    }

    @Override
    int getCurrentLineIndex() {
      return lineIndex;
    }

    /**
     * Appends a string to the code, keeping track of the current line length.
     */
    @Override
    void append(String str) {
      code.append(str);
      lineLength += str.length();
    }

    /**
     * Adds a newline to the code, resetting the line length.
     */
    @Override
    void startNewLine() {
      if (lineLength > 0) {
        code.append('\n');
        lineLength = 0;
        lineIndex++;
        lineStartPosition = code.length();
      }
    }

    @Override
    void maybeLineBreak() {
      if (lineBreak) {
        if (sawFunction) {
          startNewLine();
          sawFunction = false;
        }
      }

      // Since we are at a legal line break, can we upgrade the
      // preferred break position?  We prefer to break after a
      // semicolon rather than before it.
      int len = code.length();
      if (preferredBreakPosition == len - 1) {
        char ch = code.charAt(len - 1);
        if (ch == ';') {
          preferredBreakPosition = len;
        }
      }
      maybeCutLine();
    }

    /**
     * This may start a new line if the current line is longer than the line
     * length threshold.
     */
    @Override
    void maybeCutLine() {
      if (lineLength > lineLengthThreshold) {
        // Use the preferred position provided it will break the line.
        if (preferredBreakPosition > lineStartPosition &&
            preferredBreakPosition < lineStartPosition + lineLength) {
          int position = preferredBreakPosition;
          code.insert(position, '\n');
          reportLineCut(lineIndex, position - lineStartPosition);
          lineIndex++;
          lineLength -= (position - lineStartPosition);
          lineStartPosition = position + 1;
        } else {
          startNewLine();
        }
      }
    }

    @Override
    void notePreferredLineBreak() {
      preferredBreakPosition = code.length();
    }
  }

  static class Builder {
    private final Node root;
    private boolean prettyPrint = false;
    private boolean lineBreak = false;
    private boolean outputTypes = false;
    private int lineLengthThreshold = DEFAULT_LINE_LENGTH_THRESHOLD;
    private SourceMap sourceMap = null;
    // Specify a charset to use when outputting source code.  If null,
    // then just output ASCII.
    private Charset outputCharset = null;
    private boolean validation = true;

    /**
     * Sets the root node from which to generate the source code.
     * @param node The root node.
     */
    Builder(Node node) {
      root = node;
    }

    /**
     * Sets whether pretty printing should be used.
     * @param prettyPrint If true, pretty printing will be used.
     */
    Builder setPrettyPrint(boolean prettyPrint) {
      this.prettyPrint = prettyPrint;
      return this;
    }

    /**
     * Sets whether line breaking should be done automatically.
     * @param lineBreak If true, line breaking is done automatically.
     */
    Builder setLineBreak(boolean lineBreak) {
      this.lineBreak = lineBreak;
      return this;
    }

    /**
     * Sets whether to output closure-style type annotations.
     * @param outputTypes If true, outputs closure-style type annotations.
     */
    Builder setOutputTypes(boolean outputTypes) {
      this.outputTypes = outputTypes;
      return this;
    }

    /**
     * Sets the line length threshold that will be used to determine
     * when to break lines, if line breaking is on.
     *
     * @param threshold The line length threshold.
     */
    Builder setLineLengthThreshold(int threshold) {
      this.lineLengthThreshold = threshold;
      return this;
    }

    /**
     * Sets the source map to which to write the metadata about
     * the generated source code.
     *
     * @param sourceMap The source map.
     */
    Builder setSourceMap(SourceMap sourceMap) {
      this.sourceMap = sourceMap;
      return this;
    }

    /**
     * Set the charset to use when determining what characters need to be
     * escaped in the output.
     */
    Builder setOutputCharset(Charset outCharset) {
      this.outputCharset = outCharset;
      return this;
    }

    /**
     * Whether the input AST guaranteed to be properly formed, fail if it isn't.
     */
    Builder setValidation(boolean validation) {
      this.validation = validation;
      return this;
    }

    /**
     * Generates the source code and returns it.
     */
    String build() {
      if (root == null) {
        throw new IllegalStateException(
            "Cannot build without root node being specified");
      }

      Format outputFormat = outputTypes
          ? Format.TYPED
          : prettyPrint
              ? Format.PRETTY
              : Format.COMPACT;

      return toSource(root, outputFormat, lineBreak, lineLengthThreshold,
          sourceMap, outputCharset, validation);
    }
  }

  enum Format {
    COMPACT,
    PRETTY,
    TYPED
  }

  /**
   * Converts a tree to js code
   */
  private static String toSource(Node root, Format outputFormat,
                                 boolean lineBreak,  int lineLengthThreshold,
                                 SourceMap sourceMap,
                                 Charset outputCharset,
                                 boolean validation) {
    boolean createSourceMap = (sourceMap != null);
    CodeConsumer cp =
        outputFormat == Format.COMPACT
        ? new CompactCodePrinter(
            lineBreak, lineLengthThreshold, createSourceMap)
        : new PrettyCodePrinter(lineLengthThreshold, createSourceMap);
    CodeGenerator cg =
        outputFormat == Format.TYPED
        ? new TypedCodeGenerator(cp, outputCharset)
        : new CodeGenerator(cp, outputCharset, validation);
    cg.add(root);

    String code = ((HasGetCode) cp).getCode();

    if (createSourceMap) {
      cp.generateSourceMap(sourceMap);
    }

    return code;
  }
}
