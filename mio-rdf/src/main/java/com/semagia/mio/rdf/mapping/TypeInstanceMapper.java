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

import com.semagia.mio.IMapHandler;
import com.semagia.mio.IRef;
import com.semagia.mio.MIOException;
import com.semagia.mio.rdf.api.IErrorHandler;
import com.semagia.mio.rdf.api.IMapper;

/**
 * <tt>rtm:instance-of</tt> implementation in the unconstrained scope.
 * 
 * Since this {@link IMapper} does not keep any state information, it is
 * implemented as singleton, use {@link #getInstance()} to obtain an instance.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 473 $ - $Date: 2010-09-08 13:36:04 +0200 (Mi, 08 Sep 2010) $
 */
final class TypeInstanceMapper extends AbstractMapper {

    private static final IMapper _INSTANCE = new TypeInstanceMapper();

    private TypeInstanceMapper() {
        super("rtm:instance-of");
    }

    /**
     * Returns an instance of this class.
     *
     * @return The instance.
     */
    public static IMapper getInstance() {
        return _INSTANCE;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.rdf.IMapper#handle(com.semagia.mio.helpers.SimpleMapHandler, com.semagia.mio.IRef, java.lang.String, java.lang.String, boolean)
     */
    @Override
    public void handle(final IMapHandler handler, final IErrorHandler errorHandler,
            final IRef subject, final String p1, final String obj,
            final boolean objBNode) throws MIOException {
        handler.startIsa();
        handler.topicRef(referenceFromObjectIRI(obj, objBNode));
        handler.endIsa();
    }

}
