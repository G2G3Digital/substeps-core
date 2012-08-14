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

import com.technophobia.substeps.execution.ExecutionNode;

/**
 * represents the failure of an execution - could be a step method, or a setup method, may or may not be critical
 * @author ian
 *
 */
public class SubstepExecutionFailure {

	private final Throwable cause;
	private ExecutionNode execcutionNode;
	private boolean setupOrTearDown = false;
	private boolean nonCritical = false;
	
	
	public SubstepExecutionFailure(final Throwable cause) {
        
		this.cause = cause;
    }


	/**
	 * @param targetException
	 * @param node
	 */
	public SubstepExecutionFailure(final Throwable targetException, final ExecutionNode node) {
		this.cause = targetException;
		this.execcutionNode = node;
	}


	public SubstepExecutionFailure(final Throwable targetException, final ExecutionNode node, final boolean setupOrTearDown) {

		this.cause = targetException;
		this.execcutionNode = node;
		this.setupOrTearDown = setupOrTearDown; 
	}


	/**
	 * @return the execcutionNode
	 */
	public ExecutionNode getExeccutionNode() {
		return execcutionNode;
	}


	/**
	 * @param execcutionNode the execcutionNode to set
	 */
	public void setExeccutionNode(final ExecutionNode execcutionNode) {
		this.execcutionNode = execcutionNode;
	}


	/**
	 * @return the setupOrTearDown
	 */
	public boolean isSetupOrTearDown() {
		return setupOrTearDown;
	}


	/**
	 * @param setupOrTearDown the setupOrTearDown to set
	 */
	public void setSetupOrTearDown(final boolean setupOrTearDown) {
		this.setupOrTearDown = setupOrTearDown;
	}


	/**
	 * @return the cause
	 */
	public Throwable getCause() {
		return cause;
	}


	/**
	 * @param b
	 */
	public void setNonCritical(final boolean isNonCritical) {
		this.nonCritical = isNonCritical;
		
	}


	/**
	 * @return the nonCritical
	 */
	public boolean isNonCritical() {
		return nonCritical;
	}

}
