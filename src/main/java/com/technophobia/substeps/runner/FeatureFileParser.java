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
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.technophobia.substeps.model.Background;
import com.technophobia.substeps.model.FeatureFile;
import com.technophobia.substeps.model.Scenario;
import com.technophobia.substeps.model.Step;

/**
 * @author ian
 * 
 */
public class FeatureFileParser {

    private final Logger log = LoggerFactory.getLogger(FeatureFileParser.class);

    private static Map<String, Directive> directiveMap = new HashMap<String, Directive>();

    // TODO - remove ?
    private List<String> currentFeatureFileLines = null;
    private int[] currentFileOffsets = null;

    private String currentOriginalFileContents = null;


    public FeatureFile loadFeatureFile(final File featureFile) {
        // IM - this is a little clumsy, feature file created, passed around and
        // if invalid, discarded..

        // rest our current set of lines
        this.currentFeatureFileLines = null;

        final FeatureFile ff = new FeatureFile();
        ff.setSourceFile(featureFile);

        Assert.assertTrue("Feature file: " + featureFile.getAbsolutePath()
                + " does not exist!", featureFile.exists());

        readFeatureFile(featureFile);

        final String deCommented = stripCommentsAndBlankLines(this.currentFeatureFileLines);

        chunkUpFeatureFile(deCommented, ff);

        if (parseFeatureDescription(ff)) {
            // now we're in chunks, time to process each scenario..
            if (ff.getScenarios() != null) {

                for (final Scenario sc : ff.getScenarios()) {
                    buildScenario(sc, featureFile);

                }

                cascadeTags(ff);

                cleanup();

                return ff;
            } else {
                this.log.debug("discarding feature " + featureFile.getName()
                        + "as no scenarios");
                return null;
            }
        } else {
            this.log.debug("discarding feature " + featureFile.getName()
                    + "as no feature description");
            return null;
        }

    }


    /**
	 * 
	 */
    private void cleanup() {
        this.currentOriginalFileContents = null;
        this.currentFeatureFileLines = null;
        this.currentFileOffsets = null;
    }


    /**
     * @param featureFile
     */
    private void readFeatureFile(final File featureFile) {

        this.currentFeatureFileLines = null;

        try {
            this.currentFeatureFileLines = Files.readLines(featureFile,
                    Charset.forName("UTF-8"));

            // TODO - is this trim still required now we have line numbers ?

            // new ArrayList<String>(Collections2.transform(
            // Files.readLines(featureFile, Charset.forName("UTF-8")), new
            // Function<String, String>() {
            // public String apply(final String input) {
            // return input.trim();
            // }
            // }));

            this.currentOriginalFileContents = Files.toString(featureFile,
                    Charset.forName("UTF-8"));

            this.currentFileOffsets = new int[this.currentFeatureFileLines
                    .size()];

            int lastOffset = 0;
            for (int i = 0; i < this.currentFeatureFileLines.size(); i++) {

                final String s = this.currentFeatureFileLines.get(i);

                this.currentFileOffsets[i] = this.currentOriginalFileContents
                        .indexOf(s, lastOffset);
                lastOffset = this.currentFileOffsets[i] + s.length();
            }

        } catch (final IOException e) {
            this.log.error("failed to load feature file: " + e.getMessage(), e);
        }
    }


    private String getFirstLinePattern(final String element) {

        final StringBuilder buf = new StringBuilder();
        final String[] lines = element.split("\n");
        // add a wildcard to allow # comments on the end of the line and
        // also tab / space formatting

        final boolean first = true;
        buf.append("(").append(Pattern.quote(lines[0])).append(")");
        // for (final String s : lines) {
        //
        // if (!first) {
        // buf.append(".*");
        // }
        // first = false;
        // buf.append(Pattern.quote(s));
        // }
        // buf.append(")");
        return buf.toString();
    }


    /**
     * @param ff
     */
    private void cascadeTags(final FeatureFile ff) {
        // add any feature level tags to all scenario children

        if (ff != null && ff.getTags() != null && !ff.getTags().isEmpty()) {
            for (final Scenario sc : ff.getScenarios()) {
                if (sc.getTags() == null) {
                    sc.setTags(ff.getTags());
                } else {
                    sc.getTags().addAll(ff.getTags());
                }
            }
        }
    }


