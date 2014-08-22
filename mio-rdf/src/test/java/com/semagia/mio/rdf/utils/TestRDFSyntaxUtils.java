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
package com.semagia.mio.rdf.utils;

import com.semagia.mio.Syntax;

import junit.framework.TestCase;

/**
 * Tests against {@link RDFSyntaxUtils}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 571 $ - $Date: 2010-09-28 23:37:38 +0200 (Di, 28 Sep 2010) $
 */
public class TestRDFSyntaxUtils extends TestCase {

    /**
     * Test retrieving a syntax by a file extension.
     */
    public void testByFileExtension() {
        assertEquals(Syntax.RDFXML, RDFSyntaxUtils.forFileExtension("rdf"));
        assertEquals(Syntax.RDFXML, RDFSyntaxUtils.forFileExtension("rdfs"));
        assertEquals(Syntax.RDFXML, RDFSyntaxUtils.forFileExtension("owl"));
        assertEquals(Syntax.RDFXML, RDFSyntaxUtils.forFileExtension("xml"));
        assertEquals(Syntax.RDFA, RDFSyntaxUtils.forFileExtension("htm"));
        assertEquals(Syntax.RDFA, RDFSyntaxUtils.forFileExtension("html"));
        assertEquals(Syntax.RDFA, RDFSyntaxUtils.forFileExtension("xhtml"));
        assertEquals(Syntax.N3, RDFSyntaxUtils.forFileExtension("n3"));
        assertEquals(Syntax.NTRIPLES, RDFSyntaxUtils.forFileExtension("nt"));
        assertEquals(Syntax.NTRIPLES, RDFSyntaxUtils.forFileExtension("ntriple"));
        assertEquals(Syntax.NTRIPLES, RDFSyntaxUtils.forFileExtension("ntriples"));
        assertEquals(Syntax.TRIG, RDFSyntaxUtils.forFileExtension("trig"));
        assertEquals(Syntax.TRIX, RDFSyntaxUtils.forFileExtension("trix"));
        assertEquals(Syntax.TURTLE, RDFSyntaxUtils.forFileExtension("ttl"));
    }

    /**
     * Test retrieving a syntax by an unknown file extension.
     */
    public void testByFileExtensionInvalid() {
        assertNull(RDFSyntaxUtils.forFileExtension("ctm"));
        assertNull(RDFSyntaxUtils.forFileExtension("crtm"));
    }

    /**
     * Tests retrieval of an unknown syntax.
     */
    public void testByFileExtensionDefaultSyntax() {
        assertEquals(Syntax.RDFXML, RDFSyntaxUtils.forFileExtension("unknown-syntax", Syntax.RDFXML));
        assertEquals(Syntax.N3, RDFSyntaxUtils.forFileExtension("unknown-syntax", Syntax.N3));
    }

    /**
     * Test retrieving a syntax by a MIME type.
     */
    public void testByMIMEType() {
        assertEquals(Syntax.RDFXML, RDFSyntaxUtils.forMIMEType("application/xml"));
        assertEquals(Syntax.RDFXML, RDFSyntaxUtils.forMIMEType("application/rdf+xml"));
        assertEquals(Syntax.RDFA, RDFSyntaxUtils.forMIMEType("text/html"));
        assertEquals(Syntax.RDFA, RDFSyntaxUtils.forMIMEType("application/xhtml+xml"));
        assertEquals(Syntax.N3, RDFSyntaxUtils.forMIMEType("application/rdf+n3"));
        assertEquals(Syntax.N3, RDFSyntaxUtils.forMIMEType("text/rdf+n3"));
        assertEquals(Syntax.NTRIPLES, RDFSyntaxUtils.forMIMEType("text/plain"));
        assertEquals(Syntax.TRIG, RDFSyntaxUtils.forMIMEType("application/x-trig"));
        assertEquals(Syntax.TRIX, RDFSyntaxUtils.forMIMEType("application/trix"));
        assertEquals(Syntax.TURTLE, RDFSyntaxUtils.forMIMEType("application/x-turtle"));
    }

    /**
     * Test retrieving a syntax by an unknown MIME type with a default value
     */
    public void testByMIMETypeDefault() {
        assertEquals(Syntax.RDFXML, RDFSyntaxUtils.forMIMEType("unknown/syntax", Syntax.RDFXML));
    }

    /**
     * Test retrieving a syntax by an unknown MIME type.
     */
    public void testByMIMETypeInvalid() {
        assertNull(RDFSyntaxUtils.forMIMEType("application/x-tm+ctm"));
        assertNull(RDFSyntaxUtils.forMIMEType("application/x-tm+crtm"));
    }

