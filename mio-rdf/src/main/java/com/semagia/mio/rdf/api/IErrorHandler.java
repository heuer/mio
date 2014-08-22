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
package com.semagia.mio.rdf.api;

import com.semagia.mio.MIOException;

/**
 * Handler which receives notifications of errors.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 612 $ - $Date: 2011-04-07 13:20:05 +0200 (Do, 07 Apr 2011) $
 */
public interface IErrorHandler {

    /**
     * Notification if a {@link IMapper} rejects a literal value.
     *
     * @param name The {@link IMapper} name, i.e. rtm:association.
     * @param value The literal value.
     * @param datatype The datatype of the literal.
     * @throws MIOException If the handler want to stop the mapping process.
     */
    public void rejectLiteral(String name, String value, String datatype) throws MIOException;

    /**
     * Notification if a {@link IMapper} rejects a non-literal value.
     *
     * @param name The {@link IMapper} name, i.e. rtm:basename. 
     * @param obj The object IRI.
     * @throws MIOException If the handler want to stop the mapping process.
     */
    public void rejectNonLiteral(String name, String obj) throws MIOException;

    /**
     * Notification if a {@link IMapper} rejects a blank node.
     *
     * @param name The {@link IMapper} name, i.e. rtm:subject-identifier.
     * @throws MIOException If the handler want to stop the mapping process.
     */
    public void rejectBlankNode(String name) throws MIOException;

}
