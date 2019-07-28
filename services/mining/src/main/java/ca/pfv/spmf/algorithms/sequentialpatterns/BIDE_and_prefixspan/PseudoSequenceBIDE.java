package ca.pfv.spmf.algorithms.sequentialpatterns.BIDE_and_prefixspan;

import java.util.ArrayList;
import java.util.List;

import ca.pfv.spmf.input.sequence_database_list_integers.Sequence;
import ca.pfv.spmf.patterns.itemset_list_integers_without_support.Itemset;


/**
 * This represents a sequence from a projected database (as based in BIDE+).
 * Since it is a projected sequence, 
 * it makes reference to the original Sequence.Java class used by BIDE+.
 * 
 * This class also include several methods for calculating the maximum periods, 
 * semi-maximum periods, etc. as required by the BIDE+ algorithm.
 * These methods are quite complex so if you want to understand them, it is
 * recommended to read the BIDE+ paper carefully before reading the code.
 *
 * Copyright (c) 2008-2012 Philippe Fournier-Viger
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
 */

// note that this class extends PseudoSequence used by PrefixSpan:
public class PseudoSequenceBIDE extends PseudoSequence {

	// variable to indicate if the pseudo sequence is cut at right (a prefix)
	int lastItemset; // the position where the cut was done in terms of itemset in the original sequence
	int lastItem;   //  the position where the cut was done in terms of item in the original sequence
	
	/**
	 * Constructor of a pseudo-sequence based on a pseudo-sequence (overloaded)
	 * @param sequence the original pseudo-sequence
	 * @param indexItemset the position of the itemset where the pseudo sequence starts (if it is cut at left)
	 * @param indexItem the position of the item  where the pseudo sequence starts (if it is cut at left)
	 */
	protected PseudoSequenceBIDE(PseudoSequenceBIDE sequence, int indexItemset, int indexItem){
		// record the original sequence
		this.sequence = sequence.sequence;
		// record the position where the sequence starts if it is cut at left
		this.firstItemset = indexItemset + sequence.firstItemset;
		if(this.firstItemset == sequence.firstItemset){
			this.firstItem = indexItem + sequence.firstItem;
		}else{
			this.firstItem = indexItem; 
		}
		// this sequence ends at the same place as the original sequence
		this.lastItemset = sequence.lastItemset;
		this.lastItem = sequence.lastItem;
	}
	
	/**
	 * Constructor of a pseudo-sequence based on a pseudo-sequence (overloaded)
	 * @param sequence the original pseudo-sequence
	 * @param indexItemset the position of the itemset where the pseudo sequence starts (!=0 if it is cut at left)
	 * @param indexItem the position of the item  where the pseudo sequence starts (!=0 if it is cut at left)
	 * @param lastItemset the position of the itemset where this pseudo sequence ends
	 * @param lastItem the position of the item where this pseudo sequence ends
	 */
	protected PseudoSequenceBIDE(PseudoSequenceBIDE sequence, int indexItemset, int indexItem, int lastItemset, int lastItem){
		// record the original sequence
		this.sequence = sequence.sequence;
		// record the position where the sequence starts if it is cut at left
		this.firstItemset = indexItemset + sequence.firstItemset;
		if(this.firstItemset == sequence.firstItemset){
			this.firstItem = indexItem + sequence.firstItem;
		}else{
			this.firstItem = indexItem; // ?????????? necessary??
		}
		// record the position where the sequence ends if it is cut at right
		this.lastItemset = lastItemset;
		this.lastItem = lastItem;
	}
	
	/**
	 * Constructor of a pseudo-sequence based on an original sequence (overloaded)
	 * @param sequence the original sequence
	 * @param indexItemset the position of the itemset where the pseudo sequence starts (!=0 if it is cut at left)
	 * @param indexItem the position of the item  where the pseudo sequence starts (!=0 if it is cut at left)
	 */
	public  PseudoSequenceBIDE(Sequence sequence, int indexItemset, int indexItem){
		this.sequence = sequence;
		// record the position where the sequence starts if it is cut at left
		this.firstItemset = indexItemset;
		this.firstItem = indexItem;
		// We assume that the sequence is not cut at right.
		// So we set the last itemset to the last itemset in the original sequence
		// and the last item to the last item in the last itemset of the original
		// sequence.
		this.lastItemset = sequence.size()-1;
		this.lastItem = sequence.getItemsets().get(lastItemset).size()-1;
	}
	
	
	/******************************************
	 * All the following methods are specific for BIDE+.
	 * I provide a brief description of them.
	 * Please refer to the original BIDE+ paper for more
	 * details about the BIDE+ algorithm and how it works because
	 * it is quite complex.
	 * 
	 * Remember that a sequential pattern is not closed if there exists 
	 * a forward-extension or a backward-extension.
	 ******************************************/

