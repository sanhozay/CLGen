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
package org.flightgear.clgen.reverse;

import java.nio.file.Path;

/**
 * Abstract XML parser.
 *
 * @author Richard Senior
 */
public abstract class AbstractXmlParser {

    /**
     * Parses an XML file with the given path.
     *
     * @param path the path
     */
     abstract protected void parse(final Path path);

     /**
      * Quotes a string, escaping characters as necessary.
      *
      * @param s the string to quote
      * @return the quoted string
      */
     protected String quote(final String s) {
         String escaped = s;
         if (s != null)
            escaped = escaped
                 .replace("\\", "\\\\")
                 .replace("\"", "\\\"");
         return String.format("\"%s\"", escaped);
     }

}
