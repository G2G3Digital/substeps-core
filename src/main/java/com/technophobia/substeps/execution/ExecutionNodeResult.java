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

package com.technophobia.substeps.execution;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author ian
 * 
 */
public class ExecutionNodeResult {
    private ExecutionResult result = ExecutionResult.NOT_RUN;

    private Throwable thrown = null;

    private long start, end = 0;


    // long duration ?

    public String getStackTrace() {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);

        thrown.printStackTrace(pw);

        pw.close();

        return sw.toString();
    }


    /**
     * @return the result
     */
    public ExecutionResult getResult() {
        return result;
    }


    /**
     * @param result
     *            the result to set
     */
    public void setResult(final ExecutionResult result) {
        this.result = result;
    }


    /**
     * @return the failureStackTrace
     */
    public Throwable getThrown() {
        return thrown;
    }


    /**
     * @param failureStackTrace
     *            the failureStackTrace to set
     */
    public void setThrown(final Throwable failureStackTrace) {
        thrown = failureStackTrace;
    }


    /**
     * @param theException
     */
    public void setFailed(final Throwable theException) {
        end = System.currentTimeMillis();

        result = ExecutionResult.FAILED;
        thrown = theException;
    }


    /**
	 * 
	 */
    public void setFinished() {
        end = System.currentTimeMillis();

        result = ExecutionResult.PASSED;
    }


    /**
	 * 
	 */
    public void setStarted() {
        start = System.currentTimeMillis();
        result = ExecutionResult.RUNNING;
    }


    /**
     * @param t
     */
    public void setFailedToParse(final Throwable t) {
        result = ExecutionResult.PARSE_FAILURE;
        thrown = t;
    }

}
