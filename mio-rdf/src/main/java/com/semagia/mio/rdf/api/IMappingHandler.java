/*
 * Copyright 2010 Lars Heuer (heuer[at]semagia.com)
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
package com.semagia.mio.rdf.api;

import com.semagia.mio.MIOException;

/**
 * EXPERIMENTAL Handler to read/write RDF2TM mappings.
 * 
 * All IRIs must be reported as absolute IRIs.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 610 $ - $Date: 2011-04-04 18:09:15 +0200 (Mo, 04 Apr 2011) $
 */
public interface IMappingHandler {

    /**
     * First event.
     *
     * @throws MIOException In case of an error.
     */
    public void start() throws MIOException;

    /**
     * Last event.
     *
     * @throws MIOException In case of an error.
     */
    public void end() throws MIOException;

    /**
     * Notification about a comment.
     *
     * @param comment The comment content.
     * @throws MIOException In case of an error.
     */
    public void handleComment(String comment) throws MIOException;

    /**
     * Notification about a prefix / IRI pair.
     *
     * @param prefix The prefix.
     * @param iri The IRI.
     * @throws MIOException In case of an error.
     */
    public void handlePrefix(String prefix, String iri) throws MIOException;

    /**
     * Notification about an association mapping.
     *
     * @param predicate The RDF predicate IRI.
     * @param subjectRole The role type of the subject.
     * @param objectRole The role type of the object.
     * @param scope The scope or {@code null}
     * @param type The type or {@code null}.
     * @throws MIOException In case of an error.
     */
    public void handleAssociation(String predicate, String subjectRole, String objectRole, String[] scope, String type) throws MIOException;

    /**
     * Notification about a name mapping. 
     *
     * @param predicate The RDF predicate IRI.
     * @param scope The scope or {@code null}
     * @param type The type or {@code null}.
     * @param lang2Scope
     * @throws MIOException In case of an error.
     */
    public void handleName(String predicate, String[] scope, String type, boolean lang2Scope) throws MIOException;

    /**
     * Notification about an occurrence mapping.
     *
     * @param predicate The RDF predicate IRI.
     * @param scope The scope or {@code null}
     * @param type The type or {@code null}.
     * @param lang2Scope
     * @throws MIOException In case of an error.
     */
    public void handleOccurrence(String predicate, String[] scope, String type, boolean lang2Scope) throws MIOException;

    /**
     * Notification about a type-instance mapping.
     *
     * @param predicate The RDF predicate IRI.
     * @param scope The scope or {@code null}
     * @throws MIOException In case of an error.
     */
    public void handleInstanceOf(String predicate, String[] scope) throws MIOException;

    /**
     * Notification about a supertype-subtype mapping.
     *
     * @param predicate The RDF predicate IRI.
     * @param scope The scope or {@code null}
     * @throws MIOException In case of an error.
     */
    public void handleSubtypeOf(String predicate, String[] scope) throws MIOException;

    /**
     * Notification about a subject identifier mapping.
     *
     * @param predicate The RDF predicate IRI.
     * @throws MIOException In case of an error.
     */
    public void handleSubjectIdentifier(String predicate) throws MIOException;

    /**
     * Notification about a subject locator mapping.
     *
     * @param predicate The RDF predicate IRI.
     * @throws MIOException In case of an error.
     */
    public void handleSubjectLocator(String predicate) throws MIOException;

    /**
     * Notification about an item identifier mapping.
     *
     * @param predicate The RDF predicate IRI.
     * @throws MIOException In case of an error.
     */
    public void handleItemIdentifier(String predicate) throws MIOException;

}
