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
import org.apache.velocity.exception.ResourceNotFoundException;
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

    private Template template;

    /**
     * Constructs a license.
     * <p>
     * The license attempt to load a Velocity template called 'license.vm'
     * from the working directory. If not found, it loads a template based on
     * the variant parameter from the classpath.
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
        p.put("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogChute");
        VelocityEngine velocity = new VelocityEngine(p);
        try {
            template = velocity.getTemplate("license.vm");
        } catch (ResourceNotFoundException e) {
            p = new Properties();
            p.put(RuntimeConstants.RESOURCE_LOADER, "classpath");
            p.put("classpath.resource.loader.class",
                    ClasspathResourceLoader.class.getName()
            );
            velocity = new VelocityEngine(p);
            template = velocity.getTemplate(variant + ".vm");
        }
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

    /**
     * @return the author of the checklists
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author the author of the checklists
     */
    public void setAuthor(final String author) {
        this.author = author;
    }

    /**
     * @return the title of the document to which this license is attached
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title of the document to which this license is attached
     */
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
