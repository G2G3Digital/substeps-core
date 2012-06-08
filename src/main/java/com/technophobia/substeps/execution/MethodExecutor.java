package com.technophobia.substeps.execution;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

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
public interface MethodExecutor {

    /**
     * @param setupAndTearDownMethods
     */
    void executeMethods(List<Method> setupAndTearDownMethods) throws Exception;


    void addImplementationClasses(final Class<?>[] implementationClasses);


    /**
     * @param targetClass
     * @param targetMethod
     * @param methodArgs
     */
    void executeMethod(final Class<?> targetClass, final Method targetMethod,
            final Object[] methodArgs) throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException;
}
