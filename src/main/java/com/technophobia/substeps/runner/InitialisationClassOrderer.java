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
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.technophobia.substeps.model.exception.SubstepsConfigurationException;

/**
 * 
 * Responsible for determining the running order of initialisation classes.
 * 
 * To do this this class builds a dependency tree of initialisation classes, as
 * it adds a new initialisation class to the tree it ensures it is not at the
 * same time a parent and child of another node. Such a condition is equivalent
 * to a class which must be executed both before and after another.
 * 
 * Once this graph is fully built it is converted into a list by traversing the
 * graph only adding nodes whose parents have already been added, it keeps doing
 * this until all nodes have been added.
 * 
 * @author rbarefield
 */
public class InitialisationClassOrderer {

    private final InitialisationClassNode root = new InitialisationClassNode(null);

    protected static final Logger log = LoggerFactory.getLogger(InitialisationClassOrderer.class);

    public void addOrderedInitialisationClasses(Class<?>... classes) {

        InitialisationClassNode parentNode = root;

        for (Class<?> initClass : classes) {

            InitialisationClassNode newNode = new InitialisationClassNode(initClass);

            InitialisationClassNode existingNode = root.findNodeWith(initClass);

            if (existingNode != null) {

                newNode = existingNode;

                if (existingNode.hasChild(parentNode)) {

                    throw new SubstepsConfigurationException("The order is invalid as "
                            + existingNode.getInitialisationClass().getName() + " must come before and after "
                            + parentNode.getInitialisationClass().getName());
                }

                parentNode.addChild(existingNode);

            } else {
                parentNode.addChild(newNode);
            }

            parentNode = newNode;
        }

    }

    public List<Class<?>> getOrderedList() {

        List<Class<?>> linerizeClasses = Lists.newArrayList();
        List<InitialisationClassNode> allNodes = root.linerize();
        for (InitialisationClassNode node : allNodes.subList(1, allNodes.size())) {

            linerizeClasses.add(node.getInitialisationClass());
        }

        return linerizeClasses;
    }

}

class InitialisationClassNode {

    private final Set<InitialisationClassNode> parentNodes = Sets.newHashSet();
    private final Set<InitialisationClassNode> childNodes = Sets.newHashSet();
    private final Class<?> initialisationClass;

    public InitialisationClassNode(Class<?> initialisationClass) {

        this.initialisationClass = initialisationClass;
    }

    public boolean hasParent(InitialisationClassNode parent) {

        return getAllParents().contains(parent);

    }

    private Collection<InitialisationClassNode> getAllParents() {

        List<InitialisationClassNode> parents = Lists.newArrayList(this);

        int i = 0;
        while (i < parents.size()) {

            InitialisationClassNode node = parents.get(i);

            Set<InitialisationClassNode> parentsOfNodes = node.parentNodes;
            for (InitialisationClassNode parentOfNode : parentsOfNodes) {
                if (!parents.contains(parentOfNode)) {
                    parents.add(parentOfNode);
                }
            }
            i++;
        }

        return parents;
    }

    public Class<?> getInitialisationClass() {
        return initialisationClass;
    }

    public void removeChild(InitialisationClassNode child) {

        this.childNodes.remove(child);
        child.parentNodes.remove(this);
    }

    public void addChild(InitialisationClassNode child) {

        this.childNodes.add(child);
        child.parentNodes.add(this);
    }

    public boolean hasChild(InitialisationClassNode child) {

        if (this.childNodes.contains(child)) {
            return true;
        }

        for (InitialisationClassNode myChildNode : childNodes) {
            if (myChildNode.hasChild(child)) {
                return true;
            }
        }

        return false;
    }

    public InitialisationClassNode findNodeWith(Class<?> initialisationClass) {

        if (this.initialisationClass != null && this.initialisationClass == initialisationClass) {
            return this;
        } else {
            for (InitialisationClassNode childNode : childNodes) {
                InitialisationClassNode fromChild = childNode.findNodeWith(initialisationClass);
                if (fromChild != null) {
                    return fromChild;
                }
            }

            return null;
        }
    }

    private boolean hasParentNotIn(Collection<InitialisationClassNode> nodes) {

        for (InitialisationClassNode parent : parentNodes) {
            if (!nodes.contains(parent)) {
                return true;
            }
        }
        return false;
    }

    public boolean addChildren(List<InitialisationClassNode> nodes) {

        boolean complete = true;

        for (InitialisationClassNode child : childNodes) {

            if (!child.hasParentNotIn(nodes)) {

                if (!nodes.contains(child)) {

                    nodes.add(child);
                }
                complete &= child.addChildren(nodes);
            }
        }

        return complete;
    }

    public List<InitialisationClassNode> linerize() {

        List<InitialisationClassNode> allNodes = Lists.newArrayList();

        allNodes.add(this);

        int safetyCount = 0;

        while (!this.addChildren(allNodes)) {
            safetyCount++;
            if (safetyCount > 100) {
                String message = "Unable to resolve class initialisation order, please log this as a bug with substeps";
                InitialisationClassOrderer.log.error(message);
                throw new RuntimeException(message);
            }
        }

        return allNodes;

    }

    @Override
    public String toString() {
        return this.initialisationClass == null ? "root" : this.initialisationClass.toString();
    }

}