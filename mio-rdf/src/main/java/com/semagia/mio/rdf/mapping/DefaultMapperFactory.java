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

import java.util.Collection;

import com.semagia.mio.IRef;
import com.semagia.mio.rdf.api.IMapper;

/**
 * Factory for {@link IMapper} implementations.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 550 $ - $Date: 2010-09-26 11:35:06 +0200 (So, 26 Sep 2010) $
 */
final class DefaultMapperFactory {

    private static final DefaultMapperFactory _INSTANCE = new DefaultMapperFactory();

    private DefaultMapperFactory() {
        // noop.
    }

    /**
     * Returns the default instance.
     *
     * @return The instance.
     */
    public static DefaultMapperFactory getInstance() {
        return _INSTANCE;
    }

    /**
     * Returns a {@link IMapper} implementation that is able to handle
     * rtm:instance-of declarations.
     * 
     * @param scope
     *            The scope, maybe {@code null} or empty to indicate the
     *            unconstrained scope.
     * @return A {@link IMapper} instance.
     */
    public IMapper createTypeInstanceMapper(final Collection<IRef> scope) {
        return scope == null || scope.isEmpty() ? TypeInstanceMapper.getInstance()
                : new TypeInstanceScopedMapper(scope);
    }

    /**
     * Returns a {@link IMapper} implementation that is able to handle
     * to create supertype-subtype associations.
     * 
     * @param scope
     *            The scope, maybe {@code null} or empty to indicate the
     *            unconstrained scope.
     * @return A {@link IMapper} instance.
     */
    public IMapper createSupertypeSubtypeMapper(final Collection<IRef> scope) {
        return new SupertypeSubtypeMapper(scope);
    }

    /**
     * Returns a {@link IMapper} instance which implements 
     * rtm:subject-identifier.
     *
     * @return A {@link IMapper} instance.
     */
    public IMapper createSubjectIdentifierMapper() {
        return IdentityMapper.getSubjectIdentifierMapper();
    }

    /**
     * Returns a {@link IMapper} instance which implements 
     * rtm:subject-locator.
     *
     * @return A {@link IMapper} instance.
     */
    public IMapper createSubjectLocatorMapper() {
        return IdentityMapper.getSubjectLocatorMapper();
    }
    
    /**
     * Returns a {@link IMapper} instance which implements 
     * rtm:source-locator.
     *
     * @return A {@link IMapper} instance.
     */
    public IMapper createItemIdentifierMapper() {
        return IdentityMapper.getItemIdentifierMapper();
    }

    /**
     * Returns a {@link IMapper} instance which implements rtm:association.
     * 
     * @param type
     *            The type or {@code null} to indicate that the IRI of the RDF
     *            predicate should be used as type.
     * @param scope
     *            The scope, maybe {@code null} or empty to indicate the
     *            unconstrained scope.
     * @param subjectType
     *            The role type of the RDF subject.
     * @param objectType
     *            The role type of the RDF object.
     * @return A {@link IMapper} instance.
     */
    public IMapper createAssociationMapper(final IRef type, 
            final Collection<IRef> scope, final IRef subjectType, 
            final IRef objectType) {
        return new AssociationMapper(scope, type, subjectType, objectType);
    }

    /**
     * Returns a {@link IMapper} instance which implements rtm:occurrence.
     *
     * @param type
     *            The type or {@code null} to indicate that the IRI of the RDF
     *            predicate should be used as type.
     * @param scope
     *            The scope, maybe {@code null} or empty to indicate the
     *            unconstrained scope.
     * @param lang2Scope
     *            {@code true} indicates that the optional RDF language tag
     *            should be translated into a theme and should be added to the
     *            scope, {@code false} indicates that the language tag should
     *            be ignored.
     * @return A {@link IMapper} instance.
     */
    public IMapper createOccurrenceMapper(final IRef type, 
            final Collection<IRef> scope, final boolean lang2Scope) {
        return new OccurrenceMapper(scope, type, lang2Scope);
    }

    /**
     * Returns a {@link IMapper} instance which implements rtm:basename.
     * 
     * @param type
     *            The type or {@code null} to indicate that the IRI of the RDF
     *            predicate should be used as type.
     * @param scope
     *            The scope, maybe {@code null} or empty to indicate the
     *            unconstrained scope.
     * @param lang2Scope
     *            {@code true} indicates that the optional RDF language tag
     *            should be translated into a theme and should be added to the
     *            scope, {@code false} indicates that the language tag should
     *            be ignored.
     * @return A {@link IMapper} instance.
     */
    public IMapper createNameMapper(final IRef type, 
            final Collection<IRef> scope, final boolean lang2Scope) {
        return new NameMapper(scope, type, lang2Scope);
    }

}
