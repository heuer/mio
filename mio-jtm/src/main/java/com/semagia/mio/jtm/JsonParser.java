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
package com.semagia.mio.jtm;

import java.io.IOException;
import java.io.Reader;

import com.semagia.mio.MIOException;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 471 $ - $Date: 2010-09-08 13:25:57 +0200 (Mi, 08 Sep 2010) $
 */
final class JsonParser {

    private final JsonLexer _lexer;
    private int _current = -1;

    public JsonParser(final Reader reader) {
        _lexer = new JsonLexer(reader);
    }

    /**
     * Returns the next token.
     * 
     * Note: Colons and commas are omitted.
     *
     * @return The next token.
     * @throws IOException In case of an I/O error.
     * @throws MIOException In case of syntax errors.
     */
    public int nextToken() throws IOException, MIOException {
        _current = _lexer.token();
        if (_current == JsonToken.COLON || _current == JsonToken.COMMA) {
            _current = _lexer.token();
        }
        return _current;
    }

    /**
     * Returns the current token.
     *
     * @return The current token.
     */
    public int getCurrentToken() {
        return _current;
    }

    /**
     * Returns the text associated to the current token.
     * 
     * The text is unescaped (if necessary) automatically.
     *
     * @return The unescaped text.
     */
    public String getText() {
        final String value = _lexer.value();
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
            else if (c == '/') {
                sb.append('/');
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
            else {
                throw new IllegalArgumentException("Unescaped backslash in: " + value);
            }
            backSlash = value.indexOf('\\', pos);
        }
        sb.append(value.substring(pos));
        return sb.toString();
    }

}
