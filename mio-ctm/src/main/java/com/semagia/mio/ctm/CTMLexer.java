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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.semagia.mio.MIOException;
import com.semagia.mio.ctm.RealCTMParser.yyInput;

/**
 * CTM lexer which provides the API which the parser expects.
 * 
 * This class is used to keep the (JFlex) lexer independent of the (Jay) parser.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 606 $ - $Date: 2011-01-20 00:48:46 +0100 (Do, 20 Jan 2011) $
 */
final class CTMLexer extends RealCTMLexer implements yyInput {

    /**
     * Current token identifier.
     */
    private int _current;

    /**
     * 
     *
     * @param in
     */
    public CTMLexer(final InputStream in) {
        this(new InputStreamReader(in));
    }

    /**
     * 
     *
     * @param reader
     */
    public CTMLexer(final Reader reader) {
        super(reader);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.RealCTMParser.yyInput#advance()
     */
    @Override
    public boolean advance() throws IOException, MIOException {
        _current = super.token();
        return _current != RealCTMLexer.EOF;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.RealCTMLexer#token()
     */
    @Override
    public int token() {
        return _current;
    }

}
