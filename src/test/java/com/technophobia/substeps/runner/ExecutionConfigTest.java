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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.technophobia.substeps.model.SubSteps.StepImplementations;
import com.technophobia.substeps.model.exception.SubstepsConfigurationException;
import com.technophobia.substeps.runner.logger.AnsiColourExecutionLogger;

/**
 * @author ian
 * 
 */
public class ExecutionConfigTest {

    private static final String MUST_COME_BEFORE_AND_AFTER = " must come before and after ";
    private static final String THE_ORDER_IS_INVALID_AS = "The order is invalid as ";

    public static class InitClass1 {
    }

    public static class InitClass2 {
    }

    public static class InitClass3 {
    }

    public static class InitClass4 {
    }

    public static class InitClass5 {
    }

    public static class InitClass6 {
    }

    @StepImplementations(requiredInitialisationClasses = { InitClass1.class })
    public static class StepImplsClass1 {
    }

    @StepImplementations(requiredInitialisationClasses = { InitClass2.class, InitClass1.class })
    public static class StepImplsClass2_1 {
    }

    @StepImplementations(requiredInitialisationClasses = { InitClass3.class, InitClass2.class })
    public static class StepImplsClass3_2 {
    }

    @StepImplementations(requiredInitialisationClasses = { InitClass3.class, InitClass4.class, InitClass5.class })
    public static class StepImplsClass3_4_5 {
    }

    @StepImplementations(requiredInitialisationClasses = { InitClass3.class, InitClass6.class })
    public static class StepImplsClass3_6 {
    }

    @StepImplementations(requiredInitialisationClasses = { InitClass3.class, InitClass2.class })
    public static class StepImplsClass3_2_Duplicate {
    }

    @StepImplementations(requiredInitialisationClasses = { InitClass1.class, InitClass2.class, InitClass3.class })
    public static class StepImplsClass1_2_3 {
    }

    @StepImplementations(requiredInitialisationClasses = { InitClass1.class, InitClass4.class, InitClass3.class })
    public static class StepImplsClass1_4_3 {
    }

    @StepImplementations(requiredInitialisationClasses = { InitClass1.class, InitClass2.class, InitClass3.class,
            InitClass4.class })
    public static class StepImplsClass1_2_3_4 {
    }

    @StepImplementations(requiredInitialisationClasses = { InitClass2.class, InitClass3.class })
    public static class StepImplsClass2_3 {
    }

    @StepImplementations(requiredInitialisationClasses = { InitClass2.class, InitClass5.class, InitClass6.class })
    public static class StepImplsClass2_5_6 {
    }

    @StepImplementations(requiredInitialisationClasses = { InitClass5.class, InitClass6.class, InitClass1.class })
    public static class StepImplsClass5_6_1 {
    }

    @StepImplementations(requiredInitialisationClasses = { InitClass1.class, InitClass4.class })
    public static class StepImplsClass1_4 {
    }

    @StepImplementations(requiredInitialisationClasses = { InitClass1.class, InitClass5.class, InitClass2.class })
    public static class StepImplsClass1_5_2 {
    }

    @StepImplementations(requiredInitialisationClasses = { InitClass2.class, InitClass5.class })
    public static class StepImplsClass2_5 {
    }

    @Test
    public void testDeterminInitialisationClasses() {

        final ExecutionConfigWrapper config = new ExecutionConfigWrapper(new SubstepsExecutionConfig());

        final List<Class<?>> stepImplClasses = new ArrayList<Class<?>>();

        stepImplClasses.add(StepImplsClass1_2_3.class);
        stepImplClasses.add(StepImplsClass2_3.class);
        stepImplClasses.add(StepImplsClass1_4.class);
        stepImplClasses.add(StepImplsClass1_5_2.class);

        config.setStepImplementationClasses(stepImplClasses);

        final List<Class<?>> initialisationClasses = Arrays.asList(config.determineInitialisationClasses());

        Assert.assertThat(initialisationClasses.size(), is(5));

        assertThat(initialisationClasses, InitClass3.class, InitClass2.class);
        assertThat(initialisationClasses, InitClass2.class, InitClass1.class);
        assertThat(initialisationClasses, InitClass4.class, InitClass1.class);
        assertThat(initialisationClasses, InitClass2.class, InitClass5.class);
        assertThat(initialisationClasses, InitClass5.class, InitClass1.class);

    }

    public void assertThat(final List<Class<?>> within, final Class<?> initialisationClass, final Class<?> isPreceededBy) {

        final int index = within.indexOf(initialisationClass);
        final List<Class<?>> range = within.subList(0, index);
        Assert.assertTrue(range.contains(isPreceededBy));
    }

