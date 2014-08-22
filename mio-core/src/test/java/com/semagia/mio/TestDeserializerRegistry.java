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
package com.semagia.mio;

import java.io.IOException;

import junit.framework.TestCase;

/**
 * Tests against the {@link DeserializerRegistry}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 607 $ - $Date: 2011-01-20 02:28:15 +0100 (Do, 20 Jan 2011) $
 */
public class TestDeserializerRegistry extends TestCase {

    public void testRegisterUnregister() {
        final DeserializerRegistry registry = DeserializerRegistry.getInstance();
        final IDeserializerFactory factory = new NoopDeserializerFactory();
        registry.registerFactory(factory);
        assertTrue(registry.createDeserializer(factory.getSyntax()) instanceof NoopDeserializer);
        registry.unregisterFactory(factory);
        assertNull(registry.createDeserializer(factory.getSyntax()));
    }

    public void testReplacement() {
        final DeserializerRegistry registry = DeserializerRegistry.getInstance();
        final IDeserializerFactory factory = new NoopDeserializerFactory();
        final IDeserializerFactory factory2 = new NoopDeserializerFactory2();
        assertEquals(factory.getSyntax(), factory2.getSyntax());
        registry.registerFactory(factory);
        assertTrue(registry.createDeserializer(factory.getSyntax()) instanceof NoopDeserializer);
        registry.registerFactory(factory2);
        assertTrue(registry.createDeserializer(factory.getSyntax()) instanceof NoopDeserializer2);
    }

    public void testIllegalFactory() {
        try {
            DeserializerRegistry.getInstance().registerFactory(null);
            fail("Registry should not accept a null factory");
        }
        catch (IllegalArgumentException ex) {
            // noop.
        }
    }

    public void testUnregister() {
        final DeserializerRegistry registry = DeserializerRegistry.getInstance();
        final IDeserializerFactory factory = new NoopDeserializerFactory();
        registry.unregisterFactory(null);
        registry.unregisterFactory(factory);
    }

    private static class NoopDeserializerFactory implements IDeserializerFactory {

        @Override
        public IDeserializer createDeserializer() {
            return new NoopDeserializer();
        }

        @Override
        public Syntax getSyntax() {
            return Syntax.ASTMA;
        }

    }
    
    private static class NoopDeserializerFactory2 extends NoopDeserializerFactory {
        
        @Override
        public IDeserializer createDeserializer() {
            return new NoopDeserializer2();
        }
    }

    private static class NoopDeserializer implements IDeserializer {

        @Override
        public Context getContext() {
            return null;
        }

        @Override
        public void parse(Source src) throws IOException, MIOException {
        }

        @Override
        public void setContext(Context ctx) {
        }

        @Override
        public void setMapHandler(IMapHandler handler) {
        }

        @Override
        public void setSubordinate(boolean subordinate) {
        }

        @Override
        public Object getProperty(String iri) {
            return null;
        }

        @Override
        public void setProperty(String iri, Object value) {
        }
        
    }
    
    private static class NoopDeserializer2 extends NoopDeserializer {}
}
