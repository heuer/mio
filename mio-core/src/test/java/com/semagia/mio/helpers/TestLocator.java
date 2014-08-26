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

import junit.framework.TestCase;

/**
 * Tests against the {@link Locator} class.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 607 $ - $Date: 2011-01-20 02:28:15 +0100 (Do, 20 Jan 2011) $
 */
public class TestLocator extends TestCase {

    public void testLocator() {
        final Locator loc = Locator.create("http://www.semagia.com/");
        assertEquals("http://www.semagia.com/", loc.getReference());
        assertEquals("http://www.semagia.com/#frag", loc.resolve("#frag").getReference());
        assertEquals("http://www.semagia.net/", loc.resolve("http://www.semagia.net/").getReference());
        assertEquals("http://www.semagia.com/something", loc.resolve("something").getReference());
    }

/* This does not work yet.

    public void testNormalization() {
        final String reference = "http://www.semagia.com/";
        final Locator refLoc = Locator.create(reference);
        String[] addresses = new String[] {
                "http://www.Semagia.Com",
                "http://www.semagia.com:80",
                "http://www.semagia.com/",
                "http://www.semagia.com:80/",
                "http://www.semagia.com:/",
                "hTTp://www.semagia.com/",
                "hTTp://www.semagia.com"
        };
        for (int i=0; i<addresses.length; i++) {
            Locator loc = Locator.create(addresses[i]);
            assertEquals("Expected: '" + reference + "', got: '" + loc.getReference() + "'", 
                    refLoc, loc);
            assertEquals(reference, loc.getReference());
        }
    }

    public void testNormalizationFile() {
        final String reference = "file:///c:/foo.bar";
        String[] addresses = new String[] {
                "file:///c:/foo.bar",
                "file:///c|/foo.bar",
                "file:///C:/foo.bar",
                "file:///C|/foo.bar"
        };
        for (int i=0; i<addresses.length; i++) {
            Locator loc = Locator.create(addresses[i]);
            assertEquals(addresses[i], reference, loc.getReference());
        }
    }
*/
}
