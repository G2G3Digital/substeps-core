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

import java.io.Serializable;
import java.util.Comparator;

import com.technophobia.substeps.model.FeatureFile;


public class FeatureFileComparator implements Comparator<FeatureFile>, Serializable {

	private static final long serialVersionUID = -8032832302837878628L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	
	public int compare(final FeatureFile ff1, final FeatureFile ff2) {
		if (ff1 != null && ff2 != null) {
			return ff1.getName().compareTo(ff2.getName());
		} else {
			return -1;
		}
	}
}