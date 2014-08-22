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

import com.semagia.mio.helpers.Ref;
import com.semagia.mio.voc.TMDM;
import com.semagia.mio.voc.XSD;

import junit.framework.TestCase;

/**
 * Tests against a IMapHandler implementation.
 * 
 * These tests are independent of a particular implementation. To test 
 * an implementation, create a derived class and implement the abstract 
 * methods.
 * Further, the implementations should provide access to a topic map which 
 * MUST be empty.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 * @version $Rev: 607 $ - $Date: 2011-01-20 02:28:15 +0100 (Do, 20 Jan 2011) $
 */
public abstract class AbstractMapHandlerTest extends TestCase {

    /**
     * Returns the IMapHandler instance.
     * This method is invoked by each test and should return a "fresh" 
     * instance each time.
     * 
     * Further, the underlying topic map must be empty after invoking this
     * method.
     */
    protected abstract IMapHandler makeMapHandler();

    /**
     * Returns the number of topics in the topic map.
     */
    protected abstract int getTopicSize();

    /**
     * Returns the number of topics in the topic map.
     */
    protected abstract int getAssociationSize();

    /**
     * Returns a topic with the provided subject identifier.
     */
    protected abstract Object getTopicBySubjectIdentifier(String sid);
    
    /**
     * Returns a topic with the provided subject locator.
     */
    protected abstract Object getTopicBySubjectLocator(String slo);

    /**
     * Returns a construct with the provided item identifier.
     */
    protected abstract Object getConstructByItemIdentifier(String iid);

    /**
     * Returns the topic (or null) which reifies the provided construct.
     * (association, role, occurrence, name, variant).
     */
    protected abstract Object getReifier(Object obj);
    
    /**
     * Returns the construct (or null) which is reified by the provided topic.
     * (topic map, association, role, occurrence, name, variant).
     */
    protected abstract Object getReified(Object obj);

    /**
     * Returns the reifier of the topic map or null.
     */
    protected abstract Object getTopicMapReifier();

    /**
     * Returns the parent of the provided construct.
     * The parent of an association or topic is the topic map, the parent
     * of a role is the association, the parent of an occurrence / name is
     * the topic. The parent of a variant is the name.
     */
    protected abstract Object getParent(Object obj);

    /**
     * Returns the datatype of an occurrence / variant.
     */
    protected abstract String getDatatypeAsString(Object obj);

    /**
     * Returns the value of an occurrence, name, variant.
     */
    protected abstract String getValue(Object obj);

    /**
     * Returns the type of an association, role, occurrence, or name.
     */
    protected abstract Object getType(Object obj);

    public void testVariantNoValue() throws Exception {
        final IRef theTopic = Ref.createItemIdentifier("http://test.semagia.com/the-topic");
        final IRef theme = Ref.createItemIdentifier("http://test.semagia.com/theme");
        IMapHandler handler = makeMapHandler();
        handler.startTopicMap();
        handler.startTopic(theTopic);
        handler.startName();
        handler.value("Semagia");
        handler.startVariant();
        handler.startScope();
        handler.startTheme();
        handler.topicRef(theme);
        handler.endTheme();
        handler.endScope();
        try {
            handler.endVariant();
            handler.endName();
            fail("Expected an error since the variant has no value");
        }
        catch (MIOException ex) {
            // noop.
        }
    }

    public void testNameNoValue() throws Exception {
        final IRef theTopic = Ref.createItemIdentifier("http://test.semagia.com/the-topic");
        IMapHandler handler = makeMapHandler();
        handler.startTopicMap();
        handler.startTopic(theTopic);
        handler.startName();
        try {
            handler.endName();
            fail("Expected an error since the name has no value");
        }
        catch (MIOException ex) {
            // noop.
        }
    }

    /**
     * <a href="http://code.google.com/p/mappa/issues/detail?id=23">http://code.google.com/p/mappa/issues/detail?id=23</a>
     */
    public void testMappaIssue23() throws Exception {
        String iid = "http://mappa.semagia.com/issue-23";
        String iid2 = "http://mappa.semagia.com/issue-23_";
        final IRef TOPIC_NAME = Ref.createSubjectIdentifier(TMDM.TOPIC_NAME);
        IMapHandler handler = makeMapHandler();
        handler.startTopicMap();
        handler.startTopic(Ref.createItemIdentifier(iid));
        handler.startName();
        handler.value("test");
        handler.startType();
        handler.topicRef(TOPIC_NAME);
        handler.endType();
        handler.endName();
        handler.endTopic();
        handler.startTopic(Ref.createItemIdentifier(iid2));
        handler.startName();
        handler.value("a test");
        handler.startType();
        handler.topicRef(TOPIC_NAME);
        handler.endType();
        handler.endName();
        handler.subjectIdentifier(TOPIC_NAME.getIRI());
        handler.endTopic();
        handler.endTopicMap();
    }

