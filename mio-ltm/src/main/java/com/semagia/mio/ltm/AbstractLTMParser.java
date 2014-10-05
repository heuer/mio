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
package com.semagia.mio.ltm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.semagia.mio.IRIContext;
import com.semagia.mio.DeserializerRegistry;
import com.semagia.mio.IDeserializer;
import com.semagia.mio.IMapHandler;
import com.semagia.mio.IRef;
import com.semagia.mio.MIOException;
import com.semagia.mio.Source;
import com.semagia.mio.Syntax;
import com.semagia.mio.helpers.Literal;
import com.semagia.mio.helpers.Locator;
import com.semagia.mio.helpers.QName;
import com.semagia.mio.helpers.Ref;
import com.semagia.mio.helpers.SimpleMapHandler;
import com.semagia.mio.ltm.api.IPrefixListener;

import com.semagia.mio.voc.TMDM;
import com.semagia.mio.voc.XTM10;

/**
 * Abstract LTM parser which provides just logic, but is not grammar-aware.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
abstract class AbstractLTMParser {

    protected SimpleMapHandler _handler;

    protected static final IRef 
        _DISPLAY_NAME = Ref.createSubjectIdentifier(XTM10.DISPLAY),
        _XTM_SORT = Ref.createSubjectIdentifier(XTM10.SORT),
        _TMDM_SORT = Ref.createSubjectIdentifier(TMDM.SORT),
        _TOPIC_NAME = Ref.createSubjectIdentifier(TMDM.TOPIC_NAME),
        _ROLE = Ref.createSubjectIdentifier(XTM10.ROLE);

    private Map<String, String> _sidPrefixes;
    private Map<String, String> _sloPrefixes;

    private Collection<Locator> _included;

    private boolean _baseLocatorSeen;
    private boolean _ignoreMergemap;
    private boolean _ignoreInclude;
    protected boolean _legacy;
    private Locator _docLocator;
    private Locator _baseLocator;
    private IRIContext _context;
    private boolean _isSubordinate;
    private IPrefixListener _listener;
    protected IRef _sort;

    public AbstractLTMParser() {
        _sidPrefixes = new HashMap<String, String>();
        _sloPrefixes = new HashMap<String, String>();
        _context = new IRIContext();
        _included = new ArrayList<Locator>();
        _sort = _TMDM_SORT;
    }

    /**
     * Sets the input handler.
     *
     * @param handler
     */
    public void setMapHandler(final IMapHandler handler) {
        _handler = SimpleMapHandler.create(handler);
    }

    /**
     * Sets the prefix listener.
     *
     * @param listener An instance of {@link IPrefixListener} or {@code null}.
     */
    public void setPrefixListener(final IPrefixListener listener) {
        _listener = listener;
    }

    public void setDocumentIRI(final String docIRI) {
        _baseLocator = Locator.create(docIRI);
        _docLocator = _baseLocator;
        _context.addIRI(_docLocator.getReference());
    }

    public void setIncludedBy(final Collection<Locator> included) {
        if (_included.size() > 0) {
            throw new IllegalStateException("Internal error: The included IRIs are already set");
        }
        // Shared
        _included = included;
    }

    public IRIContext getContext() {
        return _context;
    }

    public void setContext(final IRIContext ctx) {
        _context = ctx;
    }

    public void setLegacyMode(final boolean enableLegacy) {
        _legacy = enableLegacy;
        _sort = enableLegacy ? _XTM_SORT : _TMDM_SORT;
    }

    public void setIgnoreMergemap(final boolean ignore) {
        _ignoreMergemap = ignore;
    }

    public void setIgnoreInclude(final boolean ignore) {
        _ignoreInclude = ignore;
    }

    public void setSubordinate(final boolean subordinate) {
        _isSubordinate = subordinate;
    }

    /**
     * Registeres the base locator.
     * 
     * This method is called if the <code>#BASEURI</code> directive is detected.
     *
     * @param iri The base locator.
     * @throws MIOException If a base locator was already registered.
     */
    protected final void _registerBaseLocator(final String iri) throws MIOException {
        if (_baseLocatorSeen) {
            _reportError("The base locator was already set");
        }
        _baseLocatorSeen = true;
        _baseLocator = _resolveLocator(iri);
        if (_listener != null) {
            _listener.handleBaseLocator(iri);
        }
    }

    /**
     * Registeres a prefix for a subject identifier.
     *
     * @param prefix The prefix
     * @param iri The IRI.
     * @throws MIOException If the prefix is already registered (either as 
     *          subject identifier or subject locator).
     */
    protected final void _registerSubjectIdentifierPrefix(final String prefix, final String iri) throws MIOException {
        _checkNotRegistered(prefix);
        _sidPrefixes.put(prefix, iri);
        if (_listener != null) {
            _listener.handleSubjectIdentifierNamespace(prefix, iri);
        }
    }

    /**
     * Registeres a prefix for a subject locator.
     *
     * @param prefix The prefix
     * @param iri The IRI.
     * @throws MIOException If the prefix is already registered (either as 
     *          subject identifier or subject locator).
     */
    protected final void _registerSubjectLocatorPrefix(final String prefix, final String iri) throws MIOException {
        _checkNotRegistered(prefix);
        _sloPrefixes.put(prefix, iri);
        if (_listener != null) {
            _listener.handleSubjectLocatorNamespace(prefix, iri);
        }
    }

    /**
     * Checks if the prefix is already bound to an IRI. 
     *
     * @param prefix The prefix
     * @throws MIOException If the prefix is already used as subject identifier
     *          or subject locator prefix.
     */
    private void _checkNotRegistered(final String prefix) throws MIOException {
        if (_sidPrefixes.containsKey(prefix) || _sloPrefixes.containsKey(prefix)) {
            _reportError("The prefix '" + prefix + "' is already registered.");
        }
    }

    /**
     * Checks, if the LTM version is supported.
     *
     * @param version The version.
     * @throws MIOException If the version is not supported.
     */
    protected final void _checkVersion(final String version) throws MIOException {
        if (!"1.3".equals(version)) {
            _reportError("Invalid version. Expected '1.3', got '" + version + "'");
        }
    }

    protected final String _resolveIRIAgainstBase(final String iri) {
        return _baseLocator.resolve(iri).getReference();
    }

    protected final String _resolveIRI(final String iri) {
        return _resolveLocator(iri).getReference();
    }

    private final Locator _resolveLocator(final String iri) {
        return iri.charAt(0) == '#' ? _docLocator.resolve(iri)
                                    : _baseLocator.resolve(iri);
    }

    /**
     * Creates a topic by a QName.
     * <p>
     * The topic is either resolved by a subject identifier or by a subject
     * locator (depends on the type of the prefix). 
     * </p>
     *
     * @param name A QName (a string with a colon).
     * @return A topic.
     * @throws MIOException If the prefix of the QName wasn't registered
     *          previously.
     */
    protected final IRef _createTopicByQName(final String name) throws MIOException {
        final QName qName = QName.create(name);
        String iri = _sidPrefixes.get(qName.getPrefix());
        if (iri != null) {
            return Ref.createSubjectIdentifier(_resolveIRI(iri + qName.getLocal()));
        }
        iri = _sloPrefixes.get(qName.getPrefix());
        if (iri != null) {
            return Ref.createSubjectLocator(_resolveIRI(iri + qName.getLocal()));
        }
        _reportError("The prefix '" + qName.getPrefix() + "' is not registered");
        return null;
    }

    /**
     * Creates a topic by an item identifier.
     * <p>
     * If the LTM instance was included from another LTM document, the topic
     * gets several item identifiers.
     * </p>
     * 
     * @param identifier A local identifier.
     * @return A topic.
     * @throws MIOException 
     */
    protected final IRef _createTopic(final String identifier) throws MIOException {
        String id = "#" + identifier;
        IRef ref = Ref.createItemIdentifier(_resolveIRI(id));
        _handler.startTopic(ref);
        for (Locator loc : _included) {
            _handler.itemIdentifier(loc.resolve(id).getReference());
        }
        _handler.endTopic();
        return ref;
    }

    protected final Literal _createIRI(final String reference) {
        return Literal.createIRI(_resolveIRI(reference));
    }

    /**
     * Returns a literal with datatype <code>xsd:string</code>.
     * <p>
     * Quote stuffing and unicode escaping is supported.
     * </p>
     *
     * @param value The string value.
     * @return A literal with the provided string value and datatype <code>xsd:string</code>.
     */
    protected final Literal _createString(final String value) {
        return _createString(value, true);
    }

    /**
     * Returns a literal with datatype <code>xsd:string</code>.
     * <p>
     * Unicode escaping is supported, but no quote stuffing is performed.
     * </p>
     * @param value The string value.
     * @return A literal with the provided string value and datatype <code>xsd:string</code>.
     */
    protected final Literal _createData(final String value) {
        return _createString(value, false);
    }

    /**
     * Returns a literal with the datatype <code>xsd:string</code>.
     * <p>
     * Unicode escape sequences are supported.
     * </p>
     *
     * @param value The string value.
     * @param removeStuffing If set to <code>true</code>, occurrences of
     *          <code>""</code> are replaced by <code>"</code>.
     * @return The created literal.
     */
    private final Literal _createString(final String value, final boolean removeStuffing) {
        return Literal.createString(_unescapeUnicode(removeStuffing 
                                                ? value.replace("\"\"", "\"") 
                                                : value));
    }

    /**
     * Unescapes a given source string for unicode characters. Converts
     * sequences of the form [BACKSLASH]uXXXX(XX) where the Xs are hexadecimal
     * digits and [BACKSLASH] is the actual character '\', and the last two
     * digits (in brackets) are optional. E.g. converts \0041 to 'A' and \005c
     * to '\'. If the character is outside the range 0hex..FFFFhex, it is
     * converted to a surrogate pair, that represents the 'big' character.
     */
    // Taken from Ontopia
    private static String _unescapeUnicode(final String source) {
        String retVal = "";
        int charactersProcessed = 0;
        int i = source.indexOf("\\u");
        // For each potential unicode escape sequence:
        while (i != -1) {
            // Characters upto i contain no escaping. Add these as they are.
            retVal += source.substring(charactersProcessed, i);
            charactersProcessed = i;

            int unicodeValue = 0; // Used for String to hexadecimal conversion.
            int j;
            // Match up-to six hexadecimal digits, calculating the unicode char
            // value.
            for (j = i + 2; j < i + 8 && j < source.length(); j++) {
                int charValue = _hexValue(source.charAt(j));
                if (charValue == -1)
                    break;
                unicodeValue = unicodeValue * 16 + charValue;
            }

            // j is the index position in source of the first
            // non-hexadecimal-digit.
            // If escape sequence is valid (contains at least 4 hexadecimal
            // digits.
            if (j - i >= 6) {
                // Create the appropriate unicode character.
                if (unicodeValue < 65536) {// 65536 == 10000hex
                    // Add the unicode character as it is.
                    retVal += (char) unicodeValue;
                }
                else {
                    // Create two surrogates to represent one big character.
                    // Necessary for characters >= 10000hex (65536).

                    // Calculation of the surrogates as follows:
                    // Let u = unicodeValue, x = highSurrogate, y =
                    // lowSurrogate, then:
                    // u == 10000hex + 400hex(x - D800hex) + y - DC00hx
                    // i.e. u == 65536 + 1024(x - 55296) + y - 56320
                    // i.e. u == 1024x + y - 56613888
                    // and range(y) is (DC00hex, DFFFhex), i.e. (56320, 57343)
                    // ymin - ymax == 1023 < 1024
                    // Therefore x == rounddown((u - ymin + 56613888) / 1024)
                    // I.e. x == rounddown((u + 56557568) / 1024)
                    // And hence y == u + 56613888 - 1024x
                    int highSurrogate = (unicodeValue + 56557568) / 1024;
                    int lowSurrogate = unicodeValue + 56613888 - 1024
                            * highSurrogate;
                    retVal += (char) highSurrogate;
                    retVal += (char) lowSurrogate;
                }
            }
            else {
                // Move past the string, which was not a valid unicode sequence.
                retVal += source.substring(charactersProcessed, j);
            }
            charactersProcessed = j;
            i = source.indexOf("\\u", charactersProcessed);
        }
        retVal += source.substring(charactersProcessed);
        return retVal;
    }

    /**
     * @return the integer value of a hexadecimal character (0-9 | A-F | a-f).
     */
    // Taken from Ontopia
    private static int _hexValue(final char source) {
        if (_in('a', source, 'f'))
            return 10 + source - 'a';
        else if (_in('A', source, 'F'))
            return 10 + source - 'A';
        else if (_in('0', source, '9'))
            return source - '0';
        else
            return -1;
    }

    /**
     * @return true iff source is in the interval min..max (inclusive).
     */
    // Taken from Ontopia
    private static boolean _in(final int min, final int source, final int max) {
        return min <= source && source <= max;
    }

    /**
     * Includes another LTM instance.
     * <p>
     * The LTM document is only included iff it was not loaded previously.
     * </p>
     *
     * @param iri The IRI to retrieve the LTM document from.
     * @throws IOException If an I/O error occurs.
     * @throws MIOException If a syntax error etc. happens.
     */
    protected final void _include(final String iri) throws IOException, MIOException {
        if (!_ignoreInclude) {
            final Collection<Locator> included = new ArrayList<Locator>(_included);
            included.add(_docLocator);
            _mergeInLTM(iri, included);
        }
    }

    /**
     * Merges a LTM instance into this topic map.
     *
     * @param iri
     * @throws IOException
     * @throws MIOException
     */
    protected final void _mergeInLTM(final String iri) throws IOException, MIOException {
        if (!_ignoreMergemap) {
            _mergeInLTM(iri, Collections.<Locator>emptyList());
        }
    }

    /**
     * Merges another topic map instance into the current.
     *
     * @param iri
     * @param included
     * @throws IOException
     * @throws MIOException
     */
    private final void _mergeInLTM(final String iri, final Collection<Locator> included) throws IOException, MIOException {
        final Locator docIRI = _resolveLocator(iri);
        if (_context.containsIRI(docIRI.toExternalForm())) {
            return;
        }
        _context.addIRI(docIRI.toExternalForm());
        final LTMDeserializer deser = new LTMDeserializer();
        deser.setMapHandler(_handler);
        deser.setSubordinate(true);
        deser.setPrefixListener(_listener);
        deser.setIRIContext(_context);
        deser.setIncludedBy(included);
        deser.parse(new Source(docIRI.toExternalForm()));
    }

    /**
     * Merges another topic map instance into the current. 
     *
     * @param iri
     * @param syntax
     * @throws IOException
     * @throws MIOException
     */
    protected final void _mergeIn(final String iri, final String syntax) throws IOException, MIOException {
        if (_ignoreMergemap) {
            return;
        }
        if ("ltm".equalsIgnoreCase(syntax)) {
            _mergeInLTM(iri);
        }
        else {
            final Locator docIRI = _resolveLocator(iri);
            if (_context.containsIRI(docIRI.toExternalForm())) {
                return;
            }
            _context.addIRI(docIRI.toExternalForm());
            final IDeserializer deser = DeserializerRegistry.getInstance().createDeserializer(Syntax.valueOf(syntax));
            if (deser == null) {
                _reportError("Unknown syntax '" + syntax + "'");
            }
            deser.setMapHandler(_handler);
            deser.setSubordinate(true);
            deser.setIRIContext(_context);
            deser.parse(new Source(docIRI.toExternalForm()));
        }
    }

    /**
     * Assigns an item identifier to the topic map iff the handler is not
     * in subordinate mode.
     */
    protected void _topicMapItemIdentifier(final String id) throws MIOException {
        if (!_isSubordinate) {
            _handler.itemIdentifier(_resolveIRI("#"+id));
        }
    }

    /**
     * Assigns a topic map reifier to the topic map iff the handler is not
     * in subordinate mode.
     */
    protected void _topicMapReifier(final String reifierId) throws MIOException {
        if (_legacy || !_isSubordinate) {
            _reifier(reifierId);
        }
        else {
            final IRef reifier = Ref.createItemIdentifier(_resolveIRI("#"+reifierId));
            _handler.startTopic(reifier);
            _handler.endTopic();
        }
    }

    /**
     * Handles reification. If the parser is in the "legacy" mode, the
     * parser creates an item identifier and subject identifier in the form
     * of "#--reified--<reifierId>". In non-legacy mode, these IRIs are
     * not created and a "reifier" event is issued. 
     *
     * @param reifierId The id of the reifying topic or {@code null}.
     * @throws MIOException In case of an error.
     */
    protected void _reifier(final String reifierId) throws MIOException {
        if (reifierId == null) {
            return;
        }
        final IRef reifier = Ref.createItemIdentifier(_resolveIRI("#"+reifierId));
        if (_legacy) {
            final String id = "#--reified--" + reifierId;
            final String iri = _resolveIRIAgainstBase(id);
            _handler.itemIdentifier(iri);
            _handler.startReifier();
            _handler.startTopic(reifier);
            _handler.subjectIdentifier(iri);
            _handler.endTopic();
            _handler.endReifier();
        }
        else {
            _handler.reifier(reifier);
        }
    }

    private static void _reportError(final String msg) throws MIOException {
        throw new MIOException(msg);
    }
}
