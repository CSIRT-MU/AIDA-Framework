package ca.pfv.spmf.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import ca.pfv.spmf.algorithms.frequentpatterns.MSApriori.AlgoMSApriori;

/**
 *  Example of how to use the MSAPRIORI algorithm in source code.
 */
public class MainTestMSApriori_saveToFile {

	public static void main(String [] arg) throws IOException{

		String input = fileToPath("contextIGB.txt");
		String output = ".//output.txt";  // the path for saving the frequent itemsets found
		
		double beta=0.4;
		double LS=0.2;
		
		// Applying the MSApriori algorithm
		AlgoMSApriori algo = new AlgoMSApriori();
		
		// Uncomment the following line to set the maximum pattern length (number of items per itemset)
//		algo.setMaximumPatternLength(3);
		
		algo.runAlgorithm(input, output, beta, LS);
		algo.printStats();
	}
	
	public static String fileToPath(String filename) throws UnsupportedEncodingException{
		URL url = MainTestMSApriori_saveToFile.class.getResource(filename);
		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}
}
