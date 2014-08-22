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

/**
 * This class provides access to standard property IRIs.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 607 $ - $Date: 2011-01-20 02:28:15 +0100 (Do, 20 Jan 2011) $
 */
public final class Property {

    /**
     * Base for all property IRIs.
     */
    private static final String _BASE = "http://psi.semagia.com/mio/property/";

    /**
     * Indicates that the syntax of the source should be validated if this property
     * is set to {@code true}.
     */
    public static final String VALIDATE = _BASE + "validate";

    /**
     * Indicates that any directive to merge in another topic map is ignored if this property
     * is set to {@code true}.
     */
    public static final String IGNORE_MERGEMAP = _BASE + "ignore-mergemap";

    /**
     * Indicates that any directive to include another topic map is ignored if this property
     * is set to {@code true}.
     * Example: The {@code #INCLUDE} directive in LTM or {@code %include} in CTM.
     */
    public static final String IGNORE_INCLUDE = _BASE + "ignore-include";

    /**
     * Indicates a RDF to Topic Maps mapping (RTM).
     */
    public static final String RDF2TM_MAPPING = _BASE + "rdf2tm-mapping";

    /**
     * Indicates the RDF mapping syntax for RDF to Topic Maps (RTM).
     */
    public static final String RDF2TM_MAPPING_SYNTAX = _BASE + "rdf2tm-mapping-syntax";

    /**
     * Indicates an IRI to the mapping source for RDF to Topic Maps (RTM).
     */
    public static final String RDF2TM_MAPPING_IRI = _BASE + "rdf2tm-mapping-iri";

    /**
     * Indicates if unhandled statements (triples) should be logged. 
     */
    public static final String RDF2TM_REPORT_UNHANDLED_STATEMENTS = _BASE + "rdf2tm-report-unhandled-statements";

    /**
     * Indicates if the translation process should be stopped if an error occurs.
     */
    public static final String RDF2TM_STOP_ON_ERROR = _BASE + "rdf2tm-stop-on-error";

    /**
     * Indicates that the LTM deserializer should act in the legacy mode if set to {@code true}
     */
    public static final String LTM_LEGACY = _BASE + "ltm-legacy";


}
