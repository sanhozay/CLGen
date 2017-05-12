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

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an item that can appear in a checklist.
 *
 * @author Richard Senior
 */
public class Item {

    private final String name;
    private final Map<String, State> states = new HashMap<>();
    private Marker marker;

    public Item(final String name) {
        this.name = name;
    }

    /**
     * Gets the name of the item.
     * <p>
     * The item name appears in the Flightgear dialog as the name of a check
     * and is also used as the key for the lookup table of items built in
     * the initial pass over the parse tree.
     *
     * @return the item name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the states associated with an item.
     *
     * @return a map of states, keyed by name
     */
    public Map<String, State> getStates() {
        return states;
    }

    /**
     * Adds a state to the item.
     *
     * @param state the state to add
     */
    public void addState(final State state) {
        states.put(state.getName(), state);
    }

    /**
     * Gets the marker associated with an item.
     *
     * @return the marker, may be null
     */
    public Marker getMarker() {
        return marker;
    }

    /**
     * Sets the marker.
     *
     * @param marker the marker
     */
    public void setMarker(final Marker marker) {
        this.marker = marker;
    }

}
