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


/**
 * Factory class to create parameter converters and to convert values from String
 * to whatever the parameter actually is.
 * <br/>
 * It allows for custom converters to be defined using the @StepParameter annotation,
 * but also maintains a collection of default converters that will cover the majority
 * of cases.
 * <br/>
 *
 * Possible modifications:
 * TODO Load converters based on naming convention.
 * TODO Register converters based on an annotation on the Converter
 * TODO Create more default converters (BigDecimal, Float, URL, File, etc.)
 *
 * @author irawson
 */
public final class ConverterFactory {

	// TODO Need a better way to inject/configure this list (annotations? config file?)
	private static final Converter<?>[] CONVERTER_LIST = {
		new StringConverter(),
		new IntegerConverter(),
		new LongConverter(),
		new DoubleConverter(),
		new BooleanConverter(),
	};


	private ConverterFactory() {
		// hide constructor
	}


	public static <T> T convert(final String value, final Class<T> cls, final Class<? extends Converter<?>> custom) {
		return (T)getConverter(cls, custom).convert(value);
	}


	public static Converter<?> getConverter(final Class<?> cls, final Class<? extends Converter<?>> custom) {

		// Return a custom converter if one has been requested
		if (custom != null) {
			return createCustomConverter(cls, custom);
		}

		// If a custom converter hasn't been requested try the list of default converters.
		for (final Converter<?> converter : CONVERTER_LIST) {
			if (converter.canConvert(cls)) {
				return converter;
			}
		}

		throw new IllegalArgumentException("Cannot find converter for " + cls.getName());
	}


	private static Converter<?> createCustomConverter(final Class<?> cls, final Class<? extends Converter<?>> custom) {

		try {
			final Converter<?> converter = custom.getConstructor().newInstance();
			if (converter.canConvert(cls)) {
				return converter;
			}

			throw new IllegalArgumentException(custom.getName() + " can not convert " + cls.getName() + " parameter");

		} catch (final Exception ex) {
			throw new IllegalArgumentException("Cannot create instance of " + custom.getName() + " class", ex);
		}
	}
}
