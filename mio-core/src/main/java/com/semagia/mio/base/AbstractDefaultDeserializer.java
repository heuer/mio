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

import java.util.HashMap;
import java.util.Map;

import com.semagia.mio.IRIContext;

/**
 * Abstract {@link IDeserializer} implementation which extends the 
 * {@link AbstractDeserializer} and provides common properties like 
 * {@link com.semagia.mio.IDeserializer#getContext()}, 
 * {@link com.semagia.mio.IDeserializer#setContext(IRIContext)} and
 * {@link com.semagia.mio.IDeserializer#getProperty(String)}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public abstract class AbstractDefaultDeserializer extends
        AbstractDeserializer {

    private IRIContext _iris;
    private final Map<String, Object> _properties;

    protected AbstractDefaultDeserializer() {
        _iris = new IRIContext();
        _properties = new HashMap<String, Object>();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IDeserializer#getContext()
     */
    @Override
    public IRIContext getIRIContext() {
        return _iris;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IDeserializer#setContext(com.semagia.mio.Context)
     */
    @Override
    public void setIRIContext(final IRIContext ctx) {
        _iris = ctx;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IPropertyAware#getProperty(java.lang.String)
     */
    @Override
    public Object getProperty(final String iri) {
        return _properties.get(iri);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IPropertyAware#setProperty(java.lang.String, java.lang.Object)
     */
    @Override
    public void setProperty(final String iri, final Object value) {
        _properties.put(iri, value);
    }

}
