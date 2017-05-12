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
package org.flightgear.clgen.listener;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;

/**
 * Listener support functions.
 *
 * @author Richard Senior
 */
class ListenerSupport {

    /**
     * Unquotes a double-quoted string.
     *
     * @param q the quoted string
     * @return the string with double-quotes removed
     */
     static String unquote(final String q) {
        if (q.charAt(0) != '"' || q.charAt(q.length() - 1) != '"') {
            String message = String.format("String '%s' is not quoted", q);
            throw new IllegalArgumentException(message);
        }
        /* This is not a general purpose removal of escaped characters. The
         * grammar only supports escaping of backslash and double quote.
         */
        return q.substring(1, q.length() - 1)
            .replace("\\\"", "\"")
            .replace("\\\\", "\\");
    }

     /**
      * Creates a string that shows the context of a syntax or semantic error.
      *
      * @param line the line number where the error occurred
      * @param charPositionInLine the position of the error within the line
      * @param tokenStream the token stream that produced the error
      * @return a string showing the context of the error
      */
     static String errorContext(final int line, final int charPositionInLine,
         final CommonTokenStream tokenStream
     ) {
         StringBuilder sb = new StringBuilder();
         CharStream stream = tokenStream.getTokenSource().getInputStream();
         String[] lines = stream.toString().split("\n");
         if (line - 1 < lines.length) {
             sb.append(lines[line - 1] + "\n");
             for (int i = 0; i < charPositionInLine; ++i)
                 sb.append(' ');
             sb.append("^");
         }
         return sb.toString();
     }

}
