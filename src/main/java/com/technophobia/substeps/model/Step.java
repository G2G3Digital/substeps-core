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
	
	/**
	 * @return the line
	 */
	public String getLine() {
		return line;
	}

	public Step(final String line) {
		this(line, false, null);
	}
	
	public Step(final String line, final File source) {
		this(line, false, source);
	}

	public Step(final String theLine, final boolean isSubStep) {
		this(theLine, isSubStep, null);
	}
	
	public Step(final String theLine, final boolean isSubStep, final File source) {
		if (theLine == null || theLine.length() == 0) {
			throw new IllegalArgumentException("null or empty args");
		}
		
		this.source = source;
		
		// pick out the first word
		line = theLine.trim();

		// TODO - change to use a reg expression and a capture?

		final int last = line.indexOf(' ');
		if (last > 0) {

			keyword = line.substring(0, last);

			// TODO no need to to do if no parameter to the annotation..?
			if (isSubStep) {
				setParamAndParamNames();
			}

		} else if (line.length() > 0) {
			// we've got just an annotation with no parameter
			keyword = line;
		} 
	}

	// only used in tests
	public Step(final String keyword, final String line, final boolean isSubStep) {
		this.keyword = keyword;
		this.line = line;

		if (isSubStep) {
			setParamAndParamNames();
		}
	}

	private void setParamAndParamNames() {
		// do we have any params in the string that we need to swap for regex
		// expressions?

		// look for params
		final Pattern p = Pattern.compile("(<([^>]*)>)");
		final Matcher matcher = p.matcher(line);

		int findIdx = 0;
		while (matcher.find(findIdx)) {
			if (paramNames == null) {
				paramNames = new ArrayList<String>();
			}

			paramNames.add(matcher.group(2));
			findIdx = matcher.end(2);
		}

		// replace the params with a reg ex, a quoted and non quoted variant

		pattern = line.replaceAll("(<[^>]*>)", "\"?([^\"]*)\"?");
	}

	/**
	 * @return
	 */
	public String toDebugString() {
		if (keyword == null) {
			log.debug("annot of step is null: " + this.getClass().getSimpleName());
		}

		return " [" + line + "]";
	}

	@Override
	public String toString() {
		return toDebugString();
	}

	/**
	 * @return the annotationName
	 */
	public String getKeyword() {
		return keyword;
	}

	public String getPattern() {
		return pattern;
	}

	public List<String> getParamNames() {
		return paramNames;
	}

	/**
	 * @param data
	 */
	public void addTableData(final String[] data) {

		if (tableHeadings == null) {
			tableHeadings = data;
		} else {

			if (inlineTable == null) {
				inlineTable = new ArrayList<Map<String, String>>();
			}
			final Map<String, String> map = new HashMap<String, String>();
			inlineTable.add(map);

			for (int i = 1; i < tableHeadings.length; i++) {

				map.put(tableHeadings[i].trim(), data[i].trim());
			}
		}

	}

	public String getParameterLine() {
		return parameterLine != null ? parameterLine : line;

	}

	public void setParameterLine(final String parameterLine) {
		this.parameterLine = parameterLine;
	}


	/**
	 * @return the inlineTable
	 */
	public List<Map<String, String>> getInlineTable() {
		return inlineTable;
	}

	/**
	 * @return the inlineTable
	 */
	public List<Map<String, String>> getSubstitutedInlineTable() {
		return substitutedInlineTable != null ? substitutedInlineTable : inlineTable;
	}
	
	/**
	 * @param replacedInlineTable
	 */
	public void setSubstitutedInlineTable(final List<Map<String, String>> substitutedInlineTable)
	{
		this.substitutedInlineTable = substitutedInlineTable;
	}

	/**
	 * @return the source
	 */
	public File getSource()
	{
		return source;
	}
	
	public Step cloneWithAlternativeLine(final String alt){
		final Step step = new Step(alt, this.pattern !=null);
		
		step.inlineTable = this.inlineTable;
				
		step.tableHeadings = this.tableHeadings;
		
		step.substitutedInlineTable = this.substitutedInlineTable;

		step.source= this.source ;
		
		return step;
	}
}
