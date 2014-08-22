/*
 * Copyright 2008 - 2011 Lars Heuer (heuer[at]semagia.com)
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
import com.semagia.mio.voc.XSD;

/**
 * <tt>rtm:occurrence</tt> implementation.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 614 $ - $Date: 2011-04-07 16:12:07 +0200 (Do, 07 Apr 2011) $
 */
final class OccurrenceMapper extends AbstractScopeTypeAwareMapper {

    private final boolean _lang2scope;

    public OccurrenceMapper(final Collection<IRef> scope, final IRef type, boolean lang2scope) {
        super("rtm:occurrence", scope, type);
        _lang2scope = lang2scope;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.semagia.mio.rdf.IMapping#handle(com.semagia.mio.helpers.SimpleMapHandler, com.semagia.mio.IRef, java.lang.String, com.semagia.mio.internal.utils.Literal)
     */
    @Override
    public void handle(final IMapHandler handler, final IErrorHandler errorHandler, final IRef subject, 
            final String p1, final String value, final String datatype, final IRef lang) throws MIOException {
        handler.startOccurrence();
        type(handler, p1);
        handler.value(value, datatype);
        processScope(handler, _lang2scope ? lang : null);
        handler.endOccurrence();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.rdf.IMapper#handle(com.semagia.mio.helpers.SimpleMapHandler, com.semagia.mio.IRef, java.lang.String, java.lang.String, boolean)
     */
    @Override
    public void handle(final IMapHandler handler, final IErrorHandler errorHandler,
            final IRef subject, final String p1, final String obj,
            final boolean objBNode) throws MIOException {
        if (objBNode) {
            errorHandler.rejectBlankNode(_name);
        }
        else {
            handle(handler, errorHandler, subject, p1, obj, XSD.ANY_URI, null);
        }
    }

}
