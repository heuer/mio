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
package com.semagia.mio;

/**
 * Receiver of notifications for Topic Maps constructs.
 * <p>
 * The implementation of this interface is application / Topic Maps processor
 * specific.
 * </p>
 * <p>
 * Commonly, the values (IRIs, dates, whatever) are not normalized or 
 * canonicalized, the implementation of the this interface must take care to
 * normalize / canonicalize the values.
 * </p>
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 607 $ - $Date: 2011-01-20 02:28:15 +0100 (Do, 20 Jan 2011) $
 */
public interface IMapHandler {

    /**
     * Notification about the beginning of a topic map.
     * 
     * This method is only called once.
     *
     * @throws MIOException If an unrecoverable error occurs.
     */
    public void startTopicMap() throws MIOException;

    /**
     * Notification about the end of a topic map.
     * <p>
     * This method MUST be invoked even if an error happens.
     * </p>
     *
     * @throws MIOException If an unrecoverable error occurs.
     */
    public void endTopicMap() throws MIOException;

    /**
     * Notification about the start of a topic.
     *
     * @param identity A subject identifier, subject locator or item identifier.
     * @throws MIOException If an unrecoverable error occurs.
     */
    public void startTopic(IRef identity) throws MIOException;

    /**
     * Notification about the end of a topic.
     *
     * @throws MIOException If an unrecoverable error occurs.
     */
    public void endTopic() throws MIOException;

    /**
     * Notification about the start of an association.
     *
     * @throws MIOException If an unrecoverable error occurs.
     */
    public void startAssociation() throws MIOException;

    /**
     * Notification about the end of an association.
     *
     * @throws MIOException If an unrecoverable error occurs.
     */
    public void endAssociation() throws MIOException;

    /**
     * Notification about the start of a role.
     *
     * @throws MIOException If an unrecoverable error occurs.
     */
    public void startRole() throws MIOException;

    /**
     * Notification about the end of a role.
     *
     * @throws MIOException If an unrecoverable error occurs.
     */
    public void endRole() throws MIOException;

    /**
     * Notification about the start of an occurrence.
     *
     * @throws MIOException If an unrecoverable error occurs.
     */
    public void startOccurrence() throws MIOException;

    /**
     * Notification about the end of an occurrence.
     *
     * @throws MIOException If an unrecoverable error occurs.
     */
    public void endOccurrence() throws MIOException;

    /**
     * Notification about the start of a name.
     *
     * @throws MIOException If an unrecoverable error occurs.
     */
    public void startName() throws MIOException;

    /**
     * Notification about the end of a name.
     * 
     * If there was no <tt>startType</tt> and <tt>endType</tt>
     * event, the <tt>MapHandler</tt> MUST assume that the read
     * name has the default name type.
     *
     * @throws MIOException If an unrecoverable error occurs.
     */
    public void endName() throws MIOException;

    /**
     * Notification about the start of a variant.
     * 
     * The parser guarantees that the scope of the name has been parsed.
     * The scope of the name is not part of the scope of the variant.
     *
     * @throws MIOException If an unrecoverable error occurs.
     */
    public void startVariant() throws MIOException;

    /**
     * Notification about the end of a variant.
     * 
     * If the scope of the reported variant is not the superset of the
     * <tt>parent</tt>'s scope, this method MUST throw a {@link MIOException}.
     *
     * @throws MIOException If an unrecoverable error occurs.
     */
    public void endVariant() throws MIOException;

    /**
     * Notification about the start of scope processing.
     * 
     * This method is either called once for a scoped construct or never.
     *
     * @throws MIOException If an unrecoverable error occurs.
     */
    public void startScope() throws MIOException;

    /**
     * Notification about the end of scope processing.
     *
     * @throws MIOException If an unrecoverable error occurs.
     */
    public void endScope() throws MIOException;

    /**
     * Notification about the start of a theme declaration.
     *
     * @throws MIOException If an unrecoverable error occurs.
     */
    public void startTheme() throws MIOException;

    /**
     * Notification about the end of a theme declaration.
     *
     * @throws MIOException If an unrecoverable error occurs.
     */
    public void endTheme() throws MIOException;

    /**
     * Reports a name value.
     *
     * @param value The value of a name.
     * @throws MIOException If an unrecoverable error occurs.
     */
    public void value(String value) throws MIOException;

    /**
     * Reports the value of an occurrence or variant.
     *
     * @param value The string representation of the value.
     * @param datatype The datatype of the value.
     * @throws MIOException If an unrecoverable error occurs.
     */
    public void value(String value, String datatype) throws MIOException;

    /**
     * Reports a subject identifier.
     * 
     * The subject identifier is an absolute IRI which should be added to the
     * currently processed topic.
     *
     * @param sid An absolute IRI.
     * @throws MIOException If an unrecoverable error occurs.
     */
    public void subjectIdentifier(String sid) throws MIOException;

    /**
     * Reports a subject locator.
     *
     * The subject locator is an absolute IRI which should be added to the
     * currently processed topic.
     *
     * @param slo An absolute IRI.
     * @throws MIOException If an unrecoverable error occurs.
     */
    public void subjectLocator(String slo) throws MIOException;

    /**
     * Reports an item identifier.
     * 
     * The item identifier is an absolute IRI which should be added to the
     * currently processed Topic Maps construct.
     *
     * @param iid An absolute IRI.
     * @throws MIOException If an unrecoverable error occurs.
     */
    public void itemIdentifier(String iid) throws MIOException;

    /**
     * Notification about the start of a player declaration.
     *
     * @throws MIOException If an unrecoverable error occurs.
     */
    public void startPlayer() throws MIOException;

    /**
     * Notification about the end of a player declaration. 
     *
     * @throws MIOException If an unrecoverable error occurs.
     */
    public void endPlayer() throws MIOException;

    /**
     * Notification about the start of a type declaration.
     *
     * @throws MIOException If an unrecoverable error occurs.
     */
    public void startType() throws MIOException;

    /**
     * Notification about the end of a type declaration.
     *
     * @throws MIOException If an unrecoverable error occurs.
     */
    public void endType() throws MIOException;

    /**
     * Notification about the start of a reifier declaration.
     *
     * @throws MIOException If an unrecoverable error occurs.
     */
    public void startReifier() throws MIOException;

    /**
     * Notification about the end of a reifier declaration.
     *
     * @throws MIOException If an unrecoverable error occurs.
     */
    public void endReifier() throws MIOException;

    /**
     * Notification about a topic reference.
     * 
     * The interpretation of the topic reference depends on the context (i.e.
     * after a {@link #startType()}, {@link #startPlayer()}, or {@link #startTheme()}
     * event).
     *
     * @param identity The identity of the topic.
     * @throws MIOException If an unrecoverable error occurs.
     */
    public void topicRef(IRef identity) throws MIOException;

    /**
     * Notification about the the start of <tt>type-instance</tt> 
     * relationships.
     * 
     * After this event there may occurr at minimum one {@link #topicRef(IRef)}
     * or one {@link #startTopic()} (with the correspondending {@link #endTopic()}
     * event.
     * 
     * The reported topics after a {@link #startIsa} event are meant as
     * type of the currently parsed topic.
     *
     * @throws MIOException If an unrecoverable error occurs.
     */
    public void startIsa() throws MIOException;

    /**
     * Notification about the end of the <tt>type-instance</tt> relationships.
     *
     * @throws MIOException If an unrecoverable error occurs.
     */
    public void endIsa() throws MIOException;

}
