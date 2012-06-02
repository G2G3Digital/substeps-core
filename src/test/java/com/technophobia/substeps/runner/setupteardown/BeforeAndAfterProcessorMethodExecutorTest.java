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

import static com.technophobia.substeps.runner.setupteardown.fake.BeforeAndAfterHierarchicalMethodsParentClass.isAfterAllFeaturesHierarchyExecuted;
import static com.technophobia.substeps.runner.setupteardown.fake.BeforeAndAfterHierarchicalMethodsParentClass.isAfterEveryFeatureHierarchyExecuted;
import static com.technophobia.substeps.runner.setupteardown.fake.BeforeAndAfterHierarchicalMethodsParentClass.isAfterEveryScenarioHierarchyExecuted;
import static com.technophobia.substeps.runner.setupteardown.fake.BeforeAndAfterHierarchicalMethodsParentClass.isBeforeAllFeaturesHierarchyExecuted;
import static com.technophobia.substeps.runner.setupteardown.fake.BeforeAndAfterHierarchicalMethodsParentClass.isBeforeEveryFeatureHierarchyExecuted;
import static com.technophobia.substeps.runner.setupteardown.fake.BeforeAndAfterHierarchicalMethodsParentClass.isBeforeEveryScenarioHierarchyExecuted;
import static com.technophobia.substeps.runner.setupteardown.fake.BeforeAndAfterMethodsClass.isAfterAllFeaturesExecuted;
import static com.technophobia.substeps.runner.setupteardown.fake.BeforeAndAfterMethodsClass.isAfterEveryFeatureExecuted;
import static com.technophobia.substeps.runner.setupteardown.fake.BeforeAndAfterMethodsClass.isAfterEveryScenarioExecuted;
import static com.technophobia.substeps.runner.setupteardown.fake.BeforeAndAfterMethodsClass.isBeforeAllFeaturesExecuted;
import static com.technophobia.substeps.runner.setupteardown.fake.BeforeAndAfterMethodsClass.isBeforeEveryFeatureExecuted;
import static com.technophobia.substeps.runner.setupteardown.fake.BeforeAndAfterMethodsClass.isBeforeEveryScenarioExecuted;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Test;

import com.technophobia.substeps.runner.setupteardown.fake.BeforeAndAfterAnnotationProcessorFakeMultipleMethodsClass1;
import com.technophobia.substeps.runner.setupteardown.fake.BeforeAndAfterAnnotationProcessorFakeMultipleMethodsClass2;
import com.technophobia.substeps.runner.setupteardown.fake.BeforeAndAfterAnnotationProcessorFakeMultipleObject;
import com.technophobia.substeps.runner.setupteardown.fake.BeforeAndAfterAnnotationProcessorFakeObject;
import com.technophobia.substeps.runner.setupteardown.fake.BeforeAndAfterAnnotationProcessorHierarchicalFakeObject;
import com.technophobia.substeps.runner.setupteardown.fake.BeforeAndAfterHierarchicalMethodsClass;
import com.technophobia.substeps.runner.setupteardown.fake.BeforeAndAfterMethodsClass;


public class BeforeAndAfterProcessorMethodExecutorTest {

    @Test
    public void canLocateBeforeAndAfterAnnotatedMethods() throws Throwable {
        final BeforeAndAfterProcessorMethodExecutor executor = new BeforeAndAfterProcessorMethodExecutor();

        executor.setInitialisationClasses(new Class<?>[]{BeforeAndAfterMethodsClass.class});
        
        executor.locate(BeforeAndAfterAnnotationProcessorFakeObject.class);
        checkNotSet(isBeforeAllFeaturesExecuted, isBeforeEveryFeatureExecuted, isBeforeEveryScenarioExecuted, isAfterEveryScenarioExecuted,
                isAfterEveryFeatureExecuted, isAfterAllFeaturesExecuted);

        executor.executeMethods(BeforeAndAfterAnnotationProcessorFakeObject.class, MethodState.BEFORE_ALL);
        assertThat(isBeforeAllFeaturesExecuted, is(true));
        checkNotSet(isBeforeEveryFeatureExecuted, isBeforeEveryScenarioExecuted, isAfterEveryScenarioExecuted, isAfterEveryFeatureExecuted,
                isAfterAllFeaturesExecuted);

        executor.executeMethods(BeforeAndAfterAnnotationProcessorFakeObject.class, MethodState.BEFORE_FEATURES);
        assertThat(isBeforeEveryFeatureExecuted, is(true));
        checkNotSet(isBeforeEveryScenarioExecuted, isAfterEveryScenarioExecuted, isAfterEveryFeatureExecuted, isAfterAllFeaturesExecuted);

        executor.executeMethods(BeforeAndAfterAnnotationProcessorFakeObject.class, MethodState.BEFORE_SCENARIOS);
        assertThat(isBeforeEveryScenarioExecuted, is(true));
        checkNotSet(isAfterEveryScenarioExecuted, isAfterEveryFeatureExecuted, isAfterAllFeaturesExecuted);
    }


