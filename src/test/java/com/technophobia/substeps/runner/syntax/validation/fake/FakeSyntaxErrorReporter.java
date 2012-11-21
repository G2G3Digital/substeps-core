package com.technophobia.substeps.runner.syntax.validation.fake;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.technophobia.substeps.model.exception.StepImplementationException;
import com.technophobia.substeps.model.exception.SubstepsParsingException;
import com.technophobia.substeps.runner.syntax.SyntaxErrorReporter;

public class FakeSyntaxErrorReporter implements SyntaxErrorReporter {

    private final List<SyntaxErrorData> syntaxErrors;
    private final List<StepImplErrorData> stepErrors;

    public FakeSyntaxErrorReporter() {
        this.syntaxErrors = new ArrayList<FakeSyntaxErrorReporter.SyntaxErrorData>();
        this.stepErrors = new ArrayList<FakeSyntaxErrorReporter.StepImplErrorData>();
    }

    public void reportFeatureError(final File file, final String line, final int lineNumber, final String description)
            throws RuntimeException {
        syntaxErrors.add(new SyntaxErrorData(true, file, line, lineNumber, description));
    }

    public void reportFeatureError(final File file, final String line, final int lineNumber, final String description,
            final RuntimeException ex) throws RuntimeException {
        syntaxErrors.add(new SyntaxErrorData(true, file, line, lineNumber, description));
    }

    public void reportSubstepsError(final SubstepsParsingException ex) {
        syntaxErrors.add(new SyntaxErrorData(false, ex.getFile(), ex.getLine(), ex.getLineNumber(), ex.getMessage()));
    }

    public void reportStepImplError(final StepImplementationException ex) {
        stepErrors.add(new StepImplErrorData(ex.getImplementingClass(), ex.getImplementingMethod().getName(), ex
                .getMessage()));
    }

    public List<SyntaxErrorData> syntaxErrors() {
        Collections.sort(syntaxErrors);
        return syntaxErrors;
    }

    public List<StepImplErrorData> stepImplErrors() {
        Collections.sort(stepImplErrors());
        return stepErrors;
    }

    public static class SyntaxErrorData implements Comparable<SyntaxErrorData> {

        private final boolean isFeature;
        private final File file;
        private final String line;
        private final int lineNumber;
        private final String description;

        public SyntaxErrorData(final boolean isFeature, final File file, final String line, final int lineNumber,
                final String description) {
            this.isFeature = isFeature;
            this.file = file;
            this.line = line;
            this.lineNumber = lineNumber;
            this.description = description;
        }

        public boolean isFeature() {
            return isFeature;
        }

        public File getFile() {
            return file;
        }

        public String getLine() {
            return line;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public String getDescription() {
            return description;
        }

        public int compareTo(final SyntaxErrorData other) {
            return lineNumber - other.lineNumber;
        }
    }

    private static class StepImplErrorData implements Comparable<StepImplErrorData> {

        private final Class<?> clazz;
        private final String methodName;
        private final String description;

        public StepImplErrorData(final Class<?> clazz, final String methodName, final String description) {
            this.clazz = clazz;
            this.methodName = methodName;
            this.description = description;
        }

        public Class<?> getClazz() {
            return clazz;
        }

        public String getMethodName() {
            return methodName;
        }

        public String getDescription() {
            return description;
        }

        public int compareTo(final StepImplErrorData other) {
            final int result = clazz.getName().compareTo(other.clazz.getName());

            if (result != 0) {
                return result;
            }
            return methodName.compareTo(other.methodName);
        }
    }

}
