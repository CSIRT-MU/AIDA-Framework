package ca.pfv.spmf.algorithms.sequentialpatterns.spam;

import java.util.BitSet;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of a bitmap for SPAM.
 * <br/><br/>
 * 
 * Copyright (c) 2008-2012 Philippe Fournier-Viger
 *  <br/><br/>
 * 
 * This file is part of the SPMF DATA MINING SOFTWARE
 * (http://www.philippe-fournier-viger.com/spmf).
 * <br/><br/>
 * 
 * SPMF is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <br/><br/>
 * 
 * SPMF is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <br/><br/>
 * 
 * You should have received a copy of the GNU General Public License
 * along with SPMF.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * @see AlgoCMSPAM
 * @see AlgoSPAM
 * @see AlgoTKS
 * @see AlgoVMSP
 */
public class Bitmap {


	public static long INTERSECTION_COUNT = 0;
	
	// A bitmap is implemented using the BitSet class of Java.
	BitSet bitmap = new BitSet();  
	
	// For calculating the support more efficiently
	// we keep some information:
	int lastSID = -1;  // the sid of the last sequence inserted in that bitmap that contains a bit set to 1
	
	int firstItemsetID = -1; // the id of the first itemset containing a bit set to 1 (in any sequence)
	
	private int support = 0;  // the number of bits that are currently set to 1 corresponding to different sequences
	
	int sidsum = 0;
	
	private int supportWithoutGapTotal = 0;
	
	/**
	 * Constructor
	 * @param lastBitIndex the desired size of the bitset minus 1
	 */
	Bitmap(int lastBitIndex){
		this.bitmap = new BitSet(lastBitIndex+1); 
	}
	
	/**
	 * Constructor
	 * @param bitmap  a bitset to initialize this Bitmap.
	 */
	private Bitmap(BitSet bitmap){
		this.bitmap = bitmap; 
	}

	/**
	 * Set a bit to 1 in this bitmap
	 * @param sid the sid corresponding to that bit
	 * @param tid the tid corresponding to that bit
	 * @param sequencesSize the list of sequence length to know how many bits are allocated to each sequence
	 */
	public void registerBit(int sid, int tid, List<Integer> sequencesSize) {
		// calculate the position of the bit that we need to set to 1
		int pos = sequencesSize.get(sid) + tid;
		// set the bit to 1
		bitmap.set(pos, true);
		
		// Update the  count of bit set to 1
		if(sid != lastSID){
			support++;
			sidsum += sid;  // FOR THE VGEN ALGORITHM
		}
//		
		if(firstItemsetID == -1 || tid < firstItemsetID){
			firstItemsetID = tid;
		}
		
		// remember the last SID with a bit set to 1
		lastSID = sid;
	}
	
	/**
	 * Given the position of a bit, return the corresponding sequence ID.
	 * @param bit  the position of the bit in the bitmap
	 * @param sequencesSize  the list of lengths of sequence by sequence ID.
	 * @return the corresponding sequence ID
	 */
	private int bitToSID(int bit, List<Integer> sequencesSize) {
		// Do a binary search
		int result = Collections.binarySearch(sequencesSize, bit);
		if(result >= 0){
			return result;
		}
		return 0 - result -2;
	}

	/**
	 * Get the support of this bitmap (the number of bits set to 1)
	 * @return the support.
	 */
	public int getSupport() {
		return support;
	}

	/**
	 * Create a new bitmap for the s-step by doing a AND between this
	 * bitmap and the bitmap of an item.
	 * @param bitmapItem  the bitmap of the item used for the S-Step
	 * @param sequencesSize the  sequence lengths
	 * @param lastBitIndex  the last bit index
	 * @param maxGap 
	 * @return return the new bitmap
	 */
	Bitmap createNewBitmapSStep(Bitmap bitmapItem, List<Integer> sequencesSize, int lastBitIndex, int maxGap) {
		//INTERSECTION_COUNT++;
		
		// create a new bitset that will be use for the new bitmap
		Bitmap newBitmap = new Bitmap(new BitSet(lastBitIndex));
		
		// if no maxGap constraint is used
		if(maxGap == Integer.MAX_VALUE) {
			// We do an AND with the bitmap of the item and this bitmap
			for (int bitK = bitmap.nextSetBit(0); bitK >= 0; bitK = bitmap.nextSetBit(bitK+1)) {
				
				// find the sequence (sid) which includes this bit
				int sid = bitToSID(bitK, sequencesSize);

				// get the index of the last bit representing this sequence (sid)
	 			int lastBitOfSID = lastBitOfSID(sid, sequencesSize, lastBitIndex);
				
	 			// check if the item appear whithin that sequence after the current bit position
				boolean match = false;   
				for (int bit = bitmapItem.bitmap.nextSetBit(bitK+1); 
						bit >= 0 && bit <= lastBitOfSID; 
						bit = bitmapItem.bitmap.nextSetBit(bit+1)) {
					
					// set the bit in the new bitmap
					newBitmap.bitmap.set(bit);
					match = true;
					
					// remember the id of the first itemset containing a bit set to 1 
					int tid = bit - sequencesSize.get(sid);					
					if(firstItemsetID == -1 || tid < firstItemsetID){
						firstItemsetID = tid;
					}
				}
				if(match){
					// update the support
					if(sid != newBitmap.lastSID){
						newBitmap.support++;
						newBitmap.supportWithoutGapTotal++;
						newBitmap.sidsum += sid;
						newBitmap.lastSID = sid;
					}
				}
				
				// SPAM OPTIMIZATION:
     			// to skip the bit from the same sequence
				bitK = lastBitOfSID;
			}
		}
		// If we need to check the max gap constraint
		else {
			
			// variable to keep track of the previous sid for support count without gap
			int previousSid = -1;
			
			// We do an AND with the bitmap of the item and this bitmap
			for (int bitK = bitmap.nextSetBit(0); bitK >= 0; bitK = bitmap.nextSetBit(bitK+1)) {
				
				// find the sid of this bit
				int sid = bitToSID(bitK, sequencesSize);

				// get the last bit for this sid
	 			int lastBitOfSID = lastBitOfSID(sid, sequencesSize, lastBitIndex);
				
				boolean match = false;
				boolean matchWithoutGap = false;
				for (int bit = bitmapItem.bitmap.nextSetBit(bitK+1); 
						bit >= 0 && bit <= lastBitOfSID; 
						bit = bitmapItem.bitmap.nextSetBit(bit+1)) {
					
					matchWithoutGap = true;
					
					// if the maxgap constraint is not respected, we don't need to continue
					// checking
					if (bit - bitK > maxGap) {
						break;
					}
					
					// set the bit to 1 in the new bitmap
					newBitmap.bitmap.set(bit);
					// remember that we found that item 
					match = true;
					
					// get the tid
					int tid = bit - sequencesSize.get(sid);
					
					if(firstItemsetID == -1 || tid < firstItemsetID){
						firstItemsetID = tid;
					}
				}
				
				// When we calculate the support without considering the gap constraint,
				// we should not count it more than once for this sequence. 
				// Thus we check that "previousSid != sid"
				if(matchWithoutGap && previousSid != sid) {
					newBitmap.supportWithoutGapTotal  += 1;
					previousSid = sid;
				}
				
				if(match){
					// update the support
					if(sid != newBitmap.lastSID){
						newBitmap.support++;
						newBitmap.sidsum += sid;
					}
					newBitmap.lastSID = sid;
					// IMPORTANT:  WE DON'T DO THAT WHEN WE ARE USING THE GAP CONSTRAINT because
					// we need to check all positions
//					bitK = lastBitOfSID; // to skip the bit from the same sequence
				}
				
				
			}
		}
		
		// We return the resulting bitmap
		return newBitmap;
	}
	

	public int getSupportWithoutGapTotal() {
		return supportWithoutGapTotal;
	}

	/**
	 * Get the last bit of this sequence 
	 * @param sid the sequence id
	 * @param sequencesSize the list of sequences size
	 * @param lastBitIndex the last bit used in bitmaps
	 * @return the last bit of this sequence
	 */
	private int lastBitOfSID(int sid, List<Integer> sequencesSize, int lastBitIndex) {
		if(sid+1 >= sequencesSize.size()){
			return lastBitIndex;
		}else{
			return sequencesSize.get(sid+1) -1;
		}
	}


	/**
	 * Create a new bitmap by performing the I-STEP with this
	 * bitmap and the bitmap of an item.
	 * @param bitmapItem the bitmap of the item
	 * @param sequencesSize the sequence lengths
	 * @param lastBitIndex the last bit index
	 * @return the new bitmap
	 */
	Bitmap createNewBitmapIStep(Bitmap bitmapItem, List<Integer> sequencesSize, int lastBitIndex) {
		//INTERSECTION_COUNT++;
		
		// We create the new bitmap
		BitSet newBitset = new BitSet(lastBitIndex);
		Bitmap newBitmap = new Bitmap(newBitset);
		
		// We do an AND with the bitmap of the item
		for (int bit = bitmap.nextSetBit(0); bit >= 0; bit = bitmap.nextSetBit(bit+1)) {
			if(bitmapItem.bitmap.get(bit)){ // if both bits are TRUE
				
				// set the bit
				newBitmap.bitmap.set(bit);
				// update the support
				int sid = bitToSID(bit, sequencesSize);
				
				if(sid != newBitmap.lastSID){
					newBitmap.sidsum += sid;
					newBitmap.support++;
				}
				newBitmap.lastSID = sid; // remember the last SID
				
				// new
				int tid = bit - sequencesSize.get(sid);
				if(firstItemsetID == -1 || tid < firstItemsetID){
					firstItemsetID = tid;
				}
				// end new
			}
		}
//		 Then do the AND
//		newBitset.and(bitmapItem.bitmap);
		
		// We return the resulting bitmap
		return newBitmap;
	}
	
	/**
	 * Set the support of this bitmap without using the internal BitSet object.
	 * This method is used by VGEN
	 * @param support the support as an integer value.
	 */
	public void setSupport(int support) {
		this.support = support;
//		bitmap = null;
	}

	/**
	 * Get the list of sids corresponding to this bitmap as a string
	 * @param sequencesSize the list of sequence length to know how many bits are allocated to each sequence
	 * @return a string
	 */
	public String getSIDs(List<Integer> sequencesSize) {
		StringBuilder builder = new StringBuilder();
		
		// variable to remember the last sid
		int lastSidSeen = -1;
		// We do an AND with the bitmap of the item and this bitmap
		for (int bitK = bitmap.nextSetBit(0); bitK >= 0; bitK = bitmap.nextSetBit(bitK+1)) {
			
			// find the sid of this bit
			int sid = bitToSID(bitK, sequencesSize);
			// if we did not see this sid already 
			if(sid != lastSidSeen) {
				if(lastSidSeen != -1){
					builder.append(" ");
				}
				builder.append(sid);
				lastSidSeen = sid;
			}
		}
		return builder.toString();
	}


}
