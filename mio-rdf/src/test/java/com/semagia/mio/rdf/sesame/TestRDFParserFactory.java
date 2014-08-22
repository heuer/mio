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

import junit.framework.TestCase;

import com.semagia.mio.Syntax;

/**
 * Tests against the (internal) {@link RDFParserFactory}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 563 $ - $Date: 2010-09-28 11:49:34 +0200 (Di, 28 Sep 2010) $
 */
public class TestRDFParserFactory extends TestCase {

    public void testValid() {
        assertNotNull(RDFParserFactory.createParser(Syntax.RDFXML));
        assertNotNull(RDFParserFactory.createParser(Syntax.N3));
        assertNotNull(RDFParserFactory.createParser(Syntax.TRIG));
        assertNotNull(RDFParserFactory.createParser(Syntax.TRIX));
        assertNotNull(RDFParserFactory.createParser(Syntax.NTRIPLES));
        assertNotNull(RDFParserFactory.createParser(Syntax.TURTLE));
        assertNotNull(RDFParserFactory.createParser(Syntax.RDFA));
    }

    public void testInvalid() {
        try {
            RDFParserFactory.createParser(Syntax.CTM);
            fail("Expected an error for syntax CTM");
        }
        catch (RuntimeException ex) {
            // noop.
        }
    }
}
