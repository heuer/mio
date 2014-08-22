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
package com.semagia.mio.stm;

import com.semagia.mio.MIOException;
import com.semagia.mio.MIOParseException;

/**
 * Snello lexer.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 436 $ - $Date: 2010-09-04 01:10:49 +0200 (Sa, 04 Sep 2010) $
 */
@SuppressWarnings("unused")
%%

%class RealSnelloLexer

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
        return new String(zzBuffer, zzStartRead+_leftOffset, zzMarkedPos-zzStartRead-_leftOffset-_rightOffset);
    }

    private int _token(int type) {
        return _token(type, 0, 0);
    }

    private int _token(int type, int leftOffset) {
        return _token(type, leftOffset, 0);
    }

    private int _token(int type, int leftOffset, int rightOffset) {
        _leftOffset = leftOffset;
        _rightOffset = rightOffset;
        return type;
    }
%}

IRI               = {IRIProtocol}([\.:@,]*[^\r\n\t\.:@, \(])+

IRIProtocol       = (({SchemaName}":/") | ({RootlessScheme}))

/**
 * IRI detection.
 * 
 * All URI schemes that are not composed of [a-zA-Z]+"://"
 * must be listed here, otherwise the lexer / parser assumes that
 * it is a QName.
 */
RootlessScheme = ((urn                        /* RFC 2141 */
                   | mailto                   /* RFC 2368 */
                   | mid | cid                /* RFC 2392 */
                   | data                     /* RFC 2397 */
                   | service                  /* RFC 2609 */
                   | tel | fax | modem        /* RFC 2806 */
                   | sip                      /* RFC 3261 */
                   | h323                     /* RFC 3508 */
                   | pres                     /* RFC 3859 */
                   | im                       /* RFC 3860 */
                   | tag                      /* RFC 4151 */
                   | dns                      /* RFC 4501 */
                   )
                   ":")

LineTerminator  = \r|\n|\r\n
Whitespace      = {LineTerminator}|[ \t\f]

IdentifierStart = [a-zA-Z_] | [\u00C0-\u00D6] | [\u00D8-\u00F6] 
                            | [\u00F8-\u02FF] | [\u0370-\u037D] 
                            | [\u037F-\u1FFF] | [\u200C-\u200D] 
                            | [\u2070-\u218F] | [\u2C00-\u2FEF] 
                            | [\u3001-\uD7FF] | [\uF900-\uFDCF] 
                            | [\uFDF0-\uFFFD] 
                            //| [\u10000-\uEFFFF]

IdentifierChar  = {IdentifierStart} | [\-0-9] 
                                    | \u00B7 | [\u0300-\u036F] 
                                    | [\u203F-\u2040]

Identifier      = {IdentifierStart}(\.?{IdentifierChar})*

// Schema name acc. to RFC 3987
SchemaName      = [a-zA-Z]+[a-zA-Z0-9\+\-\.]*

QName           = {Identifier}":"(\.?{IdentifierChar})+

Comment         = "#"[^\r\n]*
Variable        = "$"{Identifier}
IRI2            = "<"[^<>\"\{\}\`\\ ]+">"
VIRI            = "$"{IRI2}

String          = \"[^\"]*\"
VString         = "$"{String}
StringTriple    = \"{3} ~\"{3}
VStringTriple   = "$"\"{3} ~\"{3}
Integer         = ("-" | "+")? [0-9]+
Decimal         = ("-" | "+")? [0-9]+ \. [0-9]+
YearMonth       = [0-9]{4} "-" (0 [1-9] | 1 [1-2])
Date            = {YearMonth} "-" (0 [1-9] | 1 [0-9] | 2 [0-9] | 3 [0-1])
Time            = [0-9]{2} : [0-9]{2} : [0-9]{2} (\.[0-9]+)? ({TimeZone})?
TimeZone        = Z | ( ( "+" | "-" ) [0-9]{2} : [0-9]{2} )
DateTime        = {Date}"T"{Time} 

EoD             = [ \t\n\r\f\v]*{LineTerminator}([ \t\n\r\f\v]*{LineTerminator})+

%%

{Whitespace}            { /* noop */ }

<YYINITIAL> {
    {Comment}           { /* noop */ }

    // Keywords
    "%encoding"         { return _token(TokenTypes.DIR_ENCODING); }
    "%prefix"           { return _token(TokenTypes.DIR_PREFIX); }
    "%alias"            { return _token(TokenTypes.DIR_ALIAS); }
    "%stm"              { return _token(TokenTypes.DIR_STM); }
    "%stop"             { return EOF; }

    // Identifiers
    {QName}             { return _token(TokenTypes.QNAME); }
    {Identifier}        { return _token(TokenTypes.IDENT); }
    {Variable}          { return _token(TokenTypes.VARIABLE, 1); }
    {IRI}               { return _token(TokenTypes.IRI); }
    {IRI2}              { return _token(TokenTypes.IRI, 1, 1); }    // <...>
    {EoD}               { return _token(TokenTypes.EOD); }

    // Brackets
    "("                 { return _token(TokenTypes.LPAREN); }
    ")"                 { return _token(TokenTypes.RPAREN); }

    "=>"                { return _token(TokenTypes.ASSIGN); }
    ":-"                { return _token(TokenTypes.IMPLIES); }
    ","                 { return _token(TokenTypes.COMMA); }
    ":"                 { return _token(TokenTypes.COLON); }
    "."                 { return _token(TokenTypes.DOT); }
    "^^"                { return _token(TokenTypes.DOUBLE_CIRCUMFLEX); }
    "="                 { return _token(TokenTypes.EQ); }
    "~"                 { return _token(TokenTypes.TILDE); }
    "-"                 { return _token(TokenTypes.HYPHEN); }
    "@"                 { return _token(TokenTypes.AT); }

    // Datatypes
    {VStringTriple}     { return _token(TokenTypes.VSTRING, 4, 3); }
    {VString}           { return _token(TokenTypes.VSTRING, 2, 1); } // $"..."
    {String}            { return _token(TokenTypes.STRING, 1, 1); }
    {StringTriple}      { return _token(TokenTypes.STRING, 3, 3); }
    {VIRI}              { return _token(TokenTypes.VIRI, 2, 1); }   // $<...>
    {Integer}           { return _token(TokenTypes.INTEGER); }
    {Decimal}           { return _token(TokenTypes.DECIMAL); }
    {YearMonth}         { return _token(TokenTypes.YEAR_MONTH); }
    {Date}              { return _token(TokenTypes.DATE); }
    {DateTime}          { return _token(TokenTypes.DATE_TIME); }
}

.|\n                    { throw new MIOParseException("Illegal character <" + yytext() + "> at line " + getLine() + " column: " + getColumn(), getLine(), getColumn()); }

