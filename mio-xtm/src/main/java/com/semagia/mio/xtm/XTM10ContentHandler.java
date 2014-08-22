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
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.semagia.mio.IRef;
import com.semagia.mio.MIOException;
import com.semagia.mio.Property;
import com.semagia.mio.helpers.Locator;
import com.semagia.mio.helpers.Ref;
import com.semagia.mio.voc.TMDM;
import com.semagia.mio.voc.XSD;
import com.semagia.mio.voc.XTM10;

/**
 * Content handler for <a href="http://www.topicmaps.org/xtm/1.0/">XTM 1.0</a> 
 * topic maps.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 462 $ - $Date: 2010-09-08 01:20:13 +0200 (Mi, 08 Sep 2010) $
 */
final class XTM10ContentHandler extends AbstractXTMContentHandler {

    /**
     * Default instance of a merge map which does nothing.
     */
    private static final IMergeMap _NOOP_MERGEMAP = new NoopMergeMap();

    /**
     * The mergemap scope.
     */
    private Set<IRef> _mergeMapScope;

    /**
     * Holds the processed elements.
     */
    private Stack<String> _parentElements;

    /**
     * Stack of base locators.
     * The xml:base attribute is allowed everywhere. This stack
     * is used to keep track about the base locator that should
     * be used to resolve references against. 
     */
    private Stack<Locator> _baseLocs;

    /**
     * Keeps track of nested variants.
     */
    private Stack<Variant> _variants;

    /**
     * Inidicates input characters should be kept.
     */
    private boolean _acceptContent;

    private IMergeMap _mergeMap;

    
    /*
     * Namespace constants
     */

    /**
     * XML namespace.
     */
    private static final String _NS_XML = "http://www.w3.org/XML/1998/namespace";

    /**
     * XML Topic Maps 1.0 namespace.
     */
    static final String NS_XTM = "http://www.topicmaps.org/xtm/1.0/";

    /**
     * XLink namespace. 
     */
    private static final String NS_XLINK = "http://www.w3.org/1999/xlink";

    /*
     * Constants for the XML elements.
     */
    static final String 
        MERGE_MAP = "mergeMap",
        TOPIC_MAP = "topicMap",
        TOPIC = "topic",
        ASSOCIATION = "association",
        MEMBER = "member",
        OCCURRENCE = "occurrence",
        BASENAME = "baseName",
        VARIANT = "variant",

        INSTANCE_OF = "instanceOf",
        ROLE_SPEC = "roleSpec",

        TOPIC_REF = "topicRef",

        BASENAME_STRING = "baseNameString",
        VARIANT_NAME = "variantName",
        RESOURCE_REF = "resourceRef",
        RESOURCE_DATA = "resourceData",

        SCOPE = "scope",
        PARAMETERS = "parameters",

        SUBJECT_IDENTITY = "subjectIdentity",
        SUBJECT_INDICATOR_REF = "subjectIndicatorRef"
        ;

    private static final IRef 
        _ASSOCIATION = Ref.createSubjectIdentifier(XTM10.ASSOCIATION),
        _TOPIC_NAME = Ref.createSubjectIdentifier(TMDM.TOPIC_NAME),
        _ROLE = Ref.createSubjectIdentifier(XTM10.ROLE),
        _OCCURRENCE = Ref.createSubjectIdentifier(XTM10.OCCURRENCE)
        ;

    private boolean _seenType;
    private boolean _seenScope;
    private IRef _roleType;

    /**
     * Indicates that the <tt>mergeMap</tt> element is not processed.
     */
    private boolean _ignoreMergeMap;

    /**
     * Logger.
     */
    private static Logger LOG = LoggerFactory.getLogger(XTM10ContentHandler.class);

