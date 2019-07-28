package ca.pfv.spmf.algorithms.clustering.kmeans;
/* This file is copyright (c) 2008-2013 Philippe Fournier-Viger
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ca.pfv.spmf.algorithms.clustering.distanceFunctions.DistanceFunction;
import ca.pfv.spmf.algorithms.clustering.instancereader.AlgoInstanceFileReader;
import ca.pfv.spmf.patterns.cluster.ClusterWithMean;
import ca.pfv.spmf.patterns.cluster.ClustersEvaluation;
import ca.pfv.spmf.patterns.cluster.DoubleArray;
import ca.pfv.spmf.patterns.cluster.DoubleArrayInstance;
import ca.pfv.spmf.tools.MemoryLogger;

/**
 * An implementation of the K-means algorithm (J. MacQueen, 1967). 
 * <br/><br/>
 * 
 * The K-means  algorithm steps are (text from Wikipedia) : 1) Choose the number of clusters, k.
 * * 2) Randomly generate k clusters and determine the cluster centers, or directly
 * generate k random points as cluster centers. 3) Assign each point to the
 * nearest cluster center. 4) Recompute the new cluster centers. 5) Repeat the two
 * previous steps until some convergence criterion is met (usually that the
 * assignment hasn't changed).
 * 
 * @author Philippe Fournier-Viger
 */

public class AlgoKMeans {

	// The list of clusters generated
	protected List<ClusterWithMean> clusters = null;
	
	// A random number generator because K-Means is a randomized algorithm
	protected final static Random random = new Random(System.currentTimeMillis());
	
	// For statistics
	protected long startTimestamp; // the start time of the latest execution
	protected long endTimestamp;  // the end time of the latest execution
	long iterationCount; // the number of iterations that was performed
	
	/* The distance function to be used for clustering */
	protected DistanceFunction distanceFunction = null;
	
	/** The names of the attributes **/
	private List<String> attributeNames = null;

	/**
	 * Default constructor
	 */
	public AlgoKMeans() { 
		
	}
	
	/**
	 * Run the K-Means algorithm
	 * @param inputFile an input file path containing a list of vectors of double values
	 * @param k the parameter k
	 * @param distanceFunction 
	 * @param separator the character used to separate double values in the input file
	 * @return a list of clusters (some of them may be empty)
	 * @throws IOException exception if an error while writing the file occurs
	 */
	public List<ClusterWithMean> runAlgorithm(String inputFile, int k, DistanceFunction distanceFunction, String separator) throws NumberFormatException, IOException {
		// record the start time
		startTimestamp =  System.currentTimeMillis();
		// reset the number of iterations
		iterationCount =0;
		
		this.distanceFunction = distanceFunction;
		
		// Structure to store the vectors from the file
		List<DoubleArray> instances;
		
		// variables to store the minimum and maximum values in vectors
		double minValue = Integer.MAX_VALUE;
		double maxValue = 0;
		
		// Read the input file
		AlgoInstanceFileReader reader = new AlgoInstanceFileReader();
		instances = reader.runAlgorithm(inputFile, separator);
		int dimensionCount = reader.getAttributeNames().size();
		attributeNames = reader.getAttributeNames();
		
		// For each instance
		for(DoubleArray instance : instances){
			for(double value : instance.data){
				if(value < minValue){
					minValue = value;
				}
				if(value > maxValue){
					maxValue = value;
				}
			}
		}
		
		// Get the size of vectors
		int vectorsSize = instances.get(0).data.length;
		
		// if the user ask for only one cluster!
		if(k == 1) {
			// Create a single cluster and return it 
			clusters = new ArrayList<ClusterWithMean>();
			ClusterWithMean cluster = new ClusterWithMean(vectorsSize);
			for(DoubleArray vector : instances) {
				cluster.addVector(vector);
			}
			cluster.setMean(new DoubleArray(new double[vectorsSize]));
			cluster.recomputeClusterMean();
			clusters.add(cluster);
			
			// check memory usage
			MemoryLogger.getInstance().checkMemory();
			
			// record end time
			endTimestamp =  System.currentTimeMillis();
			return clusters;
		}
		
		// SPECIAL CASE: If only one vector
		if (instances.size() == 1) {
			// Create a single cluster and return it 
			clusters = new ArrayList<ClusterWithMean>();
			DoubleArray vector = instances.get(0);
			ClusterWithMean cluster = new ClusterWithMean(vectorsSize);
			cluster.addVector(vector);
			cluster.recomputeClusterMean();
			cluster.setMean(new DoubleArray(new double[vectorsSize]));
			clusters.add(cluster);
			
			// check memory usage
			MemoryLogger.getInstance().checkMemory();
			
			// record end time
			endTimestamp =  System.currentTimeMillis();
			return clusters;
		}
		
		// if the user asks for more cluster then there is data,
		// we set k to the number of data points.
		if(k > instances.size()) {
			k = instances.size();
		}

		applyAlgorithm(k, distanceFunction, instances, minValue, maxValue,
				vectorsSize); 

		// check memory usage
		MemoryLogger.getInstance().checkMemory();
		
		// record end time
		endTimestamp =  System.currentTimeMillis();
		
		// return the clusters
		return clusters;
	}

