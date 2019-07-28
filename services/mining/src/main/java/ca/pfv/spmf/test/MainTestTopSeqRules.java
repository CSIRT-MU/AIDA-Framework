package ca.pfv.spmf.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import ca.pfv.spmf.algorithms.sequential_rules.topseqrules_and_tns.AlgoTopSeqRules;
import ca.pfv.spmf.datastructures.redblacktree.RedBlackTree;
import ca.pfv.spmf.input.sequence_database_array_integers.SequenceDatabase;

/**
 *  * Example of how to use the TopSeqRules algorithm in source code.
 * @author Philippe Fournier-Viger (Copyright 2018)
 */
public class MainTestTopSeqRules {

	public static void main(String [] arg) throws IOException{
		// load database
		SequenceDatabase sequenceDatabase = new SequenceDatabase(); 
		try {
			sequenceDatabase.loadFile(fileToPath("contextPrefixSpan.txt"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		sequenceDatabase.printDatabaseStats();
		 
		int k = 3;
		double minconf = 0.5;
		
//
		AlgoTopSeqRules algo = new AlgoTopSeqRules();
		
		
//		// This optional parameter allows to specify the maximum number of items in the 
//		// left side (antecedent) of rules found:
//		algo.setMaxAntecedentSize(1);  // optional
		
//		// This optional parameter allows to specify the maximum number of items in the 
//		// right side (consequent) of rules found:
//		algo.setMaxConsequentSize(1);  // optional
		
		RedBlackTree<ca.pfv.spmf.algorithms.sequential_rules.topseqrules_and_tns.Rule> rules 
		 = algo.runAlgorithm(k, sequenceDatabase, minconf);
		algo.printStats();
		algo.writeResultTofile(".//output.txt");   // to save results to file
	}
	
	public static String fileToPath(String filename) throws UnsupportedEncodingException{
		URL url = MainTestTopSeqRules.class.getResource(filename);
		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}
}
