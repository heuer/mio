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
package com.semagia.mio.rdf.api;

import com.semagia.mio.Syntax;

/**
 * Factory for {@link IMappingReader} instances.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 554 $ - $Date: 2010-09-26 21:18:02 +0200 (So, 26 Sep 2010) $
 */
public interface IMappingReaderFactory {

    /**
     * Returns a reader.
     *
     * @return A reader instance which supports {@link #getSyntax()}.
     */
    public IMappingReader createReader();

    /**
     * Returns the syntax for which this factory creates {@link IMappingReader}s.
     *
     * @return A syntax instance.
     */
    public Syntax getSyntax();

}
