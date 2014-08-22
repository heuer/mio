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

import java.io.IOException;
import java.net.URL;

import org.openrdf.OpenRDFException;
import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.helpers.RDFHandlerBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.semagia.mio.IMapHandler;
import com.semagia.mio.IRef;
import com.semagia.mio.MIOException;
import com.semagia.mio.MIOParseException;
import com.semagia.mio.Property;
import com.semagia.mio.Source;
import com.semagia.mio.Syntax;
import com.semagia.mio.base.AbstractDefaultDeserializer;
import com.semagia.mio.helpers.Locator;
import com.semagia.mio.helpers.Ref;
import com.semagia.mio.rdf.api.IErrorHandler;
import com.semagia.mio.rdf.api.IMapper;
import com.semagia.mio.rdf.api.IMapping;
import com.semagia.mio.rdf.api.IMappingReader;
import com.semagia.mio.rdf.api.IMappingPrefixListener;
import com.semagia.mio.rdf.mapping.DefaultMappingHandler;
import com.semagia.mio.voc.XSD;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 554 $ - $Date: 2010-09-26 21:18:02 +0200 (So, 26 Sep 2010) $
 */
final class RDFDeserializer extends AbstractDefaultDeserializer {

    private static final Logger _LOG = LoggerFactory.getLogger(RDFDeserializer.class);

    private static final ILanguageTagProvider _LANG_PROVIDER = OasisLanguageTagProvider.getInstance();

    private final RDFParser _rdfParser;

