//  ZDT3.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package jmetal.problems;

//This if for the first 75 problems for effectiveness
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.main.SSBP.Rule;
import com.main.SSBP.TestCase;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.encodings.solutionType.ArrayRealSolutionType;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;
import jmetal.util.wrapper.XReal;

/**
 * Class representing problem ZDT3
 */
public class TwoObj extends Problem {
	private List<TestCase> testCaseList = new ArrayList<>();
	private Map<String, List<Rule>> ruleMap = new HashMap<String, List<Rule>>();
	private Map<String, Set<String>> failedRuleNameMap = new HashMap<String, Set<String>>();
	private Map<String, Set<String>> partialFailedRuleNameMap = new HashMap<String, Set<String>>();
	private Map<String, Set<String>> passedRuleNameMap = new HashMap<String, Set<String>>();
	private Map<String, Set<String>> partialPassedRuleNameMap = new HashMap<String, Set<String>>();

	private Map<String, Integer> countRule = new HashMap<String, Integer>();
	private Map<String, Integer> countRuleOneDecides = new HashMap<String, Integer>();
	private List<String> skipTC = new ArrayList<String>();

	private int numberOfCase;

	private ArrayList<String> caseOutTemp;

	/**
	 * Constructor. Creates default instance of problem ZDT3 (30 decision variables.
	 * 
	 * @param solutionType
	 *            The solution type must "Real", "BinaryReal, and "ArrayReal".
	 */
	public TwoObj(String solutionType) throws ClassNotFoundException {

		this(solutionType, 5000); // 30 variables by default
	} // ZDT3

	/**
	 * Constructor. Creates a instance of ZDT3 problem.
	 * 
	 * @param numberOfVariables
	 *            Number of variables.
	 * @param solutionType
	 *            The solution type must "Real", "BinaryReal, and "ArrayReal".
	 */
	public TwoObj(String solutionType, Integer numberOfVariables) {
		numberOfCase = numberOfVariables;
		numberOfVariables_ = numberOfVariables.intValue();
		numberOfObjectives_ = 2;
		numberOfConstraints_ = 0;
		problemName_ = "Kongsberg";

		upperLimit_ = new double[numberOfVariables_];
		lowerLimit_ = new double[numberOfVariables_];

		for (int var = 0; var < numberOfVariables_; var++) {
			lowerLimit_[var] = 0;
			upperLimit_[var] = 1;
		} // for

		if (solutionType.compareTo("BinaryReal") == 0)
			solutionType_ = new BinaryRealSolutionType(this);
		else if (solutionType.compareTo("Real") == 0)
			solutionType_ = new RealSolutionType(this);
		else if (solutionType.compareTo("ArrayReal") == 0)
			solutionType_ = new ArrayRealSolutionType(this);
		else {
			System.out.println("Error: solution type " + solutionType + " invalid");
			System.exit(-1);
		}
	} // ZDT3

	/**
	 * Evaluates a solution
	 * 
	 * @param solution
	 *            The solution to evaluate
	 * @throws JMException
	 */
	public void evaluate(Solution solution) throws JMException {
		XReal vars = new XReal(solution);
		double[] x = new double[numberOfVariables_];
		double[] f = new double[numberOfObjectives_];

		Map<Double, Integer> numVariablesHash = new HashMap<Double, Integer>();
		List<Double> numVariables = new ArrayList<Double>();
		// sort the numbers
		for (int i = 0; i < numberOfVariables_; i++) {
			while (numVariables.contains(vars.getValue(i)))
				vars.setValue(i, vars.getValue(i) + 0.0001);
			numVariables.add(vars.getValue(i));
			numVariablesHash.put(vars.getValue(i), i);
		}
		Collections.sort(numVariables, Collections.reverseOrder());
		ArrayList<TestCase> tempCase2 = new ArrayList<TestCase>();
		caseOutTemp = new ArrayList<String>();
		for (int i = 0; i < numberOfVariables_; i++) {
			tempCase2.add(testCaseList.get(numVariablesHash.get(numVariables.get(i))));
			caseOutTemp.add(testCaseList.get(numVariablesHash.get(numVariables.get(i))).getName());
		}
		if (caseOutTemp.size() > 0) {
			double usedFailedRule = 0, usedPassedRule = 0, usedTime = 0, usedFDC = 0, usedRule = 0;
			double totalFailedRule = 0, totalPassedRule = 0, totalTime = 0, totalFDC = 0, totalRule = 0;
			int count = 0;
			Set<String> coveredFailedTestCase = new HashSet<String>();
			Set<String> coveredPassedTestCase = new HashSet<String>();
			int pos = 0;
			for (int j = 0; j < tempCase2.size(); j++) {
				TestCase tempCase = new TestCase();
				tempCase = tempCase2.get(j);

				int num = 0;
				if (countRule.containsKey(tempCase.getName()))
					num = countRule.get(tempCase.getName());
				if (countRuleOneDecides.containsKey(tempCase.getName()))
					num += countRuleOneDecides.get(tempCase.getName());

				usedRule += (num * ((double) (numberOfVariables_ - count) / numberOfVariables_));

				usedTime += (tempCase.getExecutionTime()
						* ((double) (numberOfVariables_ - count) / numberOfVariables_));
				usedFDC += (tempCase.getFDC() * ((double) (numberOfVariables_ - count) / numberOfVariables_));

				totalRule += num;

				totalTime += tempCase.getExecutionTime();
				totalFDC += tempCase.getFDC();
				count++;
			}
			// System.out.println(totalFailedRule + " " + totalPassedRule);
			double objRule, objTime, objFDC;
			objRule = usedRule / totalRule;
			objTime = usedTime / totalTime;
			objFDC = usedFDC / totalFDC;
			f[0] = 1 - objRule;
			// f[1] = objTime;
			f[1] = 1 - objFDC;
			double normDistance = 0;
			for (int i = 0; i < numberOfObjectives_; i++) {
				solution.setObjective(i, f[i]);
				normDistance += Math.pow(f[i], 2);
			}

			Set<String> nameSet = new HashSet<String>();
			cases = new String[caseOutTemp.size() + skipTC.size()];
			cases = caseOutTemp.toArray(cases);

			solution.setCases_(cases);
			solution.setNormDistance(Math.sqrt(normDistance));
		}
	} // evaluate

