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
package org.flightgear.clgen.backend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Post processing of XML files.
 *
 * @author Richard Senior
 */
public class XmlPostProcessor {

    private String xml;
    private final List<String> breakPatterns = new ArrayList<>();

    /**
     * Sets the XML content to be processed by this post-processor.
     *
     * @param xml the XML content
     */
    public void setXml(final String xml) {
        this.xml = xml;
    }

    /**
     * Gets the processed XML content from this post-processor
     *
     * @return the processed XML
     */
    public String getXml() {
        xml = xml.replaceAll("--><", "-->\n<");
        breakPatterns.forEach(pattern -> xml = xml.replaceAll(pattern, pattern + "\n"));
        return xml;
    }

    /**
     * Adds patterns after which line breaks will be generated.
     *
     * @param patterns a variable argument list of patterns
     */
    public void addBreakPatterns(final String ... patterns) {
        Collections.addAll(breakPatterns, patterns);
    }

}
