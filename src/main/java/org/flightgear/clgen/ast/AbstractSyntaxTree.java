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
package org.flightgear.clgen.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract representation of checklists and checks.
 *
 * @author Richard Senior
 */
public class AbstractSyntaxTree implements Visitable {

    private String author;
    private String project;

    private final List<Checklist> checklists = new ArrayList<>();

    /**
     * Gets the name of the project.
     *
     * @return the project name
     */
    public String getProject() {
        return project;
    }

    /**
     * Sets the project name.
     *
     * @param project the project name
     */
    public void setProject(final String project) {
        this.project = project;
    }

    /**
     * Gets the author of the checklists.
     * <p>
     * The author will be null if there is no author directive in the input.
     *
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the author of the checklists.
     *
     * @param author the author
     */
    public void setAuthor(final String author) {
        this.author = author;
    }

    /**
     * Gets the list of checklists defined in the input.
     *
     * @return a list of checklists
     */
    public List<Checklist> getChecklists() {
        return checklists;
    }

    /**
     * Adds a checklist to the abstract syntax tree.
     *
     * @param checklist the checklist to add
     */
    public void addChecklist(final Checklist checklist) {
        checklists.add(checklist);
    }

    /**
     * Accepts a visitor and sends it to each checklist.
     *
     * @param visitor the visitor
     */
    @Override
    public void accept(final Visitor visitor) {
        visitor.enter(this);
        checklists.forEach(checklist -> checklist.accept(visitor));
        visitor.exit(this);
    }

    @Override
    public String toString() {
        return "AST";
    }

}
