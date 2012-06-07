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
package com.technophobia.substeps.runner.setupteardown;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.technophobia.substeps.execution.ImplementationCache;
import com.technophobia.substeps.runner.BeforeAndAftersStaticTest;
import com.technophobia.substeps.runner.BeforeAndAftersTestParent;
import com.technophobia.substeps.runner.setupteardown.Annotations.BeforeAndAfterProcessors;
import com.technophobia.substeps.runner.setupteardown.fake.BeforeAndAfterSequencing1;
import com.technophobia.substeps.runner.setupteardown.fake.BeforeAndAfterSequencing2;
import com.technophobia.substeps.runner.setupteardown.fake.BeforeAndAfterSequencing3;

/**
 * 
 * 
 * @author imoore
 * 
 */
public class SetupAndTearDownTest {
    @BeforeClass
    public static void resetCounters() {
        BeforeAndAftersStaticTest.beforeAllFeaturesCounter = 0;
        BeforeAndAftersStaticTest.afterAllFeaturesCounter = 0;
        BeforeAndAftersStaticTest.beforeFeatureCounter = 0;
        BeforeAndAftersStaticTest.afterFeatureCounter = 0;
        BeforeAndAftersStaticTest.beforeScenarioCounter = 0;
        BeforeAndAftersStaticTest.afterScenarioCounter = 0;

        // reset parent counters too

        BeforeAndAftersTestParent.parentBeforeAllFeaturesCounter = 0;
        BeforeAndAftersTestParent.parentAfterAllFeaturesCounter = 0;
        BeforeAndAftersTestParent.parentBeforeFeatureCounter = 0;
        BeforeAndAftersTestParent.parentAfterFeatureCounter = 0;
        BeforeAndAftersTestParent.parentBeforeScenarioCounter = 0;
        BeforeAndAftersTestParent.parentAfterScenarioCounter = 0;
    }

    @BeforeAndAfterProcessors({ BeforeAndAfterSequencing3.class, BeforeAndAfterSequencing2.class,
            BeforeAndAfterSequencing1.class })
    public static class ClassWithBeforesAndAfters {
        // no op
    }


    @Test
    public void testOrderingOfSetupAndTearDown() {

        final SetupAndTearDown setupAndTearDown = new SetupAndTearDown(new Class<?>[] {
                BeforeAndAfterSequencing3.class, BeforeAndAfterSequencing2.class,
                BeforeAndAfterSequencing1.class }, new ImplementationCache());

        try {
            setupAndTearDown.runBeforeAll();
            setupAndTearDown.runAfterAll();
        } catch (final Throwable e) {

            e.printStackTrace();
            Assert.fail("befores and afters shouldn't fail for this test");
        }
        // check execution order:
        // BeforeAndAfterSequencing3, BeforeAndAfterSequencing2,
        // BeforeAndAfterSequencing1

        Assert.assertTrue("before all features executuon order incorrect",
                BeforeAndAfterSequencing3.beforeFeaturesExecTime > 0);

        Assert.assertTrue(
                "before all features executuon order incorrect",
                BeforeAndAfterSequencing3.beforeFeaturesExecTime < BeforeAndAfterSequencing2.beforeFeaturesExecTime);

        Assert.assertTrue(
                "before all features executuon order incorrect",
                BeforeAndAfterSequencing2.beforeFeaturesExecTime < BeforeAndAfterSequencing1.beforeFeaturesExecTime);

        // check the tear down order
        // BeforeAndAfterSequencing1, BeforeAndAfterSequencing2,
        // BeforeAndAfterSequencing3

        Assert.assertTrue("before all features executuon order incorrect",
                BeforeAndAfterSequencing1.afterAllFeaturesExecTime > 0);

        Assert.assertTrue(
                "before all features executuon order incorrect",
                BeforeAndAfterSequencing1.afterAllFeaturesExecTime < BeforeAndAfterSequencing2.afterAllFeaturesExecTime);

        Assert.assertTrue(
                "before all features executuon order incorrect",
                BeforeAndAfterSequencing2.afterAllFeaturesExecTime < BeforeAndAfterSequencing3.afterAllFeaturesExecTime);

    }

    // TODO - refactoring - are these tests worth it any more?

