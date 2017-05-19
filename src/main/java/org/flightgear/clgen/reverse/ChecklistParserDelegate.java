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
package org.flightgear.clgen.reverse;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.TreeMap;

import org.flightgear.clgen.GeneratorException;
import org.flightgear.clgen.ast.AbstractSyntaxTree;
import org.flightgear.clgen.ast.Check;
import org.flightgear.clgen.ast.Checklist;
import org.flightgear.clgen.ast.Item;
import org.flightgear.clgen.ast.Marker;
import org.flightgear.clgen.ast.State;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Delegate for the SAX parser that parses existing checklist XML files.
 *
 * @author Richard Senior
 */
public class ChecklistParserDelegate extends DefaultHandler {

    private final AbstractXmlParser parser;
    private final Deque<StringBuilder> builderStack = new ArrayDeque<>();

    private final AbstractSyntaxTree ast = new AbstractSyntaxTree();
    private final Map<String, Item> items = new TreeMap<>();

    private Checklist checklist;
    private Check check;
    private Item item;
    private State state;
    private Marker marker;

    private boolean conditionOrBinding = false;

    /**
     * Constructs a parser delegate with a reference back to its parser.
     *
     * @param parser the parent parser
     */
    public ChecklistParserDelegate(final AbstractXmlParser parser) {
        this.parser = parser;
    }

    @Override
    public void startElement(final String namespaceURI, final String localName,
            final String qName, final Attributes atts) throws SAXException {
        builderStack.push(new StringBuilder());
        switch(qName) {
        case "checklist":
            checklist = new Checklist();
            processIncludes(atts);
            break;
        case "item":
            item = new Item();
            state = new State();
            check = new Check(item, state);
            break;
        case "condition":
        case "binding":
            conditionOrBinding = true;
            break;
        case "marker":
            marker = new Marker();
            break;
        }
    }

    @Override
    public void endElement(final String uri, final String localName,
            final String qName) throws SAXException {
        final String s = builderStack.pop().toString();
        switch(qName) {
        case "checklist":
            ast.addChecklist(checklist);
            break;
        case "title":
            checklist.setTitle(s);
            break;
        case "item":
            if (items.containsKey(item.getName()))
                item = items.get(item.getName());
            item.addState(state);
            items.put(item.getName(), item);
            checklist.addCheck(check);
            break;
        case "name":
            item.setName(s);
            break;
        case "value":
            if (state.getName() == null)
                state.setName(s);
            else if (!conditionOrBinding)
                check.addAdditionalValue(s);
            break;
        case "condition":
        case "binding":
            conditionOrBinding = false;
            break;
        case "marker":
            item.setMarker(marker);
            break;
        case "x-m":
            marker.getCoordinate().setX(Double.parseDouble(s));
            break;
        case "y-m":
            marker.getCoordinate().setY(Double.parseDouble(s));
            break;
        case "z-m":
            marker.getCoordinate().setZ(Double.parseDouble(s));
            break;
        case "scale":
            marker.setScale(Double.parseDouble(s));
            break;
        }
    }

    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        if (!builderStack.isEmpty())
            builderStack.peek().append(ch, start, length);
    }

    // Accessors

    /**
     * @return the AST
     */
    public AbstractSyntaxTree getAst() {
        return ast;
    }

    /**
     * @return the items
     */
    public Map<String, Item> getItems() {
        return items;
    }

    // Other methods

    private void processIncludes(final Attributes atts) throws GeneratorException {
        for (int i = 0; i < atts.getLength(); ++i)
            if ("include".equals(atts.getQName(i))) {
                Path path = Paths.get(atts.getValue(i));
                if (path.toFile().canRead())
                    parser.parse(path);
                else {
                    String message = String.format("Cannot read included file at path %s",
                        path.toAbsolutePath().toString()
                    );
                    throw new GeneratorException(message);
                }
            }
    }

}
