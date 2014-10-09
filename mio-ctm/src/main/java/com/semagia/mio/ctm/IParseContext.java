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

import com.semagia.mio.IMapHandler;
import com.semagia.mio.MIOException;
import com.semagia.mio.ctm.api.IPrefixListener;
import com.semagia.mio.helpers.Locator;
import com.semagia.mio.helpers.SimpleMapHandler;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
interface IParseContext {

    public void setMapHandler(IMapHandler handler);

    public SimpleMapHandler getMapHandler();

    public IReference resolveWildcardIdentifier(String name);
    public String makeNextWildcardId(final String name);

    public void registerTemplate(ITemplate template) throws MIOException;

    public ITemplate getTemplate(String name, int arity) throws MIOException;

    public Locator getDocumentIRI();

    /**
     * Resolves a QName.
     *
     * @param qName The QName (prefix:local) to resolve.
     * @return An absolute IRI.
     * @throws MIOException If the QName cannot be resolved, i.e. the prefix part is unkown. 
     */
    public IReference resolveQName(String qName) throws MIOException;

    /**
     * Returns a topic 
     * 
     * @param name A wildcard name or {@code null} if a anonymous topic
     *              should be created.
     * @return A topic.
     */
    public IReference getTopicByWildcard(String name) throws MIOException;


    /**
     * Adds the provided prefix / IRI pair to the environment.
     *
     * @param prefix The prefix.
     * @param iri The IRI.
     * @throws MIOException If the prefix / IRI pair is invalid, i.e. the 
     *                      the prefix is already bound to another IRI.
     */
    public void registerPrefix(String prefix, String iri) throws MIOException;

    public IReference resolveIRI(String iri);

    /**
     * Resolves the provided identifier against the document IRI.
     *
     * @param ident The identifier to resolve.
     * @return An absolute IRI.
     */
    public IReference resolveTopicIdentifier(String ident);

    public void setDocumentIRI(Locator baseIRI);

    public Locator resolveLocator(String iri);

    public void setPrefixListener(IPrefixListener listener);

    public ITemplateContext createTemplateContext(IReference[] _args,
            IReference[] _bindings) throws MIOException;

    public void pushTemplateContext(ITemplateContext ctx) throws MIOException;

    public void popTemplateContext() throws MIOException;

}
