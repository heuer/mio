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

import java.io.IOException;

import com.semagia.mio.MIOException;
import com.semagia.mio.Source;

/**
 * EXPERIMENTAL: Reader that reads a RDF to Topic Maps mapping from a source.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 554 $ - $Date: 2010-09-26 21:18:02 +0200 (So, 26 Sep 2010) $
 */
public interface IMappingReader {

    /**
     * Reads the mapping. 
     *
     * @param source The source to read the mapping from.
     * @throws IOException In case of an I/O error.
     * @throws MIOException In case of an error which is not an I/O error.
     */
    public void read(Source source) throws IOException, MIOException;

    /**
     * Returns the mapping handler.
     *
     * @return The mapping handler or {@code null}.
     */
    public IMappingHandler getMappingHandler();

    /**
     * Sets the mapping handler.
     *
     * @param handler The mapping handler instance.
     */
    public void setMappingHandler(IMappingHandler handler);

    /**
     * Sets the prefix listener.
     *
     * @param listener A listener or {@code null}.
     */
    public void setPrefixListener(IMappingPrefixListener listener);

}
