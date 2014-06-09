/*
 *  Copyright Technophobia Ltd 2012
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

package com.technophobia.substeps.runner.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.technophobia.substeps.runner.ExecutionLogger;

public class StepExecutionLogger extends ExecutionLogger {

    private static final Logger log = LoggerFactory.getLogger(StepExecutionLogger.class);

    @Override
    public void printFailed(final String msg) {
        log.info(msg);
    }

    @Override
    public void printStarted(final String msg) {
        log.info(msg);
    }

    @Override
    public void printPassed(final String msg) {
        log.info(msg);
    }

    @Override
    public void printSkipped(final String msg) {
        log.info(msg);
    }
}
