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

import com.semagia.mio.IDeserializer;
import com.semagia.mio.Property;

/**
 * Tests against the {@link XTM21Deserializer} (validating mode).
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 462 $ - $Date: 2010-09-08 01:20:13 +0200 (Mi, 08 Sep 2010) $
 */
public class TestXTM21ValidatingDeserializer extends TestXTM21Deserializer {

    public TestXTM21ValidatingDeserializer(File file, String inputDir, String referenceDir, boolean convertToTMDM) {
        super(file, inputDir, referenceDir, convertToTMDM);
    }

    @Override
    protected IDeserializer makeDeserializer() throws Exception {
        final IDeserializer deser = super.makeDeserializer();
        deser.setProperty(Property.VALIDATE, Boolean.TRUE);
        return deser;
    }

}
