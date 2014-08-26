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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.semagia.mio.IMapHandler;
import com.semagia.mio.IRef;
import com.semagia.mio.MIOException;

/**
 * {@link IMapHandler} implementation that generates events for a
 * {@link HamsterHandler}.
 * <p>
 * This implementation ensures that the {@link HamsterHandler} never gets
 * illegal <tt>null</tt> values.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 607 $ - $Date: 2011-01-20 02:28:15 +0100 (Do, 20 Jan 2011) $
 */
public abstract class AbstractHamsterMapHandler<T> extends HamsterHandler<T> implements IMapHandler {

    protected static final byte 
        INITIAL = 1, 
        TOPIC = 2, 
        ASSOCIATION = 3,
        ROLE = 4, 
        OCCURRENCE = 5, 
        NAME = 6, 
        VARIANT = 7, 
        SCOPE = 8,
        THEME = 9, 
        REIFIER = 10, 
        PLAYER = 11, 
        ISA = 12, 
        TYPE = 13;

    private static final int _CONSTRUCT_SIZE = 6;
    private static final int _STATE_SIZE = 10;
    private static final int _SCOPE_SIZE = 2;

    private byte[] _stateStack;
    private int _stateSize;
    private int _constructSize;
    private Object[] _constructStack;
    private List<List<T>> _scope;

    protected AbstractHamsterMapHandler() {
      // noop.
    }

    public void notifyMerge(final T source, final T target) {
        for (int i=0; i <_constructSize; i++) {
            if (_constructStack[i].equals(source)) {
                _constructStack[i] = target;
            }
        }
    }

    @Override
    public void startTopicMap() throws MIOException {
        _constructStack = new Object[_CONSTRUCT_SIZE];
        _stateStack = new byte[_STATE_SIZE];
        _constructSize = 0;
        _stateSize = 0;
        _scope = new ArrayList<List<T>>(_SCOPE_SIZE);
        _enterState(INITIAL);
    }

    @Override
    public void endTopicMap() throws MIOException {
        if (!_scope.isEmpty()) {
            throw new MIOException("ERROR: The internal scope is not empty");
        }
    }

