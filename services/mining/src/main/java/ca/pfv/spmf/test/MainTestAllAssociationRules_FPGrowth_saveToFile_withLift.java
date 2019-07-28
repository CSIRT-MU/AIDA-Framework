package ca.pfv.spmf.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AlgoAgrawalFaster94;
import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth.AlgoFPGrowth;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemsets;
/**
 * Example of how to mine all association rules with FPGROWTH
 * and use the lift, and save the result to a file, 
 * from the source code.
 * 
 * @author Philippe Fournier-Viger (Copyright 2013)
 */
public class MainTestAllAssociationRules_FPGrowth_saveToFile_withLift {

	public static void main(String [] arg) throws IOException{
		String input = fileToPath("contextIGB.txt");
		String output = ".//output.txt";
		
		// By changing the following lines to some other values
		// it is possible to restrict the number of items in the antecedent and
		// consequent of rules
		int maxConsequentLength = 40;
		int maxAntecedentLength = 40;
		
		// STEP 1: Applying the FP-GROWTH algorithm to find frequent itemsets
		double minsupp = 0.5;
		AlgoFPGrowth fpgrowth = new AlgoFPGrowth();
		fpgrowth.setMaximumPatternLength(maxAntecedentLength + maxConsequentLength);
		Itemsets patterns = fpgrowth.runAlgorithm(input, null, minsupp);
//		patterns.printItemsets(database.size());
		int databaseSize = fpgrowth.getDatabaseSize();
		fpgrowth.printStats();
		
		// STEP 2: Generating all rules from the set of frequent itemsets (based on Agrawal & Srikant, 94)
		double  minlift = 0.1;
		double  minconf = 0.50; 
		AlgoAgrawalFaster94 algoAgrawal = new AlgoAgrawalFaster94();
		algoAgrawal.setMaxConsequentLength(maxConsequentLength);
		algoAgrawal.setMaxAntecedentLength(maxAntecedentLength);
		algoAgrawal.runAlgorithm(patterns, output, databaseSize, minconf, minlift);
		algoAgrawal.printStats();
	}
	
	public static String fileToPath(String filename) throws UnsupportedEncodingException{
		URL url = MainTestAllAssociationRules_FPGrowth_saveToFile_withLift.class.getResource(filename);
		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}
}
