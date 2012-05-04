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
package com.technophobia.substeps.model;

import java.lang.reflect.Method;

/**
 * @author ian
 * 
 */
public class StepImplementation {
    /**
     * this is the pattern as defined in the step implementation
     * 
     */
    private final String value;
    private final Class<?> implementedIn;
    private final Method method;

    private final String keyword;


    public StepImplementation(final Class<?> loadedClass, final String keyword,
            final String valueString, final Method m) {
        implementedIn = loadedClass;
        value = valueString;
        method = m;
        this.keyword = keyword;
    }


    public static StepImplementation parse(final String fullLine, final Class<?> loadedClass,
            final Method m) {
        final int idx = fullLine.indexOf(' ');
        if (idx > 0) {
            final String key = fullLine.substring(0, idx);
            return new StepImplementation(loadedClass, key, fullLine, m);
        } else {
            final String key = fullLine;
            final String val = fullLine;
            return new StepImplementation(loadedClass, key, val, m);
        }
    }


    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }


    /**
     * @return the implementedIn
     */
    public Class<?> getImplementedIn() {
        return implementedIn;
    }


    /**
     * @return the method
     */
    public Method getMethod() {
        return method;
    }


    /**
     * @return
     */
    public String getKeyword() {
        return keyword;
    }


    public StepImplementation cloneWithKeyword(final String keyword) {
        return new StepImplementation(getImplementedIn(), keyword, getValue().replaceFirst(
                getKeyword(), keyword), getMethod());
    }

}