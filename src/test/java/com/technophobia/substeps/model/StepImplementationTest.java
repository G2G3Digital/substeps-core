package com.technophobia.substeps.model;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

/**
 * Created by ian on 02/10/15.
 */
public class StepImplementationTest {

    @Test
    public void testStepImplParsing() {
        final StepImplementation si = StepImplementation.parse("Given it is Christmas", this.getClass(), null);

        Assert.assertNotNull(si);
        Assert.assertThat(si.getValue(), is("Given it is Christmas"));
        Assert.assertThat(si.getKeyword(), is("Given"));

    }
}
