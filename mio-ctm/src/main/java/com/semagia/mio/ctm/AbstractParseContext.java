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

import java.util.HashMap;
import java.util.Map;

import com.semagia.mio.IMapHandler;
import com.semagia.mio.MIOException;
import com.semagia.mio.helpers.Locator;
import com.semagia.mio.helpers.QName;
import com.semagia.mio.helpers.SimpleMapHandler;
import com.semagia.mio.ctm.api.IPrefixListener;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
abstract class AbstractParseContext implements IParseContext {

    private IPrefixListener _listener;
    private SimpleMapHandler _handler;
    private final Map<String, ITemplate> _templates;
    private TemplateContext _tplCtx;
    private Locator _baseIRI;
    private final Map<String, String> _prefixes;
    private final Map<String, IReference> _wc2Identity;

    AbstractParseContext() {
        _templates = new HashMap<String, ITemplate>();
        _prefixes = new HashMap<String, String>();
        _wc2Identity = new HashMap<String, IReference>();
    }

    @Override
    public IReference resolveWildcardIdentifier(String name) {
        return resolveTopicIdentifier(name);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IReadOnlyTopicLookup#getTopicByWildcard(java.lang.String)
     */
    @Override
    public IReference getTopicByWildcard(final String name) throws MIOException {
        if (name == null) {
            return resolveWildcardIdentifier(makeNextWildcardId(null));
        }
        IReference identity = _wc2Identity.get(name);
        if (identity == null) {
            identity = resolveWildcardIdentifier(makeNextWildcardId(name));
            _wc2Identity.put(name, identity);
        }
        return identity;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.ITopicLookup#resolveIRI(java.lang.String)
     */
    @Override
    public IReference resolveIRI(String iri) {
        return Reference.createIRI(resolveLocator(iri).toExternalForm());
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.ITopicLookup#resolveIdentifier(java.lang.String)
     */
    @Override
    public IReference resolveTopicIdentifier(String ident) {
        return Reference.createIID(resolveLocator("#" + ident).toExternalForm());
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.ITopicLookup#getDocumentIRI()
     */
    @Override
    public Locator getDocumentIRI() {
        return _baseIRI;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.ITopicLookup#setDocumentIRI(com.semagia.mio.helpers.Locator)
     */
    @Override
    public void setDocumentIRI(Locator baseIRI) {
        _baseIRI = baseIRI;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.ITopicLookup#resolveLocator(java.lang.String)
     */
    @Override
    public Locator resolveLocator(String iri) {
        return _baseIRI.resolve(iri);
    }


    @Override
    public IReference resolveQName(String qName) throws MIOException {
        final QName qn = QName.create(qName);
        final String iri = _prefixes.get(qn.getPrefix());
        if (iri == null) {
            throw new MIOException("The prefix '" + qn.getPrefix() + "' is unknown");
        }
        return Reference.createIRI(resolveLocator(iri + qn.getLocal()).toExternalForm());
    }

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
     * @see com.semagia.mio.ctm.IParseContext#createTemplateContext(com.semagia.mio.ctm.IReference[], com.semagia.mio.ctm.IReference[])
     */
    @Override
    public ITemplateContext createTemplateContext(final IReference[] tplParams, final IReference[] values) throws MIOException {
        Map<IReference, IReference> bindings = new HashMap<IReference, IReference>();
        for (int i=0; i<tplParams.length; i++) {
            IReference val = values[i];
            if (val.isWildcard() || val.isNamedWildcard()) {
                val = _tplCtx != null ? _tplCtx.getTopicReference(val)
                                      : getTopicByWildcard(val.isWildcard() ? null : (String)val.getValue());
            }
            bindings.put(tplParams[i], val);
        }
        return new TemplateContext(this, _tplCtx, bindings);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IParseContext#pushTemplateContext(com.semagia.mio.ctm.ITemplateContext)
     */
    @Override
    public void pushTemplateContext(ITemplateContext tplContext) throws MIOException {
        _tplCtx = (TemplateContext) tplContext;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IParseContext#popTemplateContext()
     */
    @Override
    public void popTemplateContext() throws MIOException {
        final TemplateContext currentCtx = _tplCtx;
        _tplCtx = currentCtx._parent;
        currentCtx.close();
    }

    @Override
    public void setMapHandler(IMapHandler handler) {
        _handler = SimpleMapHandler.create(handler);
    }

    @Override
    public SimpleMapHandler getMapHandler() {
        return _handler;
    }

    @Override
    public void setPrefixListener(final IPrefixListener listener) {
        _listener = listener;
    }

    @Override
    public ITemplate getTemplate(final String name, int arity) throws MIOException {
        final String key = name + "/" + arity;
        final ITemplate tpl = _templates.get(key);
        if (tpl == null) {
            throw new MIOException("The template '" + key + "' does not exist");
        }
        return tpl;
    }

    @Override
    public void registerTemplate(final ITemplate template) throws MIOException {
        final String key = template.getName() + "/" + template.getArity();
        final ITemplate existing = _templates.put(key, template);
        if (existing != null) {
            throw new MIOException("A template '" + key + "' is already registered");
        }
    }

}
