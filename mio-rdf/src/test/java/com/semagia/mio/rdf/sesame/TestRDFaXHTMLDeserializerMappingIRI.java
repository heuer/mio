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

import java.io.File;
import java.net.URL;
import java.util.Collection;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.semagia.mio.IDeserializer;
import com.semagia.mio.Property;
import com.semagia.mio.cxtm.AbstractValidCXTMTestCase;
import com.semagia.mio.cxtm.CXTMTestUtils;
import com.semagia.mio.rdf.api.IMapping;

/**
 * Tests against the {@link RDFaDeserializer}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 544 $ - $Date: 2010-09-22 11:29:20 +0200 (Mi, 22 Sep 2010) $
 */
@RunWith(Parameterized.class)
public class TestRDFaXHTMLDeserializerMappingIRI extends AbstractValidCXTMTestCase {

    public TestRDFaXHTMLDeserializerMappingIRI(File file, String inputDir, String referenceDir, boolean convertToTMDM) {
        super(file, inputDir, referenceDir, convertToTMDM);
    }

    @Parameters
    public static Collection<Object> makeTestCases() {
        return CXTMTestUtils.findCXTMTests("xhtml", "/cxtm/rdf/");
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.cxtm.AbstractValidCXTMTestCase#afterParsing(com.semagia.mio.IDeserializer)
     */
    @Override
    protected void afterParsing(IDeserializer deser) {
        Object mappingProp = deser.getProperty(Property.RDF2TM_MAPPING);
        assertNotNull(mappingProp);
        assertTrue(mappingProp instanceof IMapping);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.cxtm.AbstractValidCXTMTestCase#makeDeserializer()
     */
    @Override
    protected IDeserializer makeDeserializer() throws Exception {
        final IDeserializer deser = new RDFaDeserializerFactory().createDeserializer();
        final URL url = TestRDFaXHTMLDeserializerMappingIRI.class.getResource("/cxtm/rdf/externalmapping/rdfa-mapping.rdf");
        deser.setProperty(Property.RDF2TM_MAPPING_IRI, url.toExternalForm());
        return deser;
    }

}
