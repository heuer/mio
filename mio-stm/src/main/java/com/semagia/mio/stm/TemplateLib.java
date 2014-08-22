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

import java.util.HashMap;
import java.util.Map;

import com.semagia.mio.MIOException;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 469 $ - $Date: 2010-09-08 12:39:06 +0200 (Mi, 08 Sep 2010) $
 */
final class TemplateLib implements ITemplateLib {

    private Map<String, ITemplate> _templates;

    public TemplateLib() {
        _templates = new HashMap<String, ITemplate>();
    }
    
    /* (non-Javadoc)
     * @see com.semagia.mio.stm.dm.ITemplateLib#getTemplate(java.lang.String)
     */
    @Override
    public ITemplate getTemplate(final String name, final int arity) throws MIOException {
        final String key = name + "/" + arity;
        final ITemplate tpl = _templates.get(key);
        if (tpl == null) {
            _reportError("A template '" + key + "' is not defined");
        }
        return tpl;
    }

    @Override
    public Iterable<String> getTemplateNames() {
        return _templates.keySet();
    }

    /**
     * 
     *
     * @param name
     * @param template
     * @throws MIOException
     */
    @Override
    public void registerTemplate(String name, ITemplate template) throws MIOException {
        final String key = name + "/" + (template.getParameterNames().length - template.getDefaultValues().length);
        ITemplate existing = _templates.put(key, template);
        if (existing != null) {
            _reportError("A template with the name '" + key + "' exists already");
        }
    }

    /**
     * 
     *
     * @param msg
     * @throws MIOException
     */
    protected static final void _reportError(String msg) throws MIOException {
        throw new MIOException(msg);
    }
}
