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
package com.semagia.mio.ctm;

import java.util.List;

import com.semagia.mio.MIOException;

/**
 * {@link IContentHandler} implementation that delegates all events to an underlying
 * {@code IContentHandler} implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 606 $ - $Date: 2011-01-20 00:48:46 +0100 (Do, 20 Jan 2011) $
 */
class DelegatingContentHandler implements IContentHandler {

    protected IContentHandler _handler;

    public DelegatingContentHandler(final IContentHandler delegate) {
        _handler = delegate;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#getParseContext()
     */
    @Override
    public IParseContext getParseContext() {
        return _handler.getParseContext();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#resolveIdentifier(java.lang.String)
     */
    @Override
    public IReference resolveIdentifier(String identifier) throws MIOException {
        return _handler.resolveIdentifier(identifier);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#ako(com.semagia.mio.ctm.IReference)
     */
    @Override
    public void ako(IReference supertype) throws MIOException {
        _handler.ako(supertype);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#isa(com.semagia.mio.ctm.IReference)
     */
    @Override
    public void isa(IReference type) throws MIOException {
        _handler.isa(type);
        
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#callTemplate(java.lang.String, java.util.List)
     */
    @Override
    public void callTemplate(String name, List<IReference> args)
            throws MIOException {
        _handler.callTemplate(name, args);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#endAssociation()
     */
    @Override
    public void endAssociation() throws MIOException {
        _handler.endAssociation();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#endName()
     */
    @Override
    public void endName() throws MIOException {
        _handler.endName();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#endOccurrence()
     */
    @Override
    public void endOccurrence() throws MIOException {
        _handler.endOccurrence();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#endScope()
     */
    @Override
    public void endScope() throws MIOException {
        _handler.endScope();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#endTemplate()
     */
    @Override
    public void endTemplate() throws MIOException {
        _handler.endTemplate();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#endTopic()
     */
    @Override
    public void endTopic() throws MIOException {
        _handler.endTopic();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#endVariant()
     */
    @Override
    public void endVariant() throws MIOException {
        _handler.endVariant();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#handleRole(com.semagia.mio.ctm.IReference, com.semagia.mio.ctm.IReference, com.semagia.mio.ctm.IReference)
     */
    @Override
    public void handleRole(IReference type, IReference player, IReference reifier) throws MIOException {
        _handler.handleRole(type, player, reifier);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#identity(com.semagia.mio.ctm.IReference)
     */
    @Override
    public void identity(IReference ref) throws MIOException {
        _handler.identity(ref);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#itemIdentifier(com.semagia.mio.ctm.IReference)
     */
    @Override
    public void itemIdentifier(IReference ref) throws MIOException {
        _handler.itemIdentifier(ref);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#itemIdentifierVariable(com.semagia.mio.ctm.IReference)
     */
    @Override
    public void itemIdentifierVariable(IReference ref) throws MIOException {
        _handler.itemIdentifierVariable(ref);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#nameValue(com.semagia.mio.ctm.IReference)
     */
    @Override
    public void nameValue(IReference value) throws MIOException {
        _handler.nameValue(value);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#reifier(com.semagia.mio.ctm.IReference)
     */
    @Override
    public void reifier(IReference reifier) throws MIOException {
        _handler.reifier(reifier);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#startAssociation(com.semagia.mio.ctm.IReference)
     */
    @Override
    public void startAssociation(IReference type) throws MIOException {
        _handler.startAssociation(type);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#startName(com.semagia.mio.ctm.IReference)
     */
    @Override
    public void startName(IReference type) throws MIOException {
        _handler.startName(type);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#startOccurrence(com.semagia.mio.ctm.IReference)
     */
    @Override
    public void startOccurrence(IReference type) throws MIOException {
        _handler.startOccurrence(type);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#startScope()
     */
    @Override
    public void startScope() throws MIOException {
        _handler.startScope();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#startTemplate(java.lang.String, java.util.List)
     */
    @Override
    public void startTemplate(String name, List<IReference> args)
            throws MIOException {
        _handler.startTemplate(name, args);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#startTopic()
     */
    @Override
    public IReference startTopic() throws MIOException {
        return _handler.startTopic();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#startTopic(com.semagia.mio.ctm.IReference)
     */
    @Override
    public void startTopic(IReference ref) throws MIOException {
        _handler.startTopic(ref);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#startTopic(java.lang.String)
     */
    @Override
    public IReference startTopic(String name) throws MIOException {
        return _handler.startTopic(name);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#startVariant()
     */
    @Override
    public void startVariant() throws MIOException {
        _handler.startVariant();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#subjectIdentifier(com.semagia.mio.ctm.IReference)
     */
    @Override
    public void subjectIdentifier(IReference ref) throws MIOException {
        _handler.subjectIdentifier(ref);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#subjectLocator(com.semagia.mio.ctm.IReference)
     */
    @Override
    public void subjectLocator(IReference ref) throws MIOException {
        _handler.subjectLocator(ref);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#subjectLocatorVariable(com.semagia.mio.ctm.IReference)
     */
    @Override
    public void subjectLocatorVariable(IReference ref) throws MIOException {
        _handler.subjectLocatorVariable(ref);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#theme(com.semagia.mio.ctm.IReference)
     */
    @Override
    public void theme(IReference theme) throws MIOException {
        _handler.theme(theme);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#value(com.semagia.mio.ctm.IReference)
     */
    @Override
    public void value(IReference value) throws MIOException {
        _handler.value(value);
    }

}
