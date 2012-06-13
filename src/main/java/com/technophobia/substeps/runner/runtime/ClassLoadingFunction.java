package com.technophobia.substeps.runner.runtime;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;

public class ClassLoadingFunction implements Function<File, Class<?>> {

	private final Logger log = LoggerFactory
			.getLogger(ClassLoadingFunction.class);

	private final ClassLoader classLoader;

	private final String classesDirectory;

	public ClassLoadingFunction(final String classesDirectory) {
		this(
				new URLClassLoader(
						new URL[] { toDirectoryURL(classesDirectory) }),
				classesDirectory);
	}

	ClassLoadingFunction(final ClassLoader classLoader,
			final String classesDirectory) {
		this.classLoader = classLoader;
		this.classesDirectory = classesDirectory;
	}

	public Class<?> apply(final File file) {
		log.debug("About to try and load class " + file.getAbsolutePath());
		try {
			return classLoader.loadClass(toClassName(file.getAbsolutePath()));
		} catch (final ClassNotFoundException ex) {
			log.warn("Could not load class " + file.getAbsolutePath()
					+ ", returning null", ex);
			return null;
		}
	}

	private String toClassName(final String filePath)
			throws ClassNotFoundException {

		if (filePath.startsWith(classesDirectory)
				&& filePath.endsWith(".class")) {
			return filePath.substring(classesDirectory.length() + 1,
					filePath.length() - 6).replace(File.separatorChar, '.');
		}
		throw new ClassNotFoundException("Could not find class with path "
				+ filePath);
	}

	private static URL toDirectoryURL(final String classesDirectory) {
		try {
			return new URL("file:///" + ensureDirectoryFormat(classesDirectory));
		} catch (final MalformedURLException ex) {
			throw new IllegalStateException(
					"Could not create url for directory " + classesDirectory,
					ex);
		}
	}

	private static String ensureDirectoryFormat(final String classesDirectory) {
		return classesDirectory.endsWith("/") ? classesDirectory
				: classesDirectory + "/";
	}
}
