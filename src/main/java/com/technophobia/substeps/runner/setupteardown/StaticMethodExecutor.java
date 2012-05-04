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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
/**
 * Implementation of {@link MethodExecutor} that executes static methods
 * belonging to a target class
 * 
 * @author sforbes
 * 
 */
public class StaticMethodExecutor extends AbstractMethodExecutor {

    public void executeMethods(final Class<?> targetClass, final MethodState currentState) throws Throwable {
        final List<Method> methods = methodsForState(currentState);
        for (final Method method : methods) {
            runMethodExplosively(method, targetClass);
        }
    }


    protected List<Method> methodsFor(final Class<?> targetClass) {
        final Collection<Method> allMethods = Arrays.asList(targetClass.getMethods());
        return new ArrayList<Method>(Collections2.filter(allMethods, new Predicate<Method>() {
            public boolean apply(final Method method) {
                return Modifier.isStatic(method.getModifiers());
            }
        }));
    }


    private void runMethodExplosively(final Method m, final Class<?> targetClass) throws Throwable {
        if (m != null) {
            try {
                m.invoke(targetClass);
            }

            catch (final InvocationTargetException e) {
                throw e.getTargetException();
            } catch (final Throwable e) {
                throw e;
            }
        }
    }


	/* (non-Javadoc)
	 * @see uk.co.itmoore.bddrunner.runner.setupteardown.AbstractMethodExecutor#getInitialisationClasses(java.lang.Class)
	 */
	@Override
	protected Class<?>[] getInitialisationClasses(Class<?> targetClass)
	{
		return new Class<?>[]{targetClass};
	}
}
