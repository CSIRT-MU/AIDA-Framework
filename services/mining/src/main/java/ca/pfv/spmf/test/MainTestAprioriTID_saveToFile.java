package ca.pfv.spmf.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import ca.pfv.spmf.algorithms.frequentpatterns.aprioriTID.AlgoAprioriTID;

/**
 * Example of how to use APRIORI-TID and save the output to a file,
 * from the source code.
 * 
 * @author Philippe Fournier-Viger 
 */
public class MainTestAprioriTID_saveToFile {

	public static void main(String [] arg) throws NumberFormatException, IOException{
		// Loading the binary context
		String input = fileToPath("contextPasquier99.txt");  // the database
		String output = ".//output.txt";  // the path for saving the frequent itemsets found
		
		double minsup = 0.4; // means a minsup of 2 transaction (we used a relative support)
		
		// Applying the AprioriTID algorithm
		AlgoAprioriTID algo = new AlgoAprioriTID();
		
//		// Set this variable to true to show the transaction identifiers where patterns appear in the output file
//		algo.setShowTransactionIdentifiers(true);
		
		// Uncomment the following line to set the maximum pattern length (number of items per itemset)
		algo.setMaximumPatternLength(3);
		
		// Run the algorithm
		algo.runAlgorithm(input, output, minsup);
		
		// Display statistics about the algorithm execution
		algo.printStats();
	}
	
	public static String fileToPath(String filename) throws UnsupportedEncodingException{
		URL url = MainTestAprioriTID_saveToFile.class.getResource(filename);
		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}
}
