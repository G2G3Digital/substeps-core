package com.technophobia.substeps.runner.syntax;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSyntaxErrorReporter implements SyntaxErrorReporter {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultSyntaxErrorReporter.class);


    @Override
    public void reportFeatureError(final File file, final String line, final int lineNumber, final String description)
            throws RuntimeException {
        LOG.error("Error on line " + lineNumber + " of feature file " + file.getAbsolutePath() + ": " + line
                + " - reason: " + description);
    }


    @Override
    public void reportFeatureError(final File file, final String line, final int lineNumber, final String description,
            final RuntimeException ex) throws RuntimeException {
        LOG.error("Error on line " + lineNumber + " of feature file " + file.getAbsolutePath() + ": " + line
                + " - reason: " + description);
        throw ex;
    }


    @Override
    public void reportSubstepsError(final File file, final String line, final int lineNumber, final String description)
            throws RuntimeException {
        LOG.error("Error on line " + lineNumber + " of substeps file " + file.getAbsolutePath() + ": " + line
                + " - reason: " + description);
    }


    @Override
    public void reportSubstepsError(final File file, final String line, final int lineNumber, final String description,
            final RuntimeException ex) throws RuntimeException {
        LOG.error("Error on line " + lineNumber + " of substeps file " + file.getAbsolutePath() + ": " + line
                + " - reason: " + description);
        throw ex;
    }


    @Override
    public void reportStepImplError(final Class<?> stepImplClass, final String method, final String description) {
        LOG.error("Error in class " + stepImplClass + ", method " + method + " - reason " + description);
    }


    @Override
    public void reportStepImplError(final Class<?> stepImplClass, final String method, final String description,
            final RuntimeException ex) {
        LOG.error("Error in class " + stepImplClass + ", method " + method + " - reason " + description);
        throw ex;
    }
}
