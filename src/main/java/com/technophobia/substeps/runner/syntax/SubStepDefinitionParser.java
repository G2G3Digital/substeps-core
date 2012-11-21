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
package com.technophobia.substeps.runner.syntax;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.technophobia.substeps.model.DuplicatePatternException;
import com.technophobia.substeps.model.ParentStep;
import com.technophobia.substeps.model.PatternMap;
import com.technophobia.substeps.model.Step;
import com.technophobia.substeps.parser.FileContents;

/**
 * @author ian
 * 
 */
public class SubStepDefinitionParser {
    private final Logger log = LoggerFactory
            .getLogger(SubStepDefinitionParser.class);

    private ParentStep currentParentStep;

    private final PatternMap<ParentStep> parentMap = new PatternMap<ParentStep>();

    private final boolean failOnDuplicateSubsteps;

    private final SyntaxErrorReporter syntaxErrorReporter;

    private FileContents currentFileContents;


    public SubStepDefinitionParser(final SyntaxErrorReporter syntaxErrorReporter) {
        this(true, syntaxErrorReporter);
    }


    public SubStepDefinitionParser(final boolean failOnDuplicateSubsteps,
            final SyntaxErrorReporter syntaxErrorReporter) {
        this.failOnDuplicateSubsteps = failOnDuplicateSubsteps;
        this.syntaxErrorReporter = syntaxErrorReporter;
    }


    private void parseSubStepFile(final File substepFile) {

        this.currentFileContents = new FileContents();

        try {

            this.currentFileContents.readFile(substepFile);

            final List<String> lines = this.currentFileContents.getLines();// Files.readLines(substepFile,
                                                                           // Charset.forName("UTF-8"));

            for (int i = 0; i < this.currentFileContents.getNumberOfLines(); i++) {

                processLine(i);
            }

            // for (int i = 0; i < lines.size(); i++) {
            // // for (final String line : lines) {
            // final String line = lines.get(i);
            // this.log.trace("substep line[" + substepFile.getName() + "]: "
            // + line);
            // processLine(line, substepFile, i);
            // }

            if (this.currentParentStep != null) {
                // add the last scenario in, but only if it has some steps

                if (this.currentParentStep.getSteps() != null
                        && !this.currentParentStep.getSteps().isEmpty()) {
                    storeForPatternOrReportFailure(substepFile,
                            this.currentParentStep.getParent().getPattern(),
                            this.currentParentStep);
                } else {

                    this.log.warn("Ignoring substep definition ["
                            + this.currentParentStep.getParent().getLine()
                            + "] as it has no steps");
                }
                // we're moving on to another file, so set this to null.
                // TODO - pass this around rather than stash the state
                this.currentParentStep = null;
            }
        } catch (final FileNotFoundException e) {
            this.log.error(e.getMessage(), e);
        } catch (final IOException e) {

            this.log.error(e.getMessage(), e);
        }
    }


    public PatternMap<ParentStep> loadSubSteps(final File definitions) {

        final List<File> substepsFiles = FileUtils.getFiles(definitions,
                ".substeps");

        for (final File f : substepsFiles) {
            parseSubStepFile(f);
        }

        return this.parentMap;
    }


    // private void processLine(final String line, final File source,
    // final int lineNumber) {
    //
    // if (line != null && line.length() > 0) {
    // // does this line begin with any of annotation values that we're
    // // interested in ?
    //
    // // pick out the first word
    // final String trimmed = line.trim();
    // if (trimmed.length() > 0 && !trimmed.startsWith("#")) {
    // processTrimmedLine(trimmed, source, lineNumber);
    // }
    //
    // }
    // }

    private void processLine(final int lineNumberIdx) {

        final String line = this.currentFileContents.getLineAt(lineNumberIdx);

        if (this.log.isTraceEnabled()) {
            this.log.trace("substep line[" + line + "] @ " + lineNumberIdx
                    + ":" + this.currentFileContents.getFile().getName());
        }

        if (line != null && line.length() > 0) {
            // does this line begin with any of annotation values that we're
            // interested in ?

            // pick out the first word
            final String trimmed = line.trim();
            if (trimmed.length() > 0 && !trimmed.startsWith("#")) {
                processTrimmedLine(trimmed, lineNumberIdx);
            }

        }
    }


    private void processTrimmedLine(final String trimmed,
            final int lineNumberIdx) {

        // TODO convert <> into regex wildcards

        final int scolon = trimmed.indexOf(':');

        boolean lineProcessed = false;

        if (scolon > 0) {
            // is this a directive line
            final String word = trimmed.substring(0, scolon);
            final String remainder = trimmed.substring(scolon + 1);
            final Directive d = isDirective(word);
            if (d != null) {
                final String trimmedRemainder = remainder.trim();
                if (!Strings.isNullOrEmpty(trimmedRemainder)) {
                    processDirective(d, remainder, lineNumberIdx);
                    lineProcessed = true;
                }
            }
        }

        if (!lineProcessed) {
            if (this.currentParentStep != null) {

                final int sourceOffset = this.currentFileContents
                        .getSourceStartOffsetForLineIndex(lineNumberIdx);
                // no context at the mo
                this.currentParentStep.addStep(new Step(trimmed, true,
                        this.currentFileContents.getFile(), lineNumberIdx + 1,
                        sourceOffset));
            }
        }
    }


