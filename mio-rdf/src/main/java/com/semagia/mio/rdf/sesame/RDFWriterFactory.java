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

import java.io.OutputStream;

import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.n3.N3WriterFactory;
import org.openrdf.rio.ntriples.NTriplesWriterFactory;
import org.openrdf.rio.rdfxml.RDFXMLWriterFactory;
import org.openrdf.rio.trig.TriGWriterFactory;
import org.openrdf.rio.trix.TriXWriterFactory;
import org.openrdf.rio.turtle.TurtleWriterFactory;

import com.semagia.mio.Syntax;

/**
 * Factory for {@link RDFWriter} instances.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 569 $ - $Date: 2010-09-28 22:34:01 +0200 (Di, 28 Sep 2010) $
 */
final class RDFWriterFactory {

    private static final org.openrdf.rio.RDFWriterFactory
        _N3_FACTORY = new N3WriterFactory(),
        _NT_FACTORY = new NTriplesWriterFactory(),
        _RDFXML_FACTORY = new RDFXMLWriterFactory(),
        _TRIG_FACTORY = new TriGWriterFactory(),
        _TRIX_FACTORY = new TriXWriterFactory(),
        _TURTLE_FACTORY = new TurtleWriterFactory()
    ;

    /**
     * 
     *
     * @param rdfSyntax
     * @return
     */
    public static RDFWriter createWriter(final Syntax rdfSyntax, final OutputStream out) {
        final org.openrdf.rio.RDFWriterFactory factory = _getFactory(rdfSyntax);
        return factory != null ? factory.getWriter(out) : null;
    }

    /**
     * 
     *
     * @param rdfSyntax
     * @return
     */
    private static org.openrdf.rio.RDFWriterFactory _getFactory(final Syntax rdfSyntax) {
        if (Syntax.N3.equals(rdfSyntax)) {
            return _N3_FACTORY;
        }
        else if (Syntax.NTRIPLES.equals(rdfSyntax)) {
            return _NT_FACTORY;
        }
        else if (Syntax.RDFXML.equals(rdfSyntax)) {
            return _RDFXML_FACTORY;
        }
        else if (Syntax.TRIG.equals(rdfSyntax)) {
            return _TRIG_FACTORY;
        }
        else if (Syntax.TRIX.equals(rdfSyntax)) {
            return _TRIX_FACTORY;
        }
        else if (Syntax.TURTLE.equals(rdfSyntax)) {
            return _TURTLE_FACTORY;
        }
        throw new RuntimeException("Internal error: No factory found for: " + rdfSyntax);
    }

}
