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

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;

/**
 * Represents an immutable IRI.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 607 $ - $Date: 2011-01-20 02:28:15 +0100 (Do, 20 Jan 2011) $
 */
public final class Locator {

    private final URI _uri;
    private String _reference;

    private Locator(final URI uri) {
        try {
            _reference = URLDecoder.decode(uri.toString(), "utf-8");
        }
        catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
        _uri = uri;
    }

    
    /**
     * Returns a locator from the specified string <tt>reference</tt>.
     *
     * @param reference An IRI.
     * @return A locator.
     */
    public static Locator create(final String reference) {
        return new Locator(URI.create(reference));
    }

    /**
     * Resolves the specified reference against this locator and
     * return the result.
     *
     * @param reference An IRI.
     * @return A locator representing the resulting IRI.
     */
    public Locator resolve(final String reference) {
        if ("".equals(reference)) {
            return this;
        }
        return new Locator(_uri.resolve(reference));
    }

    /**
     * Returns the reference (address) of the IRI.
     *
     * @return A string representing the IRI.
     */
    public String getReference() {
        return _reference;
    }

    /**
     * Returns the external form of the locator.
     *
     * @return A string with IRI-encoded characters if necessary.
     */
    public String toExternalForm() {
        return _uri.toASCIIString();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Locator)) {
            return false;
        }
        return _uri.equals(((Locator) obj)._uri);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return _uri.hashCode();
    }

}
