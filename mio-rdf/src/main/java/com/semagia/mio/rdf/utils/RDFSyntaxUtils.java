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
package com.semagia.mio.rdf.utils;

import com.semagia.mio.Syntax;

/**
 * EXPERIMENTAL: Provides retrieval of RDF-specific syntaxes.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 574 $ - $Date: 2010-09-29 11:47:39 +0200 (Mi, 29 Sep 2010) $
 */
public final class RDFSyntaxUtils {

    /**
     * The supported RDF syntaxes.
     */
    private static final Syntax[] _SYNTAXES = new Syntax[] {
        Syntax.RDFXML, Syntax.RDFA, Syntax.N3, 
        Syntax.NTRIPLES, Syntax.TRIG, Syntax.TRIX, 
        Syntax.TURTLE
    };

    /**
     * The supported mapping syntaxes.
     */
    private static final Syntax[] _MAPPING_SYNTAXES = new Syntax[_SYNTAXES.length+1];

    static {
        System.arraycopy(_SYNTAXES, 0, _MAPPING_SYNTAXES, 0, _SYNTAXES.length);
        _MAPPING_SYNTAXES[_MAPPING_SYNTAXES.length-1] = Syntax.CRTM;
    }

    private RDFSyntaxUtils() {
        // noop.
    }

    /**
     * Returns an array of RDF syntaxes.
     *
     * @return An array of RDF syntaxes.
     */
    public static Syntax[] getRDFSyntaxes() {
        return _SYNTAXES.clone();
    }

    /**
     * Returns an array of RTM mapping syntaxes.
     *
     * @return An array of mapping syntaxes.
     */
    public static Syntax[] getMappingSyntaxes() {
        return _MAPPING_SYNTAXES.clone();
    }

    /**
     * Returns a {@link Syntax} instance for the specified file <tt>extension</tt>.
     * 
     * @see #forFileExtension(String, Syntax)
     *
     * @param extension A file extension.
     * @return A {@link Syntax} instance, or {@code null} if no syntax was found.
     */
    public static final Syntax forFileExtension(final String extension) {
        return forFileExtension(extension, null);
    }

    /**
     * Returns a {@link Syntax} instance for the specified file <tt>extension</tt>.
     * <p>
     * If no syntax matches the specified extension, <tt>default</tt> will be
     * returned.
     * </p>
     *
     * @param extension The file extension.
     * @param defaultSyntax The default syntax to return if no other matches.
     * @return A {@link Syntax} instance or the default syntax (which may be {@code null}).
     */
    public static final Syntax forFileExtension(final String extension,
            final Syntax defaultSyntax) {
        return _syntaxForFileExtension(extension, defaultSyntax, _SYNTAXES);
    }

    /**
     * Returns a {@link Syntax} instance for the specified file <tt>extension</tt>.
     * 
     * @see #forFileExtension(String, Syntax)
     *
     * @param extension A file extension.
     * @return A {@link Syntax} instance or {@code null} if no syntax was found.
     */
    public static final Syntax mappingForFileExtension(final String extension) {
        return mappingForFileExtension(extension, null);
    }

    /**
     * Returns a {@link Syntax} instance for the specified file <tt>extension</tt>.
     * <p>
     * If no syntax matches the specified extension, <tt>default</tt> will be
     * returned.
     * </p>
     *
     * @param extension The file extension.
     * @param defaultSyntax The default syntax to return if no other matches.
     * @return A {@link Syntax} instance or the default syntax (which may be {@code null}).
     */
    public static final Syntax mappingForFileExtension(final String extension,
            final Syntax defaultSyntax) {
        return _syntaxForFileExtension(extension, defaultSyntax, _MAPPING_SYNTAXES);
    }

    /**
     * Returns a syntax for the specified MIME type.
     *
     * @param mimeType
     * @return A syntax for the specified <tt>mimeType</tt> or <tt>null</tt>.
     */
    public static final Syntax forMIMEType(final String mimeType) {
        return forMIMEType(mimeType, null);
    }

    /**
     * Returns a syntax for the specified MIME type.
     *
     * @param mimeType
     * @param defaultSyntax The syntax to return if no appropiate syntax was 
     *                      found.
     * @return A syntax for the specified <tt>mimeType</tt> or the 
     *          <tt>defaultSyntax</tt> if no syntax matches the specified
     *          <tt>mimeType</tt>.
     */
    public static final Syntax forMIMEType(final String mimeType, final Syntax defaultSyntax) {
        return _syntaxForMIMEType(mimeType, defaultSyntax, _SYNTAXES);
    }

    /**
     * Returns a syntax for the specified MIME type.
     *
     * @param mimeType
     * @return A syntax for the specified <tt>mimeType</tt> or <tt>null</tt>.
     */
    public static final Syntax mappingForMIMEType(final String mimeType) {
        return mappingForMIMEType(mimeType, null);
    }

    /**
     * Returns a syntax for the specified MIME type.
     *
     * @param mimeType
     * @param defaultSyntax The syntax to return if no appropiate syntax was 
     *                      found.
     * @return A syntax for the specified <tt>mimeType</tt> or the 
     *          <tt>defaultSyntax</tt> if no syntax matches the specified
     *          <tt>mimeType</tt>.
     */
    public static final Syntax mappingForMIMEType(final String mimeType, final Syntax defaultSyntax) {
        return _syntaxForMIMEType(mimeType, defaultSyntax, _MAPPING_SYNTAXES);
    }

    private static Syntax _syntaxForMIMEType(final String mimeType, final Syntax defaultSyntax, final Syntax[] syntaxes) {
        for (Syntax syntax : syntaxes) {
            for (String mt: syntax.getMIMETypes()) {
                if (mt.equalsIgnoreCase(mimeType)) {
                    return syntax;
                }
            }
        }
        return defaultSyntax;
    }

    private static Syntax _syntaxForFileExtension(final String extension, final Syntax defaultSyntax, final Syntax[] syntaxes) {
        for (Syntax syntax : syntaxes) {
            for (String ext: syntax.getFileExtensions()) {
                if (ext.equalsIgnoreCase(extension)) {
                    return syntax;
                }
            }
        }
        return defaultSyntax;
    }
}
