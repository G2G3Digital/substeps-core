package com.technophobia.substeps.runner;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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

        SubstepsExecutionConfig theConfig = new SubstepsExecutionConfig();

        theConfig.setDescription("Feature set");
        theConfig.setInitialisationClass(new String[] { TestInitialisationClass.class.toString() });

        final String feature = "./target/test-classes/features/example.feature";
        final String substeps = "./target/test-classes/substeps/simple.substeps";

        theConfig.setFeatureFile(feature);
        theConfig.setSubStepsFileName(substeps);

        final List<Class<?>> stepImplementationClasses = new ArrayList<Class<?>>();
        stepImplementationClasses.add(TestStepImplementations.class);

        theConfig.setStepImplementationClasses(stepImplementationClasses);

        runner.prepareExecutionConfig(theConfig);

    }

    @Test
    public void testSetupTearDownAndImplsAreCalledIfNotOnDryRun() {

        runner.run();

        Assert.assertTrue(TestInitialisationClass.accessed);
    }

    @Test
    public void testNoSetupIsCalled() {

    }

    @Test
    public void testNoTearDownIsCalled() {

    }

    @Test
    public void testNoTestImplementationsAreCalled() {

        // TestRootNodeBuilder rootNodeBuilder = new TestRootNodeBuilder();
        // TestFeatureNodeBuilder featureNodeBuilder =
        // rootNodeBuilder.addFeature(new Feature("a feature", "a filename"));
        // featureNodeBuilder.addBasicScenario("basic scenario").addSubsteps()
        // .addStepImpl(targetClass, targetMethod, methodArgs);

    }

    private static class TestInitialisationClass {

        private static boolean failOnAccess = true;

        private static boolean accessed = false;

        static void setFailOnAccess(boolean failOnAccess) {
            TestInitialisationClass.failOnAccess = failOnAccess;
        }

        static boolean hasBeenAccessed() {
            return accessed;
        }

        static void reset() {
            failOnAccess = true;
            accessed = false;
        }

        @BeforeAllFeatures
        @BeforeEveryFeature
        @BeforeEveryScenario
        @AfterEveryScenario
        @AfterEveryFeature
        @AfterAllFeatures
        public void setupAndTeardownMethod() {

            if (failOnAccess) {
                Assert.fail("Method should not have been invoked since we were on a dry run");
            }

            accessed = true;
        }

    }

}
