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
import java.util.Iterator;

import org.apache.commons.io.FileUtils;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;

public class PredicatedClassLocator implements ClassLocator {

	private final Predicate<Class<?>> predicate;
	private final Function<File, Class<?>> classLoader;

	public PredicatedClassLocator(final Predicate<Class<?>> predicate,
			final Function<File, Class<?>> classLoader) {
		this.predicate = predicate;
		this.classLoader = classLoader;
	}

	public Iterator<Class<?>> fromPath(final String path) {
		final File directory = new File(path);
		final Iterator<File> files = FileUtils.iterateFiles(directory,
				new String[] { "class" }, true);
		final Iterator<Class<?>> unsafeTransformedClasses = Iterators
				.transform(files, classLoader);

		return Iterators.filter(unsafeTransformedClasses,
				Predicates.and(Predicates.notNull(), predicate));
	}
}
