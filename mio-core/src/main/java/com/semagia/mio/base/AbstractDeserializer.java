/*
 * Copyright 2007 - 2010 Lars Heuer (heuer[at]semagia.com)
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
package com.semagia.mio.base;

import java.io.IOException;

import com.semagia.mio.IRIContext;
import com.semagia.mio.IDeserializer;
import com.semagia.mio.IMapHandler;
import com.semagia.mio.MIOException;
import com.semagia.mio.Source;

/**
 * Abstract deserializer which handles the common cases.
 * <p>
 * This deserializer converts every input into an {@link Source} and checks
 * if neither the input source nor the document IRI is null.
 * </p>
 * <p>
 * If this is a sub-deserializer, this deserializer omits calls to 
 * {@link IMapHandler#startTopicMap()} and {@link IMapHandler#endTopicMap()}. 
 * </p>
 * <p>
 * Derived classes have to implement the {@link #doParse(Source)} method which
 * does the parsing.
 * </p>
 * <p>
 * This class is not thread-safe.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public abstract class AbstractDeserializer implements IDeserializer {

    protected IMapHandler _handler;
    protected boolean _isSubordinate;

    /* (non-Javadoc)
     * @see com.semagia.mio.IDeserializer#setSubordinate(boolean)
     */
    @Override
    public void setSubordinate(final boolean subordinate) {
        _isSubordinate = subordinate;
    }

    /**
     * Called....
     *
     * @param src
     * @param docIRI
     * @throws IOException
     * @throws MIOException
     */
    protected abstract void doParse(final Source src) throws IOException,
            MIOException;

    /*
     * (non-Javadoc)
     * 
     * @see com.semagia.mio.IDeserializer#parse(com.semagia.mio.Source)
     */
    @Override
    public void parse(final Source src) throws IOException, MIOException {
        if (getIRIContext() == null) {
            setIRIContext(new IRIContext());
        }
        if (_handler == null) {
            throw new IllegalStateException("The input handler was not set");
        }
        if (src == null) {
            throw new IllegalArgumentException("The input must not be null");
        }
        if (src.getBaseIRI() == null) {
            throw new IllegalArgumentException("The base IRI must not be null");
        }
        if (!_isSubordinate) {
            _handler.startTopicMap();
        }
        try {
            doParse(src);
        }
        finally {
            if (!_isSubordinate) {
                _handler.endTopicMap();
                if (src.getByteStream() != null) {
                    src.getByteStream().close();
                }
                if (src.getCharacterStream() != null) {
                    src.getCharacterStream().close();
                }
            }
            _handler = null;
        }
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IDeserializer#setMapHandler(com.semagia.mio.IMapHandler)
     */
    @Override
    public void setMapHandler(final IMapHandler handler) {
        if (handler == null) {
            throw new IllegalArgumentException("The handler must not be null");
        }
        _handler = handler;
    }

}
