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

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

import com.technophobia.substeps.execution.ExecutionNode;


/**
 * 
 * @author imoore
 * 
 */
public interface INotifier {

    /**
     * @param notifier
     * @param junitDescription
     */
    void notifyTestStarted(final Description junitDescription);


    /**
     * @param notifier
     * @param junitDescription
     */
    void notifyTestFinished(final Description junitDescription);


    /**
     * @param notifier
     * @param junitDescription
     * @param cause
     */
    void notifyTestFailed(final Description junitDescription, final Throwable cause);


    void addListener(final INotifier listener);


    /**
	 * 
	 */
    void pleaseStop();


    /**
     * @param junitNotifier
     */
    void setJunitRunNotifier(RunNotifier junitNotifier);


    /**
     * @param junitDescription
     */
    void notifyTestIgnored(Description junitDescription);


    // WIP

    /**
     * @param rootNode
     * @param cause
     */
    void notifyTestFailed(ExecutionNode rootNode, Throwable cause);


    /**
     * @param node
     */
    void notifyTestStarted(ExecutionNode node);


    /**
     * @param node
     */
    void notifyTestFinished(ExecutionNode node);

}