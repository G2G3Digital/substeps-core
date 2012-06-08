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
import java.nio.charset.Charset;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.technophobia.substeps.model.ParentStep;
import com.technophobia.substeps.model.PatternMap;
import com.technophobia.substeps.model.Step;

/**
 * @author ian
 * 
 */
public class SubStepDefinitionParser {
    private final Logger log = LoggerFactory.getLogger(SubStepDefinitionParser.class);

    private ParentStep currentParentStep;
    private File currentFile;

    private final PatternMap<ParentStep> parentMap = new PatternMap<ParentStep>();

    private void parseSubStepFile(final File substepFile){
        currentFile = substepFile;
        try {
            final List<String> lines = Files.readLines(substepFile,
                    Charset.forName("UTF-8"));

            for (final String line : lines) {
                log.trace("substep line[" + substepFile.getName() + "]: " + line);
                processLine(line, substepFile);
            }

            if (currentParentStep != null) {
                // add the last scenario in
                parentMap.put(currentParentStep.getParent().getPattern(), currentParentStep);

                // we're moving on to another file, so set this to null.
                // TODO - pass this around rather than stash the state
                currentParentStep = null;
            }
        } catch (final FileNotFoundException e) {
            log.error(e.getMessage(), e);
        } catch (final IOException e) {

            log.error(e.getMessage(), e);
        }
    }

    public PatternMap<ParentStep> loadSubSteps(final File definitions) {
        
    	final List<File> substepsFiles = FileUtils.getFiles(definitions, ".substeps");
    	
    	for (final File f : substepsFiles){
    		parseSubStepFile(f);
    	}
    	
        return parentMap;
    }


    private void processLine(final String line, final File source) {

        if (line != null && line.length() > 0) {
            // does this line begin with any of annotation values that we're
            // interested in ?

            // pick out the first word
            final String trimmed = line.trim();
            if (trimmed.length() > 0 && !trimmed.startsWith("#")) {
                processTrimmedLine(trimmed, source);
            }

        }
    }


    private void processTrimmedLine(final String trimmed, final File source) {

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
                    processDirective(d, remainder, source);
                    lineProcessed = true;
                }
            }
        }

        if (!lineProcessed) {
            if (currentParentStep != null) {
                // no context at the mo
                currentParentStep.addStep(new Step(trimmed, true, source));
            }
        }
    }


    private void processDirective(final Directive d, final String remainder, final File source) {
        currentDirective = d;

        switch (currentDirective) {

        case DEFINITION: {

            // build up a Step from the remainder

            final Step parent = new Step(remainder, true, source);

            if (currentParentStep != null) {
                final String newPattern = currentParentStep.getParent().getPattern();
                // check for existing values
                if (parentMap.containsPattern(newPattern)) {
                    final ParentStep otherValue = parentMap.getValueForPattern(newPattern);

                    log.error("duplicate patterns detected: " + newPattern + " in : "
                            + otherValue.getSubStepFile() + " and "
                            + currentParentStep.getSubStepFile());

                }

                parentMap.put(newPattern, currentParentStep);
            }

            currentParentStep = new ParentStep(parent, currentFile.getName());

            break;
        }
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
