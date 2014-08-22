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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Step {

    private final Logger log = LoggerFactory.getLogger(Step.class);

    // eg Given, When, Then
    private String keyword;

    private List<String> paramNames = null;

    // this only gets set for Steps which are defined as sub steps
    private String pattern;

    // the whole original line, eg Given blah blah
    private final String line;

    // the remainder of the line, eg blah blah
    // private final String param;

    // a string that can be manipulated for variable substituion
    private String parameterLine;

    private List<Map<String, String>> inlineTable = null;

    private String[] tableHeadings = null;

    private List<Map<String, String>> substitutedInlineTable = null;

    private File source;

    private final int sourceLineNumber;

    // the offest in the source file of the first character of this line
    private int sourceStartOffset = -1;

    /**
     * @return the sourceStartOffset
     */
    public int getSourceStartOffset() {
        return this.sourceStartOffset;
    }

    /**
     * @param sourceStartOffset
     *            the sourceStartOffset to set
     */
    public void setSourceStartOffset(final int sourceStartOffset) {
        this.sourceStartOffset = sourceStartOffset;
    }

    /**
     * @return the line
     */
    public String getLine() {
        return this.line;
    }

    // tests
    public Step(final String line) {
        this(line, false, null, -1, -1);
    }

    public Step(final String line, final File source, final int lineNumber, final int sourceStartOffset) {

        this(line, false, source, lineNumber, sourceStartOffset);
    }

    // called by tests
    public Step(final String theLine, final boolean isSubStep) {
        this(theLine, isSubStep, null, -1, -1);
    }

    // preferred ctor
    public Step(final String theLine, final boolean isSubStep, final File source, final int lineNumber,
            final int sourceStartOffset) {
        if (theLine == null || theLine.length() == 0) {
            throw new IllegalArgumentException("null or empty args");
        }

        this.source = source;
        this.sourceLineNumber = lineNumber;
        this.sourceStartOffset = sourceStartOffset;

        // pick out the first word
        this.line = theLine.trim();

        // TODO - change to use a reg expression and a capture?

        final int last = this.line.indexOf(' ');
        if (last > 0) {

            this.keyword = this.line.substring(0, last);

            // TODO no need to to do if no parameter to the annotation..?
            if (isSubStep) {
                setParamAndParamNames();
            }

        } else if (this.line.length() > 0) {
            // we've got just an annotation with no parameter
            this.keyword = this.line;
            this.pattern = this.keyword;
        }
    }

    // only used in tests
    public Step(final String keyword, final String line, final boolean isSubStep) {
        this.keyword = keyword;
        this.line = line;

        if (isSubStep) {
            setParamAndParamNames();
        }
        this.sourceLineNumber = -1;
    }

    private void setParamAndParamNames() {
        // do we have any params in the string that we need to swap for regex
        // expressions?

        // look for params
        final Pattern p = Pattern.compile("(<([^>]*)>)");
        final Matcher matcher = p.matcher(this.line);

        int findIdx = 0;
        while (matcher.find(findIdx)) {
            if (this.paramNames == null) {
                this.paramNames = new ArrayList<String>();
            }

            this.paramNames.add(matcher.group(2));
            findIdx = matcher.end(2);
        }

        // replace the params with a reg ex, a quoted and non quoted variant

        this.pattern = this.line.replaceAll("(<[^>]*>)", "\"?([^\"]*)\"?");
    }

    /**
     * @return
     */
    public String toDebugString() {
        if (this.keyword == null) {
            this.log.debug("annot of step is null: " + this.getClass().getSimpleName());
        }

        return " [" + this.line + "]";
    }

    @Override
    public String toString() {
        return toDebugString();
    }

    /**
     * @return the annotationName
     */
    public String getKeyword() {
        return this.keyword;
    }

    /**
     * returns the regex pattern string including the capture groups
     * @return
     */
    public String getPattern() {
        return this.pattern;
    }

    public List<String> getParamNames() {
        return this.paramNames;
    }

    /**
     * @param data
     */
    public void addTableData(final String[] data) {

        if (this.tableHeadings == null) {
            this.tableHeadings = data;
        } else {

            if (this.inlineTable == null) {
                this.inlineTable = new ArrayList<Map<String, String>>();
            }
            final Map<String, String> map = new HashMap<String, String>();
            this.inlineTable.add(map);

            for (int i = 1; i < this.tableHeadings.length; i++) {

                map.put(this.tableHeadings[i].trim(), data[i].trim());
            }
        }

    }

    /**
     * returns the text as it appears in the substep files, including any <angle> brackets denoting parameters
     * @return
     */
    public String getParameterLine() {
        return this.parameterLine != null ? this.parameterLine : this.line;

    }

    public void setParameterLine(final String parameterLine) {
        this.parameterLine = parameterLine;
    }

    /**
     * @return the inlineTable
     */
    public List<Map<String, String>> getInlineTable() {
        return this.inlineTable;
    }

    /**
     * @return the inlineTable
     */
    public List<Map<String, String>> getSubstitutedInlineTable() {
        return this.substitutedInlineTable != null ? this.substitutedInlineTable : this.inlineTable;
    }

    /**
     * @param replacedInlineTable
     */
    public void setSubstitutedInlineTable(final List<Map<String, String>> substitutedInlineTable) {
        this.substitutedInlineTable = substitutedInlineTable;
    }

    /**
     * @return the source
     */
    public File getSource() {
        return this.source;
    }

    public Step cloneWithAlternativeLine(final String alt) {
        final Step step = new Step(alt, this.pattern != null, this.source, this.sourceLineNumber,
                this.sourceStartOffset);

        step.inlineTable = this.inlineTable;

        step.tableHeadings = this.tableHeadings;

        step.substitutedInlineTable = this.substitutedInlineTable;

        return step;
    }

    /**
     * @return the sourceLineNumber
     */
    public int getSourceLineNumber() {
        return this.sourceLineNumber;
    }

}
