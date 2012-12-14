package com.technophobia.substeps.report;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.technophobia.substeps.execution.AbstractExecutionNodeVisitor;
import com.technophobia.substeps.execution.node.ExecutionNode;
import com.technophobia.substeps.execution.node.StepImplementationNode;

public final class ScreenshotWriter extends AbstractExecutionNodeVisitor<Void> {

    public static final String SCREENSHOT_SUFFIX = "node-failure.png";

    private File directoryForScreenshots;

    private final static Logger log = LoggerFactory.getLogger(ScreenshotWriter.class);

    public static void writeScreenshots(File directoryForScreenshots, ExecutionNode rootNode) {

        rootNode.accept(new ScreenshotWriter(directoryForScreenshots));
    }

    private ScreenshotWriter(File directoryForScreenshots) {

        directoryForScreenshots.mkdirs();
        this.directoryForScreenshots = directoryForScreenshots;
    }

    public Void visit(StepImplementationNode executionNode) {

        if (executionNode.getResult() != null) {

            byte[] screenshot = executionNode.getResult().getScreenshot();

            if (screenshot != null) {

                File screenshotFile = new File(directoryForScreenshots, executionNode.getId() + SCREENSHOT_SUFFIX);

                try {

                    IOUtils.write(screenshot, new FileOutputStream(screenshotFile));

                } catch (Exception e) {

                    log.error("Unable to create screenshot", e);
                }

            }
        }
        
        return null;
    }

}
