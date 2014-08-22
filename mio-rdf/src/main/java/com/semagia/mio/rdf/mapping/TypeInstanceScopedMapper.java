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
 * <tt>rtm:instance-of</tt> implementation with an associated scope.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 546 $ - $Date: 2010-09-25 11:21:45 +0200 (Sa, 25 Sep 2010) $
 */
final class TypeInstanceScopedMapper extends AbstractScopeTypeAwareMapper {

    private static final IRef _TYPE_INSTANCE = Ref.createSubjectIdentifier(TMDM.TYPE_INSTANCE);
    private static final IRef _TYPE = Ref.createSubjectIdentifier(TMDM.TYPE);
    private static final IRef _INSTANCE = Ref.createSubjectIdentifier(TMDM.INSTANCE);

    public TypeInstanceScopedMapper(final Collection<IRef> scope) {
        super("rtm:instance-of", scope, _TYPE_INSTANCE);
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
        role(handler, _INSTANCE, subject);
        role(handler, _TYPE, referenceFromObjectIRI(obj, objBNode));
        processScope(handler);
        handler.endAssociation();
    }

}
