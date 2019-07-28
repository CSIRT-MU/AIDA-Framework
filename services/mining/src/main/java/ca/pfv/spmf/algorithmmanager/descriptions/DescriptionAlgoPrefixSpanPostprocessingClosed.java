package ca.pfv.spmf.algorithmmanager.descriptions;

import java.io.IOException;

import ca.pfv.spmf.algorithmmanager.DescriptionOfAlgorithm;
import ca.pfv.spmf.algorithmmanager.DescriptionOfParameter;
import ca.pfv.spmf.algorithms.sequentialpatterns.clospan_AGP.AlgoCloSpan;
import ca.pfv.spmf.algorithms.sequentialpatterns.prefixSpan_AGP.AlgoPrefixSpan_AGP;
/* This file is copyright (c) 2008-2016 Philippe Fournier-Viger
* 
* This file is part of the SPMF DATA MINING SOFTWARE
* (http://www.philippe-fournier-viger.com/spmf).
* 
* SPMF is free software: you can redistribute it and/or modify it under the
* terms of the GNU General Public License as published by the Free Software
* Foundation, either version 3 of the License, or (at your option) any later
* version.
* 
* SPMF is distributed in the hope that it will be useful, but WITHOUT ANY
* WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
* A PARTICULAR PURPOSE. See the GNU General Public License for more details.
* You should have received a copy of the GNU General Public License along with
* SPMF. If not, see <http://www.gnu.org/licenses/>.
*/

/**
 * This class describes the PrefixSpan algorithm parameters (version implemented 
 * by Antonio G.P. which applies post-processing to find closed sequential patterns). 
 * It is designed to be used by the graphical and command line interface.
 * 
 * @see AlgoPrefixSpan_AGP
 * @author Philippe Fournier-Viger
 */
public class DescriptionAlgoPrefixSpanPostprocessingClosed extends DescriptionOfAlgorithm {

	/**
	 * Default constructor
	 */
	public DescriptionAlgoPrefixSpanPostprocessingClosed(){
	}

	@Override
	public String getName() {
		return "PrefixSpan_PostProcessingClosed";
	}

	@Override
	public String getAlgorithmCategory() {
		return "SEQUENTIAL PATTERN MINING";
	}

	@Override
	public String getURLOfDocumentation() {
		return "http://www.philippe-fournier-viger.com/spmf/SPAM_PrefixSpan_Closed.php";
	}

	@Override
	public void runAlgorithm(String[] parameters, String inputFile, String outputFile) throws IOException {
		ca.pfv.spmf.algorithms.sequentialpatterns.clospan_AGP.items.creators.AbstractionCreator abstractionCreator = ca.pfv.spmf.algorithms.sequentialpatterns.clospan_AGP.items.creators.AbstractionCreator_Qualitative
				.getInstance();

		double minSupport = getParamAsDouble(parameters[0]);
		
		boolean outputSeqIdentifiers = false;
		if (parameters.length >=2 && "".equals(parameters[1]) == false) {
			outputSeqIdentifiers = getParamAsBoolean(parameters[1]);
		}

		AlgoCloSpan algo = new AlgoCloSpan(minSupport, abstractionCreator, true, false);

		ca.pfv.spmf.algorithms.sequentialpatterns.clospan_AGP.items.SequenceDatabase sd = new ca.pfv.spmf.algorithms.sequentialpatterns.clospan_AGP.items.SequenceDatabase();
		sd.loadFile(inputFile, minSupport);

		algo.runAlgorithm(sd, true, false, outputFile,outputSeqIdentifiers);
		System.out.println(algo.printStatistics());
	}

	@Override
	public DescriptionOfParameter[] getParametersDescription() {
        
		DescriptionOfParameter[] parameters = new DescriptionOfParameter[2];
		parameters[0] = new DescriptionOfParameter("Minsup (%)", "(e.g. 0.4 or 40%)", Double.class, false);
		parameters[1] = new DescriptionOfParameter("Show sequence ids?", "(default: false)", Boolean.class, true);
		return parameters;
	}

	@Override
	public String getImplementationAuthorNames() {
		return "Antonio Gomariz Penalver";
	}

	@Override
	public String[] getInputFileTypes() {
		return new String[]{"Database of instances","Sequence database", "Simple sequence database"};
	}

	@Override
	public String[] getOutputFileTypes() {
		return new String[]{"Patterns", "Sequential patterns", "Frequent closed sequential patterns"};
	}
	
}
