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
package com.technophobia.substeps.report;

public class TestCounterSet {
    private String tag = null;
    private TestCounters featureStats = new TestCounters();
    private TestCounters scenarioStats = new TestCounters();
    private TestCounters scenarioStepStats = new TestCounters();


    public String getTag() {
        return tag;
    }


    public void setTag(final String tag) {
        this.tag = tag;
    }


    public TestCounters getFeatureStats() {
        return featureStats;
    }


    public void setFeatureStats(final TestCounters featureStats) {
        this.featureStats = featureStats;
    }


    public TestCounters getScenarioStats() {
        return scenarioStats;
    }


    public void setScenarioStats(final TestCounters scenarioStats) {
        this.scenarioStats = scenarioStats;
    }


    public TestCounters getScenarioStepStats() {
        return scenarioStepStats;
    }


    public void setScenarioStepStats(final TestCounters scenarioStepStats) {
        this.scenarioStepStats = scenarioStepStats;
    }

}