    @SuppressWarnings("unchecked")
    @Test
    public void canLocateBeforeAndAfterAnnotatedMethodsInClassHierarchy() throws Throwable {
        final BeforeAndAfterProcessorMethodExecutor executor = new BeforeAndAfterProcessorMethodExecutor();

        executor.setInitialisationClasses(new Class<?>[]{BeforeAndAfterHierarchicalMethodsClass.class});
        
        executor.locate(BeforeAndAfterAnnotationProcessorHierarchicalFakeObject.class);
        checkNotSet(isBeforeAllFeaturesHierarchyExecuted, isBeforeEveryFeatureHierarchyExecuted, isBeforeEveryScenarioHierarchyExecuted,
                isAfterEveryScenarioHierarchyExecuted, isAfterEveryFeatureHierarchyExecuted, isAfterAllFeaturesHierarchyExecuted);

        executor.executeMethods(BeforeAndAfterAnnotationProcessorHierarchicalFakeObject.class, MethodState.BEFORE_ALL);
        checkOrderOfExecution(isBeforeAllFeaturesHierarchyExecuted, "Parent", "Child");
        checkNotSet(isBeforeEveryFeatureHierarchyExecuted, isBeforeEveryScenarioHierarchyExecuted, isAfterEveryScenarioHierarchyExecuted,
                isAfterEveryFeatureHierarchyExecuted, isAfterAllFeaturesHierarchyExecuted);

        executor.executeMethods(BeforeAndAfterAnnotationProcessorHierarchicalFakeObject.class, MethodState.BEFORE_FEATURES);
        checkOrderOfExecution(isBeforeEveryFeatureHierarchyExecuted, "Parent", "Child");
        checkNotSet(isBeforeEveryScenarioHierarchyExecuted, isAfterEveryScenarioHierarchyExecuted, isAfterEveryFeatureHierarchyExecuted,
                isAfterAllFeaturesHierarchyExecuted);

        executor.executeMethods(BeforeAndAfterAnnotationProcessorHierarchicalFakeObject.class, MethodState.BEFORE_SCENARIOS);
        checkOrderOfExecution(isBeforeEveryScenarioHierarchyExecuted, "Parent", "Child");
        checkNotSet(isAfterEveryScenarioHierarchyExecuted, isAfterEveryFeatureHierarchyExecuted, isAfterAllFeaturesHierarchyExecuted);

        executor.executeMethods(BeforeAndAfterAnnotationProcessorHierarchicalFakeObject.class, MethodState.AFTER_SCENARIOS);
        checkOrderOfExecution(isAfterEveryScenarioHierarchyExecuted, "Child", "Parent");
        checkNotSet(isAfterEveryFeatureHierarchyExecuted, isAfterAllFeaturesHierarchyExecuted);

        executor.executeMethods(BeforeAndAfterAnnotationProcessorHierarchicalFakeObject.class, MethodState.AFTER_FEATURES);
        checkOrderOfExecution(isAfterEveryFeatureHierarchyExecuted, "Child", "Parent");
        checkNotSet(isAfterAllFeaturesHierarchyExecuted);

        executor.executeMethods(BeforeAndAfterAnnotationProcessorHierarchicalFakeObject.class, MethodState.AFTER_ALL);
        checkOrderOfExecution(isAfterAllFeaturesHierarchyExecuted, "Child", "Parent");
    }


    @Test
    public void canLocateBeforeAndAfterAnnotatedMethodsForMultipleAnnotationProcessors() throws Throwable {
        final BeforeAndAfterProcessorMethodExecutor executor = new BeforeAndAfterProcessorMethodExecutor();

        executor.setInitialisationClasses(new Class<?>[]{ BeforeAndAfterAnnotationProcessorFakeMultipleMethodsClass1.class, BeforeAndAfterAnnotationProcessorFakeMultipleMethodsClass2.class });
        
        executor.locate(BeforeAndAfterAnnotationProcessorFakeMultipleObject.class);
        checkNotSet(BeforeAndAfterAnnotationProcessorFakeMultipleMethodsClass1.isBeforeAllFeaturesExecuted,
                BeforeAndAfterAnnotationProcessorFakeMultipleMethodsClass2.isBeforeAllFeaturesExecuted);

        executor.executeMethods(BeforeAndAfterAnnotationProcessorFakeMultipleObject.class, MethodState.BEFORE_ALL);
        assertThat(BeforeAndAfterAnnotationProcessorFakeMultipleMethodsClass1.isBeforeAllFeaturesExecuted, is(true));
        assertThat(BeforeAndAfterAnnotationProcessorFakeMultipleMethodsClass2.isBeforeAllFeaturesExecuted, is(true));
    }


    private void checkNotSet(final boolean... fields) {
        for (final boolean field : fields) {
            assertThat(field, is(false));
        }
    }


    private void checkNotSet(final Collection<String>... fields) {
        for (final Collection<String> field : fields) {
            assertThat(field, is(Matchers.<String> empty()));
        }
    }


    private void checkOrderOfExecution(final List<String> property, final String... expectedOrder) {
        assertThat(property.size(), is(expectedOrder.length));
        for (int i = 0; i < property.size(); i++) {
            assertThat(property.get(i), is(expectedOrder[i]));
        }
    }
    // final MethodLocator locator = new BeforeAndAfterProcessorMethodLocator();
    //
    // final String[] expectedMethodNames = new String[] { //
    // "beforeAllFeatures", "afterAllFeatures", "beforeFeature", //
    // "beforeFeature2", "afterFeature", "afterFeature2", //
    // "beforeScenario", "afterScenario", "resetCounters" };
    //
    // checkMethodsArePresent(locator, BeforeAndAftersAnnotationTest.class,
    // expectedMethodNames);
    // }
}
