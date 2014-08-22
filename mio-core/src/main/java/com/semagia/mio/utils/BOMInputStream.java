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
package com.semagia.mio.utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Input stream which tries to detect the BOM. If the BOM was found, it is
 * dropped from the stream.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 607 $ - $Date: 2011-01-20 02:28:15 +0100 (Do, 20 Jan 2011) $
 */
public final class BOMInputStream extends InputStream {

    private final static int _BOM_SIZE = 4;

    private final InputStream _in;
    private final boolean _foundBOM;
    private final String _encoding;
    private byte[] _lead = new byte[_BOM_SIZE];
    private int _offset;

    public BOMInputStream(final InputStream in, final String defaultEncoding) throws IOException {
        _in = in;
        int read = in.read(_lead, 0, _BOM_SIZE);
        if (_lead[0] == (byte) 0xEF 
                && _lead[1] == (byte) 0xBB 
                && _lead[2] == (byte) 0xBF) {
            _encoding = "UTF-8";
            _offset = 3;
            _foundBOM = true;
        }
        else if (_lead[0] == (byte) 0xFE 
                    && _lead[1] == (byte) 0xFF) {
            _encoding = "UTF-16BE";
            _offset = 2;
            _foundBOM = true;
        } 
        else if (_lead[0] == (byte) 0xFF 
                    && _lead[1] == (byte) 0xFE) {
            _encoding = "UTF-16LE";
            _offset = 2;
            _foundBOM = true;
        } 
        else if (_lead[0] == (byte) 0x00 
                && _lead[1] == (byte) 0x00
                && _lead[2] == (byte) 0xFE 
                && _lead[3] == (byte) 0xFF) {
            _encoding = "UTF-32BE";
            _lead = null;
            _foundBOM = true;
        } 
        else if (_lead[0] == (byte) 0xFF 
                    && _lead[1] == (byte) 0xFE
                    && _lead[2] == (byte) 0x00 
                    && _lead[3] == (byte) 0x00) {
            _encoding = "UTF-32LE";
            _lead = null;
            _foundBOM = true;
        }
        else {
            if (read > -1) {
                if (read < _lead.length) {
                    byte[] tmp = new byte[read];
                    System.arraycopy(_lead, 0, tmp, 0, read);
                    _lead = tmp;
                }
            }
            else {
                _lead = new byte[0];
            }
            _foundBOM = false;
            _encoding = defaultEncoding;
        }
    }

    /**
     * Returns either the default encoding or the encoding read from the BOM.
     *
     * @return The encoding.
     */
    public String getEncoding() {
        return _encoding;
    }

    /**
     * Indicates if a BOM was found.
     *
     * @return <tt>true</tt> if a BOM was found, otherwise <tt>false</tt>.
     */
    public boolean foundBOM() {
        return _foundBOM;
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#read()
     */
    @Override
    public int read() throws IOException {
        if (_lead == null) {
            return _in.read();
        }
        int i = _lead[_offset++];
        if (_offset >= _lead.length) {
            _lead = null;
        }
        return i;
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#read(byte[])
     */
    @Override
    public int read(final byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#read(byte[], int, int)
     */
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        if (_lead == null) {
            return _in.read(b, off, len);
        }
        int result = Math.min(_lead.length-_offset, len);
        System.arraycopy(_lead, _offset, b, off, result);
        _offset += result;
        if(_offset >= _lead.length) {
            _lead = null;
        }
        if (result != -1 && result < len) {
            int tmp = read(b, off+result, len-result);
            return tmp == -1 ? result : result + tmp;
        }
        return result;
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#close()
     */
    @Override
    public void close() throws IOException {
        _in.close();
    }
}
