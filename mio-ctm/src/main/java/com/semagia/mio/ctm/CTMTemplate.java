/*
 * Copyright 2007 - 2014 Lars Heuer (heuer[at]semagia.com)
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
import com.semagia.mio.voc.XSD;

/**
 * EXPERIMENTAL: Helper class to execute CTM templates programmatically.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public final class CTMTemplate implements ITemplate {

    private final String _name;
    private final IParseContext _ctx;
    private final int _arity;

    private CTMTemplate(final String name, final int arity, final IParseContext ctx) {
        _name = name;
        _arity = arity;
        _ctx = ctx;
    }

    /**
     * Returns the name of the template.
     * 
     * @return The name, never {@code null}.
     */
    @Override
    public String getName() {
        return _name;
    }

    /**
     * Returns the number of parameters.
     * 
     * @return Number of parameters.
     */
    @Override
    public int getArity() {
        return _arity;
    }

    /**
     * Executes the template using the provided {@link IMapHandler} for events.
     * 
     * The template is executed between the {@link IMapHandler#startTopicMap()} and
     * {@link IMapHandler#endTopicMap()}.
     * 
     * @param handler The handler which should receive the MIO events.
     * @param args The template parameters.
     * @throws MIOException In case of an error.
     */
    public void execute(final IMapHandler handler, final List<? extends IItem> args) throws MIOException {
        execute(handler, args.toArray(new IItem[args.size()]));
    }

    /**
     * Executes the template using the provided {@link IMapHandler} for events.
     * 
     * The template is executed between the {@link IMapHandler#startTopicMap()} and
     * {@link IMapHandler#endTopicMap()}.
     * 
     * @param handler The handler which should receive the MIO events.
     * @param args The template parameters.
     * @throws MIOException In case of an error.
     */
    public void execute(final IMapHandler handler, final IItem... args) throws MIOException {
        if (args.length != _arity) {
            throw new MIOException("Expecting " + _arity + " argument" + (_arity == 1 ? "" : 's') + ", got " + args.length);
        }
        final List<IReference> tplArgs = new ArrayList<IReference>(args.length);
        for (final IItem arg: args) {
            switch (arg.getType()) {
                case ITEM_IDENTIFIER: tplArgs.add(Reference.createIID(arg.getIRI())); break;
                case SUBJECT_IDENTIFIER: tplArgs.add(Reference.createIRI(arg.getIRI())); break;
                case SUBJECT_LOCATOR: tplArgs.add(Reference.createSLO(arg.getIRI())); break;
                case LITERAL: tplArgs.add(arg.getIRI().equals(XSD.ANY_URI) ? Reference.createIRI(((ILiteral) arg).getValue()): Reference.create(((ILiteral) arg).getValue(), arg.getIRI())); break;
            }
        }
        final TemplateInvocation invocation = new TemplateInvocation(_name, tplArgs);
        _ctx.setMapHandler(handler);
        handler.startTopicMap();
        invocation.execute(_ctx);
        handler.endTopicMap();
    }

    /**
     * Returns a {@link Builder} where the template uses the provided {@code baseIRI} to
     * resolve topic identifiers.
     * 
     * The returned builder supports registering of new prefixes via {@link Builder#addPrefix(String, String)}.
     * 
     * @param baseIRI An absolute IRI to resolve topic identifiers against.
     * @return A {@link Builder} instance.
     */
    public static Builder builder(final String baseIRI) {
        return builder(baseIRI, null);
    }

    /**
     * Returns a {@link Builder} where the template uses the provided {@code baseIRI} to
     * resolve topic identifiers and a read-only context to resolve QNames.
     * 
     * Adding new prefixes via {@link Builder#addPrefix(String, String)} is not possible.
     * 
     * @param baseIRI An absolute IRI to resolve topic identifiers against.
     * @param lookup Context to generate topic identifiers from wildcards and to resolve QNames.
     * @return A {@link Builder} instance.
     */
    public static Builder builder(final String baseIRI, final ITopicLookup lookup) {
        final Builder builder = new Builder(baseIRI, lookup);
        return builder;
    }

    /**
     * Creates a reference to a topic indicated by the provided item
     * identifier.
     * 
     * @param iri An absolute IRI.
     * @return A topic reference.
     */
    public static IItem createItemIdentifier(final String iri) {
        return new Item(IItem.ItemType.ITEM_IDENTIFIER, iri);
    }

    /**
     * Creates a reference to a topic indicated by the provided subject
     * identifier.
     * 
     * @param iri An absolute IRI.
     * @return A topic reference.
     */
    public static IItem createSubjectIdentifier(final String iri) {
        return new Item(IItem.ItemType.SUBJECT_IDENTIFIER, iri);
    }

    /**
     * Creates a reference to a topic indicated by the provided subject
     * locator.
     * 
     * @param iri An absolute IRI.
     * @return A topic reference.
     */
    public static IItem createSubjectLocator(final String iri) {
        return new Item(IItem.ItemType.SUBJECT_LOCATOR, iri);
    }

    /**
     * Creates a literal with datatype xsd:string with the provided value.
     * 
     * @param val The string value.
     * @return A literal.
     */
    public static ILiteral createString(final String val) {
        return createLiteral(val, XSD.STRING);
    }

    /**
     * Creates a literal with datatype xsd:date with the provided value.
     * 
     * @param val The date value.
     * @return A literal.
     */
    public static ILiteral createDate(final String val) {
        return createLiteral(val, XSD.DATE);
    }

    /**
     * Creates a literal with datatype xsd:dateTime with the provided value.
     * 
     * @param val The date/time value.
     * @return A literal.
     */
    public static ILiteral createDateTime(final String val) {
        return createLiteral(val, XSD.DATE_TIME);
    }

    /**
     * Creates a literal with datatype xsd:integer with the provided value.
     * 
     * @param val The integer value.
     * @return A literal.
     */
    public static ILiteral createInteger(final String val) {
        return createLiteral(val, XSD.INTEGER);
    }

    /**
     * Creates a literal with datatype xsd:decimal with the provided value.
     * 
     * @param val The decimal value.
     * @return A literal.
     */
    public static ILiteral createDecimal(final String val) {
        return createLiteral(val, XSD.DECIMAL);
    }

    /**
     * Creates a literal with datatype xsd:anyURI with the provided IRI.
     * 
     * @param iri An absolute IRI.
     * @return A literal.
     */
    public static ILiteral createIRI(final String iri) {
        return createLiteral(iri, XSD.ANY_URI);
    }

    /**
     * Creates a literal with the specified value and datatype.
     * 
     * @param val The string representation.
     * @param datatype The datatype, an IRI.
     * @return A literal.
     */
    public static ILiteral createLiteral(final String val, final String datatype) {
        return new Literal(val, datatype);
    }

    /**
     * Represents a topic reference or a literal.
     */
    public static interface IItem {

        public static enum ItemType {
            SUBJECT_IDENTIFIER,
            SUBJECT_LOCATOR,
            ITEM_IDENTIFIER,
            LITERAL
        }

        /**
         * Returns the type of the item.
         * 
         * @return The type of the item.
         */
        ItemType getType();
    
        /**
         * Returns an absolute, externalized IRI.
         * 
         * @return A string representing an IRI.
         */
        String getIRI();
    }

    /**
     * Represents a literal.
     */
    public static interface ILiteral extends IItem {

        /**
         * Returns the literal's value.
         * 
         * @return The literal's value, never {@code null}.
         */
        String getValue();

        /**
         * Returns the literal's datatype.
         * 
         * @return A string representing an IRI.
         */
        String getIRI();
    }

    /**
     * Internal default implementation of IItem.
     */
    private static class Item implements IItem {

        private final ItemType _type;
        private final String _iri;

        Item(final ItemType type, final String iri) {
            _type = type;
            _iri = iri;
        }

        @Override
        public ItemType getType() {
            return _type;
        }

        @Override
        public String getIRI() {
            return _iri;
        }
        
    }

    /**
     * Default internal implementation of ILiteral.
     */
    private static final class Literal extends Item implements ILiteral {

        private final String _value;

        Literal(final String value, final String iri) {
            super(IItem.ItemType.LITERAL, iri);
            _value = value;
        }

        @Override
        public String getValue() {
            return _value;
        }
        
    }

    /**
     * Builder to create {@link CTMTemplate} instances.
     */
    public static class Builder {

        private final IParseContext _ctx;
        private String _name;

        private Builder(final String baseIRI, final ITopicLookup lookup) {
            if (baseIRI == null) {
                throw new IllegalArgumentException("The base IRI must not be null");
            }
            _ctx = lookup != null ? new ParseContextAdapter(lookup) : new ParseContext();
            _ctx.setDocumentIRI(Locator.create(baseIRI));
            _name = "tpl-" + UUID.randomUUID().toString();
        }

        /**
         * Assigns an optional template name.
         * 
         * If this method isn't called, a random template name will be used.
         * 
         * @param name The name, not {@code null} (the name does not have to be 
         *              a valid CTM template name, any string works).
         * @return The builder instance.
         */
        public Builder name(final String name) {
            if (name == null) {
                throw new IllegalArgumentException("The template name must not be null");
            }
            _name = name;
            return this;
        }

        /**
         * Adds a prefix/IRI pair to the context which is used to resolve QNames.
         * 
         * This operation fails iff a {@link ITopicLookup} was provided to create the builder
         * instance.
         * 
         * @param prefix A string like "xsd".
         * @param iri An IRI like "http://www.w3.org/2001/XMLSchema#"
         * @return The builder instance.
         * @throws MIOException In case the prefix was bound to another IRI or if the underlying
         *          context is read-only (i.e. if a {@link ITopicLookup} was provided to create
         *          the builder).
         */
        public Builder addPrefix(final String prefix, final String iri) throws MIOException {
            _ctx.registerPrefix(prefix, iri);
            return this;
        }

        /**
         * Parses the provided string as CTM source and returns a {@link CTMTemplate}.
         * 
         * The provided source must be conform to 
         * <a href="http://www.isotopicmaps.org/ctm/ctm.html#template-body">template-body</a>. 
         * 
         * @param ctm A string containing CTM syntax.
         * @return A {@link CTMTemplate} instance.
         * @throws MIOException In case of an error.
         */
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
            final ITemplate tpl = contentHandler.dispose();
            _ctx.registerTemplate(tpl);
            return new CTMTemplate(_name, tpl.getArity(), _ctx);
        }
    }

}
