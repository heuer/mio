/*
 * Copyright 2007 - 2009 Lars Heuer (heuer[at]semagia.com)
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
package com.semagia.mio.jtm;

import com.semagia.mio.MIOException;
import com.semagia.mio.MIOParseException;
import com.semagia.mio.jtm.JsonToken;

/**
 * A lexer for <a href="http://www.cerny-online.com/jtm/">JSON Topic Maps (JTM)</a>.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 426 $ - $Date: 2010-09-02 19:01:19 +0200 (Do, 02 Sep 2010) $
 */
@SuppressWarnings("unused")
%%

%class JsonLexer

%unicode

%line 
%column

%pack

//%public

%int

%function token
%yylexthrow com.semagia.mio.MIOException

%{

    public static final int EOF = YYEOF;

    private int _leftOffset;
    private int _rightOffset;

    public int getLine() {
        return yyline+1;
    }

    public int getColumn() {
        return yycolumn;
    }

    public String value() {
        return new String(zzBuffer, zzStartRead+_leftOffset, yylength()-_leftOffset-_rightOffset);
    }

    private int _token(final int type) throws MIOException {
        return _token(type, 0, 0);
    }

    private int _token(final int type, final int leftOffset, final int rightOffset) throws MIOException {
        _leftOffset = leftOffset;
        _rightOffset = rightOffset;
        return type;
    }
%}

LineTerminator  = \r|\n|\r\n
Whitespace      = {LineTerminator} | [ \t\f]

String          = \"([^\\\"]|(\\[\\\"rntu/]))*\"

%%

{Whitespace}        { /* noop */ }

<YYINITIAL> {
    "{"                 { return _token(JsonToken.START_OBJECT); }
    "}"                 { return _token(JsonToken.END_OBJECT); }
    "["                 { return _token(JsonToken.START_ARRAY); }
    "]"                 { return _token(JsonToken.END_ARRAY); }
    \"version\"         { return _token(JsonToken.KW_VERSION, 1, 1); }
    \"item_type\"       { return _token(JsonToken.KW_ITEM_TYPE, 1, 1); }
    \"topics\"          { return _token(JsonToken.KW_TOPICS, 1, 1); }
    \"associations\"    { return _token(JsonToken.KW_ASSOCIATIONS, 1, 1); }
    \"roles\"           { return _token(JsonToken.KW_ROLES, 1, 1); }
    \"occurrences\"     { return _token(JsonToken.KW_OCCURRENCES, 1, 1); }
    \"names\"           { return _token(JsonToken.KW_NAMES, 1, 1); }
    \"variants\"        { return _token(JsonToken.KW_VARIANTS, 1, 1); }
    \"scope\"           { return _token(JsonToken.KW_SCOPE, 1, 1); }
    \"type\"            { return _token(JsonToken.KW_TYPE, 1, 1); }
    \"player\"          { return _token(JsonToken.KW_PLAYER, 1, 1); }
    \"value\"           { return _token(JsonToken.KW_VALUE, 1, 1); }
    \"datatype\"        { return _token(JsonToken.KW_DATATYPE, 1, 1); }
    \"reifier\"         { return _token(JsonToken.KW_REIFIER, 1, 1); }
    \"parent\"          { return _token(JsonToken.KW_PARENT, 1, 1); }
    \"item_identifiers\" { return _token(JsonToken.KW_IIDS, 1, 1); }
    \"subject_identifiers\" { return _token(JsonToken.KW_SIDS, 1, 1); }
    \"subject_locators\" { return _token(JsonToken.KW_SLOS, 1, 1); }
    \"instance_of\"     { return _token(JsonToken.KW_INSTANCE_OF, 1, 1); }
    \"prefixes\"        { return _token(JsonToken.KW_PREFIXES, 1, 1); }
    "null"              { return _token(JsonToken.VALUE_NULL, 1, 1); }
    {String}            { return _token(JsonToken.VALUE_STRING, 1, 1); }
    ","                 { return _token(JsonToken.COMMA); }
    ":"                 { return _token(JsonToken.COLON); }
}

.|\n                    { throw new MIOParseException("Illegal character <" + yytext() + "> at line " + getLine() + " column: " + getColumn(), getLine(), getColumn()); }

