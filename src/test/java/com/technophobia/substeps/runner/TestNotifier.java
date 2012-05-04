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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.technophobia.substeps.execution.ExecutionNode;
import com.technophobia.substeps.runner.IJunitNotifier;
import com.technophobia.substeps.runner.INotifier;


/**
 * test to get to the bottom of sequencing test started notifications etc
 * 
 * @author imoore
 * 
 */
@Ignore("not a test!")
public class TestNotifier implements IJunitNotifier {

    private final Logger log = LoggerFactory.getLogger(TestNotifier.class);

    private Description currentlyRunning = null;


    public Description getCurrentlyRunning() {
        return currentlyRunning;
    }


    public List<Description> getFinished() {
        return finished;
    }


    public List<Description> getFailed() {
        return failed;
    }


    public List<Description> getAllStarted() {
        return allStarted;
    }

    private final List<Description> finished = new ArrayList<Description>();
    private final List<Description> failed = new ArrayList<Description>();
    private final List<Description> allStarted = new ArrayList<Description>();
    private final List<Description> ignored = new ArrayList<Description>();


    /**
     * {@inheritDoc}
     */
    public void notifyTestStarted(final Description des) {
        // final Throwable t = new Throwable();
        // t.fillInStackTrace();
        //
        // log.debug(des.getDisplayName() + " notifyTestStarted from", t);

        currentlyRunning = des;
        allStarted.add(des);
    }


    /**
     * {@inheritDoc}
     */
    public void notifyTestFinished(final Description des) {
        finished.add(des);
        currentlyRunning = null;

    }


    /**
     * {@inheritDoc}
     */
    public void notifyTestFailed(final Description des, final Throwable cause) {
        failed.add(des);

    }


    /**
     * {@inheritDoc}
     */
    public void pleaseStop() {
        // TODO Auto-generated method stub

    }


    /**
     * {@inheritDoc}
     */
    public void setJunitRunNotifier(final RunNotifier junitNotifier) {
        // TODO Auto-generated method stub

    }


    /**
     * {@inheritDoc}
     */
    public void notifyTestIgnored(final Description des) {
        ignored.add(des);

    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * uk.co.itmoore.bddrunner.runner.INotifier#notifyTestFailed(uk.co.itmoore
     * .bddrunner.execution.ExecutionNode, java.lang.Throwable)
     */
    public void notifyTestFailed(final ExecutionNode rootNode, final Throwable cause) {
        // TODO Auto-generated method stub

    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * uk.co.itmoore.bddrunner.runner.INotifier#notifyTestStarted(uk.co.itmoore
     * .bddrunner.execution.ExecutionNode)
     */
    public void notifyTestStarted(final ExecutionNode node) {
        // TODO Auto-generated method stub

    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * uk.co.itmoore.bddrunner.runner.INotifier#notifyTestFinished(uk.co.itmoore
     * .bddrunner.execution.ExecutionNode)
     */
    public void notifyTestFinished(final ExecutionNode node) {
        // TODO Auto-generated method stub

    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * uk.co.itmoore.bddrunner.runner.IJunitNotifier#setDescriptionMap(java.
     * util.Map)
     */
    public void setDescriptionMap(final Map<Long, Description> descriptionMap) {
        // TODO Auto-generated method stub

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
