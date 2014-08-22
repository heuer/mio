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

import com.semagia.mio.IMapHandler;
import com.semagia.mio.MIOException;
import com.semagia.mio.helpers.Locator;

/**
 * 
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 469 $ - $Date: 2010-09-08 12:39:06 +0200 (Mi, 08 Sep 2010) $
 */
abstract class AbstractSnelloParser {

    protected ISnelloContentHandler _contentHandler;

    protected AbstractSnelloParser() {
        _contentHandler = new MainSnelloContentHandler(new GlobalParserEnvironment(new SnelloRuntime()));
    }

    /**
     * Sets the map handler.
     *
     * @param handler
     */
    public void setMapHandler(IMapHandler handler) {
        _contentHandler.getParserEnvironment().getRuntime().setMapHandler(handler);
    }

    public IMapHandler getMapHandler() {
        return _contentHandler.getParserEnvironment().getRuntime().getMapHandler();
    }

    public void setDocumentIRI(String iri) {
        ((GlobalParserEnvironment)_contentHandler.getParserEnvironment()).setDocumentIRI(Locator.create(iri));
        _registerPredefinedPrefixes();
    }

    protected void _registerPredefinedPrefixes() {
        try {
            _contentHandler.addPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
        }
        catch (MIOException ex) {
            // noop. Unlikely that the reg. throws an exception
        }
    }

    protected void _checkVersion(String version) throws MIOException {
        if (!"1.0".equals(version)) {
            _reportError("Unexpected version '" + version + "'");
        }
    }

    protected final SnelloRef _resolveIdent(SnelloRef ident) {
        return _resolveIdent(ident.getString());
    }

    protected final SnelloRef _resolveIdent(String ident) {
        return _contentHandler.getParserEnvironment().resolveIdentifier(ident);
    }

    protected final SnelloRef _resolveQName(String name) throws MIOException {
        return _contentHandler.getParserEnvironment().resolveQName(name);
    }

    protected final SnelloRef _resolveIRI(String iri) throws MIOException {
        return _contentHandler.getParserEnvironment().resolveIRI(iri);
    }

    protected final Locator _resolveLocator(String iri) throws MIOException {
        return _contentHandler.getParserEnvironment().resolveLocator(iri);
    }

    protected static void _reportError(String msg) throws MIOException {
        throw new MIOException(msg);
    }

    /**
     * 
     *
     * @param value
     * @return
     */
    protected String _unescapeString(String value) {
        int backSlash = value.indexOf('\\');
        if (backSlash == -1) {
            return value;
        }
        StringBuilder sb = new StringBuilder();
        int pos = 0;
        int length = value.length();
        char c;
        while (backSlash != -1) {
            sb.append(value.substring(pos, backSlash));
            if (backSlash + 1 >= length) {
                throw new IllegalArgumentException("Invalid escape syntax: " + value);
            }
            c = value.charAt(backSlash + 1);
            switch(c) {
            case 't':
            case 'r':
            case 'n': {
                sb.append('\\')
                  .append(c);
              pos = backSlash + 2;
              break;
            }
            case '"':
            case '\'': {
                sb.append(c);
                pos = backSlash + 2;
                break;
            }
            case 'u': {
                // \\uxxxx
                if (backSlash + 5 >= length) {
                    throw new IllegalArgumentException(
                            "Incomplete Unicode escape sequence in: " + value);
                }
                String xx = value.substring(backSlash + 2, backSlash + 6);
                try {
                    c = (char)Integer.parseInt(xx, 16);
                    sb.append(c);
                    pos = backSlash + 6;
                }
                catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                            "Illegal Unicode escape sequence '\\u" + xx + "' in: " + value);
                }
                break;
            }
            case 'U': {
                // \\Uxxxxxxxx
                if (backSlash + 9 >= length) {
                    throw new IllegalArgumentException(
                            "Incomplete Unicode escape sequence in: " + value);
                }
                String xx = value.substring(backSlash + 2, backSlash + 10);
                try {
                    c = (char)Integer.parseInt(xx, 16);
                    sb.append( c );
                    pos = backSlash + 10;
                }
                catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                            "Illegal Unicode escape sequence '\\U" + xx + "' in: " + value);
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("Unescaped backslash in: " + value);
            }
            }
            backSlash = value.indexOf('\\', pos);
        }
        sb.append(value.substring(pos));
        return sb.toString();
    }
}
