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
import com.technophobia.substeps.report.DefaultExecutionReportBuilder;


/**
 * @author ian
 * 
 */
public class EclipseDescriptionProvider implements DescriptionProvider {
    private final Logger log = LoggerFactory.getLogger(EclipseDescriptionProvider.class);

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

        private final List<MutableInteger> indexlist = new ArrayList<MutableInteger>();

        private static class MutableInteger {

            private int count = 0;


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

        DefaultExecutionReportBuilder.buildDescriptionString(status.getIndexStringForNode(node) +  ": "
        		, node, buf);
        
        // TODO - think on Jenkins the report looks like the dot is being
        // interpreted as package delimiter

        return buf.toString();
    }
}
