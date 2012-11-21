package com.technophobia.substeps.runner.syntax.validation.fake;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.technophobia.substeps.runner.syntax.SyntaxErrorReporter;

public class FakeSyntaxErrorReporter implements SyntaxErrorReporter {

    private final List<SyntaxErrorData> errors;


    public FakeSyntaxErrorReporter() {
        this.errors = new ArrayList<FakeSyntaxErrorReporter.SyntaxErrorData>();
    }


    public void reportFeatureError(final File file, final String line,
            final int lineNumber, final String description)
            throws RuntimeException {
        this.errors.add(new SyntaxErrorData(true, file, line, lineNumber,
                description));
    }


    public void reportFeatureError(final File file, final String line,
            final int lineNumber, final String description,
            final RuntimeException ex) throws RuntimeException {
        this.errors.add(new SyntaxErrorData(true, file, line, lineNumber,
                description));
    }


    public void reportSubstepsError(final File file, final String line,
            final int lineNumber, final String description)
            throws RuntimeException {
        this.errors.add(new SyntaxErrorData(false, file, line, lineNumber,
                description));
    }


    public void reportSubstepsError(final File file, final String line,
            final int lineNumber, final String description,
            final RuntimeException ex) throws RuntimeException {
        this.errors.add(new SyntaxErrorData(false, file, line, lineNumber,
                description));
    }


    public List<SyntaxErrorData> errors() {
        Collections.sort(this.errors);
        return this.errors;
    }

    public static class SyntaxErrorData implements Comparable<SyntaxErrorData> {
        private final boolean isFeature;
        private final File file;
        private final String line;
        private final int lineNumber;
        private final String description;


        public SyntaxErrorData(final boolean isFeature, final File file,
                final String line, final int lineNumber,
                final String description) {
            this.isFeature = isFeature;
            this.file = file;
            this.line = line;
            this.lineNumber = lineNumber;
            this.description = description;
        }


        public boolean isFeature() {
            return this.isFeature;
        }


        public File getFile() {
            return this.file;
        }


        public String getLine() {
            return this.line;
        }


        public int getLineNumber() {
            return this.lineNumber;
        }


        public String getDescription() {
            return this.description;
        }


        public int compareTo(final SyntaxErrorData other) {
            return this.lineNumber - other.lineNumber;
        }
    }
}
