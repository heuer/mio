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
package com.semagia.mio.jtm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import com.semagia.mio.IRef;
import com.semagia.mio.MIOException;
import com.semagia.mio.helpers.Locator;
import com.semagia.mio.helpers.Ref;
import com.semagia.mio.helpers.SimpleMapHandler;
import com.semagia.mio.voc.TMDM;
import com.semagia.mio.voc.XSD;

/**
 * JTM parser.
 * 
 * The JTM parser supports the top level item types "topicmap", "topic", 
 * "association", "occurrence" and "name". In other words: Detached roles
 * and variants are not supported.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 578 $ - $Date: 2010-09-30 21:52:37 +0200 (Do, 30 Sep 2010) $
 */
final class JTMParser {

    private static final IRef _DEFAULT_NAME_TYPE = Ref.createSubjectIdentifier(TMDM.TOPIC_NAME);
    private static final String _XSD = "http://www.w3.org/2001/XMLSchema#";
    private boolean _jtm11Mode;
    private final Map<String, String> _prefixes;

    private static enum ItemType {
        TOPICMAP,
        TOPIC,
        ASSOCIATION,
        ROLE,
        OCCURRENCE,
        NAME,
        VARIANT
    }
    
    private final Locator _docIRI;

    public JTMParser(final String docIRI) {
        _docIRI = Locator.create(docIRI);
        _prefixes = new HashMap<String, String>();
        _prefixes.put("xsd", _XSD);
    }

