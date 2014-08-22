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

import java.io.IOException;
import java.io.StringReader;

import com.semagia.mio.MIOException;

import junit.framework.TestCase;

/**
 * Tests against the {@link CRTMLexer}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 473 $ - $Date: 2010-09-08 13:36:04 +0200 (Mi, 08 Sep 2010) $
 */
public class TestCRTMLexer extends TestCase {

    private CRTMLexer _lexer(final String input) {
        final StringReader reader = new StringReader(input);
        return new CRTMLexer(reader);
    }

    private void _lex(final String input, int[] expected) throws IOException, MIOException {
        final CRTMLexer lexer = _lexer(input);
        int i = 0;
        while (lexer.advance()) {
            if (lexer.token() == TokenTypes.EOF) {
                break;
            }
            int result = lexer.token();
            int expect = expected[i];
            assertEquals("Expected: " + TokenTypes.name(expect) + ", got: " + TokenTypes.name(result), expect, result);
            i++;
        }
        assertEquals(expected.length, i);
    }

    public void testEOF() throws Exception {
        final String input = "";
        final int[] expected = new int[] { };
        _lex(input, expected);
    }

    public void testDirectives() throws Exception {
        String input = "%prefix %include %version";
        int[] expected = new int[] {
                TokenTypes.DIR_PREFIX,
                TokenTypes.DIR_INCLUDE,
                TokenTypes.DIR_VERSION
                };
        _lex(input, expected);
    }

    public void testQName() throws Exception {
        String input = "q:name q:123 foaf:name q:12name q:12.2";
        int[] expected = new int[] {
                TokenTypes.QNAME,
                TokenTypes.QNAME,
                TokenTypes.QNAME,
                TokenTypes.QNAME,
                TokenTypes.QNAME,
                };
        _lex(input, expected);
    }

    public void testIdentifier() throws Exception {
        String input = "hello.again _1 lang name true false occurrence occ assoc association hello.ag.ain";
        int[] expected = new int[] {
                TokenTypes.IDENT,
                TokenTypes.IDENT,
                TokenTypes.IDENT,
                TokenTypes.IDENT,
                TokenTypes.IDENT,
                TokenTypes.IDENT,
                TokenTypes.IDENT,
                TokenTypes.IDENT,
                TokenTypes.IDENT,
                TokenTypes.IDENT,
                TokenTypes.IDENT,
                };
        _lex(input, expected);
    }

    public void testLangBooleanKeyword() throws Exception {
        String input = "lang true false";
        int[] expected = new int[] {
                TokenTypes.IDENT,
                TokenTypes.IDENT,
                TokenTypes.IDENT,
                };
        _lex(input, expected);
        input = ";lang true false";
        expected = new int[] {
                TokenTypes.SEMI,
                TokenTypes.KW_LANG,
                TokenTypes.KW_TRUE,
                TokenTypes.IDENT,
                };
        _lex(input, expected);
        input = ";lang false false";
        expected = new int[] {
                TokenTypes.SEMI,
                TokenTypes.KW_LANG,
                TokenTypes.KW_FALSE,
                TokenTypes.IDENT,
                };
        _lex(input, expected);
        input = "%langtoscope true true";
        expected = new int[] {
                TokenTypes.DIR_LANG2SCOPE,
                TokenTypes.KW_TRUE,
                TokenTypes.IDENT,
                };
        _lex(input, expected);
    }


    public void testPrefixDirective() throws Exception {
        String input = "%prefix bla <http://psi.semagia.com/test>";
        int[] expected = new int[] {
                TokenTypes.DIR_PREFIX,
                TokenTypes.IDENT,
                TokenTypes.IRI,
                };
        _lex(input, expected);
    }

    public void testStatementName() throws Exception {
        String input = "foaf:name: name";
        int[] expected = new int[] {
                TokenTypes.QNAME,
                TokenTypes.COLON,
                TokenTypes.KW_NAME,
                };
        _lex(input, expected);
    }

    public void testStatementOccurrence() throws Exception {
        String input = "web:site: occurrence web:site: occ";
        int[] expected = new int[] {
                TokenTypes.QNAME,
                TokenTypes.COLON,
                TokenTypes.KW_OCC,
                TokenTypes.QNAME,
                TokenTypes.COLON,
                TokenTypes.KW_OCC,
                };
        _lex(input, expected);
    }

}
