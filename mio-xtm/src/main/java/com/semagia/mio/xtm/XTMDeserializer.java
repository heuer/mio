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
package com.semagia.mio.xtm;

/**
 * {@link com.semagia.mio.IDeserializer} implementation which is capable to
 * deserialize XML Topic Maps version 
 * <a href="http://www.topicmaps.org/xtm/1.0/">1.0</a>, version 
 * <a href="http://www.isotopicmaps.org/sam/sam-xtm/">2.0</a>, and version
 * <a href="http://www.itscj.ipsj.or.jp/sc34/open/1378.htm">2.1</a>.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
final class XTMDeserializer extends AbstractXTMDeserializer<IXTMContentHandler> {

    private String _version;

    public XTMDeserializer() {
        super(new XTMContentHandler());
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.xtm.AbstractXTMDeserializer#afterParse()
     */
    @Override
    protected void afterParse() {
        IXTMContentHandler contentHandler = null;
        try {
            contentHandler = (IXTMContentHandler) ((XTMContentHandler) _contentHandler).getContentHandler();
        }
        catch (ClassCastException ex) {
            // noop.
        }
        _version = (contentHandler instanceof XTM2ContentHandler) 
                        ? ((XTM2ContentHandler) contentHandler).getVersion()
                        : "1.0";
    }

    /* (non-Javadoc)
     * @see com.semagia.mio.IVersionAwareDeserializer#getVersion()
     */
    @Override
    public String getVersion() {
        if (_version == null) {
            throw new IllegalStateException("The version information is not available yet");
        }
        return _version;
    }

}
