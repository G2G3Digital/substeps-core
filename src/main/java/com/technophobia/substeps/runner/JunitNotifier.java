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

import java.util.ArrayList;
import java.util.List;
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
    public static final String NOTIFIER_EXECUTION_KEY = "notifier";

    private final Logger log = LoggerFactory.getLogger(JunitNotifier.class);

    private RunNotifier junitRunNotifier;

    private List<INotifier> listeners;

    private Map<Long, Description> descriptionMap;


    public void addListener(final INotifier listener) {
        if (listeners == null) {
            listeners = new ArrayList<INotifier>();
        }
        listeners.add(listener);
    }


    /**
     * {@inheritDoc}
     */
    public void notifyTestStarted(final Description junitDescription) {

        if (junitRunNotifier != null && junitDescription != null) {
            log.debug(junitDescription.getDisplayName() + " notifyTestStarted");

            junitRunNotifier.fireTestStarted(junitDescription);
        }
        notifyListenersTestStarted(junitDescription);
    }


    /**
     * @param listeners2
     * @param junitDescription
     */
    private void notifyListenersTestStarted(final Description junitDescription) {
        if (listeners != null) {
            for (final INotifier listener : listeners) {
                listener.notifyTestStarted(junitDescription);
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void notifyTestFinished(final Description junitDescription) {
        if (junitRunNotifier != null && junitDescription != null) {
            log.debug(junitDescription.getDisplayName() + " notifyTestFinished");

            junitRunNotifier.fireTestFinished(junitDescription);
        }
        notifyListenersTestFinished(junitDescription);
    }


    /**
     * @param listeners2
     * @param junitDescription
     */
    private void notifyListenersTestFinished(final Description junitDescription) {
        if (listeners != null) {
            for (final INotifier listener : listeners) {
                listener.notifyTestFinished(junitDescription);
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void notifyTestIgnored(final Description junitDescription) {
        if (junitRunNotifier != null && junitDescription != null) {
            junitRunNotifier.fireTestIgnored(junitDescription);
        }
        notifyListenersTestIgnored(junitDescription);
    }


    /**
     * @param listeners2
     * @param junitDescription
     */
    private void notifyListenersTestIgnored(final Description junitDescription) {
        if (listeners != null) {
            for (final INotifier listener : listeners) {
                listener.notifyTestIgnored(junitDescription);
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void notifyTestFailed(final Description junitDescription, final Throwable cause) {
        if (junitRunNotifier != null && junitDescription != null) {
            log.debug(junitDescription.getDisplayName() + " notify running TestFailed");

            log.debug(junitDescription.getDisplayName() + " notify TestFailed");
            junitRunNotifier.fireTestFailure(new Failure(junitDescription, cause));

        }
        notifyListenersTestFailed(junitDescription, cause);
    }


    /**
     * @param listeners2
     * @param junitDescription
     * @param cause
     */
    private void notifyListenersTestFailed(final Description junitDescription, final Throwable cause) {
        if (listeners != null) {
            for (final INotifier listener : listeners) {
                listener.notifyTestFailed(junitDescription, cause);
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void pleaseStop() {
        junitRunNotifier.pleaseStop();
    }


    /**
     * @param notifier
     * @param junitDescription
     */
    public static void notifyTestStarted(final INotifier notifier,
            final Description junitDescription) {
        if (notifier != null) {
            notifier.notifyTestStarted(junitDescription);
        }
    }


    /**
     * @param notifier
     * @param junitDescription
     */
    public static void notifyTestFinished(final INotifier notifier,
            final Description junitDescription) {
        if (notifier != null) {
            notifier.notifyTestFinished(junitDescription);
        }
    }


    /**
     * @param notifier
     * @param junitDescription
     * @param cause
     */
    public static void notifyTestFailed(final INotifier notifier,
            final Description junitDescription, final Throwable cause) {
        if (notifier != null) {
            notifier.notifyTestFailed(junitDescription, cause);
        }
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


    public void notifyTestStarted(final ExecutionNode node) {

        final Description description = descriptionMap.get(Long.valueOf(node.getId()));

        final boolean b = description != null;
        log.debug("notifyTestStarted nodeid: " + node.getId() + " description: " + b);

        notifyTestStarted(description);
    }


    /**
     * @param node
     */
    public void notifyTestFinished(final ExecutionNode node) {

        final Description description = descriptionMap.get(Long.valueOf(node.getId()));
        notifyTestFinished(description);
    }


    /**
     * @param node
     * @param theException
     */
    public void notifyTestFailed(final ExecutionNode node, final Throwable theException) {

        final Description description = descriptionMap.get(Long.valueOf(node.getId()));
        notifyTestFailed(description, theException);
    }

}
