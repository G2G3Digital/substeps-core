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
package com.technophobia.substeps.runner;

import static org.hamcrest.CoreMatchers.is;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.Description;

import com.technophobia.substeps.runner.JunitFeatureRunner;

/**
 * 
 * 
 * @author imoore
 * 
 */
public abstract class BaseJunitFeatureRunnerTest implements TestCallback {
	protected TestNotifier testNotifier = null;
	// List<MethodInvocation> invocations = null;

	protected List<DescriptionSnapshot> descriptionSnapshots = null;
	protected int descriptionSnapshotsIdx = 0;
	protected List<String> descriptionKeys = null;
	protected List<AssertionError> assertions = null;

	@Before
	public void before() {
		testNotifier = new TestNotifier();
		// invocations = new ArrayList<MethodInvocation>();
		descriptionKeys = new ArrayList<String>();

		descriptionSnapshots = new ArrayList<DescriptionSnapshot>();
		descriptionSnapshotsIdx = 0;

		assertions = new ArrayList<AssertionError>();
	}

	protected HashMap<Class<?>, Object> getImplsCache(final JunitFeatureRunner runner) {
		HashMap<Class<?>, Object> implsCache = null;

		try {
			final Field runnerField = runner.getClass().getDeclaredField("runner");
			runnerField.setAccessible(true);

			
//			final Field runnerField = runner.getClass().getDeclaredField("scenarioRunner");
//			runnerField.setAccessible(true);

			final Object runnerObject = runnerField.get(runner);

			final Field implCacheField = runnerObject.getClass().getDeclaredField("implsCache");
			implCacheField.setAccessible(true);

			implsCache = (HashMap<Class<?>, Object>) implCacheField.get(runnerObject);

		} catch (final Exception e) {
			e.printStackTrace();
		}

		Assert.assertNotNull("implsCache should not be null", implsCache);

		return implsCache;
	}

	/**
	 * @param description
	 */
	protected void buildDescriptionKeys(final Description description) {
		// add this description

		final String displayName = description.getDisplayName();
		final String key = displayName.split(" ")[0];
		descriptionKeys.add(key);

		if (description.getChildren() != null) {
			for (final Description child : description.getChildren()) {
				buildDescriptionKeys(child);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void doCallback(final String methodName, final String[] params) {
		System.out.println("got callback from method: " + methodName);

		// NB. this method will be called for every step - some of which will be
		// defined as substeps and therefore not have a description

		// methods may be called multiple times, however they will have
		// different descriptions...

		// where are we in the list of anticipated invocations?

		// check that all of the started in the snapshot have been fired

		// NB. this method is called as if it's the running test, so any failure
		// has no bearing on this test class succeeding or failing

		try {
			assertNotificationState();
		} catch (final java.lang.AssertionError ae) {
			assertions.add(ae);
		}

		// final Description current = testNotifier.getCurrentlyRunning();
		// if (current != null)
		// {
		// final String displayName = current.getDisplayName();
		//
		// final String key = displayName.split(" ")[0];
		//
		// }

		descriptionSnapshotsIdx++;
		// from this method name we want to get back to the description

		// check that when this method is invoked, fireTestStarted has been
		// invoked for this test, but not finished, and finished or failed for
		// prior tests and nothing for subsequent
	}

	/**
	 * 
	 */
	private void assertNotificationState() {
		final Description current = testNotifier.getCurrentlyRunning();

		if (!descriptionSnapshots.isEmpty()) {
			final DescriptionSnapshot snapshot = descriptionSnapshots.get(descriptionSnapshotsIdx);
			// TODO - add a currently running to the snapshot

			printState(snapshot);

			final List<String> shouldHaveBeenStarted = Arrays.asList(snapshot.getStarted());

			if (shouldHaveBeenStarted != null) {
				Assert.assertThat("incorrect number of starts", testNotifier.getAllStarted().size(),
						is(shouldHaveBeenStarted.size() + 1));
				// NB. + 1 because of the root description

				Assert.assertTrue("Not all descriptions that should have been started, have been",
						toKeyList(testNotifier.getAllStarted()).containsAll(shouldHaveBeenStarted));
			} else {
				Assert.assertThat(testNotifier.getAllStarted().size(), is(0));
			}
		}
		// TODO check completetions
	}

	List<String> toKeyList(final List<Description> dess) {
		List<String> rtn = null;
		if (dess != null) {
			rtn = new ArrayList<String>();
			for (final Description d : dess) {
				rtn.add(d.getDisplayName().split(" ")[0]);
			}
		}
		return rtn;
	}

	/**
	 * @param snapshot
	 */
	private void printState(final DescriptionSnapshot snapshot) {
		final String[] shouldHaveBeenStarted = snapshot.getStarted();

		if (shouldHaveBeenStarted != null) {
			for (final String s : shouldHaveBeenStarted) {
				System.out.println("should have started: " + s);
			}
		} else {
			System.out.println("nothing should have been started");
		}

		for (final Description des : testNotifier.getAllStarted()) {
			System.out.println("actually started: " + des.getDisplayName());
		}
	}

	public static class DescriptionSnapshot {
		private final String[] started;
		private final String[] finished;
		private final String[] failed;

		/**
		 * @param started
		 * @param finished
		 * @param failed
		 */
		public DescriptionSnapshot(final String[] started, final String[] finished, final String[] failed) {
			super();
			this.started = started;
			this.finished = finished;
			this.failed = failed;
		}

		public String[] getStarted() {
			return started;
		}

		public String[] getFinished() {
			return finished;
		}

		public String[] getFailed() {
			return failed;
		}

	}
}
