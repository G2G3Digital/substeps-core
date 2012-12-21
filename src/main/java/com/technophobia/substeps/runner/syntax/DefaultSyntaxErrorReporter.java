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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.technophobia.substeps.model.exception.StepImplementationException;
import com.technophobia.substeps.model.exception.SubstepsParsingException;

public class DefaultSyntaxErrorReporter implements SyntaxErrorReporter {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultSyntaxErrorReporter.class);


    public void reportFeatureError(final File file, final String line, final int lineNumber, final int offset,
            final String description) throws RuntimeException {
        LOG.error("Error on line " + lineNumber + " of feature file " + file.getAbsolutePath() + ": " + line
                + " - reason: " + description);
    }


    public void reportFeatureError(final File file, final String line, final int lineNumber, final int offset,
            final String description, final RuntimeException ex) throws RuntimeException {
        LOG.error("Error on line " + lineNumber + " of feature file " + file.getAbsolutePath() + ": " + line
                + " - reason: " + description);
        throw ex;
    }


    public void reportSubstepsError(final SubstepsParsingException ex) {
        LOG.error(ex.getMessage());
    }


    public void reportStepImplError(final StepImplementationException ex) {
        LOG.error(ex.getMessage());
    }

}
