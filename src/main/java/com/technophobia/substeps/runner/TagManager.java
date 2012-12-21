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
package com.technophobia.substeps.runner;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.technophobia.substeps.execution.AbstractExecutionNodeVisitor;
import com.technophobia.substeps.execution.node.IExecutionNode;
import com.technophobia.substeps.execution.node.TaggedNode;

/**
 * 
 * @author imoore
 * 
 */
public class TagManager extends AbstractExecutionNodeVisitor<Boolean> {

    private final Logger log = LoggerFactory.getLogger(TagManager.class);

    private static final String IGNORE_TAG_PREFIX = "--";

    private boolean acceptAll = true;

    private Set<String> acceptedTags = null;
    private Set<String> excludedTags = null;

    public TagManager(final String tagList) {

        acceptedTags = new HashSet<String>();
        excludedTags = new HashSet<String>();

        if (tagList != null && tagList.length() > 0) {
            log.debug("running with tags: " + tagList);

            // got some tag values
            acceptAll = false;

            log.debug("Creating tagManager with tags " + tagList);
            // parse, process and store
            parseTextValue(tagList);
        }
        insertCommandLineTags();
    }

    public void insertTagOverlay(final String textValue) {
        log.debug("Inserting tag overlays " + textValue);
        final String[] split = toArray(textValue);

        for (final String s : split) {
            final String normalised = normaliseTag(s);
            acceptedTags.remove(normalised);
            excludedTags.remove(normalised);
            insertTag(s);
        }
    }

    /**
     * @param annotationValue
     */
    private void parseTextValue(final String annotationValue) {

        final String[] split = toArray(annotationValue);

        for (final String s : split) {
            insertTag(s);
        }
    }

    private void insertTag(final String tag) {
        if (tag.startsWith(IGNORE_TAG_PREFIX)) {
            excludedTags.add(normaliseTag(tag));
        } else {
            acceptedTags.add(tag);
        }
    }

    // public boolean acceptTaggedScenario(final String... tags) {
    //
    // Set<String> strList = null;
    //
    // if (tags != null) {
    // for (final String s : tags) {
    // if (s != null) {
    // if (strList == null) {
    // strList = new HashSet<String>();
    // }
    // strList.add(s);
    // }
    // }
    // }
    // return acceptTaggedScenario(strList);
    // }

    @Override
    public Boolean visit(IExecutionNode node) {

        return acceptTaggedScenario(Collections.<String> emptySet());
    }

    @Override
    public Boolean visit(TaggedNode taggedNode) {

        return acceptTaggedScenario(taggedNode.getTags());
    }

    public boolean isApplicable(final IExecutionNode node) {

        return node.dispatch(this);
    }

    // passed a set of tags, works out if we should run this feature or not
    public boolean acceptTaggedScenario(final Set<String> tags) {

        if (acceptAll || (acceptedTags.isEmpty() && excludedTags.isEmpty())) {
            return true;
        } else if (acceptedTags.size() > 0 && (tags == null || tags.isEmpty())) {
            return false;
        } else if (containsAny(tags, excludedTags)) {
            return false;
        } else {
            return tags == null || tags.containsAll(acceptedTags);
        }
    }

    private <T> boolean containsAny(final Collection<T> col1, final Collection<T> col2) {
        if (col1 != null && col2 != null) {
            if (col1.size() > col2.size()) {
                for (final T item : col1) {
                    if (col2.contains(item)) {
                        return true;
                    }
                }
            } else {
                for (final T item : col2) {
                    if (col1.contains(item)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private String[] toArray(final String annotationValue) {
        final String[] split = annotationValue.split("[ \\s]");
        final String[] results = new String[split.length];
        for (int i = 0; i < split.length; i++) {
            results[i] = split[i].trim();
        }
        return split;
    }

    public Set<String> getAcceptedTags() {
        return acceptedTags;
    }

    private String normaliseTag(final String tag) {
        if (tag.startsWith(IGNORE_TAG_PREFIX)) {
            return tag.substring(2);
        }
        return tag;
    }

    private void insertCommandLineTags() {
        final String tagParams = System.getProperty("tags");
        if (tagParams != null) {
            insertTagOverlay(tagParams);
        }

    }

}
