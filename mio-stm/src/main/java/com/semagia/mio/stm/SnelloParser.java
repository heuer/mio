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

import java.io.IOException;
import java.io.Reader;

import com.semagia.mio.MIOException;
import com.semagia.mio.MIOParseException;

/**
 * This class is the main entry point for parsing STM. It utilizes the 
 * {@link SnelloLexer}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 516 $ - $Date: 2010-09-11 19:18:19 +0200 (Sa, 11 Sep 2010) $
 */
final class SnelloParser extends RealSnelloParser {

    /**
     * Reads the STM instance from the specified <tt>reader</tt>.
     *
     * @param reader A reader.
     * @throws IOException If an I/O error occurs.
     * @throws MIOException If a syntax error happens.
     */
    public void parse(Reader reader) throws IOException, MIOException {
        yyInput lexer = new SnelloLexer(reader);
        try {
            yyparse(lexer);
        }
        catch (yyException ex) {
            throw new MIOParseException(ex.getMessage());
        }
    }

    @Override
    protected String tokenToName(int token) {
        try {
            return TokenTypes.name(token);
        }
        catch (RuntimeException ex) {
            return super.tokenToName(token);
        }
    }

}
