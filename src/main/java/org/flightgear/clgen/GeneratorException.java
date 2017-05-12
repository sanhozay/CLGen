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

/**
 * Wrapper exception used by generator classes.
 * <p>
 * Unrecoverable exceptions thrown in listeners and visitors are wrapped in
 * this exception to allow them to propagate up to the main class where they
 * are caught and reported.
 *
 * @author Richard Senior
 */
@SuppressWarnings("serial")
public class GeneratorException extends RuntimeException {

    /**
     * Construct a generator exception with a message.
     *
     * @param message the message
     */
    public GeneratorException(final String message) {
        super(message);
    }

    /**
     * Construct a generator exception with a cause.
     *
     * @param cause the underlying cause
     */
    public GeneratorException(final Throwable cause) {
        super(cause);
    }

    /**
     * Construct a generator exception with a message and cause.
     *
     * @param message the message
     * @param cause the underlying cause
     */
    public GeneratorException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
