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
package com.semagia.mio.xtm;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.helpers.DefaultHandler;

import com.semagia.mio.IRIContext;
import com.semagia.mio.IMapHandler;
import com.semagia.mio.MIOException;
import com.semagia.mio.helpers.Locator;
import com.semagia.mio.helpers.SimpleMapHandler;


/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 462 $ - $Date: 2010-09-08 01:20:13 +0200 (Mi, 08 Sep 2010) $
 */
abstract class AbstractXTMContentHandler extends DefaultHandler implements IXTMContentHandler {

    /**
     * The handler which receives the events.
     */
    protected SimpleMapHandler _handler;

    /**
     * Buffer for characters (name value, occurrence value etc.)
     */
    protected final StringBuilder _content;

    protected final Map<String, Object> _properties;

    /**
     * The document locator.
     */
    protected Locator _docLocator;

    protected IRIContext _context;

    protected boolean _isSubordinate;

    protected AbstractXTMContentHandler() {
        _content = new StringBuilder();
        _properties = new HashMap<String, Object>();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.xtm.IXTMContentHandler#setMapHandler(com.semagia.mio.IMapHandler)
     */
    @Override
    public void setMapHandler(IMapHandler handler) {
        _handler = SimpleMapHandler.create(handler);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.xtm.IXTMContentHandler#setDocumentIRI(java.lang.String)
     */
    @Override
    public void setDocumentIRI(String iri) {
        _docLocator = Locator.create(iri);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.xtm.IXTMContentHandler#getContext()
     */
    @Override
    public IRIContext getIRIContext() {
        return _context;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.xtm.IXTMContentHandler#setContext(com.semagia.mio.Context)
     */
    @Override
    public void setIRIContext(IRIContext ctx) {
        _context = ctx;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.xtm.IXTMContentHandler#setProperty(java.lang.String, java.lang.Object)
     */
    @Override
    public void setProperty(String iri, Object value) {
        _properties.put(iri, value);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.xtm.IXTMContentHandler#getProperty(java.lang.String)
     */
    @Override
    public Object getProperty(String iri) {
        return _properties.get(iri);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.xtm.IXTMContentHandler#setSubordianate(boolean)
     */
    @Override
    public void setSubordianate(boolean subordinate) {
        _isSubordinate = subordinate;
    }

    /**
     * Throws an exception with the specified message.
     *
     * @param msg The error message.
     * @throws MIOException Thrown in any case.
     */
    protected static final void _reportError(String msg) throws MIOException {
        throw new MIOException(msg);
    }

    /**
     * Throws an exception with an embedded exception.
     *
     * @param ex The exception.
     * @throws MIOException Thrown in any case.
     */
    protected static final void _reportError(Exception ex) throws MIOException {
        throw new MIOException(ex);
    }

}
