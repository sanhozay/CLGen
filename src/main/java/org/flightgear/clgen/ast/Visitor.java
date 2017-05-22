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

import org.flightgear.clgen.ast.bindings.CommandBinding;
import org.flightgear.clgen.ast.bindings.PropertyBinding;
import org.flightgear.clgen.ast.bindings.ValueBinding;
import org.flightgear.clgen.ast.conditions.BinaryCondition;
import org.flightgear.clgen.ast.conditions.Condition;
import org.flightgear.clgen.ast.conditions.Terminal;
import org.flightgear.clgen.ast.conditions.UnaryCondition;
import org.flightgear.clgen.backend.AbstractVisitor;

/**
 * Visitor interface.
 * <p>
 * @see AbstractVisitor
 *
 * @author Richard Senior
 */
@SuppressWarnings("javadoc")
public interface Visitor {

    void enter(final AbstractSyntaxTree ast);
    void enter(final Checklist checklist);
    void enter(final Page page);
    void enter(final Check check);
    void enter(final Item state);
    void enter(final State state);
    void enter(final ValueBinding binding);
    void enter(final CommandBinding binding);
    void enter(final PropertyBinding binding);
    void enter(final Condition condition);
    void enter(final BinaryCondition condition);
    void enter(final UnaryCondition condition);
    void enter(final Terminal terminal);
    void enter(final Marker marker);

    default void exit(final AbstractSyntaxTree ast) {}
    default void exit(final Checklist checklist) {}
    default void exit(final Page page) {}
    default void exit(final Check check) {}
    default void exit(final Item state) {}
    default void exit(final State state) {}
    default void exit(final ValueBinding binding) {}
    default void exit(final CommandBinding binding) {}
    default void exit(final PropertyBinding binding) {}
    default void exit(final Condition condition) {}
    default void exit(final BinaryCondition condition) {}
    default void exit(final UnaryCondition condition) {}
    default void exit(final Terminal terminal) {}
    default void exit(final Marker marker) {}

}
