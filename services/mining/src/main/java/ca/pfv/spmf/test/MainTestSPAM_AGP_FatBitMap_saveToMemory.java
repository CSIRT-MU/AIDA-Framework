package ca.pfv.spmf.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.AlgoSPAM_AGP;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.dataStructures.creators.AbstractionCreator;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.dataStructures.creators.AbstractionCreator_Qualitative;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.dataStructures.database.SequenceDatabase;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.idLists.creators.IdListCreator;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.idLists.creators.IdListCreator_FatBitmap;

/**
 * Example of how to use the algorithm SPAM, saving the results in the 
 * main  memory
 * 
 * @author agomariz
 */
public class MainTestSPAM_AGP_FatBitMap_saveToMemory {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // Load a sequence database
        double support = 0.5;

        boolean keepPatterns = true;
        boolean verbose = false;
        
        // if you set the following parameter to true, the sequence ids of the sequences where
        // each pattern appears will be shown in the result
        boolean outputSequenceIdentifiers = false; 

        AbstractionCreator abstractionCreator = AbstractionCreator_Qualitative.getInstance();

        IdListCreator idListCreator = IdListCreator_FatBitmap.getInstance();

        SequenceDatabase sequenceDatabase = new SequenceDatabase(abstractionCreator, idListCreator);

        sequenceDatabase.loadFile(fileToPath("contextPrefixSpan.txt"), support);

        System.out.println(sequenceDatabase.toString());

        AlgoSPAM_AGP algorithm = new AlgoSPAM_AGP(support);

        algorithm.runAlgorithm(sequenceDatabase, keepPatterns, verbose, null,outputSequenceIdentifiers);
        System.out.println("Minimum support (relative) = "+support);
        System.out.println(algorithm.getNumberOfFrequentPatterns() + " frequent patterns.");

        System.out.println(algorithm.printStatistics());
    }

    public static String fileToPath(String filename) throws UnsupportedEncodingException {
        URL url = MainTestSPADE_AGP_FatBitMap_saveToFile.class.getResource(filename);
        return java.net.URLDecoder.decode(url.getPath(), "UTF-8");
    }
}
