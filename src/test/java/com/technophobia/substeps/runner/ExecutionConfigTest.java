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
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.technophobia.substeps.model.SubStepConfigurationException;
import com.technophobia.substeps.model.SubSteps.StepImplementations;

/**
 * @author ian
 *
 */
public class ExecutionConfigTest {

	public static class InitClass1{
	}

	public static class InitClass2{
	}

	public static class InitClass3{
	}

	public static class InitClass4{
	}

	public static class InitClass5{
	}

	@StepImplementations(requiredInitialisationClasses={InitClass1.class})
	public static class StepImplsClass1{
	}
	
	@StepImplementations(requiredInitialisationClasses={InitClass2.class, InitClass1.class})
	public static class StepImplsClass2{
	}

	@StepImplementations(requiredInitialisationClasses={InitClass3.class, InitClass2.class})
	public static class StepImplsClass3{
	}

	@StepImplementations(requiredInitialisationClasses={InitClass3.class, InitClass2.class})
	public static class StepImplsClass4{
	}


	
	
	@StepImplementations(requiredInitialisationClasses={InitClass1.class,InitClass2.class, InitClass3.class})
	public static class StepImplsClassA{
	}
	
	@StepImplementations(requiredInitialisationClasses={InitClass2.class, InitClass3.class})
	public static class StepImplsClassB{
	}

	@StepImplementations(requiredInitialisationClasses={InitClass1.class, InitClass4.class})
	public static class StepImplsClassC{
	}

	@StepImplementations(requiredInitialisationClasses={InitClass1.class, InitClass5.class, InitClass2.class})
	public static class StepImplsClassD{
	}

	@StepImplementations(requiredInitialisationClasses={InitClass2.class, InitClass5.class})
	public static class StepImplsClassE{
	}
	
	
	@Test
	public void testDeterminInitialisationClasses(){
		
		final ExecutionConfig config = new ExecutionConfig();
		
		final List<Class<?>> stepImplClasses = new ArrayList<Class<?>>();
		
		stepImplClasses.add(StepImplsClassA.class);
		stepImplClasses.add(StepImplsClassB.class);
		stepImplClasses.add(StepImplsClassC.class);
		stepImplClasses.add(StepImplsClassD.class);
		
		config.setStepImplementationClasses(stepImplClasses);
		
		final Class<?>[] initialisationClasses = config.determineInitialisationClasses();
		
		Assert.assertThat(initialisationClasses.length, is(5));
		
		int idx = 0;
		Assert.assertEquals(initialisationClasses[idx++], InitClass1.class);
		Assert.assertEquals(initialisationClasses[idx++], InitClass5.class);
		Assert.assertEquals(initialisationClasses[idx++], InitClass2.class);
		Assert.assertEquals(initialisationClasses[idx++], InitClass3.class);
		Assert.assertEquals(initialisationClasses[idx++], InitClass4.class);
	}

	@Test (expected=SubStepConfigurationException.class)
	public void testIncompatibleDeterminInitialisationClasses(){
		
		final ExecutionConfig config = new ExecutionConfig();
		
		final List<Class<?>> stepImplClasses = new ArrayList<Class<?>>();
		
		stepImplClasses.add(StepImplsClassA.class);
		stepImplClasses.add(StepImplsClassB.class);
		stepImplClasses.add(StepImplsClassC.class);
		stepImplClasses.add(StepImplsClassD.class);
		stepImplClasses.add(StepImplsClassE.class);
		
		config.setStepImplementationClasses(stepImplClasses);
		
		final Class<?>[] initialisationClasses = config.determineInitialisationClasses();
		
	}

	
	@Test
	public void testDeterminInitialisationClasses2(){
		
		final ExecutionConfig config = new ExecutionConfig();
		
		final List<Class<?>> stepImplClasses = new ArrayList<Class<?>>();
		
		stepImplClasses.add(StepImplsClass1.class);
		stepImplClasses.add(StepImplsClass2.class);
		stepImplClasses.add(StepImplsClass3.class);
		
		config.setStepImplementationClasses(stepImplClasses);
		
		final Class<?>[] initialisationClasses = config.determineInitialisationClasses();
		
		Assert.assertThat(initialisationClasses.length, is(3));

		Assert.assertEquals(initialisationClasses[0], InitClass3.class);
		Assert.assertEquals(initialisationClasses[1], InitClass2.class);
		Assert.assertEquals(initialisationClasses[2], InitClass1.class);

	}

	@Test
	public void testDeterminInitialisationClassesTheOldWay(){

		final ExecutionConfig config = new ExecutionConfig();
		
		final String[] initClasses = {"java.lang.String", "java.math.BigDecimal"};
		
		config.setInitialisationClass(initClasses);
		final Class<?>[] initialisationClasses = config.determineInitialisationClasses();

		Assert.assertEquals(initialisationClasses[0], String.class);
		Assert.assertEquals(initialisationClasses[1], BigDecimal.class);

	}
}
