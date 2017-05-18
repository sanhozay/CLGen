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
package org.flightgear.clgen.symbol;

/**
 * Symbol.
 *
 * @author Richard Senior
 */
public class Symbol {

    private final String id;
    private final String expansion;
    private Type type;

    /**
     * Construct a symbol with an identifier and property expansion.
     * <p>
     * The identifier and its expansion are immutable once constructed,
     * only the type is mutable.
     *
     * @param id the identifier
     * @param expansion the expansion
     */
    public Symbol(final String id, final String expansion) {
        this.id = id;
        this.expansion = expansion;
        type = Type.NULL;
    }

    /**
     * @return the id of the symbol, i.e. the alias name
     */
    public String getId() {
        return id;
    }

    /**
     * @return the expansion of this symbol, e.g. "controls/gear/brake-parking"
     */
    public String getExpansion() {
        return expansion;
    }

    /**
     * Gets the type of the symbol.
     * <p>
     * Types are initially Type.NULL and are resolved by the
     * {@link org.flightgear.clgen.listener.ItemListener} based on the types
     * of terminals used in conditions and bindings that this symbol is used in.
     *
     * @return the type of the symbol
     */
    public Type getType() {
        return type;
    }

    /**
     * Sets the type of the symbol, with type checking.
     * <p>
     * Symbols with no type assigned can be converted to any type. Numeric
     * symbols can be converted to another numeric type. No other type conversions
     * are allowed.
     *
     * @param type the new type
     * @throws TypeException in the case of questionable type usage
     */
    public void setType(final Type type) throws TypeException {
        if (this.type == Type.NULL || this.type == type) {
            this.type = type;
            return;
        }
        if (this.type.isNumeric() && type.isNumeric()) {
            this.type = type;
            return;
        }
        String message = String.format(
            "Alias '%s' used as %s, previously used as %s",
            id, type, this.type
        );
        throw new TypeException(message);
    }

    @Override
    public String toString() {
        return String.format(" * %s %s = %s", type, id, expansion);
    }

}
