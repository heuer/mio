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
package com.semagia.mio.rdf;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.semagia.mio.Syntax;
import com.semagia.mio.rdf.api.IMappingReader;
import com.semagia.mio.rdf.api.IMappingReaderFactory;

/**
 * Default registry for {@link IMappingReaderFactory} instances. 
 * 
 * Use {@link #getInstance()} to get an instance of this class. This
 * class it thread-safe.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 560 $ - $Date: 2010-09-27 13:19:16 +0200 (Mo, 27 Sep 2010) $
 */
public final class MappingReaderRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(MappingReaderRegistry.class);

    private static final MappingReaderRegistry _INSTANCE = new MappingReaderRegistry();

    private final Map<Syntax, IMappingReaderFactory> _syntax2Factory;

    private MappingReaderRegistry() {
        _syntax2Factory = new ConcurrentHashMap<Syntax, IMappingReaderFactory>();
        // Try to find IDeserializerFactory providers
        // Won't find anything in an OSGi environment but in an OSGi environment
        // we use the register/unregister methods
        // See com.semagia.mio.internal.osgi.Activator
        for(IMappingReaderFactory factory: ServiceLoader.load(IMappingReaderFactory.class, MappingReaderRegistry.class.getClassLoader())) {
            registerFactory(factory);
        }
    }

    /**
     * Returns the instance.
     *
     * @return The registry instance.
     */
    public static MappingReaderRegistry getInstance() {
        return _INSTANCE;
    }

    /**
     * INTERNAL: Registeres the provided {@code factory}.
     * 
     * If another factory is already registered and provides the same syntax,
     * the existing factory is replaced by the provided factory.
     *
     * @param factory The factory to register.
     */
    public void registerFactory(final IMappingReaderFactory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("The factory must not be null");
        }
        final IMappingReaderFactory oldFactory = _syntax2Factory.put(factory.getSyntax(), factory);
        if (oldFactory != null) {
            LOG.info("The mapping reader factory " + oldFactory.getClass() + 
                    " was replaced by " + factory.getClass() + 
                    " (syntax: " + oldFactory.getSyntax().getName() + ")");
        }
    }

    /**
     * INTERNAL: Unregisteres the provided {@code factory}.
     * 
     * If the factory is not registered, this method does nothing.
     *
     * @param factory The factory to unregister.
     */
    public void unregisterFactory(final IMappingReaderFactory factory) {
        if (factory != null) {
            _syntax2Factory.remove(factory.getSyntax());
        }
    }

    /**
     * Returns a {@link IMappingReader} instance for the provided syntax.
     *
     * @param syntax The syntax.
     * @return A {@link IMappingReader} instance or {@code null} if no reader
     *          is available for the provided syntax.
     */
    public IMappingReader createReader(final Syntax syntax) {
        final IMappingReaderFactory factory = _syntax2Factory.get(syntax);
        return factory != null ? factory.createReader() : null;
    }

}
