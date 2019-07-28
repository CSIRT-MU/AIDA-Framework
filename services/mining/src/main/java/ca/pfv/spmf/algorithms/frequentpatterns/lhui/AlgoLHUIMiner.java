package ca.pfv.spmf.algorithms.frequentpatterns.lhui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.pfv.spmf.tools.MemoryLogger;

/* This file is copyright (c) 2018  Yimin Zhang, Philippe Fournier-Viger
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
 * This is an implementation of the "LHUI-Miner" algorithm for local high
 * utility itemset mining as described in the conference paper : <br/>
 * <br/>
 *
 * Fournier-Viger, P., Zhang, Y., Lin, J. C.W., Fujita, H., Koh, Y.S. (2019).
 * Mining Local and Peak High Utility Itemsets. Information Sciences, Elsevier,
 * 481: 344-367.
 * 
 * @author Yimin Zhang, Philippe Fournier-Viger
 * @see UtilityListLHUI
 */
public class AlgoLHUIMiner {

	/** the time at which the algorithm started */
	public long startTimestamp = 0;

	/** the time at which the algorithm ended */
	public long endTimestamp = 0;

	/** the number of high-utility itemsets generated */
	public int huiCount = 0;

	public int candidateCount = 0;

	/** Map to remember the TWU of each item */
	Map<Integer, Long> mapItemToTWU;

	/** writer to write the output file */
	BufferedWriter writer = null;

	/** the number of utility-list that was constructed */
	private int joinCount;

	/**
	 * buffer for storing the current itemset that is mined when performing
	 * mining the idea is to always reuse the same buffer to reduce memory
	 * usage.
	 */
	final int BUFFERS_SIZE = 200;
	private int[] itemsetBuffer = null;

	/** this class represent an item and its utility in a transaction */
	class Pair {
		int item = 0;
		long utility = 0;
	}

	/** to store the time of every transaction */
	ArrayList<Long> timeTid = new ArrayList<Long>();

	/**
	 * Default constructor
	 */
	public AlgoLHUIMiner() {
	}

