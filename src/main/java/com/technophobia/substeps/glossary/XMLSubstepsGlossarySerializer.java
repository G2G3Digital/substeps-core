package com.technophobia.substeps.glossary;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

/*
 *	Copyright Technophobia Ltd 2012
 *
 *   This file is part of Substeps.
 *
 *    Substeps is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    Substeps is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with Substeps.  If not, see <http://www.gnu.org/licenses/>.
 */
public class XMLSubstepsGlossarySerializer {

    public static final String XML_FILE_NAME = "substeps-metainfo.xml";

    private final Logger log = LoggerFactory.getLogger(XMLSubstepsGlossarySerializer.class);


    public String toXML(final List<StepImplementationsDescriptor> classStepTags) {

        final XStream xstream = new XStream();
        return xstream.toXML(classStepTags);
    }


    public List<StepImplementationsDescriptor> fromXML(final InputStream inputStream) {
        final XStream xstream = new XStream();
        return (List<StepImplementationsDescriptor>) xstream.fromXML(inputStream);
    }


    public List<StepImplementationsDescriptor> loadStepImplementationsDescriptorFromJar(
            final JarFile jarFileForClass) {

        List<StepImplementationsDescriptor> classStepTagList = null;

        final ZipEntry entry = jarFileForClass
                .getEntry(XMLSubstepsGlossarySerializer.XML_FILE_NAME);

        if (entry != null) {

            try {
                final InputStream is = jarFileForClass.getInputStream(entry);

                classStepTagList = fromXML(is);

            } catch (final IOException e) {
                log.error("Error loading from jarfile: ", e);
            }
        } else {
            log.error("couldn't locate file in jar: " + XMLSubstepsGlossarySerializer.XML_FILE_NAME);
        }

        return classStepTagList;
    }

}