    @Test(expected = SubstepsConfigurationException.class)
    public void testIncompatibleDeterminInitialisationClasses() {

        final ExecutionConfigWrapper config = new ExecutionConfigWrapper(new SubstepsExecutionConfig());

        final List<Class<?>> stepImplClasses = new ArrayList<Class<?>>();

        stepImplClasses.add(StepImplsClass1_2_3.class);
        stepImplClasses.add(StepImplsClass2_3.class);
        stepImplClasses.add(StepImplsClass1_4.class);
        stepImplClasses.add(StepImplsClass1_5_2.class);
        stepImplClasses.add(StepImplsClass2_5.class);

        config.setStepImplementationClasses(stepImplClasses);

        try {
            config.determineInitialisationClasses();

        } catch (final SubstepsConfigurationException exception) {

            Assert.assertEquals(THE_ORDER_IS_INVALID_AS + InitClass5.class.getName() + MUST_COME_BEFORE_AND_AFTER
                    + InitClass2.class.getName(), exception.getMessage());
            throw exception;
        }
    }

    @Test
    public void testDeterminInitialisationClasses2() {

        final ExecutionConfigWrapper config = new ExecutionConfigWrapper(new SubstepsExecutionConfig());

        final List<Class<?>> stepImplClasses = new ArrayList<Class<?>>();

        stepImplClasses.add(StepImplsClass1.class);
        stepImplClasses.add(StepImplsClass2_1.class);
        stepImplClasses.add(StepImplsClass3_2.class);

        config.setStepImplementationClasses(stepImplClasses);

        final Class<?>[] initialisationClasses = config.determineInitialisationClasses();

        Assert.assertThat(initialisationClasses.length, is(3));

        Assert.assertEquals(initialisationClasses[0], InitClass3.class);
        Assert.assertEquals(initialisationClasses[1], InitClass2.class);
        Assert.assertEquals(initialisationClasses[2], InitClass1.class);

    }

    @Test
    public void testDeterminInitialisationClassesTheOldWay() {

        final SubstepsExecutionConfig configImpl = new SubstepsExecutionConfig();
        final ExecutionConfigWrapper config = new ExecutionConfigWrapper(configImpl);

        final String[] initClasses = { "java.lang.String", "java.math.BigDecimal" };

        configImpl.setInitialisationClass(initClasses);
        final Class<?>[] initialisationClasses = config.determineInitialisationClasses();

        Assert.assertEquals(initialisationClasses[0], String.class);
        Assert.assertEquals(initialisationClasses[1], BigDecimal.class);

    }

    @Test
    public void testDermineClassesForTPCLA223() {

        final ExecutionConfigWrapper config = new ExecutionConfigWrapper(new SubstepsExecutionConfig());

        final List<Class<?>> stepImplClasses = new ArrayList<Class<?>>();

        stepImplClasses.add(StepImplsClass1_2_3_4.class);
        stepImplClasses.add(StepImplsClass2_5_6.class);
        stepImplClasses.add(StepImplsClass3_6.class);

        config.setStepImplementationClasses(stepImplClasses);

        final List<Class<?>> initialisationClasses = Arrays.asList(config.determineInitialisationClasses());

        Assert.assertThat(initialisationClasses.size(), is(6));

        assertThat(initialisationClasses, InitClass4.class, InitClass3.class);
        assertThat(initialisationClasses, InitClass3.class, InitClass2.class);
        assertThat(initialisationClasses, InitClass2.class, InitClass1.class);
        assertThat(initialisationClasses, InitClass6.class, InitClass5.class);
        assertThat(initialisationClasses, InitClass5.class, InitClass2.class);
        assertThat(initialisationClasses, InitClass6.class, InitClass3.class);

        System.out.println(initialisationClasses);
    }

    @Test(expected = SubstepsConfigurationException.class)
    public void testDetermineClassesForInvalidLoop() {

        final ExecutionConfigWrapper config = new ExecutionConfigWrapper(new SubstepsExecutionConfig());

        final List<Class<?>> stepImplClasses = new ArrayList<Class<?>>();

        stepImplClasses.add(StepImplsClass1_2_3.class);
        stepImplClasses.add(StepImplsClass3_4_5.class);
        stepImplClasses.add(StepImplsClass5_6_1.class);

        config.setStepImplementationClasses(stepImplClasses);

        try {
            config.determineInitialisationClasses();
        } catch (final SubstepsConfigurationException sce) {

            Assert.assertEquals(THE_ORDER_IS_INVALID_AS + InitClass1.class.getName() + MUST_COME_BEFORE_AND_AFTER
                    + InitClass6.class.getName(), sce.getMessage());
            throw sce;
        }

    }

    @Test
    public void testINotiferAssignment() {

        final Class<?> clazz = AnsiColourExecutionLogger.class;

        Assert.assertTrue(IExecutionListener.class.isAssignableFrom(clazz));

    }

}
