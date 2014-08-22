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
package com.semagia.mio.xtm;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.semagia.mio.IDeserializer;
import com.semagia.mio.Property;
import com.semagia.mio.cxtm.AbstractValidCXTMTestCase;

/**
 * Tests against the {@link XTMDeserializer}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 462 $ - $Date: 2010-09-08 01:20:13 +0200 (Mi, 08 Sep 2010) $
 */
@RunWith(Parameterized.class)
public class TestXTMDeserializerIgnoreMergemap extends AbstractValidCXTMTestCase {

    public TestXTMDeserializerIgnoreMergemap(File file, String inputDir, String referenceDir, boolean convertToTMDM) {
        super(file, inputDir, referenceDir, convertToTMDM);
    }

    @Parameters
    public static Collection<Object> makeTestCases() {
        Collection<Object> result = new ArrayList<Object>();
        result.addAll(TestXTM10DeserializerIgnoreMergemap.makeTestCases());
        result.addAll(TestXTM20DeserializerIgnoreMergemap.makeTestCases());
        return result;
    }

    @Override
    protected IDeserializer makeDeserializer() throws Exception {
        final IDeserializer deser = new XTMDeserializerFactory().createDeserializer();
        deser.setProperty(Property.IGNORE_MERGEMAP, Boolean.TRUE);
        return deser;
    }

}
 