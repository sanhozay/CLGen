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

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.flightgear.clgen.GeneratorException;
import org.flightgear.clgen.ast.AbstractSyntaxTree;
import org.flightgear.clgen.ast.Check;
import org.flightgear.clgen.ast.Checklist;
import org.flightgear.clgen.ast.License;
import org.flightgear.clgen.ast.Marker;
import org.flightgear.clgen.ast.Page;
import org.flightgear.clgen.ast.bindings.CommandBinding;
import org.flightgear.clgen.ast.bindings.PropertyBinding;
import org.flightgear.clgen.ast.bindings.ValueBinding;
import org.flightgear.clgen.ast.conditions.BinaryCondition;
import org.flightgear.clgen.ast.conditions.Condition;
import org.flightgear.clgen.ast.conditions.Operator;
import org.flightgear.clgen.ast.conditions.Terminal;
import org.flightgear.clgen.ast.conditions.UnaryCondition;
import org.flightgear.clgen.symbol.Symbol;
import org.flightgear.clgen.symbol.Type;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * XML Visitor.
 * <p>
 * This is the workhorse of the CLGen back-end. It visits the nodes of the
 * abstract checklist representation and creates a DOM of the various XML
 * output documents before writing them to disk.
 *
 * @author Richard Senior
 */
public class XmlVisitor extends AbstractVisitor {

    private final DocumentBuilder documentBuilder;
    private Transformer transformer;

    private final License license = new License("gpl2");
    private boolean multiPage = false;

    private final Path outputDir;
    private Document wrapper, document;

    private final Deque<Element> wrapperElements = new ArrayDeque<>();
    private final Deque<Element> elements = new ArrayDeque<>();

    private final Deque<BinaryCondition> binaryConditions = new ArrayDeque<>();

