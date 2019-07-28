package ca.pfv.spmf.algorithms.frequentpatterns.HUIM_GA_tree;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * * * * This is an implementation of the high utility itemset mining algorithm
 * based on Genetic Algorithm and using OR/NOR-tree structure.
 * 
 * Copyright (c) 2016 Jerry Chun-Wei Lin, Lu Yang, Philippe Fournier-Viger
 * 
 * This file is part of the SPMF DATA MINING SOFTWARE *
 * (http://www.philippe-fournier-viger.com/spmf).
 * 
 * 
 * SPMF is free software: you can redistribute it and/or modify it under the *
 * terms of the GNU General Public License as published by the Free Software *
 * Foundation, either version 3 of the License, or (at your option) any later *
 * version. *
 * 
 * SPMF is distributed in the hope that it will be useful, but WITHOUT ANY *
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * *
 * 
 * You should have received a copy of the GNU General Public License along with
 * * SPMF. If not, see .
 * 
 * @author Jerry Chun-Wei Lin, Lu Yang, Philippe Fournier-Viger
 */

public class AlgoHUIM_GA_tree {
	// variable for statistics
	double maxMemory = 0; // the maximum memory usage
	long startTimestamp = 0; // the time the algorithm started
	long endTimestamp = 0; // the time the algorithm terminated
	final int pop_size = 20;// the size of populations
	final int iterations = 2000;// the iterations of algorithms

	Map<Integer, Integer> mapItemToTWU;
	List<Integer> twuPattern;// the items which has twu value more than minUtil

	BufferedWriter writer = null; // writer to write the output file

	// this class represent an item and its utility in a transaction
	class Pair {
		int item = 0;
		int utility = 0;
	}

	// this class represent the chromosome
	class ChroNode {
		List<Integer> chromosome;// the chromosome
		int fitness;// fitness value of chromosome
		double rfitness;//�����Ӧ������ѡ��Ⱦɫ����н������
		int rank;// the rank of chromosome's fitness in population

		public ChroNode() {
			chromosome = new ArrayList<Integer>();
		}

		public ChroNode(int length) {
			chromosome = new ArrayList<Integer>();
			for (int i = 0; i < length; i++) {
				chromosome.add(i, 0);
			}
		}
		public void deepCopy(ChroNode chroNode1){
			for(int i=0;i<chroNode1.chromosome.size();++i){
				this.chromosome.set(i, chroNode1.chromosome.get(i).intValue());
			}
			this.fitness=chroNode1.fitness;
		}
	}

	class HUI {
		String itemset;
		int fitness;

		public HUI(String itemset, int fitness) {
			super();
			this.itemset = itemset;
			this.fitness = fitness;
		}

	}

	// populations,
	List<ChroNode> population = new ArrayList<ChroNode>();// populations
	List<ChroNode> subPopulation = new ArrayList<ChroNode>();// son of
																// populations
	List<HUI> huiSets = new ArrayList<HUI>();// the set of HUIs
												// chromosome
	// Create a list to store database
	List<List<Pair>> database = new ArrayList<List<Pair>>();
	// Create a list to store maximal pattern
	List<List<Integer>> maximalPatterns = new ArrayList<List<Integer>>();
	List<Double> percentage = new ArrayList<Double>();// the portation of twu
														// value of each
														// 1-HTWUIs in sum of
														// twu value
	treeNode orNorTree;// the root of OR/NOR-tree

	/**
	 * Default constructor
	 */
	public AlgoHUIM_GA_tree() {
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
	 * @throws IOException
	 *             exception if error while writing the file
	 */
	public void runAlgorithm(String input, String output, int minUtility)
			throws IOException {
		// reset maximum
		maxMemory = 0;

		startTimestamp = System.currentTimeMillis();

		writer = new BufferedWriter(new FileWriter(output));

		// We create a map to store the TWU of each item
		mapItemToTWU = new HashMap<Integer, Integer>();

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
				int transactionUtility = Integer.parseInt(split[1]);
				// for each item, we add the transaction utility to its TWU
				for (int i = 0; i < items.length; i++) {
					// convert item to integer
					Integer item = Integer.parseInt(items[i]);
					// get the current TWU of that item
					Integer twu = mapItemToTWU.get(item);
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

		//���������д�����Դ�����еģ�Ȼ�������д��������Ǵ����
		//���뱾����twuPattern����HTWU-1item�����²�����û�дﵽ���Ŀ��
		/*twuPattern = new ArrayList<Integer>(mapItemToTWU.keySet());
		Collections.sort(twuPattern);*/
		
		
		// SECOND DATABASE PASS TO CONSTRUCT THE DATABASE
		// OF 1-ITEMSETS HAVING TWU >= minutil (promising items)
		try {
			// prepare object for reading the file
			myInput = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(input))));
			// variable to count the number of transaction
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

				// Create a list to store items and its utility
				List<Pair> revisedTransaction = new ArrayList<Pair>();
				// Create a list to store items
				List<Integer> pattern = new ArrayList<Integer>();
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
						pattern.add(pair.item);
					}
				}
				// Copy the transaction into database but
				// without items with TWU < minutility
				database.add(revisedTransaction);
				// judge the pattern whether is a maximal pattern
				maximalJudge(pattern);
			}
		} catch (Exception e) {
			// to catch error while reading the input file
			e.printStackTrace();
		} finally {
			if (myInput != null) {
				myInput.close();
			}
		}
		
		twuPattern = new ArrayList<Integer>(mapItemToTWU.keySet());//����������ݴ�list���б���
		Collections.sort(twuPattern);//���ֵ�����
		
		for(int i=0;i<twuPattern.size();++i){
			if(mapItemToTWU.get(twuPattern.get(i))<minUtility){
				twuPattern.remove(i);
				--i;
			}
		}
		
		// check the memory usage
		checkMemory();

		// Mine the database recursively
		if (twuPattern.size() > 0) {
			double pMax, pMin;
			int m = database.size();
			int n = twuPattern.size();
			int temp1 = 0, temp2 = 0;
			// initial OR/NOR-tree
			orNorTree = initialTree(twuPattern);
			// initial population
			generatePop(minUtility);
			// calculate the mutation rate
			if (m > n) {
				pMin = 1 / (m + 0.0);
				pMax = 1 / (n + 0.0);
			} else {
				pMin = 1 / (n + 0.0);
				pMax = 1 / (m + 0.0);
			}

			for (int i = 0; i < iterations; i++) {
				// the portation of twu value of each 1-HTWUIs in sum of twu
				// value
				//percentage = roulettePercent();
				// update subPopulation and HUIset
				calculateRfitness();
				while (subPopulation.size() < pop_size) {
					// selection
					temp1 = selectChromosome();
					temp2 = selectChromosome();
					while (temp1 == temp2) {
						temp2 = selectChromosome();
					}
					// crossover
					crossover(temp1, temp2, minUtility);
				}
				// mutation
				subPopulation = rankedMutation(pMax, pMin, i, minUtility);

//				System.out.println(i + "-update end. HUIs No. is "
//						+ huiSets.size());
				// get next population
				subPopulation.addAll(population);
				rankData(subPopulation);
				for (int j = 0; j < population.size(); j++) {
					population.set(j, subPopulation.get(j));
				}
				subPopulation.clear();
			}
		}

		writeOut();
		// check the memory usage again and close the file.
		checkMemory();
		// close output file
		writer.close();
		// record end time
		endTimestamp = System.currentTimeMillis();
	}

	/**
	 * Method to check whether an itemset is the maximal pattern
	 * 
	 * @param pattern
	 *            the transaction in database need to judge
	 */
	private void maximalJudge(List<Integer> pattern) {
		int i, j, k, temp;
		List<Integer> tempPattern;

		if (maximalPatterns.size() == 0) {
			maximalPatterns.add(pattern);
		} else {
			for (i = 0; i < maximalPatterns.size(); i++) {
				tempPattern = maximalPatterns.get(i);
				j = 0;// the position of pattern
				k = 0;// the position of tempPattern
				temp = 0;
				while (j < pattern.size() && k < tempPattern.size()) {
					if (pattern.get(j) < tempPattern.get(k))
						j++;
					else if (pattern.get(j) > tempPattern.get(k))
						k++;
					else {
						j++;
						k++;
						temp++;
					}
				}
				if (temp == pattern.size() || temp == tempPattern.size()) {
					if (pattern.size() > tempPattern.size())// case1��pattern
															// contains
															// tempPattern
															// eg:abcd & abc
					{
						maximalPatterns.remove(i);
						maximalPatterns.add(pattern);
						break;
					} else
						break;// case 2: pattern = tempPattern eg:abc & abc
								// case 3: pattern is contained by tempPattern
								// eg��abc & abcd
				}

			}
			if (i == maximalPatterns.size())// case4��pattern and tempPattern
											// does not have containing
											// relationship eg:abc & abd
			{
				maximalPatterns.add(pattern);
			}
		}
	}

	/**
	 * Method to initial OR/NOR-tree structure
	 * 
	 * @param htwuiList
	 *            the list of items which has higher twu value than minUtil
	 * @return the root of OR.NOR-tree structure
	 */
	private treeNode initialTree(List<Integer> htwuiList) {
		int i, j, k;
		// root initialization
		treeNode root = new treeNode(htwuiList.get(0));
		// Create a node to point to current node
		treeNode currentNode;
		List<Integer> tempPattern;
		//
		for (i = 0; i < maximalPatterns.size(); i++) {
//			System.out.print(System.lineSeparator() + maximalPatterns.get(i) + ":");
			currentNode = root;
			tempPattern = maximalPatterns.get(i);
			j = 0;// j is the position of maximalPatterns.get(i)
			k = 0;// k is the position of twu pattern
			while (k < htwuiList.size()) {
				if (j < tempPattern.size()) {
					if (tempPattern.get(j) > htwuiList.get(k))// maximal:bcd
																// twu:abcd
					{
						if (currentNode.NOR == null) {
							treeNode leafNode;
							if (k + 1 < htwuiList.size()) {
								leafNode = new treeNode(htwuiList.get(k + 1));
							} else {
								leafNode = new treeNode(-1);
							}
							currentNode.NOR = leafNode;
						}
						k++;
						currentNode = currentNode.NOR;
					} else// maximal:abcd twu:abcd
					{
						if (currentNode.OR == null) {
							treeNode leafNode;
							if (k + 1 < htwuiList.size()) {
								leafNode = new treeNode(htwuiList.get(k + 1));
							} else {
								leafNode = new treeNode(-1);
							}
							currentNode.OR = leafNode;
						}
						k++;
						j++;
						currentNode = currentNode.OR;
					}
				} else {// j==tempPattern.size()-1 eg:maximal:ab twu:abcd
					if (currentNode.NOR == null) {
						treeNode leafNode;
						if (k + 1 < htwuiList.size()) {
							leafNode = new treeNode(htwuiList.get(k + 1));
						} else {
							leafNode = new treeNode(-1);
						}
						currentNode.NOR = leafNode;
					}
					currentNode = currentNode.NOR;
					k++;
				}
			}
		}
		return root;
	}

	/**
	 * This is the method to initial population
	 * 
	 * @param minUtility
	 *            minimum utility threshold
	 */
	private void generatePop(int minUtility)//
	{
		int i = 0, j, k;

		while (i < pop_size) {
			// generate a chromosome
			ChroNode tempNode = new ChroNode(twuPattern.size());
			// initial chromosome according to OR/NOR-tree
			tempNode = chromosomeInitial(tempNode);
			k = 0;
			for (j = 0; j < twuPattern.size(); j++) {
				if (tempNode.chromosome.get(j) == 1)
					k++;
			}
			// calculate the fitenss of chromosome
			tempNode.fitness = fitCalculate(tempNode.chromosome, k);
			tempNode.rank = 0;
			population.add(tempNode);
			if (tempNode.fitness >= minUtility) {
				insert(tempNode);
			}
			i++;
		}
	}
	/**
	 * Method to crossover population[temp1] and population[temp2]
	 * 
	 * @param temp1
	 *            the number of chromosome to crossover
	 * @param temp2
	 *            the number of chromosome to crossover
	 * @param minUtility
	 */
	private void crossover(int temp1, int temp2, int minUtility) {
		int i = 0;
		int tempA = 0, tempB = 0;// record the number of 1 in chromosomes
		List<Integer> temp1Chro = new ArrayList<Integer>();// record the
															// chromosome after
															// crossover
		List<Integer> temp2Chro = new ArrayList<Integer>();
		ChroNode tempNode = new ChroNode();

		int position = (int) (Math.random() * twuPattern.size());// this is the
																	// position
																	// to
																	// crossover
		for (i = 0; i < twuPattern.size(); i++) {// i<=position, crossover
			if (i <= position) {
				temp1Chro.add(population.get(temp2).chromosome.get(i));
				if (temp1Chro.get(i) == 1)
					tempA++;
				temp2Chro.add(population.get(temp1).chromosome.get(i));
				if (temp2Chro.get(i) == 1)
					tempB++;
			} else {// i>position, not crossover
				temp1Chro.add(population.get(temp1).chromosome.get(i));
				if (temp1Chro.get(i) == 1)
					tempA++;
				temp2Chro.add(population.get(temp2).chromosome.get(i));
				if (temp2Chro.get(i) == 1)
					tempB++;
			}
		}
		// get the chromosome after crossover
		tempNode.chromosome = temp1Chro;
		tempNode.fitness = fitCalculate(temp1Chro, tempA);
		tempNode.rank = 0;
		subPopulation.add(tempNode);
		if (tempNode.fitness >= minUtility) {
			insert(tempNode);
		}

		tempNode.chromosome = temp2Chro;
		tempNode.fitness = fitCalculate(temp2Chro, tempB);
		tempNode.rank = 0;
		subPopulation.add(tempNode);
		if (tempNode.fitness >= minUtility) {
			insert(tempNode);
		}
	}

	/**
	 * Method to rank chromosomes in population according to fitness
	 * 
	 * @param tempPop
	 * @return
	 */
	private void rankData(List<ChroNode> tempPop) {
		int i, j, p, q, temp;

		for (i = 0; i < tempPop.size() - 1; i++) {
			p = i;
			for (j = i + 1; j < tempPop.size(); j++) {
				if (tempPop.get(p).fitness < tempPop.get(j).fitness)
					p = j;
			}
			if (i != p) {
				temp = tempPop.get(i).fitness;
				tempPop.get(i).fitness = tempPop.get(p).fitness;
				tempPop.get(p).fitness = temp;
				for (q = 0; q < twuPattern.size(); q++) {
					temp = tempPop.get(i).chromosome.get(q);
					tempPop.get(i).chromosome.set(q,
							tempPop.get(p).chromosome.get(q));
					tempPop.get(p).chromosome.set(q, temp);
				}
			}
			tempPop.get(i).rank = i + 1;
		}
		tempPop.get(i).rank = i + 1;
	}

	/**
	 * Method to get the rank of subPopulation in the set of subPopulation and
	 * population
	 * 
	 * @return
	 */
	private List<Integer> getRank() {
		int i, j;
		List<Integer> rank = new ArrayList<Integer>();
		for (i = 0; i < subPopulation.size(); i++) {
			int temp = 0;// record the chromosome has better fitness than it
			for (j = 0; j < subPopulation.size(); j++) {
				if (i != j) {
					if (subPopulation.get(i).fitness <= subPopulation.get(j).fitness) {
						temp++;
					}
				}
			}
			rank.add(temp + 1);
		}

		return rank;
	}

	/**
	 * 
	 * @param pMax
	 *            maximum mutation rate
	 * @param pMin
	 *            minimum mutation rate
	 * @param currentIteration
	 *            current iteration
	 * @param minUtility
	 *            minimum utility threshold
	 * @return
	 */
	private List<ChroNode> rankedMutation(double pMax, double pMin,
			int currentIteration, int minUtility) {
		double pm, rankNum;// pm is ranked mutation rate
		List<Integer> record = getRank();
		for (int i = 0; i < pop_size; i++) {
			// calculate pm
			pm = (pMax - (pMax - pMin) * currentIteration / iterations)
					* record.get(i) / subPopulation.size();
			rankNum = Math.random();
			// compare pm an random
			if (rankNum < pm) {// single point mutation
				int temp = (int) (Math.random() * twuPattern.size());
				if (subPopulation.get(i).chromosome.get(temp) == 1) {
					subPopulation.get(i).chromosome.set(temp, 0);
				} else {
					subPopulation.get(i).chromosome.set(temp, 1);
				}
				// get the number of 1 in chromosome
				int k = 0;
				for (int j = 0; j < twuPattern.size(); j++) {
					if (subPopulation.get(i).chromosome.get(j) == 1) {
						k++;
					}
				}
				// calculate the fitness of chromosome
				subPopulation.get(i).fitness = fitCalculate(
						subPopulation.get(i).chromosome, k);
				// insert chromosome has higher utility into huiSets
				if (subPopulation.get(i).fitness >= minUtility) {
					insert(subPopulation.get(i));
				}
			}

		}

		return subPopulation;
	}

	/**
	 * Method to initial chromosome use OR/NOR-tree
	 * 
	 * @param tempNode
	 * @return the updated chromosome
	 */
	private ChroNode chromosomeInitial(ChroNode tempNode) {
		int i, temp, temp1;
		treeNode currentNode;// point to current node
		currentNode = orNorTree;

		for (i = 0; i < twuPattern.size(); i++) {
			if (currentNode.OR == null) {
				tempNode.chromosome.set(i, 0);
				currentNode = currentNode.NOR;
			} else if (currentNode.NOR == null) {
				temp1 = Math.random() > 0.5 ? 1 : 0;
				if (temp1 == 1) {
					tempNode.chromosome.set(i, 1);
					currentNode = currentNode.OR;
				} else {
					tempNode.chromosome.set(i, 0);
					currentNode = currentNode.OR;
				}
			} else // OR & NOR are not null
			{

				temp = Math.random() > 0.5 ? 1 : 0;
				if (temp == 1)// or
				{
					temp1 = Math.random() > 0.5 ? 1 : 0;
					if (temp1 == 1) {
						tempNode.chromosome.set(i, 1);
						currentNode = currentNode.OR;
					} else {
						tempNode.chromosome.set(i, 0);
						currentNode = currentNode.OR;
					}
				} else // nor
				{
					tempNode.chromosome.set(i, 0);
					currentNode = currentNode.NOR;
				}
			}
		}
		return tempNode;
	}

	/**
	 * Method to inseret tempChroNode to huiSets
	 * 
	 * @param tempChroNode
	 *            the chromosome to be inserted
	 */
	private void insert(ChroNode tempChroNode) {
		int i;
		StringBuilder temp = new StringBuilder();
		for (i = 0; i < twuPattern.size(); i++) {
			if (tempChroNode.chromosome.get(i) == 1) {
				temp.append(twuPattern.get(i));
				temp.append(' ');
			}
		}
		// huiSets is null
		if (huiSets.size() == 0) {
			huiSets.add(new HUI(temp.toString(), tempChroNode.fitness));
		} else {
			// huiSets is not null, judge whether exist an itemset in huiSets
			// same with tempChroNode
			for (i = 0; i < huiSets.size(); i++) {
				if (temp.toString().equals(huiSets.get(i).itemset)) {
					break;
				}
			}
			// if not exist same itemset in huiSets with tempChroNode,insert it
			// into huiSets
			if (i == huiSets.size())
				huiSets.add(new HUI(temp.toString(), tempChroNode.fitness));
		}
	}

	/**
	 * Method to calculate the fitness of each chromosome
	 * 
	 * @param tempChroNode
	 * @param k
	 *            the number of 1 in chromosome
	 * @return fitness
	 */
	private int fitCalculate(List<Integer> tempChroNode, int k) {
		if (k == 0)
			return 0;
		int i, j, p, q, temp;

		int sum, fitness = 0;
		for (p = 0; p < database.size(); p++) {// p scan the transactions in
												// database
			i = 0;
			j = 0;
			q = 0;
			temp = 0;
			sum = 0;
			// j scan the 1 in chromosome, q scan each transaction, i scan each
			// chromosome
			while (j < k && q < database.get(p).size()
					&& i < tempChroNode.size()) {
				if (tempChroNode.get(i) == 1) {
					if (database.get(p).get(q).item < twuPattern.get(i))
						q++;
					else if (database.get(p).get(q).item == twuPattern.get(i)) {
						sum = sum + database.get(p).get(q).utility;
						j++;
						q++;
						temp++;
						i++;
					} else if (database.get(p).get(q).item > twuPattern.get(i)) {
						j++;
						i++;
					}
				} else
					i++;
			}
			if (temp == k) {
				fitness = fitness + sum;
			}
		}
		return fitness;
	}
	/**
	 * ����Ⱦɫ����Ϣ��rfitness
	 */
	public void calculateRfitness(){
		int sum=0;
		int temp=0;
		//�ϼ���Ӧֵ
		for(int i=0; i< population.size();++i){
			sum =sum+population.get(i).fitness;
		}
		//��������
		for(int i=0; i< population.size();++i){
			temp =temp+population.get(i).fitness;
			population.get(i).rfitness= temp/(sum+0.0);
		}
	}
	
	/**
	 * ѡ��Ⱦɫ����н������
	 * @return
	 */
	private int selectChromosome() {
		int i, temp = 0;
		double randNum;
		randNum = Math.random();
		for (i = 0; i < population.size(); i++) {
			if (i == 0) {
				if ((randNum >= 0) && (randNum <= population.get(0).rfitness)) {
					temp = 0;
					break;
				}
			} else if ((randNum > population.get(i - 1).rfitness)
					&& (randNum <= population.get(i).rfitness)) {
				temp = i;
				break;
			}
		}
		return temp;
	}

	/**
	 * Method to write a high utility itemset to the output file.
	 * 
	 * @throws IOException
	 */
	private void writeOut() throws IOException {
		// Create a string buffer
		StringBuilder buffer = new StringBuilder();
		// append the prefix
		for (int i = 0; i < huiSets.size(); i++) {
			buffer.append(huiSets.get(i).itemset);
			// append the utility value
			buffer.append("#UTIL: ");
			buffer.append(huiSets.get(i).fitness);
			buffer.append(System.lineSeparator());
		}
		// write to file
		writer.write(buffer.toString());
		writer.newLine();
	}

	/**
	 * Method to check the memory usage and keep the maximum memory usage.
	 */
	private void checkMemory() {
		// get the current memory usage
		double currentMemory = (Runtime.getRuntime().totalMemory() - Runtime
				.getRuntime().freeMemory()) / 1024d / 1024d;
		// if higher than the maximum until now
		if (currentMemory > maxMemory) {
			// replace the maximum with the current memory usage
			maxMemory = currentMemory;
		}
	}

	/**
	 * Print statistics about the latest execution to System.out.
	 */
	public void printStats() {
		System.out
				.println("=============  HUIM-GA-tree ALGORITHM v.2.11 - STATS =============");
		System.out.println(" Total time ~ " + (endTimestamp - startTimestamp)
				+ " ms");
		System.out.println(" Memory ~ " + maxMemory + " MB");
		System.out.println(" High-utility itemsets count : " + huiSets.size());
		System.out
				.println("===================================================");
	}
}
