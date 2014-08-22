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
package com.semagia.mio.rdf.api;

import com.semagia.mio.IMapHandler;
import com.semagia.mio.IRef;
import com.semagia.mio.MIOException;

/**
 * Represents a handler which can map a RDF predicate IRI to a Topic Maps
 * statement.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 473 $ - $Date: 2010-09-08 13:36:04 +0200 (Mi, 08 Sep 2010) $
 */
public interface IMapper {

    /**
     * Handles a statement with a literal value.
     *
     * @param handler The handler which receives the events.
     * @param errorHandler The handler which receives notifications about errors.
     * @param subject The topic which has the focus.
     * @param predicateIRI An absolute IRI which represents the predicate.
     * @param value The string value.
     * @param datatype The datatype.
     * @param language The language or {@code null}.
     * @throws MIOException In case of an error.
     */
    public void handle(IMapHandler handler, IErrorHandler errorHandler, IRef subject, String predicateIRI, String value, String datatype, IRef language) throws MIOException;

    /**
     * Handles a statement with an object.
     *
     * @param handler The handler which receives the events.
     * @param errorHandler The handler which receives notifications about errors.
     * @param subject The topic which has the focus.
     * @param predicateIRI An absolute IRI which represents the predicate.
     * @param objIRI An absolute IRI which represents the object.
     * @param objBNode Indicates if the object IRI was created from a blank node.
     * @throws MIOException In case of an error.
     */
    public void handle(IMapHandler handler, IErrorHandler errorHandler, IRef subject, String predicateIRI, String objIRI, boolean objBNode) throws MIOException;

}
