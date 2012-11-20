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
package com.technophobia.substeps.model.exception;

/**
 * @author ian
 * 
 */
public class SubstepsConfigurationException extends SubstepsException {

    private static final long serialVersionUID = -7081029538420278487L;

    public SubstepsConfigurationException(final Throwable cause) {
        super(cause);
    }

    /*
     * TODO: The following probably shouldn't be required - but we use this
     * exception a bit hap-hazzardly - review after error handling overhaul.
     */

    public SubstepsConfigurationException() {
        super();
    }

    public SubstepsConfigurationException(final String message, final Throwable cause, final boolean enableSuppression,
            final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public SubstepsConfigurationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public SubstepsConfigurationException(final String message) {
        super(message);
    }

}
