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

import org.flightgear.clgen.ast.Visitor;
import org.flightgear.clgen.symbol.Type;

/**
 * Terminal.
 *
 * @author Richard Senior
 */
public class Terminal extends AbstractCondition {

    private final Object value;

    /**
     * Constructs a terminal with a value.
     * <p>
     * The type of the value determines the type of the terminal.
     *
     * @param value the value of the terminal
     */
    public Terminal(final Object value) {
        this.value = value;
    }

    /**
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    @Override
    public Type getType() {
        return Type.typeOf(value);
    }

    @Override
    public void accept(final Visitor visitor) {
        visitor.enter(this);
        visitor.exit(this);
    }

    @Override
    public String toString() {
        return String.format("Terminal: %s",  value);
    }

    @Override
    public void addChild(final AbstractCondition child) {}

}
