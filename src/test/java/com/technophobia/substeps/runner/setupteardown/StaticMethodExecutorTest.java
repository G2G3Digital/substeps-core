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

import static com.technophobia.substeps.runner.setupteardown.fake.StaticAnnotatedMethodsFakeObject.isAfterAllFeaturesExecuted;
import static com.technophobia.substeps.runner.setupteardown.fake.StaticAnnotatedMethodsFakeObject.isAfterEveryFeatureExecuted;
import static com.technophobia.substeps.runner.setupteardown.fake.StaticAnnotatedMethodsFakeObject.isAfterEveryScenarioExecuted;
import static com.technophobia.substeps.runner.setupteardown.fake.StaticAnnotatedMethodsFakeObject.isBeforeAllFeaturesExecuted;
import static com.technophobia.substeps.runner.setupteardown.fake.StaticAnnotatedMethodsFakeObject.isBeforeEveryFeatureExecuted;
import static com.technophobia.substeps.runner.setupteardown.fake.StaticAnnotatedMethodsFakeObject.isBeforeEveryScenarioExecuted;
import static com.technophobia.substeps.runner.setupteardown.fake.StaticAnnotatedMethodsHierarchicalFakeParent.isAfterAllFeaturesHierarchyExecuted;
import static com.technophobia.substeps.runner.setupteardown.fake.StaticAnnotatedMethodsHierarchicalFakeParent.isAfterEveryFeatureHierarchyExecuted;
import static com.technophobia.substeps.runner.setupteardown.fake.StaticAnnotatedMethodsHierarchicalFakeParent.isAfterEveryScenarioHierarchyExecuted;
import static com.technophobia.substeps.runner.setupteardown.fake.StaticAnnotatedMethodsHierarchicalFakeParent.isBeforeAllFeaturesHierarchyExecuted;
import static com.technophobia.substeps.runner.setupteardown.fake.StaticAnnotatedMethodsHierarchicalFakeParent.isBeforeEveryFeatureHierarchyExecuted;
import static com.technophobia.substeps.runner.setupteardown.fake.StaticAnnotatedMethodsHierarchicalFakeParent.isBeforeEveryScenarioHierarchyExecuted;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Test;

import com.technophobia.substeps.runner.setupteardown.MethodExecutor;
import com.technophobia.substeps.runner.setupteardown.MethodState;
import com.technophobia.substeps.runner.setupteardown.StaticMethodExecutor;
import com.technophobia.substeps.runner.setupteardown.fake.StaticAnnotatedMethodsFakeObject;
import com.technophobia.substeps.runner.setupteardown.fake.StaticAnnotatedMethodsHierarchicalFakeObject;


public class StaticMethodExecutorTest {

    @Test
    public void canLocateStaticMethods() throws Throwable {
        final MethodExecutor executor = new StaticMethodExecutor();

        executor.locate(StaticAnnotatedMethodsFakeObject.class);
        checkNotSet(isBeforeAllFeaturesExecuted, isBeforeEveryFeatureExecuted, isBeforeEveryScenarioExecuted, isAfterEveryScenarioExecuted,
                isAfterEveryFeatureExecuted, isAfterAllFeaturesExecuted);

        executor.executeMethods(StaticAnnotatedMethodsFakeObject.class, MethodState.BEFORE_ALL);
        assertThat(isBeforeAllFeaturesExecuted, is(true));
        checkNotSet(isBeforeEveryFeatureExecuted, isBeforeEveryScenarioExecuted, isAfterEveryScenarioExecuted, isAfterEveryFeatureExecuted,
                isAfterAllFeaturesExecuted);

        executor.executeMethods(StaticAnnotatedMethodsFakeObject.class, MethodState.BEFORE_FEATURES);
        assertThat(isBeforeEveryFeatureExecuted, is(true));
        checkNotSet(isBeforeEveryScenarioExecuted, isAfterEveryScenarioExecuted, isAfterEveryFeatureExecuted, isAfterAllFeaturesExecuted);

        executor.executeMethods(StaticAnnotatedMethodsFakeObject.class, MethodState.BEFORE_SCENARIOS);
        assertThat(isBeforeEveryScenarioExecuted, is(true));
        checkNotSet(isAfterEveryScenarioExecuted, isAfterEveryFeatureExecuted, isAfterAllFeaturesExecuted);

        executor.executeMethods(StaticAnnotatedMethodsFakeObject.class, MethodState.AFTER_SCENARIOS);
        assertThat(isAfterEveryScenarioExecuted, is(true));
        checkNotSet(isAfterEveryFeatureExecuted, isAfterAllFeaturesExecuted);

        executor.executeMethods(StaticAnnotatedMethodsFakeObject.class, MethodState.AFTER_FEATURES);
        assertThat(isAfterEveryFeatureExecuted, is(true));
        checkNotSet(isAfterAllFeaturesExecuted);

        executor.executeMethods(StaticAnnotatedMethodsFakeObject.class, MethodState.AFTER_ALL);
        assertThat(isAfterAllFeaturesExecuted, is(true));
    }


