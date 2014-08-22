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
package com.semagia.mio.rdf;

import java.io.IOException;

import com.semagia.mio.MIOException;
import com.semagia.mio.Source;
import com.semagia.mio.Syntax;
import com.semagia.mio.rdf.api.IMappingHandler;
import com.semagia.mio.rdf.api.IMappingPrefixListener;
import com.semagia.mio.rdf.api.IMappingReader;
import com.semagia.mio.rdf.api.IMappingReaderFactory;

import junit.framework.TestCase;

/**
 * Tests against the {@link MappingReaderRegistry}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 593 $ - $Date: 2010-11-06 14:04:53 +0100 (Sa, 06 Nov 2010) $
 */
public class TestMappingReaderRegistry extends TestCase {

    private static final Syntax _SYNTAX = Syntax.XTM;

    public void testAdding() {
        final MappingReaderRegistry registry = MappingReaderRegistry.getInstance();
        assertNull(registry.createReader(_SYNTAX));
        IMappingReaderFactory factory = new MockMappingReaderFactory();
        registry.registerFactory(factory);
        IMappingReader reader = registry.createReader(_SYNTAX);
        assertNotNull(reader);
        assertTrue(reader instanceof MockMappingReader);
        factory = new MockMappingReaderFactory2();
        registry.registerFactory(factory);
        reader = registry.createReader(_SYNTAX);
        assertNotNull(reader);
        assertTrue(reader instanceof MockMappingReader2);
        // clean up
        registry.unregisterFactory(factory);
    }

    public void testAddingIllegal() {
        try {
            MappingReaderRegistry.getInstance().registerFactory(null);
            fail("registerFactory(null) is illegal");
        }
        catch (IllegalArgumentException ex) {
            // noop.
        }
    }

    public void testRemoval() {
        final MappingReaderRegistry registry = MappingReaderRegistry.getInstance();
        assertNull(registry.createReader(_SYNTAX));
        IMappingReaderFactory factory = new MockMappingReaderFactory();
        registry.registerFactory(factory);
        IMappingReader reader = registry.createReader(_SYNTAX);
        assertNotNull(reader);
        assertTrue(reader instanceof MockMappingReader);
        registry.unregisterFactory(factory);
        assertNull(registry.createReader(_SYNTAX));
        factory = new MockMappingReaderFactory2();
        registry.unregisterFactory(factory);
        assertNull(registry.createReader(_SYNTAX));
        registry.unregisterFactory(null);
    }

    private static class MockMappingReaderFactory implements IMappingReaderFactory {

        @Override
        public IMappingReader createReader() {
            return new MockMappingReader();
        }

        @Override
        public Syntax getSyntax() {
            return _SYNTAX;
        }

    }

    private static class MockMappingReader implements IMappingReader {

        @Override
        public void read(Source source) throws IOException, MIOException { }

        @Override
        public IMappingHandler getMappingHandler() { return null; }

        @Override
        public void setMappingHandler(IMappingHandler handler) { }

        @Override
        public void setPrefixListener(IMappingPrefixListener listener) { }
        
    }

    private static class MockMappingReaderFactory2 implements IMappingReaderFactory {

        @Override
        public IMappingReader createReader() {
            return new MockMappingReader2();
        }

        @Override
        public Syntax getSyntax() {
            return _SYNTAX;
        }

    }

    private static class MockMappingReader2 implements IMappingReader {

        @Override
        public void read(Source source) throws IOException, MIOException { }

        @Override
        public IMappingHandler getMappingHandler() { return null; }

        @Override
        public void setMappingHandler(IMappingHandler handler) { }

        @Override
        public void setPrefixListener(IMappingPrefixListener listener) { }
        
    }
}
