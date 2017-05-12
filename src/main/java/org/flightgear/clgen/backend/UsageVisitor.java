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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
        public final Map<String, Integer> stateUsages = new HashMap<>();
    }

    private final Map<String, ItemUsage> itemUsages = new HashMap<>();
    private int warnings = 0;

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
        Item item = check.getItem();
        ItemUsage usage = itemUsages.get(item.getName());
        ++usage.count;
        State state = check.getState();
        int count = usage.stateUsages.get(state.getName());
        usage.stateUsages.replace(state.getName(), ++count);
    }

    @Override
    public void exit(final AbstractSyntaxTree ast) {
        List<String> itemNames = new ArrayList<>(itemUsages.keySet());
        Collections.sort(itemNames);
        for (String itemName : itemNames) {
            ItemUsage usage = itemUsages.get(itemName);
            if (usage.count == 0) {
                System.out.format("warning: item '%s' is not used in any checklist\n", itemName);
                ++warnings;
                continue;
            }
            List<String> stateNames = new ArrayList<>(usage.stateUsages.keySet());
            Collections.sort(stateNames);
            for (String stateName: stateNames) {
                Integer count = usage.stateUsages.get(stateName);
                if (count == 0) {
                    System.out.format("warning: state '%s' in item '%s' is not used\n",
                        stateName, itemName
                    );
                    ++warnings;
                }
            }
        }
    }

    public int getNumberOfWarnings() {
        return warnings;
    }

}
