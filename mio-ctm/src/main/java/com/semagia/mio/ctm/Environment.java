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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.semagia.mio.Context;
import com.semagia.mio.DeserializerRegistry;
import com.semagia.mio.IDeserializer;
import com.semagia.mio.IMapHandler;
import com.semagia.mio.MIOException;
import com.semagia.mio.Source;
import com.semagia.mio.Syntax;
import com.semagia.mio.ctm.api.IPrefixListener;
import com.semagia.mio.helpers.Literal;
import com.semagia.mio.helpers.Locator;
import com.semagia.mio.helpers.QName;
import com.semagia.mio.helpers.SimpleMapHandler;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 606 $ - $Date: 2011-01-20 00:48:46 +0100 (Do, 20 Jan 2011) $
 */
final class Environment implements IEnvironment {

    private static final String _SYNTAX_CTM = "http://psi.topicmaps.org/iso13250/ctm";
    private static final String _SYNTAX_XTM = "http://psi.topicmaps.org/iso13250/xtm";

    private final Map<String, String> _prefixes;
    private IPrefixListener _listener;
    private SimpleMapHandler _handler;
    private final Map<String, ITemplate> _templates;
    private final Map<String, IReference> _wc2Identity;
    private int _counter;
    private Locator _baseIRI;
    private TemplateContext _tplCtx;
    private boolean _isSubordinate;
    private Context _context;
    private List<Locator> _includedBy;

