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
 * CRTM utility functions.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 580 $ - $Date: 2010-10-15 00:40:15 +0200 (Fr, 15 Okt 2010) $
 */
final class CRTMUtils {

    private CRTMUtils() {
        // noop.
    }

    private static boolean _isValidIdentifierStartChar(final int c) {
        return c == '_' ||
                c >= 65 && c <= 90 ||   // A - Z
                c >= 97 && c <= 122 ||  // a - z
                c >= 0x00C0 && c <= 0x00D6 ||
                c >= 0x00D8 && c <= 0x00F6 ||
                c >= 0x00F8 && c <= 0x02FF ||
                c >= 0x0370 && c <= 0x037D ||
                c >= 0x037F && c <= 0x1FFF ||
                c >= 0x200C && c <= 0x200D ||
                c >= 0x2070 && c <= 0x218F ||
                c >= 0x2C00 && c <= 0x2FEF ||
                c >= 0x3001 && c <= 0xD7FF ||
                c >= 0xF900 && c <= 0xFDCF ||
                c >= 0xFDF0 && c <= 0xFFFD ||
                c >= 0x10000 && c <= 0xEFFFF;
    }

    private static boolean _isNumber(final int c) {
        return c >= 48 && c <= 57; // 0 - 9
    }

    private static boolean isValidIdentifierPartChar(int c) {
        return _isValidIdentifierStartChar(c) ||
                _isNumber(c) ||
                c == '-' ||
                c == 0x00B7 ||
                c >= 0x0300 && c <= 0x036F ||
                c >= 0x203F && c <= 0x2040;
    }

    /**
     * Returns if the provided {@code id} is a valid CRTM identifier.
     *
     * @param id The identifier to check.
     * @return {@code true} if the id is valid, otherwise {@code false}.
     */
    public static boolean isValidId(final String id) {
        if (id == null) {
            return false; 
        }
        final char[] ch = id.toCharArray();
        final int len = ch.length;
        if (len == 0) {
            return false;
        }
        if (!_isValidIdentifierStartChar(ch[0])) {
            return false;
        }
        for(int i=1; i<len; i++) {
            if (!(isValidIdentifierPartChar(ch[i]) || ch[i] == '.')) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns if the provided {@code id} is a valid local part of a QName.
     *
     * @param id The id to check.
     * @return {@code true} if the id is valid, otherwise {@code false}.
     */
    public static boolean isValidLocalPart(final String id) {
        if (id == null) {
            return false; 
        }
        final char[] ch = id.toCharArray();
        final int len = ch.length;
        if (len == 0) {
            return false;
        }
        if ((!_isValidIdentifierStartChar(ch[0]) && !_isNumber(ch[0]))) {
            return false;
        }
        for(int i=1; i<len; i++) {
            if (!(isValidIdentifierPartChar(ch[i]) || ch[i] == '.')) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns if the provided reference can be used as IRI.
     *
     * @param reference The reference to check.
     * @return {@code true} if the {@code reference} represents a valid IRI, 
     *          otherwise {@code false}.
     */
    public static boolean isValidIRI(final String reference) {
        if (reference == null) {
            return false;
        }
        final char[] ch = reference.toCharArray();
        final int len = ch.length;
        if (len == 0) {
            return false;
        }
        for(int i=0; i < len; i++) {
            if (Character.isWhitespace(ch[i]) 
                    || ch[i] == '<' || ch[i] == '>' 
                    || ch[i] == '"' || ch[i] == '`' 
                    || ch[i] == '{' || ch[i] == '}' || ch[i] == '\\') {
                return false;
            }
        }
        return true;
    }

}
