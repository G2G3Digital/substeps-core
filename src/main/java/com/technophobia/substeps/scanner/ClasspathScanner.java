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
package com.technophobia.substeps.scanner;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 
 * @author imoore
 * 
 */
public class ClasspathScanner {

	public List<Class<?>> getClassesWithAnnotation(final Class<? extends Annotation> desiredAnnotation,
			final ClassLoader classLoader, final String[] cpElements) {

		final List<Class<?>> classList = new ArrayList<Class<?>>();

		final List<String> classNameList = new ArrayList<String>();

		for (final String cpElement : cpElements) {
			final File f = new File(cpElement);

			if (f.exists() && f.isDirectory()) {
				final List<File> files = getAllFiles(f, "class");

				for (final File classFile : files) {
					classNameList.add(convertFileToClass(classFile, f));
				}

			} else {
				// jar file
				JarFile jarFile;
				try {
					jarFile = new JarFile(f);

					final Enumeration<JarEntry> entries = jarFile.entries();

					while (entries.hasMoreElements()) {
						final JarEntry nextElement = entries.nextElement();

						if (!nextElement.isDirectory()) {
							final String name = nextElement.getName();

							if (name.endsWith(".class")) {
								final String classname = name.replace(File.separatorChar, '.');
								classNameList.add(classname.substring(0, classname.length() - 6));
							}
						}
					}
				} catch (final IOException e) {
					// don't care
				}

				// load up contents of jar

			}

		}

		for (final String className : classNameList) {
			try {
				if (!className.contains("$")) {
					// no inner classes here thanks
					final Class<?> clazz = classLoader.loadClass(className);

					if (clazz.isAnnotationPresent(desiredAnnotation)) {
						classList.add(clazz);
					}
				}
			} catch (final NoClassDefFoundError e) {
				// don't care
			} catch (final ClassNotFoundException e) {
				// don't care about that eitehr
			}

		}

		return classList;

	}

	/**
	 * @param class1
	 * @param classLoader
	 * @return
	 */

	public List<Class<?>> getClassesWithAnnotation(final Class<? extends Annotation> desiredAnnotation,
			final ClassLoader classLoader) {
		// scan the classpath and look for classes with that annotation
		// any dirs on the classpath - recursively look into them

		final String cp = System.getProperty("java.class.path");

		// chop up the path into constituent parts

		final String[] cpElements = cp.split(String.valueOf(File.pathSeparatorChar));

		return getClassesWithAnnotation(desiredAnnotation, classLoader, cpElements);
	}

	private String convertFileToClass(final File f, final File root) {
		final String fqp = f.getAbsolutePath().substring(root.getAbsolutePath().length() + 1,
				f.getAbsolutePath().length() - 6);
		return fqp.replace(File.separatorChar, '.');
	}

	private List<File> getAllFiles(final File root, final String extension) {
		final FileFilter filter = new FileFilter() {
			public boolean accept(final File f) {
				return f.isDirectory() || (f.isFile() && f.getName().endsWith(extension));
			}
		};

		final List<File> files = new ArrayList<File>();

		if (root.exists()) {
			final File[] children = root.listFiles(filter);
			for (final File child : children) {
				if (child != null && child.exists()) {
					if (child.isDirectory()) {
						// recurse
						final List<File> childsFiles = getAllFiles(child, extension);
						files.addAll(childsFiles);
					} else {
						files.add(child);
					}
				}
			}
		}
		return files;
	}

}
