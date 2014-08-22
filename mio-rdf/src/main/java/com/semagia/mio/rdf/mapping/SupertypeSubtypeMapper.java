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

import java.util.Collection;

import com.semagia.mio.IMapHandler;
import com.semagia.mio.IRef;
import com.semagia.mio.MIOException;
import com.semagia.mio.helpers.Ref;
import com.semagia.mio.rdf.api.IErrorHandler;
import com.semagia.mio.voc.TMDM;

/**
 * <tt>rtm:subtype-of</tt> implementation with an optional scope.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 546 $ - $Date: 2010-09-25 11:21:45 +0200 (Sa, 25 Sep 2010) $
 */
final class SupertypeSubtypeMapper extends AbstractScopeTypeAwareMapper {

    private static final IRef _SUPERTYPE_SUBTYPE = Ref.createSubjectIdentifier(TMDM.SUPERTYPE_SUBTYPE);
    private static final IRef _SUPERTYPE = Ref.createSubjectIdentifier(TMDM.SUPERTYPE);
    private static final IRef _SUBTYPE = Ref.createSubjectIdentifier(TMDM.SUBTYPE);

    public SupertypeSubtypeMapper(final Collection<IRef> scope) {
        super("rtm:subtype-of", scope, _SUPERTYPE_SUBTYPE);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.rdf.IMapper#handle(com.semagia.mio.helpers.SimpleMapHandler, com.semagia.mio.IRef, java.lang.String, java.lang.String, boolean)
     */
    @Override
    public void handle(final IMapHandler handler, final IErrorHandler errorHandler,
            final IRef subject, final String p1, final String obj,
            final boolean objBNode) throws MIOException {
        handler.startAssociation();
        type(handler);
        role(handler, _SUBTYPE, subject);
        role(handler, _SUPERTYPE, referenceFromObjectIRI(obj, objBNode));
        processScope(handler);
        handler.endAssociation();
    }

}
