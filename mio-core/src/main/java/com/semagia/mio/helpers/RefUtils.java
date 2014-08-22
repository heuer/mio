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

import com.semagia.mio.IRef;

/**
 * Utility functions related to {@link IRef} instances.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 607 $ - $Date: 2011-01-20 02:28:15 +0100 (Do, 20 Jan 2011) $
 */
public final class RefUtils {

    private RefUtils() {
        // noop.
    }

    /**
     * Returns {@code true} if the {@code type} represents an item identifier,
     * a subject identifier, or subject locator.
     *
     * @param referenceType An integer.
     * @return {@code true} if the {@code type} represents a valid {@link IRef#getType()},
     *          otherwise {@code false}
     */
    public static boolean isValidReference(final int referenceType) {
        return referenceType == IRef.SUBJECT_IDENTIFIER 
                || referenceType == IRef.ITEM_IDENTIFIER
                || referenceType == IRef.SUBJECT_LOCATOR;
    }

    /**
     * Checks if {@code referenceType} represents a valid {@link IRef#getType}.
     *
     * @param referenceType An integer.
     * @throws IllegalArgumentException In case {@code referenceType} does not
     *          represent a legal reference type.
     */
    public static void checkIsValidReference(final int referenceType) throws IllegalArgumentException {
        if (!isValidReference(referenceType)) {
            throw new IllegalArgumentException("Expected <subject-identifier>, <subject-locator> or <item-identifier>, got: " + referenceType);
        }
    }
}
