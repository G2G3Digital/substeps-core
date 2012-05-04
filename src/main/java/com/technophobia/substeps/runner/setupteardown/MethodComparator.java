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
import java.util.Comparator;
import java.util.List;

public class MethodComparator implements Comparator<Method> {
    private final List<Class<?>> classHierarchy;


    public MethodComparator(final List<Class<?>> classHierarchy) {
        this.classHierarchy = classHierarchy;
    }


    /**
     * {@inheritDoc}
     */
    public int compare(final Method m1, final Method m2) {
        int rtn = 0;
        final int m1Idx = classHierarchy.indexOf(m1.getDeclaringClass());

        final int m2Idx = classHierarchy.indexOf(m2.getDeclaringClass());

        if (m1Idx == -1 || m2Idx == -1) {
            throw new IllegalStateException("Got methods not in our known class hierachy");
        }

        if (m1Idx == m2Idx) {
            // both methods are in the same class
            rtn = m1.getName().compareTo(m2.getName());
        } else {
            rtn = m2Idx - m1Idx;
        }

        if (rtn == 0) {
            throw new IllegalStateException("Two methods can never be considered equal");
        }

        return rtn;
    }

}