	/**
	 * Structure to contains a sequence and list of positions to elements in the sequence.
	 */
	protected static class PseudoSequencePair{
		// a pseudo sequence
		final PseudoSequenceBIDE pseudoSequence;
		// a list of positions
		final List<Position> list;
		// a simple constructor
		public PseudoSequencePair(PseudoSequenceBIDE pseudoSequence, List<Position> list){
			this.pseudoSequence = pseudoSequence;
			this.list = list;
		}
	}
	

	
	/**
	 * Get the position of the last item.
	 * @return the position.
	 */
	protected int getLastItemPosition(){
		return lastItem - firstItem -1;
	}
	

	/**
	 * Check if a given itemset is the last itemset (overloaded).
	 * @return true if yes.
	 */
	protected boolean isLastItemset(int index) {
		return (index + firstItemset) == lastItemset;
	}
	
	/**
	 * Check if a given itemset is the last itemset (overloaded).
	 * @return true if yes.
	 */
	public int getSizeOfItemsetAt(int index) {
		int size = sequence.getItemsets().get(index + firstItemset).size();
		if(isLastItemset(index)){
			size = 1 + lastItem;
		}
		if(isFirstItemset(index)){
			size -=  firstItem;
		}
		return size;
	}

	/**
	 * Get a string representation of this sequence.
	 */
	public String toString() {
		StringBuilder r = new StringBuilder();
		for(int i=0; i < size(); i++){
			r.append("{");
			for(int j=0; j < getSizeOfItemsetAt(i); j++){
				if(!isLastItemset(i) || (j <= lastItem)){
					r.append(getItemAtInItemsetAt(j, i).toString());
					if(isPostfix(i)){
						r.append('*');
					}
					r.append(' ');
				}
			}
			r.append("}");
		}
		r.append("  ");
		return r.toString();
	}
	
	
	public int size(){
		int size = sequence.size() - firstItemset - ((sequence.size()-1) - lastItemset);
		if(size == 1 && sequence.getItemsets().get(firstItemset).size() == 0){
			return 0;
		}
		return size;
	}
	