    public Environment() {
        _counter = 0;
        _prefixes = new HashMap<String, String>();
        _templates = new HashMap<String, ITemplate>();
        _wc2Identity = new HashMap<String, IReference>();
        _includedBy = null;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IEnvironment#getWildcardCounter()
     */
    @Override
    public int getWildcardCounter() {
        return _counter;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IEnvironment#setWildcardCounter(int)
     */
    @Override
    public void setWildcardCounter(int counter) {
        _counter = counter;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IEnvironment#setMapHandler(com.semagia.mio.IMapHandler)
     */
    @Override
    public void setMapHandler(final IMapHandler handler) {
        _handler = SimpleMapHandler.create(handler);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IEnvironment#getMapHandler()
     */
    @Override
    public SimpleMapHandler getMapHandler() {
        return _handler;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IEnvironment#setDocumentIRI(java.lang.String)
     */
    @Override
    public void setDocumentIRI(String baseIRI) {
        _baseIRI = Locator.create(baseIRI);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IEnvironment#setSubordinate(boolean)
     */
    @Override
    public void setSubordinate(boolean subordinate) {
        _isSubordinate = subordinate;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IEnvironment#isSubordinate()
     */
    @Override
    public boolean isSubordinate() {
        return _isSubordinate;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IEnvironment#getContext()
     */
    @Override
    public Context getContext() {
        return _context;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IEnvironment#setContext(com.semagia.mio.Context)
     */
    @Override
    public void setContext(final Context context) {
        _context = context;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IEnvironment#getIncludedBy()
     */
    @Override
    public List<Locator> getIncludedBy() {
        return _includedBy == null ? Collections.<Locator>emptyList()
                                   : _includedBy;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IEnvironment#setIncludedBy(java.util.Set)
     */
    @Override
    public void setIncludedBy(final List<Locator> includedBy) {
        _includedBy = includedBy;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IEnvironment#topicByWildcard(java.lang.String)
     */
    @Override
    public IReference topicByWildcard(final String name) {
        IReference identity = _wc2Identity.get(name);
        if (identity == null) {
            identity = _nextTopicIdentity(name);
            if (name != null) {
                _wc2Identity.put(name, identity);
            }
        }
        return identity;
    }

    private IReference _nextTopicIdentity(final String name) {
        _counter++;
        final StringBuffer sb = new StringBuffer();
        sb.append("$__" + _counter);
        if (name != null) {
            sb.append('.');
            sb.append(name);
        }
        return _includedBy != null 
                        ? Reference.createIID(_includedBy.get(0).resolve("#" + sb.toString()).toExternalForm())
                        : resolveIdentifier(sb.toString());
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IEnvironment#registerPrefix(java.lang.String, java.lang.String)
     */
    @Override
    public void registerPrefix(String prefix, String iri) throws MIOException {
        final String existing = _prefixes.put(prefix, iri);
        if (existing != null) {
            if (!existing.equals(iri)) {
                throw new MIOException("The prefix '" + prefix + "' is already bound to <" + existing + ">");
            }
            // Here we *could* inform the prefix listener (if any) about
            // the same prefix / IRI binding again, but there is no need to do it.
        }
        else if (_listener != null) {
            _listener.handleNamespace(prefix, iri);
        }
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IEnvironment#setPrefixListener(com.semagia.mio.ctm.api.IPrefixListener)
     */
    @Override
    public void setPrefixListener(final IPrefixListener listener) {
        _listener = listener;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IEnvironment#getTemplate(java.lang.String, int)
     */
    @Override
    public ITemplate getTemplate(final String name, int arity) throws MIOException {
        final String key = name + "/" + arity;
        final ITemplate tpl = _templates.get(key);
        if (tpl == null) {
            throw new MIOException("The template '" + key + "' does not exist");
        }
        return tpl;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IEnvironment#registerTemplate(com.semagia.mio.ctm.ITemplate)
     */
    @Override
    public void registerTemplate(final ITemplate template) throws MIOException {
        final String key = template.getName() + "/" + template.getArity();
        final ITemplate existing = _templates.put(key, template);
        if (existing != null) {
            throw new MIOException("A template '" + key + "' is already registered");
        }
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IEnvironment#getTemplates()
     */
    @Override
    public Collection<ITemplate> getTemplates() {
        return _templates.values();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IEnvironment#resolveIRI(java.lang.String)
     */
    @Override
    public IReference resolveIRI(final String iri) {
        return Reference.createIRI(_resolveLocator(iri).toExternalForm());
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IEnvironment#resolveIdentifier(java.lang.String)
     */
    @Override
    public IReference resolveIdentifier(final String ident) {
        return Reference.createIID(_resolveLocator("#" + ident).toExternalForm());
    }

    private Locator _resolveLocator(final String iri) {
        return _baseIRI.resolve(iri);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IEnvironment#resolveQName(java.lang.String)
     */
    @Override
    public IReference resolveQName(final String qName) throws MIOException {
        final QName qn = QName.create(qName);
        final String iri = _prefixes.get(qn.getPrefix());
        if (iri == null) {
            throw new MIOException("The prefix '" + qn.getPrefix() + "' is unknown");
        }
        return resolveIRI(iri + qn.getLocal());
    }

    public ITemplateContext createTemplateContext(final IReference[] tplParams, final IReference[] values) throws MIOException {
        Map<IReference, IReference> bindings = new HashMap<IReference, IReference>();
        for (int i=0; i<tplParams.length; i++) {
            IReference val = values[i];
            if (val.isWildcard() || val.isNamedWildcard()) {
                val = _tplCtx != null ? _tplCtx.getTopicReference(val)
                                      : _nextTopicIdentity(val.isWildcard() ? null : (String)val.getValue());
            }
            bindings.put(tplParams[i], val);
        }
        return new TemplateContext(this, _tplCtx, bindings);
    }

    public void pushTemplateContext(final ITemplateContext tplContext) {
        _tplCtx = (TemplateContext) tplContext;
    }

    public void popTemplateContext() {
        final TemplateContext currentCtx = _tplCtx;
        _tplCtx = currentCtx._parent;
        currentCtx._env = null;
        currentCtx._parent = null;
    }

    /**
     * Merges another topic map instance into the current. 
     *
     * @param iri
     * @param syntaxIRI
     * @throws IOException
     * @throws MIOException
     */
    public void mergeIn(final String iri, final String syntaxIRI) throws IOException, MIOException {
        if (_SYNTAX_CTM == syntaxIRI) {
            _mergeInCTM(iri, null);
        }
        else if (_SYNTAX_XTM == syntaxIRI){
            Context context = getContext();
            Locator docIRI = _resolveLocator(iri);
            if (context.containsIRI(docIRI.toExternalForm())) {
                return;
            }
            context.addIRI(docIRI.toExternalForm());
            final IDeserializer deser = DeserializerRegistry.getInstance().createDeserializer(Syntax.XTM);
            if (deser == null) {
                throw new MIOException("Unknown syntax '" + syntaxIRI + "'");
            }
            deser.setMapHandler(_handler);
            deser.setSubordinate(true);
            deser.setContext(context);
            deser.parse(new Source(docIRI.toExternalForm()));
        }
        else {
            throw new MIOException("Unknown syntax '" + syntaxIRI + "'");
        }
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IEnvironment#include(java.lang.String)
     */
    @Override
    public void include(final String iri) throws IOException, MIOException {
        final Locator loc =  _resolveLocator(iri);
        if (_includedBy != null && _includedBy.contains(loc)) {
            return;
        }
        final List<Locator> includedBy = _includedBy != null ? new ArrayList<Locator>(_includedBy) : new ArrayList<Locator>(2);
        includedBy.add(_baseIRI);
        _mergeInCTM(iri, includedBy);
    }

    /**
     * Merges another topic map instance into the current.
     *
     * @param iri
     * @param included
     * @throws IOException
     * @throws MIOException
     */
    private final void _mergeInCTM(final String iri, final List<Locator> included) throws IOException, MIOException {
        final Context context = getContext();
        final Locator docIRI = _resolveLocator(iri);
        if (context.containsIRI(docIRI.toExternalForm())) {
            return;
        }
        context.addIRI(docIRI.toExternalForm());
        final CTMDeserializer deser = new CTMDeserializer();
        deser.setProperty("http://psi.semagia.com/mio/property/ctm/prefix-listener", _listener);
        deser.setMapHandler(_handler);
        deser.setSubordinate(true);
        deser.setContext(context);
        final CTMParser parser = deser.getParser();
        if (included != null) {
            parser.setWildcardCounter(_counter);
            parser.setIncludedBy(included);
        }
        deser.parse(new Source(docIRI.toExternalForm()));
        if (included != null) {
            _counter = parser.getWildcardCounter();
            for (ITemplate tpl: parser.getContentHandler().getEnvironment().getTemplates()) {
                registerTemplate(tpl);
            }
        }
    }


    private static final class TemplateContext implements ITemplateContext {

        private Environment _env;
        private TemplateContext _parent;
        private IReference[] _focus;
        private int _focusIdx;
        private Map<IReference, IReference> _bindings;

        public TemplateContext(final Environment environment, final TemplateContext tplCtx, final Map<IReference, IReference> bindings) {
            _env = environment;
            _parent = tplCtx;
            _focus = new IReference[2];
            _focusIdx = -1;
            _bindings = bindings;
        }

        /* (non-Javadoc)
         * @see com.semagia.mio.ctm.ITemplateContext#getEnvironment()
         */
        @Override
        public IEnvironment getEnvironment() {
            return _env;
        }

        /* (non-Javadoc)
         * @see com.semagia.mio.ctm.ITemplateContext#getLiteral(com.semagia.mio.ctm.IReference)
         */
        @Override
        public Literal getLiteral(final IReference ref) throws MIOException {
            final IReference res = ref.isVariable() ? _getReferenceByVariable(ref) : ref;
            return res.isIRI() ? Literal.createIRI(res.getIRI()) : res.getLiteral();
        }

        /* (non-Javadoc)
         * @see com.semagia.mio.ctm.ITemplateContext#getLocator(java.lang.String)
         */
        @Override
        public String getIRI(final IReference var) throws MIOException {
            return _getReferenceByVariable(var).getIRI();
        }

        /* (non-Javadoc)
         * @see com.semagia.mio.ctm.ITemplateContext#getMapHandler()
         */
        @Override
        public SimpleMapHandler getMapHandler() {
            return _env.getMapHandler();
        }

        /* (non-Javadoc)
         * @see com.semagia.mio.ctm.ITemplateContext#getTopicReference(com.semagia.mio.ctm.IReference)
         */
        @Override
        public IReference getTopicReference(final IReference ref) throws MIOException {
            if (ref == Reference.TOPIC_IN_FOCUS) {
                return getFocus();
            }
            if (ref.isItemIdentifier() || ref.isIRI() || ref.isSubjectLocator()) {
                return ref;
            }
            if (ref.isVIID()) {
                return Reference.createIID(_getReferenceByVariable(ref).getIRI());
            }
            if (ref.isVSLO()) {
                return Reference.createSLO(_getReferenceByVariable(ref).getIRI());
            }
            if (ref.isVariable()) {
                IReference res = _getReferenceByVariable(ref);
                if (res.isWildcard() || res.isNamedWildcard()) {
                    return getTopicReference(res);
                }
                return res;
            }
            if (ref.isWildcard() || ref.isNamedWildcard()) {
                IReference res = _bindings.get(ref);
                if (res == null) {
                    res = _env._nextTopicIdentity(ref.isWildcard() ? null : (String)ref.getValue());
                    _bindings.put(ref, res);
                }
                return res;
            }
            throw new MIOException("Cannot find " + ref);
        }

        private IReference _getReferenceByVariable(IReference ref) throws MIOException {
            if (ref.isVIID() || ref.isVSLO()) {
                ref = Reference.createVariable(ref.getString());
            }
            if (!ref.isVariable()) {
                throw new MIOException("Internal error. Expected a variable, got: " + ref);
            }
            IReference res = _bindings.get(ref);
            if (res == null) {
                res = ref;
            }
            if (res.isVariable() && _parent != null) {
                res = _parent._getReferenceByVariable(res);
            }
            if (res == Reference.TOPIC_IN_FOCUS) {
                res = getFocus();
            }
            if (res == null) {
                throw new MIOException("Unknown variable " + ref.getString());
            }
            return res;
        }

        /* (non-Javadoc)
         * @see com.semagia.mio.ctm.ITemplateContext#pushFocus(com.semagia.mio.ctm.IReference)
         */
        @Override
        public void pushFocus(final IReference focus) {
            if (_focus.length == _focusIdx+1) {
                IReference[] topics = new IReference[_focus.length*2];
                System.arraycopy(_focus, 0, topics, 0, _focus.length);
                _focus = topics;
            }
            _focusIdx++;
            _focus[_focusIdx] = focus;
        }

        /* (non-Javadoc)
         * @see com.semagia.mio.ctm.ITemplateContext#popFocus()
         */
        @Override
        public void popFocus() {
            _focus[_focusIdx] = null;
            _focusIdx--;
        }

        /* (non-Javadoc)
         * @see com.semagia.mio.ctm.ITemplateContext#getFocus()
         */
        @Override
        public IReference getFocus() {
            if (_focusIdx < 0) {
                return _parent != null ? _parent.getFocus() : null;
            }
            return _focus[_focusIdx];
        }
    }

}
