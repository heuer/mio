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
package com.semagia.mio.rdf.sesame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.semagia.mio.MIOException;
import com.semagia.mio.rdf.api.IErrorHandler;

/**
 * Default {@link IErrorHandler} which loggs errors and may throw an exception
 * (configurable).
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 473 $ - $Date: 2010-09-08 13:36:04 +0200 (Mi, 08 Sep 2010) $
 */
final class DefaultErrorHandler implements IErrorHandler {

    private static final Logger _LOG = LoggerFactory.getLogger(DefaultErrorHandler.class);

    private final boolean _stopOnError;

    public DefaultErrorHandler(boolean stopOnError) {
        _stopOnError = stopOnError;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.rdf.api.IErrorHandler#rejectBlankNode(java.lang.String)
     */
    @Override
    public void rejectBlankNode(String name) throws MIOException {
        final String err = "The object of " + name + " must not be a blank node";
        _LOG.warn(err);
        if (_stopOnError) {
            throw new MIOException(err);
        }
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.rdf.api.IErrorHandler#rejectLiteral(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void rejectLiteral(String name, String value, String datatype) throws MIOException {
        final String err = "The object of " + name + " must not be a literal, got: '" + value + "'^^<" + datatype + ">";
        _LOG.warn(err);
        if (_stopOnError) {
            throw new MIOException(err);
        }
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.rdf.api.IErrorHandler#rejectNonLiteral(java.lang.String, java.lang.String)
     */
    @Override
    public void rejectNonLiteral(String name, String obj) throws MIOException {
        final String err = "The object of " + name + " must be a literal, got: <" + obj + ">";
        _LOG.warn(err);
        if (_stopOnError) {
            throw new MIOException(err);
        }
    }

}
