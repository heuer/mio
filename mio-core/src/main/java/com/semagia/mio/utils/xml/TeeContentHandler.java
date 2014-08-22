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
package com.semagia.mio.utils.xml;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * A {@link ContentHandler} implementation which directs events to two 
 * underlying {@link ContentHandler}s.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 607 $ - $Date: 2011-01-20 02:28:15 +0100 (Do, 20 Jan 2011) $
 */
public final class TeeContentHandler implements ContentHandler {

    private final ContentHandler _first;
    private final ContentHandler _second;

    /**
     * Constructs a new instance.
     *
     * @param first The content handler which receives the events firstly.
     * @param second The content handler which receives the events secondly.
     */
    public TeeContentHandler(final ContentHandler first, final ContentHandler second) {
        _first = first;
        _second = second;
    }

    public ContentHandler getFirst() {
        return _first;
    }

    public ContentHandler getSecond() {
        return _second;
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#characters(char[], int, int)
     */
    @Override
    public void characters(final char[] ch, final int start, final int length)
            throws SAXException {
        _first.characters(ch, start, length);
        _second.characters(ch, start, length);
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#endDocument()
     */
    @Override
    public void endDocument() throws SAXException {
        _first.endDocument();
        _second.endDocument();
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(final String uri, final String localName, final String name)
            throws SAXException {
        _first.endElement(uri, localName, name);
        _second.endElement(uri, localName, name);
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
     */
    @Override
    public void endPrefixMapping(final String prefix) throws SAXException {
        _first.endPrefixMapping(prefix);
        _second.endPrefixMapping(prefix);
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
     */
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length)
            throws SAXException {
        _first.ignorableWhitespace(ch, start, length);
        _second.ignorableWhitespace(ch, start, length);
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String, java.lang.String)
     */
    @Override
    public void processingInstruction(final String target, final String data)
            throws SAXException {
        _first.processingInstruction(target, data);
        _second.processingInstruction(target, data);
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
     */
    @Override
    public void setDocumentLocator(final Locator locator) {
        _first.setDocumentLocator(locator);
        _second.setDocumentLocator(locator);
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
     */
    @Override
    public void skippedEntity(final String name) throws SAXException {
        _first.skippedEntity(name);
        _second.skippedEntity(name);
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#startDocument()
     */
    @Override
    public void startDocument() throws SAXException {
        _first.startDocument();
        _second.startDocument();
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(final String uri, final String localName, 
            final String name,
            Attributes atts) throws SAXException {
        _first.startElement(uri, localName, name, atts);
        _second.startElement(uri, localName, name, atts);
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String, java.lang.String)
     */
    @Override
    public void startPrefixMapping(final String prefix, final String uri)
            throws SAXException {
        _first.startPrefixMapping(prefix, uri);
        _second.startPrefixMapping(prefix, uri);
    }

}
