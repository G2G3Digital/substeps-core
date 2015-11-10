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
package com.technophobia.substeps.runner.builder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.technophobia.substeps.execution.node.StepImplementationNode;
import com.technophobia.substeps.execution.node.StepNode;
import com.technophobia.substeps.execution.node.SubstepNode;
import com.technophobia.substeps.model.ExampleParameter;
import com.technophobia.substeps.model.ParentStep;
import com.technophobia.substeps.model.PatternMap;
import com.technophobia.substeps.model.Step;
import com.technophobia.substeps.model.StepImplementation;
import com.technophobia.substeps.model.SubSteps.StepParameter;
import com.technophobia.substeps.model.Util;
import com.technophobia.substeps.model.exception.SubstepsConfigurationException;
import com.technophobia.substeps.model.parameter.Converter;
import com.technophobia.substeps.runner.TestParameters;

public class SubstepNodeBuilder {

    private static final Logger log = LoggerFactory.getLogger(SubstepNodeBuilder.class);
    private final TestParameters parameters;

    SubstepNodeBuilder(final TestParameters parameters) {

        this.parameters = parameters;
    }

    public SubstepNode build(final String scenarioDescription, final List<Step> steps,
            final PatternMap<ParentStep> subStepsMapLocal, final ParentStep parent,
            final ExampleParameter parametersForSteps, final boolean throwExceptionIfUnableToBuildMethodArgs,
            final Set<String> tags, final int depth) {

        if (steps == null || steps.isEmpty()) {

            throw new SubstepsConfigurationException("There are no steps for " + scenarioDescription + " or a substep");
        }

        final List<StepNode> substeps = Lists.newArrayList();

        for (final Step step : steps) {

            substeps.add(buildStepNode(scenarioDescription, step, subStepsMapLocal, parent, parametersForSteps,
                    throwExceptionIfUnableToBuildMethodArgs, tags, depth + 1));

        }

        return new SubstepNode(substeps, tags, depth);
    }

    public StepNode buildStepNode(final String scenarioDescription, final Step step,
            final PatternMap<ParentStep> subStepsMapLocal, final ParentStep parent,
            final ExampleParameter parametersForSteps, final boolean throwExceptionIfUnableToBuildMethodArgs,
            final Set<String> tags, final int depth) {

        substituteStepParametersIntoStep(parametersForSteps, step);

        // is this step defined as a root of some sub steps, ie a parent?
        ParentStep substepsParent = null;

        if (subStepsMapLocal != null) {
            substepsParent = locateSubStepsParent(subStepsMapLocal, step);
        }

        StepNode stepNode;

        if (substepsParent != null) {

            log.trace("substepsParent != null for stepLine: " + step.getLine());

            stepNode = buildSubstepNode(scenarioDescription, step, subStepsMapLocal,
                    throwExceptionIfUnableToBuildMethodArgs, tags, depth, substepsParent);
            
            // this step was implemented by a substep as opposed to a step impl

        } else {
            log.trace("substepsParent == null for stepLine: " + step.getLine());

            stepNode = buildStepImplementationNode(parent, step, throwExceptionIfUnableToBuildMethodArgs, tags, depth);
        }

        return stepNode;
    }

    private SubstepNode buildSubstepNode(final String scenarioDescription, final Step step,
            final PatternMap<ParentStep> subStepsMapLocal, final boolean throwExceptionIfUnableToBuildMethodArgs,
            final Set<String> tags, final int depth, final ParentStep substepsParent) {

        log.trace("buildSubstepNode: scenarioDescription: " + scenarioDescription + " step line " + step.getLine() + " param line: " + step.getParameterLine());

        // to allow parameter substitution when we're not strict
        if (!this.parameters.getSyntax().isStrict()) {

            log.trace("init param vals as not strict");

            substepsParent.initialiseParamValues(-1, step.getParameterLine(), this.parameters.getSyntax().getNonStrictKeywordPrecedence());

        }
        else {

            log.trace("init param vals as strict");

            substepsParent.initialiseParamValues(-1, step.getParameterLine());
        }

        final ExampleParameter parametersForSubSteps = substepsParent.getParamValueMap();

        final List<StepImplementation> list = this.parameters.getSyntax().checkForStepImplementations(
                step.getKeyword(), step.getParameterLine(), step.getSource(), step.getSourceLineNumber());

        if (list != null && !list.isEmpty()) {
            final StepImplementation problem = list.get(0);

            // we've got a step implementation that matches a parent
            // step, ie a step that has substeps
            // fail immediately or mark as parse error

            final String msg = "line: [" + step.getParameterLine() + "] in [" + step.getSource()
                    + "] matches step implementation method: [" + problem.getMethod().toString()
                    + "] AND matches a sub step definition: [" + substepsParent.getParent().getParameterLine()
                    + "] in [" + substepsParent.getSubStepFile() + "]";

            throw new SubstepsConfigurationException(msg);

        }

        final SubstepNode substepNode = build(scenarioDescription, substepsParent.getSteps(), subStepsMapLocal,
                substepsParent, parametersForSubSteps, throwExceptionIfUnableToBuildMethodArgs, tags, depth);
     // Change TPCLA-299
     //  substepNode.setLine(substepsParent.getParent().getParameterLine());
       substepNode.setLine(step.getLine());
        substepNode.setFileUri(substepsParent.getSubStepFileUri());
        substepNode.setLineNumber(substepsParent.getSourceLineNumber());
        return substepNode;
    }

