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
 * Default, immutable implementation of {@link IRef}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 607 $ - $Date: 2011-01-20 02:28:15 +0100 (Do, 20 Jan 2011) $
 */
public class Ref implements IRef {

    private final String _iri;
    private final int _type;

    protected Ref(final String iri, final int type) {
        _iri = iri;
        _type = type;
    }

    /**
     * Creates a {@link IRef} which represents a subject identifier with the
     * specified <tt>iri</tt>.
     *
     * @param iri The IRI of the subject locator.
     * @return A {@link IRef} which represents a subject identifier.
     */
    public static final IRef createSubjectIdentifier(final String iri) {
        return new Ref(iri, SUBJECT_IDENTIFIER);
    }

    /**
     * Creates a {@link IRef} which represents a subject locator with the
     * specified <tt>iri</tt>.
     *
     * @param iri The IRI of the subject locator.
     * @return A {@link IRef} which represents a subject locator.
     */
    public static final IRef createSubjectLocator(final String iri) {
        return new Ref(iri, SUBJECT_LOCATOR);
    }

    /**
     * Creates a {@link IRef} which represents an item identifier with the
     * specified <tt>iri</tt>.
     *
     * @param iri The IRI of the subject locator.
     * @return A {@link IRef} which represents an item identifier.
     */
    public static final IRef createItemIdentifier(final String iri) {
        return new Ref(iri, ITEM_IDENTIFIER);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ITopicRef#getIRI()
     */
    @Override
    public String getIRI() {
        return _iri;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ITopicRef#getType()
     */
    @Override
    public int getType() {
        return _type;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof Ref) 
                    && _type == ((Ref) obj)._type 
                    && _iri.equals(((Ref) obj)._iri);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return _type * 31 + _iri.hashCode();
    }

    @Override
    public String toString() {
        if (_type == SUBJECT_IDENTIFIER) {
            return "<subject identifier " + _iri + ">"; 
        }
        else if (_type == SUBJECT_LOCATOR) {
            return "<subject locator " + _iri + ">";
        }
        else if (_type == ITEM_IDENTIFIER) {
            return "<item identifier " + _iri + ">";
        }
        return super.toString();
    }

}
