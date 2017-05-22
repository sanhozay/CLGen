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
 * Represents a checklist.
 *
 * @author Richard Senior
 */
public class Checklist implements Visitable, CheckContainer {

    private final List<Page> pages = new ArrayList<>();
    private String title;

    /**
     * Constructs a blank checklist.
     */
    public Checklist() {}

    /**
     * Constructs a checklist with a title.
     *
     * @param title the title
     */
    public Checklist(final String title) {
        this.title = title;
    }

    /**
     * Gets the title of the checklist.
     * <p>
     * The title appears in the checklist selection at the top of the
     * Flightgear dialog.
     *
     * @return the checklist title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * Gets the list of pages associated with this checklist.
     * <p>
     * For a non-paged checklist with checks, returns list containing
     * a single page.
     *
     * @return a list of pages
     */
    public List<Page> getPages() {
        return pages;
    }

    /**
     * Adds a page to this checklist.
     *
     * @param page the page to add
     */
    public void addPage(final Page page) {
        pages.add(page);
    }

    /**
     * Adds a check to this checklist.
     * <p>
     * The check is actually added to a page within the checklist, even if
     * pages are not defined. A single page is created for checks added to
     * checklists. The grammar must exclude the possiblity of a mixture of
     * checks direct in the checklist and checks in pages.
     *
     * @param check the check to add
     */
    @Override
    public void addCheck(final Check check) {
        if (pages.size() == 0)
            pages.add(new Page());
        assert pages.size() == 1;
        pages.get(0).addCheck(check);
    }

    /**
     * Gets the checks associated with this checklist.
     * <p>
     * These checks come from the default page.
     *
     * @return a list of checks
     */
    @Override
    public List<Check> getChecks() {
        return pages.size() == 0 ? new ArrayList<>() : pages.get(0).getChecks();
    }

    /**
     * Accepts a visitor and sends it to each page of this checklist.
     *
     * @param visitor the visitor
     */
    @Override
    public void accept(final Visitor visitor) {
        visitor.enter(this);
        pages.forEach(page -> page.accept(visitor));
        visitor.exit(this);
    }

    @Override
    public String toString() {
        return String.format("Checklist: %s",  title);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (title == null ? 0 : title.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Checklist other = (Checklist)obj;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equalsIgnoreCase(other.title))
            return false;
        return true;
    }

}
