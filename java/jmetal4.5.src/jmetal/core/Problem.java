//  Problem.java
//
//  Authors:
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

package jmetal.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.main.SSBP.Rule;
import com.main.SSBP.TestCase;

import jmetal.util.JMException;

/**
 * Abstract class representing a multiobjective optimization problem
 */
public abstract class Problem implements Serializable {

	/**
	 * Defines the default precision of binary-coded variables
	 */
	private final static int DEFAULT_PRECISSION = 16;

	/**
	 * Stores the number of variables of the problem
	 */
	protected int numberOfVariables_;

	/**
	 * Stores the number of objectives of the problem
	 */
	protected int numberOfObjectives_;

	/**
	 * Stores the number of constraints of the problem
	 */
	protected int numberOfConstraints_;

	/**
	 * Stores the problem name
	 */
	protected String problemName_;

	/**
	 * Stores the type of the solutions of the problem
	 */
	protected SolutionType solutionType_;

	/**
	 * Stores the lower bound values for each encodings.variable (only if needed)
	 */
	protected double[] lowerLimit_;

	/**
	 * Stores the upper bound values for each encodings.variable (only if needed)
	 */
	protected double[] upperLimit_;

	/**
	 * Stores the number of bits used by binary-coded variables (e.g., BinaryReal
	 * variables). By default, they are initialized to DEFAULT_PRECISION)
	 */
	private int[] precision_;

	/**
	 * Stores the length of each encodings.variable when applicable (e.g., Binary
	 * and Permutation variables)
	 */
	protected int[] length_;

	protected double norm;

	/**
	 * Stores the type of each encodings.variable
	 */
	// public Class [] variableType_;

	/**
	 * Constructor.
	 */
	public Problem() {
		solutionType_ = null;
	} // Problem

	/**
	 * Constructor.
	 */
	public Problem(SolutionType solutionType) {
		solutionType_ = solutionType;
	} // Problem

	/**
	 * Gets the number of decision variables of the problem.
	 * 
	 * @return the number of decision variables.
	 */
	public int getNumberOfVariables() {
		return numberOfVariables_;
	} // getNumberOfVariables

	/**
	 * Sets the number of decision variables of the problem.
	 */
	public void setNumberOfVariables(int numberOfVariables) {
		numberOfVariables_ = numberOfVariables;
	} // getNumberOfVariables

	/**
	 * Gets the the number of objectives of the problem.
	 * 
	 * @return the number of objectives.
	 */
	public int getNumberOfObjectives() {
		return numberOfObjectives_;
	} // getNumberOfObjectives

	/**
	 * Gets the lower bound of the ith encodings.variable of the problem.
	 * 
	 * @param i
	 *            The index of the encodings.variable.
	 * @return The lower bound.
	 */
	public double getLowerLimit(int i) {
		return lowerLimit_[i];
	} // getLowerLimit

	/**
	 * Gets the upper bound of the ith encodings.variable of the problem.
	 * 
	 * @param i
	 *            The index of the encodings.variable.
	 * @return The upper bound.
	 */
	public double getUpperLimit(int i) {
		return upperLimit_[i];
	} // getUpperLimit

	/**
	 * Evaluates a <code>Solution</code> object.
	 * 
	 * @param solution
	 *            The <code>Solution</code> to evaluate.
	 */
	public abstract void evaluate(Solution solution) throws JMException;

	public void evaluateRS(Solution solution) throws JMException {

	}

	/**
	 * Gets the number of side constraints in the problem.
	 * 
	 * @return the number of constraints.
	 */
	public int getNumberOfConstraints() {
		return numberOfConstraints_;
	} // getNumberOfConstraints

	/**
	 * Evaluates the overall constraint violation of a <code>Solution</code> object.
	 * 
	 * @param solution
	 *            The <code>Solution</code> to evaluate.
	 */
	public void evaluateConstraints(Solution solution) throws JMException {
		// The default behavior is to do nothing. Only constrained problems have
		// to
		// re-define this method
	} // evaluateConstraints

	/**
	 * Returns the number of bits that must be used to encode binary-real variables
	 * 
	 * @return the number of bits.
	 */
	public int getPrecision(int var) {
		return precision_[var];
	} // getPrecision

	/**
	 * Returns array containing the number of bits that must be used to encode
	 * binary-real variables.
	 * 
	 * @return the number of bits.
	 */
	public int[] getPrecision() {
		return precision_;
	} // getPrecision

	/**
	 * Sets the array containing the number of bits that must be used to encode
	 * binary-real variables.
	 * 
	 * @param precision
	 *            The array
	 */
	public void setPrecision(int[] precision) {
		precision_ = precision;
	} // getPrecision

