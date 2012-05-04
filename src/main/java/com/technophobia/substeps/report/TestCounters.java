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
package com.technophobia.substeps.report;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.technophobia.substeps.execution.ExecutionNode;
import com.technophobia.substeps.execution.ExecutionNodeResult;


/**
 * @author ian
 * 
 */
public class TestCounters {
    private int count = 0;
    private int run = 0;
    private int ignored = 0;
    private int passed = 0;
    private int failed = 0;
    
    public double getSuccessPc(){
    
    	double rtn = 0.0;
        if (run > 0) {

            double d =  (double)passed / (double)run * 100;
            BigDecimal bd = BigDecimal.valueOf(d).setScale(1, RoundingMode.HALF_UP);
            rtn = bd.doubleValue();
        }
        return rtn;
    }

    public void addCount() {
        count++;
    }


    public void addRun() {
        run++;
    }


    public void addIgnored() {
        ignored++;
    }


    public void addPassed() {
        passed++;
    }


    public void addFailed() {
        failed++;
    }


    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }


    /**
     * @return the run
     */
    public int getRun() {
        return run;
    }


    /**
     * @return the ignored
     */
    public int getIgnored() {
        return ignored;
    }


    /**
     * @return the passed
     */
    public int getPassed() {
        return passed;
    }


    /**
     * @return the failed
     */
    public int getFailed() {
        return failed;
    }


    /**
     * @param node
     */
    public void apply(final ExecutionNode node) {
        // TODO Auto-generated method stub

        final ExecutionNodeResult result = node.getResult();

        switch (result.getResult()) {
        case IGNORED: {
            ignored++;
            count++;
            break;
        }
        case NOT_INCLUDED: {
            ignored++;
            count++;
            break;
        }

        case NOT_RUN: {
            ignored++;
            count++;
            break;
        }

        case RUNNING: {
            run++;
            count++;
            break;
        }

        case PASSED: {
            run++;
            passed++;
            count++;
            break;
        }

        case FAILED: {
            run++;
            failed++;
            count++;
            break;
        }

        }
    }

}
