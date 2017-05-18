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
 * Unary condition.
 *
 * @author Richard Senior
 */
public class UnaryCondition extends AbstractCondition {

    private final Operator operator;
    private AbstractCondition operand;

    /**
     * Constructs a unary condition with no operator.
     */
    public UnaryCondition() {
        operator = null;
    }

    /**
     * Constructs a unary condition with an operator.
     *
     * @param operator the operator
     */
    public UnaryCondition(final Operator operator) {
        this.operator = operator;
    }

    /**
     * @return the operator, may be null
     */
    public Operator getOperator() {
        return operator;
    }

    /**
     * @return the operand
     */
    public AbstractCondition getOperand() {
        return operand;
    }

    @Override
    public Type getType() {
        return Type.BOOL;
    }

    /**
     * Resolves the type of symbols that form part of this condition.
     *
     * @throws TypeException if there is a type conflict
     */
    public void resolveTypes() throws TypeException {
        if (operand instanceof Terminal && ((Terminal)operand).getValue() instanceof Symbol) {
            Symbol symbol = (Symbol)((Terminal)operand).getValue();
            symbol.setType(getType());
        }
    }

    @Override
    public void addChild(final AbstractCondition child) {
        operand = child;
    }

    @Override
    public void accept(final Visitor visitor) {
        visitor.enter(this);
        operand.accept(visitor);
        visitor.exit(this);
    }

    @Override
    public String toString() {
        return String.format("UnaryCondition: %s %s", operator, operand);
    }

}
