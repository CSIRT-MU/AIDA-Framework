/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.pfv.spmf.algorithms.sequentialpatterns.prefixSpan_AGP.savers;

import ca.pfv.spmf.algorithms.sequentialpatterns.prefixSpan_AGP.items.Sequences;
import ca.pfv.spmf.algorithms.sequentialpatterns.prefixSpan_AGP.items.patterns.Pattern;



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
     * if true, sequences ids will be output for each pattern
     */
    boolean outputSequenceIdentifiers;
    
    /**
     * Constructor
     * @param outputSequenceIdentifiers if true, sequences ids will be output for each pattern
     */
    public SaverIntoMemory(boolean outputSequenceIdentifiers){
        patterns = new Sequences("FREQUENT SEQUENTIAL PATTERNS");
        this.outputSequenceIdentifiers = outputSequenceIdentifiers;
    }
    
    public SaverIntoMemory(String name,boolean outputSequenceIdentifiers){
        patterns = new Sequences(name);
        this.outputSequenceIdentifiers = outputSequenceIdentifiers;
    }
    
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
    
    @Override
    public String print() {
        return patterns.toStringToFile(outputSequenceIdentifiers);
    }
    
}
