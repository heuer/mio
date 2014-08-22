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

import com.semagia.mio.ltm.LTMParser;

/**
 * LTM token types.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 467 $ - $Date: 2010-09-08 12:17:40 +0200 (Mi, 08 Sep 2010) $
 */
final class TokenTypes {

    public static final int
        DIR_PREFIX = LTMParser.DIR_PREFIX,
        DIR_BASEURI = LTMParser.DIR_BASEURI,
        DIR_MERGEMAP = LTMParser.DIR_MERGEMAP,
        DIR_INCLUDE = LTMParser.DIR_INCLUDE,
        DIR_VERSION = LTMParser.DIR_VERSION,
        DIR_TOPICMAP = LTMParser.DIR_TOPICMAP,

        QNAME = LTMParser.QNAME,
        IDENT = LTMParser.IDENT,

        LBRACK = LTMParser.LBRACK,
        RBRACK = LTMParser.RBRACK,
        LPAREN = LTMParser.LPAREN,
        RPAREN = LTMParser.RPAREN,
        LCURLY = LTMParser.LCURLY,
        RCURLY = LTMParser.RCURLY,

        AT = LTMParser.AT,
        PERCENT = LTMParser.PERCENT,
        EQ      = LTMParser.EQ,
        SEMI = LTMParser.SEMI,
        COLON = LTMParser.COLON,
        COMMA = LTMParser.COMMA,
        TILDE = LTMParser.TILDE,
        SLASH = LTMParser.SLASH,

        STRING = LTMParser.STRING,
        DATA = LTMParser.DATA;

    /**
     * Returns a name for the specified token <code>type</code>.
     * <p>
     * Useful for debugging purposes and error messages.
     * </p>
     * 
     * @param type A token type.
     * @return The name of the token type.
     */
    public static String name(final int type) {
        switch (type) {
            case DIR_PREFIX:    return "#PREFIX";
            case DIR_BASEURI:   return "#BASEURI";
            case DIR_MERGEMAP:  return "#MERGEMAP";
            case DIR_INCLUDE:   return "#INCLUDE";
            case DIR_VERSION:   return "#VERSION";
            case DIR_TOPICMAP:  return "#TOPICMAP";
            // Identifiers
            case IDENT:         return "<identifier>";
            case QNAME:         return "<qname>";
            // Delimiters
            // Brackets
            case LBRACK:        return "[";
            case RBRACK:        return "]";
            case LPAREN:        return "(";
            case RPAREN:        return ")";
            case LCURLY:        return "{";
            case RCURLY:        return "}";

            case AT:            return "@";
            case PERCENT:       return "%";
            case EQ:            return "=";
            case SEMI:          return ";";
            case COLON:         return ":";
            case COMMA:         return ",";
            case TILDE:         return "~";
            case SLASH:         return "/";

            // Datatypes
            case STRING:        return "<string>";
            case DATA:          return "<data>";
        }
        throw new RuntimeException("Unknown token type '" + type + "'");
    }
}
