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

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * Error listener.
 *
 * @author Richard Senior
 */
public class ErrorListener extends BaseErrorListener implements SemanticErrorListener {

    private final CommonTokenStream tokenStream;

    /**
     * Construct an error listener with a token stream
     *
     * @param tokenStream the token stream (from a lexer)
     */
    public ErrorListener(final CommonTokenStream tokenStream) {
        this.tokenStream = tokenStream;
    }

    @Override
    public void syntaxError(final Recognizer<?, ?> recognizer, final Object offendingSymbol,
            final int line, final int charPositionInLine,
            final String msg, final RecognitionException e) {
        System.err.format("error at line %d: %s\n", line, msg);
        System.err.println(errorContext(line, charPositionInLine));
    }

    @Override
    public void semanticError(final ParseTreeListener listener,
            final Token token, final String msg) {
        System.err.format("error at line %d: %s\n", token.getLine(), msg);
        System.err.println(errorContext(token.getLine(), token.getCharPositionInLine()));
    }

    @Override
    public void semanticWarning(final ParseTreeListener listener,
            final Token token, final String msg) {
        System.err.format("warning at line %d: %s\n", token.getLine(), msg);
        System.err.println(errorContext(token.getLine(), token.getCharPositionInLine()));
    }

    /**
     * Creates a string that shows the context of a syntax or semantic error.
     *
     * @param line the line number where the error occurred
     * @param charPositionInLine the position of the error within the line
     * @return a string showing the context of the error
     */
    private String errorContext(final int line, final int charPositionInLine) {
        StringBuilder sb = new StringBuilder();
        CharStream stream = tokenStream.getTokenSource().getInputStream();
        String[] lines = stream.toString().split("\n");
        if (line - 1 < lines.length) {
            sb.append(lines[line - 1]).append("\n");
            for (int i = 0; i < charPositionInLine; ++i)
                sb.append(' ');
            sb.append("^");
        }
        return sb.toString();
    }

}
