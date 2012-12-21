/*
 *	Copyright Technophobia Ltd 2012
 *
 *   This file is part of Substeps.
 *
 *    Substeps is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    Substeps is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with Substeps.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.technophobia.substeps.runner;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.technophobia.substeps.model.SubSteps.StepImplementations;
import com.technophobia.substeps.runner.setupteardown.Annotations.AfterAllFeatures;
import com.technophobia.substeps.runner.setupteardown.Annotations.AfterEveryFeature;
import com.technophobia.substeps.runner.setupteardown.Annotations.AfterEveryScenario;
import com.technophobia.substeps.runner.setupteardown.Annotations.BeforeAllFeatures;
import com.technophobia.substeps.runner.setupteardown.Annotations.BeforeEveryFeature;
import com.technophobia.substeps.runner.setupteardown.Annotations.BeforeEveryScenario;
import com.technophobia.substeps.steps.TestStepImplementations;

public class DryRunTest {

    private final ExecutionNodeRunner runner = new ExecutionNodeRunner();

    @Before
    public void configureRunner() {

        TestInitialisationClass.reset();
        TestStepImplementations.somethingCalled = false;

        SubstepsExecutionConfig theConfig = new SubstepsExecutionConfig();

        theConfig.setDescription("Feature set");

        final String feature = "./target/test-classes/features/example.feature";
        final String substeps = "./target/test-classes/substeps/simple.substeps";

        theConfig.setFeatureFile(feature);
        theConfig.setSubStepsFileName(substeps);

        final List<Class<?>> stepImplementationClasses = new ArrayList<Class<?>>();
        stepImplementationClasses.add(TestStepImplementations.class);
        stepImplementationClasses.add(TestInitialisationClass.class);

        theConfig.setStepImplementationClasses(stepImplementationClasses);

        runner.prepareExecutionConfig(theConfig);

    }

    @Test
    public void testSetupTearDownAndImplsAreCalledIfNotOnDryRun() {

        TestInitialisationClass.failOnAccess = false;

        runner.run();

        Assert.assertTrue(TestInitialisationClass.setupCalled);
        Assert.assertTrue(TestInitialisationClass.tearDownCalled);
        Assert.assertTrue(TestStepImplementations.somethingCalled);
    }

    @Test
    public void testNoSetupOrTearDownIsCalled() {

        try {
            System.setProperty("dryRun", "true");

            runner.run();

            Assert.assertFalse(TestInitialisationClass.setupCalled);
            Assert.assertFalse(TestInitialisationClass.tearDownCalled);
        } finally {

            System.clearProperty("dryRun");
        }
    }

    @Test
    public void testNoTestImplementationsAreCalled() {

        try {
            System.setProperty("dryRun", "true");

            runner.run();

            Assert.assertFalse(TestStepImplementations.somethingCalled);
        } finally {

            System.clearProperty("dryRun");
        }

    }

    @StepImplementations(requiredInitialisationClasses = TestInitialisationClass.class)
    public static class TestInitialisationClass {

        static boolean failOnAccess = true;

        static boolean setupCalled = false;
        static boolean tearDownCalled = false;

        static void reset() {
            failOnAccess = true;
            setupCalled = false;
            tearDownCalled = false;
        }

        @BeforeAllFeatures
        @BeforeEveryFeature
        @BeforeEveryScenario
        public void setupMethod() {

            if (failOnAccess) {
                Assert.fail("Setup method should not have been invoked since we were on a dry run");
            }

            setupCalled = true;
        }

        @AfterEveryScenario
        @AfterEveryFeature
        @AfterAllFeatures
        public void tearDownMethod() {

            if (failOnAccess) {
                Assert.fail("Tear down method should not have been invoked since we were on a dry run");
            }

            tearDownCalled = true;
        }
    }

}
