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

/**
 * Represents a reference to a subject identifier, subject locator or item
 * identifier.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 607 $ - $Date: 2011-01-20 02:28:15 +0100 (Do, 20 Jan 2011) $
 */
public interface IRef {

    /**
     * Constant for an item identifier.
     */
    public static final int ITEM_IDENTIFIER = 1;

    /**
     * Constant for a subject identifier.
     */
    public static final int SUBJECT_IDENTIFIER = 2;

    /**
     * Constant for a subject locator.
     */
    public static final int SUBJECT_LOCATOR = 3;

    /**
     * Returns the IRI of this reference.
     * 
     * The IRI is always absolute.
     *
     * @return The IRI.
     */
    public String getIRI();

    /**
     * The type of this reference.
     * 
     * Returns {@link #ITEM_IDENTIFIER}, {@link #SUBJECT_IDENTIFIER}, or
     * {@link #SUBJECT_LOCATOR}.
     *
     * @return The type of this reference.
     */
    public int getType();

}
