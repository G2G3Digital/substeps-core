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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.technophobia.substeps.model.FeatureFile;
import com.technophobia.substeps.model.Scenario;
import com.technophobia.substeps.model.Syntax;
import com.technophobia.substeps.runner.syntax.FileUtils;

/**
 * @author ian
 * 
 */
public class TestParameters {
    private final Logger log = LoggerFactory.getLogger(TestParameters.class);

    private final TagManager tagManager;
    private final Syntax syntax;
    private final String featureFile;
    private List<FeatureFile> featureFileList = null;
    private boolean failParseErrorsImmediately = true;


    public TestParameters(final TagManager tagManager, final Syntax syntax, final String featureFile) {
        this.tagManager = tagManager;
        this.syntax = syntax;
        this.featureFile = featureFile;
    }


    public void init() {
        init(true);
    }


    public void init(final boolean failOnNoFeatures) {
        final List<File> featureFiles = FileUtils.getFiles(new File(featureFile), ".feature");

        final FeatureFileParser fp2 = new FeatureFileParser();
        for (final File f : featureFiles) {
            final FeatureFile fFile = fp2.loadFeatureFile(f);
            if (featureFileList == null) {
                featureFileList = new ArrayList<FeatureFile>();
            }
            if (fFile != null) {
                featureFileList.add(fFile);
            }
        }

        final File f = new File(".");
        log.debug("Current dir is: " + f.getAbsolutePath());

        if (failOnNoFeatures) {
            Assert.assertNotNull("No Feature files found!", featureFileList);
            Assert.assertFalse("No Feature files found!", featureFileList.isEmpty());
        } else if (featureFileList == null) {
            featureFileList = Collections.emptyList();
        }

        Collections.sort(featureFileList, new FeatureFileComparator());
    }


    /**
     * @return
     */
    public List<FeatureFile> getFeatureFileList() {
        return featureFileList;
    }


    public boolean isRunnable(final Scenario scenario) {
        return tagManager.acceptTaggedScenario(scenario.getTags());
    }


    public boolean isRunnable(final FeatureFile feature) {
        // a feature is runnable if any of the child scenarios are tagged
        // feature level tags are added to all children

        boolean runnable = false;
        for (final Scenario sc : feature.getScenarios()) {
            runnable = isRunnable(sc);
            if (runnable) {
                break;
            }
        }

        return runnable;
    }


    /**
     * @return
     */
    public Syntax getSyntax() {
        return syntax;
    }


    /**
     * @return
     */
    public boolean isFailParseErrorsImmediately() {
        return failParseErrorsImmediately;
    }


    /**
     * @param failParseErrorsImmediately
     *            the failParseErrorsImmediately to set
     */
    public void setFailParseErrorsImmediately(final boolean failParseErrorsImmediately) {
        this.failParseErrorsImmediately = failParseErrorsImmediately;
    }
}
