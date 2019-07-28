package ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.dataStructures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.dataStructures.patterns.Pattern;

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
    
    /**
     * Get patterns as string
     * @param outputSequenceIdentifiers if true, the sequential identifiers will be shown
     * @return a string
     */
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
            Collections.sort(nivel);
        }
    }

    public void clear() {
        for(List<Pattern> nivel:levels){
            nivel.clear();
        }
        levels.clear();
        levels=null;
    }

    public void addSequences(List<Pattern> list) {
        for(Pattern p:list){
            addSequence(p, p.size());
        }
    }

}
