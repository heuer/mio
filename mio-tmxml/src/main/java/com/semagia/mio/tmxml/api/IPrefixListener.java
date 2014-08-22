/*
 * Copyright 2008 - 2010 Lars Heuer (heuer[at]semagia.com)
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
package com.semagia.mio.tmxml.api;

/**
 * EXPERIMENTAL: Listener which gets informed if a namespace / prefix 
 * declaration was found in a TM/XML source.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 467 $ - $Date: 2010-09-08 12:17:40 +0200 (Mi, 08 Sep 2010) $
 */
public interface IPrefixListener {

    /**
     * Called if a new prefix / iri pair was found.
     * 
     * Note that a prefix may be reported several times with different IRIs!
     *
     * @param prefix The prefix.
     * @param iri The namespace IRI.
     */
    public void handleNamespace(String prefix, String iri);

}
