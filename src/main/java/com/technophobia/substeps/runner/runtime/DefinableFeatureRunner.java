package com.technophobia.substeps.runner.runtime;

import java.io.File;

import com.google.common.collect.Lists;
import com.technophobia.substeps.runner.IJunitNotifier;
import com.technophobia.substeps.runner.JunitFeatureRunner;

public class DefinableFeatureRunner extends JunitFeatureRunner {

    public DefinableFeatureRunner() {
        super();
    }


    public DefinableFeatureRunner(final IJunitNotifier notifier) {
        super(notifier);
    }


    public DefinableFeatureRunner(final Class<?> clazz) {
        super();
        final String outputFolder = System.getProperty("outputFolder");
        final String path = new File(outputFolder).getAbsolutePath();
        final ClassLocator classLocator = new StepClassLocator(path);

        init(clazz, Lists.newArrayList(classLocator.fromPath(path)), System.getProperty("substepsFeatureFile"), "", "",
                new Class<?>[0]);
    }
}
