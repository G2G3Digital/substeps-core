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

import static org.hamcrest.CoreMatchers.is;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.technophobia.substeps.runner.setupteardown.fake.BeforeAndAfterAnnotationProcessorFakeMultipleMethodsClass1;
import com.technophobia.substeps.runner.setupteardown.fake.BeforeAndAfterAnnotationProcessorFakeMultipleMethodsClass2;
import com.technophobia.substeps.runner.setupteardown.fake.BeforeAndAfterHierarchicalMethodsClass;
import com.technophobia.substeps.runner.setupteardown.fake.BeforeAndAfterHierarchicalMethodsParentClass;
import com.technophobia.substeps.runner.setupteardown.fake.BeforeAndAfterMethodsClass;

public class BeforeAndAfterProcessorMethodExecutorTest {

    private Method getMethod(final Class clazz, final String methodName) {
        Method m = null;
        try {
            m = clazz.getDeclaredMethod(methodName);
        } catch (final SecurityException e) {
            e.printStackTrace();
        } catch (final NoSuchMethodException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull("methodName: " + methodName + " can't be null", m);
        return m;
    }


    @Test
    public void canLocateBeforeAndAfterAnnotatedMethods() throws Throwable {
        final BeforeAndAfterMethods executor = new BeforeAndAfterMethods(
                new Class<?>[] { BeforeAndAfterMethodsClass.class });

        checkMethodsAreIncluded(executor.getSetupAndTearDownMethods(MethodState.BEFORE_ALL),
                getMethod(BeforeAndAfterMethodsClass.class, "beforeAllFeatures"));

        checkMethodsAreIncluded(executor.getSetupAndTearDownMethods(MethodState.BEFORE_FEATURES),
                getMethod(BeforeAndAfterMethodsClass.class, "beforeEveryFeatures"));

        checkMethodsAreIncluded(executor.getSetupAndTearDownMethods(MethodState.BEFORE_SCENARIOS),
                getMethod(BeforeAndAfterMethodsClass.class, "beforeEveryScenario"));

    }


    private void checkMethodsAreIncluded(final List<Method> methodList, final Method... methods) {

        if (methods == null) {

            Assert.assertTrue(methodList.isEmpty());
        } else {
            Assert.assertThat(methodList.size(), is(methods.length));

            for (final Method m : methods) {

                Assert.assertTrue(methodList.contains(m));
            }
        }
    }


    @SuppressWarnings("unchecked")
    @Test
    public void canLocateBeforeAndAfterAnnotatedMethodsInClassHierarchy() throws Throwable {

        final BeforeAndAfterMethods executor = new BeforeAndAfterMethods(
                new Class<?>[] { BeforeAndAfterHierarchicalMethodsClass.class });

        checkMethodsAreIncluded(
                executor.getSetupAndTearDownMethods(MethodState.BEFORE_ALL),
                getMethod(BeforeAndAfterHierarchicalMethodsParentClass.class,
                        "beforeAllFeaturesParent"),
                getMethod(BeforeAndAfterHierarchicalMethodsClass.class, "beforeAllFeatures"));

        checkMethodsAreIncluded(
                executor.getSetupAndTearDownMethods(MethodState.BEFORE_FEATURES),
                getMethod(BeforeAndAfterHierarchicalMethodsParentClass.class,
                        "beforeEveryFeaturesParent"),
                getMethod(BeforeAndAfterHierarchicalMethodsClass.class, "beforeEveryFeatures"));

        checkMethodsAreIncluded(
                executor.getSetupAndTearDownMethods(MethodState.BEFORE_SCENARIOS),
                getMethod(BeforeAndAfterHierarchicalMethodsParentClass.class,
                        "beforeEveryScenarioParent"),
                getMethod(BeforeAndAfterHierarchicalMethodsClass.class, "beforeEveryScenario"));

        checkMethodsAreIncluded(
                executor.getSetupAndTearDownMethods(MethodState.AFTER_SCENARIOS),
                getMethod(BeforeAndAfterHierarchicalMethodsClass.class, "afterEveryScenario"),
                getMethod(BeforeAndAfterHierarchicalMethodsParentClass.class,
                        "afterEveryScenarioParent"));

        checkMethodsAreIncluded(
                executor.getSetupAndTearDownMethods(MethodState.AFTER_FEATURES),
                getMethod(BeforeAndAfterHierarchicalMethodsClass.class, "afterEveryFeatures"),
                getMethod(BeforeAndAfterHierarchicalMethodsParentClass.class,
                        "afterEveryFeaturesParent"));

        checkMethodsAreIncluded(
                executor.getSetupAndTearDownMethods(MethodState.AFTER_ALL),
                getMethod(BeforeAndAfterHierarchicalMethodsClass.class, "afterAllFeatures"),
                getMethod(BeforeAndAfterHierarchicalMethodsParentClass.class,
                        "afterAllFeaturesParent"));
    }


    @Test
    public void canLocateBeforeAndAfterAnnotatedMethodsForMultipleAnnotationProcessors()
            throws Throwable {

        final BeforeAndAfterMethods executor = new BeforeAndAfterMethods(new Class<?>[] {
                BeforeAndAfterAnnotationProcessorFakeMultipleMethodsClass1.class,
                BeforeAndAfterAnnotationProcessorFakeMultipleMethodsClass2.class });

        checkMethodsAreIncluded(
                executor.getSetupAndTearDownMethods(MethodState.BEFORE_ALL),
                getMethod(BeforeAndAfterAnnotationProcessorFakeMultipleMethodsClass1.class,
                        "beforeAllFeatures"),
                getMethod(BeforeAndAfterAnnotationProcessorFakeMultipleMethodsClass2.class,
                        "beforeAllFeatures"));
    }
}
