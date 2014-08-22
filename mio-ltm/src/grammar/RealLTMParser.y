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
package com.semagia.mio.ltm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.semagia.mio.IRef;
import com.semagia.mio.MIOException;
import com.semagia.mio.helpers.Literal;
import com.semagia.mio.voc.XSD;

/**
 * This LTM parser utilizes the AbstractLTMParser and is responsible for 
 * the grammar.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 234 $ - $Date: 2010-05-13 09:43:29 +0200 (Do, 13 Mai 2010) $
 */
class RealLTMParser extends AbstractLTMParser { 

    private boolean _seenSLO;

    private void _makeVariant(String value, IRef theme) throws MIOException {
        _handler.startVariant();
        _handler.value(value, XSD.STRING);
        _handler.startScope();
        _handler.theme(theme);
        _handler.endScope();
        _handler.endVariant();
    }

%}

%token
    DIR_PREFIX
    DIR_BASEURI
    DIR_MERGEMAP
    DIR_INCLUDE
    DIR_VERSION
    DIR_TOPICMAP

    IDENT
    QNAME

    LBRACK
    RBRACK
    LCURLY
    RCURLY
    LPAREN
    RPAREN

    AT
    PERCENT
    EQ
    SEMI
    COLON
    COMMA
    TILDE
    SLASH

    STRING
    DATA

%type <String>
        QNAME, IDENT, STRING, DATA, sortname, displayname, opt_reifier

%type <List<String>>
        opt_sort_display

%type <IRef>
        tid, topic, opt_type

%type <Collection<IRef>> 
        opt_scope, themes, scope, tids

%type <Literal> 
        resource, string

%%

instance    : prolog 
            | tm
            | prolog tm
            ;

tm          : topic
            | association
            | occurrence
            | directive
            | tm topic
            | tm association
            | tm occurrence
            | tm directive
            ;

tid         : QNAME                         { $$ = _createTopicByQName($1); }
            | IDENT                         { $$ = _createTopic($1); }
            ;

prolog      : encoding_directive
            | version_directive
            | encoding_directive version_directive
            ;

version_directive
            : DIR_VERSION STRING            { _checkVersion($2); }
            ;

encoding_directive
            : AT STRING     // Ignored, handled by the deserializer
            ;

directive   : prefix_directive
            | baselocator_directive
            | topicmap_directive
            | mergemap_directive
            | include_directive
            ;

prefix_directive
            : DIR_PREFIX IDENT AT STRING        { _registerSubjectIdentifierPrefix($2, $4); }
            | DIR_PREFIX IDENT PERCENT STRING   { _registerSubjectLocatorPrefix($2, $4); }
            ;

baselocator_directive
            : DIR_BASEURI STRING            { _registerBaseLocator($2); }
            ;

topicmap_directive
            : DIR_TOPICMAP TILDE IDENT      { _topicMapReifier($3); }
            | DIR_TOPICMAP IDENT            { _topicMapItemIdentifier($2); }
            ;

mergemap_directive
            : DIR_MERGEMAP STRING           { _mergeInLTM($2); }
            | DIR_MERGEMAP STRING STRING    { _mergeIn($2, $3); }
            ;

include_directive
            : DIR_INCLUDE STRING            { _include($2); }
            ;

topic       : LBRACK tid { _handler.startTopic($2); _seenSLO = false; } opt_types opt_names opt_identities RBRACK
                {
                    _handler.endTopic();
                    $$=$2;
                }
            ;

opt_types   : 
            | COLON tids
                {
                    _handler.startIsa();
                    for (IRef type: $2) {
                        _handler.topicRef(type);
                    }
                    _handler.endIsa();
                }
            ;

opt_identities
            : 
            | identities
            ;

identities  : sid 
            | slo
            | identities sid
            | identities slo
            ;

sid         : AT STRING             { _handler.subjectIdentifier(_resolveIRI($2)); }
            ;

slo         : PERCENT STRING        { 
                                      if (_seenSLO && _legacy) { 
                                          throw new MIOException("Only one subject locator is allowed");
                                      }
                                      _handler.subjectLocator(_resolveIRI($2)); 
                                      _seenSLO = true;
                                    }
            ;

opt_names   : 
            | names
            ;

