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
package com.technophobia.substeps.runner.setupteardown;

import junit.framework.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.technophobia.substeps.runner.ExecutionContext;
import com.technophobia.substeps.runner.Scope;


/**
 * Class to encapsulate setup and tear down methods and the ordering of them
 * 
 * @author imoore
 * 
 */
public class SetupAndTearDown {

    private final Logger log = LoggerFactory.getLogger(SetupAndTearDown.class);

    private Class<?> classContainingTheTests;
    private String loggingConfigName = null;


    public String getLoggingConfigName() {
        return loggingConfigName;
    }


    public void setLoggingConfigName(final String loggingConfigName) {
        this.loggingConfigName = loggingConfigName;
    }

    private final MethodExecutor methodExecutor;
    private boolean dryRun = false;


    public SetupAndTearDown(final MethodExecutor methodExecutor) {
        this.methodExecutor = methodExecutor;
    }


    public void initialise(final Class<?> classContainingTheTests) {
        this.classContainingTheTests = classContainingTheTests;

        methodExecutor.locate(classContainingTheTests);
    }


    /**
	 * 
	 */
    public void runBeforeAll() throws Throwable {

        prepareLoggingConfig();

        runAllMethods(MethodState.BEFORE_ALL);
    }


    /**
	 */
    public void runAfterAll() throws Throwable {
        runAllMethods(MethodState.AFTER_ALL);
        ExecutionContext.clear(Scope.SUITE);

        removeLoggingConfig();
    }


    /**
	 * 
	 */
    public void runBeforeFeatures() throws Throwable {
        runAllMethods(MethodState.BEFORE_FEATURES);
    }


    /**
	 */
    public void runAfterFeatures() throws Throwable {
        runAllMethods(MethodState.AFTER_FEATURES);
        ExecutionContext.clear(Scope.FEATURE);
    }


    /**
	 * 
	 */
    public void runBeforeScenarios() throws Throwable {
        runAllMethods(MethodState.BEFORE_SCENARIOS);
        // runBeforeAfterMethodExplosively(scenarioBefore);
    }


    /**
	 */
    public void runAfterScenarios() throws Throwable {
        runAllMethods(MethodState.AFTER_SCENARIOS);

        ExecutionContext.clear(Scope.SCENARIO);
    }


    private void runAllMethods(final MethodState methodState) throws Throwable {

        // TODO - perhaps pass this down into the executor to actually print
        // what we're going to do
        if (!dryRun) {
            // for (final MethodExecutor methodExecutor : methodExecutor) {
            methodExecutor.executeMethods(classContainingTheTests, methodState);
            // }
        }
    }


    private void prepareLoggingConfig() {

        if (classContainingTheTests != null) {
            MDC.put("className", classContainingTheTests.getName());
        } else {
            Assert.assertNotNull("please set loggingConfigName", loggingConfigName);
            MDC.put("className", loggingConfigName);
        }

    }


    private void removeLoggingConfig() {
        MDC.remove("className");
    }


    /**
     * @param scope
     * @throws Throwable
     */
    public void runSetup(final Scope currentScope) throws Throwable {
        log.debug("running setup for scope: " + currentScope);

        switch (currentScope) {
        case SUITE: {
            runBeforeAll();
            break;
        }
        case FEATURE: {
            runBeforeFeatures();
            break;
        }
        case SCENARIO: {
            runBeforeScenarios();
            break;
        }
        case SCENARIO_OUTLINE_ROW: {
            runBeforeScenarios();
            break;
        }
        default: {
            // no op STEP, SCENARIO_BACKGROUND
        }
        }

    }


    /**
     * @param scope
     */
    public void runTearDown(final Scope currentScope) throws Throwable {
        log.debug("runTearDown: " + currentScope);

        // TODO could implement this as methods on Scope itself
        switch (currentScope) {
        case SUITE: {
            runAfterAll();
            break;
        }
        case FEATURE: {
            runAfterFeatures();
            break;
        }
        case SCENARIO: {
            runAfterScenarios();
            // TODO for outline scenarios this might mean setup and tear down
            // gets run an extra time each...
            break;
        }
        case SCENARIO_OUTLINE_ROW: {
            runAfterScenarios();

            break;
        }
        default: {
            // no op STEP, SCENARIO_BACKGROUND
        }
        }

    }


    /**
     * @param b
     */
    public void setDryRun(final boolean dryRun) {
        this.dryRun = dryRun;

    }
}
