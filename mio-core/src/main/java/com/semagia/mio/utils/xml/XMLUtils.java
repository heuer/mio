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
package com.semagia.mio.utils.xml;

import org.xml.sax.InputSource;

import com.semagia.mio.Source;

/**
 * XML-related utility functions.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 607 $ - $Date: 2011-01-20 02:28:15 +0100 (Do, 20 Jan 2011) $
 */
public final class XMLUtils {

    private XMLUtils() {
        // noop.
    }

    /**
     * Converts a {@link Source} instance into a {@link InputSource}.
     *
     * @param source The source to convert.
     * @return The converted source.
     */
    public static InputSource asInputSource(final Source source) {
        final InputSource inputSrc = new InputSource();
        inputSrc.setByteStream(source.getByteStream());
        inputSrc.setCharacterStream(source.getCharacterStream());
        inputSrc.setEncoding(source.getEncoding());
        inputSrc.setSystemId(source.getIRI());
        return inputSrc;
    }
}
