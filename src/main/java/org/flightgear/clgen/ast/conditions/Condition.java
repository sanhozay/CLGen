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
import org.flightgear.clgen.ast.Visitor;

/**
 * Condition root class.
 * <p>
 * This might look like a slightly bizarre class, but it represents the
 * root element of a condition block in a checklist. Visitors that visit
 * instances of this class can create a wrapper around other conditional
 * elements.
 */
public class Condition extends AbstractCondition implements Visitable {

    private AbstractCondition condition;

    @Override
    public void addChild(final AbstractCondition child) {
        condition = child;
    }

    /**
     * Gets the condition child associated with this condition wrapper.
     *
     * @return the condition
     */
    public AbstractCondition getCondition() {
        return condition;
    }

    @Override
    public void accept(final Visitor visitor) {
        visitor.enter(this);
        condition.accept(visitor);
        visitor.exit(this);
    }

    @Override
    public String toString() {
        return String.format("Condition: %s",  condition);
    }

}