    /**
     * <a href="http://code.google.com/p/ontopia/issues/detail?id=84">http://code.google.com/p/ontopia/issues/detail?id=84</a>
     * <a href="http://code.google.com/p/ontopia/issues/detail?id=77">http://code.google.com/p/ontopia/issues/detail?id=77</a>
     */
    public void testOntopiaIssue84() throws Exception {
        IMapHandler handler = makeMapHandler();
        final IRef assocType = Ref.createItemIdentifier("http://test.semagia.com/assoc-type");
        final IRef roleType = Ref.createItemIdentifier("http://test.semagia.com/role-type");
        final IRef rolePlayer = Ref.createItemIdentifier("http://test.semagia.com/role-player");
        final IRef reifier = Ref.createItemIdentifier("http://test.semagia.com/reifier");
        final String assocIID = "http://test.semagia.com/assoc-iid";
        final String roleIID = "http://test.semagia.com/role-iid";
        handler.startTopicMap();
        handler.startAssociation();
        handler.itemIdentifier(assocIID);
        handler.startReifier();
        handler.topicRef(reifier);
        handler.endReifier();
        handler.startType();
        handler.topicRef(assocType);
        handler.endType();
        handler.startRole();
        handler.itemIdentifier(roleIID);
        handler.startType();
        handler.topicRef(roleType);
        handler.endType();
        handler.startPlayer();
        handler.topicRef(rolePlayer);
        handler.endPlayer();
        handler.endRole();
        handler.endAssociation();
        
        handler.startAssociation();
        handler.startReifier();
        handler.topicRef(reifier);
        handler.endReifier();
        handler.startType();
        handler.topicRef(assocType);
        handler.endType();
        handler.startRole();
        handler.startType();
        handler.topicRef(roleType);
        handler.endType();
        handler.startPlayer();
        handler.topicRef(rolePlayer);
        handler.endPlayer();
        handler.endRole();
        handler.endAssociation();
        handler.endTopicMap();
        assertEquals(1, getAssociationSize());
        final Object assoc = getConstructByItemIdentifier(assocIID);
        assertNotNull(getReifier(assoc));
        final Object tmc = getConstructByItemIdentifier(roleIID);
        assertNotNull(tmc);
        assertEquals(assoc, getParent(tmc));
    }

    /**
     * <a href="http://code.google.com/p/ontopia/issues/detail?id=84">http://code.google.com/p/ontopia/issues/detail?id=84</a>
     * <a href="http://code.google.com/p/ontopia/issues/detail?id=77">http://code.google.com/p/ontopia/issues/detail?id=77</a>
     */
    public void testOntopiaIssue84_2() throws Exception {
        IMapHandler handler = makeMapHandler();
        final IRef assocType = Ref.createItemIdentifier("http://test.semagia.com/assoc-type");
        final IRef roleType = Ref.createItemIdentifier("http://test.semagia.com/role-type");
        final IRef rolePlayer = Ref.createItemIdentifier("http://test.semagia.com/role-player");
        final IRef reifier = Ref.createItemIdentifier("http://test.semagia.com/reifier");
        final String assocIID = "http://test.semagia.com/assoc-iid";
        final String roleIID = "http://test.semagia.com/role-iid";
        handler.startTopicMap();
        handler.startAssociation();
        handler.itemIdentifier(assocIID);
        handler.startReifier();
        handler.topicRef(reifier);
        handler.endReifier();
        handler.startType();
        handler.topicRef(assocType);
        handler.endType();
        handler.startRole();
        handler.startType();
        handler.topicRef(roleType);
        handler.endType();
        handler.startPlayer();
        handler.topicRef(rolePlayer);
        handler.endPlayer();
        handler.endRole();
        handler.endAssociation();
        
        handler.startAssociation();
        handler.startReifier();
        handler.topicRef(reifier);
        handler.endReifier();
        handler.startType();
        handler.topicRef(assocType);
        handler.endType();
        handler.startRole();
        handler.itemIdentifier(roleIID);
        handler.startType();
        handler.topicRef(roleType);
        handler.endType();
        handler.startPlayer();
        handler.topicRef(rolePlayer);
        handler.endPlayer();
        handler.endRole();
        handler.endAssociation();
        handler.endTopicMap();
        assertEquals(1, getAssociationSize());
        final Object assoc = getConstructByItemIdentifier(assocIID);
        assertNotNull(getReifier(assoc));
        final Object tmc = getConstructByItemIdentifier(roleIID);
        assertNotNull(tmc);
        assertEquals(assoc, getParent(tmc));
    }

