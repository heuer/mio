/*
 * Copyright 2008 - 2010 Lars Heuer (heuer[at]semagia.com)
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
package com.semagia.mio.rdf.crtm;

import com.semagia.mio.MIOException;
import com.semagia.mio.MIOParseException;
import com.semagia.mio.rdf.crtm.TokenTypes;

/**
 * CRTM tokenizer.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 336 $ - $Date: 2010-08-08 11:41:40 +0200 (So, 08 Aug 2010) $
 */
@SuppressWarnings("unused")
%%

%class RealCRTMLexer

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
        return yycolumn+1;
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
Whitespace      = {LineTerminator}|[ \t\f]
Comment         = "#"[^\r\n]*

IdentifierStart = [a-zA-Z_] | [\u00C0-\u00D6] | [\u00D8-\u00F6] 
                            | [\u00F8-\u02FF] | [\u0370-\u037D] 
                            | [\u037F-\u1FFF] | [\u200C-\u200D] 
                            | [\u2070-\u218F] | [\u2C00-\u2FEF] 
                            | [\u3001-\uD7FF] | [\uF900-\uFDCF] 
                            | [\uFDF0-\uFFFD] | [\u{10000}-\u{EFFFF}]
IdentifierChar = {IdentifierStart} | [\-0-9] 
                                   | \u00B7 
                                   | [\u0300-\u036F] 
                                   | [\u203F-\u2040]
Identifier      = {IdentifierStart}(\.*{IdentifierChar})*
LocalPart       = ([0-9]+(\.*{IdentifierChar})*)
QName           = {Identifier}":"({LocalPart}|{Identifier})

IRI             = "<"[^<>\"\{\}\`\\ ]+">"

%xstate KW LANG

%%

{Whitespace}        { /* noop */ }
{Comment}           { /* noop */ }

<YYINITIAL> {
    // Directives
    "%prefix"           { return _token(TokenTypes.DIR_PREFIX); }
    "%include"          { return _token(TokenTypes.DIR_INCLUDE); }
    "%langtoscope"      { yybegin(LANG); return _token(TokenTypes.DIR_LANG2SCOPE); }
    "%version"          { return _token(TokenTypes.DIR_VERSION); }

    {QName}             { return _token(TokenTypes.QNAME); }
    {Identifier}        { return _token(TokenTypes.IDENT); }
    {LocalPart}         { return _token(TokenTypes.LOCAL_IDENT); }
    {IRI}               { return _token(TokenTypes.IRI, 1, 1); }    // <...>

    // Brackets
    "("                 { return _token(TokenTypes.LPAREN); }
    ")"                 { return _token(TokenTypes.RPAREN); }
    "{"                 { return _token(TokenTypes.LCURLY); }
    "}"                 { return _token(TokenTypes.RCURLY); }

    "@"                 { return _token(TokenTypes.AT); }
    "-"                 { return _token(TokenTypes.HYPHEN); }
    ":"                 { yybegin(KW); return _token(TokenTypes.COLON); }
    ","                 { return _token(TokenTypes.COMMA); }
    ";"                 { yybegin(LANG); return _token(TokenTypes.SEMI); }
}

<LANG> {
    {Whitespace}        { /* noop */ }
    "="                 { return _token(TokenTypes.EQ); }
    "lang"              { return _token(TokenTypes.KW_LANG); }
    "true"              { yybegin(YYINITIAL); return _token(TokenTypes.KW_TRUE); }
    "false"             { yybegin(YYINITIAL); return _token(TokenTypes.KW_FALSE); }
    .                   { yypushback(1); yybegin(YYINITIAL); }
}

<KW> {
    {Whitespace}                  { /* noop */ }
    // Keywords
    "subject-identifier" | "sid"  { yybegin(YYINITIAL); return _token(TokenTypes.KW_SID); }
    "subject-locator" | "slo"     { yybegin(YYINITIAL); return _token(TokenTypes.KW_SLO); }
    "item-identifier" | "iid"     { yybegin(YYINITIAL); return _token(TokenTypes.KW_IID); }
    "isa"                         { yybegin(YYINITIAL); return _token(TokenTypes.KW_ISA); }
    "ako"                         { yybegin(YYINITIAL); return _token(TokenTypes.KW_AKO); }
    "name"                        { yybegin(YYINITIAL); return _token(TokenTypes.KW_NAME); }
    "occurrence" | "occ"          { yybegin(YYINITIAL); return _token(TokenTypes.KW_OCC); }
    "association" | "assoc"       { yybegin(YYINITIAL); return _token(TokenTypes.KW_ASSOC); }
    .                             { yypushback(1); yybegin(YYINITIAL); }
}

<<EOF>>                 { return _token(TokenTypes.EOF); }

.|\n                    { throw new MIOParseException("Illegal character <" + yytext() + "> at line " + getLine() + " column: " + getColumn(), getLine(), getColumn()); }
