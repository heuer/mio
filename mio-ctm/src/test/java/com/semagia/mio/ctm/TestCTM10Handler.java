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
package com.semagia.mio.ctm;

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
 * Tests against the {@link CTM10Handler}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 616 $ - $Date: 2011-04-10 14:59:36 +0200 (So, 10 Apr 2011) $
 */
@RunWith(Parameterized.class)
public class TestCTM10Handler extends AbstractCXTMWritingHandlerTestCase {

    public TestCTM10Handler(File file, String inputDir, String referenceDir, boolean convertToTMDM) {
        super(file, inputDir, referenceDir, convertToTMDM);
    }

    @Parameters
    public static Collection<Object> makeTestCases() {
        Collection<Object> result = new ArrayList<Object>();
        result.addAll(Filter.from("/cxtm/ctm/")
                        .using("ctm")
                        .exclude(
                                // Topic is serialized in advance of the tm reifier
                                "tm-reifier2.ctm"
                        )
                        .filter());
        result.addAll(Filter.from("/cxtm/xtm2/", "/cxtm/xtm21/")
                .using("xtm")
                .exclude(
                        "subjid-escaping.xtm", // Uncertain about this one
                        "subjid-escaping2.xtm", // Uncertain about this one
                        // Constructs != topic which have an iid
                        "association-duplicate-iid.xtm",
                        "association-duplicate-iid2.xtm",
                        "association-duplicate-iid3.xtm",
                        "association-duplicate-reified2.xtm",
                        "association-duplicate-reified3.xtm",
                        "association-duplicate-reified4.xtm",
                        "itemid-association.xtm",
                        "itemid-name.xtm",
                        "itemid-occurrence.xtm",
                        "itemid-role.xtm",
                        "itemid-tm.xtm",
                        "itemid-variant.xtm",
                        "mergemap-itemid.xtm",
                        "name-duplicate-iid.xtm",
                        "name-duplicate-reified3.xtm",
                        "name-duplicate-reified4.xtm",
                        "occurrence-duplicate-iid.xtm",
                        "occurrence-duplicate-iid2.xtm",
                        "role-duplicate-iid.xtm",
                        "role-duplicate-iid2.xtm",
                        "variant-duplicate-iid.xtm"
                )
                .filter());
        return result;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.cxtm.AbstractCXTMWritingHandlerTestCase#getFileExtension()
     */
    @Override
    protected String getFileExtension() {
        return "ctm";
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.cxtm.AbstractCXTMWritingHandlerTestCase#makeOutputHandler(java.io.OutputStream, java.lang.String)
     */
    @Override
    protected IMapHandler makeOutputHandler(final OutputStream out, final String baseIRI)
            throws Exception {
        CTM10Handler handler = new CTM10Handler(out);
        handler.addPrefix("_", baseIRI + "#");
        return handler;
    }

}
