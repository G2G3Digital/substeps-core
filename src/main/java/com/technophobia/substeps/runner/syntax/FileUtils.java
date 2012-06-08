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
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;


/**
 * @author ian
 *
 */
public class FileUtils
{
	public static List<File> getFiles(final File fFile, final String extension) {

        final List<File> files = new ArrayList<File>();
		if (fFile.exists()) {
			if (fFile.isDirectory()) {
				final File[] children = fFile.listFiles(new FileFilter() {
					public boolean accept(final File dir) {
						return dir.isDirectory()
								|| (dir.isFile() && dir.getName().endsWith(
										extension));
					}

				});
				if (children != null && children.length > 0) {
					for (final File f : children) {
						files.addAll(getFiles(f, extension));
					}
				}
			} else {
				files.add(fFile);
			}
		}
		return files;
    }
}
