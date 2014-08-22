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
package com.semagia.mio.cxtm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.LinkedList;

import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;

import com.semagia.mio.DeserializerRegistry;
import com.semagia.mio.IDeserializer;
import com.semagia.mio.IMapHandler;
import com.semagia.mio.MIOException;
import com.semagia.mio.Source;
import com.semagia.mio.Syntax;
import com.semagia.mio.cxtm.diff_match_patch.Patch;

/**
 * Abstract test to validate {@link IMapHandler} implementations which serialize
 * events.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 607 $ - $Date: 2011-01-20 02:28:15 +0100 (Do, 20 Jan 2011) $
 */
public abstract class AbstractCXTMWritingHandlerTestCase {

    private final String _inputDir;
    private final String _referenceDir;
    private final boolean _convertToTMDM;
    private final File _file;
    private TopicMapSystem _sys;

    public AbstractCXTMWritingHandlerTestCase(File file, String inputDir, String referenceDir, boolean convertToTMDM) {
        _inputDir = inputDir;
        _referenceDir = referenceDir;
        _file = file;
        _convertToTMDM = convertToTMDM;
    }

    private IDeserializer _makeDeserializer(final String iri) throws Exception {
        final String ext = iri.substring(iri.lastIndexOf(".")+1);
        return _getDeserializerByFileExtension(ext);
    }

    private IDeserializer _getDeserializerByFileExtension(final String ext) {
        return DeserializerRegistry.getInstance().createDeserializer(Syntax.forFileExtension(ext));
    }

    protected abstract IMapHandler makeOutputHandler(final OutputStream out, final String iri) throws Exception;
    
    protected abstract String getFileExtension();

    private InputStream _makeInputStream(final ByteArrayOutputStream out) {
        return new ByteArrayInputStream(out.toByteArray());
    }

    private static String _getStackTrace(final Throwable t) {
        if (t == null) {
            return "null";
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        t.printStackTrace(pw);
        pw.flush();
        sw.flush();
        return sw.toString();
    }

    @Before
    public void setUp() throws Exception {
        _sys = TopicMapSystemFactory.newInstance().newTopicMapSystem();
    }

    @Test
    public void testValidCXTM() throws Exception {
        final String iri = _file.toURI().toURL().toExternalForm();
        TopicMap tm = _sys.createTopicMap(iri);
        final String referenceFile = _file.toURI().toURL().getFile().replace("/" + _inputDir + "/", "/" + _referenceDir + "/") + ".cxtm";
        IDeserializer deser = _makeDeserializer(iri);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        deser.setMapHandler(makeOutputHandler(out, iri));
        try {
            deser.parse(new Source(iri));
        }
        catch (MIOException ex) {
            fail("Parsing failed for <" + iri + "> \n" + _getStackTrace(ex) + "\nCause: " + _getStackTrace(ex.getCause()));
        }
        tm.remove();
        tm = _sys.createTopicMap(iri);
        final InputStream in = _makeInputStream(out);
        deser = _getDeserializerByFileExtension(getFileExtension());
        deser.setMapHandler(TMAPICXTMUtils.createMapHandler(tm));
        try {
            deser.parse(new Source(in, iri));
        }
        catch (MIOException ex) {
            fail("Parsing failed for <" + iri + "> \n" + _getStackTrace(ex) + "\nCause: " + _getStackTrace(ex.getCause()) + "\n --- Written topic map:\n" + out.toString("utf-8"));
        }
        if (_convertToTMDM) {
            TMAPICXTMUtils.convertToTMDM(tm);
        }
        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        final ITMAPICXTMWriter cxtmWriter = TMAPICXTMUtils.createCXTMWriter(tm, result, iri);
        cxtmWriter.write(tm);
        final ByteArrayOutputStream expected = new ByteArrayOutputStream();
        final InputStream tmp = new FileInputStream(referenceFile);
        int b;
        while ((b = tmp.read()) != -1) {
            expected.write(b);
        }
        tmp.close();
        byte[] res = result.toByteArray();
        byte[] ref = expected.toByteArray();
        if (!Arrays.equals(res, ref)) {
            final String expectedString = expected.toString("utf-8");
            final String resultString = result.toString("utf-8");
            diff_match_patch dmp = new diff_match_patch();
            LinkedList<Patch> patches = dmp.patch_make(expectedString, resultString); 
            fail(iri + ":\n" + dmp.patch_toText(patches) + "\n - - -\nExpected: " + expectedString + "\n, got: " + resultString + "\n Written topic map: \n" + out.toString("utf-8"));
        }
    }

}
