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

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.flightgear.clgen.ast.AbstractSyntaxTree;
import org.flightgear.clgen.ast.Check;
import org.flightgear.clgen.ast.Item;
import org.flightgear.clgen.ast.State;

/**
 * Usage visitor.
 * <p>
 * Checks for unused items and states.
 *
 * @author Richard Senior
 */
public class UsageVisitor extends AbstractVisitor {

    private class ItemUsage {
        public int count = 0;
        public final Map<String, Integer> stateUsages = new TreeMap<>();
    }

    private final Map<String, ItemUsage> itemUsages = new TreeMap<>();
    private int warnings = 0;

    /**
     * Constructs a usage visitor with the items lookup table.
     *
     * @param items the items lookup table
     */
    public UsageVisitor(final Map<String, Item> items) {
        for (Entry<String, Item> itemEntry : items.entrySet()) {
            ItemUsage usage = new ItemUsage();
            itemUsages.put(itemEntry.getKey(), usage);
            Map<String, State> states = itemEntry.getValue().getStates();
            for (Entry<String, State> stateEntry : states.entrySet())
                usage.stateUsages.put(stateEntry.getKey(), 0);
        }
    }

    @Override
    public void enter(final Check check) {
        if (check.isSpacer() || check.isSubtitle())
            return;
        Item item = check.getItem();
        ItemUsage usage = itemUsages.get(item.getName());
        ++usage.count;
        State state = check.getState();
        int count = usage.stateUsages.get(state.getName());
        usage.stateUsages.replace(state.getName(), ++count);
    }

    @Override
    public void exit(final AbstractSyntaxTree ast) {
        for (Entry<String, ItemUsage> itemUsage : itemUsages.entrySet()) {
            if (itemUsage.getValue().count == 0) {
                System.out.format(
                    "warning: item '%s' is not used in any checklist\n",
                    itemUsage.getKey()
                );
                ++warnings;
                continue;
            }
            Map<String, Integer> stateUsages = itemUsage.getValue().stateUsages;
            for (Entry<String, Integer> stateUsage : stateUsages.entrySet())
                if (stateUsage.getValue() == 0) {
                    System.out.format("warning: state '%s' in item '%s' is not used\n",
                        stateUsage.getKey(), itemUsage.getKey()
                    );
                    ++warnings;
                }
        }
    }

    /**
     * Gets the number of usage warnings found by this usage visitor.
     * <p>
     * Usage warnings are generated for unused states and items.
     *
     * @return the number of usage warnings
     */
    public int getNumberOfWarnings() {
        return warnings;
    }

}
