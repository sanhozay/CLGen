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
package org.flightgear.clgen.listener;

import static org.flightgear.clgen.listener.ListenerSupport.errorContext;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * Semantic error listener.
 *
 * @author Richard Senior
 */
public class WalkErrorListener extends SemanticErrorListener {

    public WalkErrorListener(final CommonTokenStream tokenStream) {
        super(tokenStream);
    }

    @Override
    public void semanticError(final ParseTreeListener listener,
            final Token token, final String msg) {
        System.err.format("error at line %d: %s\n", token.getLine(), msg);
        System.err.println(errorContext(token.getLine(),
            token.getCharPositionInLine(), tokenStream
        ));
    }

}
