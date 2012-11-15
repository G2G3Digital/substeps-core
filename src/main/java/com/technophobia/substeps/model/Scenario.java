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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.runner.Description;

public class Scenario extends RootFeature {
    @Override
    public String toString() {
        return "Scenario: " + description;
    }

    private String description;
    private Background background = null;
    private List<Step> steps;
    private List<ExampleParameter> exampleParameters = null;
    private String[] paramNames = null;
    private boolean outline;

    private int scenarioLineNumber;
    private int exampleKeysLineNumber;

    private Description junitDescription;


    private int sourceStartOffset = -1;
    private int sourceStartLineNumber = -1;
	private int sourceEndOffset = -1;
    /**
     * @return the background
     */
    public Background getBackground() {
        return background;
    }


    public void setBackground(final Background background) {
        this.background = background;
    }


    public boolean hasBackground() {
        return background != null;
    }


    /**
     * @return the junitDescription
     */
    public Description getJunitDescription() {
        return junitDescription;
    }


    /**
     * @param junitDescription
     *            the junitDescription to set
     */
    public void setJunitDescription(final Description junitDescription) {
        this.junitDescription = junitDescription;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(final String description) {
        this.description = description;
    }


    public List<Step> getSteps() {
        return steps;
    }


    public List<ExampleParameter> getExampleParameters() {
        return exampleParameters;
    }


    public void setOutline(final boolean outline) {
        this.outline = outline;
    }


    public int getScenarioLineNumber() {
        return scenarioLineNumber;
    }


    public int getExampleKeysLineNumber() {
        return exampleKeysLineNumber;
    }


    public void setScenarioLineNumber(final int scenarioLineNumber) {
        this.scenarioLineNumber = scenarioLineNumber;
    }


    public void setExampleKeysLineNumber(final int exampleKeysLineNumber) {
        this.exampleKeysLineNumber = exampleKeysLineNumber;
    }


    /**
     * @param cukeArg
     */
    public void addStep(final Step cukeArg) {
        if (cukeArg != null) {
            if (steps == null) {
                steps = new ArrayList<Step>();
            }
            steps.add(cukeArg);
        }
    }


    /**
     * @param split
     */
    public void addExampleKeys(final String[] split) {
        paramNames = split;
        exampleParameters = new ArrayList<ExampleParameter>();

    }


    public void addExampleValues(final int lineNumber, final String[] split) {
        // Cucumber compatibility - with cuke you can get away with not defining
        // all your columns of data, so we'll do the same
        final Map<String, String> row = new HashMap<String, String>();
        for (int i = 1; i < split.length; i++) {
            row.put(paramNames[i].trim(), split[i].trim());
        }
        exampleParameters.add(new ExampleParameter(lineNumber, row));
    }


    /**
     * @return
     */
    public boolean isOutline() {
        return outline;
    }

	/**
	 * @return the sourceStartOffset
	 */
	public int getSourceStartOffset() {
		return sourceStartOffset;
	}


	/**
	 * @param sourceStartOffset the sourceStartOffset to set
	 */
	public void setSourceStartOffset(final int sourceStartOffset) {
		this.sourceStartOffset = sourceStartOffset;
	}


	/**
	 * @return the sourceStartLineNumber
	 */
	public int getSourceStartLineNumber() {
		return sourceStartLineNumber;
	}


	/**
	 * @param sourceStartLineNumber the sourceStartLineNumber to set
	 */
	public void setSourceStartLineNumber(final int sourceStartLineNumber) {
		this.sourceStartLineNumber = sourceStartLineNumber;
	}


	/**
	 * @param end
	 */
	public void setSourceEndOffset(final int end) {
		this.sourceEndOffset = end;
	}


	/**
	 * @return the sourceEndOffset
	 */
	public int getSourceEndOffset() {
		return sourceEndOffset;
	}

}
