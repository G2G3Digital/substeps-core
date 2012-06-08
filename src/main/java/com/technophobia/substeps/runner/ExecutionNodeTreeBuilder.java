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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.technophobia.substeps.execution.ExecutionNode;
import com.technophobia.substeps.execution.Feature;
import com.technophobia.substeps.model.FeatureFile;
import com.technophobia.substeps.model.ParentStep;
import com.technophobia.substeps.model.PatternMap;
import com.technophobia.substeps.model.Scenario;
import com.technophobia.substeps.model.Step;
import com.technophobia.substeps.model.StepImplementation;
import com.technophobia.substeps.model.SubStepConfigurationException;
import com.technophobia.substeps.model.SubSteps.StepParameter;
import com.technophobia.substeps.model.Util;
import com.technophobia.substeps.model.parameter.Converter;

/**
 * @author ian
 * 
 */
public class ExecutionNodeTreeBuilder {
    private final Logger log = LoggerFactory.getLogger(ExecutionNodeTreeBuilder.class);

    private final TestParameters parameters;

    public ExecutionNodeTreeBuilder(final TestParameters parameters) {
        this.parameters = parameters;
    }

    public ExecutionNode buildExecutionNodeTree() {

        final ExecutionNode theRootNode = new ExecutionNode();

        for (final FeatureFile ff : parameters.getFeatureFileList()) {

            buildExecutionNodesForFeature(ff, theRootNode);
        }
        return theRootNode;
    }


    /**
     * @param notifier
     * @param ff
     */
    private void buildExecutionNodesForFeature(final FeatureFile ff, final ExecutionNode rootNode) {

        if (parameters.isRunnable(ff)) {

            final Feature feature = new Feature(ff.getName(), ff.getSourceFile().getName());

            final ExecutionNode featureNode = new ExecutionNode();
            featureNode.setFilename(ff.getSourceFile().getName());
            rootNode.addChild(featureNode);

            featureNode.setFeature(feature);

            featureNode.setTags(ff.getTags());

            for (final Scenario sc : ff.getScenarios()) {

                buildExectionNodeForScenario(sc, featureNode);
            }
        } else {
            log.debug("feature not runnable: " + ff.toString());
        }
    }

    // TODO - to turn off - @SuppressWarnings("PMD.AvoidCatchingThrowable")
    private void buildExectionNodeForScenario(final Scenario scenario,
            final ExecutionNode featureNode) {
        if (scenario != null) {

            if (parameters.isRunnable(scenario)) {

                final ExecutionNode scenarioNode = new ExecutionNode();
                featureNode.addChild(scenarioNode);

                scenarioNode.setScenarioName(scenario.getDescription());
                scenarioNode.setTags(scenario.getTags());

                try {
                    if (scenario.isOutline()) {

                        log.debug("building scenario outline tree");

                        int idx = 0;
                        for (final Map<String, String> outlineParameters : scenario
                                .getExampleParameters()) {
                            // invoke the scenario with these parameters as a
                            // context

                            final ExecutionNode scenarioOutlineNode = new ExecutionNode();
                            scenarioNode.addChild(scenarioOutlineNode);
                            scenarioNode.setOutline(true);
                            scenarioOutlineNode.setRowNumber(idx);

                            buildExectionNodeForScenario(scenario, outlineParameters,
                                    scenarioOutlineNode);
                            idx++;
                        }
                    }

                    else {

                        buildExectionNodeForScenario(scenario, null, scenarioNode);
                    }
                } catch (final Throwable t) {

                    // something has gone wrong parsing this scenario, no point
                    // running it so mark it as failed now
                    scenarioNode.getResult().setFailedToParse(t);

                    if (parameters.isFailParseErrorsImmediately()) {

                        throw new SubStepConfigurationException(t);
                    }
                }

            } else {
                log.debug("scenario not runnable: " + scenario.toString());
            }
        }
    }


    public void buildExectionNodeForScenario(final Scenario scenario,
            final Map<String, String> scenarioParameters, final ExecutionNode scenarioNode) {
        if (scenario.hasBackground()) {
            log.debug("building scenario background steps");

            processListOfSteps(scenario.getBackgroundSteps(), parameters.getSyntax()
                    .getSubStepsMap(), null, false, scenarioParameters, scenarioNode);
        }

        if (scenario.getSteps() != null && !scenario.getSteps().isEmpty()) {
            log.debug("processing scenario steps");

            processListOfSteps(scenario.getSteps(), parameters.getSyntax().getSubStepsMap(), null,
                    true, scenarioParameters, scenarioNode);
        }
    }


