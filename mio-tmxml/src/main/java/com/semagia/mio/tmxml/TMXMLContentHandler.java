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

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.semagia.mio.IRef;
import com.semagia.mio.MIOException;
import com.semagia.mio.helpers.Locator;
import com.semagia.mio.helpers.QName;
import com.semagia.mio.helpers.Ref;
import com.semagia.mio.helpers.SimpleMapHandler;
import com.semagia.mio.tmxml.api.IPrefixListener;
import com.semagia.mio.voc.TMDM;
import com.semagia.mio.voc.XSD;

/**
 * Content handler for TM/XML topic maps.
 * <p>
 * <a href="http://www.ontopia.net/topicmaps/tmxml.html">TM/XML homepage</a>
 * </p> 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 467 $ - $Date: 2010-09-08 12:17:40 +0200 (Mi, 08 Sep 2010) $
 */
final class TMXMLContentHandler extends DefaultHandler {

    private static final String _NS_TMXML = "http://psi.ontopia.net/xml/tm-xml/";
    private static final String _NS_TMDM = "http://psi.topicmaps.org/iso13250/model/";

    /**
     * Subject identifier for the default name type.
     */
    private static final IRef _TOPIC_NAME = Ref.createSubjectIdentifier(TMDM.TOPIC_NAME);

    /**
     * Unconstrained scope.
     */
    private static final IRef[] _UCS = new IRef[0];

    private static final int
        _INITIAL = 0, 
        _TOPICMAP = 1,
        _TOPIC = 2, 
        _IDENTITY = 3, 
        _ASSOCIATION = 4, 
        _AFTER_ROLE = 5,
        _BASENAME = 6, 
        _NAME = 7, 
        _VARIANT = 8, 
        _PROPERTY = 9;

    private SimpleMapHandler _handler;
    private final Map<String, String> _prefixes;
    private final StringBuilder _content;
    private int _state;
    private String _datatype;
    private String _reifier;
    private IRef _currentTopic;
    private IRef _type;
    private IRef[] _scope;
    private Locator _docIRI;
    private boolean _seenIdentity;
    private IRef _topicType;
    private IPrefixListener _listener;

    TMXMLContentHandler() {
        _prefixes = new HashMap<String, String>();
        _state = _INITIAL;
        _content = new StringBuilder();
    }

