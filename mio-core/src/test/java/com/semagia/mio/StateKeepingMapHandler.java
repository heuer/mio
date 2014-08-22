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

import java.util.Stack;

/**
 * Map handler that keeps track of states and compares if a particular event is
 * valid within the state. 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 607 $ - $Date: 2011-01-20 02:28:15 +0100 (Do, 20 Jan 2011) $
 */
@SuppressWarnings("boxing")
public class StateKeepingMapHandler implements IMapHandler {

    private static final int
        _STATE_TOPIC_MAP = 1,
        _STATE_TOPIC = 2,
        _STATE_ASSOCIATION = 3,
        _STATE_ROLE = 4,
        _STATE_OCCURRENCE = 5,
        _STATE_NAME = 6,
        _STATE_VARIANT = 7,
        _STATE_ISA = 9,
        _STATE_AKO = 10,
        _STATE_REIFIER = 11,
        _STATE_SCOPE = 12,
        _STATE_THEME = 13,
        _STATE_TYPE = 14,
        _STATE_PLAYER = 15
        ;

    private Stack<Integer> _states;

    private void _leave(int state, String method) throws MIOException {
        _assert(_states.pop(), state, method);
    }

    private void _enter(int state) {
        _states.push(state);
    }

    private static void _assert(int state, int expectedState, String method) throws MIOException {
        if (state != expectedState) {
            throw new MIOException("Expected '" + _name(expectedState) + "', got '" + _name(state) + "' (method: '" + method + "')");
        }
    }

    private void _assertOneOf(int ... possibleStates) throws MIOException {
        boolean found = false;
        int currentState = _states.peek();
        for (int state: possibleStates) {
            found = state == currentState;
            if (found) {
                break;
            }
        }
        if (!found) {
            throw new MIOException(""); //TODO
        }
    }

