%{
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

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import com.semagia.mio.MIOException;
import com.semagia.mio.voc.TMDM;

/**
 * Snello parser.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 445 $ - $Date: 2010-09-04 16:37:41 +0200 (Sa, 04 Sep 2010) $
 */
public class RealSnelloParser extends AbstractSnelloParser {

    private static final SnelloRef _TOPIC_NAME = SnelloRef.createSID(TMDM.TOPIC_NAME);

    private static List<SnelloRef> _makeList(SnelloRef ... refs) {
        if (refs == null || refs.length == 0) {
            return new ArrayList<SnelloRef>(0);
        }
        List<SnelloRef> l = new ArrayList<SnelloRef>(refs.length);
        for (SnelloRef ref : refs) {
            l.add(ref);
        }
        return l;
    }

    private static void _checkTemplateHead(String name, List<SnelloRef> args) throws MIOException {
        SnelloRef ref = null;
        List<String> args_ = new ArrayList<String>(args.size());
        String arg = null;
        for (int i=0; i<args.size(); i++) {
            ref = args.get(i);
            if (ref.isKeyValue()) {
                if (!ref.getKeyValue().isVariable()) {
                    _reportSyntaxError("Only variable assignments are allowed in the template (" + name + ")");
                }
                SnelloRef val = ref.getKeyValue().getValue();
                if (val.isVariable() || val.isVSLO()) {
                    _reportSyntaxError("The value of an assignment must not be a variable");
                }
                arg = ref.getKeyValue().getKey();
            }
            else {
                if(!ref.isVariable()) {
                    _reportSyntaxError("Only variables are allowed in the template (" + name + ")");
                }
                else {
                    arg = ref.getString();
                }
            }
            if (args_.contains(arg)) {
                _reportSyntaxError("Duplicate argument '" + arg + "' in template '" + name + "'");
            }
            args_.add(arg);
        }
    }

    private static void _reportSyntaxError(String msg) throws MIOException {
        throw new MIOException(msg);
    }

%}

%token<String>
        // Keywords
        DIR_ENCODING
        DIR_STM
        DIR_PREFIX
        DIR_ALIAS

        // Identifiers
        IDENT
        QNAME
        VARIABLE

        // Delimiters //

        // Brackets
        LPAREN
        RPAREN

        COMMA
        COLON
        DOT
        DOUBLE_CIRCUMFLEX
        EQ
        APOS
        TILDE
        HYPHEN
        AT
        IMPLIES
        ASSIGN

        // Datatypes
        VSTRING
        STRING
        VIRI
        IRI
        INTEGER
        DECIMAL
        DATE
        DATE_TIME
        YEAR_MONTH

        EOD
        EOF


%type <List<SnelloRef>>
        kw_args, args,
        common_head_lparen_var, common_head_lparen_identity_or_var,
        ident_var, ident_identity, ident_ident,
        common_head_lparen, common_head

%type <SnelloRef>
        ident, tid, qiri, slo, block_start, kw_arg
        arg, opt_reifier, variable, literal, literal_no_qiri, string,
        name_value, name_start, assoc_type, alias,
        topic
        

%type <String>
        tpl_ref

%%

instance    : prolog header topicmap
            | prolog topicmap
            | prolog header
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
            | EOD                           {}
            | topicmap EOD                  {}
            ;

prolog      : // empty
            | encoding_directive stm_directive
            | encoding_directive
            | stm_directive
            ;

header      : directive
            | directive tm_reifier
            | tm_reifier
            | header directive
            | header directive tm_reifier
            ;

encoding_directive
            : DIR_ENCODING STRING           // Handled outside
            ;

stm_directive
            : DIR_STM DECIMAL               { _checkVersion($2); }
            ;

directive   : prefix_directive
            | alias_directive
            ;

prefix_directive
            : DIR_PREFIX IDENT qiri         { _contentHandler.addPrefix($2, $3.getString()); }
            ;

alias_directive
            : DIR_ALIAS IDENT alias          { _contentHandler.addAlias($2, $3); }
            ;

alias       : qiri                          { $$=SnelloRef.createSID($1.getString()); }
            | EQ qiri                       { $$=SnelloRef.createSLO($2.getString()); }
            ;

tm_reifier  : TILDE topic { _contentHandler.reifier($2); }
            ;

ident       : IDENT                         { $$=_resolveIdent($1); }
            ;

tid         : qiri                          { $$=$1; }
            | slo                           { $$=$1; }
            | ident                         { $$=$1; }
            | variable                      { $$=$1; }
            ;

slo         : EQ qiri                       { $$=SnelloRef.createSLO($2.getString()); }
            | EQ VARIABLE                   { $$=SnelloRef.createVSLO($2); }
            ;

qiri        : QNAME                         { $$=_resolveQName($1); }
            | IRI                           { $$=_resolveIRI($1); }
//            | VIRI                          { $$=SnelloRef.createVIRI($1); }
            ;

variable    : VARIABLE                      { $$=SnelloRef.createVariable($1); }

topic       : ident_var { 
                            _contentHandler.startTopic(_resolveIdent($1.get(0)));
                            _contentHandler.identity($1.get(1));
                        }
              opt_assignments eot
            | ident_var { 
                            _contentHandler.startTopic(_resolveIdent($1.get(0)));
                            _contentHandler.startOccurrence($1.get(1));
                        }
              occ_body opt_assignments eot 
            | ident_identity { 
                            _contentHandler.startTopic(_resolveIdent($1.get(0)));
                            SnelloRef ref = $1.get(1);
                            if (ref.isSID()) {
                                _contentHandler.subjectIdentifier(ref);
                            }
                            else if (ref.isSLO()) {
                                _contentHandler.subjectLocator(ref);
                            }
                            else if (ref.isVSLO()) {
                                _contentHandler.subjectLocatorVariable(ref);
                            }
//                            else if (ref.isIID()) {
//                                _contentHandler.itemIdentifier(ref);
//                            }
                            else {
                                System.out.println("Unknown identity" + ref);
                            }
                        }
              opt_assignments eot
            | ident_identity { 
                                _contentHandler.startTopic(_resolveIdent($1.get(0)));
                                _contentHandler.startOccurrence($1.get(1));
                             } 
              occ_body opt_assignments eot
            | ident_ident { 
                                _contentHandler.startTopic(_resolveIdent($1.get(0)));
                                _contentHandler.startOccurrence(_resolveIdent($1.get(1)));
                          }
              occ_body opt_assignments eot
            | ident_ident args { 
                            _contentHandler.startTopic(_resolveIdent($1.get(0)));
                            _contentHandler.callTemplate($1.get(1).getString(), $2);
                        }
              opt_assignments eot
            | ident_ident kw_args { 
                            _contentHandler.startTopic(_resolveIdent($1.get(0)));
                            _contentHandler.callTemplate($1.get(1).getString(), $2);
                        }
              opt_assignments eot
            | ident_ident LPAREN args RPAREN { 
                                    _contentHandler.startTopic(_resolveIdent($1.get(0)));
                                    _contentHandler.callTemplate($1.get(1).getString(), $3);
                                  }
              opt_assignments eot
            | ident_ident LPAREN kw_args RPAREN { 
                                    _contentHandler.startTopic(_resolveIdent($1.get(0)));
                                    _contentHandler.callTemplate($1.get(1).getString(), $3);
                                  }
              opt_assignments eot
            | ident { _contentHandler.startTopic($1); } eot
            | ident { _contentHandler.startTopic($1); } name opt_assignments eot
            | block_start { _contentHandler.startTopic($1); } opt_assignments eot
            ;

// occ body the type of the occ is already read
occ_body    : opt_scope COLON literal opt_reifier
                {
                    _contentHandler.value($3);
                    _contentHandler.reifier($4);
                    _contentHandler.endOccurrence();
                }
            ;

block_start : qiri                          { $$=$1; }
            | slo                           { $$=$1; }
            | variable                      { $$=$1; }
            ;

eot         : DOT                           { _contentHandler.endTopic(); }
            | EOD                           { _contentHandler.endTopic(); }
            ;

opt_assignments
            :
            | assignments 
            ;

assignments : name
            | occ
            | ctx_tpl_call
            | identity
            | assignments name
            | assignments occ
            | assignments ctx_tpl_call
            | assignments identity
            ;

identity    : qiri
                { 
                    _contentHandler.subjectIdentifier($1);
                }
            | variable
                {
                    _contentHandler.identity($1);
                }
            | slo
                {
                    SnelloRef ref = $1;
                    if (ref.isVSLO()) {
                        _contentHandler.subjectLocatorVariable(ref);
                    }
                    else {
                        _contentHandler.subjectLocator(ref);
                    }
                }
            ;

name        : name_start { _contentHandler.startName($1); } scope COLON name_value opt_reifier opt_variants
                {
                    _contentHandler.nameValue($5);
                    _contentHandler.reifier($6);
                    _contentHandler.endName();
                }
            | name_start { _contentHandler.startName($1); } COLON name_value opt_reifier opt_variants
                {
                    _contentHandler.nameValue($4);
                    _contentHandler.reifier($5);
                    _contentHandler.endName();
                }
            | HYPHEN { _contentHandler.startName(_TOPIC_NAME); } scope COLON name_value opt_reifier opt_variants
                {
                    _contentHandler.nameValue($5);
                    _contentHandler.reifier($6);
                    _contentHandler.endName();
                }
            | name_start { _contentHandler.startName(_TOPIC_NAME); } opt_reifier opt_variants
                {
                    SnelloRef value = $1;
                    if (!value.isVariable()) {
                        _reportSyntaxError("Incomplete name definition.");
                    }
                    _contentHandler.nameValue($1);
                    _contentHandler.reifier($3);
                    _contentHandler.endName();
                }
            | HYPHEN COLON { _contentHandler.startName(_TOPIC_NAME); } name_value opt_reifier opt_variants
                {
                    _contentHandler.nameValue($4);
                    _contentHandler.reifier($5);
                    _contentHandler.endName();
                }
            | HYPHEN { _contentHandler.startName(_TOPIC_NAME); } string opt_reifier opt_variants
                {
                    _contentHandler.nameValue($3);
                    _contentHandler.reifier($4);
                    _contentHandler.endName();
                }
            ;

name_start  : HYPHEN tid                    { $$=$2; }
            ;

name_value  : string                        { $$=$1; }
            | variable                      { $$=$1; }
            ;

opt_variants
            :
            | variants
            ;

variants    : variant
            | variants variant
            ;

variant     : LPAREN {_contentHandler.startVariant(); } scope COLON literal opt_reifier RPAREN
                {
                    _contentHandler.value($5);
                    _contentHandler.endVariant();
                }
            ;

occ         : tid { _contentHandler.startOccurrence($1); } opt_scope COLON literal opt_reifier
                {

                    _contentHandler.endOccurrence();
                }
            ;

assoc       : assoc_type { _contentHandler.startAssociation($1); } 
                opt_scope LPAREN roles RPAREN opt_reifier
                {
                    _contentHandler.reifier($7);
                    _contentHandler.endAssociation();
                }
            | ident { _contentHandler.startAssociation($1); } scope LPAREN roles RPAREN opt_reifier
                {
                    _contentHandler.reifier($7);
                    _contentHandler.endAssociation();
                }
            | common_head_lparen_identity_or_var COLON tid opt_reifier 
                    { 
                        List<SnelloRef> head=$1;
                        _contentHandler.startAssociation(_resolveIdent(head.get(0).getString()));
                        _contentHandler.handleRole(head.get(1), $3, $4); 
                    } opt_more_roles RPAREN opt_reifier
                {
                    _contentHandler.reifier($8);
                    _contentHandler.endAssociation();
                }
            ;

assoc_type  : variable                      { $$=$1; }
            | slo                           { $$=$1; }
            | qiri                          { $$=$1; }
            ;

opt_more_roles
            :
            | COMMA roles
            ;

roles       : role
            | roles COMMA role
            ;

role        : tid COLON tid opt_reifier
                {
                    _contentHandler.handleRole($1, $3, $4);
                }
            ;

opt_scope   :
            | scope
            ;

scope       : AT { _contentHandler.startScope(); } themes  { _contentHandler.endScope(); }
            ;

themes      : tid                           { _contentHandler.theme($1); }
            | themes tid                    { _contentHandler.theme($2); }
            ;

opt_reifier :                               { $$=null; }
            | TILDE tid                     { $$=$2; } 
            ;

kw_args     : kw_arg
                { 
                    List<SnelloRef> args = new ArrayList<SnelloRef>();
                    args.add($1); 
                    $$=args; 
                }
            | kw_args COMMA kw_arg
                {
                    SnelloRef kw = $3;
                    Collection<SnelloRef> args = $1;
                    if (args.contains(kw)) {
                        _reportSyntaxError("The keyword argument '" + kw.getKeyValue().getKey() + "' was defined more than once");
                    }
                    args.add(kw);
                    $$=args;
                }
            ;

kw_arg      : IDENT ASSIGN arg              { $$=SnelloRef.createKeyValue($1, $3); }
            | VARIABLE ASSIGN arg           { $$=SnelloRef.createKeyValue($1, $3, true); }
            ;

args        : arg
                { 
                    List<SnelloRef> args = new ArrayList<SnelloRef>(); 
                    args.add($1); 
                    $$=args; 
                }
            | args COMMA arg                { $1.add($3); $$=$1; }
            | args COMMA kw_args            { $1.addAll($3); }
            ;

arg         : slo                           { $$=$1; }
            | ident                         { $$=$1; }
            | literal                       { $$=$1; }
            ;

tpl_def     : tpl_head tpl_body DOT         { _contentHandler.endTemplate(); }
            | tpl_head DOT                  { _contentHandler.endTemplate(); }
            ;

tpl_head    : IDENT IMPLIES
                { 
                    _contentHandler.startTemplate($1, Collections.<SnelloRef>emptyList());
                }
            | ident_var IMPLIES
                { 
                    _contentHandler.startTemplate($1.get(0).getString(), Collections.singletonList($1.get(1))); 
                }
            | common_head IMPLIES
                {
                    String name = $1.get(0).getString();
                    if ($1.size()>1) {
                        List<SnelloRef> args = $1.subList(1, $1.size());
                        _checkTemplateHead(name, args);
                        _contentHandler.startTemplate(name, args);
                    }
                    else {
                        _contentHandler.startTemplate(name, Collections.<SnelloRef>emptyList());
                    }
                } 
            ;

tpl_body    : topic                         {}
            | assoc                         {}
            | tpl_call                      {}
            | prefix_directive              {}
            | alias_directive               {}
            | EOD
            | tpl_body topic                {}
            | tpl_body assoc                {}
            | tpl_body tpl_call             {}
            | tpl_body prefix_directive     {}
            | tpl_body alias_directive      {}
            | tpl_body EOD
            ;

tpl_ref     : IDENT                         { $$=$1; }
//            | QNAME                         { $$=$1; }
            ;

ctx_tpl_call
            : tpl_ref args                  { _contentHandler.callTemplate($1, $2); }
            | tpl_ref LPAREN args RPAREN    { _contentHandler.callTemplate($1, $3); }
            | tpl_ref kw_args               { _contentHandler.callTemplate($1, $2); }
            | tpl_ref LPAREN kw_args RPAREN { _contentHandler.callTemplate($1, $3); }
            ;

tpl_call    : IDENT literal_no_qiri COMMA args
                { 
                    $4.add(0, $2);
                    _contentHandler.callTemplate($1, $4); 
                }
            | ident_ident COMMA args
                {
                    SnelloRef ref = _resolveIdent($1.get(1).getString());
                    $3.add(0, ref);
                    _contentHandler.callTemplate($1.get(0).getString(), $3);
                }
            | ident_identity COMMA args 
                {
                    $3.add(0, $1.get(1));
                    _contentHandler.callTemplate($1.get(0).getString(), $3);
                }
            | common_head
                {
                    String name = $1.get(0).getString();
                    if ($1.size() > 1) {
                        _contentHandler.callTemplate(name, $1.subList(1, $1.size()));
                    }
                    else {
                        _contentHandler.callTemplate(name, _makeList(new SnelloRef[0]));
                    }
                }
            ;

literal_no_qiri
            : string                        { $$=$1; }
            | STRING DOUBLE_CIRCUMFLEX qiri { $$=SnelloRef.create($1, $3.getString()); }
            | DECIMAL                       { $$=SnelloRef.createDecimal($1); }
            | INTEGER                       { $$=SnelloRef.createInteger($1); }
            | DATE                          { $$=SnelloRef.createDate($1); }
            | DATE_TIME                     { $$=SnelloRef.createDateTime($1); }
            | YEAR_MONTH                    { $$=SnelloRef.createYearMonth($1); }
            ;

literal     : literal_no_qiri               { $$=$1; }
            | qiri                          { $$=$1; }
            | variable                      { $$=$1; }
            ;

string      : STRING                        { $$=SnelloRef.createString(_unescapeString($1)); }
            ;

ident_var   : IDENT variable                { $$=_makeList(SnelloRef.createIdent($1), $2); }
            ;

ident_identity
            : IDENT qiri                    { $$=_makeList(SnelloRef.createIdent($1), $2); }
            | IDENT slo                     { $$=_makeList(SnelloRef.createIdent($1), $2); }
            ;

ident_ident : IDENT IDENT                   { $$=_makeList(SnelloRef.createIdent($1), SnelloRef.createIdent($2)); }
            ;

common_head 
            : IDENT LPAREN RPAREN           { $$=_makeList(SnelloRef.createIdent($1)); }
            | ident_var COMMA args
                {
                    $1.addAll($3);
                    $$=$1;
                }
            | ident_var COMMA kw_args
                {
                    $1.addAll($3);
                    $$=$1;
                }
            | IDENT kw_args
                {
                    List<SnelloRef> l =_makeList(SnelloRef.createIdent($1));
                    l.addAll($2);
                    $$=l;
                }
            | IDENT LPAREN kw_args RPAREN
                {
                    List<SnelloRef> l = _makeList(SnelloRef.createIdent($1));
                    l.addAll($3);
                    $$=l;
                }
            | common_head_lparen RPAREN     { $$=$1; }
            | common_head_lparen COMMA kw_args RPAREN 
                {
                    $1.addAll($3);
                    $$=$1;
                }
            | common_head_lparen COMMA args RPAREN
                {
                    $1.addAll($3);
                    $$=$1;
                }
            ;

common_head_lparen
            : IDENT LPAREN string           { $$=_makeList(SnelloRef.createIdent($1), $3); }
            | IDENT LPAREN INTEGER          { $$=_makeList(SnelloRef.createIdent($1), SnelloRef.createInteger($3)); }
            | IDENT LPAREN DECIMAL          { $$=_makeList(SnelloRef.createIdent($1), SnelloRef.createDecimal($3)); }
            | IDENT LPAREN DATE             { $$=_makeList(SnelloRef.createIdent($1), SnelloRef.createDate($3)); }
            | IDENT LPAREN DATE_TIME        { $$=_makeList(SnelloRef.createIdent($1), SnelloRef.createDateTime($3)); }
            | common_head_lparen_identity_or_var { $$=$1; }
            ;

common_head_lparen_identity_or_var
            : IDENT LPAREN ident            { $$=_makeList(SnelloRef.createIdent($1), $3); }
            | IDENT LPAREN qiri             { $$=_makeList(SnelloRef.createIdent($1), $3); }
            | IDENT LPAREN slo              { $$=_makeList(SnelloRef.createIdent($1), $3); }
            | common_head_lparen_var        { $$=$1; }
            ;

common_head_lparen_var
            : IDENT LPAREN variable         { $$=_makeList(SnelloRef.createIdent($1), $3); }
            ;

%%
}