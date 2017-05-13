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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Symbol Table.
 * <p>
 * Supports distinct local and global scopes.
 *
 * @author Richard Senior
 */
public class SymbolTable {

    /**
     * Name for global scope.
     */
    public static final String GLOBAL = "__global";

    private final Map<String, Map<String, Symbol>> symbolTable = new HashMap<>();

    /**
     * Construct a new symbol table.
     */
    public SymbolTable() {
        symbolTable.put(GLOBAL, new HashMap<>());
    }

    /**
     * Adds a symbol to the symbol table.
     *
     * @param scope the scope
     * @param symbol the symbol to add
     * @throws DuplicateSymbolException if the symbol already exists in the table
     */
    public void add(final String scope, final Symbol symbol) throws DuplicateSymbolException {
        if (symbolTable.get(scope) == null) {
            Map<String, Symbol> scopeTable = new HashMap<>();
            symbolTable.put(scope, scopeTable);
        }
        if (symbolTable.get(scope).get(symbol.getId()) != null) {
            String message = String.format(
                "Symbol '%s' already exists in scope '%s'",
                symbol, scope
            );
            throw new DuplicateSymbolException(message);
        }
        symbolTable.get(scope).put(symbol.getId(), symbol);
    }

    /**
     * Looks up a symbol in the symbol table.
     *
     * @param scope the scope
     * @param id the identifier
     * @return the symbol, or null if not found
     */
    public Symbol lookup(final String scope, final String id) {
        Map<String, Symbol> globalTable = symbolTable.get(GLOBAL);
        Map<String, Symbol> scopeTable = symbolTable.get(scope);
        if (scopeTable == null)
            return globalTable.get(id);
        Symbol found = scopeTable.get(id);
        return found != null ? found : globalTable.get(id);
    }

    /**
     * Dumps the symbol table (for debugging)
     */
    public void dump() {
        for (Entry<String, Map<String, Symbol>> scopeEntry : symbolTable.entrySet()) {
            System.out.println(scopeEntry.getKey());
            for (Entry<String, Symbol> symbolEntry : scopeEntry.getValue().entrySet()) {
                Symbol symbol = symbolEntry.getValue();
                System.out.format(" * %s %s = %s\n",
                    symbol.getType(),
                    symbol.getId(),
                    symbol.getExpansion()
                );
            }
        }
    }

}
