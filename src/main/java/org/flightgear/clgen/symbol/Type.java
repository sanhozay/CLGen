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
package org.flightgear.clgen.symbol;

/**
 * Symbol type.
 *
 * @author Richard Senior
 */
public enum Type {

    NULL, INT, DOUBLE, BOOL, STRING;

    public boolean isNumeric() {
        return this == INT || this == DOUBLE;
    }

    /**
     * Gets a type based on an object type.
     *
     * @param o the object
     * @return the inferred type from the object
     */
    public static Type typeOf(final Object o) {
        if (o instanceof Symbol) {
            Symbol symbol = (Symbol)o;
            return symbol.getType();
        }
        if (o instanceof Integer) return Type.INT;
        if (o instanceof Double) return Type.DOUBLE;
        if (o instanceof Boolean) return Type.BOOL;
        if (o instanceof String) return Type.STRING;
        return Type.NULL;
    }
}
