package com.technophobia.substeps.model.exception;

import com.technophobia.substeps.model.StepImplementation;

public class DuplicateStepImplementationException extends StepImplementationException {

    private static final long serialVersionUID = 6851509341143564326L;

    public DuplicateStepImplementationException(final String pattern, final StepImplementation originalSource,
            final StepImplementation duplicatingSource) {

        super(duplicatingSource.getImplementedIn(), duplicatingSource.getMethod(), String.format(
                "Duplicate step implementation detected: Pattern [%s] is implemented in %s and %s", pattern,
                fullMethodNameFrom(originalSource), fullMethodNameFrom(duplicatingSource)));

    }

    private static String fullMethodNameFrom(final StepImplementation stepImplementation) {
        return stepImplementation.getImplementedIn().getName() + "." + stepImplementation.getMethod().getName();
    }

}
