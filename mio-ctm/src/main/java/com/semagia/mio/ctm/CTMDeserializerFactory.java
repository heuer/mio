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
package com.semagia.mio.ctm;

import com.semagia.mio.IDeserializer;
import com.semagia.mio.IDeserializerFactory;
import com.semagia.mio.Syntax;

/**
 * {@link IDeserializerFactory} to create {@link IDeserializer}s which are
 * able to read <a href="http://www.isotopicmaps.org/ctm/">ISO/IEC 13250-6 - Compact Topic Maps (CTM) 1.0</a>.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 603 $ - $Date: 2011-01-18 23:04:34 +0100 (Di, 18 Jan 2011) $
 */
public final class CTMDeserializerFactory implements IDeserializerFactory {

    public CTMDeserializerFactory() {
        // noop.
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IDeserializerFactory#createDeserializer()
     */
    @Override
    public IDeserializer createDeserializer() {
        return new CTMDeserializer();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IDeserializerFactory#getSyntax()
     */
    @Override
    public Syntax getSyntax() {
        return Syntax.CTM;
    }

}
