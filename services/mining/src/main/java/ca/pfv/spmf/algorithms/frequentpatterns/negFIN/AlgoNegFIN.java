package ca.pfv.spmf.algorithms.frequentpatterns.negFIN;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import ca.pfv.spmf.tools.MemoryLogger;

/*
 ** The implementation of the "negFIN algorithm", the algorithm presented in:
 * "Nader Aryabarzan, Behrouz Minaei-Bidgoli, and Mohammad Teshnehlab. (2018). negFIN: An efficient algorithm for fast mining frequent itemsets. Expert System with Applications, 105, 129–143"
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
 *
 * You should have received a copy of the GNU General Public License along with
 * SPMF. If not, see <http://www.gnu.org/licenses/>.
 */
 
 /**
 * This implementation was obtained by converting the C++ code of the negFIN algorithm to Java.
 * The C++ code of this algorithm was provided by Nader Aryabarzan, available on GitHub via https://github.com/aryabarzan/dFIN and https://github.com/aryabarzan/negFIN/ respectively. 
 *
 * Both the C++/Java code of the negFIN algorithms are respectively based on the C++/Java code of the "FIN algorithm", the algorithm which is presented in:
 * "Z. H. Deng and S. L. Lv. (2014). Fast mining frequent itemsets using Nodesets. Expert System with Applications, 41, 4505–4512"
 * 
 *
 * @author Nader Aryabarzan (Copyright 2018)
 * @Email aryabarzan@aut.ac.ir or aryabarzan@gmail.com
 */

public class AlgoNegFIN {

	// the start time and end time of the last algorithm execution
	long startTimestamp;
	long endTimestamp;

	// number of itemsets found
	int outputCount = 0;

	// object to write the output file
	BufferedWriter writer = null;

	public int[][] bf;
	public int bf_cursor;
	public int bf_size;
	public int bf_col;
	public int bf_currentSize;

	public int numOfFItem; // Number of items
	public int minSupport; // minimum support
	public Item[] item; // list of items sorted by support

	// public FILE out;
	public int[] result; // the current itemset
	public int resultLen = 0; // the size of the current itemset
	public int resultCount = 0;
	public int nlLenSum = 0; // node list length of the current itemset

	// Tree stuff
	public BMCTreeNode bmcTreeRoot;
	public NodeListTreeNode nlRoot;
	public int[] itemsetCount;

	public int[] nlistBegin;
	public int nlistCol;
	public int[] nlistLen;
	public int firstNlistBegin;
	public int bmcTreeNodeCount;
	public int[] supportDict;
	public BitSet[] bitmapCodeDict;


	public int[] sameItems;
	public int nlNodeCount;

	/**
	 * Comparator to sort items by decreasing order of frequency
	 */
	static Comparator<Item> comp = new Comparator<Item>() {
		public int compare(Item a, Item b) {
			return ((Item) b).num - ((Item) a).num;
		}
	};

	private int numOfTrans;

	/**
	 * Run the algorithm
	 * 
	 * @param filename
	 *            the input file path
	 * @param minsup
	 *            the minsup threshold
	 * @param output
	 *            the output file path
	 * @throws IOException
	 *             if error while reading/writting to file
	 */
	public void runAlgorithm(String filename, double minsup, String output)
			throws IOException {

		bmcTreeRoot = new BMCTreeNode();
		nlRoot = new NodeListTreeNode();
		nlNodeCount = 0;

		MemoryLogger.getInstance().reset();

		// create object for writing the output file
		writer = new BufferedWriter(new FileWriter(output));

		// record the start time
		startTimestamp = System.currentTimeMillis();

		bf_size = 1000000;
		bf = new int[100000][];
		bf_currentSize = bf_size * 10;
		bf[0] = new int[bf_currentSize];

		bf_cursor = 0;
		bf_col = 0;

		// ==========================
		// Read Dataset
		getData(filename, minsup);

		resultLen = 0;
		result = new int[numOfFItem];

		// Build BMC-tree
		construct_BMC_tree(filename);//Lines 2 to 6 of algorithm 3 in the paper

		nlRoot.label = numOfFItem;
		nlRoot.firstChild = null;
		nlRoot.next = null;

		//Lines 12 to 19 of algorithm 3 in the paper
		// Initialize tree
		initializeTree();
		sameItems = new int[numOfFItem];

		int from_cursor = bf_cursor;
		int from_col = bf_col;
		int from_size = bf_currentSize;

		// Recursively traverse the tree
		NodeListTreeNode curNode = nlRoot.firstChild;
		NodeListTreeNode next = null;
		while (curNode != null) {
			next = curNode.next;
			// call the recursive "traverse" method
			constructing_frequent_itemset_tree (curNode,1, 0);
			for (int c = bf_col; c > from_col; c--) {
				bf[c] = null;
			}
			bf_col = from_col;
			bf_cursor = from_cursor;
			bf_currentSize = from_size;
			curNode = next;
		}
		writer.close();

		MemoryLogger.getInstance().checkMemory();

		// record the end time
		endTimestamp = System.currentTimeMillis();
	}