    /**
     * Sets the prefix listener.
     *
     * @param listener An instance of {@link IPrefixListener} or {@code null}.
     */
    public void setPrefixListener(final IPrefixListener listener) {
        _listener = listener;
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(final String uri, final String name, 
            final String qName,final Attributes attrs) throws SAXException {
        switch (_state) {
            case _INITIAL: {
                _reifier(attrs);
                _state = _TOPICMAP;
                break;
            }
            case _TOPICMAP: {
                final String id = attrs.getValue("", "id");
                // The id is optional iff the topic has at least one sid / slo
                if (id != null) {
                    _currentTopic = _resolveItemIdentifier(id);
                    _handler.startTopic(_currentTopic);
                    _seenIdentity = true;
                }
                else {
                    _seenIdentity = false;
                }
                final IRef type = _resolveType(uri, name);
                if (type != null) {
                    if (_seenIdentity) {
                        _handler.isa(type);
                    }
                    else {
                        // No startTopic event, delay the type-instance assoc.
                        _topicType = type;
                    }
                }
                _state = _TOPIC;
                break;
            }
            case _TOPIC: {
                    if (uri == _NS_TMXML && (name == "identifier" || name == "locator")) {
                        _state = _IDENTITY;
                        _content.setLength(0);
                    }
                    else {
                        if (!_seenIdentity) {
                            throw new MIOException("Cannot process role / occurrence / name. The parent topic has no identity");
                        }
                        if (attrs.getValue("", "role") != null) {
                            _handler.startAssociation(_resolveType(uri, name));
                            _scope(attrs);
                            _reifier(attrs);
                            // 1 .. n-ary
                            _handler.startRole(_resolveTopicByReference(attrs.getValue("", "role")));
                            _handler.player(_currentTopic);
                            _handler.endRole();
                            if (attrs.getValue("", "topicref") != null) {
                                // 2-ary
                                _handler.startRole(_resolveTopicByReference(attrs.getValue("", "otherrole")));
                                _handler.player(_resolveTopicByReference(attrs.getValue("", "topicref")));
                                _handler.endRole();
                            }
                            _state = _ASSOCIATION;
                        }
                        else {
                            _scope = _collectScope(attrs);
                            _type = _resolveType(uri, name);
                            _reifier = attrs.getValue("", "reifier");
                            _content.setLength(0);
                            _datatype = _getDatatype(attrs);
                            _state = _PROPERTY;
                        }
                    }
                    break;
                }
            case _ASSOCIATION: {
                    _handler.startRole(_resolveType(uri, name));
                    _handler.player(_resolveTopicByReference(attrs.getValue("", "topicref")));
                    _reifier(attrs);
                    _handler.endRole();
                    _state = _AFTER_ROLE;
                    break;
                }
            case _PROPERTY: {
                    if (uri == _NS_TMXML && name == "value") {
                        _content.setLength(0);
                        _state = _BASENAME;
                    }
                    break;
                }
            case _NAME: {
                    if (uri == _NS_TMXML && name == "variant") {
                        _scope = _collectScope(attrs);
                        _reifier = attrs.getValue("", "reifier");
                        _datatype = _getDatatype(attrs);
                        _content.setLength(0);
                        _state = _VARIANT;
                    }
                    break;
                }
        }
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(final String uri, final String name, 
            final String qName) throws SAXException {
        switch (_state) {
            case _TOPICMAP: {
                    _state = _INITIAL;
                    break;
                }
            case _TOPIC: {
                    _handler.endTopic();
                    _state = _TOPICMAP;
                    break;
                }
            case _IDENTITY: {
                    final String iri = _createIRI(_content.toString());
                    if (!_seenIdentity) {
                        _handleDelayedTopic(name, iri);
                        _seenIdentity = true;
                    }
                    else if (name == "identifier") {
                        _handler.subjectIdentifier(iri);
                    }
                    else {
                        assert name == "locator";
                        _handler.subjectLocator(iri);
                    }
                    _content.setLength(0);
                    _state = _TOPIC;
                    break;
                }
            case _PROPERTY: {
                    _handler.startOccurrence(_resolveType(uri, name));
                    _handler.value(_content.toString(), _datatype);
                    _processScope(_scope);
                    _processReifier(_reifier);
                    _handler.endOccurrence();
                    _state = _TOPIC;
                    break;
                }
            case _BASENAME: {
                    _handler.startName(_type);
                    _processScope(_scope);
                    _processReifier(_reifier);
                    _handler.value(_content.toString());
                    _state = _NAME;
                    break;
                }
            case _NAME: {
                    _handler.endName();
                    _state = _TOPIC;
                    break;
                }
            case _VARIANT: {
                    _handler.startVariant();
                    _processScope(_scope);
                    _processReifier(_reifier);
                    _handler.value(_content.toString(), _datatype);
                    _handler.endVariant();
                    _state = _NAME;
                    break;
                }
            case _ASSOCIATION: {
                    _handler.endAssociation();
                    _state = _TOPIC;
                    break;
                }
            case _AFTER_ROLE: {
                _state = _ASSOCIATION;
                break;
            }
        }
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        if (_state == _IDENTITY 
                || _state == _PROPERTY 
                || _state == _BASENAME
                || _state == _VARIANT) {
            _content.append(ch, start, length);
        }
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#startPrefixMapping(java.lang.String, java.lang.String)
     */
    @Override
    public void startPrefixMapping(final String prefix, final String uri)
            throws SAXException {
        _prefixes.put(prefix, uri);
        if (_listener != null) {
            _listener.handleNamespace(prefix, uri);
        }
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#endPrefixMapping(java.lang.String)
     */
    @Override
    public void endPrefixMapping(final String prefix) throws SAXException {
        _prefixes.remove(prefix);
    }

    /**
     * Sends a startTopic event with a subject identifier or subject locator.
     *
     * @param name The element's name.
     * @param iri The iri for of the subject identifier/locator.
     * @throws MIOException In case an error happens.
     */
    private void _handleDelayedTopic(final String name, final String iri) 
                throws MIOException {
        if (name == "identifier") {
            _currentTopic = Ref.createSubjectIdentifier(iri);
        }
        else {
            assert name == "locator";
            _currentTopic = Ref.createSubjectLocator(iri);
        }
        // The topic has no id, send the missing startTopic event
        _handler.startTopic(_currentTopic);
        if (_topicType != null) {
            _handler.isa(_topicType);
            _topicType = null;
        }
    }

    /**
     * Returns the datatype from the attributes.
     *
     * @param attrs
     * @return The datatype value or <code>xsd:string</code> if the datatype
     *          information is not present.
     */
    private String _getDatatype(final Attributes attrs) {
        final String datatype = attrs.getValue("", "datatype");
        return datatype != null ? datatype : XSD.STRING;
    }

    /**
     * Resolves the specified <code>ref</code> to a subject identifier or 
     * item identifier.
     *
     * @param ref A topic reference.
     * @return A reference to a subject identifier if <code>ref</code> is a QName,
     *          otherwise a reference to an item identifier.
     * @throws MIOException
     */
    private IRef _resolveTopicByReference(final String ref) throws MIOException {
        return ref.indexOf(':') != -1 ? _resolveSubjectIdentifier(ref)
                                      : _resolveItemIdentifier(ref);
    }

    /**
     * Returns a reference to a topic by its subject identifier.
     *
     * @param name A qname (prefix:local).
     * @return A subject identifier reference.
     * @throws MIOException If the prefix is unknown.
     */
    private IRef _resolveSubjectIdentifier(final String name) throws MIOException {
        final QName qname = QName.create(name);
        final String iri = _prefixes.get(qname.getPrefix());
        if (iri == null) {
            _reportError("Undeclared prefix '" + qname.getPrefix() + "'");
        }
        return Ref.createSubjectIdentifier(_createIRI(iri + qname.getLocal()));
    }

    private IRef _resolveItemIdentifier(final String name) {
        return Ref.createItemIdentifier(_createIRI("#" + name));
    }

    private IRef _resolveType(final String uri, final String name) {
        if (uri == null || uri == "") {
            return _resolveItemIdentifier(name);
        }
        if (uri == _NS_TMDM && name == "topic-name") {
            return _TOPIC_NAME;
        }
        else if (uri == _NS_TMXML && name == "topic") {
            return null;
        }
        return Ref.createSubjectIdentifier(_createIRI(uri + name));
    }

    private String _createIRI(final String reference) {
        return _docIRI.resolve(reference).getReference();
    }

    /**
     * Extracts the scope information from the specified attributes.
     *
     * @param attrs The attributes to extract the scope from.
     * @return A (maybe empty) set of resolved topic references.
     * @throws MIOException If an error occurs.
     */
    private IRef[] _collectScope(final Attributes attrs) throws MIOException {
        final String strScope = attrs.getValue("", "scope");
        if (strScope == null) {
            return _UCS;
        }
        final String[] themes = strScope.split(" ");
        final IRef[] scope = new IRef[themes.length];
        for (int i=0; i<themes.length; i++) {
            scope[i] = _resolveTopicByReference(themes[i]);
        }
        return scope;
    }

    /**
     * Extracts and processes the scope from the specified attributes.
     *
     * @param attrs The attributes to extract the scope from.
     * @throws MIOException If an error occurs.
     */
    private void _scope(final Attributes attrs) throws MIOException {
        _processScope(_collectScope(attrs));
    }

    /**
     * Notifies the underlying map handler about the scope iff the scope is
     * not the unconstrained scope.
     *
     * @param scope The scope to process (never <code>null</code>).
     * @throws MIOException If an error occurs.
     */
    private void _processScope(final IRef[] scope) throws MIOException {
        if (scope.length == 0) {
            return;
        }
        _handler.startScope();
        for (IRef theme: scope) {
            _handler.theme(theme);
        }
        _handler.endScope();
    }

    /**
     * Extracts the reifier value from the specified attributes and
     * sends a reifier event to the map handler iff the reifier information
     * is provided.
     *
     * @param attrs The attributes to extract the reifier information from.
     * @throws MIOException If an error occurs.
     */
    private void _reifier(final Attributes attrs) throws MIOException {
        _processReifier(attrs.getValue("", "reifier"));
    }

    /**
     * Notifies the map handler about a reifier event iff <code>reifier</code> 
     * is not null.
     *
     * @param reifier Either a QName (prefix:local) or a simple reference.
     * @throws MIOException If the reifier cannot be processed.
     */
    private void _processReifier(final String reifier) throws MIOException {
        if (reifier != null) {
            _handler.reifier(_resolveTopicByReference(reifier));
        }
    }

    private static void _reportError(final String msg) throws MIOException {
        throw new MIOException(msg);
    }

    public void setDocumentIRI(final String docIRI) {
        _docIRI = Locator.create(docIRI);
    }

    public void setMapHandler(final SimpleMapHandler handler) {
        _handler = handler;
    }

}
