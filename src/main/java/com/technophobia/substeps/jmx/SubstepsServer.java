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

package com.technophobia.substeps.jmx;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.technophobia.substeps.execution.ExecutionNode;
import com.technophobia.substeps.runner.ExecutionConfig;
import com.technophobia.substeps.runner.ExecutionNodeRunner;
import com.technophobia.substeps.runner.INotifier;
import com.technophobia.substeps.runner.SubstepExecutionFailure;

/**
 * @author ian
 * 
 */
public class SubstepsServer extends NotificationBroadcasterSupport implements
        SubstepsServerMBean, INotifier {

    private final Logger log = LoggerFactory.getLogger(SubstepsServer.class);

    private ExecutionNodeRunner nodeRunner = null;
    private final CountDownLatch shutdownSignal;


    /**
     * @param shutdownSignal
     */
    public SubstepsServer(final CountDownLatch shutdownSignal) {
        this.shutdownSignal = shutdownSignal;
    }


    public void shutdown() {
        this.shutdownSignal.countDown();
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * com.technopobia.substeps.jmx.SubstepsMBean#prepareExecutionConfig(com
     * .technophobia.substeps.runner.ExecutionConfig)
     */
    public ExecutionNode prepareExecutionConfig(final ExecutionConfig theConfig) {
        // TODO - synchronise around the init call ?
        this.nodeRunner = new ExecutionNodeRunner();
        return this.nodeRunner.prepareExecutionConfig(theConfig);
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.technopobia.substeps.jmx.SubstepsMBean#run()
     */
    public List<SubstepExecutionFailure> run() {

        // attach a result listener to broadcast

        this.nodeRunner.addNotifier(this);
        final List<SubstepExecutionFailure> failures;
        try {
            failures = this.nodeRunner.run();
        } finally {
            // now send the final notification

            final Notification n = new Notification("ExecConfigComplete", this,
                    this.notificationSequenceNumber);

            this.log.trace("sending complete notification sequence: "
                    + this.notificationSequenceNumber);

            sendNotification(n);
        }
        return failures;

    }

    private long notificationSequenceNumber = 1;


    private void doNotification(final ExecutionNode node) {

        final Notification n = new Notification("ExNode", this,
                this.notificationSequenceNumber);

        this.notificationSequenceNumber++;

        n.setUserData(node.getResult());

        this.log.trace("sending notification for node id: " + node.getId()
                + " sequence: " + this.notificationSequenceNumber);

        sendNotification(n);

    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * com.technophobia.substeps.runner.INotifier#notifyNodeFailed(com.technophobia
     * .substeps.execution.ExecutionNode, java.lang.Throwable)
     */
    public void notifyNodeFailed(final ExecutionNode node, final Throwable cause) {

        doNotification(node);

    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * com.technophobia.substeps.runner.INotifier#notifyNodeStarted(com.technophobia
     * .substeps.execution.ExecutionNode)
     */
    public void notifyNodeStarted(final ExecutionNode node) {

        doNotification(node);

    }


    /*
     * (non-Javadoc)
     * 
     * @see com.technophobia.substeps.runner.INotifier#notifyNodeFinished(com.
     * technophobia.substeps.execution.ExecutionNode)
     */
    public void notifyNodeFinished(final ExecutionNode node) {

        doNotification(node);

    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * com.technophobia.substeps.runner.INotifier#notifyNodeIgnored(com.technophobia
     * .substeps.execution.ExecutionNode)
     */
    public void notifyNodeIgnored(final ExecutionNode node) {

        doNotification(node);
    }
}