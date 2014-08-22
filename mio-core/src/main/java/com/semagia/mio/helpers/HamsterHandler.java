/*
 * Copyright 2007 - 2010 Lars Heuer (heuer[at]semagia.com)
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
package com.semagia.mio.helpers;

import java.util.Collection;
import java.util.Set;

import com.semagia.mio.MIOException;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 607 $ - $Date: 2011-01-20 02:28:15 +0100 (Do, 20 Jan 2011) $
 */
public abstract class HamsterHandler<T> {

    /**
     * Returns either an existing topic with the specified item identifier
     * or creates a topic with the specified item identifier.
     * 
     * @param iri An absolute IRI representing an item identifier.
     * @return A topic with the item identifier <tt>iri</tt>.
     * @throws MIOException In case of an error.
     */
    protected abstract T createTopicByItemIdentifier(final String iri)
            throws MIOException;

    /**
     * Returns either an existing topic with the specified subject identifier
     * or creates a topic with the specified subject identifier.
     * 
     * @param iri An absolute IRI representing a subject identifier.
     * @return A topic with the subject identifier <tt>iri</tt>.
     * @throws MIOException In case of an error.
     */
    protected abstract T createTopicBySubjectIdentifier(final String iri)
            throws MIOException;

    /**
     * Returns either an existing topic with the specified subject locator
     * or creates a topic with the specified subject locator.
     * 
     * @param iri An absolute IRI representing a subject locator.
     * @return A topic with the subject identifier <tt>iri</tt>.
     * @throws MIOException In case of an error.
     */
    protected abstract T createTopicBySubjectLocator(final String iri)
            throws MIOException;

    /**
     * Creates a tmdm:type-instance relationship between <tt>instance</tt> and
     * <tt>type</tt>.
     * 
     * @param instance The topic that should play the tmdm:instance role.
     * @param type The topic that should play the tmdm:type role.
     * @throws MIOException In case of an error.
     */
    protected abstract void handleTypeInstance(final T instance, final T type)
            throws MIOException;

    /**
     * Adds the item identifier <tt>iri</tt> to the topic.
     * <p>
     * Adding the item identifier to the topic may cause a merge operation 
     * that must be handled transparently..
     * </p>
     * 
     * @param topic The topic to add the item identifier to.
     * @param iri An absolute IRI.
     * @throws MIOException In case of an error.
     */
    protected abstract void handleItemIdentifier(final T topic, final String iri)
            throws MIOException;

    /**
     * Adds the subject identifier <tt>iri</tt> to the topic.
     * <p>
     * Adding the subject identifier to the topic may cause a merge operation 
     * that must be handled transparently..
     * </p>
     * 
     * @param topic The topic to add the subject identifier to.
     * @param iri An absolute IRI.
     * @throws MIOException In case of an error.
     */
    protected abstract void handleSubjectIdentifier(final T topic, final String iri)
            throws MIOException;

    /**
     * Adds the subject locator <tt>iri</tt> to the topic.
     * <p>
     * Adding the subject locator to the topic may cause a merge operation 
     * that must be handled transparently..
     * </p>
     * 
     * @param topic The topic to add the subject locator to.
     * @param iri An absolute IRI.
     * @throws MIOException In case of an error.
     */
    protected abstract void handleSubjectLocator(final T topic, final String iri)
            throws MIOException;

    /**
     * Adds the specified item identifier <tt>iri</tt> to the topic map.
     * 
     * @param iri An absolute IRI.
     * @throws MIOException In case of an error.
     */
    protected abstract void handleTopicMapItemIdentifier(final String iri)
            throws MIOException;

    /**
     * Sets the [reifier] property of the topic map.
     * 
     * @param reifier The reifier.
     * @throws MIOException In case of an error.
     */
    protected abstract void handleTopicMapReifier(final T reifier)
            throws MIOException;

    /**
     * Creates an association.
     * 
     * @param type The type of the association.
     * @param scope The scope of the association or <tt>null</tt> to indicate the unconstrained scope.
     * @param reifier The reifier of the association or <tt>null</tt>.
     * @param iids The item identifiers of the association. This collection is never <tt>null</tt> but may be empty.
     * @param roles The roles of the association.
     * @throws MIOException In case of an error.
     */
    protected abstract void createAssociation(final T type, 
            final Collection<T> scope, final T reifier, 
            final Collection<String> iids, final Collection<IRole<T>> roles)
            throws MIOException;
    

    /**
     * Creates an occurrence.
     * 
     * @param parent The parent topic.
     * @param type The occurrence type.
     * @param value The value of the occurrence.
     * @param datatype The datatype IRI.
     * @param scope The scope of the occurrence or <tt>null</tt> to indicate the unconstrained scope.
     * @param reifier The reifier of the occurrence or <tt>null</tt>.
     * @param iids The item identifiers of the occurrence. This collection is never <tt>null</tt> but may be empty.
     * @throws MIOException In case of an error.
     */
    protected abstract void createOccurrence(final T parent, final T type, 
            final String value, final String datatype, 
            final Collection<T> scope, final T reifier,
            final Collection<String> iids) throws MIOException;

    /**
     * Creates a name.
     * 
     * @param parent The parent topic.
     * @param type The name type or <tt>null</tt> to indicate the default name type.
     * @param value The value of the name.
     * @param scope The scope of the name or <tt>null</tt> to indicate the unconstrained scope.
     * @param reifier The reifier of the name or <tt>null</tt>.
     * @param iids The item identifiers of the name. This collection is never <tt>null</tt> but may be empty.
     * @param variants The variants of the name. This collection is never <tt>null</tt> but may be emtpy.
     * @throws MIOException In case of an error.
     */
    protected abstract void createName(final T parent, final T type, 
            final String value, final Collection<T> scope, final T reifier, 
            final Collection<String> iids,
            final Collection<IVariant<T>> variants) throws MIOException;

    /**
     * Represents an association role.
     */
    public interface IRole<T> {

        /**
         * Returns a (maybe empty) iterable of absolute IRIs which represent
         * the item identifiers of the role.
         *
         * @return A (maybe empty) iterable of absolute IRIs.
         */
        public Set<String> getItemIdentifiers();

        /**
         * Returns the type of the role.
         *
         * @return The type of the role, never <tt>null</tt>
         */
        public T getType();

        /**
         * Returns the role player.
         *
         * @return The role player, never <tt>null</tt>
         */
        public T getPlayer();

        /**
         * Returns the reifier or <tt>null</tt> if the role is not reified.
         *
         * @return The reifier or <tt>null</tt>.
         */
        public T getReifier();
    }

    /**
     * Represents a variant.
     */
    public interface IVariant<T> {

        /**
         * Returns a (maybe empty) iterable of absolute IRIs which represent
         * the item identifiers of the variant.
         *
         * @return A (maybe empty) iterable of absolute IRIs.
         */
        public Set<String> getItemIdentifiers();

        /**
         * Returns the value of the variant.
         *
         * @return The variant's value, never <tt>null</tt>
         */
        public String getValue();

        /**
         * Returns an absolute IRI indicating the datatype.
         *
         * @return The datatype IRI, never <tt>null</tt>
         */
        public String getDatatype();

        /**
         * Returns the scope of the variant.
         * <p>
         * Note: The returned collection may not include the scope of the 
         * variant's parent.
         * </p>
         *
         * @return The variant's scope.
         */
        public Set<T> getScope();

        /**
         * Returns the reifier or <tt>null</tt> if the variant is not reified.
         *
         * @return The reifier or <tt>null</tt>.
         */
        public T getReifier();
    }

}
