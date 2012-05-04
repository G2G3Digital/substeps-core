/*
 *	Copyright Technophobia Ltd 2012
 *
 *   This file is part of Substeps.
 *
 *    Substeps is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    Substeps is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with Substeps.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.technophobia.substeps.runner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

import com.technophobia.substeps.runner.TagManager;

/**
 * 
 * @author imoore
 * 
 */
public class TagManagerTest {

    final String[] empty = null;

    @Test
    public void testEmptyTagManager() {

        final TagManager tMgr = new TagManager("");

        Assert.assertTrue(tMgr.acceptTaggedScenario(empty));
        Assert.assertTrue(tMgr.acceptTaggedScenario("@fred"));
        Assert.assertTrue(tMgr.acceptTaggedScenario("@fred", "@bob"));

    }

    @Test
    public void testNonEmptyTagManager() {
        final String runTags = "@bob";

        final TagManager tMgr = new TagManager(runTags);

        Assert.assertFalse(tMgr.acceptTaggedScenario(empty));
        Assert.assertFalse(tMgr.acceptTaggedScenario("@fred"));
        Assert.assertTrue(tMgr.acceptTaggedScenario("@bob"));
        Assert.assertTrue(tMgr.acceptTaggedScenario("@fred", "@bob"));

    }

    @Test
    public void testAndedTagManager() {
        final String runTags = "@bob @fred";

        final TagManager tMgr = new TagManager(runTags);

        Assert.assertFalse(tMgr.acceptTaggedScenario(empty));
        Assert.assertFalse(tMgr.acceptTaggedScenario("@fred"));
        Assert.assertFalse(tMgr.acceptTaggedScenario("@bob"));
        Assert.assertTrue(tMgr.acceptTaggedScenario("@fred", "@bob"));

    }

    @Test
    public void testNegativeTags() {
        final String runTags = "--@fred";

        final TagManager tMgr = new TagManager(runTags);

        Assert.assertFalse(tMgr.acceptTaggedScenario("@fred"));
        Assert.assertFalse(tMgr.acceptTaggedScenario("@fred", "@bob"));
        Assert.assertTrue(tMgr.acceptTaggedScenario(empty));
        Assert.assertTrue(tMgr.acceptTaggedScenario("@bob"));

    }

    @Test
    public void testPositiveTagOverlay() {
        final String runTags = "@fred --@bob";

        final TagManager tMgr = new TagManager(runTags);

        Assert.assertTrue(tMgr.acceptTaggedScenario("@fred"));
        Assert.assertFalse(tMgr.acceptTaggedScenario("@bob"));
        Assert.assertFalse(tMgr.acceptTaggedScenario("@bill"));

        tMgr.insertTagOverlay("@bill @bob");
        Assert.assertTrue(tMgr.acceptTaggedScenario("@bill", "@fred", "@bob"));
    }


    @Test
    public void testNegativeTagOverlay() {
        final String runTags = "--@fred";

        final TagManager tMgr = new TagManager(runTags);

        Assert.assertFalse(tMgr.acceptTaggedScenario("@fred"));
        Assert.assertTrue(tMgr.acceptTaggedScenario("@bob"));

        tMgr.insertTagOverlay("--@bob");
        Assert.assertFalse(tMgr.acceptTaggedScenario("@fred"));
        Assert.assertFalse(tMgr.acceptTaggedScenario("@bob"));
    }


    @Test
    @Ignore
    public void testRegEx() {
        final String input = "@bob @fred & @wilma | @george";

        final String splitPattern = "([ ,&\\|])+";

        final String[] split = input.split(splitPattern);

        for (final String s : split) {
            System.out.println(s);
        }

        final Pattern p = Pattern.compile(splitPattern);
        final Matcher m = p.matcher(input);

        final int groupCount = m.groupCount();

        System.out.println("groupCount: " + groupCount);

        if (m.lookingAt()) {

            for (int i = 0; i <= groupCount; i++) {
                System.out.println("group[" + i + "]: " + m.group(i));
            }
        } else {
            System.out.println("no match");
        }

    }

}
