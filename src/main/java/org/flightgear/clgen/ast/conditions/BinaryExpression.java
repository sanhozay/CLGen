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

/**
 * Binary expression.
 *
 * @author Richard Senior
 */
public class BinaryExpression extends AbstractCondition {

    private final Operator operator;
    private AbstractCondition lhs = null;
    private AbstractCondition rhs = null;

    public BinaryExpression(final Operator operator) {
        this.operator = operator;
    }

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
    public void accept(final Visitor visitor) {
        visitor.enter(this);
        lhs.accept(visitor);
        rhs.accept(visitor);
        visitor.exit(this);
    }

    @Override
    public String toString() {
        return String.format("BinaryExpression: %s %s %s", lhs, operator, rhs);
    }

}
