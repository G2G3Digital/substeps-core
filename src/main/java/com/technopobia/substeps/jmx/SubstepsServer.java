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

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.technophobia.substeps.execution.ExecutionNode;
import com.technophobia.substeps.runner.ExecutionConfig;
import com.technophobia.substeps.runner.ExecutionNodeRunner;
import com.technophobia.substeps.runner.SubstepExecutionFailure;


/**
 * @author ian
 *
 */
public class SubstepsServer implements SubstepsServerMBean{
	Logger log = LoggerFactory.getLogger(SubstepsServer.class);
	
	private ExecutionNodeRunner nodeRunner = null;
	private final CountDownLatch shutdownSignal;
	/**
	 * @param shutdownSignal
	 */
	public SubstepsServer(final CountDownLatch shutdownSignal) {
		this.shutdownSignal = shutdownSignal;
	}
	
	public void shutdown(){
		this.shutdownSignal.countDown();
	}

	/* (non-Javadoc)
	 * @see com.technopobia.substeps.jmx.SubstepsMBean#prepareExecutionConfig(com.technophobia.substeps.runner.ExecutionConfig)
	 */
	public ExecutionNode prepareExecutionConfig(final ExecutionConfig theConfig) {
		// TODO - synchronise around the init call ?
		nodeRunner = new ExecutionNodeRunner();
		return nodeRunner.prepareExecutionConfig(theConfig);
	}

	/* (non-Javadoc)
	 * @see com.technopobia.substeps.jmx.SubstepsMBean#run()
	 */
	public List<SubstepExecutionFailure> run() {
		
		// attach a result listener to broadcast
		
		return nodeRunner.run();
	}


}
