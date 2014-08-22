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

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import com.semagia.mio.IDeserializerFactory;
import com.semagia.mio.ctm.CTMDeserializerFactory;
import com.semagia.mio.jtm.JTMDeserializerFactory;
import com.semagia.mio.ltm.LTMDeserializerFactory;
import com.semagia.mio.rdf.MappingReaderRegistry;
import com.semagia.mio.rdf.api.IMappingReaderFactory;
import com.semagia.mio.rdf.crtm.CRTMMappingReaderFactory;
import com.semagia.mio.rdf.sesame.N3DeserializerFactory;
import com.semagia.mio.rdf.sesame.NTriplesDeserializerFactory;
import com.semagia.mio.rdf.sesame.RDFXMLDeserializerFactory;
import com.semagia.mio.rdf.sesame.RDFaDeserializerFactory;
import com.semagia.mio.rdf.sesame.TrigDeserializerFactory;
import com.semagia.mio.rdf.sesame.TrixDeserializerFactory;
import com.semagia.mio.rdf.sesame.TurtleDeserializerFactory;
import com.semagia.mio.stm.SnelloDeserializerFactory;
import com.semagia.mio.tmxml.TMXMLDeserializerFactory;
import com.semagia.mio.xtm.XTMDeserializerFactory;
import com.semagia.mio.xtm.XTM10DeserializerFactory;
import com.semagia.mio.xtm.XTM20DeserializerFactory;
import com.semagia.mio.xtm.XTM21DeserializerFactory;

/**
 * Registeres the {@link IDeserializerFactory} instances.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 605 $ - $Date: 2011-01-19 00:10:28 +0100 (Mi, 19 Jan 2011) $
 */
public class Activator implements BundleActivator {

    private static final String _RDF_FILTER = "(objectclass=" + IMappingReaderFactory.class.getName() + ")";
    private RDFMappingFactoryServiceListener _listener;
    protected BundleContext _bundleContext;

    @SuppressWarnings("unchecked")
    private static final Class<IDeserializerFactory>[] _RDF_FACTORIES = new Class[] {
        N3DeserializerFactory.class, NTriplesDeserializerFactory.class,
        RDFaDeserializerFactory.class, RDFXMLDeserializerFactory.class, 
        TrigDeserializerFactory.class, TrixDeserializerFactory.class, 
        TurtleDeserializerFactory.class
    };

    @SuppressWarnings("unchecked")
    private static final Class<IDeserializerFactory>[] _FACTORIES = new Class[] {
        CTMDeserializerFactory.class,
        JTMDeserializerFactory.class, LTMDeserializerFactory.class,
        SnelloDeserializerFactory.class, TMXMLDeserializerFactory.class,
        XTMDeserializerFactory.class, XTM10DeserializerFactory.class,
        XTM20DeserializerFactory.class, XTM21DeserializerFactory.class
    };

    /* (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception {
        _bundleContext = context;
        _listener = new RDFMappingFactoryServiceListener();
        final ServiceReference references[] = context.getServiceReferences(null, _RDF_FILTER);
        for (int i = 0; references != null && i < references.length; i++) {
            _listener.registerMappingReaderFactory(references[i]);
        }
        context.addServiceListener(_listener, _RDF_FILTER);
        context.registerService(IMappingReaderFactory.class.getName(), new CRTMMappingReaderFactory(), null);
        for (Class<IDeserializerFactory> cls: _RDF_FACTORIES) {
            Object obj = cls.newInstance();
            context.registerService(IDeserializerFactory.class.getName(), obj, null);
            context.registerService(IMappingReaderFactory.class.getName(), obj, null);
        }
        for (Class<IDeserializerFactory> cls: _FACTORIES) {
            context.registerService(IDeserializerFactory.class.getName(), cls.newInstance(), null);
        }
    }

    /* (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        final ServiceReference references[] = context.getServiceReferences(null, _RDF_FILTER);
        for (int i = 0; references != null && i < references.length; i++) {
            _listener.unregisterMappingReaderFactory(references[i]);
        }
        _listener = null;
    }

    private class RDFMappingFactoryServiceListener implements ServiceListener {

        /* (non-Javadoc)
         * @see org.osgi.framework.ServiceListener#serviceChanged(org.osgi.framework.ServiceEvent)
         */
        @Override
        public void serviceChanged(ServiceEvent evt) {
            switch (evt.getType()) {
                case ServiceEvent.REGISTERED:
                    registerMappingReaderFactory(evt.getServiceReference());
                    break;
                case ServiceEvent.UNREGISTERING:
                    unregisterMappingReaderFactory(evt.getServiceReference());
                    break;
            }
        }
    
        private void registerMappingReaderFactory(final ServiceReference reference) {
            MappingReaderRegistry.getInstance().registerFactory((IMappingReaderFactory) _bundleContext.getService(reference));
        }
    
        private void unregisterMappingReaderFactory(final ServiceReference reference) {
            MappingReaderRegistry.getInstance().unregisterFactory((IMappingReaderFactory) _bundleContext.getService(reference));
        }

  }
}
