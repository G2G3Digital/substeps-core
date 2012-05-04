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

import com.technophobia.substeps.model.parameter.LongConverter;

public class LongConverterTest {

	private LongConverter converter;


	@Before
	public void setUp() {
		converter = new LongConverter();
	}


	@Test
	public void testCanConvertLong() {
		final boolean canConvert = converter.canConvert(Long.class);
		assertTrue(canConvert);
	}


	@Test
	public void testCanConvertPrimitiveLong() {
		final boolean canConvert = converter.canConvert(long.class);
		assertTrue(canConvert);
	}


	@Test
	public void testCanConvertString() {
		final boolean canConvert = converter.canConvert(String.class);
		assertFalse(canConvert);
	}


	@Test
	public void testConvertValidLong() {
		final Long expected = Long.valueOf(7L);
		final String value = "7";

		final Long actual = converter.convert(value);
		assertEquals(expected, actual);
	}


	@Test(expected = NumberFormatException.class)
	public void testConvertInvalidLong() {
		final String value = "x";

		converter.convert(value);
	}


	@Test(expected = NumberFormatException.class)
	public void testConvertNullLong() {
		converter.convert(null);
	}
}
