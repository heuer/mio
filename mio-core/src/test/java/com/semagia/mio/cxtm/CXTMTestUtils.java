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
package com.semagia.mio.cxtm;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Utility functions to find CXTM tests.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 607 $ - $Date: 2011-01-20 02:28:15 +0100 (Do, 20 Jan 2011) $
 */
public final class CXTMTestUtils {

    private CXTMTestUtils() {
        // noop.
    }

    public static Collection<Object> findCXTMTests(String extension, String... dirs) {
        return findCXTMTests(extension, Arrays.asList(dirs), "in", "baseline", false, Collections.<String>emptyList());
    }

    public static Collection<Object> findCXTMTestsAndConvertToTMDM(String extension, String... dirs) {
        return findCXTMTests(extension, Arrays.asList(dirs), "in", "baseline", true, Collections.<String>emptyList());
    }

    @SuppressWarnings("boxing")
    private static Collection<Object> findCXTMTests(String extension, Collection<String> dirs, 
                                                    String inputDir, String baseDir, 
                                                    boolean convertToTMDM, Collection<String> exclude) {
        Collection<Object> result = new ArrayList<Object>();
        for (String directory: dirs) {
            URL url = CXTMTestUtils.class.getResource(directory);
            File dir;
            try {
                dir = new File(url.toURI().resolve(inputDir));
            }
            catch (URISyntaxException ex) {
                throw new RuntimeException(ex);
            }
            for (File file: dir.listFiles()) {
                if (!file.getName().endsWith("." + extension) || exclude.contains(file.getName())) {
                    continue;
                }
                result.add(new Object[] {file, inputDir, baseDir, convertToTMDM});
            }
        }
        return result;
    }

    public static Collection<Object> findInvalidCXTMTests(String extension, String... dirs) {
        return findInvalidCXTMTests(extension, Arrays.asList(dirs), "invalid");
    }

    private static Collection<Object> findInvalidCXTMTests(String extension, Collection<String> dirs, String inputDir) {
        Collection<Object> result = new ArrayList<Object>();
        for (String directory: dirs) {
            URL url = CXTMTestUtils.class.getResource(directory);
            File dir;
            try {
                dir = new File(url.toURI().resolve(inputDir));
            }
            catch (URISyntaxException ex) {
                throw new RuntimeException(ex);
            }
            for (File file: dir.listFiles()) {
                if (!file.getName().endsWith(extension)) {
                    continue;
                }
                result.add(new Object[] {file});
            }
        }
        return result;
    }

    public static final class Filter {

        private final String[] _dirs;
        private String[] _exclude;
        private String _extension;
        private String _inputDir = "in";
        private String _baseDir = "baseline";
        private boolean _convertToTMDM;

        private Filter(String[] dirs) {
            _dirs = dirs;
            _exclude = new String[0];
        }

        public static Filter from(String...dirs) {
            return new Filter(dirs);
        }

        public Filter using(String extension) {
            _extension = extension;
            return this;
        }

        public Filter convertToTMDM() {
            _convertToTMDM = true;
            return this;
        }

        public Filter exclude(String...files) {
            _exclude = files;
            return this;
        }

        public Collection<Object> filter() {
            return CXTMTestUtils.findCXTMTests(_extension, Arrays.asList(_dirs), _inputDir, _baseDir, 
                                            _convertToTMDM, Arrays.asList(_exclude));
        }
    }
}
