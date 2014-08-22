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
package com.semagia.mio.stm;

import com.semagia.mio.MIOException;

/**
 * Representation of a template library.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 469 $ - $Date: 2010-09-08 12:39:06 +0200 (Mi, 08 Sep 2010) $
 */
interface ITemplateLib {

    /**
     * Returns the template with the specified <tt>name</tt>.
     *
     * @param name The name of the template.
     * @param arity
     * @return A {@link ITemplate} implementation.
     * @throws MIOException If a template with the provided name does not exist.
     */
    public ITemplate getTemplate(String name, int arity) throws MIOException;

    public void registerTemplate(String name, ITemplate template) throws MIOException;

    public Iterable<String> getTemplateNames();

}
