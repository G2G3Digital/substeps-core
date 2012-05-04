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
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.technophobia.substeps.model.parameter.Converter;
import com.technophobia.substeps.model.parameter.ConverterFactory;

public class ConverterFactoryTest {

	public static class DummyConverter implements Converter<String> {

		public boolean canConvert(final Class<?> cls) {
			return cls == String.class;
		}

		public String convert(final String value) {
			return "dummy";
		}
	}


	@Test
	public void testConvertInteger() {
		final Integer expected = Integer.valueOf(7);
		final String value = "7";

		final Integer actual = ConverterFactory.convert(value, Integer.class, null);
		assertEquals(expected, actual);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testConvertObject() {
		ConverterFactory.convert("test text", Object.class, null);
	}


	@Test
	public void testCustomStringConverterWithString() {
		final Converter<?> converter = ConverterFactory.getConverter(String.class, DummyConverter.class);
		assertTrue(converter instanceof DummyConverter);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testCustomStringConverterWithInteger() {
		ConverterFactory.getConverter(Integer.class, DummyConverter.class);
	}
}
