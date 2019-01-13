//  NSGAII.java
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

package jmetal.metaheuristics.nsgaII;

import java.io.File;

//import machineLearning.ML_ClassifyInstanceTest_allNew_2_4Obj;
//import machineLearning.ML_ClassifyInstanceTest_allNew_2_3Obj;
import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.Distance;
import jmetal.util.JMException;
import jmetal.util.Ranking;
import jmetal.util.comparators.CrowdingComparator;

/**
 * Implementation of NSGA-II. This implementation of NSGA-II makes use of a
 * QualityIndicator object to obtained the convergence speed of the algorithm.
 * This version is used in the paper: A.J. Nebro, J.J. Durillo, C.A. Coello
 * Coello, F. Luna, E. Alba
 * "A Study of Convergence Speed in Multi-Objective Metaheuristics." To be
 * presented in: PPSN'08. Dortmund. September 2008.
 */

public class NSGAII_ProjectScheduling extends Algorithm {
	/**
	 * Constructor
	 * 
	 * @param problem
	 *            Problem to solve
	 */
	public NSGAII_ProjectScheduling(Problem problem) {
		super(problem);
	} // NSGAII

	File fileName;

	/**
	 * Runs the NSGA-II algorithm.
	 * 
	 * @return a <code>SolutionSet</code> that is a set of non dominated
	 *         solutions as a result of the algorithm execution
	 * @throws JMException
	 */
	public SolutionSet execute() throws JMException, ClassNotFoundException {
		int populationSize;
		int maxEvaluations;
		int evaluations;

		QualityIndicator indicators; // QualityIndicator object
		int requiredEvaluations; // Use in the example of use of the
		// indicators object (see below)

		SolutionSet population;
		SolutionSet offspringPopulation;
		SolutionSet union;

		Operator mutationOperator;
		Operator crossoverOperator;
		Operator selectionOperator;

		Distance distance = new Distance();

		// Read the parameters
		populationSize = ((Integer) getInputParameter("populationSize"))
				.intValue();
		maxEvaluations = ((Integer) getInputParameter("maxEvaluations"))
				.intValue();
		indicators = (QualityIndicator) getInputParameter("indicators");

		// Initialize the variables
		population = new SolutionSet(populationSize);
		evaluations = 0;

		requiredEvaluations = 0;

		// Read the operators
		mutationOperator = operators_.get("mutation");
		crossoverOperator = operators_.get("crossover");
		selectionOperator = operators_.get("selection");

		// Create the initial solutionSet
		Solution newSolution;

		// double[][] sendCaseValue = machineLearning(populationSize, problem_);
		// //Dipesh

		while (population.size() < 100) {
			newSolution = new Solution(problem_);

			problem_.evaluate(newSolution);
			problem_.evaluateConstraints(newSolution);
			// newSolution.setCases_(problem_.getCases());

			if (newSolution.getNormDistance() < 1.41) {
				evaluations++;
				population.add(newSolution);
			}
		} // for

		// Generations
		while (evaluations < maxEvaluations) {
			// Create the offSpring solutionSet
			offspringPopulation = new SolutionSet(populationSize);
			Solution[] parents = new Solution[2];
			for (int i = 0; i < (populationSize / 2); i++) {
				if (evaluations < maxEvaluations) {
					// obtain parents
					parents[0] = (Solution) selectionOperator
							.execute(population);
					parents[1] = (Solution) selectionOperator
							.execute(population);
					Solution[] offSpring = (Solution[]) crossoverOperator
							.execute(parents);
					mutationOperator.execute(offSpring[0]);
					mutationOperator.execute(offSpring[1]);
					problem_.evaluate(offSpring[0]);
					problem_.evaluateConstraints(offSpring[0]);
					// offSpring[0].setCases_(problem_.getCases());
					problem_.evaluate(offSpring[1]);
					problem_.evaluateConstraints(offSpring[1]);
					// offSpring[1].setCases_(problem_.getCases());
					if (offSpring[0].getNormDistance() < 1.41) {
						evaluations++;
						offspringPopulation.add(offSpring[0]);
					}
					if (offSpring[1].getNormDistance() < 1.41) {
						evaluations++;
						offspringPopulation.add(offSpring[1]);
					}
				} // if
			} // for

			// Create the solutionSet union of solutionSet and offSpring
			union = ((SolutionSet) population).union(offspringPopulation);

			// Ranking the union
			Ranking ranking = new Ranking(union);

			int remain = populationSize;
			int index = 0;
			SolutionSet front = null;
			population.clear();

			// Obtain the next front
			front = ranking.getSubfront(index);

			while ((remain > 0) && (remain >= front.size())) {
				// Assign crowding distance to individuals
				distance.crowdingDistanceAssignment(front,
						problem_.getNumberOfObjectives());
				// Add the individuals of this front
				for (int k = 0; k < front.size(); k++) {
					population.add(front.get(k));
				} // for

				// Decrement remain
				remain = remain - front.size();

				// Obtain the next front
				index++;
				if (remain > 0) {
					front = ranking.getSubfront(index);
				} // if
			} // while

			// Remain is less than front(index).size, insert only the best one
			if (remain > 0) { // front contains individuals to insert
				distance.crowdingDistanceAssignment(front,
						problem_.getNumberOfObjectives());
				front.sort(new CrowdingComparator());
				for (int k = 0; k < remain; k++) {
					population.add(front.get(k));
				} // for

				remain = 0;
			} // if

			// This piece of code shows how to use the indicator object into the
			// code
			// of NSGA-II. In particular, it finds the number of evaluations
			// required
			// by the algorithm to obtain a Pareto front with a hypervolume
			// higher
			// than the hypervolume of the true Pareto front.
			if ((indicators != null) && (requiredEvaluations == 0)) {
				double HV = indicators.getHypervolume(population);
				if (HV >= (0.98 * indicators.getTrueParetoFrontHypervolume())) {
					requiredEvaluations = evaluations;
				} // if
			} // if

			// System.out.println(evaluations);
		} // while

		// Return as output parameter the required evaluations
		setOutputParameter("evaluations", requiredEvaluations);

		// Return the first non-dominated front
		Ranking ranking = new Ranking(population);
		ranking.getSubfront(0).printFeasibleFUN("FUN_NSGAII");

		return ranking.getSubfront(0);
	} // execute

	// public double[][] machineLearning(int populationSize, Problem problem){
	// ML_ClassifyInstanceTest_allNew_2_4Obj ml = new
	// ML_ClassifyInstanceTest_allNew_2_4Obj();
	// try {
	// ml.executeML(populationSize, problem);
	// // ml.executeML(20, problem);
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// double[][] sendCaseValue = ml.getSendCaseValue();
	// return sendCaseValue;
	// }

	// Dipesh
	public void printObj(SolutionSet population) {
		double obj1 = 0, obj2 = 0, obj3 = 0, obj4 = 0;

		for (int i = 0; i < population.size(); i++) {
			obj1 += population.get(i).getObjective(0);
			obj2 += population.get(i).getObjective(1);
			obj3 += population.get(i).getObjective(2);
			obj4 += population.get(i).getObjective(3);
		}
		System.out.println(obj1 / population.size() + "\t" + obj2
				/ population.size() + "\t" + obj3 / population.size() + "\t"
				+ obj4 / population.size());
	}

} // NSGA-II
