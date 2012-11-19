package com.technophobia.substeps.model.exception;

import java.io.File;

public class SubstepsParsingException extends SubstepsException {

    private static final long serialVersionUID = 3310663144363390571L;

    private final File file;
    private final long offset;
    private final String line;
    private final int lineNumber;

    public SubstepsParsingException(final File file, final int lineNumber, final String line, final long offset,
            final String message) {
        super(message);
        this.file = file;
        this.line = line;
        this.offset = offset;
        this.lineNumber = lineNumber;
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

    public long getOffset() {
        return offset;
    }

}
