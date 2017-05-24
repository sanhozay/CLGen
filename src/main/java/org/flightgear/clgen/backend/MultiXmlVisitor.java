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

import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;

import org.flightgear.clgen.GeneratorException;
import org.flightgear.clgen.ast.AbstractSyntaxTree;
import org.flightgear.clgen.ast.Checklist;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Multi-document XML Visitor.
 * <p>
 * Writes multiple output XML files, one for each checklist, plus a wrapper.
 *
 * @author Richard Senior
 */
public class MultiXmlVisitor extends XmlVisitor {

    private String author;
    private Document wrapper;
    private final Deque<Element> wrapperElements = new ArrayDeque<>();

    /**
     * Constructs an XML visitor with the path to an output directory.
     *
     * @param outputDir the path to the output directory
     * @throws GeneratorException if the visitor could not be created
     */
    public MultiXmlVisitor(final Path outputDir) throws GeneratorException {
        super(outputDir);
    }

    @Override
    public void enter(final AbstractSyntaxTree ast) {
        String title = ast.getProject() != null ? ast.getProject() : "Checklists";
        // Save author to field for use in enter checklist
        author = ast.getAuthor();
        wrapper = open(title, author);

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
        String title = String.format("Checklist: %s", checklist.getTitle());
        document = open(title, author);
        multiPage = checklist.getPages().size() > 1;

        Element e = document.createElement("PropertyList");
        appendText(e, "title", checklist.getTitle());
        document.appendChild(e);
        elements.push(e);

        Element c = wrapper.createElement("checklist");
        c.setAttribute("include", filename(checklist));
        wrapperElements.peek().appendChild(c);
        wrapperElements.push(c);
    }

    @Override
    public void exit(final Checklist checklist) {
        elements.pop();
        assert elements.isEmpty();
        write(document, filename(checklist), new XmlPostProcessor());
        wrapperElements.pop();
    }

}
