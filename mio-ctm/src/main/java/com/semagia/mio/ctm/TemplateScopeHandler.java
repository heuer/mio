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

import java.util.Collections;
import java.util.List;

import com.semagia.mio.MIOException;
import com.semagia.mio.helpers.Literal;
import com.semagia.mio.helpers.SimpleMapHandler;
import com.semagia.mio.voc.XSD;

/**
 * {@link IContentHandler} implementation which acts within template definitions
 * and creates templates.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 606 $ - $Date: 2011-01-20 00:48:46 +0100 (Do, 20 Jan 2011) $
 */
final class TemplateScopeHandler implements IContentHandler {

    private IEnvironment _env;
    private Template _tpl;
    private int _topicIdx;


    public TemplateScopeHandler(final IEnvironment environment, final String name,
            final List<IReference> args) {
        _env = environment;
        _tpl = new Template(name, args);
        _topicIdx = 0;
    }

    @Override
    public IEnvironment getEnvironment() {
        return _env;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IContentHandler#resolveIdentifier(java.lang.String)
     */
    @Override
    public IReference resolveIdentifier(final String identifier) throws MIOException {
        throw new MIOException("Internal error: 'resolveIdentifier' is not implemented by intention");
    }

    public ITemplate dispose() {
        _env = null;
        final ITemplate tpl = _tpl;
        _tpl = null;
        return tpl;
    }

    @Override
    public void ako(IReference supertype) throws MIOException {
        _tpl.add(Template.AKO, supertype);
    }

    @Override
    public void callTemplate(String name, List<IReference> args)
            throws MIOException {
        if (_topicIdx > 0) {
            args.add(0, Reference.TOPIC_IN_FOCUS);
        }
        _tpl.add(new TemplateInvocation(name, args));
    }

    @Override
    public void endAssociation() throws MIOException {
        _tpl.add(Template.END_ASSOCIATION);
    }

    @Override
    public void endName() throws MIOException {
        _tpl.add(Template.END_NAME);
    }

    @Override
    public void endOccurrence() throws MIOException {
        _tpl.add(Template.END_OCCURRENCE);
    }

    @Override
    public void endScope() throws MIOException {
        _tpl.add(Template.END_SCOPE);
    }

    @Override
    public void endTemplate() throws MIOException {
        throw new MIOException("Illegal invocation of 'endTemplate'");
    }

    @Override
    public void endTopic() throws MIOException {
        _topicIdx--;
        _tpl.add(Template.END_TOPIC);
    }

    @Override
    public void endVariant() throws MIOException {
        _tpl.add(Template.END_VARIANT);
    }

    @Override
    public void handleRole(IReference type, IReference player,
            IReference reifier) throws MIOException {
        _tpl.add(Template.ROLE, type, player, reifier);
    }

    @Override
    public void identity(IReference ref) throws MIOException {
        _tpl.add(Template.VSID, ref);
    }

    @Override
    public void isa(IReference type) throws MIOException {
        _tpl.add(Template.ISA, type);
    }

    @Override
    public void itemIdentifier(IReference ref) throws MIOException {
        _tpl.add(Template.IID, ref);
    }

    @Override
    public void itemIdentifierVariable(IReference ref) throws MIOException {
        _tpl.add(Template.VIID, ref);
    }

    @Override
    public void nameValue(IReference value) throws MIOException {
        _tpl.add(Template.NAME_VALUE, value);
    }

    @Override
    public void reifier(IReference reifier) throws MIOException {
        _tpl.add(Template.REIFIER, reifier);
    }

    @Override
    public void startAssociation(IReference type) throws MIOException {
        _tpl.add(Template.START_ASSOCIATION, type);
    }

    @Override
    public void startName(IReference type) throws MIOException {
        _tpl.add(Template.START_NAME, type);
    }

    @Override
    public void startOccurrence(IReference type) throws MIOException {
        _tpl.add(Template.START_OCCURRENCE, type);
    }

    @Override
    public void startScope() throws MIOException {
        _tpl.add(Template.START_SCOPE);
    }

    @Override
    public void startTemplate(String name, List<IReference> args)
            throws MIOException {
        throw new MIOException("Illegal invocation of 'startTemplate'");
    }

    @Override
    public IReference startTopic() throws MIOException {
        return startTopic((String) null);
    }

    @Override
    public void startTopic(final IReference ref) throws MIOException {
        _topicIdx++;
        _tpl.add(Template.START_TOPIC, ref);
    }

    @Override
    public IReference startTopic(final String name) throws MIOException {
        final IReference identity = name == null 
                ? Reference.createWildcard(_tpl.getName() + "_" + _topicIdx)
                : Reference.createNamedWildcard(name);
        startTopic(identity);
        return identity;
    }

    @Override
    public void startVariant() throws MIOException {
        _tpl.add(Template.START_VARIANT);
    }

    @Override
    public void subjectIdentifier(IReference ref) throws MIOException {
        _tpl.add(Template.SID, ref);
    }

    @Override
    public void subjectLocator(IReference ref) throws MIOException {
        _tpl.add(Template.SLO, ref);
    }

    @Override
    public void subjectLocatorVariable(IReference ref) throws MIOException {
        _tpl.add(Template.VSLO, ref);
    }

    @Override
    public void theme(IReference theme) throws MIOException {
        _tpl.add(Template.THEME, theme);
    }

    @Override
    public void value(IReference value) throws MIOException {
        _tpl.add(Template.VALUE, value);
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
            VSID = 19,
            SLO = 20,
            VSLO = 21,
            IID = 22,
            VIID = 23,
            ISA = 24,
            AKO = 25,
            TEMPLATE_INVOCATION = 26
        ;

        private final String _name;
        private final List<IReference> _args;
        private byte[] _states;
        private IReference[] _values;
        private TemplateInvocation[] _tplCalls;
        private int _stateSize;
        private int _valueSize;
        private int _tplSize;

        public Template(final String name, final List<IReference> args) {
            _name = name;
            _args = Collections.unmodifiableList(args);
            _stateSize = 0;
            _valueSize = 0;
            _tplSize = 0;
            // TODO: Define good default values here.
            _states = new byte[12];
            _values = new IReference[8];
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

        void add(final byte state, final IReference reference) {
            add(state);
            _addValue(reference);
        }

        void add(final byte state, final IReference type, final IReference player, final IReference reifier) {
            add(state);
            _addValue(type);
            _addValue(player);
            _addValue(reifier);
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

        private void _addValue(final IReference ref) {
            if (_values.length == _valueSize) {
                IReference[] values = new IReference[_values.length*2];
                System.arraycopy(_values, 0, values, 0, _values.length);
                _values = values;
            }
            _values[_valueSize] = ref;
            _valueSize++;
        }

        /* (non-Javadoc)
         * @see com.semagia.mio.ctm.ITemplate#getArity()
         */
        @Override
        public int getArity() {
            return _args.size();
        }

        /* (non-Javadoc)
         * @see com.semagia.mio.ctm.ITemplate#getName()
         */
        @Override
        public String getName() {
            return _name;
        }

        public void execute(final ITemplateContext ctx) throws MIOException {
            final SimpleMapHandler handler = ctx.getMapHandler();
            int tplIdx = 0;
            int offset = 0;
            for (int i=0; i<_stateSize; i++) {
                switch (_states[i]) {
                    case START_TOPIC:
                        IReference identity = ctx.getTopicReference(_values[offset]);
                        ctx.pushFocus(identity);
                        handler.startTopic(identity);
                        offset++;
                        break;
                    case END_TOPIC:
                        ctx.popFocus();
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
                        IReference reifier = _values[offset];
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
                        handler.ako(ctx.getFocus(), ctx.getTopicReference(_values[offset]));
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
                        IReference ref = _values[offset];
                        if (ref.isIRI()) {
                            handler.value(ref.getIRI(), XSD.ANY_URI);
                        }
                        else {
                            final Literal lit = ctx.getLiteral(ref);
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
                    case VSID:
                        IReference sid = ctx.getTopicReference(_values[offset]);
                        if (!sid.isIRI()) {
                            throw new MIOException("Expected an IRI, got: " + sid);
                        }
                        handler.subjectIdentifier(sid.getIRI());
                        offset++;
                        break;
                    case SLO:
                        handler.subjectLocator(_values[offset].getIRI());
                        offset++;
                        break;
                    case VSLO:
                        handler.subjectLocator(ctx.getIRI(_values[offset]));
                        offset++;
                        break;
                    case IID:
                        handler.itemIdentifier(_values[offset].getIRI());
                        offset++;
                        break;
                    case VIID:
                        handler.itemIdentifier(ctx.getIRI(_values[offset]));
                        offset++;
                        break;
                    case TEMPLATE_INVOCATION:
                        _tplCalls[tplIdx].execute(ctx.getEnvironment());
                        tplIdx++;
                        break;
                    default: 
                        throw new MIOException("Internal error: Unexpected state '" + _states[i] + "'");
                }
            }
        }

    }

    /**
     * Represents a CTM template invocation.
     */
    public static final class TemplateInvocation {
        
        private static final IReference[] _EMPTY_REFS = new IReference[0];
        
        private final String _name;
        private final IReference[] _args;
        private final IReference[] _bindings;
        private Template _tpl;

        public TemplateInvocation(final String name, final List<IReference> args) {
            _name = name;
            _bindings = args.isEmpty() ? _EMPTY_REFS : args.toArray(new IReference[args.size()]);
            _args = _bindings.length == 0 ? _EMPTY_REFS : new IReference[_bindings.length];
        }

        public void execute(final IEnvironment environment) throws MIOException {
            final Environment env = (Environment) environment;
            if (_tpl == null) {
                _initTemplate((Template) env.getTemplate(_name, _bindings.length));
            }
            final ITemplateContext ctx = env.createTemplateContext(_args, _bindings);
            env.pushTemplateContext(ctx);
            _tpl.execute(ctx);
            env.popTemplateContext();
        }

        private void _initTemplate(final Template tpl) {
            _tpl = tpl;
            for (int i=0; i<_bindings.length; i++) {
                _args[i] = _tpl._args.get(i);
            }
        }
    }

}
