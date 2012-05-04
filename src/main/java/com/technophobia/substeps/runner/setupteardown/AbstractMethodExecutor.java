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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


import com.google.common.collect.Maps;
import com.technophobia.substeps.runner.JunitFeatureRunner.AfterAllFeatures;
import com.technophobia.substeps.runner.JunitFeatureRunner.AfterEveryFeature;
import com.technophobia.substeps.runner.JunitFeatureRunner.AfterEveryScenario;
import com.technophobia.substeps.runner.JunitFeatureRunner.BeforeAllFeatures;
import com.technophobia.substeps.runner.JunitFeatureRunner.BeforeEveryFeature;
import com.technophobia.substeps.runner.JunitFeatureRunner.BeforeEveryScenario;

public abstract class AbstractMethodExecutor implements MethodExecutor {

    private final Map<MethodState, List<Method>> methodMap;


    public AbstractMethodExecutor() {
        this.methodMap = Maps.newHashMap();
    }


    public void locate(final Class<?> targetClass) {
        final Collection<Method> methods = methodsFor(targetClass);
        for (final Method method : methods) {
            updateMethodMapWith(method);
        }

        
//        sortMethodLists(classHierarchyFor(targetClass));
        
        sortMethodList(targetClass);
    }


    /**
	 * @param targetClass
	 */
	private void sortMethodList(Class<?> targetClass)
	{
        final Class<?>[] processorClasses = getInitialisationClasses(targetClass);
        
        // TODO - WIP - before / setup needs to take into account the order of processorClasses
        // if this is using static annotations on the test class itself then this will be just the test class
        // otherwise this will be a list
        
        // order of execution is base classes first, then the order of processorClasses, reversed for tear downs
        
        // build up the order:
        List<Class<?>> hierarchy = new ArrayList<Class<?>>();
        
        for (int i = processorClasses.length; i > 0; i--){
//        for (Class<?> processorClass : ){
        	
        	hierarchy.addAll(classHierarchyFor(processorClasses[i-1]));
        }
                
        sortMethodLists(hierarchy);
	}


	protected List<Method> methodsForState(final MethodState methodState) {
        return methodMap.containsKey(methodState) ? methodMap.get(methodState) : Collections.<Method> emptyList();
    }


    protected abstract List<Method> methodsFor(Class<?> targetClass);

    protected abstract Class<?>[] getInitialisationClasses(Class<?> targetClass);

    private void updateMethodMapWith(final Method method) {
        updateMethodMapIfPresent(BeforeAllFeatures.class, MethodState.BEFORE_ALL, method);
        updateMethodMapIfPresent(BeforeEveryFeature.class, MethodState.BEFORE_FEATURES, method);
        updateMethodMapIfPresent(BeforeEveryScenario.class, MethodState.BEFORE_SCENARIOS, method);
        updateMethodMapIfPresent(AfterEveryScenario.class, MethodState.AFTER_SCENARIOS, method);
        updateMethodMapIfPresent(AfterEveryFeature.class, MethodState.AFTER_FEATURES, method);
        updateMethodMapIfPresent(AfterAllFeatures.class, MethodState.AFTER_ALL, method);
    }


    private void updateMethodMapIfPresent(final Class<? extends Annotation> annotationClass, final MethodState methodState, final Method method) {
        if (method.isAnnotationPresent(annotationClass)) {
            addToMethodMap(methodState, method);
        }
    }


    private void addToMethodMap(final MethodState methodState, final Method method) {
        if (!methodMap.containsKey(methodState)) {
            methodMap.put(methodState, new ArrayList<Method>());
        }
        methodMap.get(methodState).add(method);
    }


    protected List<Class<?>> classHierarchyFor(final Class<?> targetClass) {
        final List<Class<?>> classHierarchy = new LinkedList<Class<?>>();

        classHierarchy.add(targetClass);

        Class<?> tempClass = targetClass;

        while (!tempClass.equals(Object.class)) {
            tempClass = tempClass.getSuperclass();

            // no need to add Object
            if (!tempClass.equals(Object.class)) {
                classHierarchy.add(tempClass);
            }
        }
        return Collections.unmodifiableList(classHierarchy);
    }


    private void sortMethodLists(final List<Class<?>> classHierarchy) {
        final Comparator<Method> methodComparator = new MethodComparator(classHierarchy);
        
        
        
        for (final MethodState methodState : methodMap.keySet()) {
            final List<Method> methodsForState = methodMap.get(methodState);
            sortMethodList(methodsForState, methodComparator);

            // Execute after tests in reverse order
            if (!methodState.isBeforeTest()) {
                Collections.reverse(methodsForState);
            }
        }
    }


    private void sortMethodList(final List<Method> methodsList, final Comparator<Method> methodComparator) {
        Collections.sort(methodsList, methodComparator);
    }
}
