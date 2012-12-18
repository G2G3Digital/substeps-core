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
