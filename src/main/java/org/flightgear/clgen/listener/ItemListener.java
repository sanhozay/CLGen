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

import static org.flightgear.clgen.listener.ListenerSupport.unquote;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;

import org.flightgear.clgen.CLGenBaseListener;
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
import org.flightgear.clgen.ast.conditions.TerminalType;
import org.flightgear.clgen.ast.conditions.UnaryCondition;

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
public class ItemListener extends CLGenBaseListener {

    private final Map<String, Item> items = new HashMap<>();
    private final Map<String, String> symbols = new HashMap<>();
    private final List<SemanticErrorListener> errorListeners = new ArrayList<>();
    private int errors = 0;

    private Item item;
    private State state;
    private CommandBinding commandBinding;
    private final Deque<AbstractCondition> conditions = new ArrayDeque<>();
    private Condition bindingCondition = null;

    @Override
    public void enterItem(final ItemContext ctx) {
        item = new Item(unquote(ctx.getChild(2).getText()));
    }

    @Override
    public void exitItem(final ItemContext ctx) {
        if (items.containsKey(item.getName())) {
            Token token = (Token)ctx.getChild(0).getPayload();
            String message = String.format("Duplicate definition of item '%s'", item.getName());
            errorListeners.forEach(l -> l.semanticError(this, token, message));
            ++errors;
        } else
            items.put(item.getName(), item);
        symbols.clear();
    }

    @Override
    public void enterDeclaration(final DeclarationContext ctx) {
        String key = ctx.getChild(0).getText();
        String value = unquote(ctx.getChild(2).getText());
        if (symbols.get(key) != null) {
            Token token = (Token)ctx.getChild(0).getPayload();
            String message = String.format("Alias '%s' is already defined in item '%s'",
                key, item.getName()
            );
            errorListeners.forEach(l -> l.semanticError(this, token, message));
            ++errors;
            return;
        }
        symbols.put(key, value);
    }

    @Override
    public void enterState(final StateContext ctx) {
        state = new State(unquote(ctx.getChild(2).getText()));
    }

    @Override
    public void exitState(final StateContext ctx) {
        if (item.getStates().containsKey(state.getName())) {
            Token token = (Token)ctx.getChild(2).getPayload();
            String message = String.format(
                "Duplicate definition of state '%s' in item '%s'",
                state.getName(), item.getName()
            );
            errorListeners.forEach(l -> l.semanticError(this, token, message));
            ++errors;
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
        conditions.pop();
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
        conditions.pop();
    }

    @Override
    public void enterDoubleTerminal(final DoubleTerminalContext ctx) {
        Double d = Double.parseDouble(ctx.getText());
        conditions.peek().addChild(new Terminal(TerminalType.DOUBLE, d));
    }

    @Override
    public void enterIntegerTerminal(final IntegerTerminalContext ctx) {
        Integer i = Integer.parseInt(ctx.getText());
        conditions.peek().addChild(new Terminal(TerminalType.INTEGER, i));
    }

    @Override
    public void enterBooleanTerminal(final BooleanTerminalContext ctx) {
        Boolean b = Boolean.parseBoolean(ctx.getText());
        conditions.peek().addChild(new Terminal(TerminalType.BOOLEAN, b));
    }

    @Override
    public void enterStringTerminal(final StringTerminalContext ctx) {
        String s = unquote(ctx.getText());
        conditions.peek().addChild(new Terminal(TerminalType.STRING, s));
    }

    @Override
    public void enterIdTerminal(final IdTerminalContext ctx) {
        String p = lookup(ctx.getChild(0));
        conditions.peek().addChild(new Terminal(TerminalType.PROPERTY, p));
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
        String p = lookup(ctx.getChild(0));
        Integer v = Integer.parseInt(ctx.getChild(2).getText());
        ValueBinding binding = new ValueBinding(p, v);
        binding.setCondition(bindingCondition);
        state.addBinding(binding);
    }

    @Override
    public void enterAssignDouble(final AssignDoubleContext ctx) {
        String p = lookup(ctx.getChild(0));
        Double v = Double.parseDouble(ctx.getChild(2).getText());
        ValueBinding binding = new ValueBinding(p, v);
        binding.setCondition(bindingCondition);
        state.addBinding(binding);
    }

    @Override
    public void enterAssignBool(final CLGenParser.AssignBoolContext ctx) {
        String p = lookup(ctx.getChild(0));
        Boolean b = Boolean.parseBoolean(ctx.getChild(2).getText());
        ValueBinding binding = new ValueBinding(p, b);
        binding.setCondition(bindingCondition);
        state.addBinding(binding);
    }

    @Override
    public void enterAssignString(final AssignStringContext ctx) {
        String p = lookup(ctx.getChild(0));
        String s = unquote(ctx.getChild(2).getText());
        ValueBinding binding = new ValueBinding(p, s);
        binding.setCondition(bindingCondition);
        state.addBinding(binding);
    }

    @Override
    public void enterAssignId(final AssignIdContext ctx) {
        String p = lookup(ctx.getChild(0));
        String v = lookup(ctx.getChild(2));
        PropertyBinding binding = new PropertyBinding(p, v);
        binding.setCondition(bindingCondition);
        state.addBinding(binding);
    }

    @Override
    public void enterCommand(final CommandContext ctx) {
        String command = unquote(ctx.getChild(2).getText());
        commandBinding = new CommandBinding(command);
        commandBinding.setCondition(bindingCondition);
        state.addBinding(commandBinding);
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
        String v = lookup(ctx.getChild(2));
        commandBinding.addParam(n, v);
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

    public Map<String, Item> getItems() {
        return items;
    }

    public int getNumberOfSemanticErrors() {
        return errors;
    }

    // Other methods

    public void addErrorListener(final SemanticErrorListener el) {
        errorListeners.add(el);
    }

    public void removeErrorListeners() {
        errorListeners.clear();
    }

    private String lookup(final ParseTree parseTree) {
        Token token = (Token)parseTree.getPayload();
        String symbol = parseTree.getText();
        String property = symbols.get(symbol);
        if (property == null) {
            String message = String.format("Alias '%s' is not defined in item '%s'",
                symbol, item.getName()
            );
            errorListeners.forEach(l -> l.semanticError(this, token, message));
            ++errors;
            return "not-found";
        }
        return property;
    }

}
