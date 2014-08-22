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
package com.semagia.mio.stm;

import java.io.File;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.semagia.mio.IDeserializer;
import com.semagia.mio.cxtm.AbstractValidCXTMTestCase;
import com.semagia.mio.cxtm.CXTMTestUtils;

/**
 * Tests against the {@link SnelloDeserializer}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 605 $ - $Date: 2011-01-19 00:10:28 +0100 (Mi, 19 Jan 2011) $
 */
@RunWith(Parameterized.class)
public class TestSnelloDeserializer extends AbstractValidCXTMTestCase {

    public TestSnelloDeserializer(File file, String inputDir, String referenceDir, boolean convertToTMDM) {
        super(file, inputDir, referenceDir, convertToTMDM);
    }

    @Parameters
    public static Collection<Object> makeTestCases() {
        return CXTMTestUtils.findCXTMTests("stm", "/cxtm/snello/");
      
    }

    @Override
    protected IDeserializer makeDeserializer() throws Exception {
        return new SnelloDeserializerFactory().createDeserializer();
    }

}
