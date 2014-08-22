/*
 * Copyright 2007 - 2009 Lars Heuer (heuer[at]semagia.com)
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
package com.semagia.mio.stm;

import com.semagia.mio.helpers.Locator;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 469 $ - $Date: 2010-09-08 12:39:06 +0200 (Mi, 08 Sep 2010) $
 */
final class GlobalParserEnvironment extends AbstractParserEnvironment {

    private Locator _docIRI;
    private final SnelloRuntime _runtime;

    GlobalParserEnvironment(SnelloRuntime runtime) {
        _runtime = runtime;
    }

    
    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.IParserEnvironment#getEnvironment()
     */
    @Override
    public SnelloRuntime getRuntime() {
        return _runtime;
    }

    /**
     * 
     *
     * @param docIRI
     */
    public void setDocumentIRI(Locator docIRI) {
        _docIRI = docIRI;
        _runtime.setDocumentIRI(docIRI);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.IParserEnvironment#resolveLocator(java.lang.String)
     */
    @Override
    public Locator resolveLocator(String reference) {
        return _docIRI.resolve(reference);
    }

}
