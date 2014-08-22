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
package com.semagia.mio.rdf.crtm;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.semagia.mio.MIOException;
import com.semagia.mio.helpers.Locator;
import com.semagia.mio.helpers.QName;
import com.semagia.mio.rdf.api.IMappingHandler;
import com.semagia.mio.rdf.api.IMappingPrefixListener;
import com.semagia.mio.utils.BOMInputStream;

/**
 * Abstract class which is not grammar-aware but provides the logic.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 552 $ - $Date: 2010-09-26 16:56:55 +0200 (So, 26 Sep 2010) $
 */
abstract class AbstractCRTMParser {

    protected IMappingHandler _handler;
    private final Map<String, Locator> _prefixes;
    private final Locator _docIRI;
    private Collection<Locator> _included;
    private Boolean _globalLang2Scope;
    private IMappingPrefixListener _listener;

    protected AbstractCRTMParser(final String iri) {
        _handler = null;
        _prefixes = new HashMap<String, Locator>();
        _docIRI = Locator.create(iri);
        _included = new ArrayList<Locator>();
        _globalLang2Scope = Boolean.FALSE;
        _listener = null;
    }

    void setMappingHandler(IMappingHandler handler) {
        _handler = handler;
    }

    void setPrefixListener(IMappingPrefixListener listener) {
        _listener = listener;
    }

    protected final void setIncludedBy(final Collection<Locator> included) {
        if (_included.size() > 0) {
            throw new IllegalStateException("Internal error: The included IRIs are already set");
        }
        _included = included;
    }

    /**
     * Registeres a prefix mapping.
     *
     * @param ident The identifier.
     * @param iri The IRI.
     * @throws MIOException If the identifier is already assigned to a different IRI.
     */
    protected final void registerPrefix(final String ident, final String iri) throws MIOException {
        final Locator existing = _prefixes.put(ident, _makeLocator(iri));
        if (existing != null && !existing.equals(_prefixes.get(ident))) {
            throw new MIOException("The prefix '" + ident + "' is already bound to <" + existing.getReference() + ">");
        }
        if (_listener != null) {
            _listener.handleNamespace(ident, iri);
        }
    }

    /**
     * 
     *
     * @param iri
     * @return
     * @throws MIOException
     */
    protected final String registerAnonymousPrefix(final String iri) throws MIOException {
        final Locator loc = _makeLocator(iri);
        String ident = loc.toExternalForm();
        if (ident.endsWith("#")) { 
            ident = ident.substring(0, ident.length()-1);
        }
        if (ident.endsWith("/")) {
            ident = ident.substring(0, ident.length()-1);
        }
        final int lastSlash = ident.lastIndexOf('/');
        if (lastSlash != -1) {
            ident = ident.substring(lastSlash+1);
        }
        Locator existingLocator = _prefixes.get(ident);
        if (existingLocator != null) {
            String newPrefix = ident;
            int cnt = 0;
            while (existingLocator != null && !existingLocator.equals(loc)) {
                newPrefix = ident + cnt;
                existingLocator = _prefixes.get(newPrefix);
            }
            ident = newPrefix;
        }
        registerPrefix(ident, loc.toExternalForm());
        return ident;
    }

    /**
     * Sets the global handling of the RDF language tag.
     *
     * @param lang2Scope A boolean, not {@code null}.
     */
    protected final void setConvertLanguageToScope(final Boolean lang2Scope) {
        if (lang2Scope == null) {
            throw new IllegalArgumentException("The value of the language to scope mapping must not be null");
        }
        _globalLang2Scope = lang2Scope;
    }

    /**
     * Returns the provided {@code iri} resolved against the base locator.
     *
     * @param iri An IRI.
     * @return An absolute IRI.
     */
    private Locator _makeLocator(final String iri) {
        return _docIRI.resolve(iri);
    }

    /**
     * Includes another CRTM instance into this instance.
     *
     * @param iri The IRI of the other CRTM instance.
     * @throws IOException In case of an I/O error.
     * @throws MIOException In case of an other error.
     */
    protected final void include(final String iri) throws IOException, MIOException {
        final Locator includeIRI = _docIRI.resolve(iri);
        if (!_included.contains(includeIRI)) {
            _included.add(includeIRI);
            final BOMInputStream stream = new BOMInputStream(new URL(includeIRI.toExternalForm()).openStream(), "utf-8");
            final Reader reader = new InputStreamReader(stream, stream.getEncoding());
            final CRTMParser parser = new CRTMParser(includeIRI.toExternalForm());
            parser.setMappingHandler(_handler);
            parser.setPrefixListener(_listener);
            parser.setIncludedBy(_included);
            parser.parse(reader);
        }
    }

    /**
     * Returns an IRI for the provided QName.
     *
     * @param name The QName.
     * @return An IRI.
     * @throws MIOException In case the QName's prefix is unknown.
     */
    protected final String resolveQName(final String name) throws MIOException {
        final QName qName = QName.create(name);
        return resolveQName(qName.getPrefix(), qName.getLocal());
    }

    /**
     * Returns an IRI for the provided QName.
     *
     * @param prefix The QName's prefix.
     * @param local The local part of the QName.s
     * @return An IRI.
     * @throws MIOException In case the QName's prefix is unknown.
     */
    protected final String resolveQName(final String prefix, final String local) throws MIOException {
        final Locator loc = _prefixes.get(prefix);
        if (loc == null) {
            throw new MIOException("The prefix '" + prefix + "' is not registered");
        }
        return resolveIRI(loc.toExternalForm() + local);
    }

    /**
     * Returns the provided {@code iri} resolved against the base locator.
     *
     * @param iri An IRI.
     * @return An absolute IRI.
     */
    protected final String resolveIRI(final String iri) {
        return _makeReference(_makeLocator(iri));
    }

    /**
     * Converts a {@link Locator} instance into an {@link String}.
     *
     * @param loc The locator.
     * @return The reference.
     */
    private String _makeReference(final Locator loc) {
        return loc.toExternalForm();
    }

    /**
     * Returns if the RDF language tag should be converted into Topic Maps scope.
     * 
     * If {@code lang2scope} is {@code null}, the global setting will be returned
     * (false by default). 
     *
     * @param lang2scope The local setting or {@code null} to evaluate the global setting.
     * @return If the language tag should be converted into a scope theme.
     */
    protected final boolean getLang2ScopeSetting(final Boolean lang2scope) {
        return lang2scope != null ? lang2scope.booleanValue() 
                                  : _globalLang2Scope.booleanValue();
    }

}
