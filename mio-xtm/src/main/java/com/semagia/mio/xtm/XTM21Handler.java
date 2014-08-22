/*
 * Copyright 2008 - 2010 Lars Heuer (heuer[at]semagia.com)
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
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import com.semagia.mio.IRef;
import com.semagia.mio.MIOException;
import com.semagia.mio.helpers.AbstractHamsterMapHandler;
import com.semagia.mio.helpers.Ref;
import com.semagia.mio.utils.xml.XMLWriter;
import com.semagia.mio.voc.TMDM;
import com.semagia.mio.voc.XSD;

/**
 * EXPERIMENTAL {@link IMapHandler} implementation that translates all events into 
 * <a href="http://www.itscj.ipsj.or.jp/sc34/open/1378.htm">XML Topic Maps (XTM) 2.1</a>.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 598 $ - $Date: 2011-01-13 22:24:26 +0100 (Do, 13 Jan 2011) $
 */
public final class XTM21Handler extends AbstractHamsterMapHandler<IRef> {

    private static final Logger LOG = LoggerFactory.getLogger(XTM21Handler.class);

    private static final IRef _DEFAULT_NAME = Ref.createSubjectIdentifier(TMDM.TOPIC_NAME);
    private static final String _XTM_NS = "http://www.topicmaps.org/xtm/";

    private static final int 
        _INITIAL = 1,
        _CHARACTERISTIC = 2,
        _INSTANCE_OF = 3,
        _TM_IID = 4,
        _TOPIC_MAP = 5,
        _IDENTITY = 6;

    private final XMLWriter _out;
    private final AttributesImpl _attrs;
    private Topic _lastTopic;
    private int _state;

    private String _title;
    private String _author;
    private String _license;
    private String _comment;

    public XTM21Handler(final OutputStream out) throws IOException {
        _out = new XMLWriter(out, "UTF-8");
        _attrs = new AttributesImpl();
    }

    /**
     * Indicates if the XML should be prettified.
     *
     * @param prettify {@code true} to prettify the output, otherwise {@code false}.
     */
    public void setPrettify(final boolean prettify) {
        _out.setPrettify(prettify);
    }

    /**
     * Returns if the XML is prettified.
     *
     * @return {@code true} if the XML is prettified, otherwise {@code false}.
     */
    public boolean getPrettify() {
        return _out.getPrettify();
    }

    /**
     * Sets the title of the topic map which appears in the header comment of
     * the file.
     *
     * @param title The title of the topic map.
     */
    public void setTitle(final String title) {
        _title = title;
    }

    /**
     * Returns the title of the topic map.
     *
     * @return The title or <tt>null</tt> if no title was set.
     */
    public String getTitle() {
        return _title;
    }

    /**
     * Sets the author which appears in the header comment of the file.
     *
     * @param author The author.
     */
    public void setAuthor(final String author) {
        _author = author;
    }

    /**
     * Returns the author.
     *
     * @return The author or <tt>null</tt> if no author was set.
     */
    public String getAuthor() {
        return _author;
    }

    /**
     * Sets the license which should appear in the header comment of the file.
     * <p>
     * The license of the topic map. This could be a name or an IRI or both, i.e.
     * "Creative Commons-License <http://creativecommons.org/licenses/by-nc-sa/3.0/>".
     * </p>
     *
     * @param license The license.
     */
    public void setLicense(final String license) {
        _license = license;
    }

    /**
     * Returns the license.
     *
     * @return The license or <tt>null</tt> if no license was set.
     */
    public String getLicense() {
        return _license;
    }

    /**
     * The an additional comment which appears in the header comment of the file.
     * <p>
     * The comment could describe the topic map, or provide an additional 
     * copyright notice, or SVN/CVS keywords etc.
     * </p>
     *
     * @param comment The comment.
     */
    public void setComment(final String comment) {
        _comment = comment;
    }

