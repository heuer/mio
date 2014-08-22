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

/**
 * Factory to create {@link IDeserializer}s.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 607 $ - $Date: 2011-01-20 02:28:15 +0100 (Do, 20 Jan 2011) $
 */
public interface IDeserializerFactory {

    /**
     * Creates and returns a deserializer that is capable to parse the syntax
     * returned by {@link #getSyntax()}.
     *
     * @return A deserializer instance.
     */
    public IDeserializer createDeserializer();

    /**
     * Returns the syntax for which this factory creates {@link IDeserializer}s.
     *
     * @return A syntax instance.
     */
    public Syntax getSyntax();

}
