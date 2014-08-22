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

import com.semagia.mio.IDeserializer;
import com.semagia.mio.IDeserializerFactory;
import com.semagia.mio.Syntax;
import com.semagia.mio.rdf.api.IMappingReader;
import com.semagia.mio.rdf.api.IMappingReaderFactory;

/**
 * Common super class for RDF deserializer factories which wrap a {@org.openrdf.rio.RDFParserFactory}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 552 $ - $Date: 2010-09-26 16:56:55 +0200 (So, 26 Sep 2010) $
 */
abstract class AbstractRDFDeserializerFactory implements IDeserializerFactory, IMappingReaderFactory {

    private final Syntax _syntax;

    protected AbstractRDFDeserializerFactory(final Syntax syntax) {
        _syntax = syntax;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IDeserializerFactory#createDeserializer()
     */
    @Override
    public final IDeserializer createDeserializer() {
        return new RDFDeserializer(RDFParserFactory.createParser(_syntax));
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.rdf.api.IMappingReaderFactory#createReader()
     */
    @Override
    public IMappingReader createReader() {
        return new RDFSourceMappingReader(RDFParserFactory.createParser(_syntax));
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IDeserializerFactory#getSyntax()
     */
    @Override
    public final Syntax getSyntax() {
        return _syntax;
    }

}