    RDFDeserializer(final RDFParser parser) {
        super();
        _rdfParser = parser;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.base.AbstractDeserializer#parse(com.semagia.mio.Source)
     */
    @Override
    public void parse(final Source src) throws IOException, MIOException {
        // 1st: read the mapping if not set
        IMapping mapping = (IMapping) getProperty(Property.RDF2TM_MAPPING);
        if (mapping == null) {
            String mappingIRI = (String) getProperty(Property.RDF2TM_MAPPING_IRI);
            if (mappingIRI == null) {
                mappingIRI = src.getIRI();
            }
            if (mappingIRI == null) {
                throw new MIOException("No source given to read the mapping from");
            }
            final IMappingReader mappingReader = MappingReaderUtils.createReader(mappingIRI, (Syntax) getProperty(Property.RDF2TM_MAPPING_SYNTAX));
            final DefaultMappingHandler handler = new DefaultMappingHandler();
            mappingReader.setMappingHandler(handler);
            mappingReader.setPrefixListener((IMappingPrefixListener) getProperty("http://psi.semagia.com/mio/property/rdf2tm/prefix-listener"));
            mappingReader.read(new Source(mappingIRI));
            setProperty(Property.RDF2TM_MAPPING, handler.getMapping());
        }
        // 2nd: Delegate superclass which invokes handler.startTopicMap etc. and
        // finally invokes #doParse
        super.parse(src);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.base.AbstractDeserializer#doParse(com.semagia.mio.Source)
     */
    @Override
    protected void doParse(final Source src) throws IOException, MIOException {
        final IMapping mapping = (IMapping) getProperty(Property.RDF2TM_MAPPING);
        final boolean infoLog = Boolean.TRUE.equals(getProperty(Property.RDF2TM_REPORT_UNHANDLED_STATEMENTS));
        final boolean warnLog = infoLog && Boolean.TRUE.equals(getProperty("http://psi.semagia.com/mio/property/logging-level/warn"));
        final boolean stopOnError = !Boolean.FALSE.equals(getProperty(Property.RDF2TM_STOP_ON_ERROR));
        _rdfParser.setRDFHandler(new RDFStatementHandler(_handler, 
                                    mapping, src.getBaseIRI(), 
                                    _LANG_PROVIDER, 
                                    warnLog ? WarnLogMapper.getInstance():
                                        infoLog ? InfoLogMapper.getInstance() : null,
                                    new DefaultErrorHandler(stopOnError),
                                    //TODO: Decide!
                                    // (IMappingPrefixListener) getProperty("http://psi.semagia.com/mio/property/rdf2tm/prefix-listener")
                                    null));
        _rdfParser.setVerifyData(!Boolean.FALSE.equals(getProperty(Property.VALIDATE)));
        try {
            if (src.getByteStream() != null) {
                _rdfParser.parse(src.getByteStream(), src.getBaseIRI());
            }
            else if (src.getCharacterStream() != null) {
                _rdfParser.parse(src.getCharacterStream(), src.getBaseIRI());
            }
            else {
                _rdfParser.parse(new URL(src.getIRI()).openStream(), src.getBaseIRI());
            }
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
    }

    private static class RDFStatementHandler extends RDFHandlerBase {

        private final IMapHandler _handler;
        private final Locator _docIRI;
        private final IMapping _mapping;
        private final ILanguageTagProvider _langProvider;
        private final IMapper _defaultMapper;
        private final IErrorHandler _errorHandler;
        private final IMappingPrefixListener _prefixListener;

        public RDFStatementHandler(final IMapHandler handler, 
                final IMapping mapping, final String docIRI, 
                final ILanguageTagProvider langProvider,
                final IMapper defaultMapper,
                final IErrorHandler errorHandler,
                final IMappingPrefixListener listener) {
            _handler = handler;
            _mapping = mapping;
            _docIRI = Locator.create(docIRI);
            _langProvider = langProvider;
            _defaultMapper = defaultMapper;
            _errorHandler = errorHandler;
            _prefixListener = listener;
        }

        @Override
        public void handleNamespace(String prefix, String uri)
                throws RDFHandlerException {
            super.handleNamespace(prefix, uri);
            if (_prefixListener != null) {
                _prefixListener.handleNamespace(prefix, uri);
            }
        }

        /* (non-Javadoc)
         * @see org.openrdf.rio.RDFHandler#handleStatement(org.openrdf.model.Statement)
         */
        @Override
        public void handleStatement(final Statement stmt) throws RDFHandlerException {
            final String p1 = stmt.getPredicate().stringValue();
            IMapper mapper = _mapping.getMapper(p1);
            if (mapper == null) {
                mapper = _defaultMapper;
            }
            if (mapper == null) {
                return;
            }
            final Resource res = stmt.getSubject();
            // Let s1 be the subject of that triple, p1 the predicate, and o1 the object. 
            final String s1 = res.stringValue();
            final Value o1 = stmt.getObject();
            final IRef subj = res instanceof BNode ? Ref.createItemIdentifier(_resolveLocator((BNode) res))
                                             : Ref.createSubjectIdentifier(s1);
            try {
                _handler.startTopic(subj);
                if (o1 instanceof URI) {
                    mapper.handle(_handler, _errorHandler, 
                            subj, p1, _resolveLocator(o1.stringValue()), false);
                }
                else if (o1 instanceof BNode) {
                    mapper.handle(_handler, _errorHandler, 
                            subj, p1, _resolveLocator((BNode) o1), true);
                }
                else {
                    // org.openrdf.model.Literal, not mio.Literal!
                    final Literal rdfLiteral = (Literal) o1;
                    final String datatype = rdfLiteral.getDatatype() != null 
                                            ? rdfLiteral.getDatatype().stringValue()
                                            : XSD.STRING;
                    mapper.handle(_handler, _errorHandler, 
                            subj, p1, rdfLiteral.stringValue(), datatype, _langProvider.getLanguage(rdfLiteral.getLanguage()));
                }
                _handler.endTopic();
            }
            catch (MIOException ex) {
                throw new RDFHandlerException(ex);
            }
        }

        private String _resolveLocator(final String reference) {
            return _docIRI.resolve(reference).toExternalForm();
        }

        private String _resolveLocator(final BNode bNode) {
            return _resolveLocator("#" + bNode.getID());
        }
    }

    /**
     * {@link IMapper} implementation that writes unhandled statements to the log.
     */
    private static final class InfoLogMapper implements IMapper {

        private static final IMapper _INSTANCE = new InfoLogMapper();

        private InfoLogMapper() {
            // noop.
        }

        public static final IMapper getInstance() {
            return _INSTANCE;
        }

        @Override
        public void handle(IMapHandler handler, IErrorHandler errorHandler,
                IRef subject, String predicateIRI, String value,
                String datatype, IRef language) throws MIOException {
            _LOG.info("Ignoring: <{}>, <{}>, '{}'^^<{}>@{}, ", new Object[] {
                    subject, predicateIRI, value, datatype, language });
        }

        @Override
        public void handle(IMapHandler handler, IErrorHandler errorHandler,
                IRef subject, String predicateIRI, String objIRI,
                boolean objBNode) throws MIOException {
            _LOG.info("Ignoring: <{}>, <{}>, <{}>", new Object[] { subject,
                    predicateIRI, objIRI });
        }
    }

    /**
     * {@link IMapper} implementation that writes unhandled statements to the log.
     */
    private static final class WarnLogMapper implements IMapper {

        private static final IMapper _INSTANCE = new WarnLogMapper();

        private WarnLogMapper() {
            // noop.
        }

        public static final IMapper getInstance() {
            return _INSTANCE;
        }

        @Override
        public void handle(IMapHandler handler, IErrorHandler errorHandler,
                IRef subject, String predicateIRI, String value,
                String datatype, IRef language) throws MIOException {
            _LOG.warn("Ignoring: <{}>, <{}>, '{}'^^<{}>@{}, ", new Object[] {
                    subject, predicateIRI, value, datatype, language });
        }

        @Override
        public void handle(IMapHandler handler, IErrorHandler errorHandler,
                IRef subject, String predicateIRI, String objIRI,
                boolean objBNode) throws MIOException {
            _LOG.warn("Ignoring: <{}>, <{}>, <{}>", new Object[] { subject,
                    predicateIRI, objIRI });
        }
    }

}
