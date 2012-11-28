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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.technophobia.substeps.execution.ExecutionNode;

/**
 * @author ian
 * 
 */
public class ExecutionStats {
    private final TestCounterSet totals = new TestCounterSet();

    private final Map<String, TestCounterSet> taggedStats = new HashMap<String, TestCounterSet>();

    private List<TestCounterSet> sortedList = null;


    public void buildStats(final ReportData data) {
        for (final ExecutionNode node : data.getNodeList()) {
            final List<TestCounterSet> testStats = new ArrayList<TestCounterSet>();

            testStats.add(totals);

            final Set<String> tags = node.getTags();
            if (tags != null) {
                for (final String tag : tags) {
                    TestCounterSet testStatSet = taggedStats.get(tag);
                    if (testStatSet == null) {
                        testStatSet = new TestCounterSet();
                        testStatSet.setTag(tag);
                        taggedStats.put(tag, testStatSet);
                    }
                    testStats.add(testStatSet);
                }
            }
            for (final TestCounterSet testStatSet : testStats) {
                if (node.isFeature()) {
                    testStatSet.getFeatureStats().apply(node);
                } else if (node.isScenario()) {
                    testStatSet.getScenarioStats().apply(node);
                } else if (node.isStep()) {
                    testStatSet.getScenarioStepStats().apply(node);
                }
            }
        }
    }


    public int getTotalFeatures() {
        return totals.getFeatureStats().getCount();
    }


    public int getTotalFeaturesRun() {
        return totals.getFeatureStats().getRun();
    }


    public int getTotalFeaturesPassed() {
        return totals.getFeatureStats().getPassed();
    }


    public int getTotalFeaturesFailed() {
        return totals.getFeatureStats().getFailed();
    }


    public int getTotalFeaturesSkipped() {
        return totals.getFeatureStats().getIgnored();
    }


    public double getTotalFeaturesSuccess() {

        return totals.getFeatureStats().getSuccessPc();
    }


    public double getTotalFeaturesFailedPC() {

        return 100 - totals.getFeatureStats().getSuccessPc();
    }


    public int getTotalScenarios() {
        return totals.getScenarioStats().getCount();
    }


    public int getTotalScenariosRun() {
        return totals.getScenarioStats().getRun();
    }


    public int getTotalScenariosPassed() {
        return totals.getScenarioStats().getPassed();
    }


    public int getTotalScenariosFailed() {
        return totals.getScenarioStats().getFailed();
    }


    public int getTotalScenariosSkipped() {
        return totals.getScenarioStats().getIgnored();
    }


    public double getTotalScenariosSuccess() {

        return totals.getScenarioStats().getSuccessPc();
    }


    public int getTotalScenarioSteps() {
        return totals.getScenarioStepStats().getCount();
    }


    public int getTotalScenarioStepsRun() {
        return totals.getScenarioStepStats().getRun();
    }


    public int getTotalScenarioStepsPassed() {
        return totals.getScenarioStepStats().getPassed();
    }


    public int getTotalScenarioStepsFailed() {
        return totals.getScenarioStepStats().getFailed();
    }


    public int getTotalScenarioStepsSkipped() {
        return totals.getScenarioStepStats().getIgnored();
    }


    public double getTotalScenarioStepsSuccess() {

        return totals.getScenarioStepStats().getSuccessPc();

    }

    private static class TestStatSetComparator implements
            Comparator<TestCounterSet>, Serializable {
        private static final long serialVersionUID = -1736428075471005357L;


        /*
         * (non-Javadoc)
         * 
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(final TestCounterSet t1, final TestCounterSet t2) {
            // not sure which way around this is!!
            return t1.getScenarioStepStats().getFailed()
                    - t2.getScenarioStepStats().getFailed();
        }

    }


    /**
     * @return the sortedList
     */
    public List<TestCounterSet> getSortedList() {

        if (taggedStats != null) { //FIXME RB Removed && !taggedStats.isEmpty()
            sortedList = new ArrayList<TestCounterSet>();
            sortedList.addAll(taggedStats.values());

            if (taggedStats.size() > 1) {
                Collections.sort(sortedList, new TestStatSetComparator());
            }
        }

        return sortedList;
    }
}
