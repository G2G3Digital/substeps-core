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

import static org.hamcrest.CoreMatchers.is;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import com.technophobia.substeps.model.ParentStep;
import com.technophobia.substeps.model.Step;

//import static org.junit.Assert.*;

/**
 * @author ian
 * 
 */
public class ParsingTests {
    @Test
    public void testRegEx() {
        final String stepParameter = "bob <start_locateButton>";
        final String paramRegEx = ".*<([^>]*)>.*";
        final Pattern findParamPattern = Pattern.compile(paramRegEx);
        Assert.assertTrue(findParamPattern.matcher(stepParameter).matches());

    }


    @Test
    public void testMultiParamScenario() {
        final String topLevelStepFeature = "Given two postcodes S11 8UP, \"Penrhyn Road\", S11 8UE, \"Hunter Hill Road\"";

        final String parentDefinition = "Given two postcodes <s_outer_pc> <s_inner_pc>, <from_street>, <e_outer_pc> <e_inner_pc>, <to_street>";

        final Step theParentStep = new Step(parentDefinition, true);
        final ParentStep parentStep = new ParentStep(theParentStep, "aFile");

        final Step topLevelStop = new Step(topLevelStepFeature);

        parentStep.initialiseParamValues(topLevelStop);

        final Map<String, String> paramValueMap = parentStep.getParamValueMap();
        Assert.assertNotNull(paramValueMap);

        Assert.assertThat(paramValueMap.size(), is(6));

        Assert.assertThat(paramValueMap.get("s_outer_pc"), is("S11"));
        Assert.assertThat(paramValueMap.get("s_inner_pc"), is("8UP"));
        Assert.assertThat(paramValueMap.get("from_street"), is("Penrhyn Road"));

        Assert.assertThat(paramValueMap.get("e_outer_pc"), is("S11"));
        Assert.assertThat(paramValueMap.get("e_inner_pc"), is("8UE"));
        Assert.assertThat(paramValueMap.get("to_street"), is("Hunter Hill Road"));

    }


    @Test
    public void testStepConstruction() {
        final Step aNormalStep = new Step("Given the usual");

        Assert.assertThat(aNormalStep.getKeyword(), is("Given"));
        Assert.assertThat(aNormalStep.getLine(), is("Given the usual"));
        // Assert.assertThat(aNormalStep.getParam(), is("the usual"));
        Assert.assertNull(aNormalStep.getPattern());

        // a step defined as a root of a series of steps
        final Step aParentSubStep = new Step("Given the usual with a <parameter>", true);

        Assert.assertThat(aParentSubStep.getKeyword(), is("Given"));
        Assert.assertThat(aParentSubStep.getLine(), is("Given the usual with a <parameter>"));

        // we would want this string to match with the compiled pattern

        Assert.assertThat(aParentSubStep.getPattern(), is("Given the usual with a \"?([^\"]*)\"?"));

        // param is "the usual with a <parameter>" as normal... TODO is this
        // expected?
        // Assert.assertThat(aParentSubStep.getParam(),
        // is(aParentSubStep.getPattern()));

    }


    @Test
    public void testRegExFindAndReplace() {
        // used in the Runner

        String input = "hello bob here is <something> else and <anotherthing> blah";

        final String paramRegEx = "(<([^>]*)>)";
        final Pattern p = Pattern.compile(".*" + paramRegEx + ".*");

        final String paramRegEx2 = ".*<(.*)";
        final Pattern p2 = Pattern.compile(paramRegEx2);

        final String[] splits = input.split(">");

        for (final String s : splits) {
            System.out.println("split: " + s);

            final Matcher matcher = p2.matcher(s);
            if (matcher.find()) {
                System.out.println("matcher group 1: " + matcher.group(1));
            } else {
                System.out.println("no match");
            }
        }
        // TODO do we want to do this instead? All we want to do is get all the
        // params out!

        // Assert.assertTrue(paramPattern.matcher(param).matches());

        // Matcher matcher1 = p.matcher(input);
        Matcher matcher = p.matcher(input);
        // Assert.assertTrue(matcher1.matches());

        while (matcher.find()) {
            final int groupCount = matcher.groupCount();
            System.out.println(groupCount);

            // for (int i = 1; i <= groupCount; i++)
            // {
            // if (matcher.find(i)) {
            System.out.println(matcher.group(1));
            System.out.println(matcher.group(2));

            input = input.replaceAll(matcher.group(1), "replacement");

            // } else {
            // System.out.println("no capturing group: " + i);
            // }
            matcher = p.matcher(input);
            // groupCount = matcher.groupCount();
            // break;
            // }
        }
        System.out.println("input: " + input);

    }


    @Test
    public void testRequiredPattern() {
        final String input = "the usual with a unquoted";

        final String pattern = "the usual with a \"?([^\"]*)\"?";

        // |the usual with a ([^\"]*)

        final Pattern p = Pattern.compile(pattern);
        final Matcher matcher1 = p.matcher(input);
        final Matcher matcher = p.matcher(input);
        Assert.assertTrue(matcher1.matches());

        final int groupCount = matcher.groupCount();
        System.out.println(groupCount);
        if (matcher.find()) {
            for (int i = 1; i <= groupCount; i++) {
                // if (matcher.find(i)) {
                System.out.println(matcher.group(i));

                // } else {
                // System.out.println("no capturing group: " + i);
                // }
            }
        }

    }


    @Test
    public void testParentParameterChaining() {
        final Step aParentSubStep = new Step("Given", // "the usual with a <parameter>",
                "Given the usual with a <parameter>", true);

        final Step topLevelStepReDefinedInSubSteps = new Step(
                "Given the usual with a \"fantastically tickety boo\"");

        final ParentStep parentStep = new ParentStep(aParentSubStep, "aFile");

        parentStep.initialiseParamValues(topLevelStepReDefinedInSubSteps);
        // String[] paramValues = Util.getArgs(this.parent.pattern, step.param);

        final Map<String, String> paramValueMap = parentStep.getParamValueMap();
        Assert.assertNotNull(paramValueMap);

        Assert.assertThat(paramValueMap.size(), is(1));

        Assert.assertTrue(paramValueMap.containsKey("parameter"));

        Assert.assertThat(paramValueMap.get("parameter"), is("fantastically tickety boo"));

    }

}
