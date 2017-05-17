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

import java.io.IOException;
import java.nio.file.Path;

import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.flightgear.clgen.GeneratorException;
import org.flightgear.clgen.ast.AbstractSyntaxTree;
import org.flightgear.clgen.ast.Check;
import org.flightgear.clgen.ast.Checklist;

import rst.pdfbox.layout.elements.Document;
import rst.pdfbox.layout.elements.Paragraph;
import rst.pdfbox.layout.elements.VerticalSpacer;
import rst.pdfbox.layout.elements.render.RenderContext;
import rst.pdfbox.layout.elements.render.RenderListener;
import rst.pdfbox.layout.elements.render.VerticalLayoutHint;
import rst.pdfbox.layout.text.Alignment;
import rst.pdfbox.layout.text.FontDescriptor;
import rst.pdfbox.layout.text.Position;
import rst.pdfbox.layout.text.TextFlow;
import rst.pdfbox.layout.text.TextFlowUtil;
import rst.pdfbox.layout.text.TextSequenceUtil;

/**
 * Creates a PDF representation of the checklists.
 *
 * @author Richard Senior
 */
public class PdfVisitor extends AbstractVisitor implements RenderListener {

    private static final FontDescriptor normal;
    private static final FontDescriptor heading;

    private final Path outputDir;
    private final Document document;

    static {
        normal = new FontDescriptor(PDType1Font.COURIER, 12.0f);
        heading = new FontDescriptor(PDType1Font.COURIER_BOLD, 12.0f);
    }

    public PdfVisitor(final Path outputDir) {
        this.outputDir = outputDir;
        document = new Document(70, 70, 50, 100);
        document.addRenderListener(this);
    }

    @Override
    public void enter(final AbstractSyntaxTree ast) {
        try {
            Paragraph p = new Paragraph();
            p.addText("CHECKLIST", 18, PDType1Font.COURIER_BOLD);
            document.add(p, VerticalLayoutHint.CENTER);
            document.add(new VerticalSpacer(18));
        } catch (IOException e) {
            throw new GeneratorException(e);
        }
    }

    @Override
    public void exit(final AbstractSyntaxTree ast) {
        try {
            Path path = outputDir.resolve("checklists.pdf");
            document.save(path.toFile());
            System.out.println(path.toAbsolutePath().normalize().toString());
        } catch (IOException e) {
            throw new GeneratorException(e);
        }
    }

    @Override
    public void enter(final Checklist checklist) {
        try {
            Paragraph p = new Paragraph();
            p.addText(checklist.getTitle().toUpperCase(), heading.getSize(), heading.getFont());
            document.add(p);
        } catch (IOException e) {
            throw new GeneratorException(e);
        }
    }

    @Override
    public void exit(final Checklist checklist) {
        document.add(new VerticalSpacer(24));
    }

    @Override
    public void enter(final Check check) {
        try {
            Paragraph p = new Paragraph();
            p.setLineSpacing(1.5f);
            String s = String.format("%s %s %s",
                check.getItem().getName(),
                dots(check.getItem().getName(), check.getState().getName(), textWidth(normal) - 2),
                check.getState().getName()
            );
            p.addText(s, normal.getSize(), normal.getFont());
            for (String value : check.getAdditionalValues()) {
                p.setAlignment(Alignment.Right);
                p.addText(value + "\n", normal.getSize(), normal.getFont());
            }
            document.add(p);
        } catch (IOException e) {
            throw new GeneratorException(e);
        }
    }

    private int textWidth(final FontDescriptor fontDescriptor) {
        try {
            float w = TextSequenceUtil.getEmWidth(fontDescriptor);
            return (int)Math.floor(document.getPageWidth() / w);
        } catch (IOException e) {
            throw new GeneratorException(e);
        }
    }

    private String dots(final String pre, final String post, int width) {
        width -= pre.length();
        width -= post.length();
        StringBuilder sb = new StringBuilder();
        while (width-- > 0)
            sb.append('.');
        return sb.toString();
    }

    // Render Listener

    @Override
    public void beforePage(final RenderContext renderContext) throws IOException {}

    @Override
    public void afterPage(final RenderContext renderContext) throws IOException {
        String content = String.format("%s", renderContext.getPageIndex() + 1);
        TextFlow text = TextFlowUtil.createTextFlow(content, normal.getSize(), normal.getFont());
        float offset = renderContext.getPageFormat().getMarginLeft();
        offset += TextSequenceUtil.getOffset(text, renderContext.getWidth(), Alignment.Center);
        Position p = new Position(offset, 60);
        text.drawText(renderContext.getContentStream(), p, Alignment.Center, null);
    }
}
