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

import java.util.HashMap;
import java.util.Map;

import com.semagia.mio.MIOException;
import com.semagia.mio.helpers.Locator;
import com.semagia.mio.helpers.QName;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 469 $ - $Date: 2010-09-08 12:39:06 +0200 (Mi, 08 Sep 2010) $
 */
abstract class AbstractParserEnvironment implements IParserEnvironment {

    private Map<String, Locator> _prefixes;
    private Map<String, SnelloRef> _aliases;

    protected AbstractParserEnvironment() {
        _prefixes = new HashMap<String, Locator>();
        _aliases = new HashMap<String, SnelloRef>();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.runtime.IEnvironment#resolveIRI(java.lang.String)
     */
    @Override
    public SnelloRef resolveIRI(String iri) {
        return SnelloRef.createIRI(resolveLocator(iri).getReference());
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.runtime.IEnvironment#registerPrefix(java.lang.String, java.lang.String)
     */
    @Override
    public void registerPrefix(String name, String iri) throws MIOException {
        Locator existing = _prefixes.put(name, resolveLocator(iri));
        if (existing != null && !existing.equals(_prefixes.get(name))) {
            _reportError("The prefix '" + name + "' is already registered to '" + existing.getReference() + "'");
        }
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.IParserEnvironment#registerAlias(java.lang.String, com.semagia.mio.stm.dm.SnelloRef)
     */
    @Override
    public void registerAlias(String name, SnelloRef ref)
            throws MIOException {
        SnelloRef existing = _aliases.put(name, ref);
        if (existing != null && !existing.equals(_aliases.get(name))) {
            _reportError("The alias '" + name + "' is already registered to '" + existing.getIRI() + "'");
        }
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.IParserEnvironment#resolveQName(java.lang.String)
     */
    @Override
    public SnelloRef resolveQName(String name) throws MIOException {
        QName qName = QName.create(name);
        Locator loc = _prefixes.get(qName.getPrefix());
        if (loc == null) {
            _reportError("Unknown prefix '" + qName.getPrefix() + "'");
        }
        final String absoluteIRI = loc.getReference() + qName.getLocal();
        return resolveIRI(absoluteIRI);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.IParserEnvironment#resolveIdentifier(java.lang.String)
     */
    @Override
    public SnelloRef resolveIdentifier(String ident) {
        SnelloRef ref = _aliases.get(ident);
        return ref != null ? ref
                           : SnelloRef.createIID(resolveLocator("#" + ident).getReference());
    }

    protected static final void _reportError(String msg) throws MIOException {
        throw new MIOException(msg);
    }
}
