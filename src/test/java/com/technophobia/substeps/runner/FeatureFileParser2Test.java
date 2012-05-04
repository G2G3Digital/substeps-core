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

import java.io.File;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.technophobia.substeps.model.FeatureFile;
import com.technophobia.substeps.model.Scenario;
import com.technophobia.substeps.model.Step;
import com.technophobia.substeps.runner.FeatureFileParser;


/**
 * 
 * 
 * @author imoore
 * 
 */
public class FeatureFileParser2Test {

	@Test
	public void testFeatureFileParsing() {
		final FeatureFileParser parser = new FeatureFileParser();

		final FeatureFile ff = parser.loadFeatureFile(new File("./target/test-classes/features/example2.feature"));

		Assert.assertNotNull(ff);
		Assert.assertNotNull(ff.getName());

		Assert.assertNotNull(ff.getScenarios());

		Assert.assertThat(ff.getScenarios().size(), is(4));

		final Scenario sc1 = ff.getScenarios().get(1);
		Assert.assertTrue(sc1.hasBackground());
		Assert.assertThat(sc1.getBackgroundSteps().size(), is(1));
		Assert.assertThat(sc1.getSteps().size(), is(4));

		final Step withEmailAddress = ff.getScenarios().get(0).getSteps().get(0);
		Assert.assertThat(withEmailAddress.getLine(), is("Given something with an@emailaddress.com"));

		final Scenario sc2 = ff.getScenarios().get(2);
		Assert.assertTrue(sc2.isOutline());
		Assert.assertThat(sc2.getSteps().size(), is(6));

		Assert.assertThat(sc2.getExampleParameters().size(), is(8));

		final Scenario sc3 = ff.getScenarios().get(3);
		Assert.assertThat(sc3.getSteps().size(), is(5));

		final Step step = sc3.getSteps().get(2);
		Assert.assertThat(step.getInlineTable().size(), is(1));

		final Map<String, String> inlineTableRow0 = step.getInlineTable().get(0);

		Assert.assertThat(inlineTableRow0.size(), is(4));

		Assert.assertFalse(ff.getScenarios().get(0).getTags().isEmpty());

		// TODO - test out the tags

		final Set<String> tags = ff.getScenarios().get(0).getTags();
		Assert.assertNotNull(tags);
		Assert.assertTrue(tags.contains("@tag1"));
		Assert.assertTrue(tags.contains("@tag2"));

	}
}
