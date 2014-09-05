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
package com.semagia.mio.xtm;

import com.semagia.mio.IDeserializer;
import com.semagia.mio.IDeserializerFactory;
import com.semagia.mio.Syntax;

/**
 * Factory to create deserializers which are capable to read XTM 1.0 topic maps.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public final class XTM10DeserializerFactory implements IDeserializerFactory {

    public XTM10DeserializerFactory() {
        // noop
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IDeserializerFactory#createDeserializer()
     */
    @Override
    public IDeserializer createDeserializer() {
        return new XTM10Deserializer();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IDeserializerFactory#getSyntax()
     */
    @Override
    public Syntax getSyntax() {
        return Syntax.XTM_10;
    }

}
