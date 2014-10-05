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
package com.semagia.mio.xtm;

import java.io.IOException;
import java.io.StringReader;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.semagia.mio.IRIContext;
import com.semagia.mio.ISyntaxVersionAware;
import com.semagia.mio.MIOException;
import com.semagia.mio.MIOParseException;
import com.semagia.mio.Property;
import com.semagia.mio.Source;
import com.semagia.mio.base.AbstractDeserializer;
import com.semagia.mio.utils.xml.RelaxNGValidatingContentHandler;
import com.semagia.mio.utils.xml.XMLUtils;

/**
 * Abstract superclass for all XTM-based deserializers.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 583 $ - $Date: 2010-10-18 23:31:34 +0200 (Mo, 18 Okt 2010) $
 */
abstract class AbstractXTMDeserializer<T extends IXTMContentHandler> extends
        AbstractDeserializer implements ISyntaxVersionAware {

    protected T _contentHandler;

    protected AbstractXTMDeserializer(T contentHandler) {
        _contentHandler = contentHandler;
        setProperty(Property.VALIDATE, Boolean.TRUE);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.base.AbstractDeserializer#_parse(org.xml.sax.InputSource, java.lang.String)
     */
    @Override
    protected void doParse(final Source src) throws IOException,
            MIOException {
        try {
            XMLReader reader = _createXMLReader();
            _contentHandler.setDocumentIRI(src.getBaseIRI());
            _contentHandler.setMapHandler(super._handler);
            _contentHandler.setSubordianate(_isSubordinate);
            if (src.getBaseIRI() != null) {
                getIRIContext().addIRI(src.getBaseIRI());
            }
            if (src.getIRI() != null) {
                getIRIContext().addIRI(src.getIRI());
            }
            if (!Boolean.FALSE.equals(getProperty(Property.VALIDATE)) 
                    // The XTMContentHandler handles the validation itself
                    && !(_contentHandler instanceof XTMContentHandler)) {
                reader.setContentHandler(RelaxNGValidatingContentHandler.create(_contentHandler, _contentHandler.getRelaxURL()));
            }
            else {
                reader.setContentHandler(_contentHandler);
            }
            reader.parse(XMLUtils.asInputSource(src));
            afterParse();
        }
        catch (SAXException ex) {
            if (ex.getException() instanceof IOException) {
                throw (IOException) ex.getException();
            }
            if (ex instanceof SAXParseException) {
                throw new MIOParseException(ex.getMessage(), ex, ((SAXParseException) ex).getLineNumber(), ((SAXParseException) ex).getColumnNumber());
            }
            throw new MIOException(ex.getMessage(), ex);
        }
        finally {
            _contentHandler = null;
        }
    }

    /**
     * Called after successfully parsing, does nothing by default.
     */
    protected void afterParse() {
        // noop.
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IDeserializer#getContext()
     */
    @Override
    public IRIContext getIRIContext() {
        return _contentHandler.getIRIContext();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IDeserializer#setContext(com.semagia.mio.Context)
     */
    @Override
    public void setIRIContext(IRIContext ctx) {
        _contentHandler.setIRIContext(ctx);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IDeserializer#setProperty(java.lang.String, java.lang.String)
     */
    @Override
    public void setProperty(String iri, Object value) {
        _contentHandler.setProperty(iri, value);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IPropertyAware#getProperty(java.lang.String)
     */
    @Override
    public Object getProperty(String iri) {
        return _contentHandler.getProperty(iri);
    }

    /**
     * Returns a new XMLReader instance.
     *
     * @return A new XMLReader instance.
     * @throws SAXException In case of an error.
     */
    protected final static XMLReader _createXMLReader() throws SAXException {
        final XMLReader reader = XMLReaderFactory.createXMLReader();
        final String NS_SAX = "http://xml.org/sax/features/";
        reader.setFeature(NS_SAX + "validation", false);
        reader.setFeature(NS_SAX + "string-interning", true);
        reader.setFeature(NS_SAX + "namespaces", true);
        reader.setFeature(NS_SAX + "external-parameter-entities", false);
        reader.setEntityResolver(new IgnoreDTDEntityResolver());
        return reader;
    }


    /**
     * EntityResolver implementation which ignored DTDs.
     */
    private static final class IgnoreDTDEntityResolver implements EntityResolver {

        /* (non-Javadoc)
         * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String, java.lang.String)
         */
        @Override
        public InputSource resolveEntity(final String publicId, final String systemId) 
                throws SAXException, IOException {
            if (publicId != null && "-//TopicMaps.Org//DTD XML Topic Map (XTM) 1.0//EN".equals(publicId) 
                    || (systemId != null && systemId.toLowerCase().endsWith(".dtd"))) {
                return new InputSource(new StringReader(""));
            }
            return null;
        }

    }
}
