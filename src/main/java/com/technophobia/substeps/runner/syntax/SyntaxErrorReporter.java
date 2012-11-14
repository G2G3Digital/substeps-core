package com.technophobia.substeps.runner.syntax;

import java.io.File;

public interface SyntaxErrorReporter {

    void reportFeatureError(File file, int lineNumber, String description) throws RuntimeException;


    void reportFeatureError(File file, int lineNumber, String description, RuntimeException ex) throws RuntimeException;


    void reportSubstepsError(File file, int lineNumber, String description) throws RuntimeException;


    void reportSubstepsError(File file, int lineNumber, String description, RuntimeException ex)
            throws RuntimeException;
}
