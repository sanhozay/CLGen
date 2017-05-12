/*
 * Copyright (C) 2017 Richard Senior
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.flightgear.clgen;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Properties singleton.
 * <p>
 * Properties are loaded from a properties file on the classpath.
 *
 * @author Richard Senior
 */
public class CLGenProperties {

    private static final String PROPERTIES_FILE = "clgen.properties";

    private static final CLGenProperties instance = new CLGenProperties();

    private Properties properties;

    /*
     * Private constructor
     */
    private CLGenProperties() {
        try (InputStream in = ClassLoader.getSystemResourceAsStream(PROPERTIES_FILE)) {
            properties = new Properties();
            properties.load(in);
        } catch (IOException e) {}
    }

    /**
     * Gets the singleton instance.
     *
     * @return a singleton instance of CLGenProperties
     */
    public static CLGenProperties getInstance() {
        return instance;
    }

    /**
     * Gets the version property.
     *
     * @return the current program version as a string, e.g. 1.0.0
     */
    public String getVersion() {
        return properties.getProperty("version");
    }
}
