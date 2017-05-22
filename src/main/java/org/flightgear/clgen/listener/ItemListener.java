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
package org.flightgear.clgen.listener;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.Token;

import org.flightgear.clgen.CLGenParser;
import org.flightgear.clgen.CLGenParser.*;
import org.flightgear.clgen.ast.Coordinate;
import org.flightgear.clgen.ast.Item;
import org.flightgear.clgen.ast.Marker;
import org.flightgear.clgen.ast.State;
import org.flightgear.clgen.ast.bindings.CommandBinding;
import org.flightgear.clgen.ast.bindings.PropertyBinding;
import org.flightgear.clgen.ast.bindings.ValueBinding;
import org.flightgear.clgen.ast.conditions.AbstractCondition;
import org.flightgear.clgen.ast.conditions.BinaryCondition;
import org.flightgear.clgen.ast.conditions.Condition;
import org.flightgear.clgen.ast.conditions.Operator;
import org.flightgear.clgen.ast.conditions.Terminal;
import org.flightgear.clgen.ast.conditions.UnaryCondition;
import org.flightgear.clgen.symbol.DuplicateSymbolException;
import org.flightgear.clgen.symbol.Symbol;
import org.flightgear.clgen.symbol.SymbolTable;
import org.flightgear.clgen.symbol.Type;
import org.flightgear.clgen.symbol.TypeException;

/**
 * Listener that builds a lookup table of items.
 * <p>
 * This listener is used to walk the parse tree and build the lookup table of
 * items that is used in AST generation. The product is a hash table of item
 * names and items with embedded states. The states form the body of the output
 * from the checklist generation.
 *
 * @author Richard Senior
 */
public class ItemListener extends AbstractListener {

    private final Map<String, Item> items = new HashMap<>();

    private final SymbolTable symbolTable = new SymbolTable();

    private Item item;
    private State state;
    private final Deque<AbstractCondition> conditions = new ArrayDeque<>();
    private CommandBinding commandBinding;
    private Condition bindingCondition = null;

    @Override
    public void enterItem(final ItemContext ctx) {
        item = new Item(unquote(ctx.getChild(2).getText()));
    }

    @Override
    public void exitItem(final ItemContext ctx) {
        if (items.containsKey(item.getName())) {
            Token token = (Token)ctx.getChild(0).getPayload();
            error(token, "Duplicate definition of item '%s'", item.getName());
        } else
            items.put(item.getName(), item);
        // Item must be null outside item block to detect global aliases
        item = null;
    }

    @Override
    public void enterDeclaration(final DeclarationContext ctx) {
        String key = ctx.getChild(0).getText();
        String value = unquote(ctx.getChild(2).getText());
        try {
            String scope = item == null ? SymbolTable.GLOBAL : item.getName();
            symbolTable.add(scope, new Symbol(key, value));
        } catch (DuplicateSymbolException e) {
            Token token = (Token)ctx.getChild(0).getPayload();
            error(token, "Alias '%s' is already defined in item '%s'",
                key, item.getName()
            );
        }
    }

    @Override
    public void enterState(final StateContext ctx) {
        state = new State(unquote(ctx.getChild(2).getText()));
    }

    @Override
    public void exitState(final StateContext ctx) {
        if (item.getStates().containsKey(state.getName())) {
            Token token = (Token)ctx.getChild(2).getPayload();
            error(token, "Duplicate definition of state '%s' in item '%s'",
                state.getName(), item.getName()
            );
            return;
        }
        item.addState(state);
    }

    @Override
    public void enterConditionRoot(final ConditionRootContext ctx) {
        Condition condition = new Condition();
        state.setCondition(condition);
        conditions.push(condition);
    }

    @Override
    public void exitConditionRoot(final ConditionRootContext ctx) {
        conditions.pop();
    }

    @Override
    public void enterNotCondition(final NotConditionContext ctx) {
        UnaryCondition e = new UnaryCondition(Operator.NOT);
        conditions.peek().addChild(e);
        conditions.push(e);
    }

    @Override
    public void exitNotCondition(final NotConditionContext ctx) {
        UnaryCondition condition = (UnaryCondition)conditions.pop();
        try {
            condition.resolveTypes();
        } catch (TypeException e) {
            Token token = (Token)ctx.getChild(0).getPayload();
            warning(token, e.getMessage());
        }
    }

    @Override
    public void enterAndCondition(final AndConditionContext ctx) {
        BinaryCondition e = new BinaryCondition(Operator.AND);
        conditions.peek().addChild(e);
        conditions.push(e);
    }

