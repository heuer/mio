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
package com.semagia.mio.jtm;

/**
 * Defines the known token types.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 577 $ - $Date: 2010-09-30 21:03:55 +0200 (Do, 30 Sep 2010) $
 */
final class JsonToken {

    /**
     * Token types.
     */
    public static final int
        START_OBJECT = 0,
        END_OBJECT = 1,
        START_ARRAY = 2,
        END_ARRAY = 4,
        VALUE_NULL = 5,
        
        KW_VERSION = 6,
        KW_ITEM_TYPE = 7,
        KW_TOPICS = 8,
        KW_ASSOCIATIONS  = 9,
        KW_ROLES = 10,
        KW_OCCURRENCES = 11,
        KW_NAMES = 12,
        KW_VARIANTS = 13,
        KW_SCOPE = 14,
        KW_TYPE = 15,
        KW_PLAYER = 16,
        KW_VALUE = 17,
        KW_DATATYPE = 18,
        KW_REIFIER = 19,
        KW_SIDS = 20,
        KW_SLOS = 21,
        KW_IIDS = 22,
        KW_PARENT = 23,
        KW_INSTANCE_OF = 24,
        KW_PREFIXES = 25,

        VALUE_STRING = 26,
        COLON = 27,
        COMMA = 28
        ;

    /**
     * Returns a string representation of the provided token type.
     *
     * @param type The token type.
     * @return A string representing the token.
     */
    public static String nameOf(final int type) {
        switch (type) {
            case START_OBJECT:      return "{";
            case END_OBJECT:        return "}";
            case START_ARRAY:       return "[";
            case END_ARRAY:         return "]";
            case COMMA:             return ",";
            case COLON:             return ":";
            case VALUE_NULL:        return "null";
            case VALUE_STRING:      return "<string>";
            case KW_VERSION:        return "version";
            case KW_ITEM_TYPE:      return "item_type";
            case KW_TOPICS:         return "topics";
            case KW_ASSOCIATIONS:   return "associations";
            case KW_ROLES:          return "roles";
            case KW_OCCURRENCES:    return "occurrences";
            case KW_NAMES:          return "names";
            case KW_VARIANTS:       return "variants";
            case KW_SCOPE:          return "scope";
            case KW_TYPE:           return "type";
            case KW_PLAYER:         return "player";
            case KW_VALUE:          return "value";
            case KW_DATATYPE:       return "datatype";
            case KW_REIFIER:        return "reifier";
            case KW_SIDS:           return "subject_identifiers";
            case KW_SLOS:           return "subject_locators";
            case KW_IIDS:           return "item_identifiers";
            case KW_PARENT:         return "parent";
            case KW_INSTANCE_OF:    return "instance_of";
            case KW_PREFIXES:       return "prefixes";
        }
        throw new IllegalArgumentException("Unknown token type:" + type);
    }
}
