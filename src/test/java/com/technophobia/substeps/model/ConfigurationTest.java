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

package com.technophobia.substeps.model;

import static org.hamcrest.CoreMatchers.is;

import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author ian
 *
 */
public class ConfigurationTest {

	@Test
	public void testConfig(){
		
		// tests around Config
		// set the custom props, then override with defaults
		System.setProperty("environment", "custom");
		
		final URL defaultPropsUrl = ConfigurationTest.class.getResource("/default.properties");
		Configuration.INSTANCE.addDefaultProperties(defaultPropsUrl, "default");
		
		// overridden
		Assert.assertThat(Configuration.INSTANCE.getString("overridden.key"), is("overridden"));
		
		// default
		Assert.assertThat(Configuration.INSTANCE.getString("default.key"), is("default-key"));
		
		// custom
		Assert.assertThat(Configuration.INSTANCE.getString("custom.key"), is("custom-key"));
		
		Assert.assertNull(Configuration.INSTANCE.getString("non-existant"));
	}
}
