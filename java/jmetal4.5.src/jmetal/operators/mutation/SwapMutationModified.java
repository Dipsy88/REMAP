//  SwapMutation.java
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

package jmetal.operators.mutation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.encodings.solutionType.PermutationSolutionType;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.wrapper.XReal;

/**
 * This class implements a swap mutation. The solution type of the solution must
 * be Permutation.
 */
public class SwapMutationModified extends Mutation {
	/**
	 * Valid solution types to apply this operator
	 */
	private static final List VALID_TYPES = Arrays.asList(PermutationSolutionType.class);

	private Double mutationProbability_ = null;

	/**
	 * Constructor
	 */
	public SwapMutationModified(HashMap<String, Object> parameters) {
		super(parameters);

		if (parameters.get("probability") != null)
			mutationProbability_ = (Double) parameters.get("probability");
	} // Constructor

	/**
	 * Constructor
	 */
	// public SwapMutation(Properties properties) {
	// this();
	// } // Constructor

	/**
	 * Performs the operation
	 * 
	 * @param probability
	 *            Mutation probability
	 * @param solution
	 *            The solution to mutate
	 * @throws JMException
	 */
	public void doMutation(double probability, Solution solution) throws JMException {
		int permutation[];
		int permutationLength;
		// if (solution.getType().getClass() == PermutationSolutionType.class) {

		permutationLength = solution.numberOfVariables();
		// permutation =
		// ((Permutation)solution.getDecisionVariables()[0]).vector_ ;
		XReal x = new XReal(solution);
		for (int var = 0; var < solution.numberOfVariables(); var++) {
			if (PseudoRandom.randDouble() < probability) {
				int pos1;
				int pos2;

				pos1 = PseudoRandom.randInt(0, permutationLength - 1);
				pos2 = PseudoRandom.randInt(0, permutationLength - 1);

				while (pos1 == pos2) {
					if (pos1 == (permutationLength - 1))
						pos2 = PseudoRandom.randInt(0, permutationLength - 2);
					else
						pos2 = PseudoRandom.randInt(pos1, permutationLength - 1);
				} // while
					// swap
				double temp = x.getValue(pos1);
				x.setValue(pos1, x.getValue(pos2));
				x.setValue(pos2, temp);
			} // if
		} // for
		/*
		 * } // if else {
		 * Configuration.logger_.severe("SwapMutation.doMutation: invalid type. " + ""+
		 * solution.getDecisionVariables()[0].getVariableType());
		 * 
		 * Class cls = java.lang.String.class; String name = cls.getName(); throw new
		 * JMException("Exception in " + name + ".doMutation()") ; }
		 */
	} // doMutation

	/**
	 * Executes the operation
	 * 
	 * @param object
	 *            An object containing the solution to mutate
	 * @return an object containing the mutated solution
	 * @throws JMException
	 */
	public Object execute(Object object) throws JMException {
		Solution solution = (Solution) object;

		/*
		 * if (!VALID_TYPES.contains(solution.getType().getClass())) {
		 * Configuration.logger_.severe("SwapMutation.execute: the solution " +
		 * "is not of the right type. The type should be 'Binary', " +
		 * "'BinaryReal' or 'Int', but " + solution.getType() + " is obtained");
		 * 
		 * Class cls = java.lang.String.class; String name = cls.getName(); throw new
		 * JMException("Exception in " + name + ".execute()"); } // if
		 */

		this.doMutation(mutationProbability_, solution);
		return solution;
	} // execute

	@Override
	public Object executeProblem(Object object, Problem problem) throws JMException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object execute(Object object, double value) throws JMException {
		Solution solution = (Solution) object;
		this.doMutation(value, solution);
		return solution;
	}
} // SwapMutation
