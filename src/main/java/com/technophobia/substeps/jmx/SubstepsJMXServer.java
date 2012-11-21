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

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ian
 * 
 */
public class SubstepsJMXServer {
    private final Logger log = LoggerFactory.getLogger(SubstepsJMXServer.class);

    private final CountDownLatch shutdownSignal = new CountDownLatch(1);


    /**
     * @param args
     */
    public static void main(final String[] args) {

        // this is the thing that will be instantiated by an external process

        // TODO check the system args for this make sure the jmx args are set

        // -Dcom.sun.management.jmxremote.port=9999
        // -Dcom.sun.management.jmxremote.authenticate=false
        // -Dcom.sun.management.jmxremote.ssl=false

        final SubstepsJMXServer server = new SubstepsJMXServer();
        server.run();
    }

    public static final String SUBSTEPS_JMX_MBEAN_NAME = "com.technopobia.substeps.jmx:type=SubstepsServerMBean";


    /**
	 * 
	 */
    private void run() {

        this.log.trace("starting jmx server");

        final SubstepsServer mBeanImpl = new SubstepsServer(this.shutdownSignal);

        final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        try {

            final ObjectName name = new ObjectName(SUBSTEPS_JMX_MBEAN_NAME);

            mbs.registerMBean(mBeanImpl, name);

            this.log.trace("bean registered");

            // works String url = "service:jmx:rmi://localhost";
            final String url = "service:jmx:rmi:///jndi/rmi://localhost";

            // final JMXServiceURL serviceURL = new JMXServiceURL(url);
            final Map<String, ?> environment = null;

            while (this.shutdownSignal.getCount() > 0) {
                try {
                    this.log.debug("awaiting the shutdown notification...");

                    this.shutdownSignal.await();
                    this.log.debug("shutdown notification received");

                } catch (final InterruptedException e) {

                    e.printStackTrace();
                }
            }

        } catch (final MalformedObjectNameException ex) {

            this.log.error("exception starting substeps mbean server", ex);
        } catch (final InstanceAlreadyExistsException ex) {
            this.log.error("exception starting substeps mbean server", ex);
        } catch (final MBeanRegistrationException ex) {
            this.log.error("exception starting substeps mbean server", ex);
        } catch (final NotCompliantMBeanException ex) {
            this.log.error("exception starting substeps mbean server", ex);
        }

        this.log.debug("run method complete");
    }

}