    /**
     * <a href="http://code.google.com/p/ontopia/issues/detail?id=84">http://code.google.com/p/ontopia/issues/detail?id=84</a>
     * <a href="http://code.google.com/p/ontopia/issues/detail?id=77">http://code.google.com/p/ontopia/issues/detail?id=77</a>
     */
    public void testOntopiaIssue84RoleReifier() throws Exception {
        IMapHandler handler = makeMapHandler();
        final IRef assocType = Ref.createItemIdentifier("http://test.semagia.com/assoc-type");
        final IRef roleType = Ref.createItemIdentifier("http://test.semagia.com/role-type");
        final IRef rolePlayer = Ref.createItemIdentifier("http://test.semagia.com/role-player");
        final IRef reifier = Ref.createItemIdentifier("http://test.semagia.com/reifier");
        final String assocIID = "http://test.semagia.com/assoc-iid";
        final String roleIID = "http://test.semagia.com/role-iid";
        handler.startTopicMap();
        handler.startAssociation();
        handler.itemIdentifier(assocIID);
        handler.startType();
        handler.topicRef(assocType);
        handler.endType();
        handler.startRole();
        handler.startReifier();
        handler.topicRef(reifier);
        handler.endReifier();
        handler.itemIdentifier(roleIID);
        handler.startType();
        handler.topicRef(roleType);
        handler.endType();
        handler.startPlayer();
        handler.topicRef(rolePlayer);
        handler.endPlayer();
        handler.endRole();
        handler.endAssociation();
        
        handler.startAssociation();
        handler.startType();
        handler.topicRef(assocType);
        handler.endType();
        handler.startRole();
        handler.startReifier();
        handler.topicRef(reifier);
        handler.endReifier();
        handler.startType();
        handler.topicRef(roleType);
        handler.endType();
        handler.startPlayer();
        handler.topicRef(rolePlayer);
        handler.endPlayer();
        handler.endRole();
        handler.endAssociation();
        handler.endTopicMap();
        assertEquals(1, getAssociationSize());
        final Object assoc = getConstructByItemIdentifier(assocIID);
        assertNull(getReifier(assoc));
        final Object tmc = getConstructByItemIdentifier(roleIID);
        assertNotNull(tmc);
        assertNotNull(getReifier(tmc));
        assertEquals(assoc, getParent(tmc));
    }

    /**
     * <a href="http://code.google.com/p/ontopia/issues/detail?id=84">http://code.google.com/p/ontopia/issues/detail?id=84</a>
     * <a href="http://code.google.com/p/ontopia/issues/detail?id=77">http://code.google.com/p/ontopia/issues/detail?id=77</a>
     */
    public void testOntopiaIssue84RoleReifier2() throws Exception {
        IMapHandler handler = makeMapHandler();
        final IRef assocType = Ref.createItemIdentifier("http://test.semagia.com/assoc-type");
        final IRef assocType2 = Ref.createItemIdentifier("http://test.semagia.com/assoc-type2");
        final IRef roleType = Ref.createItemIdentifier("http://test.semagia.com/role-type");
        final IRef rolePlayer = Ref.createItemIdentifier("http://test.semagia.com/role-player");
        final String reifierIID = "http://test.semagia.com/reifier";
        final IRef reifier = Ref.createItemIdentifier(reifierIID);
        final String roleIID = "http://test.semagia.com/role-iid";
        handler.startTopicMap();
        handler.startAssociation();
        handler.startType();
        handler.topicRef(assocType);
        handler.endType();
        handler.startRole();
        handler.startReifier();
        handler.topicRef(reifier);
        handler.endReifier();
        handler.itemIdentifier(roleIID);
        handler.startType();
        handler.topicRef(roleType);
        handler.endType();
        handler.startPlayer();
        handler.topicRef(rolePlayer);
        handler.endPlayer();
        handler.endRole();
        handler.endAssociation();
        try {
            handler.startAssociation();
            handler.startType();
            handler.topicRef(assocType2);
            handler.endType();
            handler.startRole();
            handler.startReifier();
            handler.topicRef(reifier);
            handler.endReifier();
            handler.startType();
            handler.topicRef(roleType);
            handler.endType();
            handler.startPlayer();
            handler.topicRef(rolePlayer);
            handler.endPlayer();
            handler.endRole();
            handler.endAssociation();
            handler.endTopicMap();
            fail("The topic " + reifierIID + " reifies another role");
        }
        catch (MIOException ex) {
            // noop.
        }
    }

