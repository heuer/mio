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
import java.io.PushbackInputStream;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.semagia.mio.Context;
import com.semagia.mio.MIOException;
import com.semagia.mio.Source;
import com.semagia.mio.base.AbstractDeserializer;
import com.semagia.mio.ctm.CTMParser;
import com.semagia.mio.ctm.api.IPrefixListener;
import com.semagia.mio.utils.BOMInputStream;

/**
 * Deserializer which reads
 * <a href="http://www.isotopicmaps.org/ctm/">CTM</a>.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 606 $ - $Date: 2011-01-20 00:48:46 +0100 (Do, 20 Jan 2011) $
 */
final class CTMDeserializer extends AbstractDeserializer {

    /**
     * Indicates how much bytes are needed to detect an encoding directive. 
     */
    private static final int _BUFFER_SIZE = 50;

    /**
     * Default CTM encoding.
     */
    private static final String _DEFAULT_ENCODING = "utf-8";

    private CTMParser _parser;
    private final Map<String, Object> _properties;

    /**
     * 
     *
     */
    public CTMDeserializer() {
        super();
        _parser = new CTMParser();
        _properties = new HashMap<String, Object>();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.base.AbstractDeserializer#_parse(org.xml.sax.InputSource, java.lang.String)
     */
    @Override
    protected void doParse(Source src) throws IOException,
            MIOException {
        final Reader reader = _reader(src);
        try {
            _parser.setSubordinate(super._isSubordinate);
            _parser.setPrefixListener((IPrefixListener) getProperty("http://psi.semagia.com/mio/property/ctm/prefix-listener"));
            _parser.setDocumentIRI(src.getBaseIRI());
            _parser.setMapHandler(super._handler);
            _parser.parse(reader);
        }
        finally {
            _parser = null;
            if (reader != null) {
                reader.close();
            }
        }
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IDeserializer#getContext()
     */
    @Override
    public Context getContext() {
        return _parser.getContext();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IDeserializer#setContext(com.semagia.mio.Context)
     */
    @Override
    public void setContext(final Context ctx) {
        _parser.setContext(ctx);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IPropertyAware#setProperty(java.lang.String, java.lang.Object)
     */
    @Override
    public void setProperty(final String iri, final Object value) {
        _properties.put(iri, value);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IDeserializer#getProperty(java.lang.String)
     */
    @Override
    public Object getProperty(final String iri) {
        return _properties.get(iri);
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
     * @throws MIOException If the source is invalid (i.e. bad syntax).
     */
    private static Reader _reader(final Source src) throws IOException, MIOException {
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
     * correct encoding either from the BOM or the encoding directive.
     * <p>
     * If the neither the <code>encoding</code>, nor the BOM nor an encoding 
     * directive is provided, the default encoding is used.
     * </p>
     *
     * @param in An input stream.
     * @param encoding An encoding or <code>null</code> if the encoding should be
     *          detected.
     * @return A reader.
     * @throws IOException If the construction of the reader fails.
     * @throws MIOException If the encoding directive contradicts the BOM.
     */
    private static Reader _reader(InputStream in, String encoding) throws IOException, MIOException {
        if (encoding != null) {
            return new InputStreamReader(in, encoding);
        }
        final BOMInputStream stream = new BOMInputStream(in, _DEFAULT_ENCODING);
        encoding = stream.getEncoding();
        final PushbackInputStream pbStream = new PushbackInputStream(stream, _BUFFER_SIZE);
        final byte[] buffer = new byte[_BUFFER_SIZE];
        final int read = pbStream.read(buffer, 0, _BUFFER_SIZE);
        if (read != -1) {
            final String tmp = new String(buffer);
            pbStream.unread(buffer, 0, read);
            if (tmp.startsWith("%encoding")) {
                int start = tmp.indexOf(34, 9)+1;
                if (start == 0) {
                    throw new MIOException("Invalid CTM document: The encoding directive seems to be invalid");
                }
                int end = tmp.indexOf(34, start); // 34 == "
                if (end > -1) {
                    encoding = tmp.substring(start, end);
                    if (stream.foundBOM() && !stream.getEncoding().equalsIgnoreCase(encoding)) {
                        throw new MIOException("The BOM '" + stream.getEncoding() + "' contradicts the encoding directive '" + encoding + "'");
                    }
                }
                else {
                    throw new MIOException("Invalid CTM document: The encoding directive is not closed");
                }
            }
            return new InputStreamReader(pbStream, encoding);
        }
        return new InputStreamReader(stream, stream.getEncoding());
    }

    CTMParser getParser() {
        return _parser;
    }
}
