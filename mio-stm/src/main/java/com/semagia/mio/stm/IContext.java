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
import com.semagia.mio.MIOException;
import com.semagia.mio.helpers.SimpleMapHandler;
import com.semagia.mio.helpers.Literal;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 469 $ - $Date: 2010-09-08 12:39:06 +0200 (Mi, 08 Sep 2010) $
 */
interface IContext extends ITemplateLib {

    public static final SnelloRef TOPIC_IN_FOCUS = SnelloRef.createVariable("http://www.semagia.com/this-isa-impossible-variable-name");

    public SimpleMapHandler getMapHandler();

    public IRef getTopicReference(SnelloRef ref) throws MIOException;

    public IRef getTopicReference(String name) throws MIOException;

    public Literal getLiteral(String name) throws MIOException;

    public Literal getLiteral(SnelloRef ref) throws MIOException;

    public String getLocator(String name) throws MIOException;

}
