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

/**
 * CRTM token types.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 546 $ - $Date: 2010-09-25 11:21:45 +0200 (Sa, 25 Sep 2010) $
 */
final class TokenTypes {

    public static final int
        DIR_VERSION = CRTMParser.DIR_VERSION,
        DIR_PREFIX = CRTMParser.DIR_PREFIX,
        DIR_INCLUDE = CRTMParser.DIR_INCLUDE,
        DIR_LANG2SCOPE = CRTMParser.DIR_LANG2SCOPE,
        
        KW_SID = CRTMParser.KW_SID,
        KW_SLO = CRTMParser.KW_SLO,
        KW_IID = CRTMParser.KW_IID,
        
        KW_ASSOC = CRTMParser.KW_ASSOC,
        KW_OCC = CRTMParser.KW_OCC,
        KW_NAME = CRTMParser.KW_NAME,
        KW_ISA = CRTMParser.KW_ISA,
        KW_AKO = CRTMParser.KW_AKO,
        KW_LANG = CRTMParser.KW_LANG,
        KW_TRUE = CRTMParser.KW_TRUE,
        KW_FALSE = CRTMParser.KW_FALSE,
        
        IDENT = CRTMParser.IDENT,
        LOCAL_IDENT = CRTMParser.LOCAL_IDENT,
        QNAME = CRTMParser.QNAME,
        IRI = CRTMParser.IRI,
        
        EQ = CRTMParser.EQ,
        AT = CRTMParser.AT,
        COMMA = CRTMParser.COMMA,
        SEMI = CRTMParser.SEMI,
        COLON = CRTMParser.COLON,
        HYPHEN = CRTMParser.HYPHEN,
        LPAREN = CRTMParser.LPAREN,
        RPAREN = CRTMParser.RPAREN,
        LCURLY = CRTMParser.LCURLY,
        RCURLY = CRTMParser.RCURLY,
        
        EOF = CRTMParser.EOF;

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
            // Directives
            case DIR_VERSION:   return "%version";
            case DIR_PREFIX:    return "%prefix";
            case DIR_INCLUDE:   return "%include";
            case DIR_LANG2SCOPE: return "%langtoscope";
            // Keywords
            case KW_SID:        return "subject-identifier";
            case KW_SLO:        return "subject-locator";
            case KW_IID:        return "item-identifier";

            case KW_ASSOC:      return "association";
            case KW_OCC:        return "occurrence";
            case KW_NAME:       return "name";
            case KW_ISA:        return "isa";
            case KW_AKO:        return "ako";
            case KW_LANG:       return "lang";
            case KW_TRUE:       return "true";
            case KW_FALSE:      return "false";

            case IDENT:         return "<identifier>";
            case LOCAL_IDENT:   return "<local-identifier>";
            case QNAME:         return "<qname>";
            case IRI:           return "<iri>";

            case AT:            return "@";
            case EQ:            return "=";
            case COMMA:         return ",";
            case SEMI:          return ";";
            case COLON:         return ":";
            case HYPHEN:        return "-";
            case LPAREN:        return "(";
            case RPAREN:        return ")";
            case LCURLY:        return "{";
            case RCURLY:        return "}";

            case EOF:           return "<EOF>";
        }
        throw new IllegalArgumentException("Unknown token type '" + type + "'");
    }
}
