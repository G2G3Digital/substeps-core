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

package com.technopobia.substeps.jmx;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.technophobia.substeps.runner.ExecutionConfig;
import com.technophobia.substeps.runner.SubstepExecutionFailure;

/**
 * @author ian
 * 
 */
public class SubstepsJMXClient implements NotificationListener {
    Logger log = LoggerFactory.getLogger(SubstepsJMXClient.class);
    private SubstepsServerMBean mbean;

    private final Map<Long, ExecutionNode> nodeMap = new HashMap<Long, ExecutionNode>();


    public void init(final int portNumber) {

        final String url = "service:jmx:rmi:///jndi/rmi://:" + portNumber
                + "/jmxrmi";

        // The address of the connector server
        try {
            final JMXServiceURL serviceURL = new JMXServiceURL(url);

            final Map<String, ?> environment = null;

            // Create the JMXCconnectorServer
            final JMXConnector cntor = JMXConnectorFactory.connect(serviceURL,
                    environment);

            // Obtain a "stub" for the remote MBeanServer
            final MBeanServerConnection mbsc = cntor.getMBeanServerConnection();

            final ObjectName objectName = new ObjectName(
                    SubstepsJMXServer.SUBSTEPS_JMX_MBEAN_NAME);
            this.mbean = MBeanServerInvocationHandler.newProxyInstance(mbsc,
                    objectName, SubstepsServerMBean.class, false);

            // register this as a listener
            mbsc.addNotificationListener(objectName, this, null, null);

        } catch (final MalformedURLException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final MalformedObjectNameException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final NullPointerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final InstanceNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public ExecutionNode prepareExceutionConfig(final ExecutionConfig cfg) {

        final ExecutionNode rootNode = this.mbean.prepareExecutionConfig(cfg);

        if (rootNode != null) {
            populateNodeMap(rootNode);
        }
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

        return this.mbean.run();

        // TODO listener interface to upade the root node hierarchy
    }


    public void shutdown() {
        this.mbean.shutdown();
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

        final ExecutionNodeResult newResult = (ExecutionNodeResult) notification
                .getUserData();

        this.log.debug("received notification seq: "
                + notification.getSequenceNumber() + " nodeid: "
                + newResult.getExecutionNodeId() + " result: "
                + newResult.getResult());

        // update the results
        final ExecutionNode executionNode = this.nodeMap.get(Long
                .valueOf(newResult.getExecutionNodeId()));

        final ExecutionNodeResult origResult = executionNode.getResult();

        origResult.setResult(newResult.getResult());
        origResult.setThrown(newResult.getThrown());

    }

}
