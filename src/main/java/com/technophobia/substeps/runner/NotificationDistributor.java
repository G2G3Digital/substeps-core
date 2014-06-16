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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.technophobia.substeps.execution.node.IExecutionNode;

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
public class NotificationDistributor implements INotificationDistributor {

    private static final Logger log = LoggerFactory.getLogger(NotificationDistributor.class);

    private List<IExecutionListener> listeners;

    public void addListener(final IExecutionListener listener) {
        if (this.listeners == null) {
            this.listeners = new ArrayList<IExecutionListener>();
        }
        this.listeners.add(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.technophobia.substeps.runner.INotifier#notifyNodeFailed(com.technophobia
     * .substeps.execution.ExecutionNode, java.lang.Throwable)
     */
    public final void onNodeFailed(final IExecutionNode node, final Throwable cause) {

        notifyListenersTestFailed(node, cause);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.technophobia.substeps.runner.INotifier#notifyNodeStarted(com.technophobia
     * .substeps.execution.ExecutionNode)
     */
    public final void onNodeStarted(final IExecutionNode node) {

        notifyListenersTestStarted(node);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.technophobia.substeps.runner.INotifier#notifyNodeFinished(com.
     * technophobia.substeps.execution.ExecutionNode)
     */
    public final void onNodeFinished(final IExecutionNode node) {

        notifyListenersTestFinished(node);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.technophobia.substeps.runner.INotifier#notifyNodeIgnored(com.technophobia
     * .substeps.execution.ExecutionNode)
     */
    public final void onNodeIgnored(final IExecutionNode node) {

        notifyListenersTestIgnored(node);

    }

    /**
     * @param listeners2
     * @param junitDescription
     * @param cause
     */
    private void notifyListenersTestFailed(final IExecutionNode node, final Throwable cause) {
        if (this.listeners != null) {
            for (final IExecutionListener listener : this.listeners) {
                listener.onNodeFailed(node, cause);
            }
        }
    }

    /**
     * @param listeners2
     * @param junitDescription
     */
    private void notifyListenersTestIgnored(final IExecutionNode node) {
        if (this.listeners != null) {
            for (final IExecutionListener listener : this.listeners) {
                listener.onNodeIgnored(node);
            }
        }
    }

    /**
     * @param listeners2
     * @param junitDescription
     */
    private void notifyListenersTestFinished(final IExecutionNode node) {
        if (this.listeners != null) {
            for (final IExecutionListener listener : this.listeners) {
                listener.onNodeFinished(node);
            }
        }
    }

    private void notifyListenersTestStarted(final IExecutionNode node) {
        if (this.listeners != null) {
            for (final IExecutionListener listener : this.listeners) {

                log.trace("Notifying " + listener.getClass() + " that the node has started");

                listener.onNodeStarted(node);
            }
        }
    }

}