    private void processListOfSteps(final List<Step> steps,
            final PatternMap<ParentStep> subStepsMapLocal, final ParentStep parent,
            final boolean nonBackground, final Map<String, String> parametersForSteps,
            final ExecutionNode scenarioNode) {

        for (final Step step : steps) {

            substituteStepParameters(parametersForSteps, step);

            final ExecutionNode stepNode = new ExecutionNode();

            if (nonBackground) {
                scenarioNode.addChild(stepNode);
                // ie 'real'
            } else {
                // ie background
                scenarioNode.addBackground(stepNode);
            }

            // is this step defined as a root of some sub steps, ie a parent?
            ParentStep substepsParent = null;

            if (subStepsMapLocal != null) {
                substepsParent = locateSubStepsParent(subStepsMapLocal, step);
            }

            if (substepsParent != null) {

                // these are the child steps we want to execute

                substepsParent.initialiseParamValues(step.getParameterLine());

                final Map<String, String> parametersForSubSteps = substepsParent.getParamValueMap();

                stepNode.setLine(substepsParent.getParent().getParameterLine());

                final List<StepImplementation> list = parameters.getSyntax()
                        .checkForStepImplementations(step.getKeyword(), step.getParameterLine());

                if (list != null && !list.isEmpty()) {
                    final StepImplementation problem = list.get(0);

                    // we've got a step implementation that matches a parent
                    // step, ie a step that has substeps
                    // fail immediately or mark as parse error

                    final String msg = "line: [" + step.getParameterLine() + "] in ["
                            + step.getSource() + "] matches step implementation method: ["
                            + problem.getMethod().toString()
                            + "] AND matches a sub step definition: ["
                            + substepsParent.getParent().getParameterLine() + "] in ["
                            + substepsParent.getSubStepFile() + "]";

                    throw new SubStepConfigurationException(msg);

                }

                processListOfSteps(substepsParent.getSteps(), subStepsMapLocal, substepsParent,
                        nonBackground, parametersForSubSteps, stepNode);

            } else {

                executeStep(parent, nonBackground, step, stepNode);
            }
        }
    }


    /**
     * @param subStepsMapLocal
     * @param step
     * @return
     */
    private ParentStep locateSubStepsParent(final PatternMap<ParentStep> subStepsMapLocal,
            final Step step) {
        ParentStep substepsParent = subStepsMapLocal.get(step.getLine(), 0);

        // if we're not strict then we can look for other step defs that fit
        if (!parameters.getSyntax().isStrict() && substepsParent == null) {
            final String originalKeyword = step.getKeyword();

            for (final String altKeyword : parameters.getSyntax().getNonStrictKeywordPrecedence()) {
                // don't use the same keyword again
                if (altKeyword.compareToIgnoreCase(originalKeyword) != 0) {

                    final String altLine = step.getLine().replaceFirst(originalKeyword, altKeyword);
                    substepsParent = subStepsMapLocal.get(altLine, 0);
                    if (substepsParent != null) {
                        // do we need to modify the parent ??

                        substepsParent = substepsParent.cloneWithAltLine(altLine);

                        break;
                    }
                }
            }
        }

        return substepsParent;
    }