	/**
	 * Build the tree
	 * 
	 * @param filename
	 *            the input filename
	 * @throws IOException
	 *             if an exception while reading/writting to file
	 */
	void construct_BMC_tree(String filename) throws IOException {

		bmcTreeNodeCount = 0;
		bmcTreeRoot.label = -1;
		bmcTreeRoot.bitmapCode =new BitSet(numOfFItem);

		nlistBegin = new int[numOfFItem ];
		nlistLen = new int[numOfFItem ];
		for (int i=0;i<numOfFItem;i++){
			nlistLen[i]=0;
		}

		// READ THE FILE
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		String line;

		// we will use a buffer to store each transaction that is read.
		Item[] transaction = new Item[1000];

		// for each line (transaction) until the end of the file
		while (((line = reader.readLine()) != null)) {
			// if the line is a comment, is empty or is a
			// kind of metadata
			if (line.isEmpty() == true || line.charAt(0) == '#'
					|| line.charAt(0) == '%' || line.charAt(0) == '@') {
				continue;
			}

			// split the line into items
			String[] lineSplited = line.split(" ");

			// for each item in the transaction
			int tLen = 0; // tLen
			for (String itemString : lineSplited) {
				// get the item
				int itemX = Integer.parseInt(itemString);

				// add each item from the transaction except infrequent item
				for (int j = 0; j < numOfFItem; j++) {
					// if the item appears in the list of frequent items, we add
					// it
					if (itemX == item[j].index) {
						transaction[tLen] = new Item();
						transaction[tLen].index = itemX; // the item
						transaction[tLen].num = 0 - j;
						tLen++;
						break;
					}
				}
			}

			// sort the transaction
			Arrays.sort(transaction, 0, tLen, comp);

			int curPos = 0;
			BMCTreeNode curRoot = (bmcTreeRoot);
			BMCTreeNode rightSibling = null;
			while (curPos != tLen) {
				BMCTreeNode child = curRoot.firstChild;
				while (child != null) {
					if (child.label == 0 - transaction[curPos].num) {
						curPos++;
						child.count++;
						curRoot = child;
						break;
					}
					if (child.rightSibling == null) {
						rightSibling = child;
						child = null;
						break;
					}
					child = child.rightSibling;
				}
				if (child == null)
					break;
			}
			for (int j = curPos; j < tLen; j++) {
				BMCTreeNode bmcTreeNode = new BMCTreeNode();
				bmcTreeNode.label = 0 - transaction[j].num;
				if (rightSibling != null) {
					rightSibling.rightSibling = bmcTreeNode;
					rightSibling = null;
				} else {
					curRoot.firstChild = bmcTreeNode;
				}
				bmcTreeNode.rightSibling = null;
				bmcTreeNode.firstChild = null;
				bmcTreeNode.father = curRoot;
				bmcTreeNode.count = 1;
				curRoot = bmcTreeNode;
				bmcTreeNodeCount++;
				nlistLen[bmcTreeNode.label]++;
			}
		}
		// close the input file
		reader.close();

		//build 1-itemset nlist
		int sum = 0;
		for (int i = 0; i < numOfFItem; i++) {
			nlistBegin[i] = sum;
			sum += nlistLen[i];
		}

		if (bf_cursor + sum > bf_currentSize * 0.85) {
			bf_col++;
			bf_cursor = 0;
			bf_currentSize = sum + 1000;
			bf[bf_col] = new int[bf_currentSize];
		}
		nlistCol = bf_col;
		firstNlistBegin = bf_cursor;
		bf_cursor += sum;


		BMCTreeNode root = bmcTreeRoot.firstChild;
		int pre = 0; //The pre order index is used as the node index.
		//itemsetCount = new int[(numOfFItem - 1) * numOfFItem / 2];
		//nlistBegin = new int[(numOfFItem - 1) * numOfFItem / 2];
		//nlistLen = new int[(numOfFItem - 1) * numOfFItem / 2];
		supportDict = new int[bmcTreeNodeCount + 1];
		bitmapCodeDict = new BitSet[bmcTreeNodeCount + 1];
		while (root != null) {
//			root.id = preOrderRank;
			supportDict[pre] = root.count;

			root.bitmapCode=(BitSet) root.father.bitmapCode.clone();
			root.bitmapCode.set(root.label);//bitIndex=numOfFItem - 1 - root.label
			bitmapCodeDict[pre] = root.bitmapCode;


			int cursor = firstNlistBegin + nlistBegin[root.label] ;
			bf[nlistCol][cursor] = pre;
			nlistBegin[root.label]++;

			pre++;

			if (root.firstChild != null) {
				root = root.firstChild;
			} else {
				if (root.rightSibling != null) {
					root = root.rightSibling;
				} else {
					root = root.father;
					while (root != null) {
						if (root.rightSibling != null) {
							root = root.rightSibling;
							break;
						}
						root = root.father;
					}
				}
			}
		}

		for (int i = 0; i < numOfFItem; i++) {
			nlistBegin[i] = nlistBegin[i] - nlistLen[i];
		}
	}

