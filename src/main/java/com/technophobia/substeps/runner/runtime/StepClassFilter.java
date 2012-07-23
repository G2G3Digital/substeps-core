package com.technophobia.substeps.runner.runtime;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.google.common.base.Predicate;
import com.technophobia.substeps.model.SubSteps;

public class StepClassFilter implements Predicate<Class<?>> {

    public boolean apply(final Class<?> clazz) {
        if (hasAnnotation(clazz, SubSteps.AdditionalStepImplementations.class)) {
            return true;
        }
        for (final Method method : clazz.getMethods()) {
            if (hasAnnotation(method, SubSteps.Step.class)) {
                return true;
            }
        }
        return false;
    }


    private boolean hasAnnotation(final Method method, final Class<?> annotationClass) {
        return hasAnnotation(method.getAnnotations(), annotationClass);
    }


    private boolean hasAnnotation(final Class<?> clazz, final Class<?> annotationClass) {
        return hasAnnotation(clazz.getAnnotations(), annotationClass);
    }


    private boolean hasAnnotation(final Annotation[] annotations, final Class<?> annotationClass) {
        // compare the class names rather than the classes themselves, as its
        // entirely possible that the class was created in a different
        // classloader to
        // the current classs (e.g. if this is the eclipse plugin)
        for (final Annotation annotation : annotations) {
            if (annotation.annotationType().getName().equals(annotationClass.getName())) {
                return true;
            }
        }
        return false;
    }

}