    /**
     * @param ff
     */
    private boolean parseFeatureDescription(final FeatureFile ff) {
        boolean valid = true;
        final String raw = ff.getRawText();

        if (Strings.isNullOrEmpty(raw)) {
            valid = false;
        } else {
            final String[] lines = raw.split("\n");
            final StringBuilder description = new StringBuilder();

            for (int i = 0; i < lines.length; i++) {
                final String line = lines[i];
                if (i == 0) {
                    // first line, description is everything after the :
                    final int idx = line.indexOf(':');
                    ff.setName(line.substring(idx + 1).trim());
                } else {
                    if (description.length() > 0) {
                        description.append("\n");
                    }
                    description.append(line);
                }
            }
        }
        return valid;
    }


    private int getSourceLineNumber(final String line, final int offset) {

        int lineNumber = -1;
        // find the line from the offset
        final int idx = this.currentOriginalFileContents.indexOf(line);

        if (idx != -1) {
            // what's the line number of this offset ?
            lineNumber = getSourceLineNumberForOffset(offset);
        }
        return lineNumber;
    }


    private int getSourceLineNumberForOffset(final int offset) {

        int lineNumber = -1;
        lineNumber = 0;
        for (; lineNumber < this.currentFileOffsets.length; lineNumber++) {

            if (this.currentFileOffsets[lineNumber] > offset) {
                break;
            }
        }
        return lineNumber;
    }


    /**
     * @param sc
     */
    private void buildScenario(final Scenario sc, final File file) {

        final String raw = sc.getRawText();

        final String[] lines = raw.split("\n");

        boolean collectExamples = false;

        int lastOffset = sc.getSourceStartOffset();

        sc.setSourceStartLineNumber(getSourceLineNumberForOffset(lastOffset));

        for (int i = 0; i < lines.length; i++) {
            final String line = lines[i];

            // need to find the line number using an offset. move the offset as
            // we progress through the lines, that way we can take into account
            // duplicates

            final int lineNumber = getSourceLineNumber(line, lastOffset);
            if (lineNumber + 1 < this.currentFileOffsets.length) {
                lastOffset = this.currentFileOffsets[lineNumber + 1] - 1;
            } else {
                lastOffset = this.currentOriginalFileContents.length();
            }

            if (i == 0) {
                // first line, description is everything after the :
                final int idx = line.indexOf(':');
                sc.setDescription(line.substring(idx + 1).trim());
                sc.setScenarioLineNumber(lineNumber);
            } else if (line.startsWith(Directive.EXAMPLES.val)) {
                collectExamples = true;
            } else {
                if (line.startsWith("|")) {

                    if (collectExamples) {
                        // we're now onto the examples
                        parseExamples(lineNumber, line, sc);
                    } else {
                        // this is an inline table
                        final Step last = sc.getSteps().get(
                                sc.getSteps().size() - 1);
                        final String[] data = line.split("\\|");
                        last.addTableData(data);
                    }

                } else {
                    sc.addStep(new Step(line, file, lineNumber));
                }
            }
        }
    }

    private static final Pattern DIRECTIVE_PATTERN = Pattern
            .compile("([\\w ]*):");


    /**
     * @param fileContents
     * @param ff
     */
    private void chunkUpFeatureFile(final String fileContents,
            final FeatureFile ff) {
        // get the feature name / description
        // split the feature file up

        final String topLevelFeatureElements[] = fileContents
                .split("(?=Tags:)|(?=Feature:)|(?=Background:)|(?=Scenario:)|(?=Scenario Outline:)");

        Set<String> currentTags = null;

        if (topLevelFeatureElements != null) {
            String currentBackground = null;

            for (final String element : topLevelFeatureElements) {

                if (!Strings.isNullOrEmpty(element)) {

                    this.log.trace("topLevelElement:\n" + element);

                    // grab the identifer

                    final Matcher m = DIRECTIVE_PATTERN.matcher(element);
                    if (m.lookingAt()) {
                        final Directive directive = directiveMap
                                .get(m.group(1));

                        switch (directive) {
                        case TAGS: {
                            if (currentTags == null) {
                                currentTags = new HashSet<String>();
                            }
                            processTags(currentTags, element);
                            break;
                        }
                        case FEATURE: {
                            ff.setRawText(element);
                            if (currentTags != null) {
                                ff.setTags(currentTags);
                            }
                            currentTags = null;
                            currentBackground = null;
                            break;
                        }
                        case BACKGROUND: {
                            // stash
                            currentBackground = element;
                            break;
                        }
                        case SCENARIO:
                        case SCENARIO_OUTLINE: {

                            final String firstLinePattern = getFirstLinePattern(element);

                            final Pattern finderPattern = Pattern
                                    .compile(firstLinePattern);// ,
                                                               // Pattern.DOTALL);

                            final Matcher matcher = finderPattern
                                    .matcher(this.currentOriginalFileContents);
                            int start = -1;

                            if (matcher.find()) {
                                start = matcher.start(0);
                                // start offsets of this elem into the
                                // original file
                            }

                            processScenarioDirective(ff, currentTags,
                                    currentBackground, element,
                                    directive == Directive.SCENARIO_OUTLINE,
                                    start);

                            currentTags = null;
                            break;
                        }
                        default: {
                            this.log.error("unknown directive");
                            break;
                        }
                        }
                    }

                }
            }
        }
    }


