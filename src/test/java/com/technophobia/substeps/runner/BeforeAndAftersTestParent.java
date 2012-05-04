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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.technophobia.substeps.runner.JunitFeatureRunner.AfterAllFeatures;
import com.technophobia.substeps.runner.JunitFeatureRunner.AfterEveryFeature;
import com.technophobia.substeps.runner.JunitFeatureRunner.AfterEveryScenario;
import com.technophobia.substeps.runner.JunitFeatureRunner.BeforeAllFeatures;
import com.technophobia.substeps.runner.JunitFeatureRunner.BeforeEveryFeature;
import com.technophobia.substeps.runner.JunitFeatureRunner.BeforeEveryScenario;


/**
 * test class to check the order of befores and afters initialisation
 * 
 * @author imoore
 * 
 */
public class BeforeAndAftersTestParent extends BaseBDDRunnerTest {
	private static final Logger log = LoggerFactory.getLogger(BeforeAndAftersTestParent.class);

	public static int parentBeforeAllFeaturesCounter = 0;
	public static int parentAfterAllFeaturesCounter = 0;
	public static int parentBeforeFeatureCounter = 0;
	public static int parentAfterFeatureCounter = 0;
	public static int parentBeforeScenarioCounter = 0;
	public static int parentAfterScenarioCounter = 0;

	@BeforeAllFeatures
	public static final void rootBeforeAllFeatures() {
		log.debug("PARENT beforeAllFeatures");

		parentBeforeAllFeaturesCounter++;
	}

	@AfterAllFeatures
	public static final void rootAfterAllFeatures() {
		log.debug("PARENT afterAllFeatures");

		parentAfterAllFeaturesCounter++;
	}

	@BeforeEveryFeature
	public static final void rootBeforeFeature() {
		log.debug("PARENT beforeFeature");

		parentBeforeFeatureCounter++;
	}

	@AfterEveryFeature
	public static final void rootAfterFeature() {
		log.debug("PARENT afterFeature");

		parentAfterFeatureCounter++;
	}

	@BeforeEveryScenario
	public static final void rootBeforeScenario() {
		log.debug("PARENT beforeScenario");

		parentBeforeScenarioCounter++;
	}

	@AfterEveryScenario
	public static final void rootAfterScenario() {
		log.debug("PARENT afterScenario");

		parentAfterScenarioCounter++;
	}
}
