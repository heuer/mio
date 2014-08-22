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

import java.util.List;

import com.semagia.mio.MIOException;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 469 $ - $Date: 2010-09-08 12:39:06 +0200 (Mi, 08 Sep 2010) $
 */
final class MainSnelloContentHandler extends DelegatingContentHandler {

    private GlobalScopeSnelloContentHandler _globalContentHandler;

    MainSnelloContentHandler(GlobalParserEnvironment env) {
        _globalContentHandler = new GlobalScopeSnelloContentHandler(env);
        _contentHandler = _globalContentHandler;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.DelegatingContentHandler#startName(com.semagia.mio.stm.dm.SnelloRef)
     */
    @Override
    public void startName(SnelloRef type) throws MIOException {
        super.startName(type);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.DelegatingContentHandler#reifier(com.semagia.mio.stm.dm.SnelloRef)
     */
    @Override
    public void reifier(SnelloRef reifier) throws MIOException {
        if (reifier != null) {
            _contentHandler.reifier(reifier);
        }
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.DelegatingContentHandler#startTemplate(java.lang.String, java.util.List)
     */
    @Override
    public void startTemplate(String name, List<SnelloRef> args) throws MIOException {
        _contentHandler = new TemplateScopeSnelloContentHandler(_globalContentHandler.getParserEnvironment(), name, args);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.parser.DelegatingContentHandler#endTemplate()
     */
    @Override
    public void endTemplate() throws MIOException {
        TemplateScopeSnelloContentHandler tplScopeHandler = (TemplateScopeSnelloContentHandler) _contentHandler;
        _globalContentHandler.getParserEnvironment().getRuntime()
                                .registerTemplate(tplScopeHandler.getName(), 
                                                  tplScopeHandler.getTemplate());
        _contentHandler = _globalContentHandler;
    }

}
