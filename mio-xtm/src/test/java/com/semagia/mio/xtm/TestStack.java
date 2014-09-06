/*
 * Copyright 2007 - 2014 Lars Heuer (heuer[at]semagia.com)
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

import junit.framework.TestCase;

/**
 * Tests against the stack.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public class TestStack extends TestCase {

    public void testStack() {
        final Stack<Something> stack = new Stack<Something>();
        assertTrue(stack.isEmpty());
        try {
            stack.peek();
            fail("Expected an EmptyStackException for 'peek' on an empty stack");
        }
        catch (EmptyStackException ex) {
            // noop.
        }
        try {
            stack.get(0);
        }
        catch (IndexOutOfBoundsException ex) {
            // noop.
        }
        Something x = new Something();
        stack.push(x);
        assertSame(x, stack.peek());
        assertEquals(1, stack.size());
        assertFalse(stack.isEmpty());
        assertSame(x, stack.get(0));
        assertSame(x, stack.pop());
        assertEquals(0, stack.size());
        assertTrue(stack.isEmpty());
        stack.push(x);
        assertFalse(stack.isEmpty());
        assertEquals(1, stack.size());
        stack.pop();
        assertTrue(stack.isEmpty());
        for (int i=0; i < 30; i++) {
            assertEquals(i, stack.size());
            x = new Something();
            stack.push(x);
            assertSame(x, stack.get(i));
            assertEquals(i+1, stack.size());
        }
        assertEquals(30, stack.size());
        for (int i=30; i>0; i--) {
            assertEquals(i, stack.size());
            x = stack.peek();
            assertEquals(x, stack.pop());
            assertEquals(i-1, stack.size());
        }
        assertTrue(stack.isEmpty());
        assertEquals(0, stack.size());
    }

    public void testInvalid() {
        Stack<Something> stack = new Stack<Something>();
        try {
            stack.peek();
            fail("Expected an EmptyStackException for 'peek' on an empty stack");
        }
        catch (EmptyStackException ex) {
            // noop.
        }
        try {
            stack.pop();
            fail("Expected an EmptyStackException for 'pop' on an empty stack");
        }
        catch (Exception ex) {
            // noop.
        }
        try {
            stack.get(1);
            fail("Expected an IndexOutOfBoundsException for 'get' on an empty stack");
        }
        catch (IndexOutOfBoundsException ex) {
            // noop.
        }
        try {
            stack.get(0);
            fail("Expected an IndexOutOfBoundsException for 'get' on an empty stack");
        }
        catch (IndexOutOfBoundsException ex) {
            // noop.
        }
    }

    private static class Something { };
}