    /**
     * 
     *
     */
    public XTM10ContentHandler() {
        super();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.xtm.IXTMContentHandler#getRelaxURL()
     */
    @Override
    public URL getRelaxURL() {
        return getClass().getResource("/xtm1.rnc");
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#startDocument()
     */
    @Override
    public void startDocument() throws SAXException {
        _ignoreMergeMap =  Boolean.TRUE.equals(_properties.get(Property.IGNORE_MERGEMAP));
        _parentElements = new Stack<String>();
        _variants = new Stack<Variant>();
        _baseLocs = new Stack<Locator>();
        _baseLocs.push(_docLocator);
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#endDocument()
     */
    @Override
    public void endDocument() throws SAXException {
        assert _parentElements.isEmpty();
        _parentElements = null;
        _variants = null;
        _baseLocs = null;
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
        if (uri != NS_XTM) {
            if (!uri.equals("")) {
                return;
            }
        }
        else {
            qName = localName;
        }
        String base = attrs.getValue(_NS_XML, "base");
        if (base != null) {
            _baseLocs.push(_baseLocs.peek().resolve(base));
        }
        else {
            _baseLocs.push(_baseLocs.peek());
        }
        if (TOPIC_REF == qName) {
            _processTopicReference(_createLocator(_href(attrs)));
        }
        else if (INSTANCE_OF == qName) {
            _parentElements.push(INSTANCE_OF);
        }
        else if (TOPIC == qName) {
            _handler.startTopic(Ref.createItemIdentifier(_createLocator('#' + attrs.getValue("", "id"))));
            _parentElements.push(TOPIC);
        }
        else if (SUBJECT_IDENTITY == qName) {
            _parentElements.push(SUBJECT_IDENTITY);
        }
        else if (SUBJECT_INDICATOR_REF == qName) {
            _processSubjectIdentifier(_href(attrs));
        }
        else if (RESOURCE_REF == qName) {
            _processResourceRef(_href(attrs));
        }
        else if (ASSOCIATION == qName) {
            _handler.startAssociation();
            _itemIdentifier(attrs);
            _parentElements.push(ASSOCIATION);
            _seenType = false;
            _seenScope = false;
        }
        else if (MEMBER == qName) {
            _roleType = null;
            _parentElements.push(MEMBER);
        }
        else if (ROLE_SPEC == qName) {
            _parentElements.push(ROLE_SPEC);
        }
        else if (OCCURRENCE == qName) {
            _handler.startOccurrence();
            _itemIdentifier(attrs);
            _parentElements.push(OCCURRENCE);
            _seenType = false;
            _seenScope = false;
        }
        else if (BASENAME == qName) {
            _handler.startName();
            _itemIdentifier(attrs);
            _parentElements.push(BASENAME);
            _seenType = false;
            _seenScope = false;
        }
        else if (BASENAME_STRING == qName 
                    || RESOURCE_DATA == qName) {
            _content.setLength(0);
            _acceptContent = true;
        }
        else if (VARIANT == qName) {
            String id = attrs.getValue("", "id");
            Variant var = id != null ? new Variant(_createLocator("#"+id)) : new Variant();
            // Inherit the scope of the variant parents
            for (int i=0; i<_variants.size(); i++) {
                var.scope.addAll(_variants.get(i).scope);
            }
            _variants.push(var);
        }
        else if (VARIANT_NAME == qName) {
            _parentElements.push(VARIANT_NAME);
        }
        else if (SCOPE == qName) {
            _parentElements.push(SCOPE);
            _handler.startScope();
            _seenScope = true;
            _processMergeMapThemes();
        }
        else if (PARAMETERS == qName) {
            _parentElements.push(PARAMETERS);
        }
        else if (TOPIC_MAP == qName) {
            _parentElements.push(TOPIC_MAP);
            _itemIdentifier(attrs);
        }
        else if (MERGE_MAP == qName) {
             _mergeMap = _ignoreMergeMap ? _NOOP_MERGEMAP 
                                         : new MergeMap(Locator.create(_createLocator(_href(attrs))));
            _parentElements.push(MERGE_MAP);
        }
        else {
            LOG.info("Unknown element '" + qName + "'");
        }
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (uri != NS_XTM) {
            if (!uri.equals("")) {
                return;
            }
        }
        else {
            qName = localName;
        }
        if (TOPIC == qName) {
            _parentElements.pop();
            _handler.endTopic();
        }
        else if (ASSOCIATION == qName) {
            _parentElements.pop();
            if (!_seenType) {
                _handler.type(_ASSOCIATION);
            }
            _processMergeMapScope();
            _handler.endAssociation();
        }
        else if (OCCURRENCE == qName) {
            _parentElements.pop();
            if (!_seenType) {
                _handler.type(_OCCURRENCE);
            }
            _processMergeMapScope();
            _handler.endOccurrence();
        }
        else if (BASENAME == qName) {
            _parentElements.pop();
            if (!_seenType) {
                _handler.type(_TOPIC_NAME);
            }
            _processMergeMapScope();
            _handler.endName();
        }
        else if (BASENAME_STRING == qName) {
            _handler.value(_content.toString());
            _acceptContent = false;
        }
        else if (RESOURCE_DATA == qName) {
            _acceptContent = false;
            _processResourceData(_content.toString());
        }
        else if (VARIANT == qName) {
            _processVariant(_variants.pop());
        }
        else if (MERGE_MAP == qName) {
            Locator loc = _mergeMap.getLocator();
            if (!_context.containsIRI(loc.getReference())) {
                _mergeMap.execute();
            }
            _parentElements.pop();
        }
        else if (MEMBER == qName) {
            _parentElements.pop();
        }
        else if (SCOPE == qName) {
            _parentElements.pop();
            _handler.endScope();
        }
        else if (INSTANCE_OF == qName
                || SUBJECT_IDENTITY == qName
                || TOPIC_MAP == qName
                || VARIANT_NAME == qName
                || PARAMETERS == qName
                || ROLE_SPEC == qName) {
            _parentElements.pop();
        }
        else if (TOPIC_REF == qName
                || RESOURCE_REF == qName
                || SUBJECT_INDICATOR_REF == qName) {
            // noop.
        }
        else {
            LOG.info("Unknown element '" + qName + "'");
        }
        _baseLocs.pop();
    }

    private void _processMergeMapScope() throws MIOException {
        if (_seenScope || _mergeMapScope == null) {
            return;
        }
        _handler.startScope();
        _processMergeMapThemes();
        _handler.endScope();
    }

    private void _processMergeMapThemes() throws MIOException {
        if (_mergeMapScope == null) {
            return;
        }
        for (IRef theme: _mergeMapScope) {
            _handler.theme(theme);
        }
    }

    private void _processVariant(final Variant variant) throws MIOException {
        _handler.startVariant();
        _handler.startScope();
        for (IRef theme: variant.scope) {
            _handler.theme(theme);
        }
        _processMergeMapThemes();
        _handler.endScope();
        if (variant.itemIdentifier != null) {
            _handler.itemIdentifier(variant.itemIdentifier);
        }
        _handler.value(variant.value, variant.datatype);
        _handler.endVariant();
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    @Override
    public void characters(char[] chars, int start, int length) throws SAXException {
        if (_acceptContent) {
            _content.append(chars, start, length);
        }
    }

    void setMergeMapScope(final Set<IRef> scope) {
        _mergeMapScope = scope;
    }

    Set<IRef> getMergeMapScope() {
        return _mergeMapScope;
    }

    /**
     * Returns the value of the "xlink:href" attribute.
     *
     * @param attrs Attributes to read the value from.
     * @return The string value of the "href" attribute or <code>null</code>
     *          if the "href" attribute is not set.
     * @throws MIOException 
     */
    private String _href(final Attributes attrs) throws MIOException {
        String reference = attrs.getValue(NS_XLINK, "href");
        if (reference == null) {
            reference = attrs.getValue("xlink:href");
        }
        if (reference == null) {
            throw new MIOException("Invalid 'href' reference. It is null");
        }
        return reference;
    }

    /**
     * 
     *
     * @param reference
     * @return
     */
    private String _createLocator(final String reference) {
        if (reference.length() == 0) {
            return _baseLocs.peek().getReference();
        }
        return _baseLocs.peek().resolve(reference).getReference();
    }

    /**
     * Extracts the <code>id</code> from the <code>attrs</code> and
     * adds it to the item identifiers (if it is not <code>null</code>).
     *
     * @param attrs The XML attributes.
     * @throws MIOException 
     */
    private void _itemIdentifier(final Attributes attrs) throws MIOException {
        final String id = attrs.getValue("", "id");
        if (id == null) {
            return;
        }
        _handler.itemIdentifier(_createLocator("#" + id));
    }

    /**
     * 
     *
     * @param reference
     * @throws MIOException 
     */
    private void _processSubjectIdentifier(final String reference) throws MIOException {
        final String sid = _createLocator(reference);
        final String parent = _parentElements.peek();
        if (parent == SUBJECT_IDENTITY) {
            _handler.subjectIdentifier(sid);
        }
        else {
            _processTopicReference(parent, Ref.createSubjectIdentifier(sid));
        }
    }

    /**
     * Processes the locator (created by a topicRef element) in the current 
     * context.
     * <p>
     * If the topicRef occurrs within a <code>subjectIdentity</code> element,
     * the locator is added to the <code>[subject locators]</code> property of
     * the currently processed topic, otherwise the locator is used to
     * reference a topic by its item identifier.
     * </p>
     *
     * @param locator
     * @throws MIOException 
     */
    private void _processTopicReference(final String locator) throws MIOException {
        final String parentEl = _parentElements.peek();
        if (parentEl == SUBJECT_IDENTITY) {
            _handler.itemIdentifier(locator);
        }
        else {
            _processTopicReference(parentEl, Ref.createItemIdentifier(locator));
        }
    }

    /**
     * Processes the topic (created by a topicRef element) in the current 
     * context.
     * 
     * The <code>topicRef</code> may occur inside a <code>instanceOf</code>, 
     * <code>member</code>, <code>mergeMap</code>, <code>parameters</code>, 
     * <code>roleSpec</code>, <code>scope</code>, <code>subjectIdentity</code>.
     * This method decides what to do with the topic inside the mentioned 
     * contexts (<code>subjectIdentity</code> is handled by 
     * {@linkplain #_processTopicReference(Object)}.
     * 
     * @param parentEl The parent element.
     * @param topic The topic to process.
     * @throws MIOException 
     */
    private void _processTopicReference(final String parentEl, final IRef ref) throws MIOException {
        if (parentEl == INSTANCE_OF) {
            if (_parentElements.contains(ASSOCIATION) 
                    || _parentElements.contains(OCCURRENCE)
                    // instanceOf in name is invalid but Ontopia uses it
                    || _parentElements.contains(BASENAME)) {
                _handler.type(ref);
                _seenType = true;
            }
            else if (_parentElements.contains(TOPIC)) {
                _handler.isa(ref);
            }
            else {
                _reportError("Unexpected 'instanceOf' element");
            }
        }
        else if (parentEl == MEMBER) {
            _processRole(ref);
        }
        else if (parentEl == SCOPE) { 
            _processTheme(ref);
        }
        else if (parentEl == PARAMETERS) {
            _variants.peek().scope.add(ref);
        }
        else if (parentEl == ROLE_SPEC) {
            _roleType = ref;
        }
        else if (parentEl == MERGE_MAP) {
            _mergeMap.addTheme(ref);
        }
        else {
            _reportError("Unexpected parent element of a topicRef element '" + parentEl + "'");
        }
    }

    /**
     * Creates an association role from a player.
     *
     * @param player A topic instance or {@code null} if the role has no 
     *                  player.
     * @throws MIOException 
     */
    private void _processRole(final IRef player) throws MIOException {
        IRef type = _roleType;
        if (type == null) {
            type = _ROLE;
            _roleType = type;
        }
        _handler.startRole(type);
        _handler.player(player);
        _handler.endRole();
    }

    /**
     * Sets the specified value as value of an occurrence or variant.
     *
     * @param value The value to set.
     * @throws MIOException 
     */
    private void _processResourceData(final String value) throws MIOException {
        final String parentEl = _parentElements.peek();
        if (parentEl == OCCURRENCE) {
            _handler.value(value, XSD.STRING);
        }
        else if (parentEl == VARIANT_NAME) {
            _variants.peek().value = value;
        }
        else {
            _reportError("Unexpected parent element '" + parentEl + "' while processing 'resourceData'");
        }
    }

    /**
     * Processes the URI of a resourceRef element.
     * 
     * The reference is either set as value of an occurrence or
     * variant or is resolved to a topic's subject locator.
     * This method decides context-sensitive how to process the IRI. 
     *
     * @param reference An IRI.
     * @throws MIOException 
     */
    private void _processResourceRef(final String reference) throws MIOException {
        final String parentEl = _parentElements.peek();
        final String iri = _createLocator(reference);
        if (parentEl == SUBJECT_IDENTITY) {
            _handler.subjectLocator(iri);
        }
        else if (parentEl == MEMBER) {
            _processRole(Ref.createSubjectLocator(iri));
        }
        else if (parentEl == OCCURRENCE) {
            _handler.value(iri, XSD.ANY_URI);
        }
        else if (parentEl == VARIANT_NAME) {
            Variant var = _variants.peek();
            var.value = iri;
            var.datatype = XSD.ANY_URI;
        }
        else if (parentEl == SCOPE) {
            _processTheme(Ref.createSubjectLocator(iri));
        }
        else if (parentEl == MERGE_MAP) {
            _mergeMap.addTheme(Ref.createSubjectLocator(iri));
        }
        else {
            _reportError("Unexpected parent element '" + parentEl + "' while processing resourceRef");
        }
    }

    /**
     * Adds the specified topic to the scope of the last processed element.
     *
     * @param theme The topic to add.
     * @throws MIOException 
     */
    private void _processTheme(final IRef theme) throws MIOException {
        if (_parentElements.contains(ASSOCIATION)
                || _parentElements.contains(OCCURRENCE)
                || _parentElements.contains(BASENAME)) {
            _handler.theme(theme);
        }
        else {
            _reportError("Unexpected parent element while processing scope.");
        }
    }

    /**
     * Representation of a variant.
     * Since XTM 1.0 variants can be stacked and the scope is inherited from
     * the parent variant name, the events to create a variant are not 
     * issued immediately.
     */
    private static class Variant {
        String datatype;
        String value;
        final String itemIdentifier;
        final List<IRef> scope;

        public Variant() {
            this(null);
        }

        public Variant(String itemIdentifier) {
            this.itemIdentifier = itemIdentifier;
            this.scope = new ArrayList<IRef>();
            this.datatype = XSD.STRING;
        }
    }

    /**
     * Representation a mergeMap directive.
     */
    private static interface IMergeMap {

        /**
         * Returns the location of the referenced topic map.
         *
         * @return The IRI of the topic map.
         */
        Locator getLocator();

        /**
         * Adds a theme to the mergemap scope.
         *
         * @param topic A theme.
         */
        void addTheme(IRef topic);

        /**
         * Reads the referenced topic map.
         *
         * @throws SAXException In case of an error.
         */
        void execute() throws SAXException;
    }

    /**
     * Merge map that does nothing.
     */
    private static final class NoopMergeMap implements IMergeMap {

        private static final Locator _LOC = Locator.create(Property.IGNORE_MERGEMAP);

        @Override
        public Locator getLocator() {
            return _LOC;
        }

        @Override
        public void addTheme(final IRef topic) {
            // noop.
        }

        @Override
        public void execute() throws SAXException {
            // noop.
        }
    }

    /**
     * Default implementation of the mergeMap directive.
     */
    private final class MergeMap implements IMergeMap {

        private final Locator _locator;
        private final Set<IRef> _scope;

        MergeMap(Locator locator) {
            _locator = locator;
            _scope = new HashSet<IRef>();
        }

        @Override
        public Locator getLocator() {
            return _locator;
        }

        @Override
        public void addTheme(final IRef topic) {
            _scope.add(topic);
        }

        @Override
        public void execute() throws SAXException {
            final XMLReader xmlReader = XTMDeserializer._createXMLReader();
            final XTM10ContentHandler contentHandler = new XTM10ContentHandler();
            contentHandler.setDocumentIRI(_locator.toExternalForm());
            contentHandler.setMapHandler(_handler);
            contentHandler.setContext(getContext());
            final Set<IRef> scope = new HashSet<IRef>(_scope);
            final Collection<IRef> mergeMapScope = getMergeMapScope();
            if (mergeMapScope != null) {
                scope.addAll(mergeMapScope);
            }
            contentHandler.setMergeMapScope(scope);
            xmlReader.setContentHandler(contentHandler);
            final InputSource inputSource = new InputSource(_locator.toExternalForm());
            try {
                xmlReader.parse(inputSource);
            } catch (IOException ex) {
                throw new SAXException("I/O error while processing merge map " + _locator.toExternalForm() , ex);
            }
        }
    }
}