    @Override
    public void exitAndCondition(final AndConditionContext ctx) {
        conditions.pop();
    }

    @Override
    public void enterOrCondition(final OrConditionContext ctx) {
        BinaryCondition e = new BinaryCondition(Operator.OR);
        conditions.peek().addChild(e);
        conditions.push(e);
    }

    @Override
    public void exitOrCondition(final OrConditionContext ctx) {
        conditions.pop();
    }

    @Override
    public void enterBinaryCondition(final BinaryConditionContext ctx) {
        Operator op = Operator.fromString(ctx.getChild(1).getText());
        BinaryCondition e = new BinaryCondition(op);
        conditions.peek().addChild(e);
        conditions.push(e);
    }

    @Override
    public void exitBinaryCondition(final BinaryConditionContext ctx) {
        BinaryCondition condition = (BinaryCondition)conditions.pop();
        try {
            condition.resolveTypes();
        } catch (TypeException e) {
            Token token = (Token)ctx.getChild(1).getPayload();
            warning(token, e.getMessage());
        }
    }

    @Override
    public void enterUnaryCondition(final UnaryConditionContext ctx) {
        UnaryCondition e = new UnaryCondition();
        conditions.peek().addChild(e);
        conditions.push(e);
    }

    @Override
    public void exitUnaryCondition(final UnaryConditionContext ctx) {
        UnaryCondition condition = (UnaryCondition)conditions.pop();
        try {
            condition.resolveTypes();
        } catch (TypeException e) {
            Token token = (Token)ctx.getChild(0).getChild(0).getPayload();
            warning(token, e.getMessage());
        }
    }

    @Override
    public void enterDoubleTerminal(final DoubleTerminalContext ctx) {
        Double d = Double.parseDouble(ctx.getText());
        conditions.peek().addChild(new Terminal(d));
    }

    @Override
    public void enterIntegerTerminal(final IntegerTerminalContext ctx) {
        Integer i = Integer.parseInt(ctx.getText());
        conditions.peek().addChild(new Terminal(i));
    }

    @Override
    public void enterBooleanTerminal(final BooleanTerminalContext ctx) {
        Boolean b = Boolean.parseBoolean(ctx.getText());
        conditions.peek().addChild(new Terminal(b));
    }

    @Override
    public void enterStringTerminal(final StringTerminalContext ctx) {
        String s = unquote(ctx.getText());
        conditions.peek().addChild(new Terminal(s));
    }

    @Override
    public void enterIdTerminal(final IdTerminalContext ctx) {
        Symbol symbol = lookup((Token)ctx.getChild(0).getPayload());
        conditions.peek().addChild(new Terminal(symbol));
    }

    @Override
    public void enterConditionalBinding(final ConditionalBindingContext ctx) {
        bindingCondition = new Condition();
        conditions.push(bindingCondition);
    }

    @Override
    public void exitConditionalBinding(final ConditionalBindingContext ctx) {
        conditions.pop();
        bindingCondition = null;
    }

    @Override
    public void enterAssignInt(final AssignIntContext ctx) {
        Symbol symbol = lookup((Token)ctx.getChild(0).getPayload());
        Integer value = Integer.parseInt(ctx.getChild(2).getText());
        ValueBinding binding = new ValueBinding(symbol, value);
        binding.setCondition(bindingCondition);
        state.addBinding(binding);
        try {
            if (symbol != null)
                symbol.setType(Type.INT);
        } catch (TypeException e) {
            Token token = (Token)ctx.getChild(1).getPayload();
            warning(token, e.getMessage());
        }
    }

    @Override
    public void enterAssignDouble(final AssignDoubleContext ctx) {
        Symbol symbol = lookup((Token)ctx.getChild(0).getPayload());
        Double value = Double.parseDouble(ctx.getChild(2).getText());
        ValueBinding binding = new ValueBinding(symbol, value);
        binding.setCondition(bindingCondition);
        state.addBinding(binding);
        try {
            if (symbol != null)
                symbol.setType(Type.DOUBLE);
        } catch (TypeException e) {
            Token token = (Token)ctx.getChild(1).getPayload();
            warning(token, e.getMessage());
        }
    }

