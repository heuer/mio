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
class DelegatingContentHandler implements ISnelloContentHandler {

    protected ISnelloContentHandler _contentHandler;

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#addPrefix(java.lang.String, java.lang.String)
     */
    @Override
    public void addPrefix(String name, String iri) throws MIOException {
        _contentHandler.addPrefix(name, iri);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#addAlias(java.lang.String, com.semagia.mio.stm.dm.SnelloRef)
     */
    @Override
    public void addAlias(String name, SnelloRef ref) throws MIOException {
        _contentHandler.addAlias(name, ref);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#callTemplate(java.lang.String, java.util.List)
     */
    @Override
    public void callTemplate(String name, List<SnelloRef> args)
            throws MIOException {
        _contentHandler.callTemplate(name, args);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#endAssociation()
     */
    @Override
    public void endAssociation() throws MIOException {
        _contentHandler.endAssociation();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#endName()
     */
    @Override
    public void endName() throws MIOException {
        _contentHandler.endName();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#endOccurrence()
     */
    @Override
    public void endOccurrence() throws MIOException {
        _contentHandler.endOccurrence();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#endScope()
     */
    @Override
    public void endScope() throws MIOException {
        _contentHandler.endScope();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#endTemplate()
     */
    @Override
    public void endTemplate() throws MIOException {
        _contentHandler.endTemplate();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#endTopic()
     */
    @Override
    public void endTopic() throws MIOException {
        _contentHandler.endTopic();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#endVariant()
     */
    @Override
    public void endVariant() throws MIOException {
        _contentHandler.endVariant();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#getParserEnvironment()
     */
    @Override
    public IParserEnvironment getParserEnvironment() {
        return _contentHandler.getParserEnvironment();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#handleRole(com.semagia.mio.stm.dm.SnelloRef, com.semagia.mio.stm.dm.SnelloRef, com.semagia.mio.stm.dm.SnelloRef)
     */
    @Override
    public void handleRole(SnelloRef type, SnelloRef player, SnelloRef reifier)
            throws MIOException {
        _contentHandler.handleRole(type, player, reifier);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#identity(com.semagia.mio.stm.dm.SnelloRef)
     */
    @Override
    public void identity(SnelloRef ref) throws MIOException {
        _contentHandler.identity(ref);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#itemIdentifier(com.semagia.mio.stm.dm.SnelloRef)
     */
    @Override
    public void itemIdentifier(SnelloRef ref) throws MIOException {
        _contentHandler.itemIdentifier(ref);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#itemIdentifierVariable(com.semagia.mio.stm.dm.SnelloRef)
     */
    @Override
    public void itemIdentifierVariable(SnelloRef ref) throws MIOException {
        _contentHandler.itemIdentifierVariable(ref);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#nameValue(com.semagia.mio.stm.dm.SnelloRef)
     */
    @Override
    public void nameValue(SnelloRef value) throws MIOException {
        _contentHandler.nameValue(value);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#reifier(com.semagia.mio.stm.dm.SnelloRef)
     */
    @Override
    public void reifier(SnelloRef reifier) throws MIOException {
        _contentHandler.reifier(reifier);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#startAssociation(com.semagia.mio.stm.dm.SnelloRef)
     */
    @Override
    public void startAssociation(SnelloRef type) throws MIOException {
        _contentHandler.startAssociation(type);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#startName(com.semagia.mio.stm.dm.SnelloRef)
     */
    @Override
    public void startName(SnelloRef type) throws MIOException {
        _contentHandler.startName(type);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#startOccurrence(com.semagia.mio.stm.dm.SnelloRef)
     */
    @Override
    public void startOccurrence(SnelloRef type) throws MIOException {
        _contentHandler.startOccurrence(type);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#startScope()
     */
    @Override
    public void startScope() throws MIOException {
        _contentHandler.startScope();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#startTemplate(java.lang.String, java.util.List)
     */
    @Override
    public void startTemplate(String name, List<SnelloRef> args)
            throws MIOException {
        _contentHandler.startTemplate(name, args);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#startTopic()
     */
    @Override
    public SnelloRef startTopic() throws MIOException {
        return _contentHandler.startTopic();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#startTopic(com.semagia.mio.stm.dm.SnelloRef)
     */
    @Override
    public void startTopic(SnelloRef ref) throws MIOException {
        _contentHandler.startTopic(ref);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#startTopic(java.lang.String)
     */
    @Override
    public SnelloRef startTopic(String string) throws MIOException {
        return _contentHandler.startTopic(string);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#startVariant()
     */
    @Override
    public void startVariant() throws MIOException {
        _contentHandler.startVariant();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#subjectIdentifier(com.semagia.mio.stm.dm.SnelloRef)
     */
    @Override
    public void subjectIdentifier(SnelloRef ref) throws MIOException {
        _contentHandler.subjectIdentifier(ref);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#subjectLocator(com.semagia.mio.stm.dm.SnelloRef)
     */
    @Override
    public void subjectLocator(SnelloRef ref) throws MIOException {
        _contentHandler.subjectLocator(ref);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#subjectLocatorVariable(com.semagia.mio.stm.dm.SnelloRef)
     */
    @Override
    public void subjectLocatorVariable(SnelloRef ref) throws MIOException {
        _contentHandler.subjectLocatorVariable(ref);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#theme(com.semagia.mio.stm.dm.SnelloRef)
     */
    @Override
    public void theme(SnelloRef theme) throws MIOException {
        _contentHandler.theme(theme);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#value(com.semagia.mio.stm.dm.SnelloRef)
     */
    @Override
    public void value(SnelloRef value) throws MIOException {
        _contentHandler.value(value);
    }

}
