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

import java.io.File;
import java.util.List;

import com.technophobia.substeps.model.FeatureFile;
import com.technophobia.substeps.model.ParentStep;
import com.technophobia.substeps.model.PatternMap;
import com.technophobia.substeps.model.Scenario;
import com.technophobia.substeps.model.Step;
import com.technophobia.substeps.model.StepImplementation;
import com.technophobia.substeps.model.Syntax;
import com.technophobia.substeps.runner.syntax.SyntaxErrorReporter;

public class SyntaxAwareStepValidator implements StepValidator {

    private final PatternMap<StepImplementation> stepImplMap;
    private final Syntax syntax;


    public SyntaxAwareStepValidator(final Syntax syntax) {
        this.syntax = syntax;
        this.stepImplMap = createStepImplMap();
    }


    public void validateFeatureFile(final FeatureFile featureFile, final SyntaxErrorReporter syntaxErrorReporter) {
        final List<Scenario> scenarios = featureFile.getScenarios();
        if (scenarios != null) {
            for (final Scenario scenario : scenarios) {
                validate(scenario, featureFile.getSourceFile(), syntaxErrorReporter);
            }
        }
    }


    public void validateSubstep(final ParentStep substep, final SyntaxErrorReporter syntaxErrorReporter) {
        final List<Step> steps = substep.getSteps();
        if (steps != null) {
            for (final Step step : steps) {
                validate(step, new File(substep.getSubStepFileUri()), syntaxErrorReporter);
            }
        }
    }


    protected void validate(final Scenario scenario, final File sourceFile,
            final SyntaxErrorReporter syntaxErrorReporter) {
        final List<Step> steps = scenario.getSteps();
        if (steps != null) {
            for (final Step step : steps) {
                validate(step, sourceFile, syntaxErrorReporter);
            }
        }
    }


    protected void validate(final Step step, final File sourceFile, final SyntaxErrorReporter syntaxErrorReporter) {
        if (!isValid(step)) {
            syntaxErrorReporter.reportFeatureError(sourceFile, step.getLine(), step.getSourceLineNumber(),
                    step.getSourceStartOffset(), "Step \"" + step.getLine() + "\" is not defined");
        }
    }


    protected boolean isValid(final Step step) {
        final List<ParentStep> substeps = this.syntax.getSubStepsMap().get(step.getLine());
        if (substeps != null && !substeps.isEmpty()) {
            return true;
        }

        final List<StepImplementation> stepImpls = this.stepImplMap.get(step.getLine());
        if (stepImpls != null && !stepImpls.isEmpty()) {
            return true;
        }
        return false;
    }


    private PatternMap<StepImplementation> createStepImplMap() {
        final PatternMap<StepImplementation> results = new PatternMap<StepImplementation>();

        final List<StepImplementation> stepImpls = syntax.getStepImplementations();
        for (final StepImplementation stepImpl : stepImpls) {
            results.put(stepImpl.getValue(), stepImpl);
        }

        return results;
    }

}
