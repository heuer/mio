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
package com.semagia.mio.voc;

/**
 * Constants for XML Schema Datatypes.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 607 $ - $Date: 2011-01-20 02:28:15 +0100 (Do, 20 Jan 2011) $
 */
public final class XSD {

    private static final String _BASE = "http://www.w3.org/2001/XMLSchema#";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#anyType">xsd:anyType</a>
     * datatype.
     */
    public static final String ANY_TYPE        = _BASE + "anyType";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#anySimpleType">xsd:anySimpleType</a>
     * datatype.
     */
    public static final String ANY_SIMPLE_TYPE = _BASE + "anySimpleType";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#duration">xsd:duration</a>
     * datatype.
     */
    public static final String DURATION        = _BASE + "duration";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#dateTime">xsd:dateTime</a>
     * datatype.
     */
    public static final String DATE_TIME       = _BASE + "dateTime";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#time">xsd:time</a>
     * datatype.
     */
    public static final String TIME            = _BASE + "time";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#date">xsd:date</a>
     * datatype.
     */
    public static final String DATE            = _BASE + "date";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#gYearMonth">xsd:gYearMonth</a>
     * datatype.
     */
    public static final String G_YEAR_MONTH    = _BASE + "gYearMonth";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#gYear">xsd:gYear</a>
     * datatype.
     */
    public static final String G_YEAR          = _BASE + "gYear";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#gMonthDay">xsd:gMonthDay</a>
     * datatype.
     */
    public static final String G_MONTH_DAY     = _BASE + "gMonthDay";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#gDay">xsd:gDay</a>
     * datatype.
     */
    public static final String G_DAY           = _BASE + "gDay";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#gMonth">xsd:gMonth</a>
     * datatype.
     */
    public static final String G_MONTH         = _BASE + "gMonth";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#boolean">xsd:boolean</a>
     * datatype.
     */
    public static final String BOOLEAN         = _BASE + "boolean";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#base64Binary">xsd:base64Binary</a>
     * datatype.
     */
    public static final String BASE64_BINARY   = _BASE + "base64Binary";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#hexBinary">xsd:hexBinary</a>
     * datatype.
     */
    public static final String HEX_BINARY      = _BASE + "hexBinary";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#float">xsd:float</a>
     * datatype.
     */
    public static final String FLOAT           = _BASE + "float";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#double">xsd:double</a>
     * datatype.
     */
    public static final String DOUBLE          = _BASE + "double";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#anyURI">xsd:anyURI</a>
     * datatype.
     */
    public static final String ANY_URI         = _BASE + "anyURI";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#QName">xsd:QName</a>
     * datatype.
     */
    public static final String QNAME           = _BASE + "QName";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#NOTATION">xsd:NOTATION</a>
     * datatype.
     */
    public static final String NOTATION        = _BASE + "notation";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#string">xsd:string</a>
     * datatype.
     */
    public static final String STRING          = _BASE + "string";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#decimal">xsd:decimal</a>
     * datatype.
     */
    public static final String DECIMAL         = _BASE + "decimal";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#normalizedString">xsd:normalizedString</a>
     * datatype.
     */
    public static final String NORMALIZED_STRING = _BASE + "normalizedString";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#integer">xsd:integer</a>
     * datatype.
     */
    public static final String INTEGER         = _BASE + "integer";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#token">xsd:token</a>
     * datatype.
     */
    public static final String TOKEN           = _BASE + "token";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#nonPositiveInteger">xsd:nonPositiveInteger</a>
     * datatype.
     */
    public static final String NON_POSITIVE_INTEGER = _BASE + "nonPositiveInteger";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#long">xsd:long</a>
     * datatype.
     */
    public static final String LONG            = _BASE + "long";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#nonNegativeInteger">xsd:nonNegativeInteger</a>
     * datatype.
     */
    public static final String NON_NEGATIVE_INTEGER = _BASE + "nonNegativeInteger";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#language">xsd:language</a>
     * datatype.
     */
    public static final String LANGUAGE        = _BASE + "language";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#Name">xsd:Name</a>
     * datatype.
     */
    public static final String NAME            = _BASE + "Name";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#NMTOKEN">xsd:NMTOKEN</a>
     * datatype.
     */
    public static final String NMTOKEN         = _BASE + "NMTOKEN";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#negativeInteger">xsd:negativeInteger</a>
     * datatype.
     */
    public static final String NEGATIVE_INTEGER = _BASE + "negativeInteger";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#int">xsd:int</a>
     * datatype.
     */
    public static final String INT             = _BASE + "int";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#unsignedLong">xsd:unsignedLong</a>
     * datatype.
     */
    public static final String UNSIGNED_LONG   = _BASE + "unsignedLong";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#positiveInteger">xsd:positiveInteger</a>
     * datatype.
     */
    public static final String POSITIVE_INTEGER = _BASE + "positiveInteger";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#NCName">xsd:NCName</a>
     * datatype.
     */
    public static final String NCNAME          = _BASE + "NCName";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#NMTOKEN">xsd:NMTOKEN</a>
     * datatype.
     */
    public static final String NMTOKENS        = _BASE + "NMTOKEN";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#short">xsd:short</a>
     * datatype.
     */
    public static final String SHORT           = _BASE + "short";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#unsignedInt">xsd:unsignedInt</a>
     * datatype.
     */
    public static final String UNSIGNED_INT    = _BASE + "unsignedInt";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#ID">xsd:ID</a>
     * datatype.
     */
    public static final String ID              = _BASE + "ID";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#IDREF">xsd:IDREF</a>
     * datatype.
     */
    public static final String IDREF           = _BASE + "IDREF";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#ENTITY">xsd:ENTITY</a>
     * datatype.
     */
    public static final String ENTITY          = _BASE + "ENTITY";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#String">xsd:byte</a>
     * datatype.
     */
    public static final String BYTE            = _BASE + "byte";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#unsignedShort">xsd:unsignedShort</a>
     * datatype.
     */
    public static final String UNSIGNED_SHORT  = _BASE + "unsignedShort";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#IDREFS">xsd:IDREFS</a>
     * datatype.
     */
    public static final String IDREFS          = _BASE + "IDREFS";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#ENTITIES">xsd:ENTITIES</a>
     * datatype.
     */
    public static final String ENTITIES        = _BASE + "ENTITIES";

    /**
     * IRI for the 
     * <a href="http://www.w3.org/2001/XMLSchema#unsignedByte">xsd:unsignedByte</a>
     * datatype.
     */
    public static final String UNSIGNED_BYTE   = _BASE + "unsignedByte";
}
