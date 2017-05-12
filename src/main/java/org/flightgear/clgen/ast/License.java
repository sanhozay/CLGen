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

import java.io.StringWriter;
import java.util.Calendar;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.flightgear.clgen.CLGenProperties;

/**
 * License builder.
 * <p>
 * Creates a license text block for inclusion in a header comment.
 *
 * @author Richard Senior
 */
public class License {

    private String author;
    private String title;

    private final Template template;

    /**
     * Constructs a license.
     * <p>
     * License variants are implemented using Velocity templates on the
     * classpath. To add a new license, create a template in the
     * src/main/resources source directory based on the gpl2.vm template.
     * <p>
     * Supported placeholders are:
     * <ul>
     * <li>name - the name or title of the file
     * <li>year - the current year
     * <li>author - the author and copyright holder
     * <li>version - the CLGen version
     * </ul>
     *
     * @param variant the template filename without extension, e.g. "gpl2".
     */
    public License(final String variant) {
        Properties p = new Properties();
        p.put(RuntimeConstants.RESOURCE_LOADER, "classpath");
        p.put("classpath.resource.loader.class",
            ClasspathResourceLoader.class.getName()
        );
        VelocityEngine velocity = new VelocityEngine(p);
        template = velocity.getTemplate(variant + ".vm");
    }

    /**
     * Gets the license text.
     * <p>
     * Note that null is returned if either the name field or the
     * author field is null.
     *
     * @return the license as a string
     */
    public String getText() {
        if (title == null || author == null)
            return null;
        StringWriter sw = new StringWriter();
        template.merge(velocityContext(), sw);
        return sw.toString();
    }

    // Accessors

    public String getAuthor() {
        return author;
    }

    public void setAuthor(final String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    // Other methods

    private VelocityContext velocityContext() {
        CLGenProperties properties = CLGenProperties.getInstance();
        Calendar calendar = Calendar.getInstance();
        VelocityContext ctx = new VelocityContext();
        ctx.put("title", title);
        ctx.put("year", Integer.toString(calendar.get(Calendar.YEAR)));
        ctx.put("author", author);
        ctx.put("version", properties.getVersion());
        return ctx;
    }

}
