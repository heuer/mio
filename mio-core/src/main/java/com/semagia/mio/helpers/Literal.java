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

import com.semagia.mio.voc.XSD;

/**
 * Immutable object which holds a value/datatype pair.
 * <p>
 * No validation takes place; this class holds just two strings.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 607 $ - $Date: 2011-01-20 02:28:15 +0100 (Do, 20 Jan 2011) $
 */
public final class Literal {

    private final String _value;
    private final String _datatype;

    private Literal(final String value, final String datatype) {
        _value = value;
        _datatype = datatype;
    }

    public static Literal create(final String value, final String datatype) {
        return new Literal(value, datatype);
    }

    /**
     * Creates a literal with the datatype <tt>xsd:anyURI</tt>.
     *
     * @param value The string value.
     * @return A literal.
     */
    public static Literal createIRI(final String value) {
        return create(value, XSD.ANY_URI);
    }

    /**
     * Creates a literal with the datatype <tt>xsd:string</tt>.
     *
     * @param value The string value.
     * @return A literal.
     */
    public static Literal createString(final String value) {
        return create(value, XSD.STRING);
    }

    /**
     * Creates a literal with the datatype <tt>xsd:integer</tt>.
     *
     * @param value The string value.
     * @return A literal.
     */
    public static Literal createInteger(final String value) {
        return create(value, XSD.INTEGER);
    }

    /**
     * Creates a literal with the datatype <tt>xsd:decimal</tt>.
     *
     * @param value The string value.
     * @return A literal.
     */
    public static Literal createDecimal(final String value) {
        return create(value, XSD.DECIMAL);
    }

    /**
     * Creates a literal with the datatype <tt>xsd:date</tt>.
     *
     * @param value The string value.
     * @return A literal.
     */
    public static Literal createDate(final String value) {
        return create(value, XSD.DATE);
    }

    /**
     * Creates a literal with the datatype <tt>xsd:dateTime</tt>.
     *
     * @param value The string value.
     * @return A literal.
     */
    public static Literal createDateTime(final String value) {
        return create(value, XSD.DATE_TIME);
    }

    /**
     * Creates a literal with the datatype <tt>xsd:gYearMonth</tt>.
     * 
     * @param value The string value.
     * @return A literal.
     */
    public static Literal createYearMonth(final String value) {
        return create(value, XSD.G_YEAR_MONTH);
    }

    /**
     * Returns the value of the literal
     *
     * @return The value.
     */
    public final String getValue() {
        return _value;
    }

    /**
     * Returns the datatype of the literal.
     *
     * @return The datatype.
     */
    public final String getDatatype() {
        return _datatype;
    }

}
