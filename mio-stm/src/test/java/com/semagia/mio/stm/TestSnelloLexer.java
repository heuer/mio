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

import java.io.IOException;
import java.io.StringReader;

import com.semagia.mio.MIOException;

import junit.framework.TestCase;

/**
 * Tests against the Snello lexer.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 605 $ - $Date: 2011-01-19 00:10:28 +0100 (Mi, 19 Jan 2011) $
 */
public class TestSnelloLexer extends TestCase {

    private SnelloLexer _lexer(String input) throws IOException {
        StringReader reader = new StringReader(input);
        return new SnelloLexer(reader);
    }

    private void _lex(String input, int[] expected) throws IOException, MIOException {
        SnelloLexer lexer = _lexer(input);
        int i = 0;
        while (lexer.advance()) {
            int result = lexer.token();
            if (i == expected.length) {
                assertEquals(result, TokenTypes.EOD);
            }
            else {
                int expect = expected[i];
                assertEquals("Expected: <" + TokenTypes.name(expect) + "> got: <" + TokenTypes.name(result) + ">",
                        expect, result);
                
            }
            i++;
        }
        assertEquals("Unexpected token length", i-1, expected.length);
    }

    public void testDirectives() throws Exception {
        String input = "%prefix %stm %encoding %alias ";
        int[] expected = new int[] {
                TokenTypes.DIR_PREFIX,
                TokenTypes.DIR_STM,
                TokenTypes.DIR_ENCODING,
                TokenTypes.DIR_ALIAS,
                };
        _lex(input, expected);
    }

    public void testLexing1() throws Exception {
        String input = "%prefix ident http://psi.semagia.com/";
        int[] expected = new int[] {
                TokenTypes.DIR_PREFIX,
                TokenTypes.IDENT,
                TokenTypes.IRI
                };
        _lex(input, expected);
    }

    public void testLexingAlias() throws Exception {
        String input = "%alias ident http://psi.semagia.com/ident";
        int[] expected = new int[] {
                TokenTypes.DIR_ALIAS,
                TokenTypes.IDENT,
                TokenTypes.IRI
                };
        _lex(input, expected);
    }

    public void testLexing2() throws Exception {
        String input = "Semagi.-_a";
        int[] expected = new int[] {
                TokenTypes.IDENT,
                };
        _lex(input, expected);
    }

    public void testLexing3() throws Exception {
        String input = "semagia - \"Semagia\"";
        int[] expected = new int[] {
                TokenTypes.IDENT,
                TokenTypes.HYPHEN,
                TokenTypes.STRING,
                };
        _lex(input, expected);
    }

    public void testLexing4() throws Exception {
        String input = "format-for(format: stm, standard: topic-maps)";
        int[] expected = new int[] {
                TokenTypes.IDENT,
                TokenTypes.LPAREN,
                TokenTypes.IDENT,
                TokenTypes.COLON,
                TokenTypes.IDENT,
                TokenTypes.COMMA,
                TokenTypes.IDENT,
                TokenTypes.COLON,
                TokenTypes.IDENT,
                TokenTypes.RPAREN
                };
        _lex(input, expected);
    }

    public void testLexing5() throws Exception {
        String input = "http://www.example.org/something(format: stm, standard: topic-maps)";
        int[] expected = new int[] {
                TokenTypes.IRI,
                TokenTypes.LPAREN,
                TokenTypes.IDENT,
                TokenTypes.COLON,
                TokenTypes.IDENT,
                TokenTypes.COMMA,
                TokenTypes.IDENT,
                TokenTypes.COLON,
                TokenTypes.IDENT,
                TokenTypes.RPAREN
                };
        _lex(input, expected);
    }

    public void testStringStrip() throws Exception {
        String input = "\"Semagia\"";
        SnelloLexer lexer = _lexer(input);
        assertTrue(lexer.advance());
        assertEquals(TokenTypes.STRING, lexer.token());
        assertEquals("Semagia", lexer.value());
    }

    public void testStringStripTriple() throws Exception {
        String input = "\"\"\"They say \"It's the root of all evil\".\"\"\"";
        SnelloLexer lexer = _lexer(input);
        assertTrue(lexer.advance());
        assertEquals(TokenTypes.STRING, lexer.token());
        assertEquals("They say \"It's the root of all evil\".", lexer.value());
    }

