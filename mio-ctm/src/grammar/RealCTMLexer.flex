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
package com.semagia.mio.ctm;

import com.semagia.mio.MIOException;
import com.semagia.mio.MIOParseException;

/**
 * CTM lexer.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 451 $ - $Date: 2010-09-04 21:53:28 +0200 (Sa, 04 Sep 2010) $
 */
@SuppressWarnings("unused")
%%

%class RealCTMLexer

%unicode

%line 
%column

%pack

%int

%function token

%yylexthrow com.semagia.mio.MIOException

%{

    public static final int EOF = YYEOF;

    private int _commentCounter;

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

    private int _token(int type) throws MIOException {
        return _token(type, 0, 0);
    }

    private int _token(int type, int leftOffset) throws MIOException {
        return _token(type, leftOffset, 0);
    }

    private int _token(int type, int leftOffset, int rightOffset) throws MIOException {
        _leftOffset = leftOffset;
        _rightOffset = rightOffset;
        return type;
    }
%}

IRI             = {SchemaName}"://"([;\.\)]*[^\r\n\t;\]\.\(\) ]+)+

LineTerminator  = \r|\n|\r\n
Whitespace      = {LineTerminator} | [ \t\f]

IdentifierStart = [a-zA-Z_] | [\u00C0-\u00D6] | [\u00D8-\u00F6] 
                            | [\u00F8-\u02FF] | [\u0370-\u037D] 
                            | [\u037F-\u1FFF] | [\u200C-\u200D] 
                            | [\u2070-\u218F] | [\u2C00-\u2FEF] 
                            | [\u3001-\uD7FF] | [\uF900-\uFDCF] 
                            | [\uFDF0-\uFFFD] 
                            //| [\u10000-\uEFFFF]
IdentifierChar = {IdentifierStart} | [\-0-9] 
                                   | \u00B7 
                                   | [\u0300-\u036F] 
                                   | [\u203F-\u2040]
Identifier      = {IdentifierStart}("."*{IdentifierChar})*
SchemaName      = [a-zA-Z]+[a-zA-Z0-9\+\-\.]*
QName           = {Identifier}":"(([0-9]+{IdentifierChar}*)|{Identifier})

Variable        = "$"{Identifier}
IRI2            = "<"[^<>\"\{\}\`\\ ]+">"
NamedWildcard   = "?"{Identifier}

String          = \"([^\\\"]|(\\[\\\"rntuU]))*\"
StringTriple    = \"{3} ~\"{3}
Sign            = ("-" | "+")
Integer         = {Sign}? [0-9]+
Decimal         = {Sign}? [0-9]* \. [0-9]+
Date            = \-?(000[1-9]|00[1-9][0-9]|0[1-9][0-9][0-9]|[1-9][0-9][0-9][0-9]+)"-"(0[1-9]|1[0-2])"-"(0[1-9]|1[0-9]|2[0-9]|3 [0-1])
Time            = [0-9]{2} : [0-9]{2} : [0-9]{2} (\.[0-9]+)? ({TimeZone})?
TimeZone        = Z | ( {Sign} [0-9]{2} : [0-9]{2} )
DateTime        = {Date}"T"{Time} 

%xstates COMMENT, ML_COMMENT

%%

"#"                 { yybegin(COMMENT); }
{Whitespace}        { /* noop */ }

<YYINITIAL> {
    // Keywords
    "def"               { return _token(TokenTypes.KW_DEF); }
    "end"               { return _token(TokenTypes.KW_END); }
    "isa"               { return _token(TokenTypes.KW_ISA); }
    "ako"               { return _token(TokenTypes.KW_AKO); }
    "%encoding"         { return _token(TokenTypes.DIR_ENCODING); }
    "%version"          { return _token(TokenTypes.DIR_VERSION); }
    "%prefix"           { return _token(TokenTypes.DIR_PREFIX); }
    "%include"          { return _token(TokenTypes.DIR_INCLUDE); }
    "%mergemap"         { return _token(TokenTypes.DIR_MERGEMAP); }

    // Identifiers
    "?"                 { return _token(TokenTypes.WILDCARD); }
    {NamedWildcard}     { return _token(TokenTypes.NAMED_WILDCARD, 1); }
    {QName}             { return _token(TokenTypes.QNAME); }
    {Identifier}        { return _token(TokenTypes.IDENT); }
    {Variable}          { return _token(TokenTypes.VARIABLE, 1); }

    // Brackets
    "("                 { return _token(TokenTypes.LPAREN); }
    ")"                 { return _token(TokenTypes.RPAREN); }
    "["                 { return _token(TokenTypes.LBRACK); }
    "]"                 { return _token(TokenTypes.RBRACK); }

    ","                 { return _token(TokenTypes.COMMA); }
    ";"                 { return _token(TokenTypes.SEMI); }
    ":"                 { return _token(TokenTypes.COLON); }
    "."                 { return _token(TokenTypes.DOT); }
    "^^"                { return _token(TokenTypes.DOUBLE_CIRCUMFLEX); }
    "="                 { return _token(TokenTypes.EQ); }
    "^"                 { return _token(TokenTypes.CIRCUMFLEX); }
    "~"                 { return _token(TokenTypes.TILDE); }
    "-"                 { return _token(TokenTypes.HYPHEN); }
    "@"                 { return _token(TokenTypes.AT); }

    // Datatypes
    {String}            { return _token(TokenTypes.STRING, 1, 1); }
    {StringTriple}      { return _token(TokenTypes.STRING, 3, 3); }
    {IRI}               { return _token(TokenTypes.IRI); }
    {IRI2}              { return _token(TokenTypes.IRI, 1, 1); }    // <...>
    {Date}              { return _token(TokenTypes.DATE); }
    {DateTime}          { return _token(TokenTypes.DATE_TIME); }
    {Integer}           { return _token(TokenTypes.INTEGER); }
    {Decimal}           { return _token(TokenTypes.DECIMAL); }
    "*"                 { return _token(TokenTypes.STAR); }
}

<COMMENT> {
    .\(                 { /* noop */ }
    \(                  { _commentCounter=1; yybegin(ML_COMMENT); }
    [^\r\n]             { /* noop */ }
    {LineTerminator}    { yybegin(YYINITIAL); }
}

<ML_COMMENT> {
    "#("                { _commentCounter++; }
    ")#"                { _commentCounter--; if (_commentCounter == 0) { yybegin(YYINITIAL); } }
    [^]                 { /* noop */ }
}

.|\n                    { throw new MIOParseException("Illegal character <" + yytext() + "> at line " + getLine() + " column: " + getColumn(), getLine(), getColumn()); }

