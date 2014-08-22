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

import java.io.IOException;
import java.io.Reader;

import com.semagia.mio.MIOException;
import com.semagia.mio.ltm.RealLTMParser.yyInput;

/**
 * Tokenizer for LTM tokens.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 467 $ - $Date: 2010-09-08 12:17:40 +0200 (Mi, 08 Sep 2010) $
 */
final class LTMLexer extends RealLTMLexer implements yyInput {

    private int _current;

    public LTMLexer(final Reader reader) {
        super(reader);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ltm.parser.LTMParser.yyInput#advance()
     */
    @Override
    public boolean advance() throws IOException, MIOException {
        _current = super.token();
        return _current != RealLTMLexer.EOF;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ltm.lexer.RealLTMLexer#token()
     */
    @Override
    public int token() {
        return _current;
    }

}