    @Override
    public void startTopic(final IRef identity) throws MIOException {
        _enterState(TOPIC, createTopic(identity));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void endTopic() throws MIOException {
        T topic = (T) _leaveStatePopConstruct(TOPIC);
        _handleTopic(topic);
    }

    @Override
    public void itemIdentifier(final String iid) throws MIOException {
        if (iid == null) {
            throw new MIOException("The item identifier must not be null");
        }
        final byte state = _state();
        if (state == TOPIC) {
            handleItemIdentifier(peekTopic(), iid);
        }
        else if (state == INITIAL) {
            handleTopicMapItemIdentifier(iid);
        }
        else {
            peekConstruct().iids.add(iid);
        }
    }

    @Override
    public void subjectIdentifier(final String iri) throws MIOException {
        if (iri == null) {
            throw new MIOException("The subject identifier must not be null");
        }
        handleSubjectIdentifier(peekTopic(), iri);
    }

    @Override
    public void subjectLocator(final String iri) throws MIOException {
        if (iri == null) {
            throw new MIOException("The subject locator must not be null");
        }
        handleSubjectLocator(peekTopic(), iri);
    }

    @Override
    public void startIsa() throws MIOException {
        assert _state() == TOPIC;
        _enterState(ISA);
    }

    @Override
    public void endIsa() throws MIOException {
        _leaveState(ISA);
        assert _state() == TOPIC;
    }

    @Override
    public void startAssociation() throws MIOException {
        _enterState(ASSOCIATION, new Association());
    }

    @Override
    public void endAssociation() throws MIOException {
        @SuppressWarnings("unchecked")
        Association assoc = (Association) _leaveStatePopConstruct(ASSOCIATION);
        if (assoc.type == null) {
            throw new MIOException("The association's type must not be null");
        }
        if (assoc.roles.isEmpty()) {
            throw new MIOException("The association has no roles");
        }
        createAssociation(assoc.type, 
                            assoc.scope, 
                            assoc.reifier, 
                            assoc.iids, 
                            assoc.roles);
    }

    @Override
    public void startName() throws MIOException {
        _enterState(NAME, new Name());
    }

    @Override
    public void endName() throws MIOException {
        @SuppressWarnings("unchecked")
        Name name = (Name) _leaveStatePopConstruct(NAME);
        if (name.value == null) {
            throw new MIOException("The name's value must not be null");
        }
        createName(peekTopic(), 
                    name.type,
                    name.value,
                    name.scope,
                    name.reifier,
                    name.iids,
                    name.variants);
    }

    @Override
    public void startOccurrence() throws MIOException {
        _enterState(OCCURRENCE, new Occurrence());
    }

    @Override
    public void endOccurrence() throws MIOException {
        @SuppressWarnings("unchecked")
        Occurrence occ = (Occurrence) _leaveStatePopConstruct(OCCURRENCE);
        if (occ.type == null) {
            throw new MIOException("The type of the occurrence must not be null");
        }
        if (occ.value == null) {
            throw new MIOException("The value of the occurrence must not be null");
        }
        if (occ.datatype == null) {
            throw new MIOException("The datatype of the occurrence must not be null");
        }
        createOccurrence(peekTopic(), 
                            occ.type, 
                            occ.value, 
                            occ.datatype,
                            occ.scope, 
                            occ.reifier, 
                            occ.iids);
    }

    @Override
    public void startRole() throws MIOException {
        assert _state() == ASSOCIATION;
        _enterState(ROLE, new Role());
    }

    @Override
    public void endRole() throws MIOException {
        @SuppressWarnings("unchecked")
        Role role = (Role) _leaveStatePopConstruct(ROLE);
        if (role.type == null) {
            throw new MIOException("The type of the role must not be null");
        }
        if (role.player == null) {
            throw new MIOException("The player of the role must not be null");
        }
        assert _state() == ASSOCIATION;
        peekAssociation().addRole(role);
    }

    @Override
    public void startVariant() throws MIOException {
        assert _state() == NAME;
        _enterState(VARIANT, new Variant());
    }

    @Override
    public void endVariant() throws MIOException {
        @SuppressWarnings("unchecked")
        Variant variant = (Variant) _leaveStatePopConstruct(VARIANT);
        assert _state() == NAME;
        if (variant.value == null) {
            throw new MIOException("The variant's value must not be null");
        }
        if (variant.datatype == null) {
            throw new MIOException("The variant's datatype must not be null");
        }
        if (variant.scope == null) {
            throw new MIOException("The variant's scope must not be unconstrained");
        }
        peekName().addVariant(variant);
    }

    @Override
    public void startPlayer() throws MIOException {
        assert _state() == ROLE;
        _enterState(PLAYER);
    }

    @Override
    public void endPlayer() throws MIOException {
        _leaveState(PLAYER);
        assert _state() == ROLE;
    }

    @Override
    public void startReifier() throws MIOException {
        _enterState(REIFIER);
    }

    @Override
    public void endReifier() throws MIOException {
        _leaveState(REIFIER);
    }

    @Override
    public void startScope() throws MIOException {
        _enterState(SCOPE);
        _scope.add(new ArrayList<T>());
    }

    @Override
    public void endScope() throws MIOException {
        _leaveState(SCOPE);
        peekScoped().setScope(_scope.remove(_scope.size()-1));
    }

    @Override
    public void startTheme() throws MIOException {
        assert _state() == SCOPE;
        _enterState(THEME);
    }

    @Override
    public void endTheme() throws MIOException {
        _leaveState(THEME);
        assert _state() == SCOPE;
    }

    @Override
    public void startType() throws MIOException {
        _enterState(TYPE);
    }

    @Override
    public void endType() throws MIOException {
        _leaveState(TYPE);
    }

    @Override
    public void topicRef(IRef identity) throws MIOException {
        _handleTopic(createTopic(identity));
    }

    @Override
    public void value(final String value, final String datatype) throws MIOException {
        // No need to check to value == null / datatype == null, it's done in 
        // endOccurrence / endVariant
        if (_state() == OCCURRENCE) {
            Occurrence occ = peekOccurrence();
            occ.value = value;
            occ.datatype = datatype;
        }
        else if (_state() == VARIANT) {
            Variant variant = peekVariant();
            variant.value = value;
            variant.datatype = datatype;
        }
        else {
            throw new MIOException("Illegal state, expected either occurrence or variant");
        }
    }

    @Override
    public void value(final String value) throws MIOException {
        // No need to check to value == null it's done in endName
        peekName().value = value;
    }

    private void _enterState(final byte state, final TMConstruct tmc) {
        __enterState(state, tmc);
    }

    private void _enterState(final byte state, final T topic) {
        __enterState(state, topic);
    }

    private void __enterState(final byte state, final Object tmo) {
        _enterState(state);
        if (_constructSize >= _constructStack.length) {
            Object[] constructs = new Object[_constructStack.length * 2];
            System.arraycopy(_constructStack, 0, constructs, 0, _constructStack.length);
            _constructStack = constructs;
        }
        _constructStack[_constructSize++] = tmo;
    }

    /**
     * Enters a state.
     * 
     * @param state
     *            The state to push ontop of the state stack.
     */
    private void _enterState(final byte state) {
        if (_stateSize >= _stateStack.length) {
            byte[] states = new byte[_stateStack.length * 2];
            System.arraycopy(_stateStack, 0, states, 0, _stateStack.length);
            _stateStack = states;
        }
        _stateStack[_stateSize++] = state;
    }

    /**
     * Leaves a state.
     * 
     * @param state
     *            The state to leave.
     * @throws MIOException
     *             If the state is not equals to the current state.
     */
    private void _leaveState(final byte state) throws MIOException {
        if (state != _state()) {
            _reportError("Unexpected state: " + _state() + ", expected: " + state);
        }
        _stateSize--;
    }

    private Object _leaveStatePopConstruct(final byte state) throws MIOException {
        _leaveState(state);
        final Object construct = _peek();
        _constructSize--;
        _constructStack[_constructSize] = null;
        return construct;
    }

    /**
     * Returns the current state.
     * 
     * @return The current state.
     */
    private byte _state() {
        return _stateStack[_stateSize - 1];
    }

    private Object _peek() {
        return _constructStack[_constructSize - 1];
    }

    /**
     * Returns the topic on top of the stack.
     * 
     * @return The topic.
     */
    @SuppressWarnings("unchecked")
    private T peekTopic() {
        return (T) _peek();
    }
    

    /**
     * Reports an error.
     * 
     * @param msg
     *            The error message.
     * @throws MIOException
     *             Thrown in any case.
     */
    private static void _reportError(final String msg) throws MIOException {
        throw new MIOException(msg);
    }

    /**
     * Returns either an existing topic with the specified identity or creates a
     * topic with the given identity.
     * 
     * @param ref
     *            The identity of the topic.
     * @return A topic instance.
     * @throws MIOException
     */
    private T createTopic(final IRef ref) throws MIOException {
        if (ref == null) {
            throw new MIOException("The topic's identity must not be null");
        }
        switch (ref.getType()) {
            case IRef.ITEM_IDENTIFIER:
                return createTopicByItemIdentifier(ref.getIRI());
            case IRef.SUBJECT_IDENTIFIER:
                return createTopicBySubjectIdentifier(ref.getIRI());
            case IRef.SUBJECT_LOCATOR:
                return createTopicBySubjectLocator(ref.getIRI());
            default:
                _reportError("Unknown reference type " + ref.getType());
        }
        // Never returned, an exception was thrown
        return null;
    }

    /**
     * Handles the topic dependent on the current state.
     *
     * @param topic The topic to handle.
     * @throws MIOException 
     */
    private void _handleTopic(final T topic) throws MIOException {
        switch (_state()) {
            case ISA: handleTypeInstance(peekTopic(), topic); break;
            case TYPE: peekTyped().setType(topic); break;
            case PLAYER: peekRole().player = topic; break;
            case THEME: _scope.get(_scope.size()-1).add(topic); break;
            case REIFIER: 
                if (_stateStack[_stateSize - 2] == INITIAL) { 
                    handleTopicMapReifier(topic);
                }
                else { 
                    peekConstruct().reifier = topic; 
                } 
                break;
        }
    }

    @SuppressWarnings("unchecked")
    private IScoped<T> peekScoped() {
        return (IScoped<T>) _peek();
    }

    @SuppressWarnings("unchecked")
    private ITyped<T> peekTyped() {
        return (ITyped<T>) _peek();
    }

    @SuppressWarnings("unchecked")
    private TMConstruct peekConstruct() {
        return (TMConstruct) _peek();
    }

    @SuppressWarnings("unchecked")
    private Association peekAssociation() {
        return (Association) _peek();
    }

    @SuppressWarnings("unchecked")
    private Role peekRole() {
        return (Role) _peek();
    }

    @SuppressWarnings("unchecked")
    private Occurrence peekOccurrence() {
        return (Occurrence) _peek();
    }

    @SuppressWarnings("unchecked")
    private Name peekName() {
        return (Name) _peek();
    }

    @SuppressWarnings("unchecked")
    private Variant peekVariant() {
        return (Variant) _peek();
    }


    private interface IScoped<T> {
        void setScope(List<T> scope);
    }

    private interface ITyped<T> {
        void setType(T type);
    }

    private class TMConstruct {
        Set<String> iids = new HashSet<String>();
        T reifier = null;
    }

    private class Scoped extends TMConstruct implements IScoped<T>{
        Set<T> scope = null;
        @Override
        public void setScope(List<T> scope) {
            this.scope = new HashSet<T>(scope);
        }
    }

    private class Association extends Scoped implements ITyped<T> {
        
        List<IRole<T>> roles = new ArrayList<IRole<T>>();
        T type;
        
        public void setType(T type) {
            this.type = type;
        }
        public void addRole(IRole<T> role) {
            roles.add(role);
        }
    }

    private class Role extends TMConstruct implements ITyped<T>, IRole<T> {
        T type;
        T player;

        public void setType(T type) {
            this.type = type;
        }

        @Override
        public Set<String> getItemIdentifiers() {
            return iids;
        }

        @Override
        public T getPlayer() {
            return player;
        }

        @Override
        public T getReifier() {
            return reifier;
        }

        @Override
        public T getType() {
            return type;
        }
    }

    private class Name extends Scoped implements ITyped<T> {
        T type;
        public String value;
        List<IVariant<T>> variants = new ArrayList<IVariant<T>>();
        
        public void setType(T type) {
            this.type = type;
        }
        
        public void addVariant(IVariant<T> variant) {
            variants.add(variant);
        }
    }

    private class Occurrence extends Scoped implements ITyped<T> {
        T type;
        public String datatype;
        public String value;
        public void setType(T type) {
            this.type = type;
        }
    }

    private class Variant extends Scoped implements IVariant<T> {

        public String datatype;
        public String value;
        @Override
        public String getDatatype() {
            return datatype;
        }
        @Override
        public Set<String> getItemIdentifiers() {
            return iids;
        }
        @Override
        public T getReifier() {
            return reifier;
        }
        @Override
        public Set<T> getScope() {
            return scope;
        }
        @Override
        public String getValue() {
            return value;
        }
        
    }

}