    private ParentStep locateSubStepsParent(final PatternMap<ParentStep> subStepsMapLocal, final Step step) {

        log.trace("locateSubStepsParent for step line: " + step.getLine());

        ParentStep substepsParent = subStepsMapLocal.get(step.getLine(), 0);

        // if we're not strict then we can look for other step defs that fit
        if (!this.parameters.getSyntax().isStrict() && substepsParent == null) {

            log.trace("nothing in subStepsMapLocal, not strict, checking elsewhere..");

            final String originalKeyword = step.getKeyword();

            for (final String altKeyword : this.parameters.getSyntax().getNonStrictKeywordPrecedence()) {
                // don't use the same keyword again
                if (altKeyword.compareToIgnoreCase(originalKeyword) != 0) {

                    final String altLine = step.getLine().replaceFirst(originalKeyword, altKeyword);
                    substepsParent = subStepsMapLocal.get(altLine, 0);
                    if (substepsParent != null) {
                        // do we need to modify the parent ??
                        log.trace("subStepsMapLocal.get(altLine) result for: " + altLine + " cloning with alt line");

                        // TODO - the alt line is the original source line, with the keyword swapped - variable names might not map when this step is initialised

                        substepsParent = substepsParent.cloneWithAltLine(substepsParent.getParent().getLine());

                        break;
                    }
                    else {
                        log.trace("subStepsMapLocal.get(altLine) no results for: " + altLine);
                    }
                }
            }
        }

        return substepsParent;
    }

    public void substituteStepParametersIntoStep(final ExampleParameter parametersForSteps, final Step step) {
        // if this is an outline, need to perform token replacement at this
        // level before passing down the chain
        if (parametersForSteps != null && !parametersForSteps.getParameters().isEmpty()) {

            // replace any tokens in this step
            step.setParameterLine(substitutePlaceholders(step.getLine(), parametersForSteps.getParameters()));

            final List<Map<String, String>> inlineTable = step.getInlineTable();
            if (inlineTable != null) {
                log.trace("substituting inline table values");

                final List<Map<String, String>> replacedInlineTable = new ArrayList<Map<String, String>>();

                for (final Map<String, String> row : inlineTable) {
                    final Map<String, String> replacedRow = new HashMap<String, String>();
                    replacedInlineTable.add(replacedRow);
                    final Set<Entry<String, String>> entrySet = row.entrySet();

                    for (final Entry<String, String> e : entrySet) {
                        replacedRow.put(e.getKey(),
                                substitutePlaceholders(e.getValue(), parametersForSteps.getParameters()));
                    }
                }

                step.setSubstitutedInlineTable(replacedInlineTable);
            }
        }
    }

    public StepImplementationNode buildStepImplementationNode(final ParentStep parent, final Step step,
            final boolean throwExceptionIfUnableToBuildMethodArgs, final Set<String> tags, final int depth) {

        log.debug("looking for impl for step: " + step.toString());

        if (parent != null && parent.getParamValueMap() != null) {
            step.setParameterLine(substitutePlaceholders(step.getLine(), parent.getParamValueMap().getParameters()));
        }

        final StepImplementation execImpl = pickImplToExecute(step);

        if (execImpl != null) {

            final StepImplementationNode stepImplementationNode = new StepImplementationNode(
                    execImpl.getImplementedIn(), execImpl.getMethod(), tags, depth);

            stepImplementationNode.setLine(step.getParameterLine());
            stepImplementationNode.setFileUri(step.getSource().getAbsolutePath());
            stepImplementationNode.setLineNumber(step.getSourceLineNumber());

            try {
                setMethodParameters(execImpl, step.getParameterLine(), parent, step.getSubstitutedInlineTable(),
                        stepImplementationNode);

            } catch (final Throwable e) {

                if (throwExceptionIfUnableToBuildMethodArgs) {
                    throw new RuntimeException(e);
                } else {
                    log.debug(e.getMessage(), e);
                }
            } finally {

                // need to clear this out for the next time around
                step.setParameterLine(null);
            }

            return stepImplementationNode;

        } else {

            log.error("Unable to locate an implementation for the step: " + step.toDebugString());

            throw new SubstepsConfigurationException("Unable to locate an implementation for the step: "
                    + step.toDebugString() + " in " + step.getSource());
        }
    }

