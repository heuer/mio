%{
/*
 * Copyright 2008 - 2010 Lars Heuer (heuer[at]semagia.com)
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
package com.semagia.mio.rdf.crtm;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

import com.semagia.mio.MIOException;

/**
 * This CRTM parser utilizes the AbstractCRTMParser and is responsible for 
 * the grammar.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 318 $ - $Date: 2010-07-16 23:08:40 +0200 (Fr, 16 Jul 2010) $
 */
abstract class RealCRTMParser extends AbstractCRTMParser { 

    private boolean _isName;
    private String _name;
    private List<String> _predicates;
    private String[] _scope;
    private String _type;
    private String _nextPredicate;
    private String _subjectRole;
    private String _objectRole;
    private Boolean _lang;

    protected RealCRTMParser(final String iri) {
        super(iri);
        _reset();
    }

    private void _processCharacteristic() throws MIOException {
        if (_isName) {
            for (String pred: _predicates) { 
                _handler.handleName(pred, _scope, _type, getLang2ScopeSetting(_lang));
            }
        }
        else {
            for (String pred: _predicates) { 
                _handler.handleOccurrence(pred, _scope, _type, getLang2ScopeSetting(_lang));
            }
        }
    }

    private void _processAssociation() throws MIOException {
        for (String pred: _predicates) { 
            _handler.handleAssociation(pred, _subjectRole, _objectRole, _scope, _type); 
        }
    }

    private void _addNextPredicate() {
        if (_nextPredicate != null) {
            _predicates.add(_nextPredicate);
        }
    }

    private void _reset() {
        _isName = false;
        _subjectRole = null;
        _objectRole = null;
        _nextPredicate = null;
        _lang = null;
        _type = null;
        _scope = null;
    }
%}

%token<String>
    // Directives
    DIR_PREFIX, DIR_INCLUDE, DIR_VERSION, DIR_LANG2SCOPE, 
    
    // Keywords
    KW_AKO, KW_ISA, KW_SID, KW_SLO, KW_IID, KW_ASSOC, KW_OCC, KW_NAME, KW_LANG, KW_TRUE, KW_FALSE,

    IDENT, QNAME, LOCAL_IDENT, IRI, 

    LPAREN, RPAREN, LCURLY, RCURLY, AT, EQ, COMMA, HYPHEN, SEMI, COLON

    EOF
    
%type <Boolean>
        bool

%type <String>
        qiri, local

%type <List<String>> 
        qiris, locals

%%

instance    : prolog body
            | body
            ;

body        : statement
            | scoped_statement
            | body statement
            | body scoped_statement
            | body prefix_directive
            | body EOF
            | EOF
            ;

prolog      : directive                     { }
            | prolog directive              { }
            ;

directive   : prefix_directive              { }
            | DIR_INCLUDE IRI               { include($2); }
            | DIR_LANG2SCOPE bool           { setConvertLanguageToScope($2); }
            ;

prefix_directive
            : DIR_PREFIX IDENT IRI          { registerPrefix($2, $3); }
            ;

statement   : qiris COLON { _predicates = $1; _addNextPredicate(); _reset(); } statement_body { }
            ;

statement_body
            : name
            | occurrence
            | isa
            | ako
            | identity
            | association
            ;

scoped_statement
            : IDENT { _name = $1; } LCURLY in_scope_statements RCURLY
            | IRI LCURLY { _name = super.registerAnonymousPrefix($1); } in_scope_statements RCURLY
            ;

in_scope_statements
            : in_scope_statement
            | in_scope_statements in_scope_statement
            ;

in_scope_statement
            : locals COLON { _predicates = $1; _addNextPredicate(); _reset(); } statement_body {}
            ;

local       : LOCAL_IDENT                   { $$=resolveQName(_name, $1); }
            | IDENT                         { $$=resolveQName(_name, $1); }
            ;

locals      : local                         { final List<String> l = new ArrayList<String>(); l.add($1); $$=l; }
            | locals COMMA local            { $1.add($3); $$=$1; }
            ;

identity    : KW_SID                        { for (String pred: _predicates) { _handler.handleSubjectIdentifier(pred); } }
            | KW_SLO                        { for (String pred: _predicates) { _handler.handleSubjectLocator(pred); } }
            | KW_IID                        { for (String pred: _predicates) { _handler.handleItemIdentifier(pred); } }
            ;

isa         : KW_ISA opt_scope              { for (String pred: _predicates) { _handler.handleInstanceOf(pred, _scope); } }
            ;

ako         : KW_AKO opt_scope              { for (String pred: _predicates) { _handler.handleSubtypeOf(pred, _scope); } }
            ;

name        : KW_NAME { _isName = true; } opt_char_body
            | HYPHEN  { _isName = true; } opt_char_body
            ;

opt_char_body
            : char_body
            |                               { _processCharacteristic(); }
            ;

char_body   : type scope opt_lang           { _processCharacteristic(); }
            | type opt_lang                 { _processCharacteristic(); }
            | scope opt_lang                { _processCharacteristic(); }
            | qiri COMMA                    { _processCharacteristic(); 
                                              _reset(); 
                                              _nextPredicate=$1; 
                                            }
            | qiri COLON { _processCharacteristic();
                           _reset(); 
                           _predicates = Collections.singletonList($1); 
                         } statement_body
            | IRI LCURLY { _processCharacteristic();
                           _reset(); 
                           _name = super.registerAnonymousPrefix($1);
                         } in_scope_statements RCURLY
            ;

occurrence  : KW_OCC opt_char_body
            | char_body
            ;

opt_lang    :                               { _lang=null; }
            | SEMI KW_LANG EQ bool          { _lang=$4; }
            ;

bool        : KW_TRUE                       { $$=Boolean.TRUE; }
            | KW_FALSE                      { $$=Boolean.FALSE; }
            ;

association : KW_ASSOC opt_type roles opt_scope { _processAssociation(); }
            | opt_type roles opt_scope          { _processAssociation(); }
            ;

roles       : LPAREN qiri COMMA qiri RPAREN { _subjectRole = $2; _objectRole = $4; }
            ;

opt_type    :                               { _type=null; }
            | type                          { }
            ;

type        : qiri                          { _type=$1; }
            ;

opt_scope   :                               { _scope=null; }
            | scope                         { }
            ;

scope       : AT qiris                      { _scope=$2.toArray(new String[0]); }
            ;

qiri        : QNAME                         { $$=resolveQName($1); }
            | IRI                           { $$=resolveIRI($1); }
            ;

qiris       : qiri                          { final List<String> l = new ArrayList<String>(); l.add($1); $$=l; }
            | qiris COMMA qiri              { $1.add($3); $$=$1; }
            ;
%%
}
