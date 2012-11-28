package com.technophobia.substeps.model.exception;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.technophobia.substeps.model.StepImplementation;

public class DuplicateStepImplementationExceptionTest {

    @Test
    public void shouldProduceSuitableMessage() throws NoSuchMethodException, SecurityException {
        final DuplicateStepImplementationException ex = new DuplicateStepImplementationException("test-pattern",
                stepImplementation("originalMethod"), stepImplementation("duplicatingMethod"));

        assertThat(ex.getMessage(),
                is("Duplicate step implementation detected: Pattern [test-pattern] is implemented in "
                        + getClass().getName() + ".originalMethod and " + getClass().getName() + ".duplicatingMethod"));
    }

    private StepImplementation stepImplementation(final String methodName) throws NoSuchMethodException,
            SecurityException {
        return new StepImplementation(getClass(), "keyword", "test-pattern", getClass().getMethod(methodName));
    }

    public void originalMethod() {
        // NoOp - here for testing.
    }

    public void duplicatingMethod() {
        // NoOp - here for testing.
    }
}
