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
package com.semagia.mio;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * The context provides information about IRIs which have been parsed already.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 607 $ - $Date: 2011-01-20 02:28:15 +0100 (Do, 20 Jan 2011) $
 */
public final class Context {

    private final Set<String> _iris;

    /**
     * Creates a new context.
     */
    public Context() {
        _iris = new HashSet<String>();
    }

    /**
     * Adds the provided IRI to the already loaded IRIs.
     *
     * @param iri The IRI to add.
     */
    public synchronized void addIRI(String iri) {
        _iris.add(iri);
    }

    /**
     * A collection of loaded IRIs.
     *
     * @return A (maybe empty) unmodifiable collection of loaded IRIs.
     */
    public synchronized Set<String> getIRIs() {
        return Collections.unmodifiableSet(_iris);
    }

    /**
     * Returns if the provided IRI has been parsed.
     *
     * @param iri The IRI.
     * @return <tt>true</tt> if the IRI is known, otherwise <tt>false</tt>.
     */
    public synchronized boolean containsIRI(String iri) {
        return _iris.contains(iri);
    }

}
