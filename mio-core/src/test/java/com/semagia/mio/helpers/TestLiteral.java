/*
 * Copyright 2007 - 2010 Lars Heuer (heuer[at]semagia.com)
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
package com.semagia.mio.helpers;

import com.semagia.mio.voc.XSD;

import junit.framework.TestCase;

/**
 * Tests against the {@link Literal} class.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 607 $ - $Date: 2011-01-20 02:28:15 +0100 (Do, 20 Jan 2011) $
 */
public class TestLiteral extends TestCase {

    public void testString() {
        Literal lit = Literal.createString("Semagia");
        assertEquals("Semagia", lit.getValue());
        assertEquals(XSD.STRING, lit.getDatatype());
    }

    public void testIRI() {
        Literal lit = Literal.createIRI("http://www.semagia.com/");
        assertEquals("http://www.semagia.com/", lit.getValue());
        assertEquals(XSD.ANY_URI, lit.getDatatype());
    }

    public void testInteger() {
        Literal lit = Literal.createInteger("1");
        assertEquals("1", lit.getValue());
        assertEquals(XSD.INTEGER, lit.getDatatype());
    }

    public void testDecimal() {
        Literal lit = Literal.createDecimal("2.0");
        assertEquals("2.0", lit.getValue());
        assertEquals(XSD.DECIMAL, lit.getDatatype());
    }

    public void testDate() {
        Literal lit = Literal.createDate("1976-09-19");
        assertEquals("1976-09-19", lit.getValue());
        assertEquals(XSD.DATE, lit.getDatatype());
    }

    public void testDateTime() {
        Literal lit = Literal.createDateTime("1976-09-19T19:29:20");
        assertEquals("1976-09-19T19:29:20", lit.getValue());
        assertEquals(XSD.DATE_TIME, lit.getDatatype());
    }

    public void testNonStandard() {
        Literal lit = Literal.create("Hello", "World");
        assertEquals("Hello", lit.getValue());
        assertEquals("World", lit.getDatatype());
    }
}
