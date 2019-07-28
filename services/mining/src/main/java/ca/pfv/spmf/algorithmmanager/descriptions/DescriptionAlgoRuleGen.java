package ca.pfv.spmf.algorithmmanager.descriptions;

import java.io.IOException;

import ca.pfv.spmf.algorithmmanager.DescriptionOfAlgorithm;
import ca.pfv.spmf.algorithmmanager.DescriptionOfParameter;
import ca.pfv.spmf.algorithms.sequential_rules.rulegen.AlgoRuleGen;
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
 * This class describes the RuleGen algorithm parameters. 
 * It is designed to be used by the graphical and command line interface.
 * 
 * @see AlgoRuleGen
 * @author Philippe Fournier-Viger
 */
public class DescriptionAlgoRuleGen extends DescriptionOfAlgorithm {

	/**
	 * Default constructor
	 */
	public DescriptionAlgoRuleGen(){
	}

	@Override
	public String getName() {
		return "RuleGen";
	}

	@Override
	public String getAlgorithmCategory() {
		return "SEQUENTIAL RULE MINING";
	}

	@Override
	public String getURLOfDocumentation() {
		return "http://www.philippe-fournier-viger.com/spmf/RuleGen.php";
	}

	@Override
	public void runAlgorithm(String[] parameters, String inputFile, String outputFile) throws IOException {
		int minsup = getParamAsInteger(parameters[0]);
		double minconf = getParamAsDouble(parameters[1]);

		AlgoRuleGen rulegen = new AlgoRuleGen();
		rulegen.runAlgorithm(minsup, minconf, inputFile, outputFile);
		rulegen.printStats();
	}

	@Override
	public DescriptionOfParameter[] getParametersDescription() {
		DescriptionOfParameter[] parameters = new DescriptionOfParameter[2];
		parameters[0] = new DescriptionOfParameter("Minsup", "(e.g. 3 sequences)", Integer.class, false);
		parameters[1] = new DescriptionOfParameter("Minconf (%)", "(e.g. 0.6 or 60%)", Double.class, false);
		return parameters;
	}

	@Override
	public String getImplementationAuthorNames() {
		return "Philippe Fournier-Viger";
	}

	@Override
	public String[] getInputFileTypes() {
		return new String[]{"Database of instances","Sequence database", "Simple sequence database"};
	}

	@Override
	public String[] getOutputFileTypes() {
		return new String[]{"Patterns", "Sequential rules", "Ordered frequent sequential rules"};
	}
	
}
