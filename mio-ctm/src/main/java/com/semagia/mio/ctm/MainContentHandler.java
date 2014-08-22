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

import java.util.Collection;
import java.util.List;

import com.semagia.mio.IMapHandler;
import com.semagia.mio.MIOException;
import com.semagia.mio.helpers.Locator;

/**
 * [@link IContentHandler} implementation which delegates events to the 
 * {@link GlobalScopeHandler} or {@link TemplateScopeHandler}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 606 $ - $Date: 2011-01-20 00:48:46 +0100 (Do, 20 Jan 2011) $
 */
final class MainContentHandler extends DelegatingContentHandler {

    private final IContentHandler _globalHandler;

    public MainContentHandler() {
        this(new GlobalScopeHandler());
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
        final IEnvironment env = getEnvironment();
        final IReference res = env.resolveIdentifier(identifier);
        final Collection<Locator> includedBy = env.getIncludedBy();
        if (!includedBy.isEmpty()) {
            final IMapHandler handler = env.getMapHandler();
            handler.startTopic(res);
            final String frag = '#' + identifier;
            for (Locator iri: includedBy) {
                handler.itemIdentifier(iri.resolve(frag).toExternalForm());
            }
            handler.endTopic();
        }
        return res;
    }

    @Override
    public void startTemplate(final String name, final List<IReference> args)
            throws MIOException {
        super._handler = new TemplateScopeHandler(_globalHandler.getEnvironment(), name, args);
    }

    @Override
    public void endTemplate() throws MIOException {
        final ITemplate tpl = ((TemplateScopeHandler) _handler).dispose();
        _globalHandler.getEnvironment().registerTemplate(tpl);
        super._handler = _globalHandler;
    }

}
