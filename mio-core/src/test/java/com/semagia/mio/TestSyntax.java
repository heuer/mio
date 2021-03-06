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
package com.semagia.mio;

import junit.framework.TestCase;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 607 $ - $Date: 2011-01-20 02:28:15 +0100 (Do, 20 Jan 2011) $
 */
public class TestSyntax extends TestCase {

    public void testValueOf() {
        assertEquals(Syntax.CTM, Syntax.valueOf("cTM"));
        assertEquals(Syntax.CTM, Syntax.valueOf("ctm"));
    }

    public void testForFileExtension() {
        assertEquals(null, Syntax.forFileExtension("the-not-yet-invented-syntax"));
        assertEquals(Syntax.N3, Syntax.forFileExtension("n3"));
        assertEquals(Syntax.N3, Syntax.forFileExtension("n3", null));
        assertEquals(Syntax.CRTM, Syntax.forFileExtension("crtm", null));
    }

    public void testForFilename() {
        assertEquals(null, Syntax.forFilename("the-not-yet-invented-syntax"));
        assertNull(Syntax.forFilename("n3"));
        assertEquals(Syntax.N3, Syntax.forFilename("a-file.here.n3"));
        assertEquals(Syntax.N3, Syntax.forFilename("a.name.here.n3", null));
        assertEquals(Syntax.CRTM, Syntax.forFilename("xy.crtm", null));
    }
}
