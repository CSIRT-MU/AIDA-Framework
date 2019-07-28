package ca.pfv.spmf.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import ca.pfv.spmf.algorithms.frequentpatterns.defme.AlgoDefMe;
import ca.pfv.spmf.input.transaction_database_list_integers.TransactionDatabase;


/**
 * Example of how to use DefMe algorithm from the source code.
 * @author Philippe Fournier-Viger - 2009
 */
public class MainTestDefMe_saveToFile {

	public static void main(String [] arg) throws IOException{
		// Loading the binary context
		String input = fileToPath("contextZart.txt");  // the database
		String output = ".//output.txt";  // the path for saving the frequent itemsets found
		
		double minsup = 0.4; // means a minsup of 2 transaction (we used a relative support)
		
		TransactionDatabase database = new TransactionDatabase();
		try {
			database.loadFile(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Applying the DefMe algorithm
		AlgoDefMe algo = new AlgoDefMe();
		
		// Uncomment the following line to set the maximum pattern length (number of items per itemset)
//		algo.setMaximumPatternLength(2);
		
		algo.runAlgorithm(output, database, minsup);
		algo.printStats();
	}
	
	public static String fileToPath(String filename) throws UnsupportedEncodingException{
		URL url = MainTestDefMe_saveToFile.class.getResource(filename);
		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}
}