	/**
	 * Return true if this itemset is cut at right (a prefix).
	 * @param indexItemset the position of the given itemset.
	 * @return true if it is cut at right.
	 */
	protected boolean isCutAtRight(int index) {
		if(!isLastItemset(index)){
			return false;
		}
		return (sequence.getItemsets().get(index + firstItemset).size() -1) != lastItem;
	}
	
//	/**
//	 * Method that find all instances of a prefix in a sequence S.
//	 * The meaning of instance is the one from the BIDE+ article when they talk about
//	 * "first instance", "last instance".  Here instead of finding only 
//	 * the "first instance" or "last instance", we find all instances.
//	 */
//	protected  List<PseudoSequencePair> getAllInstancesOfPrefix(List<Itemset> prefix, int i){
//		List<List<Position>> listInstances  
//		  = getAllInstancesOfPrefixHelper(prefix, 0, new ArrayList<List<Position>>(), new ArrayList<Position>(), 0);
//		//we cut the instances found according to the maximum size
//		// of the prefix that we are searching for.
//		List<PseudoSequencePair> allPairs = new ArrayList<PseudoSequencePair>();
//		for(List<Position> listPositions : listInstances){
//			PseudoSequenceBIDE newSequence = new PseudoSequenceBIDE(this, this.firstItemset, this.firstItem, 
//					listPositions.get(i-1).itemset, listPositions.get(i-1).item);
//			allPairs.add(new PseudoSequencePair(newSequence,listPositions));
//		}
//		return allPairs;
//	}
//	
//	// helper for the above method
//	protected  List<List<Position>> getAllInstancesOfPrefixHelper(List<Itemset> prefix, int indexItemset, 
//			 List<List<Position>> allInstances, List<Position> listPositionsTotal, 
//			int itemsetShift){
//
//		for(int i=itemsetShift; i< size(); i++){
//			int indexItem =0;
//			List<Position> listPositions = new ArrayList<Position>();
//			int iDCourant = prefix.get(indexItemset).get(indexItem);
//			
//			for(int j=0; j < getSizeOfItemsetAt(i); j++){
//				int id = getItemAtInItemsetAt(j, i);
//				if(id == iDCourant){// l'item match
//					listPositions.add(new Position(i,j));
//					if(listPositions.size()+ listPositionsTotal.size()	== getItemOccurencesTotalCount(prefix))  // si on a trouv� tout le pr�fix
//					{
//						List<Position> newList = new ArrayList<Position>(listPositionsTotal);
//						newList.addAll(listPositions);
//						allInstances.add(newList);
//					}else if(indexItem+1 >= prefix.get(indexItemset).size()){ 
//						// if we have found the itemset
//						
//						List<Position> newList = new ArrayList<Position>(listPositionsTotal);
//						newList.addAll(listPositions);
//						
//						if(indexItemset+1 < prefix.size()){ 
//							getAllInstancesOfPrefixHelper(prefix, indexItemset+1, allInstances, newList, i+1);
//						}
//					}else{
//						indexItem++;
//						iDCourant = prefix.get(indexItemset).get(indexItem);
//					}
//				}
//			}
//		}
//		return allInstances;
//	}

//	/**
//	 * Last instance of a prefix sequence X in a sequence S.
//	 * For example, the last instance of AB in ABBCA is  ABB.
//	 * Additionnal difficulty : this sequence must respect timestamps!
//	 */
//	protected  PseudoSequencePair getLastInstanceOfPrefixSequence(List<Itemset> prefix, int i){
//		List<PseudoSequencePair> list = getAllInstancesOfPrefix(prefix, i); 
//		// Return the last one
//		PseudoSequencePair sequenceRetourPair = list.get(0);
//		for(PseudoSequencePair sequencePair : list){
//			PseudoSequenceBIDE sequence = sequencePair.pseudoSequence;
//			PseudoSequenceBIDE sequenceRetour = sequenceRetourPair.pseudoSequence;
//			if((sequence.lastItemset > sequenceRetour.lastItemset) ||
//					(sequenceRetour.lastItemset == sequence.lastItemset  && sequence.lastItem > sequenceRetour.lastItem)
//					){
//				sequenceRetourPair = sequencePair;
//			}
//		}
//		return sequenceRetourPair;
//	}
	
//	protected  PseudoSequencePair getLastInstanceOfPrefixSequence(List<PseudoSequencePair> list){
////		List<PseudoSequencePair> list = getAllInstancesOfPrefix(prefix, i); 
//		// Return the last one
//		PseudoSequencePair sequenceRetourPair = list.get(0);
//		for(PseudoSequencePair sequencePair : list){
//			PseudoSequenceBIDE sequence = sequencePair.pseudoSequence;
//			PseudoSequenceBIDE sequenceRetour = sequenceRetourPair.pseudoSequence;
//			if((sequence.lastItemset > sequenceRetour.lastItemset) ||
//					(sequenceRetour.lastItemset == sequence.lastItemset  && sequence.lastItem > sequenceRetour.lastItem)
//					){
//				sequenceRetourPair = sequencePair;
//			}
//		}
//		return sequenceRetourPair;
//	}

//	/**
//	 * First Instance of the prefix X in a sequence S.
//	 * Method to find the first Instance of the prefix sequence X= e1, e2... ei+1 in a sequence S.
//	 * Exemple: first instance of AB in the sequence CAABC = CAAB.
//	 * Additionnal difficulty : this sequence must respect timestamps!
//	 */
//	protected  PseudoSequencePair getFirstInstanceOfPrefixSequence(List<Itemset> prefix, int i){
//		List<PseudoSequencePair> list = getAllInstancesOfPrefix(prefix, i);
//		// Return the first one
//		PseudoSequencePair sequenceRetourPair = list.get(0);
//		for(PseudoSequencePair sequencePair : list){
//			PseudoSequenceBIDE sequence = sequencePair.pseudoSequence;
//			PseudoSequenceBIDE sequenceRetour = sequenceRetourPair.pseudoSequence;
//			if((sequence.lastItemset < sequenceRetour.lastItemset) ||
//					(sequenceRetour.lastItemset == sequence.lastItemset  && sequence.lastItem < sequenceRetour.lastItem)){
//				sequenceRetourPair = sequencePair;
//			}
//		}
//		return sequenceRetourPair;
//	}
//	
//	protected  PseudoSequencePair getFirstInstanceOfPrefixSequence(List<PseudoSequencePair> list){
////		List<PseudoSequencePair> list = getAllInstancesOfPrefix(prefix, i);
//		// Return the first one
//		PseudoSequencePair sequenceRetourPair = list.get(0);
//		for(PseudoSequencePair sequencePair : list){
//			PseudoSequenceBIDE sequence = sequencePair.pseudoSequence;
//			PseudoSequenceBIDE sequenceRetour = sequenceRetourPair.pseudoSequence;
//			if((sequence.lastItemset < sequenceRetour.lastItemset) ||
//					(sequenceRetour.lastItemset == sequence.lastItemset  && sequence.lastItem < sequenceRetour.lastItem)){
//				sequenceRetourPair = sequencePair;
//			}
//		}
//		return sequenceRetourPair;
//	}
	
