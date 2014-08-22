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
package com.semagia.mio.voc;

/**
 * This class provides constants for the 
 * <a href="http://psi.ontopia.net/rdf2tm/">RDF-to-TM mapping (RTM)</a> vocabulary.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 549 $ - $Date: 2010-09-25 20:06:52 +0200 (Sa, 25 Sep 2010) $
 */
public final class RDF2TM {

    private RDF2TM() {
        // noop.
    }

    private static final String _BASE = "http://psi.ontopia.net/rdf2tm/#";

    /**
     * This property is used to describe what an RDF property maps to in a topic map. 
     * Statements using RDF properties which are not described with <tt>rtm:maps-to</tt> are 
     * ignored during conversion.
     */
    public static final String MAPS_TO = _BASE + "maps-to";

    /**
     * By default, RDF statements mapped to occurrences or associations get as their 
     * type the topic representing the RDF property of the statement. 
     * This statement is used to override that default and to explicitly set the type of the 
     * created occurrence/association. 
     */
    public static final String TYPE = _BASE + "type";

    /**
     * Using this property the name, occurrence, or association created from an RDF 
     * statement can have topics added to its scope. This property can be repeated any 
     * number of times for an rdf:Property, and all the corresponding topics are added to 
     * the scope of the created topic characteristic. 
     */
    public static final String IN_SCOPE = _BASE + "in-scope";

    /**
     * Properties mapped to associations must have this property set to the topic that 
     * provides the role type played in the resulting association by the subject in the statement. 
     */
    public static final String SUBJECT_ROLE = _BASE + "subject-role";

    /**
     * Properties mapped to associations must have this property set to the topic that provides the 
     * role type played in the resulting association by the object in the statement. 
     */
    public static final String OBJECT_ROLE = _BASE + "object-role";

    /** 
     * Maps statements using the property to base names
     */
    public static final String BASENAME = _BASE + "basename";

    /** 
     * Maps statements using the property to occurrences
     */
    public static final String OCCURRENCE = _BASE + "occurrence";

    /** 
     * Maps statements using the property to associations
     */
    public static final String ASSOCIATION = _BASE + "association";

    /** 
     * Maps statements using the property to type-instance relationships.
     */
    public static final String INSTANCE_OF = _BASE + "instance-of";

    /** 
     * Maps the values of statements using the property to subject identifiers in the topic map
     */
    public static final String SUBJECT_IDENTIFIER = _BASE + "subject-identifier";

    /** 
     * Maps the values of statements using the property to subject locators in the topic map
     */
    public static final String SUBJECT_LOCATOR = _BASE + "subject-locator";

    /** 
     * Maps the values of statements using the property to item identifiers in the topic map
     */
    public static final String SOURCE_LOCATOR = _BASE + "source-locator";

}
