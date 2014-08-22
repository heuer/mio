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
 * Constants for XTM 1.0 PSIs.
 * <p>
 * The XTM 1.0 PSIs are outdated and have no relevance for the 
 * Topic Maps Data Model. These constants are provided for (de-)serializing
 * topic maps which depend on the XTM 1.0 "model" but they should not be
 * used for new topic maps, use {@link TMDM}.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 607 $ - $Date: 2011-01-20 02:28:15 +0100 (Do, 20 Jan 2011) $
 */
public final class XTM10 {

    private static final String _BASE = "http://www.topicmaps.org/xtm/1.0/core.xtm#";

    /**
     * The core concept of class-instance; the class of association that 
     * represents class-instance relationships between topics, and that is 
     * semantically equivalent to the use of <instanceOf> subelements.
     */
    public static final String CLASS_INSTANCE = _BASE + "class-instance";

    /**
     * The core concept of class; the role of class as played by one of the 
     * members of a class-instance association.
     */
    public static final String CLASS = _BASE + "class";

    /**
     * The core concept of instance; the role of instance as played by one of 
     * the members of a class-instance association.
     */
    public static final String INSTANCE = _BASE + "instance";

    /**
     * The core concept of superclass-subclass; the class of association that 
     * represents superclass-subclass relationships between topics.
     */
    public static final String SUPERCLASS_SUBCLASS = _BASE + "superclass-subclass";

    /**
     * The core concept of superclass; the role of superclass as played by one 
     * of the members of a superclass-subclass association.
     */
    public static final String SUPERCLASS = _BASE + "superclass";

    /**
     * The core concept of subclass; the role of subclass as played by one of 
     * the members of a superclass-subclass association.
     */
    public static final String SUBCLASS = _BASE + "subclass";

    /**
     * The core concept of association; the generic class to which all 
     * associations belong unless otherwise specified.
     */
    public static final String ASSOCIATION = _BASE + "association";

    /**
     * The core concept of occurrence; the generic class to which all 
     * occurrences belong unless otherwise specified.
     */
    public static final String OCCURRENCE = _BASE + "occurrence";

    /**
     * 
     */
    public static final String SORT = _BASE + "sort";

    /**
     * 
     */
    public static final String DISPLAY = _BASE + "display";

    /**
     * 
     */
    public static final String ROLE = "http://psi.semagia.com/xtm/1.0/role";
}

