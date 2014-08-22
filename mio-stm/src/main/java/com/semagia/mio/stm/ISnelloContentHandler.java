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
package com.semagia.mio.stm;

import java.util.List;

import com.semagia.mio.MIOException;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 469 $ - $Date: 2010-09-08 12:39:06 +0200 (Mi, 08 Sep 2010) $
 */
interface ISnelloContentHandler {

    public IParserEnvironment getParserEnvironment();

    public void addPrefix(String name, String iri) throws MIOException;

    public void addAlias(String name, SnelloRef ref) throws MIOException;

    public void startTopic(SnelloRef ref) throws MIOException;

    public SnelloRef startTopic() throws MIOException;

    public SnelloRef startTopic(String name) throws MIOException;

    public void endTopic() throws MIOException;

    public void subjectIdentifier(SnelloRef ref) throws MIOException;

    public void subjectLocator(SnelloRef ref) throws MIOException;

    public void itemIdentifier(SnelloRef ref) throws MIOException;

    public void itemIdentifierVariable(SnelloRef ref) throws MIOException;

    /**
     * Called if the type of the identity is unknown.
     *
     * @param ref A reference which is of type {@link SnelloRef#VAR}
     */
    public void identity(SnelloRef ref) throws MIOException;

    /**
     * Called if the value of the subject locator is unknown.
     *
     * @param ref A reference, which is of type {@link SnelloRef#VSLO}
     */
    public void subjectLocatorVariable(SnelloRef ref) throws MIOException;

    /**
     * Indicates that a template with the specified name should
     * be called.
     * 
     * If this method is invoked after a {@link #startTopic(SnelloRef)} event,
     * the arguments <tt>args</tt> have to be modified, so the first
     * arg becomes the topic.
     *
     * @param name
     * @param args
     */
    public void callTemplate(String name, List<SnelloRef> args) throws MIOException;

    public void startTemplate(String name, List<SnelloRef> args) throws MIOException;

    public void endTemplate() throws MIOException;

    public void handleRole(SnelloRef type, SnelloRef player, SnelloRef reifier) throws MIOException;

    public void startAssociation(SnelloRef type) throws MIOException;

    public void endAssociation() throws MIOException;

    public void startScope() throws MIOException;

    public void endScope() throws MIOException;

    public void theme(SnelloRef theme) throws MIOException;

    public void startOccurrence(SnelloRef type) throws MIOException;

    public void endOccurrence() throws MIOException;

    public void startName(SnelloRef type) throws MIOException;

    public void endName() throws MIOException;

    public void reifier(SnelloRef reifier) throws MIOException;

    public void value(SnelloRef value) throws MIOException;

    public void nameValue(SnelloRef value) throws MIOException;

    public void startVariant() throws MIOException;

    public void endVariant() throws MIOException;

}
