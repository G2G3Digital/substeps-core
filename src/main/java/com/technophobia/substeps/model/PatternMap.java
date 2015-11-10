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
package com.technophobia.substeps.model;

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;

/**
 * a map of regex patterns to other things.
 * 
 * @author imoore
 * 
 */
public class PatternMap<V> {

    private final Map<Pattern, V> patternMap = new HashMap<Pattern, V>();
    private final Map<String, V> keys = new HashMap<String, V>();
    private V nullValue = null;


    public V getNullVale() {
        return nullValue;
    }


    /**
     * Adds a new pattern to the map.
     * 
     * Users of this class <em>must</em> check if the pattern has already been added
     * by using {@link containsPattern} to avoid IllegalStateException in the event that
     * the pattern has already been added to the map. 
     * 
     * @param pattern
     * @param value
     * 
     * @throws IllegalStateException - if the map already contains the specified patter.
     */
    public void put(final String pattern, final V value) throws IllegalStateException {

        if (pattern != null) {
            if (keys.containsKey(pattern)) {
                throw new IllegalStateException("");
            }
            keys.put(pattern, value);

            final Pattern p = Pattern.compile(pattern);

            patternMap.put(p, value);
        } else {
            nullValue = value;
        }
    }


    public int size() {
        return patternMap.size();
    }


    public Set<Pattern> keySet() {
        return patternMap.keySet();
    }


    public Collection<V> values() {
        return patternMap.values();
    }


    public List<V> getRelaxedValue(String sourceKey, String[] keywordPrecedence){
        // TODO - lookup in the relaxed map, based on the keyword precedence

        String baseLine = null;

        for (final String altKeyword : keywordPrecedence) {

            baseLine = StringUtils.removeStart(sourceKey, altKeyword);
            if (!baseLine.equals(sourceKey)){
                break;
            }
        }

        List<V> vals = null;
        for (final String altKeyword : keywordPrecedence) {

            vals = get(altKeyword + baseLine);
            if (!vals.isEmpty()){
                break;
            }
        }

        return vals;
    }

    public List<V> get(final String string) {
        List<V> vals = null;

        if (!Strings.isNullOrEmpty(string)) {
            final Set<Entry<Pattern, V>> entrySet = patternMap.entrySet();

            for (final Entry<Pattern, V> e : entrySet) {

                if (e.getKey().matcher(string).matches()) {

                    if (vals == null) {
                        vals = new ArrayList<V>();
                    }
                    vals.add(e.getValue());
                }
            }
        } else {
            if (nullValue != null) {
                vals = new ArrayList<V>();
                vals.add(nullValue);
            }
        }
        if (vals == null) {
            vals = Collections.emptyList();
        }

        return vals;
    }


    /**
     * @param param
     * @param i
     * 
     * @return
     */
    public V get(final String param, final int idx) {
        V rtn = null;
        if (param != null) {
            final List<V> list = this.get(param);
            if (list != null && !list.isEmpty() && list.size() > idx) {
                rtn = list.get(idx);
            }
        } else {
            rtn = nullValue;
        }
        return rtn;
    }


    /**
     * @param pattern
     * @return
     */
    public boolean containsPattern(final String pattern) {
        return keys.containsKey(pattern);
    }


    /**
     * @param newPattern
     * @return
     */
    public V getValueForPattern(final String pattern) {
        return keys.get(pattern);
    }

}
