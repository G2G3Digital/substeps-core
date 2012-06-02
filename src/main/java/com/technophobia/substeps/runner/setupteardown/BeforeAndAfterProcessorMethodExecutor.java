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
import java.util.List;
import java.util.Map;

import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;

/**
 * Implementation of {@link MethodExecutor} that instantiates objects referenced
 * by the {@link BeforeAndAfterProcessor} and executes methods on them
 * 
 * @author sforbes
 * 
 */
public class BeforeAndAfterProcessorMethodExecutor extends AbstractMethodExecutor {

    private final Map<Class<?>, Object> instanceMap;


    public BeforeAndAfterProcessorMethodExecutor() {
        this.instanceMap = Maps.newHashMap();
    }

    public void executeMethods(final Class<?> targetClass, final MethodState currentState) throws Throwable {
        final List<Method> methodsForState = methodsForState(currentState);
        for (final Method method : methodsForState) {
            for (final Object object : findSuitableInstancesOf(method.getDeclaringClass())) {
                method.invoke(object);
            }
        }
    }

    @Override
	public void locate(final Class<?> targetClass) {
        super.locate(targetClass);

        for (final Class<?> processorClass : getInitialisationClasses(targetClass)) {
            instanceMap.put(processorClass, instantiate(processorClass));
        }
    }

    @Override
	protected List<Method> methodsFor(final Class<?> targetClass) {

        final List<Method> methods = new ArrayList<Method>();
        
        // these are the classes that have the before / after annotations
        Class<?>[] processorClasses = getInitialisationClasses(targetClass);
        
        for (final Class<?> processorClass : processorClasses) {
            appendMethodsIn(processorClass, methods);
        }
        return Collections.unmodifiableList(methods);
    }


   
    @Override
	protected List<Class<?>> classHierarchyFor(final Class<?> processorClass) {

//        final Class<?>[] processorClasses = getInitialisationClasses(targetClass);

        final List<Class<?>> hierarchy = new ArrayList<Class<?>>();

//        for (final Class<?> processorClass : processorClasses) {
            final List<Class<?>> classHierarchyForProcessor = super.classHierarchyFor(processorClass);
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
//        }

        return Collections.unmodifiableList(hierarchy);
    }


    private void appendMethodsIn(final Class<?> processorClass, final List<Method> methods) {
        Collections.addAll(methods, processorClass.getMethods());
    }

    private Class<?>[] initialisationClasses = null;
    
	@Override
	public Class<?>[] getInitialisationClasses(final Class<?> targetClass) {
        
		if (initialisationClasses != null) {
            return initialisationClasses;
        }
        return new Class<?>[0];
    }


    private Object instantiate(final Class<?> processorClass) {
        try {
            return processorClass.newInstance();
        } catch (final InstantiationException ex) {
            throw new IllegalStateException("Could not create instance of " + processorClass, ex);
        } catch (final IllegalAccessException ex) {
            throw new IllegalStateException("Could not create instance of " + processorClass, ex);
        }
    }


    private Collection<Object> findSuitableInstancesOf(final Class<?> methodClass) {
        final Collection<Class<?>> suitableClassDefs = Collections2.filter(instanceMap.keySet(), new Predicate<Class<?>>() {
            
            public boolean apply(final Class<?> instanceClass) {
                return methodClass.isAssignableFrom(instanceClass);
            }
        });

        return Collections2.transform(suitableClassDefs, Functions.forMap(instanceMap));
    }

	/**
	 * @return the initialisationClasses
	 */
	public Class<?>[] getInitialisationClasses()
	{
		return initialisationClasses;
	}

	/**
	 * @param initialisationClasses the initialisationClasses to set
	 */
	public void setInitialisationClasses(Class<?>[] initialisationClasses)
	{
		this.initialisationClasses = initialisationClasses;
	}
}
