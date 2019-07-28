package ca.pfv.spmf.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import ca.pfv.spmf.algorithms.sequentialpatterns.spam.AlgoSPAM;


/**
 * Example of how to use the SPAM algorithm in source code.
 * @author Philippe Fournier-Viger
 */
public class MainTestSPAM_saveToFile {

	public static void main(String [] arg) throws IOException{    
		// Load a sequence database
		String input = fileToPath("contextPrefixSpan.txt");
		String output = ".//output.txt";
		
		// Create an instance of the algorithm 
		AlgoSPAM algo = new AlgoSPAM(); 
//		algo.setMinimumPatternLength(3);
//		algo.setMaximumPatternLength(3);
		
        // if you set the following parameter to true, the sequence ids of the sequences where
        // each pattern appears will be shown in the result
//		algo.showSequenceIdentifiersInOutput(true);
		
		// execute the algorithm with minsup = 2 sequences  (50 %)
		algo.runAlgorithm(input, output, 0.5);    
		algo.printStatistics();
	}
	
	public static String fileToPath(String filename) throws UnsupportedEncodingException{
		URL url = MainTestSPAM_saveToFile.class.getResource(filename);
		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}
}