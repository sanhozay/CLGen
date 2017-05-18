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
import java.nio.file.Files;
import java.nio.file.Path;

import org.flightgear.clgen.GeneratorException;
import org.flightgear.clgen.ast.AbstractSyntaxTree;
import org.flightgear.clgen.ast.Check;
import org.flightgear.clgen.ast.Checklist;

/**
 * Creates a Graphviz DOT representation of the checklists.
 * <p>
 * This can be rendered to an image using:
 * <code>$ dot -ochecklists.png -Tpng checklists.dot</code>
 *
 * @author Richard Senior
 */
public class DotVisitor extends AbstractVisitor {

    private final Path outputDir;

    private final StringBuilder dot = new StringBuilder();
    private final StringBuilder nodes = new StringBuilder();
    private final StringBuilder edges = new StringBuilder();

    private int index = 0;
    private double hue = 0.0;
    private double colorCycle;

    /**
     * Constructs a DOT visitor with the path to the output directory.
     *
     * @param outputDir the path to the output directory
     */
    public DotVisitor(final Path outputDir) {
        this.outputDir = outputDir;
    }

    @Override
    public void enter(final AbstractSyntaxTree ast) {
        colorCycle = 1.0 / ast.getChecklists().size();
        dot.append("digraph G {\n")
            .append("    pad=0.5;\n")
            .append("    ranksep=0.35;\n")
            .append("    node [fontsize=12];\n")
            .append("    node [fontcolor=white,fontname=\"helvetica-bold\"];\n")
            .append("    node [shape=Mrecord,width=2.5,style=filled];\n");
    }

    @Override
    public void exit(final AbstractSyntaxTree ast) {
        dot.append("    node [color=\"#404040\",fontcolor=\"#404040\",fontname=\"helvetica\"];\n")
            .append("    node [shape=note,width=2.25,style=\"\"];\n");

        dot.append(nodes.toString());
        dot.append(edges.toString());

        dot.append("}\n");
        Path path = outputDir.resolve("checklists.dot");
        try {
            Files.write(path, dot.toString().getBytes());
            System.out.println(path.toAbsolutePath().normalize().toString());
        } catch (IOException e) {
            String message = String.format("Failed to create DOT file at '%s'", path);
            throw new GeneratorException(message, e);
        }
    }

    @Override
    public void enter(final Checklist checklist) {
        String s = String.format("    node [color=\"%.04f,0.6,0.6\"]; %s;\n",
            hue, quote(checklist.getTitle())
        );
        dot.append(s);
        edges.append("    " + quote(checklist.getTitle()));
        int totalChecks = checklist.getPages()
            .parallelStream()
            .mapToInt(page -> page.getChecks().size())
            .sum();
        for (int i = 0; i < totalChecks; ++i)
            edges.append(" -> ").append(index + i);
        edges.append(";\n");
    }

    @Override
    public void exit(final Checklist checklist) {
        hue += colorCycle;
    }

    @Override
    public void enter(final Check check) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("    %d [label=\"%s %s",
            index++, check.getItem().getName(), check.getState().getName()
        ));
        for (String value : check.getAdditionalValues())
            sb.append("&#92;n" + value.replaceAll("[\"]", "\\\\\""));
        sb.append("\"];\n");
        nodes.append(sb.toString());
    }

    private String quote(final String s) {
        return String.format("\"%s\"", s);
    }

}
