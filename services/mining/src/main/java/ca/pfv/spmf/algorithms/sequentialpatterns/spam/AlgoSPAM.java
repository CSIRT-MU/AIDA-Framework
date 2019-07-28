package ca.pfv.spmf.algorithms.sequentialpatterns.spam;
/* This file is copyright (c) 2008-2013 Philippe Fournier-Viger
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
* You should have received a copy of the GNU General Public License along with
* SPMF. If not, see <http://www.gnu.org/licenses/>.
*/

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ca.pfv.spmf.patterns.itemset_list_integers_without_support.Itemset;
import ca.pfv.spmf.tools.MemoryLogger;

/*** 
 * This is an implementation of the SPAM algorithm. 
 * <br/><br/>
 * The SPAM algorithm was originally described in this paper:
 * <br/><br/>
 * 
 *     Jay Ayres, Johannes Gehrke, Tomi Yiu, and Jason Flannick. Sequential PAttern Mining Using Bitmaps. 
 *     In Proceedings of the Eighth ACM SIGKDD International Conference on Knowledge Discovery and Data Mining. 
 *     Edmonton, Alberta, Canada, July 2002.
 * <br/><br/>
 * 
 * I tried to do what is indicated in that paper but some optimizations are not described with enough details in the paper.
 * So my implementation does not include these optimizations for example:
 * - lookup tables for bitmaps
 * - compression of bitmaps.
 * 
 * My implementation allows some additional constraints such as setting the maximum pattern length and maximum gap between
 * itemsets.
 *
*  @see Bitmap
*  @see Prefix
* @author Philippe Fournier-Viger
 */

public class AlgoSPAM{
		
	/** for statistics */
	private long startTime;
	private long endTime;
	private int patternCount;
	
	 /**  minsup */
	private int minsup = 0;

    /** object to write to a file */
    BufferedWriter writer = null;
    
    /** Vertical database */
    Map<Integer, Bitmap> verticalDB = new HashMap<Integer, Bitmap>();
    
    /** List indicating the number of bits per sequence */
    List<Integer> sequencesSize = null;
    
    /** the last bit position that is used in bitmaps */
    int lastBitIndex = 0;  
    
	/** maximum pattern length in terms of item count */
	private int minimumPatternLength = 0;
    /** maximum pattern length in terms of item count */
    private int maximumPatternLength = 1000;
	
	/** the max gap between two itemsets of a pattern. It is an optional parameter that the user can set. */
	private int maxGap = Integer.MAX_VALUE;
	
	/** Optional parameter to decide whether sequence identifiers should be shown in the output for each pattern found */
	private boolean outputSequenceIdentifiers; 
		
	/**
	 * Default constructor
	 */
	public AlgoSPAM(){
	}

	/**
	 * Method to run the algorithm
	 * @param input  path to an input file
	 * @param outputFilePath path for writing the output file
	 * @param minsupRel the minimum support as a relative value 
	 * @throws IOException exception if error while writing the file or reading
	 */
	public void runAlgorithm(String input, String outputFilePath, double minsupRel) throws IOException {
		// create an object to write the file
		writer = new BufferedWriter(new FileWriter(outputFilePath)); 
		// initialize the number of patterns found
		patternCount =0; 
		// to log the memory used
		MemoryLogger.getInstance().reset(); 
		
		// record start time
		startTime = System.currentTimeMillis(); 
		// RUN THE ALGORITHM
		spam(input, minsupRel); 
		// record end time
		endTime = System.currentTimeMillis(); 
		// close the file
		writer.close(); 
	}
	