	/**
	 * Get the ith last-in-last appearance with respect to a prefix sequence Sp.
	 * n = size of S
	 * If i == n, it is the last appearance of ei in the last instance of Sp.
	 * If 0 <= i < n, it is the last appearance of ei in the last instance of Sp, and LLi must appear
	 *   before LLi+1.
	 * Example : If S= CAABC and SP  = AB   then  LL0 =  second A in CAABC
	 *           If S= CACAC and SP = CAC  then LL0 =  second C in S, 
	 *                                          LL1 =  second A in S and 
	 *                                          LL2 = third C in S
	 * @param prefix : le prefix
	 * @param i : le i�me �l�ment du pr�fixe.
	 * @param lastInstancePair 
	 * @return
	 */
	protected Position getIthLastInLastApearanceWithRespectToPrefix(List<Itemset> prefix, int i, PseudoSequencePair lastInstancePair){ 

		// ith item of prefix id is :
		int iditem = getIthItem(prefix, i);
		if (lastInstancePair != null) {
			if (i == getItemOccurencesTotalCount(prefix) - 1) {
				// return the last occurence of that item:
				for (int j = lastInstancePair.pseudoSequence.size() - 1; j >= 0; j--) {
					int sizeItemsetJ = lastInstancePair.pseudoSequence
							.getItemset(j).size();
					for (int k = sizeItemsetJ - 1; k >= 0; k--) {
						int item = lastInstancePair.pseudoSequence
								.getItemAtInItemsetAt(k, j);
						if (item == iditem) {
							return new Position(j, k);
						} else if (item < iditem) {
							break;
						}
					}
				}
			} else {
				// return the last before LLi+1
				Position LLiplus1 = getIthLastInLastApearanceWithRespectToPrefix(
						prefix, i + 1, lastInstancePair); // RecursiveCALL
				for (int j = LLiplus1.itemset; j >= 0; j--) {
					for (int k = lastInstancePair.pseudoSequence.getItemset(j)
							.size() - 1; k >= 0; k--) {
						if (j == LLiplus1.itemset && k >= LLiplus1.item) {
							continue;
						}
						if (lastInstancePair.pseudoSequence
								.getItemAtInItemsetAt(k, j) == iditem) {
							return new Position(j, k);
						}
					}
				}
			}
		}
		return null; // should not happen!

	}

