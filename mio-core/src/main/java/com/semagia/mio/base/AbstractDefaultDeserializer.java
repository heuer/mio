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

import com.semagia.mio.Context;

/**
 * Abstract {@link IDeserializer} implementation which extends the 
 * {@link AbstractDeserializer} and provides common properties like 
 * {@link com.semagia.mio.IDeserializer#getContext()}, 
 * {@link com.semagia.mio.IDeserializer#setContext(Context)} and
 * {@link com.semagia.mio.IDeserializer#getProperty(String)}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 607 $ - $Date: 2011-01-20 02:28:15 +0100 (Do, 20 Jan 2011) $
 */
public abstract class AbstractDefaultDeserializer extends
        AbstractDeserializer {

    private Context _context;
    private final Map<String, Object> _properties;

    protected AbstractDefaultDeserializer() {
        _context = new Context();
        _properties = new HashMap<String, Object>();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IDeserializer#getContext()
     */
    @Override
    public Context getContext() {
        return _context;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IDeserializer#setContext(com.semagia.mio.Context)
     */
    @Override
    public void setContext(final Context ctx) {
        _context = ctx;
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
