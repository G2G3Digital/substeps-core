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
package com.technophobia.substeps.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * represents a step that consists of sub steps
 * 
 * @author ian
 * 
 */
public class ParentStep {

	private static final Logger log = LoggerFactory.getLogger(ParentStep.class);


	private final Step parent;
	private List<Step> substeps;
	private ExampleParameter paramValueMap;

	public static final ParentStepNameComparator PARENT_STEP_COMPARATOR = new ParentStepNameComparator();

	public int getSourceLineNumber() {
		return this.parent.getSourceLineNumber();
	}

	public ParentStep(final Step parent) {
		this.parent = parent;
	}

	public void addStep(final Step step) {
		if (this.substeps == null) {
			this.substeps = new ArrayList<Step>();
		}
		this.substeps.add(step);
	}

	/**
	 * @return
	 */
	public Step getParent() {
		return this.parent;
	}

	/**
	 * @return
	 */
	public List<Step> getSteps() {
		return this.substeps;
	}

	/**
	 * @param step
	 */
	// only called by tests
	public void initialiseParamValues(final Step step) {
		final HashMap<String, String> map = new HashMap<String, String>();

		final String[] paramValues = Util.getArgs(this.parent.getPattern(),
				step.getLine(), null);

		if (paramValues != null) {
			for (int i = 0; i < paramValues.length; i++) {
				map.put(this.parent.getParamNames().get(i), paramValues[i]);
			}
		}
		this.paramValueMap = new ExampleParameter(step.getSourceLineNumber(),
				map);
	}

	public void initialiseParamValues(final int lineNumber, final String line) {

		initialiseParamValues(lineNumber, line, null);
	}

	public void initialiseParamValues(final int lineNumber, final String line, String[] keywordPrecedence) {

		log.debug("initialiseParamValues with line: " + line);

		final String[] paramValues = Util.getArgs(this.parent.getPattern(),
				line, keywordPrecedence);

		if (paramValues != null) {

			final Map<String, String> map = new HashMap<String, String>();

			for (int i = 0; i < paramValues.length; i++) {

				String key = this.parent.getParamNames().get(i);

				if (key.equals("value1")){
					log.debug("break");
				}
				log.debug("putting value: " + paramValues[i] +
						" under key: " + key + " i " + i);

				map.put(this.parent.getParamNames().get(i), paramValues[i]);
			}
			this.paramValueMap = new ExampleParameter(lineNumber, map);
		}
	}

	public ExampleParameter getParamValueMap() {
		return this.paramValueMap;
	}

	public final String getSubStepFileUri() {
		return this.getParent().getSource().getAbsolutePath();
	}

	public final String getSubStepFile() {
		return this.getParent().getSource().getName();
	}

	/**
	 * @param altLine
	 * @return
	 */
	public ParentStep cloneWithAltLine(final String altLine) {
		final ParentStep clone = new ParentStep(
				this.parent.cloneWithAlternativeLine(altLine));
		// clone.initialiseParamValues(clone.parent.getParameterLine());

		clone.substeps = this.substeps;
		clone.paramValueMap = this.paramValueMap;

		return clone;
	}
}