	/**
	 * Get the ith maximum period of a prefix sequence for this sequence S.
	 * The ith maximum period of the prefix Sp in S is : 
	 *  * if 0 < i <= n, it is the piece of sequence between the end of the first instance of prefix e1... ei-1 in S 
	 *    and the ith last-in-last appearance with respect to prefix Sp
	 *  * if i = 0, it is the piece of sequence in S located before the first last-in-last appearance with respect to prefix Sp.
	 *   Example1:  if S = ABCB  and Sp = AB
	 *   	the 1th semi-period of Sp in S is empty
	 *      the 2th semi-period of Sp in S is BC
	 *   Example2:  if S = ABBB  and Sp = BB
	 *   	the 1th semi-period of Sp in S is AB
	 *      the 2th semi-period of Sp in S is B
	 */
	protected PseudoSequenceBIDE getIthMaximumPeriodOfAPrefix(List<Itemset> prefix, int i){
		
		// we obtain the last instance:
		// The last instance is a PseudoSequencePair object.
		// It consists of 
		// - the pseudo sequence that is the last instance
		// - the list of positions for each element of prefix in that last instance.
		PseudoSequencePair lastInstancePair = getLastInstanceOfPrefixSequenceNEW(prefix, getItemOccurencesTotalCount(prefix));
		Position ithlastlast = this.getIthLastInLastApearanceWithRespectToPrefix(prefix, i, lastInstancePair);
		
		if(i == 0){ //it is the piece of sequence in S located before the first last-in-last appearance with respect to prefix Sp.
			return trimBeginingAndEnd(null, ithlastlast);
		}
		
		// ELSE it is the piece of sequence between the end of the first instance of prefix e1... ei-1 in S 
		// and the ith last-in-last appearance with respect to prefix Sp
		// Important : We thus have to cut the prefix at ei-1  (short prefix = e1 ... ei-1). 
		//      It is because the parameter i is used by getLastInstanceOfPrefixSequence(...). (???)
// y
		PseudoSequencePair firstInstance = this.getFirstInstanceOfPrefixSequenceNEW(prefix, i); 
		
		// added lines to fix bug (proposed by N. Mord - 2017)
		if (firstInstance == null || firstInstance.list.isEmpty()){ 
			return trimBeginingAndEnd(null, ithlastlast); 
		} 
		// end of the added lines 
		
		Position lastOfFirstInstance = firstInstance.list.get(i-1); 
		
		
		
		return trimBeginingAndEnd(lastOfFirstInstance, ithlastlast);
	}
	
	/**
	 * Last instance of a prefix sequence X in a sequence S.
	 * For example, the last instance of AB in ABBCA is  ABB.
	 * Additionnal difficulty : this sequence must respect timestamps!
	 */
	protected  PseudoSequencePair getLastInstanceOfPrefixSequenceNEW(List<Itemset> prefix, int i){
		
		int remainingToMatchFromPrefix = i;
		
		List<Position> listPositions = new ArrayList<Position>();
		
		int prefixItemsetPosition = prefix.size() -1;
		// for each itemset of sequence
		for(int j = size() -1; j>=0; j--){
			int itemInPrefixPosition = prefix.get(prefixItemsetPosition).size()-1;
			boolean allMatched = false;
			
			int searchedItem = prefix.get(prefixItemsetPosition).get(itemInPrefixPosition);
			
			List<Position> tempList = new ArrayList<Position>();
			// for each item in itemset
			for(int k=getSizeOfItemsetAt(j)-1; k >=0; k--) {
				int currentItem = getItemAtInItemsetAt(k, j);
				if(currentItem == searchedItem) {
					tempList.add(new Position(j, k));
					itemInPrefixPosition--;
					if(itemInPrefixPosition == -1 || tempList.size() == remainingToMatchFromPrefix) {
						allMatched = true;
						break;
					}
					searchedItem = prefix.get(prefixItemsetPosition).get(itemInPrefixPosition);
				}else if(currentItem < searchedItem) {
					break;
				}
			}
			if(allMatched) {
				remainingToMatchFromPrefix -= tempList.size();
				listPositions.addAll(tempList);
				prefixItemsetPosition--;
				if(prefixItemsetPosition == -1) {
					return new PseudoSequencePair(this, listPositions);
				}
			}
		}

		return null; // should not happen
	}
	
