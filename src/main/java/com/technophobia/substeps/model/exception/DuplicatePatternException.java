package com.technophobia.substeps.model.exception;

import java.io.File;

import com.technophobia.substeps.model.ParentStep;

public class DuplicatePatternException extends SubstepsParsingException {

    private static final long serialVersionUID = 2426674698756596968L;

    public DuplicatePatternException(final String pattern, final ParentStep originalSource,
            final ParentStep duplicatingSource) {
        // TODO - offset
        super(new File(duplicatingSource.getSubStepFile()), originalSource.getParent().getSourceLineNumber(),
                originalSource.getParent().getLine(), 0L, messageFrom(pattern, originalSource, duplicatingSource));

    }

    private static String messageFrom(final String pattern, final ParentStep originalSource,
            final ParentStep duplicatingSource) {
        // TODO: is 'pattern' actually a end-user friendly word?
        return String.format("Duplicate pattern detected: Pattern [%s] is defined in %s and %s", pattern,
                patternLocationFrom(originalSource), patternLocationFrom(duplicatingSource));
    }

    private static String patternLocationFrom(final ParentStep parentStep) {
        return parentStep.getSubStepFile() + "::" + parentStep.getParent().getSourceLineNumber();
    }
}