	// normalization function
	public double Nor(double n) {
		double m = n / (n + 1);
		return m;
	}

	// Generate random
	public int randInt(int min, int max) {

		// NOTE: Usually this should be a field rather than a method
		// variable so that it is not re-seeded every call.
		Random rand = new Random();

		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;
	}

	/**
	 * Evaluates the constraint overhead of a solution
	 * 
	 * @param solution
	 *            The solution
	 * @throws JMException
	 */
	public void evaluateConstraints(Solution solution) throws JMException {

	}

	/**
	 * Returns the value of the ZDT3 function H.
	 * 
	 * @param f
	 *            First argument of the function H.
	 * @param g
	 *            Second argument of the function H.
	 */
	public double evalH(double f, double g) {
		double h = 0.0;
		h = 1.0 - java.lang.Math.sqrt(f / g) - (f / g) * java.lang.Math.sin(10.0 * java.lang.Math.PI * f);
		return h;
	} // evalH

	public double getCombination(int n, int r) {
		double ret = 0;
		int numerator = n * (n - 1);

		ret = (double) numerator / r;
		return ret;
	}

	public int getFactorial(int number) {
		int factorial = number;
		for (int i = (number - 1); i > 1; i--) {
			factorial = factorial * i;
		}
		return factorial;
	}

	public List<TestCase> getTestCaseList() {
		return testCaseList;
	}

	public void setTestCaseList(List<TestCase> testCaseList) {
		this.testCaseList = testCaseList;
	}

	public Map<String, List<Rule>> getRuleMap() {
		return ruleMap;
	}

	public void setRuleMap(Map<String, List<Rule>> ruleMap) {
		this.ruleMap = ruleMap;
	}

	public Map<String, Set<String>> getFailedRuleNameMap() {
		return failedRuleNameMap;
	}

	public Map<String, Set<String>> getPassedRuleNameMap() {
		return passedRuleNameMap;
	}

	public void setPassedRuleNameMap(Map<String, Set<String>> passedRuleNameMap) {
		this.passedRuleNameMap = passedRuleNameMap;
	}

	public Map<String, Set<String>> getPartialPassedRuleNameMap() {
		return partialPassedRuleNameMap;
	}

	public void setPartialPassedRuleNameMap(Map<String, Set<String>> partialPassedRuleNameMap) {
		this.partialPassedRuleNameMap = partialPassedRuleNameMap;
	}

	public void setFailedRuleNameMap(Map<String, Set<String>> failedRuleNameMap) {
		this.failedRuleNameMap = failedRuleNameMap;
	}

	public Map<String, Set<String>> getPartialFailedRuleNameMap() {
		return partialFailedRuleNameMap;
	}

	public void setPartialFailedRuleNameMap(Map<String, Set<String>> partialFailedRuleNameMap) {
		this.partialFailedRuleNameMap = partialFailedRuleNameMap;
	}

	public Map<String, Integer> getCountRule() {
		return countRule;
	}

	public void setCountRule(Map<String, Integer> countRule) {
		this.countRule = countRule;
	}

	public Map<String, Integer> getCountRuleOneDecides() {
		return countRuleOneDecides;
	}

	public void setCountRuleOneDecides(Map<String, Integer> countRuleOneDecides) {
		this.countRuleOneDecides = countRuleOneDecides;
	}

	public List<String> getSkipTC() {
		return skipTC;
	}

	public void setSkipTC(List<String> skipTC) {
		this.skipTC = skipTC;
	}
}
