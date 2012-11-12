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

import com.google.common.base.Strings;

public class Scenario extends RootFeature {
    @Override
    public String toString() {
        return "Scenario: " + description;
    }

    private String description;
    private List<Step> steps;
    private List<ExampleParameter> exampleParameters = null;
    private String[] paramNames = null;
    private List<Step> backgroundSteps;
    private boolean outline;

    private int scenarioLineNumber;
    private int exampleKeysLineNumber;

    private Description junitDescription;

    private String backgroundRawText;


    /**
     * @return the backgroundRawText
     */
    public String getBackgroundRawText() {
        return backgroundRawText;
    }


    public boolean hasBackground() {
        return !Strings.isNullOrEmpty(backgroundRawText);
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


    public List<Step> getBackgroundSteps() {
        return backgroundSteps;
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
     * @param background
     */
    public void addBackgroundStep(final Step background) {
        if (background != null) {
            if (backgroundSteps == null) {
                backgroundSteps = new ArrayList<Step>();
            }
            backgroundSteps.add(background);
        }
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
     * @param currentBackground
     */
    public void setBackgroundRawText(final String backgroundRawText) {
        this.backgroundRawText = backgroundRawText;
    }

}
