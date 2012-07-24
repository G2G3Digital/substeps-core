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

import java.util.Map;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.technophobia.substeps.execution.ExecutionNode;

/**
 * A wrapper around the Junit notifier and any other registered test listeners
 * 
 * @author imoore
 * 
 */
public class JunitNotifier implements IJunitNotifier {

    private final Logger log = LoggerFactory.getLogger(JunitNotifier.class);

    private RunNotifier junitRunNotifier;

    private Map<Long, Description> descriptionMap;


    /**
     * {@inheritDoc}
     */
    private void notifyTestStarted(final Description junitDescription) {

        if (junitRunNotifier != null && junitDescription != null) {
            log.debug(junitDescription.getDisplayName() + " notifyTestStarted");

            junitRunNotifier.fireTestStarted(junitDescription);
        }

    }


    /**
     * {@inheritDoc}
     */
    private void notifyTestFinished(final Description junitDescription) {
        if (junitRunNotifier != null && junitDescription != null) {
            log.debug(junitDescription.getDisplayName() + " notifyTestFinished");

            junitRunNotifier.fireTestFinished(junitDescription);
        }

    }


    /**
     * {@inheritDoc}
     */
    private void notifyTestIgnored(final Description junitDescription) {
        if (junitRunNotifier != null && junitDescription != null) {
            junitRunNotifier.fireTestIgnored(junitDescription);
        }

    }


    /**
     * {@inheritDoc}
     */
    private void notifyTestFailed(final Description junitDescription, final Throwable cause) {
        if (junitRunNotifier != null && junitDescription != null) {
            log.debug(junitDescription.getDisplayName() + " notify running TestFailed");

            log.debug(junitDescription.getDisplayName() + " notify TestFailed");
            junitRunNotifier.fireTestFailure(new Failure(junitDescription, cause));

        }

    }


    /**
     * {@inheritDoc}
     */
    public void pleaseStop() {
        junitRunNotifier.pleaseStop();
    }


    /**
     * {@inheritDoc}
     */
    public void setJunitRunNotifier(final RunNotifier junitNotifier) {
        junitRunNotifier = junitNotifier;
    }


    /**
     * @param descriptionMap
     */
    public void setDescriptionMap(final Map<Long, Description> descriptionMap) {
        this.descriptionMap = descriptionMap;
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * com.technophobia.substeps.runner.AbstractBaseNotifier#handleNotifyNodeFailed
     * (com.technophobia.substeps.execution.ExecutionNode, java.lang.Throwable)
     */

    public void notifyNodeFailed(final ExecutionNode node, final Throwable cause) {

        final Description description = descriptionMap.get(Long.valueOf(node.getId()));
        notifyTestFailed(description, cause);

    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * com.technophobia.substeps.runner.AbstractBaseNotifier#handleNotifyNodeStarted
     * (com.technophobia.substeps.execution.ExecutionNode)
     */

    public void notifyNodeStarted(final ExecutionNode node) {

        final Description description = descriptionMap.get(Long.valueOf(node.getId()));

        final boolean b = description != null;
        log.debug("notifyTestStarted nodeid: " + node.getId() + " description: " + b);

        notifyTestStarted(description);

    }


    /*
     * (non-Javadoc)
     * 
     * @see com.technophobia.substeps.runner.AbstractBaseNotifier#
     * handleNotifyNodeFinished
     * (com.technophobia.substeps.execution.ExecutionNode)
     */

    public void notifyNodeFinished(final ExecutionNode node) {

        final Description description = descriptionMap.get(Long.valueOf(node.getId()));
        notifyTestFinished(description);

    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * com.technophobia.substeps.runner.AbstractBaseNotifier#handleNotifyNodeIgnored
     * (com.technophobia.substeps.execution.ExecutionNode)
     */

    public void notifyNodeIgnored(final ExecutionNode node) {

        final Description description = descriptionMap.get(Long.valueOf(node.getId()));
        notifyTestIgnored(description);

    }

}
