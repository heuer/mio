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

import java.util.ArrayList;
import java.util.List;

import com.semagia.mio.MIOException;
import com.semagia.mio.helpers.SimpleMapHandler;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 469 $ - $Date: 2010-09-08 12:39:06 +0200 (Mi, 08 Sep 2010) $
 */
final class TemplateInvocation {

    private final static SnelloRef[] _NO_REFS = new SnelloRef[0];
    private final static String[] _NO_NAMES = new String[0];

    private final String _name;
    private ITemplate _tpl;
    private SnelloRef[] _args;
    private String[] _kwNames;
    private String[] _constNames;
    private SnelloRef[] _constValues;

    public TemplateInvocation(String name) {
        this(name, SnelloRef.NO_REFS);
    }

    public TemplateInvocation(String name, SnelloRef arg) {
        this(name, arg == null ? _NO_REFS : new SnelloRef[] {arg});
    }

    public TemplateInvocation(String name, List<SnelloRef> args) {
        this(name, args == null || args.size() == 0 ? _NO_REFS : args.toArray(new SnelloRef[args.size()]));
    }

    public TemplateInvocation(String name, SnelloRef[] args) {
        _name = name;
        _args = new SnelloRef[args.length];
        List<String> kwNames = new ArrayList<String>();
        for (int i=0; i<args.length; i++) {
            SnelloRef ref = args[i];
            if (ref.isKeyValue()) {
                KeyValue kv = ref.getKeyValue();
                kwNames.add(kv.getKey());
                _args[i] = kv.getValue();
            }
            else {
                _args[i] = ref;
            }
        }
        _kwNames = kwNames.toArray(new String[kwNames.size()]);
        _constNames = _NO_NAMES;
        _constValues = SnelloRef.NO_REFS;
    }

    public void execute(SnelloRuntime runtime) throws MIOException {
        if (_tpl == null) {
            _tpl = runtime.getTemplate(_name, _args.length);
            _init();
        }
        Context ctx = runtime.newContext();
        final SimpleMapHandler handler = ctx.getMapHandler();
        _initContext(ctx);
        runtime.pushContext(ctx);
        try {
            _tpl.execute(handler, ctx);
        }
        finally {
            runtime.popContext();
        }
    }

    /**
     * 
     *
     * @throws MIOException
     */
    private void _init() throws MIOException {
        String[] tplParams = _tpl.getParameterNames();
        SnelloRef[] defaults = _tpl.getDefaultValues();
        List<String> constArgs = new ArrayList<String>();
        List<SnelloRef> constValues = new ArrayList<SnelloRef>();
        final int defaultsOffset = tplParams.length - defaults.length;
        String param = null;
        SnelloRef value = null;
        final int offset = _args.length-_kwNames.length;
        for (int i=0; i<tplParams.length; i++) {
            param = tplParams[i];
            value = null;
            if (i >= defaultsOffset) {
                value = defaults[i-defaultsOffset];
            }
            for (int j=_kwNames.length-1; j>=0; j--) {
                if (param.equals(_kwNames[j])) {
                    value = _args[offset+j];
                    break;
                }
            }
            constArgs.add(param);
            if (value != null) {
                constValues.add(value);
            }
            else {
                constValues.add(_args[i]);
            }
        }
        _constNames = constArgs.toArray(new String[constArgs.size()]);
        _constValues = constValues.toArray(new SnelloRef[constValues.size()]);
        _args = null;
        _kwNames = null;
    }

    /**
     * 
     *
     * @param ctx
     * @throws MIOException
     */
    private void _initContext(Context ctx) throws MIOException {
        SnelloRef r = null;
        for (int i=0; i<_constNames.length; i++) {
            r = _constValues[i];
            while(r.isVariable() || r.isInternalWildcard() || r.isNamedWildcard()) {
                r = ctx.getRef(r.getString());
            }
            ctx.bind(_constNames[i], r);
        }
    }

}
