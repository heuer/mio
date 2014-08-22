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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import com.semagia.mio.MIOException;
import com.semagia.mio.Source;
import com.semagia.mio.base.AbstractDefaultDeserializer;
import com.semagia.mio.helpers.SimpleMapHandler;
import com.semagia.mio.utils.BOMInputStream;

/**
 * A {@link IDeserializer} for 
 * <a href="http://www.cerny-online.com/jtm/">JSON Topic Maps (JTM)</a>.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 471 $ - $Date: 2010-09-08 13:25:57 +0200 (Mi, 08 Sep 2010) $
 */
final class JTMDeserializer extends AbstractDefaultDeserializer {

    /**
     * Default JSON encoding.
     */
    private static final String _DEFAULT_ENCODING = "utf-8";

    public JTMDeserializer() {
        super();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.base.AbstractDeserializer#doParse(com.semagia.mio.Source)
     */
    @Override
    protected void doParse(final Source src) throws IOException, MIOException {
        final Reader reader = _reader(src);
        final JTMParser parser = new JTMParser(src.getBaseIRI());
        parser.parse(new JsonParser(reader), SimpleMapHandler.create(_handler));
    }

    /**
     * Returns a reader from the input source.
     * <p>
     * If the input source provides a byte stream, the reader operates upon
     * that stream. If the input source provides no byte stream, the character
     * stream is used.
     * </p>
     *
     * @param src An input source.
     * @return A reader.
     * @throws IOException If the construction of the reader fails.
     */
    private static Reader _reader(final Source src) throws IOException {
        if (src.getByteStream() != null) {
            return _reader(src.getByteStream(), src.getEncoding());
        }
        else {
            final Reader reader = src.getCharacterStream();
            return reader != null ? reader 
                                  : _reader(new URL(src.getIRI()).openStream(), null);
        }
    }

    /**
     * Returns a reader from the input stream which uses either the provided
     * encoding (if not set to <code>null</code>) or tries to extract the 
     * correct encoding from the BOM or uses the default encoding.
     *
     * @param in An input stream.
     * @param encoding An encoding or <code>null</code> if the encoding should be
     *          detected.
     * @return A reader.
     * @throws IOException If the construction of the reader fails.
     */
    private static Reader _reader(final InputStream in, String encoding) throws IOException {
        if (encoding != null) {
            return new InputStreamReader(in, encoding);
        }
        final BOMInputStream stream = new BOMInputStream(in, _DEFAULT_ENCODING);
        return new InputStreamReader(stream, stream.getEncoding());
    }

}
