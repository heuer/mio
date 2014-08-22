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
package com.semagia.mio.ctm;

import com.semagia.mio.IRef;
import com.semagia.mio.helpers.Literal;
import com.semagia.mio.voc.TMDM;
import com.semagia.mio.voc.XSD;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 606 $ - $Date: 2011-01-20 00:48:46 +0100 (Do, 20 Jan 2011) $
 */
final class Reference implements IReference {

    private static final int
        IID = IRef.ITEM_IDENTIFIER,
        SID = IRef.SUBJECT_IDENTIFIER,
        SLO = IRef.SUBJECT_LOCATOR,
        IDENT = 1000,
        VSLO = 1001,
        VAR = 1002,
        IRI = SID,
        STRING = 1003,
        VIID = 1009,
        WILDCARD = 1010,
        NAMED_WILDCARD = 1011,
        LITERAL = 1012,
        FOCUS = 1013;

    static final IReference CTM_INTEGER = create("*", CTMUtils.CTM_INTEGER);
    static final IReference TOPIC_NAME = createIRI(TMDM.TOPIC_NAME);
    static final IReference TOPIC_IN_FOCUS = new Reference(FOCUS, null);

    private final int _type;
    private final Object _val;
    
    private Reference(final int type, final Object value) {
        _type = type;
        _val = value;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IReference#isVIID()
     */
    @Override
    public boolean isVIID() {
        return VIID == _type;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IReference#isVSLO()
     */
    @Override
    public boolean isVSLO() {
        return VSLO == _type;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IReference#isVariable()
     */
    @Override
    public boolean isVariable() {
        return VAR == _type;
    }

    @Override
    public boolean isIRI() {
        return IRI == _type;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IReference#isItemIdentifier()
     */
    @Override
    public boolean isItemIdentifier() {
        return IID == _type;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IReference#isSubjectLocator()
     */
    @Override
    public boolean isSubjectLocator() {
        return SLO == _type;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IReference#isNamedWildcard()
     */
    @Override
    public boolean isNamedWildcard() {
        return NAMED_WILDCARD == _type;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IReference#isWildcard()
     */
    @Override
    public boolean isWildcard() {
        return WILDCARD == _type;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IReference#getValue()
     */
    @Override
    public Object getValue() {
        return _val;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IRef#getIRI()
     */
    @Override
    public String getIRI() {
        return getString();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IReference#getString()
     */
    @Override
    public String getString() {
        return _type == STRING ? ((Literal) _val).getValue() : (String) _val;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.ctm.IReference#getLiteral()
     */
    @Override
    public Literal getLiteral() {
        return (Literal) _val;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IRef#getType()
     */
    @Override
    public int getType() {
        return _type;
    }

    public static IReference createString(final String value) {
        return new Reference(STRING, Literal.createString(value));
    }

    public static IReference createVariable(final String name) {
        return new Reference(VAR, name);
    }

    public static IReference createSLO(final String iri) {
        return new Reference(SLO, iri);
    }

    public static IReference createVSLO(final String var) {
        return new Reference(VSLO, var);
    }

    public static IReference createIID(final String iri) {
        return new Reference(IID, iri);
    }

    public static IReference createVIID(final String var) {
        return new Reference(VIID, var);
    }

    public static IReference create(final String value, final String datatype) {
        return new Reference(LITERAL, Literal.create(value, datatype));
    }

    public static IReference createDecimal(final String value) {
       return create(value, XSD.DECIMAL);
    }

    public static IReference createInteger(final String value) {
        return create(value, XSD.INTEGER);
    }

    public static IReference createDate(final String value) {
        return create(value, XSD.DATE);
    }

    public static IReference createDateTime(final String value) {
        return create(value, XSD.DATE_TIME);
    }

    public static IReference createIdent(final String ident) {
        return new Reference(IDENT, ident);
    }

    public static IReference createIRI(String iri) {
        return new Reference(IRI, iri);
    }

    public static IReference createNamedWildcard(String name) {
        return new Reference(NAMED_WILDCARD, name);
    }

    public static IReference createWildcard(String name) {
        return new Reference(WILDCARD, name);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "<" + _type + "> <" + _val + ">";
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IReference)) {
            return false;
        }
        final IReference other = (IReference) obj;
        return _type == other.getType() && _val != null ? _val.equals(other.getValue()) : other.getValue() == null;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return _type + (_val != null ? _val.hashCode() : 30);
    }

}
