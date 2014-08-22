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
package com.semagia.mio.rdf.crtm;

import java.io.StringReader;

import com.semagia.mio.rdf.mapping.DefaultMappingHandler;

import junit.framework.TestCase;

/**
 * Tests against the {@link CRTMParser}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 550 $ - $Date: 2010-09-26 11:35:06 +0200 (So, 26 Sep 2010) $
 */
public class TestCRTMParser extends TestCase {

    private static final String _BASE = "http://test.semagia.com/";

    private CRTMParser _parser() {
        return new CRTMParser(_BASE);
    }

    private void _parse(final String val) throws Exception {
        CRTMParser parser = _parser();
        parser.setMappingHandler(new DefaultMappingHandler());
        parser.parse(new StringReader(val));
    }

    public void testPrefix() throws Exception {
        _parse("%prefix ident <iri>");
    }

    public void testOccurrenceNoTypeNoScope() throws Exception {
        _parse("%prefix ident <iri> ident:local: occ");
        _parse("%prefix ident <iri> ident:local: occurrence");
    }

    public void testOccurrenceType() throws Exception {
        _parse("%prefix ident <iri> ident:local: occ <bla:type>");
        _parse("%prefix ident <iri> ident:local: occurrence <bla:type>");
        _parse("%prefix ident <iri> ident:local: <bla:type>");
    }

    public void testNameNoTypeNoScope() throws Exception {
        _parse("%prefix ident <iri> ident:local: name");
        _parse("%prefix ident <iri> ident:local: -");
    }

    public void testCurlies() throws Exception {
        _parse("%prefix q <iri> q { name: name 123: occurrence 393.333: -}");
    }

    public void testMultipredicates() throws Exception {
        _parse("%prefix q <iri> q { b, c: name d,e: occurrence f, g, h: -}");
    }
    
    public void testMultipredicates2() throws Exception {
        _parse("%prefix q <iri>\n" +
                "q:b, q:c: name\n" +
                "q:d, q:e: occurrence q:f, q:g, q:h: -");
    }
}
