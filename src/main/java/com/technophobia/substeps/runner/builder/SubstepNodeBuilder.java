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

    private static final Logger log = LoggerFactory.getLogger(ScenarioNodeBuilder.class);
    private final TestParameters parameters;

    SubstepNodeBuilder(TestParameters parameters) {

        this.parameters = parameters;
    }

    public SubstepNode build(String scenarioDescription, final List<Step> steps,
            final PatternMap<ParentStep> subStepsMapLocal, final ParentStep parent,
            final ExampleParameter parametersForSteps, boolean throwExceptionIfUnableToBuildMethodArgs,
            Set<String> tags, int depth) {

        if (steps == null || steps.isEmpty()) {

            throw new SubstepsConfigurationException("There are no steps for " + scenarioDescription + " or a substep");
        }

        List<StepNode> substeps = Lists.newArrayList();

        for (final Step step : steps) {

            substeps.add(buildStepNode(scenarioDescription, step, subStepsMapLocal, parent, parametersForSteps,
                    throwExceptionIfUnableToBuildMethodArgs, tags, depth));

        }

        return new SubstepNode(substeps, tags, depth);
    }

    public StepNode buildStepNode(String scenarioDescription, Step step, final PatternMap<ParentStep> subStepsMapLocal,
            final ParentStep parent, ExampleParameter parametersForSteps,
            boolean throwExceptionIfUnableToBuildMethodArgs, Set<String> tags, int depth) {

        substituteStepParametersIntoStep(parametersForSteps, step);

        // is this step defined as a root of some sub steps, ie a parent?
        ParentStep substepsParent = null;

        if (subStepsMapLocal != null) {
            substepsParent = locateSubStepsParent(subStepsMapLocal, step);
        }

        StepNode stepNode;

        if (substepsParent != null) {

            stepNode = buildSubstepNode(scenarioDescription, step, subStepsMapLocal,
                    throwExceptionIfUnableToBuildMethodArgs, tags, depth, substepsParent);

        } else {

            stepNode = buildStepImplementationNode(parent, step, throwExceptionIfUnableToBuildMethodArgs, depth);
        }

        return stepNode;
    }

    private SubstepNode buildSubstepNode(String scenarioDescription, Step step,
            final PatternMap<ParentStep> subStepsMapLocal, boolean throwExceptionIfUnableToBuildMethodArgs,
            Set<String> tags, int depth, ParentStep substepsParent) {
        substepsParent.initialiseParamValues(-1, step.getParameterLine());

        final ExampleParameter parametersForSubSteps = substepsParent.getParamValueMap();

        final List<StepImplementation> list = parameters.getSyntax().checkForStepImplementations(step.getKeyword(),
                step.getParameterLine());

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

        SubstepNode substepNode = build(scenarioDescription, substepsParent.getSteps(), subStepsMapLocal,
                substepsParent, parametersForSubSteps, throwExceptionIfUnableToBuildMethodArgs, tags, depth);

        substepNode.setLine(substepsParent.getParent().getParameterLine());
        substepNode.setFileUri(substepsParent.getSubStepFileUri());
        substepNode.setLineNumber(substepsParent.getSourceLineNumber());
        return substepNode;
    }

    private ParentStep locateSubStepsParent(final PatternMap<ParentStep> subStepsMapLocal, final Step step) {
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
            boolean throwExceptionIfUnableToBuildMethodArgs, int depth) {

        log.debug("looking for impl for step: " + step.toString());

        if (parent != null && parent.getParamValueMap() != null) {
            step.setParameterLine(substitutePlaceholders(step.getLine(), parent.getParamValueMap().getParameters()));
        }

        final StepImplementation execImpl = pickImplToExecute(step);

        if (execImpl != null) {

            StepImplementationNode stepImplementationNode = new StepImplementationNode(execImpl.getImplementedIn(),
                    execImpl.getMethod(), depth);

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

        final List<StepImplementation> list = parameters.getSyntax().getStepImplementations(step.getKeyword(),
                step.getParameterLine());

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
                    String val = parentArguments.get(key);
                    log.debug("replacing: <" + key + "> with: " + val + " in string: " + rtn);

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