    /**
     * <a href="http://code.google.com/p/ontopia/issues/detail?id=84">http://code.google.com/p/ontopia/issues/detail?id=84</a>
     * <a href="http://code.google.com/p/ontopia/issues/detail?id=77">http://code.google.com/p/ontopia/issues/detail?id=77</a>
     */
    public void testOntopiaIssue84VariantReifier() throws Exception {
        IMapHandler handler = makeMapHandler();
        final IRef theTopic = Ref.createItemIdentifier("http://test.semagia.com/the-topic");
        final String reifierIID = "http://test.semagia.com/reifier";
        final String variantIID = "http://test.semagia.com/variant";
        final IRef reifier = Ref.createItemIdentifier(reifierIID);
        final IRef theme = Ref.createItemIdentifier("http://test.semagia.com/theme");
        handler.startTopicMap();
        handler.startTopic(theTopic);
        handler.startName();
        handler.value("Semagia");
        handler.startVariant();
        handler.startReifier();
        handler.topicRef(reifier);
        handler.endReifier();
        handler.value("variant", XSD.STRING);
        handler.itemIdentifier(variantIID);
        handler.startScope();
        handler.startTheme();
        handler.topicRef(theme);
        handler.endTheme();
        handler.endScope();
        handler.endVariant();
        handler.endName();

        handler.startName();
        handler.value("Semagia");
        handler.startVariant();
        handler.startReifier();
        handler.topicRef(reifier);
        handler.endReifier();
        handler.value("variant", XSD.STRING);
        handler.startScope();
        handler.startTheme();
        handler.topicRef(theme);
        handler.endTheme();
        handler.endScope();
        handler.endVariant();
        handler.endName();
        handler.endTopic();
        handler.endTopicMap();
        Object reifying = getConstructByItemIdentifier(reifierIID);
        assertNotNull(reifying);
        assertNotNull(getReified(reifying));
        assertEquals(getReified(reifying), getConstructByItemIdentifier(variantIID));
    }
    
    /**
     * <a href="http://code.google.com/p/ontopia/issues/detail?id=84">http://code.google.com/p/ontopia/issues/detail?id=84</a>
     * <a href="http://code.google.com/p/ontopia/issues/detail?id=77">http://code.google.com/p/ontopia/issues/detail?id=77</a>
     */
    public void testOntopiaIssue84VariantReifier2() throws Exception {
        IMapHandler handler = makeMapHandler();
        final IRef theTopic = Ref.createItemIdentifier("http://test.semagia.com/the-topic");
        final String reifierIID = "http://test.semagia.com/reifier";
        final String variantIID = "http://test.semagia.com/variant";
        final IRef reifier = Ref.createItemIdentifier(reifierIID);
        final IRef theme = Ref.createItemIdentifier("http://test.semagia.com/theme");
        handler.startTopicMap();
        handler.startTopic(theTopic);
        handler.startName();
        handler.value("Semagia");
        handler.startVariant();
        handler.startReifier();
        handler.topicRef(reifier);
        handler.endReifier();
        handler.value("variant", XSD.STRING);
        handler.startScope();
        handler.startTheme();
        handler.topicRef(theme);
        handler.endTheme();
        handler.endScope();
        handler.endVariant();
        handler.endName();

        handler.startName();
        handler.value("Semagia");
        handler.startVariant();
        handler.startReifier();
        handler.topicRef(reifier);
        handler.endReifier();
        handler.value("variant", XSD.STRING);
        handler.itemIdentifier(variantIID);
        handler.startScope();
        handler.startTheme();
        handler.topicRef(theme);
        handler.endTheme();
        handler.endScope();
        handler.endVariant();
        handler.endName();
        handler.endTopic();
        handler.endTopicMap();
        Object reifying = getConstructByItemIdentifier(reifierIID);
        assertNotNull(reifying);
        assertNotNull(getReified(reifying));
        assertEquals(getReified(reifying), getConstructByItemIdentifier(variantIID));
    }
    
