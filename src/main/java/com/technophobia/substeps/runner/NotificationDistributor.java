package com.technophobia.substeps.runner;

import java.util.ArrayList;
import java.util.List;

import com.technophobia.substeps.execution.node.ExecutionNode;

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

    private List<INotifier> listeners;

    public void addListener(final INotifier listener) {
        if (this.listeners == null) {
            this.listeners = new ArrayList<INotifier>();
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
    public final void notifyNodeFailed(final ExecutionNode node, final Throwable cause) {

        notifyListenersTestFailed(node, cause);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.technophobia.substeps.runner.INotifier#notifyNodeStarted(com.technophobia
     * .substeps.execution.ExecutionNode)
     */
    public final void notifyNodeStarted(final ExecutionNode node) {

        notifyListenersTestStarted(node);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.technophobia.substeps.runner.INotifier#notifyNodeFinished(com.
     * technophobia.substeps.execution.ExecutionNode)
     */
    public final void notifyNodeFinished(final ExecutionNode node) {

        notifyListenersTestFinished(node);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.technophobia.substeps.runner.INotifier#notifyNodeIgnored(com.technophobia
     * .substeps.execution.ExecutionNode)
     */
    public final void notifyNodeIgnored(final ExecutionNode node) {

        notifyListenersTestIgnored(node);

    }

    /**
     * @param listeners2
     * @param junitDescription
     * @param cause
     */
    private void notifyListenersTestFailed(final ExecutionNode node, final Throwable cause) {
        if (this.listeners != null) {
            for (final INotifier listener : this.listeners) {
                listener.notifyNodeFailed(node, cause);
            }
        }
    }

    /**
     * @param listeners2
     * @param junitDescription
     */
    private void notifyListenersTestIgnored(final ExecutionNode node) {
        if (this.listeners != null) {
            for (final INotifier listener : this.listeners) {
                listener.notifyNodeIgnored(node);
            }
        }
    }

    /**
     * @param listeners2
     * @param junitDescription
     */
    private void notifyListenersTestFinished(final ExecutionNode node) {
        if (this.listeners != null) {
            for (final INotifier listener : this.listeners) {
                listener.notifyNodeFinished(node);
            }
        }
    }

    private void notifyListenersTestStarted(final ExecutionNode node) {
        if (this.listeners != null) {
            for (final INotifier listener : this.listeners) {
                listener.notifyNodeStarted(node);
            }
        }
    }

}
