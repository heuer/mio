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
package com.semagia.mio.tabella.internal.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.semagia.mio.IDeserializerFactory;
import com.semagia.mio.tabella.CSVDeserializerFactory;

/**
 * Registeres the CSV {@link IDeserializerFactory} instance.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 605 $ - $Date: 2011-01-19 00:10:28 +0100 (Mi, 19 Jan 2011) $
 */
public class Activator implements BundleActivator {

    /* (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception {
        context.registerService(IDeserializerFactory.class.getName(), new CSVDeserializerFactory(), null);
    }

    /* (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        // noop.
    }

}
