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
 * Tests against the {@link N3Deserializer}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 473 $ - $Date: 2010-09-08 13:36:04 +0200 (Mi, 08 Sep 2010) $
 */
@RunWith(Parameterized.class)
public class TestN3DeserializerMappingIRI extends AbstractValidCXTMTestCase {

    public TestN3DeserializerMappingIRI(File file, String inputDir, String referenceDir, boolean convertToTMDM) {
        super(file, inputDir, referenceDir, convertToTMDM);
    }

    @Parameters
    public static Collection<Object> makeTestCases() {
        return CXTMTestUtils.findCXTMTests("n3", "/cxtm/rdf/externalmapping/");
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
        final IDeserializer deser = new N3DeserializerFactory().createDeserializer();
        final URL url = TestN3DeserializerMappingIRI.class.getResource("/cxtm/rdf/externalmapping/foaf-mapping.rdf");
        deser.setProperty(Property.RDF2TM_MAPPING_IRI, url.toExternalForm());
        return deser;
    }

}
