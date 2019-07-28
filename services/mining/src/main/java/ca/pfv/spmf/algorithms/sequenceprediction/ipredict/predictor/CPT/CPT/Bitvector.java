package ca.pfv.spmf.algorithms.sequenceprediction.ipredict.predictor.CPT.CPT;

import java.util.BitSet;
/*
 * This file is copyright (c) Ted Gueniche 
 * <ted.gueniche@gmail.com>
 *
 * This file is part of the IPredict project
 * (https://github.com/tedgueniche/IPredict).
 *
 * IPredict is distributed under The MIT License (MIT).
 * You may obtain a copy of the License at
 * https://opensource.org/licenses/MIT 
 */
/**
 * PHIL08
 * This class encapsulate the BitSet class and make sure
 * that when we call cardinality, it is not calculated more than once.
 * 
 * There are two cases:
 *  - When we do a AND operation, the cardinality is recalculated.
 *  - When we set a it  during the "preload" phase, we do cardinality ++;.
 */
public class Bitvector {

	BitSet bitset = new BitSet(); // the bitset
	int cardinality;  // the cardinality

	/**
	 * Public constructor
	 */
	Bitvector() {
		bitset = new BitSet();
		cardinality = 0;
	}
	
	/**
	 * Private constructor used by the clone() method.
	 * @param bitset  a bitset to be cloned
	 * @param cardinality the cardinality of the bitset
	 */
	private Bitvector(BitSet bitset, int cardinality) {
		this.bitset = bitset;
		this.cardinality = cardinality;
	}

	/**
	 * Performing the AND operation.
	 * @param bitvector2 another bit vector
	 */ 
	public void and(Bitvector bitvector2) {
		bitset.and(bitvector2.bitset);
		cardinality = -1;
	}

	/**
	 * Clone this bit vector.
	 * @return a BitVector
	 */
	protected Object clone(){
		try {
			 return new Bitvector((BitSet)bitset.clone(), cardinality);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public int size() {
		return bitset.size();
	}

	public int nextSetBit(int i) {
		return bitset.nextSetBit(i);
	}

	public int cardinality() {
		// if the cardinality is unknown because of the AND operation
		if(cardinality == -1){
			// we recalculate it
			cardinality = bitset.cardinality();
		}
		return cardinality;
	}

	// set a bit to 1 and increment the cardinality by 1
	public void setBitAndIncrementCardinality(int i) {
		bitset.set(i);
		cardinality++;
	}
	
	// FOR DEBUGGING
	public String toString(){
		return  bitset.toString() +  " cardinality : " + cardinality;
	}
}