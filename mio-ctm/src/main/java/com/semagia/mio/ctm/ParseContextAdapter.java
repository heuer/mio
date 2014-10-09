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
import com.semagia.mio.ctm.api.IPrefixListener;

/**
 * Adapts a {@link ITopicLookup} instance to a parse context.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
final class ParseContextAdapter extends AbstractParseContext {

    private final ITopicLookup _lookup;

    ParseContextAdapter(ITopicLookup lookup) {
        super();
        _lookup = lookup;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IReadOnlyTopicLookup#resolveQName(java.lang.String)
     */
    @Override
    public IReference resolveQName(final String qName) throws MIOException {
        return Reference.wrap(_lookup.resolveQName(qName));
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IReadOnlyTopicLookup#getTopicByWildcard(java.lang.String)
     */
    @Override
    public String makeNextWildcardId(final String name) {
        return _lookup.getTopicIdentifierByWildcard(name);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.ITopicLookup#registerPrefix(java.lang.String, java.lang.String)
     */
    @Override
    public void registerPrefix(String prefix, String iri) throws MIOException {
        throw new MIOException("Unsupported method");
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.ITopicLookup#setPrefixListener(com.semagia.mio.ctm.api.IPrefixListener)
     */
    @Override
    public void setPrefixListener(IPrefixListener listener) {
        // Ignore since prefixes are immutable.
    }

}
