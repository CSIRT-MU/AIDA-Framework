package ca.pfv.spmf.algorithmmanager.descriptions;

import java.io.IOException;

import ca.pfv.spmf.algorithmmanager.DescriptionOfAlgorithm;
import ca.pfv.spmf.algorithmmanager.DescriptionOfParameter;
import ca.pfv.spmf.algorithms.sequentialpatterns.fournier2008_seqdim.AlgoFournierViger08;
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
 * This class describes the AlgoFournierViger08 algorithm. 
 * It is designed to be used by the graphical and command line interface.
 * 
 * @see AlgoFournierViger08
 * @author Philippe Fournier-Viger
 */
public class DescriptionAlgoFournier08ClosedTime extends DescriptionOfAlgorithm {

	/**
	 * Default constructor
	 */
	public DescriptionAlgoFournier08ClosedTime(){
	}

	@Override
	public String getName() {
		return "Fournier08-Closed+time";
	}

	@Override
	public String getAlgorithmCategory() {
		return "SEQUENTIAL PATTERN MINING";
	}

	@Override
	public String getURLOfDocumentation() {
		return "http://www.philippe-fournier-viger.com/spmf/ClosedSequentialPatterns_TimeConstraints.php";
	}

	@Override
	public void runAlgorithm(String[] parameters, String inputFile, String outputFile) throws IOException {
		// Get the parameter values
		double minsup = getParamAsDouble(parameters[0]); 
		double minInterval = getParamAsDouble(parameters[1]);
		double maxInterval = getParamAsDouble(parameters[2]);
		double minWholeInterval = getParamAsDouble(parameters[3]);
		double maxWholeInterval = getParamAsDouble(parameters[4]);

		ca.pfv.spmf.algorithms.sequentialpatterns.fournier2008_seqdim.SequenceDatabase database = new ca.pfv.spmf.algorithms.sequentialpatterns.fournier2008_seqdim.SequenceDatabase();
		database.loadFile(inputFile);

		// Apply algorithm
		AlgoFournierViger08 algo = new AlgoFournierViger08(minsup, minInterval,
				maxInterval, minWholeInterval, maxWholeInterval, null, true,
				true);

		algo.runAlgorithm(database, outputFile);
		algo.printStatistics();
	}

	@Override
	public DescriptionOfParameter[] getParametersDescription() {
        
		DescriptionOfParameter[] parameters = new DescriptionOfParameter[5];
		parameters[0] = new DescriptionOfParameter("Minsup (%)", "(e.g. 0.4 or 40%)", Double.class, false);
		parameters[1] = new DescriptionOfParameter("Min time interval", "(e.g. 0 itemsets)", Double.class, false);
		parameters[2] = new DescriptionOfParameter("Max time interval", "(e.g. 2 itemsets)", Double.class, false);
		parameters[3] = new DescriptionOfParameter("Min whole time interval", "(e.g. 0 itemsets)", Double.class, false);
		parameters[4] = new DescriptionOfParameter("Max whole time interval", "(e.g. 2 itemsets)", Double.class, false);
		return parameters;
	}

	@Override
	public String getImplementationAuthorNames() {
		return "Philippe Fournier-Viger";
	}

	@Override
	public String[] getInputFileTypes() {
		return new String[]{"Database of instances","Sequence database", "Sequence Database with timestamps"};
	}

	@Override
	public String[] getOutputFileTypes() {
		return new String[]{"Patterns", "Sequential patterns", "Frequent Sequential patterns", "Frequent closed sequential patterns with timestamps"};
	}
	
}
