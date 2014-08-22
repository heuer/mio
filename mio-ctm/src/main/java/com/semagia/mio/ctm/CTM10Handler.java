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
package com.semagia.mio.ctm;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.semagia.mio.IRef;
import com.semagia.mio.MIOException;
import com.semagia.mio.helpers.AbstractHamsterMapHandler;
import com.semagia.mio.helpers.Ref;
import com.semagia.mio.voc.TMDM;
import com.semagia.mio.voc.XSD;

/**
 * EXPERIMENTAL {@link IMapHandler} implementation that translates all events into 
 * <a href="http://www.isotopicmaps.org/ctm/">Compact Topic Maps (CTM) 1.0</a>.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 602 $ - $Date: 2011-01-18 17:24:44 +0100 (Di, 18 Jan 2011) $
 */
public final class CTM10Handler extends AbstractHamsterMapHandler<IRef> {
    
    private static final IRef _DEFAULT_NAME = Ref.createSubjectIdentifier(TMDM.TOPIC_NAME);

    private static final Logger LOG = LoggerFactory.getLogger(CTM10Handler.class);
    
    private boolean _somethingWritten;
    private Topic _lastTopic;
    private Writer _out;
    private char[] _indent;
    private static final char[] _END_OF_STATEMENT = new char[] {';', '\n'};
    private final Map<String, String> _prefixes;
    private boolean _headerWritten;
    private final String _encoding;
    
    private String _title;
    private String _author;
    private String _license;
    private String _comment;

    /**
     * Creates a new CTM handler.
     *
     * @param out The output stream to write upon.
     * @throws IOException 
     */
    public CTM10Handler(final OutputStream out) throws IOException {
        this(out, "UTF-8");
    }

    public CTM10Handler(final OutputStream out, final String encoding) throws IOException {
        this(new OutputStreamWriter(out, encoding), encoding);
    }

