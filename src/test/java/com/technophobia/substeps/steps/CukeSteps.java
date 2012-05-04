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
package com.technophobia.substeps.steps;

import com.technophobia.substeps.model.SubSteps.Step;
import com.technophobia.substeps.model.SubSteps.StepImplementations;

/**
 * In here we'll have some cuke implementations of some methods
 * 
 * @author imoore
 * 
 */
@StepImplementations
public class CukeSteps {

    @Step("Given something")
    public void given() {
        System.out.println("given");
    }


    @Step("When an event occurs")
    public void when() {
        System.out.println("when");
    }


    @Step("Then bad things happen")
    public void then() {
        System.out.println("then");
    }


    @Step("And people get upset")
    public void and() {
        System.out.println("and");
    }


    @Step("And a parameter ([^\"]*) is supplied")
    public void param(final String param) {
        System.out.println("param: " + param);
    }


    @Step("Then method with param ([^\"]*)")
    public void meth1(final String name1) {
        System.out.println("meth1 param " + name1);
    }


    @Step("Then another method with param ([^\"]*)")
    public void meth2(final String name2) {
        System.out.println("meth2 param " + name2);
    }


    @Step("Whatever yee hah")
    public void whatever() {
        System.out.println("yeah whatever!!!");
    }
}
