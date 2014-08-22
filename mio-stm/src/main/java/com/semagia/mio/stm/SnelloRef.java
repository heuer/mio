/*
 * Copyright 2007 - 2009 Lars Heuer (heuer[at]semagia.com)
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
package com.semagia.mio.stm;

import com.semagia.mio.IRef;
import com.semagia.mio.helpers.Literal;
import com.semagia.mio.voc.XSD;

/**
 * Represents either a reference to a literal to a topic.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 469 $ - $Date: 2010-09-08 12:39:06 +0200 (Mi, 08 Sep 2010) $
 */
final class SnelloRef implements IRef {

    public static final SnelloRef[] NO_REFS = new SnelloRef[0];

    public static final int
        IID = IRef.ITEM_IDENTIFIER,
        SID = IRef.SUBJECT_IDENTIFIER,
        SLO = IRef.SUBJECT_LOCATOR,
        IDENT = 1000,
        VSLO = 1001,
        VAR = 1002,
        IRI = SID,
        STRING = 1003,
        DATE = 1004,
        DATE_TIME = 1005,
        YEAR_MONTH = 1006,
        INTEGER = 1007,
        DECIMAL = 1008,
        USER_DATATYPE = 1009,
        KEY_VALUE = 1010,
        VIID = 1011,
        INTERNAL_WILDCARD = 1012,
        NAMED_WILDCARD = 1013;

    static final SnelloRef INVALID = new SnelloRef(-1, null);

    private final int _type;
    private final Object _value;

    private SnelloRef(int type, Object value) {
        _type = type;
        _value = value;
    }

    public static SnelloRef createVariable(String value) {
        return new SnelloRef(VAR, value);
    }

    public static SnelloRef createString(String value) {
        return new SnelloRef(STRING, Literal.createString(value));
    }

    public static SnelloRef create(String value, String datatype) {
        return new SnelloRef(USER_DATATYPE, Literal.create(value, datatype));
    }

    public static SnelloRef createInteger(String value) {
        return new SnelloRef(INTEGER, Literal.createInteger(value));
    }

    public static SnelloRef createDecimal(String value) {
        return new SnelloRef(DECIMAL, Literal.createDecimal(value));
    }

    public static SnelloRef createDoublePositiveInfinity() {
        return new SnelloRef(USER_DATATYPE, Literal.create("INF", XSD.DOUBLE));
    }

    public static SnelloRef createDoubleNegativeInfinity() {
        return new SnelloRef(USER_DATATYPE, Literal.create("-INF", XSD.DOUBLE));
    }

    public static SnelloRef createDate(String value) {
        return new SnelloRef(DATE, Literal.createDate(value));
    }

    public static SnelloRef createDateTime(String value) {
        return new SnelloRef(DATE_TIME, Literal.createDateTime(value));
    }

    public static Object createYearMonth(String value) {
        return new SnelloRef(YEAR_MONTH, Literal.createYearMonth(value));
    }

    public static SnelloRef createIRI(String value) {
        return new SnelloRef(IRI, value);
    }

    public static SnelloRef createSID(String value) {
        return createIRI(value);
    }

    public static SnelloRef createIID(String value) {
        return new SnelloRef(IID, value);
    }

    public static SnelloRef createVIID(String value) {
        return new SnelloRef(VIID, value);
    }

    public static SnelloRef createIdent(String value) {
        return new SnelloRef(IID, value);
    }

    public static SnelloRef createSLO(String value) {
        return new SnelloRef(SLO, value);
    }

    public static SnelloRef createVSLO(String value) {
        return new SnelloRef(VSLO, value);
    }

    public static SnelloRef createKeyValue(String name, SnelloRef ref) {
        return createKeyValue(name, ref, false);
    }

    public static SnelloRef createKeyValue(String name, SnelloRef ref, boolean variable) {
        return new SnelloRef(KEY_VALUE, new KeyValue(name, ref, variable));
    }

    public static SnelloRef createWildcard(String name) {
        return new SnelloRef(NAMED_WILDCARD, name);
    }

    public static SnelloRef createInternalWildcard(String name) {
        return new SnelloRef(INTERNAL_WILDCARD, name);
    }

    public int getType() {
        return _type;
    }

    public Object getValue() {
        return _value;
    }

    public boolean isVariable() {
        return VAR == _type;
    }

    public boolean isIID() {
        return IID == _type;
    }

    public boolean isVIID() {
        return VIID == _type;
    }

    public boolean isIRI() {
        return IRI == _type;
    }

    public boolean isSLO() {
        return SLO == _type;
    }

    public boolean isSID() {
        return SID == _type;
    }

    public boolean isVSLO() {
        return VSLO == _type;
    }

    public boolean isKeyValue() {
        return KEY_VALUE == _type;
    }

    public boolean isNamedWildcard() {
        return NAMED_WILDCARD == _type;
    }

    public boolean isInternalWildcard() {
        return INTERNAL_WILDCARD == _type;
    }

    public String getString() {
        return (String) _value;
    }

    public KeyValue getKeyValue() {
        return (KeyValue) _value;
    }

    public Literal getLiteral() {
        return (Literal) _value;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IRef#getIRI()
     */
    @Override
    public String getIRI() {
        return getString();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (_type == SUBJECT_IDENTIFIER) {
            return "<subject identifier " + _value + ">"; 
        }
        else if (_type == SUBJECT_LOCATOR) {
            return "<subject locator " + _value + ">";
        }
        else if (_type == ITEM_IDENTIFIER) {
            return "<item identifier " + _value + ">";
        }
        else if (_type == VSLO) {
            return "<variable subject locator " + _value + ">";
        }
        else if (isVariable()) {
            return "<variable " + _value + ">";
        }
        return super.toString();
    }

}
