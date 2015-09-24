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

import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.net.URLClassLoader;
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

    /**
	 * 
	 */
    private void run() {

        this.log.trace("starting jmx server");

//        URLClassLoader classloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
//
//        URL[]  urls = classloader.getURLs();
//
//        StringBuilder buf = new StringBuilder();
//        for (URL u : urls){
//            buf.append(u.getFile()).append("\n");
//        }
//
//        System.out.println("Started SubstepsJMXServer with command: " + System.getProperty("sun.java.command") + " and classpath:\n" + buf.toString());
//
//        System.out.println("SubstepsJMXServer system props");
//
//
//        System.getProperties().list(System.out);

        System.out.println("starting substeps server");

        final SubstepsServer mBeanImpl = new SubstepsServer(this.shutdownSignal);

        final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

        System.out.println("got mbean server");

        try {

            final ObjectName name = new ObjectName(SubstepsServerMBean.SUBSTEPS_JMX_MBEAN_NAME);

            mbs.registerMBean(mBeanImpl, name);

            this.log.trace("bean registered");
            System.out.println("mbean registered");

            // TODO use notifications instead of parsing the log file

            // TODO think something going wrong around here - appears like occaisional deadlock around the countdownlatch..???
            boolean rpt = true;
            while (rpt) {
//            while (this.shutdownSignal.getCount() > 0) {
                try {
                    // ** NB. this can't be a log statement as it can be turned
                    // off
                    System.out.println("awaiting the shutdown notification...");
                    // ** see above

                    this.shutdownSignal.await();
                    rpt = false;
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
