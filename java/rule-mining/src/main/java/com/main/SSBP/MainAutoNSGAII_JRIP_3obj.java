package com.main.SSBP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import jmetal.metaheuristics.nsgaII.NSGAII;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.ThreeObj;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.JMException;

public class MainAutoNSGAII_JRIP_3obj {
	// Location of files
	private static String readTime = "files/testCase.txt";
	// rules obtained from the rule mining algorithm
	private static String readFailedRule = "files/failed";
	private static String readPassedRule = "files/passed";

	private static Algorithm algorithm;
	private static int numberTestCase = 0;
	private static File fileName;

	private static List<TestCase> testCaseList = new ArrayList<TestCase>();
	private static Map<String, Set<String>> ruleNameMap = new HashMap<String, Set<String>>();

	private static Map<String, Integer> countRule = new HashMap<String, Integer>();
	private static Map<String, Integer> countRuleOneDecides = new HashMap<String, Integer>();
	private static Map<String, Integer> countRuleOneDecidesToSend = new HashMap<String, Integer>();

	public static void main(String[] args) throws Exception {
		String variable, result;
		Problem problem;
		long initTime = System.currentTimeMillis();

		// repetitions
		for (int r = 1; r < 2; r++) {
			reset();
			readTestCase();
			numberTestCase = testCaseList.size();
			problem = new ThreeObj("ArrayReal", numberTestCase);
			algorithm = new NSGAII(problem);
			variable = "output/var_nsgaii" + r;
			result = "output/nsgaii" + r;
			calculate(problem);

			run(algorithm, problem, variable, result, initTime);
		}
		long estimatedTime = System.currentTimeMillis() - initTime;
		System.out.println("Time is :" + estimatedTime);
	}

	public static void reset() {
		testCaseList = new ArrayList<>();
		ruleNameMap = new HashMap<String, Set<String>>();
		countRule = new HashMap<String, Integer>();
		countRuleOneDecides = new HashMap<String, Integer>();
		countRuleOneDecidesToSend = new HashMap<String, Integer>();
	}

	public static void countUniqueOccurences() {
		Map<String, Set<String>> countUnique = new HashMap<String, Set<String>>();
		for (Map.Entry<String, Set<String>> entry : ruleNameMap.entrySet()) {
			String key = entry.getKey().replaceAll("\\D+", "");
			Set<String> tempString = new HashSet<String>();
			if (countUnique.containsKey(key)) {
				tempString = countUnique.get(key);
			}
			for (String val : entry.getValue()) {
				String valTemp = val.replaceAll("\\D+", "");
				tempString.add(valTemp);
			}

			countUnique.put(key, tempString);
			countRuleOneDecidesToSend.put(key, tempString.size());
		}
	}

	public static void countOccurences() {
		for (Map.Entry<String, Set<String>> entry : ruleNameMap.entrySet()) {
			int number = entry.getValue().size();
			String key = entry.getKey();
			// System.out.println(key);
			int totalNumber = 0;
			if (countRule.containsKey(key))
				totalNumber = countRule.get(key);
			totalNumber += number;
			countRule.put(key, totalNumber);
		}
	}

	public static void countOccurencesTC() {
		for (Map.Entry<String, Integer> entry : countRule.entrySet()) {
			String key = entry.getKey().replaceAll("\\D+", "");
			if (countRuleOneDecidesToSend.containsKey(key)) {
				int size = countRuleOneDecidesToSend.get(key);
				countRuleOneDecidesToSend.put(key, size + entry.getValue());
			} else
				countRuleOneDecidesToSend.put(key, entry.getValue());
		}
	}

	public static void calculate(Problem problem) {
		// getTestCaseTime();
		getRule(readFailedRule);
		getRule(readPassedRule);
		// countOccurences();
		// countOccurencesTC();
		countUniqueOccurences();
		problem.setTestCaseList(testCaseList);

		problem.setCountRule(countRuleOneDecidesToSend);
		// printRule(ruleNameMap);
		// System.out.println("Partial starts");
		// printRule(partialRuleNameMap);
		algorithm = new NSGAII(problem);
	}

	public static long run(Algorithm algorithm, Problem problem, String variable, String result, long initTime)
			throws JMException, SecurityException, IOException, ClassNotFoundException {
		Operator crossover; // Crossover operator
		Operator mutation; // Mutation operator
		Operator selection; // Selection operator
		HashMap parameters; // Operator parameters

		QualityIndicator indicators; // Object to get quality indicators
		indicators = null;

		// Algorithm parameters
		algorithm.setInputParameter("populationSize", 100);
		algorithm.setInputParameter("maxEvaluations", 50000);

		// Mutation and Crossover for Real codification
		parameters = new HashMap();
		// parameters.put("probability", 1.0) ;
		parameters.put("probability", 0.9);
		parameters.put("distributionIndex", 20.0);
		crossover = CrossoverFactory.getCrossoverOperator("SinglePointCrossover_DPR", parameters);

		parameters = new HashMap();
		// parameters.put("probability", 0.2) ;
		parameters.put("probability", 1.0 / problem.getNumberOfVariables());
		parameters.put("distributionIndex", 20.0);
		mutation = MutationFactory.getMutationOperator("SwapMutationModified", parameters);

		// Selection Operator n
		parameters = null;
		selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters);

		// Add the operators to the algorithm
		algorithm.addOperator("crossover", crossover);
		algorithm.addOperator("mutation", mutation);
		algorithm.addOperator("selection", selection);

