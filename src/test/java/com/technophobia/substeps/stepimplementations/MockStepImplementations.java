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
package com.technophobia.substeps.stepimplementations;

import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.technophobia.substeps.model.SubSteps.Step;
import com.technophobia.substeps.model.SubSteps.StepImplementations;
import com.technophobia.substeps.runner.TestCallback;

/**
 * A Step implementation that will capture the number of times called
 * 
 * @author imoore
 * 
 */
@StepImplementations
public class MockStepImplementations {
    private final Logger log = LoggerFactory.getLogger(MockStepImplementations.class);

    private static boolean delay = false;
    private TestCallback callback;


    public MockStepImplementations(final TestCallback callback) {
        log.debug("BDDRunnerStepImplementations ctor with callback");
        this.callback = callback;
    }


    public MockStepImplementations() {
        log.debug("BDDRunnerStepImplementations ctor");
    }


    private void delay() {
        if (delay) {

            try {
                Thread.sleep(2000);
            } catch (final InterruptedException e) {
                // don't care
            }
        }
    }


    private void doCallback(final String methodName, final Object... params) {
        if (callback != null) {
            if (params != null) {
                final String[] strParams = new String[params.length];
                for (int i = 0; i < strParams.length; i++) {
                    strParams[i] = (String) params[i];
                }
                callback.doCallback(methodName, strParams);
            } else {
                callback.doCallback(methodName, null);
            }

        }
    }


    // Test_Given some background
    @Step(value = "Test_Given some background")
    public void meth1() {
        log.debug("meth 1");
        doCallback("meth1");
        delay();
    }


    // Test_Given something
    @Step(value = "Test_Given something")
    public void meth2() {
        log.debug("meth 2");
        doCallback("meth2");
        delay();
    }


    // Test_Given some outline background
    @Step(value = "Test_Given some outline background")
    public void meth3() {
        log.debug("meth 3");
        doCallback("meth3");
        delay();
    }


    // Test_Then something with a "parameter"
    @Step(value = "Test_Then something with a \"([^\"]*)\"")
    public void meth4(final String param) {
        log.debug("meth 4 with param: " + param);
        doCallback("meth4", param);
        delay();
        if (param.equalsIgnoreCase("barf")) {
            Assert.fail("I can't take it anymore");
        }

    }


    @Step(value = "Test_Then some other method never called")
    public void meth5() {
        log.debug("meth 5");
        doCallback("meth5");
        delay();
    }


    @Step(value = "Test_Then something has happened")
    public void meth6() {
        log.debug("meth 6");
        doCallback("meth6");
        delay();
    }


    @Step(value = "Test_Whatever gets called with ([^\"]*)")
    public void meth7(final String param) {
        log.debug("meth 7 with param: " + param);
        doCallback("meth7", param);
        delay();
    }


    @Step(value = "Test_Whatever is called with a quoted parameter \"?([^\"]*)\"?")
    public void meth8(final String param) {
        log.debug("meth 8 with param: " + param);
        doCallback("meth8");
        delay();
    }


    @Step(value = "Test_Then something else has happened")
    public void meth9() {
        log.debug("meth 9");
        doCallback("meth9");
        delay();
    }


    @Step(value = "Test_Given a step is defined with a table parameter")
    public void meth10(final List<Map<String, String>> tableParam) {
        log.debug("meth 10");
        doCallback("meth10");
        delay();
    }


    @Step(value = "Test_Then something blows up")
    public void meth11() {
        log.debug("meth 11");
        doCallback("meth11");
        Assert.fail("I can't take it anymore, something failing");
    }


    @Step("SingleWord")
    public void meth12() {
        log.debug("meth 12");
        doCallback("meth12");
    }

    @Step("Step with a variable \"([^\"]*)\"")
    public void meth13(String var){
        log.debug("meth13: " + var);
        doCallback("step-with-a-variable " + var);
    }
}
