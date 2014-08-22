/*
 * Copyright 2007 - 2009 Lars Heuer (heuer[at]semagia.com)
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
package com.semagia.mio.stm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.semagia.mio.IRef;
import com.semagia.mio.MIOException;
import com.semagia.mio.helpers.SimpleMapHandler;
import com.semagia.mio.helpers.Literal;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 469 $ - $Date: 2010-09-08 12:39:06 +0200 (Mi, 08 Sep 2010) $
 */
final class Context implements IContext {

    private final SnelloRuntime _runtime;
    private final Context _parent;
    private Map<String, SnelloRef> _variables;
    private List<SnelloRef> _focus;

    public Context() {
        this(null, null);
    }

    public Context(SnelloRuntime runtime) {
        this(runtime, null);
    }

    public Context(SnelloRuntime runtime, Context parent) {
        _runtime = runtime;
        _variables = new HashMap<String, SnelloRef>();
        _parent = parent;
        _focus = new ArrayList<SnelloRef>();
        if (parent != null) {
            _focus.addAll(parent._focus);
        }
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.dm.IContext#getMapHandler()
     */
    @Override
    public SimpleMapHandler getMapHandler() {
        return getRuntime().getMapHandler();
    }

    public void bind(String name, SnelloRef value) {
        _variables.put(name, value);
    }

    public void unbind(String name) {
        _variables.remove(name);
    }

    /**
     * 
     *
     * @param name
     * @return
     * @throws MIOException
     */
    public SnelloRef getRef(String name) throws MIOException {
        return _getRef(name, null);
    }

    /**
     * 
     *
     * @param name
     * @param defaultValue
     * @return
     * @throws MIOException
     */
    private final SnelloRef _getRef(String name, SnelloRef defaultValue) throws MIOException {
        if (IContext.TOPIC_IN_FOCUS.getString().equals(name)) {
            return (SnelloRef) getTopicReference(IContext.TOPIC_IN_FOCUS);
        }
        SnelloRef ref = _variables.get(name);
        if (ref == null && _parent != null) {
            ref = _parent._getRef(name, defaultValue);
        }
        if (ref == null) {
            _reportError("The variable $" + name + " is undefined");
        }
        return ref;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.dm.ITemplateLib#getTemplate(java.lang.String)
     */
    @Override
    public ITemplate getTemplate(final String name, final int arity) throws MIOException {
        return getRuntime().getTemplate(name, arity);
    }

    @Override
    public Iterable<String> getTemplateNames() {
        return getRuntime().getTemplateNames();
    }

    @Override
    public void registerTemplate(String name, ITemplate template)
            throws MIOException {
        getRuntime().registerTemplate(name, template);
    }

    private static void _reportError(String msg) throws MIOException {
        throw new MIOException(msg);
    }

    public SnelloRuntime getRuntime() {
        if (_parent != null) {
            return _parent.getRuntime();
        }
        return _runtime;
    }

    Context parent() {
        return _parent;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.dm.IContext#getTopic(com.semagia.mio.stm.dm.Ref)
     */
    @Override
    public IRef getTopicReference(SnelloRef ref) throws MIOException {
        if (ref == null) {
            return null;
        }
        if (IContext.TOPIC_IN_FOCUS == ref) {
            return _focus.get(_focus.size()-1);
        }
        if(ref.isVariable()) {
            ref = getRef(ref.getString());
        }
        if (ref.isSID() || ref.isSLO() || ref.isIID()) {
            return ref;
        }
        if (ref.isVSLO()) {
            return SnelloRef.createSLO(getRef(ref.getString()).getString());
        }
        if (ref.isVIID()) {
            return SnelloRef.createIID(getRef(ref.getString()).getString());
        }
        if (ref.isInternalWildcard() || ref.isNamedWildcard()) {
            return newTopicReference(ref);
        }
        throw new RuntimeException("Unexpected ref: " + ref.getType());
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.dm.IContext#getTopic(java.lang.String)
     */
    @Override
    public IRef getTopicReference(String name) throws MIOException {
        return getTopicReference(getRef(name));
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.dm.IContext#getLocator(java.lang.String)
     */
    @Override
    public String getLocator(String name) throws MIOException {
        return getRef(name).getIRI();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.dm.IContext#getLiteral(com.semagia.mio.stm.dm.Ref)
     */
    @Override
    public Literal getLiteral(SnelloRef ref) throws MIOException {
        while(ref.isVariable()) {
            ref = getRef(ref.getString());
        }
        if (ref.isIRI()) {
            return Literal.createIRI(ref.getString());
        }
        return ref.getLiteral();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.dm.IContext#getLiteral(java.lang.String)
     */
    @Override
    public Literal getLiteral(String name) throws MIOException {
        return getLiteral(getRef(name));
    }

    public SnelloRef newTopicReference() {
        return getRuntime().nextTopicId();
    }

    private SnelloRef newTopicReference(SnelloRef r) {
        String name = r.getString();
        SnelloRef ref = _variables.get(name);
        if (ref == null) {
            ref = r.isInternalWildcard() ? getRuntime().nextTopicId() : getRuntime().nextTopicId(name);
            _variables.put(name, ref);
        }
        return ref;
    }

    public void pushFocus(SnelloRef focus) {
        _focus.add(focus);
    }

    public void popFocus() {
        _focus.remove(_focus.size()-1);
    }

}
