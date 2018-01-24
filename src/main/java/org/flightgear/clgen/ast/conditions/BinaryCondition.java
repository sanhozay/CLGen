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

import org.flightgear.clgen.ast.Visitor;
import org.flightgear.clgen.symbol.Symbol;
import org.flightgear.clgen.symbol.Type;
import org.flightgear.clgen.symbol.TypeException;

/**
 * Binary condition.
 *
 * @author Richard Senior
 */
public class BinaryCondition extends AbstractCondition {

    private final Operator operator;
    private AbstractCondition lhs = null;
    private AbstractCondition rhs = null;

    /**
     * Constructs a binary condition with its operator.
     *
     * @param operator the operator
     */
    public BinaryCondition(final Operator operator) {
        this.operator = operator;
    }

    /**
     * Gets the operator associated with this condition.
     *
     * @return the operator, should not be null
     */
    public Operator getOperator() {
        return operator;
    }

    @Override
    public void addChild(final AbstractCondition child) {
        if (lhs == null)
            lhs = child;
        else {
            assert rhs == null;
            rhs = child;
        }
    }

    @Override
    Type getType() {
        if (lhs.getType() == Type.NULL && rhs.getType() == Type.NULL)
            return Type.NULL;
        if (rhs.getType() != Type.NULL)
            return rhs.getType();
        return lhs.getType();
    }

    /**
     * Resolves the type of symbols that form part of this condition.
     *
     * @throws TypeException if there is a type conflict
     */
    public void resolveTypes() throws TypeException {
        if (lhs instanceof Terminal && ((Terminal)lhs).getValue() instanceof Symbol) {
            Symbol symbol = (Symbol)((Terminal)lhs).getValue();
            symbol.setType(getType());
        }
        if (rhs instanceof Terminal && ((Terminal)rhs).getValue() instanceof Symbol) {
            Symbol symbol = (Symbol)((Terminal)rhs).getValue();
            symbol.setType(getType());
        }
    }

    @Override
    public void accept(final Visitor visitor) {
        visitor.enter(this);
        lhs.accept(visitor);
        rhs.accept(visitor);
        visitor.exit(this);
    }

    @Override
    public String toString() {
        return String.format("BinaryCondition: %s %s %s", lhs, operator, rhs);
    }

}
