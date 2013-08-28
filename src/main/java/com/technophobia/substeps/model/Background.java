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
package com.technophobia.substeps.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Background {

    private final int lineNumber;
    private final String description;
    private final List<Step> steps;
    private final String rawText;

    public Background(final int lineNumber, final String rawText, final File sourceFile) {
        super();
        this.lineNumber = lineNumber;
        this.rawText = rawText;
        this.description = descriptionFor(rawText);
        this.steps = stepsFrom(lineNumber, rawText, sourceFile);
    }

    private List<Step> stepsFrom(final int backgroundLineNumber, final String backgroundText, final File sourceFile) {
        final List<Step> backgroundSteps = new ArrayList<Step>();
        final String[] bLines = backgroundText.split("\n");
        for (int i = 1; i < bLines.length; i++) {

            backgroundSteps.add(new Step(bLines[i], sourceFile, backgroundLineNumber + i, -1));
        }
        return Collections.unmodifiableList(backgroundSteps);
    }

    public String getDescription() {
        return this.description;
    }

    public int getLineNumber() {
        return this.lineNumber;
    }

    public String getRawText() {
        return this.rawText;
    }

    public List<Step> getSteps() {
        return this.steps;
    }

    private String descriptionFor(final String text) {
        final int startIndex = text.indexOf(":") + 1;

        final int endIndex = text.indexOf("\n");
        // using System.getProperty("line.separator") here causes issues on
        // Windows with files that don't end in /r/n..

        if (endIndex == -1 || startIndex == -1) {
            return "";
        }
        return text.substring(startIndex, endIndex).trim();
    }
}