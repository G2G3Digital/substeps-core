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

import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class StepInitPoc {

    private final Node root = new Node(null);

    private void addPhrase(Character... chars) {

        Node parentNode = root;

        for (Character c : chars) {

            Node newNode = new Node(c);

            Node existingNode = root.findNodeWith(c);

            if (existingNode != null) {

                newNode = existingNode;

                if (existingNode.hasChild(parentNode)) {
                    throw new RuntimeException("Invalid");
                }

                if (!existingNode.hasParent(parentNode)) {

                    existingNode.parentNode.removeChild(existingNode);
                    parentNode.addChild(existingNode);
                }

            } else {
                parentNode.addChild(newNode);
            }

            parentNode = newNode;
        }

    }

    public List<Node> getOrderedNodes() {

        List<Node> allNodes = root.linerize();
        return allNodes.subList(1, allNodes.size());
    }

    @Test
    public void testOrderer() {

        StepInitPoc orderer = new StepInitPoc();

        orderer.addPhrase('A', 'B', 'C', 'D');
        orderer.addPhrase('B', 'E', 'F');
        orderer.addPhrase('E', 'F');
        orderer.addPhrase('D', 'F');
        orderer.addPhrase('C', 'Y', 'D', 'F');

        System.out.println(orderer.getOrderedNodes());
    }

}

class Node {

    Node parentNode;
    Set<Node> childNodes = Sets.newHashSet();
    Character c;

    public Node(Character c) {
        this.c = c;
    }

    public boolean hasParent(Node parent) {

        if (this.parentNode == null && parent != null) {

            return false;
        }

        if (this.parentNode == parent) {
            return true;
        } else {
            return this.parentNode.hasParent(parent);
        }

    }

    public void removeChild(Node child) {

        this.childNodes.remove(child);
        child.parentNode = null;
    }

    public void addChild(Node child) {

        this.childNodes.add(child);
        child.parentNode = this;
    }

    public boolean hasChild(Node child) {

        if (this.childNodes.contains(child)) {
            return true;
        }

        for (Node myChildNode : childNodes) {
            if (myChildNode.hasChild(child)) {
                return true;
            }
        }

        return false;
    }

    public Node findNodeWith(Character c) {

        if (this.c != null && this.c.equals(c)) {
            return this;
        } else {
            for (Node childNode : childNodes) {
                Node fromChild = childNode.findNodeWith(c);
                if (fromChild != null) {
                    return fromChild;
                }
            }

            return null;
        }
    }

    public List<Node> linerize() {

        int index = 0;

        List<Node> allNodes = Lists.newArrayList(this);

        while (index < allNodes.size()) {

            Node node = allNodes.get(index);

            allNodes.addAll(node.childNodes);

            index++;
        }

        return allNodes;
    }

    @Override
    public String toString() {
        return this.c == null ? "" : this.c.toString();
    }
}