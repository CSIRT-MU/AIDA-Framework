package ca.pfv.spmf.algorithms.sequenceprediction.ipredict.predictor.TDAG;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/*
 * This file is copyright (c) Ted Gueniche 
 * <ted.gueniche@gmail.com>
 *
 * This file is part of the IPredict project
 * (https://github.com/tedgueniche/IPredict).
 *
 * IPredict is distributed under The MIT License (MIT).
 * You may obtain a copy of the License at
 * https://opensource.org/licenses/MIT 
 */
public class TDAGNode {

	/**
	 * Symbol of the Node
	 */
	public Integer symbol;
	
	/**
	 * Incoming weight
	 */
	public Integer inCount;
	
	/**
	 * Outgoing weight
	 */
	public Integer outCount;
	
	/**
	 * List of symbols from Root (included) to this node (included)
	 */
	public List<Integer> pathFromRoot;
	
	/**
	 * List of children of this node
	 */
	public HashMap<Integer, TDAGNode> children;
	
	/**
	 * Probability of getting this node given its parent
	 */
	public Double score;
	
	
	/**
	 * Construct a node with the given symbol
	 * @param symbol Symbol of the node
	 */
	public TDAGNode(Integer symbol, List<Integer> parentPath) {
		this.symbol = symbol;
		inCount = 0;
		outCount = 0;
		children = new HashMap<Integer, TDAGNode>();
		
		pathFromRoot = new ArrayList<Integer>(parentPath);
		pathFromRoot.add(symbol);
	}
	
	/**
	 * Create and Add a new child to this node.
	 * @param item Item to use to create the child node.
	 * @return Returns the new child.
	 */
	public TDAGNode addChild(Integer symbol) {
		
		//If necessary: create and insert the node in the children
		//Else extract the existing child from the children
		TDAGNode child = children.get(symbol);
		if(child == null) {
			child = new TDAGNode(symbol, pathFromRoot);
			children.put(symbol, child);
		}
		
		//increments this node's outCount
		outCount++;
		
		//increments the new child inCount to 1
		child.inCount++;
		
		return child;
	}
	
	@Override
	public String toString() {
		return symbol + "("+ inCount + "," + outCount +")";
	}

	/**
	 * Return true if the i-th node has a child
	 * @param child the position i
	 * @return true if has a child. otherwise false.
	 */
	public boolean hasChild(Integer child) {
		return this.children.get(child) != null;
	}
}