	/**
	 * Initialize the tree
	 */
	void initializeTree() {

		NodeListTreeNode lastChild = null;
		for (int t = numOfFItem - 1; t >= 0; t--) {
			NodeListTreeNode nlNode = new NodeListTreeNode();
			nlNode.label = t;
			nlNode.support = 0;
			nlNode.NLStartinBf = nlistBegin[t];
			nlNode.NLLength = nlistLen[t];
			nlNode.NLCol = bf_col;
			nlNode.firstChild = null;
			nlNode.next = null;
			nlNode.support = item[t].num;
			if (nlRoot.firstChild == null) {
				nlRoot.firstChild = nlNode;
				lastChild = nlNode;
			} else {
				lastChild.next = nlNode;
				lastChild = nlNode;
			}
		}
	}

	/**
	 * Read the input file to find the frequent items
	 * 
	 * @param filename
	 *            input file name
	 * @param minSupport
	 * @throws IOException
	 */
	void getData(String filename, double minSupport) throws IOException {
		numOfTrans = 0;

		// (1) Scan the database and count the support of each item.
		// The support of items is stored in map where
		// key = item value = support count
		Map<Integer, Integer> mapItemCount = new HashMap<Integer, Integer>();
		// scan the database
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		String line;
		// for each line (transaction) until the end of the file
		while (((line = reader.readLine()) != null)) {
			// if the line is a comment, is empty or is a
			// kind of metadata
			if (line.isEmpty() == true || line.charAt(0) == '#'
					|| line.charAt(0) == '%' || line.charAt(0) == '@') {
				continue;
			}

			numOfTrans++;

			// split the line into items
			String[] lineSplited = line.split(" ");
			// for each item in the transaction
			for (String itemString : lineSplited) {
				// increase the support count of the item by 1
				Integer item = Integer.parseInt(itemString);
				Integer count = mapItemCount.get(item);
				if (count == null) {
					mapItemCount.put(item, 1);
				} else {
					mapItemCount.put(item, ++count);
				}
			}

		}
		// close the input file
		reader.close();

		this.minSupport = (int)Math.ceil(minSupport * numOfTrans);

		numOfFItem = mapItemCount.size();

		Item[] tempItems = new Item[numOfFItem];
		int i = 0;
		for (Entry<Integer, Integer> entry : mapItemCount.entrySet()) {
			if (entry.getValue() >= minSupport) {
				tempItems[i] = new Item();
				tempItems[i].index = entry.getKey();
				tempItems[i].num = entry.getValue();
				i++;
			}
		}

		item = new Item[i];
		System.arraycopy(tempItems, 0, item, 0, i);

		numOfFItem = item.length;

		Arrays.sort(item, comp);
	}


