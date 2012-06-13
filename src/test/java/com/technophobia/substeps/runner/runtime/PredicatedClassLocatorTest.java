package com.technophobia.substeps.runner.runtime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.technophobia.substeps.runner.runtime.ClassLocator;
import com.technophobia.substeps.runner.runtime.PredicatedClassLocator;
import com.technophobia.substeps.runner.runtime.fake.AnnotatedStepClassFake;
import com.technophobia.substeps.runner.runtime.fake.NonAnnotatedStepClassFake;

public class PredicatedClassLocatorTest {

	private ClassLocator classLocator;

	private Predicate<Class<?>> predicate;
	private Function<File, Class<?>> classLoaderFunc;

	@SuppressWarnings("unchecked")
	@Before
	public void initialise() {
		this.predicate = mock(Predicate.class);
		this.classLoaderFunc = mock(Function.class);

		this.classLocator = new PredicatedClassLocator(predicate,
				classLoaderFunc);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void onlyFilesMatchingPredicateAreLocated() {
		when(predicate.apply(AnnotatedStepClassFake.class)).thenReturn(true);
		when(predicate.apply(NonAnnotatedStepClassFake.class))
				.thenReturn(false);

		final Class returnClass = AnnotatedStepClassFake.class;
		when(
				classLoaderFunc.apply(eq(new File(asFile("fake/"
						+ AnnotatedStepClassFake.class.getSimpleName()
						+ ".class"))))).thenReturn(returnClass);
		final Iterator<Class<?>> classes = classLocator
				.fromPath(asFile("fake"));
		assertTrue(classes.hasNext());
		assertEquals(AnnotatedStepClassFake.class, classes.next());
		assertFalse(classes.hasNext());
	}

	private String asFile(final String relPath) {
		return getClass().getResource(relPath).getFile();
	}
}
