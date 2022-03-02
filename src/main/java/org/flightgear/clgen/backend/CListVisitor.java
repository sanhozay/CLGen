/*
 * Copyright (C) 2020 Richard Senior
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

import org.flightgear.clgen.GeneratorException;
import org.flightgear.clgen.ast.AbstractSyntaxTree;
import org.flightgear.clgen.ast.Check;
import org.flightgear.clgen.ast.Checklist;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Creates a XChecklist representation of the checklists for X-Plane.
 *
 * @author Richard Senior
 */
public class CListVisitor extends AbstractVisitor {

    private final Path outputDir;
    private final StringBuilder clist = new StringBuilder();

    private boolean firstCheck = true;
    private boolean firstChecklist = true;

    /**
     * Constructs a CList visitor with the path to the output directory.
     *
     * @param outputDir the path to the output directory
     */
    public CListVisitor(final Path outputDir) {
        this.outputDir = outputDir;
    }

    @Override
    public void enter(final AbstractSyntaxTree ast) {
        clist.append("sw_checklist:CHECKLIST\n");
        if (ast.getProject() != null)
            clist.append("sw_itemvoid:").append(ast.getProject()).append("\n");
    }

    @Override
    public void exit(final AbstractSyntaxTree ast) {
        Path path = outputDir.resolve("clist.txt");
        try {
            Files.write(path, clist.toString().getBytes());
            System.out.println(path.toAbsolutePath().normalize().toString());
        } catch (IOException e) {
            String message = String.format("Failed to create XChecklist file at '%s'", path);
            throw new GeneratorException(message, e);
        }
    }

    @Override
    public void enter(final Checklist checklist) {
        if (!firstChecklist) {
            clist.append("sw_continue:")
                .append(checklist.getTitle())
                .append("\n");
        }
        clist.append("\n# ")
            .append(checklist.getTitle())
            .append("\n");
        clist.append("sw_checklist:")
            .append(checklist.getTitle())
            .append(":")
            .append(checklist.getTitle())
            .append("\n");
        clist.append("sw_itemvoid:")
            .append(checklist.getTitle().toUpperCase())
            .append("\n");
        firstChecklist = false;
        firstCheck = true;
    }

    @Override
    public void enter(final Check check) {
        if (check.isSpacer()) {
            if (!firstCheck) {
                clist.append("sw_itemvoid:\n");
            }
        } else if (check.isSubtitle()) {
            if (!firstCheck) {
                clist.append("sw_itemvoid:\n");
            }
            clist.append("sw_itemvoid:")
            .append(check.getItem().getName())
            .append("\n");
        } else {
            clist.append("sw_item:")
                .append(check.getItem().getName())
                .append("|")
                .append(check.getState().getName())
                .append("\n");
            for (String value : check.getAdditionalValues()) {
                clist.append("sw_itemvoid:").append(value).append("\n");
            }
        }
        firstCheck = false;
    }

}