    // private void processTrimmedLine(final String trimmed, final File source,
    // final int lineNumber) {
    //
    // // TODO convert <> into regex wildcards
    //
    // final int scolon = trimmed.indexOf(':');
    //
    // boolean lineProcessed = false;
    //
    // if (scolon > 0) {
    // // is this a directive line
    // final String word = trimmed.substring(0, scolon);
    // final String remainder = trimmed.substring(scolon + 1);
    // final Directive d = isDirective(word);
    // if (d != null) {
    // final String trimmedRemainder = remainder.trim();
    // if (!Strings.isNullOrEmpty(trimmedRemainder)) {
    // processDirective(d, remainder, source, lineNumber);
    // lineProcessed = true;
    // }
    // }
    // }
    //
    // if (!lineProcessed) {
    // if (this.currentParentStep != null) {
    // // no context at the mo
    // this.currentParentStep.addStep(new Step(trimmed, true, source,
    // lineNumber));
    // }
    // }
    // }

    private void processDirective(final Directive d, final String remainder,
            final int lineNumberIdx) {
        this.currentDirective = d;

        switch (this.currentDirective) {

        case DEFINITION: {

            // build up a Step from the remainder

            final int sourceOffset = this.currentFileContents
                    .getSourceStartOffsetForLineIndex(lineNumberIdx);

            final Step parent = new Step(remainder, true,
                    this.currentFileContents.getFile(), lineNumberIdx + 1,
                    sourceOffset);

            if (this.currentParentStep != null) {
                final String newPattern = this.currentParentStep.getParent()
                        .getPattern();
                // check for existing values
                if (this.parentMap.containsPattern(newPattern)) {
                    final ParentStep otherValue = this.parentMap
                            .getValueForPattern(newPattern);

                    this.log.error("duplicate patterns detected: " + newPattern
                            + " in : " + otherValue.getSubStepFile() + " and "
                            + this.currentParentStep.getSubStepFile());

                }

                storeForPatternOrReportFailure(
                        this.currentFileContents.getFile(), newPattern,
                        this.currentParentStep);
            }

            this.currentParentStep = new ParentStep(parent);

            break;
        }
        default: // whatever
        }
    }


    /*
     * private void processDirective(final Directive d, final String remainder,
     * final File source, final int lineNumber) { this.currentDirective = d;
     * 
     * switch (this.currentDirective) {
     * 
     * case DEFINITION: {
     * 
     * // build up a Step from the remainder
     * 
     * final Step parent = new Step(remainder, true, source, lineNumber);
     * 
     * if (this.currentParentStep != null) { final String newPattern =
     * this.currentParentStep.getParent() .getPattern(); // check for existing
     * values if (this.parentMap.containsPattern(newPattern)) { final ParentStep
     * otherValue = this.parentMap .getValueForPattern(newPattern);
     * 
     * this.log.error("duplicate patterns detected: " + newPattern + " in : " +
     * otherValue.getSubStepFile() + " and " +
     * this.currentParentStep.getSubStepFile());
     * 
     * }
     * 
     * storeForPatternOrReportFailure(source, newPattern,
     * this.currentParentStep); }
     * 
     * this.currentParentStep = new ParentStep(parent);
     * 
     * break; } } }
     */

    private void storeForPatternOrReportFailure(final File source,
            final String newPattern, final ParentStep parentStep) {

        // TODO use this.currentFileContents.getFile() instead of passing around

        try {
            storeParentStepForPattern(newPattern, parentStep);
        } catch (final RuntimeException ex) {
            this.syntaxErrorReporter.reportSubstepsError(source, parentStep
                    .getParent().getLine(), parentStep.getParent()
                    .getSourceLineNumber(), ex.getMessage(), ex);
        }
    }


    private void storeParentStepForPattern(final String newPattern,
            final ParentStep parentStep) {
        try {
            this.parentMap.put(newPattern, parentStep);
        } catch (final DuplicatePatternException ex) {
            if (this.failOnDuplicateSubsteps) {
                throw ex;
            }
            this.log.warn("Encountered duplicate substep " + newPattern, ex);
        }
    }

    private static enum Directive {
        // @formatter:off
        DEFINITION("Define");

        // @formatter:on

        Directive(final String name) {
            this.name = name;
        }

        private final String name;
    }

    private Directive currentDirective = null;


    private Directive isDirective(final String word) {

        Directive rtn = null;

        for (final Directive d : Directive.values()) {

            if (word.equalsIgnoreCase(d.name)) {
                rtn = d;
                break;
            }
        }
        return rtn;
    }

}
