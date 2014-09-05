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

import java.util.EmptyStackException;

/**
 * Simple stack which is unsynchronized.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
final class Stack<E> {

    private Object[] _items;
    private int _itemCount; 

    public Stack() {
       this(8);
    }

    public Stack(int initialCapacity) {
       _itemCount = 0;
       _items = new Object[initialCapacity];
    }

    public boolean isEmpty() {
        return _itemCount == 0;
    }

    public int size() {
        return _itemCount;
    }

    @SuppressWarnings("unchecked")
    public E get(final int index) {
        if (index >= _itemCount) {
            throw new IndexOutOfBoundsException();
        }
        return (E) _items[index];
    }

    public boolean contains(E obj) {
        for (int i=0; i<_itemCount; i++) {
            if (_items[i].equals(obj)) {
                return true;
            }
        }
        return false;
    }
    
    @SuppressWarnings("unchecked")
    public E peek() {
       if (_itemCount == 0) {
           throw new EmptyStackException();
       }
       return (E) _items[_itemCount-1];
    }

    @SuppressWarnings("unchecked")
    public E pop() {
       if (_itemCount == 0) {
           throw new RuntimeException("Stack is empty");
       }
       return (E) _items[--_itemCount];
    }

    public void push(E item) {
       if (_itemCount == _items.length) {
           Object newItems[] = new Object[_itemCount*2 + 1];
           System.arraycopy(_items, 0, newItems, 0, _itemCount);
           _items = newItems;
       }
       _items[_itemCount] = item;
       _itemCount++;
    }

    public void clear() {
        _itemCount = 0;
    }
}