    /**
     * <a href="http://code.google.com/p/ontopia/issues/detail?id=84">http://code.google.com/p/ontopia/issues/detail?id=84</a>
     * <a href="http://code.google.com/p/ontopia/issues/detail?id=77">http://code.google.com/p/ontopia/issues/detail?id=77</a>
     */
    public void testOntopiaIssue84VariantReifier3() throws Exception {
        IMapHandler handler = makeMapHandler();
        final IRef theTopic = Ref.createItemIdentifier("http://test.semagia.com/the-topic");
        final String reifierIID = "http://test.semagia.com/reifier";
        final String variantIID = "http://test.semagia.com/variant";
        final IRef reifier = Ref.createItemIdentifier(reifierIID);
        final IRef theme = Ref.createItemIdentifier("http://test.semagia.com/theme");
        handler.startTopicMap();
        handler.startTopic(theTopic);
        handler.startName();
        handler.value("Semagia");
        handler.startVariant();
        handler.startReifier();
        handler.topicRef(reifier);
        handler.endReifier();
        handler.value("variant", XSD.STRING);
        handler.startScope();
        handler.startTheme();
        handler.topicRef(theme);
        handler.endTheme();
        handler.endScope();
        handler.endVariant();
        handler.endName();

        try {
            handler.startName();
            handler.value("Not Semagia");
            handler.startVariant();
            handler.startReifier();
            handler.topicRef(reifier);
            handler.endReifier();
            handler.value("variant", XSD.STRING);
            handler.itemIdentifier(variantIID);
            handler.startScope();
            handler.startTheme();
            handler.topicRef(theme);
            handler.endTheme();
            handler.endScope();
            handler.endVariant();
            handler.endName();
            handler.endTopic();
            handler.endTopicMap();
            fail("The topic " + reifierIID + " reifies a variant of another name which is not equal");
        }
        catch (MIOException ex) {
            // noop.
        }
    }

    /**
     * <a href="http://code.google.com/p/ontopia/issues/detail?id=84">http://code.google.com/p/ontopia/issues/detail?id=84</a>
     * <a href="http://code.google.com/p/ontopia/issues/detail?id=77">http://code.google.com/p/ontopia/issues/detail?id=77</a>
     */
    public void testOntopiaIssue84VariantReifier4() throws Exception {
        IMapHandler handler = makeMapHandler();
        final IRef theTopic = Ref.createItemIdentifier("http://test.semagia.com/the-topic");
        final IRef theOtherTopic = Ref.createItemIdentifier("http://test.semagia.com/the-other-topic");
        final String reifierIID = "http://test.semagia.com/reifier";
        final String variantIID = "http://test.semagia.com/variant";
        final IRef reifier = Ref.createItemIdentifier(reifierIID);
        final IRef theme = Ref.createItemIdentifier("http://test.semagia.com/theme");
        handler.startTopicMap();
        handler.startTopic(theTopic);
        handler.startName();
        handler.value("Semagia");
        handler.startVariant();
        handler.startReifier();
        handler.topicRef(reifier);
        handler.endReifier();
        handler.value("variant", XSD.STRING);
        handler.startScope();
        handler.startTheme();
        handler.topicRef(theme);
        handler.endTheme();
        handler.endScope();
        handler.endVariant();
        handler.endName();
        handler.endTopic();

        try {
            handler.startTopic(theOtherTopic);
            handler.startName();
            handler.value("Semagia");
            handler.startVariant();
            handler.startReifier();
            handler.topicRef(reifier);
            handler.endReifier();
            handler.value("variant", XSD.STRING);
            handler.itemIdentifier(variantIID);
            handler.startScope();
            handler.startTheme();
            handler.topicRef(theme);
            handler.endTheme();
            handler.endScope();
            handler.endVariant();
            handler.endName();
            handler.endTopic();
            handler.endTopicMap();
            fail("The topic " + reifierIID + " reifies a variant of another name which is not equal");
        }
        catch (MIOException ex) {
            // noop.
        }
    }

    public void testSameIIDIssueAssociation() throws Exception {
        IMapHandler handler = makeMapHandler();
        final IRef assocType = Ref.createItemIdentifier("http://test.semagia.com/assoc-type");
        final IRef roleType = Ref.createItemIdentifier("http://test.semagia.com/role-type");
        final IRef rolePlayer = Ref.createItemIdentifier("http://test.semagia.com/role-player");
        final String iid = "http://test.semagia.com/iid";
        handler.startTopicMap();
        handler.startAssociation();
        handler.itemIdentifier(iid);
        handler.startType();
        handler.topicRef(assocType);
        handler.endType();
        handler.startRole();
        handler.startType();
        handler.topicRef(roleType);
        handler.endType();
        handler.startPlayer();
        handler.topicRef(rolePlayer);
        handler.endPlayer();
        handler.endRole();
        handler.endAssociation();
        
        handler.startAssociation();
        handler.itemIdentifier(iid);
        handler.startType();
        handler.topicRef(assocType);
        handler.endType();
        handler.startRole();
        handler.startType();
        handler.topicRef(roleType);
        handler.endType();
        handler.startPlayer();
        handler.topicRef(rolePlayer);
        handler.endPlayer();
        handler.endRole();
        handler.endAssociation();
        handler.endTopicMap();
        assertEquals(1, getAssociationSize());
        Object assoc = getConstructByItemIdentifier(iid);
        assertNotNull(assoc);
    }

