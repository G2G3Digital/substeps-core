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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

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
public class BeforeAndAfterMethods {

    private final Map<MethodState, List<Method>> methodMap;

    private final Class<?>[] initialisationClasses;


    public BeforeAndAfterMethods(final Class<?>[] initialisationClasses) {

        methodMap = Maps.newHashMap();
        this.initialisationClasses = initialisationClasses;
        locateInitialisationMethods();
    }


    // from the previous base class

    private void sortMethodList() {

        // order of execution is base classes first, then the order of
        // processorClasses, reversed for tear downs

        // build up the order:
        final List<Class<?>> hierarchy = new ArrayList<Class<?>>();

        if (initialisationClasses != null) {
            for (int i = initialisationClasses.length; i > 0; i--) {

                hierarchy.addAll(classHierarchyFor(initialisationClasses[i - 1]));
            }
        }
        sortMethodLists(hierarchy);
    }


    protected List<Method> methodsForState(final MethodState methodState) {
        return methodMap.containsKey(methodState) ? methodMap.get(methodState) : Collections
                .<Method> emptyList();
    }


    private void updateMethodMapWith(final Method method) {

        for (final MethodState methodState : MethodState.values()) {

            updateMethodMapIfPresent(methodState, method);
        }
    }


    private void updateMethodMapIfPresent(final MethodState methodState, final Method method) {
        if (method.isAnnotationPresent(methodState.getAnnotationClass())) {
            addToMethodMap(methodState, method);
        }
    }


    private void addToMethodMap(final MethodState methodState, final Method method) {
        if (!methodMap.containsKey(methodState)) {
            methodMap.put(methodState, new ArrayList<Method>());
        }
        methodMap.get(methodState).add(method);
    }


    protected List<Class<?>> parentClassHierarchyFor(final Class<?> targetClass) {
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


    private void sortMethodList(final List<Method> methodsList,
            final Comparator<Method> methodComparator) {
        Collections.sort(methodsList, methodComparator);
    }


    public List<Method> getSetupAndTearDownMethods(final MethodState state) {
        return methodsForState(state);
    }


    private void locateInitialisationMethods() {

        final Collection<Method> methods = getAllInitialisationClassMethods();
        for (final Method method : methods) {
            updateMethodMapWith(method);
        }

        sortMethodList();
    }


    private List<Method> getAllInitialisationClassMethods() {

        final List<Method> methods = new ArrayList<Method>();

        // these are the classes that have the before / after annotations
        if (initialisationClasses != null) {
            for (final Class<?> processorClass : initialisationClasses) {
                appendMethodsIn(processorClass, methods);
            }
        }
        return Collections.unmodifiableList(methods);
    }


    private List<Class<?>> classHierarchyFor(final Class<?> processorClass) {

        final List<Class<?>> hierarchy = new ArrayList<Class<?>>();

        final List<Class<?>> classHierarchyForProcessor = parentClassHierarchyFor(processorClass);
        for (final Class<?> processorClassAncestor : classHierarchyForProcessor) {
            int superClassIndex = -1;
            for (int i = 0; i < hierarchy.size(); i++) {
                if (hierarchy.get(i).isAssignableFrom(processorClassAncestor)) {
                    superClassIndex = i;
                    break;
                }
            }
            if (superClassIndex == -1) {
                hierarchy.add(processorClassAncestor);
            } else {
                hierarchy.add(superClassIndex, processorClassAncestor);
            }
        }

        return Collections.unmodifiableList(hierarchy);
    }


    private void appendMethodsIn(final Class<?> processorClass, final List<Method> methods) {

        Collections.addAll(methods, processorClass.getMethods());
    }
}
