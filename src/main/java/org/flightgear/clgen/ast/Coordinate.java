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
 * Cartesian coordinate in 3D space.
 *
 * @author Richard Senior
 */
public class Coordinate {

    private double x, y, z;

    /**
     * Constructs a blank coordinate
     */
    public Coordinate() {}

    /**
     * Constructs a coordinate with x, y and z values
     *
     * @param x the x value
     * @param y the y value
     * @param z the z value
     */
    public Coordinate(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Gets the x coordinate
     *
     * @return the x coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * @param x the x coordinate
     */
    public void setX(final double x) {
        this.x = x;
    }

    /**
     * Gets the y coordinate
     *
     * @return the y coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * @param y the y coordinate
     */
    public void setY(final double y) {
        this.y = y;
    }

    /**
     * Gets the z coordinate
     *
     * @return the z coordinate
     */
    public double getZ() {
        return z;
    }

    /**
     * @param z the z coordinate
     */
    public void setZ(final double z) {
        this.z = z;
    }

}
