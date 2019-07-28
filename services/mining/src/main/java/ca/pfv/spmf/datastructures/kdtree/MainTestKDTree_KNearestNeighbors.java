package ca.pfv.spmf.datastructures.kdtree;
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

import java.util.ArrayList;
import java.util.List;

import ca.pfv.spmf.datastructures.redblacktree.RedBlackTree;
import ca.pfv.spmf.patterns.cluster.DoubleArray;

/**
 * This test show how to use the KDTree structure to find 
 * the K nearest neighbors to a given point and is intended for testing the KDtree structure
 * by developers.
* 
* @see KDTree
 * @author Philippe Fournier-Viger
 */
class MainTestKDTree_KNearestNeighbors {

	public static void main(String[] args) {
		// create kd tree with two dimensions  and of type double
		KDTree tree = new KDTree();
		
		// Use a list of point to create the kd-tree
		List<DoubleArray> points = new ArrayList<DoubleArray>();
		points.add(new DoubleArray(new double[]{2d,3d}));
		points.add(new DoubleArray(new double[]{5d,4d}));
		points.add(new DoubleArray(new double[]{9d,6d}));
		points.add(new DoubleArray(new double[]{4d,7d}));
		points.add(new DoubleArray(new double[]{8d,1d}));
		points.add(new DoubleArray(new double[]{7d,2d}));
		
		// Create a KD Tree with the points
		tree.buildtree(points);
		
		// Print the tree for debugging
		System.out.println("\nTREE: \n" + tree.toString() + "  \n\n Number of elements in tree: " + tree.size());
	
		// Find the nearest neighboor to the point 4,4
		DoubleArray query = new DoubleArray(new double[]{4d,4d});
		int k = 3;
		RedBlackTree<KNNPoint> result = tree.knearest(query, k);
		
		System.out.println("THE K NEAREST NEIGHBOORS ARE : " + result.toString());	
	}
	
	public static String toString(double [] values){
		StringBuilder buffer = new StringBuilder();
		for(Double element : values ){
			buffer.append("   " + element);
		}
		return buffer.toString();
	}
}
