/*
 * Copyright 2007 - 2014 Lars Heuer (heuer[at]semagia.com)
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

import java.net.URL;

import org.xml.sax.ContentHandler;

import com.semagia.mio.Context;
import com.semagia.mio.IMapHandler;

/**
 * Enhanced {@link org.xml.sax.ContentHandler}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
interface IXTMContentHandler extends ContentHandler {

    /**
    * @see {@link com.semagia.mio.IDeserializer#setMapHandler(IMapHandler)}
     */
    public void setMapHandler(IMapHandler inputHandler);

    /**
     * Sets the document IRI.
     *
     * @param iri The document IRI.
     */
    public void setDocumentIRI(String iri);

    /**
     * Returns the context.
     *
     * @return A context instance or <code>null</code> if the context wasn't set.
     */
    public Context getContext();

    /**
     * Sets the context.
     *
     * @param ctx A context instance.
     */
    public void setContext(Context ctx);

    /**
     * Sets the content handler into the subordinate mode.
     *
     * @param subordinate {@code true} to indicate that the handler is in 
     *                      subordinate mode, otherwise {@code false} (default).
     */
    public void setSubordianate(boolean subordinate);

    /**
     * Sets a property.
     *
     * @param iri The property name (an IRI).
     * @param value The property value.
     */
    public void setProperty(String iri, Object value);

    /**
     * Returns the value of a property.
     *
     * @param iri The property name (an IRI).
     * @return The value of the property or {@code null} if the property is 
     *          unknown or has no value.
     */
    public Object getProperty(String iri);

    /**
     * Returns the URL of the RELAX NG schema.
     *
     * @return The URL of the RELAX NG schema.
     */
    public URL getRelaxURL();

}
