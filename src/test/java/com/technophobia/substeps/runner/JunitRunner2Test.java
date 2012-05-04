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

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.technophobia.substeps.model.FeatureFile;
import com.technophobia.substeps.model.Scenario;
import com.technophobia.substeps.runner.TagManager;
import com.technophobia.substeps.runner.TestParameters;
import com.technophobia.substeps.steps.TestStepImplementations;


/**
 * @author imoore
 * 
 */
public class JunitRunner2Test {

	// TODO - doesn't acutally test the JunitRunner... these tests should be refactored into two
	
	@Test
	public void testTagAnnotations() {


		final List<Class<?>> stepImplsList = new ArrayList<Class<?>>();
		stepImplsList.add(TestStepImplementations.class);

		// pass in the stuff that would normally be placed in the annotation

		TagManager tagManager = new TagManager(null);
		TestParameters testParams = new TestParameters(tagManager, null, "./target/test-classes/features/tagged.feature");
		testParams.init();
		
		List<FeatureFile> featureFileList = testParams.getFeatureFileList();

		List<Scenario> scenarios = featureFileList.get(0).getScenarios();
		Assert.assertThat(scenarios.size(), is(4));

		for (final Scenario sc : scenarios) {
			if (! tagManager.acceptTaggedScenario(sc.getTags())) {
				Assert.fail("all scenarios should be runnable");
			}
		}


		TagManager tagManager2 = new TagManager("@runme");
		TestParameters testParams2 = new TestParameters(tagManager2, null, "./target/test-classes/features/tagged.feature");
		testParams2.init();
		
		featureFileList = testParams2.getFeatureFileList();

		scenarios = featureFileList.get(0).getScenarios();
		Assert.assertThat(scenarios.size(), is(4));

		final Set<String> excludedTaggedScenarios = new HashSet<String>();

		excludedTaggedScenarios.add("An excluded tagged scenario");
		excludedTaggedScenarios.add("An untagged scenario");
		excludedTaggedScenarios.add("multilined tagged scenario");

		for (final Scenario sc : scenarios) {
			
			if (tagManager2.acceptTaggedScenario(sc.getTags())) {
//			if (runner.isRunnable(sc)) {
				Assert.assertThat(sc.getDescription(), is("A tagged scenario"));
			} else {
				if (!excludedTaggedScenarios.contains(sc.getDescription())) {
					Assert.fail("expecting some excluded tags: " + sc.getDescription());
				}
			}
		}

		// check that the multiline tagged scenario works ok
		final Scenario scenario = featureFileList.get(0).getScenarios().get(3);

		Assert.assertThat("expecting a tag to be present", scenario.getTags(), hasItem("@all"));
		Assert.assertThat("expecting a tag to be present", scenario.getTags(), hasItem("@searchcontracts"));
		Assert.assertThat("expecting a tag to be present", scenario.getTags(), hasItem("@searchcontracts_30"));
	}
}
