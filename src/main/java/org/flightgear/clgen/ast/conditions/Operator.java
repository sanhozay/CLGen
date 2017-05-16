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
package org.flightgear.clgen.ast.conditions;

/**
 * Operator.
 *
 * @author Richard Senior
 */
public enum Operator {

    AND, OR, NOT, EQ, NE, GT, LT, GE, LE;

    public static Operator fromString(final String s) {
        switch(s) {
        case "&&": return AND;
        case "||": return OR;
        case "!":  return NOT;
        case "==": return EQ;
        case "!=": return NE;
        case ">" : return GT;
        case "<" : return LT;
        case ">=": return GE;
        case "<=": return LE;
        }
        return null;
    }

}
