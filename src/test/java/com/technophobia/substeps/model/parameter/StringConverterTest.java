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
package com.technophobia.substeps.model.parameter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.technophobia.substeps.model.parameter.StringConverter;

public class StringConverterTest {

	private StringConverter converter;


	@Before
	public void setUp() {
		converter = new StringConverter();
	}


	@Test
	public void testCanConvertString() {
		final boolean canConvert = converter.canConvert(String.class);
		assertTrue(canConvert);
	}


	@Test
	public void testCanConvertInteger() {
		final boolean canConvert = converter.canConvert(Integer.class);
		assertFalse(canConvert);
	}


	@Test
	public void testConvertValidString() {
		final String value = "test string";

		final String actual = converter.convert(value);
		assertEquals(value, actual);
	}


	@Test
	public void testConvertNullString() {
		final String actual = converter.convert(null);
		assertNull(actual);
	}
}
