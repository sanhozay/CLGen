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

    protected final List<SemanticErrorListener> errorListeners = new ArrayList<>();
    private int errors = 0, warnings = 0;

    public int getNumberOfErrors() {
        return errors;
    }

    public int getNumberOfWarnings() {
        return warnings;
    }

    public void addErrorListener(final SemanticErrorListener el) {
        errorListeners.add(el);
    }

    public void removeErrorListeners() {
        errorListeners.clear();
    }

    protected void error(final Token token, final String format, final Object ... args) {
        String message = String.format(format, args);
        errorListeners.forEach(l -> l.semanticError(this, token, message));
        ++errors;
    }

    protected void warning(final Token token, final String format, final Object ... args) {
        String message = String.format(format, args);
        errorListeners.forEach(l -> l.semanticWarning(this, token, message));
        ++warnings;
    }

    /**
     * Unquotes a double-quoted string.
     *
     * @param q the quoted string
     * @return the string with double-quotes removed
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
