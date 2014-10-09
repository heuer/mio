/*
 * Copyright 2007 - 2014 Lars Heuer (heuer[at]semagia.com)
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
import com.semagia.mio.ctm.TemplateScopeHandler.TemplateInvocation;
import com.semagia.mio.helpers.Literal;
import com.semagia.mio.helpers.SimpleMapHandler;
import com.semagia.mio.voc.XSD;

/**
 * {@link IContentHandler} implementation that handles everything outside of 
 * CTM templates.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
final class GlobalScopeHandler implements IContentHandler {

    private IReference[] _topicStack;
    private int _topicIdx;
    private final IParseContext _ctx;

    public GlobalScopeHandler(final IParseContext ctx) {
        _ctx = ctx;
        _topicStack = new IReference[2];
        _topicIdx = -1;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#getParseContext()
     */
    @Override
    public IParseContext getParseContext() {
        return _ctx;
    }

    private SimpleMapHandler _handler() {
        return _ctx.getMapHandler();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#resolveIdentifier(java.lang.String)
     */
    @Override
    public IReference resolveIdentifier(String identifier) throws MIOException {
        throw new MIOException("Internal error: 'resolveIdentifier' is not implemented by intention");
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#callTemplate(java.lang.String, java.util.List)
     */
    @Override
    public void callTemplate(final String name, final List<IReference> args)
            throws MIOException {
        if (_topicIdx > -1) {
            args.add(0, _topicStack[_topicIdx]);
        }
        final TemplateInvocation tplCall = new TemplateInvocation(name, args);
        tplCall.execute(_ctx);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#ako(com.semagia.mio.ctm.IReference)
     */
    @Override
    public void ako(final IReference supertype) throws MIOException {
        _handler().ako(_topicStack[_topicIdx], supertype);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#isa(com.semagia.mio.ctm.IReference)
     */
    @Override
    public void isa(final IReference type) throws MIOException {
        _handler().isa(type);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#endAssociation()
     */
    @Override
    public void endAssociation() throws MIOException {
        _handler().endAssociation();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#endName()
     */
    @Override
    public void endName() throws MIOException {
        _handler().endName();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#endOccurrence()
     */
    @Override
    public void endOccurrence() throws MIOException {
        _handler().endOccurrence();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#endScope()
     */
    @Override
    public void endScope() throws MIOException {
        _handler().endScope();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#endTopic()
     */
    @Override
    public void endTopic() throws MIOException {
        _topicStack[_topicIdx] = null;
        _topicIdx--;
        _handler().endTopic();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#endVariant()
     */
    @Override
    public void endVariant() throws MIOException {
        _handler().endVariant();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#handleRole(com.semagia.mio.ctm.IReference, com.semagia.mio.ctm.IReference, com.semagia.mio.ctm.IReference)
     */
    @Override
    public void handleRole(IReference type, IReference player, IReference reifier) throws MIOException {
        final SimpleMapHandler handler = _handler();
        handler.startRole(type);
        handler.player(player);
        if (reifier != null) {
            handler.reifier(reifier);
        }
        handler.endRole();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#itemIdentifier(com.semagia.mio.ctm.IReference)
     */
    @Override
    public void itemIdentifier(IReference iid) throws MIOException {
        _handler().itemIdentifier(iid.getIRI());
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#nameValue(com.semagia.mio.ctm.IReference)
     */
    @Override
    public void nameValue(final IReference value) throws MIOException {
        _handler().value(value.getLiteral().getValue());
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#reifier(com.semagia.mio.ctm.IReference)
     */
    @Override
    public void reifier(IReference reifier) throws MIOException {
        if (reifier != null) {
            _handler().reifier(reifier);
        }
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#startAssociation(com.semagia.mio.ctm.IReference)
     */
    @Override
    public void startAssociation(IReference type) throws MIOException {
        _handler().startAssociation(type);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#startName(com.semagia.mio.ctm.IReference)
     */
    @Override
    public void startName(IReference type) throws MIOException {
        _handler().startName(type);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#startOccurrence(com.semagia.mio.ctm.IReference)
     */
    @Override
    public void startOccurrence(IReference type) throws MIOException {
        _handler().startOccurrence(type);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#startScope()
     */
    @Override
    public void startScope() throws MIOException {
        _handler().startScope();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#startTopic(com.semagia.mio.ctm.IReference)
     */
    @Override
    public void startTopic(final IReference ref) throws MIOException {
        _handler().startTopic(ref);
        if (_topicStack.length == _topicIdx+1) {
            IReference[] topics = new IReference[_topicStack.length*2];
            System.arraycopy(_topicStack, 0, topics, 0, _topicStack.length);
            _topicStack = topics;
        }
        _topicIdx++;
        _topicStack[_topicIdx] = ref;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#startTopic()
     */
    @Override
    public IReference startTopic() throws MIOException {
        return startTopic((String)null);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#startTopic(java.lang.String)
     */
    @Override
    public IReference startTopic(final String name) throws MIOException {
        final IReference ref = _ctx.getTopicByWildcard(name);
        startTopic(ref);
        return ref;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#startVariant()
     */
    @Override
    public void startVariant() throws MIOException {
        _handler().startVariant();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#subjectIdentifier(com.semagia.mio.ctm.IReference)
     */
    @Override
    public void subjectIdentifier(final IReference sid) throws MIOException {
        _handler().subjectIdentifier(sid.getIRI());
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#subjectLocator(com.semagia.mio.ctm.IReference)
     */
    @Override
    public void subjectLocator(final IReference slo) throws MIOException {
        _handler().subjectLocator(slo.getIRI());
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#theme(com.semagia.mio.ctm.IReference)
     */
    @Override
    public void theme(IReference theme) throws MIOException {
        _handler().theme(theme);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#value(com.semagia.mio.ctm.IReference)
     */
    @Override
    public void value(final IReference value) throws MIOException {
        if (value.isVariable()) {
            throw new MIOException("Variables are not allowed outside of templates. Got: $" + value.getString());
        }
        if (value.isIRI()) {
            _handler().value(value.getIRI(), XSD.ANY_URI);
        }
        else {
            final Literal lit = value.getLiteral();
            _handler().value(lit.getValue(), lit.getDatatype());
        }
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISTMContentHandler#addIdentity(com.semagia.mio.stm.runtime.Ref)
     */
    @Override
    public void identity(IReference ref) throws MIOException {
        reportIllegalState("identity");
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#addItemIdentifierVariable(com.semagia.mio.stm.dm.Ref)
     */
    @Override
    public void itemIdentifierVariable(IReference ref) throws MIOException {
        reportIllegalState("addItemIdentifierVariable");
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISTMContentHandler#addSubjectLocatorVariable(com.semagia.mio.stm.runtime.Ref)
     */
    @Override
    public void subjectLocatorVariable(IReference ref) throws MIOException {
        reportIllegalState("addSubjectLocatorVariable");
    }
    
    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISTMContentHandler#startTemplate(java.lang.String, java.util.List)
     */
    @Override
    public void startTemplate(String name, List<IReference> args) throws MIOException {
        reportIllegalState("startTemplate");
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISTMContentHandler#endTemplate()
     */
    @Override
    public void endTemplate() throws MIOException {
        reportIllegalState("endTemplate");
    }

    private static void reportIllegalState(final String method) throws MIOException {
        throw new MIOException("Illegal invocation of '" + method + "'");
    }

}
