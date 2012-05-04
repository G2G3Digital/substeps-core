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

import com.technophobia.substeps.model.parameter.BooleanConverter;

public class BooleanConverterTest {

	private BooleanConverter converter;


	@Before
	public void setUp() {
		converter = new BooleanConverter();
	}


	@Test
	public void testCanConvertBoolean() {
		final boolean canConvert = converter.canConvert(Boolean.class);
		assertTrue(canConvert);
	}


	@Test
	public void testCanConvertPrimitiveBoolean() {
		final boolean canConvert = converter.canConvert(boolean.class);
		assertTrue(canConvert);
	}


	@Test
	public void testCanConvertString() {
		final boolean canConvert = converter.canConvert(String.class);
		assertFalse(canConvert);
	}


	@Test
	public void testConvertValidBoolean() {
		final Boolean expected = Boolean.TRUE;
		final String value = "true";

		final Boolean actual = converter.convert(value);
		assertEquals(expected, actual);
	}


	@Test
	public void testConvertInvalidBoolean() {
		final String value = "x";

		final Boolean actual = converter.convert(value);
		assertEquals(Boolean.FALSE, actual);
	}


	@Test
	public void testConvertNullBoolean() {
		final Boolean actual = converter.convert(null);
		assertEquals(Boolean.FALSE, actual);
	}
}
