package com.technophobia.substeps.runner.runtime;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Predicate;
import com.technophobia.substeps.runner.runtime.StepClassFilter;
import com.technophobia.substeps.runner.runtime.fake.AnnotatedStepClassFake;
import com.technophobia.substeps.runner.runtime.fake.NonAnnotatedStepClassFake;

public class StepClassFilterTest {
	
	private Predicate<Class<?>> stepClassFilter;
	
	@Before
	public void initialise(){
		this.stepClassFilter = new StepClassFilter();
	}

	@Test
	public void findsClassesWithAnnotatedStepsCorrectly(){
		assertTrue(stepClassFilter.apply(AnnotatedStepClassFake.class));
	}
	
	@Test
	public void filtersOutNonStepClasses(){
		assertFalse(stepClassFilter.apply(NonAnnotatedStepClassFake.class));
	}
}
