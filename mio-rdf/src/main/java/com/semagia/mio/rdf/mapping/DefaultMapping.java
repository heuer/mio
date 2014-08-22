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
package com.semagia.mio.rdf.mapping;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.semagia.mio.rdf.api.IMapper;
import com.semagia.mio.rdf.api.IMapping;

/**
 * Thread-safe implementation of {@link IMapping} which holds everything in-memory.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 550 $ - $Date: 2010-09-26 11:35:06 +0200 (So, 26 Sep 2010) $
 */
final class DefaultMapping implements IMapping {

    private final Map<String, IMapper> _pred2Mapping;

    public DefaultMapping() {
        _pred2Mapping = new ConcurrentHashMap<String, IMapper>();
    }

    /**
     * Creates an association from the provided {@code predicateIRI} to 
     * the provided {@code mapper}.
     *
     * @param predicateIRI The predicate IRI.
     * @param mapper The mapper.
     */
    public void addMapper(final String predicateIRI, final IMapper mapper) {
        if (predicateIRI == null) {
            throw new IllegalArgumentException("The predicate IRI must not be null");
        }
        if (mapper == null) {
            throw new IllegalArgumentException("The mapper must not be null");
        }
        _pred2Mapping.put(predicateIRI, mapper);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.rdf.mapping.IMapping#getMapper(java.lang.String)
     */
    @Override
    public IMapper getMapper(final String predicateIRI) {
        return _pred2Mapping.get(predicateIRI);
    }

}
