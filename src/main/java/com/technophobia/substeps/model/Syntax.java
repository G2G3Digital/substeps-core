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
package com.technophobia.substeps.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.technophobia.substeps.model.exception.DuplicateStepImplementationException;
import com.technophobia.substeps.model.exception.StepImplementationException;
import com.technophobia.substeps.model.exception.UnimplementedStepException;
import com.technophobia.substeps.runner.syntax.DefaultSyntaxErrorReporter;
import com.technophobia.substeps.runner.syntax.SyntaxErrorReporter;

/**
 * 
 * 
 * @author imoore
 * 
 */
public class Syntax {

    // These two will always be populated
    private final Map<String, PatternMap<StepImplementation>> stepImplementationMap = new HashMap<String, PatternMap<StepImplementation>>();

    // this might not be populated
    private PatternMap<ParentStep> subStepsMap = null;

    private boolean strict;
    private boolean failOnDuplicateStepImplementations = true;
    private String[] nonStrictKeywordPrecedence;

    private final SyntaxErrorReporter syntaxErrorReporter;

    public Syntax() {
        this(new DefaultSyntaxErrorReporter());
    }

    public Syntax(final SyntaxErrorReporter syntaxErrorReporter) {
        this.syntaxErrorReporter = syntaxErrorReporter;
    }

    public Map<String, PatternMap<StepImplementation>> getStepImplementationMap() {
        return stepImplementationMap;
    }

    public List<StepImplementation> getStepImplementations() {
        // build a list of the impls in the order of the annotations
        final List<StepImplementation> allImpls = new ArrayList<StepImplementation>();

        final List<String> sortedAnnotations = new ArrayList<String>();
        sortedAnnotations.addAll(stepImplementationMap.keySet());

        Collections.sort(sortedAnnotations);

        // get all the PatternMaps for each annotation:

        for (final String annotation : sortedAnnotations) {
            final PatternMap<StepImplementation> patternMap = stepImplementationMap.get(annotation);

            if (patternMap != null) {
                for (final StepImplementation impl : patternMap.values()) {
                    allImpls.add(impl);
                }
            }
        }
        return allImpls;
    }

    /**
     * @param keyWord
     * @return
     */
    private PatternMap<StepImplementation> getPatternMapForAnnotation(final String keyWord) {
        return stepImplementationMap.get(keyWord);
    }

    /**
     * @param loadSubSteps
     */
    public void setSubStepsMap(final PatternMap<ParentStep> loadSubSteps) {
        subStepsMap = loadSubSteps;
    }

    public PatternMap<ParentStep> getSubStepsMap() {
        return subStepsMap;
    }

    /**
     * @return
     */
    public List<ParentStep> getSortedRootSubSteps() {
        final Collection<ParentStep> rootSubSteps = subStepsMap.values();

        final List<ParentStep> sortedList = new ArrayList<ParentStep>();
        sortedList.addAll(rootSubSteps);

        Collections.sort(sortedList, ParentStep.PARENT_STEP_COMPARATOR);

        return sortedList;
    }

    /**
     * @param impl
     */
    public void addStepImplementation(final StepImplementation impl) {

        PatternMap<StepImplementation> patternMap = stepImplementationMap.get(impl.getKeyword());

        if (patternMap == null) {
            patternMap = new PatternMap<StepImplementation>();
            stepImplementationMap.put(impl.getKeyword(), patternMap);
        }

        final String pattern = impl.getValue();
        if (!patternMap.containsPattern(pattern)) {
            patternMap.put(pattern, impl);
        } else {
            final StepImplementationException ex = new DuplicateStepImplementationException(pattern,
                    patternMap.getValueForPattern(pattern), impl);
            syntaxErrorReporter.reportStepImplError(ex);
            if (failOnDuplicateStepImplementations) {
                throw ex;
            }
        }

    }

    /**
     * @param strict
     */
    public void setStrict(final boolean strict, final String[] nonStrictKeywordPrecedence) {
        this.strict = strict;
        this.nonStrictKeywordPrecedence = nonStrictKeywordPrecedence;

        if (!strict && (this.nonStrictKeywordPrecedence == null || this.nonStrictKeywordPrecedence.length == 0)) {
            throw new IllegalArgumentException(
                    "Please provide a keyword precedence in parameter nonStrictKeywordPrecedence to use when running in non strict mode");
        }
    }

    public void setFailOnDuplicateStepImplementations(final boolean failOnDuplicateStepImplementations) {
        this.failOnDuplicateStepImplementations = failOnDuplicateStepImplementations;
    }

    /**
     * @param parameterLine
     * @return
     */
    public List<StepImplementation> getStepImplementations(final String keyword, final String parameterLine) {
        return getStepImplementationsInternal(keyword, parameterLine, false);
    }

    public List<StepImplementation> checkForStepImplementations(final String keyword, final String parameterLine) {
        return getStepImplementationsInternal(keyword, parameterLine, true);
    }

    /**
     * @param keyword
     * @param parameterLine
     * @return
     */
    private List<StepImplementation> getStepImplementationsInternal(final String keyword, final String parameterLine,
            final boolean okNotTofindAnything) {
        List<StepImplementation> list = getStrictStepimplementation(keyword, parameterLine, okNotTofindAnything);

        if (!strict
                && ((list == null && okNotTofindAnything) || (!okNotTofindAnything && list != null && list.isEmpty()))) {
            // look for an alternative, iterate through the
            // nonStrictKeywordPrecedence until we get what we want

            for (final String altKeyword : nonStrictKeywordPrecedence) {
                // don't use the same keyword again
                if (altKeyword.compareToIgnoreCase(keyword) != 0) {
                    final List<StepImplementation> altStepImplementations = getStrictStepimplementation(altKeyword,
                            parameterLine.replaceFirst(keyword, altKeyword), okNotTofindAnything);
                    if (!altStepImplementations.isEmpty()) {
                        // found an alternative, bail immediately
                        list = new ArrayList<StepImplementation>(Collections2.transform(altStepImplementations,
                                new CloneStepImplementationsWithNewKeywordFunction(keyword)));
                        break;
                    }
                }
            }
        }

        return list;
    }

    /**
     * @param keyword
     * @param parameterLine
     * @return
     */
    private List<StepImplementation> getStrictStepimplementation(final String keyword, final String parameterLine,
            final boolean okNotTofindAnything) {

        List<StepImplementation> list = null;

        final PatternMap<StepImplementation> pMap = getPatternMapForAnnotation(keyword);

        if (pMap != null) {
            list = pMap.get(parameterLine);
        }

        else if (!okNotTofindAnything) {
            throw new UnimplementedStepException(parameterLine);
        }

        return list;
    }

    private static final class CloneStepImplementationsWithNewKeywordFunction implements
            Function<StepImplementation, StepImplementation> {

        private final String keyword;

        public CloneStepImplementationsWithNewKeywordFunction(final String keyword) {
            this.keyword = keyword;
        }

        public StepImplementation apply(final StepImplementation stepImplementation) {
            return stepImplementation.cloneWithKeyword(keyword);
        }
    }

    /**
     * @return the strict
     */
    public boolean isStrict() {
        return strict;
    }

    /**
     * @return the nonStrictKeywordPrecedence
     */
    public String[] getNonStrictKeywordPrecedence() {
        return nonStrictKeywordPrecedence;
    }
}
