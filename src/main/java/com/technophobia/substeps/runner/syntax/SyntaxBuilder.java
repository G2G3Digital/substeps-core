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
package com.technophobia.substeps.runner.syntax;

import java.io.File;
import java.util.Collections;
import java.util.List;

import com.technophobia.substeps.model.SubSteps;
import com.technophobia.substeps.model.Syntax;
import com.technophobia.substeps.scanner.ClasspathScanner;

/**
 * 
 * @author imoore
 * 
 */
public final class SyntaxBuilder {
    private SyntaxBuilder() {
    }


    public static List<Class<?>> getStepImplementationClasses(final ClassLoader classLoader, final String[] classpath) {
        final ClasspathScanner cpScanner = new ClasspathScanner();

        final List<Class<?>> implClassList = cpScanner.getClassesWithAnnotation(SubSteps.StepImplementations.class,
                classLoader, classpath);

        return implClassList;
    }


    public static Syntax buildSyntax(final List<Class<?>> stepImplementationClasses, final File subStepsFile) {
        return buildSyntax(stepImplementationClasses, subStepsFile, true, null);
    }


    public static Syntax buildSyntax(final List<Class<?>> stepImplementationClasses, final File subStepsFile,
            final boolean strict, final String[] nonStrictKeywordPrecedence) {
        return buildSyntax(stepImplementationClasses, subStepsFile, strict, nonStrictKeywordPrecedence,
                new ClassAnalyser());
    }


    public static Syntax buildSyntax(final List<Class<?>> stepImplementationClasses, final File subStepsFile,
            final boolean strict, final String[] nonStrictKeywordPrecedence, final ClassAnalyser classAnalyser) {
        return buildSyntax(stepImplementationClasses, subStepsFile, strict, nonStrictKeywordPrecedence, classAnalyser,
                true);
    }


    public static Syntax buildSyntax(final List<Class<?>> stepImplementationClasses, final File subStepsFile,
            final boolean strict, final String[] nonStrictKeywordPrecedence, final ClassAnalyser classAnalyser,
            final boolean failOnDuplicateEntries) {
        final Syntax syntax = buildBaseSyntax(stepImplementationClasses, classAnalyser, failOnDuplicateEntries);

        syntax.setStrict(strict, nonStrictKeywordPrecedence);

        if (subStepsFile != null) {
            final SubStepDefinitionParser subStepParser = new SubStepDefinitionParser(failOnDuplicateEntries);
            syntax.setSubStepsMap(subStepParser.loadSubSteps(subStepsFile));
        }

        return syntax;
    }


    private static Syntax buildBaseSyntax(final List<Class<?>> stepImplementationClasses,
            final ClassAnalyser classAnalyser, final boolean failOnDuplicateEntries) {
        // step implementations (arranged by StepDefinition, ie the annotation)
        // +
        // sub step definitions

        final Syntax syntax = new Syntax();
        syntax.setFailOnDuplicateStepImplementations(failOnDuplicateEntries);

        final List<Class<?>> implClassList;

        if (stepImplementationClasses != null) {
            implClassList = stepImplementationClasses;
        } else {
            implClassList = Collections.emptyList();
        }

        for (final Class<?> implClass : implClassList) {
            classAnalyser.analyseClass(implClass, syntax);
        }

        return syntax;
    }
}
