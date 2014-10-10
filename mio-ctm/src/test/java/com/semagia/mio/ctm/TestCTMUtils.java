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

import java.util.List;

import com.semagia.mio.voc.XSD;

import junit.framework.TestCase;

/**
 * Tests against {@link CTMUtils}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public class TestCTMUtils extends TestCase {

    public void testIsKeyword() {
        assertTrue(CTMUtils.isKeyword("def"));
        assertTrue(CTMUtils.isKeyword("isa"));
        assertTrue(CTMUtils.isKeyword("ako"));
        assertTrue(CTMUtils.isKeyword("end"));
        assertFalse(CTMUtils.isKeyword("dev"));
        assertFalse(CTMUtils.isKeyword("ende"));
        assertFalse(CTMUtils.isKeyword("en"));
    }

    public void testValidLocalPart() {
        assertFalse(CTMUtils.isValidLocalPart(null));
        assertFalse(CTMUtils.isValidLocalPart(""));
        assertFalse(CTMUtils.isValidLocalPart("-issue"));
        assertFalse(CTMUtils.isValidLocalPart(".123..1"));
        assertFalse(CTMUtils.isValidLocalPart("123 .1"));
        assertTrue(CTMUtils.isValidLocalPart("1976-09-19"));
        assertTrue(CTMUtils.isValidLocalPart("1976"));
        assertTrue(CTMUtils.isValidLocalPart("semagia"));
    }

    public void testValidId() {
        assertFalse(CTMUtils.isValidId(null));
        assertFalse(CTMUtils.isValidId(""));
        assertFalse(CTMUtils.isValidId("-issue"));
        assertFalse(CTMUtils.isValidId("1976-09-19"));
        assertTrue(CTMUtils.isValidId("a1976-09-19"));
        assertFalse(CTMUtils.isValidId("issue."));
        assertFalse(CTMUtils.isValidId("is sue"));
        assertTrue(CTMUtils.isValidId("is.sue"));
        assertTrue(CTMUtils.isValidId("issue12"));
    }

    public void testValidIRI() {
        assertFalse(CTMUtils.isValidIRI(null));
        assertFalse(CTMUtils.isValidIRI(""));
        assertFalse(CTMUtils.isValidIRI("http://www.semagia.com/{"));
        assertFalse(CTMUtils.isValidIRI("http://www.semagia.com/}"));
        assertFalse(CTMUtils.isValidIRI("http://www.semagia.`com"));
        assertFalse(CTMUtils.isValidIRI("http://www.semagia.com/something\\here"));
        assertFalse(CTMUtils.isValidIRI("<http://www.semagia.com>"));
        assertFalse(CTMUtils.isValidIRI("http://www.semagia.com/something here"));
        assertTrue(CTMUtils.isValidIRI("http://www.semagia.com"));
        assertTrue(CTMUtils.isValidIRI("http://www.semagia.com/something%20here"));
    }

    public void testNativeDatatype() {
        assertFalse(CTMUtils.isNativeDatatype(XSD.BASE64_BINARY));
        assertFalse(CTMUtils.isNativeDatatype(XSD.STRING));
        assertFalse(CTMUtils.isNativeDatatype(XSD.ANY_URI));
        assertFalse(CTMUtils.isNativeDatatype(XSD.DOUBLE));
        assertTrue(CTMUtils.isNativeDatatype(XSD.DATE));
        assertTrue(CTMUtils.isNativeDatatype(XSD.DATE_TIME));
        assertTrue(CTMUtils.isNativeDatatype(XSD.INTEGER));
        assertTrue(CTMUtils.isNativeDatatype(XSD.DECIMAL));
        assertTrue(CTMUtils.isNativeDatatype(CTMUtils.CTM_INTEGER));
    }

    public void testFindVariables() {
        final String s = "$a, $b, djddjd, $c, $______-d";
        final String[] expected = {"$a", "$b", "$c", "$______-d"};
        List<String> res = CTMUtils.findVariables(s);
        assertEquals(expected.length, res.size());
        for (int i=0; i<expected.length; i++) {
            assertEquals(expected[i], res.get(i));
        }
    }

    public void testFindVariablesOmitDollar() {
        final String s = "$a, $b, djddjd, $c, $______-d";
        final String[] expected = {"a", "b", "c", "______-d"};
        List<String> res = CTMUtils.findVariables(s, true);
        assertEquals(expected.length, res.size());
        for (int i=0; i<expected.length; i++) {
            assertEquals(expected[i], res.get(i));
        }
    }

    public void testFindVariablesOmitDuplicates() {
        final String s = "$a, $b, ddd $a djddjd, $c, $______-d";
        final String[] expected = {"a", "b", "c", "______-d"};
        List<String> res = CTMUtils.findVariables(s, true);
        assertEquals(expected.length, res.size());
        for (int i=0; i<expected.length; i++) {
            assertEquals(expected[i], res.get(i));
        }
    }

    public void testFindVariables2() {
        final String s = "$a isa $b.";
        final String[] expected = {"a", "b"};
        List<String> res = CTMUtils.findVariables(s, true);
        assertEquals(expected.length, res.size());
        for (int i=0; i<expected.length; i++) {
            assertEquals(expected[i], res.get(i));
        }
    }
}
