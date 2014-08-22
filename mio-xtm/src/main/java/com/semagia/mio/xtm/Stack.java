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
package com.semagia.mio.xtm;

import java.util.ArrayList;
import java.util.EmptyStackException;

/**
 * Simple stack which is unsynchronized.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 462 $ - $Date: 2010-09-08 01:20:13 +0200 (Mi, 08 Sep 2010) $
 */
@SuppressWarnings("serial")
final class Stack<E> extends ArrayList<E>{

    private static final int _SIZE = 4;

    /**
     * Creates an empty stack.
     */
    public Stack() {
        super(_SIZE);
    }

    /**
     * Returns if the stack is empty.
     *
     * @return <code>true</code> if the stack is emtpy, otherwise <code>false</code>.
     */
    public boolean empty() {
        return isEmpty();
    }

    /**
     * Removes and returns the item at the top of this stack.
     * 
     * @return The item at the top of this stack.
     */
    public E pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return remove(size()-1);
    }

    /**
     * Pushes an item onto the top of the stack.
     * 
     * @param item The item to be pushed onto this stack.
     * @return The item.
     */
    public E push(E item) {
        add(item);
        return item;
    }

    /**
     * Returns the item at the top of the stack without removing it.
     * 
     * @return Returns the item on top of the stack.
     */
    public E peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return get(size() - 1);
    }

}
