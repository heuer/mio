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

import java.net.URL;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;

import com.semagia.mio.MIOException;

import com.thaiopensource.relaxng.SchemaFactory;
import com.thaiopensource.util.SinglePropertyMap;
import com.thaiopensource.validate.Schema;
import com.thaiopensource.validate.ValidateProperty;
import com.thaiopensource.xml.sax.DraconianErrorHandler;

/**
 * {@link org.xml.sax.ContentHandler} factory that validates the input
 * against a RELAX NG schema and delegates events to a 
 * {@link org.xml.sax.ContentHandler} instance.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 607 $ - $Date: 2011-01-20 02:28:15 +0100 (Do, 20 Jan 2011) $
 */
public final class RelaxNGValidatingContentHandler {

    /**
     * Creates a content handler which validates the input against the specified
     * <tt>schemaSource</tt>.
     * 
     * If the {@code schemaSource} ends with ".rnc", the Relax NG Compact Syntax
     * is assumed, otherwise Relax NG XML.
     *
     * @param contentHandler The content handler which receives the event in
     *          case the content is valid acc. to the schema.
     * @param schemaSource The schema source.
     * @return A validating content handler.
     * @throws MIOException In case of an exception (i.e. invalid schema source)
     */
    public static ContentHandler create(final ContentHandler contentHandler, 
            final URL schemaSource) throws MIOException {
        final SchemaFactory factory = new SchemaFactory();
        factory.setErrorHandler(new DraconianErrorHandler());
        factory.setCompactSyntax(schemaSource.getFile().endsWith(".rnc"));
        Schema schema = null;
        try {
            schema = factory.createSchema(new InputSource(schemaSource.openStream()));
        }
        catch (Exception ex) {
            throw new MIOException(ex);
        }
        final ContentHandler validator = schema.createValidator(new SinglePropertyMap(ValidateProperty.ERROR_HANDLER, new DraconianErrorHandler())).getContentHandler();
        return new TeeContentHandler(validator, contentHandler);
    }

}
