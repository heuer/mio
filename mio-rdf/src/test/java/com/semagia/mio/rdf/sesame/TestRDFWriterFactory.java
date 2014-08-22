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
import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;

import com.semagia.mio.Syntax;

/**
 * Tests against the (internal) {@link RDFWriterFactory}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 569 $ - $Date: 2010-09-28 22:34:01 +0200 (Di, 28 Sep 2010) $
 */
public class TestRDFWriterFactory extends TestCase {

    public void testValid() {
        final OutputStream out = new ByteArrayOutputStream();
        assertNotNull(RDFWriterFactory.createWriter(Syntax.RDFXML, out));
        assertNotNull(RDFWriterFactory.createWriter(Syntax.N3, out));
        assertNotNull(RDFWriterFactory.createWriter(Syntax.TRIG, out));
        assertNotNull(RDFWriterFactory.createWriter(Syntax.TRIX, out));
        assertNotNull(RDFWriterFactory.createWriter(Syntax.NTRIPLES, out));
        assertNotNull(RDFWriterFactory.createWriter(Syntax.TURTLE, out));
    }

    public void testInvalid() {
        try {
            RDFWriterFactory.createWriter(Syntax.CTM, new ByteArrayOutputStream());
            fail("Expected an error for syntax CTM");
        }
        catch (RuntimeException ex) {
            // noop.
        }
    }
}
