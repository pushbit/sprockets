/*
 * Copyright 2013-2015 pushbit <pushbit@gmail.com>
 * 
 * This file is part of Sprockets.
 * 
 * Sprockets is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * 
 * Sprockets is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with Sprockets. If
 * not, see <http://www.gnu.org/licenses/>.
 */

package net.sf.sprockets;

import static java.util.logging.Level.WARNING;

import java.io.File;
import java.net.URL;
import java.util.logging.Logger;

import net.sf.sprockets.util.logging.Loggers;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

/**
 * Allows the configuration of library settings. If you need to override the default settings, for
 * example to provide your <a href="https://console.developers.google.com/" target="_blank">Google
 * API key</a>, the recommended method is to download <a href=
 * "https://raw.githubusercontent.com/pushbit/sprockets/master/src/main/resources/net/sf/sprockets/sprockets.xml"
 * target="_blank">sprockets.xml</a> and place it in the root of your application classpath (e.g.
 * {@code src/main/resources/} in a Maven project). You can then update the values in this file and
 * they will automatically be loaded at run-time.
 * <p>
 * If you would like to override the default settings in another way, you can choose any of the
 * following options, listed from highest to lowest precedence.
 * </p>
 * <ol>
 * <li>Update the {@link Configuration} programmatically.
 * <ul>
 * <li>{@code Sprockets.getConfig().setProperty("google.api-key", "<your_api_key>");}</li>
 * </ul>
 * </li>
 * <li>Specify settings on the command line as system properties.
 * <ul>
 * <li>{@code java -Dgoogle.api-key=<your_api_key> ...}</li>
 * </ul>
 * </li>
 * <li>Specify the file system path to your {@code sprockets.xml} file on the command line.
 * <ul>
 * <li>{@code java -Dsprockets.config.file=/path/to/sprockets.xml ...}</li>
 * </ul>
 * </li>
 * <li>Specify the classpath location of your {@code sprockets.xml} file on the command line.
 * <ul>
 * <li>{@code java -Dsprockets.config.resource=com/example/sprockets.xml ...}</li>
 * </ul>
 * </li>
 * </ol>
 */
public class Sprockets {
	private static final Logger sLog = Loggers.get(Sprockets.class);
	private static final CompositeConfiguration sConfig = new CompositeConfiguration();
	static {
		sConfig.addConfiguration(new SystemConfiguration());
		/* on the file system, if user specified */
		String key = "sprockets.config.file";
		String config = System.getProperty(key);
		if (config != null) {
			File file = new File(config);
			if (file.isFile() && file.canRead()) {
				try {
					sConfig.addConfiguration(new XMLConfiguration(config));
				} catch (ConfigurationException e) {
					throw new RuntimeException("loading " + key + ": " + config, e);
				}
			} else {
				sLog.log(WARNING, "can''t read {0}: {1}", new String[] { key, config });
			}
		}
		/* in the class path; if not user specified then check for default */
		key = "sprockets.config.resource";
		config = System.getProperty(key);
		String defConfig = "sprockets.xml";
		URL url = Sprockets.class.getClassLoader().getResource(config != null ? config : defConfig);
		if (url != null) {
			try {
				sConfig.addConfiguration(new XMLConfiguration(url));
			} catch (ConfigurationException e) {
				throw new RuntimeException("loading " + key + ": " + url, e);
			}
		} else if (config != null) {
			sLog.log(WARNING, "can''t read {0}: {1}", new String[] { key, config });
		}
		/* in this package */
		url = Sprockets.class.getResource(defConfig);
		if (url != null) {
			try {
				sConfig.addConfiguration(new XMLConfiguration(url));
			} catch (ConfigurationException e) {
				throw new RuntimeException("loading sprockets default config: " + defConfig, e);
			}
		}
	}

	private Sprockets() {
	}

	/**
	 * Library settings that can be updated programmatically. See the class description for how
	 * these values are loaded and how you can override them.
	 */
	public static Configuration getConfig() {
		return sConfig;
	}
}
