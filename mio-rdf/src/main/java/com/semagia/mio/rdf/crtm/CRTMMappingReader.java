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
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import com.semagia.mio.MIOException;
import com.semagia.mio.Source;
import com.semagia.mio.rdf.api.IMappingHandler;
import com.semagia.mio.rdf.api.IMappingPrefixListener;
import com.semagia.mio.rdf.api.IMappingReader;
import com.semagia.mio.utils.BOMInputStream;

/**
 * {@link IMappingReader} implementation for Compact RDF to Topic Maps mappings.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 552 $ - $Date: 2010-09-26 16:56:55 +0200 (So, 26 Sep 2010) $
 */
final class CRTMMappingReader implements IMappingReader {

    private final String _DEFAULT_ENCODING = "utf-8";
    private IMappingPrefixListener _listener;
    private IMappingHandler _handler;

    public CRTMMappingReader() {
        _listener = null;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.rdf.api.IMappingReader#read()
     */
    @Override
    public void read(final Source src) throws IOException, MIOException {
        //TODO: Check for characterstream / bytetream
        final BOMInputStream stream = new BOMInputStream(new URL(src.getBaseIRI()).openStream(), _DEFAULT_ENCODING);
        final Reader reader = new InputStreamReader(stream, stream.getEncoding());
        final CRTMParser parser = new CRTMParser(src.getBaseIRI());
        parser.setMappingHandler(_handler);
        parser.setPrefixListener(_listener);
        _handler.start();
        parser.parse(reader);
        _handler.end();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.rdf.api.IMappingReader#getMappingHandler()
     */
    @Override
    public IMappingHandler getMappingHandler() {
        return _handler;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.rdf.api.IMappingReader#setMappingHandler(com.semagia.mio.rdf.api.IMappingHandler)
     */
    @Override
    public void setMappingHandler(final IMappingHandler handler) {
        _handler = handler;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.rdf.api.IMappingReader#setPrefixListener(com.semagia.mio.rdf.api.IMappingPrefixListener)
     */
    @Override
    public void setPrefixListener(IMappingPrefixListener listener) {
        _listener = listener;
    }

}
