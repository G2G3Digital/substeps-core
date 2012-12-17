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
package com.technophobia.substeps.execution;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
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
public class ImplementationCache implements MethodExecutor {

    protected final Map<Class<?>, Object> instanceMap;

    public ImplementationCache() {
        instanceMap = Maps.newHashMap();
    }

    public void addImplementationClasses(final Class<?>... implementationClasses) {

        if (implementationClasses != null) {
            for (final Class<?> implementationClass : implementationClasses) {
                if (!instanceMap.containsKey(implementationClass)) {
                    instanceMap.put(implementationClass, instantiate(implementationClass));
                }
            }
        }
    }

    private Object instantiate(final Class<?> implementationClass) {

        try {
            return implementationClass.newInstance();
        } catch (final InstantiationException ex) {
            throw new IllegalStateException("Could not create instance of " + implementationClass, ex);
        } catch (final IllegalAccessException ex) {
            throw new IllegalStateException("Could not create instance of " + implementationClass, ex);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.technophobia.substeps.execution.MethodExecutor#executeMethods(java
     * .util.List)
     */
    public void executeMethods(final List<Method> methods) throws Exception {

        for (final Method method : methods) {

            // TODO - curious about the findSuitableInstancesOf ? won't
            // method.getDeclaringClass be ok??

            for (final Object object : findSuitableInstancesOf(method.getDeclaringClass())) {

                method.invoke(object);

            }
        }
    }

    private Collection<Object> findSuitableInstancesOf(final Class<?> methodClass) {
        final Collection<Class<?>> suitableClassDefs = Collections2.filter(instanceMap.keySet(),
                new Predicate<Class<?>>() {

                    public boolean apply(final Class<?> instanceClass) {
                        return methodClass.isAssignableFrom(instanceClass);
                    }
                });

        return Collections2.transform(suitableClassDefs, Functions.forMap(instanceMap));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.technophobia.substeps.execution.MethodExecutor#executeMethod(java
     * .lang.Class, java.lang.reflect.Method, java.lang.Object[])
     */
    public void executeMethod(final Class<?> targetClass, final Method targetMethod, final Object[] methodArgs)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {

        addImplementationClasses(targetClass);

        if (methodArgs != null) {
            targetMethod.invoke(instanceMap.get(targetClass), methodArgs);
        } else {
            targetMethod.invoke(instanceMap.get(targetClass));
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getImplementation(Class<T> implementationClass) {

        addImplementationClasses(implementationClass);
        return (T) instanceMap.get(implementationClass);
    }

}
