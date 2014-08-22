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

import com.semagia.mio.IRef;

import junit.framework.TestCase;

/**
 * Tests against {@link OasisLanguageTagProvider}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 473 $ - $Date: 2010-09-08 13:36:04 +0200 (Mi, 08 Sep 2010) $
 */
public class TestOasisLanguageTagProvider extends TestCase {

    private void _assertEquals(final String lang, final String fragment) {
        final IRef iri = OasisLanguageTagProvider.getInstance().getLanguage(lang);
        if (fragment == null) {
            assertNull(iri);
        }
        else {
            assertTrue(iri.getIRI().endsWith("#" + fragment));
        }
    }

    public void testNull() {
        _assertEquals(null, null);
    }

    public void testIllegalAlpha2Code() {
        _assertEquals("fq", null);
    }

    public void testAlpha2Code() {
        _assertEquals("de", "deu");
        _assertEquals("en", "eng");
    }

    public void testIllgalAlpha3Code() {
        _assertEquals("xxx", null);
    }

    public void testAlpha3Code() {
        _assertEquals("deu", "deu");
        _assertEquals("ger", "deu");
        _assertEquals("eng", "eng");
        _assertEquals("wel", "cym");
        _assertEquals("cym", "cym");
    }
}
