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

import java.util.regex.Pattern;

/**
 * Represent an immutable QName. 
 * 
 * QNames consist of a prefix and a local part divided through a colon.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 607 $ - $Date: 2011-01-20 02:28:15 +0100 (Do, 20 Jan 2011) $
 */
public final class QName {

    private static final Pattern _QNAME_PATTERN = Pattern.compile(":");
    private final String _prefix;
    private final String _local;

    /**
     * Constructs a QName.
     *
     * @param prefix The prefix part.
     * @param local The local part.
     */
    private QName(final String prefix, final String local) {
        _prefix = prefix;
        _local = local;
    }

    /**
     * Returns the prefix part of the QName.
     *
     * @return The prefix part.
     */
    public String getPrefix() {
        return _prefix;
    }

    /**
     * Returns the local part of the QName.
     *
     * @return The local part.
     */
    public String getLocal() {
        return _local;
    }

    /**
     * Creates a QName from a string.
     *
     * @param qName A string representing a QName.
     * @return A QName instance.
     * @throws IllegalArgumentException If the string represents not a valid QName. 
     */
    public static QName create(final String qName) throws IllegalArgumentException {
        String[] parts = _QNAME_PATTERN.split(qName);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Illegal QName: '" + qName + "'");
        }
        return new QName(parts[0], parts[1]);
    }
}