    @SuppressWarnings("unchecked")
    @Test
    public void canLocateStaticMethodsInClassHierarchy() throws Throwable {
        final MethodExecutor executor = new StaticMethodExecutor();

        executor.locate(StaticAnnotatedMethodsHierarchicalFakeObject.class);
        checkNotSet(isBeforeAllFeaturesHierarchyExecuted, isBeforeEveryFeatureHierarchyExecuted, isBeforeEveryScenarioHierarchyExecuted,
                isAfterEveryScenarioHierarchyExecuted, isAfterEveryFeatureHierarchyExecuted, isAfterAllFeaturesHierarchyExecuted);

        executor.executeMethods(StaticAnnotatedMethodsFakeObject.class, MethodState.BEFORE_ALL);
        checkOrderOfExecution(isBeforeAllFeaturesHierarchyExecuted, "Parent", "Child");
        checkNotSet(isBeforeEveryFeatureHierarchyExecuted, isBeforeEveryScenarioHierarchyExecuted, isAfterEveryScenarioHierarchyExecuted,
                isAfterEveryFeatureHierarchyExecuted, isAfterAllFeaturesHierarchyExecuted);

        executor.executeMethods(StaticAnnotatedMethodsFakeObject.class, MethodState.BEFORE_FEATURES);
        checkOrderOfExecution(isBeforeEveryFeatureHierarchyExecuted, "Parent", "Child");
        checkNotSet(isBeforeEveryScenarioHierarchyExecuted, isAfterEveryScenarioHierarchyExecuted, isAfterEveryFeatureHierarchyExecuted,
                isAfterAllFeaturesHierarchyExecuted);

        executor.executeMethods(StaticAnnotatedMethodsFakeObject.class, MethodState.BEFORE_SCENARIOS);
        checkOrderOfExecution(isBeforeEveryScenarioHierarchyExecuted, "Parent", "Child");
        checkNotSet(isAfterEveryScenarioHierarchyExecuted, isAfterEveryFeatureHierarchyExecuted, isAfterAllFeaturesHierarchyExecuted);

        executor.executeMethods(StaticAnnotatedMethodsFakeObject.class, MethodState.AFTER_SCENARIOS);
        checkOrderOfExecution(isAfterEveryScenarioHierarchyExecuted, "Child", "Parent");
        checkNotSet(isAfterEveryFeatureHierarchyExecuted, isAfterAllFeaturesHierarchyExecuted);

        executor.executeMethods(StaticAnnotatedMethodsFakeObject.class, MethodState.AFTER_FEATURES);
        checkOrderOfExecution(isAfterEveryFeatureHierarchyExecuted, "Child", "Parent");
        checkNotSet(isAfterAllFeaturesHierarchyExecuted);

        executor.executeMethods(StaticAnnotatedMethodsFakeObject.class, MethodState.AFTER_ALL);
        checkOrderOfExecution(isAfterAllFeaturesHierarchyExecuted, "Child", "Parent");
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
}
