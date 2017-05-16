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
package org.flightgear.clgen.backend;

import org.flightgear.clgen.ast.AbstractSyntaxTree;
import org.flightgear.clgen.ast.Check;
import org.flightgear.clgen.ast.Checklist;
import org.flightgear.clgen.ast.Item;
import org.flightgear.clgen.ast.Marker;
import org.flightgear.clgen.ast.Page;
import org.flightgear.clgen.ast.State;
import org.flightgear.clgen.ast.Visitor;
import org.flightgear.clgen.ast.bindings.CommandBinding;
import org.flightgear.clgen.ast.bindings.PropertyBinding;
import org.flightgear.clgen.ast.bindings.ValueBinding;
import org.flightgear.clgen.ast.conditions.BinaryCondition;
import org.flightgear.clgen.ast.conditions.Condition;
import org.flightgear.clgen.ast.conditions.Terminal;
import org.flightgear.clgen.ast.conditions.UnaryCondition;

/**
 * Abstract visitor.
 * <p>
 * @see Visitor
 *
 * @author Richard Senior
 */
public class AbstractVisitor implements Visitor {

    @Override
    public void enter(final AbstractSyntaxTree ast) {}

    @Override
    public void enter(final Checklist checklist) {}

    @Override
    public void enter(final Page page) {}

    @Override
    public void enter(final Check check) {}

    @Override
    public void enter(final Item item) {}

    @Override
    public void enter(final State state) {}

    @Override
    public void enter(final ValueBinding binding) {}

    @Override
    public void enter(final CommandBinding binding) {}

    @Override
    public void enter(final PropertyBinding binding) {}

    @Override
    public void enter(final Condition condition) {}

    @Override
    public void enter(final BinaryCondition condition) {}

    @Override
    public void enter(final UnaryCondition condition) {}

    @Override
    public void enter(final Terminal terminal) {}

    @Override
    public void enter(final Marker marker) {}

}
