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
package com.semagia.mio.ltm;

import com.semagia.mio.IDeserializer;
import com.semagia.mio.IDeserializerFactory;
import com.semagia.mio.Syntax;

/**
 * {@link IDeserializerFactory} for deserializer which handle the 
 * <a href="http://www.ontopia.net/download/ltm.html">Linear Topic Maps (LTM)</a> 
 * syntax.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 467 $ - $Date: 2010-09-08 12:17:40 +0200 (Mi, 08 Sep 2010) $
 */
public final class LTMDeserializerFactory implements IDeserializerFactory {

    /* (non-Javadoc)
     * @see com.semagia.mio.IDeserializerFactory#getDeserializer()
     */
    @Override
    public IDeserializer createDeserializer() {
        return new LTMDeserializer();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IDeserializerFactory#getSyntax()
     */
    @Override
    public Syntax getSyntax() {
        return Syntax.LTM;
    }

}