	/**
	 * Run the algorithm
	 * 
	 * @param input
	 *            the input file path
	 * @param output
	 *            the output file path
	 * @param minUtility
	 *            the minimum utility threshold
	 * @param window
	 * @throws IOException
	 *             exception if error while writing the file
	 */
	public void runAlgorithm(String input, String output, long minUtility,
			long window) throws IOException {
		// reset maximum
		MemoryLogger.getInstance().reset();

		// initialize the buffer for storing the current itemset
		itemsetBuffer = new int[BUFFERS_SIZE];

		startTimestamp = System.currentTimeMillis();

		writer = new BufferedWriter(new FileWriter(output));

		// We create a map to store the TWU of each item
		mapItemToTWU = new HashMap<Integer, Long>();

		// We scan the database a first time to calculate the TWU of each item.
		BufferedReader myInput = null;
		String thisLine;
		try {
			// prepare the object for reading the file
			myInput = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(input))));

			// for each line (transaction) until the end of file
			while ((thisLine = myInput.readLine()) != null) {
				// if the line is a comment, is empty or is a
				// kind of metadata
				if (thisLine.isEmpty() == true || thisLine.charAt(0) == '#'
						|| thisLine.charAt(0) == '%'
						|| thisLine.charAt(0) == '@') {
					continue;
				}

				// split the transaction according to the : separator
				String split[] = thisLine.split(":");
				// the first part is the list of items
				String items[] = split[0].split(" ");
				// the second part is the transaction utility
				long transactionUtility = Integer.parseInt(split[1]);
				// store timestamp for every transaction
				timeTid.add(Long.parseLong(split[3]));
				// for each item, we add the transaction utility to its TWU
				for (int i = 0; i < items.length; i++) {
					// convert item to integer
					Integer item = Integer.parseInt(items[i]);
					// get the current TWU of that item
					Long twu = mapItemToTWU.get(item);
					// add the utility of the item in the current transaction to
					// its twu
					twu = (twu == null) ? transactionUtility : twu
							+ transactionUtility;
					mapItemToTWU.put(item, twu);
				}
			}
		} catch (Exception e) {
			// catches exception if error while reading the input file
			e.printStackTrace();
		} finally {
			if (myInput != null) {
				myInput.close();
			}
		}

		// CREATE A LIST TO STORE THE UTILITY LIST OF ITEMS WITH TWU >=
		// MIN_UTILITY.
		List<UtilityListLHUI> listOfUtilityListPeaks = new ArrayList<UtilityListLHUI>();
		// CREATE A MAP TO STORE THE UTILITY LIST FOR EACH ITEM.
		// Key : item Value : utility list associated to that item
		Map<Integer, UtilityListLHUI> mapItemToUtilityListPeak = new HashMap<Integer, UtilityListLHUI>();

		// For each item
		for (Integer item : mapItemToTWU.keySet()) {
			// if the item is promising (TWU >= minutility)
			if (mapItemToTWU.get(item) >= minUtility) {
				// create an empty Utility List that we will fill later.
				UtilityListLHUI uList = new UtilityListLHUI(item);
				mapItemToUtilityListPeak.put(item, uList);
				// add the item to the list of high TWU items
				listOfUtilityListPeaks.add(uList);

			}
		}
		// SORT THE LIST OF HIGH TWU ITEMS IN ASCENDING ORDER
		Collections.sort(listOfUtilityListPeaks,
				new Comparator<UtilityListLHUI>() {
					public int compare(UtilityListLHUI o1, UtilityListLHUI o2) {
						// compare the TWU of the items
						return compareItems(o1.item, o2.item);
					}
				});

		// SECOND DATABASE PASS TO CONSTRUCT THE UTILITY LISTS
		// OF 1-ITEMSETS HAVING TWU >= minutil (promising items)
		try {
			// prepare object for reading the file
			myInput = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(input))));
			// variable to count the number of transaction
			int tid = 0;
			// for each line (transaction) until the end of file
			while ((thisLine = myInput.readLine()) != null) {
				// if the line is a comment, is empty or is a
				// kind of metadata
				if (thisLine.isEmpty() == true || thisLine.charAt(0) == '#'
						|| thisLine.charAt(0) == '%'
						|| thisLine.charAt(0) == '@') {
					continue;
				}

				// split the line according to the separator
				String split[] = thisLine.split(":");
				// get the list of items
				String items[] = split[0].split(" ");
				// get the list of utility values corresponding to each item
				// for that transaction
				String utilityValues[] = split[2].split(" ");

				// Copy the transaction into lists but
				// without items with TWU < minutility

				long remainingUtility = 0;

				// Create a list to store items
				List<Pair> revisedTransaction = new ArrayList<Pair>();
				// for each item
				for (int i = 0; i < items.length; i++) {
					// / convert values to integers
					Pair pair = new Pair();
					pair.item = Integer.parseInt(items[i]);
					pair.utility = Integer.parseInt(utilityValues[i]);
					// if the item has enough utility
					if (mapItemToTWU.get(pair.item) >= minUtility) {
						// add it
						revisedTransaction.add(pair);
						remainingUtility += pair.utility;
					}
				}

				Collections.sort(revisedTransaction, new Comparator<Pair>() {
					public int compare(Pair o1, Pair o2) {
						return compareItems(o1.item, o2.item);
					}
				});

				// for each item left in the transaction
				for (Pair pair : revisedTransaction) {
					// subtract the utility of this item from the remaining
					// utility
					remainingUtility = remainingUtility - pair.utility;

					// get the utility list of this item
					UtilityListLHUI UtilityListPeakOfItem = mapItemToUtilityListPeak
							.get(pair.item);

					// Add a new Element to the utility list of this item
					// corresponding to this transaction
					Element element = new Element(tid, pair.utility,
							remainingUtility);

					UtilityListPeakOfItem.addElement(element);
				}
				tid++; // increase tid number for next transaction

			}
		} catch (Exception e) {
			// to catch error while reading the input file
			e.printStackTrace();
		} finally {
			if (myInput != null) {
				myInput.close();
			}
		}

		// ----- to generate periods for 1-itemset utility list -----
		for (UtilityListLHUI ulp : listOfUtilityListPeaks) {
			generatePeriod(ulp, minUtility, window);
		}

		// check the memory usage
		MemoryLogger.getInstance().checkMemory();

		// Mine the database recursively
		lhuiMiner(itemsetBuffer, 0, null, listOfUtilityListPeaks, minUtility,
				window);

		// check the memory usage again and close the file.
		MemoryLogger.getInstance().checkMemory();
		// close output file
		writer.close();
		// record end time
		endTimestamp = System.currentTimeMillis();
	}

	private int compareItems(int item1, int item2) {
		long compare = mapItemToTWU.get(item1) - mapItemToTWU.get(item2);
		// if the same, use the lexical order otherwise use the TWU
		return (compare == 0) ? item1 - item2 : ((int) compare);
	}

	/**
	 * This is the recursive method to find all high utility itemsets. It writes
	 * the itemsets to the output file.
	 * 
	 * @param prefix
	 *            This is the current prefix. Initially, it is empty.
	 * @param pUL
	 *            This is the Utility List of the prefix. Initially, it is
	 *            empty.
	 * @param ULs
	 *            The utility lists corresponding to each extension of the
	 *            prefix.
	 * @param minUtility
	 *            The minUtility threshold.
	 * @param prefixLength
	 *            The current prefix length
	 * @throws IOException
	 */
	private void lhuiMiner(int[] prefix, int prefixLength, UtilityListLHUI pUL,
			List<UtilityListLHUI> ULs, long minUtility, long window)
			throws IOException {

		// For each extension X of prefix P
		for (int i = 0; i < ULs.size(); i++) {
			UtilityListLHUI X = ULs.get(i);

			// If LHUI periods of pX is not empty.
			// we save the itemset: pX
			if (!X.iutilPeriod.isEmpty()) {
				// save to file
				writeOut(prefix, prefixLength, X);
			}

			// If the PLHUI periods of pX is not empty
			// we explore extensions of pX.
			// (this is the pruning condition)
			if (!X.utilPeriod.isEmpty()) {
				// This list will contain the utility lists of pX extensions.
				List<UtilityListLHUI> exULs = new ArrayList<UtilityListLHUI>();
				// For each extension of p appearing
				candidateCount++;
				// after X according to the ascending order
				for (int j = i + 1; j < ULs.size(); j++) {
					UtilityListLHUI Y = ULs.get(j);
					// we construct the extension pXY
					UtilityListLHUI pXY = construct(pUL, X, Y);
					// scan the utility-list to get periods information
					generatePeriod(pXY, minUtility, window);
					// and add it to the list of extensions of pX
					exULs.add(pXY);
					joinCount++;
				}
				// We create new prefix pX
				itemsetBuffer[prefixLength] = X.item;

				// We make a recursive call to discover all itemsets with the prefix pXY
				lhuiMiner(itemsetBuffer, prefixLength + 1, X, exULs, minUtility, window);
			}
		}
	}

	/**
	 * This method constructs the utility list of pXY
	 * 
	 * @param P
	 *            : the utility list of prefix P.
	 * @param px
	 *            : the utility list of pX
	 * @param py
	 *            : the utility list of pY
	 * @return the utility list of pXY
	 */
	private UtilityListLHUI construct(UtilityListLHUI P, UtilityListLHUI px,
			UtilityListLHUI py) {
		// create an empy utility list for pXY
		UtilityListLHUI pxyUL = new UtilityListLHUI(py.item);
		// for each element in the utility list of pX
		for (Element ex : px.elements) {
			// do a binary search to find element ey in py with tid = ex.tid
			Element ey = findElementWithTID(py, ex.tid);
			if (ey == null) {
				continue;
			}
			// if the prefix p is null
			if (P == null) {
				// Create the new element
				Element eXY = new Element(ex.tid, ex.iutils + ey.iutils,
						ey.rutils);
				// add the new element to the utility list of pXY
				pxyUL.addElement(eXY);

			} else {
				// find the element in the utility list of p wih the same tid
				Element e = findElementWithTID(P, ex.tid);
				if (e != null) {
					// Create new element
					Element eXY = new Element(ex.tid, ex.iutils + ey.iutils
							- e.iutils, ey.rutils);
					// add the new element to the utility list of pXY
					pxyUL.addElement(eXY);
				}
			}
		}
		// return the utility list of pXY.
		return pxyUL;
	}

	/**
	 * Do a binary search to find the element with a given tid in a utility list
	 * 
	 * @param ulist
	 *            the utility list
	 * @param tid
	 *            the tid
	 * @return the element or null if none has the tid.
	 */
	private Element findElementWithTID(UtilityListLHUI ulist, int tid) {
		List<Element> list = ulist.elements;

		// perform a binary search to check if the subset appears in level k-1.
		int first = 0;
		int last = list.size() - 1;

		// the binary search
		while (first <= last) {
			int middle = (first + last) >>> 1; // divide by 2

			if (list.get(middle).tid < tid) {
				first = middle + 1; // the itemset compared is larger than the
									// subset according to the lexical order
			} else if (list.get(middle).tid > tid) {
				last = middle - 1; // the itemset compared is smaller than the
									// subset is smaller according to the
									// lexical order
			} else {
				return list.get(middle);
			}
		}
		return null;
	}

	/**
	 * Method to write a high utility itemset to the output file.
	 * 
	 * @param the
	 *            prefix to be writent o the output file
	 * @param an
	 *            item to be appended to the prefix
	 * @param utility
	 *            the utility of the prefix concatenated with the item
	 * @param prefixLength
	 *            the prefix length
	 */
	private void writeOut(int[] prefix, int prefixLength, UtilityListLHUI ulp)
			throws IOException {
		huiCount++; // increase the number of high utility itemsets found

		// Create a string buffer
		StringBuilder buffer = new StringBuilder();
		// append the prefix
		for (int i = 0; i < prefixLength; i++) {
			buffer.append(prefix[i]);
			buffer.append(' ');
		}
		// append the last item
		buffer.append(ulp.item);
		// append the utility value
		buffer.append(" #UTIL: ");
		buffer.append(ulp.sumIutils + " ");

		for (int i = 0; i < ulp.iutilPeriod.size(); i++) {
			Period p = ulp.iutilPeriod.get(i);
			buffer.append("[" + timeTid.get(ulp.elements.get(p.beginIndex).tid)
					+ "," + timeTid.get(ulp.elements.get(p.endIndex).tid)
					+ "] ");
		}
		// write to file
		writer.write(buffer.toString());
		writer.newLine();
	}

	/**
	 * generate iutilPeriod and rutilPeriod for UtilityList
	 * 
	 * @param ulp
	 *            utility list
	 * @param minutil
	 *            threshold during a certain window length
	 * @param window
	 *            length of window
	 */
	public void generatePeriod(UtilityListLHUI ulp, long minutil, long window) {
		// ituil to store the itemset utility of window, rutil to store
		// remaining
		// utility of itemset, winEnd to mark the end point of the window
		// find first window
		long iutil = 0, rutil = 0;
		int winEnd = 0;

		// these flags indicates if the first window is a PHUI period or LHUI
		// period
		boolean iutilPreflag = false, utilPreflag = false;

		// find first window
		for (; winEnd < ulp.elements.size()
				&& timeTid.get(ulp.elements.get(winEnd).tid).longValue() < timeTid
						.get(ulp.elements.get(0).tid).longValue() + window; winEnd++) {
			iutil += ulp.elements.get(winEnd).iutils;
			rutil += ulp.elements.get(winEnd).rutils;
		}

		if (iutil > minutil)
			iutilPreflag = true;
		if (iutil + rutil > minutil)
			utilPreflag = true;

		// slide window to find all period information
		slideWindow(ulp, winEnd, minutil, iutil, iutilPreflag, rutil,
				utilPreflag, window);
	}

	/**
	 * generate periods information using sliding window technique
	 * 
	 * @param ulp
	 *            LU-list of itemset p
	 * @param winEnd
	 *            the ending index of first window, actually this is the
	 *            beiginning index of sencond window, and the real ending index
	 *            should be winEnd - 1)
	 * @param minutil
	 *            utility threshold
	 * @param iutil
	 *            the sum of iutil in first window
	 * @param iutilPreflag
	 *            indicates if first window is a LHUI period
	 * @param rutil
	 *            the sum of iutil+rutil in first window
	 * @param utilPreflag
	 *            indicates if first window is a promising LHUI period
	 * @param window
	 *            window length threshold
	 */
	private void slideWindow(UtilityListLHUI ulp, int winEnd, long minutil,
			long iutil, boolean iutilPreflag, long rutil, boolean utilPreflag,
			long window) {
		int beginIndex = 0, endIndex = winEnd, uBeginIndex = 0, uEndIndex = winEnd;
		for (int i = 0; i < ulp.elements.size();) {
			int x, y;

			for (y = i; y < ulp.elements.size()
					&& timeTid.get(ulp.elements.get(y).tid) == timeTid
							.get(ulp.elements.get(i).tid); y++) {
				iutil -= ulp.elements.get(y).iutils;
				rutil -= ulp.elements.get(y).rutils;
			}
			i = y;

			for (x = winEnd; x < ulp.elements.size()
					&& timeTid.get(ulp.elements.get(x).tid) < timeTid
							.get(ulp.elements.get(y).tid) + window; x++) {
				iutil += ulp.elements.get(x).iutils;
				rutil += ulp.elements.get(x).rutils;
				winEnd = x + 1;
			}

			// add the high utility period that iUtil>minutil
			if (iutilPreflag) {
				if (iutil < minutil) {
					ulp.iutilPeriod.add(new Period(beginIndex, endIndex - 1));
					iutilPreflag = false;
				} else
					endIndex = winEnd;
			} else {
				if (iutil > minutil) {
					iutilPreflag = true;
					beginIndex = i;
					endIndex = winEnd;
				}
			}

			// add high utility period that iUtil+rUtil>minutil
			if (utilPreflag) {
				if (iutil + rutil < minutil) {
					ulp.utilPeriod.add(new Period(uBeginIndex, uEndIndex - 1));
					utilPreflag = false;
				} else
					uEndIndex = winEnd;
			} else {
				if (iutil + rutil > minutil) {
					utilPreflag = true;
					uBeginIndex = i;
					uEndIndex = winEnd;
				}
			}
		}

	}

	/**
	 * Print statistics about the latest execution to System.out.
	 */
	public void printStats() {
		System.out
				.println("=============  LHUI-MINER ALGORITHM - STATS =============");
		System.out.println(" Total time ~ " + (endTimestamp - startTimestamp)
				+ " ms");
		System.out.println(" Memory ~ "
				+ MemoryLogger.getInstance().getMaxMemory() + " MB");
		System.out.println(" Local High-utility itemsets count : " + huiCount);
		System.out.println(" Join count : " + joinCount);
		System.out.println(" Candidate count : " + candidateCount);
		System.out
				.println("===================================================");
	}
}