names       : name
            | names name
            ;

name        : EQ { _handler.startName(_TOPIC_NAME); } 
                string opt_sort_display opt_name_scope opt_reifier opt_variants 
                {
                    _handler.value($3.getValue());
                    _reifier($6);
                    List<String> vars = $4;
                    if (vars != null) {
                        if (vars.get(0) != null) {
                            _makeVariant(vars.get(0), _SORT_NAME);
                        }
                        if (vars.get(1) != null) {
                            _makeVariant(vars.get(1), _DISPLAY_NAME);
                        }
                    }
                    _handler.endName();
                }
            ;

opt_name_scope
            :
            | SLASH tids
                { 
                    _handler.startScope();
                    for(IRef ref: $2) {
                        _handler.theme(ref);
                    } 
                    _handler.endScope();
                }
            ;

tids        : tid
                { 
                    Collection<IRef> l = new ArrayList<IRef>();
                    l.add($1);
                    $$=l;
                }
            | tids tid                      { $1.add($2); $$=$1; }
            ;

opt_sort_display:                           { $$=null; }
            | sortname displayname
                { 
                    List<String> l = new ArrayList<String>(2);
                    l.add($1);
                    l.add($2);
                    $$=l;
                }
            | sortname
                { 
                    List<String> l = new ArrayList<String>(2);
                    l.add($1);
                    l.add(null);
                    $$=l;
                }
            | SEMI displayname
                { 
                    List<String> l = new ArrayList<String>(2);
                    l.add(null);
                    l.add($2);
                    $$=l;
                }
            ;

sortname    : SEMI string   { $$=$2.getValue(); }
            ;

displayname : SEMI string   { $$=$2.getValue(); }
            ;

opt_variants:
            | variants
            ;

variants    : variant
            | variants variant
            ;

variant     : LPAREN string SLASH tids opt_reifier RPAREN {
                _handler.startVariant();
                _handler.value($2.getValue(), XSD.STRING);
                _handler.startScope();
                for (IRef ref: $4) {
                    _handler.theme(ref);
                }
                _handler.endScope();
                _reifier($5);
                _handler.endVariant();
            }
            ;

association : tid LPAREN { _handler.startAssociation($1); } roles RPAREN opt_scope opt_reifier
                {
                    if ($6 != null) {
                        _handler.startScope();
                        for (IRef ref: $6) {
                            _handler.theme(ref);
                        }
                        _handler.endScope();
                    }
                    _reifier($7);
                    _handler.endAssociation();
                }
            ;

roles       : role
            | roles COMMA role
            ;

role        : { _handler.startRole(); _handler.startPlayer(); } player opt_type opt_reifier
                {
                    _handler.type($3);
                    _reifier($4);
                    _handler.endRole(); 
                }
            ;

player      : tid               { _handler.topicRef($1); _handler.endPlayer(); }
            | topic             { _handler.endPlayer(); }
            ;

opt_type    :                   { $$=_ROLE; }
            | COLON tid         { $$=$2; }
            ;

occurrence  : LCURLY tid COMMA tid COMMA resource RCURLY opt_scope opt_reifier
                {
                    _handler.startTopic($2);
                    _handler.startOccurrence($4);
                    Literal lit = $6;
                    _handler.value(lit.getValue(), lit.getDatatype());
                    if ($8 != null) {
                        _handler.startScope();
                        for (IRef ref: $8) {
                            _handler.theme(ref);
                        }
                        _handler.endScope();
                    }
                    _reifier($9);
                    _handler.endOccurrence();
                    _handler.endTopic();
                }
            ;

opt_scope   :                   { $$=null; }
            | scope             { $$=$1; }
            ;

scope       : SLASH themes      { $$=$2; }
            ;

themes      : tid                { Collection<IRef> tids = new ArrayList<IRef>(); tids.add($1); $$ = tids; }
            | themes tid         { $1.add($2); }
            | themes association { $$ = $1; }
            ;

opt_reifier :                   { $$ = null; }
            | TILDE IDENT       { $$ = $2; }
            ;

resource    : STRING            { $$=_createIRI($1); }
            | DATA              { $$=_createData($1); }
            ;

string      : STRING            { $$=_createString($1); }
            ;

%%
}