	/**
	 * Returns the length of the encodings.variable.
	 * 
	 * @return the encodings.variable length.
	 */
	public int getLength(int var) {
		if (length_ == null)
			return DEFAULT_PRECISSION;
		return length_[var];
	} // getLength

	/**
	 * Sets the type of the variables of the problem.
	 * 
	 * @param type
	 *            The type of the variables
	 */
	public void setSolutionType(SolutionType type) {
		solutionType_ = type;
	} // setSolutionType

	/**
	 * Returns the type of the variables of the problem.
	 * 
	 * @return type of the variables of the problem.
	 */
	public SolutionType getSolutionType() {
		return solutionType_;
	} // getSolutionType

	/**
	 * Returns the problem name
	 * 
	 * @return The problem name
	 */
	public String getName() {
		return problemName_;
	}

	protected String[] cases;

	public String[] getCases() {
		return cases;
	}

	public void setCases(String[] cases) {
		this.cases = cases;
	}

	protected double timeWeight, priorityWeight, probabilityWeight, consequenceWeight, riskWeight, effPriorityWeight,
			effProbabilityWeight, effConsequenceWeight, effRiskWeight;
	protected int timeBudget;

	public int getTimeBudget() {
		return timeBudget;
	}

	public void setTimeBudget(int timeBudget) {
		this.timeBudget = timeBudget;
	}

	public double getTimeWeight() {
		return timeWeight;
	}

	public void setTimeWeight(double timeWeight) {
		this.timeWeight = timeWeight;
	}

	public double getPriorityWeight() {
		return priorityWeight;
	}

	public void setPriorityWeight(double priorityWeight) {
		this.priorityWeight = priorityWeight;
	}

	public double getProbabilityWeight() {
		return probabilityWeight;
	}

	public void setProbabilityWeight(double probabilityWeight) {
		this.probabilityWeight = probabilityWeight;
	}

	public double getConsequenceWeight() {
		return consequenceWeight;
	}

	public void setConsequenceWeight(double consequenceWeight) {
		this.consequenceWeight = consequenceWeight;
	}

	public double getRiskWeight() {
		return riskWeight;
	}

	public void setRiskWeight(double riskWeight) {
		this.riskWeight = riskWeight;
	}

	public double getEffPriorityWeight() {
		return effPriorityWeight;
	}

	public void setEffPriorityWeight(double effPriorityWeight) {
		this.effPriorityWeight = effPriorityWeight;
	}

	public double getEffProbabilityWeight() {
		return effProbabilityWeight;
	}

	public void setEffProbabilityWeight(double effProbabilityWeight) {
		this.effProbabilityWeight = effProbabilityWeight;
	}

	public double getEffConsequenceWeight() {
		return effConsequenceWeight;
	}

	public void setEffConsequenceWeight(double effConsequenceWeight) {
		this.effConsequenceWeight = effConsequenceWeight;
	}

	public double getEffRiskWeight() {
		return effRiskWeight;
	}

	public void setEffRiskWeight(double effRiskWeight) {
		this.effRiskWeight = effRiskWeight;
	}

	/**
	 * Returns the number of bits of the solutions of the problem
	 * 
	 * @return The number of bits solutions of the problem
	 */
	public int getNumberOfBits() {
		int result = 0;
		for (int var = 0; var < numberOfVariables_; var++) {
			result += getLength(var);
		}
		return result;
	} // getNumberOfBits();

	private List<TestCase> testCaseList = new ArrayList<>();
	private Map<String, List<Rule>> ruleMap = new HashMap<String, List<Rule>>();
	private Map<String, Set<String>> failedRuleNameMap = new HashMap<String, Set<String>>();
	private Map<String, Set<String>> partialFailedRuleNameMap = new HashMap<String, Set<String>>();
	private Map<String, Set<String>> passedRuleNameMap = new HashMap<String, Set<String>>();
	private Map<String, Set<String>> partialPassedRuleNameMap = new HashMap<String, Set<String>>();
	private Map<String, Integer> countRule = new HashMap<String, Integer>();
	private Map<String, Integer> countRuleOneDecides = new HashMap<String, Integer>();

	private List<String> skipTC = new ArrayList<String>();

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

	public void setTestCaseList(List<TestCase> testCaseList2) {
		this.testCaseList = testCaseList2;
	}

	public Map<String, List<Rule>> getRuleMap() {
		return ruleMap;
	}

	public void setRuleMap(Map<String, List<Rule>> ruleMap) {
		this.ruleMap = ruleMap;
	}

	public Map<String, Integer> getCountRule() {
		return countRule;
	}

	public void setCountRule(Map<String, Integer> countRule) {
		countRule = countRule;
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

} // Problem
