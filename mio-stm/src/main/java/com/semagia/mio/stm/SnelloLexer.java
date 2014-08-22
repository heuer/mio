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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.semagia.mio.MIOException;
import com.semagia.mio.stm.RealSnelloParser.yyInput;

/**
 * Snello lexer which provides the API which the parser expects.
 * 
 * This class is used to keep the (JFlex) lexer independent of the (Jay) parser.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 469 $ - $Date: 2010-09-08 12:39:06 +0200 (Mi, 08 Sep 2010) $
 */
final class SnelloLexer extends RealSnelloLexer implements yyInput {

    /**
     * Current token identifier.
     */
    private int _current;

    /**
     * Indicates if the JFlex lexer has seen the EOF.
     */
    private boolean _eofSeen;

    /**
     * 
     *
     * @param in
     */
    public SnelloLexer(InputStream in) {
        this(new InputStreamReader(in));
    }

    /**
     * 
     *
     * @param reader
     */
    public SnelloLexer(Reader reader) {
        super(reader);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.RealSnelloParser.yyInput#advance()
     */
    @Override
    public boolean advance() throws IOException, MIOException {
        if (_eofSeen) {
            return false;
        }
        _current = super.token();
        _eofSeen = _current == RealSnelloLexer.EOF;
        if (_eofSeen) {
            // Ensure that "topic <<EOF>>" works: The last token is always a EOD!
            _current = TokenTypes.EOD;
            return true;
        }
        return !_eofSeen;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.lexer.RealSnelloLexer#value()
     */
    @Override
    public String value() {
        // Return a value if the EOD was inserted.
        return _eofSeen ? "\n\n" : super.value();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.lexer.RealSnelloLexer#token()
     */
    @Override
    public int token() {
        return _current;
    }

}
