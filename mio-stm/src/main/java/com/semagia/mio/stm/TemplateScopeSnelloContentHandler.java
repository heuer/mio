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

import java.util.ArrayList;
import java.util.List;

import com.semagia.mio.MIOException;
import com.semagia.mio.helpers.Literal;
import com.semagia.mio.helpers.SimpleMapHandler;
import com.semagia.mio.voc.XSD;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 469 $ - $Date: 2010-09-08 12:39:06 +0200 (Mi, 08 Sep 2010) $
 */
final class TemplateScopeSnelloContentHandler implements ISnelloContentHandler {

    private IParserEnvironment _env;
    private final String _name;
    private final Template _tpl;
    private int _topicDepth;
    private int _topicCounter;

    public TemplateScopeSnelloContentHandler(GlobalParserEnvironment env, String name, List<SnelloRef> args) {
        _env = new LocalParserEnvironment(env);
        _name = name;
        _tpl = new Template(name, args);
    }

    
    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISTMContentHandler#getEnvironment()
     */
    @Override
    public IParserEnvironment getParserEnvironment() {
        return _env;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISTMContentHandler#addPrefix(java.lang.String, java.lang.String)
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
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#startTopic()
     */
    @Override
    public SnelloRef startTopic() throws MIOException {
        _topicDepth++;
        SnelloRef ref = SnelloRef.createInternalWildcard(_topicCounter++ + _name);
        _tpl.add(Template.START_TOPIC, ref);
        return ref;
    }


    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#startTopic(java.lang.String)
     */
    @Override
    public SnelloRef startTopic(String name) throws MIOException {
        _topicDepth++;
        SnelloRef ref = SnelloRef.createWildcard(name);
        _tpl.add(Template.START_TOPIC, ref);
        return ref;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISTMContentHandler#startTopic(com.semagia.mio.stm.runtime.Ref)
     */
    @Override
    public void startTopic(SnelloRef ref) throws MIOException {
        _topicDepth++;
        _tpl.add(Template.START_TOPIC, ref);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISTMContentHandler#endTopic()
     */
    @Override
    public void endTopic() throws MIOException {
        _tpl.add(Template.END_TOPIC);
        _topicDepth--;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISTMContentHandler#addIdentity(com.semagia.mio.stm.runtime.Ref)
     */
    @Override
    public void identity(SnelloRef ref) throws MIOException {
        _tpl.add(Template.IDENTITY, ref);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISTMContentHandler#addItemIdentifier(com.semagia.mio.stm.runtime.Ref)
     */
    @Override
    public void itemIdentifier(SnelloRef ref) throws MIOException {
        _tpl.add(Template.IID, ref);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISTMContentHandler#addSubjectIdentifier(com.semagia.mio.stm.runtime.Ref)
     */
    @Override
    public void subjectIdentifier(SnelloRef ref) throws MIOException {
        _tpl.add(Template.SID, ref);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISTMContentHandler#addSubjectLocator(com.semagia.mio.stm.runtime.Ref)
     */
    @Override
    public void subjectLocator(SnelloRef ref) throws MIOException {
        _tpl.add(Template.SLO, ref);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#itemIdentifierVariable(com.semagia.mio.stm.dm.SnelloRef)
     */
    @Override
    public void itemIdentifierVariable(SnelloRef ref) throws MIOException {
        _tpl.add(Template.IID, ref);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#subjectLocatorVariable(com.semagia.mio.stm.dm.SnelloRef)
     */
    @Override
    public void subjectLocatorVariable(SnelloRef ref) throws MIOException {
        _tpl.add(Template.VSLO, ref);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISTMContentHandler#callTemplate(java.lang.String, java.util.List)
     */
    @Override
    public void callTemplate(String name, List<SnelloRef> args) throws MIOException {
        if (_topicDepth > 0) {
            args.add(0, IContext.TOPIC_IN_FOCUS);
        }
        _tpl.add(new TemplateInvocation(name, args));
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#startAssociation(com.semagia.mio.stm.dm.SnelloRef)
     */
    @Override
    public void startAssociation(SnelloRef type) throws MIOException {
        _tpl.add(Template.START_ASSOCIATION, type);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#endAssociation()
     */
    @Override
    public void endAssociation() throws MIOException {
        _tpl.add(Template.END_ASSOCIATION);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#handleRole(com.semagia.mio.stm.dm.SnelloRef, com.semagia.mio.stm.dm.SnelloRef, com.semagia.mio.stm.dm.SnelloRef)
     */
    @Override
    public void handleRole(SnelloRef type, SnelloRef player, SnelloRef reifier)
            throws MIOException {
        _tpl.add(Template.ROLE, type, player, reifier);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#startName(com.semagia.mio.stm.dm.SnelloRef)
     */
    @Override
    public void startName(SnelloRef type) throws MIOException {
        _tpl.add(Template.START_NAME, type);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#endName()
     */
    @Override
    public void endName() throws MIOException {
        _tpl.add(Template.END_NAME);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#startName(com.semagia.mio.stm.dm.SnelloRef)
     */
    @Override
    public void startOccurrence(SnelloRef type) throws MIOException {
        _tpl.add(Template.START_OCCURRENCE, type);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#endName()
     */
    @Override
    public void endOccurrence() throws MIOException {
        _tpl.add(Template.END_OCCURRENCE);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#startVariant()
     */
    @Override
    public void startVariant() throws MIOException {
        _tpl.add(Template.START_VARIANT);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#startVariant()
     */
    @Override
    public void endVariant() throws MIOException {
        _tpl.add(Template.END_VARIANT);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#startScope()
     */
    @Override
    public void startScope() throws MIOException {
        _tpl.add(Template.START_SCOPE);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#startScope()
     */
    @Override
    public void endScope() throws MIOException {
        _tpl.add(Template.END_SCOPE);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#theme(com.semagia.mio.stm.dm.SnelloRef)
     */
    @Override
    public void theme(SnelloRef theme) throws MIOException {
        _tpl.add(Template.THEME, theme);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#reifier(com.semagia.mio.stm.dm.SnelloRef)
     */
    @Override
    public void reifier(SnelloRef reifier) throws MIOException {
        _tpl.add(Template.REIFIER, reifier);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#nameValue(com.semagia.mio.stm.dm.SnelloRef)
     */
    @Override
    public void nameValue(SnelloRef value) throws MIOException {
        _tpl.add(Template.NAME_VALUE, value);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.ISnelloContentHandler#value(com.semagia.mio.stm.dm.SnelloRef)
     */
    @Override
    public void value(SnelloRef value) throws MIOException {
        _tpl.add(Template.VALUE, value);
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

    public String getName() {
        return _name;
    }

    public ITemplate getTemplate() {
        return _tpl;
    }

    private static void _reportIllegalState(String method) throws MIOException {
        throw new MIOException("Unexpected invocation of '" + method + "'");
    }

    private static final class Template implements ITemplate {

        private static final byte 
            START_TOPIC = 1, END_TOPIC = 2,
            START_ASSOCIATION = 3, END_ASSOCIATION = 4,
            ROLE = 5,
            START_OCCURRENCE = 6, END_OCCURRENCE = 7,
            START_NAME = 8, END_NAME = 9,
            START_VARIANT = 10, END_VARIANT = 11,
            START_SCOPE = 12, END_SCOPE = 13,
            THEME = 14,
            REIFIER = 15,
            VALUE = 16,
            NAME_VALUE = 17,
            SID = 18,
            IDENTITY = 19,
            SLO = 20,
            VSLO = 21,
            IID = 22,
            VIID = 23,
            ISA = 24,
            AKO = 25,
            TEMPLATE_INVOCATION = 26
        ;

        private final String _name;
        private final String[] _args;
        private final SnelloRef[] _defaults;

        private int _stateSize;
        private int _valueSize;
        private TemplateInvocation[] _tplCalls;
        private int _tplSize;
        private byte[] _states;

        private SnelloRef[] _values;

        public Template(final String name, final List<SnelloRef> args) {
            _name = name;
            _args = new String[args.size()];
            List<SnelloRef> defaults = new ArrayList<SnelloRef>();
            for (int i=0; i<_args.length; i++) {
                SnelloRef ref = args.get(i);
                if (ref.isKeyValue()) {
                    KeyValue kv = ref.getKeyValue();
                    _args[i] = kv.getKey();
                    defaults.add(kv.getValue());
                }
                else {
                    _args[i] = ref.getString();
                }
            }
            _defaults = defaults.toArray(new SnelloRef[defaults.size()]);
            _stateSize = 0;
            _valueSize = 0;
            _tplSize = 0;
            // TODO: Define good default values here.
            _states = new byte[12];
            _values = new SnelloRef[8];
            _tplCalls = new TemplateInvocation[4];
        }

        void add(final byte state) {
            if (_states.length == _stateSize) {
                byte[] states = new byte[_states.length*2];
                System.arraycopy(_states, 0, states, 0, _states.length);
                _states = states;
            }
            _states[_stateSize] = state;
            _stateSize++;
        }

        void add(final TemplateInvocation invocation) {
            add(TEMPLATE_INVOCATION);
            if (_tplCalls.length == _tplSize) {
                TemplateInvocation[] calls = new TemplateInvocation[_tplCalls.length*2];
                System.arraycopy(_tplCalls, 0, calls, 0, _tplCalls.length);
                _tplCalls = calls;
            }
            _tplCalls[_tplSize] = invocation;
            _tplSize++;
        }

        void add(final byte state, final SnelloRef ref) {
            add(state);
            _addValue(ref);
        }

        void add(final byte state, final SnelloRef type, final SnelloRef player, final SnelloRef reifier) {
            add(state);
            _addValue(type);
            _addValue(player);
            _addValue(reifier);
        }

        private void _addValue(final SnelloRef ref) {
            if (_values.length == _valueSize) {
                SnelloRef[] values = new SnelloRef[_values.length*2];
                System.arraycopy(_values, 0, values, 0, _values.length);
                _values = values;
            }
            _values[_valueSize] = ref;
            _valueSize++;
        }

        @Override
        public int getArity() {
            System.out.println(_name + ": " + (_defaults.length + _args.length));
            return _defaults.length + _args.length;
        }

        /* (non-Javadoc)
         * @see com.semagia.mio.stm.dm.ITemplate#getParameterNames()
         */
        @Override
        public String[] getParameterNames() {
            return _args;
        }

        /* (non-Javadoc)
         * @see com.semagia.mio.stm.dm.ITemplate#getDefaultValues()
         */
        @Override
        public SnelloRef[] getDefaultValues() {
            return _defaults;
        }

        /* (non-Javadoc)
         * @see com.semagia.mio.stm.dm.ITemplate#execute(com.semagia.mio.stm.dm.IContext)
         */
        @Override
        public void execute(final SimpleMapHandler handler, final IContext ctx) throws MIOException {
            int tplIdx = 0;
            int offset = 0;
            for (int i=0; i<_stateSize; i++) {
                switch (_states[i]) {
                    case START_TOPIC:
                        Context context  = (Context) ctx;
                        SnelloRef focus = (SnelloRef) ctx.getTopicReference(_values[offset]);
                        context.pushFocus(focus);
                        handler.startTopic(focus);
                        offset++;
                        break;
                    case END_TOPIC:
                        ((Context) ctx).popFocus();
                        handler.endTopic();
                        break;
                    case START_ASSOCIATION:
                        handler.startAssociation(ctx.getTopicReference(_values[offset]));
                        offset++;
                        break;
                    case END_ASSOCIATION:
                        handler.endAssociation();
                        break;
                    case ROLE:
                        handler.startRole(ctx.getTopicReference(_values[offset]));
                        offset++;
                        handler.player(ctx.getTopicReference(_values[offset]));
                        offset++;
                        SnelloRef reifier = _values[offset];
                        if (reifier != null) {
                            handler.reifier(ctx.getTopicReference(reifier));
                        }
                        offset++;
                        handler.endRole();
                        break;
                    case START_OCCURRENCE:
                        handler.startOccurrence(ctx.getTopicReference(_values[offset]));
                        offset++;
                        break;
                    case END_OCCURRENCE:
                        handler.endOccurrence();
                        break;
                    case START_NAME:
                        handler.startName(ctx.getTopicReference(_values[offset]));
                        offset++;
                        break;
                    case END_NAME:
                        handler.endName();
                        break;
                    case START_VARIANT:
                        handler.startVariant();
                        break;
                    case END_VARIANT:
                        handler.endVariant();
                        break;
                    case START_SCOPE:
                        handler.startScope();
                        break;
                    case END_SCOPE:
                        handler.endScope();
                        break;
                    case ISA:
                        handler.isa(ctx.getTopicReference(_values[offset]));
                        offset++;
                        break;
                    case AKO:
                        handler.ako(ctx.getTopicReference(IContext.TOPIC_IN_FOCUS), ctx.getTopicReference(_values[offset]));
                        offset++;
                        break;
                    case REIFIER:
                        handler.reifier(ctx.getTopicReference(_values[offset]));
                        offset++;
                        break;
                    case THEME:
                        handler.theme(ctx.getTopicReference(_values[offset]));
                        offset++;
                        break;
                    case VALUE:
                        SnelloRef val = _values[offset];
                        if (val.isIRI()) {
                            handler.value(val.getIRI(), XSD.ANY_URI);
                        }
                        else {
                            final Literal lit = ctx.getLiteral(val);
                            handler.value(lit.getValue(), lit.getDatatype());
                        }
                        offset++;
                        break;
                    case NAME_VALUE:
                        Literal lit = ctx.getLiteral(_values[offset]);
                        handler.value(lit.getValue());
                        offset++;
                        break;
                    case SID:
                        handler.subjectIdentifier(_values[offset].getIRI());
                        offset++;
                        break;
                    case IDENTITY:
                        SnelloRef ident = (SnelloRef) ctx.getTopicReference(_values[offset]);
                        if (ident.isSID()) {
                            handler.subjectIdentifier(ident.getString());
                        }
                        else if (ident.isSLO()) {
                            handler.subjectLocator(ident.getString());
                        }
                        else if (ident.isIID()) {
                            handler.itemIdentifier(ident.getString());
                        }
                        offset++;
                        break;
                    case SLO:
                        handler.subjectLocator(_values[offset].getIRI());
                        offset++;
                        break;
                    case VSLO:
                        handler.subjectLocator(ctx.getLocator(_values[offset].getString()));
                        offset++;
                        break;
                    case IID:
                        handler.itemIdentifier(_values[offset].getIRI());
                        offset++;
                        break;
                    case VIID:
                        handler.itemIdentifier(ctx.getLocator(_values[offset].getString()));
                        offset++;
                        break;
                    case TEMPLATE_INVOCATION:
                        _tplCalls[tplIdx].execute(((Context) ctx).getRuntime());
                        tplIdx++;
                        break;
                    default: 
                        throw new MIOException("Internal error: Unexpected state '" + _states[i] + "'");
                }
            }
        }

    }

}
