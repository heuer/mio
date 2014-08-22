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

import net.rootdev.javardfa.RDFaHtmlParserFactory;

import org.openrdf.rio.RDFParser;
import org.openrdf.rio.n3.N3ParserFactory;
import org.openrdf.rio.ntriples.NTriplesParserFactory;
import org.openrdf.rio.rdfxml.RDFXMLParserFactory;
import org.openrdf.rio.trig.TriGParserFactory;
import org.openrdf.rio.trix.TriXParserFactory;
import org.openrdf.rio.turtle.TurtleParserFactory;

import com.semagia.mio.Syntax;

/**
 * Factory for {@link RDFParser} instances.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 563 $ - $Date: 2010-09-28 11:49:34 +0200 (Di, 28 Sep 2010) $
 */
final class RDFParserFactory {

    private static final org.openrdf.rio.RDFParserFactory
        _N3_FACTORY = new N3ParserFactory(),
        _NT_FACTORY = new NTriplesParserFactory(),
        _RDFXML_FACTORY = new RDFXMLParserFactory(),
        _TRIG_FACTORY = new TriGParserFactory(),
        _TRIX_FACTORY = new TriXParserFactory(),
        _TURTLE_FACTORY = new TurtleParserFactory(),
        _RDFA_FACTORY = new RDFaHtmlParserFactory()
    ;

    /**
     * 
     *
     * @param rdfSyntax
     * @return
     */
    public static RDFParser createParser(final Syntax rdfSyntax) {
        final org.openrdf.rio.RDFParserFactory factory = _getFactory(rdfSyntax);
        return factory != null ? factory.getParser() : null;
    }

    /**
     * 
     *
     * @param rdfSyntax
     * @return
     */
    private static org.openrdf.rio.RDFParserFactory _getFactory(final Syntax rdfSyntax) {
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
        else if (Syntax.RDFA.equals(rdfSyntax)) {
            return _RDFA_FACTORY;
        }
        throw new RuntimeException("Internal error: No factory found for: " + rdfSyntax);
    }

}
