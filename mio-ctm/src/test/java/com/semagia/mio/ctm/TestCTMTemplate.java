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
package com.semagia.mio.ctm;

import com.semagia.mio.MIOException;

import junit.framework.TestCase;

/**
 * Tests against {@link CTMTemplate}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public class TestCTMTemplate extends TestCase {

    private static final String _BASE = "http://test.semagia.com/map";

    private static CTMTemplate.Builder builder() throws Exception {
        return CTMTemplate.builder(_BASE);
    }

    private static CTMTemplate.Builder builder(String name) throws Exception {
        return builder().name(name);
    }

    private static CTMTemplate buildEmpty(final String name) throws Exception {
        return builder(name).build("");
    }

    private static CTMTemplate buildEmpty() throws Exception {
        return builder().build("");
    }

    public void testName() throws Exception {
        CTMTemplate tpl = buildEmpty("tpl");
        assertEquals("tpl", tpl.getName());
        tpl = buildEmpty();
        assertNotNull(tpl.getName());
        assertTrue(CTMUtils.isValidId(tpl.getName()));
    }

    public void testParse() throws Exception {
        builder().build("topic.");
    }

    public void testParse2() throws Exception {
        builder().build("$x isa $y.");
    }

    public void testQNameInvalid() throws Exception {
        try {
            builder().build("q:name isa qname.");
            fail("The prefix 'q' is not defined");
        }
        catch (MIOException ex) {
            // noop.
        }
    }

    public void testQNameValid() throws Exception {
        builder().addPrefix("q", "http://example.org/").build("q:name isa qname.");
    }

}