    /**
     * Constructs an XML visitor with the path to an output directory.
     *
     * @param outputDir the path to the output directory
     * @throws GeneratorException if the visitor could not be created
     */
    public XmlVisitor(final Path outputDir) throws GeneratorException {
        this.outputDir = outputDir;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            documentBuilder = dbf.newDocumentBuilder();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.setOutputProperty(
                "{http://xml.apache.org/xslt}indent-amount",
                "2"
            );
            transformer = t;
        } catch (TransformerConfigurationException | ParserConfigurationException e) {
            throw new GeneratorException("Could not create XmlVisitor", e);
        }
    }

    @Override
    public void enter(final AbstractSyntaxTree ast) {
        wrapper = documentBuilder.newDocument();
        license.setAuthor(ast.getAuthor());
        license.setTitle("Checklists");
        String l = license.getText();
        if (l != null)
            wrapper.appendChild(wrapper.createComment("\n" + l));
        Element e = wrapper.createElement("PropertyList");
        wrapperElements.push(e);
        wrapper.appendChild(e);
    }

    @Override
    public void exit(final AbstractSyntaxTree ast) throws GeneratorException {
        wrapperElements.pop();
        assert wrapperElements.isEmpty();
        XmlPostProcessor postProcessor = new XmlPostProcessor();
        write(wrapper, "checklists.xml", postProcessor);
    }

    @Override
    public void enter(final Checklist checklist) throws GeneratorException {
        document = documentBuilder.newDocument();
        license.setTitle(String.format("Checklist: %s", checklist.getTitle()));
        String l = license.getText();
        if (l != null)
            document.appendChild(document.createComment("\n" + l));

        Element e = document.createElement("PropertyList");
        appendText(e, "title", checklist.getTitle());
        document.appendChild(e);
        elements.push(e);

        Element c = wrapper.createElement("checklist");
        c.setAttribute("include", filename(checklist));
        wrapperElements.peek().appendChild(c);
        wrapperElements.push(c);
        multiPage = checklist.getPages().size() > 1;
    }

    @Override
    public void exit(final Checklist checklist) {
        wrapperElements.pop();
        elements.pop();
        assert elements.isEmpty();
        XmlPostProcessor xpp = new XmlPostProcessor();
        write(document, filename(checklist), xpp);
    }

    @Override
    public void enter(final Page page) {
        if (multiPage) {
            Element e = document.createElement("page");
            elements.peek().appendChild(e);
            elements.push(e);
        }
    }

    @Override
    public void exit(final Page page) {
        if (multiPage)
            elements.pop();
    }

    @Override
    public void enter(final Check check) {
        Element e = document.createElement("item");
        appendText(e, "name", check.getItem().getName());
        appendText(e, "value", check.getState().getName());
        for (String additionalValue : check.getAdditionalValues())
            appendText(e, "value", additionalValue);
        elements.peek().appendChild(e);
        elements.push(e);
    }

    @Override
    public void exit(final Check check) {
        elements.pop();
    }

    @Override
    public void enter(final Condition condition) {
        Element e = document.createElement("condition");
        elements.peek().appendChild(e);
        elements.push(e);
    }

    @Override
    public void exit(final Condition condition) {
        elements.pop();
    }

    @Override
    public void enter(final BinaryCondition condition) {
        /*
         * To optimize the XML output, operator elements are only pushed onto the
         * stack if the parent condition differs from the current condition. This
         * simplifies conditional structures based on parse trees built from
         * expressions like a && b&& c -> ((a && b) && c)
         */
        if (!nestedCondition(condition)) {
            Element e = document.createElement(operatorTag(condition.getOperator()));
            elements.peek().appendChild(e);
            elements.push(e);
        }
        binaryConditions.push(condition);
    }

    @Override
    public void exit(final BinaryCondition condition) {
        binaryConditions.pop();
        if (!nestedCondition(condition))
            elements.pop();
    }

    @Override
    public void enter(final UnaryCondition condition) {
        if (condition.getOperator() != null) {
            Element e = document.createElement(operatorTag(condition.getOperator()));
            elements.peek().appendChild(e);
            elements.push(e);
        }
    }

    @Override
    public void exit(final UnaryCondition condition) {
        if (condition.getOperator() != null)
            elements.pop();
    }

    @Override
    public void enter(final Terminal terminal) {
        appendTerminal(elements.peek(), terminal);
    }

    @Override
    public void enter(final ValueBinding binding) {
        Element e = document.createElement("binding");
        appendText(e, "command", "property-assign");
        appendText(e, "property", binding.getSymbol().getExpansion());
        appendText(e, "value", binding.getValue(), binding.getSymbol().getType());
        elements.peek().appendChild(e);
        elements.push(e);
    }

    @Override
    public void exit(final ValueBinding binding) {
        elements.pop();
    }

    @Override
    public void enter(final CommandBinding binding) {
        Element e = document.createElement("binding");
        appendText(e, "command", binding.getCommand());
        for (String name : binding.getParams().keySet()) {
            Object value = binding.getParams().get(name);
            if (value instanceof Symbol) {
                Symbol symbol = (Symbol)value;
                appendText(e, name, symbol.getExpansion());
            } else if (value instanceof String)
                appendText(e, name, value, Type.STRING);
            else if (value instanceof Boolean)
                appendText(e, name, value, Type.BOOL);
            else
                appendText(e, name, value);
        }
        elements.peek().appendChild(e);
        elements.push(e);
    }

    @Override
    public void exit(final CommandBinding binding) {
        elements.pop();
    }

    @Override
    public void enter(final PropertyBinding binding) {
        Element e = document.createElement("binding");
        appendText(e, "command", "property-assign");
        appendText(e, "property", binding.getLval());
        appendText(e, "property", binding.getRval());
        elements.peek().appendChild(e);
        elements.push(e);
    }

    @Override
    public void exit(final PropertyBinding binding) {
        elements.pop();
    }

    @Override
    public void enter(final Marker marker) {
        Element e = document.createElement("marker");
        appendText(e, "x-m", Double.toString(marker.getCoordinate().getX()));
        appendText(e, "y-m", Double.toString(marker.getCoordinate().getY()));
        appendText(e, "z-m", Double.toString(marker.getCoordinate().getZ()));
        appendText(e, "scale", Double.toString(marker.getScale()));
        elements.peek().appendChild(e);
    }

    // Other methods

    private void appendTerminal(final Element parent, final Terminal terminal) {
        if (terminal.getValue() instanceof Symbol) {
            Symbol symbol = (Symbol)terminal.getValue();
            appendText(parent, "property", symbol.getExpansion());
        } else if (terminal.getValue() instanceof String)
            appendText(parent, "value", terminal.getValue(), Type.STRING);
        else if (terminal.getValue() instanceof Boolean)
            appendText(parent, "value", terminal.getValue(), Type.BOOL);
        else
            appendText(parent, "value", terminal.getValue());
    }

    private void appendText(final Element parent, final String node,
        final Object value, final Type type) {
        Element e = document.createElement(node);
        parent.appendChild(e);
        String typeAttribute = typeAttributeForType(type);
        if (typeAttribute != null)
            e.setAttribute("type", typeAttribute);
        e.appendChild(document.createTextNode(value.toString()));
    }

    private void appendText(final Element parent, final String node, final Object value) {
        appendText(parent, node, value, Type.NULL);
    }

    private String filename(final Checklist checklist) {
        String s = checklist.getTitle()
            .toLowerCase()
            .replaceAll(" ", "-");
        return s + ".xml";
    }

    private boolean nestedCondition(final BinaryCondition condition) {
        return !binaryConditions.isEmpty() &&
            condition.getOperator() == binaryConditions.peek().getOperator();
    }

    private String operatorTag(final Operator op) {
        switch (op) {
        case AND: return "and";
        case OR: return "or";
        case NOT: return "not";
        case EQ: return "equals";
        case NE: return "not-equals";
        case GT: return "greater-than";
        case LT: return "less-than";
        case GE: return "greater-than-equals";
        case LE: return "less-than-equals";
        }
        assert false;
        return null;
    }

    private String typeAttributeForType(final Type type) {
        if (type == Type.BOOL)
            return "bool";
        if (type == Type.STRING)
            return "string";
        return null;
    }

    private void write(final Document document, final String filename,
            final XmlPostProcessor postProcessor) throws GeneratorException {
        Path p = outputDir.resolve(filename);
        StringWriter sw = new StringWriter();
        try {
            transformer.transform(
                new DOMSource(document),
                new StreamResult(sw)
            );
            postProcessor.setXml(sw.toString());
            Files.write(p, postProcessor.getXml().getBytes());
            System.out.println(p.toAbsolutePath().normalize().toString());
        } catch (TransformerException | IOException e) {
            String message = String.format("Failed to write output file: %s", filename);
            throw new GeneratorException(message, e);
        }
    }

}
