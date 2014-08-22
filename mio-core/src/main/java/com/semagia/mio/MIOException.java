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

import org.xml.sax.SAXException;

/**
 * An exception that can be thrown by a deserializer or serializer when it 
 * encounters an error from which it cannot recover.
 * <p>
 * Since the exception is derived from {@link org.xml.sax.SAXException}, it can
 * be utilized by {@link org.xml.sax.ContentHandler}s easily. 
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 607 $ - $Date: 2011-01-20 02:28:15 +0100 (Do, 20 Jan 2011) $
 */
public class MIOException extends SAXException {

    /**
     * 
     */
    private static final long serialVersionUID = 1346934770921965394L;

    /**
     * Creates a MIOException with the specified message.
     *
     * @param message The error message.
     */
    public MIOException(final String message) {
        super(message);
    }

    /**
     * Creates a MIOException with the specified message and cause.
     *
     * @param message The error message.
     * @param cause The cause of this exception.
     */
    public MIOException(final String message, final Throwable cause) {
        super(message);
        initCause(cause);
    }

    /**
     * Creates a MIOException with the specified cause.
     *
     * @param cause The cause of this exception.
     */
    public MIOException(final Throwable cause) {
        super();
        initCause(cause);
    }

}
