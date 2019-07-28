package ca.pfv.spmf.algorithms.sequentialpatterns.clasp_AGP.dataStructures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ca.pfv.spmf.algorithms.sequentialpatterns.clasp_AGP.dataStructures.abstracciones.Abstraction_Qualitative;
import ca.pfv.spmf.algorithms.sequentialpatterns.clasp_AGP.dataStructures.abstracciones.ItemAbstractionPair;
import ca.pfv.spmf.algorithms.sequentialpatterns.clasp_AGP.dataStructures.patterns.Pattern;

/** Inspired in SPMF
 * This class implements a list of frequent sequence lists (or frequent 
 * pattern lists) that it is organized by levels.
 * That level contains all of sequences that have a concrete number of items.
 * Therefore, we allocate 1-sequences in level 1, 2-sequences in level 2,
 * and so forth...
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
public class Sequences {

    public List<List<Pattern>> levels = new ArrayList<List<Pattern>>();
    public int numberOfFrequentSequences = 0;
    private String name;

    public Sequences(String name) {
        levels.add(new ArrayList<Pattern>());
        this.name = name;
    }

    @Override
    public String toString() {
        StringBuilder r = new StringBuilder(200);
        r.append(name);
        int levelCount = 0;
        for (List<Pattern> level : levels) {
            r.append("\n***Level ").append(levelCount).append("***\n\n");
            for (Pattern sequence : level) {
                r.append(sequence.toString());
                r.append('\n');
            }
            levelCount++;
        }
        return r.toString();
    }
    
    public String toStringToFile(boolean outputSequenceIdentifiers) {
        StringBuilder r = new StringBuilder(200);
        int levelCount = 0;
        if (levels != null) {
            for (List<Pattern> level : levels) {
                r.append("\n***Level ").append(levelCount).append("***\n\n");
                for (Pattern sequence : level) {
                    r.append(sequence.toStringToFile(outputSequenceIdentifiers));
                    r.append('\n');
                }
                levelCount++;
            }
        }
        return r.toString();
    }

    public void addSequence(Pattern sequence, int k) {
        while (levels.size() <= k) {
            levels.add(new ArrayList<Pattern>());
        }
        levels.get(k).add(sequence);
        numberOfFrequentSequences++;
    }

    public List<Pattern> getLevel(int index) {
        return levels.get(index);
    }

    public int getLevelCount() {
        return levels.size();
    }

    public List<List<Pattern>> getLevels() {
        return levels;
    }

    public int size() {
        int total = 0;
        for (List<Pattern> level : levels) {
            total = total + level.size();
        }
        return total;
    }

    public void sort() {
        for (List<Pattern> nivel : levels) {
            Collections.sort(nivel,new patternComparator());
        }
    }

    public void clear() {
        for (List<Pattern> nivel : levels) {
            nivel.clear();
        }
        levels.clear();
        levels = null;
    }

    private static class patternComparator implements Comparator<Pattern> {

        public patternComparator() {
        }

        @Override
        public int compare(Pattern p1, Pattern p2) {
            for (int i = 0; i < p1.size(); i++) {
                ItemAbstractionPair par1=p1.getIthElement(i);
                ItemAbstractionPair par2=p2.getIthElement(i);
                int comparacion = par1.getItem().compareTo(par2.getItem());
                if (comparacion != 0) {
                    return comparacion;
                }else{
                    Abstraction_Qualitative abs1=(Abstraction_Qualitative)(par1.getAbstraction());
                    Abstraction_Qualitative abs2=(Abstraction_Qualitative)(par2.getAbstraction());
                    if(abs1.hasEqualRelation() && !abs2.hasEqualRelation())
                        return -1;
                    else if(!abs1.hasEqualRelation() && abs2.hasEqualRelation())
                        return 1;
                }
            }
            return 0;
        }
    }
}