    public void testVStringStrip() throws Exception {
        String input = "$\"Semagia\"";
        SnelloLexer lexer = _lexer(input);
        assertTrue(lexer.advance());
        assertEquals(TokenTypes.VSTRING, lexer.token());
        assertEquals("Semagia", lexer.value());
    }

    public void testVStringStripTriple() throws Exception {
        String input = "$\"\"\"They say \"It's the root of all evil\".\"\"\"";
        SnelloLexer lexer = _lexer(input);
        assertTrue(lexer.advance());
        assertEquals(TokenTypes.VSTRING, lexer.token());
        assertEquals("They say \"It's the root of all evil\".", lexer.value());
    }

    public void testVIRIStrip() throws Exception {
        String input = "$<http://www.semagia.com/>";
        SnelloLexer lexer = _lexer(input);
        assertTrue(lexer.advance());
        assertEquals(TokenTypes.VIRI, lexer.token());
        assertEquals("http://www.semagia.com/", lexer.value());
    }

    public void testIRIStrip() throws Exception {
        String input = "<http://www.semagia.com/>";
        SnelloLexer lexer = _lexer(input);
        assertTrue(lexer.advance());
        assertEquals(TokenTypes.IRI, lexer.token());
        assertEquals("http://www.semagia.com/", lexer.value());
    }

    public void testVariableStrip() throws Exception {
        String input = "$semagia";
        SnelloLexer lexer = _lexer(input);
        assertTrue(lexer.advance());
        assertEquals(TokenTypes.VARIABLE, lexer.token());
        assertEquals("semagia", lexer.value());
    }

    public void testIRIDetection() throws Exception {
        String[] iris = new String[] {
                "http://www.semagia.com/",
                "<http://www.semagia.com/>",
                "mailto:test@example.org"
        };
        int[] expected = new int[] { TokenTypes.IRI };
        for (String iri : iris) {
            _lex(iri, expected);
            _lex(" " + iri + "  ", expected);
        }
    }

    public void testVariable() throws Exception {
        String input = "$semagia $_12";
        int[] expected = new int[] {
                TokenTypes.VARIABLE,
                TokenTypes.VARIABLE,
                };
        _lex(input, expected);
    }

    public void testTemplate() throws Exception {
        String input = "has-sid($topic, $sid) :- " +
                        "$topic $sid . " +
                        ".";
        int[] expected = new int[] {
                TokenTypes.IDENT,
                TokenTypes.LPAREN,
                TokenTypes.VARIABLE,
                TokenTypes.COMMA,
                TokenTypes.VARIABLE,
                TokenTypes.RPAREN,
                TokenTypes.IMPLIES,
                TokenTypes.VARIABLE,
                TokenTypes.VARIABLE,
                TokenTypes.DOT,
                TokenTypes.DOT
        };
        _lex(input, expected);
    }

    public void testEOD() throws Exception {
        String input = "semagia\n\n";
        SnelloLexer lexer = _lexer(input);
        assertTrue(lexer.advance());
        assertEquals(TokenTypes.IDENT, lexer.token());
        assertTrue(lexer.advance());
        assertEquals(TokenTypes.EOD, lexer.token());
    }

    public void testDateDetection() throws Exception {
        final String input = "2008-09-19";
        SnelloLexer lexer = _lexer(input);
        assertTrue(lexer.advance());
        assertEquals(TokenTypes.DATE, lexer.token());
        assertEquals("2008-09-19", lexer.value());
    }

    public void testGYearMonthDetection() throws Exception {
        final String input = "2008-09";
        SnelloLexer lexer = _lexer(input);
        assertTrue(lexer.advance());
        assertEquals(TokenTypes.YEAR_MONTH, lexer.token());
        assertEquals("2008-09", lexer.value());
    }

    public void testDecimal() throws Exception {
        String[] input = {"1.2", "0.111", "0.0"};
        for (int i=0; i<input.length; i++) {
            SnelloLexer lexer = _lexer(input[i]);
            assertTrue(lexer.advance());
            assertEquals(TokenTypes.DECIMAL, lexer.token());
            assertEquals(input[i], lexer.value());
        }
    }

    public void testQName() throws Exception {
        String[] input = {"q:name", "q:12name", "q:.name"};
        for (int i=0; i<input.length; i++) {
            SnelloLexer lexer = _lexer(input[i]);
            assertTrue(lexer.advance());
            assertEquals(TokenTypes.QNAME, lexer.token());
            assertEquals(input[i], lexer.value());
        }
    }
}