    /**
     * Simple startTopicMap, followed by an endTopicMap event.
     */
    public void testEmpty() throws Exception {
        IMapHandler handler = makeMapHandler();
        assertEquals(0, getAssociationSize());
        assertEquals(0, getTopicSize());
        handler.startTopicMap();
        handler.endTopicMap();
        assertEquals(0, getAssociationSize());
        assertEquals(0, getTopicSize());
    }

    /**
     * Tests reifying a topic map.
     */
    public void testTMReifier() throws Exception {
        IMapHandler handler = makeMapHandler();
        String itemIdent = "http://sf.net/projects/tinytim/test#1";
        assertEquals(0, getAssociationSize());
        assertEquals(0, getTopicSize());
        handler.startTopicMap();
        handler.startReifier();
        handler.startTopic(Ref.createItemIdentifier(itemIdent));
        handler.endTopic();
        handler.endReifier();
        handler.endTopicMap();
        assertEquals(0, getAssociationSize());
        assertEquals(1, getTopicSize());
        Object topic = getConstructByItemIdentifier(itemIdent);
        assertNotNull(topic);
        assertEquals(topic, getTopicMapReifier());
    }

    /**
     * Tests topic creation with an item identifier.
     */
    public void testTopicIdentityItemIdentifier() throws Exception {
        String itemIdent = "http://sf.net/projects/tinytim/test#1";
        IMapHandler handler = makeMapHandler();
        handler.startTopicMap();
        handler.startTopic(Ref.createItemIdentifier(itemIdent));
        handler.endTopic();
        handler.endTopicMap();
        assertEquals(1, getTopicSize());
        Object topic = getConstructByItemIdentifier(itemIdent);
        assertNotNull(topic);
    }

    /**
     * Tests topic creation with a subject identifier.
     */
    public void testTopicIdentitySubjectIdentifier() throws Exception {
        String subjIdent = "http://sf.net/projects/tinytim/test#1";
        IMapHandler handler = makeMapHandler();
        handler.startTopicMap();
        handler.startTopic(Ref.createSubjectIdentifier(subjIdent));
        handler.endTopic();
        handler.endTopicMap();
        assertEquals(1, getTopicSize());
        Object topic = getTopicBySubjectIdentifier(subjIdent);
        assertNotNull(topic);
    }

    /**
     * Tests topic creation with a subject locator.
     */
    public void testTopicIdentitySubjectLocator() throws Exception {
        String subjLoc = "http://sf.net/projects/tinytim/test#1";
        IMapHandler handler = makeMapHandler();
        handler.startTopicMap();
        handler.startTopic(Ref.createSubjectLocator(subjLoc));
        handler.endTopic();
        handler.endTopicMap();
        assertEquals(1, getTopicSize());
        Object topic = getTopicBySubjectLocator(subjLoc);
        assertNotNull(topic);
    }

    /**
     * Tests transparent merging.
     */
    public void testTopicMergingInvalid() throws Exception {
        String ref = "http://sf.net/projects/tinytim/test#1";
        String itemIdent = "http://example.org/1";
        IMapHandler handler = makeMapHandler();
        handler.startTopicMap();
        handler.startTopic(Ref.createSubjectIdentifier(ref));
        // TopicIF in TopicIF event
        handler.startTopic(Ref.createItemIdentifier(itemIdent));
        handler.itemIdentifier(ref);
        handler.endTopic();
        handler.startOccurrence();
        handler.value("tinyTiM", XSD.STRING);
        try {
            handler.endOccurrence();
            handler.endTopic();
            fail("No occurrence type provided");
        }
        catch (MIOException ex) {
            // pass
        }
    }

    /**
     * Tests transparent merging.
     */
    public void testTopicMerging() throws Exception {
        String type = "http://sf.net/projects/tinytim/occ-type";
        String ref = "http://sf.net/projects/tinytim/test#1";
        String itemIdent = "http://example.org/1";
        String occIID = "http://example.org/occ";
        IMapHandler handler = makeMapHandler();
        handler.startTopicMap();
        handler.startTopic(Ref.createSubjectIdentifier(ref));
        // Topic in topic event
        handler.startTopic(Ref.createItemIdentifier(itemIdent));
        handler.itemIdentifier(ref);
        handler.endTopic();
        handler.startOccurrence();
        handler.itemIdentifier(occIID);
        handler.value("tinyTiM", XSD.STRING);
        handler.startType();
        handler.topicRef(Ref.createItemIdentifier(type));
        handler.endType();
        handler.endOccurrence();
        handler.endTopic();
        handler.endTopicMap();
        assertEquals(2, getTopicSize());
        Object topic = getTopicBySubjectIdentifier(ref);
        assertNotNull(topic);
        assertEquals(topic, getConstructByItemIdentifier(ref));
        assertEquals(topic, getConstructByItemIdentifier(itemIdent));
        Object occ = getConstructByItemIdentifier(occIID);
        assertEquals("tinyTiM", getValue(occ));
    }

