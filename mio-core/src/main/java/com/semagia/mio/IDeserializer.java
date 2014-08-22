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
package com.semagia.mio;

import java.io.IOException;

/**
 * A deserializer is capable to read a syntax and creates events for
 * Topic Maps constructs. 
 * <p>
 * A deserializer instance is not guaranteed to be thread-safe.
 * </p>
 * <p>
 * A deserializer instance should not be reused; once it has parsed a serialized
 * topic map, it should be thrown away.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 607 $ - $Date: 2011-01-20 02:28:15 +0100 (Do, 20 Jan 2011) $
 */
public interface IDeserializer extends IPropertyAware {

    /**
     * 
     *
     * @param src
     * @throws IOException
     * @throws MIOException
     */
    public void parse(Source src) throws IOException, MIOException;

    /**
     * 
     *
     * @param handler
     */
    public void setMapHandler(IMapHandler handler);

    /**
     * Indicates if this deserializer is used by another deserializer.
     * <p>
     * If a deserializer is a subordinate it MUST NOT call 
     * {@link IMapHandler#startTopicMap()} and 
     * {@link IMapHandler#endTopicMap()}.
     * </p>
     * <p>
     * By default, every deserializer assumes that it is not a subordinate.
     * </p>
     *
     * @param subordinate <tt>true</tt> to indicate that this deserializer
     *          is a subordinate, otherwise <tt>false</tt>.
     */
    public void setSubordinate(boolean subordinate);

    /**
     * 
     *
     * @param ctx
     */
    public void setContext(Context ctx);

    /**
     * 
     *
     * @return
     */
    public Context getContext();

}