	/**
	 * This is the main method for the SPAM algorithm
	 * @param an input file
	 * @param minsupRel the minimum support as a relative value
	 * @throws IOException 
	 */
	private void spam(String input, double minsupRel) throws IOException{
		// the structure to store the vertical database
		// key: an item    value : bitmap
		verticalDB = new HashMap<Integer, Bitmap>();
		
		// STEP 0: SCAN THE DATABASE TO STORE THE FIRST BIT POSITION OF EACH SEQUENCE 
		// AND CALCULATE THE TOTAL NUMBER OF BIT FOR EACH BITMAP
		sequencesSize = new ArrayList<Integer>();
		lastBitIndex =0; // variable to record the last bit position that we will use in bitmaps
		try {
			// read the file
			FileInputStream fin = new FileInputStream(new File(input));
			BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
			String thisLine;
			int bitIndex =0;
			// for each line (sequence) in the file until the end
			while ((thisLine = reader.readLine()) != null) {
				// if the line is  a comment, is  empty or is a
				// kind of metadata
				if (thisLine.isEmpty() == true ||
						thisLine.charAt(0) == '#' || thisLine.charAt(0) == '%'
								|| thisLine.charAt(0) == '@') {
					continue;
				}
				
				// record the length of the current sequence (for optimizations)
				sequencesSize.add(bitIndex);
				// split the sequence according to spaces into tokens
				for(String token:  thisLine.split(" ")){
					// if it is not an itemset separator
					if(token.equals("-1")){ // indicate the end of an itemset
						// increase the number of bits that we will need for each bitmap
						bitIndex++;
					}
				}
			}
			// record the last bit position for the bitmaps
			lastBitIndex = bitIndex -1;
			reader.close(); // close the input file
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Calculate the absolute minimum support 
		// by multipling the percentage with the number of
		// sequences in this database
		minsup = (int)Math.ceil((minsupRel * sequencesSize.size()));
		if(minsup ==0){
			minsup =1;
		}
		
		// STEP1: SCAN THE DATABASE TO CREATE THE BITMAP VERTICAL DATABASE REPRESENTATION
		try {
			FileInputStream fin = new FileInputStream(new File(input));
			BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
			String thisLine;
			int sid =0; // to know which sequence we are scanning
			int tid =0;  // to know which itemset we are scanning
			
			// for each line (sequence) from the input file
			while ((thisLine = reader.readLine()) != null) {
				// if the line is  a comment, is  empty or is a
				// kind of metadata
				if (thisLine.isEmpty() == true ||
						thisLine.charAt(0) == '#' || thisLine.charAt(0) == '%'
								|| thisLine.charAt(0) == '@') {
					continue;
				}
				
				// split the sequence according to spaces into tokens
				for(String token:  thisLine.split(" ")){
					if(token.equals("-1")){ // indicate the end of an itemset
						tid++;
					}else if(token.equals("-2")){ // indicate the end of a sequence
//						determineSection(bitindex - previousBitIndex);  // register the sequence length for the bitmap
						sid++;
						tid =0;
					}else{  // indicate an item
						// Get the bitmap for this item. If none, create one.
						Integer item = Integer.parseInt(token);
						Bitmap bitmapItem = verticalDB.get(item);
						if(bitmapItem == null){
							bitmapItem = new Bitmap(lastBitIndex);
							verticalDB.put(item, bitmapItem);
						}
						// Register the bit in the bitmap for this item
						bitmapItem.registerBit(sid, tid, sequencesSize);
					}
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// STEP2: REMOVE INFREQUENT ITEMS FROM THE DATABASE BECAUSE THEY WILL NOT APPEAR IN ANY FREQUENT SEQUENTIAL PATTERNS
		List<Integer> frequentItems = new ArrayList<Integer>();
		Iterator<Entry<Integer, Bitmap>> iter = verticalDB.entrySet().iterator();
		// we iterate over items from the vertical database that we have in memory
		while (iter.hasNext()) {
			//  we get the bitmap for this item
			Entry<Integer, Bitmap> entry = (Entry<Integer, Bitmap>) iter.next();
			// if the cardinality of this bitmap is lower than minsup
			if(entry.getValue().getSupport() < minsup){
				// we remove this item from the database.
				iter.remove(); 
			}else{
				// otherwise, we save this item as a frequent
				// sequential pattern of size 1
				if(minimumPatternLength <= 1 && maximumPatternLength >=1) {
            		savePattern(entry.getKey(), entry.getValue());
            	}
				// and we add this item to a list of frequent items
				// that we will use later.
				frequentItems.add(entry.getKey());
			}
		}
		
		// STEP3: WE PERFORM THE RECURSIVE DEPTH FIRST SEARCH
		// to find longer sequential patterns recursively
		
		if(maximumPatternLength == 1){
			return;
		}
		// for each frequent item
		for(Entry<Integer, Bitmap> entry: verticalDB.entrySet()){
			// We create a prefix with that item
			Prefix prefix = new Prefix();
			prefix.addItemset(new Itemset(entry.getKey()));
			// We call the depth first search method with that prefix
			// and the list of frequent items to try to find
			// larger sequential patterns by appending some of these
			// items.
			dfsPruning(prefix, entry.getValue(), frequentItems, frequentItems, entry.getKey(), 2);
		}
	}
	
	/**
	 * This is the dfsPruning method as described in the SPAM paper.
	 * @param prefix the current prefix
	 * @param prefixBitmap  the bitmap corresponding to the current prefix
	 * @param sn  a list of items to be considered for i-steps
	 * @param in  a list of items to be considered for s-steps
	 * @param hasToBeGreaterThanForIStep
	 * @param m size of the current prefix in terms of items
	 * @throws IOException  if there is an error writing a pattern to the output file
	 */
	private void dfsPruning(Prefix prefix, Bitmap prefixBitmap, List<Integer> sn, List<Integer> in, int hasToBeGreaterThanForIStep, int m) throws IOException {
//		System.out.println(prefix.toString());
		
		//  ======  S-STEPS ======
		// Temporary variables (as described in the paper)
		List<Integer> sTemp = new ArrayList<Integer>();
		List<Bitmap> sTempBitmaps = new ArrayList<Bitmap>();
		
		// for each item in sn
		for(Integer i : sn){
			// perform the S-STEP with that item to get a new bitmap
			Bitmap newBitmap = prefixBitmap.createNewBitmapSStep(verticalDB.get(i), sequencesSize,  lastBitIndex, maxGap);
			// if the support is higher than minsup
			if(newBitmap.getSupportWithoutGapTotal() >= minsup){
				// record that item and pattern in temporary variables
				sTemp.add(i); 
				sTempBitmaps.add(newBitmap);
			}
		}
		// for each pattern recorded for the s-step
		for(int k=0; k < sTemp.size(); k++){
			int item = sTemp.get(k);
			// create the new prefix
			Prefix prefixSStep = prefix.cloneSequence();
			prefixSStep.addItemset(new Itemset(item));
			// create the new bitmap
			Bitmap newBitmap = sTempBitmaps.get(k);

			 if(newBitmap.getSupport() >= minsup) {
				// save the pattern to the file
				 if(m >= minimumPatternLength) {
		            	savePattern(prefixSStep, newBitmap);
		        }
				// recursively try to extend that pattern
				if(maximumPatternLength > m ){
					dfsPruning(prefixSStep, newBitmap, sTemp, sTemp, item, m+1);
				}
			}
			
		}
		
		// ========  I STEPS =======
		// Temporary variables
		List<Integer> iTemp = new ArrayList<Integer>();
		List<Bitmap> iTempBitmaps = new ArrayList<Bitmap>();
		
		// for each item in in
		for(Integer i : in){
			// the item has to be greater than the largest item
			// already in the last itemset of prefix.
			if(i > hasToBeGreaterThanForIStep){
				
				// Perform an i-step with this item and the current prefix.
				// This creates a new bitmap
				Bitmap newBitmap = prefixBitmap.createNewBitmapIStep(verticalDB.get(i), sequencesSize,  lastBitIndex);
				// If the support is no less than minsup
				if(newBitmap.getSupport() >= minsup){
					// record that item and pattern in temporary variables
					iTemp.add(i);
					iTempBitmaps.add(newBitmap);
				}
			}
		}
		// for each pattern recorded for the i-step
		for(int k=0; k < iTemp.size(); k++){
			int item = iTemp.get(k);
			// create the new prefix
			Prefix prefixIStep = prefix.cloneSequence();
			prefixIStep.getItemsets().get(prefixIStep.size()-1).addItem(item);
			// create the new bitmap
			Bitmap newBitmap = iTempBitmaps.get(k);
			
			// save the pattern
			 if(m >= minimumPatternLength) {
	            	savePattern(prefixIStep, newBitmap);
	        }
			// recursively try to extend that pattern
			if(maximumPatternLength > m){
				dfsPruning(prefixIStep, newBitmap, sTemp, iTemp, item, m+1);
			}
		}	
		// check the memory usage
		MemoryLogger.getInstance().checkMemory();
	}

	/**
	 * Save a pattern of size 1 to the output file
	 * @param item the item
	 * @param bitmap its bitmap
	 * @throws IOException exception if error while writing to the file
	 */
	private void savePattern(Integer item, Bitmap bitmap) throws IOException {
		patternCount++; // increase the pattern count
		StringBuilder r = new StringBuilder("");
		r.append(item);
		r.append(" -1 ");
		r.append("#SUP: ");
		r.append(bitmap.getSupport());
        // if the user wants the sequence IDs, we will show them
        if(outputSequenceIdentifiers) {
        	r.append(" #SID: ");
        	r.append(bitmap.getSIDs(sequencesSize));
        }
		writer.write(r.toString());
		writer.newLine();
	}
	
	/**
	 * Save a pattern of size > 1 to the output file.
	 * @param prefix the prefix
	 * @param bitmap its bitmap
	 * @throws IOException exception if error while writing to the file
	 */
	private void savePattern(Prefix prefix, Bitmap bitmap) throws IOException {
		patternCount++;
		
		StringBuilder r = new StringBuilder("");
		for(Itemset itemset : prefix.getItemsets()){
//			r.append('(');
			for(Integer item : itemset.getItems()){
				String string = item.toString();
				r.append(string);
				r.append(' ');
			}
			r.append("-1 ");
		}

		r.append("#SUP: ");
		r.append(bitmap.getSupport());
        // if the user wants the sequence IDs, we will show them
        if(outputSequenceIdentifiers) {
        	r.append(" #SID: ");
        	r.append(bitmap.getSIDs(sequencesSize));
        }
		
		writer.write(r.toString());
//		System.out.println(r.toString());
		writer.newLine();
	}

	/**
	 * Print the statistics of the algorithm execution to System.out.
	 */
	public void printStatistics() {
		StringBuilder r = new StringBuilder(200);
		r.append("=============  SPAM v0.97a- STATISTICS =============\n Total time ~ ");
		r.append(endTime - startTime);
		r.append(" ms\n");
		r.append(" Frequent sequences count : " + patternCount);
		r.append('\n');
		r.append(" Max memory (mb) : " );
		r.append(MemoryLogger.getInstance().getMaxMemory());
		r.append(patternCount);
		r.append('\n');		
		r.append("minsup " + minsup);
		r.append('\n');
		r.append("===================================================\n");
		System.out.println(r.toString());
	}

	/**
	 * Get the maximum length of patterns to be found (in terms of itemset count)
	 * @return the maximumPatternLength
	 */
	public int getMaximumPatternLength() {
		return maximumPatternLength;
	}

	/**
	 * Set the maximum length of patterns to be found (in terms of itemset count)
	 * @param maximumPatternLength the maximumPatternLength to set
	 */
	public void setMaximumPatternLength(int maximumPatternLength) {
		this.maximumPatternLength = maximumPatternLength;
	}
	
	/**
	 * Set the minimum length of patterns to be found (in terms of itemset count)
	 * @param minimumPatternLength the minimum pattern length to set
	 */
	public void setMinimumPatternLength(int minimumPatternLength) {
		this.minimumPatternLength = minimumPatternLength;
	}
	
	/**
	 * This method allows to specify the maximum gap 
	 * between itemsets of patterns found by the algorithm. 
	 * If set to 1, only patterns of contiguous itemsets
	*  will be found (no gap).
	 * @param maxGap the maximum gap (an integer)
	 */
	public void setMaxGap(int maxGap) {
		this.maxGap = maxGap;
	}

	/**
	 * This method allows to specify if sequence identifiers should be shown in the output
	 * @param showSequenceIdentifiers if true, sequence identifiers will be shown (boolean)
	 */
	public void showSequenceIdentifiersInOutput(boolean showSequenceIdentifiers) {
		this.outputSequenceIdentifiers = showSequenceIdentifiers;
	}
}
