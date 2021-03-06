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

import org.flightgear.clgen.ast.Visitable;
import org.flightgear.clgen.symbol.Type;

/**
 * Abstract condition.
 *
 * @author Richard Senior
 */
public abstract class AbstractCondition implements Visitable {

    /**
     * Adds a child condition to this condition.
     *
     * @param child the child condition
     */
    public abstract void addChild(AbstractCondition child);

    /**
     * Gets the type of the condition.
     * <p>
     * For terminals, the condition type is based on the value. For
     * conditions, the type is pulled up from any terminals with
     * known types before being pushed down to symbols (aliases).
     *
     * @return the type of the condition
     */
    Type getType() {
        return Type.NULL;
    }

}
