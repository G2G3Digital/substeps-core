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
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.technophobia.substeps.model.parameter.DoubleConverter;

public class DoubleConverterTest {


	private DoubleConverter converter;


	@Before
	public void setUp() {
		converter = new DoubleConverter();
	}


	@Test
	public void testCanConvertDouble() {
		final boolean canConvert = converter.canConvert(Double.class);
		assertTrue(canConvert);
	}


	@Test
	public void testCanConvertPrimitiveDouble() {
		final boolean canConvert = converter.canConvert(double.class);
		assertTrue(canConvert);
	}


	@Test
	public void testCanConvertString() {
		final boolean canConvert = converter.canConvert(String.class);
		assertFalse(canConvert);
	}


	@Test
	public void testConvertValidDouble() {
		final Double expected = Double.valueOf(7.90d);
		final String value = "7.90";

		final Double actual = converter.convert(value);
		assertEquals(expected, actual);
	}


	@Test(expected = NumberFormatException.class)
	public void testConvertInvalidDouble() {
		final String value = "x";

		converter.convert(value);
	}


	@Test(expected = NumberFormatException.class)
	public void testConvertNullDouble() {
		converter.convert(null);
	}
}
