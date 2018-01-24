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

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;

import org.flightgear.clgen.CLGenBaseListener;

/**
 * Abstract listener.
 *
 * @author Richard Senior
 */
public abstract class AbstractListener extends CLGenBaseListener {

    private final List<SemanticErrorListener> errorListeners = new ArrayList<>();
    private int errors = 0, warnings = 0;

    /**
     * Gets the number of errors found by this listener
     *
     * @return the number of errors
     */
    public int getNumberOfErrors() {
        return errors;
    }

    /**
     * Gets the number of warnings found by this listener
     *
     * @return the number of warnings
     */
    public int getNumberOfWarnings() {
        return warnings;
    }

    /**
     * Adds an error listener to the error listeners for this listener
     *
     * @param errorListener the error listener to add
     */
    public void addErrorListener(final SemanticErrorListener errorListener) {
        errorListeners.add(errorListener);
    }

    /**
     * Removes all error listeners from this listener.
     */
    public void removeErrorListeners() {
        errorListeners.clear();
    }

    /**
     * Notify an error to the registered error listeners.
     *
     * @param token the offending token
     * @param format a format string for the error message
     * @param args an array of arguments to the format string
     */
    void error(final Token token, final String format, final Object... args) {
        String message = String.format(format, args);
        errorListeners.forEach(l -> l.semanticError(this, token, message));
        ++errors;
    }

    /**
     * Notify a warning to the registered error listeners.
     *
     * @param token the offending token
     * @param format a format string for the warning message
     * @param args an array of arguments to the format string
     */
    void warning(final Token token, final String format, final Object... args) {
        String message = String.format(format, args);
        errorListeners.forEach(l -> l.semanticWarning(this, token, message));
        ++warnings;
    }

    /**
     * Unquotes a double-quoted string.
     *
     * @param q the quoted string
     * @return the string with double-quotes removed
     * @throws IllegalArgumentException if the string is not quoted
     */
     protected String unquote(final String q) {
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

}
