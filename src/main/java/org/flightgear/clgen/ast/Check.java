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

/**
 * Represents a check within a checklist.
 *
 * @author Richard Senior
 */
public class Check implements Visitable {

    private final Item item;
    private final State state;

    private final List<String> additionalValues = new ArrayList<>();

    /**
     * Constructs a check with an item and state.
     *
     * @param item the item
     * @param state the state
     */
    public Check(final Item item, final State state) {
        this.item = item;
        this.state = state;
    }

    /**
     * Checks with an item but no value are often used by Flightgear checklists
     * as subtitles in the checklist dialog.
     *
     * @return true if this item is being used as a dummy subtitle
     */
    public boolean isSubtitle() {
        return state == null ||
            state.getName() == null ||
            state.getName().trim().length() == 0;
    }

    /**
     * Checks with an empty item and value are sometimes used in Flightgear checklists
     * as spacer lines in the checklist dialog.
     *
     * @return true if this item is being used as a spacer
     */
    public boolean isSpacer() {
        return item == null ||
            item.getName() == null |
            item.getName().trim().length() == 0;
    }

    /**
     * Gets the item associated with this check.
     * <p>
     * The item provides the title of the check in the Flightgear dialog.
     *
     * @return the item
     */
    public Item getItem() {
        return item;
    }

    /**
     * Gets the state associated with this check.
     * <p>
     * The state forms the basis of most of the output associated with a check;
     * the conditions, bindings and marker.
     *
     * @return the state
     */
    public State getState() {
        return state;
    }

    /**
     * Adds an additional value to this check.
     * <p>
     * Additional values are used to provide additional detail about the
     * check and appear in the Flightgear dialog on separate lines. In XML
     * terms, they appear as additional value tags.
     *
     * @param value the value to add
     */
    public void addAdditionalValue(final String value) {
        additionalValues.add(value);
    }

    /**
     * Gets the list of additional values associated with this check.
     *
     * @return a list of additional values
     */
    public List<String> getAdditionalValues() {
        return additionalValues;
    }

    /**
     * Accepts a visitor and sends it to the state associated with this check
     * and the marker, if not null.
     *
     * @param visitor the visitor
     */
    @Override
    public void accept(final Visitor visitor) {
        visitor.enter(this);
        item.accept(visitor);
        state.accept(visitor);
        visitor.exit(this);
    }

    @Override
    public String toString() {
        return String.format("Check: %s %s",  item.getName(), state.getName());
    }

}
