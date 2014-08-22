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
package com.semagia.mio.xtm;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.semagia.mio.IMapHandler;
import com.semagia.mio.cxtm.AbstractCXTMWritingHandlerTestCase;
import com.semagia.mio.cxtm.CXTMTestUtils.Filter;

/**
 * Tests against the {@link XTM21Handler}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 496 $ - $Date: 2010-09-09 18:45:19 +0200 (Do, 09 Sep 2010) $
 */
@RunWith(Parameterized.class)
public class TestXTM21Handler extends AbstractCXTMWritingHandlerTestCase {

    public TestXTM21Handler(File file, String inputDir, String referenceDir, boolean convertToTMDM) {
        super(file, inputDir, referenceDir, convertToTMDM);
    }

    @Parameters
    public static Collection<Object> makeTestCases() {
        Collection<Object> result = new ArrayList<Object>();
        result.addAll(Filter.from("/cxtm/xtm2/", "/cxtm/xtm21/")
                        .using("xtm")
                        .exclude(
                                "subjid-escaping.xtm", // Uncertain about this one
                                "subjid-escaping2.xtm" // Uncertain about this one
                        )
                        .filter());
        result.addAll(Filter.from("/cxtm/ctm/")
                        .using("ctm")
                        .exclude(
                                "occurrence-string-multiline2.ctm",
                                "string-escape.ctm",
                                // Topic is serialized in advance of the tm reifier
                                "tm-reifier2.ctm"
                        )
                        .filter());
        result.addAll(Filter.from("/cxtm/jtm/")
                        .using("jtm")
                        .exclude(
                                "subjid-escaping.xtm2.jtm", // Uncertain about this one
                                "subjid-escaping2.xtm2.jtm" // Uncertain about this one
                        )
                        .filter());
        return result;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.cxtm.AbstractCXTMWritingHandlerTestCase#getFileExtension()
     */
    @Override
    protected String getFileExtension() {
        return "xtm";
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.cxtm.AbstractCXTMWritingHandlerTestCase#makeOutputHandler(java.io.OutputStream, java.lang.String)
     */
    @Override
    protected IMapHandler makeOutputHandler(final OutputStream out, final String baseIRI)
            throws Exception {
        return new XTM21Handler(out);
    }

}
