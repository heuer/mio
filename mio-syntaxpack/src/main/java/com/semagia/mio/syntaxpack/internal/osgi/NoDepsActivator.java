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
package com.semagia.mio.syntaxpack.internal.osgi;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import com.semagia.mio.DeserializerRegistry;
import com.semagia.mio.IDeserializerFactory;

/**
 * Registeres the {@link IDeserializerFactory} instances.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 605 $ - $Date: 2011-01-19 00:10:28 +0100 (Mi, 19 Jan 2011) $
 */
public class NoDepsActivator extends Activator implements ServiceListener {

    private static final String _DSF_FILTER = "(objectclass=" + IDeserializerFactory.class.getName() + ")";

    /* (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        final ServiceReference references[] = context.getServiceReferences(null, _DSF_FILTER);
        for (int i = 0; references != null && i < references.length; i++) {
            registerFactory(references[i]);
        }
        context.addServiceListener(this, _DSF_FILTER);
    }

    /* (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
        final ServiceReference references[] = context.getServiceReferences(null, _DSF_FILTER);
        for (int i = 0; references != null && i < references.length; i++) {
            unregisterFactory(references[i]);
        }
    }

    /* (non-Javadoc)
     * @see org.osgi.framework.ServiceListener#serviceChanged(org.osgi.framework.ServiceEvent)
     */
    @Override
    public void serviceChanged(final ServiceEvent evt) {
        switch (evt.getType()) {
            case ServiceEvent.REGISTERED:
                registerFactory(evt.getServiceReference());
                break;
            case ServiceEvent.UNREGISTERING:
                unregisterFactory(evt.getServiceReference());
                break;
        }
    }

    /**
     * Registeres a {@link IDeserializerFactory} in the {@link DeserializerRegistry}.
     *
     * @param reference A service reference which can be used to fetch a
     *                  {@link IDeserializerFactory} instance from the bundle context.
     */
    private void registerFactory(final ServiceReference reference) {
        DeserializerRegistry.getInstance().registerFactory((IDeserializerFactory) _bundleContext.getService(reference));
    }

    /**
     * Removes a {@link IDeserializerFactory} from the {@link DeserializerRegistry}.
     *
     * @param reference A service reference which can be used to fetch a
     *                  {@link IDeserializerFactory} instance from the bundle context.
     */
    private void unregisterFactory(final ServiceReference reference) {
        DeserializerRegistry.getInstance().unregisterFactory((IDeserializerFactory) _bundleContext.getService(reference));
    }

}
 