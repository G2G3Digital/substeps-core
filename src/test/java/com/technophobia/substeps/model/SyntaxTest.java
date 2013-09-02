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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class SyntaxTest {

    private static final String DEFINITION_1_1_KEYWORD = "Definition 1.1";
    private static final String DEFINITION_1_2_KEYWORD = "Definition 1.2";
    private static final String DEFINITION_2_1_KEYWORD = "Definition 2.1";
    private static final String DEFINITION_2_2_KEYWORD = "Definition 2.2";

    private static final String DEFINITION_1_1_PARAMATER_LINE_SUFFIX = "parameter line for class 1, method 1";
    private static final String DEFINITION_1_2_PARAMATER_LINE_SUFFIX = "parameter line for class 1, method 2";
    private static final String DEFINITION_2_1_PARAMATER_LINE_SUFFIX = "parameter line for class 2, method 1";
    private static final String DEFINITION_2_2_PARAMATER_LINE_SUFFIX = "parameter line for class 2, method 2";

    private static final String DEFINITION_1_1_PARAMATER_LINE_FULL = DEFINITION_1_1_KEYWORD
            + DEFINITION_1_1_PARAMATER_LINE_SUFFIX;
    private static final String DEFINITION_1_2_PARAMATER_LINE_FULL = DEFINITION_1_2_KEYWORD
            + DEFINITION_1_2_PARAMATER_LINE_SUFFIX;
    private static final String DEFINITION_2_1_PARAMATER_LINE_FULL = DEFINITION_2_1_KEYWORD
            + DEFINITION_2_1_PARAMATER_LINE_SUFFIX;
    private static final String DEFINITION_2_2_PARAMATER_LINE_FULL = DEFINITION_2_2_KEYWORD
            + DEFINITION_2_2_PARAMATER_LINE_SUFFIX;

    private Syntax syntax;

    @Before
    public void initialise() throws Exception {
        this.syntax = new Syntax();

        this.syntax.addStepImplementation(stepImplementation(DEFINITION_1_1_KEYWORD,
                DEFINITION_1_1_PARAMATER_LINE_FULL, StepDefinitionClass1.class, "method1"));
        this.syntax.addStepImplementation(stepImplementation(DEFINITION_1_2_KEYWORD,
                DEFINITION_1_2_PARAMATER_LINE_FULL, StepDefinitionClass1.class, "method2"));
        this.syntax.addStepImplementation(stepImplementation(DEFINITION_2_1_KEYWORD,
                DEFINITION_2_1_PARAMATER_LINE_FULL, StepDefinitionClass2.class, "method1"));
        this.syntax.addStepImplementation(stepImplementation(DEFINITION_2_2_KEYWORD,
                DEFINITION_2_2_PARAMATER_LINE_FULL, StepDefinitionClass2.class, "method2"));
    }

    @Test
    public void canGetStepImplementationsInStrictModeWhenKeywordMatchesAnnotation() throws Exception {
        this.syntax.setStrict(true, new String[0]);

        final String keyword = DEFINITION_1_2_KEYWORD;
        final String parameterLine = DEFINITION_1_2_PARAMATER_LINE_FULL;

        final List<StepImplementation> stepImplementations = this.syntax.getStepImplementations(keyword, parameterLine,
                null, 21);
        assertThat(stepImplementations.size(), is(1));

        final StepImplementation stepImplementation = stepImplementations.get(0);

        assertThat(stepImplementation.getImplementedIn(), is((Object) StepDefinitionClass1.class));
        assertThat(stepImplementation.getMethod(), is(StepDefinitionClass1.class.getMethod("method2")));
    }

    @Test
    public void noStepImplementationsFoundInStrictModeWhenKeywordDoesNotMatchAnnotation() throws Exception {

        this.syntax.setStrict(true, new String[0]);

        final String keyword = DEFINITION_1_1_KEYWORD;
        final String parameterLine = DEFINITION_1_2_PARAMATER_LINE_FULL;

        final List<StepImplementation> stepImplementations = this.syntax.getStepImplementations(keyword, parameterLine,
                null, 21);
        assertThat(stepImplementations.size(), is(0));
    }

    @Test
    public void canGetStepImplementationsInNonStrictModeWhenKeywordMatchesAnnotation() throws Exception {

        this.syntax.setStrict(false, new String[] { DEFINITION_1_1_KEYWORD, DEFINITION_1_2_KEYWORD,
                DEFINITION_2_1_KEYWORD, DEFINITION_2_2_KEYWORD });

        final String keyword = DEFINITION_2_2_KEYWORD;
        final String parameterLine = DEFINITION_2_2_PARAMATER_LINE_FULL;

        final List<StepImplementation> stepImplementations = this.syntax.getStepImplementations(keyword, parameterLine,
                null, 21);
        assertThat(stepImplementations.size(), is(1));

        final StepImplementation stepImplementation = stepImplementations.get(0);

        assertThat(stepImplementation.getImplementedIn(), is((Object) StepDefinitionClass2.class));
        assertThat(stepImplementation.getMethod(), is(StepDefinitionClass2.class.getMethod("method2")));
    }

    @Test
    public void canGetStepImplementationsInNonStrictModeWhenKeywordDoesNotMatchAnnotation() throws Exception {

        this.syntax.setStrict(false, new String[] { DEFINITION_1_1_KEYWORD, DEFINITION_1_2_KEYWORD,
                DEFINITION_2_1_KEYWORD, DEFINITION_2_2_KEYWORD });

        final String keyword = DEFINITION_2_2_KEYWORD;
        final String parameterLine = DEFINITION_2_2_KEYWORD + DEFINITION_2_1_PARAMATER_LINE_SUFFIX;

        final List<StepImplementation> stepImplementations = this.syntax.getStepImplementations(keyword, parameterLine,
                null, 21);
        assertThat(stepImplementations.size(), is(1));

        final StepImplementation stepImplementation = stepImplementations.get(0);

        assertThat(stepImplementation.getKeyword(), is(DEFINITION_2_2_KEYWORD));
        assertThat(stepImplementation.getImplementedIn(), is((Object) StepDefinitionClass2.class));
        assertThat(stepImplementation.getMethod(), is(StepDefinitionClass2.class.getMethod("method1")));
    }

    @Test
    public void testInvalidPatternInStepImplDoesntStopSyntaxProcessing() throws Exception {
        // eg @Step("ExecuteQueryAndStashResults {([^}]*)}") <- this is an
        // invalid pattern

        // this should not throw an exception
        this.syntax.addStepImplementation(stepImplementation("ExecuteQueryAndStashResults",
                "ExecuteQueryAndStashResults {([^}]*)}", StepDefinitionClass2.class, "method2"));

    }

    private StepImplementation stepImplementation(final String keyword, final String parameterLine,
            final Class<?> clazz, final String methodName) throws Exception {
        return new StepImplementation(clazz, keyword, parameterLine, clazz.getMethod(methodName));
    }

    public class StepDefinitionClass1 {

        public void method1() {
        }

        public void method2() {
        }
    }

    public class StepDefinitionClass2 {

        public void method1() {
        }

        public void method2() {
        }
    }
}
