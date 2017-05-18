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

/**
 * Represents a tutorial marker.
 *
 * @author Richard Senior
 */
public class Marker implements Visitable {

    private final Coordinate coordinate;
    private final double scale;

    /**
     * Constructs a tutorial marker with a coordinate and scale.
     *
     * @param coordinate the coordinate
     * @param scale the scale (size of pink circle in Flightgear)
     */
    public Marker(final Coordinate coordinate, final double scale) {
        this.coordinate = coordinate;
        this.scale = scale;
    }

    /**
     * @return the coordinate
     */
    public Coordinate getCoordinate() {
        return coordinate;
    }

    /**
     * @return the scale
     */
    public double getScale() {
        return scale;
    }

    @Override
    public void accept(final Visitor visitor) {
        visitor.enter(this);
        visitor.exit(this);
    }

    @Override
    public String toString() {
        return "Marker";
    }

}
