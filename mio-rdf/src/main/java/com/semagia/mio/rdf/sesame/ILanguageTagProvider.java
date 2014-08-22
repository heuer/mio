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
package com.semagia.mio.rdf.sesame;

import com.semagia.mio.IRef;

/**
 * Implementations of this class return a PSI for the RDF language tag and can
 * be used to add the translated language to the scope of a name or occurrence.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 473 $ - $Date: 2010-09-08 13:36:04 +0200 (Mi, 08 Sep 2010) $
 */
interface ILanguageTagProvider {

    /**
     * Returns the PSI for the provided language.
     *
     * @param lang The RDF language tag or {@code null}.
     * @return Either a IRef instance for the provided {@code lang} or 
     *          {@code null} if the {@code lang} is {@code null} or if the 
     *          provider cannot map the language to a PSI.
     */
    public IRef getLanguage(String lang);
}
