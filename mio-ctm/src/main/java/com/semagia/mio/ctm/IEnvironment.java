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
import java.util.Collection;
import java.util.List;

import com.semagia.mio.Context;
import com.semagia.mio.IMapHandler;
import com.semagia.mio.MIOException;
import com.semagia.mio.ctm.api.IPrefixListener;
import com.semagia.mio.helpers.Locator;
import com.semagia.mio.helpers.SimpleMapHandler;

/**
 * Parser environment to keep track of registered templates, prefixes etc.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 606 $ - $Date: 2011-01-20 00:48:46 +0100 (Do, 20 Jan 2011) $
 */
interface IEnvironment {

    public void setMapHandler(IMapHandler handler);

    public SimpleMapHandler getMapHandler();

    /**
     * Adds the provided prefix / IRI pair to the environment.
     *
     * @param prefix The prefix.
     * @param iri The IRI.
     * @throws MIOException If the prefix / IRI pair is invalid, i.e. the 
     *                      the prefix is already bound to another IRI.
     */
    public void registerPrefix(String prefix, String iri) throws MIOException;

    /**
     * Sets the prefix listener.
     *
     * @param listener An instance of {@link IPrefixListener} or {@code null}.
     */
    public void setPrefixListener(IPrefixListener listener);

    /**
     * Resolves a QName.
     *
     * @param qName The QName (prefix:local) to resolve.
     * @return An absolute IRI.
     * @throws MIOException If the QName cannot be resolved, i.e. the prefix part is unkown. 
     */
    public IReference resolveQName(String qName) throws MIOException;

    /**
     * Resolves the provided identifier against the document IRI.
     *
     * @param ident The identifier to resolve.
     * @return An absolute IRI.
     */
    public IReference resolveIdentifier(String ident);

    /**
     * Resolves the provided IRI against the document IRI.
     *
     * @param iri The IRI to resolve.
     * @return An absolute IRI.
     */
    public IReference resolveIRI(final String iri);

    public void registerTemplate(ITemplate template) throws MIOException;

    public ITemplate getTemplate(String name, int arity) throws MIOException;

    public Collection<ITemplate> getTemplates();

    public IReference topicByWildcard(String name);

    public void setDocumentIRI(String baseIRI);

    public void setSubordinate(boolean subordinate);

    boolean isSubordinate();

    public void setContext(Context context);

    public Context getContext();

    public void include(String iri) throws IOException, MIOException;

    public void mergeIn(String iri, String syntaxIRI) throws IOException, MIOException;

    public List<Locator> getIncludedBy();

    public void setIncludedBy(List<Locator> includedBy);

    public void setWildcardCounter(int counter);

    public int getWildcardCounter();

}
