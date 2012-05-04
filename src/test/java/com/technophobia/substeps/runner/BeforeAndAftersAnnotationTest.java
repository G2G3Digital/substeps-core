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

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.notification.RunNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.technophobia.substeps.runner.JunitFeatureRunner;
import com.technophobia.substeps.runner.JunitFeatureRunner.AfterAllFeatures;
import com.technophobia.substeps.runner.JunitFeatureRunner.AfterEveryFeature;
import com.technophobia.substeps.runner.JunitFeatureRunner.AfterEveryScenario;
import com.technophobia.substeps.runner.JunitFeatureRunner.BeforeAllFeatures;
import com.technophobia.substeps.runner.JunitFeatureRunner.BeforeAndAfterProcessors;
import com.technophobia.substeps.runner.JunitFeatureRunner.BeforeEveryFeature;
import com.technophobia.substeps.runner.JunitFeatureRunner.BeforeEveryScenario;
import com.technophobia.substeps.stepimplementations.BDDRunnerStepImplementations;


/**
 * 
 * 
 * @author imoore
 * 
 */
@BeforeAndAfterProcessors({ BeforeAndAftersAnnotationMethods.class })
@Ignore
public class BeforeAndAftersAnnotationTest {
}

@Ignore
class BeforeAndAftersAnnotationMethods extends BeforeAndAftersTestParent {
    private static final Logger log = LoggerFactory.getLogger(BeforeAndAftersAnnotationTest.class);

    static int beforeAllFeaturesCounter = 0;
    static int afterAllFeaturesCounter = 0;
    static int beforeFeatureCounter = 0;
    static int afterFeatureCounter = 0;
    static int beforeScenarioCounter = 0;
    static int afterScenarioCounter = 0;


    @BeforeAllFeatures
    public static void beforeAllFeatures() {
        log.debug("beforeAllFeatures");
        Assert.assertTrue(parentBeforeAllFeaturesCounter > beforeAllFeaturesCounter);

        beforeAllFeaturesCounter++;
    }


    @AfterAllFeatures
    public static void afterAllFeatures() {
        log.debug("afterAllFeatures");

        afterAllFeaturesCounter++;

        Assert.assertTrue(parentAfterAllFeaturesCounter < afterAllFeaturesCounter);
    }


    @BeforeEveryFeature
    public static void beforeFeature() {
        log.debug("beforeFeature beforeFeatureCounter is " + beforeFeatureCounter + " parent is: "
                + parentBeforeFeatureCounter);

        // Assert.assertTrue("parentBeforeFeatureCounter is " +
        // parentBeforeFeatureCounter + " beforeFeatureCounter is: "
        // + beforeFeatureCounter, parentBeforeFeatureCounter >
        // beforeFeatureCounter);

        beforeFeatureCounter++;
    }


    @BeforeEveryFeature
    public static void beforeFeature2() {
        log.debug("beforeFeature2");
        // this will get called second so the parent assertion isn't valid
        beforeFeatureCounter++;
    }


    @AfterEveryFeature
    public static void afterFeature() {
        log.debug("afterFeature");

        afterFeatureCounter++;

        // Assert.assertTrue("parentBeforeFeatureCounter is " +
        // parentBeforeFeatureCounter + " beforeFeatureCounter is: "
        // + beforeFeatureCounter, parentAfterFeatureCounter <
        // afterFeatureCounter);

    }


    @AfterEveryFeature
    public static void afterFeature2() {
        log.debug("afterFeature2");
        afterFeatureCounter++;
    }


    @BeforeEveryScenario
    public static void beforeScenario() {
        log.debug("beforeScenario");

        Assert.assertTrue(parentBeforeScenarioCounter > beforeScenarioCounter);

        beforeScenarioCounter++;
    }


    @AfterEveryScenario
    public static void afterScenario() {
        log.debug("afterScenario");

        afterScenarioCounter++;

        Assert.assertTrue(parentAfterScenarioCounter < afterScenarioCounter);
    }


    @BeforeClass
    public static void resetCounters() {
        beforeAllFeaturesCounter = 0;
        afterAllFeaturesCounter = 0;
        beforeFeatureCounter = 0;
        afterFeatureCounter = 0;
        beforeScenarioCounter = 0;
        afterScenarioCounter = 0;

        // reset parent counters too

        parentBeforeAllFeaturesCounter = 0;
        parentAfterAllFeaturesCounter = 0;
        parentBeforeFeatureCounter = 0;
        parentAfterFeatureCounter = 0;
        parentBeforeScenarioCounter = 0;
        parentAfterScenarioCounter = 0;
    }


    // this test will run a set of features and the before / after methods in
    // this class
    @Test
    public void testRunBeforesAndAfters() {
        final JunitFeatureRunner runner = new JunitFeatureRunner(testNotifier);

        final List<Class<?>> stepImplsList = new ArrayList<Class<?>>();
        stepImplsList.add(BDDRunnerStepImplementations.class);

        // @FeatureFiles(featureFile = "./target/test-classes/features",
        // subStepsFile = "./target/test-classes/substeps", stepImplementations
        // =
        // { BDDRunnerStepImplementations.class }, tagList =
        // "@beforesAndAfters")

        runner.init(this.getClass(), stepImplsList, "./target/test-classes/features",
                "@beforesAndAfters", "./target/test-classes/substeps/simple.substeps");

        final BDDRunnerStepImplementations stepImpls = new BDDRunnerStepImplementations(this);

        final HashMap<Class<?>, Object> implsCache = getImplsCache(runner);

        implsCache.put(BDDRunnerStepImplementations.class, stepImpls);

        final RunNotifier notifier = mock(RunNotifier.class);

        runner.run(notifier);

        // now verify that what was run was indeed run

        // should run 2 features, 3 scenarios

        Assert.assertThat(beforeAllFeaturesCounter, is(1));
        Assert.assertThat(beforeFeatureCounter, is(4)); // two before setup
                                                        // methods per feature
        Assert.assertThat(beforeScenarioCounter, is(3));
        Assert.assertThat(afterScenarioCounter, is(3));
        Assert.assertThat(afterFeatureCounter, is(4));
        Assert.assertThat(afterAllFeaturesCounter, is(1));

    }
}
