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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.flightgear.clgen.GeneratorException;
import org.flightgear.clgen.ast.AbstractSyntaxTree;
import org.flightgear.clgen.ast.Check;
import org.flightgear.clgen.ast.Checklist;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Creates a PDF representation of the checklists.
 *
 * @author Richard Senior
 */
public class PdfVisitor extends AbstractVisitor {

    private static final Font H1 = new Font(Font.COURIER, 14.0f, Font.BOLD);
    private static final Font H2 = new Font(Font.COURIER, 10.0f, Font.BOLD);
    private static final Font S = new Font(Font.COURIER, 8.0f, Font.NORMAL);
    private static final Font P = new Font(Font.COURIER, 10.0f, Font.NORMAL);
    private static final Font B = new Font(Font.COURIER, 10.0f, Font.BOLD);

    private static final float MARGIN = 70.0f;

    private final Document document = new Document();
    private final Footer footer = new Footer();
    private final Path filename;

    /**
     * Constructs a PDF visitor with the path to the output directory.
     *
     * @param outputDir the output directory
     */
    public PdfVisitor(final Path outputDir) {
        try {
            document.setMargins(MARGIN, MARGIN, MARGIN, MARGIN);
            filename = outputDir.resolve("checklists.pdf");
            FileOutputStream out = new FileOutputStream(filename.toFile());
            PdfWriter writer = PdfWriter.getInstance(document, out);
            writer.setPageEvent(footer);
            document.open();
        } catch (FileNotFoundException | DocumentException e) {
            document.close();
            throw new GeneratorException(e);
        }
    }

    @Override
    public void enter(final AbstractSyntaxTree ast) {
        try {
            String title = ast.getProject() != null ? ast.getProject() : "Checklists";
            Paragraph t = new Paragraph(title.toUpperCase(), H1);
            t.setSpacingAfter(10.0f);
            t.setAlignment(Element.ALIGN_LEFT);
            document.add(t);
            footer.date = new Date();
        } catch (DocumentException e) {
            document.close();
            throw new GeneratorException(e);
        }
    }

    @Override
    public void exit(final AbstractSyntaxTree ast) {
        document.close();
        System.out.println(filename.toAbsolutePath().normalize().toString());
    }

    @Override
    public void enter(final Checklist checklist) {
        try {
            Paragraph p = new Paragraph(checklist.getTitle().toUpperCase(), H2);
            p.setSpacingBefore(20.0f);
            document.add(p);
        } catch (DocumentException e) {
            document.close();
            throw new GeneratorException(e);
        }
    }

    @Override
    public void enter(final Check check) {
        try {
            String i = check.getItem() != null ? nvl(check.getItem().getName()) : "";
            String s = check.getState() != null ? nvl(check.getState().getName()) : "";
            String line = String.format("%s %s %s", i, dots(i, s, normalTextWidth() - 2), s);
            Paragraph p = new Paragraph(line, empty(s) ? B : P);
            p.setSpacingBefore(empty(s) ? 10.0f : 5.0f);
            document.add(p);
            for (String value : check.getAdditionalValues()) {
                p = new Paragraph(value, P);
                p.setSpacingBefore(5.0f);
                p.setAlignment(Element.ALIGN_RIGHT);
                document.add(p);
            }
        } catch (DocumentException e) {
            document.close();
            throw new GeneratorException(e);
        }
    }

    // Other methods

    private int normalTextWidth() {
        BaseFont b = PdfVisitor.P.getCalculatedBaseFont(false);
        float w = b.getWidthPoint(".", PdfVisitor.P.getSize());
        return (int)Math.floor((document.getPageSize().getWidth() - MARGIN * 2) / w);
    }

    private String dots(final String pre, final String post, int width) {
        // Empty checks may be used as subtitles or spacers, in which
        // case no dots are required
        if (empty(pre) || empty(post))
            return "";
        width -= pre.length();
        width -= post.length();
        StringBuilder sb = new StringBuilder();
        while (width-- > 0)
            sb.append('.');
        return sb.toString();
    }

    private boolean empty(final String s) {
        return s == null || s.trim().length() == 0;
    }

    private String nvl(final String s) {
        return s != null ? s : "";
    }

    // Page Event Helper

    private static final class Footer extends PdfPageEventHelper {

        Date date;

        private String formattedDate() {
            DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
            return df.format(date);
        }

        @Override
        public void onEndPage(final PdfWriter writer, final Document document) {
            Phrase datestamp = new Phrase(formattedDate(), S);
            Phrase pageNo = new Phrase(Integer.toString(document.getPageNumber()), P);
            float r = document.getPageSize().getWidth() - MARGIN;
            float y = MARGIN / 2;
            ColumnText.showTextAligned(
                writer.getDirectContent(),
                Element.ALIGN_RIGHT,
                pageNo, r, y, 0f
            );
            ColumnText.showTextAligned(
                writer.getDirectContent(),
                Element.ALIGN_LEFT,
                datestamp, MARGIN, y, 0f
            );
        }

    }

}
