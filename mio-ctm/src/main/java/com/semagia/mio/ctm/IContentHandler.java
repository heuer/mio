/*
 * Copyright 2007 - 2009 Lars Heuer (heuer[at]semagia.com)
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

import java.util.List;

import com.semagia.mio.MIOException;

/**
 * Handler which gets informed about parsing events.
 * 
 * The design of this interface mimics the {@link com.semagia.mio.IMapHandler}
 * to some extent.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 606 $ - $Date: 2011-01-20 00:48:46 +0100 (Do, 20 Jan 2011) $
 */
interface IContentHandler {

    /**
     * Returns the environment.
     *
     * @return The environment, never {@code null}.
     */
    public IEnvironment getEnvironment();

    /**
     * Resolves the provided topic identifier against the base IRI.
     *
     * @param identifier The identifier to resolve.
     * @return An absolute IRI.
     * @throws MIOException In case of an error.
     */
    public IReference resolveIdentifier(String identifier) throws MIOException;

    /**
     * Creates a topic with the provided identity.
     *
     * @param ref The identity of the topic.
     * @throws MIOException in case of an error.
     */
    public void startTopic(IReference ref) throws MIOException;

    /**
     * Creates a topic with a generated item identifier.
     * Shortcut for {@link #startTopic(null)}.
     * 
     * @throws MIOException in case of an error.
     */
    public IReference startTopic() throws MIOException;

    /**
     * Creates a topic with an item identifier using the provided 
     * wildcard name.
     *
     * @param name The name of the wildcard or {@code null}.
     * @throws MIOException in case of an error.
     */
    public IReference startTopic(String name) throws MIOException;

    /**
     * Indicates the end of a topic declaration.
     * 
     * @throws MIOException in case of an error.
     */
    public void endTopic() throws MIOException;

    /**
     * Adds the provided subject identifier 
     *
     * @param ref The subject identifier.
     * @throws MIOException in case of an error.
     */
    public void subjectIdentifier(IReference ref) throws MIOException;

    /**
     * Adds the provided subject locator.
     *
     * @param ref The subject locator.
     * @throws MIOException in case of an error.
     */
    public void subjectLocator(IReference ref) throws MIOException;

    /**
     * Adds the provided item identifier.
     *
     * @param ref The item identifier.
     * @throws MIOException in case of an error.
     */
    public void itemIdentifier(IReference ref) throws MIOException;

    /**
     * Called if the detects an item identifier which is not yet known.
     *
     * @param ref A variable.
     * @throws MIOException in case of an error.
     */
    public void itemIdentifierVariable(IReference ref) throws MIOException;

    /**
     * Called if the type of the identity is unknown.
     *
     * @param ref A variable.
     * @throws MIOException in case of an error.
     */
    public void identity(IReference ref) throws MIOException;

    /**
     * Called if the value of the subject locator is unknown.
     *
     * @param ref A variable.
     * @throws MIOException in case of an error.
     */
    public void subjectLocatorVariable(IReference ref) throws MIOException;

    /**
     * Indicates that a template with the specified name should
     * be called.
     * 
     * If this method is invoked after a {@link #startTopic(IReference)} event,
     * the arguments <tt>args</tt> have to be modified, so the first
     * arg becomes the topic.
     *
     * @param name The name of the template.
     * @param args The arguments.
     * @throws MIOException in case of an error.
     */
    public void callTemplate(String name, List<IReference> args) throws MIOException;

    /**
     * Indicates that a template definition was found.
     * 
     * @param name The name of the template.
     * @param args The parameters.
     * @throws MIOException in case of an error.
     */
    public void startTemplate(String name, List<IReference> args) throws MIOException;

    /**
     * Indicates that the current template has been completed.
     * 
     * @throws MIOException in case of an error.
     */
    public void endTemplate() throws MIOException;

    /**
     * Creates an association.
     *
     * @param type The association type.
     * @throws MIOException in case of an error.
     */
    public void startAssociation(IReference type) throws MIOException;

    /**
     * Indicates that the current association has been parsed completly.
     * 
     * @throws MIOException in case of an error.
     */
    public void endAssociation() throws MIOException;

    /**
     * Creates a role within the current association.
     *
     * @param type Role type
     * @param player Role player
     * @param reifier Role reifier or {@code null}.
     * @throws MIOException in case of an error.
     */
    public void handleRole(IReference type, IReference player, IReference reifier) throws MIOException;

    /**
     * Indicates the start of a scope definition.
     * 
     * @throws MIOException in case of an error.
     */
    public void startScope() throws MIOException;

    /**
     * Indicates the end of a scope definition.
     * 
     * @throws MIOException in case of an error.
     */
    public void endScope() throws MIOException;

    /**
     * Adds the provided topic to the current scope.
     *
     * @param theme The theme.
     * @throws MIOException in case of an error.
     */
    public void theme(IReference theme) throws MIOException;

    /**
     * Creates an occurrence.
     *
     * @param type The occurrence type.
     * @throws MIOException in case of an error.
     */
    public void startOccurrence(IReference type) throws MIOException;

    /**
     * Indicates the end of the current occurrence definition.
     * 
     * @throws MIOException in case of an error.
     */
    public void endOccurrence() throws MIOException;

    /**
     * Creates a name.
     *
     * @param type The name type or {@code null} to indicate the default name type.
     * @throws MIOException in case of an error.
     */
    public void startName(IReference type) throws MIOException;

    /**
     * Indicates the end of the current name definition. 
     * 
     * @throws MIOException in case of an error.
     */
    public void endName() throws MIOException;

    /**
     * Assigns a reifier to the current construct.
     * 
     * @param reifier The reifier or {@code null} if the construct has no reifier.
     * @throws MIOException in case of an error.
     */
    public void reifier(IReference reifier) throws MIOException;

    /**
     * Assigns a value / datatype to an occurrence / variant.
     * 
     * @param value The literal to assign.
     * @throws MIOException in case of an error.
     */
    public void value(IReference value) throws MIOException;

    /**
     * Assigns a value to the current name.
     * 
     * @param value The value of the name.
     * @throws MIOException in case of an error.
     */
    public void nameValue(IReference value) throws MIOException;

    /**
     * Indicates the start of a variant definition. 
     * 
     * @throws MIOException in case of an error.
     */
    public void startVariant() throws MIOException;

    /**
     * Indicates the end of the current variant definition. 
     * 
     * @throws MIOException in case of an error.
     */
    public void endVariant() throws MIOException;

    /**
     * Creates a type-instance relationship between the current topic
     * an the provided {@code type}.
     *
     * @param type The topic which plays the type role.
     * @throws MIOException in case of an error.
     */
    public void isa(IReference type) throws MIOException;

    /**
     * Creates a supertype-subtype relationship between the current topic
     * an the provided {@code supertype}.
     *
     * @param supertype The topic which plays the supertype role.
     * @throws MIOException in case of an error.
     */
    public void ako(IReference supertype) throws MIOException;

}
