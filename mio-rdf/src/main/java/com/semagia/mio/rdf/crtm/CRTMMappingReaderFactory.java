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
package com.semagia.mio.rdf.crtm;

import com.semagia.mio.Syntax;
import com.semagia.mio.rdf.api.IMappingReader;
import com.semagia.mio.rdf.api.IMappingReaderFactory;

/**
 * Factory for {@link IMappingReader}s reading
 * <a href="http://www.semagia.com/tr/crtm/">Compact RTM</a> syntax.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 568 $ - $Date: 2010-09-28 20:56:03 +0200 (Di, 28 Sep 2010) $
 */
public final class CRTMMappingReaderFactory implements IMappingReaderFactory {

    /* (non-Javadoc)
     * @see com.semagia.mio.rdf.api.IMappingReaderFactory#createReader()
     */
    @Override
    public IMappingReader createReader() {
        return new CRTMMappingReader();
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.rdf.api.IMappingReaderFactory#getSyntax()
     */
    @Override
    public Syntax getSyntax() {
        return Syntax.CRTM;
    }

}
