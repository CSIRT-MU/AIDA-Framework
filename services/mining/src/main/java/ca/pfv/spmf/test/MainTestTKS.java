package ca.pfv.spmf.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.PriorityQueue;

import ca.pfv.spmf.algorithms.sequentialpatterns.spam.AlgoTKS;
import ca.pfv.spmf.algorithms.sequentialpatterns.spam.PatternTKS;


/**
 * Example of how to use the TKS algorithm in source code.
 * @author Philippe Fournier-Viger
 */
public class MainTestTKS {

	public static void main(String [] arg) throws IOException{ 

		// Load a sequence database
		String input = fileToPath("contextPrefixSpan.txt");
		String output = ".//output.txt";
		
		int k=5;
		
		// Create an instance of the algorithm 
		AlgoTKS algo = new AlgoTKS(); 
		
		// This optional parameter allows to specify the minimum pattern length:
//		algo.setMinimumPatternLength(0);  // optional

		// This optional parameter allows to specify the maximum pattern length:
//		algo.setMaximumPatternLength(4);  // optional
		
		// This optional parameter allows to specify constraints that some
		// items MUST appear in the patterns found by TKS
		// E.g.: This requires that items 1 and 3 appears in every patterns found
//		algo.setMustAppearItems(new int[] {1,3});
		
		// This optional parameter allows to specify the max gap between two
		// itemsets in a pattern. If set to 1, only patterns of contiguous itemsets
		// will be found (no gap).
		//algo.setMaxGap(1);
		
	    // if you set the following parameter to true, the sequence ids of the sequences where
        // each pattern appears will be shown in the result
//		algo.showSequenceIdentifiersInOutput(true);
		
		// execute the algorithm, which returns some patterns
		PriorityQueue<PatternTKS> patterns = algo.runAlgorithm(input, output, k);  
		// save results to file
		algo.writeResultTofile(output);   
		algo.printStatistics();

	}
	
	public static String fileToPath(String filename) throws UnsupportedEncodingException{
		URL url = MainTestTKS.class.getResource(filename);
		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}
}