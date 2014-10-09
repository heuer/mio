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
 * {@link IParseContext} implementation for CTM sources which have been included
 * by a parent CTM source.
 * 
 * Further, all templates registered within this context are made available
 * within the parent context as well.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
final class IncludeParseContext extends AbstractParseContext {

    /**
     * The parent context.
     */
    private final IParseContext _parent;

    /**
     * Creates a new instance with the provided parent which is
     * used to resolve wildcards.
     * 
     * @param parent The parent parse context which is used to create wildcard topics.
     */
    IncludeParseContext(final IParseContext parent) {
        super();
        if (parent == null) {
            throw new IllegalArgumentException("The parent parse context must not be null");
        }
        _parent = parent;
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
