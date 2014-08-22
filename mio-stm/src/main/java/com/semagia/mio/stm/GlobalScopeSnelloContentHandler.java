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
package com.semagia.mio.stm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.semagia.mio.MIOException;
import com.semagia.mio.helpers.SimpleMapHandler;
import com.semagia.mio.helpers.Literal;
import com.semagia.mio.voc.XSD;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 469 $ - $Date: 2010-09-08 12:39:06 +0200 (Mi, 08 Sep 2010) $
 */
final class GlobalScopeSnelloContentHandler implements ISnelloContentHandler {

    private GlobalParserEnvironment _env;
    private SnelloRef[] _topicStack;
    private int _topicIdx;
    private Map<String, SnelloRef> _name2Ref;

    GlobalScopeSnelloContentHandler(GlobalParserEnvironment env) {
        _env = env;
        _topicStack = new SnelloRef[2];
        _topicIdx = -1;
        _name2Ref = new HashMap<String, SnelloRef>();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#getParserEnvironment()
     */
    @Override
    public GlobalParserEnvironment getParserEnvironment() {
        return _env;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#startScope()
     */
    @Override
    public void startScope() throws MIOException {
        _handler().startScope();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#endScope()
     */
    @Override
    public void endScope() throws MIOException {
        _handler().endScope();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#theme(com.semagia.mio.stm.dm.SnelloRef)
     */
    @Override
    public void theme(SnelloRef theme) throws MIOException {
        _handler().theme(theme);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#startTopic()
     */
    @Override
    public SnelloRef startTopic() throws MIOException {
        SnelloRef ref = _env.getRuntime().nextTopicId();
        startTopic(ref);
        return ref;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#startTopic(java.lang.String)
     */
    @Override
    public SnelloRef startTopic(String name) throws MIOException {
        SnelloRef ref = _getNamedRef(name);
        startTopic(ref);
        return ref;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISTMContentHandler#startTopic(com.semagia.mio.stm.dm.Ref)
     */
    @Override
    public void startTopic(SnelloRef ref) throws MIOException {
        _handler().startTopic(ref);
        if (_topicStack.length == _topicIdx+1) {
            SnelloRef[] topics = new SnelloRef[_topicStack.length*2];
            System.arraycopy(_topicStack, 0, topics, 0, _topicStack.length);
            _topicStack = topics;
        }
        _topicIdx++;
        _topicStack[_topicIdx] = ref;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISTMContentHandler#endTopic()
     */
    @Override
    public void endTopic() throws MIOException {
        _topicStack[_topicIdx] = null;
        _topicIdx--;
        _handler().endTopic();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISTMContentHandler#addSubjectIdentifier(com.semagia.mio.stm.dm.Ref)
     */
    @Override
    public void subjectIdentifier(SnelloRef ref) throws MIOException {
        _handler().subjectIdentifier(_createLocator(ref));
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISTMContentHandler#addSubjectLocator(com.semagia.mio.stm.dm.Ref)
     */
    @Override
    public void subjectLocator(SnelloRef ref) throws MIOException {
        _handler().subjectLocator(_createLocator(ref));
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISTMContentHandler#addItemIdentifier(com.semagia.mio.stm.runtime.Ref)
     */
    @Override
    public void itemIdentifier(SnelloRef ref) throws MIOException {
        _handler().itemIdentifier(_createLocator(ref));
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#addPrefix(java.lang.String, java.lang.String)
     */
    @Override
    public void addPrefix(String name, String iri) throws MIOException {
        _env.registerPrefix(name, iri);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#addAlias(java.lang.String, com.semagia.mio.stm.dm.SnelloRef)
     */
    @Override
    public void addAlias(String name, SnelloRef ref) throws MIOException {
        _env.registerAlias(name, ref);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISTMContentHandler#callTemplate(java.lang.String, java.util.List)
     */
    @Override
    public void callTemplate(String name, List<SnelloRef> args) throws MIOException {
        if (_topicIdx > -1) {
            args.add(0, _topicStack[_topicIdx]);
        }
        new TemplateInvocation(name, args).execute(_env.getRuntime());
    }

    /**
     * 
     *
     * @param name
     * @return
     */
    private SnelloRef _getNamedRef(String name) {
        SnelloRef ref = _name2Ref.get(name);
        if (ref == null) {
            ref = _env.getRuntime().nextTopicId(name);
            _name2Ref.put(name, ref);
        }
        return ref;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#startAssociation(com.semagia.mio.stm.dm.SnelloRef)
     */
    @Override
    public void startAssociation(SnelloRef type) throws MIOException {
        _handler().startAssociation(type);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#endAssociation()
     */
    @Override
    public void endAssociation() throws MIOException {
        _handler().endAssociation();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#handleRole(com.semagia.mio.stm.dm.SnelloRef, com.semagia.mio.stm.dm.SnelloRef, com.semagia.mio.stm.dm.SnelloRef)
     */
    @Override
    public void handleRole(SnelloRef type, SnelloRef player, SnelloRef reifier)
            throws MIOException {
        SimpleMapHandler handler = _handler();
        handler.startRole(type);
        handler.player(player);
        reifier(reifier);
        handler.endRole();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#reifier(com.semagia.mio.stm.dm.SnelloRef)
     */
    @Override
    public void reifier(SnelloRef reifier) throws MIOException {
        if (reifier != null) {
            _handler().reifier(reifier);
        }
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#startOccurrence(com.semagia.mio.stm.dm.SnelloRef)
     */
    @Override
    public void startOccurrence(SnelloRef type) throws MIOException {
        _handler().startOccurrence(type);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#endOccurrence()
     */
    @Override
    public void endOccurrence() throws MIOException {
        _handler().endOccurrence();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#startName(com.semagia.mio.stm.dm.SnelloRef)
     */
    @Override
    public void startName(SnelloRef type) throws MIOException {
        _handler().startName(type);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#endName()
     */
    @Override
    public void endName() throws MIOException {
        _handler().endName();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#value(com.semagia.mio.stm.dm.SnelloRef)
     */
    @Override
    public void value(SnelloRef value) throws MIOException {
        if (value.isIRI()) {
            _handler().value(value.getIRI(), XSD.ANY_URI);
        }
        else {
            Literal lit = value.getLiteral();
            _handler().value(lit.getValue(), lit.getDatatype());
        }
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#nameValue(com.semagia.mio.stm.dm.SnelloRef)
     */
    @Override
    public void nameValue(SnelloRef value) throws MIOException {
        _handler().value(value.getLiteral().getValue());
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#startVariant()
     */
    @Override
    public void startVariant() throws MIOException {
        _handler().startVariant();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#endVariant()
     */
    @Override
    public void endVariant() throws MIOException {
        _handler().endVariant();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISTMContentHandler#addIdentity(com.semagia.mio.stm.runtime.Ref)
     */
    @Override
    public void identity(SnelloRef ref) throws MIOException {
        _reportIllegalState("addIdentity");
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#addItemIdentifierVariable(com.semagia.mio.stm.dm.Ref)
     */
    @Override
    public void itemIdentifierVariable(SnelloRef ref) throws MIOException {
        _reportIllegalState("addItemIdentifierVariable");
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISTMContentHandler#addSubjectLocatorVariable(com.semagia.mio.stm.runtime.Ref)
     */
    @Override
    public void subjectLocatorVariable(SnelloRef ref) throws MIOException {
        _reportIllegalState("addSubjectLocatorVariable");
    }
    
    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISTMContentHandler#startTemplate(java.lang.String, java.util.List)
     */
    @Override
    public void startTemplate(String name, List<SnelloRef> args) throws MIOException {
        _reportIllegalState("startTemplate");
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISTMContentHandler#endTemplate()
     */
    @Override
    public void endTemplate() throws MIOException {
        _reportIllegalState("endTemplate");
    }

    /**
     * 
     *
     * @return
     */
    private SimpleMapHandler _handler() {
        return _env.getRuntime().getMapHandler();
    }

    /**
     * 
     *
     * @param ref
     * @return
     */
    private String _createLocator(SnelloRef ref) {
        return ref.getString();
    }

    /**
     * 
     *
     * @param method
     * @throws MIOException
     */
    private static void _reportIllegalState(String method) throws MIOException {
        throw new MIOException("Unexpected invocation of '" + method + "'");
    }

}
