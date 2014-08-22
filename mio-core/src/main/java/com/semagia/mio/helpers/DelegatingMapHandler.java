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

import com.semagia.mio.IMapHandler;
import com.semagia.mio.IRef;
import com.semagia.mio.MIOException;

/**
 * {@link IMapHandler} implementation that does nothing but delegates the
 * events to an underlying {@link IMapHandler}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 607 $ - $Date: 2011-01-20 02:28:15 +0100 (Do, 20 Jan 2011) $
 */
public class DelegatingMapHandler implements IMapHandler {

    private IMapHandler _handler;

    /**
     * Creates a new <tt>DelegatingMapHandler</tt> where all events are 
     * forwarded to the <tt>mapHandler</tt>.
     *
     * @param mapHandler A {@link IMapHandler} implementation, never <tt>null</tt>.
     */
    public DelegatingMapHandler(final IMapHandler mapHandler) {
        if (mapHandler == null) {
            throw new IllegalArgumentException("The MapHandler must not be null");
        }
        _handler = mapHandler;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endAssociation()
     */
    @Override
    public void endAssociation() throws MIOException {
        _handler.endAssociation();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endIsa()
     */
    @Override
    public void endIsa() throws MIOException {
        _handler.endIsa();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endName()
     */
    @Override
    public void endName() throws MIOException {
        _handler.endName();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endOccurrence()
     */
    @Override
    public void endOccurrence() throws MIOException {
        _handler.endOccurrence();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endPlayer()
     */
    @Override
    public void endPlayer() throws MIOException {
        _handler.endPlayer();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endReifier()
     */
    @Override
    public void endReifier() throws MIOException {
        _handler.endReifier();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endRole()
     */
    @Override
    public void endRole() throws MIOException {
        _handler.endRole();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endScope()
     */
    @Override
    public void endScope() throws MIOException {
        _handler.endScope();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endTheme()
     */
    @Override
    public void endTheme() throws MIOException {
        _handler.endTheme();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endTopic()
     */
    @Override
    public void endTopic() throws MIOException {
        _handler.endTopic();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endTopicMap()
     */
    @Override
    public void endTopicMap() throws MIOException {
        _handler.endTopicMap();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endType()
     */
    @Override
    public void endType() throws MIOException {
        _handler.endType();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endVariant()
     */
    @Override
    public void endVariant() throws MIOException {
        _handler.endVariant();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#itemIdentifier(java.lang.String)
     */
    @Override
    public void itemIdentifier(final String iid) throws MIOException {
        _handler.itemIdentifier(iid);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startAssociation()
     */
    @Override
    public void startAssociation() throws MIOException {
        _handler.startAssociation();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startIsa()
     */
    @Override
    public void startIsa() throws MIOException {
        _handler.startIsa();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startName()
     */
    @Override
    public void startName() throws MIOException {
        _handler.startName();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startOccurrence()
     */
    @Override
    public void startOccurrence() throws MIOException {
        _handler.startOccurrence();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startPlayer()
     */
    @Override
    public void startPlayer() throws MIOException {
        _handler.startPlayer();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startReifier()
     */
    @Override
    public void startReifier() throws MIOException {
        _handler.startReifier();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startRole()
     */
    @Override
    public void startRole() throws MIOException {
        _handler.startRole();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startScope()
     */
    @Override
    public void startScope() throws MIOException {
        _handler.startScope();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startTheme()
     */
    @Override
    public void startTheme() throws MIOException {
        _handler.startTheme();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startTopic(com.semagia.mio.IRef)
     */
    @Override
    public void startTopic(final IRef identity) throws MIOException {
        _handler.startTopic(identity);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startTopicMap()
     */
    @Override
    public void startTopicMap() throws MIOException {
        _handler.startTopicMap();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startType()
     */
    @Override
    public void startType() throws MIOException {
        _handler.startType();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startVariant()
     */
    @Override
    public void startVariant() throws MIOException {
        _handler.startVariant();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#subjectIdentifier(java.lang.String)
     */
    @Override
    public void subjectIdentifier(final String sid) throws MIOException {
        _handler.subjectIdentifier(sid);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#subjectLocator(java.lang.String)
     */
    @Override
    public void subjectLocator(final String slo) throws MIOException {
        _handler.subjectLocator(slo);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#topicRef(com.semagia.mio.IRef)
     */
    @Override
    public void topicRef(final IRef identity) throws MIOException {
        _handler.topicRef(identity);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#value(java.lang.String)
     */
    @Override
    public void value(final String value) throws MIOException {
        _handler.value(value);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#value(java.lang.String, java.lang.String)
     */
    @Override
    public void value(final String value, final String datatype) throws MIOException {
        _handler.value(value, datatype);
    }

}
