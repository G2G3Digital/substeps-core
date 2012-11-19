package com.technophobia.substeps.runner.syntax;

import java.io.File;

import com.technophobia.substeps.model.exception.StepImplementationException;
import com.technophobia.substeps.model.exception.SubstepsParsingException;

public interface SyntaxErrorReporter {

    void reportFeatureError(File file, String line, int lineNumber, String description) throws RuntimeException;

    void reportFeatureError(File file, String line, int lineNumber, String description, RuntimeException ex)
            throws RuntimeException;

    void reportSubstepsError(SubstepsParsingException ex);

    void reportStepImplError(StepImplementationException ex);
}
