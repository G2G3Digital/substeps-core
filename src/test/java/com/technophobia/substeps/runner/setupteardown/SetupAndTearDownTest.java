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

import org.junit.Assert;
import org.junit.Test;

import com.technophobia.substeps.execution.ImplementationCache;
import com.technophobia.substeps.runner.setupteardown.fake.BeforeAndAfterSequencing1;
import com.technophobia.substeps.runner.setupteardown.fake.BeforeAndAfterSequencing2;
import com.technophobia.substeps.runner.setupteardown.fake.BeforeAndAfterSequencing3;

/**
 * 
 * 
 * @author imoore
 * 
 */
public class SetupAndTearDownTest {

    @Test
    public void testOrderingOfSetupAndTearDown() {

        final SetupAndTearDown setupAndTearDown = new SetupAndTearDown(new Class<?>[] {
                BeforeAndAfterSequencing3.class, BeforeAndAfterSequencing2.class,
                BeforeAndAfterSequencing1.class }, new ImplementationCache());

        try {
            setupAndTearDown.runBeforeAll();
            setupAndTearDown.runAfterAll();
        } catch (final Throwable e) {

            e.printStackTrace();
            Assert.fail("befores and afters shouldn't fail for this test");
        }
        // check execution order:
        // BeforeAndAfterSequencing3, BeforeAndAfterSequencing2,
        // BeforeAndAfterSequencing1

        Assert.assertTrue("before all features executuon order incorrect",
                BeforeAndAfterSequencing3.beforeFeaturesExecTime > 0);

        Assert.assertTrue(
                "before all features executuon order incorrect",
                BeforeAndAfterSequencing3.beforeFeaturesExecTime < BeforeAndAfterSequencing2.beforeFeaturesExecTime);

        Assert.assertTrue(
                "before all features executuon order incorrect",
                BeforeAndAfterSequencing2.beforeFeaturesExecTime < BeforeAndAfterSequencing1.beforeFeaturesExecTime);

        // check the tear down order
        // BeforeAndAfterSequencing1, BeforeAndAfterSequencing2,
        // BeforeAndAfterSequencing3

        Assert.assertTrue("before all features executuon order incorrect",
                BeforeAndAfterSequencing1.afterAllFeaturesExecTime > 0);

        Assert.assertTrue(
                "before all features executuon order incorrect",
                BeforeAndAfterSequencing1.afterAllFeaturesExecTime < BeforeAndAfterSequencing2.afterAllFeaturesExecTime);

        Assert.assertTrue(
                "before all features executuon order incorrect",
                BeforeAndAfterSequencing2.afterAllFeaturesExecTime < BeforeAndAfterSequencing3.afterAllFeaturesExecTime);

    }

}
