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

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.semagia.mio.Context;
import com.semagia.mio.IMapHandler;
import com.semagia.mio.Property;
import com.semagia.mio.utils.xml.RelaxNGValidatingContentHandler;

/**
 * Content handler which can be used to parse XTM 1.0, XTM 2.0 and XTM 2.1 
 * topic maps.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 462 $ - $Date: 2010-09-08 01:20:13 +0200 (Mi, 08 Sep 2010) $
 */
final class XTMContentHandler extends DefaultHandler implements IXTMContentHandler {

    private final Map<String, Object> _properties;
    private final Map<String, String> _prefixes;
    private ContentHandler _contentHandler;
    private IMapHandler _mapHandler;
    private String _docIRI;
    private Context _context;
    private boolean _isSubordinate;

    XTMContentHandler() {
        _properties = new HashMap<String, Object>();
        _prefixes = new HashMap<String, String>();
    }

    /**
     * Returns the underlying content handler.
     *
     * @return The content handler or <code>null</code> if the content handler 
     *          wasn't detected yet.
     */
    public ContentHandler getContentHandler() {
        return _contentHandler;
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#startPrefixMapping(java.lang.String, java.lang.String)
     */
    @Override
    public void startPrefixMapping(String prefix, String uri)
            throws SAXException {
        if (_contentHandler == null) {
            _prefixes.put(prefix, uri);
        }
        else {
            _contentHandler.startPrefixMapping(prefix, uri);
        }
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#endPrefixMapping(java.lang.String)
     */
    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        _contentHandler.endPrefixMapping(prefix);
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attrs) throws SAXException {
        if (_contentHandler == null) {
            _createContentHandler(uri, localName, qName, attrs);
        }
        _contentHandler.startElement(uri, localName, qName, attrs);
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        _contentHandler.endElement(uri, localName, qName);
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        _contentHandler.characters(ch, start, length);
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#endDocument()
     */
    @Override
    public void endDocument() throws SAXException {
        _contentHandler.endDocument();
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#ignorableWhitespace(char[], int, int)
     */
    @Override
    public void ignorableWhitespace(char[] ch, int start, int length)
            throws SAXException {
        if (_contentHandler != null) {
            _contentHandler.ignorableWhitespace(ch, start, length);
        }
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#processingInstruction(java.lang.String, java.lang.String)
     */
    @Override
    public void processingInstruction(String target, String data)
            throws SAXException {
        if (_contentHandler != null) {
            _contentHandler.processingInstruction(target, data);
        }
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.xtm.IXTMContentHandler#setInputHandler(com.semagia.mio.IInputHandler)
     */
    @Override
    public void setMapHandler(final IMapHandler inputHandler) {
        _mapHandler = inputHandler;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.xtm.IXTMContentHandler#setDocumentIRI(java.lang.String)
     */
    @Override
    public void setDocumentIRI(final String iri) {
        _docIRI = iri;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.xtm.IXTMContentHandler#setSubordianate(boolean)
     */
    @Override
    public void setSubordianate(final boolean subordinate) {
        _isSubordinate = subordinate;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.xtm.IXTMContentHandler#getContext()
     */
    @Override
    public Context getContext() {
        return _context;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.xtm.IXTMContentHandler#setContext(com.semagia.mio.Context)
     */
    @Override
    public void setContext(final Context ctx) {
        _context = ctx;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.xtm.IXTMContentHandler#setProperty(java.lang.String, java.lang.Object)
     */
    @Override
    public void setProperty(final String iri, final Object value) {
        _properties.put(iri, value);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.xtm.IXTMContentHandler#getProperty(java.lang.String)
     */
    @Override
    public Object getProperty(final String iri) {
        return _properties.get(iri);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.xtm.IXTMContentHandler#getRelaxURI()
     */
    @Override
    public URL getRelaxURL() {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates the content handler to delegate to.
     *
     * @param uri The namespace URI.
     * @param localName The local name (without prefix), or the empty string if 
     *                      Namespace processing is not being performed.
     * @param qName The qualified name (with prefix), or the empty string if 
     *                  qualified names are not available.
     * @param attrs The attributes attached to the element. 
     *                  If there are no attributes, it shall be an empty Attributes object.
     * @throws SAXException If the underlying handler throws that exception or
     *                          if the handler cannot be detected.
     */
    private void _createContentHandler(final String uri, final String localName, 
            final String qName, final Attributes attrs) throws SAXException {
        IXTMContentHandler contentHandler = null;
        if (uri == XTM2ContentHandler.NS_XTM) {
            contentHandler = new XTM2ContentHandler();
        }
        else if (uri == XTM10ContentHandler.NS_XTM) {
            contentHandler = new XTM10ContentHandler();
        }
        else if (localName == XTM10ContentHandler.TOPIC_MAP) {
            final String version = attrs.getValue("", "version");
            contentHandler = version != null  ? new XTM2ContentHandler()
                                              : new XTM10ContentHandler();
        }
        else {
            contentHandler = new XTM10ContentHandler();
        }
        // Provide the missing info
        contentHandler.setMapHandler(_mapHandler);
        contentHandler.setContext(_context);
        contentHandler.setDocumentIRI(_docIRI);
        contentHandler.setSubordianate(_isSubordinate);
        for (String key: _properties.keySet()) {
            contentHandler.setProperty(key, _properties.get(key));
        }
        final boolean validate = !Boolean.FALSE.equals(_properties.get(Property.VALIDATE));
        _contentHandler = validate ? RelaxNGValidatingContentHandler.create(contentHandler, contentHandler.getRelaxURL()) 
                                   : contentHandler;
        // Provide the missing events
        _contentHandler.startDocument();
        for (String key: _prefixes.keySet()) {
            _contentHandler.startPrefixMapping(key, _prefixes.get(key));
        }
    }

}
