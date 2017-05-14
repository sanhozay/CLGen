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
 * Property binding (property-assign).
 *
 * @author Richard Senior
 */
public class PropertyBinding extends AbstractBinding {

    private final Symbol lval;
    private final Symbol rval;

    public PropertyBinding(final Symbol lval, final Symbol rval) {
        this.lval = lval;
        this.rval = rval;
    }

    public Symbol getLval() {
        return lval;
    }

    public Symbol getRval() {
        return rval;
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
        return String.format("Binding: %s %s",  lval, rval);
    }

}
