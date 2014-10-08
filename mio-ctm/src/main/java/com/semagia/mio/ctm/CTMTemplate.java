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

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.semagia.mio.IMapHandler;
import com.semagia.mio.MIOException;
import com.semagia.mio.ctm.TemplateScopeHandler.TemplateInvocation;
import com.semagia.mio.helpers.Locator;

/**
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public final class CTMTemplate {

    private final String _name;
    private final IParseContext _ctx;

    private CTMTemplate(final String name, final IParseContext env) {
        _name = name;
        _ctx = env;
    }

    public String getName() {
        return _name;
    }

    public void execute(IMapHandler handler, final List<?> args) throws MIOException {
        final List<IReference> tplArgs = new ArrayList<IReference>(args.size());
        final TemplateInvocation invocation = new TemplateInvocation(_name, tplArgs);
        _ctx.setMapHandler(handler);
        handler.startTopicMap();
        invocation.execute(_ctx);
        handler.endTopicMap();
    }

    public static Builder builder(final String baseIRI) {
        return builder(baseIRI, null);
    }

    public static Builder builder(final String baseIRI, final IReadOnlyTopicLookup lookup) {
        final Builder builder = new Builder(baseIRI, lookup);
        return builder;
    }


    public static class Builder {

        private IParseContext _ctx;
        private String _name;

        private Builder(final String baseIRI, final IReadOnlyTopicLookup lookup) {
            if (baseIRI == null) {
                throw new IllegalArgumentException("The base IRI must not be null");
            }
            _ctx = lookup != null ? new ParseContextAdapter(lookup) : new ParseContext();
            _ctx.setDocumentIRI(Locator.create(baseIRI));
            _name = "tpl-" + UUID.randomUUID().toString();
        }

        public Builder name(final String name) {
            if (name == null) {
                throw new IllegalArgumentException("The template name must not be null");
            }
            _name = name;
            return this;
        }

        public Builder addPrefix(final String prefix, final String iri) throws MIOException {
            _ctx.registerPrefix(prefix, iri);
            return this;
        }

        public CTMTemplate build(final String ctm) throws MIOException {
            final List<IReference> variables = new ArrayList<IReference>();
            for (final String name: CTMUtils.findVariables(ctm, true)) {
                variables.add(Reference.createVariable(name));
            }
            final TemplateScopeHandler contentHandler = new TemplateScopeHandler(_ctx, _name, variables);
            final CTMParser parser = new CTMParser(new MainContentHandler(contentHandler));
            try {
                parser.parse(new StringReader(ctm));
            }
            catch (IOException ex) {
                // Very unlikely
                throw new MIOException(ex.getMessage(), ex);
            }
            _ctx.registerTemplate(contentHandler.dispose());
            return new CTMTemplate(_name, _ctx);
        }
    }

}
