/*
 *  Copyright Technophobia Ltd 2012
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
package com.technophobia.substeps.runner.syntax;

import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.technophobia.substeps.model.ParentStep;
import com.technophobia.substeps.model.PatternMap;
import com.technophobia.substeps.model.exception.SubstepsParsingException;

public class SubStepDefinitionParserTest {

    private static final boolean FAIL_ON_DUPLICATE_SUBSTEPS = true;
    private static final boolean DO_NOT_FAIL_ON_DUPLICATE_SUBSTEPS = !FAIL_ON_DUPLICATE_SUBSTEPS;

    private SyntaxErrorReporter errorReporter;
    private SubStepDefinitionParser parser;

    @Before
    public void initialiseDependencies() {
        this.errorReporter = mock(SyntaxErrorReporter.class);
    }

    @Test
    public void shouldReportDuplicateDefinition() {

        this.parser = new SubStepDefinitionParser(DO_NOT_FAIL_ON_DUPLICATE_SUBSTEPS, this.errorReporter);

        this.parser.parseSubStepFile(new File(
                "./target/test-classes/com/technophobia/substeps/runner/syntax/duplicate-definition.substeps"));

        verify(this.errorReporter).reportSubstepsError(argThat(is(any(SubstepsParsingException.class))));
    }

    @Test
    public void shouldReportEmptyDefinition() {
        this.parser = new SubStepDefinitionParser(DO_NOT_FAIL_ON_DUPLICATE_SUBSTEPS, this.errorReporter);

        this.parser.parseSubStepFile(new File(
                "./target/test-classes/com/technophobia/substeps/runner/syntax/empty-definition.substeps"));

        verify(this.errorReporter).reportSubstepsError(argThat(is(any(SubstepsParsingException.class))));
    }

    @Test
    public void testCommentOnEol() {

        this.parser = new SubStepDefinitionParser(FAIL_ON_DUPLICATE_SUBSTEPS, this.errorReporter);

        final PatternMap<ParentStep> substepDefs = this.parser.loadSubSteps(new File(
                "./target/test-classes/com/technophobia/substeps/runner/syntax/eol-comment.substeps"));

        Assert.assertNotNull(substepDefs);

        final String def1 = "something else";

        List<ParentStep> list = substepDefs.get(def1);

        Assert.assertThat(list, hasSize(1));

        ParentStep parentStep = list.get(0);

        String line = parentStep.getParent().getLine();

        Assert.assertThat(line, is(def1));

        final String def2 = "and something different";

        list = substepDefs.get(def2);

        Assert.assertThat(list, hasSize(1));

        parentStep = list.get(0);

        line = parentStep.getParent().getLine();

        Assert.assertThat(line, is(def2));

        final String commentedDefine = "a normal comment";

        list = substepDefs.get(commentedDefine);
        Assert.assertThat(list, empty());
    }

    @Ignore
    @Test
    public void shouldReportDuplicateFinalDefinition() {
        // TODO: work out what the other condition is
        fail("Not yet implemented");
    }

    @Ignore
    @Test
    public void shouldReportEmptyFinalDefinition() {
        // TODO: work out what the other condition is
        fail("Not yet implemented");
    }

    @Ignore
    @Test
    public void shouldNotLoadEmptySubStepDefinitions() {

        fail("This was copied from a previous test, but may be invalid.");

        final SubStepDefinitionParser parser = new SubStepDefinitionParser(new DefaultSyntaxErrorReporter());

        final PatternMap<ParentStep> loadedSubSteps = parser.loadSubSteps(new File(
                "./target/test-classes/substeps/error.substeps"));

        Assert.assertFalse(loadedSubSteps.containsPattern("An empty substep definition"));
    }
}
