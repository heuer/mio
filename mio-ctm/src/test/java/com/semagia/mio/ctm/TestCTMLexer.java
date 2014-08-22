/*
 * Copyright 2007 - 2011 Lars Heuer (heuer[at]semagia.com)
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

import com.semagia.mio.MIOException;

import junit.framework.TestCase;

/**
 * Tests against {@link CTMLexer}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 610 $ - $Date: 2011-04-04 18:09:15 +0200 (Mo, 04 Apr 2011) $
 */
public class TestCTMLexer extends TestCase {

    private CTMLexer _lexer(String input) throws IOException {
        return new CTMLexer(new StringReader(input));
    }

    private void _lex(String input, int[] expected) throws IOException, MIOException {
        CTMLexer lexer = _lexer(input);
        int i = 0;
        while (lexer.advance()) {
            int result = lexer.token();
            int expect = expected[i];
            assertEquals("Expected: <" + TokenTypes.name(expect) + "> got: <" + TokenTypes.name(result) + ">", 
                        expect, result);
            i++;
        }
        assertEquals("Unexpected token length", expected.length, i);
    }

    public void testDate() throws Exception {
        String input = "2010-01-17 -2010-01-17 0001-01-02 0011-01-02 0111-01-02 201111-01-02";
        int[] expected = new int[] {
                TokenTypes.DATE,
                TokenTypes.DATE,
                TokenTypes.DATE,
                TokenTypes.DATE,
                TokenTypes.DATE,
                TokenTypes.DATE,
                };
        _lex(input, expected);
    }

    public void testLexing() throws Exception {
        String input = "topic o1: 0001-12-23;  o2: 0011-12-23;.";
        int[] expected = new int[] {
                TokenTypes.IDENT,
                TokenTypes.IDENT,
                TokenTypes.COLON,
                TokenTypes.DATE,
                TokenTypes.SEMI,
                TokenTypes.IDENT,
                TokenTypes.COLON,
                TokenTypes.DATE,
                TokenTypes.SEMI,
                TokenTypes.DOT,
                };
        _lex(input, expected);
    }

    public void testLexing2() throws Exception {
        String input = "#\n#\n#\nident.";
        int[] expected = new int[] {
                TokenTypes.IDENT,
                TokenTypes.DOT,
                };
        _lex(input, expected);
    }

}