    // @Test
    // public void runningBeforeAllFeaturesExecutesAppropriateMethods() throws
    // Throwable {
    // final MethodExecutor methodExecutor1 = mock(MethodExecutor.class,
    // "methodExecutor1");
    // // final MethodExecutor methodExecutor2 = mock(MethodExecutor.class,
    // // "methodExecutor2");
    //
    // final SetupAndTearDown setupAndTearDown = new
    // SetupAndTearDown(methodExecutor1);// ,
    // // methodExecutor2);
    // // setupAndTearDown.initialise(BeforeAndAftersStaticTest.class);
    // setupAndTearDown.runBeforeAll();
    //
    // verify(methodExecutor1, times(1)).executeMethods(MethodState.BEFORE_ALL);
    //
    // // verify(methodExecutor2,
    // // times(1)).executeMethods(BeforeAndAftersStaticTest.class,
    // // MethodState.BEFORE_ALL);
    // }
    //
    //
    // @Test
    // public void runningAfterAllFeaturesExecutesAppropriateMethods() throws
    // Throwable {
    // final MethodExecutor methodExecutor1 = mock(MethodExecutor.class,
    // "methodExecutor1");
    // // final MethodExecutor methodExecutor2 = mock(MethodExecutor.class,
    // // "methodExecutor2");
    //
    // final SetupAndTearDown setupAndTearDown = new
    // SetupAndTearDown(methodExecutor1);// ,
    // // methodExecutor2);
    // // setupAndTearDown.initialise(BeforeAndAftersStaticTest.class);
    // setupAndTearDown.runAfterAll();
    //
    // verify(methodExecutor1, times(1)).executeMethods(MethodState.AFTER_ALL);
    // // verify(methodExecutor2,
    // // times(1)).executeMethods(BeforeAndAftersStaticTest.class,
    // // MethodState.AFTER_ALL);
    // }
    //
    //
    // @Test
    // public void runningBeforeEveryFeatureExecutesAppropriateMethods() throws
    // Throwable {
    // final MethodExecutor methodExecutor1 = mock(MethodExecutor.class,
    // "methodExecutor1");
    // // final MethodExecutor methodExecutor2 = mock(MethodExecutor.class,
    // // "methodExecutor2");
    //
    // final SetupAndTearDown setupAndTearDown = new
    // SetupAndTearDown(methodExecutor1);// ,
    // // methodExecutor2);
    // // setupAndTearDown.initialise(BeforeAndAftersStaticTest.class);
    // setupAndTearDown.runBeforeFeatures();
    //
    // verify(methodExecutor1,
    // times(1)).executeMethods(MethodState.BEFORE_FEATURES);
    // // verify(methodExecutor2,
    // // times(1)).executeMethods(BeforeAndAftersStaticTest.class,
    // // MethodState.BEFORE_FEATURES);
    // }
    //
    //
    // @Test
    // public void runningAfterEveryFeaturesExecutesAppropriateMethods() throws
    // Throwable {
    // final MethodExecutor methodExecutor1 = mock(MethodExecutor.class,
    // "methodExecutor1");
    // // final MethodExecutor methodExecutor2 = mock(MethodExecutor.class,
    // // "methodExecutor2");
    //
    // final SetupAndTearDown setupAndTearDown = new
    // SetupAndTearDown(methodExecutor1);// ,
    // // methodExecutor2);
    // // setupAndTearDown.initialise(BeforeAndAftersStaticTest.class);
    // setupAndTearDown.runAfterFeatures();
    //
    // verify(methodExecutor1,
    // times(1)).executeMethods(MethodState.AFTER_FEATURES);
    // // verify(methodExecutor2,
    // // times(1)).executeMethods(BeforeAndAftersStaticTest.class,
    // // MethodState.AFTER_FEATURES);
    // }
    //
    //
    // @Test
    // public void runningBeforeEveryScenarioExecutesAppropriateMethods() throws
    // Throwable {
    // final MethodExecutor methodExecutor1 = mock(MethodExecutor.class,
    // "methodExecutor1");
    // // final MethodExecutor methodExecutor2 = mock(MethodExecutor.class,
    // // "methodExecutor2");
    //
    // final SetupAndTearDown setupAndTearDown = new
    // SetupAndTearDown(methodExecutor1);// ,
    // // methodExecutor2);
    // // setupAndTearDown.initialise(BeforeAndAftersStaticTest.class);
    // setupAndTearDown.runBeforeScenarios();
    //
    // verify(methodExecutor1,
    // times(1)).executeMethods(MethodState.BEFORE_SCENARIOS);
    // // verify(methodExecutor2,
    // // times(1)).executeMethods(BeforeAndAftersStaticTest.class,
    // // MethodState.BEFORE_SCENARIOS);
    // }
    //
    //
    // @Test
    // public void runningAfterEveryScenarioExecutesAppropriateMethods() throws
    // Throwable {
    // final MethodExecutor methodExecutor1 = mock(MethodExecutor.class,
    // "methodExecutor1");
    // // final MethodExecutor methodExecutor2 = mock(MethodExecutor.class,
    // // "methodExecutor2");
    //
    // final SetupAndTearDown setupAndTearDown = new
    // SetupAndTearDown(methodExecutor1);// ,
    // // methodExecutor2);
    // // setupAndTearDown.initialise(BeforeAndAftersStaticTest.class);
    // setupAndTearDown.runAfterScenarios();
    //
    // verify(methodExecutor1,
    // times(1)).executeMethods(MethodState.AFTER_SCENARIOS);
    // // verify(methodExecutor2,
    // // times(1)).executeMethods(BeforeAndAftersStaticTest.class,
    // // MethodState.AFTER_SCENARIOS);
    // }

}
