/*
 * Copyright 2007 - 2010 Lars Heuer (heuer[at]semagia.com)
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
package com.semagia.mio.ltm;

import com.semagia.mio.MIOException;
import com.semagia.mio.MIOParseException;

/**
 * LTM tokenizer.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 443 $ - $Date: 2010-09-04 13:26:55 +0200 (Sa, 04 Sep 2010) $
 */
@SuppressWarnings("unused")
%%

%class RealLTMLexer

%unicode

%line 
%column

%pack

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

Comment         = "/*" [^*] ~"*/" | "/*" "*"+ "/"
LineTerminator  = \r|\n|\r\n
Whitespace      = {LineTerminator} | [ \t\f]

IdentifierStart = [a-zA-Z_] | [\u00C0-\u00D6] | [\u00D8-\u00F6] 
                            | [\u00F8-\u02FF] | [\u0370-\u037D] 
                            | [\u037F-\u1FFF] | [\u200C-\u200D] 
                            | [\u2070-\u218F] | [\u2C00-\u2FEF] 
                            | [\u3001-\uD7FF] | [\uF900-\uFDCF] 
                            | [\uFDF0-\uFFFD] 
                            //| [\u10000-\uEFFFF]
IdentifierChar  = {IdentifierStart} | [\-\.0-9] | \u00B7 | [\u0300-\u036F] 
                                                | [\u203F-\u2040]
Identifier      = {IdentifierStart}{IdentifierChar}*
QName           = {Identifier}":"([0-9]|{IdentifierStart}){IdentifierChar}*
String          = \"([^\"]|\"\")*\"
Data            = "["{2} ~ "]"{2}

%%

<YYINITIAL> {

    {Comment}           { /* noop */ }
    {Whitespace}        { /* noop */ }

    {String}            { return _token(TokenTypes.STRING, 1, 1); }
    {Data}              { return _token(TokenTypes.DATA, 2, 2); }

    // Directives
    "#PREFIX"           { return _token(TokenTypes.DIR_PREFIX); }
    "#BASEURI"          { return _token(TokenTypes.DIR_BASEURI); }
    "#MERGEMAP"         { return _token(TokenTypes.DIR_MERGEMAP); }
    "#INCLUDE"          { return _token(TokenTypes.DIR_INCLUDE); }
    "#VERSION"          { return _token(TokenTypes.DIR_VERSION); }
    "#TOPICMAP"         { return _token(TokenTypes.DIR_TOPICMAP); }

    {QName}             { return _token(TokenTypes.QNAME); }
    {Identifier}        { return _token(TokenTypes.IDENT); }

    // Brackets
    "["                 { return _token(TokenTypes.LBRACK); }
    "]"                 { return _token(TokenTypes.RBRACK); }
    "("                 { return _token(TokenTypes.LPAREN); }
    ")"                 { return _token(TokenTypes.RPAREN); }
    "{"                 { return _token(TokenTypes.LCURLY); }
    "}"                 { return _token(TokenTypes.RCURLY); }

    "@"                 { return _token(TokenTypes.AT); }
    "%"                 { return _token(TokenTypes.PERCENT); }
    "="                 { return _token(TokenTypes.EQ); }
    ";"                 { return _token(TokenTypes.SEMI); }
    ":"                 { return _token(TokenTypes.COLON); }
    ","                 { return _token(TokenTypes.COMMA); }
    "~"                 { return _token(TokenTypes.TILDE); }
    "/"                 { return _token(TokenTypes.SLASH); }

}

.|\n                    { throw new MIOParseException("Illegal character <" + yytext() + "> at line " + getLine() + " column: " + getColumn(), getLine(), getColumn()); }
