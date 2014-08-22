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
package com.semagia.mio.ctm;

import com.semagia.mio.MIOException;
import com.semagia.mio.helpers.Literal;
import com.semagia.mio.helpers.SimpleMapHandler;

/**
 * Template context which keeps track of variable bindings.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 606 $ - $Date: 2011-01-20 00:48:46 +0100 (Do, 20 Jan 2011) $
 */
interface ITemplateContext {

    /**
     * Returns the underlying {@link com.semagia.mio.IMapHandler}.
     *
     * @return The map handler.
     */
    SimpleMapHandler getMapHandler();

    /**
     * Returns the environment of the template context.
     *
     * @return The environment.
     */
    IEnvironment getEnvironment();

    /**
     * Resolves the provided reference to a topic.
     *
     * @param ref The reference to resolve.
     * @return A topic reference.
     * @throws MIOException In case the reference cannot be resolved to a topic.
     */
    IReference getTopicReference(IReference ref) throws MIOException;

    /**
     * 
     *
     * @param var
     * @return
     * @throws MIOException
     */
    String getIRI(IReference var) throws MIOException;

    /**
     * Resolves the provided variable into a literal.
     *
     * @param var The variable to resolve.
     * @return A literal.
     * @throws MIOException In case the reference cannot be resolved to a literal.
     */
    Literal getLiteral(IReference var) throws MIOException;

    /**
     * Sets the provided topic into the focus.
     *
     * @param topic The topic which should receive the focus.
     */
    void pushFocus(IReference topic);

    /**
     * Removes a topic from the focus.
     */
    void popFocus();

    /**
     * Returns the topic in the focus.
     *
     * @return The topic in the focus.
     */
    IReference getFocus();

}
