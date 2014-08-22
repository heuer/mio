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

import java.io.IOException;
import java.io.StringReader;

import com.semagia.mio.MIOException;
import com.semagia.mio.ltm.TokenTypes;

import junit.framework.TestCase;

/**
 * Tests against the LTM lexer.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 467 $ - $Date: 2010-09-08 12:17:40 +0200 (Mi, 08 Sep 2010) $
 */
public class TestLTMLexer extends TestCase {

    private LTMLexer _lexer(String input) throws IOException {
        StringReader reader = new StringReader(input);
        return new LTMLexer(reader);
    }

    private void _lex(String input, int[] expected) throws IOException, MIOException {
        LTMLexer lexer = _lexer(input);
        int i = 0;
        while (lexer.advance()) {
            int result = lexer.token();
            int expect = expected[i];
            assertEquals(expect, result);
            i++;
        }
        assertEquals(expected.length, i);
    }

    public void testDirectives() throws Exception {
        String input = "#PREFIX #BASEURI #INCLUDE #MERGEMAP #TOPICMAP #VERSION";
        int[] expected = new int[] {
                TokenTypes.DIR_PREFIX,
                TokenTypes.DIR_BASEURI,
                TokenTypes.DIR_INCLUDE,
                TokenTypes.DIR_MERGEMAP,
                TokenTypes.DIR_TOPICMAP,
                TokenTypes.DIR_VERSION
                };
        _lex(input, expected);
    }

    public void testLexing1() throws Exception {
        String input = "#PREFIX ident @\"http://psi.semagia.com/\"";
        int[] expected = new int[] {
                TokenTypes.DIR_PREFIX,
                TokenTypes.IDENT,
                TokenTypes.AT,
                TokenTypes.STRING
                };
        _lex(input, expected);
    }

    public void testLexing2() throws Exception {
        String input = "[Semagi.-_a]";
        int[] expected = new int[] {
                TokenTypes.LBRACK,
                TokenTypes.IDENT,
                TokenTypes.RBRACK
                };
        _lex(input, expected);
    }

    public void testLexing3() throws Exception {
        String input = "[semagia = \"Semagia\"]";
        int[] expected = new int[] {
                TokenTypes.LBRACK,
                TokenTypes.IDENT,
                TokenTypes.EQ,
                TokenTypes.STRING,
                TokenTypes.RBRACK
                };
        _lex(input, expected);
    }

    public void testLexing4() throws Exception {
        String input = "format-for(ltm : format, topic-maps : standard)";
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

    public void testData1() throws Exception {
        String input = "{simple, statement, [[hello]world]]}";
        int[] expected = new int[] {
                TokenTypes.LCURLY,
                TokenTypes.IDENT,
                TokenTypes.COMMA,
                TokenTypes.IDENT,
                TokenTypes.COMMA,
                TokenTypes.DATA,
                TokenTypes.RCURLY
                };
        _lex(input, expected);
    }

    public void testData2() throws Exception {
        String input = "{simple, statement, [[] ]]}";
        int[] expected = new int[] {
                TokenTypes.LCURLY,
                TokenTypes.IDENT,
                TokenTypes.COMMA,
                TokenTypes.IDENT,
                TokenTypes.COMMA,
                TokenTypes.DATA,
                TokenTypes.RCURLY
                };
        _lex(input, expected);
    }

    public void testDataStrip() throws Exception {
        String input = "[[] ]]";
        LTMLexer lexer = _lexer(input);
        assertTrue(lexer.advance());
        assertEquals(TokenTypes.DATA, lexer.token());
        assertEquals("] ", lexer.value());
        assertFalse(lexer.advance());
    }

    public void testStringStrip() throws Exception {
        String input = "\"Semagia\"";
        LTMLexer lexer = _lexer(input);
        assertTrue(lexer.advance());
        assertEquals(TokenTypes.STRING, lexer.token());
        assertEquals("Semagia", lexer.value());
        assertFalse(lexer.advance());
    }

    public void testStringStuffing() throws Exception {
        String input = "\"Se\"\"magia\"";
        LTMLexer lexer = _lexer(input);
        assertTrue(lexer.advance());
        assertEquals(TokenTypes.STRING, lexer.token());
        assertEquals("Se\"\"magia", lexer.value());
        assertFalse(lexer.advance());
    }
}
