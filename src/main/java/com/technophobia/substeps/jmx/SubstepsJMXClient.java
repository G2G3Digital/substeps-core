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

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.technophobia.substeps.execution.ExecutionNode;
import com.technophobia.substeps.execution.ExecutionNodeResult;
import com.technophobia.substeps.execution.ExecutionResult;
import com.technophobia.substeps.model.SubStepConfigurationException;
import com.technophobia.substeps.runner.ExecutionConfig;
import com.technophobia.substeps.runner.SubstepExecutionFailure;
import com.technophobia.substeps.runner.SubstepsRunner;

/**
 * @author ian
 * 
 */
public class SubstepsJMXClient implements NotificationListener, SubstepsRunner {
    Logger log = LoggerFactory.getLogger(SubstepsJMXClient.class);
    private SubstepsServerMBean mbean;

    private final Map<Long, ExecutionNode> nodeMap = new HashMap<Long, ExecutionNode>();
    private Set<Long> nodesNotPassed = null;
    private int totalNodesToRun;

    private JMXConnector cntor = null;

    private CountDownLatch complete = null;

    private final boolean ansiColourOutput = true;


    public void init(final int portNumber) {

        final String url = "service:jmx:rmi:///jndi/rmi://:" + portNumber
                + "/jmxrmi";

        // The address of the connector server

        try {
            final JMXServiceURL serviceURL = new JMXServiceURL(url);

            final Map<String, ?> environment = null;

            // Create the JMXCconnectorServer
            this.cntor = JMXConnectorFactory.connect(serviceURL, environment);

            // Obtain a "stub" for the remote MBeanServer
            final MBeanServerConnection mbsc = this.cntor
                    .getMBeanServerConnection();

            final ObjectName objectName = new ObjectName(
                    SubstepsJMXServer.SUBSTEPS_JMX_MBEAN_NAME);
            this.mbean = MBeanServerInvocationHandler.newProxyInstance(mbsc,
                    objectName, SubstepsServerMBean.class, false);

            // register this as a listener
            mbsc.addNotificationListener(objectName, this, null, null);

        } catch (final MalformedURLException e) {

            // Eclipse grumbles about cntor not being closed, but it will be by
            // the finally block below..
            throw new SubStepConfigurationException(e);

        } catch (final IOException e) {

            throw new SubStepConfigurationException(e);

        } catch (final MalformedObjectNameException e) {

            throw new SubStepConfigurationException(e);

        } catch (final NullPointerException e) {

            throw new SubStepConfigurationException(e);

        } catch (final InstanceNotFoundException e) {

            throw new SubStepConfigurationException(e);
        }
    }


    public ExecutionNode prepareExecutionConfig(final ExecutionConfig cfg) {

        final ExecutionNode rootNode = this.mbean.prepareExecutionConfig(cfg);

        if (rootNode != null) {
            populateNodeMap(rootNode);
        }

        this.nodesNotPassed = new HashSet<Long>();
        this.nodesNotPassed.addAll(this.nodeMap.keySet());
        this.totalNodesToRun = this.nodesNotPassed.size();
        return rootNode;
    }


    private void populateNodeMap(final ExecutionNode node) {

        if (node != null) {
            this.nodeMap.put(node.getLongId(), node);

            if (node.getBackgrounds() != null) {
                for (final ExecutionNode n : node.getBackgrounds()) {
                    populateNodeMap(n);
                }
            }

            if (node.getChildren() != null) {
                for (final ExecutionNode n : node.getChildren()) {
                    populateNodeMap(n);
                }
            }
        }
    }


    public List<SubstepExecutionFailure> run() {

        this.complete = new CountDownLatch(1);

        return this.mbean.run();
    }


    public void shutdown() {

        if (this.complete != null) {
            try {
                this.complete.await(10L, TimeUnit.SECONDS);
            } catch (final InterruptedException e1) {

                e1.printStackTrace();
            }

            if (this.complete.getCount() > 0) {

                this.log.debug("waited 10 secs for the final notification... not heard, shutting down");
            }
        }
        this.mbean.shutdown();

        if (this.cntor != null) {
            try {
                this.cntor.close();
            } catch (final IOException e) {

                e.printStackTrace();
            }
        }
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.management.NotificationListener#handleNotification(javax.management
     * .Notification, java.lang.Object)
     */
    public void handleNotification(final Notification notification,
            final Object handback) {

        final String msgType = notification.getType();

        // this.log.trace("notificaiton msgType: " + msgType + " sequence: "
        // + notification.getSequenceNumber());

        if (msgType.equals("ExNode")) {

            final ExecutionNodeResult newResult = (ExecutionNodeResult) notification
                    .getUserData();

            if (this.log.isTraceEnabled()) {
                this.log.trace("received notification seq: "
                        + notification.getSequenceNumber() + " nodeid: "
                        + newResult.getExecutionNodeId() + " result: "
                        + newResult.getResult());
            }

            final Long id = Long.valueOf(newResult.getExecutionNodeId());
            // update the results
            final ExecutionNode executionNode = this.nodeMap.get(id);

            if (newResult.getResult() == ExecutionResult.PASSED) {

                this.nodesNotPassed.remove(id);

            }

            final int numberRun = this.totalNodesToRun
                    - this.nodesNotPassed.size();

            final double pcent = (double) numberRun
                    / (double) this.totalNodesToRun * 100;

            final BigDecimal percentComplete = BigDecimal.valueOf(pcent)
                    .setScale(3, RoundingMode.HALF_DOWN);

            this.log.info(percentComplete.toString()
                    + " % of steps passed (of total steps)");

            final ExecutionNodeResult origResult = executionNode.getResult();

            origResult.setResult(newResult.getResult());
            origResult.setThrown(newResult.getThrown());

            if (newResult.getResult() == ExecutionResult.FAILED) {

                printFail(executionNode);
            } else if (newResult.getResult() == ExecutionResult.PASSED) {

                printPass(executionNode);
            }

        } else if (msgType.equals("ExecConfigComplete")) {

            this.log.trace("It's the final countdown..");
            this.complete.countDown();

        } else {

            this.log.debug("unknown message type");
        }
    }

    private static final String PREFIX = "\033[";
    private static final String POSTFIX = "m";
    private static final String SEPARATOR = ";";


    /**
     * @param executionNode
     */
    private void printPass(final ExecutionNode node) {

        if (this.ansiColourOutput) {
            printBold(node.getLine());
        } else {
            System.out.println("PASSED: " + node.getLine());
        }
    }


    private void printRedBold(final String line) {

        System.out.println(PREFIX + "1" + SEPARATOR + "31" + POSTFIX + line
                + PREFIX + POSTFIX);
    }


    private void printBold(final String line) {

        System.out.println(PREFIX + "1" + POSTFIX + line + PREFIX + POSTFIX);

    }


    /**
     * @param executionNode
     */
    private void printFail(final ExecutionNode node) {

        if (this.ansiColourOutput) {
            printRedBold(node.getLine());
        } else {
            System.out.println("FAILED: " + node.getLine());
        }
    }

}
