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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Description;

public class FeatureFile extends RootFeature {

    private File sourceFile;
    private String name;
    private String description;
    private List<Scenario> scenarios;

    private Description junitDescription;


    @Override
    public String toString() {
        return "Feature: " + name;
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


    public List<Scenario> getScenarios() {
        return scenarios;
    }


    public void setName(final String name) {
        this.name = name;
    }


    public FeatureFile() {
    }


    public String getName() {

        return name;
    }


    public void addScenario(final Scenario sc) {
        if (scenarios == null) {

            scenarios = new ArrayList<Scenario>();
        }
        scenarios.add(sc);
    }


    /**
     * @return the sourceFile
     */
    public File getSourceFile() {
        return sourceFile;
    }


    /**
     * @param sourceFile
     *            the sourceFile to set
     */
    public void setSourceFile(final File sourceFile) {
        this.sourceFile = sourceFile;
    }

}
