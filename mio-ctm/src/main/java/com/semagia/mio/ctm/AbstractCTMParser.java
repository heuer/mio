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
import java.util.HashSet;
import java.util.Set;

import com.semagia.mio.DeserializerRegistry;
import com.semagia.mio.IDeserializer;
import com.semagia.mio.IRIContext;
import com.semagia.mio.IMapHandler;
import com.semagia.mio.IRef;
import com.semagia.mio.MIOException;
import com.semagia.mio.Source;
import com.semagia.mio.Syntax;
import com.semagia.mio.ctm.api.IPrefixListener;
import com.semagia.mio.helpers.Locator;

/**
 * Abstract CTM parser which provides some logic but no grammar.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
abstract class AbstractCTMParser {

    private static final String 
        _SYNTAX_CTM = "http://psi.topicmaps.org/iso13250/ctm",
        _SYNTAX_XTM = "http://psi.topicmaps.org/iso13250/xtm";

    protected boolean _isSubordinate;
    protected final IContentHandler _contentHandler;
    private IRIContext _iris;

    protected AbstractCTMParser(final IParseContext ctx) {
        this(new MainContentHandler(ctx));
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

    void setSubordinate(final boolean subordinate) {
        _isSubordinate = subordinate;
    }

    /**
     * Sets the input handler.
     *
     * @param handler
     */
    public void setMapHandler(final IMapHandler handler) {
        _contentHandler.getParseContext().setMapHandler(handler);
    }

    /**
     * Sets the prefix listener.
     *
     * @param listener An instance of {@link IPrefixListener} or {@code null}.
     */
    public void setPrefixListener(final IPrefixListener listener) {
        _contentHandler.getParseContext().setPrefixListener(listener);
    }

    void setIRIContext(final IRIContext ctx) {
        _iris = ctx;
    }

    IRIContext getIRIContext() {
        return _iris;
    }

    void setIncludedBy(final Set<Locator> includedBy) {
        ((MainContentHandler ) _contentHandler).setIncludedBy(includedBy);
    }

    protected final void checkVersion(final String version) throws MIOException {
        if (!"1.0".equals(version)) {
            throw new MIOException("Illegal CTM version. Expected '1.0', got '" + version + "'");
        }
    }

    protected final void registerPrefix(final String prefix, final String iri) throws MIOException {
        _contentHandler.getParseContext().registerPrefix(prefix, iri);
    }

    protected final IRef resolveQName(final String qName) throws MIOException {
        return _contentHandler.getParseContext().resolveQName(qName);
    }

    protected final IRef resolveIRI(final String iri) throws MIOException {
        return _contentHandler.getParseContext().resolveIRI(iri);
    }

    void setDocumentIRI(Locator baseIRI) throws MIOException {
        _contentHandler.getParseContext().setDocumentIRI(baseIRI);
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

    /**
     * Merges another topic map instance into the current. 
     *
     * @param iri
     * @param syntaxIRI
     * @throws IOException
     * @throws MIOException
     */
    protected void mergeIn(final String iri, final String syntaxIRI) throws IOException, MIOException {
        final IParseContext ctx = _contentHandler.getParseContext();
        if (_SYNTAX_CTM == syntaxIRI) {
            _mergeInCTM(null, iri, null);
        }
        else if (_SYNTAX_XTM == syntaxIRI){
            final Locator docIRI = ctx.resolveLocator(iri);
            if (_iris.containsIRI(docIRI.toExternalForm())) {
                return;
            }
            _iris.addIRI(docIRI.toExternalForm());
            final IDeserializer deser = DeserializerRegistry.getInstance().createDeserializer(Syntax.XTM);
            if (deser == null) {
                throw new MIOException("Unknown syntax '" + syntaxIRI + "'");
            }
            deser.setMapHandler(ctx.getMapHandler());
            deser.setSubordinate(true);
            deser.setIRIContext(_iris);
            deser.parse(new Source(docIRI.toExternalForm()));
        }
        else {
            throw new MIOException("Unknown syntax '" + syntaxIRI + "'");
        }
    }

    protected final void include(final String iri) throws IOException, MIOException {
        final IParseContext ctx = _contentHandler.getParseContext();
        final Locator loc =  _contentHandler.getParseContext().resolveLocator(iri);
        final Set<Locator> includedBy = ((MainContentHandler) _contentHandler).getIncludedBy();
        if (includedBy != null && includedBy.contains(loc)) {
            return;
        }
        final Set<Locator> newIncludedBy = includedBy != null ? new HashSet<Locator>(includedBy) : new HashSet<Locator>(2);
        newIncludedBy.add(ctx.getDocumentIRI());
        _mergeInCTM(new IncludeParseContext(ctx), iri, newIncludedBy);
    }

    /**
     * Merges another topic map instance into the current.
     *
     * @param iri
     * @param included
     * @throws IOException
     * @throws MIOException
     */
    private final void _mergeInCTM(IParseContext ctx, final String iri, final Set<Locator> included) throws IOException, MIOException {
        final Locator docIRI = _contentHandler.getParseContext().resolveLocator(iri);
        if (_iris.containsIRI(docIRI.toExternalForm())) {
            return;
        }
        _iris.addIRI(docIRI.toExternalForm());
        final CTMDeserializer deser = new CTMDeserializer();
        //deser.setProperty("http://psi.semagia.com/mio/property/ctm/prefix-listener", _listener);
        deser.setParseContext(ctx);
        deser.setMapHandler(_contentHandler.getParseContext().getMapHandler());
        deser.setSubordinate(true);
        deser.setIRIContext(_iris);
        if (included != null) {
            deser.setIncludedBy(included);
        }
        deser.parse(new Source(docIRI.toExternalForm()));
    }


    protected final IReference createTopic(final String name) throws MIOException {
        final IReference ref = name == null ? _contentHandler.startTopic() : _contentHandler.startTopic(name);
        _contentHandler.endTopic();
        return ref;
    }

}
