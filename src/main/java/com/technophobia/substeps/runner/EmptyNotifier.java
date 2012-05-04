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
 * Class designed to be extended and override the methods you're really
 * interested in
 * 
 * @author imoore
 * 
 */
public class EmptyNotifier implements INotifier {

    /**
     * {@inheritDoc}
     */
    public void notifyTestStarted(final Description junitDescription) {
        // no op
    }


    /**
     * {@inheritDoc}
     */
    public void notifyTestFinished(final Description junitDescription) {
        // no op
    }


    /**
     * {@inheritDoc}
     */
    public void notifyTestFailed(final Description junitDescription, final Throwable cause) {
        // no op
    }


    /**
     * {@inheritDoc}
     */
    public void pleaseStop() {
        // no op
    }


    /**
     * {@inheritDoc}
     */
    public void setJunitRunNotifier(final RunNotifier junitNotifier) {
        // no op
    }


    /**
     * {@inheritDoc}
     */
    public void notifyTestIgnored(final Description junitDescription) {
        // no op
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * uk.co.itmoore.bddrunner.runner.INotifier#notifyTestFailed(uk.co.itmoore
     * .bddrunner.execution.ExecutionNode, java.lang.Throwable)
     */
    public void notifyTestFailed(final ExecutionNode rootNode, final Throwable cause) {
        // no op

    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * uk.co.itmoore.bddrunner.runner.INotifier#notifyTestStarted(uk.co.itmoore
     * .bddrunner.execution.ExecutionNode)
     */
    public void notifyTestStarted(final ExecutionNode node) {
        // no op

    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * uk.co.itmoore.bddrunner.runner.INotifier#notifyTestFinished(uk.co.itmoore
     * .bddrunner.execution.ExecutionNode)
     */
    public void notifyTestFinished(final ExecutionNode node) {
        // no op

    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * uk.co.itmoore.bddrunner.runner.INotifier#addListener(uk.co.itmoore.bddrunner
     * .runner.INotifier)
     */
    public void addListener(final INotifier listener) {
        // TODO Auto-generated method stub

    }

}
