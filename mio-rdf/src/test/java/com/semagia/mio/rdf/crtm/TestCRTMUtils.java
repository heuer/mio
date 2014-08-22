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
package com.semagia.mio.rdf.crtm;

import junit.framework.TestCase;

/**
 * Tests against {@link CRTMUtils}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 576 $ - $Date: 2010-09-29 15:53:49 +0200 (Mi, 29 Sep 2010) $
 */
public class TestCRTMUtils extends TestCase {

    public void testValidLocalPart() {
        assertFalse(CRTMUtils.isValidLocalPart("-issue"));
        assertTrue(CRTMUtils.isValidLocalPart("1976-09-19"));
        assertTrue(CRTMUtils.isValidLocalPart("1976"));
        assertTrue(CRTMUtils.isValidLocalPart("semagia"));
    }
    
    public void testValidId() {
        assertFalse(CRTMUtils.isValidId("-issue"));
        assertFalse(CRTMUtils.isValidId("1976-09-19"));
        assertTrue(CRTMUtils.isValidId("a1976-09-19"));
        assertTrue(CRTMUtils.isValidId("issue."));
        assertTrue(CRTMUtils.isValidId("is.sue"));
        assertTrue(CRTMUtils.isValidId("issue12"));
    }

}
