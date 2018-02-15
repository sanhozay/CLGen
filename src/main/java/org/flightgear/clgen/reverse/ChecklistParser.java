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

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.flightgear.clgen.ast.Check;
import org.flightgear.clgen.ast.Checklist;
import org.flightgear.clgen.ast.Item;
import org.flightgear.clgen.ast.Marker;
import org.flightgear.clgen.ast.State;
import org.flightgear.clgen.backend.DotVisitor;
import org.flightgear.clgen.backend.PdfVisitor;
import org.xml.sax.SAXException;

/**
 * Reverse engineer checklists into CLGen input file.
 *
 * @author Richard Senior
 */
public class ChecklistParser extends AbstractXmlParser {

    private final Path input;
    private final ChecklistParserDelegate delegate;

    /**
     * Construct a CLRev instance with the path of an input file.
     *
     * @param input the input file
     */
    public ChecklistParser(final Path input) {
        this.input = input;
        delegate = new ChecklistParserDelegate(this);
    }

    /**
     * Runs the checklist parser.
     *
     * @throws IOException if the output file cannot be written
     */
    public void run() throws IOException {
        parse(input);
        Path outputDir = input.toAbsolutePath().getParent();
        Path filename = Paths.get(outputDir.toString(), "checklists.clg");
        emit(filename);
        delegate.getAst().accept(new DotVisitor(outputDir));
        delegate.getAst().accept(new PdfVisitor(outputDir));
    }

    @Override
    protected void parse(final Path path) {
        try {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            saxParser.parse(path.toFile(), delegate);
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
    }

    private void emit(final Path filename) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(filename.toFile()))) {
            for (Item item : delegate.getItems().values())
                emitItem(out, item);
            for (Checklist checklist : delegate.getAst().getChecklists())
                emitChecklist(out, checklist);
        }
        System.out.println(filename.toAbsolutePath().normalize().toString());
    }

    private void emitChecklist(final PrintWriter out, final Checklist checklist) {
        out.format("checklist(%s) {\n", quote(checklist.getTitle()));
        for (Check check : checklist.getChecks())
            emitCheck(out, check);
        out.println("}\n");
    }

    private void emitCheck(final PrintWriter out, final Check check) {
        StringBuilder av = new StringBuilder();
        check.getAdditionalValues().forEach(v -> av.append(", ").append(quote(v)));
        if (check.isSpacer())
            out.println("    text();");
        else if (check.isSubtitle())
            out.format("    text(%s);\n",
                quote(check.getItem().getName())
            );
        else
            out.format("    check(%s, %s%s);\n",
                quote(check.getItem().getName()),
                quote(check.getState().getName()),
                av.toString()
            );
    }

    private void emitItem(final PrintWriter out, final Item item) {
        out.format("item(%s) {\n", quote(item.getName()));
        for (Entry<String, State> state : item.getStates().entrySet())
            out.format("    state(%s);\n", quote(state.getValue().getName()));
        Marker marker = item.getMarker();
        if (marker != null)
            emitMarker(out, marker);
        out.println("}\n");
    }

    private void emitMarker(final PrintWriter out, final Marker marker) {
        out.format("    marker(%.4f, %.4f, %.4f, %.4f);\n",
            marker.getCoordinate().getX(),
            marker.getCoordinate().getY(),
            marker.getCoordinate().getZ(),
            marker.getScale()
        );
    }

}