    public void substituteStepParameters(final Map<String, String> parametersForSteps,
            final Step step) {
        // if this is an outline, need to perform token replacement at this
        // level before passing down the chain
        if (parametersForSteps != null && !parametersForSteps.isEmpty()) {

            // replace any tokens in this step
            step.setParameterLine(substitutePlaceholders(step.getLine(), parametersForSteps));

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
                                substitutePlaceholders(e.getValue(), parametersForSteps));
                    }
                }

                step.setSubstitutedInlineTable(replacedInlineTable);
            }
        }
    }


    public void executeStep(final ParentStep parent, final boolean throwException, final Step step,
            final ExecutionNode stepNode) {

        log.debug("looking for impl for step: " + step.toString());

        if (parent != null && parent.getParamValueMap() != null) {
            step.setParameterLine(substitutePlaceholders(step.getLine(), parent.getParamValueMap()));
        }

        stepNode.setLine(step.getParameterLine());

        final StepImplementation execImpl = pickImplToExecute(step);

        if (execImpl != null) {

            stepNode.setTargetClass(execImpl.getImplementedIn());
            stepNode.setTargetMethod(execImpl.getMethod());

            try {
                setMethodParameters(execImpl, step.getParameterLine(), parent,
                        step.getSubstitutedInlineTable(), stepNode);

            } catch (final Throwable e) {

                if (throwException) {
                    throw new RuntimeException(e);
                } else {
                    log.debug(e.getMessage(), e);
                }
            } finally {
                // need to clear this out for the next time around
                step.setParameterLine(null);
            }
        } else {
            log.error("Unable to locate an implementation for the step: " + step.toDebugString());

            final SubStepConfigurationException e = new SubStepConfigurationException(
                    "Unable to locate an implementation for the step: " + step.toDebugString()
                            + " in " + step.getSource());

            throw e;

        }
    }


    private StepImplementation pickImplToExecute(final Step step) {

        StepImplementation impl = null;

        // using the specified 'phrase' look for a corresponding impl

        final List<StepImplementation> list = parameters.getSyntax().getStepImplementations(
                step.getKeyword(), step.getParameterLine());

        if (list != null && list.size() > 1) {
            log.error("found too many impls for line: " + step.getLine());

            for (final StepImplementation si : list) {
                log.error("impl: regex[" + si.getValue() + "] in "
                        + si.getImplementedIn().getSimpleName() + "." + si.getMethod().getName());
            }

            throw new SubStepConfigurationException("Ambiguity resolving step to impl: "
                    + step.toDebugString());
        }

        if (list != null && !list.isEmpty()) {
            impl = list.get(0);
        }

        return impl;
    }


    private void setMethodParameters(final StepImplementation execImpl, final String stepParameter,
            final ParentStep parent, final List<Map<String, String>> inlineTable,
            final ExecutionNode stepNode) throws IllegalArgumentException {

        final Method stepImplementationMethod = execImpl.getMethod();

        final Class<?>[] stepImplementationMethodParameterTypes = stepImplementationMethod
                .getParameterTypes();

        final Class<? extends Converter<?>>[] parameterConverters = getParameterConverters(stepImplementationMethod);

        if (stepImplementationMethodParameterTypes != null
                && stepImplementationMethodParameterTypes.length > 0) {
            Map<String, String> paramValueMap = null;

            if (parent != null) {
                paramValueMap = parent.getParamValueMap();
            }

            final Object[] methodParameters = getStepMethodArguments(stepParameter, paramValueMap,
                    execImpl.getValue(), inlineTable, stepImplementationMethodParameterTypes,
                    parameterConverters, stepNode);

            if (methodParameters.length != stepImplementationMethodParameterTypes.length) {
                throw new IllegalArgumentException(
                        "Argument mismatch between what expected for step impl and what found in feature");
            }
        }
    }


    private Object[] getStepMethodArguments(final String stepParameter,
            final Map<String, String> parentArguments, final String stepImplementationPattern,
            final List<Map<String, String>> inlineTable, final Class<?>[] parameterTypes,
            final Class<? extends Converter<?>>[] converterTypes, final ExecutionNode stepNode) {
        // does the stepParameter contain any <> which require substitution ?
        log.debug("getStepMethodArguments for: " + stepParameter);

        final String substitutedStepParam = substitutePlaceholders(stepParameter, parentArguments);

        stepNode.setLine(substitutedStepParam);
        List<Object> argsList = Util.getArgs(stepImplementationPattern, substitutedStepParam,
                parameterTypes, converterTypes);

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


    public String substitutePlaceholders(final String stepParameter,
            final Map<String, String> parentArguments) {
        // is there anything to replace?
        String rtn;
        final String paramRegEx = ".*<([^>]*)>.*";
        final Pattern findParamPattern = Pattern.compile(paramRegEx);
        if (parentArguments != null && findParamPattern.matcher(stepParameter).matches()) {
            // need to do a replacement, split on >
            rtn = stepParameter;
            final String paramRegEx2 = ".*<(.*)";
            final Pattern p2 = Pattern.compile(paramRegEx2);

            final String[] splits = stepParameter.split(">");

            for (final String s : splits) {
                final Matcher matcher = p2.matcher(s);
                if (matcher.find()) {
                    final String key = matcher.group(1);
                    final String val = parentArguments.get(key);
                    log.debug("replacing: <" + key + "> with: " + val + " in string: " + rtn);

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
