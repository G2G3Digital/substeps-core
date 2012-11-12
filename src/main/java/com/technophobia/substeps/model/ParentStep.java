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

/**
 * 
 * represents a step that consists of sub steps
 * 
 * @author ian
 * 
 */
public class ParentStep {
    private final String subStepFile;

    private final Step parent;
    private List<Step> substeps;
    private ExampleParameter paramValueMap;

    public static final ParentStepNameComparator PARENT_STEP_COMPARATOR = new ParentStepNameComparator();


    public int getSourceLineNumber() {
        return this.parent.getSourceLineNumber();
    }


    public ParentStep(final Step parent, final String subStepFile) {
        this.parent = parent;
        this.subStepFile = subStepFile;
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

        final String[] paramValues = Util.getArgs(this.parent.getPattern(), step.getLine());

        if (paramValues != null) {
            for (int i = 0; i < paramValues.length; i++) {
                map.put(this.parent.getParamNames().get(i), paramValues[i]);
            }
        }
        this.paramValueMap = new ExampleParameter(step.getSourceLineNumber(), map);
    }


    /**
     * @param step
     */
    public void initialiseParamValues(final int lineNumber, final String line) {

        final String[] paramValues = Util.getArgs(this.parent.getPattern(), line);

        if (paramValues != null) {

            final Map<String, String> map = new HashMap<String, String>();

            for (int i = 0; i < paramValues.length; i++) {
                map.put(this.parent.getParamNames().get(i), paramValues[i]);
            }
            this.paramValueMap = new ExampleParameter(lineNumber, map);
        }
    }


    public ExampleParameter getParamValueMap() {
        return this.paramValueMap;
    }


    public String getSubStepFile() {
        return this.subStepFile;
    }


    /**
     * @param altLine
     * @return
     */
    public ParentStep cloneWithAltLine(final String altLine) {
        final ParentStep clone = new ParentStep(this.parent.cloneWithAlternativeLine(altLine), this.subStepFile);
        // clone.initialiseParamValues(clone.parent.getParameterLine());

        clone.substeps = this.substeps;
        clone.paramValueMap = this.paramValueMap;

        return clone;
    }
}
