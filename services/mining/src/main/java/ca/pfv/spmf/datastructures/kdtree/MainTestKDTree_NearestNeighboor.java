package ca.pfv.spmf.datastructures.kdtree;

import java.util.ArrayList;
import java.util.List;
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

import ca.pfv.spmf.patterns.cluster.DoubleArray;

/**
 * This test show how to use the KDTree structure to find 
 * the nearest neighboor to a given point and is intended for testing the KDtree structure
 * by developers.
 * 
 * @author Philippe Fournier-Viger
 */
class MainTestKDTree_NearestNeighboor {

	public static void main(String[] args) {
		// create an empty kd tree 
		KDTree tree = new KDTree();
		
		// Use a list of point to create the kd-tree
		List<DoubleArray> points = new ArrayList<DoubleArray>();
		points.add(new DoubleArray(new double[]{2d,3d}));
		points.add(new DoubleArray(new double[]{5d,4d}));
		points.add(new DoubleArray(new double[]{9d,6d}));
		points.add(new DoubleArray(new double[]{4d,7d}));
		points.add(new DoubleArray(new double[]{8d,1d}));
		points.add(new DoubleArray(new double[]{7d,2d}));
		
		// insert the points into the tree.
		tree.buildtree(points);
		
		// print the tree for debugging purposes
		System.out.println("\nTREE: \n" + tree.toString() + "  \n\n Number of elements in tree: " + tree.size());
	
		DoubleArray query = new DoubleArray(new double[]{7.9d,4d});
		DoubleArray nearestpoint  = tree.nearest(query);
		System.out.println("The nearest neighboor is: :" + nearestpoint);
		
//		// find the best answer by brute force to verify the result
//		double min = Double.MAX_VALUE;
//		double[] closest = null;
//		for(int i=0; i< points.length; i++){
//			double dist = distance(query, points[i]);
//			if( dist < min){
//				min = dist;
//				closest = points[i];
//			}
//		}		
//		System.out.println(" good answer :" + toString(closest));
//		System.out.println();
//		
//		
	}
	
	public static String toString(double [] values){
		StringBuilder buffer = new StringBuilder();
		for(Double element : values ){
			buffer.append("   " + element);
		}
		return buffer.toString();
	}
	
	private static double distance(double[] node1, double[] node2) {
		double sum = 0;
		for(int i=0; i< node1.length; i++){
			sum +=  Math.pow(node1[i] - node2[i], 2);
		}
		return Math.sqrt(sum);
	}

}
