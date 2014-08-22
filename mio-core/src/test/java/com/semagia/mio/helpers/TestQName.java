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
 * Tests against {@link QName}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 607 $ - $Date: 2011-01-20 02:28:15 +0100 (Do, 20 Jan 2011) $
 */
public class TestQName extends TestCase {

    public void testValid() {
        String prefix = "q";
        String local = "name";
        QName qName = QName.create(prefix + ":" + local);
        assertEquals(prefix, qName.getPrefix());
        assertEquals(local, qName.getLocal());
    }

    public void testInvalid() {
        String[] invalidQNames = new String[] {"invalid:q:name", "qname", "q::name"};
        for (String invalid: invalidQNames) {
            try {
                QName.create(invalid);
                fail("Expected a failure for '" + invalid + "'");
            }
            catch (Exception ex) {
                // noop.
            }
        }
    }
}
