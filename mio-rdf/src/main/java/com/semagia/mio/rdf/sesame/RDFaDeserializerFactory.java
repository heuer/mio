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
package com.semagia.mio.rdf.sesame;

import com.semagia.mio.Syntax;

/**
 * {@link com.semagia.mio.IDeserializerFactory} to create
 * <a href="http://www.w3.org/TR/rdfa-syntax/">RDFa</a> deserializers.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 542 $ - $Date: 2010-09-17 15:36:05 +0200 (Fr, 17 Sep 2010) $
 */
public class RDFaDeserializerFactory extends AbstractRDFDeserializerFactory {

    public RDFaDeserializerFactory() {
        super(Syntax.RDFA);
    }

}
