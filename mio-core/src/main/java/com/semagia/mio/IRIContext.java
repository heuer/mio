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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The context provides information about IRIs which have been parsed already.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public final class IRIContext {

    private final ConcurrentHashMap<String, Boolean> _iris;

    /**
     * Creates a new context.
     */
    public IRIContext() {
        _iris = new ConcurrentHashMap<String, Boolean>();
    }

    /**
     * Adds the provided IRI to the already loaded IRIs.
     *
     * @param iri The IRI to add.
     */
    public void addIRI(String iri) {
        _iris.putIfAbsent(iri, Boolean.TRUE);
    }

    /**
     * A collection of loaded IRIs.
     *
     * @return A (maybe empty) unmodifiable collection of loaded IRIs.
     */
    public Set<String> getIRIs() {
        return Collections.unmodifiableSet(_iris.keySet());
    }

    /**
     * Returns if the provided IRI has been parsed.
     *
     * @param iri The IRI.
     * @return <tt>true</tt> if the IRI is known, otherwise <tt>false</tt>.
     */
    public boolean containsIRI(String iri) {
        return _iris.contains(iri);
    }

}
