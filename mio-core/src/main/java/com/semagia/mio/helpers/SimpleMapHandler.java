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
import com.semagia.mio.voc.TMDM;

/**
 * {@link IMapHandler} implementation that provides additional methods to
 * handle common use cases easily.
 * <p>
 * The events are forwarded to an underlying {@link IMapHandler}.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 607 $ - $Date: 2011-01-20 02:28:15 +0100 (Do, 20 Jan 2011) $
 */
public final class SimpleMapHandler extends DelegatingMapHandler {

    private static final IRef _TYPE_INSTANCE = Ref.createSubjectIdentifier(TMDM.TYPE_INSTANCE);
    private static final IRef _TYPE = Ref.createSubjectIdentifier(TMDM.TYPE);
    private static final IRef _INSTANCE = Ref.createSubjectIdentifier(TMDM.INSTANCE);

    private static final IRef _SUPERTYPE_SUBTYPE = Ref.createSubjectIdentifier(TMDM.SUPERTYPE_SUBTYPE);
    private static final IRef _SUPERTYPE = Ref.createSubjectIdentifier(TMDM.SUPERTYPE);
    private static final IRef _SUBTYPE = Ref.createSubjectIdentifier(TMDM.SUBTYPE);

    /**
     * Constructs a {@link SimpleMapHandler} with the underlying 
     * <tt>mapHandler</tt> which receives all notifications.
     *
     * @param mapHandler A {@link IMapHandler} implementation.
     */
    protected SimpleMapHandler(final IMapHandler mapHandler) {
        super(mapHandler);
    }

    /**
     * Factory method for SimpleMapHandlers.
     * <p>
     * If the provided <tt>mapHandler</tt> is an instance of this class, the
     * <tt>mapHandler</tt> will be returned, otherwise an instance of this 
     * class wrapping the provided <tt>mapHandler</tt>.
     * </p>
     *
     * @param mapHandler The {@link IMapHandler} instance to wrap.
     * @return An instance of this class.
     */
    public static SimpleMapHandler create(final IMapHandler mapHandler) {
        return mapHandler instanceof SimpleMapHandler 
                                            ? (SimpleMapHandler) mapHandler
                                            : new SimpleMapHandler(mapHandler);
    }

    /**
     * Generates a {@link #startType()}, {@link #topicRef(IRef)}, and a 
     * {@link #endType()} notification.
     *
     * @param ref The identity of the topic which should become the type of the
     *              currently parsed typed Topic Maps construct.
     * @throws MIOException If the underlying {@link IMapHandler} reports an error.
     */
    public void type(final IRef ref) throws MIOException {
        startType();
        topicRef(ref);
        endType();
    }

    /**
     * Generates a {@link #startReifier()}, {@link #topicRef(IRef)}, and a 
     * {@link #endReifier()} notification iff the <tt>ref</tt> is not null.
     *
     * @param ref The identity of the reifier or <tt>null</tt>.
     * @throws MIOException If the underlying {@link IMapHandler} reports an error.
     */
    public void reifier(final IRef ref) throws MIOException {
        if (ref == null) {
            return;
        }
        startReifier();
        topicRef(ref);
        endReifier();
    }

    /**
     * Generates a {@link #startPlayer()}, {@link #topicRef(IRef)}, and a 
     * {@link #endPlayer()} notification.
     *
     * @param ref The identity of the topic which should play the role in the 
     *              focus.
     * @throws MIOException If the underlying {@link IMapHandler} reports an error.
     */
    public void player(final IRef ref) throws MIOException {
        startPlayer();
        topicRef(ref);
        endPlayer();
    }

    /**
     * Geneerates a {@link #startRole(IRef)}, {@link #player(IRef)}, and a
     * {@link #endRole()} notification.
     *
     * @param type The type of the role.
     * @param player The role player.
     * @throws MIOException If the underlying {@link IMapHandler} reports an error.
     */
    public void role(final IRef type, final IRef player) throws MIOException {
        startRole(type);
        player(player);
        endRole();
    }

    /**
     * Generates a {@link #startTheme()}, {@link #topicRef(IRef)}, and a 
     * {@link #endTheme()} notification.
     *
     * @param ref The identity of the topic which should become part of the 
     *              scope in the focus.
     * @throws MIOException If the underlying {@link IMapHandler} reports an error.
     */
    public void theme(final IRef ref) throws MIOException {
        startTheme();
        topicRef(ref);
        endTheme();
    }

    /**
     * Generates a {@link #startIsa()}, {@link #topicRef(IRef)}, and a 
     * {@link #endIsa()} notification. 
     *
     * @param ref An identity of the topic which should become a type of the
     *              currently parsed topic.
     * @throws MIOException If the underlying {@link IMapHandler} reports an error.
     */
    public void isa(final IRef ref) throws MIOException {
        startIsa();
        topicRef(ref);
        endIsa();
    }

    /**
     * Generates events for type-instance association where <tt>instance</tt> 
     * plays the instance role and <tt>type</tt> the type role.
     *
     * @param instance A reference to a topic which should play the instance role.
     * @param type A reference to a topic which should play the type role.
     * @throws MIOException If the underlying {@link IMapHandler} reports an error.
     */
    public void isa(final IRef instance, final IRef type) throws MIOException {
        startAssociation(_TYPE_INSTANCE);
        role(_TYPE, type);
        role(_INSTANCE, instance);
        endAssociation();
    }

    /**
     * Generates events for a supertype-subtype association. 
     *
     * @param subtype A reference to a topic which should play the subtype role.
     * @param supertype A reference to a topic which should play the supertype role.
     * @throws MIOException If the underlying {@link IMapHandler} reports an error.
     */
    public void ako(final IRef subtype, final IRef supertype) throws MIOException {
        startAssociation(_SUPERTYPE_SUBTYPE);
        role(_SUPERTYPE, supertype);
        role(_SUBTYPE, subtype);
        endAssociation();
    }

    /**
     * Generates a {@link #startAssociation()} and a {@link #type(IRef)} event. 
     *
     * @param type The identity of the topic which should be used as type.
     * @throws MIOException If the underlying {@link IMapHandler} reports an error.
     */
    public void startAssociation(final IRef type) throws MIOException {
        startAssociation();
        type(type);
    }

    /**
     * Generates a {@link #startRole()} and a {@link #type(IRef)} event.
     *
     * @param type The identity of the topic which should be used as type.
     * @throws MIOException If the underlying {@link IMapHandler} reports an error.
     */
    public void startRole(final IRef type) throws MIOException {
        startRole();
        type(type);
    }

    /**
     * Generates a {@link #startOccurrence()} and a {@link #type(IRef)} event.
     *
     * @param type The identity of the topic which should be used as type.
     * @throws MIOException If the underlying {@link IMapHandler} reports an error.
     */
    public void startOccurrence(final IRef type) throws MIOException {
        startOccurrence();
        type(type);
    }

    /**
     * Generates a {@link #startName()} and a {@link #type(IRef)} event.
     *
     * @param type The identity of the topic which should be used as type.
     * @throws MIOException If the underlying {@link IMapHandler} reports an error.
     */
    public void startName(final IRef type) throws MIOException {
        startName();
        type(type);
    }

}