    private CTM10Handler(final Writer out, final String encoding) throws IOException {
        _out = out;
        _encoding = encoding;
        _prefixes = new LinkedHashMap<String, String>();
        setIndentation(4);
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
     * @return The title or {@code null} if no title was set.
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
     * @return The author or {@code null} if no author was set.
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
     * @return The license or {@code null} if no license was set.
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
     * @return The comment or {@code null} if no comment was set.
     */
    public String getComment() {
        return _comment;
    }

    /**
     * Sets the indentation level.
     *
     * @param level Indicates how many whitespaces should be use to indentate 
     *              statements.
     */
    public void setIndentation(final int level) {
        if (_indent == null || _indent.length != level) {
            _indent = new char[level];
            Arrays.fill(_indent, ' ');
        }
    }

    /**
     * Adds a prefix to the writer.
     * <p>
     * The writer converts all locators (item identifiers, subject identifiers,
     * subject locators) into QNames which start with the provided 
     * <tt>reference</tt>.
     * </p>
     * <p>
     * I.e. if a prefix "wp" is set to "http://en.wikipedia.org/wiki", a 
     * subject identifier like "http://en.wikipedia.org/wiki/John_Lennon" is 
     * converted into a QName "wp:John_Lennon".
     * </p>
     *
     * @param prefix The prefix to add, an existing prefix with the same name
     *                  will be overridden.
     * @param reference The IRI to which the prefix should be assigned to.
     */
    public void addPrefix(String prefix, String reference) {
        if (prefix == null) {
            throw new IllegalArgumentException("The prefix must not be null");
        }
        if (!CTMUtils.isValidId(prefix)) {
            throw new IllegalArgumentException("The prefix is an invalid CTM identifier: " + prefix);
        }
        if (reference == null) {
            throw new IllegalArgumentException("The reference must not be null");
        }
        if (!CTMUtils.isValidIRI(reference)) {
            throw new IllegalArgumentException("The reference is an invalid CTM IRI: " + reference);
        }
        if (_headerWritten) {
            try {
                _finishPendingTopic();
                _writePrefix(prefix, reference);
            }
            catch (IOException ex) {
                throw new RuntimeException("Unexpected I/O error", ex);
            }
        }
    }

    /**
     * Removes a prefix / IRI pair.
     *
     * @param prefix The prefix which should be removed.
     * @throws IllegalStateException If the provided prefix was already written.
     */
    public void removePrefix(final String prefix) throws IllegalStateException {
        if (_somethingWritten) {
            throw new IllegalStateException("The prefix '" + prefix + "' cannot be removed since it was serialized already");
        }
        _prefixes.remove(prefix);
    }

    /**
     * Returns all known prefix / IRI pairs.
     *
     * @return An immutable map of prefix / IRI pairs.
     */
    public Map<String, String> getPrefixes() {
        return Collections.unmodifiableMap(_prefixes);
    }

    private void _writePrefix(final String prefix, final String reference) throws IOException {
        _out.write("%prefix " + prefix + " <" + reference + ">");
        _newline();
    }

    @Override
    public void startTopic(IRef identity) throws MIOException {
        super.startTopic(identity);
        try {
            _startTopic(identity);
        }
        catch (IOException ex) {
            throw new MIOException(ex);
        }
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.helpers.HamsterHandler#createAssociation(java.lang.Object, java.util.Collection, java.lang.Object, java.util.Collection, java.util.Collection)
     */
    @Override
    protected void createAssociation(IRef type, Collection<IRef> scope,
            IRef reifier, Collection<String> iids, Collection<IRole<IRef>> roles)
            throws MIOException {
        _somethingWritten = true;
        try {
            _finishPendingTopic();
            _newline();
            _writeTopicRef(type);
            _out.write('(');
            final Iterator<IRole<IRef>> iter = roles.iterator();
            _writeRole(iter.next());
            while(iter.hasNext()) {
                _out.write(", ");
                _newline();
                _indent(); _indent();
                _writeRole(iter.next());
            }
            _out.write(')');
            _writeScope(scope);
            _writeReifier(reifier);
            _newline();
        }
        catch (IOException ex) {
            throw new MIOException(ex);
        }
    }

    /**
     * Writes type: player (~reifier)?
     *
     * @param role The role to serialize.
     * @throws IOException In case of an I/O error.
     */
    private void _writeRole(IRole<IRef> role) throws IOException {
        _writeTopicRef(role.getType());
        _out.write(": ");
        _writeTopicRef(role.getPlayer());
        _writeReifier(role.getReifier());
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.helpers.HamsterHandler#createName(java.lang.Object, java.lang.Object, java.lang.String, java.util.Collection, java.lang.Object, java.util.Collection, java.util.Collection)
     */
    @Override
    protected void createName(IRef parent, IRef type, String value,
            Collection<IRef> scope, IRef reifier, Collection<String> iids,
            Collection<IVariant<IRef>> vars) throws MIOException {
        try {
            _startTopic(parent);
            _indent();
            _out.write("- ");
            if (!_DEFAULT_NAME .equals(type)) {
                _writeTopicRef(type);
                _out.write(": ");
            }
            CTMUtils.writeString(_out, value);
            _writeScope(scope);
            _writeReifier(reifier);
            for (IVariant<IRef> variant : vars) {
                _newline();
                _indent();
                _indent();
                _out.write("(");
                _writeValue(variant.getValue(), variant.getDatatype());
                _writeScope(variant.getScope());
                _writeReifier(variant.getReifier());
                _out.write(')');
            }
            _endOfStatement();
        }
        catch (IOException ex) {
            throw new MIOException(ex);
        }
    }

    private void _newline() throws IOException {
        _out.write('\n');
    }

    private void _endOfStatement() throws IOException {
        _out.write(_END_OF_STATEMENT);
    }

    private void _writeValue(final String value, final String datatype) throws IOException {
        if (XSD.ANY_URI.equals(datatype)) {
            _writeURI(value);
        }
        else if (XSD.STRING.equals(datatype)) {
            CTMUtils.writeString(_out, value);
        }
        else if (CTMUtils.isNativeDatatype(datatype)) {
            _out.write(value);
        }
        else {
            CTMUtils.writeString(_out, value);
            _out.write("^^");
            _writeURI(datatype); 
        }
    }

    private void _writeURI(final String reference) throws IOException {
        for (Map.Entry<String, String> entry: _prefixes.entrySet()) {
            String iri = entry.getValue();
            if (reference.startsWith(iri)) {
                String localPart = reference.substring(iri.length());
                if (CTMUtils.isValidLocalPart(localPart)) {
                    _out.write(entry.getKey());
                    _out.write(':');
                    _out.write(localPart);
                    return;
                }
            }
        }
        // No relative IRI and no QName was written, write the reference as it is
        _out.write('<' + reference + '>');
    }

    /**
     * Writes the scope.
     *
     * @param scope {@code null} or an empty collection to indicate the UCS,
     *                  otherwise a collection of themes.
     * @throws IOException In case of an error.
     */
    private void _writeScope(final Collection<IRef> scope) throws IOException {
        if (scope == null || scope.isEmpty()) {
            return;
        }
        _out.write(" @");
        final Iterator<IRef> iter = scope.iterator();
        _writeTopicRef(iter.next());
        while(iter.hasNext()) {
            _out.write(", ");
            _writeTopicRef(iter.next());
        }
    }

    private void _writeComment(final String comment) throws IOException {
        _out.write(" #( ");
        _out.write(comment);
        _out.write(" )# ");
    }

    private void _indent() throws IOException {
        _out.write(_indent);
    }

    private void _startTopic(final IRef identity) throws IOException {
        if (_lastTopic != null && _lastTopic.hasIdentity(identity)) {
            return;
        }
        final boolean shouldMerge = _lastTopic != null 
                                        && _lastTopic.shouldMergeWith(identity);
        final boolean newTopic = _lastTopic == null || (_lastTopic != null 
                                    && !(shouldMerge || _lastTopic.hasIdentity(identity)));
        _somethingWritten = true;
        if (newTopic) {
            _finishPendingTopic();
            _lastTopic = new Topic(identity);
            _newline();
            _writeTopicRef(identity);
            _newline();
        }
        else if (shouldMerge) {
            _lastTopic.addIdentity(identity);
            _writeIdentity(identity);
        }
    }

    private void _finishPendingTopic() throws IOException {
        if (_lastTopic != null) {
            _out.write('.');
            _newline();
            _lastTopic = null;
        }
    }

    private void _writeReifier(final IRef reifier) throws IOException {
        if (reifier != null) {
            _out.write(" ~ ");
            _writeTopicRef(reifier);
        }
    }

    private void _writeTopicRef(final IRef ref) throws IOException {
        final int type = ref.getType();
        if (type ==  IRef.SUBJECT_LOCATOR) {
            _out.write("= ");
        }
        else if (type == IRef.ITEM_IDENTIFIER) {
            _out.write("^ ");
        }
        _writeURI(ref.getIRI());
    }


    /* (non-Javadoc)
     * @see com.semagia.mio.helpers.AbstractHamsterMapHandler#startTopicMap()
     */
    @Override
    public void startTopicMap() throws MIOException {
        super.startTopicMap();
        try {
            _writeHeader();
        }
        catch (IOException ex) {
            throw new MIOException(ex);
        }
    }

    private void _writeHeader() throws IOException {
        _out.write("%encoding \"" + _encoding + "\"");
        _newline();
        _out.write("%version 1.0");
        _newline();
        if (_title != null || _author != null || _license != null || _comment != null) {
            _out.write("#(");
            _newline();
            if (_title != null) {
                _out.write("Title:   ");
                _out.write(_title);
                _newline();
            }
            if (_author != null) {
                _out.write("Author:  ");
                _out.write(_author);
                _newline();
            }
            if (_license != null) {
                _out.write("License: ");
                _out.write(_license);
                _newline();
            }
            if (_comment != null) {
                _newline();
                _out.write(_comment);
                _newline();
            }
            _newline();
            _out.write(")#");
            _newline();
        }
        _writePrefixes();
        _headerWritten = true;
    }

    /**
     * Writes the registered prefixes.
     *
     * @throws IOException In case of an error.
     */
    private void _writePrefixes() throws IOException {
        _newline();
        if (_prefixes.isEmpty()) {
            return;
        }
        String[] keys = _prefixes.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        for (String ident: keys) {
            _writePrefix(ident, _prefixes.get(ident));
        }
        _newline();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.helpers.AbstractHamsterMapHandler#endTopicMap()
     */
    @Override
    public void endTopicMap() throws MIOException {
        super.endTopicMap();
        try {
            _finishPendingTopic();
            _newline();
            _out.flush();
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
            _startTopic(parent);
            _indent();
            _writeTopicRef(type);
            _out.write(": ");
            _writeValue(value, datatype);
            _writeScope(scope);
            _writeReifier(reifier);
            _endOfStatement();
        }
        catch (IOException ex) {
            throw new MIOException(ex);
        }
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.helpers.HamsterHandler#handleItemIdentifier(java.lang.Object, java.lang.String)
     */
    @Override
    protected void handleItemIdentifier(final IRef topic, final String iid)
            throws MIOException {
        _writeIdentity(topic, Ref.createItemIdentifier(iid));
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.helpers.HamsterHandler#handleSubjectIdentifier(java.lang.Object, java.lang.String)
     */
    @Override
    protected void handleSubjectIdentifier(final IRef topic, final String sid)
            throws MIOException {
        _writeIdentity(topic, Ref.createSubjectIdentifier(sid));
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.helpers.HamsterHandler#handleSubjectLocator(java.lang.Object, java.lang.String)
     */
    @Override
    protected void handleSubjectLocator(final IRef topic, final String slo)
            throws MIOException {
        _writeIdentity(topic, Ref.createSubjectLocator(slo));
    }

    private void _writeIdentity(final IRef identity) throws IOException {
        _indent();
        _writeTopicRef(identity);
        _endOfStatement();
    }

    private void _writeIdentity(final IRef topic, final IRef identity) throws MIOException {
        try {
            boolean written = false;
            if (_lastTopic != null) {
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
                _startTopic(topic);
                _writeIdentity(identity);
                _lastTopic.addIdentity(identity);
            }
        }
        catch (IOException ex) {
            throw new MIOException(ex);
        }
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.helpers.HamsterHandler#handleTopicMapItemIdentifier(java.lang.String)
     */
    @Override
    protected void handleTopicMapItemIdentifier(final String iid)
            throws MIOException {
        final String msg = "Topic map item identifier <" + iid + "> is ignored";
        try {
            _newline();
            _writeComment(msg);
            _newline();
        }
        catch (IOException ex) {
            throw new MIOException(ex);
        }
        LOG.warn(msg);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.helpers.HamsterHandler#handleTopicMapReifier(java.lang.Object)
     */
    @Override
    protected void handleTopicMapReifier(final IRef reifier) throws MIOException {
        if (reifier == null) {
            return;
        }
        if (_somethingWritten) {
            final String msg = "Ignoring the topic map reifier " + reifier;
            try {
                _newline();
                _writeComment(msg);
                _newline();
            }
            catch (IOException ex) {
                throw new MIOException(ex);
            }
            LOG.warn(msg);
            return;
        }
        _somethingWritten = true;
        try {
            _out.write('~');
            _writeTopicRef(reifier);
            _newline();
        }
        catch (IOException ex) {
            throw new MIOException(ex);
        }
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.helpers.HamsterHandler#createTopicByItemIdentifier(java.lang.String)
     */
    @Override
    protected IRef createTopicByItemIdentifier(final String iid)
            throws MIOException {
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
    protected IRef createTopicBySubjectLocator(final String slo)
            throws MIOException {
        return Ref.createSubjectLocator(slo);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.helpers.HamsterHandler#handleTypeInstance(java.lang.Object, java.lang.Object)
     */
    @Override
    protected void handleTypeInstance(final IRef instance, final IRef type) throws MIOException {
        try {
            _startTopic(instance);
            _indent();
            _out.write("isa ");
            _writeTopicRef(type);
            _endOfStatement();
        }
        catch (IOException ex) {
            throw new MIOException(ex);
        }
    }


    /**
     * Keeps information about the identities of a topic.
     */
    private static final class Topic {

        private final List<IRef> _identities;

        public Topic(final IRef identity) {
            if (identity == null) {
                throw new IllegalArgumentException("The main identity must not be null");
            }
            _identities = new ArrayList<IRef>(2);
            addIdentity(identity);
        }

        /**
         * Returns if adding the {@code identity} makes this topic equal to a 
         * topic with the provided identity.
         *
         * @param identity The identity to check.
         * @return {@code true} if the topic would merge with another topic 
         *          which has the provided identity, otherwise {@code false}.
         */
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

        /**
         * Adds the provided {@code identity} to this topic.
         *
         * @param identity The identity to add.
         */
        private void addIdentity(final IRef identity) {
            _identities.add(identity);
        }

        /**
         * Returns if this topic has the provided {@code identity}.
         *
         * @param identity The identity.
         * @return {@code true} if the identity type and the IRI are equal,
         *          otherwise {@code false}.
         */
        public boolean hasIdentity(final IRef identity) {
            return _identities.contains(identity);
        }
    }

}