    /**
     * Returns the comment.
     *
     * @return The comment or <tt>null</tt> if no comment was set.
     */
    public String getComment() {
        return _comment;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.helpers.AbstractHamsterMapHandler#startTopicMap()
     */
    @Override
    public void startTopicMap() throws MIOException {
        super.startTopicMap();
        try {
            _out.startDocument();
            _writeHeader();
            _attrs.clear();
            _attrs.addAttribute("", "xmlns", "", "CDATA", _XTM_NS);
            _attrs.addAttribute("", "version", "", "CDATA", "2.1");
            _out.startElement("topicMap", _attrs);
            _state = _INITIAL;
        }
        catch (IOException ex) {
            throw new MIOException(ex);
        }
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.helpers.AbstractHamsterMapHandler#endTopicMap()
     */
    @Override
    public void endTopicMap() throws MIOException {
        super.endTopicMap();
        try {
            _finishPendingTopic();
            _out.endElement("topicMap");
            _out.endDocument();
        }
        catch (IOException ex) {
            throw new MIOException(ex);
        }
    }

    private void _writeHeader() throws IOException {
        if (_title != null || _author != null || _license != null || _comment != null) {
            final StringBuffer buff = new StringBuffer();
            buff.append('\n');
            if (_title != null) {
                buff.append("Title:   ")
                    .append(_title)
                    .append('\n');
            }
            if (_author != null) {
                buff.append("Author:  ")
                    .append(_author)
                    .append('\n');
            }
            if (_license != null) {
                buff.append("License: ")
                    .append(_license)
                    .append('\n');
            }
            if (_comment != null) {
                buff.append('\n')
                    .append(_comment)
                    .append('\n');
            }
            buff.append('\n');
            _out.comment(buff.toString());
        }
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.helpers.AbstractHamsterMapHandler#startTopic(com.semagia.mio.IRef)
     */
    @Override
    public void startTopic(IRef identity) throws MIOException {
        super.startTopic(identity);
        try {
            _startTopic(identity, _IDENTITY);
        }
        catch (IOException ex) {
            throw new MIOException(ex);
        }
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.helpers.HamsterHandler#createAssociation(java.lang.Object, java.util.Collection, java.lang.Object, java.util.Collection, java.util.Collection)
     */
    @Override
    protected void createAssociation(final IRef type, 
            final Collection<IRef> scope,
            final IRef reifier, final Collection<String> iids,
            final Collection<IRole<IRef>> roles) throws MIOException {
        try {
            _finishPendingTopic();
            _state = _TOPIC_MAP;
            _out.startElement("association");
            _writeReifier(reifier);
            _writeItemIdentifiers(iids);
            _writeType(type);
            _writeScope(scope);
            for (IRole<IRef> role: roles) {
                _out.startElement("role");
                _writeReifier(role.getReifier());
                _writeItemIdentifiers(role.getItemIdentifiers());
                _writeType(role.getType());
                _writeTopicRef(role.getPlayer());
                _out.endElement("role");
            }
            _out.endElement("association");
        }
        catch (IOException ex) {
            throw new MIOException(ex);
        }
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.helpers.HamsterHandler#createName(java.lang.Object, java.lang.Object, java.lang.String, java.util.Collection, java.lang.Object, java.util.Collection, java.util.Collection)
     */
    @Override
    protected void createName(final IRef parent, final IRef type,
            final String value, final Collection<IRef> scope,
            final IRef reifier, final Collection<String> iids,
            final Collection<IVariant<IRef>> variants) throws MIOException {
        try {
            _startTopic(parent, _CHARACTERISTIC);
            _state =  _CHARACTERISTIC;
            _out.startElement("name");
            _writeReifier(reifier);
            _writeItemIdentifiers(iids);
            if (!_DEFAULT_NAME .equals(type)) {
                _writeType(type);
            }
            _writeScope(scope);
            _out.dataElement("value", value);
            for (IVariant<IRef> var: variants) {
                _out.startElement("variant");
                _writeReifier(var.getReifier());
                _writeItemIdentifiers(var.getItemIdentifiers());
                _writeScope(var.getScope());
                _writeValueDatatype(var.getValue(), var.getDatatype());
                _out.endElement("variant");
            }
            _out.endElement("name");
        }
        catch (IOException ex) {
            throw new MIOException(ex);
        }
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.helpers.HamsterHandler#createOccurrence(java.lang.Object, java.lang.Object, java.lang.String, java.lang.String, java.util.Collection, java.lang.Object, java.util.Collection)
     */
    @Override
    protected void createOccurrence(final IRef parent, final IRef type, final String value,
            final String datatype, final Collection<IRef> scope, final IRef reifier,
            final Collection<String> iids) throws MIOException {
        try {
            _startTopic(parent, _CHARACTERISTIC);
            _state = _CHARACTERISTIC;
            _out.startElement("occurrence");
            _writeReifier(reifier);
            _writeItemIdentifiers(iids);
            _writeType(type);
            _writeScope(scope);
            _writeValueDatatype(value, datatype);
            _out.endElement("occurrence");
        }
        catch (IOException ex) {
            throw new MIOException(ex);
        }
    }

    /**
     * 
     *
     * @param value
     * @param datatype
     * @throws IOException
     */
    private void _writeValueDatatype(final String value, final String datatype) throws IOException {
        if (XSD.ANY_URI.equals(datatype)) {
            _out.emptyElement("resourceRef", _href(value));
        }
        else if (XSD.STRING.equals(datatype)) {
            _out.dataElement("resourceData", value);
        }
        else {
            _attrs.clear();
            _attrs.addAttribute("", "datatype", "", "CDATA", datatype);
            _out.dataElement("resourceData", _attrs, value);
        }
    }

    /**
     * 
     *
     * @param type
     * @throws IOException
     */
    private void _writeType(final IRef type) throws IOException {
        _out.startElement("type");
        _writeTopicRef(type);
        _out.endElement("type");
    }

    /**
     * 
     *
     * @param scope
     * @throws IOException
     */
    private void _writeScope(final Collection<IRef> scope) throws IOException {
        if (scope != null && !scope.isEmpty()) {
            _out.startElement("scope");
            for (IRef theme: scope) {
                _writeTopicRef(theme);
            }
            _out.endElement("scope");
        }
    }

    /**
     * 
     *
     * @param iids
     * @throws IOException
     */
    private void _writeItemIdentifiers(final Collection<String> iids) throws IOException {
        for (String iid: iids) {
            _writeItemIdentifier(iid);
        }
    }

    /**
     * 
     *
     * @param iid
     * @throws IOException
     */
    private void _writeItemIdentifier(final String iid) throws IOException {
        _out.emptyElement("itemIdentity", _href(iid));
    }

    /**
     * 
     *
     * @param reifier
     * @throws IOException
     */
    private void _writeReifier(final IRef reifier) throws IOException {
        if (reifier != null) {
            _out.startElement("reifier");
            _writeTopicRef(reifier);
            _out.endElement("reifier");
        }
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.helpers.HamsterHandler#createTopicByItemIdentifier(java.lang.String)
     */
    @Override
    protected IRef createTopicByItemIdentifier(final String iid) throws MIOException {
        return Ref.createItemIdentifier(iid);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.helpers.HamsterHandler#createTopicBySubjectIdentifier(java.lang.String)
     */
    @Override
    protected IRef createTopicBySubjectIdentifier(final String sid)
            throws MIOException {
        return Ref.createSubjectIdentifier(sid);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.helpers.HamsterHandler#createTopicBySubjectLocator(java.lang.String)
     */
    @Override
    protected IRef createTopicBySubjectLocator(final String slo) throws MIOException {
        return Ref.createSubjectLocator(slo);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.helpers.HamsterHandler#handleItemIdentifier(java.lang.Object, java.lang.String)
     */
    @Override
    protected void handleItemIdentifier(final IRef topic, final String iid)
            throws MIOException {
        _handleIdentity(topic, Ref.createItemIdentifier(iid));
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.helpers.HamsterHandler#handleSubjectIdentifier(java.lang.Object, java.lang.String)
     */
    @Override
    protected void handleSubjectIdentifier(final IRef topic, final String sid)
            throws MIOException {
        _handleIdentity(topic, Ref.createSubjectIdentifier(sid));
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.helpers.HamsterHandler#handleSubjectLocator(java.lang.Object, java.lang.String)
     */
    @Override
    protected void handleSubjectLocator(final IRef topic, final String slo)
            throws MIOException {
        _handleIdentity(topic, Ref.createSubjectLocator(slo));
    }

    /**
     * 
     *
     * @throws IOException
     */
    private void _finishPendingTopic() throws IOException {
        if (_lastTopic != null) {
            _finishPendingInstanceOf();
            _out.endElement("topic");
            _lastTopic = null;
            _state = _TOPIC_MAP;
        }
    }

    private void _finishPendingInstanceOf() throws IOException {
        if (_state == _INSTANCE_OF) {
            _out.endElement("instanceOf");
        }
    }

    private void _handleIdentity(final IRef topic, final IRef identity) throws MIOException {
        try {
            boolean written = false;
            if (_lastTopic != null && _state == _TOPIC_MAP) {
                if (_lastTopic.hasIdentity(topic)) {
                    if (_lastTopic.hasIdentity(identity)) {
                        return;
                    }
                    _lastTopic.addIdentity(identity);
                    _writeIdentity(identity);
                    written = true;
                }
                else if (_lastTopic.hasIdentity(identity)) {
                    _lastTopic.addIdentity(topic);
                    _writeIdentity(topic);
                    written = true;
                }
                else if (_lastTopic.shouldMergeWith(topic)
                            || _lastTopic.shouldMergeWith(identity)) {
                    _lastTopic.addIdentity(topic);
                    _lastTopic.addIdentity(identity);
                    _writeIdentity(topic);
                    _writeIdentity(identity);
                    written = true;
                }
            }
            if (!written) {
                _startTopic(topic, _IDENTITY);
                _writeIdentity(identity);
                _lastTopic.addIdentity(identity);
            }
        }
        catch (IOException ex) {
            throw new MIOException(ex);
        }
    }

    /**
     * 
     *
     * @param identity The "main" identity of the topic.
     * @param nextState The next state.
     * @return If a new topic was started.
     * @throws IOException In case of an error.
     */
    private boolean _startTopic(final IRef identity, final int nextState) throws IOException {
        final boolean forceNewTopic = !_nextStateIsCompatible(nextState);
        if (_lastTopic != null 
                && _lastTopic.hasIdentity(identity) 
                && !forceNewTopic) {
            if (_state != nextState) {
                _finishPendingInstanceOf();
            }
            return false;
        }
        final boolean shouldMerge = _lastTopic != null 
                                        && _lastTopic.shouldMergeWith(identity)
                                        // if we're in state characteristic or
                                        // in state instanceOf, we cannot write
                                        // an additional identity.
                                        && _state == _TOPIC_MAP;
        final boolean newTopic = _lastTopic == null || forceNewTopic 
                                    || (_lastTopic != null && !(shouldMerge || _lastTopic.hasIdentity(identity)));
        if (newTopic) {
            _finishPendingTopic();
            _lastTopic = new Topic(identity);
            _out.startElement("topic");
            _writeIdentity(identity);
            _state = _TOPIC_MAP;
            return true;
        }
        else if (shouldMerge) {
            _lastTopic.addIdentity(identity);
            _writeIdentity(identity);
            _state = _TOPIC_MAP;
            return false;
        }
        return false;
    }

    private boolean _nextStateIsCompatible(final int nextState) {
        if (_state == _CHARACTERISTIC) {
            return nextState == _CHARACTERISTIC;
        }
        else if (_state == _TOPIC_MAP) {
            return nextState != _IDENTITY;
        }
        else if (_state == _INSTANCE_OF) {
            return nextState == _CHARACTERISTIC || nextState == _INSTANCE_OF;
        }
        return true;
    }

    private void _writeIdentity(final IRef identity) throws IOException {
        if (identity.getType() == IRef.ITEM_IDENTIFIER) {
            _writeItemIdentifier(identity.getIRI());
        }
        else {
            final String el = identity.getType() == IRef.SUBJECT_IDENTIFIER 
                                                        ? "subjectIdentifier"
                                                        : "subjectLocator";
            _out.emptyElement(el, _href(identity));
        }
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.helpers.HamsterHandler#handleTopicMapItemIdentifier(java.lang.String)
     */
    @Override
    protected void handleTopicMapItemIdentifier(final String iid)
            throws MIOException {
        try {
            if (_state != _INITIAL && _state != _TM_IID) {
                final String msg = "Ignoring topic map item identifier '" + iid + "' since it is not allowed to write it now according to the schema";
                LOG.warn(msg);
                _out.comment(msg);
            }
            else {
                _state = _TM_IID;
                _writeItemIdentifier(iid);
            }
        }
        catch (IOException ex) {
            throw new MIOException(ex);
        }
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.helpers.HamsterHandler#handleTopicMapReifier(java.lang.Object)
     */
    @Override
    protected void handleTopicMapReifier(final IRef reifier) throws MIOException {
        try {
            if (_state != _INITIAL) {
                final String msg = "Ignoring topic map reifier '" + reifier + "' since it is not allowed to write it now according to the schema";
                LOG.warn(msg);
                _out.comment(msg);
            }
            else {
                _writeReifier(reifier);
            }
        }
        catch (IOException ex) {
            throw new MIOException(ex);
        }
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.helpers.HamsterHandler#handleTypeInstance(java.lang.Object, java.lang.Object)
     */
    @Override
    protected void handleTypeInstance(final IRef instance, final IRef type) throws MIOException {
        try {
            if (!_startTopic(instance, _INSTANCE_OF) && _state == _INSTANCE_OF) {
                _writeTopicRef(type);
            }
            else {
                _out.startElement("instanceOf");
                _writeTopicRef(type);
                _state = _INSTANCE_OF;
            }
        }
        catch (IOException ex) {
            throw new MIOException(ex);
        }
    }

    /**
     * 
     *
     * @param ref
     * @throws IOException
     */
    private void _writeTopicRef(final IRef ref) throws IOException {
        final int type = ref.getType();
        final String el = type == IRef.SUBJECT_IDENTIFIER 
                                    ? "subjectIdentifierRef"
                                    : type == IRef.ITEM_IDENTIFIER ? "topicRef" 
                                                                   : "subjectLocatorRef";
        _out.emptyElement(el, _href(ref));
    }

    private Attributes _href(final IRef ref) {
        return _href(ref.getIRI());
    }

    private Attributes _href(final String ref) {
        _attrs.clear();
        _attrs.addAttribute("", "href", "", "CDATA", ref);
        return _attrs;
    }



    private static final class Topic {

        private final List<IRef> _identities;

        public Topic(final IRef identity) {
            if (identity == null) {
                throw new IllegalArgumentException("The main identity must not be null");
            }
            _identities = new ArrayList<IRef>(2);
            addIdentity(identity);
        }

        public boolean shouldMergeWith(final IRef identity) {
            final int identityType = identity.getType();
            if (identityType == IRef.SUBJECT_LOCATOR) {
                return hasIdentity(identity);
            }
            final String iri = identity.getIRI();
            for (IRef ref: _identities) {
                if (IRef.SUBJECT_LOCATOR == identityType) {
                    continue;
                }
                if (ref.getIRI().equals(iri)) {
                    return true;
                }
            }
            return false;
        }

        private void addIdentity(final IRef identity) {
            _identities.add(identity);
        }

        public boolean hasIdentity(final IRef identity) {
            return _identities.contains(identity);
        }

    }

}
