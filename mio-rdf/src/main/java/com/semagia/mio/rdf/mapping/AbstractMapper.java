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
import com.semagia.mio.helpers.Ref;
import com.semagia.mio.rdf.api.IErrorHandler;
import com.semagia.mio.rdf.api.IMapper;

/**
 * Base class for all {@link IMapper} implementations which provides some
 * utility methods.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 473 $ - $Date: 2010-09-08 13:36:04 +0200 (Mi, 08 Sep 2010) $
 */
abstract class AbstractMapper implements IMapper {

    /**
     * The name of the mapper, i.e. rtm:occurrence.
     */
    protected final String _name;

    /**
     * Creates a new mapper with the provided {@code name}.
     *
     * @param name The name of the mapper, i.e. "rtm:occurrence". This name 
     *             will mainly be used for error messages.
     */
    protected AbstractMapper(final String name) {
        _name = name;
    }

    /**
     * Converts the {@code iri} into a subject identifier or item identifier.
     * 
     * If {@code isBlankNode} is {@code true} an item identifier will be 
     * returned, otherwise a subject identifier.
     *
     * @param iri The absolute IRI.
     * @param isBlankNode Indicates if the IRI was created from a blank node.
     * @return A subject identifier if <tt>isBlankNode</tt> is <tt>false</tt>, 
     *          an item identifier otherwise.
     */
    protected final static IRef referenceFromObjectIRI(final String iri, 
            final boolean isBlankNode) {
        return isBlankNode ? Ref.createItemIdentifier(iri) 
                           : Ref.createSubjectIdentifier(iri);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.rdf.api.IMapper#handle(com.semagia.mio.IMapHandler, com.semagia.mio.rdf.api.IErrorHandler, com.semagia.mio.IRef, java.lang.String, java.lang.String, java.lang.String, com.semagia.mio.IRef)
     */
    @Override
    public void handle(final IMapHandler handler, final IErrorHandler errorHandler, final IRef subject, 
            final String p1, final String value, final String datatype, final IRef lang) throws MIOException {
        errorHandler.rejectLiteral(_name, value, datatype);
    }
}
