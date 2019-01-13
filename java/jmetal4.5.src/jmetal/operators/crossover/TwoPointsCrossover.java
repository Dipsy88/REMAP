//  TwoPointsCrossover.java
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

package jmetal.operators.crossover;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.PermutationSolutionType;
import jmetal.encodings.variable.Permutation;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

/**
 * This class allows to apply a two points crossover operator using two parent
 * solutions. NOTE: the type of the solutions must be Permutation..
 */
public class TwoPointsCrossover extends Crossover {

	/**
	 * Valid solution types to apply this operator
	 */
	private static final List VALID_TYPES = Arrays.asList(PermutationSolutionType.class);

	private Double crossoverProbability_ = null;

	/**
	 * Constructor Creates a new intance of the two point crossover operator
	 */
	public TwoPointsCrossover(HashMap<String, Object> parameters) {
		super(parameters);

		if (parameters.get("probability") != null)
			crossoverProbability_ = (Double) parameters.get("probability");
	} // TwoPointsCrossover

	/**
	 * Constructor
	 * 
	 * @param A
	 *            properties containing the Operator parameters Creates a new
	 *            intance of the two point crossover operator
	 */
	// public TwoPointsCrossover(Properties properties) {
	// this();
	// }

	/**
	 * Perform the crossover operation
	 * 
	 * @param probability
	 *            Crossover probability
	 * @param parent1
	 *            The first parent
	 * @param parent2
	 *            The second parent
	 * @return Two offspring solutions
	 * @throws JMException
	 */
	public Solution[] doCrossover(double probability, Solution parent1, Solution parent2) throws JMException {

		Solution[] offSpring = new Solution[2];

		offSpring[0] = new Solution(parent1);
		offSpring[1] = new Solution(parent2);

		if (parent1.getType().getClass() == PermutationSolutionType.class) {
			if (PseudoRandom.randDouble() < probability) {
				int crosspoint1;
				int crosspoint2;
				int permutationLength;
				int parent1Vector[];
				int parent2Vector[];
				int offspring1Vector[];
				int offspring2Vector[];

				permutationLength = ((Permutation) parent1.getDecisionVariables()[0]).getLength();
				parent1Vector = ((Permutation) parent1.getDecisionVariables()[0]).vector_;
				parent2Vector = ((Permutation) parent2.getDecisionVariables()[0]).vector_;
				offspring1Vector = ((Permutation) offSpring[0].getDecisionVariables()[0]).vector_;
				offspring2Vector = ((Permutation) offSpring[1].getDecisionVariables()[0]).vector_;

				// STEP 1: Get two cutting points
				crosspoint1 = PseudoRandom.randInt(0, permutationLength - 1);
				crosspoint2 = PseudoRandom.randInt(0, permutationLength - 1);

				while (crosspoint2 == crosspoint1)
					crosspoint2 = PseudoRandom.randInt(0, permutationLength - 1);

				if (crosspoint1 > crosspoint2) {
					int swap;
					swap = crosspoint1;
					crosspoint1 = crosspoint2;
					crosspoint2 = swap;
				} // if

				// STEP 2: Obtain the first child
				int m = 0;
				for (int j = 0; j < permutationLength; j++) {
					boolean exist = false;
					int temp = parent2Vector[j];
					for (int k = crosspoint1; k <= crosspoint2; k++) {
						if (temp == offspring1Vector[k]) {
							exist = true;
							break;
						} // if
					} // for
					if (!exist) {
						if (m == crosspoint1)
							m = crosspoint2 + 1;
						offspring1Vector[m++] = temp;
					} // if
				} // for

				// STEP 3: Obtain the second child
				m = 0;
				for (int j = 0; j < permutationLength; j++) {
					boolean exist = false;
					int temp = parent1Vector[j];
					for (int k = crosspoint1; k <= crosspoint2; k++) {
						if (temp == offspring2Vector[k]) {
							exist = true;
							break;
						} // if
					} // for
					if (!exist) {
						if (m == crosspoint1)
							m = crosspoint2 + 1;
						offspring2Vector[m++] = temp;
					} // if
				} // for
			} // if
		} // if
		else {
			int crosspoint1;
			int crosspoint2;

			crosspoint1 = PseudoRandom.randInt(0, parent1.numberOfVariables() - 1);
			crosspoint2 = PseudoRandom.randInt(0, parent2.numberOfVariables() - 1);
			while (crosspoint2 == crosspoint1)
				crosspoint2 = PseudoRandom.randInt(0, parent1.numberOfVariables() - 1);
			if (crosspoint1 > crosspoint2) {
				int swap;
				swap = crosspoint1;
				crosspoint1 = crosspoint2;
				crosspoint2 = swap;
			} // if

			double valueX1;
			double valueX2;
			Variable X1 = parent1.getDecisionVariables()[0];
			Variable X2 = parent2.getDecisionVariables()[0];

			List<Double> X1List = new ArrayList<Double>();
			List<Double> X2List = new ArrayList<Double>();

			Map<Double, Double> X1Hash = new HashMap<Double, Double>();
			Map<Double, Double> X2Hash = new HashMap<Double, Double>();

			// To avoid repetition
			for (int i = 0; i < crosspoint1; i++) {
				X1List.add(X1.getValue(i));
				X2List.add(X2.getValue(i));
			}

			for (int i = crosspoint2; i < parent1.numberOfVariables(); i++) {
				X1List.add(X1.getValue(i));
				X2List.add(X2.getValue(i));
			}

			for (int i = crosspoint1; i < crosspoint2; i++) {
				valueX1 = X1.getValue(i);
				valueX2 = X2.getValue(i);

				if (!X1List.contains(valueX2)) {
					X1Hash.put(valueX2, offSpring[0].getDecisionVariables()[0].getValue(i));
					offSpring[0].getDecisionVariables()[0].setValue(i, valueX2);
					X1List.add(valueX2);
					// System.out.println(i + " : " + valueX2);
				} else if (X1Hash.containsKey(valueX1)) {
					double key1Temp = X1Hash.get(valueX1);
					while (X1Hash.containsKey(key1Temp)) {
						if (X1Hash.containsKey(key1Temp))
							key1Temp = X1Hash.get(key1Temp);
						else
							continue;
					}
					offSpring[0].getDecisionVariables()[0].setValue(i, key1Temp);
					X1List.add(key1Temp);
				} else
					X1List.add(valueX1);

				if (!X2List.contains(valueX1)) {
					X2Hash.put(valueX1, offSpring[1].getDecisionVariables()[0].getValue(i));
					offSpring[1].getDecisionVariables()[0].setValue(i, valueX1);
					X2List.add(valueX1);
				} else if (X2Hash.containsKey(valueX2)) {
					double key2Temp = X2Hash.get(valueX2);
					while (X2Hash.containsKey(key2Temp)) {
						if (X2Hash.containsKey(key2Temp))
							key2Temp = X2Hash.get(key2Temp);
						else
							continue;
					}
					offSpring[1].getDecisionVariables()[0].setValue(i, key2Temp);
					X2List.add(key2Temp);
				} else
					X2List.add(valueX2);
			} // for

			int a;
			a = 3;
			// Configuration.logger_
			// .severe("TwoPointsCrossover.doCrossover: invalid "
			// + "type"
			// + parent1.getDecisionVariables()[0]
			// .getVariableType());
			// Class cls = java.lang.String.class;
			// String name = cls.getName();
			// throw new JMException("Exception in " + name + ".doCrossover()");
		}

		return offSpring;
	} // makeCrossover

