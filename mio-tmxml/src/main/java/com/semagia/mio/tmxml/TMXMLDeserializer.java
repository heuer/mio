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
package com.semagia.mio.tmxml;

import java.io.IOException;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.semagia.mio.MIOException;
import com.semagia.mio.MIOParseException;
import com.semagia.mio.Property;
import com.semagia.mio.Source;
import com.semagia.mio.base.AbstractDefaultDeserializer;
import com.semagia.mio.helpers.SimpleMapHandler;
import com.semagia.mio.tmxml.api.IPrefixListener;
import com.semagia.mio.utils.xml.RelaxNGValidatingContentHandler;
import com.semagia.mio.utils.xml.XMLUtils;

/**
 * {@link IDeserializer} implementation that is able to deserialize 
 * <a href="http://www.ontopia.net/topicmaps/tmxml.html">TM/XML</a> topic maps.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 467 $ - $Date: 2010-09-08 12:17:40 +0200 (Mi, 08 Sep 2010) $
 */
final class TMXMLDeserializer extends AbstractDefaultDeserializer {

    private TMXMLContentHandler _contentHandler;

    public TMXMLDeserializer() {
        super();
        _contentHandler = new TMXMLContentHandler();
        setProperty(Property.VALIDATE, Boolean.TRUE);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.base.AbstractDeserializer#_parse(org.xml.sax.InputSource, java.lang.String)
     */
    @Override
    protected void doParse(final Source src) throws IOException,
            MIOException {
        try {
            final XMLReader reader = _createXMLReader();
            _contentHandler.setDocumentIRI(src.getBaseIRI());
            _contentHandler.setMapHandler(SimpleMapHandler.create(super._handler));
            _contentHandler.setPrefixListener((IPrefixListener) getProperty("http://psi.semagia.com/mio/property/tmxml/prefix-listener"));
            final boolean validate = !Boolean.FALSE.equals(getProperty(Property.VALIDATE));
            reader.setContentHandler(validate ? RelaxNGValidatingContentHandler.create(_contentHandler, getClass().getResource("/tmxml.rnc")) : _contentHandler);
            reader.parse(XMLUtils.asInputSource(src));
        }
        catch (SAXException ex) {
            if (ex.getException() instanceof IOException) {
                throw (IOException) ex.getException();
            }
            if (ex instanceof SAXParseException) {
                throw new MIOParseException(ex, ((SAXParseException) ex).getLineNumber(), ((SAXParseException) ex).getColumnNumber());
            }
            throw new MIOException(ex);
        }
        finally {
            _contentHandler = null;
        }
    }

    /**
     * 
     *
     * @return
     * @throws SAXException
     */
    private static XMLReader _createXMLReader() throws SAXException {
        final XMLReader reader = XMLReaderFactory.createXMLReader();
        final String NS_SAX = "http://xml.org/sax/features/";
        reader.setFeature(NS_SAX + "string-interning", true);
        reader.setFeature(NS_SAX + "namespaces", true);
        return reader;
    }
}
