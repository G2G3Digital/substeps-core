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
package com.technophobia.substeps.glossary;

/**
 * @author ian
 * 
 */
public class StepDescriptor {
    private String expression;

    private String example;
    private String section;
    private String description;


    // TODO - add parameter information.

    /**
     * @return the example
     */
    public String getExample() {
        return example;
    }


    /**
     * @param example
     *            the example to set
     */
    public void setExample(final String example) {
        this.example = example;
    }


    /**
     * @return the section
     */
    public String getSection() {
        return section;
    }


    /**
     * @param section
     *            the section to set
     */
    public void setSection(final String section) {
        this.section = section;
    }


    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }


    /**
     * @param description
     *            the description to set
     */
    public void setDescription(final String description) {
        this.description = description;
    }


    /**
     * @return the expression
     */
    public String getExpression() {
        return expression;
    }


    /**
     * @param expression
     *            the expression to set
     */
    public void setExpression(final String expression) {
        this.expression = expression;
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((expression == null) ? 0 : expression.hashCode());
        return result;
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StepDescriptor other = (StepDescriptor) obj;
        if (expression == null) {
            if (other.expression != null) {
                return false;
            }
        } else if (!expression.equals(other.expression)) {
            return false;
        }
        return true;
    }

}
