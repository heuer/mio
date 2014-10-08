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

import com.semagia.mio.MIOException;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
final class IncludeParseContext extends AbstractParseContext {

    private final AbstractParseContext _parent;

    IncludeParseContext(final IParseContext parent) {
        super();
        _parent = (AbstractParseContext) parent;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.AbstractParseContext#makeNextWildcardId(java.lang.String)
     */
    @Override
    public String makeNextWildcardId(String name) {
        return _parent.makeNextWildcardId(name);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.AbstractParseContext#resolveWildcardIdentifier(java.lang.String)
     */
    @Override
    public IReference resolveWildcardIdentifier(String name) {
        return _parent.resolveWildcardIdentifier(name);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.AbstractParseContext#registerTemplate(com.semagia.mio.ctm.ITemplate)
     */
    @Override
    public void registerTemplate(final ITemplate template) throws MIOException {
        super.registerTemplate(template);
        _parent.registerTemplate(template);
    }

}