    @Override
    public void enterAssignBool(final CLGenParser.AssignBoolContext ctx) {
        Symbol symbol = lookup((Token)ctx.getChild(0).getPayload());
        Boolean value = Boolean.parseBoolean(ctx.getChild(2).getText());
        ValueBinding binding = new ValueBinding(symbol, value);
        binding.setCondition(bindingCondition);
        state.addBinding(binding);
        try {
            if (symbol != null)
                symbol.setType(Type.BOOL);
        } catch (TypeException e) {
            Token token = (Token)ctx.getChild(1).getPayload();
            warning(token, e.getMessage());
        }
    }

    @Override
    public void enterAssignString(final AssignStringContext ctx) {
        Symbol symbol = lookup((Token)ctx.getChild(0).getPayload());
        String value = unquote(ctx.getChild(2).getText());
        ValueBinding binding = new ValueBinding(symbol, value);
        binding.setCondition(bindingCondition);
        state.addBinding(binding);
        try {
            if (symbol != null)
                symbol.setType(Type.STRING);
        } catch (TypeException e) {
            Token token = (Token)ctx.getChild(1).getPayload();
            warning(token, e.getMessage());
        }
    }

    @Override
    public void enterAssignId(final AssignIdContext ctx) {
        Symbol lval = lookup((Token)ctx.getChild(0).getPayload());
        Symbol rval = lookup((Token)ctx.getChild(2).getPayload());
        PropertyBinding binding = new PropertyBinding(lval, rval);
        binding.setCondition(bindingCondition);
        state.addBinding(binding);
        if (rval.getType() != Type.NULL)
            try {
                if (lval != null && rval != null)
                    lval.setType(rval.getType());
            } catch (TypeException e) {
                Token token = (Token)ctx.getChild(1).getPayload();
                warning(token, e.getMessage());
            }
    }

    @Override
    public void enterCommand(final CommandContext ctx) {
        String command = unquote(ctx.getChild(2).getText());
        commandBinding = new CommandBinding(command);
        commandBinding.setCondition(bindingCondition);
        state.addBinding(commandBinding);
    }

    @Override
    public void exitCommand(final CommandContext ctx) {
        Object param = commandBinding.getParams().get("property");
        Object value = commandBinding.getParams().get("value");
        if (param != null && value != null) {
            Symbol symbol = (Symbol)param;
            try {
                symbol.setType(Type.typeOf(value));
            } catch (TypeException e) {
                Token token = (Token)ctx.getChild(0).getPayload();
                warning(token, e.getMessage());
            }
        }
    }

    @Override
    public void enterIntParam(final IntParamContext ctx) {
        String name = ctx.getChild(0).getText();
        Integer value = Integer.parseInt(ctx.getChild(2).getText());
        commandBinding.addParam(name, value);
    }

    @Override
    public void enterDoubleParam(final DoubleParamContext ctx) {
        String name = ctx.getChild(0).getText();
        Double value = Double.parseDouble(ctx.getChild(2).getText());
        commandBinding.addParam(name, value);
    }

    @Override
    public void enterBoolParam(final BoolParamContext ctx) {
        String name = ctx.getChild(0).getText();
        Boolean value = Boolean.parseBoolean(ctx.getChild(2).getText());
        commandBinding.addParam(name, value);
    }

    @Override
    public void enterStringParam(final StringParamContext ctx) {
        String name = ctx.getChild(0).getText();
        String value = unquote(ctx.getChild(2).getText());
        commandBinding.addParam(name, value);
    }

    @Override
    public void enterIdParam(final IdParamContext ctx) {
        String n = ctx.getChild(0).getText();
        Symbol symbol = lookup((Token)ctx.getChild(2).getPayload());
        commandBinding.addParam(n, symbol);
    }

    @Override
    public void enterMarker(final MarkerContext ctx) {
        double x = Double.parseDouble(ctx.getChild(2).getText());
        double y = Double.parseDouble(ctx.getChild(4).getText());
        double z = Double.parseDouble(ctx.getChild(6).getText());
        double s = Double.parseDouble(ctx.getChild(8).getText());
        Coordinate c = new Coordinate(x, y, z);
        item.setMarker(new Marker(c, s));
    }

    // Accessors

    /**
     * Gets the lookup table of items built by this listener.
     *
     * @return the item lookup table
     */
    public Map<String, Item> getItems() {
        return items;
    }

    // Other methods

    private Symbol lookup(final Token token) {
        Symbol symbol = symbolTable.lookup(item.getName(), token.getText());
        if (symbol == null) {
            error(token, "Alias '%s' is not defined in item '%s'",
                token.getText(), item.getName()
            );
            return null;
        }
        return symbol;
    }

}
