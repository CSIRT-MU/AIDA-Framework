package ca.pfv.spmf.algorithms.frequentpatterns.estDec;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

/**
 * This is an implementation of the estDec algorithm (J. Chang, W.S. Lee 2006).
 * <br/><br/>
 * 
 * This implementation was made by Azadeh Soltani
 * <br/><br/>
 * 
 * Copyright (c) 2008-2014 Azadeh Soltani, Philippe Fournier-Viger
 * <br/><br/>
 * 
 * This file is part of the SPMF DATA MINING SOFTWARE
 * (http://www.philippe-fournier-viger.com/spmf).
 * <br/><br/>
 * 
 * SPMF is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * <br/><br/>
 * 
 * SPMF is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <br/><br/>
 * 
 * You should have received a copy of the GNU General Public License along with
 * SPMF. If not, see <http://www.gnu.org/licenses/>.
 * @see  estNode
 * @see estTree
 * @author Azadeh Soltani
 */
public class Algo_estDec {
	// the "monitoring lattice" tree
	estTree tree;
	
	// for stats
	private long miningTime = 0; 
	double sumTransactionInsertionTime = 0; // sum of time for inserting transactions
	
	private double maxMemory = 0;

	/**
	 * Constructor
	 * @param mins minimum support
	 * @param minSigValue the minSig parameter
	 */
	public Algo_estDec(double mins, double minSigValue) {
		// create the "Monitoring Lattice" tree
		tree = new estTree(mins, minSigValue);
	}

	
	/**
	 * Run the algorithm by loading the transactions from an input file.
	 * @param input   the input file path
	 * @param output  the output file path for saving the result
	 * @param mins    the minsup threshold as a double value in [0, 1]
	 * @throws FileNotFoundException  if error opening the input file
	 * @throws IOException if error reading/writing files
	 */
	public void processTransactionFromFile(String input)
			throws FileNotFoundException, IOException {

		// read the input file
		BufferedReader reader = new BufferedReader(new FileReader(input));
		String line;
//		int n=0;
		// for each line (transaction)
		while (((line = reader.readLine()) != null)) { 
			String[] lineSplited = line.split(" ");
			
			// convert the array of strings to an array of integers (items)
			int[] transaction = new int[lineSplited.length];
			for (int i = 0; i < lineSplited.length; i++) {
				transaction[i] = Integer.parseInt(lineSplited[i]);
			} 
//			if (transaction.length<80)
			  processTransaction(transaction);
//			else
//				n++;

		}// while
		reader.close();
//		System.out.println(n);
	}
	
	/**
	 * Run the algorithm by loading the transactions from an input file.
	 * @param input   the input file path
	 * @param output  the output file path for saving the result
	 * @param mins    the minsup threshold as a double value in [0, 1]
	 * @param lineCount the number of lines to be read
	 * @throws FileNotFoundException  if error opening the input file
	 * @throws IOException if error reading/writing files
	 */
	public void processTransactionFromFile(String input, int lineCount)
			throws FileNotFoundException, IOException {

		// read the input file
		BufferedReader reader = new BufferedReader(new FileReader(input));
		String line;
		int n=0;
		// for each line (transaction)
		while (((line = reader.readLine()) != null) && n < lineCount) { 
			String[] lineSplited = line.split(" ");
			
			// convert the array of strings to an array of integers (items)
			int[] transaction = new int[lineSplited.length];
			for (int i = 0; i < lineSplited.length; i++) {
				transaction[i] = Integer.parseInt(lineSplited[i]);
			} 
			processTransaction(transaction);
			
			n++;

		}// while
		reader.close();
//		System.out.println("Transaction read from file:" + n);
	}


	/**
	 * Mine recent frequent itemsets from the current tree and 
	 * save the result to a file
	 * @throws IOException
	 * @param outputPath the output file path
	 */
	public void performMining_saveResultToFile(String outputPath) throws IOException {
		// Perform mining
		long startMiningTimeStamp = System.currentTimeMillis();
		
		tree.patternMining_saveToFile(outputPath);
	
		miningTime = System.currentTimeMillis() - startMiningTimeStamp;
		System.gc();
		checkMemory();
	}
	
	/**
	 * Mine recent frequent itemsets from the current tree and 
	 * save the result to memory
	 * @throws IOException
	 * @param outputPath the output file path
	 * @return 
	 */
	public Hashtable<int[], Double> performMining_saveResultToMemory() throws IOException {
		// Perform mining
		long startMiningTimeStamp = System.currentTimeMillis();
		
		Hashtable<int[], Double> patterns = tree.patternMining_saveToMemory();
		
		checkMemory();
		miningTime = System.currentTimeMillis() - startMiningTimeStamp;
		
		return patterns;
	}

	/**
	 * Process a transaction (add it to the tree and update itemsets
	 * @param transaction an array of integers
	 */
	public void processTransaction(int[] transaction) {
		double startCTimestamp = System.currentTimeMillis();
		// process the transaction
		tree.updateParams(transaction);
		tree.insertItemset(transaction);
		
		// force pruning every 1000 transactions
		if (tree.getK() % 10000 == 0)
			tree.forcePruning(tree.root);
		
		sumTransactionInsertionTime += (System.currentTimeMillis() - startCTimestamp);
	}

	/**
	 * Check the current memory consumption to record the maximum memory usage.
	 */
	private void checkMemory() {
		// Runtime.getRuntime().gc();
		double currentMemory = (Runtime.getRuntime().totalMemory() -  Runtime.getRuntime().freeMemory())
				/ 1024d / 1024d;
		if (currentMemory > maxMemory) {
			maxMemory = currentMemory;
		}
	}
	
	/**
	 * Set the decay rate
	 * @param b  decay base 
	 * @param h decay-base life
	 */
	public void setDecayRate(double b, double h) {
		tree.setDecayRate(b,h);
	}

	/**
	 * Print statistics about the algorithm execution to the console.
	 */
	public void printStats() {
		System.out.println("=============  ESTDEC - STATS =============");
		System.out.println(" Number of nodes : " + tree.nodeCount(tree.root));
		System.out.println(" Frequent itemsets count : " + tree.patternCount);
		System.out.println(" Maximum memory usage : " + maxMemory + " mb");
		System.out.println(" Number of transactions: " + tree.getK());
		System.out.println(" Total insertion time ~ " + sumTransactionInsertionTime + " ms");
		System.out.println(" Insertion time per transaction ~ "  + sumTransactionInsertionTime / tree.getK() + " ms");
		System.out.println(" Mining time ~ " + miningTime + " ms");
		System.out.println("===================================================");
	}
}
