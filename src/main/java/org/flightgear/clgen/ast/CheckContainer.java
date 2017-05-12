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

import java.util.List;

/**
 * Interface for domain objects that can contain checks, e.g. checklists and pages.
 *
 * @author Richard Senior
 */
public interface CheckContainer {

    /**
     * Adds a check to this container.
     *
     * @param check the check to add
     */
    void addCheck(final Check check);

    /**
     * Gets the list of checks associated with this container.
     *
     * @return a list of checks
     */
    List<Check> getChecks();

}
