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

package com.technophobia.substeps.model;

import java.util.List;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.junit.Test;

public class PatternMapTest {

    @Test
    public void regexTest() {

        final Pattern p1 = Pattern.compile("pattern");
        final Pattern p2 = Pattern.compile("pattern.*");

        final String val = "pattern2";

        System.out.println("p1 matches: " + p1.matcher(val).matches());
        System.out.println("p2 matches: " + p2.matcher(val).matches());

    }


    @Test
    public void testPatternMap() {

        final PatternMap<String> patternMap = new PatternMap<String>();

        patternMap.put("patter", "value1");
        patternMap.put("patter.*", "value2");

        final List<String> values = patternMap.get("patter");

        Assert.assertNotNull(values);

        Assert.assertEquals(2, values.size());
    }


    @Test
    public void testPatternMapWithGroups() {

        final PatternMap<String> patternMap = new PatternMap<String>();

        patternMap.put("I'm registered with the email address \"(.*)\" and the password \"(.*)\"",
                "value1");

        patternMap.put("I'm registered with the email address \"fred\" and the password \"wilma\"",
                "value2");

        patternMap.put("I'm registered with the email address fred and the password wilma",
                "value3");

        final List<String> values = patternMap
                .get("I'm registered with the email address \"fred\" and the password \"wilma\"");

        Assert.assertNotNull(values);

        Assert.assertEquals(2, values.size());
    }

}
