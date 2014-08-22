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
package com.semagia.mio.rdf.mapping;

import java.util.Collection;

import com.semagia.mio.IMapHandler;
import com.semagia.mio.IRef;
import com.semagia.mio.MIOException;
import com.semagia.mio.helpers.Ref;

/**
 * Abstract mapper for statements which have a type and a scope (association, occurrence,
 * name).
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 546 $ - $Date: 2010-09-25 11:21:45 +0200 (Sa, 25 Sep 2010) $
 */
abstract class AbstractScopeTypeAwareMapper extends AbstractMapper {

    private final IRef[] _scope;
    private final IRef _type;

    /**
     * Creates an instance.
     * 
     * @param name The name of the mapping.
     * @param scope The (maybe empty) scope of the statement or {@code null}.
     * @param type The type of the resulting statement or {@code null}.
     */
    protected AbstractScopeTypeAwareMapper(final String name, 
            final Collection<IRef> scope, final IRef type) {
        super(name);
        _scope = scope == null || scope.isEmpty() ? null 
                                                  : scope.toArray(new IRef[scope.size()]);
        _type = type;
    }

    /**
     * Issues a {@link IMapHandler#startType()}, {@link IMapHandler#topicRef()},
     * {@link IMapHandler#endType()} event sequence.
     * 
     * This method uses either a subject identifier reference from the provided
     * {@code pred} or the overridden type (in case of a
     * <tt>rdf-predicate rtm:type subject-identifier</tt> statment).
     * 
     * @param handler
     *            The handler which receives the events.
     * @param pred
     *            The predicate IRI.
     * @throws MIOException
     *             In case of an error.
     */
    protected final void type(final IMapHandler handler, final String pred)
            throws MIOException {
        _type(handler, _type != null ? _type : Ref.createSubjectIdentifier(pred));
    }

    /**
     * Issues a {@link IMapHandler#startType()}, {@link IMapHandler#topicRef()},
     * {@link IMapHandler#endType()} event sequence.
     * 
     * Note: It's an error if the {@code type} provided in the constructor is
     * {@code null} but this method does not check it (a MIOException should
     * be thrown by the {@code handler}.
     *
     * @param handler
     *            The handler which receives the events.
     * @throws MIOException
     *             In case of an error.
     */
    protected final void type(final IMapHandler handler)
            throws MIOException {
        _type(handler, _type);
    }

    /**
     * Issues a {@link IMapHandler#startType()}, {@link IMapHandler#topicRef()},
     * {@link IMapHandler#endType()} event sequence.
     * 
     * Note: It's an error if the {@code type} provided in the constructor is
     * {@code null} but this method does not check it (a MIOException should
     * be thrown by the {@code handler}.
     *
     * @param handler
     *            The handler which receives the events.
     * @param type The type.
     * @throws MIOException
     *             In case of an error.
     */
    private static final void _type(final IMapHandler handler, final IRef type)
            throws MIOException {
        handler.startType();
        handler.topicRef(type);
        handler.endType();
    }

    /**
     * Issues events to the {@code handler} which create a role with the provided
     * {@code type} and {@code player}.
     *
     * @param handler The handler which receives the events. 
     * @param type The role type
     * @param player The role player
     * @throws MIOException In case of an error.
     */
    protected static final void role(final IMapHandler handler, 
            final IRef type, final IRef player) throws MIOException {
        handler.startRole();
        _type(handler, type);
        handler.startPlayer();
        handler.topicRef(player);
        handler.endPlayer();
        handler.endRole();
    }

    /**
     * Processes the scope, if any.
     * 
     * This method must be called from the derived classes to process the scope.
     * 
     * @param handler
     *            The handler to operate upon.
     */
    protected final void processScope(final IMapHandler handler)
            throws MIOException {
        processScope(handler, null);
    }

    /**
     * Processes the scope, if any.
     * 
     * This method must be called from the derived classes to process the scope.
     * 
     * @param handler
     *            The handler to operate upon.
     */
    protected final void processScope(final IMapHandler handler,
            final IRef additionalTheme) throws MIOException {
        if (_scope == null && additionalTheme == null) {
            return;
        }
        handler.startScope();
        if (additionalTheme != null) {
            handler.startTheme();
            handler.topicRef(additionalTheme);
            handler.endTheme();
        }
        if (_scope != null) {
            for (IRef theme : _scope) {
                handler.startTheme();
                handler.topicRef(theme);
                handler.endTheme();
            }
        }
        handler.endScope();
    }

}