    /**
     * @param ff
     * @param currentTags
     * @param currentBackground
     * @param sc
     * @param outline
     * @return
     */
    private void processScenarioDirective(final FeatureFile ff,
            final Set<String> currentTags, final String currentBackground,
            final String sc, final boolean outline, final int start) {
        final Scenario scenario = new Scenario();

        scenario.setRawText(sc);
        scenario.setTags(currentTags);
        scenario.setOutline(outline);
        scenario.setSourceStartOffset(start);

        ff.addScenario(scenario);

        if (currentBackground != null) {
            scenario.setBackground(new Background(backgroundLineNumber(),
                    currentBackground, ff.getSourceFile()));

        }
    }


    private int backgroundLineNumber() {
        for (int i = 0; i < this.currentFeatureFileLines.size(); i++) {
            if (this.currentFeatureFileLines.get(i).startsWith("Background:")) {
                return i;
            }

        }
        return 0;
    }


    /**
     * @param currentTags
     * @param sc
     */
    private void processTags(final Set<String> currentTags, final String raw) {
        // break up the tags - TODO - this is where we will need to evaluate any
        // boolean logic of tag expressions

        final String postDirective = raw.substring(raw.indexOf(':') + 1);

        // final String[] split = raw.split("(?=@)");
        final String[] split = postDirective.split("\\s");
        for (final String s : split) {
            final String trimmed = s.trim();
            if (trimmed.length() > 0) {
                currentTags.add(s.trim());
            }
        }
    }


    public static String stripComments(final String line) {
        String trimmed = null;
        if (line != null) {

            final int idx = line.trim().indexOf("#");
            if (idx >= 0) {
                // is the # inside matched quotes

                boolean doTrim = false;

                if (idx == 0) {
                    // first char
                    doTrim = true;
                }

                final String[] splitByQuotes = line.split("\"[^\"]*\"|'[^']*'");
                // this will find parts of the string not in quotes
                for (final String split : splitByQuotes) {
                    if (split.indexOf("#") > 0) {
                        // hash exists not in a matching pair of quotes
                        doTrim = true;
                        break;
                    }
                }

                if (doTrim) {
                    trimmed = line.trim().substring(0, idx).trim();
                } else {
                    trimmed = line.trim();
                }
            } else {
                trimmed = line.trim();
            }
        }
        return trimmed;
    }


    /**
     * @param featureFile
     * @return
     */
    private String stripCommentsAndBlankLines(final List<String> lines) {

        final StringBuilder buf = new StringBuilder();

        for (final String s : lines) {

            final String trimmed = stripComments(s);

            if (!Strings.isNullOrEmpty(trimmed)) {
                // up for inclusion
                buf.append(trimmed);
                buf.append("\n");
            }
        }

        return buf.toString();
    }


    /**
     * @param trimmed
     */
    private void parseExamples(final int lineNumber, final String trimmed,
            final Scenario sc) {
        final String[] split = trimmed.split("\\|");

        if (sc.getExampleParameters() == null) {
            sc.addExampleKeys(split);
            sc.setExampleKeysLineNumber(lineNumber);
        } else {
            sc.addExampleValues(lineNumber, split);
        }

    }

    private static enum Directive {
        // @formatter:off
        TAGS("Tags"), FEATURE("Feature"), BACKGROUND("Background"), SCENARIO("Scenario"), SCENARIO_OUTLINE(
                "Scenario Outline"), EXAMPLES("Examples");

        // @formatter:on

        Directive(final String val) {
            this.val = val;
        }

        private final String val;

    }

    static {
        for (final Directive d : Directive.values()) {
            directiveMap.put(d.val, d);
        }
    }

}