	NodeListTreeNode is2ItemSetFreq(NodeListTreeNode j, NodeListTreeNode i, NodeListTreeNode lastChild, IntegerByRef sameCountRef) {
		if (bf_cursor + j.NLLength > bf_currentSize) {
			bf_col++;
			bf_cursor = 0;
			bf_currentSize = bf_size > j.NLLength * 1000 ? bf_size : j.NLLength * 1000;
			bf[bf_col] = new int[bf_currentSize];
		}

		NodeListTreeNode nlNode = new NodeListTreeNode();
		nlNode.support = j.support;
		nlNode.NLStartinBf = bf_cursor;
		nlNode.NLCol = bf_col;
		nlNode.NLLength = 0;

		int col_j = j.NLCol;

		int nodeIndexJ;
		int bitIndexI=i.label;
		int endBf = j.NLStartinBf + j.NLLength;
		//Lines 9 to 14 of the procedure constructing_frequent_itemset_tree (...) in the paper.
		for (int cursor_j = j.NLStartinBf; cursor_j < endBf; cursor_j++) {
			nodeIndexJ = bf[col_j][cursor_j];
			if (!bitmapCodeDict[nodeIndexJ].get(bitIndexI)) {
				bf[bf_col][bf_cursor++] = nodeIndexJ;
				nlNode.NLLength++;
				nlNode.support -= supportDict[nodeIndexJ];
			}
		}
		if (nlNode.support >= minSupport) {
			if (j.support == nlNode.support)
				sameItems[sameCountRef.count++] = i.label;
			else {
				nlNode.label = i.label;
				nlNode.firstChild = null;
				nlNode.next = null;
				if (j.firstChild == null) {
					j.firstChild = nlNode;
					lastChild = nlNode;
				} else {
					lastChild.next = nlNode;
					lastChild = nlNode;
				}
			}
			return lastChild;
		} else {
			bf_cursor = nlNode.NLStartinBf;
		}
		return lastChild;
	}

	NodeListTreeNode iskItemSetFreq(NodeListTreeNode j, NodeListTreeNode i, NodeListTreeNode lastChild, IntegerByRef sameCountRef) {
		if (bf_cursor + j.NLLength > bf_currentSize) {
			bf_col++;
			bf_cursor = 0;
			bf_currentSize = bf_size > j.NLLength * 1000 ? bf_size
					: j.NLLength * 1000;
			bf[bf_col] = new int[bf_currentSize];
		}

		NodeListTreeNode nlNode = new NodeListTreeNode();
		nlNode.support = j.support;
		nlNode.NLStartinBf = bf_cursor;
		nlNode.NLCol = bf_col;
		nlNode.NLLength = 0;

		int col_i = i.NLCol;

		int nodeIndexI;
		int endBf = i.NLStartinBf + i.NLLength;

		if (j.NLLength == 0) {
			for (int cursor_I = i.NLStartinBf; cursor_I < endBf; cursor_I++) {
				nodeIndexI = bf[col_i][cursor_I];
				bf[bf_col][bf_cursor++] = nodeIndexI;
				nlNode.NLLength++;
				nlNode.support -= supportDict[nodeIndexI];
			}
		} else {
			int bitIndexJ=j.label;
			//Lines 18 to 23 of the procedure constructing_frequent_itemset_tree (...) in the paper.
			for (int cursor_I = i.NLStartinBf; cursor_I < endBf; cursor_I++) {
				nodeIndexI = bf[col_i][cursor_I];
				if (bitmapCodeDict[nodeIndexI].get(bitIndexJ)) {
					bf[bf_col][bf_cursor++] = nodeIndexI;
					nlNode.NLLength++;
					nlNode.support -= supportDict[nodeIndexI];
				}
			}
		}

		if (nlNode.support >= minSupport) {
			if (j.support == nlNode.support) {
				sameItems[sameCountRef.count++] = i.label;
			} else {
				nlNode.label = i.label;
				nlNode.firstChild = null;
				nlNode.next = null;
				if (j.firstChild == null) {
					j.firstChild = nlNode;
					lastChild = nlNode;
				} else {
					lastChild.next = nlNode;
					lastChild = nlNode;
				}
			}
			return lastChild;
		} else {
			bf_cursor = nlNode.NLStartinBf;
		}
		return lastChild;
	}

	/**
	 * Recursively traverse the tree to find frequent itemsets
	 * @param curNode
	 * @param level
	 * @param sameCount
	 * @throws IOException if error while writing itemsets to file
	 */

