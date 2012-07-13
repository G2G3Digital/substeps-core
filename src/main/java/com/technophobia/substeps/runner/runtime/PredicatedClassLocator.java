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
