package ca.pfv.spmf.algorithms.frequentpatterns.upgrowth_ihup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is an implementation of the IHUP-Tree used by IHUP algorithm.
 * Copyright (c) 2014 Prashant Barhate
 * 
 * This file is part of the SPMF DATA MINING SOFTWARE *
 * (http://www.philippe-fournier-viger.com/spmf).
 * 
 * SPMF is free software: you can redistribute it and/or modify it under the *
 * terms of the GNU General Public License as published by the Free Software *
 * Foundation, either version 3 of the License, or (at your option) any later *
 * version. SPMF is distributed in the hope that it will be useful, but WITHOUT
 * ANY * WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * SPMF. If not, see <http://www.gnu.org/licenses/>.
 * 
 * @see AlgoIHUP
 * 
 * @author Prashant Barhate
 */

public class IHUPTree {

	// List of items in the header table
	List<Integer> headerList = null;

	// flag that indicate if the tree has more than one path
	boolean hasMoreThanOnePath = false;

	// List of pairs (item, Utility) of the header table
	Map<Integer, UPNode> mapItemNodes = new HashMap<Integer, UPNode>();

	// root of the tree
	UPNode root = new UPNode(); // null node

	// Map that indicates the last node for each item using the node links
	// key: item value: an fp tree node (added by Philippe)
	Map<Integer, UPNode> mapItemLastNode = new HashMap<Integer, UPNode>();

	public IHUPTree() {

	}

	/**
	 * Method for adding a transaction to the IHUP-tree (for the initial
	 * construction of the IHUP-Tree).
	 * 
	 * @param transaction    reorganised transaction
	 * @param RTU   reorganised transaction utility
	 */
	public void addTransaction(List<Item> transaction, int RTU) {
		UPNode currentNode = root;

		// For each item in the transaction
		for (int i = 0; i < transaction.size(); i++) {

			int item = transaction.get(i).getName();
			// int itm=Integer.parseInt(item);
			// look if there is a node already in the IHUP-Tree
			UPNode child = currentNode.getChildWithID(item);

			if (child == null) {
				int nodeUtility = (RTU);
				
				// there is no node, we create a new one
				currentNode = insertNewNode(currentNode, item, nodeUtility);
			} else {
				// there is a node already, we update it
				int currentNU = child.nodeUtility; // current node utility
				int nodeUtility = currentNU + RTU; 
				child.count++;
				child.nodeUtility = nodeUtility;
				currentNode = child;
			}
		}
	}

	/**
	 * Add a transaction to the UP-Tree (for a local UP-Tree)
	 * @param localPath the path to be inserted
	 * @param pathUtility the path utility
	 * @param mapMinimumItemUtility the map storing minimum item utility
	 */
	public void addLocalTransaction(List<Integer> localPath, int pathUtility) {

		UPNode currentlocalNode = root;
		
		// For each item in the transaction
		for (int i = 0; i < localPath.size(); i++) {
			int item = localPath.get(i);

			// look if there is a node already in the UP-Tree
			UPNode child = currentlocalNode.getChildWithID(item);

			if (child == null) {
				int nodeUtility = (pathUtility); ;
				
				// there is no node, we create a new one
				currentlocalNode = insertNewNode(currentlocalNode, item, nodeUtility);
			} else {
				// there is a node already, we update it
				int currentNU = child.nodeUtility; // current node utility
				int nodeUtility = currentNU + pathUtility;
				child.count++;
				child.nodeUtility = nodeUtility;
				currentlocalNode = child;
			}
		}
	}

	/**
	 * Insert a new node in the UP-Tree as child of a parent node
	 * @param currentlocalNode the parent node
	 * @param item the item in the new node
	 * @param nodeUtility the node utility of the new node
	 * @return the new node
	 */
	private UPNode insertNewNode(UPNode currentlocalNode, int item,	int nodeUtility) {
		// create the new node
		UPNode newNode = new UPNode();
		newNode.itemID = item;
		newNode.nodeUtility = nodeUtility;
		newNode.count = 1;
		newNode.parent = currentlocalNode;
		
		// we link the new node to its parrent
		currentlocalNode.childs.add(newNode);

		// check if more than one path
		if (!hasMoreThanOnePath && currentlocalNode.childs.size() > 1) {
			hasMoreThanOnePath = true;
		}

		// We update the header table.
		// We check if there is already a node with this id in the
		// header table
		UPNode localheadernode = mapItemNodes.get(item);
		if (localheadernode == null) { // there is not
			mapItemNodes.put(item, newNode);
			mapItemLastNode.put(item, newNode);
		} else { // there is
					// we find the last node with this id.
					// get the latest node in the tree with this item
			UPNode lastNode = mapItemLastNode.get(item);
			// we add the new node to the node link of the last node
			lastNode.nodeLink = newNode;

			// Finally, we set the new node as the last node
			mapItemLastNode.put(item, newNode);
		}
		
		// we return this node as the current node for the next loop
		// iteration
		return newNode;
	}

	/**
	 * Method for creating the list of items in the header table, in descending
	 * order of TWU.
	 * 
	 * @param mapItemToTWU
	 *            the Utilities of each item (key: item value: TWU or path utility)
	 */
	void createHeaderList(final Map<Integer, Integer> mapItemToTWU) {
		// create an array to store the header list with
		// all the items stored in the map received as parameter
		headerList = new ArrayList<Integer>(mapItemNodes.keySet());

		// sort the header table by decreasing order of utility
		Collections.sort(headerList, new Comparator<Integer>() {
			public int compare(Integer id1, Integer id2) {
				// compare the Utility
				int compare = mapItemToTWU.get(id2)
						- mapItemToTWU.get(id1);
				// if the same utility, we check the lexical ordering!
				if (compare == 0) {
					return (id1 - id2);
				}
				// otherwise we use the utility
				return compare;
			}
		});
	}

	@Override
	public String toString() {
		String output = "";
		output += "HEADER TABLE: " + mapItemNodes + " \n";
		output += "hasMoreThanOnePath: " + hasMoreThanOnePath + " \n";
		return output + toString("", root);
	}

	public String toString(String indent, UPNode node) {
		String output = indent + node.toString() + "\n";
		String childsOutput = "";
		for (UPNode child : node.childs) {
			childsOutput += toString(indent + " ", child);
		}
		return output + childsOutput;
	}

}