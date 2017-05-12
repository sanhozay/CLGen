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

import java.util.Map;

import org.antlr.v4.misc.OrderedHashMap;

import org.flightgear.clgen.ast.Visitor;

/**
 * Command binding (for fgcommand).
 *
 * @author Richard Senior
 */
public class CommandBinding extends AbstractBinding {

    private final String command;
    private final Map<String, Object> params = new OrderedHashMap<>();

    public CommandBinding(final String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public void addParam(final String name, final Object value) {
        params.put(name, value);
    }

    public Map<String, Object> getParams() {
        return params;
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
        return String.format("Binding: %s",  command);
    }

}
