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

import com.semagia.mio.ctm.CTMParser;

/**
 * CTM token types.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 606 $ - $Date: 2011-01-20 00:48:46 +0100 (Do, 20 Jan 2011) $
 */
final class TokenTypes {

    public static final int
        // Keywords
        KW_DEF = CTMParser.KW_DEF,
        KW_END = CTMParser.KW_END,
        KW_ISA = CTMParser.KW_ISA,
        KW_AKO = CTMParser.KW_AKO,

        DIR_ENCODING = CTMParser.DIR_ENCODING,
        DIR_VERSION = CTMParser.DIR_VERSION,
        DIR_PREFIX = CTMParser.DIR_PREFIX,
        DIR_INCLUDE = CTMParser.DIR_INCLUDE,
        DIR_MERGEMAP = CTMParser.DIR_MERGEMAP,

        // Identifiers
        IDENT = CTMParser.IDENT,
        QNAME = CTMParser.QNAME,
        VARIABLE = CTMParser.VARIABLE,
        WILDCARD = CTMParser.WILDCARD,
        NAMED_WILDCARD = CTMParser.NAMED_WILDCARD,

        // Delimiters

        // Brackets
        LPAREN = CTMParser.LPAREN,
        RPAREN = CTMParser.RPAREN,
        LBRACK = CTMParser.LBRACK,
        RBRACK = CTMParser.RBRACK,

        COMMA = CTMParser.COMMA,
        SEMI = CTMParser.SEMI,
        COLON = CTMParser.COLON,
        DOT = CTMParser.DOT,
        DOUBLE_CIRCUMFLEX = CTMParser.DOUBLE_CIRCUMFLEX,
        EQ = CTMParser.EQ,
        CIRCUMFLEX = CTMParser.CIRCUMFLEX,
        TILDE = CTMParser.TILDE,
        HYPHEN = CTMParser.HYPHEN,
        AT = CTMParser.AT,

        // Datatypes
        STRING = CTMParser.STRING,
        IRI = CTMParser.IRI,
        INTEGER = CTMParser.INTEGER,
        DECIMAL = CTMParser.DECIMAL,
        DATE = CTMParser.DATE,
        DATE_TIME = CTMParser.DATE_TIME,
        STAR = CTMParser.STAR
    ;

    /**
     * Returns a name for the specified token <code>type</code>.
     *
     * <p>
     * Useful for debugging purposes and error messages.
     * </p>
     * 
     * @param type A token type.
     * @return The name of the token type.
     */
    public static String name(final int type) {
        switch (type) {
            // Keywords
            case KW_DEF:        return "def";
            case KW_END:        return "end";
            case KW_ISA:        return "isa";
            case KW_AKO:        return "ako";

            case DIR_ENCODING:  return "%encoding";
            case DIR_VERSION:   return "%version";
            case DIR_PREFIX:    return "%prefix";
            case DIR_INCLUDE:   return "%include";
            case DIR_MERGEMAP:  return "%mergemap";

            // Identifiers
            case IDENT:         return "<identifier>";
            case QNAME:         return "<qname>";
            case VARIABLE:      return "<variable>";
            case WILDCARD:      return "?";
            case NAMED_WILDCARD: return "?<identifier>";
            // Delimiters
            // Brackets
            case LPAREN:        return "(";
            case RPAREN:        return ")";
            case LBRACK:        return "[";
            case RBRACK:        return "]";
            
            case COMMA:         return ",";
            case SEMI:          return ";";
            case COLON:         return ":";
            case DOT:           return ".";
            case DOUBLE_CIRCUMFLEX: return "^^";
            case EQ:            return "=";
            case CIRCUMFLEX:    return "^";
            case TILDE:         return "~";
            case HYPHEN:        return "-";
            case AT:            return "@";
            // Datatypes
            case STRING:        return "<string>";
            case IRI:           return "<iri>";
            case INTEGER:       return "<integer>";
            case DECIMAL:       return "<decimal>";
            case DATE:          return "<date>";
            case DATE_TIME:     return "<dateTime>";
            case STAR:          return "*";
        }
        throw new RuntimeException("Unknown token type '" + type + "'");
    }
}
