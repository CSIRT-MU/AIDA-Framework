/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.pfv.spmf.algorithms.sequentialpatterns.gsp_AGP.items;

import java.util.Comparator;

/**
 * Implementation of an item.
 * This implementation is generic in order to be able to manage any kind of item (string, integer, ...)
 * 
 * Copyright Antonio Gomariz Peñalver 2013
 * 
 * This file is part of the SPMF DATA MINING SOFTWARE
 * (http://www.philippe-fournier-viger.com/spmf).
 *
 * SPMF is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SPMF is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SPMF.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * @author agomariz
 */

public class Item<T extends Comparable> implements Comparable<Item> {

    /**
     * inner representation. Implemented as a generic element
     */
    private T id;

    /**
     * Standard constructor
     * @param id the item's content
     */
    public Item(T id) {
        this.id = id;
    }

    /**
     * get the item's content.
     * @return the item content
     */
    public T getId() {
        return id;
    }

    @Override
    public String toString() {
        return "" + getId();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Item) {
            Item item = (Item) object;
            if ((item.getId().equals(this.getId()))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public int compareTo(Item i) {
        return -id.compareTo(i.getId());
        //return id.compareTo(i.getId());
    }
}

class itemComparator implements Comparator<Item>{

    public int compare(Item o1, Item o2) {
        int value= o1.compareTo(o2);
        return -value;
    }

}
