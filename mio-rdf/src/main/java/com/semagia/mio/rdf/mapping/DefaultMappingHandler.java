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
package com.semagia.mio.rdf.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.semagia.mio.IRef;
import com.semagia.mio.MIOException;
import com.semagia.mio.helpers.Ref;
import com.semagia.mio.rdf.api.IMapping;
import com.semagia.mio.rdf.api.IMappingHandler;

/**
 * {@link IMappingHandler} implementation that collects all events an returns
 * a {@link IMapping}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 550 $ - $Date: 2010-09-26 11:35:06 +0200 (So, 26 Sep 2010) $
 */
public final class DefaultMappingHandler implements IMappingHandler {

    private DefaultMapping _mapping;
    private DefaultMapperFactory _factory;

    public DefaultMappingHandler() {
        _mapping = new DefaultMapping();
        _factory = DefaultMapperFactory.getInstance();
    }

    public IMapping getMapping() {
        return _mapping;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.rdf.api.IMappingHandler#start()
     */
    @Override
    public void start() throws MIOException {

    }

    /* (non-Javadoc)
     * @see com.semagia.mio.rdf.api.IMappingHandler#end()
     */
    @Override
    public void end() throws MIOException {

    }

    /* (non-Javadoc)
     * @see com.semagia.mio.rdf.api.IMappingHandler#handleComment(java.lang.String)
     */
    @Override
    public void handleComment(String comment) throws MIOException {

    }

    /* (non-Javadoc)
     * @see com.semagia.mio.rdf.api.IMappingHandler#handlePrefix(java.lang.String, java.lang.String)
     */
    @Override
    public void handlePrefix(String prefix, String iri) throws MIOException {

    }

    /* (non-Javadoc)
     * @see com.semagia.mio.rdf.api.IMappingHandler#handleAssociation(java.lang.String, java.lang.String, java.lang.String, java.lang.String[], java.lang.String)
     */
    @Override
    public void handleAssociation(String predicate, String subjectRole,
            String objectRole, String[] scope, String type) throws MIOException {
        if (subjectRole == null) {
            throw new MIOException("The subject role must not be null");
        }
        if (objectRole == null) {
            throw new MIOException("The object role must not be null");
        }
        _mapping.addMapper(predicate, _factory.createAssociationMapper(_asSID(type), _asSIDs(scope), _asSID(subjectRole), _asSID(objectRole)));
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.rdf.api.IMappingHandler#handleName(java.lang.String, java.lang.String[], java.lang.String, boolean)
     */
    @Override
    public void handleName(String predicate, String[] scope, String type,
            boolean lang2Scope) throws MIOException {
        _mapping.addMapper(predicate, _factory.createNameMapper(_asSID(type), _asSIDs(scope), lang2Scope));
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.rdf.api.IMappingHandler#handleOccurrence(java.lang.String, java.lang.String[], java.lang.String, boolean)
     */
    @Override
    public void handleOccurrence(String predicate, String[] scope, String type,
            boolean lang2Scope) throws MIOException {
        _mapping.addMapper(predicate, _factory.createOccurrenceMapper(_asSID(type), _asSIDs(scope), lang2Scope));
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.rdf.api.IMappingHandler#handleInstanceOf(java.lang.String, java.lang.String[])
     */
    @Override
    public void handleInstanceOf(String predicate, String[] scope)
            throws MIOException {
        _mapping.addMapper(predicate, _factory.createTypeInstanceMapper(_asSIDs(scope)));
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.rdf.api.IMappingHandler#handleSubtypeOf(java.lang.String, java.lang.String[])
     */
    @Override
    public void handleSubtypeOf(String predicate, String[] scope)
            throws MIOException {
        _mapping.addMapper(predicate, _factory.createSupertypeSubtypeMapper(_asSIDs(scope)));
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.rdf.api.IMappingHandler#handleSubjectIdentifier(java.lang.String)
     */
    @Override
    public void handleSubjectIdentifier(String predicate) throws MIOException {
        _mapping.addMapper(predicate, _factory.createSubjectIdentifierMapper());
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.rdf.api.IMappingHandler#handleSubjectLocator(java.lang.String)
     */
    @Override
    public void handleSubjectLocator(String predicate) throws MIOException {
        _mapping.addMapper(predicate, _factory.createSubjectLocatorMapper());
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.rdf.api.IMappingHandler#handleItemIdentifier(java.lang.String)
     */
    @Override
    public void handleItemIdentifier(String predicate) throws MIOException {
        _mapping.addMapper(predicate, _factory.createItemIdentifierMapper());
    }

    private static Collection<IRef> _asSIDs(String[] scope) {
        if (scope == null) {
            return null;
        }
        List<IRef> themes = new ArrayList<IRef>();
        for (String theme: scope) {
            themes.add(Ref.createSubjectIdentifier(theme));
        }
        return themes;
    }

    private static IRef _asSID(String iri) {
        return iri == null ? null : Ref.createSubjectIdentifier(iri);
    }
}
