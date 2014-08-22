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
import com.semagia.mio.rdf.api.IErrorHandler;

/**
 * <tt>rtm:association</tt> implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 473 $ - $Date: 2010-09-08 13:36:04 +0200 (Mi, 08 Sep 2010) $
 */
final class AssociationMapper extends AbstractScopeTypeAwareMapper {

    /**
     * Role type played by the subject of an RDF statement.
     */
    private final IRef _subjectRole;

    /**
     * Role type played by the object of an RDF statement.
     */
    private final IRef _objectRole;

    public AssociationMapper(final Collection<IRef> scope, final IRef type,
            final IRef subjectRole, final IRef objectRole) {
        super("rtm:association", scope, type);
        if (subjectRole == null) {
            throw new IllegalArgumentException("The subject role must not be null");
        }
        _subjectRole = subjectRole;
        if (objectRole == null) {
            throw new IllegalArgumentException("The object role must not be null");
        }
        _objectRole = objectRole;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.semagia.mio.rdf.IMapper#handle(com.semagia.mio.helpers.SimpleMapHandler
     * , com.semagia.mio.IRef, java.lang.String, java.lang.String, boolean)
     */
    @Override
    public void handle(final IMapHandler handler, final IErrorHandler errorHandler,
            final IRef subject, final String p1, final String obj,
            final boolean objBNode) throws MIOException {
        handler.startAssociation();
        type(handler, p1);
        role(handler, _subjectRole, subject);
        role(handler, _objectRole, referenceFromObjectIRI(obj, objBNode));
        processScope(handler);
        handler.endAssociation();
    }

}