    private static String _name(int state) {
        switch (state) {
            case _STATE_TOPIC_MAP:      return "topic map";
            case _STATE_TOPIC:          return "topic";
            case _STATE_ASSOCIATION:    return "association";
            case _STATE_ROLE:           return "role";
            case _STATE_OCCURRENCE:     return "occcurrence";
            case _STATE_NAME:           return "name";
            case _STATE_VARIANT:        return "variant";
            case _STATE_ISA:            return "isa";
            case _STATE_AKO:            return "ako";
            case _STATE_REIFIER:        return "reifier";
            case _STATE_SCOPE:          return "scope";
            case _STATE_THEME:          return "theme";
            case _STATE_TYPE:           return "type";
            case _STATE_PLAYER:         return "player";
        }
        throw new RuntimeException("Unknown state " + state);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endAssociation()
     */
    @Override
    public void endAssociation() throws MIOException {
        _leave(_STATE_ASSOCIATION, "endAssociation");
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endIsa()
     */
    @Override
    public void endIsa() throws MIOException {
        _leave(_STATE_ISA, "endIsa");
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endName()
     */
    @Override
    public void endName() throws MIOException {
        _leave(_STATE_NAME, "endName");
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endOccurrence()
     */
    @Override
    public void endOccurrence() throws MIOException {
        _leave(_STATE_OCCURRENCE, "endOccurrence");
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endPlayer()
     */
    @Override
    public void endPlayer() throws MIOException {
        _leave(_STATE_PLAYER, "endPlayer");
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endReifier()
     */
    @Override
    public void endReifier() throws MIOException {
        _leave(_STATE_REIFIER, "endReifier");
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endRole()
     */
    @Override
    public void endRole() throws MIOException {
        _leave(_STATE_ROLE, "endRole");
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endScope()
     */
    @Override
    public void endScope() throws MIOException {
        _leave(_STATE_SCOPE, "endScope");
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endTheme()
     */
    @Override
    public void endTheme() throws MIOException {
        _leave(_STATE_THEME, "endTheme");
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endTopic()
     */
    @Override
    public void endTopic() throws MIOException {
        _leave(_STATE_TOPIC, "endTopic");
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endTopicMap()
     */
    @Override
    public void endTopicMap() throws MIOException {
        _leave(_STATE_TOPIC_MAP, "endTopicMap");
        if (_states.empty()) {
            throw new MIOException("Expected an empty stack");
        }
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endType()
     */
    @Override
    public void endType() throws MIOException {
        _leave(_STATE_TYPE, "endType");
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#endVariant()
     */
    @Override
    public void endVariant() throws MIOException {
        _leave(_STATE_VARIANT, "endVariant");
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#itemIdentifier(java.lang.String)
     */
    @Override
    public void itemIdentifier(String iid) throws MIOException {
        _assertOneOf(_STATE_TOPIC_MAP, _STATE_TOPIC, _STATE_ASSOCIATION, 
                        _STATE_ROLE, _STATE_NAME, _STATE_VARIANT);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startAssociation()
     */
    @Override
    public void startAssociation() throws MIOException {
        _enter(_STATE_ASSOCIATION);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startIsa()
     */
    @Override
    public void startIsa() throws MIOException {
        _assert(_states.peek(), _STATE_TOPIC, "startIsa");
        _enter(_STATE_ISA);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startName()
     */
    @Override
    public void startName() throws MIOException {
        _assert(_states.peek(), _STATE_TOPIC, "startName");
        _enter(_STATE_NAME);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startOccurrence()
     */
    @Override
    public void startOccurrence() throws MIOException {
        _assert(_states.peek(), _STATE_TOPIC, "startOccurrence");
        _enter(_STATE_OCCURRENCE);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startPlayer()
     */
    @Override
    public void startPlayer() throws MIOException {
        _assert(_states.peek(), _STATE_ROLE, "startPlayer");
        _enter(_STATE_PLAYER);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startReifier()
     */
    @Override
    public void startReifier() throws MIOException {
        if (_states.peek() == _STATE_TOPIC) {
            throw new MIOException("Got a startReifier event within a topic");
        }
        _enter(_STATE_REIFIER);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startRole()
     */
    @Override
    public void startRole() throws MIOException {
        _assert(_states.peek(), _STATE_ASSOCIATION, "startRole");
        _enter(_STATE_ROLE);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startScope()
     */
    @Override
    public void startScope() throws MIOException {
        _assertOneOf(_STATE_ASSOCIATION, _STATE_OCCURRENCE, _STATE_NAME, _STATE_VARIANT);
        _enter(_STATE_SCOPE);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startTheme()
     */
    @Override
    public void startTheme() throws MIOException {
        _assert(_states.peek(), _STATE_SCOPE, "startTheme");
        _enter(_STATE_THEME);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startTopic(com.semagia.mio.IRef)
     */
    @Override
    public void startTopic(IRef identity) throws MIOException {
        _enter(_STATE_TOPIC);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startTopicMap()
     */
    @Override
    public void startTopicMap() throws MIOException {
        _enter(_STATE_TOPIC_MAP);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startType()
     */
    @Override
    public void startType() throws MIOException {
        _assertOneOf(_STATE_ASSOCIATION, _STATE_ROLE, _STATE_OCCURRENCE, _STATE_NAME);
        _enter(_STATE_TYPE);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#startVariant()
     */
    @Override
    public void startVariant() throws MIOException {
        _assert(_states.peek(), _STATE_NAME, "startVariant");
        _enter(_STATE_VARIANT);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#subjectIdentifier(java.lang.String)
     */
    @Override
    public void subjectIdentifier(String sid) throws MIOException {
        _assert(_states.peek(), _STATE_TOPIC, "subjectIdentifier");
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#subjectLocator(java.lang.String)
     */
    @Override
    public void subjectLocator(String slo) throws MIOException {
        _assert(_states.peek(), _STATE_TOPIC, "subjectLocator");
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#topicRef(com.semagia.mio.IRef)
     */
    @Override
    public void topicRef(IRef identity) throws MIOException {
        _assertOneOf(_STATE_TYPE, _STATE_PLAYER, _STATE_THEME, _STATE_REIFIER);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#value(java.lang.String)
     */
    @Override
    public void value(String value) throws MIOException {
        _assert(_states.peek(), _STATE_NAME, "value(String)");
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IMapHandler#value(java.lang.String, java.lang.String)
     */
    @Override
    public void value(String value, String datatype) throws MIOException {
        _assertOneOf(_STATE_OCCURRENCE, _STATE_VARIANT);
    }

}
