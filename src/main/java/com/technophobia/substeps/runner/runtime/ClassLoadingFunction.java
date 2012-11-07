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
package com.technophobia.substeps.runner.runtime;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;

public class ClassLoadingFunction implements Function<File, Class<?>> {

    private final Logger log = LoggerFactory.getLogger(ClassLoadingFunction.class);

    private final ClassLoader classLoader;

    private final String classesDirectory;


    public ClassLoadingFunction(final String classesDirectory) {
        this(new URLClassLoader(new URL[] { toDirectoryURL(classesDirectory) }), classesDirectory);
    }


    ClassLoadingFunction(final ClassLoader classLoader, final String classesDirectory) {
        this.classLoader = classLoader;
        this.classesDirectory = classesDirectory;
    }


    public Class<?> apply(final File file) {
        log.debug("About to try and load class " + file.getAbsolutePath());
        try {
            return classLoader.loadClass(toClassName(file.getAbsolutePath()));
        } catch (final ClassNotFoundException ex) {
            log.warn("Could not load class " + file.getAbsolutePath() + ", returning null", ex);
            return null;
        } catch (final NoClassDefFoundError ex) {
            log.warn("Could not load class " + file.getAbsolutePath() + ", returning null", ex);
            return null;
        }
    }


    private String toClassName(final String filePath) throws ClassNotFoundException {

        if (filePath.startsWith(classesDirectory) && filePath.endsWith(".class")) {
            return filePath.substring(classesDirectory.length() + 1, filePath.length() - 6).replace(File.separatorChar,
                    '.');
        }
        throw new ClassNotFoundException("Could not find class with path " + filePath);
    }


    private static URL toDirectoryURL(final String classesDirectory) {
        try {
            return new URL("file:///" + ensureDirectoryFormat(classesDirectory));
        } catch (final MalformedURLException ex) {
            throw new IllegalStateException("Could not create url for directory " + classesDirectory, ex);
        }
    }


    private static String ensureDirectoryFormat(final String classesDirectory) {
        return classesDirectory.endsWith("/") ? classesDirectory : classesDirectory + "/";
    }
}
