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

import com.semagia.mio.MIOException;
import com.semagia.mio.helpers.Locator;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 469 $ - $Date: 2010-09-08 12:39:06 +0200 (Mi, 08 Sep 2010) $
 */
final class LocalParserEnvironment extends AbstractParserEnvironment {

    private GlobalParserEnvironment _globalEnv;

    public LocalParserEnvironment(GlobalParserEnvironment env) {
        _globalEnv = env;
    }

    GlobalParserEnvironment getGlobalEnvironment() {
        return _globalEnv;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.IParserEnvironment#getEnvironment()
     */
    @Override
    public SnelloRuntime getRuntime() {
        return _globalEnv.getRuntime();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.AbstractParserEnvironment#resolveIdentifier(java.lang.String)
     */
    @Override
    public SnelloRef resolveIdentifier(String ident) {
        SnelloRef ref = super.resolveIdentifier(ident);
        return ref != null ? ref : _globalEnv.resolveIdentifier(ident);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.dm.AbstractEnvironment#resolveQName(java.lang.String)
     */
    @Override
    public SnelloRef resolveQName(String name) throws MIOException {
        try {
            // Try to resolve the QName locally
            return super.resolveQName(name);
        }
        catch (MIOException ex) {
            // Maybe the QName is resolvable by the parent env?
            return _globalEnv.resolveQName(name);
        }
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.IParserEnvironment#resolveLocator(java.lang.String)
     */
    @Override
    public Locator resolveLocator(String reference) {
        return _globalEnv.resolveLocator(reference);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.runtime.IEnvironment#resolveIRI(java.lang.String)
     */
    @Override
    public SnelloRef resolveIRI(String iri) {
        return _globalEnv.resolveIRI(iri);
    }

}
