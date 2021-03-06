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

import com.semagia.mio.MIOException;
import com.semagia.mio.helpers.SimpleMapHandler;

/**
 * Implementation of the "isa" template.
 * <p>
 * Creates a type-instance relationship between topics.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 469 $ - $Date: 2010-09-08 12:39:06 +0200 (Mi, 08 Sep 2010) $
 */
final class TemplateISA implements ITemplate {

    private static final String[] _PARAMS = new String[] {"instance", "type"};

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.dm.ITemplate#execute(com.semagia.mio.helpers.SimpleMapHandler, com.semagia.mio.stm.dm.IContext)
     */
    @Override
    public void execute(final SimpleMapHandler handler, final IContext ctx) throws MIOException {
        handler.isa(ctx.getTopicReference(_PARAMS[0]), ctx.getTopicReference(_PARAMS[1]));
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.dm.ITemplate#getArity()
     */
    @Override
    public int getArity() {
        return 2;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.dm.ITemplate#getParameterNames()
     */
    @Override
    public String[] getParameterNames() {
        return _PARAMS;
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.dm.ITemplate#getDefaultValues()
     */
    @Override
    public SnelloRef[] getDefaultValues() {
        return SnelloRef.NO_REFS;
    }

}
