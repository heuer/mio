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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Collection;

import com.semagia.mio.IRIContext;
import com.semagia.mio.MIOException;
import com.semagia.mio.Property;
import com.semagia.mio.Source;
import com.semagia.mio.base.AbstractDefaultDeserializer;
import com.semagia.mio.helpers.Locator;
import com.semagia.mio.ltm.api.IPrefixListener;
import com.semagia.mio.utils.BOMInputStream;

/**
 * Deserializer which handles the 
 * <a href="http://www.ontopia.net/download/ltm.html">LTM 1.3</a> syntax.
 * <p>
 * This deserializer handles LTM instances somewhat different from the official
 * specification:
 * <ul>
 *  <li>Directives are not limited to the header of a LTM file they are allowed
 *      everywhere.</li>
 *  <li>Identifiers are not limited to <code>[A-Za-z0-9_-.]</code>, but may 
 *      contain Unicode characters.</li>
 *  <li>Roles are not postprocessed; if a role does not have a type, the type 
 *      is set to a default role type.</li>
 *  <li>Multiple subject locators are allowed: If a topic contains more than
 *      one subject locator, the subject locator is added to the topic.</li>
 *  <li>Subject identifiers / locators may occur in any order: The LTM 
 *      specification mandates, that the subject identifiers must be specified 
 *      after an subject locator (if given); this implementation does not 
 *      mandate any order; a subject locator may be followed by an subject 
 *      identifier etc.</li>
 *  <li>Reification follows the TMDM: There is no need to create an item 
 *      identifier / subject identifier as declared by the LTM specification; 
 *      this implementation assumes that a reifier is a property of a 
 *      particular Topic Maps construct.</li>
 * </ul>
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 467 $ - $Date: 2010-09-08 12:17:40 +0200 (Mi, 08 Sep 2010) $
 */
final class LTMDeserializer extends AbstractDefaultDeserializer {

    /**
     * The LTM parser instance.
     */
    private LTMParser _parser;

    /**
     * Indicates how much bytes are needed to detect an encoding directive. 
     */
    private static final int _BUFFER_SIZE = 25;

    /**
     * Default LTM encoding.
     */
    private static final String _DEFAULT_ENCODING = "iso-8859-1";

    /**
     * Constructs a new LTM deserializer.
     *
     */
    public LTMDeserializer() {
        super();
        _parser = new LTMParser();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.base.AbstractDeserializer#_parse(org.xml.sax.InputSource, java.lang.String)
     */
    @Override
    protected void doParse(final Source src) throws IOException,
            MIOException {
        final Reader reader = _reader(src);
        try {
            _parser.setPrefixListener((IPrefixListener) getProperty("http://psi.semagia.com/mio/property/ltm/prefix-listener"));
            _parser.setLegacyMode(Boolean.TRUE.equals(getProperty(Property.LTM_LEGACY)));
            _parser.setIgnoreMergemap(Boolean.TRUE.equals(getProperty(Property.IGNORE_MERGEMAP)));
            _parser.setIgnoreInclude(Boolean.TRUE.equals(getProperty(Property.IGNORE_INCLUDE)));
            _parser.setDocumentIRI(src.getBaseIRI());
            _parser.setSubordinate(_isSubordinate);
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

    /**
     * Sets the prefix listener.
     *
     * @param listener An instance of {@link IPrefixListener} or {@code null}.
     */
    void setPrefixListener(final IPrefixListener listener) {
        _parser.setPrefixListener(listener);
    }

    /**
     * Sets the IRIs of the documents from which thie.
     *
     * @param locators A collection of IRIs which are 
     */
    public void setIncludedBy(final Collection<Locator> locators) {
        _parser.setIncludedBy(locators);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IDeserializer#getContext()
     */
    @Override
    public IRIContext getIRIContext() {
        return _parser.getContext();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IDeserializer#setContext(com.semagia.mio.Context)
     */
    @Override
    public void setIRIContext(final IRIContext ctx) {
        _parser.setContext(ctx);
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
     * If the neither the <code>encoding</code>, nor the BOM nor an encoding d
     * irective is provided, the default encoding is used.
     * </p>
     *
     * @param in An input stream.
     * @param encoding An encoding or <code>null</code> if the encoding should be
     *          detected.
     * @return A reader.
     * @throws IOException If the construction of the reader fails.
     * @throws MIOException If the encoding directive contradicts the BOM.
     */
    private static Reader _reader(final InputStream in, String encoding) throws IOException, MIOException {
        if (encoding != null) {
            return new InputStreamReader(in, encoding);
        }
        final BOMInputStream stream = new BOMInputStream(in, _DEFAULT_ENCODING);
        final PushbackInputStream pbStream = new PushbackInputStream(stream, _BUFFER_SIZE);
        final byte[] buffer = new byte[_BUFFER_SIZE];
        final int read = pbStream.read(buffer, 0, _BUFFER_SIZE);
        if (read != -1) {
            final String tmp = new String(buffer);
            pbStream.unread(buffer, 0, read);
            if (tmp.startsWith("@\"")) {
                // Find next occurrence of '"'
                int end = tmp.indexOf(34, 2); // 34 == "
                if (end > -1) {
                    encoding = tmp.substring(2, end);
                    if (stream.foundBOM() && !stream.getEncoding().equalsIgnoreCase(encoding)) {
                        throw new MIOException("The BOM '" + stream.getEncoding() + "' contradicts the encoding directive '" + encoding + "'");
                    }
                }
                else {
                    throw new MIOException("Invalid LTM document: The encoding directive is not closed");
                }
            }
        }
        return stream.foundBOM() ? new InputStreamReader(pbStream, stream.getEncoding())
                                 : new InputStreamReader(pbStream, encoding != null ? encoding : _DEFAULT_ENCODING);
    }

}
