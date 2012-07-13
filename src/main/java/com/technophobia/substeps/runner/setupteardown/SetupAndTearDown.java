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

import java.lang.reflect.Method;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.technophobia.substeps.execution.MethodExecutor;
import com.technophobia.substeps.model.Scope;

/**
 * Class to encapsulate setup and tear down methods and the ordering of them
 * 
 * @author imoore
 * 
 */
public class SetupAndTearDown {

    private final Logger log = LoggerFactory.getLogger(SetupAndTearDown.class);

    private String loggingConfigName = null;

    private final MethodExecutor methodExecutor;
    private boolean dryRun = false;

    private final BeforeAndAfterMethods beforeAndAfterMethods;


    public String getLoggingConfigName() {
        return loggingConfigName;
    }


    public void setLoggingConfigName(final String loggingConfigName) {
        this.loggingConfigName = loggingConfigName;
    }


    public SetupAndTearDown(final Class<?>[] classes, final MethodExecutor methodExecutor) {

        beforeAndAfterMethods = new BeforeAndAfterMethods(classes);
        this.methodExecutor = methodExecutor;
        this.methodExecutor.addImplementationClasses(classes);
    }


    public void runBeforeAll() throws Throwable {

        prepareLoggingConfig();

        runAllMethods(MethodState.BEFORE_ALL);
    }


    /**
	 */
    public void runAfterAll() throws Throwable {
        runAllMethods(MethodState.AFTER_ALL);

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
    }


    /**
	 * 
	 */
    public void runBeforeScenarios() throws Throwable {
        runAllMethods(MethodState.BEFORE_SCENARIOS);
    }


    /**
	 */
    public void runAfterScenarios() throws Throwable {
        runAllMethods(MethodState.AFTER_SCENARIOS);

    }


    private void runAllMethods(final MethodState methodState) throws Throwable {

        if (!dryRun) {

            final List<Method> setupAndTearDownMethods = beforeAndAfterMethods
                    .getSetupAndTearDownMethods(methodState);

            methodExecutor.executeMethods(setupAndTearDownMethods);
        }
    }


    private void prepareLoggingConfig() {

        if (loggingConfigName == null) {

            MDC.put("className", "SubSteps");
            log.info("no logging config name supplied, defaulting to Substeps");
        } else {
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
        case SCENARIO:
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
        case SCENARIO:
        case SCENARIO_OUTLINE_ROW: {
            runAfterScenarios();

            // TODO for outline scenarios this might mean setup and tear down
            // gets run an extra time each...
            break;
        }
        default: {
            // no op STEP, SCENARIO_BACKGROUND
        }
        }

    }


    public void setDryRun(final boolean dryRun) {
        this.dryRun = dryRun;

    }
}