	/**
	 * First Instance of the prefix X in a sequence S.
	 * Method to find the first Instance of the prefix sequence X= e1, e2... ei+1 in a sequence S.
	 * Exemple: first instance of AB in the sequence CAABC = CAAB.
	 * Additionnal difficulty : this sequence must respect timestamps!
	 */
	protected  PseudoSequencePair getFirstInstanceOfPrefixSequenceNEW(List<Itemset> prefix, int i){
		
		int remainingToMatchFromPrefix = i;
		
		List<Position> listPositions = new ArrayList<Position>();
		
		int prefixItemsetPosition = 0;
		// for each itemset of sequence
		for(int j=0; j< size(); j++){
			int itemInPrefixPosition =0;
			boolean allMatched = false;
			
			int searchedItem = prefix.get(prefixItemsetPosition).get(itemInPrefixPosition);
			
			List<Position> tempList = new ArrayList<Position>();
			// for each item in itemset
			for(int k=0; k < getSizeOfItemsetAt(j) && !allMatched; k++) {
				int currentItem = getItemAtInItemsetAt(k, j);
				if(currentItem == searchedItem) {
					tempList.add(new Position(j, k));
					itemInPrefixPosition++;
					if(itemInPrefixPosition == prefix.get(prefixItemsetPosition).size() || tempList.size() == remainingToMatchFromPrefix) {
						allMatched = true;
						break;
					}
					searchedItem = prefix.get(prefixItemsetPosition).get(itemInPrefixPosition);
				}else if(currentItem > searchedItem) {
					break;
				}
			}
			if(allMatched) {
				remainingToMatchFromPrefix -= tempList.size();
				listPositions.addAll(tempList);
				prefixItemsetPosition++;
				if(prefixItemsetPosition == prefix.size()) {
					PseudoSequenceBIDE newSequence = new PseudoSequenceBIDE(this, this.firstItemset, this.firstItem, 
							listPositions.get(i-1).itemset, listPositions.get(i-1).item);
					return new PseudoSequencePair(newSequence, listPositions);
				}
			}
		}

		return null; // should not happen
	}
	
	
	
	/**
	 * Method that is used by the one above.
	 * This method cut a sequence by removing some part at the begining and at the end.
	 * IMPORTANT : this method assumes that the sequence has never been cut.
	 * This simplify what this method has to do to handle time.
	 * 
	 * @return null If the result is an empty sequence.!
	 */
	protected PseudoSequenceBIDE trimBeginingAndEnd(Position positionStart, Position positionEnd){
		int itemsetStart = 0;
		int itemStart =0;
		int itemsetEnd=lastItemset;
		int itemEnd=lastItem;
		
		if(positionStart != null){  // where the cut starts
			itemsetStart = positionStart.itemset;
			itemStart =  positionStart.item + 1;
			if(itemStart == getSizeOfItemsetAt(itemsetStart)){
				itemsetStart++;
				itemStart =0;
			}
			if(itemsetStart == size()){// the resulting sequence is empty!
				return null;
			}
		}
		
		if(positionEnd != null){ // We cut the right part
			itemEnd = positionEnd.item -1;
			itemsetEnd = positionEnd.itemset;
			if(itemEnd<0){
				itemsetEnd = positionEnd.itemset -1;
				if(itemsetEnd < itemsetStart){
					return null;
				}
				itemEnd = getSizeOfItemsetAt(itemsetEnd)-1;
			}
		}
		// Check if the end is not before the beginning of the sequence!
		if(itemsetEnd == itemsetStart && itemEnd < itemStart){ 
//			System.out.println("TEST");
			return null;
		}
		return new PseudoSequenceBIDE(this, itemsetStart, itemStart, itemsetEnd, itemEnd);
	}
	
