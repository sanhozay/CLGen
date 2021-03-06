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

import org.flightgear.clgen.ast.Visitable;
import org.flightgear.clgen.ast.conditions.Condition;

/**
 * Abstract binding, parent of various binding types
 *
 * @author Richard Senior
 */
public abstract class AbstractBinding implements Visitable {

    Condition condition;

    /**
     * Gets the condition applicable to the binding.
     *
     * @return the binding condition (may be null)
     */
    public Condition getCondition() {
        return condition;
    }

    /**
     * Sets the condition applicable to the binding.
     *
     * @param condition the condition
     */
    public void setCondition(final Condition condition) {
        this.condition = condition;
    }

}
