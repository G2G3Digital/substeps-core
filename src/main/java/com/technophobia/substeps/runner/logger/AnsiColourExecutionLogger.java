/*
 *  Copyright Technophobia Ltd 2012
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

package com.technophobia.substeps.runner.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.technophobia.substeps.runner.ExecutionLogger;

public class AnsiColourExecutionLogger extends ExecutionLogger {

    private static final Logger log = LoggerFactory.getLogger(AnsiColourExecutionLogger.class);

    @Override
    public void printFailed(final String msg) {

        print(format(msg, TextFormat.BOLD, TextColour.RED));
    }

    @Override
    public void printStarted(final String msg) {

        print(format(msg, TextFormat.DARK, TextColour.GREEN));
    }

    private void print(final String formatted) {

        log.info(formatted);
    }

    @Override
    public void printPassed(final String msg) {
        print(format(msg, TextFormat.BOLD, TextColour.GREEN));
    }

    @Override
    public void printSkipped(final String msg) {
        print(format(msg, TextFormat.BOLD, TextColour.YELLOW));
    }

    private static String format(final String s, final TextFormat attribute, final TextColour colour) {

        return PREFIX + attribute + SEPARATOR + colour + POSTFIX + s + PREFIX + "39;0;" + POSTFIX;
    }

    public static final String PREFIX = "\033[";

    public static final String SEPARATOR = ";";

    public static final String POSTFIX = "m";

    public enum TextColour {

        BLACK("30"), RED("31"), GREEN("32"), YELLOW("33"), BLUE("34"), MAGENTA("35"), CYAN("36"), WHITE("37"), NONE("");

        private final String code;

        TextColour(final String code) {
            this.code = code;
        }

        public String getCode() {
            return this.code;
        }

        @Override
        public String toString() {
            return getCode();
        }
    }

    public enum BackgroundColour {

        BLACK("40"), RED("41"), GREEN("42"), YELLOW("43"), BLUE("44"), MAGENTA("45"), CYAN("46"), WHITE("47"), NONE("");

        private final String code;

        BackgroundColour(final String code) {
            this.code = code;
        }

        public String getCode() {
            return this.code;
        }

        @Override
        public String toString() {
            return getCode();
        }
    }

    public enum TextFormat {

        CLEAR("0"), BOLD("1"), LIGHT("1"), DARK("2"), UNDERLINE("4"), REVERSE("7"), HIDDEN("8"), NONE("");

        private final String code;

        TextFormat(final String code) {
            this.code = code;
        }

        public String getCode() {
            return this.code;
        }

        @Override
        public String toString() {
            return getCode();
        }
    }
}