		// Add the indicator object to the algorithm
		algorithm.setInputParameter("indicators", indicators);

		// Execute the Algorithm
		SolutionSet population = algorithm.execute();
		long estimatedTime = System.currentTimeMillis() - initTime;

		// Result messages
		// population.printVariablesToFile(variable+result);
		population.printCaseToFile(variable);
		population.printObjectivesToFile(result);

		return estimatedTime;
	} // main

	public static void createFile(int count) throws Exception {
		fileName = new File("output/out.txt");
		// if file does not exists, then create it
		if (!fileName.exists()) {
			fileName.createNewFile();
		}
	}

	public static void deleteFile(String file) throws Exception {
		File fileDelete = new File(file);
		fileDelete.delete();
	}

	public static void readTestCase() {
		int counter = 0;
		try {
			BufferedReader in = new BufferedReader(new FileReader(readTime));
			String str;
			while ((str = in.readLine()) != null) {
				if (counter == 0) {
					counter++;
					continue;
				}
				String line = str;
				String[] details = line.split("\t");

				TestCase tc = new TestCase();
				tc.setName(details[0]);
				tc.setExecutionTime(Double.parseDouble(details[1]));
				tc.setFDC(Double.parseDouble(details[2]));
				testCaseList.add(tc);
			}
			in.close();
		} catch (IOException e) {
		}
	}

	public static void getRule(String readRule) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(readRule));
			String str;
			while ((str = in.readLine()) != null) {
				String line = str;
				String[] details = line.split("=");

				Rule dependentRule = new Rule();
				String[] dependent = details[1].split("\\(");
				dependentRule.setName(dependent[0]);

				if (details[0].contains(",")) {
					String[] requiredRules = details[0].split(",");
					for (int j = 0; j < requiredRules.length; j++) {
						Set<String> ruleNameSetTemp = new HashSet<String>();
						if (ruleNameMap.containsKey(requiredRules[j])) {
							ruleNameSetTemp = ruleNameMap.get(requiredRules[j]);
						}
						ruleNameSetTemp.add(dependentRule.getName());
						ruleNameMap.put(requiredRules[j], ruleNameSetTemp);
					}
					// System.out.println(text);
				} else {
					List<Rule> ruleListTemp = new ArrayList<Rule>();
					Set<String> ruleNameSetTemp = new HashSet<String>();
					if (ruleNameMap.containsKey(details[0])) {
						ruleNameSetTemp = ruleNameMap.get(details[0]);
					}
					ruleNameSetTemp.add(dependentRule.getName());
					ruleNameMap.put(details[0], ruleNameSetTemp);
				}
			}
			in.close();
		} catch (IOException e) {
		}
	}

	public static void getRuleOneDecides(String readRule) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(readRule));
			String str;
			while ((str = in.readLine()) != null) {
				String line = str;
				String[] details = line.split("=");
				if (details[1].contains(",")) {
					String[] requiredRules = details[1].split(",");
					Set<String> ruleNameSetTemp = new HashSet<String>();
					for (int j = 0; j < requiredRules.length; j++) {
						ruleNameSetTemp.add(requiredRules[j]);
					}
					Set<String> ruleNameSet = new HashSet<String>();

					if (ruleNameMap.containsKey(details[0])) {
						ruleNameSet = ruleNameMap.get(details[0]);
					}
					ruleNameSet.addAll(ruleNameSetTemp);
					ruleNameMap.put(details[0], ruleNameSet);

					// System.out.println(text);
				} else {
					List<Rule> ruleListTemp = new ArrayList<Rule>();
					Set<String> ruleNameSetTemp = new HashSet<String>();
					if (ruleNameMap.containsKey(details[0])) {
						ruleNameSetTemp = ruleNameMap.get(details[0]);
					}
					ruleNameSetTemp.add(details[1]);
					ruleNameMap.put(details[0], ruleNameSetTemp);
				}

			}
			in.close();
		} catch (

		IOException e) {
		}
	}

	public static void getRuleWithinRule(Map<String, Set<String>> ruleNameMap) {
		for (Map.Entry<String, Set<String>> entry : ruleNameMap.entrySet()) {
			Set<String> val = new HashSet<String>();
			val.addAll(entry.getValue());
			for (String entryWithin : entry.getValue()) {
				Set<String> oldKeys = new HashSet<String>();
				getKeyWithin(val, entryWithin, oldKeys, ruleNameMap);
			}
			entry.setValue(val);
			// System.out.println(entry.getKey());
		}
	}

	public static void getKeyWithin(Set<String> tempSet, String key, Set<String> oldKeys,
			Map<String, Set<String>> ruleNameMap) {
		oldKeys.add(key);
		if (ruleNameMap.containsKey(key)) {
			// System.out.println(key);
			for (String entryWithin : ruleNameMap.get(key)) {
				if (!tempSet.contains(entryWithin) && !oldKeys.contains(entryWithin))
					getKeyWithin(tempSet, entryWithin, oldKeys, ruleNameMap);
				else
					tempSet.add(entryWithin);
			}
			tempSet.add(key);
		}
		tempSet.add(key);
	}

	public static void printRule(Map<String, Set<String>> ruleNameMap) {
		for (Map.Entry<String, Set<String>> entry : ruleNameMap.entrySet()) {
			System.out.print(entry.getKey() + ":  ");
			for (String name : entry.getValue())
				System.out.print(name + "\t ");
			System.out.println("");
		}
	}

}
