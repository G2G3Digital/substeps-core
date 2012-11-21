package com.technophobia.substeps.runner.syntax.validation;

import com.technophobia.substeps.model.FeatureFile;
import com.technophobia.substeps.model.ParentStep;
import com.technophobia.substeps.runner.syntax.SyntaxErrorReporter;

public interface StepValidator {

    void validateFeatureFile(FeatureFile featureFile, SyntaxErrorReporter syntaxErrorReporter);


    void validateSubstep(ParentStep substep, SyntaxErrorReporter syntaxErrorReporter);
}
