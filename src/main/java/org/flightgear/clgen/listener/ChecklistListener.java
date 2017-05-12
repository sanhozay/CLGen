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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.Token;

import org.flightgear.clgen.CLGenBaseListener;
import org.flightgear.clgen.CLGenParser;
import org.flightgear.clgen.ast.AbstractSyntaxTree;
import org.flightgear.clgen.ast.Check;
import org.flightgear.clgen.ast.CheckContainer;
import org.flightgear.clgen.ast.Checklist;
import org.flightgear.clgen.ast.Item;
import org.flightgear.clgen.ast.Page;
import org.flightgear.clgen.ast.State;

/**
 * Listener that builds an abstract representation of a Flightgear checklist.
 * <p>
 * The checklist listener builds the final data structure used in checklist
 * generation.
 *
 * @author Richard Senior
 */
public class ChecklistListener extends CLGenBaseListener {

    private final AbstractSyntaxTree ast = new AbstractSyntaxTree();
    private final Map<String, Item> items;
    private final List<SemanticErrorListener> errorListeners = new ArrayList<>();
    private int errors = 0;

    private Checklist checklist;
    private Page page;

    public ChecklistListener(final Map<String, Item> items) {
        this.items = items;
    }

    @Override
    public void enterAuthor(final CLGenParser.AuthorContext ctx) {
        ast.setAuthor(unquote(ctx.getChild(2).getText()));
    }

    @Override
    public void enterChecklist(final CLGenParser.ChecklistContext ctx) {
        checklist = new Checklist(unquote(ctx.getChild(2).getText()));
    }

    @Override
    public void enterPage(final CLGenParser.PageContext ctx) {
        page = new Page();
        checklist.addPage(page);
    }

    @Override
    public void exitPage(final CLGenParser.PageContext ctx) {
        page = null;
    }

    @Override
    public void enterCheck(final CLGenParser.CheckContext ctx) {
        String n = unquote(ctx.getChild(2).getText());
        String s = unquote(ctx.getChild(4).getText());
        Item item = items.get(n);
        if (item == null) {
            Token token = (Token)ctx.getChild(2).getPayload();
            String message = String.format(
                "Undefined item '%s' in checklist '%s'", n, checklist.getTitle()
            );
            errorListeners.forEach(l -> l.semanticError(this, token, message));
            ++errors;
            return;
        }
        State state = item.getStates().get(s);
        if (state == null) {
            Token token = (Token)ctx.getChild(4).getPayload();
            String message = String.format(
                "State '%s' is not defined for item '%s'", s, n
            );
            errorListeners.forEach(l -> l.semanticError(this, token, message));
            ++errors;
            return;
        }
        Check check = new Check(item, state);
        for (int i = 6; i < ctx.getChildCount() - 2; i += 2)
            check.addAdditionalValue(unquote(ctx.getChild(i).getText()));
        CheckContainer cc = page != null ? page : checklist;
        cc.addCheck(check);
    }

    @Override
    public void exitChecklist(final CLGenParser.ChecklistContext ctx) {
        ast.addChecklist(checklist);
    }

    // Accessors

    public AbstractSyntaxTree getAST() {
        return ast;
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

}