    /**
     * Tests assigning identities to a topic.
     */
    public void testTopicIdentities1() throws Exception {
        String ref = "http://sf.net/projects/tinytim/test#1";
        IMapHandler handler = makeMapHandler();
        handler.startTopicMap();
        handler.startTopic(Ref.createSubjectIdentifier(ref));
        handler.itemIdentifier(ref);
        handler.endTopic();
        handler.endTopicMap();
        assertEquals(1, getTopicSize());
        Object topic = getTopicBySubjectIdentifier(ref);
        assertNotNull(topic);
        assertEquals(topic, getConstructByItemIdentifier(ref));
    }

    /**
     * Tests assigning identities to a topic.
     */
    public void testTopicIdentities2() throws Exception {
        String ref = "http://sf.net/projects/tinytim/test#1";
        IMapHandler handler = makeMapHandler();
        handler.startTopicMap();
        handler.startTopic(Ref.createItemIdentifier(ref));
        handler.subjectIdentifier(ref);
        handler.endTopic();
        handler.endTopicMap();
        assertEquals(1, getTopicSize());
        Object topic = getTopicBySubjectIdentifier(ref);
        assertNotNull(topic);
        assertEquals(topic, getConstructByItemIdentifier(ref));
    }

    /**
     * Tests reifying the TopicIF map.
     */
    public void testTopicMapReifier() throws Exception {
        String ref = "http://sf.net/projects/tinytim/test#1";
        IMapHandler handler = makeMapHandler();
        handler.startTopicMap();
        handler.startReifier();
        handler.startTopic(Ref.createSubjectIdentifier(ref));
        handler.endTopic();
        handler.endReifier();
        handler.endTopicMap();
        assertNotNull(getTopicMapReifier());
        Object topic = getTopicBySubjectIdentifier(ref);
        assertNotNull(topic);
        assertEquals(topic, getTopicMapReifier());
    }

    /**
     * Tests occurrence creation with no type.
     */
    public void testOccurrenceNoType() throws Exception {
        String ref = "http://sf.net/projects/tinytim/test#1";
        String val = "tinyTiM";
        IMapHandler handler = makeMapHandler();
        handler.startTopicMap();
        handler.startTopic(Ref.createSubjectIdentifier(ref));
        handler.startOccurrence();
        handler.value(val, XSD.STRING);
        try {
            handler.endOccurrence();
            handler.endTopic();
            fail("Expected an exception since the occurrence has no type");
        }
        catch (MIOException ex) {
            // noop.
        }
    }

    /**
     * Tests occurrence creation with a value of datatype xsd:string.
     */
    public void testOccurrenceValueString() throws Exception {
        String type = "http://sf.net/projects/tinytim/occ-type";
        String ref = "http://sf.net/projects/tinytim/test#1";
        String occIID = "http://sf.net/projects/tinytim/occ";
        String val = "tinyTiM";
        IMapHandler handler = makeMapHandler();
        handler.startTopicMap();
        handler.startTopic(Ref.createSubjectIdentifier(ref));
        handler.startOccurrence();
        handler.itemIdentifier(occIID);
        handler.startType();
        handler.topicRef(Ref.createItemIdentifier(type));
        handler.endType();
        handler.value(val, XSD.STRING);
        handler.endOccurrence();
        handler.endTopic();
        handler.endTopicMap();
        Object topic = getTopicBySubjectIdentifier(ref);
        assertNotNull(topic);
        Object occ = getConstructByItemIdentifier(occIID);
        assertEquals(val, getValue(occ));
        assertEquals(XSD.STRING, getDatatypeAsString(occ));
    }

    /**
     * Tests occurrence creation with a value of datatype xsd:anyURI.
     */
    public void testOccurrenceValueURINoType() throws Exception {
        String ref = "http://sf.net/projects/tinytim/test#1";
        String val = "http://sf.net/projects/tinytim";
        IMapHandler handler = makeMapHandler();
        handler.startTopicMap();
        handler.startTopic(Ref.createSubjectIdentifier(ref));
        handler.startOccurrence();
        handler.value(val, XSD.ANY_URI);
        try {
            handler.endOccurrence();
            handler.endTopic();
            fail("Expected an exception since the occurrence has no type");
        }
        catch (MIOException ex) {
            // noop.
        }
    }

