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
package com.semagia.mio.rdf.mapping;

import com.semagia.mio.IMapHandler;
import com.semagia.mio.IRef;
import static com.semagia.mio.IRef.ITEM_IDENTIFIER;
import static com.semagia.mio.IRef.SUBJECT_IDENTIFIER;
import static com.semagia.mio.IRef.SUBJECT_LOCATOR;
import com.semagia.mio.MIOException;
import com.semagia.mio.helpers.RefUtils;
import com.semagia.mio.rdf.api.IErrorHandler;
import com.semagia.mio.rdf.api.IMapper;

/**
 * <tt>rtm:subject-identifier</tt>, <tt>rtm:subject-locator</tt>, and 
 * <tt>rtm:source-locator</tt> mapping implementation.
 * 
 * {@link #getSubjectIdentifierMapper()}, {@link #getSubjectLocatorMapper()}, and
 * {@link #getItemIdentifierMapper()} can be used to obtain an instance of this class.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 615 $ - $Date: 2011-04-07 16:19:33 +0200 (Do, 07 Apr 2011) $
 */
final class IdentityMapper extends AbstractMapper {

    private static final IMapper _SID = new IdentityMapper(SUBJECT_IDENTIFIER);
    private static final IMapper _SLO = new IdentityMapper(SUBJECT_LOCATOR);
    private static final IMapper _IID = new IdentityMapper(ITEM_IDENTIFIER);

    private final int _identityType;

    /**
     * Creates an instance.
     *
     * @type The identity type. Either {@link com.semagia.mio.IRef.ITEM_IDENTIFIER}, 
     *        {@link com.semagia.mio.IRef.SUBJECT_IDENTIFIER}, 
     *        or {@link com.semagia.mio.IRef.SUBJECT_LOCATOR}.
     */
    private IdentityMapper(final int type) {
        super(type == ITEM_IDENTIFIER ? "rtm:source-locator" : type == SUBJECT_IDENTIFIER ? "rtm:subject-identifier" : "rtm:subject-locator");
        RefUtils.checkIsValidReference(type);
        _identityType = type;
    }

    /**
     * Returns an instance which converts an object IRI into a subject identifier.
     *
     * @return A {@link IMapper} which maps object IRIs to subject identifiers.
     */
    public static IMapper getSubjectIdentifierMapper() {
        return _SID;
    }

    /**
     * Returns an instance which converts an object IRI into a subject locator.
     *
     * @return A {@link IMapper} which maps object IRIs to subject locators.
     */
    public static IMapper getSubjectLocatorMapper() {
        return _SLO;
    }

    /**
     * Returns an instance which converts an object IRI into an item identifier.
     *
     * @return A {@link IMapper} which maps object IRIs to item identifiers.
     */
    public static IMapper getItemIdentifierMapper() {
        return _IID;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.rdf.IMapper#handle(com.semagia.mio.helpers.SimpleMapHandler, com.semagia.mio.IRef, java.lang.String, java.lang.String, boolean)
     */
    @Override
    public void handle(final IMapHandler handler, final IErrorHandler errorHandler,
            final IRef subject, final String p1, final String obj,
            final boolean objBNode) throws MIOException {
        if (objBNode) {
            errorHandler.rejectBlankNode(_name);
        }
        else {
            if (SUBJECT_IDENTIFIER == _identityType) {
                handler.subjectIdentifier(obj);
            }
            else if (SUBJECT_LOCATOR == _identityType) {
                handler.subjectLocator(obj);
            }
            else {
                handler.itemIdentifier(obj);
            }
        }
    }

}