    private StepImplementation pickImplToExecute(final Step step) {

        StepImplementation impl = null;

        // using the specified 'phrase' look for a corresponding impl

        final List<StepImplementation> list = this.parameters.getSyntax().getStepImplementations(step.getKeyword(),
                step.getParameterLine(), step.getSource(), step.getSourceLineNumber());

        if (list != null && list.size() > 1) {
            log.error("found too many impls for line: " + step.getLine());

            for (final StepImplementation si : list) {
                log.error("impl: regex[" + si.getValue() + "] in " + si.getImplementedIn().getSimpleName() + "."
                        + si.getMethod().getName());
            }

            throw new SubstepsConfigurationException("Ambiguity resolving step to impl: " + step.toDebugString());
        }

        if (list != null && !list.isEmpty()) {
            impl = list.get(0);
        }

        return impl;
    }

    private void setMethodParameters(final StepImplementation execImpl, final String stepParameter,
            final ParentStep parent, final List<Map<String, String>> inlineTable, final StepImplementationNode stepNode)
            throws IllegalArgumentException {

        final Method stepImplementationMethod = execImpl.getMethod();

        final Class<?>[] stepImplementationMethodParameterTypes = stepImplementationMethod.getParameterTypes();

        final Class<? extends Converter<?>>[] parameterConverters = getParameterConverters(stepImplementationMethod);

        if (stepImplementationMethodParameterTypes != null && stepImplementationMethodParameterTypes.length > 0) {
            Map<String, String> paramValueMap = null;

            if (parent != null && parent.getParamValueMap() != null) {
                paramValueMap = parent.getParamValueMap().getParameters();
            }

            final Object[] methodParameters = getStepMethodArguments(stepParameter, paramValueMap, execImpl.getValue(),
                    inlineTable, stepImplementationMethodParameterTypes, parameterConverters, stepNode);

            if (methodParameters.length != stepImplementationMethodParameterTypes.length) {
                throw new IllegalArgumentException(
                        "Argument mismatch between what expected for step impl and what found in feature");
            }
        }
    }

    private Object[] getStepMethodArguments(final String stepParameter, final Map<String, String> parentArguments,
            final String stepImplementationPattern, final List<Map<String, String>> inlineTable,
            final Class<?>[] parameterTypes, final Class<? extends Converter<?>>[] converterTypes,
            final StepImplementationNode stepNode) {
        // does the stepParameter contain any <> which require substitution ?
        log.debug("getStepMethodArguments for: " + stepParameter);

        final String substitutedStepParam = substitutePlaceholders(stepParameter, parentArguments);

        stepNode.setLine(substitutedStepParam);
        List<Object> argsList = Util.getArgs(stepImplementationPattern, substitutedStepParam, parameterTypes,
                converterTypes);

        if (inlineTable != null) {
            if (argsList == null) {
                argsList = new ArrayList<Object>();
            }
            argsList.add(inlineTable);
        }

        Object[] arguments = null;

        if (argsList != null) {
            arguments = new Object[argsList.size()];
            arguments = argsList.toArray(arguments);
        }

        stepNode.setMethodArgs(arguments);

        return arguments;
    }

    private Class<? extends Converter<?>>[] getParameterConverters(final Method method) {

        final Annotation[][] annotations = method.getParameterAnnotations();
        final int size = annotations.length;

        @SuppressWarnings("unchecked")
        final Class<? extends Converter<?>>[] result = new Class[size];

        for (int i = 0; i < size; i++) {
            for (final Annotation annotation : annotations[i]) {
                if (annotation instanceof StepParameter) {
                    result[i] = ((StepParameter) annotation).converter();
                }
            }
        }

        return result;
    }

    public String substitutePlaceholders(final String stepParameter, final Map<String, String> parentArguments) {
        // is there anything to replace?

        log.trace("substitutePlaceholders stepParameter: " + stepParameter);
        String rtn;
        final String paramRegEx = ".*<([^>]*)>.*";
        final Pattern findParamPattern = Pattern.compile(paramRegEx);
        if (parentArguments != null && findParamPattern.matcher(stepParameter).matches()) {

            log.trace("parentArguments != null && findParamPattern.matcher");

            // need to do a replacement, split on >
            rtn = stepParameter;
            final String paramRegEx2 = ".*<(.*)";
            final Pattern p2 = Pattern.compile(paramRegEx2);

            final String[] splits = stepParameter.split(">");

            for (final String s : splits) {
                final Matcher matcher = p2.matcher(s);
                if (matcher.find()) {
                    final String key = matcher.group(1);
                    String val = parentArguments.get(key);
                    log.debug("replacing: <" + key + "> with: " + val + " in string: [" + rtn + "]");

                    if ("value".equals(key)){
                        log.debug("break");
                    }

                    if (val == null) {
                        val = " ";
                    }

                    rtn = rtn.replaceAll("<" + key + ">", Matcher.quoteReplacement(val));

                }
            }
        } else {
            // nothing to replace
            rtn = stepParameter;
        }

        return rtn;
    }

}
