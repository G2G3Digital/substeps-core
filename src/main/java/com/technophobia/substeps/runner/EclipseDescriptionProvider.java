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

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.technophobia.substeps.execution.ExecutionNode;
import com.technophobia.substeps.model.FeatureFile;
import com.technophobia.substeps.model.Scenario;
import com.technophobia.substeps.model.Step;


/**
 * @author ian
 * 
 */
public class EclipseDescriptionProvider implements DescriptionProvider {
    private final Logger log = LoggerFactory.getLogger(EclipseDescriptionProvider.class);

    private boolean verboseDescriptions = false;


    public EclipseDescriptionProvider() {
        final String envStr = System.getProperty("verboseDescriptions");
        if (envStr != null && Boolean.parseBoolean(envStr)) {
            verboseDescriptions = true;
        }

    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * uk.co.itmoore.bddrunner.runner.DescriptionProvider#buildDescription(java
     * .util.List)
     */
    @Deprecated
    public Description buildDescription(final ExecutionFilter executionFilter,
            final Class<?> classContainingTheTests, final List<FeatureFile> featureFileList) {
        final Description description = Description.createSuiteDescription(classContainingTheTests);

        int featureCount = 1;

        for (final FeatureFile ff : featureFileList) {

            log.debug("build desc for: " + ff.getName());

            if (executionFilter.isRunnable(ff)) {
                final Description child = buildFeatureDescription(executionFilter, ff, featureCount);
                if (child != null) {
                    featureCount++;
                    description.addChild(child);
                }
            }
        }

        log.trace("All Features:\n" + buildDescriptionString(description, 0));

        return description;
    }


    private Description buildFeatureDescription(final ExecutionFilter executionFilter,
            final FeatureFile ff, final int featureCount) {

        final List<Scenario> scenarios = ff.getScenarios();
        Description featureDescription = null;
        if (scenarios != null && !scenarios.isEmpty()) {
            featureDescription = buildDescription(featureCount + ". Feature: " + ff.getName());

            ff.setJunitDescription(featureDescription);

            int scenarioCount = 1;

            for (final Scenario sc : ff.getScenarios()) {
                if (executionFilter.isRunnable(sc)) {
                    final Description scenarioDes = buildScenarioDescription(sc, featureCount,
                            scenarioCount, ff.getName());

                    featureDescription.addChild(scenarioDes);
                    scenarioCount++;
                }
            }

        }
        return featureDescription;
    }


    private Description buildStepDescription(final Step step, final int featureCount,
            final int scenarioCount, final int stepCount, final int outLineCount,
            final String featureFile, final String scenarioName) {
        final Description des;

        final String descriptionString;

        if (outLineCount > 0) {

            // TODO - possibly change the Step.class here to use something else
            // instead - combination of Feature:Scenario ?

            if (verboseDescriptions) {
                descriptionString = String.format("%d.%s:%d.%s:%d.%d %s", featureCount,
                        featureFile, scenarioCount, scenarioName, outLineCount, stepCount,
                        step.toDebugString());
            } else {
                descriptionString = // Step.class.getName() + " " +
                featureCount + "." + scenarioCount + "." + outLineCount + "." + stepCount + " "
                        + step.toDebugString();
            }

        } else {

            if (verboseDescriptions) {
                descriptionString = String.format("%d.%s:%d.%s:%d %s", featureCount, featureFile,
                        scenarioCount, scenarioName, stepCount, step.toDebugString());
            } else {
                descriptionString = // Step.class.getName() + " " +
                featureCount + "." + scenarioCount + "." + stepCount + " " + step.toDebugString();
            }
        }

        des = buildDescription(descriptionString);

//        step.addJunitDescription(des);

        return des;
    }


    private Description buildScenarioDescription(final com.technophobia.substeps.model.Scenario sc,
            final int featureCount, final int scenarioCount, final String featureFile) {

        final Description des = buildDescription(featureCount + "." + scenarioCount + " Scenario: "
                + sc.getDescription());

        sc.setJunitDescription(des);

        if (sc.isOutline()) {
            // add a child for each outline
            final int outLineSize = sc.getExampleParameters().size();

            for (int i = 1; i <= outLineSize; i++) {
                int stepCount = 1;
                for (final Step step : sc.getSteps()) {

                    des.addChild(buildStepDescription(step, featureCount, scenarioCount, stepCount,
                            i, featureFile, sc.getDescription()));
                    stepCount++;
                }
            }
        } else {
            int stepCount = 1;
            for (final Step step : sc.getSteps()) {

                des.addChild(buildStepDescription(step, featureCount, scenarioCount, stepCount, 0,
                        featureFile, sc.getDescription()));
                stepCount++;
            }
        }

        return des;
    }


    /**
     * @param thisDescription2
     */
    private String buildDescriptionString(final Description des, final int depth) {
        final StringBuilder buf = new StringBuilder();

        for (int i = 0; i < depth; i++) {
            buf.append("\t");
        }

        buf.append(des.getDisplayName());
        buf.append("\n");

        final ArrayList<Description> children = des.getChildren();
        if (children != null) {
            for (final Description d : children) {
                buf.append(buildDescriptionString(d, depth + 1));
            }
        }
        return buf.toString();
    }


