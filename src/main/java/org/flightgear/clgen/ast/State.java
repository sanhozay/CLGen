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
package org.flightgear.clgen.ast;

import java.util.ArrayList;
import java.util.List;

import org.flightgear.clgen.ast.bindings.AbstractBinding;
import org.flightgear.clgen.ast.conditions.Condition;

/**
 * Represents a state of a checklist item, as used in a check.
 *
 * @author Richard Senior
 */
public class State implements Visitable {

    private final String name;
    private final List<AbstractBinding> bindings = new ArrayList<>();
    private Condition condition;

    /**
     * Constructs a state with the state name.
     * <p>
     * The state name is used to look up states in the items lookup table.
     *
     * @param name the name of the state, e.g. "OFF".
     */
    public State(final String name) {
        this.name = name;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the condition associated with the state.
     * <p>
     * This will be a top-level wrapper condition. The content of the
     * condition will be added as child conditions.
     *
     * @param condition the top-level condtition
     */
    public void setCondition(final Condition condition) {
        this.condition = condition;
    }

    /**
     * @return the condition for this state
     */
    public Condition getCondition() {
        return condition;
    }

    /**
     * Adds a binding to this state.
     *
     * @param binding the binding
     */
    public void addBinding(final AbstractBinding binding) {
        bindings.add(binding);
    }

    /**
     * Gets the list of bindings associated with this state.
     *
     * @return the list of bindings
     */
    public List<AbstractBinding> getBindings() {
        return bindings;
    }

    /**
     * Accepts a visitor and sends it to the condition and bindings associated
     * with this state.
     *
     * @param visitor the visitor
     */
    @Override
    public void accept(final Visitor visitor) {
        visitor.enter(this);
        if (condition != null)
            condition.accept(visitor);
        bindings.forEach(binding -> binding.accept(visitor));
        visitor.exit(this);
    }

    @Override
    public String toString() {
        return String.format("State: %s",  name);
    }

}