    /**
     * Entry point to interpret a JTM source.
     *
     * @param parser The parser.
     * @param handler The handler.
     * @throws IOException In case of an I/O error.
     * @throws MIOException In case of a syntax error.
     */
    public void parse(final JsonParser parser, final SimpleMapHandler handler) throws IOException, MIOException {
        if (parser.nextToken() != JsonToken.START_OBJECT) {
            throw new MIOException("Expected data to start with an Object");
        }
        IRef reifier = null;
        IRef type = null;
        List<IRef> parents = null;
        Iterable<IRef> scope = null;
        String value = null;
        String datatype = null;
        Iterable<String> iids = null;
        List<Role> roles = null;
        List<Variant> variants = null;
        ItemType itemType = null;
        boolean seenTopicIdentity = false;
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            switch (parser.getCurrentToken()) {
                case JsonToken.KW_VERSION:
                    parser.nextToken();
                    if ("1.0".equals(parser.getText())) {
                        _jtm11Mode = false;
                    }
                    else if ("1.1".equals(parser.getText())) {
                        _jtm11Mode = true;
                    }
                    else {
                        throw new MIOException("Unsupported version: " + parser.getText());
                    }
                    break;
                case JsonToken.KW_PREFIXES:
                    if (!_jtm11Mode) {
                        throw new MIOException("Prefixes are not allowed in JTM 1.0");
                    }
                    if (parser.nextToken() != JsonToken.START_OBJECT) {
                        throw new MIOException("Expected an object for the prefixes");
                    }
                    while (parser.nextToken() != JsonToken.END_OBJECT) {
                        String prefix = parser.getText();
                        parser.nextToken();
                        String iri = parser.getText();
                        _registerPrefix(prefix, iri);
                    }
                    break;
                case JsonToken.KW_ITEM_TYPE:
                    parser.nextToken();
                    itemType = ItemType.valueOf(parser.getText().toUpperCase());
                    if (ItemType.ROLE == itemType || ItemType.VARIANT == itemType) {
                        throw new MIOException("Detached variants or roles are not supported");
                    }
                    break;
                case JsonToken.KW_PARENT:
                    if (parser.nextToken() != JsonToken.START_ARRAY) {
                        throw new MIOException("Expected an array for the parent");
                    }
                    parents = new ArrayList<IRef>();
                    while (parser.nextToken() != JsonToken.END_ARRAY) {
                        parents.add(_makeTopicRef(parser.getText()));
                    }
                    break;
                case JsonToken.KW_IIDS:
                    iids = _readItemIdentifiers(parser);
                    break;
                case JsonToken.KW_SIDS:
                    if (!seenTopicIdentity) {
                        seenTopicIdentity = _processTopicItemIdentifiers(handler, iids);
                        iids = null;
                    }
                    if (parser.nextToken() != JsonToken.START_ARRAY) {
                        throw new MIOException("Expected an array of subject identifiers");
                    }
                    while (parser.nextToken() != JsonToken.END_ARRAY) {
                        String sid = _resolveIRI(parser.getText());
                        if (!seenTopicIdentity) {
                            handler.startTopic(Ref.createSubjectIdentifier(sid));
                            seenTopicIdentity = true;
                        }
                        else {
                            handler.subjectIdentifier(sid);
                        }
                    }
                    break;
                case JsonToken.KW_SLOS:
                    if (!seenTopicIdentity) {
                        seenTopicIdentity = _processTopicItemIdentifiers(handler, iids);
                        iids = null;
                    }
                    if (parser.nextToken() != JsonToken.START_ARRAY) {
                        throw new MIOException("Expected an array of subject locators");
                    }
                    while (parser.nextToken() != JsonToken.END_ARRAY) {
                        String slo = _resolveIRI(parser.getText());
                        if (!seenTopicIdentity) {
                            handler.startTopic(Ref.createSubjectLocator(slo));
                            seenTopicIdentity = true;
                        }
                        else {
                            handler.subjectLocator(slo);
                        }
                    }
                    break;
                case JsonToken.KW_INSTANCE_OF:
                    if (!_jtm11Mode) {
                        throw new MIOException("'instance_of' is not allowed in JTM 1.0");
                    }
                    if (!seenTopicIdentity) {
                        seenTopicIdentity = _processTopicItemIdentifiers(handler, iids);
                        iids = null;
                    }
                    if (parser.nextToken() != JsonToken.START_ARRAY) {
                        throw new MIOException("Expected an array of types");
                    }
                    while (parser.nextToken() != JsonToken.END_ARRAY) {
                        handler.isa(_makeTopicRef(parser.getText()));
                    }
                    break;
                case JsonToken.KW_OCCURRENCES:
                    if (!seenTopicIdentity) {
                        seenTopicIdentity = _processTopicItemIdentifiers(handler, iids);
                        iids = null;
                    }
                    if (!seenTopicIdentity) {
                        throw new MIOException("Cannot process occurrences without a previously read identity");
                    }
                    if (parser.nextToken() != JsonToken.START_ARRAY) {
                        throw new MIOException("Expected an array of occurrences");
                    }
                    while(parser.nextToken() != JsonToken.END_ARRAY) {
                        _handleOccurrence(parser, handler);
                    }
                    break;
                case JsonToken.KW_NAMES:
                    if (!seenTopicIdentity) {
                        seenTopicIdentity = _processTopicItemIdentifiers(handler, iids);
                        iids = null;
                    }
                    if (!seenTopicIdentity) {
                        throw new MIOException("Cannot process occurrences without a previously read identity");
                    }
                    if (parser.nextToken() != JsonToken.START_ARRAY) {
                        throw new MIOException("Expected an array of names");
                    }
                    while(parser.nextToken() != JsonToken.END_ARRAY) {
                        _handleName(parser, handler);
                    }
                    break;
                case JsonToken.KW_ROLES:
                    if (parser.nextToken() != JsonToken.START_ARRAY) {
                        throw new MIOException("Expected an array of roles");
                    }
                    roles = new ArrayList<Role>();
                    while (parser.nextToken() != JsonToken.END_ARRAY) {
                        Role role = new Role();
                        roles.add(role);
                        while (parser.nextToken() != JsonToken.END_OBJECT) {
                            switch (parser.getCurrentToken()) {
                                case JsonToken.KW_IIDS:
                                    role.iids = _readItemIdentifiers(parser);
                                    break;
                                case JsonToken.KW_REIFIER:
                                    role.reifier = _readReifier(parser);
                                    break;
                                case JsonToken.KW_TYPE:
                                    parser.nextToken();
                                    role.type = _makeTopicRef(parser.getText());
                                    break;
                                case JsonToken.KW_PLAYER:
                                    parser.nextToken();
                                    role.player = _makeTopicRef(parser.getText());
                                    break;
                            }
                        }
                    }
                    break;
                case JsonToken.KW_VARIANTS:
                    // Found a name
                    if (parser.nextToken() != JsonToken.START_ARRAY) {
                        throw new MIOException("Expected an array of variants");
                    }
                    variants = new ArrayList<Variant>();
                    while (parser.nextToken() != JsonToken.END_ARRAY) {
                        Variant variant = new Variant();
                        variants.add(variant);
                        while (parser.nextToken() != JsonToken.END_OBJECT) {
                            switch (parser.getCurrentToken()) {
                                case JsonToken.KW_IIDS:
                                    variant.iids = _readItemIdentifiers(parser);
                                    break;
                                case JsonToken.KW_REIFIER:
                                    variant.reifier = _readReifier(parser);
                                    break;
                                case JsonToken.KW_VALUE:
                                    parser.nextToken();
                                    variant.value = parser.getText();
                                    break;
                                case JsonToken.KW_DATATYPE:
                                    parser.nextToken();
                                    variant.datatype = parser.getText();
                                    break;
                                case JsonToken.KW_SCOPE:
                                    variant.scope = _readScope(parser);
                                    break;
                            }
                        }
                    }
                    break;
                case JsonToken.KW_TYPE:
                    parser.nextToken();
                    type = _makeTopicRef(parser.getText());
                    break;
                case JsonToken.KW_VALUE:
                    parser.nextToken();
                    value = parser.getText();
                    break;
                case JsonToken.KW_DATATYPE:
                    parser.nextToken();
                    datatype = _resolveIRI(parser.getText());
                    break;
                case JsonToken.KW_REIFIER:
                    reifier = _readReifier(parser);
                    break;
                case JsonToken.KW_SCOPE:
                    scope = _readScope(parser);
                    break;
                case JsonToken.KW_TOPICS:
                    if (parser.nextToken() == JsonToken.START_ARRAY) {
                        while (parser.nextToken() != JsonToken.END_ARRAY) {
                            _handleTopic(parser, handler);
                        }
                        break;
                    }
                case JsonToken.KW_ASSOCIATIONS:
                    if (parser.nextToken() == JsonToken.START_ARRAY) {
                        while (parser.nextToken() != JsonToken.END_ARRAY) {
                            _handleAssociation(parser, handler);
                        }
                        break;
                    }
                default:
                    _reportIllegalField(parser);
            }
        }
        if (itemType == null) {
            throw new MIOException("Illegal JSON topic map: No 'item_type' provided");
        }
        switch (itemType) {
            case TOPICMAP:
                _handleReifier(handler, reifier);
                _handleItemIdentifiers(handler, iids);
                break;
            case NAME:
                _handleParentTopic(handler, parents);
                handler.startName(type == null ? _DEFAULT_NAME_TYPE : type);
                _handleItemIdentifiers(handler, iids);
                _handleReifier(handler, reifier);
                _handleScope(handler, scope);
                handler.value(value);
                if (variants != null) {
                    for (Variant var: variants) {
                        handler.startVariant();
                        _handleReifier(handler, var.reifier);
                        _handleItemIdentifiers(handler, var.iids);
                        _handleScope(handler, var.scope);
                        handler.value(var.value, var.datatype);
                        handler.endVariant();
                    }
                }
                handler.endName();
                handler.endTopic();
                break;
            case OCCURRENCE:
                _handleParentTopic(handler, parents);
                handler.startOccurrence(type);
                _handleItemIdentifiers(handler, iids);
                _handleReifier(handler, reifier);
                _handleScope(handler, scope);
                handler.value(value, datatype == null ? XSD.STRING : datatype);
                handler.endOccurrence();
                handler.endTopic();
                break;
            case ASSOCIATION:
                if (roles == null) {
                    throw new MIOException("The association has no roles");
                }
                handler.startAssociation(type);
                _handleItemIdentifiers(handler, iids);
                _handleReifier(handler, reifier);
                _handleScope(handler, scope);
                for (Role role: roles) {
                    handler.startRole(role.type);
                    handler.player(role.player);
                    _handleItemIdentifiers(handler, role.iids);
                    _handleReifier(handler, role.reifier);
                    handler.endRole();
                }
                handler.endAssociation();
                break;
            case TOPIC:
                if (reifier != null || parents != null) {
                    throw new MIOException("");
                }
                if (!seenTopicIdentity) {
                    _processTopicItemIdentifiers(handler, iids);
                }
                handler.endTopic();
                break;
        }
    }

    private void _registerPrefix(final String prefix, final String iri) throws MIOException {
        if ("xsd".equals(prefix)) {
            if (!_XSD.equals(iri)) {
                throw new MIOException("The prefix 'xsd' is reserved for XML Schema Datatypes");
            }
            // Nothing to do.
            return;
        }
        _prefixes.put(prefix, _resolveIRI(iri));
    }

    private boolean _processTopicItemIdentifiers(final SimpleMapHandler handler,
            final Iterable<String> iids) throws MIOException {
        if (iids == null) {
            return false;
        }
        final Iterator<String> iter = iids.iterator();
        handler.startTopic(Ref.createItemIdentifier(iter.next()));
        while(iter.hasNext()) {
            handler.itemIdentifier(iter.next());
        }
        return true;
    }

    private Iterable<IRef> _readScope(final JsonParser parser) throws MIOException, IOException {
        if (parser.nextToken() != JsonToken.START_ARRAY) {
            throw new MIOException("Expected an array for the scope");
        }
        List<IRef> scope = new ArrayList<IRef>();
        while (parser.nextToken() != JsonToken.END_ARRAY) {
            scope.add(_makeTopicRef(parser.getText()));
        }
        return scope;
    }

    private IRef _readReifier(final JsonParser parser) throws MIOException, IOException {
        return parser.nextToken() != JsonToken.VALUE_NULL 
                        ? _makeTopicRef(parser.getText())
                        : null;
    }

    private Iterable<String> _readItemIdentifiers(final JsonParser parser) throws MIOException, IOException {
        List<String> iids = new ArrayList<String>();
        if (parser.nextToken() != JsonToken.START_ARRAY) {
            throw new MIOException("Expected an array for the item identifiers");
        }
        iids = new ArrayList<String>();
        while (parser.nextToken() != JsonToken.END_ARRAY) {
            iids.add(_resolveIRI(parser.getText()));
        }
        return iids;
    }

    private void _handleReifier(final SimpleMapHandler handler, final IRef reifier) throws MIOException {
        if (reifier != null) {
            handler.reifier(reifier);
        }
    }

    private void _handleItemIdentifiers(final SimpleMapHandler handler, Iterable<String> iids) throws MIOException {
        if (iids != null) {
            for (String iid: iids) {
                handler.itemIdentifier(iid);
            }
        }
    }

    private void _handleScope(final SimpleMapHandler handler, Iterable<IRef> scope) throws MIOException {
        if (scope != null) {
            handler.startScope();
            for (IRef theme: scope) {
                handler.theme(theme);
            }
            handler.endScope();
        }
    }

    /**
     * Parses the parent topic of a detached occurrence / name.
     * 
     * @param parser The parser.
     * @param handler The handler.
     * @throws IOException In case of an I/O error.
     * @throws MIOException In case of a syntax error.
     */
    private void _handleParentTopic(final SimpleMapHandler handler, final List<IRef> parents) throws IOException, MIOException {
        if (parents == null) {
            throw new MIOException("Cannot handle names / occurrences without parents");
        }
        final Iterator<IRef> iter = parents.iterator();
        handler.startTopic(iter.next());
        while(iter.hasNext()) {
            IRef ref = iter.next();
            switch (ref.getType()) {
                case IRef.ITEM_IDENTIFIER:
                    handler.itemIdentifier(ref.getIRI());
                    break;
                case IRef.SUBJECT_IDENTIFIER:
                    handler.subjectIdentifier(ref.getIRI());
                    break;
                case IRef.SUBJECT_LOCATOR:
                    handler.subjectLocator(ref.getIRI());
                    break;
                default:
                    throw new MIOException("Unknown reference type: " + ref.getType());
            }
        }
    }

    /**
     * 
     * 
     * @param tid
     *            A string which starts with 'si:', 'sl:' or 'ii:' followed by
     *            an IRI reference.
     * @return
     * @throws MIOException
     */
    private IRef _makeTopicRef(final String tid) throws MIOException {
        char[] chars = tid.toCharArray();
        if (chars.length > 3 && chars[2] == ':') {
            String iri = _resolveIRI(new String(chars, 3, chars.length - 3));
            if (chars[0] == 's') {
                if (chars[1] == 'i') {
                    return Ref.createSubjectIdentifier(_resolveIRI(iri));
                }
                else if (chars[1] == 'l') {
                    return Ref.createSubjectLocator(_resolveIRI(iri));
                }
            }
            else if (chars[0] == 'i' && chars[1] == 'i') {
                return Ref.createItemIdentifier(_resolveIRI(iri));
            }
        }
        throw new MIOException("Unknown topic reference: " + tid);
    }

    private String _resolveIRI(final String iri) throws MIOException {
        if (iri.charAt(0) == '[') {
            if (!_jtm11Mode) {
                throw new MIOException("Illegal CURIE in " + iri);
            }
            final int colon = iri.indexOf(':');
            if (colon == -1) {
                throw new MIOException("Illegal CURIE, no ':' found in " + iri);
            }
            final int length = iri.length();
            if (iri.charAt(length-1) != ']') {
                throw new MIOException("Illegal CURIE, no ']' found in " + iri);
            }
            final String prefix = iri.substring(1, colon);
            final String iriRef = iri.substring(colon+1, iri.length()-1);
            final String nsIRI = _prefixes.get(prefix);
            if (nsIRI == null) {
                throw new MIOException("Unknown prefix '" + prefix + "'");
            }
            return _docIRI.resolve(nsIRI + iriRef).getReference();
        }
        return _docIRI.resolve(iri).getReference();
    }

    /**
     * Reports the reifier iff it is not <tt>null</tt>.
     *
     * @param parser The parser.
     * @param handler The handler.
     * @throws IOException In case of an I/O error.
     * @throws MIOException In case of an error.
     */
    private void _handleReifier(final JsonParser parser, final SimpleMapHandler handler) throws IOException, MIOException {
        if (parser.nextToken() == JsonToken.VALUE_NULL) {
            return;
        }
        handler.reifier(_makeTopicRef(parser.getText()));
    }

    private void _handleItemIdentifiers(final JsonParser parser,
            SimpleMapHandler handler) throws IOException, MIOException {
        if (parser.nextToken() != JsonToken.START_ARRAY) {
            throw new MIOException("Expected an array for the item identifiers");
        }
        while (parser.nextToken() != JsonToken.END_ARRAY) {
            handler.itemIdentifier(_resolveIRI(parser.getText()));
        }
    }

    private void _handleScope(final JsonParser parser,
            SimpleMapHandler handler) throws IOException, MIOException {
        if (parser.nextToken() != JsonToken.START_ARRAY) {
            throw new MIOException("Expected an array for the scope themes");
        }
        handler.startScope();
        while (parser.nextToken() != JsonToken.END_ARRAY) {
            handler.theme(_makeTopicRef(parser.getText()));
        }
        handler.endScope();
    }

    private void _handleTopic(final JsonParser parser, final SimpleMapHandler handler) throws IOException, MIOException {
        boolean seenIdentity = false;
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            switch (parser.getCurrentToken()) {
                case JsonToken.KW_SIDS:
                    if (parser.nextToken() == JsonToken.START_ARRAY) {
                        while (parser.nextToken() != JsonToken.END_ARRAY) {
                            String sid = _resolveIRI(parser.getText());
                            if (!seenIdentity) {
                                handler.startTopic(Ref.createSubjectIdentifier(sid));
                                seenIdentity = true;
                            }
                            else {
                                handler.subjectIdentifier(sid);
                            }
                        }
                        break;
                    }
                case JsonToken.KW_SLOS:
                    if (parser.nextToken() == JsonToken.START_ARRAY) {
                        while (parser.nextToken() != JsonToken.END_ARRAY) {
                            String slo = _resolveIRI(parser.getText());
                            if (!seenIdentity) {
                                handler.startTopic(Ref.createSubjectLocator(slo));
                                seenIdentity = true;
                            }
                            else {
                                handler.subjectLocator(slo);
                            }
                        }
                        break;
                    }
                case JsonToken.KW_IIDS:
                    if (parser.nextToken() == JsonToken.START_ARRAY) {
                        while (parser.nextToken() != JsonToken.END_ARRAY) {
                            String iid = _resolveIRI(parser.getText());
                            if (!seenIdentity) {
                                handler.startTopic(Ref.createItemIdentifier(iid));
                                seenIdentity = true;
                            }
                            else {
                                handler.itemIdentifier(iid);
                            }
                        }
                        break;
                    }
                case JsonToken.KW_INSTANCE_OF:
                    if (!_jtm11Mode) {
                        throw new MIOException("'instance_of' is not allowed in JTM 1.0");
                    }
                    if (!seenIdentity) {
                        throw new MIOException("Cannot process instance of relationships without a previously read identity");
                    }
                    if (parser.nextToken() != JsonToken.START_ARRAY) {
                        throw new MIOException("Expected an array of types");
                    }
                    while (parser.nextToken() != JsonToken.END_ARRAY) {
                        handler.isa(_makeTopicRef(parser.getText()));
                    }
                    break;
                case JsonToken.KW_OCCURRENCES:
                    if (parser.nextToken() == JsonToken.START_ARRAY) {
                        if (!seenIdentity) {
                            throw new MIOException("Cannot process occurrences without a previously read identity");
                        }
                        while (parser.nextToken() != JsonToken.END_ARRAY) {
                            _handleOccurrence(parser, handler);
                        }
                        break;
                    }
                case JsonToken.KW_NAMES:
                    if (parser.nextToken() == JsonToken.START_ARRAY) {
                        if (!seenIdentity) {
                            throw new MIOException("Cannot process names without a previously read identity");
                        }
                        while (parser.nextToken() != JsonToken.END_ARRAY) {
                            _handleName(parser, handler);
                        }
                        break;
                    }
                default:
                _reportIllegalField(parser);
            }
        }
        if (!seenIdentity) {
            throw new MIOException("The topic has no identity");
        }
        handler.endTopic();
    }

    private void _handleOccurrence(final JsonParser parser, final SimpleMapHandler handler) throws IOException, MIOException {
        handler.startOccurrence();
        boolean seenType = false;
        String datatype = XSD.STRING;
        String value = null;
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            switch (parser.getCurrentToken()) {
                case JsonToken.KW_TYPE:
                    if (!seenType) {
                        parser.nextToken();
                        handler.type(_makeTopicRef(parser.getText()));
                        seenType = true;
                        break;
                    }
                case JsonToken.KW_VALUE:
                    if (value == null) {
                        parser.nextToken();
                        value = parser.getText();
                        break;
                    }
                case JsonToken.KW_DATATYPE:
                    parser.nextToken();
                    datatype = _resolveIRI(parser.getText());
                    break;
                case JsonToken.KW_IIDS:
                    _handleItemIdentifiers(parser, handler);
                    break;
                case JsonToken.KW_REIFIER:
                    _handleReifier(parser, handler);
                    break;
                case JsonToken.KW_SCOPE:
                    _handleScope(parser, handler);
                    break;
                default:
                    _reportIllegalField(parser);
            }
        }
        if (value == null) {
            throw new MIOException("The value of the occurrence is undefined");
        }
        if (datatype.equals(XSD.ANY_URI)) {
            value = _resolveIRI(value);
        }
        handler.value(value, datatype);
        if (!seenType) {
            throw new MIOException("The type of the occurrence is undefined");
        }
        handler.endOccurrence();
    }

    private void _handleName(final JsonParser parser, final SimpleMapHandler handler) throws IOException, MIOException {
        handler.startName();
        boolean seenValue = false;
        boolean seenType = false;
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            switch (parser.getCurrentToken()) {
                case JsonToken.KW_TYPE:
                    if (!seenType) {
                        parser.nextToken();
                        handler.type(_makeTopicRef(parser.getText()));
                        seenType = true;
                        break;
                    }
                case JsonToken.KW_VALUE:
                    if (!seenValue) {
                        parser.nextToken();
                        handler.value(parser.getText());
                        seenValue = true;
                        break;
                    }
                case JsonToken.KW_IIDS:
                    _handleItemIdentifiers(parser, handler);
                    break;
                case JsonToken.KW_REIFIER:
                    _handleReifier(parser, handler);
                    break;
                case JsonToken.KW_SCOPE:
                    _handleScope(parser, handler);
                    break;
                case JsonToken.KW_VARIANTS:
                    if (parser.nextToken() == JsonToken.START_ARRAY) {
                        while (parser.nextToken() != JsonToken.END_ARRAY) {
                            _handleVariant(parser, handler);
                        }
                        break;
                    }
                default:
                    _reportIllegalField(parser);
            }
        }
        if (!seenValue) {
            throw new MIOException("The value of the name is undefined");
        }
        if (!seenType) {
            handler.type(_DEFAULT_NAME_TYPE);
        }
        handler.endName();
    }

    private void _handleVariant(final JsonParser parser, final SimpleMapHandler handler) throws IOException, MIOException {
        handler.startVariant();
        String datatype = XSD.STRING;
        String value = null;
        boolean seenScope = false;
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            switch (parser.getCurrentToken()) {
                case JsonToken.KW_VALUE:
                    if (value == null) {
                        parser.nextToken();
                        value = parser.getText();
                        break;
                    }
                case JsonToken.KW_DATATYPE:
                    parser.nextToken();
                    datatype = _resolveIRI(parser.getText());
                    break;
                case JsonToken.KW_IIDS:
                    _handleItemIdentifiers(parser, handler);
                    break;
                case JsonToken.KW_REIFIER:
                    _handleReifier(parser, handler);
                    break;
                case JsonToken.KW_SCOPE:
                    if (!seenScope) {
                        _handleScope(parser, handler);
                        seenScope = true;
                        break;
                    }
                default:
                    _reportIllegalField(parser);
            }
        }
        if (!seenScope) {
            throw new MIOException("The scope of the variant is undefined");
        }
        if (value == null) {
            throw new MIOException("The value of the variant is undefined");
        }
        if (XSD.ANY_URI.equals(datatype)) {
            value = _resolveIRI(value);
        }
        handler.value(value, datatype);
        handler.endVariant();
    }

    private void _handleAssociation(final JsonParser parser, final SimpleMapHandler handler) throws IOException, MIOException {
        handler.startAssociation();
        boolean seenType = false;
        boolean seenRoles = false;
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            switch (parser.getCurrentToken()) {
                case JsonToken.KW_TYPE:
                    if (!seenType) {
                        parser.nextToken();
                        handler.type(_makeTopicRef(parser.getText()));
                        seenType = true;
                        break;
                    }
                case JsonToken.KW_IIDS:
                    _handleItemIdentifiers(parser, handler);
                    break;
                case JsonToken.KW_REIFIER:
                    _handleReifier(parser, handler);
                    break;
                case JsonToken.KW_SCOPE:
                    _handleScope(parser, handler);
                    break;
                case JsonToken.KW_ROLES:
                    if (!seenRoles && parser.nextToken() == JsonToken.START_ARRAY) {
                        while (parser.nextToken() != JsonToken.END_ARRAY) {
                            _handleRole(parser, handler);
                        }
                        seenRoles = true;
                        break;
                    }
                default:
                    _reportIllegalField(parser);
            }
        }
        if (!seenType) {
            throw new MIOException("The type of the association is undefined");
        }
        if (!seenRoles) {
            throw new MIOException("The association has no roles");
        }
        handler.endAssociation();
    }

    private void _handleRole(final JsonParser parser, final SimpleMapHandler handler) throws IOException, MIOException {
        handler.startRole();
        boolean seenType = false;
        boolean seenPlayer = false;
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            switch (parser.getCurrentToken()) {
                case JsonToken.KW_TYPE:
                    if (!seenType) {
                        parser.nextToken();
                        handler.type(_makeTopicRef(parser.getText()));
                        seenType = true;
                        break;
                    }
                case JsonToken.KW_PLAYER:
                    if (!seenPlayer) {
                        parser.nextToken();
                        handler.player(_makeTopicRef(parser.getText()));
                        seenPlayer = true;
                        break;
                    }
                case JsonToken.KW_IIDS:
                    _handleItemIdentifiers(parser, handler);
                    break;
                case JsonToken.KW_REIFIER:
                    _handleReifier(parser, handler);
                    break;
                default:
                    _reportIllegalField(parser);
            }
        }
        if (!seenType) {
            throw new MIOException("The type of the role is undefined");
        }
        if (!seenPlayer) {
            throw new MIOException("The player of the role is undefined");
        }
        handler.endRole();
    }

    private void _reportIllegalField(final JsonParser parser) throws IOException, MIOException {
        throw new MIOException("Unknown field name: '" + parser.getText() + "' current: " + JsonToken.nameOf(parser.getCurrentToken()));
    }

    private static final class Role {

        private IRef reifier;
        private Iterable<String> iids;
        private IRef player;
        private IRef type;
        
    }

    private static final class Variant {

        public Iterable<IRef> scope;
        private String value;
        private String datatype = XSD.STRING;
        private IRef reifier;
        private Iterable<String> iids;

    }

}
