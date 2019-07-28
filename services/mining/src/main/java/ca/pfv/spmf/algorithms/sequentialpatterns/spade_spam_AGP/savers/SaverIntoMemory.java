package ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.savers;

import java.util.Collection;

import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.dataStructures.Sequences;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.dataStructures.patterns.Pattern;

/**
 * This is an implementation of a class implementing the Saver interface. By 
 * means of these lines, the user choose to keep his patterns in the memory.
 * 
 * NOTE: This implementation saves the pattern  to a file as soon 
 * as they are found or can keep the pattern into memory, depending
 * on what the user choose.
 *
 * Copyright Antonio Gomariz Peñalver 2013
 *
 * This file is part of the SPMF DATA MINING SOFTWARE
 * (http://www.philippe-fournier-viger.com/spmf).
 *
 * SPMF is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * SPMF is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * SPMF. If not, see <http://www.gnu.org/licenses/>.
 *
 * @author agomariz
 */
public class SaverIntoMemory implements Saver{
    
    private Sequences patterns=null;
    
    /**
     * Idendicates if sequence ids should be output for each pattern
     */
    boolean outputSequenceIdentifiers = false;
    
    /**
     * Constructor
    * @param outputSequenceIdentifiers if true, the sequential identifiers will be shown
    */
    public SaverIntoMemory(boolean outputSequenceIdentifiers){
    	this.outputSequenceIdentifiers = outputSequenceIdentifiers;
        patterns = new Sequences("FREQUENT SEQUENTIAL PATTERNS");
    }
    
    /**
     * Constructor
     * @param name the name of these patterns
    * @param outputSequenceIdentifiers if true, the sequential identifiers will be shown
    */
    public SaverIntoMemory(String name,boolean outputSequenceIdentifiers){
        patterns = new Sequences(name);
        this.outputSequenceIdentifiers = outputSequenceIdentifiers;
    }
    
    /**
     * Save patterns to file
     * @param p a pattern
     */
    @Override
    public void savePattern(Pattern p) {
        patterns.addSequence(p, p.size());
    }
    
    @Override
    public void finish() {
        patterns.sort();
    }

    @Override
    public void clear() {
        patterns.clear();
        patterns=null;
    }
    
    /**
     * Print patterns
     * @return a string
     */
    @Override
    public String print() {
        return patterns.toStringToFile(outputSequenceIdentifiers);
    }
    
    /**
     * Save patterns to file
     * @param patterns a list of patterns
     */
    @Override
    public void savePatterns(Collection<Pattern> patterns) {
        for(Pattern pattern:patterns){
            this.savePattern(pattern);
        }
    }
    
}
