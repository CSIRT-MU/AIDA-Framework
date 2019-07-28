package ca.pfv.spmf.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import ca.pfv.spmf.algorithms.frequentpatterns.upgrowth_ihup.AlgoIHUP;

/**
 * Example of how to use the UPGrowth algorithm 
 * from the source code.
 * @author (c) Prashant Barhate, 2014
 */
public class MainTestIHUP {

	public static void main(String [] arg) throws IOException{
		
		String input = fileToPath("DB_Utility.txt");
		String output = ".//output.txt";

		int min_utility = 30;  //
		
		// Applying the IHUP algorithm
		AlgoIHUP algo = new AlgoIHUP();
		algo.runAlgorithm(input, output, min_utility);
		algo.printStats();
	}

	public static String fileToPath(String filename) throws UnsupportedEncodingException{
		URL url = MainTestIHUP.class.getResource(filename);
		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}
}
