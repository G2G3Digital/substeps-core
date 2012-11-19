package com.technophobia.substeps.runner.syntax;

import java.io.File;

public interface SubstepsValidator {

    void validate(File featureFile, SyntaxErrorReporter syntaxErrorReporter);
}
