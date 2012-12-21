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

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.technophobia.substeps.model.exception.SubstepsConfigurationException;

public class InitialisationClassOrdererTest {

    private static class A {
    }

    private static class B {
    }

    private static class C {
    }

    private static class D {
    }

    private static class X {

    }

    @Test(expected = SubstepsConfigurationException.class)
    public void testMultipleParents() {

        InitialisationClassOrderer orderer = new InitialisationClassOrderer();

        orderer.addOrderedInitialisationClasses(A.class, B.class, C.class);

        orderer.addOrderedInitialisationClasses(A.class, X.class, C.class);

        orderer.addOrderedInitialisationClasses(A.class, X.class, C.class, B.class);
    }

    @Test
    public void testReordering() {

        InitialisationClassOrderer orderer = new InitialisationClassOrderer();

        orderer.addOrderedInitialisationClasses(A.class, B.class, C.class);

        orderer.addOrderedInitialisationClasses(A.class, X.class, C.class);

        orderer.addOrderedInitialisationClasses(A.class, B.class, X.class, C.class);

        Assert.assertEquals(Lists.<Class<?>> newArrayList(A.class, B.class, X.class, C.class), orderer.getOrderedList());

    }

    @Test(expected = SubstepsConfigurationException.class)
    public void testBadOrder() {

        InitialisationClassOrderer orderer = new InitialisationClassOrderer();

        orderer.addOrderedInitialisationClasses(A.class, B.class, C.class);

        orderer.addOrderedInitialisationClasses(A.class, D.class);

        orderer.addOrderedInitialisationClasses(A.class, X.class, B.class);

        orderer.addOrderedInitialisationClasses(B.class, X.class);
    }

}
