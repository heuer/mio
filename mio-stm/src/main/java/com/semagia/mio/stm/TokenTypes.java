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
package com.semagia.mio.stm;


/**
 * Snello token types.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 469 $ - $Date: 2010-09-08 12:39:06 +0200 (Mi, 08 Sep 2010) $
 */
final class TokenTypes {

    public static final int
        // Keywords
        DIR_ENCODING = SnelloParser.DIR_ENCODING,
        DIR_STM = SnelloParser.DIR_STM,
        DIR_PREFIX = SnelloParser.DIR_PREFIX,
        DIR_ALIAS = SnelloParser.DIR_ALIAS,

        // Identifiers
        IDENT = SnelloParser.IDENT,
        QNAME = SnelloParser.QNAME,
        VARIABLE = SnelloParser.VARIABLE,

        // Delimiters

        // Brackets
        LPAREN = SnelloParser.LPAREN,
        RPAREN = SnelloParser.RPAREN,

        COMMA = SnelloParser.COMMA,
        COLON = SnelloParser.COLON,
        DOT = SnelloParser.DOT,
        DOUBLE_CIRCUMFLEX = SnelloParser.DOUBLE_CIRCUMFLEX,
        EQ = SnelloParser.EQ,
        APOS = SnelloParser.APOS,
        TILDE = SnelloParser.TILDE,
        HYPHEN = SnelloParser.HYPHEN,
        AT = SnelloParser.AT,
        ASSIGN = SnelloParser.ASSIGN,
        IMPLIES = SnelloParser.IMPLIES,

        // Datatypes
        VSTRING = SnelloParser.VSTRING,
        STRING = SnelloParser.STRING,
        VIRI = SnelloParser.VIRI,
        IRI = SnelloParser.IRI,
        INTEGER = SnelloParser.INTEGER,
        DECIMAL = SnelloParser.DECIMAL,
        DATE = SnelloParser.DATE,
        DATE_TIME = SnelloParser.DATE_TIME,
        YEAR_MONTH = SnelloParser.YEAR_MONTH,

        EOD = SnelloParser.EOD
    ;

    /**
     * Returns a name for the specified token <tt>type</tt>.
     *
     * <p>
     * Useful for debugging purposes and error messages.
     * </p>
     * 
     * @param type A token type.
     * @return The name of the token type.
     */
    public static String name(int type) {
        switch (type) {
            // Keywords
            case DIR_ENCODING:  return "%encoding";
            case DIR_STM:       return "%stm";
            case DIR_PREFIX:    return "%prefix";
            case DIR_ALIAS:     return "%alias";
            // Identifiers
            case IDENT:         return "<identifier>";
            case QNAME:         return "<qname>";
            case VARIABLE:      return "<variable>";
            // Delimiters
            // Brackets
            case LPAREN:        return "(";
            case RPAREN:        return ")";
            
            case COMMA:         return ",";
            case COLON:         return ":";
            case DOT:           return ".";
            case DOUBLE_CIRCUMFLEX: return "^^";
            case EQ:            return "=";
            case TILDE:         return "~";
            case HYPHEN:        return "-";
            case AT:            return "@";
            case ASSIGN:        return "=>";
            case IMPLIES:        return ":-";
            // Datatypes
            case VSTRING:       return "<variable string>";
            case STRING:        return "<string>";
            case VIRI:          return "<variable iri>";
            case IRI:           return "<iri>";
            case INTEGER:       return "<integer>";
            case DECIMAL:       return "<decimal>";
            case DATE:          return "<date>";
            case DATE_TIME:     return "<dateTime>";
            case YEAR_MONTH:    return "<gYearMonth>";
            // EOD
            case EOD:           return "<end of topic>";
        }
        throw new RuntimeException("Unknown token type '" + type + "'");
    }
}
