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
package com.semagia.mio.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.semagia.mio.IMapHandler;
import com.semagia.mio.IRef;
import com.semagia.mio.MIOException;

/**
 * {@link IMapHandler} implementation that logs all events with the level "info".
 * 
 * This class is mainly used for debugging purposes.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 515 $ - $Date: 2010-09-11 15:47:22 +0200 (Sa, 11 Sep 2010) $
 */
public final class LoggingMapHandler extends DelegatingMapHandler {

    private static final Logger _LOG = LoggerFactory.getLogger(LoggingMapHandler.class.getName());

    /**
     * Creates an instance of this class.
     *
     * @param mapHandler The map handler which receives the event after it has
     *                      been logged.
     */
    public LoggingMapHandler(final IMapHandler mapHandler) {
        super(mapHandler);
    }

    @Override
    public void endAssociation() throws MIOException {
        _LOG.info("endAssociation");
        super.endAssociation();
    }

    @Override
    public void endIsa() throws MIOException {
        _LOG.info("endIsa");
        super.endIsa();
    }

    @Override
    public void endName() throws MIOException {
        _LOG.info("endName");
        super.endName();
    }

    @Override
    public void endOccurrence() throws MIOException {
        _LOG.info("endOccurrence");
        super.endOccurrence();
    }

    @Override
    public void endPlayer() throws MIOException {
        _LOG.info("endPlayer");
        super.endPlayer();
    }

    @Override
    public void endReifier() throws MIOException {
        _LOG.info("endReifier");
        super.endReifier();
    }

    @Override
    public void endRole() throws MIOException {
        _LOG.info("endRole");
        super.endRole();
    }

    @Override
    public void endScope() throws MIOException {
        _LOG.info("endScope");
        super.endScope();
    }

    @Override
    public void endTheme() throws MIOException {
        _LOG.info("endTheme");
        super.endTheme();
    }

    @Override
    public void endTopic() throws MIOException {
        _LOG.info("endTopic");
        super.endTopic();
    }

    @Override
    public void endTopicMap() throws MIOException {
        _LOG.info("endTopicMap");
        super.endTopicMap();
    }

    @Override
    public void endType() throws MIOException {
        _LOG.info("endType");
        super.endType();
    }

    @Override
    public void endVariant() throws MIOException {
        _LOG.info("endVariant");
        super.endVariant();
    }

    @Override
    public void itemIdentifier(String iid) throws MIOException {
        _LOG.info("itemIdentifier, iid={}", iid);
        super.itemIdentifier(iid);
    }

    @Override
    public void startAssociation() throws MIOException {
        _LOG.info("startAssociation");
        super.startAssociation();
    }

    @Override
    public void startIsa() throws MIOException {
        _LOG.info("startIsa");
        super.startIsa();
    }

    @Override
    public void startName() throws MIOException {
        _LOG.info("startName");
        super.startName();
    }

    @Override
    public void startOccurrence() throws MIOException {
        _LOG.info("startOccurrence");
        super.startOccurrence();
    }

    @Override
    public void startPlayer() throws MIOException {
        _LOG.info("startPlayer");
        super.startPlayer();
    }

    @Override
    public void startReifier() throws MIOException {
        _LOG.info("startReifier");
        super.startReifier();
    }

    @Override
    public void startRole() throws MIOException {
        _LOG.info("startRole");
        super.startRole();
    }

    @Override
    public void startScope() throws MIOException {
        _LOG.info("startScope");
        super.startScope();
    }

    @Override
    public void startTheme() throws MIOException {
        _LOG.info("startTheme");
        super.startTheme();
    }

    @Override
    public void startTopic(IRef identity) throws MIOException {
        _LOG.info("startTopic, identity={}", identity);
        super.startTopic(identity);
    }

    @Override
    public void startTopicMap() throws MIOException {
        _LOG.info("startTopicMap");
        super.startTopicMap();
    }

    @Override
    public void startType() throws MIOException {
        _LOG.info("startType");
        super.startType();
    }

    @Override
    public void startVariant() throws MIOException {
        _LOG.info("startVariant");
        super.startVariant();
    }

    @Override
    public void subjectIdentifier(String sid) throws MIOException {
        _LOG.info("subjectIdentifier, iri={}", sid);
        super.subjectIdentifier(sid);
    }

    @Override
    public void subjectLocator(String slo) throws MIOException {
        _LOG.info("subjectLocator, iri={}", slo);
        super.subjectLocator(slo);
    }

    @Override
    public void topicRef(IRef identity) throws MIOException {
        _LOG.info("topicRef, identity={}", identity);
        super.topicRef(identity);
    }

    @Override
    public void value(String value, String datatype) throws MIOException {
        _LOG.info("value, value='{}', datatype='{}'", value, datatype);
        super.value(value, datatype);
    }

    @Override
    public void value(String value) throws MIOException {
        _LOG.info("value, value='{}'", value);
        super.value(value);
    }

}
