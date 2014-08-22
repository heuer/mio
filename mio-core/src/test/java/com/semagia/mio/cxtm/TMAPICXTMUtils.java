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

import java.io.IOException;
import java.io.OutputStream;

import com.semagia.mio.IMapHandler;

import org.tmapi.core.TopicMap;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 607 $ - $Date: 2011-01-20 02:28:15 +0100 (Do, 20 Jan 2011) $
 */
final class TMAPICXTMUtils {

    public static ITMAPICXTMWriter createCXTMWriter(final TopicMap tm, final OutputStream out, final String base) throws IOException {
        return isTinyTim(tm) ? new TinyTimCXTMWriter(out, base) : new OntopiaCXTMWriter(out);
    }

    public static IMapHandler createMapHandler(final TopicMap tm) {
        return isTinyTim(tm) ? makeTinyTimMapInputHandler(tm) : makeOntopiaMapInputHandler(tm);
    }

    private static boolean isTinyTim(final TopicMap topicMap) {
        return topicMap.getClass().getName().startsWith("org.tinytim");
    }

    public static void convertToTMDM(final TopicMap tm) {
        if (isTinyTim(tm)) {
            org.tinytim.utils.XTM10Utils.convertToTMDM(tm);
        }
    }

    private static IMapHandler makeTinyTimMapInputHandler(final TopicMap topicMap) {
        return new org.tinytim.mio.TinyTimMapInputHandler(topicMap);
    }

    private static IMapHandler makeOntopiaMapInputHandler(final TopicMap topicMap) {
        return new net.ontopia.topicmaps.io.OntopiaMapHandler(unwrapOntopia(topicMap));
    }

    static net.ontopia.topicmaps.core.TopicMapIF unwrapOntopia(TopicMap topicMap) {
        return ((net.ontopia.topicmaps.impl.tmapi2.TopicMapImpl) topicMap).getWrapped();
    }


    private static class TinyTimCXTMWriter implements ITMAPICXTMWriter {

        private final org.tinytim.mio.CXTMTopicMapWriter _writer;

        public TinyTimCXTMWriter(OutputStream out, String base) throws IOException {
            _writer = new org.tinytim.mio.CXTMTopicMapWriter(out, base);
        }

        @Override
        public void write(TopicMap topicMap) throws IOException {
            _writer.write(topicMap); 
        }
    }


    private static class OntopiaCXTMWriter implements ITMAPICXTMWriter {
        
        private final net.ontopia.topicmaps.xml.CanonicalXTMWriter _writer;

        public OntopiaCXTMWriter(OutputStream out) throws IOException {
            _writer = new net.ontopia.topicmaps.xml.CanonicalXTMWriter(out);
        }

        @Override
        public void write(TopicMap topicMap) throws IOException {
            _writer.write(unwrapOntopia(topicMap));
        }

    }

}
