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

import java.io.IOException;
import java.io.Reader;

import com.semagia.mio.MIOException;

/**
 * Parser which connects the Jay-generated parser with the {@link CRTMLexer}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 552 $ - $Date: 2010-09-26 16:56:55 +0200 (So, 26 Sep 2010) $
 */
final class CRTMParser extends RealCRTMParser {

    public CRTMParser(final String iri) {
        super(iri);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.rdf.crtm.RealCRTMParser#tokenToName(int)
     */
    @Override
    protected String tokenToName(final int token) {
        try {
            return TokenTypes.name(token);
        }
        catch (IllegalArgumentException ex) {
            return super.tokenToName(token);
        }
    }

    /**
     * Reads the CRTM instance from the specified <code>reader</code>.
     *
     * @param reader A reader.
     * @throws IOException If an I/O error occurs.
     * @throws MIOException If a syntax error happens.
     */
    public void parse(final Reader reader) throws IOException, MIOException {
        final yyInput lexer = new CRTMLexer(reader);
        try {
            yyparse(lexer);
        }
        catch (yyException ex) {
            throw new MIOException(ex);
        }
    }

}
