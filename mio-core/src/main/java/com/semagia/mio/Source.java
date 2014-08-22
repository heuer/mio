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

import java.io.InputStream;
import java.io.Reader;

/**
 * Represents an immutable source to read a topic map from.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 607 $ - $Date: 2011-01-20 02:28:15 +0100 (Do, 20 Jan 2011) $
 */
public final class Source {

    private final InputStream _byteStream;
    private final Reader _characterStream;
    private final String _baseIRI;
    private final String _iri;
    private final String _encoding;
    

    /**
     * 
     *
     * @param byteStream
     * @param baseIRI
     */
    public Source(final InputStream byteStream, final String baseIRI) {
        this(byteStream, baseIRI, null);
    }

    /**
     * 
     *
     * @param byteStream
     * @param baseIRI
     * @param encoding
     */
    public Source(final InputStream byteStream, final String baseIRI, 
            final String encoding) {
        _byteStream = byteStream;
        _characterStream = null;
        _iri = null;
        _baseIRI = baseIRI;
        _encoding = encoding;
    }

    /**
     * 
     *
     * @param characterStream
     * @param baseIRI
     */
    public Source(final Reader characterStream, final String baseIRI) {
        this(characterStream, baseIRI, null);
    }

    /**
     * 
     *
     * @param characterStream
     * @param baseIRI
     * @param encoding
     */
    public Source(final Reader characterStream, final String baseIRI, 
            final String encoding) {
        _byteStream = null;
        _characterStream = characterStream;
        _iri = null;
        _baseIRI = baseIRI;
        _encoding = encoding;
    }

    /**
     * 
     *
     * @param iri
     */
    public Source(final String iri) {
        _byteStream = null;
        _characterStream = null;
        _iri = iri;
        _baseIRI = iri;
        _encoding = null;
    }

    /**
     * 
     *
     * @return
     */
    public String getIRI() {
        return _iri;
    }

    /**
     * 
     *
     * @return
     */
    public String getBaseIRI() {
        return _baseIRI;
    }

    /**
     * 
     *
     * @return
     */
    public InputStream getByteStream() {
        return _byteStream;
    }

    /**
     * 
     *
     * @return
     */
    public Reader getCharacterStream() {
        return _characterStream;
    }

    /**
     * Returns the encoding or {@code null} if no encoding was provided.
     *
     * @return The encoding or {@code null}.
     */
    public String getEncoding() {
        return _encoding;
    }

}
