package com.technophobia.substeps.runner.syntax;

import java.io.File;
import java.util.List;

import com.technophobia.substeps.model.FeatureFile;
import com.technophobia.substeps.model.ParentStep;
import com.technophobia.substeps.model.PatternMap;
import com.technophobia.substeps.model.Scenario;
import com.technophobia.substeps.model.Step;
import com.technophobia.substeps.model.StepImplementation;
import com.technophobia.substeps.model.Syntax;
import com.technophobia.substeps.runner.TagManager;
import com.technophobia.substeps.runner.TestParameters;

public class SyntaxValidator implements SubstepsValidator {

    private final PatternMap<StepImplementation> stepImplMap;
    private final Syntax syntax;


    public SyntaxValidator(final Syntax syntax) {
        this.syntax = syntax;
        this.stepImplMap = createStepImplMap(syntax);
    }


    public void validate(final File featureFile, final SyntaxErrorReporter syntaxErrorReporter) {
        final TagManager tagManager = new TagManager("");
        final TestParameters testParameters = new TestParameters(tagManager, syntax, featureFile.getAbsolutePath());
        testParameters.init();

        for (final FeatureFile ff : testParameters.getFeatureFileList()) {
            validate(ff, syntaxErrorReporter);
        }
    }


    protected void validate(final FeatureFile featureFile, final SyntaxErrorReporter syntaxErrorReporter) {
        final List<Scenario> scenarios = featureFile.getScenarios();
        for (final Scenario scenario : scenarios) {
            final List<Step> steps = scenario.getSteps();
            for (final Step step : steps) {
                if (!isValid(step)) {
                    syntaxErrorReporter.reportFeatureError(featureFile.getSourceFile(), step.getLine(),
                            step.getSourceLineNumber(), "Step not defined");
                }
            }
        }
    }


    private boolean isValid(final Step step) {
        final List<ParentStep> substeps = syntax.getSubStepsMap().get(step.getLine());
        if (substeps != null) {
            return true;
        }

        if (stepImplMap.get(step.getLine()) != null) {
            return true;
        }
        return false;
    }


    private PatternMap<StepImplementation> createStepImplMap(final Syntax syntax) {
        final PatternMap<StepImplementation> results = new PatternMap<StepImplementation>();

        final List<StepImplementation> stepImpls = syntax.getStepImplementations();
        for (final StepImplementation stepImpl : stepImpls) {
            results.put(stepImpl.getValue(), stepImpl);
        }

        return results;
    }
}
