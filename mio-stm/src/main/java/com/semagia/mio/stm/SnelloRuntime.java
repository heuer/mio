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

import com.semagia.mio.IMapHandler;
import com.semagia.mio.MIOException;
import com.semagia.mio.helpers.SimpleMapHandler;
import com.semagia.mio.helpers.Locator;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 469 $ - $Date: 2010-09-08 12:39:06 +0200 (Mi, 08 Sep 2010) $
 */
final class SnelloRuntime implements ITemplateLib {

    private TemplateLib _localTemplateLib;
    private Map<String, ITemplateLib> _templateLibs;
    private Context _context;
    private SimpleMapHandler _mapHandler;
    private Locator _docIRI;
    private long _counter;

    public SnelloRuntime() {
        _localTemplateLib = new TemplateLib();
        try {
            _localTemplateLib.registerTemplate("isa", new TemplateISA());
            _localTemplateLib.registerTemplate("ako", new TemplateAKO());
        }
        catch (MIOException ex) {
            // Should not happen
        }
        _templateLibs = new HashMap<String, ITemplateLib>();
        _context = new Context(this);
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.stm.dm.ITemplateLib#getTemplate(java.lang.String)
     */
    @Override
    public ITemplate getTemplate(final String name, int arity) throws MIOException {
        return _localTemplateLib.getTemplate(name, arity);
    }

    @Override
    public Iterable<String> getTemplateNames() {
        return _localTemplateLib.getTemplateNames();
    }

    public ITemplate getTemplate(String iri, String name, int arity) throws MIOException {
        ITemplateLib lib = _templateLibs.get(iri);
        
        return lib.getTemplate(name, arity);
    }

    public void registerTemplate(String name, ITemplate template) throws MIOException {
        _localTemplateLib.registerTemplate(name, template);
    }

    public void registerTemplateLib(String iri, ITemplateLib lib) throws MIOException {
        ITemplateLib existing = _templateLibs.put(iri, lib);
        if (existing != null) {
            _reportError("...");
        }
    }

    Context newContext() {
        return new Context(this, _context);
    }

    public Context currentContext() {
        return _context;
    }

    void pushContext(Context ctx) {
        _context = ctx;
    }

    void popContext() {
        _context = _context.parent();
    }

    protected static final void _reportError(String msg) throws MIOException {
        throw new MIOException(msg);
    }

    public void setMapHandler(IMapHandler handler) {
        _mapHandler = SimpleMapHandler.create(handler);
    }
    
    public SimpleMapHandler getMapHandler() {
        return _mapHandler;
    }

    public void setDocumentIRI(Locator docIRI) {
        _docIRI = docIRI;
    }

    public SnelloRef nextTopicId() {
        _counter++;
        return resolveIdentifier("$__" + _counter);
    }

    public SnelloRef nextTopicId(String name) {
        _counter++;
        return resolveIdentifier("$__" + _counter + "." + name);
    }

    public SnelloRef resolveIdentifier(String ident) {
        return SnelloRef.createIID(_docIRI.resolve("#" + ident).getReference());
    }

}
