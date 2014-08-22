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
package com.semagia.mio.xtm.internal.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.semagia.mio.IDeserializerFactory;
import com.semagia.mio.xtm.XTM10DeserializerFactory;
import com.semagia.mio.xtm.XTM20DeserializerFactory;
import com.semagia.mio.xtm.XTM21DeserializerFactory;
import com.semagia.mio.xtm.XTMDeserializerFactory;

/**
 * Registeres the XTM {@link IDeserializerFactory} instances.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 462 $ - $Date: 2010-09-08 01:20:13 +0200 (Mi, 08 Sep 2010) $
 */
@SuppressWarnings("unchecked")
public class Activator implements BundleActivator {

    private static final Class[] _FACTORIES = new Class[] {
        XTM10DeserializerFactory.class, XTM20DeserializerFactory.class,
        XTM21DeserializerFactory.class, XTMDeserializerFactory.class
    };

    /* (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception {
        for (Class<IDeserializerFactory> cls: _FACTORIES) {
            context.registerService(IDeserializerFactory.class.getName(), cls.newInstance(), null);
        }
    }

    /* (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        // noop.
    }

}
