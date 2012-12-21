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
package com.technophobia.substeps.runner.syntax.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.technophobia.substeps.model.FeatureFile;
import com.technophobia.substeps.model.ParentStep;
import com.technophobia.substeps.model.PatternMap;
import com.technophobia.substeps.model.Syntax;
import com.technophobia.substeps.runner.FeatureFileParser;
import com.technophobia.substeps.runner.syntax.ClassAnalyser;
import com.technophobia.substeps.runner.syntax.SubStepDefinitionParser;
import com.technophobia.substeps.runner.syntax.SyntaxBuilder;
import com.technophobia.substeps.runner.syntax.validation.fake.FakeSyntaxErrorReporter;
import com.technophobia.substeps.runner.syntax.validation.fake.FakeSyntaxErrorReporter.SyntaxErrorData;
import com.technophobia.substeps.stepimplementations.MockStepImplementations;

public class SyntaxAwareStepValidatorTest {

    private static final String FEATURE_PATH = "./target/test-classes/features/";
    private static final String SUBSTEPS_PATH = "./target/test-classes/substeps/";

    private FakeSyntaxErrorReporter syntaxErrorReporter;
    private FeatureFileParser featureFileParser;
    private SubStepDefinitionParser substepsFileParser;


    @Before
    public void initialise() {

        this.syntaxErrorReporter = new FakeSyntaxErrorReporter();
        this.featureFileParser = new FeatureFileParser();
        this.substepsFileParser = new SubStepDefinitionParser(this.syntaxErrorReporter);
    }


    @Test
    public void validatorReportsMissingStepsInScenario() {
        final FeatureFile featureFile = this.featureFileParser.loadFeatureFile(createFeatureFile("error.feature"));

        createStepValidatorWithSubsteps("simple.substeps").validateFeatureFile(featureFile, syntaxErrorReporter);
        final List<SyntaxErrorData> errors = syntaxErrorReporter.syntaxErrors();
        assertThat(Integer.valueOf(errors.size()), is(Integer.valueOf(2)));

        checkError(errors.get(0), 6, "Given step 1");
        checkError(errors.get(1), 7, "Given step 2");
    }


    @Test
    public void validatorReportsNoErrorsForFeatureWithValidSteps() {
        final FeatureFile featureFile = this.featureFileParser.loadFeatureFile(createFeatureFile("error.feature"));

        createStepValidatorWithSubsteps("error.substeps").validateFeatureFile(featureFile, syntaxErrorReporter);
        final List<SyntaxErrorData> errors = syntaxErrorReporter.syntaxErrors();
        assertTrue(errors.isEmpty());
    }


    @Test
    public void validatorReportsMissingSubstepsInDefinition() {
        final PatternMap<ParentStep> substeps = substepsFileParser.loadSubSteps(createSubstepsFile("error.substeps"));

        final StepValidator stepValidator = createStepValidatorWithSubsteps("simple.substeps");
        for (final ParentStep substep : substeps.values()) {
            stepValidator.validateSubstep(substep, syntaxErrorReporter);
        }

        final List<SyntaxErrorData> errors = syntaxErrorReporter.syntaxErrors();
        assertThat(Integer.valueOf(errors.size()), is(Integer.valueOf(3)));

        checkError(errors.get(0), 5, "SingleWord");
        checkError(errors.get(1), 6, "Test_Then something else has happened");
        checkError(errors.get(2), 9, "Test_Then something has happened");
    }


    @Test
    public void validatorReportsNoErrorsForSubstepsWithValidSteps() {
        final PatternMap<ParentStep> substeps = this.substepsFileParser
                .loadSubSteps(createSubstepsFile("allFeatures.substeps"));

        final StepValidator stepValidator = createStepValidatorWithSubsteps("simple.substeps",
                MockStepImplementations.class);
        for (final ParentStep substep : substeps.values()) {
            stepValidator.validateSubstep(substep, syntaxErrorReporter);
        }
        final List<SyntaxErrorData> errors = syntaxErrorReporter.syntaxErrors();
        assertTrue(errors.isEmpty());
    }


    private void checkError(final SyntaxErrorData error, final int lineNumber, final String line) {
        assertThat(Integer.valueOf(error.getLineNumber()), is(Integer.valueOf(lineNumber)));
        assertThat(error.getLine(), is(line));
        assertThat(error.getDescription(), is("Step \"" + line + "\" is not defined"));
    }


    private File createFeatureFile(final String name) {
        return new File(FEATURE_PATH, name);
    }


    private File createSubstepsFile(final String name) {
        return new File(SUBSTEPS_PATH, name);
    }


    private StepValidator createStepValidatorWithSubsteps(final String substepsFilename,
            final Class<?>... stepImplClasses) {
        final Syntax syntax = SyntaxBuilder.buildSyntax(Arrays.asList(stepImplClasses),
                createSubstepsFile(substepsFilename), true, new String[0], new ClassAnalyser(), true,
                this.syntaxErrorReporter);

        return new SyntaxAwareStepValidator(syntax);
    }
}
