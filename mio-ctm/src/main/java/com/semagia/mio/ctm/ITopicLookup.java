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

import com.semagia.mio.IRef;
import com.semagia.mio.MIOException;

/**
 * EXPERIMENTAL: Read-only context to resolve QNames and wildcard topic
 * identifiers.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public interface ITopicLookup {

    /**
     * Resolves a QName.
     *
     * @param qName The QName (prefix:local) to resolve.
     * @return An absolute IRI.
     * @throws MIOException If the QName cannot be resolved, i.e. the prefix part is unkown. 
     */
    public IRef resolveQName(String qName) throws MIOException;

    /**
     * Returns a topic 
     * 
     * @param name A wildcard name or {@code null} if a anonymous topic
     *              should be created.
     * @return A topic.
     */
    public String getTopicIdentifierByWildcard(String name);

}
