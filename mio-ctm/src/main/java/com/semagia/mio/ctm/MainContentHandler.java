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
import java.util.Set;

import com.semagia.mio.IMapHandler;
import com.semagia.mio.MIOException;
import com.semagia.mio.helpers.Locator;

/**
 * [@link IContentHandler} implementation which delegates events to the 
 * {@link GlobalScopeHandler} or {@link TemplateScopeHandler}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
final class MainContentHandler extends DelegatingContentHandler {

    private final IContentHandler _globalHandler;
    private Set<Locator> _includedBy;

    public MainContentHandler(final IParseContext ctx) {
        this(new GlobalScopeHandler(ctx));
    }

    public MainContentHandler(final IContentHandler globalHandler) {
        super(globalHandler);
        _globalHandler = globalHandler;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.DelegatingContentHandler#resolveIdentifier(java.lang.String)
     */
    @Override
    public IReference resolveIdentifier(final String identifier) throws MIOException {
        final IParseContext ctx = getParseContext();
        final IReference res = ctx.resolveTopicIdentifier(identifier);
        if (_includedBy != null) {
            final IMapHandler handler = ctx.getMapHandler();
            handler.startTopic(res);
            final String frag = '#' + identifier;
            for (Locator iri: _includedBy) {
                handler.itemIdentifier(iri.resolve(frag).toExternalForm());
            }
            handler.endTopic();
        }
        return res;
    }

    @Override
    public void startTemplate(final String name, final List<IReference> args)
            throws MIOException {
        super._handler = new TemplateScopeHandler(getParseContext(), name, args);
    }

    @Override
    public void endTemplate() throws MIOException {
        final ITemplate tpl = ((TemplateScopeHandler) _handler).dispose();
        _globalHandler.getParseContext().registerTemplate(tpl);
        super._handler = _globalHandler;
    }

    void setIncludedBy(final Set<Locator> includedBy) {
        _includedBy = includedBy;
    }

    Set<Locator> getIncludedBy() {
        return _includedBy;
    }

}
