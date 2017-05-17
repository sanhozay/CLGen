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
package org.flightgear.clgen;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import org.flightgear.clgen.CLGenParser.SpecificationContext;
import org.flightgear.clgen.ast.AbstractSyntaxTree;
import org.flightgear.clgen.ast.Item;
import org.flightgear.clgen.backend.DotVisitor;
import org.flightgear.clgen.backend.UsageVisitor;
import org.flightgear.clgen.backend.XmlVisitor;
import org.flightgear.clgen.listener.ChecklistListener;
import org.flightgear.clgen.listener.ErrorListener;
import org.flightgear.clgen.listener.ItemListener;

/**
 * CLGen main class.
 *
 * @author Richard Senior
 */
public class CLGen {

    private ErrorListener errorListener;
    private final ParseTreeWalker walker = new ParseTreeWalker();

    private final Path input;
    private int errors = 0;
    private int warnings = 0;

    public CLGen(final Path input) {
        this.input = input;
    }

    /**
     * Main program.
     *
     * @param args the program arguments
     */
    public static void main(final String[] args) {
        CLGenProperties properties = CLGenProperties.getInstance();
        System.out.format("CLGen %s\n", properties.getVersion());
        if (args.length != 1) {
            usage();
            System.exit(-1);
        }
        Path path = Paths.get(args[0]).normalize();
        if (path.toFile().canRead())
            try {
                new CLGen(path).run();
            } catch (Exception e) {
                e.printStackTrace(System.err);
                System.err.println("Generation failed.");
                System.exit(-1);
            }
        else {
            System.err.format("Cannot read input: %s\n", path.toFile().getAbsolutePath());
            System.exit(-1);
        }
    }

    /*
     * Prints a usage message to standard output.
     */
    private static void usage() {
        System.out.println("Usage: clgen INPUT_FILE");
    }

    /*
     * Coordinates the parsing and generation process in multiple phases.
     *
     * The first phase is lexical analysis and parsing. Any errors in this
     * phase are likely to confuse subsequent phases so the program aborts
     * if there are any parse errors.
     *
     * If parsing is successful, the resulting parse tree is scanned to build
     * a lookup table of checklist items. The parse tree is then rescanned
     * to build an abstract representation of the checklist output, using the
     * item table to populate the checklists and checks. Semantic errors from
     * this phase abort the program.
     *
     * When the abstract representation is complete, visitors scan it to verify
     * and then generate the output.
     */
    private void run() throws IOException, GeneratorException {
        CLGenLexer lexer = new CLGenLexer(CharStreams.fromPath(input));
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        errorListener = new ErrorListener(tokenStream);
        SpecificationContext context = parse(tokenStream);
        if (errors != 0)
            System.err.format(
                "Generation failed with %d error%s.\n",
                errors, errors != 1 ? "s" : ""
            );
        else {
            Map<String, Item> items = buildItems(context);
            //symbolTable.dump();
            AbstractSyntaxTree ast = buildAST(items, context);
            if (errors > 0) {
                System.err.format(
                    "Generation failed with %d error%s.\n",
                    errors, errors != 1 ? "s" : ""
                );
                return;
            }
            UsageVisitor usageVisitor = new UsageVisitor(items);
            ast.accept(usageVisitor);
            warnings += usageVisitor.getNumberOfWarnings();
            ast.accept(new XmlVisitor(input.toAbsolutePath().getParent()));
            ast.accept(new DotVisitor(input.toAbsolutePath().getParent()));
            if (warnings > 0)
                System.out.format(
                    "Generation complete with %d warning%s.\n",
                    warnings,
                    warnings != 1 ? "s" : ""
                );
            else
                System.out.println("Generation complete.");
        }
    }

    /*
     * Parses the input, producing a parse tree as output.
     */
    private SpecificationContext parse(final CommonTokenStream tokenStream) throws IOException {
        CLGenParser parser = new CLGenParser(tokenStream);
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);
        SpecificationContext context = parser.specification();
        errors += parser.getNumberOfSyntaxErrors();
        return context;
    }

    /*
     * Scans the parse tree, building a lookup table of checklist items.
     */
    private Map<String, Item> buildItems(final SpecificationContext context) {
        ItemListener itemListener = new ItemListener();
        itemListener.addErrorListener(errorListener);
        walker.walk(itemListener, context);
        errors += itemListener.getNumberOfErrors();
        warnings += itemListener.getNumberOfWarnings();
        return itemListener.getItems();
    }

    /*
     * Builds an abstract representation of checklists and checks by scanning
     * the parse tree and building the checks using the items lookup table.
     */
    private AbstractSyntaxTree buildAST(final Map<String, Item> items,
            final SpecificationContext context) {
        ChecklistListener checklistListener = new ChecklistListener(items);
        checklistListener.addErrorListener(errorListener);
        walker.walk(checklistListener, context);
        errors += checklistListener.getNumberOfErrors();
        warnings += checklistListener.getNumberOfWarnings();
        return checklistListener.getAST();
    }

}
