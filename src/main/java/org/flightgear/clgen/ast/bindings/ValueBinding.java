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
package org.flightgear.clgen.ast.bindings;

import org.flightgear.clgen.ast.Visitor;
import org.flightgear.clgen.symbol.Symbol;

/**
 * Value binding (property-assign).
 *
 * @author Richard Senior
 */
public class ValueBinding extends AbstractBinding {

    protected final Symbol symbol;
    protected Object value = null;

    public ValueBinding(final Symbol symbol, final Object value) {
        this.symbol = symbol;
        this.value = value;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public void accept(final Visitor visitor) {
        visitor.enter(this);
        if (condition != null)
            condition.accept(visitor);
        visitor.exit(this);
    }

    @Override
    public String toString() {
        return String.format("Binding: %s %s",  symbol, value);
    }

}
