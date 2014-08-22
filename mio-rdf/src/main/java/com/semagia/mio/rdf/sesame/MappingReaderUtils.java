/*
 * Copyright 2008 - 2010 Lars Heuer (heuer[at]semagia.com)
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
package com.semagia.mio.rdf.sesame;

import com.semagia.mio.MIOException;
import com.semagia.mio.Syntax;
import com.semagia.mio.rdf.MappingReaderRegistry;
import com.semagia.mio.rdf.api.IMappingReader;

/**
 * Utility functions to create {@link IMappingReader} instances.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 554 $ - $Date: 2010-09-26 21:18:02 +0200 (So, 26 Sep 2010) $
 */
final class MappingReaderUtils {

    private MappingReaderUtils() {
        // noop.
    }

    /**
     * Returns a reader for the provided IRI.
     *
     * @param iri The IRI to extract the syntax from.
     * @param defaultSyntax The fallback syntax or {@code null}.
     * @return A reader instance, never {@code null}.
     * @throws MIOException If no mapping syntax for the provided IRI can be found
     *                          and the defaultSyntax is {@code null} or if 
     *                          no reader is available for the syntax.
     */
    public static IMappingReader createReader(final String iri, final Syntax defaultSyntax) throws MIOException {
        final Syntax mappingSyntax = _getSyntax(iri, defaultSyntax);
        if (mappingSyntax == null) {
            throw new MIOException("No mapping syntax found for " + iri);
        }
        final IMappingReader reader = MappingReaderRegistry.getInstance().createReader(mappingSyntax);
        if (reader == null) {
            throw new MIOException("No mapping reader found for " + mappingSyntax.getName());
        }
        return reader;
    }

    private static Syntax _getSyntax(String iri, Syntax defaultSyntax) {
        final int idx = iri.lastIndexOf('.');
        if (idx > -1) {
            return Syntax.forFileExtension(iri.substring(idx+1), defaultSyntax);
        }
        return defaultSyntax;
    }

}