    /**
     * Test retrieving a syntax by a file extension.
     */
    public void testMappingByFileExtension() {
        assertEquals(Syntax.RDFXML, RDFSyntaxUtils.mappingForFileExtension("rdf"));
        assertEquals(Syntax.RDFXML, RDFSyntaxUtils.mappingForFileExtension("rdfs"));
        assertEquals(Syntax.RDFXML, RDFSyntaxUtils.mappingForFileExtension("owl"));
        assertEquals(Syntax.RDFXML, RDFSyntaxUtils.mappingForFileExtension("xml"));
        assertEquals(Syntax.RDFA, RDFSyntaxUtils.mappingForFileExtension("htm"));
        assertEquals(Syntax.RDFA, RDFSyntaxUtils.mappingForFileExtension("html"));
        assertEquals(Syntax.RDFA, RDFSyntaxUtils.mappingForFileExtension("xhtml"));
        assertEquals(Syntax.N3, RDFSyntaxUtils.mappingForFileExtension("n3"));
        assertEquals(Syntax.NTRIPLES, RDFSyntaxUtils.mappingForFileExtension("nt"));
        assertEquals(Syntax.NTRIPLES, RDFSyntaxUtils.mappingForFileExtension("ntriple"));
        assertEquals(Syntax.NTRIPLES, RDFSyntaxUtils.mappingForFileExtension("ntriples"));
        assertEquals(Syntax.TRIG, RDFSyntaxUtils.mappingForFileExtension("trig"));
        assertEquals(Syntax.TRIX, RDFSyntaxUtils.mappingForFileExtension("trix"));
        assertEquals(Syntax.TURTLE, RDFSyntaxUtils.mappingForFileExtension("ttl"));
        assertEquals(Syntax.CRTM, RDFSyntaxUtils.mappingForFileExtension("crtm"));
    }

    /**
     * Test retrieving a syntax by an unknown file extension.
     */
    public void testMappingByFileExtensionInvalid() {
        assertNull(RDFSyntaxUtils.forFileExtension("ctm"));
        assertNull(RDFSyntaxUtils.forFileExtension("crtm"));
    }

    /**
     * Tests retrieval of an unknown syntax.
     */
    public void testMappingByFileExtensionDefaultSyntax() {
        assertEquals(Syntax.RDFXML, RDFSyntaxUtils.forFileExtension("unknown-syntax", Syntax.RDFXML));
        assertEquals(Syntax.N3, RDFSyntaxUtils.forFileExtension("unknown-syntax", Syntax.N3));
    }

    /**
     * Test retrieving a syntax by a MIME type.
     */
    public void testMappingByMIMEType() {
        assertEquals(Syntax.RDFXML, RDFSyntaxUtils.mappingForMIMEType("application/xml"));
        assertEquals(Syntax.RDFXML, RDFSyntaxUtils.mappingForMIMEType("application/rdf+xml"));
        assertEquals(Syntax.RDFA, RDFSyntaxUtils.mappingForMIMEType("text/html"));
        assertEquals(Syntax.RDFA, RDFSyntaxUtils.mappingForMIMEType("application/xhtml+xml"));
        assertEquals(Syntax.N3, RDFSyntaxUtils.mappingForMIMEType("application/rdf+n3"));
        assertEquals(Syntax.N3, RDFSyntaxUtils.mappingForMIMEType("text/rdf+n3"));
        assertEquals(Syntax.NTRIPLES, RDFSyntaxUtils.mappingForMIMEType("text/plain"));
        assertEquals(Syntax.TRIG, RDFSyntaxUtils.mappingForMIMEType("application/x-trig"));
        assertEquals(Syntax.TRIX, RDFSyntaxUtils.mappingForMIMEType("application/trix"));
        assertEquals(Syntax.TURTLE, RDFSyntaxUtils.mappingForMIMEType("application/x-turtle"));
        assertEquals(Syntax.CRTM, RDFSyntaxUtils.mappingForMIMEType("application/x-tm+crtm"));
    }

    /**
     * Test retrieving a syntax by an unknown MIME type with a default value
     */
    public void testMappingByMIMETypeDefault() {
        assertEquals(Syntax.RDFXML, RDFSyntaxUtils.mappingForMIMEType("unknown/syntax", Syntax.RDFXML));
    }

    /**
     * Test retrieving a syntax by an unknown MIME type.
     */
    public void testMappingByMIMETypeInvalid() {
        assertNull(RDFSyntaxUtils.mappingForMIMEType("application/x-tm+ctm"));
    }
}
