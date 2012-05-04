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
    private Map<String, String> paramValueMap;

    public static final ParentStepNameComparator PARENT_STEP_COMPARATOR = new ParentStepNameComparator();


    public ParentStep(final Step parent, final String subStepFile) {
        this.parent = parent;
        this.subStepFile = subStepFile;
    }


    public void addStep(final Step step) {
        if (substeps == null) {
            substeps = new ArrayList<Step>();
        }
        substeps.add(step);
    }


    /**
     * @return
     */
    public Step getParent() {
        return parent;
    }


    /**
     * @return
     */
    public List<Step> getSteps() {
        return substeps;
    }


    /**
     * @param step
     */
    // only called by tests
    public void initialiseParamValues(final Step step) {
        paramValueMap = new HashMap<String, String>();

        final String[] paramValues = Util.getArgs(parent.getPattern(), step.getLine());

        if (paramValues != null) {
            for (int i = 0; i < paramValues.length; i++) {
                paramValueMap.put(parent.getParamNames().get(i), paramValues[i]);
            }
        }
    }


    /**
     * @param step
     */
    public void initialiseParamValues(final String line) {

        final String[] paramValues = Util.getArgs(parent.getPattern(), line);

        if (paramValues != null) {

            paramValueMap = new HashMap<String, String>();

            for (int i = 0; i < paramValues.length; i++) {
                paramValueMap.put(parent.getParamNames().get(i), paramValues[i]);
            }
        }
    }


    public Map<String, String> getParamValueMap() {
        return paramValueMap;
    }


    public String getSubStepFile() {
        return subStepFile;
    }


    /**
     * @param altLine
     * @return
     */
    public ParentStep cloneWithAltLine(final String altLine) {
        final ParentStep clone = new ParentStep(parent.cloneWithAlternativeLine(altLine),
                subStepFile);
        // clone.initialiseParamValues(clone.parent.getParameterLine());

        clone.substeps = substeps;
        clone.paramValueMap = paramValueMap;

        return clone;
    }
}
