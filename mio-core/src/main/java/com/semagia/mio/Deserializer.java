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
package com.semagia.mio;

public final class Deserializer {

    private Deserializer() {
        // noop
    }

    public static IDeserializer forFilename(final String filename) {
        return forFilename(filename, null);
    }

    public static IDeserializer forFilename(final String filename, final Syntax defaultSyntax) {
        final Syntax syntax = Syntax.forFilename(filename, defaultSyntax);
        return DeserializerRegistry.getInstance().createDeserializer(syntax);
    }

    public static IDeserializer forFileExtension(final String ext) {
        return forFileExtension(ext, null);
    }

    public static IDeserializer forFileExtension(final String ext, final Syntax defaultSyntax) {
        return DeserializerRegistry.getInstance().createDeserializer(Syntax.forFileExtension(ext, defaultSyntax));
    }

}

