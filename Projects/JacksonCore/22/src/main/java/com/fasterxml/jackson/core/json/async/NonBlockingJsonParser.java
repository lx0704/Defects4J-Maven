package com.fasterxml.jackson.core.json.async;

import java.io.IOException;
import java.io.OutputStream;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.async.ByteArrayFeeder;
import com.fasterxml.jackson.core.async.NonBlockingInputFeeder;
import com.fasterxml.jackson.core.io.CharTypes;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.json.ByteSourceJsonBootstrapper;
import com.fasterxml.jackson.core.sym.ByteQuadsCanonicalizer;
import com.fasterxml.jackson.core.util.VersionUtil;

public class NonBlockingJsonParser
    extends NonBlockingJsonParserBase
    implements ByteArrayFeeder
{
    // This is the main input-code lookup table, fetched eagerly
    private final static int[] _icUTF8 = CharTypes.getInputCodeUtf8();

    // Latin1 encoding is not supported, but we do use 8-bit subset for
    // pre-processing task, to simplify first pass, keep it fast.
    protected final static int[] _icLatin1 = CharTypes.getInputCodeLatin1();

    /*
    /**********************************************************************
    /* Input source config
    /**********************************************************************
     */

    /**
     * This buffer is actually provided via {@link NonBlockingInputFeeder}
     */
    protected byte[] _inputBuffer = NO_BYTES;

    /**
     * In addition to current buffer pointer, and end pointer,
     * we will also need to know number of bytes originally
     * contained. This is needed to correctly update location
     * information when the block has been completed.
     */
    protected int _origBufferLen;

    // And from ParserBase:
//  protected int _inputPtr;
//  protected int _inputEnd;

    /*
    /**********************************************************************
    /* Location tracking, additional
    /**********************************************************************
     */

    /**
     * Alternate row tracker, used to keep track of position by `\r` marker
     * (whereas <code>_currInputRow</code> tracks `\n`). Used to simplify
     * tracking of linefeeds, assuming that input typically uses various
     * linefeed combinations (`\r`, `\n` or `\r\n`) consistently, in which
     * case we can simply choose max of two row candidates.
     */
    protected int _currInputRowAlt = 1;

    /*
    /**********************************************************************
    /* Life-cycle
    /**********************************************************************
     */

    public NonBlockingJsonParser(IOContext ctxt, int parserFeatures,
            ByteQuadsCanonicalizer sym)
    {
        super(ctxt, parserFeatures, sym);
    }

    /*
    /**********************************************************************
    /* AsyncInputFeeder impl
    /**********************************************************************
     */

    @Override
    public ByteArrayFeeder getNonBlockingInputFeeder() {
        return this;
    }

    @Override
    public final boolean needMoreInput() {
        return (_inputPtr >=_inputEnd) && !_endOfInput;
    }

    @Override
    public void feedInput(byte[] buf, int start, int end) throws IOException
    {
        // Must not have remaining input
        if (_inputPtr < _inputEnd) {
            _reportError("Still have %d undecoded bytes, should not call 'feedInput'", _inputEnd - _inputPtr);
        }
        if (end < start) {
            _reportError("Input end (%d) may not be before start (%d)", end, start);
        }
        // and shouldn't have been marked as end-of-input
        if (_endOfInput) {
            _reportError("Already closed, can not feed more input");
        }
        // Time to update pointers first
        _currInputProcessed += _origBufferLen;

        // And then update buffer settings
        _inputBuffer = buf;
        _inputPtr = start;
        _inputEnd = end;
        _origBufferLen = end - start;
    }

    @Override
    public void endOfInput() {
        _endOfInput = true;
    }

    /*
    /**********************************************************************
    /* Abstract methods/overrides from JsonParser
    /**********************************************************************
     */

    /* Implementing these methods efficiently for non-blocking cases would
     * be complicated; so for now let's just use the default non-optimized
     * implementation
     */

//    public boolean nextFieldName(SerializableString str) throws IOException
//    public String nextTextValue() throws IOException
//    public int nextIntValue(int defaultValue) throws IOException
//    public long nextLongValue(long defaultValue) throws IOException
//    public Boolean nextBooleanValue() throws IOException

    @Override
    public int releaseBuffered(OutputStream out) throws IOException {
        int avail = _inputEnd - _inputPtr;
        if (avail > 0) {
            out.write(_inputBuffer, _inputPtr, avail);
        }
        return avail;
    }
    
    /*
    /**********************************************************************
    /* Main-level decoding
    /**********************************************************************
     */

    @Override
    public JsonToken nextToken() throws IOException
    {
        // First: regardless of where we really are, need at least one more byte;
        // can simplify some of the checks by short-circuiting right away
        if (_inputPtr >= _inputEnd) {
            if (_closed) {
                return null;
            }
            // note: if so, do not even bother changing state
            if (_endOfInput) { // except for this special case
                // End-of-input within (possibly...) started token is bit complicated,
                // so offline
                if (_currToken == JsonToken.NOT_AVAILABLE) {
                    return _finishTokenWithEOF();
                }
                return _eofAsNextToken();
            }
            return JsonToken.NOT_AVAILABLE;
        }
        // in the middle of tokenization?
        if (_currToken == JsonToken.NOT_AVAILABLE) {
            return _finishToken();
        }

        // No: fresh new token; may or may not have existing one
        _numTypesValid = NR_UNKNOWN;
        _tokenInputTotal = _currInputProcessed + _inputPtr;
        // also: clear any data retained so far
        _binaryValue = null;
        int ch = _inputBuffer[_inputPtr++] & 0xFF;

        switch (_majorState) {
        case MAJOR_INITIAL:
            return _startDocument(ch);

        case MAJOR_ROOT:
            return _startValue(ch);

        case MAJOR_OBJECT_FIELD_FIRST: // field or end-object
            // expect name
            return _startFieldName(ch);
        case MAJOR_OBJECT_FIELD_NEXT: // comma
            return _startFieldNameAfterComma(ch);

        case MAJOR_OBJECT_VALUE: // require semicolon first
            return _startValueAfterColon(ch);

        case MAJOR_ARRAY_ELEMENT_FIRST: // value without leading comma
            return _startValue(ch);

        case MAJOR_ARRAY_ELEMENT_NEXT: // require leading comma
            return _startValueAfterComma(ch);

        default:
        }
        VersionUtil.throwInternal();
        return null;
    }

    /**
     * Method called when decoding of a token has been started, but not yet completed due
     * to missing input; method is to continue decoding due to at least one more byte
     * being made available to decode.
     */
    protected final JsonToken _finishToken() throws IOException
    {
        // NOTE: caller ensures there's input available...
        switch (_minorState) {
        case MINOR_FIELD_LEADING_WS:
            return _startFieldName(_inputBuffer[_inputPtr++] & 0xFF);
        case MINOR_FIELD_LEADING_COMMA:
            return _startFieldNameAfterComma(_inputBuffer[_inputPtr++] & 0xFF);

        // Field name states
        case MINOR_FIELD_NAME:
            return _parseEscapedName(_quadLength,  _pending32, _pendingBytes);
        case MINOR_FIELD_NAME_ESCAPE:
            return _finishFieldWithEscape();

        // Value states

        case MINOR_VALUE_LEADING_WS:
            return _startValue(_inputBuffer[_inputPtr++] & 0xFF);
        case MINOR_VALUE_LEADING_COMMA:
            return _startValueAfterComma(_inputBuffer[_inputPtr++] & 0xFF);
        case MINOR_VALUE_LEADING_COLON:
            return _startValueAfterColon(_inputBuffer[_inputPtr++] & 0xFF);

        case MINOR_VALUE_TOKEN_NULL:
            return _finishKeywordToken("null", _pending32, JsonToken.VALUE_NULL);
        case MINOR_VALUE_TOKEN_TRUE:
            return _finishKeywordToken("true", _pending32, JsonToken.VALUE_TRUE);
        case MINOR_VALUE_TOKEN_FALSE:
            return _finishKeywordToken("false", _pending32, JsonToken.VALUE_FALSE);
        case MINOR_VALUE_TOKEN_ERROR: // case of "almost token", just need tokenize for error
            return _finishErrorToken();

        case MINOR_NUMBER_LEADING_MINUS:
            return _finishNumberLeadingMinus(_inputBuffer[_inputPtr++] & 0xFF);
        case MINOR_NUMBER_LEADING_ZERO:
            return _finishNumberLeadingZeroes();
        case MINOR_NUMBER_INTEGER_DIGITS:
            return _finishNumberIntegralPart(_textBuffer.getBufferWithoutReset(),
                    _textBuffer.getCurrentSegmentSize());
        case MINOR_NUMBER_FRACTION_DIGITS:
            return _finishFloatFraction();
        case MINOR_NUMBER_EXPONENT_MARKER:
            return _finishFloatExponent(true, _inputBuffer[_inputPtr++] & 0xFF);
        case MINOR_NUMBER_EXPONENT_DIGITS:
            return _finishFloatExponent(false, _inputBuffer[_inputPtr++] & 0xFF);

        case MINOR_VALUE_STRING:
            return _finishRegularString();
        case MINOR_VALUE_STRING_UTF8_2:
            _textBuffer.append((char) _decodeUTF8_2(_pending32, _inputBuffer[_inputPtr++]));
            return _finishRegularString();
        case MINOR_VALUE_STRING_UTF8_3:
            if (!_decodeSplitUTF8_3(_pending32, _pendingBytes, _inputBuffer[_inputPtr++])) {
                return JsonToken.NOT_AVAILABLE;
            }
            return _finishRegularString();
        case MINOR_VALUE_STRING_UTF8_4:
            if (!_decodeSplitUTF8_4(_pending32, _pendingBytes, _inputBuffer[_inputPtr++])) {
                return JsonToken.NOT_AVAILABLE;
            }
            return _finishRegularString();

        case MINOR_VALUE_STRING_ESCAPE:
            {
                int c = _decodeSplitEscaped(_quoted32, _quotedDigits);
                if (c < 0) {
                    return JsonToken.NOT_AVAILABLE;
                }
                _textBuffer.append((char) c);
            }
            return _finishRegularString();
        }
        VersionUtil.throwInternal();
        return null;
    }

    /**
     * Method similar to {@link #_finishToken}, but called when no more input is
     * available, and end-of-input has been detected. This is usually problem
     * case, but not always: root-level values may be properly terminated by
     * this, and similarly trailing white-space may have been skipped.
     */
    protected final JsonToken _finishTokenWithEOF() throws IOException
    {
        // NOTE: caller ensures there's input available...
        JsonToken t = _currToken;
        switch (_minorState) {
        case MINOR_ROOT_GOT_SEPARATOR: // fine, just skip some trailing space
            return _eofAsNextToken();

        case MINOR_VALUE_LEADING_WS: // finished at token boundary; probably fine
        case MINOR_VALUE_LEADING_COMMA: // not fine
        case MINOR_VALUE_LEADING_COLON: // not fine
            return _eofAsNextToken();
        case MINOR_VALUE_TOKEN_NULL:
            return _finishKeywordTokenWithEOF("null", _pending32, JsonToken.VALUE_NULL);
        case MINOR_VALUE_TOKEN_TRUE:
            return _finishKeywordTokenWithEOF("true", _pending32, JsonToken.VALUE_TRUE);
        case MINOR_VALUE_TOKEN_FALSE:
            return _finishKeywordTokenWithEOF("false", _pending32, JsonToken.VALUE_FALSE);
        case MINOR_VALUE_TOKEN_ERROR: // case of "almost token", just need tokenize for error
            return _finishErrorTokenWithEOF();

        // Number-parsing states; valid stopping points, more explicit errors
        case MINOR_NUMBER_LEADING_ZERO:
            return _valueCompleteInt(0, "0");
        case MINOR_NUMBER_INTEGER_DIGITS:
            // Fine: just need to ensure we have value fully defined
            {
                int len = _textBuffer.getCurrentSegmentSize();
                if (_numberNegative) {
                    --len;
                }
                _intLength = len;
            }
            return _valueComplete(JsonToken.VALUE_NUMBER_INT);

        case MINOR_NUMBER_FRACTION_DIGITS:
            _expLength = 0;
            // fall through
        case MINOR_NUMBER_EXPONENT_DIGITS:
            return _valueComplete(JsonToken.VALUE_NUMBER_FLOAT);

        case MINOR_NUMBER_EXPONENT_MARKER:
            _reportInvalidEOF(": was expecting fraction after exponent marker", JsonToken.VALUE_NUMBER_FLOAT);
            
        // !!! TODO: rest...
        default:
        }
        _reportInvalidEOF(": was expecting rest of token (internal state: "+_minorState+")", _currToken);
        return t; // never gets here
    }

    /*
    /**********************************************************************
    /* Second-level decoding, root level
    /**********************************************************************
     */

    private final JsonToken _startDocument(int ch) throws IOException
    {
        ch &= 0xFF;

        // Very first byte: could be BOM
        if (ch == ByteSourceJsonBootstrapper.UTF8_BOM_1) {
            // !!! TODO
        }

        // If not BOM (or we got past it), could be whitespace or comment to skip
        while (ch <= 0x020) {
            if (ch != INT_SPACE) {
                if (ch == INT_LF) {
                    ++_currInputRow;
                    _currInputRowStart = _inputPtr;
                } else if (ch == INT_CR) {
                    ++_currInputRowAlt;
                    _currInputRowStart = _inputPtr;
                } else if (ch != INT_TAB) {
                    _throwInvalidSpace(ch);
                }
            }
            if (_inputPtr >= _inputEnd) {
                _minorState = MINOR_ROOT_GOT_SEPARATOR;
                if (_closed) {
                    return null;
                }
                // note: if so, do not even bother changing state
                if (_endOfInput) { // except for this special case
                    return _eofAsNextToken();
                }
                return JsonToken.NOT_AVAILABLE;
            }
            ch = _inputBuffer[_inputPtr++] & 0xFF;
        }
        return _startValue(ch);
    }

    /*
    /**********************************************************************
    /* Second-level decoding, value parsing
    /**********************************************************************
     */
    
    /**
     * Helper method called to detect type of a value token (at any level), and possibly
     * decode it if contained in input buffer.
     * Value may be preceded by leading white-space, but no separator (comma).
     */
    private final JsonToken _startValue(int ch) throws IOException
    {
        // First: any leading white space?
        if (ch <= 0x0020) {
            ch = _skipWS(ch);
            if (ch <= 0) {
                _minorState = MINOR_VALUE_LEADING_WS;
                return _currToken;
            }
        }

        if (ch == INT_QUOTE) {
            return _startString();
        }
        switch (ch) {
        case '-':
            return _startNegativeNumber();

        // Should we have separate handling for plus? Although
        // it is not allowed per se, it may be erroneously used,
        // and could be indicate by a more specific error message.
        case '0':
            return _startLeadingZero();
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
            return _startPositiveNumber(ch);
        case 'f':
            return _startFalseToken();
        case 'n':
            return _startNullToken();
        case 't':
            return _startTrueToken();
        case '[':
            return _startArrayScope();
        case ']':
            return _closeArrayScope();
        case '{':
            return _startObjectScope();
        case '}':
            return _closeObjectScope();
        default:
        }
        return _startUnexpectedValue(ch);
    }

    /**
     * Helper method called to parse token that is either a value token in array
     * or end-array marker
     */
    private final JsonToken _startValueAfterComma(int ch) throws IOException
    {
        // First: any leading white space?
        if (ch <= 0x0020) {
            ch = _skipWS(ch); // will skip through all available ws (and comments)
            if (ch <= 0) {
                _minorState = MINOR_VALUE_LEADING_COMMA;
                return _currToken;
            }
        }
        if (ch != INT_COMMA) {
            if (ch == INT_RBRACKET) {
                return _closeArrayScope();
            }
            if (ch == INT_RCURLY){
                return _closeObjectScope();
            }
            _reportUnexpectedChar(ch, "was expecting comma to separate "+_parsingContext.typeDesc()+" entries");
        }
        int ptr = _inputPtr;
        if (ptr >= _inputEnd) {
            _minorState = MINOR_VALUE_LEADING_WS;
            return (_currToken = JsonToken.NOT_AVAILABLE);
        }
        ch = _inputBuffer[ptr];
        _inputPtr = ptr+1;
        if (ch <= 0x0020) {
            ch = _skipWS(ch);
            if (ch <= 0) {
                _minorState = MINOR_VALUE_LEADING_WS;
                return _currToken;
            }
        }
        if (ch == INT_QUOTE) {
            return _startString();
        }
        switch (ch) {
        case '-':
            return _startNegativeNumber();

        // Should we have separate handling for plus? Although
        // it is not allowed per se, it may be erroneously used,
        // and could be indicate by a more specific error message.
        case '0':
            return _startLeadingZero();

        case '1':
        case '2': case '3':
        case '4': case '5':
        case '6': case '7':
        case '8': case '9':
            return _startPositiveNumber(ch);
        case 'f':
            return _startFalseToken();
        case 'n':
            return _startNullToken();
        case 't':
            return _startTrueToken();
        case '[':
            return _startArrayScope();
        case ']':
            if (JsonParser.Feature.ALLOW_TRAILING_COMMA.enabledIn(_features)) {
                return _closeArrayScope();
            }
            break;
        case '{':
            return _startObjectScope();
        case '}':
            if (JsonParser.Feature.ALLOW_TRAILING_COMMA.enabledIn(_features)) {
                return _closeObjectScope();
            }
            break;
        default:
        }
        return _startUnexpectedValue(ch);
    }

    /**
     * Helper method called to detect type of a value token (at any level), and possibly
     * decode it if contained in input buffer.
     * Value MUST be preceded by a semi-colon (which may be surrounded by white-space)
     */
    private final JsonToken _startValueAfterColon(int ch) throws IOException
    {
        // First: any leading white space?
        if (ch <= 0x0020) {
            ch = _skipWS(ch); // will skip through all available ws (and comments)
            if (ch <= 0) {
                _minorState = MINOR_VALUE_LEADING_COLON;
                return _currToken;
            }
        }
        if (ch != INT_COLON) {
            // can not omit colon here
            _reportUnexpectedChar(ch, "was expecting a colon to separate field name and value");
        }
        int ptr = _inputPtr;
        if (ptr >= _inputEnd) {
            _minorState = MINOR_VALUE_LEADING_WS;
            return (_currToken = JsonToken.NOT_AVAILABLE);
        }
        ch = _inputBuffer[ptr];
        _inputPtr = ptr+1;
        if (ch <= 0x0020) {
            ch = _skipWS(ch); // will skip through all available ws (and comments)
            if (ch <= 0) {
                _minorState = MINOR_VALUE_LEADING_WS;
                return _currToken;
            }
        }
        if (ch == INT_QUOTE) {
            return _startString();
        }
        switch (ch) {
        case '-':
            return _startNegativeNumber();

        // Should we have separate handling for plus? Although
        // it is not allowed per se, it may be erroneously used,
        // and could be indicate by a more specific error message.
        case '0':
            return _startLeadingZero();

        case '1':
        case '2': case '3':
        case '4': case '5':
        case '6': case '7':
        case '8': case '9':
            return _startPositiveNumber(ch);
        case 'f':
            return _startFalseToken();
        case 'n':
            return _startNullToken();
        case 't':
            return _startTrueToken();
        case '[':
            return _startArrayScope();
        case ']':
            return _closeArrayScope();
        case '{':
            return _startObjectScope();
        case '}':
            return _closeObjectScope();
        default:
        }
        return _startUnexpectedValue(ch);
    }

    
    protected JsonToken _startUnexpectedValue(int ch) throws IOException
    {
        // TODO: Maybe support non-standard tokens that streaming parser does:
        //
        // * NaN
        // * Infinity
        // * Plus-prefix for numbers
        // * Apostrophe for Strings

        switch (ch) {
        case '\'':
            if (isEnabled(Feature.ALLOW_SINGLE_QUOTES)) {
                return _startAposString();
            }
            break;
        case ',':
            // If Feature.ALLOW_MISSING_VALUES is enabled we may allow "missing values",
            // that is, encountering a trailing comma or closing marker where value would be expected
            if (!_parsingContext.inObject() && isEnabled(Feature.ALLOW_MISSING_VALUES)) {
                // Important to "push back" separator, to be consumed before next value;
                // does not lead to infinite loop
               --_inputPtr;
               return _valueComplete(JsonToken.VALUE_NULL);
            }
            break;
        }
        // !!! TODO: maybe try to collect more information for better diagnostics
        _reportUnexpectedChar(ch, "expected a valid value (number, String, array, object, 'true', 'false' or 'null')");
        return null;
    }

    private final int _skipWS(int ch) throws IOException
    {
        do {
            if (ch != INT_SPACE) {
                if (ch == INT_LF) {
                    ++_currInputRow;
                    _currInputRowStart = _inputPtr;
                } else if (ch == INT_CR) {
                    ++_currInputRowAlt;
                    _currInputRowStart = _inputPtr;
                } else if (ch != INT_TAB) {
                    _throwInvalidSpace(ch);
                }
            }
            if (_inputPtr >= _inputEnd) {
                if (_endOfInput) { // except for this special case
                    _eofAsNextToken();
                } else {
                    _currToken = JsonToken.NOT_AVAILABLE;
                }
                return 0;
            }
            ch = _inputBuffer[_inputPtr++] & 0xFF;
        } while (ch <= 0x0020);
        return ch;
    }

    /*
    /**********************************************************************
    /* Second-level decoding, simple tokens
    /**********************************************************************
     */

    protected JsonToken _startFalseToken() throws IOException
    {
        int ptr = _inputPtr;
        if ((ptr + 4) < _inputEnd) { // yes, can determine efficiently
            byte[] buf = _inputBuffer;
            if ((buf[ptr++] == 'a') 
                   && (buf[ptr++] == 'l')
                   && (buf[ptr++] == 's')
                   && (buf[ptr++] == 'e')) {
                int ch = buf[ptr] & 0xFF;
                if (ch < INT_0 || (ch == INT_RBRACKET) || (ch == INT_RCURLY)) { // expected/allowed chars
                    _inputPtr = ptr;
                    return _valueComplete(JsonToken.VALUE_FALSE);
                }
            }
        }
        _minorState = MINOR_VALUE_TOKEN_FALSE;
        return _finishKeywordToken("false", 1, JsonToken.VALUE_FALSE);
    }

    protected JsonToken _startTrueToken() throws IOException
    {
        int ptr = _inputPtr;
        if ((ptr + 3) < _inputEnd) { // yes, can determine efficiently
            byte[] buf = _inputBuffer;
            if ((buf[ptr++] == 'r') 
                   && (buf[ptr++] == 'u')
                   && (buf[ptr++] == 'e')) {
                int ch = buf[ptr] & 0xFF;
                if (ch < INT_0 || (ch == INT_RBRACKET) || (ch == INT_RCURLY)) { // expected/allowed chars
                    _inputPtr = ptr;
                    return _valueComplete(JsonToken.VALUE_TRUE);
                }
            }
        }
        _minorState = MINOR_VALUE_TOKEN_TRUE;
        return _finishKeywordToken("true", 1, JsonToken.VALUE_TRUE);
    }

    protected JsonToken _startNullToken() throws IOException
    {
        int ptr = _inputPtr;
        if ((ptr + 3) < _inputEnd) { // yes, can determine efficiently
            byte[] buf = _inputBuffer;
            if ((buf[ptr++] == 'u') 
                   && (buf[ptr++] == 'l')
                   && (buf[ptr++] == 'l')) {
                int ch = buf[ptr] & 0xFF;
                if (ch < INT_0 || (ch == INT_RBRACKET) || (ch == INT_RCURLY)) { // expected/allowed chars
                    _inputPtr = ptr;
                    return _valueComplete(JsonToken.VALUE_NULL);
                }
            }
        }
        _minorState = MINOR_VALUE_TOKEN_NULL;
        return _finishKeywordToken("null", 1, JsonToken.VALUE_NULL);
    }

    protected JsonToken _finishKeywordToken(String expToken, int matched,
            JsonToken result) throws IOException
    {
        final int end = expToken.length();

        while (true) {
            if (_inputPtr >= _inputEnd) {
                _pending32 = matched;
                return (_currToken = JsonToken.NOT_AVAILABLE);
            }
            int ch = _inputBuffer[_inputPtr] & 0xFF;
            if (matched == end) { // need to verify trailing separator
                if (ch < INT_0 || (ch == INT_RBRACKET) || (ch == INT_RCURLY)) { // expected/allowed chars
                    return _valueComplete(result);
                }
                break;
            }
            if (ch != expToken.charAt(matched)) {
                break;
            }
            ++matched;
            ++_inputPtr;
        }
        _minorState = MINOR_VALUE_TOKEN_ERROR;
        _textBuffer.resetWithCopy(expToken, 0, matched);
        return _finishErrorToken();
    }

    protected JsonToken _finishKeywordTokenWithEOF(String expToken, int matched,
            JsonToken result) throws IOException
    {
        if (matched == expToken.length()) {
            return (_currToken = result);
        }
        _textBuffer.resetWithCopy(expToken, 0, matched);
        return _finishErrorTokenWithEOF();
    }

    protected JsonToken _finishErrorToken() throws IOException
    {
        while (_inputPtr < _inputEnd) {
            int i = (int) _inputBuffer[_inputPtr++];

// !!! TODO: Decode UTF-8 characters properly...
//            char c = (char) _decodeCharForError(i);

            char ch = (char) i;
            if (Character.isJavaIdentifierPart(ch)) {
                // 11-Jan-2016, tatu: note: we will fully consume the character,
                // included or not, so if recovery was possible, it'd be off-by-one...
                _textBuffer.append(ch);
                if (_textBuffer.size() < MAX_ERROR_TOKEN_LENGTH) {
                    continue;
                }
            }
            _reportError("Unrecognized token '%s': was expecting %s", _textBuffer.contentsAsString(),
                    "'null', 'true' or 'false'");
        }
        return (_currToken = JsonToken.NOT_AVAILABLE);
    }

    protected JsonToken _finishErrorTokenWithEOF() throws IOException
    {
        _reportError("Unrecognized token '%s': was expecting %s", _textBuffer.contentsAsString(),
                "'null', 'true' or 'false'");
        return JsonToken.NOT_AVAILABLE; // never gets here
    }

    /*
    /**********************************************************************
    /* Second-level decoding, Number decoding
    /**********************************************************************
     */

    protected JsonToken _startPositiveNumber(int ch) throws IOException
    {
        _numberNegative = false;
        // in unlikely event of not having more input, denote location
        if (_inputPtr >= _inputEnd) {
            _minorState = MINOR_NUMBER_INTEGER_DIGITS;
            _textBuffer.emptyAndGetCurrentSegment();
            _textBuffer.append((char) ch);
            return (_currToken = JsonToken.NOT_AVAILABLE);
        }
        char[] outBuf = _textBuffer.emptyAndGetCurrentSegment();
        outBuf[0] = (char) ch;
        ch = _inputBuffer[_inputPtr];

        int outPtr = 1;

        ch &= 0xFF;
        while (true) {
            if (ch < INT_0) {
                if (ch == INT_PERIOD) {
                    _intLength = outPtr;
                    ++_inputPtr;
                    return _startFloat(outBuf, outPtr, ch);
                }
                break;
            }
            if (ch > INT_9) {
                if (ch == INT_e || ch == INT_E) {
                    _intLength = outPtr;
                    ++_inputPtr;
                    return _startFloat(outBuf, outPtr, ch);
                }
                break;
            }
            if (outPtr >= outBuf.length) {
                // NOTE: must expand to ensure contents all in a single buffer (to keep
                // other parts of parsing simpler)
                outBuf = _textBuffer.expandCurrentSegment();
            }
            outBuf[outPtr++] = (char) ch;
            if (++_inputPtr >= _inputEnd) {
                _minorState = MINOR_NUMBER_INTEGER_DIGITS;
                _textBuffer.setCurrentLength(outPtr);
                return (_currToken = JsonToken.NOT_AVAILABLE);
            }
            ch = _inputBuffer[_inputPtr] & 0xFF;
        }
        _intLength = outPtr;
        _textBuffer.setCurrentLength(outPtr);
        return _valueComplete(JsonToken.VALUE_NUMBER_INT);
    }
    
    protected JsonToken _startNegativeNumber() throws IOException
    {
        if (_inputPtr >= _inputEnd) {
            _minorState = MINOR_NUMBER_LEADING_MINUS;
            return (_currToken = JsonToken.NOT_AVAILABLE);
        }
        int ch = _inputBuffer[_inputPtr++] & 0xFF;
        if (ch <= INT_0) {
            if (ch == INT_0) {
                return _startLeadingZero();
            }
            // One special case: if first char is 0, must not be followed by a digit
            reportUnexpectedNumberChar(ch, "expected digit (0-9) to follow minus sign, for valid numeric value");
        } else if (ch > INT_9) {
            // !!! TODO: -Infinite etc
            reportUnexpectedNumberChar(ch, "expected digit (0-9) to follow minus sign, for valid numeric value");
        }
        char[] outBuf = _textBuffer.emptyAndGetCurrentSegment();
        _numberNegative = true;
        outBuf[0] = '-';
        outBuf[1] = (char) ch;
        if (_inputPtr >= _inputEnd) {
            _minorState = MINOR_NUMBER_INTEGER_DIGITS;
            _textBuffer.setCurrentLength(2);
            _intLength = 1;
            return (_currToken = JsonToken.NOT_AVAILABLE);
        }
        ch = _inputBuffer[_inputPtr];
        int outPtr = 2;

        while (true) {
            if (ch < INT_0) {
                if (ch == INT_PERIOD) {
                    _intLength = outPtr-1;
                    ++_inputPtr;
                    return _startFloat(outBuf, outPtr, ch);
                }
                break;
            }
            if (ch > INT_9) {
                if (ch == INT_e || ch == INT_E) {
                    _intLength = outPtr-1;
                    ++_inputPtr;
                    return _startFloat(outBuf, outPtr, ch);
                }
                break;
            }
            if (outPtr >= outBuf.length) {
                // NOTE: must expand, to ensure contiguous buffer, outPtr is the length
                outBuf = _textBuffer.expandCurrentSegment();
            }
            outBuf[outPtr++] = (char) ch;
            if (++_inputPtr >= _inputEnd) {
                _minorState = MINOR_NUMBER_INTEGER_DIGITS;
                _textBuffer.setCurrentLength(outPtr);
                return (_currToken = JsonToken.NOT_AVAILABLE);
            }
            ch = _inputBuffer[_inputPtr] & 0xFF;
        }
        _intLength = outPtr-1;
        _textBuffer.setCurrentLength(outPtr);
        return _valueComplete(JsonToken.VALUE_NUMBER_INT);
    }

    protected JsonToken _startLeadingZero() throws IOException
    {
        int ptr = _inputPtr;
        if (ptr >= _inputEnd) {
            _minorState = MINOR_NUMBER_LEADING_ZERO;
            return (_currToken = JsonToken.NOT_AVAILABLE);
        }

        // While we could call `_finishNumberLeadingZeroes()`, let's try checking
        // the very first char after first zero since the most common case is that
        // there is a separator

        int ch = _inputBuffer[ptr++] & 0xFF;
        // one early check: leading zeroes may or may not be allowed
        if (ch <= INT_0) {
            if (ch == INT_PERIOD) {
                _inputPtr = ptr;
                _intLength = 1;
                char[] outBuf = _textBuffer.emptyAndGetCurrentSegment();
                outBuf[0] = '0';
                outBuf[1] = '.';
                return _startFloat(outBuf, 2, ch);
            }
            // although not guaranteed, seems likely valid separator (white space,
            // comma, end bracket/curly); next time token needed will verify
            if (ch == INT_0) {
                _inputPtr = ptr;
                _minorState = MINOR_NUMBER_LEADING_ZERO;
                return _finishNumberLeadingZeroes();
            }
        } else if (ch > INT_9) {
            if (ch == INT_e || ch == INT_E) {
                _inputPtr = ptr;
                _intLength = 1;
                char[] outBuf = _textBuffer.emptyAndGetCurrentSegment();
                outBuf[0] = '0';
                outBuf[1] = (char) ch;
                return _startFloat(outBuf, 2, ch);
            }
            // Ok; unfortunately we have closing bracket/curly that are valid so need
            // (colon not possible since this is within value, not after key)
            // 
            if ((ch != INT_RBRACKET) && (ch != INT_RCURLY)) {
                reportUnexpectedNumberChar(ch,
                        "expected digit (0-9), decimal point (.) or exponent indicator (e/E) to follow '0'");
            }
        }
        // leave _inputPtr as-is, to push back byte we checked
        return _valueCompleteInt(0, "0");
    }

    protected JsonToken _finishNumberLeadingZeroes() throws IOException
    {
        // In general, skip further zeroes (if allowed), look for legal follow-up
        // numeric characters; likely legal separators, or, known illegal (letters).

        while (true) {
            if (_inputPtr >= _inputEnd) {
                return (_currToken = JsonToken.NOT_AVAILABLE);
            }
            int ch = _inputBuffer[_inputPtr++] & 0xFF;
            if (ch <= INT_0) {
                if (ch == INT_PERIOD) {
                    _intLength = 1;
                    char[] outBuf = _textBuffer.emptyAndGetCurrentSegment();
                    outBuf[0] = '0';
                    outBuf[1] = '.';
                    return _startFloat(outBuf, 2, ch);
                }
                // although not guaranteed, seems likely valid separator (white space,
                // comma, end bracket/curly); next time token needed will verify
                if (ch == INT_0) {
                    if (!isEnabled(Feature.ALLOW_NUMERIC_LEADING_ZEROS)) {
                        reportInvalidNumber("Leading zeroes not allowed");
                    }
                    continue;
                }
            } else if (ch > INT_9) {
                if (ch == INT_e || ch == INT_E) {
                    _intLength = 1;
                    char[] outBuf = _textBuffer.emptyAndGetCurrentSegment();
                    outBuf[0] = '0';
                    outBuf[1] = (char) ch;
                    return _startFloat(outBuf, 2, ch);
                }
                // Ok; unfortunately we have closing bracket/curly that are valid so need
                // (colon not possible since this is within value, not after key)
                // 
                if ((ch != INT_RBRACKET) && (ch != INT_RCURLY)) {
                    reportUnexpectedNumberChar(ch,
                            "expected digit (0-9), decimal point (.) or exponent indicator (e/E) to follow '0'");
                }
            } else { // Number between 1 and 9; go integral
                --_inputPtr;
                _minorState = MINOR_NUMBER_INTEGER_DIGITS;
                return _finishNumberIntegralPart(_textBuffer.emptyAndGetCurrentSegment(), 0);
            }
            --_inputPtr;
            return _valueCompleteInt(0, "0");
        }
    }

    protected JsonToken _finishNumberLeadingMinus(int ch) throws IOException
    {
        if (ch <= INT_0) {
            if (ch == INT_0) {
                return _startLeadingZero();
            }
            // One special case: if first char is 0, must not be followed by a digit
            reportUnexpectedNumberChar(ch, "expected digit (0-9) to follow minus sign, for valid numeric value");
        } else if (ch > INT_9) {
            // !!! TODO: -Infinite etc
            reportUnexpectedNumberChar(ch, "expected digit (0-9) to follow minus sign, for valid numeric value");
        }
        char[] outBuf = _textBuffer.emptyAndGetCurrentSegment();
        _numberNegative = true;
        outBuf[0] = '-';
        outBuf[1] = (char) ch;
        _intLength = 1;
        _minorState = MINOR_NUMBER_INTEGER_DIGITS;
        return _finishNumberIntegralPart(outBuf, 2);
    }

    protected JsonToken _finishNumberIntegralPart(char[] outBuf, int outPtr) throws IOException
    {
        int negMod = _numberNegative ? -1 : 0;

        while (true) {
            if (_inputPtr >= _inputEnd) {
                _textBuffer.setCurrentLength(outPtr);
                return (_currToken = JsonToken.NOT_AVAILABLE);
            }
            int ch = _inputBuffer[_inputPtr] & 0xFF;
            if (ch < INT_0) {
                if (ch == INT_PERIOD) {
                    _intLength = outPtr+negMod;
                    ++_inputPtr;
                    return _startFloat(outBuf, outPtr, ch);
                }
                break;
            }
            if (ch > INT_9) {
                if (ch == INT_e || ch == INT_E) {
                    _intLength = outPtr+negMod;
                    ++_inputPtr;
                    return _startFloat(outBuf, outPtr, ch);
                }
                break;
            }
            ++_inputPtr;
            if (outPtr >= outBuf.length) {
                // NOTE: must expand to ensure contents all in a single buffer (to keep
                // other parts of parsing simpler)
                outBuf = _textBuffer.expandCurrentSegment();
            }
            outBuf[outPtr++] = (char) ch;
        }
        _intLength = outPtr+negMod;
        _textBuffer.setCurrentLength(outPtr);
        return _valueComplete(JsonToken.VALUE_NUMBER_INT);
    }

    protected JsonToken _startFloat(char[] outBuf, int outPtr, int ch) throws IOException
    {
        int fractLen = 0;
        if (ch == INT_PERIOD) {
            while (true) {
                if (outPtr >= outBuf.length) {
                    outBuf = _textBuffer.expandCurrentSegment();
                }
                outBuf[outPtr++] = (char) ch;
                if (_inputPtr >= _inputEnd) {
                    _textBuffer.setCurrentLength(outPtr);
                    _minorState = MINOR_NUMBER_FRACTION_DIGITS;
                    _fractLength = fractLen;
                    return (_currToken = JsonToken.NOT_AVAILABLE);
                }
                ch = _inputBuffer[_inputPtr++]; // ok to have sign extension for now
                if (ch < INT_0 || ch > INT_9) {
                    ch &= 0xFF; // but here we'll want to mask it to unsigned 8-bit
                    // must be followed by sequence of ints, one minimum
                    if (fractLen == 0) {
                        reportUnexpectedNumberChar(ch, "Decimal point not followed by a digit");
                    }
                    break;
                }
                ++fractLen;
            }
        }
        _fractLength = fractLen;
        int expLen = 0;
        if (ch == INT_e || ch == INT_E) { // exponent?
            if (outPtr >= outBuf.length) {
                outBuf = _textBuffer.expandCurrentSegment();
            }
            outBuf[outPtr++] = (char) ch;
            if (_inputPtr >= _inputEnd) {
                _textBuffer.setCurrentLength(outPtr);
                _minorState = MINOR_NUMBER_EXPONENT_MARKER;
                _expLength = 0;
                return (_currToken = JsonToken.NOT_AVAILABLE);
            }
            ch = _inputBuffer[_inputPtr++]; // ok to have sign extension for now
            if (ch == INT_MINUS || ch == INT_PLUS) {
                if (outPtr >= outBuf.length) {
                    outBuf = _textBuffer.expandCurrentSegment();
                }
                outBuf[outPtr++] = (char) ch;
                if (_inputPtr >= _inputEnd) {
                    _textBuffer.setCurrentLength(outPtr);
                    _minorState = MINOR_NUMBER_EXPONENT_DIGITS;
                    _expLength = 0;
                    return (_currToken = JsonToken.NOT_AVAILABLE);
                }
                ch = _inputBuffer[_inputPtr];
            }
            while (ch >= INT_0 && ch <= INT_9) {
                ++expLen;
                if (outPtr >= outBuf.length) {
                    outBuf = _textBuffer.expandCurrentSegment();
                }
                outBuf[outPtr++] = (char) ch;
                if (_inputPtr >= _inputEnd) {
                    _textBuffer.setCurrentLength(outPtr);
                    _minorState = MINOR_NUMBER_EXPONENT_DIGITS;
                    _expLength = expLen;
                    return (_currToken = JsonToken.NOT_AVAILABLE);
                }
                ch = _inputBuffer[_inputPtr++];
            }
            // must be followed by sequence of ints, one minimum
            ch &= 0xFF;
            if (expLen == 0) {
                reportUnexpectedNumberChar(ch, "Exponent indicator not followed by a digit");
            }
        }
        // push back the last char
        --_inputPtr;
        _textBuffer.setCurrentLength(outPtr);
        // negative, int-length, fract-length already set, so...
        _expLength = expLen;
        return _valueComplete(JsonToken.VALUE_NUMBER_FLOAT);
    }

    protected JsonToken _finishFloatFraction() throws IOException
    {
        int fractLen = _fractLength;
        char[] outBuf = _textBuffer.getBufferWithoutReset();
        int outPtr = _textBuffer.getCurrentSegmentSize();

        // caller guarantees at least one char; also, sign-extension not needed here
        int ch;
        while (((ch = _inputBuffer[_inputPtr++]) >= INT_0) && (ch <= INT_9)) {
            ++fractLen;
            if (outPtr >= outBuf.length) {
                outBuf = _textBuffer.expandCurrentSegment();
            }
            outBuf[outPtr++] = (char) ch;
            if (_inputPtr >= _inputEnd) {
                _textBuffer.setCurrentLength(outPtr);
                _fractLength = fractLen;
                return JsonToken.NOT_AVAILABLE;
            }
        }
        
        // Ok, fraction done; what have we got next?
        // must be followed by sequence of ints, one minimum
        if (fractLen == 0) {
            reportUnexpectedNumberChar(ch, "Decimal point not followed by a digit");
        }
        _fractLength = fractLen;
        _textBuffer.setCurrentLength(outPtr);

        // Ok: end of floating point number or exponent?
        if (ch == INT_e || ch == INT_E) { // exponent?
            _textBuffer.append((char) ch);
            _expLength = 0;
            if (_inputPtr >= _inputEnd) {
                _minorState = MINOR_NUMBER_EXPONENT_MARKER;
                return JsonToken.NOT_AVAILABLE;
            }
            _minorState = MINOR_NUMBER_EXPONENT_DIGITS;
            return _finishFloatExponent(true, _inputBuffer[_inputPtr++] & 0xFF);
        }

        // push back the last char
        --_inputPtr;
        _textBuffer.setCurrentLength(outPtr);
        // negative, int-length, fract-length already set, so...
        _expLength = 0;
        return _valueComplete(JsonToken.VALUE_NUMBER_FLOAT);
    }

    protected JsonToken _finishFloatExponent(boolean checkSign, int ch) throws IOException
    {
        if (checkSign) {
            _minorState = MINOR_NUMBER_EXPONENT_DIGITS;
            if (ch == INT_MINUS || ch == INT_PLUS) {
                _textBuffer.append((char) ch);
                if (_inputPtr >= _inputEnd) {
                    _minorState = MINOR_NUMBER_EXPONENT_DIGITS;
                    _expLength = 0;
                    return JsonToken.NOT_AVAILABLE;
                }
                ch = _inputBuffer[_inputPtr];
            }
        }

        char[] outBuf = _textBuffer.getBufferWithoutReset();
        int outPtr = _textBuffer.getCurrentSegmentSize();
        int expLen = _expLength;

        while (ch >= INT_0 && ch <= INT_9) {
            ++expLen;
            if (outPtr >= outBuf.length) {
                outBuf = _textBuffer.expandCurrentSegment();
            }
            outBuf[outPtr++] = (char) ch;
            if (_inputPtr >= _inputEnd) {
                _textBuffer.setCurrentLength(outPtr);
                _expLength = expLen;
                return JsonToken.NOT_AVAILABLE;
            }
            ch = _inputBuffer[_inputPtr++];
        }
        // must be followed by sequence of ints, one minimum
        ch &= 0xFF;
        if (expLen == 0) {
            reportUnexpectedNumberChar(ch, "Exponent indicator not followed by a digit");
        }
        // push back the last char
        --_inputPtr;
        _textBuffer.setCurrentLength(outPtr);
        // negative, int-length, fract-length already set, so...
        _expLength = expLen;
        return _valueComplete(JsonToken.VALUE_NUMBER_FLOAT);
    }
 
    /*
    /**********************************************************************
    /* Second-level decoding, Primary name decoding
    /**********************************************************************
     */

    /**
     * Method that handles initial token type recognition for token
     * that has to be either FIELD_NAME or END_OBJECT.
     */
    private final JsonToken _startFieldName(int ch) throws IOException
    {
        // First: any leading white space?
        if (ch <= 0x0020) {
            ch = _skipWS(ch);
            if (ch <= 0) {
                _minorState = MINOR_FIELD_LEADING_WS;
                return _currToken;
            }
        }
        _updateLocation();
        if (ch != INT_QUOTE) {
            if (ch == INT_RCURLY) {
                return _closeObjectScope();
            }
            return _handleOddName(ch);
        }
        // First: can we optimize out bounds checks?
        if ((_inputPtr + 13) <= _inputEnd) { // Need up to 12 chars, plus one trailing (quote)
            String n = _fastParseName();
            if (n != null) {
                return _fieldComplete(n);
            }
        }
        return _parseEscapedName(0, 0, 0);
    }

    private final JsonToken _startFieldNameAfterComma(int ch) throws IOException
    {
        // First: any leading white space?
        if (ch <= 0x0020) {
            ch = _skipWS(ch); // will skip through all available ws (and comments)
            if (ch <= 0) {
                _minorState = MINOR_FIELD_LEADING_COMMA;
                return _currToken;
            }
        }
        if (ch != INT_COMMA) { // either comma, separating entries, or closing right curly
            if (ch == INT_RCURLY) {
                return _closeObjectScope();
            }
            _reportUnexpectedChar(ch, "was expecting comma to separate "+_parsingContext.typeDesc()+" entries");
        }
        int ptr = _inputPtr;
        if (ptr >= _inputEnd) {
            _minorState = MINOR_FIELD_LEADING_WS;
            return (_currToken = JsonToken.NOT_AVAILABLE);
        }
        ch = _inputBuffer[ptr];
        _inputPtr = ptr+1;
        if (ch <= 0x0020) {
            ch = _skipWS(ch);
            if (ch <= 0) {
                _minorState = MINOR_FIELD_LEADING_WS;
                return _currToken;
            }
        }
        _updateLocation();
        if (ch != INT_QUOTE) {
            if (ch == INT_RCURLY) {
                if (JsonParser.Feature.ALLOW_TRAILING_COMMA.enabledIn(_features)) {
                    return _closeObjectScope();
                }
            }
            return _handleOddName(ch);
        }
        // First: can we optimize out bounds checks?
        if ((_inputPtr + 13) <= _inputEnd) { // Need up to 12 chars, plus one trailing (quote)
            String n = _fastParseName();
            if (n != null) {
                return _fieldComplete(n);
            }
        }
        return _parseEscapedName(0, 0, 0);
    }

    /*
    /**********************************************************************
    /* Name-decoding, tertiary decoding
    /**********************************************************************
     */

    private final String _fastParseName() throws IOException
    {
        // If so, can also unroll loops nicely
        // This may seem weird, but here we do NOT want to worry about UTF-8
        // decoding. Rather, we'll assume that part is ok (if not it will be
        // caught later on), and just handle quotes and backslashes here.

        final byte[] input = _inputBuffer;
        final int[] codes = _icLatin1;
        int ptr = _inputPtr;

        int q0 = input[ptr++] & 0xFF;
        if (codes[q0] == 0) {
            int i = input[ptr++] & 0xFF;
            if (codes[i] == 0) {
                int q = (q0 << 8) | i;
                i = input[ptr++] & 0xFF;
                if (codes[i] == 0) {
                    q = (q << 8) | i;
                    i = input[ptr++] & 0xFF;
                    if (codes[i] == 0) {
                        q = (q << 8) | i;
                        i = input[ptr++] & 0xFF;
                        if (codes[i] == 0) {
                            _quad1 = q;
                            return _parseMediumName(ptr, i);
                        }
                        if (i == INT_QUOTE) { // 4 byte/char case or broken
                            _inputPtr = ptr;
                            return _findName(q, 4);
                        }
                        return null;
                    }
                    if (i == INT_QUOTE) { // 3 byte/char case or broken
                        _inputPtr = ptr;
                        return _findName(q, 3);
                    }
                    return null;
                }
                if (i == INT_QUOTE) { // 2 byte/char case or broken
                    _inputPtr = ptr;
                    return _findName(q, 2);
                }
                return null;
            }
            if (i == INT_QUOTE) { // one byte/char case or broken
                _inputPtr = ptr;
                return _findName(q0, 1);
            }
            return null;
        }
        if (q0 == INT_QUOTE) {
            _inputPtr = ptr;
            return "";
        }
        return null;
    }

    private final String _parseMediumName(int ptr, int q2) throws IOException
    {
        final byte[] input = _inputBuffer;
        final int[] codes = _icLatin1;

        // Ok, got 5 name bytes so far
        int i = input[ptr++] & 0xFF;
        if (codes[i] == 0) {
            q2 = (q2 << 8) | i;
            i = input[ptr++] & 0xFF;
            if (codes[i] == 0) {
                q2 = (q2 << 8) | i;
                i = input[ptr++] & 0xFF;
                if (codes[i] == 0) {
                    q2 = (q2 << 8) | i;
                    i = input[ptr++] & 0xFF;
                    if (codes[i] == 0) {
                        return _parseMediumName2(ptr, i, q2);
                    }
                    if (i == INT_QUOTE) { // 8 bytes
                        _inputPtr = ptr;
                        return _findName(_quad1, q2, 4);
                    }
                    return null;
                }
                if (i == INT_QUOTE) { // 7 bytes
                    _inputPtr = ptr;
                    return _findName(_quad1, q2, 3);
                }
                return null;
            }
            if (i == INT_QUOTE) { // 6 bytes
                _inputPtr = ptr;
                return _findName(_quad1, q2, 2);
            }
            return null;
        }
        if (i == INT_QUOTE) { // 5 bytes
            _inputPtr = ptr;
            return _findName(_quad1, q2, 1);
        }
        return null;
    }

    private final String _parseMediumName2(int ptr, int q3, final int q2) throws IOException
    {
        final byte[] input = _inputBuffer;
        final int[] codes = _icLatin1;

        // Got 9 name bytes so far
        int i = input[ptr++] & 0xFF;
        if (codes[i] != 0) {
            if (i == INT_QUOTE) { // 9 bytes
                _inputPtr = ptr;
                return _findName(_quad1, q2, q3, 1);
            }
            return null;
        }
        q3 = (q3 << 8) | i;
        i = input[ptr++] & 0xFF;
        if (codes[i] != 0) {
            if (i == INT_QUOTE) { // 10 bytes
                _inputPtr = ptr;
                return _findName(_quad1, q2, q3, 2);
            }
            return null;
        }
        q3 = (q3 << 8) | i;
        i = input[ptr++] & 0xFF;
        if (codes[i] != 0) {
            if (i == INT_QUOTE) { // 11 bytes
                _inputPtr = ptr;
                return _findName(_quad1, q2, q3, 3);
            }
            return null;
        }
        q3 = (q3 << 8) | i;
        i = input[ptr++] & 0xFF;
        if (i == INT_QUOTE) { // 12 bytes
            _inputPtr = ptr;
            return _findName(_quad1, q2, q3, 4);
        }
        // Could continue
        return null;
    }

    /**
     * Slower parsing method which is generally branched to when
     * an escape sequence is detected (or alternatively for long
     * names, one crossing input buffer boundary).
     * Needs to be able to handle more exceptional cases, gets slower,
     * and hence is offlined to a separate method.
     */
    private final JsonToken _parseEscapedName(int qlen, int currQuad, int currQuadBytes)
            throws IOException
    {
        // This may seem weird, but here we do not want to worry about
        // UTF-8 decoding yet. Rather, we'll assume that part is ok (if not it will get
        // caught later on), and just handle quotes and backslashes here.
        int[] quads = _quadBuffer;
        final int[] codes = _icLatin1;

        while (true) {
            if (_inputPtr >= _inputEnd) {
                _quadLength = qlen;
                _pending32 = currQuad;
                _pendingBytes = currQuadBytes;
                _minorState = MINOR_FIELD_NAME;
                return (_currToken = JsonToken.NOT_AVAILABLE);
            }
            int ch = _inputBuffer[_inputPtr++] & 0xFF;
            if (codes[ch] == 0) {
                if (currQuadBytes < 4) {
                    ++currQuadBytes;
                    currQuad = (currQuad << 8) | ch;
                    continue;
                }
                if (qlen >= quads.length) {
                    _quadBuffer = quads = growArrayBy(quads, quads.length);
                }
                quads[qlen++] = currQuad;
                currQuad = ch;
                currQuadBytes = 1;
                continue;
            }

            // Otherwise bit longer handling
            if (ch == INT_QUOTE) { // we are done
                break;
            }
            // Unquoted white space?
            if (ch != INT_BACKSLASH) {
                // Call can actually now return (if unquoted linefeeds allowed)
                _throwUnquotedSpace(ch, "name");
            } else {
                // Nope, escape sequence
                ch = _decodeCharEscape();
                if (ch < 0) { // method has set up state about escape sequence
                    _minorState = MINOR_FIELD_NAME_ESCAPE;
                    _quadLength = qlen;
                    _pending32 = currQuad;
                    _pendingBytes = currQuadBytes;
                    return (_currToken = JsonToken.NOT_AVAILABLE);
                }
            }

            // May need to UTF-8 (re-)encode it, if it's beyond
            // 7-bit ASCII. Gets pretty messy. If this happens often, may
            // want to use different name canonicalization to avoid these hits.
            if (qlen >= quads.length) {
                _quadBuffer = quads = growArrayBy(quads, quads.length);
            }
            if (ch > 127) {
                // Ok, we'll need room for first byte right away
                if (currQuadBytes >= 4) {
                    quads[qlen++] = currQuad;
                    currQuad = 0;
                    currQuadBytes = 0;
                }
                if (ch < 0x800) { // 2-byte
                    currQuad = (currQuad << 8) | (0xc0 | (ch >> 6));
                    ++currQuadBytes;
                    // Second byte gets output below:
                } else { // 3 bytes; no need to worry about surrogates here
                    currQuad = (currQuad << 8) | (0xe0 | (ch >> 12));
                    ++currQuadBytes;
                    // need room for middle byte?
                    if (currQuadBytes >= 4) {
                        quads[qlen++] = currQuad;
                        currQuad = 0;
                        currQuadBytes = 0;
                    }
                    currQuad = (currQuad << 8) | (0x80 | ((ch >> 6) & 0x3f));
                    ++currQuadBytes;
                }
                // And same last byte in both cases, gets output below:
                ch = 0x80 | (ch & 0x3f);
            }
            if (currQuadBytes < 4) {
                ++currQuadBytes;
                currQuad = (currQuad << 8) | ch;
                continue;
            }
            quads[qlen++] = currQuad;
            currQuad = ch;
            currQuadBytes = 1;
        }

        if (currQuadBytes > 0) {
            if (qlen >= quads.length) {
                _quadBuffer = quads = growArrayBy(quads, quads.length);
            }
            quads[qlen++] = _padLastQuad(currQuad, currQuadBytes);
        } else if (qlen == 0) { // rare, but may happen
            return _fieldComplete("");
        }
        String name = _symbols.findName(quads, qlen);
        if (name == null) {
            name = _addName(quads, qlen, currQuadBytes);
        }
        return _fieldComplete(name);
    }

    /**
     * Method called when we see non-white space character other
     * than double quote, when expecting a field name.
     * In standard mode will just throw an exception; but
     * in non-standard modes may be able to parse name.
     */
    private JsonToken _handleOddName(int ch) throws IOException
    {
        // First: may allow single quotes
        if (ch == '\'') {
            if (isEnabled(Feature.ALLOW_SINGLE_QUOTES)) {
                return _parseAposName();
            }
        } else if (ch == ']') { // for better error reporting...
            return _closeArrayScope();
        }
        // allow unquoted names if feature enabled:
        if (!isEnabled(Feature.ALLOW_UNQUOTED_FIELD_NAMES)) {
         // !!! TODO: Decode UTF-8 characters properly...
//            char c = (char) _decodeCharForError(ch);
            char c = (char) ch;
            _reportUnexpectedChar(c, "was expecting double-quote to start field name");
        }
        // Also: note that although we use a different table here, it does NOT handle UTF-8
        // decoding. It'll just pass those high-bit codes as acceptable for later decoding.
        final int[] codes = CharTypes.getInputCodeUtf8JsNames();
        // Also: must start with a valid character...
        if (codes[ch] != 0) {
            _reportUnexpectedChar(ch, "was expecting either valid name character (for unquoted name) or double-quote (for quoted) to start field name");
        }

        // Ok, now; instead of ultra-optimizing parsing here (as with regular JSON names),
        // let's just use the generic "slow" variant. Can measure its impact later on if need be.
        int[] quads = _quadBuffer;
        int qlen = 0;
        int currQuad = 0;
        int currQuadBytes = 0;

        while (true) {
            // Ok, we have one more byte to add at any rate:
            if (currQuadBytes < 4) {
                ++currQuadBytes;
                currQuad = (currQuad << 8) | ch;
            } else {
                if (qlen >= quads.length) {
                    _quadBuffer = quads = growArrayBy(quads, quads.length);
                }
                quads[qlen++] = currQuad;
                currQuad = ch;
                currQuadBytes = 1;
            }
            if (_inputPtr >= _inputEnd) {
                if (!_loadMore()) {
                    _reportInvalidEOF(" in field name", JsonToken.FIELD_NAME);
                }
            }
            ch = _inputBuffer[_inputPtr] & 0xFF;
            if (codes[ch] != 0) {
                break;
            }
            ++_inputPtr;
        }

        if (currQuadBytes > 0) {
            if (qlen >= quads.length) {
                _quadBuffer = quads = growArrayBy(quads, quads.length);
            }
            quads[qlen++] = currQuad;
        }
        String name = _symbols.findName(quads, qlen);
        if (name == null) {
            name = _addName(quads, qlen, currQuadBytes);
        }
        return _fieldComplete(name);
    }

    /* Parsing to support [JACKSON-173]. Plenty of duplicated code;
     * main reason being to try to avoid slowing down fast path
     * for valid JSON -- more alternatives, more code, generally
     * bit slower execution.
     */
    private JsonToken _parseAposName() throws IOException
    {
        if (_inputPtr >= _inputEnd) {
            if (!_loadMore()) {
                _reportInvalidEOF(": was expecting closing '\'' for field name", JsonToken.FIELD_NAME);
            }
        }
        int ch = _inputBuffer[_inputPtr++] & 0xFF;
        if (ch == '\'') { // special case, ''
            return _fieldComplete("");
        }
        int[] quads = _quadBuffer;
        int qlen = 0;
        int currQuad = 0;
        int currQuadBytes = 0;

        // Copied from parseEscapedFieldName, with minor mods:

        final int[] codes = _icLatin1;

        while (true) {
            if (ch == '\'') {
                break;
            }
            // additional check to skip handling of double-quotes
            if (ch != '"' && codes[ch] != 0) {
                if (ch != '\\') {
                    // Unquoted white space?
                    _throwUnquotedSpace(ch, "name");
                } else {
                    // Nope, escape sequence
                    ch = _decodeCharEscape();
                }
                if (ch > 127) {
                    // Ok, we'll need room for first byte right away
                    if (currQuadBytes >= 4) {
                        if (qlen >= quads.length) {
                            _quadBuffer = quads = growArrayBy(quads, quads.length);
                        }
                        quads[qlen++] = currQuad;
                        currQuad = 0;
                        currQuadBytes = 0;
                    }
                    if (ch < 0x800) { // 2-byte
                        currQuad = (currQuad << 8) | (0xc0 | (ch >> 6));
                        ++currQuadBytes;
                        // Second byte gets output below:
                    } else { // 3 bytes; no need to worry about surrogates here
                        currQuad = (currQuad << 8) | (0xe0 | (ch >> 12));
                        ++currQuadBytes;
                        // need room for middle byte?
                        if (currQuadBytes >= 4) {
                            if (qlen >= quads.length) {
                                _quadBuffer = quads = growArrayBy(quads, quads.length);
                            }
                            quads[qlen++] = currQuad;
                            currQuad = 0;
                            currQuadBytes = 0;
                        }
                        currQuad = (currQuad << 8) | (0x80 | ((ch >> 6) & 0x3f));
                        ++currQuadBytes;
                    }
                    // And same last byte in both cases, gets output below:
                    ch = 0x80 | (ch & 0x3f);
                }
            }
            // Ok, we have one more byte to add at any rate:
            if (currQuadBytes < 4) {
                ++currQuadBytes;
                currQuad = (currQuad << 8) | ch;
            } else {
                if (qlen >= quads.length) {
                    _quadBuffer = quads = growArrayBy(quads, quads.length);
                }
                quads[qlen++] = currQuad;
                currQuad = ch;
                currQuadBytes = 1;
            }
            if (_inputPtr >= _inputEnd) {
                if (!_loadMore()) {
                    _reportInvalidEOF(" in field name", JsonToken.FIELD_NAME);
                }
            }
            ch = _inputBuffer[_inputPtr++] & 0xFF;
        }

        if (currQuadBytes > 0) {
            if (qlen >= quads.length) {
                _quadBuffer = quads = growArrayBy(quads, quads.length);
            }
            quads[qlen++] = _padLastQuad(currQuad, currQuadBytes);
        }
        String name = _symbols.findName(quads, qlen);
        if (name == null) {
            name = _addName(quads, qlen, currQuadBytes);
        }
        return _fieldComplete(name);
    }

    @Override
    protected char _decodeEscaped() throws IOException {
        VersionUtil.throwInternal();
        return ' ';
    }

    protected final JsonToken _finishFieldWithEscape() throws IOException
    {
        // First: try finishing what wasn't yet:
        int ch = _decodeSplitEscaped(_quoted32, _quotedDigits);
        if (ch < 0) { // ... if possible
            _minorState = MINOR_FIELD_NAME_ESCAPE;
            return JsonToken.NOT_AVAILABLE;
        }
        if (_quadLength >= _quadBuffer.length) {
            _quadBuffer = growArrayBy(_quadBuffer, 32);
        }
        int currQuad = _pending32;
        int currQuadBytes = _pendingBytes;
        if (ch > 127) {
            // Ok, we'll need room for first byte right away
            if (currQuadBytes >= 4) {
                _quadBuffer[_quadLength++] = currQuad;
                currQuad = 0;
                currQuadBytes = 0;
            }
            if (ch < 0x800) { // 2-byte
                currQuad = (currQuad << 8) | (0xc0 | (ch >> 6));
                ++currQuadBytes;
                // Second byte gets output below:
            } else { // 3 bytes; no need to worry about surrogates here
                currQuad = (currQuad << 8) | (0xe0 | (ch >> 12));
                ++currQuadBytes;
                // need room for middle byte?
                if (currQuadBytes >= 4) {
                    _quadBuffer[_quadLength++] = currQuad;
                    currQuad = 0;
                    currQuadBytes = 0;
                }
                currQuad = (currQuad << 8) | (0x80 | ((ch >> 6) & 0x3f));
                ++currQuadBytes;
            }
            // And same last byte in both cases, gets output below:
            ch = 0x80 | (ch & 0x3f);
        }
        if (currQuadBytes < 4) {
            ++currQuadBytes;
            currQuad = (currQuad << 8) | ch;
        } else {
            _quadBuffer[_quadLength++] = currQuad;
            currQuad = ch;
            currQuadBytes = 1;
        }
        return _parseEscapedName(_quadLength, currQuad, currQuadBytes);
    }

    private int _decodeSplitEscaped(int value, int bytesRead) throws IOException
    {
        if (_inputPtr >= _inputEnd) {
            _quoted32 = value;
            _quotedDigits = bytesRead;
            return -1;
        }
        int c = _inputBuffer[_inputPtr++];
        if (bytesRead == -1) { // expecting first char after backslash
            switch (c) {
                // First, ones that are mapped
            case 'b':
                return '\b';
            case 't':
                return '\t';
            case 'n':
                return '\n';
            case 'f':
                return '\f';
            case 'r':
                return '\r';
    
                // And these are to be returned as they are
            case '"':
            case '/':
            case '\\':
                return c;
    
            case 'u': // and finally hex-escaped
                break;
    
            default:
                {
                 // !!! TODO: Decode UTF-8 characters properly...
    //              char ch = (char) _decodeCharForError(c);
                    char ch = (char) c;
                    return _handleUnrecognizedCharacterEscape(ch);
                }
            }
            if (_inputPtr >= _inputEnd) {
                _quotedDigits = 0;
                _quoted32 = 0;
                return -1;
            }
            c = _inputBuffer[_inputPtr++];
            bytesRead = 0;
        }
        c &= 0xFF;
        while (true) {
            int digit = CharTypes.charToHex(c);
            if (digit < 0) {
                _reportUnexpectedChar(c, "expected a hex-digit for character escape sequence");
            }
            value = (value << 4) | digit;
            if (++bytesRead == 4) {
                return value;
            }
            if (_inputPtr >= _inputEnd) {
                _quotedDigits = bytesRead;
                _quoted32 = value;
                return -1;
            }
            c = _inputBuffer[_inputPtr++] & 0xFF;
        }
    }

    /*
    /**********************************************************************
    /* Second-level decoding, String decoding
    /**********************************************************************
     */

    protected JsonToken _startAposString() throws IOException
    {
        VersionUtil.throwInternal();
        return null;
    }

    protected JsonToken _startString() throws IOException
    {
        int ptr = _inputPtr;
        int outPtr = 0;
        char[] outBuf = _textBuffer.emptyAndGetCurrentSegment();
        final int[] codes = _icUTF8;

        final int max = Math.min(_inputEnd, (ptr + outBuf.length));
        final byte[] inputBuffer = _inputBuffer;
        while (ptr < max) {
            int c = (int) inputBuffer[ptr] & 0xFF;
            if (codes[c] != 0) {
                if (c == INT_QUOTE) {
                    _inputPtr = ptr+1;
                    _textBuffer.setCurrentLength(outPtr);
                    return _valueComplete(JsonToken.VALUE_STRING);
                }
                break;
            }
            ++ptr;
            outBuf[outPtr++] = (char) c;
        }
        _textBuffer.setCurrentLength(outPtr);
        _inputPtr = ptr;
        return _finishRegularString();
    }

    private final JsonToken _finishRegularString() throws IOException
    {
        int c;

        // Here we do want to do full decoding, hence:
        final int[] codes = _icUTF8;
        final byte[] inputBuffer = _inputBuffer;

        char[] outBuf = _textBuffer.getBufferWithoutReset();
        int outPtr = _textBuffer.getCurrentSegmentSize();
        int ptr = _inputPtr;
        final int safeEnd = _inputEnd - 5; // longest escape is 6 chars
        
        main_loop:
        while (true) {
            // Then the tight ASCII non-funny-char loop:
            ascii_loop:
            while (true) {
                if (ptr >= _inputEnd) {
                    _inputPtr = ptr;
                    _minorState = MINOR_VALUE_STRING;
                    _textBuffer.setCurrentLength(outPtr);
                    return (_currToken = JsonToken.NOT_AVAILABLE);
                }
                if (outPtr >= outBuf.length) {
                    outBuf = _textBuffer.finishCurrentSegment();
                    outPtr = 0;
                }
                final int max = Math.min(_inputEnd, (ptr + (outBuf.length - outPtr)));
                while (ptr < max) {
                    c = inputBuffer[ptr++] & 0xFF;
                    if (codes[c] != 0) {
                        break ascii_loop;
                    }
                    outBuf[outPtr++] = (char) c;
                }
            }
            // Ok: end marker, escape or multi-byte?
            if (c == INT_QUOTE) {
                _inputPtr = ptr;
                _textBuffer.setCurrentLength(outPtr);
                return _valueComplete(JsonToken.VALUE_STRING);
            }
            // If possibly split, use off-lined longer version
            if (ptr >= safeEnd) {
                _inputPtr = ptr;
                _textBuffer.setCurrentLength(outPtr);
                if (!_decodeSplitMultiByte(c, codes[c], ptr < _inputEnd)) {
                    return (_currToken = JsonToken.NOT_AVAILABLE);
                }
                outBuf = _textBuffer.getBufferWithoutReset();
                outPtr = _textBuffer.getCurrentSegmentSize();
                ptr = _inputPtr;
                continue main_loop;
            }
            // otherwise use inlined
            switch (codes[c]) {
            case 1: // backslash
                _inputPtr = ptr;
                c = _decodeFastCharEscape(); // since we know it's not split
                ptr = _inputPtr;
                break;
            case 2: // 2-byte UTF
                c = _decodeUTF8_2(c, _inputBuffer[ptr++]);
                break;
            case 3: // 3-byte UTF
                c = _decodeUTF8_3(c, _inputBuffer[ptr++], _inputBuffer[ptr++]);
                break;
            case 4: // 4-byte UTF
                c = _decodeUTF8_4(c, _inputBuffer[ptr++], _inputBuffer[ptr++],
                        _inputBuffer[ptr++]);
                // Let's add first part right away:
                outBuf[outPtr++] = (char) (0xD800 | (c >> 10));
                if (outPtr >= outBuf.length) {
                    outBuf = _textBuffer.finishCurrentSegment();
                    outPtr = 0;
                }
                c = 0xDC00 | (c & 0x3FF);
                // And let the other char output down below
                break;
            default:
                if (c < INT_SPACE) {
                    // Note: call can now actually return (to allow unquoted linefeeds)
                    _throwUnquotedSpace(c, "string value");
                } else {
                    // Is this good enough error message?
                    _reportInvalidChar(c);
                }
            }
            // Need more room?
            if (outPtr >= outBuf.length) {
                outBuf = _textBuffer.finishCurrentSegment();
                outPtr = 0;
            }
            // Ok, let's add char to output:
            outBuf[outPtr++] = (char) c;
        }
    }

    private final boolean _decodeSplitMultiByte(int c, int type, boolean gotNext)
            throws IOException
    {
        switch (type) {
        case 1:
            c = _decodeSplitEscaped(0, -1);
            if (c < 0) {
                _minorState = MINOR_VALUE_STRING_ESCAPE;
                return false;
            }
            _textBuffer.append((char) c);
            return true;
        case 2: // 2-byte UTF; easy, either got both, or just miss one
            if (gotNext) {
                // NOTE: always succeeds, no need to check
                c = _decodeUTF8_2(c, _inputBuffer[_inputPtr++]);
                _textBuffer.append((char) c);
                return true;
            }
            _minorState = MINOR_VALUE_STRING_UTF8_2;
            _pending32 = c;
            return false;
        case 3: // 3-byte UTF
            c &= 0x0F;
            if (gotNext) {
                return _decodeSplitUTF8_3(c, 1, _inputBuffer[_inputPtr++]);
            }
            _minorState = MINOR_VALUE_STRING_UTF8_3;
            _pending32 = c;
            _pendingBytes = 1;
            return false;
        case 4: // 4-byte UTF
            c &= 0x07;
            if (gotNext) {
                return _decodeSplitUTF8_4(c, 1, _inputBuffer[_inputPtr++]);
            }
            _pending32 = c;
            _pendingBytes = 1;
            _minorState = MINOR_VALUE_STRING_UTF8_4;
            return false;
        default:
            if (c < INT_SPACE) {
                // Note: call can now actually return (to allow unquoted linefeeds)
                _throwUnquotedSpace(c, "string value");
            } else {
                // Is this good enough error message?
                _reportInvalidChar(c);
            }
            _textBuffer.append((char) c);
            return true;
        }
    }

    private final boolean _decodeSplitUTF8_3(int prev, int prevCount, int next)
        throws IOException
    {
        if (prevCount == 1) {
            if ((next & 0xC0) != 0x080) {
                _reportInvalidOther(next & 0xFF, _inputPtr);
            }
            prev = (prev << 6) | (next & 0x3F);
            if (_inputPtr >= _inputEnd) {
                _minorState = MINOR_VALUE_STRING_UTF8_3;
                _pending32 = prev;
                _pendingBytes = 2;
                return false;
            }
            next = _inputBuffer[_inputPtr++];
        }
        if ((next & 0xC0) != 0x080) {
            _reportInvalidOther(next & 0xFF, _inputPtr);
        }
        _textBuffer.append((char) ((prev << 6) | (next & 0x3F)));
        return true;
    }

    // @return Character value <b>minus 0x10000</c>; this so that caller
    //    can readily expand it to actual surrogates
    private final boolean _decodeSplitUTF8_4(int prev, int prevCount, int next)
        throws IOException
    {
        if (prevCount == 1) {
            if ((next & 0xC0) != 0x080) {
                _reportInvalidOther(next & 0xFF, _inputPtr);
            }
            prev = (prev << 6) | (next & 0x3F);
            if (_inputPtr >= _inputEnd) {
                _minorState = MINOR_VALUE_STRING_UTF8_4;
                _pending32 = prev;
                _pendingBytes = 2;
                return false;
            }
            prevCount = 2;
            next = _inputBuffer[_inputPtr++];
        }
        if (prevCount == 2) {
            if ((next & 0xC0) != 0x080) {
                _reportInvalidOther(next & 0xFF, _inputPtr);
            }
            prev = (prev << 6) | (next & 0x3F);
            if (_inputPtr >= _inputEnd) {
                _minorState = MINOR_VALUE_STRING_UTF8_4;
                _pending32 = prev;
                _pendingBytes = 3;
                return false;
            }
            next = _inputBuffer[_inputPtr++];
        }
        if ((next & 0xC0) != 0x080) {
            _reportInvalidOther(next & 0xFF, _inputPtr);
        }
        int c = ((prev << 6) | (next & 0x3F)) - 0x10000;
        // Let's add first part right away:
        _textBuffer.append((char) (0xD800 | (c >> 10)));
        c = 0xDC00 | (c & 0x3FF);
        // And let the other char output down below
        _textBuffer.append((char) c);
        return true;
    }

    /*
    /**********************************************************************
    /* Internal methods, UTF8 decoding
    /**********************************************************************
     */

    private final int _decodeCharEscape() throws IOException
    {
        int left = _inputEnd - _inputPtr;
        if (left < 5) { // offline boundary-checking case:
            return _decodeSplitEscaped(0, -1);
        }
        return _decodeFastCharEscape();
    }

    private final int _decodeFastCharEscape() throws IOException
    {
        int c = (int) _inputBuffer[_inputPtr++];
        switch (c) {
            // First, ones that are mapped
        case 'b':
            return '\b';
        case 't':
            return '\t';
        case 'n':
            return '\n';
        case 'f':
            return '\f';
        case 'r':
            return '\r';

            // And these are to be returned as they are
        case '"':
        case '/':
        case '\\':
            return (char) c;

        case 'u': // and finally hex-escaped
            break;

        default:
            {
             // !!! TODO: Decode UTF-8 characters properly...
//              char ch = (char) _decodeCharForError(c);
                char ch = (char) c;
                return _handleUnrecognizedCharacterEscape(ch);
            }
        }

        int ch = (int) _inputBuffer[_inputPtr++];
        int digit = CharTypes.charToHex(ch);
        int result = digit;

        if (digit >= 0) {
            ch = (int) _inputBuffer[_inputPtr++];
            digit = CharTypes.charToHex(ch);
            if (digit >= 0) {
                result = (result << 4) | digit;
                ch = (int) _inputBuffer[_inputPtr++];
                digit = CharTypes.charToHex(ch);
                if (digit >= 0) {
                    result = (result << 4) | digit;
                    ch = (int) _inputBuffer[_inputPtr++];
                    digit = CharTypes.charToHex(ch);
                    if (digit >= 0) {
                        return (result << 4) | digit;
                    }
                }
            }
        }
        _reportUnexpectedChar(ch & 0xFF, "expected a hex-digit for character escape sequence");
        return -1;
    }

    /*
    /**********************************************************************
    /* Internal methods, UTF8 decoding
    /**********************************************************************
     */

    private final int _decodeUTF8_2(int c, int d) throws IOException
    {
        if ((d & 0xC0) != 0x080) {
            _reportInvalidOther(d & 0xFF, _inputPtr);
        }
        return ((c & 0x1F) << 6) | (d & 0x3F);
    }

    private final int _decodeUTF8_3(int c, int d, int e) throws IOException
    {
        c &= 0x0F;
        if ((d & 0xC0) != 0x080) {
            _reportInvalidOther(d & 0xFF, _inputPtr);
        }
        c = (c << 6) | (d & 0x3F);
        if ((e & 0xC0) != 0x080) {
            _reportInvalidOther(e & 0xFF, _inputPtr);
        }
        return (c << 6) | (e & 0x3F);
    }

    // @return Character value <b>minus 0x10000</c>; this so that caller
    //    can readily expand it to actual surrogates
    private final int _decodeUTF8_4(int c, int d, int e, int f) throws IOException
    {
        if ((d & 0xC0) != 0x080) {
            _reportInvalidOther(d & 0xFF, _inputPtr);
        }
        c = ((c & 0x07) << 6) | (d & 0x3F);
        if ((e & 0xC0) != 0x080) {
            _reportInvalidOther(e & 0xFF, _inputPtr);
        }
        c = (c << 6) | (e & 0x3F);
        if ((f & 0xC0) != 0x080) {
            _reportInvalidOther(f & 0xFF, _inputPtr);
        }
        return ((c << 6) | (f & 0x3F)) - 0x10000;
    }

    /*
    /**********************************************************************
    /* Internal methods, other
    /**********************************************************************
     */
    // !!! TODO: only temporarily here

    private final boolean _loadMore() {
        VersionUtil.throwInternal();
        return false;
    }
}
