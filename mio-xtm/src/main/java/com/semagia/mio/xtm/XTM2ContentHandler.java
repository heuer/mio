/*
 * Copyright 2007 - 2014 Lars Heuer (heuer[at]semagia.com)
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.semagia.mio.IRef;
import com.semagia.mio.MIOException;
import com.semagia.mio.Property;
import com.semagia.mio.Source;
import com.semagia.mio.helpers.Ref;
import com.semagia.mio.utils.xml.SAXXMLWriter;
import com.semagia.mio.voc.TMDM;
import com.semagia.mio.voc.XSD;

import static com.semagia.mio.xtm.XTM10ContentHandler.TOPIC_MAP;
import static com.semagia.mio.xtm.XTM10ContentHandler.TOPIC;
import static com.semagia.mio.xtm.XTM10ContentHandler.ASSOCIATION;
import static com.semagia.mio.xtm.XTM10ContentHandler.OCCURRENCE;
import static com.semagia.mio.xtm.XTM10ContentHandler.VARIANT;
import static com.semagia.mio.xtm.XTM10ContentHandler.MERGE_MAP;
import static com.semagia.mio.xtm.XTM10ContentHandler.SCOPE;
import static com.semagia.mio.xtm.XTM10ContentHandler.TOPIC_REF;
import static com.semagia.mio.xtm.XTM10ContentHandler.INSTANCE_OF;
import static com.semagia.mio.xtm.XTM10ContentHandler.RESOURCE_DATA;
import static com.semagia.mio.xtm.XTM10ContentHandler.RESOURCE_REF;

/**
 * Content handler for <a href="http://www.isotopicmaps.org/sam/sam-xtm/">XTM 2.0</a>
 * and <a href="http://www.itscj.ipsj.or.jp/sc34/open/1378.htm">XTM 2.1</a> topic maps.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
final class XTM2ContentHandler extends AbstractXTMContentHandler {

    /**
     * XML Topic Maps namespace.
     */
    static final String NS_XTM = "http://www.topicmaps.org/xtm/";

    /**
     * Constant for the default topic name type.
     */
    private static final IRef _TOPIC_NAME = Ref.createSubjectIdentifier(TMDM.TOPIC_NAME);

    /*
     * Constants for XML elements.
     */
    private static final String 
        ROLE = "role",
        NAME = "name",
        TYPE = "type",
        VALUE = "value",
        SUBJECT_IDENTIFIER = "subjectIdentifier",
        SUBJECT_LOCATOR = "subjectLocator",
        ITEM_IDENTITY = "itemIdentity",
        // XTM 2.1 specific elements
        REIFIER = "reifier",
        SID_REF = "subjectIdentifierRef",
        SLO_REF = "subjectLocatorRef"
        ;

    private static final byte
        _STATE_INITIAL = 1,
        _STATE_TOPIC = 2,
        _STATE_ASSOCIATION = 3,
        _STATE_ROLE = 4,
        _STATE_TYPE = 5,
        _STATE_INSTANCE_OF = 6,
        _STATE_SCOPE = 7,
        _STATE_OCCURRENCE = 8,
        _STATE_NAME = 9,
        _STATE_VARIANT = 10,
        _STATE_REIFIER = 11 // XTM 2.1
        ;

    private boolean _acceptContent;
    private boolean _acceptXML;
    private String _datatype;
    private SAXXMLWriter _xmlHandler;
    private boolean _seenType;
    private byte _state;
    private byte _nextState;
    private ByteArrayOutputStream _xmlContent;
    private boolean _xtm20Mode;
    private boolean _seenIdentity;
    private boolean _ignoreMergeMap;
    private boolean _seenReifier;

    XTM2ContentHandler() {
        super();
    }

    String getVersion() {
        return _xtm20Mode ? "2.0" : "2.1";
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.xtm.IXTMContentHandler#getRelaxURL()
     */
    @Override
    public URL getRelaxURL() {
        return getClass().getResource("/xtm21.rnc");
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#startDocument()
     */
    @Override
    public void startDocument() throws SAXException {
        _xmlContent = new ByteArrayOutputStream();
        _xmlHandler = new SAXXMLWriter(_xmlContent);
        _ignoreMergeMap =  Boolean.TRUE.equals(_properties.get(Property.IGNORE_MERGEMAP));
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (_acceptXML) {
            _xmlHandler.characters(ch, start, length);
        }
        else if (_acceptContent) {
            _content.append(ch, start, length);
        }
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(String uri, String name, String qName, Attributes attrs) throws SAXException {
        if (_acceptXML) {
            _xmlHandler.startElement(uri, name, qName, attrs);
            return;
        }
        if (TOPIC_REF == name) {
            String ref = _href(attrs);
            if (_xtm20Mode && ref.indexOf('#') == -1) {
                _reportError("Invalid topic reference '" + ref + "'. Does not contain a fragment identifier");
            }
            _processTopicReference(Ref.createItemIdentifier(ref));
        }
        else if (SID_REF == name) {
            if (_xtm20Mode) {
                _reportError("The <subjectIdentifierRef/> element is disallowed in XTM 2.0");
            }
            _processTopicReference(Ref.createSubjectIdentifier(_href(attrs)));
        }
        else if (SLO_REF == name) {
            if (_xtm20Mode) {
                _reportError("The <subjectLocatorRef/> element is disallowed in XTM 2.0");
            }
            _processTopicReference(Ref.createSubjectLocator(_href(attrs)));
        }
        else if (TOPIC == name) {
            final String id = attrs.getValue("", "id");
            _seenIdentity = id != null;
            if (_seenIdentity) {
                _handler.startTopic(Ref.createItemIdentifier(_createLocator("#" + attrs.getValue("", "id"))));
            }
            _state = _STATE_TOPIC;
        }
        else if (INSTANCE_OF == name) {
            if (_state != _STATE_TOPIC) {
                _reportError("Unexpected 'instanceOf' element");
            }
            _state = _STATE_INSTANCE_OF;
        }
        else if (TYPE == name) {
            _nextState = _state;
            _state = _STATE_TYPE;
        }
        else if (SUBJECT_IDENTIFIER == name) {
            if (!_seenIdentity) {
                _handler.startTopic(Ref.createSubjectIdentifier(_href(attrs)));
                _seenIdentity = true;
            }
            _handler.subjectIdentifier(_href(attrs));
        }
        else if (SUBJECT_LOCATOR == name) {
            if (!_seenIdentity) {
                _handler.startTopic(Ref.createSubjectLocator(_href(attrs)));
                _seenIdentity = true;
            }
            _handler.subjectLocator(_href(attrs));
        }
        else if (ITEM_IDENTITY == name) {
            if (_state == _STATE_TOPIC && !_seenIdentity) {
                _handler.startTopic(Ref.createItemIdentifier(_href(attrs)));
                _seenIdentity = true;
            }
            _handler.itemIdentifier(_href(attrs));
        }
        else if (ASSOCIATION == name) {
            _handler.startAssociation();
            _reifier(attrs);
            _state = _STATE_ASSOCIATION;
        }
        else if (ROLE == name) {
            _handler.startRole();
            _reifier(attrs);
            _state = _STATE_ROLE;
        }
        else if (VALUE == name) {
            _content.setLength(0);
            _acceptContent = true;
        }
        else if (RESOURCE_DATA == name) {
            final String datatype = attrs.getValue("", "datatype");
            _datatype = datatype == null ? XSD.STRING : datatype;
            _content.setLength(0);
            _acceptContent = true;
            _acceptXML = XSD.ANY_TYPE.equals(datatype);
        }
        else if (RESOURCE_REF == name) {
            _handler.value(_href(attrs), XSD.ANY_URI);
        }
        else if (OCCURRENCE == name) {
            _handler.startOccurrence();
            _reifier(attrs);
            _state = _STATE_OCCURRENCE;
        }
        else if (NAME == name) {
            _handler.startName();
            _seenType = false;
            _reifier(attrs);
            _state = _STATE_NAME;
        }
        else if (VARIANT == name) {
            _handler.startVariant();
            _reifier(attrs);
            _state = _STATE_VARIANT;
        }
        else if (SCOPE == name) {
            _handler.startScope();
            _nextState = _state;
            _state = _STATE_SCOPE;
        }
        else if (REIFIER == name) {
            if (_xtm20Mode) {
                throw new MIOException("The <reifier/> element is disallowed in XTM 2.0");
            }
            if (_seenReifier) {
                throw new MIOException("Found a reifier attribute and reifier element");
            }
            _nextState = _state;
            _state = _STATE_REIFIER;
        }
        else if (MERGE_MAP == name) {
            if (!_ignoreMergeMap) {
                final String iri = _docLocator.resolve(attrs.getValue("", "href")).toExternalForm();
                if (!_context.containsIRI(iri)) {
                    _processMergeMap(iri);
                }
            }
        }
        else if (TOPIC_MAP == name) {
            final String version = attrs.getValue("", "version");
            if (version == null) {
                _reportError("Missing version attribute");
            }
            if (!"2.0".equals(version) && !"2.1".equals(version)) {
                _reportError("Expected version '2.0' or '2.1', got: " + version);
            }
            _xtm20Mode = "2.0".equals(version);
            final String reifier = attrs.getValue("", "reifier");
            if (reifier != null) {
                _processTopicMapReifier(Ref.createItemIdentifier(_createLocator(reifier)));
                _seenReifier = true;
            }
            _state = _STATE_INITIAL;
        }
        else {
            _reportError("Unexpected element (URI: '" + uri + "', qName: '" + qName + "', local name: '" + name + "')");
        }
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(String uri, String name, String qName)
            throws SAXException {
        if (_acceptXML && !(RESOURCE_DATA == name || RESOURCE_REF == name)) {
            _xmlHandler.endElement(uri, name, qName);
        }
        else if (TOPIC == name) {
            _state = _STATE_INITIAL;
        }
        else if (TOPIC_MAP == name
                    || TOPIC_REF == name
                    || RESOURCE_REF == name
                    || MERGE_MAP == name) {
            // noop.
        }
        else if (SCOPE == name) {
            _handler.endScope();
            _state = _nextState;
        }
        else if (TYPE == name || REIFIER == name) {
            _state = _nextState;
        }
        else if (INSTANCE_OF == name) {
            _state = _STATE_TOPIC;
        }
        else if (ASSOCIATION == name) {
            _handler.endAssociation();
            _state = _STATE_INITIAL;
        }
        else if (ROLE == name) {
            _handler.endRole();
            _state = _STATE_ASSOCIATION;
        }
        else if (OCCURRENCE == name) {
            _handler.endOccurrence();
            _state = _STATE_TOPIC;
        }
        else if (NAME == name) {
            if (!_seenType) {
                _handler.type(_TOPIC_NAME);
            }
            _handler.endName();
            _state = _STATE_TOPIC;
        }
        else if (VARIANT == name) {
            _handler.endVariant();
            _state = _STATE_NAME;
        }
        else if (RESOURCE_DATA == name) {
            if (_acceptXML) {
                try {
                    _xmlHandler.flush();
                }
                catch (IOException ex) {
                    _reportError(ex);
                }
                _handler.value(_xmlContent.toString(), XSD.ANY_TYPE);
                _xmlContent.reset();
            }
            else {
                if (XSD.ANY_URI.equals(_datatype)) {
                    _handler.value(_createLocator(_content.toString()), XSD.ANY_URI);
                }
                else {
                    _handler.value(_content.toString(), _datatype);
                }
            }
            _acceptContent = false;
            _acceptXML = false;
        }
        else if (VALUE == name) {
            _handler.value(_content.toString());
            _acceptContent = false;
        }
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#startPrefixMapping(java.lang.String, java.lang.String)
     */
    @Override
    public void startPrefixMapping(String prefix, String uri)
            throws SAXException {
        if (_acceptXML) {
            _xmlHandler.startPrefixMapping(prefix, uri);
        }
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#endPrefixMapping(java.lang.String)
     */
    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        if (_acceptXML) {
            _xmlHandler.endPrefixMapping(prefix);
        }
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#ignorableWhitespace(char[], int, int)
     */
    @Override
    public void ignorableWhitespace(char[] ch, int start, int length)
            throws SAXException {
        if (_acceptXML) {
            _xmlHandler.ignorableWhitespace(ch, start, length);
        }
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#processingInstruction(java.lang.String, java.lang.String)
     */
    @Override
    public void processingInstruction(String target, String data)
            throws SAXException {
        if (_acceptXML) {
            _xmlHandler.processingInstruction(target, data);
        }
    }

    private void _processMergeMap(final String iri) throws MIOException {
        _context.addIRI(iri);
        final XTMDeserializer deser = new XTMDeserializer();
        deser.setContext(_context);
        deser.setMapHandler(_handler);
        deser.setSubordinate(true);
        for (String key: _properties.keySet()) {
            deser.setProperty(key, _properties.get(key));
        }
        try {
            deser.parse(new Source(iri));
        }
        catch (IOException ex) {
            throw new MIOException(ex);
        }
    }

    /**
     * Assigns a reifying topic to the underlying topic map iff this handler 
     * is not in the "subordinate" mode.
     *
     * @param reifier
     * @throws MIOException
     */
    private void _processTopicMapReifier(final IRef reifier) throws MIOException {
        if (!_isSubordinate) {
            _handler.reifier(reifier);
        }
        else {
            _handler.startTopic(reifier);
            _handler.endTopic();
        }
    }

    /**
     * Reads the "reifier" attribute and reports the reifier iff it is not 
     * <tt>null</tt>.
     *
     * @param attrs
     * @throws MIOException 
     */
    private void _reifier(final Attributes attrs) throws MIOException {
        final String reifier = attrs.getValue("", "reifier");
        _seenReifier = reifier != null;
        if (_seenReifier) { 
            _handler.reifier((Ref.createItemIdentifier(_createLocator(reifier))));
        }
    }

    /**
     * Reads the "href" attribute and returns a locator which is resolved against
     * the document locator.
     *
     * @param attrs The attributes to read the "href" from.
     * @return An IRI.
     */
    private String _href(final Attributes attrs) {
        return _createLocator(attrs.getValue("", "href"));
    }

    /**
     * Returns a locator which is resolved against the document locator.
     *
     * @param ref The reference to resolve against the document locator.
     * @return A locator.
     */
    private String _createLocator(final String ref) {
        return _docLocator.resolve(ref).getReference();
    }

    /**
     * Processes a topicRef / subjectIdentifierRef / subjectLocatorRef element.
     *
     * @param topic A reference to a topic.
     * @throws MIOException In case of an error.
     */
    private void _processTopicReference(final IRef topic) throws MIOException {
        switch (_state) {
            case _STATE_INSTANCE_OF: {
                _handler.isa(topic);
                break;
            }
            case _STATE_ROLE: 
                _handler.player(topic);
                break;
            case _STATE_SCOPE:
                _handler.theme(topic);
                break;
            case _STATE_TYPE: {
                _seenType = true;
                _handler.type(topic);
                break;
                }
            case _STATE_REIFIER: {
                if (_nextState == _STATE_INITIAL) {
                    _processTopicMapReifier(topic);
                }
                else {
                    _handler.reifier(topic);
                }
                _seenReifier = true;
                break;
            }
            default:
                _reportError("Unexpected topic reference: " + topic.toString());
        }
    }

}
