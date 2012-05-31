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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.technophobia.substeps.execution.ExecutionNode;

public class JunitFeatureRunner extends org.junit.runner.Runner  {
    private final Logger log = LoggerFactory.getLogger(JunitFeatureRunner.class);

    // TODO move these annotations off elsewhere ??

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public static @interface FeatureFiles {
        String featureFile();


        String subStepsFile() default "";


        Class<?>[] stepImplementations();


        String tagList() default "";


        boolean strict() default true;


        String[] nonStrictKeywordPrecedence() default {};


        Class<? extends DescriptionProvider> descriptionProvider() default EclipseDescriptionProvider.class;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public static @interface BeforeAndAfterProcessors {
        Class<?>[] value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public static @interface BeforeAllFeatures {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public static @interface AfterAllFeatures {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public static @interface BeforeEveryFeature {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public static @interface BeforeEveryScenario {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public static @interface AfterEveryFeature {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public static @interface AfterEveryScenario {
    }

    private final ExecutionNodeRunner runner = new ExecutionNodeRunner();

    private ExecutionConfig executionConfig = null;
    
    private DescriptionProvider descriptionProvider = null;

    private Class<?> classContainingTheTests;

//    private SetupAndTearDown setupAndTearDown;

//    private Syntax syntax;

    private Description thisDescription;

    private IJunitNotifier notifier;

    private ExecutionNode rootNode;
    
    
    // Used by tests only
    public JunitFeatureRunner() {
        notifier = new JunitNotifier();
    }


    // Used by tests only
    public JunitFeatureRunner(final IJunitNotifier notifier) {
        this.notifier = notifier;

    }


    // Constructor required by Junit
    public JunitFeatureRunner(final Class<?> classContainingTheTests) {
        // where classContainingTheTests is the class annotated with
        // @RunWith....


        log.debug("JunitStepRunner2 ctor with class: " + classContainingTheTests.getSimpleName());

        final FeatureFiles annotation = classContainingTheTests.getAnnotation(FeatureFiles.class);
        if (annotation == null) {
            Assert.fail("no Feature file annotation specified on the test class");
        }

        List<Class<?>> stepImpls = null;

        if (annotation.stepImplementations() != null) {
            stepImpls = new ArrayList<Class<?>>();
            for (final Class<?> c : annotation.stepImplementations()) {
                stepImpls.add(c);
            }
        }

        init(classContainingTheTests, stepImpls, annotation.featureFile(), annotation.tagList(),
                annotation.subStepsFile(), annotation.strict(),
                annotation.nonStrictKeywordPrecedence(), annotation.descriptionProvider());

        // TODO - perform -D overrides here ? Separate class to hold the values,
        // then init using that?
        // Speak to Stu about what he's doing here..?
    }


    /**
     * init method used by tests
     * 
     * @param reportedClass
     * @param stepImplementationClasses
     * @param featureFile
     * @param tags
     * @param subStepsFile
     */
    public final void init(final Class<?> reportedClass,
            final List<Class<?>> stepImplementationClasses, final String featureFile,
            final String tags, final String subStepsFile) {
        init(reportedClass, stepImplementationClasses, featureFile, tags, subStepsFile, true, null,
                EclipseDescriptionProvider.class);
    }


    public final void init(final Class<?> reportedClass,
            final List<Class<?>> stepImplementationClasses, final String featureFile,
            final String tags, final String subStepsFileName, final boolean strict,
            final String[] nonStrictKeywordPrecedence,
            final Class<? extends DescriptionProvider> descriptionProviderClass) {

        try {
            descriptionProvider = descriptionProviderClass.newInstance();
        } catch (final InstantiationException e) {
            log.error("Exception", e);
            Assert.fail("failed to instantiate description provider: "
                    + descriptionProviderClass.getName() + ":" + e.getMessage());
        } catch (final IllegalAccessException e) {
            log.error("Exception", e);
            Assert.fail("failed to instantiate description provider: "
                    + descriptionProviderClass.getName() + ":" + e.getMessage());
        }

        Assert.assertNotNull("descriptionProvider cannot be null", descriptionProvider);

        classContainingTheTests = reportedClass;
//        setupAndTearDown = new SetupAndTearDown(determineMethodExecutorsFromClass(classContainingTheTests));
//        setupAndTearDown.initialise(classContainingTheTests);


        executionConfig = new ExecutionConfig();
        executionConfig.setTags(tags);
        executionConfig.setFeatureFile(featureFile);
        executionConfig.setSubStepsFileName(subStepsFileName);
        executionConfig.setStrict(strict);
        executionConfig.setNonStrictKeywordPrecedence(nonStrictKeywordPrecedence);
        executionConfig.setStepImplementationClasses(stepImplementationClasses);
        
        if (classContainingTheTests != null) {
	        executionConfig.setDescription(classContainingTheTests.getSimpleName());
	        
	        final BeforeAndAfterProcessors annotation = classContainingTheTests.getAnnotation(BeforeAndAfterProcessors.class);
	        if (annotation != null){
	        	executionConfig.setInitialisationClasses(annotation.value());	
	        }
	        
        }        

        // not required by junit
// junit always fail fast - used by devs and testers ?
//      private boolean fastFailParseErrors = true;
//      private String nonFatalTags;
//      private String[] stepImplementationClassNames;

        
        rootNode = runner.prepareExecutionConfig(executionConfig, notifier);
        
        
        // tags ??
//        TagManager tagmanager = new TagManager(tags);
//        insertCommandLineTags(tagmanager);
//
//        File subStepsFile = null;
//
//        if (subStepsFileName != null) {
//            subStepsFile = new File(subStepsFileName);
//        }

//        syntax = SyntaxBuilder.buildSyntax(Thread.currentThread().getContextClassLoader(),
//                stepImplementationClasses, subStepsFile, strict, nonStrictKeywordPrecedence);

        // load up the features


        
//        TestParameters parameters = new TestParameters(tagmanager, syntax, featureFile);
//        parameters.init();
//        
//        final ExecutionNodeTreeBuilder nodeTreeBuilder = new ExecutionNodeTreeBuilder(parameters);
//
//        rootNode = nodeTreeBuilder.buildExecutionNodeTree();
        
        log.debug("rootNode.toDebugString():\n" + rootNode.toDebugString());

        final Map<Long, Description> descriptionMap = descriptionProvider.buildDescriptionMap(rootNode, classContainingTheTests);
        thisDescription = descriptionMap.get(Long.valueOf(rootNode.getId()));
        notifier = new JunitNotifier();

        notifier.setDescriptionMap(descriptionMap);
        
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.junit.runner.Runner#getDescription()
     */
    @Override
    public Description getDescription() {
        // this gets called 3 times !!!

        Assert.assertNotNull(thisDescription);

        return thisDescription;

    }



    /*
     * (non-Javadoc)
     * 
     * @see
     * org.junit.runner.Runner#run(org.junit.runner.notification.RunNotifier)
     */
    @Override
    public void run(final RunNotifier junitNotifier) {

        System.out.println("rootNode.toDebugString:\n" + rootNode.toDebugString());

        // for maven
        if (thisDescription == null) {
            thisDescription = getDescription();
        }

        log.debug("Description tree:\n" + printDescription(thisDescription, 0));

        ExecutionContext.put(Scope.SUITE, JunitNotifier.NOTIFIER_EXECUTION_KEY, notifier);

        notifier.setJunitRunNotifier(junitNotifier);
        runner.setNotifier(notifier);

        Assert.assertNotNull("execution config has not been initialised", executionConfig);

        runner.run();
        
        

    }


    /**
     * @param thisDescription2
     * @return
     */
    private String printDescription(final Description desc, final int depth) {
        final StringBuilder buf = new StringBuilder();

        buf.append(Strings.repeat("\t", depth));

        buf.append(desc.getDisplayName()).append("\n");

        if (desc.getChildren() != null && !desc.getChildren().isEmpty()) {
            for (final Description d : desc.getChildren()) {
                buf.append(printDescription(d, depth + 1));
            }
        }

        return buf.toString();
    }

    /**
     * @return
     */
    public ExecutionNode getRootExecutionNode() {
        return rootNode;
    }


}