    /**
     * Tests occurrence creation with a value of datatype xsd:anyURI.
     */
    public void testOccurrenceValueURI() throws Exception {
        String type = "http://sf.net/projects/tinytim/occ-type";
        String ref = "http://sf.net/projects/tinytim/test#1";
        String occIID = "http://sf.net/projects/tinytim/test#1";
        String val = "http://sf.net/projects/tinytim";
        IMapHandler handler = makeMapHandler();
        handler.startTopicMap();
        handler.startTopic(Ref.createSubjectIdentifier(ref));
        handler.startOccurrence();
        handler.itemIdentifier(occIID);
        handler.value(val, XSD.ANY_URI);
        handler.startType();
        handler.topicRef(Ref.createItemIdentifier(type));
        handler.endType();
        handler.endOccurrence();
        handler.endTopic();
        handler.endTopicMap();
        Object topic = getTopicBySubjectIdentifier(ref);
        assertNotNull(topic);
        Object occ = getConstructByItemIdentifier(occIID);
        assertEquals(val, getValue(occ));
        assertEquals(XSD.ANY_URI, getDatatypeAsString(occ));
    }

    /**
     * Tests if the name type is automatically set.
     */
    public void testDefaultNameType() throws Exception {
        String ref = "http://sf.net/projects/tinytim/test#1";
        String val = "tinyTiM";
        String nameIID = "http://sf.net/projects/tinytim/test#name";
        IMapHandler handler = makeMapHandler();
        handler.startTopicMap();
        handler.startTopic(Ref.createSubjectIdentifier(ref));
        handler.startName();
        handler.value(val);
        handler.itemIdentifier(nameIID);
        handler.endName();
        handler.endTopic();
        handler.endTopicMap();
        Object topic = getTopicBySubjectIdentifier(ref);
        assertNotNull(topic);
        Object name = getConstructByItemIdentifier(nameIID);
        assertEquals(val, getValue(name));
        assertNotNull(getType(name));
        assertEquals(getType(name), getTopicBySubjectIdentifier(TMDM.TOPIC_NAME));
    }

    /**
     * Tests if a variant with no scope is reported as error.
     */
    public void testVariantNoScopeError() throws Exception {
        String ref = "http://sf.net/projects/tinytim/test#1";
        String val = "tinyTiM";
        IMapHandler handler = makeMapHandler();
        handler.startTopicMap();
        handler.startTopic(Ref.createSubjectIdentifier(ref));
        handler.startName();
        handler.value(val);
        handler.startVariant();
        handler.value(val, XSD.STRING);
        try {
            handler.endVariant();
            handler.endName();
            fail("A variant with no scope shouldn't be allowed");
        }
        catch (MIOException ex) {
            // noop.
        }
    }

    /**
     * Tests if a variant with a scope equals to the parent's scope is rejected.
     */
    public void testVariantNoScopeError2() throws Exception {
        String ref = "http://sf.net/projects/tinytim/test#1";
        String theme = "http://sf.net/projects/tinytim/test#theme";
        String val = "tinyTiM";
        IRef theTheme = Ref.createItemIdentifier(theme);
        IMapHandler handler = makeMapHandler();
        handler.startTopicMap();
        handler.startTopic(Ref.createSubjectIdentifier(ref));
        handler.startName();
        handler.startScope();
        handler.startTheme();
        handler.topicRef(theTheme);
        handler.endTheme();
        handler.endScope();
        handler.value(val);
        
        handler.startVariant();
        handler.value(val, XSD.STRING);
        handler.startScope();
        handler.startTheme();
        handler.topicRef(theTheme);
        handler.endTheme();
        handler.endScope();
        try {
            handler.endVariant();
            handler.endName();
            fail("A variant with a scope equals to the parent's scope shouldn't be allowed");
        }
        catch (MIOException ex) {
            // noop.
        }
    }

    /**
     * Tests nested startTopic/endTopic events.
     */
    public void testNestedTopics() throws Exception {
        IMapHandler handler = makeMapHandler();
        String base = "http://tinytim.sourceforge.net/test-nesting#";
        final int MAX = 10000;
        String[] iids = new String[MAX];
        handler.startTopicMap();
        for (int i=0; i<MAX; i++) {
            iids[i] = base + i;
            handler.startTopic(Ref.createItemIdentifier(iids[i]));
        }
        for (int i=0; i<MAX; i++) {
            handler.endTopic();
        }
        handler.endTopicMap();
        for (String iid: iids) {
            assertNotNull(getConstructByItemIdentifier(iid));
        }
    }

}
