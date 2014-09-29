%{
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.semagia.mio.MIOParseException;

/**
 * CTM parser.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
  */
abstract class RealCTMParser extends AbstractCTMParser { 

    protected RealCTMParser() {
        super();
    }

    protected RealCTMParser(final IContentHandler contentHandler) {
        super(contentHandler);
    }

    /**
     * Returns a modifiable (!) list of refs
     */
    private static List<IReference> _makeList(IReference ... refs) {
        if (refs == null || refs.length == 0) {
            return new ArrayList<IReference>(1);
        }
        List<IReference> l = new ArrayList<IReference>(refs.length);
        for (IReference ref : refs) {
            l.add(ref);
        }
        return l;
    }

%}

%token<String>
        // Keywords
        KW_DEF, KW_END, KW_ISA, KW_AKO, 
        DIR_ENCODING, DIR_VERSION, DIR_PREFIX, DIR_INCLUDE, DIR_MERGEMAP

        // Identifiers
        IDENT, QNAME, VARIABLE, WILDCARD, NAMED_WILDCARD, 

        // Delimiters //

        // Brackets
        LPAREN, RPAREN
        LBRACK, RBRACK

        COMMA, SEMI, COLON, DOT, DOUBLE_CIRCUMFLEX, EQ, CIRCUMFLEX, TILDE, HYPHEN, AT

        // Datatypes
        STRING, IRI, INTEGER
        DECIMAL, DATE, DATE_TIME, STAR

%type <List<IReference>>
        opt_args, args, variables, tpl_call_or_assoc_start
        opt_parameters, opt_more_args

%type <IReference>
        ident, qiri, slo, iid, topic_ref_no_ident, topic_ref,
        opt_role_reifier,
        arg,
        variable, literal, literal_no_qname, string,
        name_value, name_start,
        embedded_topic, embedded_start

%%

instance    : prolog header topicmap
            | prolog header
            | prolog topicmap
            | prolog
            ;

topicmap    : topic                         {}
            | topicmap topic                {}
            | assoc                         {}
            | topicmap assoc                {}
            | topicmap directive            {}
            | tpl_def                       {}
            | topicmap tpl_def              {}
            | tpl_call                      {}
            | topicmap tpl_call             {}
            ;

prolog      : // empty
            | encoding_directive version_directive
            | encoding_directive
            | version_directive
            ;

header      : directive
            | directive topicmap_reifier
            | topicmap_reifier
            | header directive
            | header directive topicmap_reifier
            ;

encoding_directive
            : DIR_ENCODING STRING           { /* This was handled by the CTMDeserializer already */ }
            ;

version_directive
            : DIR_VERSION DECIMAL           { super.checkVersion($2); }
            ;

directive   : prefix_directive              {}
            | include_directive             {}
            | mergemap_directive            {}
            ;

prefix_directive
            : DIR_PREFIX IDENT qiri         { super.registerPrefix($2, $3.getString()); }
            ;

mergemap_directive
            : DIR_MERGEMAP qiri qiri        { super.mergeIn($2.getString(), $3.getString().intern()); }
            ;

include_directive
            : DIR_INCLUDE qiri              { super.include($2.getString()); }
            ;

topicmap_reifier
            : TILDE topic_ref               { if (!isSubordinate()) { _contentHandler.reifier($2); } else { _contentHandler.startTopic($2); _contentHandler.endTopic(); } }
            ;

ident       : IDENT                         { $$=_contentHandler.resolveIdentifier($1); }
            ;

topic_ref_no_ident
            : embedded_topic                { $$=$1; }
            | WILDCARD                      { IReference ref = _contentHandler.startTopic(); _contentHandler.endTopic(); $$=ref; }
            | NAMED_WILDCARD                { IReference ref = _contentHandler.startTopic($1); _contentHandler.endTopic(); $$=ref; }
            | iid                           { $$=$1; }
            | qiri                          { $$=$1; }
            | slo                           { $$=$1; }
            | variable                      { $$=$1; }

topic_ref   : topic_ref_no_ident            { $$=$1; }
            | ident                         { $$=$1; }
            ;

block_start : qiri                          { _contentHandler.startTopic($1); }
            | slo                           { _contentHandler.startTopic($1); }
            | iid                           { _contentHandler.startTopic($1); }
            | ident                         { _contentHandler.startTopic($1); }
            | variable                      { _contentHandler.startTopic($1); }
            | WILDCARD                      { _contentHandler.startTopic(); }
            | NAMED_WILDCARD                { _contentHandler.startTopic($1); }
            ;

qiri        : QNAME                         { $$=super.resolveQName($1); }
            | IRI                           { $$=super.resolveIRI($1); }
            ;

slo         : EQ qiri                       { $$= Reference.createSLO($2.getString()); }
            | EQ VARIABLE                   { $$= Reference.createVSLO($2); }
            ;

iid         : CIRCUMFLEX qiri               { $$= Reference.createIID($2.getString()); }
            | CIRCUMFLEX VARIABLE           { $$= Reference.createVIID($2); }
            ;

variable    : VARIABLE                      { $$= Reference.createVariable($1); }
            ;

topic       : block_start assignments eot   { _contentHandler.endTopic(); }
            | block_start eot               { _contentHandler.endTopic(); }
            ;

embedded_topic
            : embedded_start assignments opt_semi RBRACK { 
                _contentHandler.endTopic();
                $$=$1; 
            }
            ;

embedded_start
            : LBRACK { $$=_contentHandler.startTopic(); }
            ;

eot         : opt_semi DOT
            ;

opt_semi    : 
            | SEMI
            ;

assignments : assignment
            | assignments SEMI assignment
            ;

assignment  : name
            | occ
            | ctx_tpl_call
            | identity
            ;

identity    : qiri                          { _contentHandler.subjectIdentifier($1); }
            | variable                      { _contentHandler.identity($1); }
            | slo
                {
                    IReference ref = $1;
                    if (ref.isVSLO()) {
                        _contentHandler.subjectLocatorVariable(ref);
                    }
                    else {
                        _contentHandler.subjectLocator(ref);
                    }
                }
            | iid
                {
                    IReference ref = $1;
                    if (ref.isVIID()) {
                        _contentHandler.itemIdentifierVariable(ref);
                    }
                    else {
                        _contentHandler.itemIdentifier(ref);
                    }
                }
            ;

occ         : topic_ref { _contentHandler.startOccurrence($1); } COLON literal opt_scope opt_reifier
                {
                    _contentHandler.value($4);
                    _contentHandler.endOccurrence();
                }
            ;

name        : name_start COLON { _contentHandler.startName($1); } 
                name_value opt_scope opt_reifier opt_variants
                {
                    _contentHandler.nameValue($4);
                    _contentHandler.endName();
                }
            | name_start { _contentHandler.startName(Reference.TOPIC_NAME); } opt_scope opt_reifier opt_variants
                {
                    // Assuming "- $value ..." since
                    //          "- ident NO-COLON-HERE" is an error
                    final IReference value = $1;
                    if (!value.isVariable()) {
                        throw new MIOParseException("Incomplete name definition.", yyLex.getLine(), yyLex.getColumn());
                    }
                    _contentHandler.nameValue($1);
                    _contentHandler.endName();
                }
            | HYPHEN { _contentHandler.startName(Reference.TOPIC_NAME); }
                string opt_scope opt_reifier opt_variants
                {
                    _contentHandler.nameValue($3);
                    _contentHandler.endName();
                }
            ;

name_start  : HYPHEN topic_ref              { $$=$2; }
            ;

name_value  : variable                      { $$=$1; }
            | string                        { $$=$1; }
            ;

opt_variants
            :
            | variants
            ;

variants    : variant
            | variants variant
            ;

variant     : LPAREN { _contentHandler.startVariant(); } literal scope opt_reifier RPAREN
                {
                    _contentHandler.value($3);
                    _contentHandler.endVariant();
                }
            ;

assoc       : topic_ref_no_ident LPAREN { _contentHandler.startAssociation($1); } roles RPAREN opt_scope opt_reifier
                {
                    _contentHandler.endAssociation();
                }
            | tpl_call_or_assoc_start COLON 
                {
                  _contentHandler.startAssociation(_contentHandler.resolveIdentifier($1.get(0).getString())); 
                }
                topic_ref opt_role_reifier { _contentHandler.handleRole($1.get(1), $4, $5); } opt_more_roles RPAREN opt_scope opt_reifier {
                    _contentHandler.endAssociation();
                }
            ;

opt_more_roles
            :
            | COMMA roles
            ;

roles       : role
            | roles COMMA role
            ;

role        : topic_ref COLON topic_ref opt_role_reifier
                {
                    _contentHandler.handleRole($1, $3, $4);;
                }
            ;

opt_role_reifier 
            :                               { $$=null; }
            | TILDE topic_ref               { $$=$2; }
            ;

opt_scope   :
            | scope
            ;

scope       : AT { _contentHandler.startScope(); } themes { _contentHandler.endScope(); }
            ;

themes      : theme
            | themes COMMA theme
            ;

theme       : topic_ref                     { _contentHandler.theme($1); }
            ;

opt_reifier :
            | reifier
            ;

reifier     : TILDE topic_ref               { _contentHandler.reifier($2); }
            ;

opt_args    :                               { $$=new ArrayList<IReference>(2); }
            | args                          { $$=$1; }
            ;

args        : arg
                { 
                    final List<IReference> args = new ArrayList<IReference>(); 
                    args.add($1); 
                    $$=args; 
                }
            | args COMMA arg                { $1.add($3); $$=$1; }
            ;

arg         : topic_ref
            | literal_no_qname
            ;

tpl_def     : tpl_head KW_END               { _contentHandler.endTemplate(); }
            | tpl_head tpl_body KW_END      { _contentHandler.endTemplate(); }
            ;

tpl_head    : KW_DEF IDENT LPAREN opt_parameters RPAREN
                {
                    _contentHandler.startTemplate($2, $4);
                }
            ;

opt_parameters
            :                               { $$=Collections.<IReference>emptyList(); }
            | variables                     { $$=$1; }
            ;

variables   : variable
                {
                    final List<IReference> l = new ArrayList<IReference>();
                    l.add($1);
                    $$=l;
                }
            | variables COMMA variable      { $1.add($3); $$=$1; }
            ;

tpl_body    : topic                         {}
            | assoc                         {}
            | tpl_call                      {}
            | tpl_body topic                {}
            | tpl_body assoc                {}
            | tpl_body tpl_call             {}
            ;

ctx_tpl_call
            : IDENT LPAREN opt_args RPAREN {
                    _contentHandler.callTemplate($1, $3);
                }
            | KW_ISA topic_ref              { _contentHandler.isa($2); }
            | KW_AKO topic_ref              { _contentHandler.ako($2); }
            ;

tpl_call    : IDENT LPAREN RPAREN
                {
                    _contentHandler.callTemplate($1, Collections.<IReference>emptyList());
                }
            | tpl_call_or_assoc_start opt_more_args RPAREN
                {
                    List<IReference> args = $2;
                    if (args != null) {
                        args.add(0, $1.get(1));
                    }
                    else {
                        args = _makeList($1.get(1));
                    }
                    _contentHandler.callTemplate($1.get(0).getString(), args);
                }
            | IDENT LPAREN literal_no_qname opt_more_args RPAREN
                {
                    List<IReference> args = $4;
                    if (args == null) {
                        args = Collections.singletonList($3);
                    }
                    else {
                        args.add(0, $3);
                    }
                    _contentHandler.callTemplate($1, args);
                }
            ;

opt_more_args
            :                               { $$=null; }
            | COMMA args                    { $$=$2; }
            ;

literal_no_qname
            : string                        { $$=$1; }
            | STRING DOUBLE_CIRCUMFLEX qiri { $$=Reference.create($1, $3.getString()); }
            | DECIMAL                       { $$=Reference.createDecimal($1); }
            | INTEGER                       { $$=Reference.createInteger($1); }
            | DATE                          { $$=Reference.createDate($1); }
            | DATE_TIME                     { $$=Reference.createDateTime($1); }
            | STAR                          { $$=Reference.CTM_INTEGER; }
            ;

literal     : literal_no_qname              { $$=$1; }
            | qiri                          { $$=$1; }
            | variable                      { $$=$1; }
            ;

string      : STRING                        { $$=Reference.createString(super.unescapeString($1)); }
            ;

tpl_call_or_assoc_start
            : IDENT LPAREN topic_ref        { $$=_makeList(Reference.createIdent($1), $3); }
            ;

%%
}