    private Description buildDescription(final String s) {

        Description newInstance = null;

        Constructor<Description> constructor;
        try {
            constructor = Description.class.getDeclaredConstructor(String.class,
                    Array.newInstance(Annotation.class, 0).getClass());
            constructor.setAccessible(true);

            newInstance = constructor.newInstance(s, null);
        } catch (final SecurityException e) {
            log.error(e.getMessage(), e);
        } catch (final NegativeArraySizeException e) {
            log.error(e.getMessage(), e);
        } catch (final NoSuchMethodException e) {
            log.error(e.getMessage(), e);
        } catch (final IllegalArgumentException e) {
            log.error(e.getMessage(), e);
        } catch (final InstantiationException e) {
            log.error(e.getMessage(), e);
        } catch (final IllegalAccessException e) {
            log.error(e.getMessage(), e);
        } catch (final InvocationTargetException e) {
            log.error(e.getMessage(), e);
        }

        Assert.assertNotNull(newInstance);
        return newInstance;
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * uk.co.itmoore.bddrunner.runner.DescriptionProvider#buildDescriptionMap
     * (uk.co.itmoore.bddrunner.execution.ExecutionNode)
     */

    public Map<Long, Description> buildDescriptionMap(final ExecutionNode rootNode,
            final Class<?> classContainingTheTests) {
        final Description rootDescription = Description
                .createSuiteDescription(classContainingTheTests);

        final Map<Long, Description> descriptionMap = new HashMap<Long, Description>();

        descriptionMap.put(Long.valueOf(rootNode.getId()), rootDescription);

        final DescriptorStatus status = new DescriptorStatus();

        if (rootNode.hasChildren()) {
            for (final ExecutionNode child : rootNode.getChildren()) {
                rootDescription.addChild(buildDescription(child, descriptionMap, status));
            }
        }

        return descriptionMap;
    }

    public static class DescriptorStatus {

        List<MutableInteger> indexlist = new ArrayList<MutableInteger>();

        private static class MutableInteger {

            public int count = 0;


            public void increment() {
                count++;
            }
        }


        public DescriptorStatus() {
            indexlist.add(new MutableInteger()); // ROOT

        }


        public String getIndexStringForNode(final ExecutionNode node) {

            // is this the first time at this depth?
            if (node.getDepth() > indexlist.size()) {

                // add a new Int
                indexlist.add(new MutableInteger());
            }
            if (node.getDepth() < indexlist.size()) {

                final List<MutableInteger> delete = new ArrayList<MutableInteger>();

                for (int i = node.getDepth(); i < indexlist.size(); i++) {
                    delete.add(indexlist.get(i));
                }
                indexlist.removeAll(delete);
            }

            final MutableInteger last = indexlist.get(node.getDepth() - 1);
            // increment the last one at this depth

            last.increment();

            final StringBuilder buf = new StringBuilder();
            boolean first = true;
            for (int i = 0; i < node.getDepth(); i++) {
                if (!first) {
                    buf.append("-");
                }
                buf.append(indexlist.get(i).count);
                first = false;
            }

            return buf.toString();
        }

    }


    private Description buildDescription(final ExecutionNode node,
            final Map<Long, Description> descriptionMap, final DescriptorStatus status) {
        final Description des = buildDescription(getDescriptionForNode(node, status));

        if (node.hasChildren() && node.getDepth() < 5) {

            for (final ExecutionNode child : node.getChildren()) {

                final Description childDescription = buildDescription(child, descriptionMap, status);
                if (childDescription != null) {
                    des.addChild(childDescription);
                }
            }
        }

        descriptionMap.put(Long.valueOf(node.getId()), des);

        return des;
    }


    /**
     * @param node
     * @return
     */
    private String getDescriptionForNode(final ExecutionNode node, final DescriptorStatus status) {
        final StringBuilder buf = new StringBuilder();

        // TODO - think on Jenkins the report looks like the dot is being
        // interpreted as package delimiter

        buf.append(status.getIndexStringForNode(node)).append(": ");

        if (node.getFeature() != null) {

            // buf.append("F: ").append(status.featureCount).append(": ")
            buf.append(node.getFeature().getName());

        } else if (node.getScenarioName() != null) {

            if (node.isOutlineScenario()) {
                buf.append("ScnO: ");
            } else {
                buf.append("Scn: ");
            }
            // buf.append(status.featureCount).append("-").append(status.scenarioCount).append(": ")
            buf.append(node.getScenarioName());
        }

        if (node.getParent() != null && node.getParent().isOutlineScenario()) {

            // buf.append("ScnO:").append(status.featureCount).append("-")
            // .append(status.scenarioCount).append("-")
            buf.append(node.getRowNumber()).append(" ").append(node.getParent().getScenarioName())
                    .append(":");
        }

        if (node.getLine() != null) {
            // buf.append("ScnO:").append(status.featureCount).append("-")
            // .append(status.scenarioCount).append("-").append(status.stepCount).append(": ")
            buf.append(node.getLine());
        }

        return buf.toString();
    }

    // private void populateDescriptionMap(final Map<Long, Description>
    // descriptionMap,
    // final ExecutionNode node, final Description parent) {
    // Description thisDescription = null;
    // if (node.getDepth() < 5) {
    // thisDescription = buildDescription(node.getDebugStringForThisNode());
    //
    // descriptionMap.put(Long.valueOf(node.getId()), thisDescription);
    //
    // if (parent != null) {
    // parent.addChild(thisDescription);
    //
    // }
    // }
    //
    // if (thisDescription != null && node.hasChildren()) {
    // for (final ExecutionNode child : node.getChildren()) {
    // populateDescriptionMap(descriptionMap, child, thisDescription);
    // }
    // }
    // }

}
