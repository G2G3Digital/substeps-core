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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.technophobia.substeps.model.parameter.Converter;
import com.technophobia.substeps.model.parameter.ConverterFactory;

/**
 * @author ian
 * 
 */
public final class Util {
    private static final Logger log = LoggerFactory.getLogger(Util.class);


    private Util() {
        // no op
    }


    // TODO - these two methods are both used - used to be one, but now it's two
    // - could they be combined ??
    public static String[] getArgs(final String patternString, final String sourceString) {

        log.debug("Util getArgs String[] with pattern: " + patternString + " and sourceStr: "
                + sourceString);

        String[] rtn = null;

        ArrayList<String> argsList = null;

        final Pattern pattern = Pattern.compile(patternString);
        final Matcher matcher = pattern.matcher(sourceString);

        final int groupCount = matcher.groupCount();

        if (matcher.find()) {

            for (int i = 1; i <= groupCount; i++) {
                final String arg = matcher.group(i);

                if (arg != null) {
                    if (argsList == null) {
                        argsList = new ArrayList<String>();
                    }
                    argsList.add(arg);
                }
            }
        }

        if (argsList != null) {
            rtn = argsList.toArray(new String[argsList.size()]);

            if (log.isDebugEnabled()) {

                final StringBuilder buf = new StringBuilder();
                buf.append("returning args: ");

                for (final String s : argsList) {

                    buf.append("[").append(s).append("] ");
                }

                log.debug(buf.toString());
            }

        }

        return rtn;
    }


    public static List<Object> getArgs(final String patternString, final String sourceString,
            final Class<?>[] parameterTypes, final Class<? extends Converter<?>>[] converterTypes) {

        log.debug("Util getArgs List<Object> with pattern: " + patternString + " and sourceStr: "
                + sourceString);

        List<Object> argsList = null;

        final Pattern pattern = Pattern.compile(patternString);
        final Matcher matcher = pattern.matcher(sourceString);

        final int groupCount = matcher.groupCount();

        int argIdx = 0;

        if (matcher.find()) {

            for (int i = 1; i <= groupCount; i++) {
                final String arg = matcher.group(i);

                if (arg != null) {
                    if (argsList == null) {
                        argsList = new ArrayList<Object>();
                    }

                    argsList.add(getObjectArg(arg, parameterTypes[argIdx], converterTypes[argIdx]));

                }
                argIdx++;
            }
        }

        return argsList;

    }


    private static Object getObjectArg(final String stringArgument, final Class<?> desiredType,
            final Class<? extends Converter<?>> converter) {
        return ConverterFactory.convert(stringArgument, desiredType, converter);
    }

}
