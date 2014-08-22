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
package com.semagia.mio.rdf.sesame;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.helpers.RDFHandlerBase;

import com.semagia.mio.MIOException;
import com.semagia.mio.MIOParseException;
import com.semagia.mio.Source;
import com.semagia.mio.rdf.api.IMapper;
import com.semagia.mio.rdf.api.IMappingHandler;
import com.semagia.mio.rdf.api.IMappingPrefixListener;
import com.semagia.mio.rdf.api.IMappingReader;
import com.semagia.mio.voc.RDF2TM;

/**
 * Mapping reader which reads a mapping from a RDF source.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 552 $ - $Date: 2010-09-26 16:56:55 +0200 (So, 26 Sep 2010) $
 */
final class RDFSourceMappingReader implements IMappingReader {

    private static final int
        UNKNOWN = 0,
        NAME = 1,
        OCCURRENCE = 2,
        ASSOCIATION = 3,
        TYPE_INSTANCE = 4;

    private final RDFParser _parser;
    private IMappingHandler _handler;
    private final Map<String, Mapping> _mappings;
    private IMappingPrefixListener _listener;

    public RDFSourceMappingReader(final RDFParser parser) {
        _parser = parser;
        _handler = null;
        _mappings = new HashMap<String, Mapping>();
        _listener = null;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.rdf.api.IMappingReader#getMappingHandler()
     */
    @Override
    public IMappingHandler getMappingHandler() {
        return _handler;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.rdf.api.IMappingReader#setMappingHandler(com.semagia.mio.rdf.api.IMappingHandler)
     */
    @Override
    public void setMappingHandler(final IMappingHandler handler) {
        _handler = handler;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.rdf.api.IMappingReader#setPrefixListener(com.semagia.mio.rdf.api.IMappingPrefixListener)
     */
    @Override
    public void setPrefixListener(IMappingPrefixListener listener) {
        _listener = listener;
    }

    /**
     * Returns either an existing Mapping or creates a Mapping for
     * the provided predicate IRI.
     *
     * @param predicate The predicate.
     * @return A Mapping, never null.
     */
    private Mapping getMapping(final String predicate) {
        Mapping mapping = _mappings.get(predicate);
        if (mapping == null) {
            mapping = new Mapping();
            _mappings.put(predicate, mapping);
        }
        return mapping;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.rdf.IMappingReader#read()
     */
    @Override
    public void read(final Source source) throws IOException, MIOException {
        //TODO: check for characterstream / bytestream
        _parser.setRDFHandler(new RDFStatementHandler());
        final InputStream stream = new BufferedInputStream(new URL(source.getIRI()).openStream());
        _handler.start();
        try {
            _parser.parse(stream, source.getBaseIRI());
        }
        catch (OpenRDFException ex) {
            if (ex.getCause() instanceof MIOException) {
                throw (MIOException) ex.getCause();
            }
            if (ex instanceof RDFParseException) {
                throw new MIOParseException(ex, ((RDFParseException) ex).getLineNumber(), ((RDFParseException) ex).getColumnNumber());
            }
            throw new MIOException(ex);
        }
        finally {
            if (stream != null) {
                stream.close();
            }
        }
        _buildMapping();
        _handler.end();
        _mappings.clear();
    }

    /**
     * Converts the collected mapping information into {@link IMapper} 
     * instances.
     *
     * @throws MIOException In case of an error.
     */
    private void _buildMapping() throws MIOException {
        for (Entry<String, Mapping> entry: _mappings.entrySet()) {
            Mapping mapping = entry.getValue();
            String predicate = entry.getKey();
            switch (mapping.kind) {
                case NAME: 
                    _handler.handleName(predicate, _toArray(mapping.scope), mapping.type, false);
                    break;
                case OCCURRENCE:
                    _handler.handleOccurrence(predicate, _toArray(mapping.scope), mapping.type, false);
                    break;
                case TYPE_INSTANCE:
                    _handler.handleInstanceOf(predicate, _toArray(mapping.scope));
                    break;
                case ASSOCIATION: {
                    _handler.handleAssociation(predicate, mapping.subject, mapping.object, _toArray(mapping.scope), mapping.type);
                    break;
                }
                default:
                    throw new MIOException("Invalid mapping for <" + entry.getKey() + ">");
            }
        }
    }

    private static String[] _toArray(Collection<String> scope) {
        return scope == null ? null : scope.toArray(new String[scope.size()]);
    }

    /**
     * RDFHandler which reports each statement to the store.
     */
    private final class RDFStatementHandler extends RDFHandlerBase {

        /* (non-Javadoc)
         * @see org.openrdf.rio.helpers.RDFHandlerBase#handleNamespace(java.lang.String, java.lang.String)
         */
        @Override
        public void handleNamespace(String prefix, String uri)
                throws RDFHandlerException {
            super.handleNamespace(prefix, uri);
            if (_listener != null) {
                _listener.handleNamespace(prefix, uri);
            }
        }

        /* (non-Javadoc)
         * @see org.openrdf.rio.helpers.RDFHandlerBase#handleStatement(org.openrdf.model.Statement)
         */
        @Override
        public void handleStatement(final Statement stmt) throws RDFHandlerException {
            final String predicate = stmt.getPredicate().stringValue();
            if (!predicate.startsWith("http://psi.ontopia.net/rdf2tm/#")) {
                return;
            }
            final String subject = stmt.getSubject().stringValue();
            if (!(stmt.getObject() instanceof URI)) {
                throw new RDFHandlerException(new MIOException("The object of the RDF mapping must be an IRI"));
            }
            final String object = stmt.getObject().stringValue();
            if (RDF2TM.MAPS_TO.equals(predicate)) {
                if (RDF2TM.BASENAME.equals(object)) {
                    getMapping(subject).kind = NAME;
                }
                else if (RDF2TM.OCCURRENCE.equals(object)) {
                    getMapping(subject).kind = OCCURRENCE;
                }
                else if (RDF2TM.INSTANCE_OF.equals(object)) {
                    getMapping(subject).kind = TYPE_INSTANCE;
                }
                else if (RDF2TM.ASSOCIATION.equals(object)) {
                    getMapping(subject).kind = ASSOCIATION;
                }
                else if (RDF2TM.SUBJECT_IDENTIFIER.equals(object)) {
                    try {
                        _handler.handleSubjectIdentifier(subject);
                    }
                    catch (MIOException ex) {
                        throw new RDFHandlerException(ex);
                    }
                }
                else if (RDF2TM.SUBJECT_LOCATOR.equals(object)) {
                    try {
                        _handler.handleSubjectLocator(subject);
                    }
                    catch (MIOException ex) {
                        throw new RDFHandlerException(ex);
                    }
                }
                else if (RDF2TM.SOURCE_LOCATOR.equals(object)) {
                    try {
                        _handler.handleItemIdentifier(subject);
                    }
                    catch (MIOException ex) {
                        throw new RDFHandlerException(ex);
                    }
                }
                else {
                    throw new RDFHandlerException(new MIOException("Unknown object of a rtm:maps-to mapping. Object: <" + object + ">"));
                }
            }
            else if (RDF2TM.TYPE.equals(predicate)) {
                getMapping(subject).type = object;
            }
            else if (RDF2TM.IN_SCOPE.equals(predicate)) {
                Mapping mapping = getMapping(subject);
                if (mapping.scope == null) {
                    mapping.scope = new ArrayList<String>();
                }
                mapping.scope.add(object);
            }
            else if (RDF2TM.SUBJECT_ROLE.equals(predicate)) {
                getMapping(subject).subject = object;
            }
            else if (RDF2TM.OBJECT_ROLE.equals(predicate)) {
                getMapping(subject).object = object;
            }
            else {
              throw new RDFHandlerException(new MIOException("Unknown predicate <" + predicate + ">"));
            }
        }
    }

    private static final class Mapping {
        private int kind = UNKNOWN;
        private String type;
        private String subject;
        private String object;
        private Collection<String> scope;
    }

}
