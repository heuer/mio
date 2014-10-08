/*
 * Copyright 2007 - 2014 Lars Heuer (heuer[at]semagia.com)
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

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
class ParseContext extends AbstractParseContext {

    private int _counter;

    ParseContext() {
        super();
        _counter = 0;
    }

    @Override
    public String makeNextWildcardId(final String name) {
        _counter++;
        final StringBuffer sb = new StringBuffer("$__").append(_counter);
        if (name != null) {
            sb.append('.');
            sb.append(name);
        }
        return sb.toString();
    }

}
