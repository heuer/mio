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
 * Constants for TMDM 1.0 (model) PSIs.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 607 $ - $Date: 2011-01-20 02:28:15 +0100 (Do, 20 Jan 2011) $
 */
public final class TMDM {

    private static final String _BASE = "http://psi.topicmaps.org/iso13250/model/";

    public static final String SUBJECT = _BASE + "subject";

    /**
     * Core concept of type-instance relationships. 
     * Used as association type.
     */
    public static final String TYPE_INSTANCE = _BASE + "type-instance";

    /**
     * Core concept of type within a type-instance relationship. 
     * Used as role type.
     */
    public static final String TYPE = _BASE + "type";

    /**
     * Core concept of instance within a type-instance relationship. 
     * Used as role type.
     */
    public static final String INSTANCE = _BASE + "instance";

    /**
     * Core concept of supertype-subtype relationship.
     * Used as association type.
     */
    public static final String SUPERTYPE_SUBTYPE = _BASE + "supertype-subtype";

    /**
     * Core concept of supertype within a supertype-subtype relationship.
     * Used as role type.
     */
    public static final String SUPERTYPE = _BASE + "supertype";

    /**
     * Core concept of subtype within a supertype-subtype relationship.
     * Used as role type.
     */
    public static final String SUBTYPE = _BASE + "subtype";

    /**
     * Core concept of a topic name.
     * Used as topic name type.
     */
    public static final String TOPIC_NAME = _BASE + "topic-name";

    /**
     * Used to indicate that a variant can be used for sorting purposes.
     * Used as variant theme.
     */
    public static final String SORT = _BASE + "sort";
}

