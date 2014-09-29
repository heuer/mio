/*
 * Copyright 2008 - 2014 Lars Heuer (heuer[at]semagia.com)
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

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.semagia.mio.voc.XSD;

/**
 * CTM utility functions.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public final class CTMUtils {

    static final String CTM_INTEGER = "http://psi.topicmaps.org/iso13250/ctm-integer";

    private static final char[] _TRIPLE_QUOTES = new char[] {'"', '"', '"'};

    // Variable names are more complex, just match everything which 
    // starts with $ and check in findVariables if it's a valid (variable) name.
    private static Pattern _VARIABLE_PATTERN = Pattern.compile("\\$[^\\s,\\(\\)<]+");


    private CTMUtils() {
        // noop.
    }

    /**
     * Returns if the provided identifier is a CTM keyword.
     *
     * @param id The id to check.
     * @return {@code true} if {@code id} is a keyword, otherwise {@code false}.
     */
    public static boolean isKeyword(final String id) {
        return id.length() == 3
                && ("def".equals(id)
                        || "end".equals(id)
                        || "isa".equals(id)
                        || "ako".equals(id));
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
                c == '.' ||
                c == 0x00B7 ||
                c >= 0x0300 && c <= 0x036F ||
                c >= 0x203F && c <= 0x2040;
    }

    /**
     * Returns if the provided {@code id} is a valid CTM topic identifier.
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
        if (!_isValidIdentifierStartChar(ch[0]) || ch[len-1] == '.') {
            return false;
        }
        for(int i=1; i<len; i++) {
            if (!isValidIdentifierPartChar(ch[i])) {
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
        if ((!_isValidIdentifierStartChar(ch[0]) && !_isNumber(ch[0])) 
                || ch[len-1] == '.') {
            return false;
        }
        for(int i=1; i<len; i++) {
            if (!isValidIdentifierPartChar(ch[i])) {
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

    /**
     * Returns if the provided {@code datatype} is supported by CTM natively.
     *
     * @param datatype The datatype to check.
     * @return {@code true} if the datatype} is supported, otherwise {@code false}.
     */
    public static boolean isNativeDatatype(final String datatype) {
        return XSD.DECIMAL.equals(datatype)
                || XSD.INTEGER.equals(datatype)
                || XSD.DATE.equals(datatype)
                || XSD.DATE_TIME.equals(datatype)
                || CTM_INTEGER.equals(datatype);
    }

    /**
     * Returns all variables contained in the provided string.
     * 
     * Returns the same result as {@link #findVariables(String, boolean)} 
     * with {@code omitDollar = false}.
     * 
     * @param str A string.
     * @return A list of variable names ($a, $b, etc.) 
     */
    public static List<String> findVariables(final String str) {
        return findVariables(str, false);
    }

    /**
     * Returns all variables contained in the provided string.
     * 
     * If {@code omitDollar} is {@code true}, the returned variables names 
     * are returned without the {@code $} prefix ({@code $a -> a}).
     * 
     * @param str A string.
     * @param omitDollar Indicates if the {@code $} prefix should be omitted.
     * @return A list of variable names.
     */
    public static List<String> findVariables(final String str, final boolean omitDollar) {
        final Matcher m = _VARIABLE_PATTERN.matcher(str);
        final List<String> variables = new ArrayList<String>(2);
        while (m.find()) {
            final String ident = m.group().substring(1);
            if (isValidId(ident)) {
                final String variable = omitDollar ? ident : m.group();
                if (!variables.contains(variable)) {
                    variables.add(variable);
                }
            }
        }
        return variables;
    }

    /**
     * Serializes the provided {@code string}.
     * <p>
     * This method recognizes characters which have to be escaped.
     * </p>
     *
     * @param out The writer to use.
     * @param string The string to write.
     * @throws IOException In case of an error.
     */
    static void writeString(final Writer out, final String string) throws IOException {
        final char[] ch = string.toCharArray();
        final int len = ch.length;
        // Avoid escaping of "
        if (len > 0 && string.indexOf('"') > 0 && ch[len-1] != '"') {
            out.write(_TRIPLE_QUOTES);
            for (int i=0; i<len; i++) {
                switch (ch[i]) {
                    case '\\':
                        out.write("\\");
                    default:
                        out.write(ch[i]);
                }
            }
            out.write(_TRIPLE_QUOTES);
        }
        else {
            // Either the string ends with a " or the string is a 'normal' string
            out.write('"');
            for (int i=0; i<len; i++) {
                switch (ch[i]) {
                    case '"':
                    case '\\':
                        out.write("\\");
                    default:
                        out.write(ch[i]);
                }
            }
            out.write('"');
        }
    }

    /**
     * Unescapes a CTM string if necessary (i.e. if Unicode escape sequences
     * are found).
     * 
     *
     * @param value The string to unescape.
     * @return An unescaped string.
     * @throws IllegalArgumentException In case of an invalid Unicode escape sequence.
     */
    static final String unescapeString(final String value) throws IllegalArgumentException {
        int backSlash = value.indexOf('\\');
        if (backSlash == -1) {
            return value;
        }
        final StringBuilder sb = new StringBuilder();
        int pos = 0;
        int length = value.length();
        while (backSlash != -1) {
            sb.append(value.substring(pos, backSlash));
            if (backSlash + 1 >= length) {
                throw new IllegalArgumentException("Invalid escape syntax: " + value);
            }
            char c = value.charAt(backSlash + 1);
            if (c == 't') {
                sb.append('\t');
                pos = backSlash + 2;
            }
            else if (c == 'r') {
                sb.append('\r');
                pos = backSlash + 2;
            }
            else if (c == 'n') {
                sb.append('\n');
                pos = backSlash + 2;
            }
            else if (c == '"') {
                sb.append('"');
                pos = backSlash + 2;
            }
            else if (c == '\\') {
                sb.append('\\');
                pos = backSlash + 2;
            }
            else if (c == 'u') {
                // \\uxxxx
                if (backSlash + 5 >= length) {
                    throw new IllegalArgumentException(
                            "Incomplete Unicode escape sequence in: " + value);
                }
                String xx = value.substring(backSlash + 2, backSlash + 6);
                try {
                    c = (char)Integer.parseInt(xx, 16);
                    sb.append(c);
                    pos = backSlash + 6;
                }
                catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                            "Illegal Unicode escape sequence '\\u" + xx + "' in: " + value);
                }
            }
            else if (c == 'U') {
                // \\Uxxxxxx
                if (backSlash + 7 >= length) {
                    throw new IllegalArgumentException(
                            "Incomplete Unicode escape sequence in: " + value);
                }
                String xx = value.substring(backSlash + 2, backSlash + 8);
                try {
                    c = (char)Integer.parseInt(xx, 16);
                    sb.append(c);
                    pos = backSlash + 8;
                }
                catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                            "Illegal Unicode escape sequence '\\U" + xx + "' in: " + value);
                }
            }
            else {
                throw new IllegalArgumentException("Unescaped backslash in: " + value);
            }
            backSlash = value.indexOf('\\', pos);
        }
        sb.append(value.substring(pos));
        return sb.toString();
    }

}