	/**
	 * Executes the operation
	 * 
	 * @param object
	 *            An object containing an array of two solutions
	 * @return An object containing an array with the offSprings
	 * @throws JMException
	 */
	public Object execute(Object object) throws JMException {
		Solution[] parents = (Solution[]) object;
		Double crossoverProbability;

		/*
		 * if (!(VALID_TYPES.contains(parents[0].getType().getClass()) && VALID_TYPES
		 * .contains(parents[1].getType().getClass()))) {
		 * 
		 * Configuration.logger_ .severe("TwoPointsCrossover.execute: the solutions " +
		 * "are not of the right type. The type should be 'Permutation', but " +
		 * parents[0].getType() + " and " + parents[1].getType() + " are obtained"); }
		 * // if
		 */
		crossoverProbability = (Double) getParameter("probability");

		if (parents.length < 2) {
			Configuration.logger_.severe("TwoPointsCrossover.execute: operator needs two " + "parents");
			Class cls = java.lang.String.class;
			String name = cls.getName();
			throw new JMException("Exception in " + name + ".execute()");
		}

		Solution[] offspring = doCrossover(crossoverProbability_, parents[0], parents[1]);

		return offspring;
	} // execute

	@Override
	public Object executeProblem(Object object, Problem problem) throws JMException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object execute(Object object, double value) throws JMException {
		// TODO Auto-generated method stub
		return null;
	}

} // TwoPointsCrossover