	/**
	 * Get the ith semi-maximum period of a prefix sequence for this sequence S.
	 * The ith semi-maximum period of the prefix Sp in S is : 
	 *  * if 0 < i <= n, it is the piece of sequence between the end of the first instance of prefix e1... ei-1 in S 
	 *    and the ith last-in-first appearance with respect to prefix Sp
	 *  * if i = 0, it is the piece of sequence in S located before the first last-in-first appearance with respect to prefix Sp.
	 * @param currentCutPosition 
	 */
	public PseudoSequenceBIDE getIthSemiMaximumPeriodOfAPrefix(List<Itemset> prefix, int i, Position currentCutPosition){
		// First, we get the first instance.
		// The first instance is a PseudoSequencePair object.
		// It consists of 
		// - the pseudosequence that is the first instance
		// - the list of positions for each element of prefix in that first instance.
//		PseudoSequencePair firstInstancePair = getFirstInstanceOfPrefixSequence(prefix, getItemOccurencesTotalCount(prefix));
		PseudoSequencePair firstInstancePairNEW  = getFirstInstanceOfPrefixSequenceNEW(prefix, getItemOccurencesTotalCount(prefix));
		
		Position ithlastfirst = getIthLastInFirstApearanceWithRespectToPrefix(prefix, i, firstInstancePairNEW);
		
		// PHILIPPE 2015: TO FIX BUG WITH n-SDB
		if(ithlastfirst.itemset < currentCutPosition.itemset){
			ithlastfirst = currentCutPosition;
		}
				
		if(i == 0){ //it is the piece of sequence in S located before the first last-in-first appearance with respect to prefix Sp.
//			pseudo.toString();
			return trimBeginingAndEnd(null, ithlastfirst);
		}
		
		// ELSE it is the piece of sequence between the end of the first instance of prefix e1... ei-1 in S 
		// and the ith last-in-first appearance with respect to prefix Sp
		/// Important: we have to cut the prefix at ei-1  (short prefix = e1 ... ei-1). 
		//  since the parameter i is used by getLastInstanceOfPrefixSequence(...) and.....(???)
		
		// THIS IS DONE AS FOLLOWS:
		// We get the first instance of prefix e1... ei-1  // e1... ei-1
		PseudoSequencePair firstInstance = getFirstInstanceOfPrefixSequenceNEW(prefix, i);
		// we get the position of the last item of that first instance
		Position endOfFirstInstance = firstInstance.list.get(i-1); 
		
		//we return the piece of sequence between the end of the first instance of prefix e1... ei-1 in S 
		// and the ith last-in-first appearance with respect to prefix Sp
		return trimBeginingAndEnd(endOfFirstInstance, ithlastfirst);
	}

	/**
	 * Return the sum of the size of all itemsets of this sequence.
	 * Note: used by the BIDE algorithm
	 */
	protected int getItemOccurencesTotalCount(List<Itemset> itemsets){
		int count =0;
		for(Itemset itemset : itemsets){
			count += itemset.size();
		}
		return count;
	}
	
	/**
	 * Get the ith item in a pseudo-sequence.
	 * @param itemsets  a list of itemsets
	 * @param i  the position of an item
	 * @return the item.
	 */
	protected Integer getIthItem(List<Itemset> itemsets, int i) { 
		for(int j=0; j< itemsets.size(); j++){
			if(i < itemsets.get(j).size()){
				return itemsets.get(j).get(i);
			}
			i = i- itemsets.get(j).size();
		}
		return null;
	}
	
	//----------------------- Backscan search space pruning  sept. 2009 updated
	
	/**
	 * Get the ith first-in-last appearance with respect to a prefix sequence Sp.
	 * n = size of S
	 * If i == n, it is the last appearance of ei in the first instance of Sp.
	 * If 0 <= i < n, it is the last appearance of ei in the first instance of Sp, and LFi must appear
	 *   before LFi+1.
	 * @param prefix : the prefix
	 * @param i : the i�me element of the prefix.
	 * @param firstInstancePair2 
	 * @return
	 */
	protected Position getIthLastInFirstApearanceWithRespectToPrefix(List<Itemset> prefix, int i, PseudoSequencePair firstInstancePair){
		
			// ith item of prefix id is :
			Integer iditem = getIthItem(prefix,i);
			
			if(i == getItemOccurencesTotalCount(prefix)-1){
				// return the last occurence of that item:
				for(int j=firstInstancePair.pseudoSequence.size()-1; j>=0; j--){
					for(int k=firstInstancePair.pseudoSequence.getItemset(j).size()-1; k>=0; k--){
						if(firstInstancePair.pseudoSequence.getItemAtInItemsetAt(k, j).equals(iditem)){
							return new Position(j, k);
						}
					}
				}
			}else{
				// return the last before LLi+1
				Position LLiplus1 = getIthLastInFirstApearanceWithRespectToPrefix(prefix, i+1, firstInstancePair);
//				if(LLiplus1 == null){
//					System.out.println("DEBUG");
//				}
				for(int j= LLiplus1.itemset; j>=0; j--){
					for(int k=firstInstancePair.pseudoSequence.getItemset(j).size()-1; k>=0; k--){
						if(j == LLiplus1.itemset && k >= LLiplus1.item){
							continue;
						}
						if(firstInstancePair.pseudoSequence.getItemAtInItemsetAt(k, j).equals(iditem)){
							return new Position(j, k);
						}
					}
				}
			}
			return null; // should not happen!
		}
}