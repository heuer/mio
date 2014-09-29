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
package com.semagia.mio.ctm;

import java.io.IOException;
import java.util.List;

import com.semagia.mio.Context;
import com.semagia.mio.IMapHandler;
import com.semagia.mio.MIOException;
import com.semagia.mio.ctm.api.IPrefixListener;
import com.semagia.mio.helpers.Locator;

/**
 * Abstract CTM parser which provides some logic but no grammar.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
abstract class AbstractCTMParser {

    protected boolean _isSubordinate;

    protected final IContentHandler _contentHandler;

    protected AbstractCTMParser() {
        this(new MainContentHandler());
    }

    protected AbstractCTMParser(final IContentHandler handler) {
        if (handler == null) {
            throw new IllegalArgumentException("The content handler must not be null");
        }
        _contentHandler = handler;
    }

    IContentHandler getContentHandler() {
        return _contentHandler;
    }

    public void setSubordinate(final boolean subordinate) {
        _contentHandler.getEnvironment().setSubordinate(subordinate);
    }

    protected final boolean isSubordinate() {
        return _contentHandler.getEnvironment().isSubordinate();
    }

    /**
     * Sets the input handler.
     *
     * @param handler
     */
    public void setMapHandler(final IMapHandler handler) {
        _contentHandler.getEnvironment().setMapHandler(handler);
    }

    /**
     * Sets the prefix listener.
     *
     * @param listener An instance of {@link IPrefixListener} or {@code null}.
     */
    public void setPrefixListener(final IPrefixListener listener) {
        _contentHandler.getEnvironment().setPrefixListener(listener);
    }

    public void setContext(final Context ctx) {
        _contentHandler.getEnvironment().setContext(ctx);
    }

    public Context getContext() {
        return _contentHandler.getEnvironment().getContext();
    }

    public void setIncludedBy(final List<Locator> includedBy) {
        _contentHandler.getEnvironment().setIncludedBy(includedBy);
    }

    public void setWildcardCounter(int counter) {
        _contentHandler.getEnvironment().setWildcardCounter(counter);
    }

    public int getWildcardCounter() {
        return _contentHandler.getEnvironment().getWildcardCounter();
    }

    protected final void checkVersion(final String version) throws MIOException {
        if (!"1.0".equals(version)) {
            throw new MIOException("Illegal CTM version. Expected '1.0', got '" + version + "'");
        }
    }

    protected final void registerPrefix(final String prefix, final String iri) throws MIOException {
        _contentHandler.getEnvironment().registerPrefix(prefix, iri);
    }

    protected final IReference resolveQName(final String qName) throws MIOException {
        return _contentHandler.getEnvironment().resolveQName(qName);
    }

    protected final IReference resolveIRI(final String iri) throws MIOException {
        return _contentHandler.getEnvironment().resolveIRI(iri);
    }

    void setDocumentIRI(String baseIRI) {
        _contentHandler.getEnvironment().setDocumentIRI(baseIRI);
    }

    /**
     * Unescapes a CTM string if necessary (i.e. if Unicode escape sequences
     * are found).
     * 
     *
     * @param value The string to unescape.
     * @return An unescaped string.
     */
    protected static final String unescapeString(final String value) throws MIOException {
        try {
            return CTMUtils.unescapeString(value);
        }
        catch (IllegalArgumentException ex) {
            throw new MIOException(ex);
        }
    }

    protected final void include(final String iri) throws IOException, MIOException {
        _contentHandler.getEnvironment().include(iri);
    }


    /**
     * Merges another topic map instance into the current. 
     *
     * @param iri
     * @param syntaxIRI
     * @throws IOException
     * @throws MIOException
     */
    protected final void mergeIn(final String iri, final String syntaxIRI) throws IOException, MIOException {
        _contentHandler.getEnvironment().mergeIn(iri, syntaxIRI);
    }

}