	/**
	 * Apply the K-means algorithm
	 * @param k the parameter k
	 * @param distanceFunction a distance function
	 * @param vectors the list of initial vectors
	 * @param minValue the min value
	 * @param maxValue the max value
	 * @param vectorsSize  the vector size
	 */
	void applyAlgorithm(int k, DistanceFunction distanceFunction,
			List<DoubleArray> vectors, double minValue, double maxValue,
			int vectorsSize) {
		// apply kmeans
		clusters = applyKMeans(k, distanceFunction, vectors, minValue, maxValue, vectorsSize);
	}
	
	/**
	 * Apply the K-means algorithm
	 * @param k the parameter k
	 * @param distanceFunction a distance function
	 * @param vectors the list of initial vectors
	 * @param minValue the min value
	 * @param maxValue the max value
	 * @param vectorsSize  the vector size
	 */
	List<ClusterWithMean> applyKMeans(int k, DistanceFunction distanceFunction,
			List<DoubleArray> vectors, double minValue, double maxValue,
			int vectorsSize) {
		List<ClusterWithMean> newClusters = new ArrayList<ClusterWithMean>();
		
		// SPECIAL CASE: If only one vector
		if (vectors.size() == 1) {
			// Create a single cluster and return it 
			DoubleArray vector = vectors.get(0);
			ClusterWithMean cluster = new ClusterWithMean(vectorsSize);
			cluster.addVector(vector);
			newClusters.add(cluster);
			return newClusters;
		}
		
		// (1) Randomly generate k empty clusters with a random mean (cluster
		// center)
		for(int i=0; i< k; i++){
			DoubleArray meanVector = generateRandomVector(minValue, maxValue, vectorsSize);
			ClusterWithMean cluster = new ClusterWithMean(vectorsSize);
			cluster.setMean(meanVector);
			newClusters.add(cluster);
		}

		// (2) Repeat the two next steps until the assignment hasn't changed
		boolean changed;
		while(true) {
			iterationCount++;
			changed = false;
			// (2.1) Assign each point to the nearest cluster center.

			// / for each vector
			for (DoubleArray vector : vectors) {
				// find the nearest cluster and the cluster containing the item
				ClusterWithMean nearestCluster = null;
				ClusterWithMean containingCluster = null;
				double distanceToNearestCluster = Double.MAX_VALUE;

				// for each cluster
				for (ClusterWithMean cluster : newClusters) {
					// calculate the distance of the cluster mean to the vector
					double distance = distanceFunction.calculateDistance(cluster.getmean(), vector);
					// if it is the smallest distance until now, record this cluster
					// and the distance
					if (distance < distanceToNearestCluster) {
						nearestCluster = cluster;
						distanceToNearestCluster = distance;
					}
					// if the cluster contain the vector already,
					// remember that too!
					if (cluster.contains(vector)) {
						containingCluster = cluster;
					}
				}

				// if the nearest cluster is not the cluster containing
				// the vector
				if (containingCluster != nearestCluster) {
					// remove the vector from the containing cluster
					if (containingCluster != null) {
						containingCluster.remove(vector);
					}
					// add the vector to the nearest cluster
					nearestCluster.addVector(vector);
					changed = true;
				}
			}

			// check the memory usage
			MemoryLogger.getInstance().checkMemory();
			
			if(!changed){     // exit condition for main loop
				break;
			}
			
			// (2.2) Recompute the new cluster means
			for (ClusterWithMean cluster : newClusters) {
				cluster.recomputeClusterMean();
			}
		}
		
		return newClusters;
	}

	/**
	 * Generate a random vector.
	 * @param minValue  the minimum value allowed
	 * @param maxValue  the maximum value allowed
	 * @param vectorsSize the desired vector size
	 * @return the random vector
	 */
	DoubleArray generateRandomVector(double minValue, double maxValue,
			int vectorsSize) {
		// create a new vector
		double[] vector = new double[vectorsSize];
		// for each position generate a random number
		for(int i=0; i < vectorsSize; i++){
			vector[i] = (random.nextDouble() * (maxValue - minValue)) + minValue;
		}
		// return the vector
		return new DoubleArray(vector);
	}


	/**
	 * Save the clusters to an output file
	 * @param output the output file path
	 * @throws IOException exception if there is some writing error.
	 */
	public void saveToFile(String output) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));
		
		// First, we will print the attribute names
		for(String attributeName : attributeNames){
			writer.write("@ATTRIBUTEDEF=" + attributeName);
			writer.newLine();
		}
		
		// for each cluster
		for(int i=0; i< clusters.size(); i++){
			// if the cluster is not empty
			if(clusters.get(i).getVectors().size() >= 1){
				// write the cluster
				writer.write(clusters.get(i).toString());
				// if not the last cluster, add a line return
				if(i < clusters.size()-1){
					writer.newLine();
				}
			}
		}
		// close the file
		writer.close();
	}
	
	/**
	 * Print statistics of the latest execution to System.out.
	 */
	public void printStatistics() {
		System.out.println("========== KMEANS - SPMF 2.09 - STATS ============");
		System.out.println(" Distance function: " + distanceFunction.getName());
		System.out.println(" Total time ~: " + (endTimestamp - startTimestamp)
				+ " ms");
		System.out.println(" SSE (Sum of Squared Errors) (lower is better) : " + ClustersEvaluation.calculateSSE(clusters, distanceFunction));
		System.out.println(" Max memory:" + MemoryLogger.getInstance().getMaxMemory() + " mb ");
		System.out.println(" Iteration count: " + iterationCount);
		System.out.println("=====================================");
	}

}
