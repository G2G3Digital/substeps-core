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

import java.net.URL;

import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.tree.OverrideCombiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * @author ian
 *
 */
public enum Configuration {
	

	INSTANCE;

	private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

	private final CombinedConfiguration combinedConfig = new CombinedConfiguration(new OverrideCombiner());

	private Configuration(){
		
		initialise();
		
		
	}
	
    private void initialise() {
    	
        final String resourceBundleName = resourceBundleName();
		
		final URL customPropsUrl = Configuration.class.getResource(resourceBundleName);

		if (customPropsUrl != null){

			try {
				final PropertiesConfiguration  customProps = new PropertiesConfiguration(customPropsUrl);
				combinedConfig.addConfiguration(customProps, "customProps");

			} catch (final ConfigurationException e) {
				logger.error("error loading custom properties", e);

			}
		}
    }

    /**
     * Implementors of substep libraries should call this with default properties for their library
     * @param url to a properties file containing default values
     */
    public void addDefaultProperties(final URL url, final String name){
    	
    	if (url != null){
			try {

			final PropertiesConfiguration  defaultProps = new PropertiesConfiguration(url);
			combinedConfig.addConfiguration(defaultProps, name);
			} catch (final ConfigurationException e) {
				logger.error("error loading default properties", e);
				throw new SubStepConfigurationException(e);
			}
    	}
    }

    private static String resourceBundleName() {
        return "/" + System.getProperty("environment", "localhost") + ".properties";
    }
    
    public String getString(final String key) {
        return combinedConfig.getString(key);
    }
    
    public int getInt(final String key) {
        return combinedConfig.getInt(key);
    }
    
    public long getLong(final String key) {
        return combinedConfig.getLong(key);
    }
    
    public boolean getBoolean(final String key) {
        return combinedConfig.getBoolean(key);
    }
}
