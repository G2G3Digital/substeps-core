package com.technophobia.substeps.report;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.technophobia.substeps.execution.ExecutionNode;
import com.technophobia.substeps.execution.ExecutionNodeVisitor;

public final class ScreenshotWriter implements ExecutionNodeVisitor {

    public static final String SCREENSHOT_SUFFIX = "node-failure.png";

    private File directoryForScreenshots;

    private final static Logger logger = LoggerFactory.getLogger(ScreenshotWriter.class);

    public static void writeScreenshots(File directoryForScreenshots, ExecutionNode rootNode) {

        rootNode.accept(new ScreenshotWriter(directoryForScreenshots));
    }

    private ScreenshotWriter(File directoryForScreenshots) {

        directoryForScreenshots.mkdirs();
        this.directoryForScreenshots = directoryForScreenshots;
    }

    public void visit(ExecutionNode executionNode) {


        if (executionNode.getResult() != null) {

            byte[] screenshot = executionNode.getResult().getScreenshot();

            if (screenshot != null) {

                File screenshotFile = new File(directoryForScreenshots, executionNode.getId() + SCREENSHOT_SUFFIX);

                try {

                    IOUtils.write(screenshot, new FileOutputStream(screenshotFile));

                } catch (FileNotFoundException e) {

                    // TODO
                } catch (IOException e) {

                    // TODO
                }

            }
        }

    }

}
