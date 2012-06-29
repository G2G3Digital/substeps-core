package com.technophobia.substeps.runner.runtime;

import java.lang.reflect.Method;

import com.google.common.base.Predicate;
import com.technophobia.substeps.model.SubSteps;

public class StepClassFilter implements Predicate<Class<?>> {

	public boolean apply(final Class<?> clazz) {
		for (final Method method : clazz.getMethods()) {
			if (method.isAnnotationPresent(SubSteps.Step.class)) {
				return true;
			}
		}
		return false;
	}

}
