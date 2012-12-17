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

import java.util.List;
import java.util.Set;

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
 * 
 * 
 * @author rbarefield
 */
public class InitialisationClassOrderer {

    private final InitialisationClassNode root = new InitialisationClassNode(null);

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

                if (!existingNode.hasParent(parentNode)) {

                    existingNode.getParentNode().removeChild(existingNode);
                    parentNode.addChild(existingNode);
                }

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

    private InitialisationClassNode parentNode;
    private final Set<InitialisationClassNode> childNodes = Sets.newHashSet();
    private final Class<?> initialisationClass;

    public InitialisationClassNode(Class<?> initialisationClass) {

        this.initialisationClass = initialisationClass;
    }

    public boolean hasParent(InitialisationClassNode parent) {

        if (this.parentNode == null && parent != null) {

            return false;
        }

        if (this.parentNode == parent) {
            return true;
        } else {
            return this.parentNode.hasParent(parent);
        }

    }

    public InitialisationClassNode getParentNode() {

        return this.parentNode;
    }

    public Class<?> getInitialisationClass() {
        return initialisationClass;
    }

    public void removeChild(InitialisationClassNode child) {

        this.childNodes.remove(child);
        child.parentNode = null;
    }

    public void addChild(InitialisationClassNode child) {

        this.childNodes.add(child);
        child.parentNode = this;
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

    public List<InitialisationClassNode> linerize() {

        int index = 0;

        List<InitialisationClassNode> allNodes = Lists.newArrayList(this);

        while (index < allNodes.size()) {

            InitialisationClassNode node = allNodes.get(index);

            allNodes.addAll(node.childNodes);

            index++;
        }

        return allNodes;
    }

    @Override
    public String toString() {
        return this.initialisationClass == null ? "root" : this.initialisationClass.toString();
    }

}