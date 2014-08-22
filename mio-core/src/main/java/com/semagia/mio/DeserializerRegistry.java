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

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default registry for {@link IDeserializerFactory} instances. 
 * 
 * Use {@link #getInstance()} to get an instance of this class. This
 * class it thread-safe.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 607 $ - $Date: 2011-01-20 02:28:15 +0100 (Do, 20 Jan 2011) $
 */
public final class DeserializerRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(DeserializerRegistry.class);

    private static final DeserializerRegistry _INSTANCE = new DeserializerRegistry();

    private final Map<Syntax, IDeserializerFactory> _syntax2Factory;

    private DeserializerRegistry() {
        _syntax2Factory = new ConcurrentHashMap<Syntax, IDeserializerFactory>();
        // Try to find IDeserializerFactory providers
        // Won't find anything in an OSGi environment but in an OSGi environment
        // we use the register/unregister methods
        // See com.semagia.mio.internal.osgi.Activator
        for (IDeserializerFactory factory: ServiceLoader.load(IDeserializerFactory.class, DeserializerRegistry.class.getClassLoader())) {
            registerFactory(factory);
        }
    }

    /**
     * Returns the instance.
     *
     * @return The registry instance.
     */
    public static DeserializerRegistry getInstance() {
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
    public void registerFactory(final IDeserializerFactory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("The factory must not be null");
        }
        final IDeserializerFactory oldFactory = _syntax2Factory.put(factory.getSyntax(), factory);
        if (oldFactory != null) {
            LOG.info("The deserializer factory " + oldFactory.getClass() + 
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
    public void unregisterFactory(final IDeserializerFactory factory) {
        if (factory != null) {
            _syntax2Factory.remove(factory.getSyntax());
        }
    }

    /**
     * Returns a {@link IDeserializer} instance for the provided syntax.
     *
     * @param syntax The syntax.
     * @return A {@link IDeserializer} instance or {@code null} if no deserializer
     *          is available for the provided syntax.
     */
    public IDeserializer createDeserializer(final Syntax syntax) {
        final IDeserializerFactory factory = _syntax2Factory.get(syntax);
        return factory != null ? factory.createDeserializer()
                               : null;
    }

}
