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
package com.semagia.mio.ctm;

import org.tinytim.mio.TinyTimMapInputHandler;
import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;

import com.semagia.mio.IMapHandler;
import com.semagia.mio.MIOException;
import com.semagia.mio.ctm.CTMTemplate.IItem;

import junit.framework.TestCase;

/**
 * Tests against {@link CTMTemplate}.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public class TestCTMTemplate extends TestCase {

    private static final String _BASE = "http://test.semagia.com/map";
    private TopicMapSystem _sys;
    private TopicMap _tm;
    private Locator _baseLoc;

    @Override
    protected void setUp() throws Exception {
        _sys = TopicMapSystemFactory.newInstance().newTopicMapSystem();
        _baseLoc = _sys.createLocator(_BASE);
        _tm = _sys.createTopicMap(_baseLoc);
    }

    @Override
    protected void tearDown() throws Exception {
        _tm.close();
        _sys.close();
    }

    private static CTMTemplate.Builder builder() throws Exception {
        return CTMTemplate.builder(_BASE);
    }

    private static CTMTemplate.Builder builder(String name) throws Exception {
        return builder().name(name);
    }

    private static CTMTemplate buildEmpty(final String name) throws Exception {
        return build(name, "");
    }

    private static CTMTemplate buildEmpty() throws Exception {
        return build("");
    }

    private static CTMTemplate build(final String ctm) throws Exception {
        return builder().build(ctm);
    }

    private static CTMTemplate build(final String name, final String ctm) throws Exception {
        return builder(name).build(ctm);
    }

    private IMapHandler makeMapHandler() {
        return makeMapHandler(_tm);
    }

    private static IMapHandler makeMapHandler(final TopicMap tm) {
        return new TinyTimMapInputHandler(tm); 
    }

    public void testQNameInvalid() throws Exception {
        try {
            build("q:name isa qname.");
            fail("The prefix 'q' is not defined");
        }
        catch (MIOException ex) {
            // noop.
        }
    }

    public void testQNameValid() throws Exception {
        builder().addPrefix("q", "http://example.org/").build("q:name isa qname.");
    }

    public void testTemplateName() throws Exception {
        CTMTemplate tpl = buildEmpty("tpl");
        assertEquals("tpl", tpl.getName());
        tpl = buildEmpty();
        assertNotNull(tpl.getName());
        assertTrue(CTMUtils.isValidId(tpl.getName()));
    }

    public void testStatic() throws Exception {
        final CTMTemplate tpl = build("topic.");
        assertEquals(0, _tm.getTopics().size());
        final IMapHandler handler = makeMapHandler();
        tpl.execute(handler);
        assertEquals(1, _tm.getTopics().size());
        final Construct c = _tm.getConstructByItemIdentifier(_baseLoc.resolve("#topic"));
        assertNotNull(c);
        assertTrue(c instanceof Topic);
        tpl.execute(handler);
        assertEquals(1, _tm.getTopics().size());
    }

    public void testAnonymousWildcard() throws Exception {
        final CTMTemplate tpl = build("?. ?.");
        assertEquals(0, _tm.getTopics().size());
        final IMapHandler handler = makeMapHandler();
        tpl.execute(handler);
        assertEquals(2, _tm.getTopics().size());
        tpl.execute(handler);
        assertEquals(4, _tm.getTopics().size());
    }

    public void testWildcard() throws Exception {
        final CTMTemplate tpl = build("?x. ?y. ?x.");
        assertEquals(0, _tm.getTopics().size());
        final IMapHandler handler = makeMapHandler();
        tpl.execute(handler);
        assertEquals(2, _tm.getTopics().size());
        tpl.execute(handler);
        assertEquals(4, _tm.getTopics().size());
    }

    public void testVariableInvalid() throws Exception {
        final CTMTemplate tpl = build("$x isa $y.");
        assertEquals(2, tpl.getArity());
        final IMapHandler handler = makeMapHandler();
        try {
            tpl.execute(handler);
            fail("Expected an error because of missing params");
        }
        catch (Exception ex) {
            // noop.
        }

    }

    public void testVariable() throws Exception {
        final CTMTemplate tpl = build("$x isa $y.");
        assertEquals(2, tpl.getArity());
        final String instanceURI = "http://psi.example.org/instance";
        final String typeURI = "http://psi.example.org/type";
        final IItem itemInstance = CTMTemplate.createSubjectIdentifier(instanceURI);
        final IItem itemType = CTMTemplate.createSubjectIdentifier(typeURI);
        assertEquals(0, _tm.getTopics().size());
        final IMapHandler handler = makeMapHandler();
        tpl.execute(handler, itemInstance, itemType);
        assertEquals(2, _tm.getTopics().size());
        final Topic instance = _tm.getTopicBySubjectIdentifier(_tm.createLocator(instanceURI));
        assertNotNull(instance);
        assertEquals(1, instance.getSubjectIdentifiers().size());
        assertEquals(instanceURI, instance.getSubjectIdentifiers().iterator().next().getReference());
        final Topic type = _tm.getTopicBySubjectIdentifier(_tm.createLocator(typeURI));
        assertNotNull(type);
        assertEquals(1, type.getSubjectIdentifiers().size());
        assertEquals(typeURI, type.getSubjectIdentifiers().iterator().next().getReference());
    }

    public void testName() throws Exception {
        assertEquals(0, _tm.getTopics().size());
       final CTMTemplate tpl = build("$t - $val.");
       final IMapHandler handler = makeMapHandler();
       final String sid = "http://psi.example.org/topic";
       final String val = "Name value";
       tpl.execute(handler, CTMTemplate.createSubjectIdentifier(sid), CTMTemplate.createString(val));
       assertEquals(2, _tm.getTopics().size()); // two topics: topic and default name type topic!
       final Topic t = _tm.getTopicBySubjectIdentifier(_tm.createLocator(sid));
       assertNotNull(t);
       assertEquals(1, t.getNames().size());
       final Name name = t.getNames().iterator().next();
       assertEquals(val, name.getValue());
    }

}