	public void constructing_frequent_itemset_tree (NodeListTreeNode curNode, int level, int sameCount) throws IOException {

		MemoryLogger.getInstance().checkMemory();

		NodeListTreeNode sibling = curNode.next;
		NodeListTreeNode lastChild = null;
		while (sibling != null) {
			if (level==1) {
				IntegerByRef sameCountTemp = new IntegerByRef();
				sameCountTemp.count = sameCount;
				lastChild = is2ItemSetFreq(curNode, sibling, lastChild, sameCountTemp);
				sameCount = sameCountTemp.count;
			} else if (level > 1) {
				IntegerByRef sameCountTemp = new IntegerByRef();
				sameCountTemp.count = sameCount;
				lastChild = iskItemSetFreq(curNode, sibling, lastChild, sameCountTemp);
				sameCount = sameCountTemp.count;
			}
			sibling = sibling.next;
		}
		resultCount += Math.pow(2.0, sameCount);
		nlLenSum += Math.pow(2.0, sameCount) * curNode.NLLength;

		result[resultLen++] = curNode.label;

		// ============= Write itemset(s) to file ===========
		writeItemsetsToFile(curNode, sameCount);

		// ======== end of write to file

		nlNodeCount++;

		int from_cursor = bf_cursor;
		int from_col = bf_col;
		int from_size = bf_currentSize;
		NodeListTreeNode child = curNode.firstChild;
		NodeListTreeNode next = null;
		while (child != null) {
			next = child.next;
			constructing_frequent_itemset_tree (child,level + 1, sameCount);
			for (int c = bf_col; c > from_col; c--) {
				bf[c] = null;
			}
			bf_col = from_col;
			bf_cursor = from_cursor;
			bf_currentSize = from_size;
			child = next;
		}
		resultLen--;
	}


	/**
	 * This method write an itemset to file + all itemsets that can be made
	 * using its node list.
	 * 
	 * @param curNode
	 *            the current node
	 * @param sameCount
	 *            the same count
	 * @throws IOException
	 *             exception if error reading/writting to file
	 */
	private void writeItemsetsToFile(NodeListTreeNode curNode, int sameCount)
			throws IOException {

		// create a stringuffer
		StringBuilder buffer = new StringBuilder();
		if(curNode.support >= minSupport) {
			outputCount++;
			// append items from the itemset to the StringBuilder
			for (int i = 0; i < resultLen; i++) {
				buffer.append(item[result[i]].index);
				buffer.append(' ');
			}
			// append the support of the itemset
			buffer.append("#SUP: ");
			buffer.append(curNode.support);
			buffer.append("\n");
		}

		// === Write all combination that can be made using the node list of
		// this itemset
		if (sameCount > 0) {
			// generate all subsets of the node list except the empty set
			for (long i = 1, max = 1 << sameCount; i < max; i++) {
				for (int k = 0; k < resultLen; k++) {
					buffer.append(item[result[k]].index);
					buffer.append(' ');
				}

				// we create a new subset
				for (int j = 0; j < sameCount; j++) {
					// check if the j bit is set to 1
					int isSet = (int) i & (1 << j);
					if (isSet > 0) {
						// if yes, add it to the set
						buffer.append(item[sameItems[j]].index);
						buffer.append(' ');
						// newSet.add(item[sameItems[j]].index);
					}
				}
				buffer.append("#SUP: ");
				buffer.append(curNode.support);
				buffer.append("\n");
				outputCount++;
			}
		}
		// write the strinbuffer to file and create a new line
		// so that we are ready for writing the next itemset.
		writer.write(buffer.toString());
	}
	

	/**
	 * Print statistics about the latest execution of the algorithm to
	 * System.out.
	 */
	public void printStats() {
		System.out.println("========== negFIN - STATS ============");
		System.out.println(" Minsup = " + minSupport
				+ "\n Number of transactions: " + numOfTrans);
		System.out.println(" Number of frequent  itemsets: " + outputCount);
		System.out.println(" Total time ~: " + (endTimestamp - startTimestamp)
				+ " ms");
		System.out.println(" Max memory:"
				+ MemoryLogger.getInstance().getMaxMemory() + " MB");
		System.out.println("=====================================");
	}

	/** Class to pass an integer by reference as in C++
	 */
	class IntegerByRef {
		int count;
	}

	class Item {
		public int index;
		public int num;
	}

	class NodeListTreeNode {
		public int label;
		public NodeListTreeNode firstChild;
		public NodeListTreeNode next;
		public int support;
		public int NLStartinBf;
		public int NLLength;
		public int NLCol;
	}

	class BMCTreeNode {
		public int label;
		public BMCTreeNode firstChild;
		public BMCTreeNode rightSibling;
		public BMCTreeNode father;
		public int count;
		BitSet bitmapCode;
	}
}
