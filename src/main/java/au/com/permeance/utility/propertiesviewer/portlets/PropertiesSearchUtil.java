/**
 * Copyright (C) 2013 Permeance Technologies
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package au.com.permeance.utility.propertiesviewer.portlets;

import com.liferay.portal.kernel.util.PropsUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This class provides utility methods
 * 
 * @author Chun Ho <chun.ho@permeance.com.au>
 * 
 */
public class PropertiesSearchUtil {

    public static Properties searchProperties(final Properties result, final String term, final Properties poolToSearch) {
        Properties toReturn = null;
        if (result == null) {
            toReturn = new Properties();
        } else {
            toReturn = result;
        }
        if (term == null || term.length() == 0) {
            toReturn.putAll(poolToSearch);
        } else {
            for (Object key : poolToSearch.keySet()) {
                if (key.toString().toLowerCase().contains(term)) {
                    toReturn.put(key, poolToSearch.getProperty(key.toString()));
                } else {
                    String value = poolToSearch.getProperty(key.toString());
                    if (value != null && value.toLowerCase().contains(term)) {
                        toReturn.put(key, poolToSearch.getProperty(key.toString()));
                    }
                }
            }
        }
        return toReturn;
    }

    public static Properties searchSystemProperties(final Properties result, final String term) {
        return searchProperties(result, term, System.getProperties());
    }

    public static Properties searchPortalProperties(final Properties result, final String term) {
        return searchProperties(result, term, PropsUtil.getProperties());
    }

    public static Properties createSortedProperties() {
        return new Properties() {
            // override methods so output properties file/display is in sorted alphabetical list

            private static final long serialVersionUID = 2666507456340160881L;

            @Override
            public Set<Object> keySet() {
                return Collections.unmodifiableSet(new TreeSet<Object>(super.keySet()));
            }

            @Override
            public synchronized Enumeration<Object> keys() {
                return Collections.enumeration(new TreeSet<Object>(super.keySet()));
            }

            @Override
            public Set<java.util.Map.Entry<Object, Object>> entrySet() {
                SortedSet<java.util.Map.Entry<Object, Object>> toReturn = new TreeSet<Map.Entry<Object, Object>>(
                        new Comparator<Map.Entry<Object, Object>>() {

                            @Override
                            public int compare(java.util.Map.Entry<Object, Object> o1, java.util.Map.Entry<Object, Object> o2) {
                                return o1.getKey().toString().compareTo(o2.getKey().toString());
                            }

                        });

                toReturn.addAll(super.entrySet());
                return toReturn;
            }
        };
    }
}
