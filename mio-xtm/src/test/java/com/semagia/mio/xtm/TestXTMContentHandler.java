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

import org.xml.sax.helpers.AttributesImpl;

import com.semagia.mio.IMapHandler;
import com.semagia.mio.Property;
import com.semagia.mio.helpers.DefaultMapHandler;

import junit.framework.TestCase;

/**
 * Tests against the (universal) XTM content handler.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 462 $ - $Date: 2010-09-08 01:20:13 +0200 (Mi, 08 Sep 2010) $
 */
public class TestXTMContentHandler extends TestCase {

    private static final String _TOPIC_MAP = XTM10ContentHandler.TOPIC_MAP;
    private static final String _VERSION = "version";
    private static final String _XTM10 = XTM10ContentHandler.NS_XTM;
    private static final String _XTM2 = XTM2ContentHandler.NS_XTM;

    private static final String _DOC_IRI = "http://www.semagia.com/test-xtm/tm";

    public void testXTM10Detection() throws Exception {
        final XTMContentHandler handler = _getContentHandler();
        assertNull("Expected that the handler is null", handler.getContentHandler());
        handler.startElement(_XTM10, _TOPIC_MAP, "", new AttributesImpl());
        assertNotNull("Expected a non-null handler", handler.getContentHandler());
        assertTrue("Unexpected content handler impl", handler.getContentHandler() instanceof XTM10ContentHandler);
    }

    public void testXTM10Detection2() throws Exception {
        final XTMContentHandler handler = _getContentHandler();
        assertNull("Expected that the handler is null", handler.getContentHandler());
        handler.startElement("", "", _TOPIC_MAP, new AttributesImpl());
        assertNotNull("Expected a non-null handler", handler.getContentHandler());
        assertTrue("Unexpected content handler impl", handler.getContentHandler() instanceof XTM10ContentHandler);
    }

    public void testXTM10Fallback() throws Exception {
        final XTMContentHandler handler = _getContentHandler();
        assertNull("Expected that the handler is null", handler.getContentHandler());
        handler.startElement("", "", "something", new AttributesImpl());
        assertNotNull("Expected a non-null handler", handler.getContentHandler());
        assertTrue("Unexpected content handler impl", handler.getContentHandler() instanceof XTM10ContentHandler);
    }

    public void testXTM20Detection() throws Exception {
        final XTMContentHandler handler = _getContentHandler();
        assertNull("Expected that the handler is null", handler.getContentHandler());
        final AttributesImpl attrs = new AttributesImpl();
        attrs.addAttribute("", _VERSION, "", "CDATA", "2.0");
        handler.startElement(_XTM2, _TOPIC_MAP, null, attrs);
        assertNotNull("Expected a non-null handler", handler.getContentHandler());
        assertTrue("Unexpected content handler impl", handler.getContentHandler() instanceof XTM2ContentHandler);
    }

    public void testXTM20DetectionNoNamespace() throws Exception {
        final XTMContentHandler handler = _getContentHandler();
        assertNull("Expected that the handler is null", handler.getContentHandler());
        final AttributesImpl attrs = new AttributesImpl();
        attrs.addAttribute("", _VERSION, "", "CDATA", "2.0");
        handler.startElement("", _TOPIC_MAP, null, attrs);
        assertNotNull("Expected a non-null handler", handler.getContentHandler());
        assertTrue("Unexpected content handler impl", handler.getContentHandler() instanceof XTM2ContentHandler);
    }

    public void testXTM21Detection() throws Exception {
        final XTMContentHandler handler = _getContentHandler();
        assertNull("Expected that the handler is null", handler.getContentHandler());
        final AttributesImpl attrs = new AttributesImpl();
        attrs.addAttribute("", _VERSION, "", "CDATA", "2.1");
        handler.startElement(_XTM2, _TOPIC_MAP, null, attrs);
        assertNotNull("Expected a non-null handler", handler.getContentHandler());
        assertTrue("Unexpected content handler impl", handler.getContentHandler() instanceof XTM2ContentHandler);
    }

    public void testXTM21DetectionNoNamespace() throws Exception {
        final XTMContentHandler handler = _getContentHandler();
        assertNull("Expected that the handler is null", handler.getContentHandler());
        final AttributesImpl attrs = new AttributesImpl();
        attrs.addAttribute("", _VERSION, "", "CDATA", "2.1");
        handler.startElement("", _TOPIC_MAP, null, attrs);
        assertNotNull("Expected a non-null handler", handler.getContentHandler());
        assertTrue("Unexpected content handler impl", handler.getContentHandler() instanceof XTM2ContentHandler);
    }

    private final static XTMContentHandler _getContentHandler() {
        final XTMContentHandler handler = new XTMContentHandler();
        handler.setMapHandler(_getMapHandler());
        handler.setDocumentIRI(_DOC_IRI);
        // Disable validation, otherwise we'd get always a xml.ValidatingContentHandler instance
        handler.setProperty(Property.VALIDATE, Boolean.FALSE);
        return handler;
    }

    private final static IMapHandler _getMapHandler() {
        return new DefaultMapHandler();
    }
}
